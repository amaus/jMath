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

class MaxSatUB {
  private boolean verbose = false;
  private MaxSatEncoding originalEncoding;
  private int partitionSize;
  private int maxLiteral = 0;
  private int firstRelaxationLit;
  private ArrayList<Clause> inconsistentClauses;

  public MaxSatUB(UndirectedGraph<Integer> graph) {
    ArrayList<ArrayList<Node<Integer>>> partition = partitionGraph(graph);
    this.partitionSize = partition.size();
    this.originalEncoding = new MaxSatEncoding(graph, partition);
    this.firstRelaxationLit = maxLiteral + 1;
    //inconsistentClauses = new ArrayList<Clause>(originalEncoding.getNumSoftClauses);
  }

  public MaxSatUB(UndirectedGraph<Integer> graph, ArrayList<ArrayList<Node<Integer>>> partition) {
    this.partitionSize = partition.size();
    this.originalEncoding = new MaxSatEncoding(graph, partition);
    this.firstRelaxationLit = maxLiteral + 1;
    //inconsistentClauses = new ArrayList<Clause>(originalEncoding.getNumSoftClauses);
  }

  public int estimateCardinality() {
    if(verbose) System.out.println("Starting MaxSatUB");
    // perform cardinality test on encoding
    int s = 0;
    while(originalEncoding.hasSoftClauses()) {
      if(verbose) System.out.printf("Working on encoding\n%s\n",originalEncoding);
      originalEncoding.initializeInconsistentClauses();
      //inconsistentClauses.clear();
      Clause min = originalEncoding.getMinUntestedSoftClause();
      // if min is null, there are no untested Soft Clauses
      if(min == null) {
        if(verbose) System.out.println("No More Untested Soft Clauses");
        break;
      }
      min.setAsTested();
      if(verbose) System.out.printf("Starting round of Failed Clause Detection\n");
      if(verbose) System.out.println("Testing min untested Clause " + min);
      if(failedClauseDetection(originalEncoding, min)) {
        if(verbose) System.out.printf("Failed Clause Detected: %s\n", min);
        if(verbose) System.out.printf("Adding Relaxation Variables to Inconsistent Set Clauses:\n");
        //originalEncoding.removeSoftClause(min);
        //originalEncoding.removeInconsistentClauses();
        List<Clause> inconsistentClauses = originalEncoding.getInconsistentClauses();
        ArrayList<Integer> newLiterals = new ArrayList<Integer>();
        inconsistentClauses.add(originalEncoding.getSoftClause(min));
        for(Clause clause : inconsistentClauses) {
          Integer newLit = maxLiteral + 1;
          maxLiteral++;
          newLiterals.add(newLit);
          if(verbose) System.out.printf("Adding %s to Clause %s.\n", newLit, clause);
          clause.addLiteral(newLit, false);
        }
        if(verbose) System.out.printf("Adding One Hot Constraint:\n");
        originalEncoding.addAtMostOneConstraint(newLiterals);
        s++;
      } else {
        if(verbose) System.out.printf("Clause %s NOT Failed, try next untested soft clause.\n", min);
      }
    }
    int cardinality = this.partitionSize - s;
    if(verbose) System.out.printf("CARDINALITY ESTIMATE %d - %d: %d\n", partitionSize, s, cardinality);
    if(verbose) System.out.println("#########################");
    //System.exit(0);
    return cardinality;
  }

  private boolean failedClauseDetection(MaxSatEncoding encoding, Clause clause) {
    if(verbose) System.out.printf("*In Failed Clause Detection, testing Clause %s\n", clause);
    for(Integer literal : clause) {
      if(verbose) System.out.printf("-Testing literal %s in %s\n", literal, clause);
      // Save the state of the encoding
      // instead of instantiating a new encoding every time failedLiteralDetection is run,
      // save the current state before and restore it after
      encoding.saveState();
      if(verbose) System.out.println("-Encoding Before Failed Lit Detection:\n" + encoding);
      if(verbose) System.out.println(encoding.getBkupSoftClausesString());
      boolean literalFailed = failedLiteralDetection(encoding, literal);
      //if(verbose) System.out.println(encoding.getBkupSoftClausesString());
      //if(verbose) System.out.println(encoding.getBkupHardClausesString());
      //if(verbose) System.out.println(encoding.getBkupConstraintsString());
      encoding.restoreState();
      if(verbose) System.out.println("-Encoding After Restore: " + encoding);
      if(!literalFailed) {
        if(verbose) System.out.printf("-Literal %s not failed, Clause %s not failed, return\n", literal, clause);
        return false;
      }
      if(verbose) System.out.printf("-Literal %s Failed\n", literal);
    }
    if(verbose) System.out.printf("-All Literals Failed, Clause %s Failed, return\n", clause);
    return true;
  }

