package com.aaronpmaus.jMath.graph;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.Date;
import java.util.Comparator;
import java.util.NoSuchElementException;

/**
* An UndirectedGraph.
* <p>
* An UndirectedGraph is composed of elements which are the vertices and the edges between them. The
* elements in the graph must be unique.
* @see com.aaronpmaus.jMath.graph.Graph
* @version 0.14.0
* @since 0.1.0
*/
public class UndirectedGraph<T extends Comparable<? super T>> extends Graph<T>{
  private static int maxPrintLevel = 0;
  private boolean verbose = false;

  /**
  * The default constructor for an UndirectedGraph, builds an UndirectedGraph with no verties.
  * Vertices and edges can be added via addEdge() and addNode().
  * @since 0.1.0
  */
  public UndirectedGraph(){
    super();
  }

  /**
  * Constructs an empty graph, prepared to hold the specified number of vertices.
  * @param numVertices the number of vertices the graph will hold.
  * @since 0.1.0
  */
  public UndirectedGraph(int numVertices){
    super(numVertices);
  }

  /**
  * Constructs an UndirectedGraph from a Graph.
  * <p>
  * Returns a deep copy of all the vertices and edges in the Graph where all edges are now
  * undirected. The elements within the vertices are not copied.
  * @param g the Graph to build from the UndirectedGraph
  * @since 0.1.0
  */
  public UndirectedGraph(Graph<T> g) {
    super(g);
    for(Node<T> node : g.getNodes()) {
      for(Node<T> neighbor : node.getNeighbors()) {
        // We know there is an edge from node to neighbor,
        // but if there is not an edge from the neighbor to this
        // node, then add it.
        if(!neighbor.hasNeighbor(node)) {
          super.addEdge(neighbor.get(), node.get());
        }
      }
    }
  }

  /**
  * A copy constructor.
  * <p>
  * Returns a deep copy of the UndirectedGraph passed in. The deep copy is of all the vertices and
  * edges in the UndirectedGraph, but not of the elements that the vertices contain.
  * @param g the UndirectedGraph to Copy
  * @since 0.2.0
  */
  public UndirectedGraph(UndirectedGraph<T> g){
    super(g);
  }

  /**
  * A constructor that takes a Collection of nodes and builds an
  * UndirectedGraph out of them.
  * @param nodes the nodes to be in the graph
  * @since 0.2.0
  */
  private UndirectedGraph(Collection<Node<T>> nodes){
    super(nodes);
  }

  /**
  * Return the subset of the Graph containing the vertices with the elements provided.
  * @param elements a Collection of the elements specifying the subset
  * @return an UndirectedGraph containing every vertex containing one of the elements and all the
  * edges between these vertices.
  * @since 0.14.0
  */
  public UndirectedGraph<T> subset(Collection<T> elements){
    LinkedList<Node<T>> nodes = new LinkedList<Node<T>>();
    for(T element : elements){
      if(this.contains(element)) {
        nodes.add(this.getNode(element));
      }
    }
    return new UndirectedGraph<T>(nodes);
  }

  /**
  * Return the subset of the Graph containing the vertices with the elements provided.
  * @param nodes a nodes of the elements specifying the subset
  * @return an UndirectedGraph containing every vertex containing one of the elements and all the
  * edges between these vertices.
  * @since 0.14.0
  */
  private UndirectedGraph<T> subset(List<Node<T>> nodes){
    return new UndirectedGraph<T>(nodes);
  }

  /**
  * Return a Graph of neighbors of the Node passed in.
  * <p>
  * The Neighbors Graph consists of the adjacent vertices and all the edges between these vertices.
  * @param element the element to get the neighbors of.
  * @return a graph of the neighbors. This is a deep copy of this subset of the total graph.
  * @since 0.14.0
  */
  public UndirectedGraph<T> getNeighbors(T element){
    return subset(getNode(element).getNeighbors());
  }

  /**
  * {@inheritDoc}
  * @since 0.1.0
  */
  @Override
  public UndirectedGraph<T> getNeighborhood(T element){
    return subset(getNode(element).getNodeAndNeighbors());
  }

  /**
  * {@inheritDoc}
  * @since 0.14.0
  */
  @Override
  public UndirectedGraph<T> getNeighborhood(Collection<T> elements){
    // default load factor is 0.75. Create a HashSet large enough that it
    // won't ever need to be enlarged.
    HashSet<Node<T>> neighborhoodElements = new HashSet<Node<T>>((int)((this.size()+1)/0.75+1));
    for(T element : elements){
      if(!contains(element)){
        throw new NoSuchElementException(String.format("Cannot get neighborhood of nodes, "
          +"Node %s not in graph.", element));
      }
      neighborhoodElements.addAll(this.getNode(element).getNodeAndNeighbors());
    }
    return new UndirectedGraph<T>(neighborhoodElements);
  }

  /**
  * {@inheritDoc}
  * @since 0.3.0
  */
  @Override
  public UndirectedGraph<T> getComplement(){
    return new UndirectedGraph<T>(getComplementNodes());
  }

