package com.aaronpmaus.jMath.graph;

import java.util.Collection;
import java.util.List;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.PriorityQueue;
import java.util.Collections;
import java.util.Iterator;
import java.util.NoSuchElementException;

//import com.aaronpmaus.jMath.graph.*;
class MaxSatUB <T extends Comparable<? super T>> {
  private  boolean verbose = false;
  private MaxSatEncoding originalEncoding;
  private int partitionSize;

  public MaxSatUB(UndirectedGraph<T> graph) {
    ArrayList<ArrayList<Node<T>>> partition = partitionGraph(graph);
    this.partitionSize = partition.size();
    this.originalEncoding = new MaxSatEncoding(graph, partition);
  }

  public MaxSatUB(UndirectedGraph<T> graph, ArrayList<ArrayList<Node<T>>> partition) {
    this.partitionSize = partition.size();
    this.originalEncoding = new MaxSatEncoding(graph, partition);
  }

  public int estimateCardinality() {
    if(verbose) System.out.println("Starting MaxSatUB");
    if(verbose) System.out.println(originalEncoding);
    // perform cardinality test on encoding
    int s = 0;
    while(originalEncoding.hasSoftClauses()) {
      originalEncoding.initializeInconsistentClauses();
      Clause min = originalEncoding.getMinUntestedSoftClause();
      // if min is null, there are no untested Soft Clauses
      if(min == null) {
        break;
      }
      min.setAsTested();
      if(verbose) System.out.println("Testing min Clause " + min);
      if(failedClauseDetection(originalEncoding, min)) {
        originalEncoding.removeSoftClause(min);
        originalEncoding.removeInconsistentClauses();
        if(verbose) System.out.println("After Failed Clause Detection, Encoding: \n" + originalEncoding);
        s++;
      }
    }
    int cardinality = this.partitionSize - s;
    if(verbose) System.out.println("CARDINALITY ESTIMATE: " + cardinality);
    if(verbose) System.out.println("#########################");
    return cardinality;
  }

  private boolean failedClauseDetection(MaxSatEncoding encoding, Clause clause) {
    for(T literal : clause) {
      if(!failedLiteralDetection(encoding, literal)) {
        return false;
      }
    }
    return true;
  }

  private boolean failedLiteralDetection(MaxSatEncoding encoding, T literal) {
    // remove all soft clauses containing literal
    encoding = new MaxSatEncoding(encoding);
    Iterator<Clause> it = encoding.getSoftClauses().iterator();
    while(it.hasNext()) {
      if(it.next().contains(literal)) {
        it.remove();
      }
    }
    // remove the literal from all hard clauses
    for(Clause clause : encoding.getHardClauses()) {
      if(clause.contains(literal)) {
        if(verbose) System.out.printf("Removing %s from Clause %s\n", literal, clause);
        clause.remove(literal);
        if(clause.isEmpty()) {
          if(verbose) System.out.println("Reached Empty Clause before Unit Prop. Return.");
          encoding.setContainsEmptyClause();
          return true;
        }
      }
    }
    // perform unit propogation
    if(verbose) System.out.println("Encoding before Unit Propogation:\n" + originalEncoding);
    encoding = unitPropogation(encoding);
    if(encoding.containsEmptyClause()) {
      return true;
    } /*else {
      for(Clause clause : encoding.getNewBinaryClauses()) {
        if(failedClauseDetection(encoding, clause)) {
          return true;
        }
      }
      return false;
    }*/
    return false;
  }

