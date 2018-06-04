package com.aaronpmaus.jMath.graph;

import java.util.Collection;
import java.util.List;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Date;

public class MaxSatUB {
  private boolean verbose = false;
  private int partitionSize;
  private int maxLiteral = 0;
  private int firstRelaxationLit;
  public static long totalTimeRunning = 0;
  public static long maxRuntime = 0;
  public static long timeConstructingEncodings = 0;
  private UndirectedGraph<Integer> originalGraph;
  // the hard clauses are represent the edges in the complement of the graph. Once at the beginning,
  // build all possible hard clauses. Then when the encoding is constructed, pull from this list.
  // This will avoid MANY clause instantiations (and hopefully garbage collections) as well.
  public ArrayList<Clause> allHardClauses;

  public MaxSatUB(UndirectedGraph<Integer> graph) {
    totalTimeRunning = 0;
    maxRuntime = 0;
    timeConstructingEncodings = 0;
    this.originalGraph = graph;
    /*int numComplementEdges = (graph.size() * (graph.size() - 1))/2; // max num edges
    numComplementEdges = numComplementEdges - graph.numEdges();
    allHardClauses = new ArrayList<Clause>(numComplementEdges);
    List<Integer> elements = graph.getElements();
    for(int i = 0; i < graph.size(); i++) {
      Integer vertex = elements.get(i);
      for(int j = i+1; j < graph.size(); j++) {
        Integer neighbor = elements.get(j);
        if(vertex != neighbor && !graph.hasEdge(vertex, neighbor)) {
          Clause clause = new Clause(vertex, neighbor, allHardClauses.size());
          clause.setAsHard();
          allHardClauses.add(clause);
        }
      }
    }
    System.out.println("Size of All Hard Clauses: " + allHardClauses.size());
    */
  }

  /*public int estimateCardinality() {
    ArrayList<ArrayList<Node<Integer>>> partition = partitionGraph(this.originalGraph);
    this.partitionSize = partition.size();
    MaxSatEncoding encoding = new MaxSatEncoding(partition);

    return estimateCardinality(encoding);
  }*/

  public int estimateCardinality(UndirectedGraph<Integer> graph) {
    ArrayList<ArrayList<Node<Integer>>> partition = partitionGraph(graph);
    this.partitionSize = partition.size();
    MaxSatEncoding encoding = new MaxSatEncoding(graph, partition);

    return estimateCardinality(encoding);
  }

  public int estimateCardinality(UndirectedGraph<Integer> graph, ArrayList<ArrayList<Node<Integer>>> partition) {
    this.partitionSize = partition.size();
    MaxSatEncoding encoding = new MaxSatEncoding(graph, partition);

    return estimateCardinality(encoding);
  }

  public int estimateCardinality(MaxSatEncoding encoding) {
    long start = new Date().getTime();
    this.firstRelaxationLit = maxLiteral + 1;

    if(verbose) System.out.println("Starting MaxSatUB");
    // perform cardinality test on encoding
    int s = 0;
    ArrayList<Integer> newLiterals = new ArrayList<Integer>();
    while(encoding.hasSoftClauses()) {
      if(verbose) System.out.printf("Working on encoding\n%s\n", encoding);
      encoding.initializeInconsistentClauses();
      //inconsistentClauses.clear();
      Clause min = encoding.getMinUntestedSoftClause();
      // if min is null, there are no untested Soft Clauses
      if(min == null) {
        if(verbose) System.out.println("No More Untested Soft Clauses");
        break;
      }
      min.setAsTested();
      if(verbose) System.out.printf("Starting round of Failed Clause Detection\n");
      if(verbose) System.out.println("Testing min untested Clause " + min);
      if(topLevelFailedClauseDetection(encoding, min)) {
        if(verbose) System.out.printf("Failed Clause Detected: %s\n", min);
        if(verbose) System.out.printf("Adding Relaxation Variables to Inconsistent Set Clauses:\n");
        //encoding.removeClause(min);
        //encoding.removeInconsistentClauses();
        List<Clause> inconsistentClauses = encoding.getInconsistentClauses();
        newLiterals.clear();
        inconsistentClauses.add(encoding.getSoftClause(min));
        for(Clause clause : inconsistentClauses) {
          Integer newLit = maxLiteral + 1;
          maxLiteral++;
          newLiterals.add(newLit);
          if(verbose) System.out.printf("Adding %s to Clause %s.\n", newLit, clause);
          clause.addLiteral(newLit, false);
        }
        if(verbose) System.out.printf("Adding One Hot Constraint:\n");
        encoding.addAtMostOneConstraint(newLiterals);
        s++;
      } else {
        if(verbose) System.out.printf("Clause %s NOT Failed, try next untested soft clause.\n", min);
      }
    }
    int cardinality = this.partitionSize - s;
    if(verbose) System.out.printf("CARDINALITY ESTIMATE %d - %d: %d\n", partitionSize, s, cardinality);
    if(verbose) System.out.println("#########################");
    //System.exit(0);
    long end = new Date().getTime();
    long time = end - start;
    if(time > maxRuntime) {
      maxRuntime = time;
    }
    totalTimeRunning += time;
    return cardinality;
  }

