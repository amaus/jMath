package com.aaronpmaus.jMath.graph;

import java.util.List;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Collections;
import java.util.Iterator;
import java.util.NoSuchElementException;

//import com.aaronpmaus.jMath.graph.*;
class MaxSatUB {
  private static boolean verbose = false;

  public static <T extends Comparable<? super T>> int estimateCardinality(UndirectedGraph<T> graph, ArrayList<ArrayList<Node<T>>> partition) {
    // Encode graph into MaxSatEncoding
    int numComplementEdges = graph.size() * (graph.size()-1) - graph.getNumEdges();
    MaxSatEncoding<T> encoding = new MaxSatEncoding<T>(partition.size(), numComplementEdges);
    
    for(ArrayList<Node<T>> set : partition) {
      if(set.size() > 0) {
        //System.out.println("Adding set: " + set);
        Clause<T> clause = new Clause<T>(set, true);
        //System.out.println("Add Soft Clause: " + clause);
        encoding.addSoftClause(clause);
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
          encoding.addHardClause(clause);
        }
      }
    }
    if(verbose) System.out.println("Starting MaxSatUB");
    if(verbose) System.out.println(encoding);
    // perform cardinality test on encoding
    int s = 0;
    while(encoding.hasUntestedSoftClause()) {
      encoding.initializeInconsistentClauses();
      Clause<T> min = encoding.getMinUntestedSoftClause();
      encoding.setAsTested(min);
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
    Iterator<Clause<T>> it = originalEncoding.getSoftClauses().iterator();
    while(it.hasNext()) {
      if(it.next().contains(literal)) {
        it.remove();
      }
    }
    /*for(Clause<T> clause : originalEncoding.getSoftClauses()) {
      if(clause.contains(literal)) {
        originalEncoding.removeSoftClause(clause);
      }
    }*/
    // remove the literal from all hard clauses
    for(Clause<T> clause : originalEncoding.getHardClauses()) {
      if(clause.contains(literal)) {
        if(verbose) System.out.printf("Removing %s from Clause %s\n", literal, clause);
        clause.remove(literal);
        if(clause.isEmpty()) {
          if(verbose) System.out.println("Reached Empty Clause before Unit Prop. Return.");
          originalEncoding.setContainsEmptyClause();
          return true;
        }
      }
    }
    // perform unit propogation
    if(verbose) System.out.println("Encoding before Unit Propogation:\n" + originalEncoding);
    MaxSatEncoding<T> encoding = unitPropogation(originalEncoding);
    if(encoding.containsEmptyClause()) {
      return true;
    } else {
      ArrayList<Clause<T>> clauses = new ArrayList<Clause<T>>(encoding.getSoftClauses());
      for(Clause<T> clause : clauses) {
        if(clause.getNumLiterals() == 2 && clause.isModified()) {
          if(failedClauseDetection(encoding, clause)) {
            return true;
          }
        }
      }
      return false;
    }
    //return false;
  }