  private boolean failedLiteralDetection(MaxSatEncoding encoding, Integer literal) {
    if(verbose) System.out.printf("**In Failed Literal Detection, for literal %s\n", literal);
    //encoding = new MaxSatEncoding(encoding);
    boolean negated;
    Clause c;
    // remove all soft clauses containing literal
    Iterator<Clause> it = encoding.getSoftClauses().iterator();
    while(it.hasNext()) {
      c = it.next();
      // if the clause contains the nonnegated form of the literal, remove the clause
      if(c.contains(literal)) {
        if(verbose) System.out.printf("--Removing Clause %s from encoding\n", c);
        it.remove();
      }
    }
    it = encoding.getHardClauses().iterator();
    while(it.hasNext()) {
      c = it.next();
      // if the clause contains the negated form of the literal, remove the literal
      if(c.contains(literal)) {
        if(verbose) System.out.printf("--Removing %s from Clause %s\n", literal, c);
        c.remove(literal);
        if(c.isEmpty()) {
          if(verbose) System.out.println("--Reached Empty Clause before Unit Prop. Return.");
          encoding.setContainsEmptyClause();
          return true;
        }
      }
    }
    // perform unit propogation
    if(verbose) System.out.println("--Perform Unit Propogation on encoding:\n" + encoding);
    encoding = unitPropogation(encoding);
    if(encoding.containsEmptyClause()) {
      return true;
    } /*else {
      for(Clause clause : encoding.getNewBinaryClauses()) {
        if(verbose) System.out.printf("--Calling Failed Clause Detection on new Binary Clause %s\n", clause);
        if(verbose) System.out.printf("--encoding:\n %s\n", encoding);
        if(failedClauseDetection(encoding, clause)) {
          if(verbose) System.out.printf("--Clause %s failed, return\n", clause);
          return true;
        }
      }
      if(verbose) System.out.printf("--No new Binary Clauses Failed, return\n");
      return false;
    }*/
    return false;
  }

