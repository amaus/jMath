package com.aaronpmaus.jMath.graph;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.ArrayList;

/**
* A general class for a Graph, by default a directed graph.
* A Graph is made of Nodes. Nodes represent vertices and are wrappers for a
* generic Object. See the Node class for more information. In this manner a graph
* can be built to represent anything.
* @author Aaron Maus aaron@aaronpmaus.com
* @version 0.8.0
* @since 0.1.0
*/
public class Graph<T extends Comparable<T>>{
  private HashMap<T, Node<T>> adjacencyList;
  private int numEdges;
  private String graphFileName;

  /**
  * A default constructor for the graph.
  * @version 0.7.0
  * @since 0.1.0
  */
  public Graph(){
    this.adjacencyList = new HashMap<T, Node<T>>(100); // default load factor is 0.75
    this.numEdges = 0;
    this.graphFileName = "g.dimacs";
  }

  /**
  * Constructs a graph given the number of nodes that will go in the graph.
  * @param numNodes the number of nodes to go in the graph.
  * @version 0.7.0
  * @since 0.1.0
  */
  public Graph(int numNodes){
    this.adjacencyList = new HashMap<T, Node<T>>((int)((numNodes)/0.75)+1);
    this.numEdges = 0;
    this.graphFileName = "g.dimacs";
  }

  /**
  * A copy constructor for a Graph. Returns a deep copy of the Graph
  * passed in. The deep copy is of all the Nodes and Edges in the Graph,
  * but not of the Objects that the Nodes contains.
  * @param g the Graph to create a copy of
  * @version 0.7.0
  * @since 0.2.0
  */
  public Graph(Graph<T> g){
    Collection<Node<T>> nodes = getDeepCopyNodes(g.getNodes());
    this.adjacencyList = new HashMap<T, Node<T>>((int)((nodes.size())/0.75)+1);
    this.numEdges = 0;
    for(Node<T> node : nodes){
      addNode(node);
    }
    this.graphFileName = g.getGraphFileName();
  }

  /**
  * A constructor that takes a Collection of nodes and builds a graph out
  * of them
  * @param nodes the nodes to be in the graph
  * @version 0.7.0
  * @since 0.2.0
  */
  public Graph(Collection<Node<T>> nodes){
    nodes = getDeepCopyNodes(nodes);
    this.adjacencyList = new HashMap<T, Node<T>>((int)((nodes.size())/0.75)+1);
    this.numEdges = 0;
    for(Node<T> node : nodes){
      addNode(node);
    }
    this.graphFileName = "g.dimacs";
  }

  /**
  * Sets the filename for the graph. This is the name
  * to be used when writing out to file. When a graph
  * is created by reading in from a
  * @param name the name to set it to.
  * @since 0.7.0
  */
  public void setGraphFileName(String name) {
    this.graphFileName = name;
  }

  /**
  * The filename to be used when writing the graph
  * out to file. When a graph is created by reading
  * from a dimacs file, this method will return
  * the name of that file.
  * @return the name of the file associated with this graph
  * @since 0.7.0
  */
  public String getGraphFileName(){
    return this.graphFileName;
  }

  /**
  * Returns a deep copy of the neighborhood from this Graph of the Node that is
  * passed in. The collection includes that node, all its neighbors, and all edges
  * where both end points of the edge are in this list of nodes.
  * @param root the root node of the neighborhood. The neighborhood is this node
  *             and all its neighbors
  * @return a collection of Nodes. This is a deep copy of the nodes and edges
  *         in the neighborhood
  * @since 0.3.0
  */
  public Collection<Node<T>> getNeighborhoodNodes(Node<T> root){
    // in case the collection of nodes passed in was a deep copy of
    // the nodes from this graph, use the actual node from this graph
    Collection<Node<T>> originalNodes = this.getNode(root.get()).getNodeAndNeighbors();
    return getDeepCopyNodes(originalNodes);
  }

