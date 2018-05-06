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
    for(Literal literal : clause) {
      if(!failedLiteralDetection(encoding, literal.get())) {
        return false;
      }
    }
    return true;
  }

  private boolean failedLiteralDetection(MaxSatEncoding encoding, T literal) {
    // remove all soft clauses containing literal
    encoding = new MaxSatEncoding(encoding);
    Iterator<Clause> it = encoding.getClauses().iterator();
    boolean negated;
    while(it.hasNext()) {
      Clause c = it.next();
      negated = false;
      // if the clause contains the nonnegated form of the literal, remove the clause
      if(c.contains(literal, negated)) {
        it.remove();
      }
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
    Clause c;
    Literal lit;
    T l;
    boolean negated;
    if(verbose) System.out.printf("queue.size(): %d\n", queue.size());
    while(!queue.isEmpty()) {
      c = queue.poll();
      lit = c.getLiteral();
      l = lit.get();
      if(verbose) System.out.println("Processing " + l);
      for(Clause clause : encoding.getClauses()) {
        if(!lit.isNegated()) {
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
    private PriorityQueue<Clause> clauses;
    private int partitionSize;
    private boolean containsEmptyClause = false;
    private LinkedList<Clause> inconsistentClauses;
    private int numSoftClauses;

    public MaxSatEncoding(UndirectedGraph<T> graph, ArrayList<ArrayList<Node<T>>> partition) {
      clauses = new PriorityQueue<Clause>();
      numSoftClauses = 0;

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
      clauses = new PriorityQueue<Clause>();
      for(Clause clause : other.getClauses()) {
        Clause copy = new Clause(clause);
        addClause(copy);
      }
      numSoftClauses = other.getNumSoftClauses();
    }

    public PriorityQueue<Clause> getClauses() {
      return clauses;
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
      for(Clause clause : clauses) {
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
      for(Clause clause : clauses) {
        if(clause.isSoft() && !clause.isTested()) {
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
      if(numSoftClauses > 0) {
        return true;
      }
      return false;
    }

    public int getNumSoftClauses() {
      int num = 0;
      for(Clause c : clauses) {
        if(c.isSoft()) {
          num++;
        }
      }
      return num;
    }

    public int getNumHardClauses() {
      int num = 0;
      for(Clause c : clauses) {
        if(!c.isSoft()) {
          num++;
        }
      }
      return num;
    }

    public Collection<Clause> getSoftClauses() {
      LinkedList<Clause> softClauses = new LinkedList<Clause>();
      for(Clause c : clauses) {
        if(c.isSoft()) {
          softClauses.add(c);
        }
      }
      return softClauses;
    }

    public List<Clause> getHardClauses() {
      LinkedList<Clause> hardClauses = new LinkedList<Clause>();
      for(Clause c : clauses) {
        if(!c.isSoft()) {
          hardClauses.add(c);
        }
      }
      return hardClauses;
    }

    public void removeSoftClause(Clause clause) {
      clauses.remove(clause);
      numSoftClauses--;
    }

    public void addClause(Clause clause) {
      clauses.add(clause);
      if(clause.isSoft()) {
        numSoftClauses++;
      }
    }

    public void setContainsEmptyClause() {
      this.containsEmptyClause = true;
    }

    public boolean containsEmptyClause() {
      return this.containsEmptyClause;
    }

    public boolean containsSoftClause(Clause other) {
      return this.clauses.contains(other);
    }

    public Clause getSoftClause(Clause other) {
      for(Clause clause : this.clauses) {
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

  private class Clause implements Comparable<Clause>, Iterable<Literal> {
    private ArrayList<Literal> literals;
    //private ArrayList<T> lits;
    //private ArrayList<Boolean> negated;
    private boolean isSoft;
    private boolean modified = false;
    private boolean tested = false;

    public Clause(ArrayList<Node<T>> vertices, boolean isSoft) {
      this.isSoft = isSoft;
      literals = new ArrayList<Literal>(vertices.size());
      boolean isNegation;
      for(Node<T> vertex : vertices) {
        if(isSoft) {
          isNegation = false;
          literals.add(new Literal(vertex.get(), isNegation));
        } else {
          isNegation = true;
          literals.add(new Literal(vertex.get(), isNegation));
        }
      }
    }

    public Clause(T a, T b, boolean isSoft) {
      this.isSoft = isSoft;
      literals = new ArrayList<Literal>(2);
      boolean isNegation;
      if(isSoft) {
        isNegation = false;
      } else {
        isNegation = true;
      }
      literals.add(new Literal(a, isNegation));
      literals.add(new Literal(b, isNegation));


    }

    public Clause(Clause other) {
      literals = new ArrayList<Literal>(other.getNumLiterals());
      for(Literal lit : other) {
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
      Iterator<Literal> it = iterator();
      while(it.hasNext()) {
        if(it.next().equals(literal)) {
          return true;
        }
      }
      return false;
    }

    public boolean contains(T literal, boolean negated) {
      Iterator<Literal> it = iterator();
      while(it.hasNext()) {
        Literal lit = it.next();
        if(lit.get().equals(literal) && lit.isNegated() == negated) {
          return true;
        }
      }
      return false;
    }


    public void remove(T literal) {
      Iterator<Literal> it = iterator();
      while(it.hasNext()) {
        if(it.next().get().equals(literal)) {
          it.remove();
        }
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
    public Literal getLiteral() {
      return literals.get(0);
    }

    @Override
    public int compareTo(Clause other) {
      return this.getNumLiterals() - other.getNumLiterals();
    }

    public ArrayList<Literal> getLiterals() {
      return this.literals;
    }

    @Override
    public Iterator<Literal> iterator() {
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
      if(!isEmpty()) {
        str = String.format("%s",literals.get(0));
        for(int i = 1; i < literals.size(); i++) {
          str = String.format("%s V %s", str, literals.get(i));
        }
      }
      return str;
    }
  }

  private class Literal {
    private T element;
    private boolean isNegation;

    public Literal(T element, boolean isNegation) {
      this.element = element;
      this.isNegation = isNegation;
    }

    public T get() {
      return element;
    }

    public boolean isNegated() {
      return isNegation;
    }

    @Override
    public String toString() {
      if(isNegated()) {
        return String.format("!%s",get());
      }
      return String.format("%s",get());
    }

    @Override
    public int hashCode() {
      return get().hashCode();
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean equals(Object obj) {
      //if(obj instanceof Literal) {
      if(obj.getClass() == Literal.class) {
        Literal other = (Literal) obj;
        if(this.get().equals(other.get())
            && (this.isNegated() == other.isNegated())) {
          return true;
        }
      }
      return false;
    }
  }
}
