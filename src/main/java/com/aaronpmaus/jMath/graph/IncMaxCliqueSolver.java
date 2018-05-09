package com.aaronpmaus.jMath.graph;
import java.util.ArrayList;
import java.util.List;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Collections;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Comparator;


/**
* A class that implements IncMaxCliqueSolver from
* Combining MaxSAT Reasoning and Incremental Upper Bound for the Maximum Clique Problem
* Li, Fang, Xu 2013
* INCOMPLETE - does not include UB max sat
* @since 0.7.0
*/
public class IncMaxCliqueSolver extends MaxCliqueSolver<Integer> {
  private ArrayList<Integer> vertexOrdering;
  //private ArrayList<Integer> vertexUB;
  private HashMap<Integer, Integer> vertexUB;
  public static long numCalls = 0;
  public static long numVOCalls = 0;
  private ArrayList<ArrayList<Node<Integer>>> colorSets;

  /**
  * {@inheritDoc}
  * @since 0.7.0
  */
  public UndirectedGraph<Integer> findMaxClique(UndirectedGraph<Integer> graph) {
    ArrayList<Integer> vertexOrdering = vertexOrdering(graph);
    // initialize vertexUB
    return findMaxClique(graph, vertexOrdering);
  }

  /**
  * Finds the maximum clique in g
  * @param graph the graph to search for a max clique in
  * @param vertexOrdering the ordering of the vertices to use when searching for the clique
  * @return An {@code UndirectedGraph<Integer>} that is a max clique in graph
  * @since 0.7.0
  */
  public UndirectedGraph<Integer> findMaxClique(UndirectedGraph<Integer> graph, ArrayList<Integer> vertexOrdering) {
    this.vertexOrdering = vertexOrdering;
    vertexUB = new HashMap<Integer, Integer>((int)(graph.size()/0.75)+1);
    for(int i = vertexOrdering.size()-1; i >= 0; i--) {
      int ubValue = incUB(i, graph);
      vertexUB.put(vertexOrdering.get(i), ubValue);
    }
    //printUB();
    List<Integer> elements = incMaxClique(graph, new LinkedList<Integer>(), new LinkedList<Integer>());
    UndirectedGraph<Integer> clique = graph.subset(elements);
    //System.out.println("is clique? " + g.isClique(clique));
    //System.out.println("is clique? " + g.checkIfClique(objs));
    return clique;
    //return clique;
  }