  private MaxSatEncoding unitPropogation( MaxSatEncoding encoding) {

    // the set of soft clauses that give rise to a contradiction, any soft clause that is modified
    // on the path to deriving an empty clause
    LinkedList<Clause> inconsistentClauses = new LinkedList<Clause>();
    LinkedList<Clause> queue = encoding.getUnitClauses();
    while(!queue.isEmpty()) {
      Clause c = queue.poll();
      T l = c.getLiteral();
      if(verbose) System.out.println("Processing " + l);

      if(c.isSoft()) {
        // remove l from all hard clauses that contain it
        for(Clause hardClause : encoding.getHardClauses()) {
          if(hardClause.contains(l)) {
            if(verbose) System.out.printf("Removing %s from Clause %s\n", l, hardClause);
            hardClause.remove(l);
            // if this clause ONLY contained l, then it is now an empty clause.
            // set containsEmptyClause to TRUE, add inconsistentClauses to original
            // encoding, and return encoding
            if(hardClause.isEmpty()) {
              if(verbose) System.out.println("Reaching Empty Clause. Return.");
              encoding.setContainsEmptyClause();
              this.originalEncoding.addInconsistentClauses(inconsistentClauses);
              return encoding;
            }
            // Otherwise, after removing l, it will become a unit clause, add it to the queue
            if(verbose) System.out.printf("Is Unit Clause, adding %s to queue\n", hardClause);
            queue.offer(hardClause);
          }
        }
      } else { // Otherwise, the unit clause is a hard clause
        // implying that the literal in it must be 0 and can be removed from all soft clauses
        for(Clause softClause : encoding.getSoftClauses()) {
          if(softClause.contains(l)) {
            // save this soft clause from the original encoding in inconsistenSubset
            // we only want to save it once, before it has been modified. If the original encoding
            // contains a soft clause with the same literals as this one, then get than clause and
            // add it to the list of inconsistent subsets.
            if(this.originalEncoding.containsSoftClause(softClause)) {
              inconsistentClauses.add(this.originalEncoding.getSoftClause(softClause));
            }
            if(verbose) System.out.printf("Removing %s from Clause %s\n", l, softClause);
            softClause.remove(l);
            // if the softClause is empty, wrap up unit propogation: set containsEmptyClause, add
            // inconsistentClauses to the original encoding, and return encoding.
            if(softClause.isEmpty()) {
              encoding.setContainsEmptyClause();
              this.originalEncoding.addInconsistentClauses(inconsistentClauses);
              return encoding;
            }
            if(softClause.isUnitClause()) {
              if(verbose) System.out.printf("Is Unit Clause, adding %s to queue\n", softClause);
              queue.offer(softClause);
            }
          }
        }
      }
    }
    return encoding;
  }

  /**
  * This method is a heuristic to estimate the number of independent sets in a graph via a greedy
  * graph coloring algorithm. The algorithm comes from Tomita et al. 2003 and 2010.
  * @param g the graph a clique is being sought in
  * @return the partition of the graph into independent sets (color sets)
  */
  private ArrayList<ArrayList<Node<T>>> partitionGraph(UndirectedGraph<T> g) {
    //System.out.println("## Calculating indSetUB");
    // get a list of nodes and sort them in descending order by degree
    List<Node<T>> descendingDegreeNodes = new ArrayList<Node<T>>(g.getNodes());
    Collections.sort(descendingDegreeNodes, Collections.reverseOrder());
    int maxColorNumber = 0;
    // initialize color sets
    // The index of the outer ArrayList is k and the inner arraylists hold all the nodes that belong
    // to that color k.
    ArrayList<ArrayList<Node<T>>> colorSets = new ArrayList<ArrayList<Node<T>>>();
    // initialize the first two color sets (k = 0,1)
    colorSets.add(new ArrayList<Node<T>>());
    colorSets.add(new ArrayList<Node<T>>());
    for(Node<T> node : descendingDegreeNodes) {
      int k = 0;
      // find the lowest k where neighbors and the set of nodes in colorSets[k] share no nodes
      // in common
      while(!Collections.disjoint(node.getNeighbors(), colorSets.get(k))) {
        k++;
      }
      if(k > maxColorNumber) {
        maxColorNumber = k;
        // initialize and add the next color set
        colorSets.add(new ArrayList<Node<T>>());
      }
      colorSets.get(k).add(node);
    }
    // return the number of colors assigned
    //return maxColorNumber + 1;
    return colorSets;
  }