  /**
  * Returns a deep copy of the neighborhood from this Graph of the Collection of
  * Nodes that are passed in. The Collection includes all the nodes passed in, all
  * their neighbors, and all edges where both end points of the edge are in this list
  * of nodes.
  * @param nodes the Collection of nodes to get the neighborhood of.
  * @return a Collection of Nodes. This is a deep copy of the nodes and edges
  *         in the neighborhood
  * @since 0.3.0
  */
  public Collection<Node<T>> getNeighborhoodNodes(Collection<Node<T>> nodes){
    // default load factor is 0.75. Create a HashSet large enough that it
    // won't ever need to be enlarged.
    HashSet<Node<T>> originalNodes = new HashSet<Node<T>>(this.size()*100/75+1);
    for(Node<T> node : nodes){
      // in case the collection of nodes passed in was a deep copy of
      // the nodes from this graph, use the actual node from this graph
      originalNodes.addAll(this.getNode(node.get()).getNodeAndNeighbors());
    }
    return getDeepCopyNodes(originalNodes);
  }

  /**
  * Returns the neighborhood from this Graph of the Node passed in.
  * The Neighborhood consists of the node, all of its neighbors,
  * and the set of edges that are between all of these nodes.
  * @param root The node to get the neighborhood around.
  * @return a graph of the neighborhood. This is a deep copy of this subset
  *         of the total graph.
  * @since 0.1.0
  */
  public Graph<T> getNeighborhood(Node<T> root){
    Collection<Node<T>> copyNodes = getNeighborhoodNodes(root);
    return new Graph<T>(copyNodes);
  }

  /**
  * Returns the neighborhood from this Graph of the collection of Nodes passed in.
  * The Neighborhood consists of the Nodes, all their neighbors,
  * and all the edges between all of these nodes.
  * @param nodes the Nodes to get the neighborhood around.
  * @return a graph of the neighborhood. This is a deep
  *         copy of this subset of the total graph.
  * @since 0.3.0
  */
  public Graph<T> getNeighborhood(Collection<Node<T>> nodes){
    Collection<Node<T>> copyNodes = getNeighborhoodNodes(nodes);
    return new Graph<T>(copyNodes);
  }

  /**
  * Returns a Graph of neighbors of the Node passed in.
  * The Neighbors Graph consists of the neighboring Nodes
  * and all the edges between all of these nodes.
  * @param node the Node to get the neighbors of.
  * @return a graph of the neighbors. This is a deep
  *         copy of this subset of the total graph.
  * @since 0.3.0
  */
  public Graph<T> getNeighbors(Node<T> node){
    Collection<Node<T>> copyNodes = getDeepCopyNodes(node.getNeighbors());
    return new Graph<T>(copyNodes);
  }

  /**
  * Returns the complement of this graph, that is, the graph containing all the nodes
  * in the original graph, none of the edges in the original graph, and all of the edges
  * NOT in the original graph
  * @return the complement of the graph
  * @since 0.3.0
  */
  public Graph<T> getComplement(){
    return new Graph<T>(getComplementNodes());
  }

  /*
  * Returns a deep copy of all the nodes in the graph.
  * The copy is a deep copy of all nodes and all edges
  * where both end points of the edge are in this list of nodes.
  */
  private Collection<Node<T>> getDeepCopyNodes(){
    return getDeepCopyNodes(getNodes());
  }

  /*
  * Returns a deep copy of all the nodes passed in.
  * The copy is a deep copy of all nodes and all edges
  * where both end points of the edge are in this list of nodes.
  */
  private Collection<Node<T>> getDeepCopyNodes(Collection<Node<T>> originalNodes){
    // first get a list of these nodes
    // store as a hashmap for constant time lookups
    // about the parameter. The default load factor is 0.75. So we want to instantiate
    // the HashMap with an initialCapacity large enough so that we never increase
    // the capacity. We know exactly how many nodes will be added to this HashMap,
    // it's the number of neighbors+1 (for the root).
    // Setting the initial capacity to (numNeighbors+1)/0.75 + 1 will do the trick.
    HashMap<T,Node<T>> copyNodes = new HashMap<T,Node<T>>((int)((originalNodes.size()+1)/0.75+1));
    for(Node<T> node : originalNodes){
      Node<T> newNode = new Node<T>(node.get());
      copyNodes.put(node.get(), newNode);
    }

    for(Node<T> originalNode : originalNodes){
      Collection<Edge<T>> edges = originalNode.getEdges();
      Node<T> newNode = copyNodes.get(originalNode.get());
      for(Edge<T> edge : edges){
        Node<T> neighbor = edge.getEnd();
        if(copyNodes.containsKey(neighbor.get())){
          newNode.addEdge(new Edge<T>(newNode, copyNodes.get(neighbor.get()), edge.getWeight()));
        }
      }
    }
    return copyNodes.values();
  }

