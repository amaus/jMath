package com.aaronpmaus.jMath.graph;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Collections;
import java.util.Collection;
import java.util.HashMap;
import java.util.Comparator;


/**
* A class that implements IncMaxCliqueSolver from
* Combining MaxSAT Reasoning and Incremental Upper Bound for the Maximum Clique Problem
* Li, Fang, Xu 2013
* INCOMPLETE - does not include UB max sat or ind set logic
* @since 0.7.0
*/
public class IncMaxCliqueSolver<T extends Comparable<? super T>> extends MaxCliqueSolver<T> {
  private ArrayList<Node<T>> vertexOrdering;
  //private ArrayList<Integer> vertexUB;
  private HashMap<Node<T>, Integer> vertexUB;
  private UndirectedGraph<T> originalGraph;
  public static long numCalls = 0;
  public static long numVOCalls = 0;

  /**
  * {@inheritDoc}
  * @since 0.7.0
  */
  public UndirectedGraph<T> findMaxClique(UndirectedGraph<T> graph){
    ArrayList<Node<T>> vertexOrdering = vertexOrdering(graph);
    // initialize vertexUB
    return findMaxClique(graph, vertexOrdering);
  }

  /**
  * Finds the maximum clique in g
  * @param graph the graph to search for a max clique in
  * @param vertexOrdering the ordering of the vertices to use when searching for the clique
  * @return An {@code UndirectedGraph<T>} that is a max clique in graph
  * @since 0.7.0
  */
  public UndirectedGraph<T> findMaxClique(UndirectedGraph<T> graph, ArrayList<Node<T>> vertexOrdering) {
    // create a deep copy of the graph
    this.originalGraph = graph;
    UndirectedGraph<T> g = new UndirectedGraph<T>(graph);
    this.vertexOrdering = vertexOrdering;
    vertexUB = new HashMap<Node<T>, Integer>((int)(g.size()/0.75)+1);
    for(int i = vertexOrdering.size()-1; i >= 0; i--){
      int ubValue = incUB(i, g);
      vertexUB.put(vertexOrdering.get(i), ubValue);
    }
    //printUB();
    UndirectedGraph<T> clique = incMaxClique(g, new UndirectedGraph<T>(), new UndirectedGraph<T>());
    clique = originalGraph.subset(clique.getElements());
    //System.out.println("is clique? " + g.isClique(clique));
    //System.out.println("is clique? " + g.checkIfClique(objs));
    return clique;
    //return clique;
  }

  private void printUB(){
    String vertices = "Vertices:  ";
    String ubValues = "UB Values: ";
    for(int i = 0; i < vertexOrdering.size(); i++){
      vertices += String.format("%s\t",vertexOrdering.get(i).get());
      ubValues += String.format("%s\t",vertexUB.get(vertexOrdering.get(i)));
    }
    //System.out.println("Vertex Ordering and initial UB values:");
    System.out.println(vertices);
    System.out.println(ubValues);
  }

  // take in a reference to a graph
  // this will be the graph to use to establish neighbors
  // when calculating UB values
  // @param index is the index of the vertex in the vertexOrdering
  // @param g is the graph to use to establish neighbors
  private int incUB(int index, UndirectedGraph<T> g){
    Node<T> vertex = g.getNode(vertexOrdering.get(index).get());
    //System.out.println("Calculating inc UB of " + vertex.get());
    if(vertex == null){
      throw new NullPointerException("IncMaxCliqueSolver::incUB() vertex at index in vertexOrdering not in g");
    }

    for(int i = index+1; i < vertexOrdering.size(); i++){
      // if v_i and v_j are neighbors
      if(g.contains(vertexOrdering.get(i).get())){
        Node<T> neighbor = g.getNode(vertexOrdering.get(i).get());
        //if(neighbor != null) System.out.println("looking at " + neighbor.get());
        if(vertex.hasNeighbor(neighbor)){
          //System.out.println(neighbor.get() + " is a neighbor with UB of " + vertexUB.get(vertexOrdering.get(i)));
          // set vertexUB[i] = vertexUB[j] + 1
          return vertexUB.get(vertexOrdering.get(i))+1;
        }
      }
    }
    // if v_i has no neighbors after it in vertexOrdering, set vertexUB[i] = 1
    return 1;
  }