  private class MaxSatEncoding {
    private LinkedList<Clause> softClauses;
    private LinkedList<Clause> hardClauses;
    private int partitionSize;
    private boolean containsEmptyClause = false;
    private LinkedList<Clause> inconsistentClauses;

    public MaxSatEncoding(UndirectedGraph<T> graph, ArrayList<ArrayList<Node<T>>> partition) {
      softClauses = new LinkedList<Clause>();
      hardClauses = new LinkedList<Clause>();

      for(ArrayList<Node<T>> set : partition) {
        if(set.size() > 0) {
          //System.out.println("Adding set: " + set);
          Clause clause = new Clause(set, true);
          //System.out.println("Add Soft Clause: " + clause);
          addSoftClause(clause);
        }
      }
      List<T> elements = graph.getElements();
      for(int i = 0; i < graph.size(); i++) {
        T vertex = elements.get(i);
        for(int j = i+1; j < graph.size(); j++) {
          T neighbor = elements.get(j);
          if(vertex != neighbor && !graph.hasEdge(vertex, neighbor)) {
            Clause clause = new Clause(vertex, neighbor, false);
            //System.out.println("Add Hard Clause: " + clause);
            addHardClause(clause);
          }
        }
      }
    }

    public MaxSatEncoding(MaxSatEncoding other) {
      softClauses = new LinkedList<Clause>();
      hardClauses = new LinkedList<Clause>();
      for(Clause clause : other.getSoftClauses()) {
        Clause copy = new Clause(clause);
        softClauses.add(copy);
      }
      for(Clause clause : other.getHardClauses()) {
        Clause copy = new Clause(clause);
        hardClauses.add(copy);
      }
    }

    public LinkedList<Clause> getNewBinaryClauses() {
      LinkedList<Clause> clauses = new LinkedList<Clause>();
      for(Clause clause : getSoftClauses()){
        if(clause.getNumLiterals() == 2 && clause.isModified()){
          clauses.add(clause);
        }
      }
      return clauses;
    }

    public LinkedList<Clause> getUnitClauses() {
      LinkedList<Clause> queue = new LinkedList<Clause>();
      for(Clause clause : softClauses) {
        if(clause.isUnitClause()){
          queue.offer(clause);
        }
      }
      for(Clause clause : hardClauses) {
        if(clause.isUnitClause()){
          queue.offer(clause);
        }
      }
      return queue;
    }

    // nullable
    public Clause getMinUntestedSoftClause() {
      int minSize = Integer.MAX_VALUE;
      Clause min = null;
      for(Clause clause : softClauses) {
        if(!clause.isTested()) {
          if(clause.getNumLiterals() < minSize) {
            minSize = clause.getNumLiterals();
            min = clause;
          }
        }
      }
      return min;
    }

    public void initializeInconsistentClauses() {
      inconsistentClauses = new LinkedList<Clause>();
    }

    public void removeInconsistentClauses() {
      for(Clause clause : inconsistentClauses) {
        removeSoftClause(clause);
      }
    }

    public void addInconsistentClauses(List<Clause> clauses) {
      inconsistentClauses.addAll(clauses);
    }

    public boolean hasSoftClauses() {
      if(softClauses.size() > 0) {
        return true;
      }
      return false;
    }

    public int getNumSoftClauses() {
      return softClauses.size();
    }

    public int getNumHardClauses() {
      return hardClauses.size();
    }

    public Collection<Clause> getSoftClauses() {
      return this.softClauses;
    }

    public List<Clause> getHardClauses() {
      return this.hardClauses;
    }

    public void removeSoftClause(Clause clause) {
      softClauses.remove(clause);
    }

    public void addSoftClause(Clause clause) {
      softClauses.add(clause);
    }

    public void addHardClause(Clause clause) {
      hardClauses.add(clause);
    }