  /**
  * Returns a the nodes that would belong to the complement of this graph.
  * that is, a copy of all the Nodes with all the edges that are NOT in
  * this graph
  * @return a Collection of Nodes that would belong to the complement of this graph
  * @since 0.3.0
  */
  public Collection<Node<T>> getComplementNodes(){
    // create a new node for every node in the graph.
    HashMap<T, Node<T>> copyNodes = getCopyNodesNoEdges();
    Collection<Node<T>> originalNodes = getNodes();
    for(Node<T> node : originalNodes){
      for(Node<T> possibleNeighbor: originalNodes){
        if(node != possibleNeighbor && !node.hasNeighbor(possibleNeighbor)){
          Node<T> copyNode = copyNodes.get(node.get());
          copyNode.addNeighbor(copyNodes.get(possibleNeighbor.get()));
        }
      }
    }
    return copyNodes.values();
  }

  /**
  * Returns the number of Nodes in this graph
  * @return the number of Nodes in this graph
  * @since 0.1.0
  */
  public int size(){
    return this.adjacencyList.size();
  }

  /**
  * @return a {@code Collection<Node<T>>} of the nodes
  * @since 0.1.0
  */
  public final Collection<Node<T>> getNodes(){
    return this.adjacencyList.values();
  }

  /*
  * Returns a copy of the nodes without any edges.
  * returns as a HashMap.
  */
  private HashMap<T,Node<T>> getCopyNodesNoEdges(){
    Collection<Node<T>> originalNodes = getNodes();
    HashMap<T,Node<T>> copyNodes = new HashMap<T,Node<T>>((int)((originalNodes.size()+1)/0.75+1));
    for(Node<T> node : originalNodes){
      Node<T> newNode = new Node<T>(node.get());
      copyNodes.put(node.get(), newNode);
    }
    return copyNodes;
  }

  /**
  * Add a node to the graph. Only adds the node if it is not already in the graph.
  * @param n the node to add to the graph.
  * @since 0.1.0
  */
  public void addNode(Node<T> n){
    if(!containsNode(n)){
      //System.out.println("Adding node: " + n.hashCode());
      adjacencyList.put(n.get() ,n);
      incrementNumEdges(n.numNeighbors());
    }
  }

  /**
  * Returns the node from the graph that holds the given object.
  * @param obj the object of the node to be retrieved
  * @return the node with that object or null if it is not in the graph.
  * @since 0.1.0
  */
  public Node<T> getNode(T obj){
    return this.adjacencyList.get(obj);
  }

  /**
  * Add an edge to the graph. Adds nodes containing these two objects
  * to the graph if they are not already in the graph. Then adds
  * an edge from start to end.
  * @param start the start object of the edge
  * @param end the end object of the edge
  * @since 0.8.0
  */
  public void addEdge(T start, T end){
    addEdge(new Node<T>(start), new Node<T>(end));
  }

