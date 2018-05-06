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

class MaxSatUB <T extends Comparable<? super T>> {
  private boolean verbose = false;
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
      if(verbose) System.out.printf("Starting round of Failed Clause Detection\n");
      if(verbose) System.out.printf("Working on encoding\n %s\n",originalEncoding);
      originalEncoding.initializeInconsistentClauses();
      Clause min = originalEncoding.getMinUntestedSoftClause();
      // if min is null, there are no untested Soft Clauses
      if(min == null) {
        break;
      }
      min.setAsTested();
      if(verbose) System.out.println("Testing min Clause " + min);
      if(failedClauseDetection(originalEncoding, min)) {
        if(verbose) System.out.printf("Failed Clause Detected: %s\n", min);
        if(verbose) System.out.printf("Removing it and inconsistent clauses:\n");
        originalEncoding.removeSoftClause(min);
        originalEncoding.removeInconsistentClauses();
        if(verbose) System.out.println("After Failed Clause Detection, Encoding: \n" + originalEncoding);
        s++;
      }
    }
    int cardinality = this.partitionSize - s;
    if(verbose) System.out.printf("CARDINALITY ESTIMATE %d - %d: %d\n", partitionSize, s, cardinality);
    if(verbose) System.out.println("#########################");
    //System.exit(0);
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
    boolean negated;
    Clause c;
    while(it.hasNext()) {
      c = it.next();
      negated = false;
      // if the clause contains the nonnegated form of the literal, remove the clause
      if(c.contains(literal, negated)) {
        it.remove();
      }
    }
    it = encoding.getHardClauses().iterator();
    while(it.hasNext()) {
      c = it.next();
      negated = true;
      // if the clause contains the negated form of the literal, remove the literal
      if(c.contains(literal, negated)) {
        if(verbose) System.out.printf("Removing %s from Clause %s\n", literal, c);
        c.remove(literal);
        if(c.isEmpty()) {
          if(verbose) System.out.println("Reached Empty Clause before Unit Prop. Return.");
          encoding.setContainsEmptyClause();
          return true;
        }
      }

    }
    // perform unit propogation
    if(verbose) System.out.println("Encoding before Unit Propogation:\n" + encoding);
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

    if(verbose) System.out.printf("In unitPropogation\n");
    // the set of soft clauses that give rise to a contradiction, any soft clause that is modified
    // on the path to deriving an empty clause
    LinkedList<Clause> inconsistentClauses = new LinkedList<Clause>();
    LinkedList<Clause> queue = encoding.getUnitClauses();
    PriorityQueue<Clause> softClauses = encoding.getSoftClauses();
    PriorityQueue<Clause> hardClauses = encoding.getHardClauses();
    PriorityQueue<Clause> clauses;
    Clause c;
    T l;
    boolean negated;
    if(verbose) System.out.printf("queue.size(): %d\n", queue.size());
    while(!queue.isEmpty()) {
      c = queue.poll();
      l = c.getLiteral();
      //l = lit.get();
      if(verbose) System.out.println("Processing " + l);
      if(c.isSoft()) {
        clauses = hardClauses;
      } else {
        clauses = softClauses;
      }
      for(Clause clause : clauses) {
        //if(!lit.isNegated()) {
        if(!c.isNegated(l)) {
          // if the literal is non negated, then we are looking for negated versions of this literal
          negated = true;
        } else {
          // otherwise, we are looking for non-negated versions of this literal
          negated = false;
        }
        // if lit is a non negated literal, then the following condition checks for negated versions
        // of this literal and removes them from their clauses.
        // if lit is negated, the this condition checks for non negated literals and removes them
        if(clause.contains(l, negated)) {
          // if the clause is soft, add it to inconsistent subsets
          // we only want to save it once, before it has been modified. If the original encoding
          // contains a soft clause with the same literals as this one, then get than clause and
          // add it to the list of inconsistent subsets.
          if(clause.isSoft() && this.originalEncoding.containsSoftClause(clause)) {
            if(verbose) System.out.printf("Adding %s to set of inconsistent clauses.\n", clause);
            inconsistentClauses.add(this.originalEncoding.getSoftClause(clause));
          }
          if(verbose) System.out.printf("Removing %s from Clause %s\n", l, clause);
          clause.remove(l);
          if(clause.isUnitClause()) {
            if(verbose) System.out.printf("Is Unit Clause, adding %s to queue\n", clause);
            queue.add(clause);
          }
          if(clause.isEmpty()) {
            if(verbose) System.out.println("Reaching Empty Clause. Return.");
            encoding.setContainsEmptyClause();
            this.originalEncoding.addInconsistentClauses(inconsistentClauses);
            return encoding;
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
    private PriorityQueue<Clause> softClauses;
    private PriorityQueue<Clause> hardClauses;
    private int partitionSize;
    private boolean containsEmptyClause = false;
    private LinkedList<Clause> inconsistentClauses;

    public MaxSatEncoding(UndirectedGraph<T> graph, ArrayList<ArrayList<Node<T>>> partition) {
      softClauses = new PriorityQueue<Clause>();
      hardClauses = new PriorityQueue<Clause>();

      for(ArrayList<Node<T>> set : partition) {
        if(set.size() > 0) {
          //System.out.println("Adding set: " + set);
          Clause clause = new Clause(set, true);
          //System.out.println("Add Soft Clause: " + clause);
          addClause(clause);
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
            addClause(clause);
          }
        }
      }
    }

    public MaxSatEncoding(MaxSatEncoding other) {
      softClauses = new PriorityQueue<Clause>();
      hardClauses = new PriorityQueue<Clause>();
      for(Clause clause : other.getSoftClauses()) {
        Clause copy = new Clause(clause);
        addClause(copy);
      }
      for(Clause clause : other.getHardClauses()) {
        Clause copy = new Clause(clause);
        addClause(copy);
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
      for(Clause clause : getSoftClauses()) {
        if(clause.isUnitClause()){
          queue.offer(clause);
        }
      }
      for(Clause clause : getHardClauses()) {
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
      for(Clause clause : getSoftClauses()) {
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
        if(verbose) System.out.printf("Removing Clause: %s\n", clause);
        removeSoftClause(clause);
      }
    }

    public void addInconsistentClauses(List<Clause> clauses) {
      inconsistentClauses.addAll(clauses);
    }

    public boolean hasSoftClauses() {
      if(getNumSoftClauses() > 0) {
        return true;
      }
      return false;
    }

    public int getNumSoftClauses() {
      return this.softClauses.size();
    }

    public int getNumHardClauses() {
      return this.hardClauses.size();
    }

    public PriorityQueue<Clause> getSoftClauses() {
      return softClauses;
    }

    public PriorityQueue<Clause> getHardClauses() {
      return hardClauses;
    }

    public void removeSoftClause(Clause clause) {
      softClauses.remove(clause);
    }

    public void addClause(Clause clause) {
      if(clause.isSoft()) {
        softClauses.add(clause);
      } else {
        hardClauses.add(clause);
      }
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
    private ArrayList<Boolean> negated;
    private boolean isSoft;
    private boolean modified = false;
    private boolean tested = false;

    public Clause(ArrayList<Node<T>> vertices, boolean isSoft) {
      this.isSoft = isSoft;
      literals = new ArrayList<T>(vertices.size());
      negated = new ArrayList<Boolean>(vertices.size());
      boolean isNegation;
      for(Node<T> vertex : vertices) {
        if(isSoft) {
          isNegation = false;
          literals.add(vertex.get());
          negated.add(isNegation);
        } else {
          isNegation = true;
          literals.add(vertex.get());
          negated.add(isNegation);
        }
      }
    }

    public Clause(T a, T b, boolean isSoft) {
      this.isSoft = isSoft;
      literals = new ArrayList<T>(2);
      negated = new ArrayList<Boolean>(2);
      boolean isNegation;
      if(isSoft) {
        isNegation = false;
      } else {
        isNegation = true;
      }
      literals.add(a);
      negated.add(isNegation);
      literals.add(b);
      negated.add(isNegation);


    }

    public Clause(Clause other) {
      literals = new ArrayList<T>(other.getNumLiterals());
      negated = new ArrayList<Boolean>(other.getNumLiterals());
      for(int i = 0; i < other.getNumLiterals(); i++) {
        literals.add(other.getLiteral(i));
        negated.add(other.isNegated(i));
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

    public boolean contains(T literal, boolean negated) {
      int index = literals.indexOf(literal);
      if(index != -1) {
        if(negated == this.negated.get(index)) {
          return true;
        }
      }
      return false;
    }


    public void remove(T literal) {
      int index = literals.indexOf(literal);
      if(index != -1) {
        literals.remove(index);
        negated.remove(index);
      }
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

    public T getLiteral(int i) {
      return literals.get(i);
    }

    public Boolean isNegated(int i) {
      return negated.get(i);
    }

    public Boolean isNegated(T lit) {
      return negated.get(literals.indexOf(lit));
    }

    @Override
    public int compareTo(Clause other) {
      return this.getNumLiterals() - other.getNumLiterals();
    }

    public ArrayList<T> getLiterals() {
      return this.literals;
    }

    public ArrayList<Boolean> getNegated() {
      return this.negated;
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
      if(obj.getClass() == Clause.class) {
        Clause other = (Clause) obj;
        if(this.literals.equals(other.getLiterals())
            && this.negated.equals(other.getNegated())) {
          return true;
        }
      }
      return false;
    }

    @Override
    public String toString() {
      String str = "";
      if(!isEmpty()) {
        if(isNegated(0)) {
          str = String.format("!%s",literals.get(0));
        } else {
          str = String.format("%s",literals.get(0));
        }
        for(int i = 1; i < literals.size(); i++) {
          if(isNegated(i)) {
            str = String.format("%s V !%s", str, literals.get(i));
          } else {
            str = String.format("%s V %s", str, literals.get(i));
          }
        }
      }
      return str;
    }
  }
}