  // How we handle state depends on the level we are operating at. If we are checking clauses
  // in the original state of the encoding, after each literal is checked, return to the initial
  // state, everything enabled.
  // The other case is if we are recursively calling failedClauseDetection. That happens when
  // we are testing new binary clauses. After each literal is tested, we don't want to return
  // to the fully enabled state, but to what ever state the encoding was in before that test.
  private boolean topLevelFailedClauseDetection(MaxSatEncoding encoding, Clause clause) {
    if(verbose) System.out.printf("*In Failed Clause Detection, testing Clause %s\n", clause);
    for(Integer literal : clause) {
      if(verbose) System.out.printf("-Testing literal %s in %s\n", literal, clause);
      if(verbose) System.out.println("-Encoding Before Failed Lit Detection:\n" + encoding);
      boolean literalFailed = failedLiteralDetection(encoding, literal);
      encoding.enableAll();
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

  private boolean failedClauseDetection(MaxSatEncoding encoding, Clause clause) {
    if(verbose) System.out.printf("*In Failed Clause Detection, testing Clause %s\n", clause);
    for(Integer literal : clause) {
      if(verbose) System.out.printf("-Testing literal %s in %s\n", literal, clause);
      // Save the state of the encoding
      // instead of instantiating a new encoding every time failedLiteralDetection is run,
      // save the current state before and restore it after
      encoding.saveState();
      if(verbose) System.out.println("-Encoding Before Failed Lit Detection:\n" + encoding);
      boolean literalFailed = failedLiteralDetection(encoding, literal);
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
    boolean negated;
    // remove all soft clauses containing literal
    for(Clause c : encoding.getSoftClauses()) {
      if(!encoding.contains(c)) {
        continue;
      }
      // if the clause contains the nonnegated form of the literal, remove the clause
      if(c.contains(literal)) {
        if(verbose) System.out.printf("--Removing Clause %s from encoding\n", c);
        encoding.removeClause(c);
        if(verbose) System.out.println("Soft Mask: " + encoding.getSoftMask());
        if(verbose) System.out.println("--Encoding\n"+ encoding);
      }
    }
    for(Clause c : encoding.getHardClauses()) {
      if(!encoding.contains(c)) {
        continue;
      }
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
    unitPropogation(encoding);
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

  private void unitPropogation( MaxSatEncoding encoding) {

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
            if(!encoding.contains(constraint)) {
              continue;
            }
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
                encoding.addInconsistentClauses(inconsistentClauses);
                return;
              }
            }
          }
        }
      } else { // c is soft or the
        // if l is a relaxation variable, we want to loop through the relaxation hard clauses
        /*if(l >= firstRelaxationLit) {
          clauses = relaxationClauses;
        } else { // otherwise we want to loop through the regular hard clauses
          clauses = hardClauses;
        }*/
        clauses = hardClauses;
      }
      for(Clause clause : clauses) {
        if(!encoding.contains(clause)) {
          continue;
        }
        // if lit is a non negated literal, then the following condition checks for negated versions
        // of this literal and removes them from their clauses.
        // if lit is negated, the this condition checks for non negated literals and removes them
        if(clause.contains(l, !litIsNegated)) {
          // if the clause is soft, add it to inconsistent subsets
          // we only want to save it once, before it has been modified. If the original encoding
          // contains a soft clause with the same literals as this one, then get than clause and
          // add it to the list of inconsistent subsets.
          if(clause.isSoft()
              && encoding.contains(clause)
              && !inconsistentClauses.contains(clause)) {
            if(verbose) System.out.printf("---Adding %s to set of inconsistent clauses.\n", clause);
            inconsistentClauses.add(clause);
            //inconsistentClauses.add(encoding.getSoftClause(clause));
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
            encoding.addInconsistentClauses(inconsistentClauses);
            return;
          }
        }
      }
    }
    return;
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
    private ArrayList<Boolean> softMask;
    private ArrayList<Boolean> hardMask;
    private ArrayList<Boolean> constraintsMask;
    private ArrayList<int[]> softMaskStack;
    private ArrayList<int[]> hardMaskStack;
    private ArrayList<int[]> constraintsMaskStack;
    private boolean containsEmptyClause = false;
    private ArrayList<Clause> inconsistentClauses;
    private int numSoftClauses = 0;
    private int numHardClauses = 0;
    private int numConstraints = 0;