  /**
  * Add an edge to the graph. Adds both nodes if they are not already in
  * the graph. Then adds an edge from start to end.
  * @param start the start node of the edge
  * @param end the end node of the edge
  * @since 0.1.0
  */
  public void addEdge(Node<T> start, Node<T> end){
    addNode(start);
    addNode(end);
    getNode(start.get()).addNeighbor(getNode(end.get()));
    incrementNumEdges();
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
  public void addEdge(T start, T end, double weight){
    addEdge(new Node<T>(start), new Node<T>(end), weight);
  }

  /**
  * Add an edge to the graph. Adds both nodes if they are not already in
  * the graph. Then adds an edge from start to end.
  * @param start the start node of the edge
  * @param end the end node of the edge
  * @param weight the weight of the edge
  * @since 0.1.0
  */
  public void addEdge(Node<T> start, Node<T> end, double weight){
    addNode(start);
    addNode(end);
    getNode(start.get()).addEdge(new Edge<T>(getNode(start.get()),
    getNode(end.get()),
    weight));
    incrementNumEdges();
  }

  /**
  * Remove an edge from the graph.
  * @param start the node at the start of the edge
  * @param end the node at the end of the edge
  * @since 0.1.0
  */
  public void removeEdge(Node<T> start, Node<T> end){
    if(containsNode(start)){
      start.removeNeighbor(end);
      decrementNumEdges();
    }
  }

  private void incrementNumEdges(){
    this.numEdges++;
  }

  private void incrementNumEdges(int num){
    this.numEdges += num;
  }

  private void decrementNumEdges(){
    this.numEdges--;
  }

  private void decrementNumEdges(int num){
    this.numEdges -= num;
  }

  /**
  * Calculates the number of edges. To be used for testing purposes to
  * check that we are keeping track of the number of edges correctly.
  * @return the number of edges
  * @since 0.1.0
  */
  public int getNumEdges(){
    int numEdges = 0;
    for(Node<T> node : this.adjacencyList.values()){
      numEdges += node.numNeighbors();
    }
    return numEdges;
  }

  /**
  * Returns the number of edges of the graph. a constant time operation
  * @return the number of edges of the graph.
  * @since 0.1.0
  */
  public int numEdges(){
    return this.numEdges;
  }

  /**
  * checks if a node is in the graph.
  * @param n the node to check for
  * @return true if the graph contains this node, false otherwise.
  * @since 0.1.0
  */
  public boolean containsNode(Node<T> n){
    T obj = n.get();
    return adjacencyList.containsKey(obj);
  }

  // remove the node from the graph and all edges that
  // go to this node.
  /**
  * Removes this node and all edges leading to or from it from the graph.
  * @param nodeToBeRemoved the node to remove
  * @version 0.2.0
  * @since 0.1.0
  */
  public void removeNodeFromGraph(Node<T> nodeToBeRemoved){
    nodeToBeRemoved = this.getNode(nodeToBeRemoved.get());
    // check all nodes in this graph to see if there is an edge from it to
    // this node
    for(Node<T> node : this.adjacencyList.values()){
      if(node.hasNeighbor(nodeToBeRemoved)){
        // if there is an edge from a node to nodeToBeRemoved,
        // remove that edge from this graph.
        this.removeEdge(node, nodeToBeRemoved);
      }
    }
    removeNodeFromAdjacencyList(nodeToBeRemoved);
  }

  // this is a hack so that the subclass
  // can remove from the adjacency list. It only removes
  // from the adjacency list. It does not remove any other
  // edges from the graph
  /**
  * Removes this node from the graph. Does not remove edges leading to this node.
  * This method is intended as a helper method for this class and subclasses. Do
  * not call this method, rather call {@code removeNodeFromGraph(Node<T&> n)}, it will
  * properly maintain all edges of the graph. This method does not guarantee that.
  * I wish that java had an access modifier to restrict access to only subclasses.
  * Also, I know this is not the best way of going about this. I'm still trying to
  * figure out a better way. Any suggestions?
  * @param n the node to remove.
  * @since 0.2.0
  */
  protected void removeNodeFromAdjacencyList(Node<T> n){
    this.adjacencyList.remove(n.get());
    decrementNumEdges(n.numNeighbors());
  }

  /**
  * Returns the density of the graph
  * @return the density of the graph
  * @since 0.3.0
  */
  public double density(){
    return ((double)getNumEdges())/(size()*(size()-1));
  }

  @Override
  /**
  * @return a string representation of this graph in the form of an adjacency list.
  * @since 0.1.0
  */
  public String toString(){
    String str = "";
    for(Node<T> node : this.adjacencyList.values()){
      str += node.toString() + "\n";// + " #neighbors: " + node.numNeighbors() + "\n";
    }
    return str;
  }
}