  /**
  * This method is a heuristic to estimate the number of independent sets in a graph via a greedy
  * graph coloring algorithm. The algorithm comes from Tomita et al. 2003 and 2010.
  * @param g the graph a clique is being sought in
  * @param cMaxSize the size of the largest clique found so far
  * @param cSize the size of the clique under construction
  */
  private int indSetUB(UndirectedGraph<T> g, int cMaxSize, int cSize) {
    //System.out.println("## Calculating indSetUB");
    // get a list of nodes and sort them in descending order by degree
    List<Node<T>> descendingDegreeNodes = g.getNodes();
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
      Collection<Node<T>> neighbors = node.getNeighbors();
      // find the lowest k where neighbors and the set of nodes in colorSets[k] share no nodes
      // in common
      while(!Collections.disjoint(neighbors, colorSets.get(k))){
        k++;
      }
      if(k > maxColorNumber) {
        maxColorNumber = k;
        // initialize and add the next color set
        colorSets.add(new ArrayList<Node<T>>());
      }
      colorSets.get(k).add(node);

      //System.out.printf("before re-number: %d\n", maxColorNumber);
      // - Re-NUMBER starts -
      int colorNumberThreshold = cMaxSize - cSize;
      if(k > colorNumberThreshold && k == maxColorNumber) {
        // re-number the vertices
        reNumber(node, k, colorNumberThreshold, colorSets);
        // if the highest number color set is empty after the re-numbering
        if(colorSets.get(colorSets.size()-1).isEmpty()) {
          // decrement the max color number
          maxColorNumber--;
        }
      }
      //System.out.printf("after re-number: %d\n", maxColorNumber);
    }
    // return the number of colors assigned
    return maxColorNumber + 1;
  }

  private void reNumber(Node<T> node, int nodeColor, int colorThreshold,
                        ArrayList<ArrayList<Node<T>>> colorSets) {

    for(int k1 = 0; k1 < colorThreshold - 1; k1++){
      ArrayList<Node<T>> intersection = intersection(colorSets.get(k1), node.getNeighbors());
      if(intersection.size() == 1) {
        Node<T> intersectedNode = intersection.get(0);
        for(int k2 = k1 + 1; k2 < colorThreshold; k2++) {
          if(Collections.disjoint(colorSets.get(k2), intersectedNode.getNeighbors())) {
            colorSets.get(nodeColor).remove(node);
            colorSets.get(k1).remove(intersectedNode);
            colorSets.get(k1).add(node);
            colorSets.get(k2).add(intersectedNode);
            return;
          }
        }
      }
    }
  }

  /**
  * @param g the UndirectedGraph to look for a max clique in
  * @param c the clique being built
  * @param gMax the max clique found so far
  * @return an undirected graph that is the maximum clique found in g
  */
  private UndirectedGraph<T> incMaxClique(UndirectedGraph<T> g, UndirectedGraph<T> c, UndirectedGraph<T> cMax){
    if(numCalls == 100){ // for testing purposes to stop infinite recursion
      //return c;
    }
    numCalls++;
    long callNumber = numCalls;
    //System.out.println("Call #: " + callNumber);
    if(g.size() == 0){
      //System.out.println("Size of graph is 0, returning c");
      //System.out.println("c:\n"+c);
      return c;
    }
    Node<T> smallestVertex = getSmallestVertex(g);
    if(numCalls < 100){
      //System.out.println("The whole graph");
      //System.out.println(g);
      //System.out.println("smallest vertex");
      //System.out.println(smallestVertex);
    }
    int smallestVertexIndex = vertexOrdering.indexOf(smallestVertex);
    UndirectedGraph<T> gWithoutSmallestVertex = new UndirectedGraph<T>(g);
    gWithoutSmallestVertex.removeNode(smallestVertex.get());
    if(numCalls < 100){
      //System.out.println("graph without smallest vertex");
      //System.out.println(gWithoutSmallestVertex);
    }
    //System.out.println("making recursive call of incMaxClique");
    UndirectedGraph<T> c1 = incMaxClique(gWithoutSmallestVertex, c, cMax);
    //System.out.println("In Call #: " + callNumber);
    //System.out.println("first recursive call complete");
    if(c1.size() > cMax.size()){
      cMax = c1;
    }
    // update vertexUB, so far only incUB. TODO include UBindSet
    // smallestVertex is not garanteed to be the same reference as that node in
    // the original graph (the node used to build vertexUB), but since the hashCodes
    // depend only on the Objects the Nodes contain and the Objects themselves
    // are only shallow copies even if the graph is a deep copy, it doesn't matter
    // if we don't have a reference to the original node. When a graph is copies, all
    // the nodes and edges are copied, but the references to the objects the nodes contain
    // are simply passed in, not deep copied.
    //System.out.println("smallest vertex: " + smallestVertex.get() );
    //System.out.println("Size of vertexUB: " + vertexUB.size());
    //for(Node<T> node : vertexUB.keySet()){
    //System.out.println(node.get());
    //}
    vertexUB.put(smallestVertex, min(vertexUB.get(smallestVertex),
                                     incUB(smallestVertexIndex, g),
                                     indSetUB(g, cMax.size(), c.size())));
    //System.out.println("Updating UB for " + smallestVertex.get());
    //printUB();

    if(cMax.size() >= (vertexUB.get(smallestVertex) + c.size())){
      return cMax;
    }
    // save the vertexUB values of the neighbors of smallestVertex
    // first, get the set of neighbors
    Collection<Node<T>> neighbors = smallestVertex.getNeighbors();
    // copy all the vertexUB values for the neighbors of smallestVertex
    HashMap<Node<T>, Integer> vertexUB_bkup = new HashMap<Node<T>,Integer>();
    for(Node<T> neighbor : neighbors){
      vertexUB_bkup.put(neighbor, vertexUB.get(neighbor));
    }
    UndirectedGraph<T> neighborsGraph = g.getNeighbors(smallestVertex.get());
    UndirectedGraph<T> cUnionSmallestVertex = new UndirectedGraph<T>(c);
    // add in smallestVertex
    // first create a new Node
    Node<T> v = new Node<T>(smallestVertex.get());
    // for every neighbor of the smallestVertex
    cUnionSmallestVertex.addVertex(v.get());
    for(Node<T> neighbor : neighbors){
      // if that neighbor is in c:
      if(cUnionSmallestVertex.contains(neighbor.get())){
        // add an edge to the union graph between smallestVertex and the neighbor
        cUnionSmallestVertex.addEdge(v.get(), neighbor.get());
      }
    }
    //System.out.println("neighbors of " + smallestVertex.get() + ":\n" + neighborsGraph);
    //System.out.println("cUnionSmallestVertex:\n" + cUnionSmallestVertex);

    UndirectedGraph<T> c2 = incMaxClique(neighborsGraph, cUnionSmallestVertex, cMax);
    //System.out.println("In Call #: " + callNumber);
    //System.out.println("second recursive call complete");

    // restore the saved vertexUB values
    for(Node<T> neighbor : neighbors){
      vertexUB.put(neighbor, vertexUB_bkup.get(neighbor));
    }

    vertexUB.put(smallestVertex, Math.min(vertexUB.get(smallestVertex), (c2.size() - c.size())));

    if(c1.size() >= c2.size()){
      return c1;
    } else {
      return c2;
    }
  }

  private Node<T> getSmallestVertex(UndirectedGraph<T> g){
    for(int i = 0; i < vertexOrdering.size(); i++){
      if(g.contains(vertexOrdering.get(i).get())){
        return g.getNode(vertexOrdering.get(i).get());
      }
    }
    throw new NoSuchElementException("Smallest Vertex Not found!");
  }

  /**
  * returns the independent set partition of a a graph
  * @param g the graph to get the independent set partition of
  * @return an ArrayList of the graphs that make up the partition
  * @since 0.7.0
  */
  public ArrayList<UndirectedGraph<T>> getIndependentSetPartition(UndirectedGraph<T> g) {
    ArrayList<Node<T>> indSetVertexOrder = g.degeneracyOrdering( );
    Collections.reverse(indSetVertexOrder);
    return getIndependentSetPartition(g, indSetVertexOrder);
  }

  private ArrayList<UndirectedGraph<T>> getIndependentSetPartition(UndirectedGraph<T> g, ArrayList<Node<T>> indSetVertexOrder) {
    ArrayList<UndirectedGraph<T>> indSets = new ArrayList<UndirectedGraph<T>>();
    UndirectedGraph<T> gComplement = g.getComplement();
    IncMaxCliqueSolver<T> indSetSolver = new IncMaxCliqueSolver<T>();
    while(gComplement.size() > 1){
      //System.out.println("gComplement.size(): " +gComplement.size());
      //System.out.println("gComplement.density(): " +gComplement.density());
      UndirectedGraph<T> indSetComplementNodes = indSetSolver.findMaxClique(gComplement, indSetVertexOrder);
      if(numVOCalls == 100){
        //throw new RuntimeException("VO debugging, QUIT VO Calls");
      }
      for(Node<T> n : indSetComplementNodes){
        gComplement.removeNode(n.get());
        indSetVertexOrder.remove(n);
      }
      indSets.add(g.subset(indSetComplementNodes.getElements()));
    }
    if(gComplement.size() > 0){
      indSets.add(g.subset(gComplement.getElements()));
    }
    return indSets;
  }

  // Combining MaxSAT Reasoning and Incremental Upper Bound for the Maximum Clique Problem
  // Li, Fang, Xu 2013
  private ArrayList<Node<T>> vertexOrdering(UndirectedGraph<T> g){
    numVOCalls++;
    // System.out.println("#####\nvertexOrdering call # " + numVOCalls + ", g.density(): " + g.density());
    // Build the Degeneracy Vertex Ordering
    ArrayList<Node<T>> vertexOrdering = g.degeneracyOrdering( );
    // System.out.println("after building degeneracy ordering, g.density(): " + g.density());

    ArrayList<UndirectedGraph<T>> indSets = new ArrayList<UndirectedGraph<T>>();

    if(g.density() < 0.70) { // if g is not dense
      // System.out.println("graph sparse, returning degeneracy vertex ordering");
      return vertexOrdering;
    } else {
      ArrayList<Node<T>> indSetVertexOrder = new ArrayList<Node<T>>(vertexOrdering);
      Collections.reverse(indSetVertexOrder);
      // System.out.println("graph dense, finding ind set partition");
      indSets = getIndependentSetPartition(g,indSetVertexOrder);

      boolean isIrregular = false; //partition is irregular if there are >=2 indSets of size 1
      int numSingleElementSets = 0;
      for(UndirectedGraph<T> indSet : indSets){
        if(indSet.size() == 1) {
          numSingleElementSets++;
        }
      }
      if(numSingleElementSets > 1){
        isIrregular = true;
      }
      if(isIrregular){
        // System.out.println("Ind Set Partition is irregular, returning degeneracy ordering");
        return vertexOrdering;
      } else { //return MaxIndSet vertex ordering
        vertexOrdering = new ArrayList<Node<T>>(g.getNodes());
        Collections.sort(vertexOrdering, new MaxIndSetComparator<T>(indSets));
        // System.out.println("Ind Set Parition is regular, returning Ind Set Ordering");
        return vertexOrdering;
      }
    }
  }
  private class MaxIndSetComparator<T extends Comparable<? super T>> implements Comparator<Node<T>> {
    private ArrayList<UndirectedGraph<T>> indSetPartition;

    /**
    * This constructor takes in an ArrayList of the Independent Sets.
    * @param partition the list of Independent Sets in the IND Set partition of a graph
    * @since 0.7.0
    */
    public MaxIndSetComparator(ArrayList<UndirectedGraph<T>> partition){
      this.indSetPartition = partition;
    }

    /**
    * Returns -1 if the size of n1's partition is smaller than n2's
    *          0 if their sizes are the same
    *          1 otherwise
    * @param n1 the first node
    * @param n2 the second node
    * @return {@literal -1 if n1 < n2, 0 if same, 1 if n1 > n2}
    * @since 0.7.0
    */
    public int compare(Node<T> n1, Node<T> n2){
      int n1_index = getIndSetPartitionIndex(n1);
      int n2_index = getIndSetPartitionIndex(n2);
      if(n1_index > n2_index){
        return -1;
      } else if(n1_index == n2_index) {
        if(n1.numNeighbors() < n2.numNeighbors()){
          return -1;
        } else if(n1.numNeighbors() == n2.numNeighbors()){
          return 0;
        } else {
          return 1;
        }
      } else {
        return 1;
      }
    }

    private int getIndSetPartitionIndex(Node<T> n){
      for(int i = 0; i < indSetPartition.size(); i++){
        UndirectedGraph<T> g = indSetPartition.get(i);
        if(g.contains(n.get())){
          return i;
        }
      }
      return -1;
    }
  }

  private int min(int... nums){
    int min = nums[0];
    for(int num : nums){
      if(num < min){
        min = num;
      }
    }
    return min;
  }

  private ArrayList<Node<T>> intersection(Collection<Node<T>> a, Collection<Node<T>> b){
    ArrayList<Node<T>> intersection = new ArrayList<Node<T>>();
    for(Node<T> node : a){
      if(b.contains(node)){
        intersection.add(node);
      }
    }
    return intersection;
  }
}