    public MaxSatEncoding(UndirectedGraph<Integer> graph, ArrayList<ArrayList<Node<Integer>>> partition) {
      softClauses = new ArrayList<Clause>();
      hardClauses = new ArrayList<Clause>();
      atMostOneConstraints = new ArrayList<Clause>();

      softMask = new ArrayList<Boolean>();
      hardMask = new ArrayList<Boolean>();
      constraintsMask = new ArrayList<Boolean>();

      softMaskStack = new ArrayList<int[]>();
      hardMaskStack = new ArrayList<int[]>();
      constraintsMaskStack = new ArrayList<int[]>();

      for(ArrayList<Node<Integer>> set : partition) {
        if(set.size() > 0) {
          Clause clause = new Clause(set, softClauses.size());
          clause.setAsSoft();
          addClause(clause);
        }
      }
      inconsistentClauses = new ArrayList<Clause>(getNumSoftClauses());

      /*
      Clause clause;
      for(int i = 0; i < allHardClauses.size(); i++) {
        clause = allHardClauses.get(i);
        Integer v1 = clause.getLiteral(0);
        Integer v2 = clause.getLiteral(1);
        if(graph.contains(v1) && graph.contains(v2) && !graph.hasEdge(v1, v2)) {
          clause.setIndex(hardClauses.size());
          addClause(clause);
        }
      }*/
      long start = new Date().getTime();
      List<Integer> elements = graph.getElements();
      for(int i = 0; i < graph.size(); i++) {
        Integer vertex = elements.get(i);
        for(int j = i+1; j < graph.size(); j++) {
          Integer neighbor = elements.get(j);
          if(!graph.hasEdge(vertex, neighbor)) {
            Clause clause = new Clause(vertex, neighbor, hardClauses.size());
            clause.setAsHard();
            addClause(clause);
          }
        }
      }
      long end = new Date().getTime();
      timeConstructingEncodings += (end - start);
    }

