package com.aaronpmaus.jMath.graph;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Collections;
import java.util.Collection;
import java.util.HashMap;

/**
* A class that implements IncMaxCliqueSolver from
* Combining MaxSAT Reasoning and Incremental Upper Bound for the Maximum Clique Problem
* Li, Fang, Xu 2013
* INCOMPLETE - does not include UB max sat or ind set logic
* @since 0.7.0
*/
public class IncMaxCliqueSolver<T extends Comparable<T>> extends MaxCliqueSolver<T> {
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
    UndirectedGraph<T> cliqueInOriginal = new UndirectedGraph<T>();
    //ArrayList<Node<T>> cliqueNodes = new ArrayList<Node<T>>();
    //ArrayList<T> objs = new ArrayList<T>();
    for(Node<T> n : clique.getNodes()){
      cliqueInOriginal.addNode(originalGraph.getNode(n.get()));
      //cliqueNodes.add(g.getNode(n.get()));
      //objs.add(n.get());
    }
    clique = new UndirectedGraph<T>(cliqueInOriginal);
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
    gWithoutSmallestVertex.removeNode(gWithoutSmallestVertex.getNode(smallestVertex.get()));
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
    vertexUB.put(smallestVertex, Math.min(vertexUB.get(smallestVertex), incUB(smallestVertexIndex, g)));
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
    UndirectedGraph<T> neighborsGraph = g.getNeighbors(g.getNode(smallestVertex.get()));
    UndirectedGraph<T> cUnionSmallestVertex = new UndirectedGraph<T>(c);
    // add in smallestVertex
    // first create a new Node
    Node<T> v = new Node<T>(smallestVertex.get());
    // for every neighbor of the smallestVertex
    for(Node<T> neighbor : neighbors){
      // if that neighbor is in c:
      if(cUnionSmallestVertex.contains(neighbor)){
        // add an edge to the union graph between smallestVertex and the neighbor
        cUnionSmallestVertex.addEdge(v, cUnionSmallestVertex.getNode(neighbor.get()));
      }
    }
    cUnionSmallestVertex.addNode(v);
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
      if(g.contains(vertexOrdering.get(i))){
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
      UndirectedGraph<T> indSet = new UndirectedGraph<T>();
      for(Node<T> n : indSetComplementNodes.getNodes()){
        gComplement.removeNode(n);
        indSet.addNode(g.getNode(n.get()));
        indSetVertexOrder.remove(n);
      }
      indSets.add(indSet);
    }
    if(gComplement.size() > 0){
      UndirectedGraph<T> indSet = new UndirectedGraph<T>();
      for(Node<T> n : gComplement.getNodes()){
        indSet.addNode(g.getNode(n.get()));
      }
      indSets.add(indSet);
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
}
