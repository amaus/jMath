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
class MaxSatUB {
  private static boolean verbose = false;

  public static <T extends Comparable<? super T>> int estimateCardinality(UndirectedGraph<T> graph) {
    return estimateCardinality(graph, partitionGraph(graph));
  }

  public static <T extends Comparable<? super T>> int estimateCardinality(UndirectedGraph<T> graph, ArrayList<ArrayList<Node<T>>> partition) {
    // Encode graph into MaxSatEncoding
    MaxSatEncoding<T> encoding = new MaxSatEncoding<T>(graph, partition);

    if(verbose) System.out.println("Starting MaxSatUB");
    if(verbose) System.out.println(encoding);
    // perform cardinality test on encoding
    int s = 0;
    while(encoding.hasSoftClauses()) {
      encoding.initializeInconsistentClauses();
      Clause<T> min = encoding.getMinUntestedSoftClause();
      // if min is null, there are no untested Soft Clauses
      if(min == null) {
        break;
      }
      min.setAsTested();
      if(verbose) System.out.println("Testing min Clause " + min);
      if(failedClauseDetection(encoding, min)) {
        encoding.removeSoftClause(min);
        encoding.removeInconsistentClauses();
        if(verbose) System.out.println("After Failed Clause Detection, Encoding: \n" + encoding);
        s++;
      }
    }
    int cardinality = partition.size() - s;
    if(verbose) System.out.println("CARDINALITY ESTIMATE: " + cardinality);
    if(verbose) System.out.println("#########################");
    return partition.size() - s;
  }

  private static <T extends Comparable<? super T>> boolean failedClauseDetection(MaxSatEncoding<T> encoding, Clause<T> clause) {
    for(T literal : clause) {
      if(!failedLiteralDetection(encoding, literal)) {
        return false;
      }
    }
    return true;
  }