    public void setContainsEmptyClause() {
      this.containsEmptyClause = true;
    }

    public boolean containsEmptyClause() {
      return this.containsEmptyClause;
    }

    public boolean containsSoftClause(Clause other) {
      return this.softClauses.contains(other);
    }

    public Clause getSoftClause(Clause other) {
      for(Clause clause : this.softClauses) {
        if(clause.equals(other)) {
          return clause;
        }
      }
      throw new NoSuchElementException(String.format("Clause %s not in Encoding", other));
    }

    @Override
    public String toString() {
      String str = "Soft Clauses:\n {";
      int i = 0;
      for(Clause soft : getSoftClauses()) {
        if(i == getNumSoftClauses()-1){
          str += soft;
        } else {
          str += soft + ", ";
        }
        i++;
      }
      str += "}\nHard Clauses:\n {";
      i = 0;
      for(Clause hard : getHardClauses()) {
        if(i == getNumHardClauses()-1){
          str += hard;
        } else {
          str += hard + ", ";
        }
        i++;
      }
      str += "}";
      return str;
    }
  }

  private class Clause implements Comparable<Clause>, Iterable<T> {
    private ArrayList<T> literals;
    private boolean isSoft;
    private boolean modified = false;
    private boolean tested = false;

    public Clause(ArrayList<Node<T>> vertices, boolean isSoft) {
      this.isSoft = isSoft;
      literals = new ArrayList<T>(vertices.size());
      for(Node<T> vertex : vertices) {
        literals.add(vertex.get());
      }
    }

    public Clause(T a, T b, boolean isSoft) {
      this.isSoft = isSoft;
      literals = new ArrayList<T>(2);
      literals.add(a);
      literals.add(b);
    }

    public Clause(Clause other) {
      literals = new ArrayList<T>(other.getNumLiterals());
      for(T lit : other) {
        literals.add(lit);
      }
      this.isSoft = other.isSoft();
    }

    public int getNumLiterals() {
      return literals.size();
    }

    public boolean isUnitClause() {
      return getNumLiterals() == 1;
    }

    public boolean isEmpty() {
      return getNumLiterals() == 0;
    }

    public boolean isSoft() {
      return isSoft;
    }

    public boolean isTested() {
      return tested;
    }

    public void setAsTested() {
      this.tested = true;
    }

    public boolean contains(T literal) {
      return literals.contains(literal);
    }

    public void remove(T literal) {
      literals.remove(literal);
      this.modified = true;
    }

    public boolean isModified() {
      return this.modified;
    }

    /**
    * Only call if this clause is a unit clause. Returns the remaining literal.
    * @return the remaining literal in a unit clause
    */
    public T getLiteral() {
      return literals.get(0);
    }

    @Override
    public int compareTo(Clause other) {
      return this.getNumLiterals() - other.getNumLiterals();
    }

    public ArrayList<T> getLiterals() {
      return this.literals;
    }

    @Override
    public Iterator<T> iterator() {
      return literals.iterator();
    }

    @Override
    public int hashCode() {
      return this.literals.hashCode();
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean equals(Object obj) {
      //if(obj instanceof Clause) {
      if(obj.getClass() == Clause.class) {
        Clause other = (Clause) obj;
        if(this.literals.equals(other.getLiterals())) {
          return true;
        }
      }
      return false;
    }

    @Override
    public String toString() {
      String str = "";
      if(isSoft()) {
        if(!isEmpty()) {
          str = String.format("%s",literals.get(0));
          for(int i = 1; i < literals.size(); i++) {
            str = String.format("%s V %s", str, literals.get(i));
          }
        }
      } else {
        if(!isEmpty()){
          str = String.format("!%s",literals.get(0));
          for(int i = 1; i < literals.size(); i++) {
            str = String.format("%s V !%s", str, literals.get(i));
          }
        }
      }
      return str;
    }
  }
}