    public MaxSatEncoding(ArrayList<ArrayList<Node<Integer>>> partition) {
      long start = new Date().getTime();
      softClauses = new ArrayList<Clause>();
      hardClauses = new ArrayList<Clause>();
      atMostOneConstraints = new ArrayList<Clause>();

      softMask = new ArrayList<Boolean>();
      hardMask = new ArrayList<Boolean>();
      constraintsMask = new ArrayList<Boolean>();

      softMaskStack = new ArrayList<int[]>();
      hardMaskStack = new ArrayList<int[]>();
      constraintsMaskStack = new ArrayList<int[]>();

      for(ArrayList<Node<Integer>> set : partition) {
        if(set.size() > 0) {
          Clause clause = new Clause(set, softClauses.size());
          clause.setAsSoft();
          addClause(clause);
        }
      }
      inconsistentClauses = new ArrayList<Clause>(getNumSoftClauses());
      Clause clause;
      for(int i = 0; i < allHardClauses.size(); i++) {
        clause = allHardClauses.get(i);
        // don't need to set its index because they are all going into the list one by one, indices
        // won't change.
        addClause(clause);
      }

      long end = new Date().getTime();
      timeConstructingEncodings += (end - start);
    }

    public void saveState() {
      // Use stacks to save the states of the masks
      // for each mask, make a copy and push it onto its stack. When the state is restored,
      // that copy is popped off the stack and set as the mask.
      // Also, tell each clause to save its state.
      int[] deactivated = new int[softMask.size() - getNumSoftClauses()];
      int index = 0;
      for(int i = 0; i < softMask.size(); i++) {
        softClauses.get(i).saveState();
        if(softMask.get(i) == false) {
          deactivated[index] = i;
          index++;
        }
        //copy.add(softMask.get(i).booleanValue());
      }
      // push the copy onto the softMaskStack
      softMaskStack.add(deactivated);
      //System.out.println("State Saved, depth of stack: " + softMaskStack.size());

      deactivated = new int[hardMask.size() - getNumHardClauses()];
      index = 0;
      for(int i = 0; i < hardMask.size(); i++) {
        hardClauses.get(i).saveState();
        if(hardMask.get(i) == false) {
          deactivated[index] = i;
          index++;
        }
      }
      // push the copy onto the hardMaskStack
      hardMaskStack.add(deactivated);

      deactivated = new int[constraintsMask.size() - getNumConstraints()];
      index = 0;
      for(int i = 0; i < constraintsMask.size(); i++) {
        atMostOneConstraints.get(i).saveState();
        if(constraintsMask.get(i) == false) {
          deactivated[index] = i;
          index++;
        }
      }
      // push the copy onto the constraintsMaskStack
      constraintsMaskStack.add(deactivated);
    }

    private boolean contains(int[] deactivatedList, int value) {
      for(int val : deactivatedList) {
        if(val == value) {
          return true;
        }
        // the values in the list are in increasing order. if val in the list is > value, then
        // value is not in the list, return false
        if(val > value) {
          return false;
        }
      }
      return false;
    }

    public void restoreState() {
      // pop the latest deactivated list off of the Stack
      int lastIndex = softMaskStack.size()-1;
      int[] deactivated = softMaskStack.get(lastIndex);
      softMaskStack.remove(lastIndex);
      // restore all clauses and set the mask values
      int count = 0;
      for(int i = 0; i < softClauses.size(); i++) {
        softClauses.get(i).restore();
        // for every index for mask, if that index is in deactivated, set mask to false,
        // true otherwise
        if(contains(deactivated, i)) {
          softMask.set(i, false);
        } else {
          softMask.set(i, true);
          count++;
        }
      }
      this.numSoftClauses = count;

      // pop the latest deactivated list off of the Stack
      lastIndex = hardMaskStack.size()-1;
      deactivated = hardMaskStack.get(lastIndex);
      hardMaskStack.remove(lastIndex);
      // restore all clauses and set the mask values
      count = 0;
      for(int i = 0; i < hardClauses.size(); i++) {
        hardClauses.get(i).restore();
        // for every index for mask, if that index is in deactivated, set mask to false,
        // true otherwise
        if(contains(deactivated, i)) {
          hardMask.set(i, false);
        } else {
          hardMask.set(i, true);
          count++;
        }
      }
      this.numHardClauses = count;

      // pop the latest deactivated list off of the Stack
      lastIndex = constraintsMaskStack.size()-1;
      deactivated = constraintsMaskStack.get(lastIndex);
      constraintsMaskStack.remove(lastIndex);
      // restore all clauses and set the mask values
      count = 0;
      for(int i = 0; i < atMostOneConstraints.size(); i++) {
        atMostOneConstraints.get(i).restore();
        // for every index for mask, if that index is in deactivated, set mask to false,
        // true otherwise
        if(contains(deactivated, i)) {
          constraintsMask.set(i, false);
        } else {
          constraintsMask.set(i, true);
          count++;
        }
      }
      this.numConstraints = count;

      containsEmptyClause = false;
    }