  /**
  * Given a set of objects, check if the group of nodes containing those
  * objects form a clique in the graph.
  * @param elements the elements to check
  * @return true if the nodes containing these objects are a clique. false otherwise
  * @since 0.7.0
  */
  public boolean checkIfClique(Collection<T> elements){
    //ArrayList<Node<T>> nodesInClique = new ArrayList<Node<T>>(elements.size());
    //for(T element : elements){
      //nodesInClique.add(getNode(element));
    //}
    //UndirectedGraph<T> clique = new UndirectedGraph<T>(nodesInClique);
    //return clique.isClique( );
    return this.subset(elements).isClique();
  }

  /**
  * Add an edge to the graph. Adds nodes containing these two objects
  * to the graph if they are not already in the graph. Then adds
  * an edge from start to end.
  * @param start the start object of the edge
  * @param end the end object of the edge
  * @since 0.8.0
  */
  @Override
  public void addEdge(T start, T end){
    super.addEdge(start, end);
  }

  /**
  * Add an edge to the graph. Adds nodes containing these two objects
  * to the graph if they are not already in the graph. Then adds
  * an edge from start to end.
  * @param start the start object of the edge
  * @param end the end object of the edge
  * @param weight, the weight of the edge
  * @since 0.8.0
  */
  @Override
  public void addEdge(T start, T end, double weight){
    super.addEdge(start, end, weight);
    super.addEdge(end, start, weight);
  }

  /**
  * {@inheritDoc}
  * @since 0.1.0
  */
  @Override
  public void removeNode(T nodeValue){
    if(contains(nodeValue)){
      Node<T> node = this.getNode(nodeValue);
      // for ever neighbor of nodeToBeRemoved,
      for(Node<T> neighbor : node.getNeighbors()){
        super.removeEdge(neighbor, node);
      }
      removeNodeFromAdjacencyList(node);
    }
  }

  /**
  * A method to return the degeneracy ordering of a graph
  * @return an ArrayList of Nodes representing the degeneracy ordering.
  *         the smallest vertex is at the 0th index
  * @since 0.7.0
  */
  public ArrayList<Node<T>> degeneracyOrdering( ){
    ArrayList<Node<T>> vertexOrdering = new ArrayList<Node<T>>(this.size());
    // Build the Degeneracy Vertex Ordering
    UndirectedGraph<T> temp = new UndirectedGraph<T>(this);
    while(temp.size() > 0){
      // get the node with the smallest degree in temp
      Node<T> theSmallestNodeTemp = Collections.min(temp.getNodes());
      // get a reference to that node in the original graph
      Node<T> theSmallestNodeOriginal = this.getNode(theSmallestNodeTemp.get());
      // add the original node reference to vertexOrdering
      vertexOrdering.add(theSmallestNodeOriginal);
      // remove the node from temp
      temp.removeNode(theSmallestNodeTemp.get());
    }
    return vertexOrdering;
  }


  /**
  * {@inheritDoc}
  */
  @Override
  public double density(){
    // the implementation of the undirected graph includes both forward and back edges.
    // ie, double the edges. so we don't need to multiply by 2.
    return super.density();
  }

  /**
  * Returns a set of all the edges in this UndirectedGraph.
  * @return a Collection of the edges in this UndirectedGraph
  * @since 0.11.0
  */
  public Collection<? extends Edge<T>> getEdges(){
    HashSet<UndirectedEdge<T>> edges = new HashSet<UndirectedEdge<T>>((int)(numEdges()/0.75) + 1);
    for(Node<T> node : this) {
      for(Edge<T> e : node.getEdges()) {
        edges.add(new UndirectedEdge<T>(e));
      }
    }
    //return edges;
    ArrayList<UndirectedEdge<T>> edgesSorted = new ArrayList<UndirectedEdge<T>>(edges);
    Collections.sort(edgesSorted);

    /*new Comparator<UndirectedEdge<T>>() {
      public int compare(UndirectedEdge<T> e1, UndirectedEdge<T> e2) {
        int comparison = e1.getStart().get().compareTo(e2.getStart().get());
        if(comparison == 0) {
          return e1.getEnd().get().compareTo(e2.getEnd().get());
        }
        return comparison;
      }
    });*/
    return edgesSorted;
  }

  /**
  * Returns the number of edges in this UndirectedGraph.
  * @return an int representing the number of edges in this UndirectedGraph
  * @since 0.3.0
  */
  @Override
  public int numEdges(){
    return super.numEdges()/2;
  }

  /**
  * Checks if a graph is a clique. A graph is a clique if there are N*(N-1)/2
  * edges in it.
  * @return true if the graph is a clique, false otherwise
  * @since 0.7.0
  */
  public boolean isClique( ){
    int numEdges = numEdges();
    int numNodes = size();
    int edgesRequired = (numNodes * (numNodes - 1))/2;
    if(numEdges == edgesRequired){
      return true;
    }
    return false;
  }
}