  private void printUB() {
    String vertices = "Vertices:  ";
    String ubValues = "UB Values: ";
    for(int i = 0; i < vertexOrdering.size(); i++) {
      vertices += String.format("%s\t",vertexOrdering.get(i));
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
  private int incUB(int index, UndirectedGraph<Integer> g) {
    Node<Integer> vertex = g.getNode(vertexOrdering.get(index));
    //System.out.println("Calculating inc UB of " + vertex.get());
    if(vertex == null) {
      throw new NullPointerException("IncMaxCliqueSolver::incUB() vertex at index in vertexOrdering not in g");
    }

    for(int i = index+1; i < vertexOrdering.size(); i++) {
      // if v_i and v_j are neighbors
      if(g.contains(vertexOrdering.get(i))) {
        Node<Integer> neighbor = g.getNode(vertexOrdering.get(i));
        //if(neighbor != null) System.out.println("looking at " + neighbor.get());
        if(vertex.hasNeighbor(neighbor)) {
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
  * @return the partition of the graph into independent sets (color sets)
  */
  private int indSetUB(UndirectedGraph<Integer> g, int cMaxSize, int cSize) {
    //System.out.println("## Calculating indSetUB");
    // get a list of nodes and sort them in descending order by degree
    List<Node<Integer>> descendingDegreeNodes = new ArrayList<Node<Integer>>(g.getNodes());
    Collections.sort(descendingDegreeNodes, Collections.reverseOrder());
    int maxColorNumber = 0;
    // initialize color sets
    // The index of the outer ArrayList is k and the inner arraylists hold all the nodes that belong
    // to that color k.
    colorSets = new ArrayList<ArrayList<Node<Integer>>>();
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
          //colorSets.remove(colorSets.size()-1);
        }
      }
      //System.out.printf("after re-number: %d\n", maxColorNumber);
    }
    // return the number of colors assigned
    return maxColorNumber + 1;
    //return colorSets;
  }

  private void reNumber(Node<Integer> node, int nodeColor, int colorThreshold,
                        ArrayList<ArrayList<Node<Integer>>> colorSets) {

    for(int k1 = 0; k1 < colorThreshold - 1; k1++) {
      LinkedList<Node<Integer>> intersection = intersection(colorSets.get(k1), node.getNeighbors());
      if(intersection.size() == 1) {
        Node<Integer> intersectedNode = intersection.get(0);
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
  * @return a list containing the elements of the maximum clique found in g
  */
  private List<Integer> incMaxClique(UndirectedGraph<Integer> g, List<Integer> c, List<Integer> cMax) {
    if(numCalls == 100) { // for testing purposes to stop infinite recursion
      //return c;
    }
    numCalls++;
    long callNumber = numCalls;
    //System.out.println("Call #: " + callNumber);
    if(g.size() == 0) {
      //System.out.println("Size of graph is 0, returning c");
      //System.out.println("c:\n"+c);
      return c;
    }
    Integer smallestVertex = getSmallestVertex(g);
    if(numCalls < 100) {
      //System.out.println("The whole graph");
      //System.out.println(g);
      //System.out.println("smallest vertex");
      //System.out.println(smallestVertex);
    }
    int smallestVertexIndex = vertexOrdering.indexOf(smallestVertex);
    //UndirectedGraph<Integer> gWithoutSmallestVertex = new UndirectedGraph<Integer>(g);
    //gWithoutSmallestVertex.removeVertex(smallestVertex);
    if(numCalls < 100) {
      //System.out.println("graph without smallest vertex");
      //System.out.println(gWithoutSmallestVertex);
    }
    //System.out.println("making recursive call of incMaxClique");
    //UndirectedGraph<Integer> c1 = incMaxClique(gWithoutSmallestVertex, c, cMax);

    // CRAZY Idea, instead of creating a copy of g for every recursive call, remove the smallest
    // vertex from g before the call, and add it back in after the call.
    Node<Integer> small = g.getNode(smallestVertex);
    g.removeVertex(smallestVertex);
    List<Integer> c1 = incMaxClique(g, c, cMax);
    g.addNode(small);

    //System.out.println("In Call #: " + callNumber);
    //System.out.println("first recursive call complete");
    if(c1.size() > cMax.size()) {
      cMax = c1;
    }
    // update vertexUB, includes incUB and indSetUB. TODO include MaxSatUB
    //System.out.println("smallest vertex: " + smallestVertex.get() );
    //System.out.println("Size of vertexUB: " + vertexUB.size());
    //for(Node<Integer> node : vertexUB.keySet()) {
    //System.out.println(node.get());
    //}
    //printUB();
    //ArrayList<ArrayList<Node<Integer>>> partition = indSetUB(g, cMax.size(), c.size());
    int indSetUpperBound = indSetUB(g, cMax.size(), c.size());
    //int maxSatUB = new MaxSatUB(g, colorSets).estimateCardinality();
    //System.out.println("Calling MaxSatUB, Graph:\n" + g);
    vertexUB.put(smallestVertex, min(vertexUB.get(smallestVertex),
                                     incUB(smallestVertexIndex, g),
                                     indSetUpperBound));
                                     //maxSatUB));
    //System.out.println("Updating UB for " + smallestVertex);
    //printUB();

    if(cMax.size() >= (vertexUB.get(smallestVertex) + c.size())) {
      return cMax;
    }
    // save the vertexUB values of the neighbors of smallestVertex
    // first, get the set of neighbors
    UndirectedGraph<Integer> neighbors = g.getNeighbors(smallestVertex);
    //Collection<Node<Integer>> neighbors = g.getNode(smallestVertex).getNeighbors();
    // copy all the vertexUB values for the neighbors of smallestVertex
    ArrayList<Integer> vertexUB_bkup_elements = new ArrayList<Integer>(neighbors.size());
    int[] vertexUB_bkup_values = new int[neighbors.size()];
    //HashMap<Integer, Integer> vertexUB_bkup = new HashMap<Integer, Integer>();
    int i = 0;
    for(Node<Integer> neighbor : neighbors) {
      vertexUB_bkup_elements.add(neighbor.get());
      vertexUB_bkup_values[i] = vertexUB.get(neighbor.get());
      i++;
      //vertexUB_bkup.put(neighbor.get(), vertexUB.get(neighbor.get()));
    }

    List<Integer> cUnionSmallestVertex = new LinkedList<Integer>(c);
    cUnionSmallestVertex.add(smallestVertex);

    //System.out.println("neighbors of " + smallestVertex + ":\n" + neighborsGraph);
    //System.out.println("cUnionSmallestVertex:\n" + cUnionSmallestVertex);

    /*Collection<Node<Integer>> allNodes = g.getNodes();
    List<Node<Integer>> nonNeighbors = new LinkedList<Node<Integer>>();
    for(Node<Integer> node : allNodes) {
      if(!neighbors.contains(node)) {
        nonNeighbors.add(node);
      }
    }
    for(Node<Integer> node : nonNeighbors) {
      g.removeVertex(node.get());
    }*/
    List<Integer> c2 = incMaxClique(neighbors, cUnionSmallestVertex, cMax);
    //List<Integer> c2 = incMaxClique(g, cUnionSmallestVertex, cMax);
    /*for(Node<Integer> node : nonNeighbors) {
      g.addNode(node);
    }*/
    //List<Integer> c2 = incMaxClique(neighborsGraph, c, cMax);
    //System.out.println("In Call #: " + callNumber);
    //System.out.println("second recursive call complete");

    // restore the saved vertexUB values
    //for(Node<Integer> neighbor : neighbors) {
    for(i = 0; i < neighbors.size(); i++) {
      vertexUB.put(vertexUB_bkup_elements.get(i), vertexUB_bkup_values[i]);
      //vertexUB.put(neighbor.get(), vertexUB_bkup.get(neighbor.get()));
    }

    vertexUB.put(smallestVertex, Math.min(vertexUB.get(smallestVertex), (c2.size() - c.size())));

    if(c1.size() >= c2.size()) {
      return c1;
    } else {
      return c2;
    }
  }

  private Integer getSmallestVertex(UndirectedGraph<Integer> g) {
    for(int i = 0; i < vertexOrdering.size(); i++) {
      if(g.contains(vertexOrdering.get(i))) {
        return vertexOrdering.get(i);
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
  public ArrayList<UndirectedGraph<Integer>> getIndependentSetPartition(UndirectedGraph<Integer> g) {
    ArrayList<Integer> indSetVertexOrder = g.degeneracyOrdering( );
    Collections.reverse(indSetVertexOrder);
    return getIndependentSetPartition(g, indSetVertexOrder);
  }

  private ArrayList<UndirectedGraph<Integer>> getIndependentSetPartition(UndirectedGraph<Integer> g, ArrayList<Integer> indSetVertexOrder) {
    ArrayList<UndirectedGraph<Integer>> indSets = new ArrayList<UndirectedGraph<Integer>>();
    UndirectedGraph<Integer> gComplement = g.getComplement();
    IncMaxCliqueSolver indSetSolver = new IncMaxCliqueSolver();
    while(gComplement.size() > 1) {
      //System.out.println("gComplement.size(): " +gComplement.size());
      //System.out.println("gComplement.density(): " +gComplement.density());
      //System.out.println("RUNNING MAX CLIQUE ON COMPLEMENT GRAPH");
      //System.out.println("VERTEX ORDER: " + indSetVertexOrder);
      //System.out.println("GRAPH: \n" + gComplement);
      UndirectedGraph<Integer> indSetComplementNodes = indSetSolver.findMaxClique(gComplement, indSetVertexOrder);
      if(numVOCalls == 100) {
        //throw new RuntimeException("VO debugging, QUIT VO Calls");
      }
      for(Node<Integer> n : indSetComplementNodes) {
        gComplement.removeVertex(n.get());
        indSetVertexOrder.remove(n.get());
      }
      indSets.add(g.subset(indSetComplementNodes.getElements()));
    }
    if(gComplement.size() > 0) {
      indSets.add(g.subset(gComplement.getElements()));
    }
    return indSets;
  }

  // Combining MaxSAT Reasoning and Incremental Upper Bound for the Maximum Clique Problem
  // Li, Fang, Xu 2013
  private ArrayList<Integer> vertexOrdering(UndirectedGraph<Integer> g) {
    numVOCalls++;
    // System.out.println("#####\nvertexOrdering call # " + numVOCalls + ", g.density(): " + g.density());
    // Build the Degeneracy Vertex Ordering
    ArrayList<Integer> vertexOrdering = g.degeneracyOrdering();
    // System.out.println("after building degeneracy ordering, g.density(): " + g.density());

    ArrayList<UndirectedGraph<Integer>> indSets = new ArrayList<UndirectedGraph<Integer>>();

    if(g.density() < 0.70) { // if g is not dense
      // System.out.println("graph sparse, returning degeneracy vertex ordering");
      return vertexOrdering;
    } else {
      ArrayList<Integer> indSetVertexOrder = new ArrayList<Integer>(vertexOrdering);
      Collections.reverse(indSetVertexOrder);
      // System.out.println("graph dense, finding ind set partition");
      //System.out.println("IND SET VERTEX ORDER: " + indSetVertexOrder);
      indSets = getIndependentSetPartition(g,indSetVertexOrder);

      boolean isIrregular = false; //partition is irregular if there are >=2 indSets of size 1
      int numSingleElementSets = 0;
      for(UndirectedGraph<Integer> indSet : indSets) {
        if(indSet.size() == 1) {
          numSingleElementSets++;
        }
      }
      if(numSingleElementSets > 1) {
        isIrregular = true;
      }
      if(isIrregular) {
        // System.out.println("Ind Set Partition is irregular, returning degeneracy ordering");
        return vertexOrdering;
      } else { //return MaxIndSet vertex ordering
        vertexOrdering = new ArrayList<Integer>(g.getElements());
        Collections.sort(vertexOrdering, new MaxIndSetComparator(g, indSets));
        // System.out.println("Ind Set Parition is regular, returning Ind Set Ordering");
        return vertexOrdering;
      }
    }
  }
  private class MaxIndSetComparator implements Comparator<Integer> {
    private ArrayList<UndirectedGraph<Integer>> indSetPartition;
    private UndirectedGraph<Integer> g;

    /**
    * This constructor takes in an ArrayList of the Independent Sets.
    * @param partition the list of Independent Sets in the IND Set partition of a graph
    * @since 0.7.0
    */
    public MaxIndSetComparator(UndirectedGraph<Integer> g, ArrayList<UndirectedGraph<Integer>> partition) {
      this.indSetPartition = partition;
      this.g = g;
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
    public int compare(Integer e1, Integer e2) {
      Node<Integer> n1 = this.g.getNode(e1);
      Node<Integer> n2 = this.g.getNode(e2);
      int n1_index = getIndSetPartitionIndex(e1);
      int n2_index = getIndSetPartitionIndex(e2);
      if(n1_index > n2_index) {
        return -1;
      } else if(n1_index == n2_index) {
        if(n1.numNeighbors() < n2.numNeighbors()) {
          return -1;
        } else if(n1.numNeighbors() == n2.numNeighbors()) {
          return 0;
        } else {
          return 1;
        }
      } else {
        return 1;
      }
    }

    private int getIndSetPartitionIndex(Integer e) {
      for(int i = 0; i < indSetPartition.size(); i++) {
        UndirectedGraph<Integer> g = indSetPartition.get(i);
        if(g.contains(e)) {
          return i;
        }
      }
      return -1;
    }
  }

  private int min(int... nums) {
    int min = nums[0];
    for(int num : nums) {
      if(num < min) {
        min = num;
      }
    }
    return min;
  }

  private LinkedList<Node<Integer>> intersection(Collection<Node<Integer>> a, Collection<Node<Integer>> b) {
    LinkedList<Node<Integer>> intersection = new LinkedList<Node<Integer>>();
    for(Node<Integer> node : a) {
      if(b.contains(node)) {
        intersection.add(node);
      }
    }
    return intersection;
  }
}