    public void enableAll() {
      for(int i = 0; i < softClauses.size(); i++) {
        softMask.set(i, true);
        softClauses.get(i).enableAll();
      }
      this.numSoftClauses = softClauses.size();

      for(int i = 0; i < hardClauses.size(); i++) {
        hardMask.set(i, true);
        hardClauses.get(i).enableAll();
      }
      this.numHardClauses = hardClauses.size();

      for(int i = 0; i < constraintsMask.size(); i++) {
        constraintsMask.set(i, true);
        atMostOneConstraints.get(i).enableAll();
      }
      this.numConstraints = constraintsMask.size();

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

    public ArrayList<Boolean> getSoftMask() {
      return softMask;
    }

    public List<Clause> getAtMostOneConstraints() {
      return atMostOneConstraints;
    }

    public int getNumConstraints() {
      return this.numConstraints;
    }

    public void addAtMostOneConstraint(ArrayList<Integer> lits) {
      Clause c;
      for(int i = 0; i < lits.size(); i++) {
        for(int j = i+1; j < lits.size(); j++) {
          c = new Clause(hardClauses.size());
          c.addLiteral(lits.get(i), true);
          c.addLiteral(lits.get(j), true);
          c.setAsRelaxation();
          addClause(c);
        }
      }
      c = new Clause(atMostOneConstraints.size());
      for(Integer lit : lits) {
        c.addLiteral(lit, false);
      }
      if(verbose) System.out.printf("Adding atMostOneConstraint %s\n", c);
      atMostOneConstraints.add(c);
      constraintsMask.add(true);
      numConstraints++;
    }

    public ArrayList<Clause> getNewBinaryClauses() {
      ArrayList<Clause> clauses = new ArrayList<Clause>();
      for(Clause clause : getSoftClauses()){
        if(contains(clause) && clause.getNumLiterals() == 2 && clause.isModified()){
          clauses.add(clause);
        }
      }
      return clauses;
    }

    public LinkedList<Clause> getUnitClauses() {
      LinkedList<Clause> queue = new LinkedList<Clause>();
      for(int i = 0; i < softClauses.size(); i++) {
        if(softMask.get(i) == true && softClauses.get(i).isUnitClause()) {
          queue.offer(softClauses.get(i));
        }
      }
      for(int i = 0; i < hardClauses.size(); i++) {
        if(hardMask.get(i) == true && hardClauses.get(i).isUnitClause()) {
          queue.offer(hardClauses.get(i));
        }
      }
      for(int i = 0; i < atMostOneConstraints.size(); i++) {
        if(constraintsMask.get(i) == true && atMostOneConstraints.get(i).isUnitClause()) {
          queue.offer(atMostOneConstraints.get(i));
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
        removeClause(clause);
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
      return this.numSoftClauses;
    }

    public int getNumHardClauses() {
      return this.numHardClauses;
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

    public void removeClause(Clause clause) {
      int index = indexOf(clause);
      if(index < 0) {
        return;
      }
      if(clause.isSoft()) {
        this.softMask.set(index, false);
        this.numSoftClauses--;
      } else if(clause.isHard() || clause.isRelaxation()) {
        this.hardMask.set(index, false);
        this.numHardClauses--;
      } else {
        this.constraintsMask.set(index, false);
        this.numConstraints--;
      }
    }

    public void addClause(Clause clause) {
      if(clause.isSoft()) {
        softClauses.add(clause);
        softMask.add(true);
        this.numSoftClauses++;
      } else if(clause.isHard() || clause.isRelaxation()) {
        hardClauses.add(clause);
        hardMask.add(true);
        this.numHardClauses++;
      } else {
        atMostOneConstraints.add(clause);
        constraintsMask.add(true);
        this.numConstraints++;
      }
    }

    public void setContainsEmptyClause() {
      this.containsEmptyClause = true;
    }

    public boolean containsEmptyClause() {
      return this.containsEmptyClause;
    }

    private int indexOf(Clause clause) {
      return clause.getIndex();
    }

    public boolean contains(Clause clause) {
      int index = indexOf(clause);
      if(index < 0) {
        throw new NoSuchElementException(String.format("Clause %s not in Encoding", clause));
      }
      if(clause.isSoft()) {
        return this.softMask.get(index);
      } else if (clause.isHard() || clause.isRelaxation()) {
        return this.hardMask.get(index);
      } else {
        return this.constraintsMask.get(index);
      }
    }

    public Clause getSoftClause(Clause other) {
      int index = indexOf(other);
      if(index < 0) {
        throw new NoSuchElementException(String.format("Clause %s not in Encoding", other));
      }
      if(this.softMask.get(index) == false) {
        throw new NoSuchElementException(String.format("Clause %s not in Encoding", other));
      }
      return this.softClauses.get(index);
    }

    @Override
    public String toString() {
      String str = "###\nSoft Clauses:\n {";
      Clause clause;
      int counter = 0;
      for(int i = 0; i < softClauses.size(); i++) {
        if(softMask.get(i) == true) {
          String softStr = "";
          clause = softClauses.get(i);
          if(clause.isTested()) {
            softStr = String.format("%s*", clause);
          } else {
            softStr = String.format("%s", clause);
          }
          if(counter == 0) {
            str += softStr;
          } else {
            str += ", " + softStr;
          }
          counter++;
        }
      }
      counter = 0;
      str += "}\nHard Clauses:\n {";
      for(int i = 0; i < hardClauses.size(); i++) {
        if(hardMask.get(i) == true) {
          clause = hardClauses.get(i);
          if(counter == 0) {
            str += clause;
          } else {
            str += ", " + clause;
          }
          counter++;
        }
      }
      counter = 0;
      str += "}\nAt Most One Constraints:\n {";
      for(int i = 0; i < atMostOneConstraints.size(); i++) {
        if(constraintsMask.get(i) == true) {
          clause = atMostOneConstraints.get(i);
          if(counter == 0) {
            str += clause;
          } else {
            str += ", " + clause;
          }
          counter++;
        }
      }
      str += "}\n###";
      return str;
    }
  }

  private class Clause implements Comparable<Clause>, Iterable<Integer> {
    private ArrayList<Integer> literals;
    private ArrayList<Boolean> negated;
    private ArrayList<Boolean> mask;
    private ArrayList<ArrayList<Boolean>> maskStack;
    private boolean isSoft;
    private boolean isHard;
    private boolean isRelaxation;
    private boolean tested = false;
    private int numLiterals = 0;
    // for efficiency, since clauses will never move in their lists, a clause knows its index
    private int index;

    public Clause(int index) {
      literals = new ArrayList<Integer>();
      negated = new ArrayList<Boolean>();
      mask = new ArrayList<Boolean>();
      maskStack = new ArrayList<ArrayList<Boolean>>();
      this.index = index;
    }

    public Clause(ArrayList<Node<Integer>> vertices, int index) {
      literals = new ArrayList<Integer>(vertices.size());
      negated = new ArrayList<Boolean>(vertices.size());
      mask = new ArrayList<Boolean>(vertices.size());
      maskStack = new ArrayList<ArrayList<Boolean>>(vertices.size());
      boolean isNegation = false;
      for(Node<Integer> vertex : vertices) {
        literals.add(vertex.get());
        negated.add(isNegation);
        if(vertex.get() > maxLiteral) {
          maxLiteral = vertex.get();
        }
        mask.add(true);
        numLiterals++;
      }
      this.index = index;
    }

    public Clause(Integer a, Integer b, int index) {
      literals = new ArrayList<Integer>(2);
      negated = new ArrayList<Boolean>(2);
      mask = new ArrayList<Boolean>(2);
      maskStack = new ArrayList<ArrayList<Boolean>>(2);
      boolean isNegation;
      if(isSoft) {
        isNegation = false;
      } else {
        isNegation = true;
      }
      literals.add(a);
      negated.add(isNegation);
      mask.add(true);
      numLiterals++;
      literals.add(b);
      negated.add(isNegation);
      mask.add(true);
      numLiterals++;
      if(a > maxLiteral) {
        maxLiteral = a;
      }
      if(b > maxLiteral) {
        maxLiteral = b;
      }
      this.index = index;
    }

    public int getIndex() {
      return this.index;
    }

    public void setIndex(int i) {
      this.index = i;
    }

    public void saveState() {
      ArrayList<Boolean> copy = new ArrayList<Boolean>(mask.size());
      for(Boolean val : mask) {
        copy.add(val.booleanValue());
      }
      // push the copy onto the maskStack
      maskStack.add(copy);
    }

    public void restore() {
      int lastIndex = maskStack.size()-1;
      mask = maskStack.get(lastIndex);
      maskStack.remove(lastIndex);
      int count = 0;
      for(Boolean active : mask) {
        if(active) {
          count++;
        }
      }
      this.numLiterals = count;
    }

    public void enableAll() {
      for(int i = 0; i < mask.size(); i++) {
        mask.set(i, true);
      }
      this.numLiterals = mask.size();
    }

    public int getMaskSize() {
      return this.mask.size();
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
      int i = 0;
      for(Boolean flag : mask) {
        if(flag) {
          i++;
        }
      }
      return i;
    }

    public boolean isUnitClause() {
      //System.out.printf("Testing if Clause %s is Unit\n", toString());
      //System.out.println("Num literals: " + getNumLiterals());
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
      int index = literals.indexOf(literal);
      if(index < 0) {
        return false;
      }
      return this.mask.get(index) == true;
      //return literals.contains(literal);
    }

    public boolean contains(Integer literal, boolean negated) {
      int index = literals.indexOf(literal);
      if(index != -1) {
        if(this.mask.get(index) == true && negated == this.negated.get(index)) {
          return true;
        }
      }
      return false;
    }

    public void remove(Integer literal) {
      int index = literals.indexOf(literal);
      if(index != -1) {
        //literals.remove(index);
        //negated.remove(index);
        mask.set(index, false);
      }
    }

    public void addLiteral(Integer literal, boolean isNegated) {
      literals.add(literal);
      negated.add(isNegated);
      mask.add(true);
    }

    public boolean isModified() {
      for(Boolean active : mask) {
        if(!active) {
          return true;
        }
      }
      return false;
    }

    /**
    * Only call if this clause is a unit clause. Returns the remaining literal.
    * @return the remaining literal in a unit clause
    */
    public Integer getLiteral() {
      for(int i = 0; i < mask.size(); i++) {
        if(mask.get(i) == true) {
          return literals.get(i);
        }
      }
      throw new NoSuchElementException("No literals in Clause");
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

    public Boolean getMaskValue(int i) {
      return mask.get(i);
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

    public ArrayList<Boolean> getMask() {
      return this.mask;
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
            //&& this.mask.equals(other.getMask())) {
          return true;
        }
      }
      return false;
    }

    @Override
    public String toString() {
      String str = "";
      if(!isEmpty()) {
        if(this.mask.get(0) == true) {
          if(isNegated(0)) {
          } else {
          }
        }
        int counter = 0;
        for(int i = 0; i < literals.size(); i++) {
          if(this.mask.get(i) == true) {
            String litStr = "";
            if(isNegated(i)) {
              litStr = String.format("!%s",literals.get(i));
            } else {
              litStr = String.format("%s",literals.get(i));
            }
            if(counter == 0) {
              str = litStr;
            } else {
              str = String.format("%s + %s", str, litStr);
            }
            counter++;
          }
        }
      }
      str = String.format("(%s)",str);
      return str;
    }
  }
}