  private static <T extends Comparable<? super T>> boolean failedLiteralDetection(MaxSatEncoding<T> originalEncoding, T literal) {
    // remove all soft clauses containing literal
    MaxSatEncoding<T> encoding = new MaxSatEncoding<T>(originalEncoding);
    Iterator<Clause<T>> it = encoding.getSoftClauses().iterator();
    while(it.hasNext()) {
      if(it.next().contains(literal)) {
        it.remove();
      }
    }
    // remove the literal from all hard clauses
    for(Clause<T> clause : encoding.getHardClauses()) {
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
    encoding = unitPropogation(encoding, originalEncoding);
    if(encoding.containsEmptyClause()) {
      return true;
    }/* else {
      for(Clause<T> clause : encoding.getNewBinaryClauses()) {
        if(failedClauseDetection(encoding, clause)) {
          return true;
        }
      }
      return false;
    }*/
    return false;
  }

  private static <T extends Comparable<? super T>> MaxSatEncoding<T> unitPropogation(
      MaxSatEncoding<T> encoding, MaxSatEncoding<T> originalEncoding) {

    // the set of soft clauses that give rise to a contradiction, any soft clause that is modified
    // on the path to deriving an empty clause
    LinkedList<Clause<T>> inconsistentClauses = new LinkedList<Clause<T>>();
    LinkedList<Clause<T>> queue = new LinkedList<Clause<T>>();
    for(Clause<T> unit : encoding.getUnitClauses()) {
      queue.offer(unit);
    }
    while(!queue.isEmpty()) {
      Clause<T> c = queue.poll();
      T l = c.getLiteral();
      if(verbose) System.out.println("Processing " + l);

      if(c.isSoft()) {
        // remove l from all hard clauses that contain it
        for(Clause<T> hardClause : encoding.getHardClauses()) {
          if(hardClause.contains(l)) {
            if(verbose) System.out.printf("Removing %s from Clause %s\n", l, hardClause);
            hardClause.remove(l);
            // if this clause ONLY contained l, then it is now an empty clause.
            // set containsEmptyClause to TRUE, add inconsistentClauses to original
            // encoding, and return encoding
            if(hardClause.isEmpty()) {
              if(verbose) System.out.println("Reaching Empty Clause. Return.");
              encoding.setContainsEmptyClause();
              originalEncoding.addInconsistentClauses(inconsistentClauses);
              return encoding;
            }
            // Otherwise, after removing l, it will become a unit clause, add it to the queue
            if(verbose) System.out.printf("Is Unit Clause, adding %s to queue\n", hardClause);
            queue.offer(hardClause);
          }
        }
      } else { // Otherwise, the unit clause is a hard clause
        // implying that the literal in it must be 0 and can be removed from all soft clauses
        for(Clause<T> softClause : encoding.getSoftClauses()) {
          if(softClause.contains(l)) {
            // save this soft clause from the original encoding in inconsistenSubset
            // we only want to save it once, before it has been modified. If the original encoding
            // contains a soft clause with the same literals as this one, then get than clause and
            // add it to the list of inconsistent subsets.
            if(originalEncoding.containsSoftClause(softClause)) {
              inconsistentClauses.add(originalEncoding.getSoftClause(softClause));
            }
            if(verbose) System.out.printf("Removing %s from Clause %s\n", l, softClause);
            softClause.remove(l);
            // if the softClause is empty, wrap up unit propogation: set containsEmptyClause, add
            // inconsistentClauses to the original encoding, and return encoding.
            if(softClause.isEmpty()) {
              encoding.setContainsEmptyClause();
              originalEncoding.addInconsistentClauses(inconsistentClauses);
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
  private static <T extends Comparable<? super T>> ArrayList<ArrayList<Node<T>>> partitionGraph(UndirectedGraph<T> g) {
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

  private static class MaxSatEncoding  <T extends Comparable<? super T>> {
    private LinkedList<Clause<T>> softClauses;
    private LinkedList<Clause<T>> hardClauses;
    private int partitionSize;
    private boolean containsEmptyClause = false;
    private LinkedList<Clause<T>> inconsistentClauses;

    public MaxSatEncoding(UndirectedGraph<T> graph, ArrayList<ArrayList<Node<T>>> partition) {
      softClauses = new LinkedList<Clause<T>>();
      hardClauses = new LinkedList<Clause<T>>();

      for(ArrayList<Node<T>> set : partition) {
        if(set.size() > 0) {
          //System.out.println("Adding set: " + set);
          Clause<T> clause = new Clause<T>(set, true);
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
            Clause<T> clause = new Clause<T>(vertex, neighbor, false);
            //System.out.println("Add Hard Clause: " + clause);
            addHardClause(clause);
          }
        }
      }
    }

    public MaxSatEncoding(MaxSatEncoding<T> other) {
      softClauses = new LinkedList<Clause<T>>();
      hardClauses = new LinkedList<Clause<T>>();
      for(Clause<T> clause : other.getSoftClauses()) {
        Clause<T> copy = new Clause<T>(clause);
        softClauses.add(copy);
      }
      for(Clause<T> clause : other.getHardClauses()) {
        Clause<T> copy = new Clause<T>(clause);
        hardClauses.add(copy);
      }
    }

    public LinkedList<Clause<T>> getNewBinaryClauses() {
      LinkedList<Clause<T>> clauses = new LinkedList<Clause<T>>();
      for(Clause<T> clause : getSoftClauses()){
        if(clause.getNumLiterals() == 2 && clause.isModified()){
          clauses.add(clause);
        }
      }
      return clauses;
    }

    public LinkedList<Clause<T>> getUnitClauses() {
      LinkedList<Clause<T>> queue = new LinkedList<Clause<T>>();
      for(Clause<T> clause : softClauses) {
        if(clause.isUnitClause()){
          queue.offer(clause);
        }
      }
      for(Clause<T> clause : hardClauses) {
        if(clause.isUnitClause()){
          queue.offer(clause);
        }
      }
      return queue;
    }

    // nullable
    public Clause<T> getMinUntestedSoftClause() {
      int minSize = Integer.MAX_VALUE;
      Clause<T> min = null;
      for(Clause<T> clause : softClauses) {
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
      inconsistentClauses = new LinkedList<Clause<T>>();
    }

    public void removeInconsistentClauses() {
      for(Clause<T> clause : inconsistentClauses) {
        removeSoftClause(clause);
      }
    }

    public void addInconsistentClauses(List<Clause<T>> clauses) {
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

    public Collection<Clause<T>> getSoftClauses() {
      return this.softClauses;
    }

    public List<Clause<T>> getHardClauses() {
      return this.hardClauses;
    }

    public void removeSoftClause(Clause<T> clause) {
      softClauses.remove(clause);
    }

    public void addSoftClause(Clause<T> clause) {
      softClauses.add(clause);
    }

    public void addHardClause(Clause<T> clause) {
      hardClauses.add(clause);
    }

    public void setContainsEmptyClause() {
      this.containsEmptyClause = true;
    }

    public boolean containsEmptyClause() {
      return this.containsEmptyClause;
    }

    public boolean containsSoftClause(Clause<T> other) {
      return this.softClauses.contains(other);
    }

    public Clause<T> getSoftClause(Clause<T> other) {
      for(Clause<T> clause : this.softClauses) {
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
      for(Clause<T> soft : getSoftClauses()) {
        if(i == getNumSoftClauses()-1){
          str += soft;
        } else {
          str += soft + ", ";
        }
        i++;
      }
      str += "}\nHard Clauses:\n {";
      i = 0;
      for(Clause<T> hard : getHardClauses()) {
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

  private static class Clause <T extends Comparable<? super T>> implements Comparable<Clause<T>>, Iterable<T> {
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

    public Clause(Clause<T> other) {
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
    public int compareTo(Clause<T> other) {
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
        Clause<T> other = (Clause<T>) obj;
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