  private static <T extends Comparable<? super T>> MaxSatEncoding<T> unitPropogation(MaxSatEncoding<T> originalEncoding) {
    // the set of soft clauses that give rise to a contradiction, any soft clause that is modified
    // on the path to deriving an empty clause
    LinkedList<Clause<T>> inconsistentClauses = new LinkedList<Clause<T>>();
    MaxSatEncoding<T> encoding = new MaxSatEncoding<T>(originalEncoding);
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
      // Otherwise, the unit clause is a hard clause
      } else {
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

  private static class MaxSatEncoding  <T extends Comparable<? super T>> {
    private ArrayList<Clause<T>> softClauses;
    private ArrayList<Clause<T>> hardClauses;
    private ArrayList<Clause<T>> unitClauses;
    private int partitionSize;
    private boolean containsEmptyClause = false;
    private ArrayList<Clause<T>> inconsistentClauses;
    private PriorityQueue<Clause<T>> untestedSoftClauses;

    public MaxSatEncoding(int numSoftClauses, int numHardClauses) {
      softClauses = new ArrayList<Clause<T>>();
      hardClauses = new ArrayList<Clause<T>>();
      inconsistentClauses = new ArrayList<Clause<T>>();
      untestedSoftClauses = new PriorityQueue<Clause<T>>();
    }

    public MaxSatEncoding(MaxSatEncoding<T> other) {
      softClauses = new ArrayList<Clause<T>>(other.getNumSoftClauses());
      hardClauses = new ArrayList<Clause<T>>(other.getNumHardClauses());
      inconsistentClauses = new ArrayList<Clause<T>>();
      unitClauses = new ArrayList<Clause<T>>();
      for(Clause<T> clause : other.getSoftClauses()) {
        Clause<T> copy = new Clause<T>(clause);
        if(copy.isUnitClause()) {
          unitClauses.add(copy);
        }
        softClauses.add(copy);
      }
      for(Clause<T> clause : other.getHardClauses()) {
        Clause<T> copy = new Clause<T>(clause);
        if(copy.isUnitClause()) {
          unitClauses.add(copy);
        }
        hardClauses.add(copy);
      }
    }

    public ArrayList<Clause<T>> getUnitClauses() {
      return unitClauses;
    }

    public void setAsTested(Clause<T> softClause) {
      untestedSoftClauses.remove(softClause);
    }

    public boolean hasUntestedSoftClause() {
      return !untestedSoftClauses.isEmpty();
    }

    public Clause<T> getMinUntestedSoftClause() {
      return untestedSoftClauses.poll();
    }

    public void initializeInconsistentClauses() {
      inconsistentClauses = new ArrayList<Clause<T>>();
    }

    public void removeInconsistentClauses() {
      for(Clause<T> clause : inconsistentClauses) {
        removeSoftClause(clause);
      }
    }

    public void addInconsistentClauses(List<Clause<T>> clauses) {
      inconsistentClauses.addAll(clauses);
    }

    public int getNumSoftClauses() {
      return softClauses.size();
    }

    public int getNumHardClauses() {
      return hardClauses.size();
    }

    public List<Clause<T>> getSoftClauses() {
      return this.softClauses;
    }

    public List<Clause<T>> getHardClauses() {
      return this.hardClauses;
    }

    public void removeSoftClause(Clause<T> clause) {
      softClauses.remove(clause);
      untestedSoftClauses.remove(clause);
    }

    public void addSoftClause(Clause<T> clause) {
      softClauses.add(clause);
      untestedSoftClauses.add(clause);
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
      for(Clause<T> clause : getSoftClauses()) {
        if(clause.equals(other)) {
          return true;
        }
      }
      return false;
    }

    public Clause<T> getSoftClause(Clause<T> other) {
      for(Clause<T> clause : getSoftClauses()) {
        if(clause.equals(other)) {
          return clause;
        }
      }
      throw new NoSuchElementException(String.format("Clause %s not in Encoding", other));
    }

    @Override
    public String toString() {
      String str = "Soft Clauses:\n {";
      //for(Clause<T> soft : getSoftClauses()) {
      for(int i = 0; i < getNumSoftClauses(); i++) {
        Clause<T> soft = this.softClauses.get(i);
        if(i == getNumSoftClauses()-1){
          str += soft;
        } else {
          str += soft + ", ";
        }
      }
      str += "}\nHard Clauses:\n {";
      //for(Clause<T> hard : getHardClauses()) {
      for(int i = 0; i < getNumHardClauses(); i++) {
        Clause<T> hard = this.hardClauses.get(i);
        if(i == getNumHardClauses()-1){
          str += hard;
        } else {
          str += hard + ", ";
        }
      }
      str += "}";
      return str;
    }
  }

  private static class Clause <T extends Comparable<? super T>> implements Comparable<Clause<T>>, Iterable<T> {
    private ArrayList<T> literals;
    private boolean isSoft;
    private boolean modified = false;

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