  private MaxSatEncoding unitPropogation( MaxSatEncoding encoding) {

    if(verbose) System.out.printf("***In unitPropogation\n");
    // the set of soft clauses that give rise to a contradiction, any soft clause that is modified
    // on the path to deriving an empty clause
    ArrayList<Clause> inconsistentClauses = new ArrayList<Clause>();
    LinkedList<Clause> queue = encoding.getUnitClauses();
    ArrayList<Clause> softClauses = encoding.getSoftClauses();
    ArrayList<Clause> hardClauses = encoding.getHardClauses();
    //ArrayList<Clause> relaxationClauses = encoding.getRelaxationClauses();
    Collection<Clause> clauses = null;
    Clause c;
    Integer l;
    boolean litIsNegated;
    Clause clause;
    Iterator<Clause> it;
    if(verbose) System.out.printf("---queue.size(): %d, %s\n", queue.size(), queue);
    while(!queue.isEmpty()) {
      c = queue.poll();
      l = c.getLiteral();
      litIsNegated = c.isNegated(l);
      //l = lit.get();
      if(verbose) System.out.printf("---Processing %s\n", c);
      //if(verbose) System.out.printf("---Is Negated %b\n", litIsNegated);
      if(c.isHard() || c.isRelaxation()) { // c is hard
        clauses = softClauses;
        if(litIsNegated) {
          for(Clause constraint : encoding.getAtMostOneConstraints()) {
            if(constraint.contains(l)) {
              if(verbose) System.out.printf("---Removing %s from Constraint Clause %s\n", l, constraint);
              constraint.remove(l);
              if(constraint.isUnitClause()) {
                if(!queue.contains(constraint)) {
                  if(verbose) System.out.printf("---Constraint Is Unit Clause, adding %s to queue\n", constraint);
                  queue.add(constraint);
                }
              }
              if(constraint.isEmpty()) {
                if(verbose) System.out.println("---Constraint Reaching Empty Clause. Return.");
                encoding.setContainsEmptyClause();
                this.originalEncoding.addInconsistentClauses(inconsistentClauses);
                return encoding;
              }
            }
          }
        }
        /*it = hardClauses.iterator();
        while(it.hasNext()) {
          clause = it.next();
          if(clause.contains(l, litIsNegated)) {
            // if the clause contains this literal in its negated state, then remove this clause
            // from the set of clauses. The clause has become true. The goal is to reduce the
            // search space as we go.
            if(verbose) System.out.printf("Removing Clause %s, contains %s\n", clause, c);
            it.remove();
          }
        }*/
      } else { // c is soft or the atMostOneClause
        // if l is a relaxation variable, we want to loop through the relaxation hard clauses
        /*if(l >= firstRelaxationLit) {
          clauses = relaxationClauses;
        } else { // otherwise we want to loop through the regular hard clauses
          clauses = hardClauses;
        }*/
        clauses = hardClauses;
      }
      it = clauses.iterator();
      //for(Clause clause : clauses) {
      while(it.hasNext()){
        clause = it.next();
        // if lit is a non negated literal, then the following condition checks for negated versions
        // of this literal and removes them from their clauses.
        // if lit is negated, the this condition checks for non negated literals and removes them
        if(clause.contains(l, !litIsNegated)) {
          // if the clause is soft, add it to inconsistent subsets
          // we only want to save it once, before it has been modified. If the original encoding
          // contains a soft clause with the same literals as this one, then get than clause and
          // add it to the list of inconsistent subsets.
          if(clause.isSoft() && this.originalEncoding.containsBkupSoftClause(clause)) {
            if(verbose) System.out.printf("---Adding %s to set of inconsistent clauses.\n", clause);
            inconsistentClauses.add(this.originalEncoding.getBkupSoftClause(clause));
          }
          if(verbose) System.out.printf("---Removing %s from Clause %s\n", l, clause);
          clause.remove(l);
          if(clause.isUnitClause()) {
            if(verbose) System.out.printf("---Is Unit Clause, adding %s to queue\n", clause);
            if(!queue.contains(clause)) {
              queue.add(clause);
            }
          }
          if(clause.isEmpty()) {
            if(verbose) System.out.println("---Reaching Empty Clause. Return.");
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
  private ArrayList<ArrayList<Node<Integer>>> partitionGraph(UndirectedGraph<Integer> g) {
    //System.out.println("## Calculating indSetUB");
    // get a list of nodes and sort them in descending order by degree
    List<Node<Integer>> descendingDegreeNodes = new ArrayList<Node<Integer>>(g.getNodes());
    Collections.sort(descendingDegreeNodes, Collections.reverseOrder());
    int maxColorNumber = 0;
    // initialize color sets
    // The index of the outer ArrayList is k and the inner arraylists hold all the nodes that belong
    // to that color k.
    ArrayList<ArrayList<Node<Integer>>> colorSets = new ArrayList<ArrayList<Node<Integer>>>();
    // initialize the first two color sets (k = 0,1)
    colorSets.add(new ArrayList<Node<Integer>>());
    colorSets.add(new ArrayList<Node<Integer>>());
    for(Node<Integer> node : descendingDegreeNodes) {
      int k = 0;
      // find the lowest k where neighbors and the set of nodes in colorSets[k] share no nodes
      // in common
      while(!Collections.disjoint(node.getNeighbors(), colorSets.get(k))) {
        k++;
      }
      if(k > maxColorNumber) {
        maxColorNumber = k;
        // initialize and add the next color set
        colorSets.add(new ArrayList<Node<Integer>>());
      }
      colorSets.get(k).add(node);
    }
    // return the number of colors assigned
    //return maxColorNumber + 1;
    return colorSets;
  }

  private class MaxSatEncoding {
    private ArrayList<Clause> softClauses;
    private ArrayList<Clause> hardClauses;
    private ArrayList<Clause> atMostOneConstraints;
    private boolean containsEmptyClause = false;
    private ArrayList<Clause> bkupSoftClauses;
    private ArrayList<Clause> bkupHardClauses;
    private ArrayList<Clause> bkupAtMostOneConstraints;
    private ArrayList<Clause> inconsistentClauses;

    public MaxSatEncoding(UndirectedGraph<Integer> graph, ArrayList<ArrayList<Node<Integer>>> partition) {
      softClauses = new ArrayList<Clause>();
      hardClauses = new ArrayList<Clause>();
      atMostOneConstraints = new ArrayList<Clause>();
      bkupSoftClauses = new ArrayList<Clause>();
      bkupHardClauses = new ArrayList<Clause>();
      bkupAtMostOneConstraints = new ArrayList<Clause>();
      inconsistentClauses = new ArrayList<Clause>(getNumSoftClauses());

      for(ArrayList<Node<Integer>> set : partition) {
        if(set.size() > 0) {
          Clause clause = new Clause(set);
          clause.setAsSoft();
          addClause(clause);
        }
      }
      List<Integer> elements = graph.getElements();
      for(int i = 0; i < graph.size(); i++) {
        Integer vertex = elements.get(i);
        for(int j = i+1; j < graph.size(); j++) {
          Integer neighbor = elements.get(j);
          if(vertex != neighbor && !graph.hasEdge(vertex, neighbor)) {
            Clause clause = new Clause(vertex, neighbor);
            clause.setAsHard();
            addClause(clause);
          }
        }
      }
    }

    public MaxSatEncoding(MaxSatEncoding other) {
      softClauses = new ArrayList<Clause>();
      hardClauses = new ArrayList<Clause>();
      atMostOneConstraints = new ArrayList<Clause>();
      bkupSoftClauses = new ArrayList<Clause>();
      bkupHardClauses = new ArrayList<Clause>();
      bkupAtMostOneConstraints = new ArrayList<Clause>();
      inconsistentClauses = new ArrayList<Clause>(getNumSoftClauses());

      for(Clause clause : other.getSoftClauses()) {
        Clause copy = new Clause(clause);
        addClause(copy);
      }
      for(Clause clause : other.getHardClauses()) {
        Clause copy = new Clause(clause);
        addClause(copy);
      }
      for(Clause clause : other.getAtMostOneConstraints()) {
        Clause copy = new Clause(clause);
        atMostOneConstraints.add(copy);
      }
    }

    public void saveState() {
      bkupSoftClauses.clear();
      bkupHardClauses.clear();
      bkupAtMostOneConstraints.clear();
      for(Clause clause : softClauses) {
        bkupSoftClauses.add(new Clause(clause));
      }
      for(Clause clause : hardClauses) {
        bkupHardClauses.add(new Clause(clause));
      }
      for(Clause clause : atMostOneConstraints) {
        bkupAtMostOneConstraints.add(new Clause(clause));
      }
    }

    public void restoreState() {
      ArrayList<Clause> temp;
      ArrayList<Clause> temp2;
      // swap softClauses and bkupSoftClauses
      temp = softClauses;
      softClauses = bkupSoftClauses;
      bkupSoftClauses = temp;
      // swap hardClauses and bkupHardClauses
      temp2 = hardClauses;
      hardClauses = bkupHardClauses;
      bkupHardClauses = temp2;
      // swap atMostOneConstraints and bkupAtMostOneConstraints
      temp2 = atMostOneConstraints;
      atMostOneConstraints = bkupAtMostOneConstraints;
      bkupAtMostOneConstraints = temp2;
      containsEmptyClause = false;
    }

    public String getClausesString(Collection<Clause> clauses) {
      int i = 0;
      String str = "{";
      for(Clause clause : clauses) {
        if(i == clauses.size() - 1) {
          str += clause;
        } else {
          str += clause + ", ";
        }
        i++;
      }
      str += "}";
      return str;
    }

    public String getBkupSoftClausesString() {
      String str = "Bkup Soft Clauses:\n" + getClausesString(bkupSoftClauses);
      return str;
    }

    public String getBkupHardClausesString() {
      String str = "Bkup Hard Clauses:\n" + getClausesString(bkupHardClauses);
      return str;
    }

    public String getBkupConstraintsString() {
      String str = "Bkup At Most One Constraints:\n" + getClausesString(bkupAtMostOneConstraints);
      return str;
    }

    public List<Clause> getAtMostOneConstraints() {
      return atMostOneConstraints;
    }

    public int getNumAtMostOneConstraints() {
      return atMostOneConstraints.size();
    }

    public boolean hasAtMostOneConstraints() {
      if(atMostOneConstraints.isEmpty()) {
        return false;
      }
      return true;
    }

    public void addAtMostOneConstraint(ArrayList<Integer> lits) {
      Clause c;
      for(int i = 0; i < lits.size(); i++) {
        for(int j = i+1; j < lits.size(); j++) {
          c = new Clause();
          c.addLiteral(lits.get(i), true);
          c.addLiteral(lits.get(j), true);
          c.setAsRelaxation();
          addClause(c);
        }
      }
      c = new Clause();
      for(Integer lit : lits) {
        c.addLiteral(lit, false);
      }
      if(verbose) System.out.printf("Adding atMostOneConstraint %s\n", c);
      atMostOneConstraints.add(c);
    }

    public ArrayList<Clause> getNewBinaryClauses() {
      ArrayList<Clause> clauses = new ArrayList<Clause>();
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
      for(Clause clause : getAtMostOneConstraints()) {
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
      inconsistentClauses.clear();
    }

    public void removeInconsistentClauses() {
      for(Clause clause : inconsistentClauses) {
        if(verbose) System.out.printf("Removing Clause: %s\n", clause);
        removeSoftClause(clause);
      }
    }

    public List<Clause> getInconsistentClauses() {
      return this.inconsistentClauses;
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

    public ArrayList<Clause> getSoftClauses() {
      return softClauses;
    }

    public ArrayList<Clause> getHardClauses() {
      return hardClauses;
    }

    public ArrayList<Clause> getRelaxationClauses() {
      ArrayList<Clause> clauses = new ArrayList<Clause>();
      for(Clause hard : getHardClauses()) {
        if(hard.isRelaxation()) {
          clauses.add(hard);
        }
      }
      return clauses;
    }

    public void removeSoftClause(Clause clause) {
      softClauses.remove(clause);
    }

    public void addClause(Clause clause) {
      if(clause.isSoft()) {
        softClauses.add(clause);
      } else if(clause.isHard()) {
        hardClauses.add(clause);
      } else if(clause.isRelaxation()) {
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

    public boolean containsBkupSoftClause(Clause clause) {
      return bkupSoftClauses.contains(clause);
    }

    public Clause getBkupSoftClause(Clause other) {
      for(Clause clause : this.bkupSoftClauses) {
        if(clause.equals(other)) {
          return clause;
        }
      }
      throw new NoSuchElementException(String.format("Clause %s not in Encoding", other));
    }

    @Override
    public String toString() {
      String str = "###\nSoft Clauses:\n {";
      int i = 0;
      for(Clause soft : getSoftClauses()) {
        String softStr = "";
        if(soft.isTested()) {
          softStr = String.format("%s*",soft);
        } else {
          softStr = String.format("%s",soft);
        }
        if(i == getNumSoftClauses()-1){
          str += softStr;
        } else {
          str += softStr + ", ";
        }
        i++;
      }
      str += "}\nHard Clauses:\n {";
      i = 0;
      for(Clause hard : getHardClauses()) {
        if(i == getNumHardClauses() - 1) {
          str += hard;
        } else {
          str += hard + ", ";
        }
        i++;
      }
      str += "}\nAt Most One Constraints:\n {";
      i = 0;
      for(Clause hard : getAtMostOneConstraints()) {
        if(i == getNumAtMostOneConstraints()-1){
          str += hard;
        } else {
          str += hard + ", ";
        }
        i++;
      }
      str += "}\n###";
      return str;
    }
  }

  private class Clause implements Comparable<Clause>, Iterable<Integer> {
    private ArrayList<Integer> literals;
    private ArrayList<Boolean> negated;
    private boolean isSoft;
    private boolean isHard;
    private boolean isRelaxation;
    private boolean modified = false;
    private boolean tested = false;

    public Clause( ) {
      literals = new ArrayList<Integer>();
      negated = new ArrayList<Boolean>();
    }

    public Clause(ArrayList<Node<Integer>> vertices) {
      literals = new ArrayList<Integer>(vertices.size());
      negated = new ArrayList<Boolean>(vertices.size());
      boolean isNegation = false;
      for(Node<Integer> vertex : vertices) {
        literals.add(vertex.get());
        negated.add(isNegation);
        if(vertex.get() > maxLiteral) {
          maxLiteral = vertex.get();
        }
      }
    }

    public Clause(Integer a, Integer b) {
      literals = new ArrayList<Integer>(2);
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
      if(a > maxLiteral) {
        maxLiteral = a;
      }
      if(b > maxLiteral) {
        maxLiteral = b;
      }
    }

    public Clause(Clause other) {
      literals = new ArrayList<Integer>(other.getNumLiterals());
      negated = new ArrayList<Boolean>(other.getNumLiterals());
      for(int i = 0; i < other.getNumLiterals(); i++) {
        literals.add(other.getLiteral(i));
        negated.add(other.isNegated(i));
      }
      this.isSoft = other.isSoft();
      this.isHard = other.isHard();
      this.isRelaxation = other.isRelaxation();
      this.tested = other.isTested();
    }

    public void setAsSoft() {
      this.isSoft = true;
      this.isHard = false;
      this.isRelaxation = false;
    }

    public void setAsHard() {
      this.isSoft = false;
      this.isHard = true;
      this.isRelaxation = false;
    }

    public void setAsRelaxation() {
      this.isSoft = false;
      this.isHard = false;
      this.isRelaxation = true;
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

    public boolean isHard() {
      return isHard;
    }
    public boolean isRelaxation() {
      return isRelaxation;
    }

    public boolean isTested() {
      return tested;
    }

    public void setAsTested() {
      this.tested = true;
    }

    public boolean contains(Integer literal) {
      return literals.contains(literal);
    }

    public boolean contains(Integer literal, boolean negated) {
      int index = literals.indexOf(literal);
      if(index != -1) {
        if(negated == this.negated.get(index)) {
          return true;
        }
      }
      return false;
    }

    public void remove(Integer literal) {
      int index = literals.indexOf(literal);
      if(index != -1) {
        literals.remove(index);
        negated.remove(index);
      }
      this.modified = true;
    }

    public void addLiteral(Integer literal, boolean isNegated) {
      literals.add(literal);
      negated.add(isNegated);
    }

    public boolean isModified() {
      return this.modified;
    }

    /**
    * Only call if this clause is a unit clause. Returns the remaining literal.
    * @return the remaining literal in a unit clause
    */
    public Integer getLiteral() {
      return literals.get(0);
    }

    public Integer getLiteral(int i) {
      return literals.get(i);
    }

    public Boolean isNegated(int i) {
      return negated.get(i);
    }

    public Boolean isNegated(Integer lit) {
      return negated.get(literals.indexOf(lit));
    }

    @Override
    public int compareTo(Clause other) {
      return this.getNumLiterals() - other.getNumLiterals();
    }

    public ArrayList<Integer> getLiterals() {
      return this.literals;
    }

    public ArrayList<Boolean> getNegated() {
      return this.negated;
    }

    @Override
    public Iterator<Integer> iterator() {
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
            str = String.format("%s + !%s", str, literals.get(i));
          } else {
            str = String.format("%s + %s", str, literals.get(i));
          }
        }
      }
      str = String.format("(%s)",str);
      return str;
    }
  }
}
