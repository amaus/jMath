package com.aaronpmaus.jMath.graph;
import java.util.HashMap;
import java.util.HashSet;
import java.util.AbstractCollection;
import java.util.Collection;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Comparator;
import java.util.Stack;

/**
* A Graph is a Directed Graph.
* <p>
* A Graph is composed of elements which are the vertices and the edges between them. The elements in
* the graph must be unique.
* @version 0.14.0
* @since 0.1.0
*/
public class Graph<T extends Comparable<? super T>> implements Iterable<Node<T>>{
  private HashMap<T, Node<T>> adjacencyList;
  private int numEdges;
  private String graphFileName;

  /**
  * The default constructor for the graph, builds a graph with no verties. Vertices and edges can
  * be added via addEdge() and addNode().
  * @version 0.7.0
  * @since 0.1.0
  */
  public Graph(){
    this.adjacencyList = new HashMap<T, Node<T>>(100); // default load factor is 0.75
    this.numEdges = 0;
    this.graphFileName = "g.dimacs";
  }

  /**
  * Constructs an empty graph, prepared to hold the specified number of vertices.
  * @param numVertices the number of vertices to go in the graph.
  * @version 0.7.0
  * @since 0.1.0
  */
  public Graph(int numVertices){
    this.adjacencyList = new HashMap<T, Node<T>>((int)((numVertices)/0.75)+1);
    this.numEdges = 0;
    this.graphFileName = "g.dimacs";
  }

  /**
  * A copy constructor.
  * <p>
  * Returns a deep copy of the Graph passed in. The deep copy is of all the vertices and edges in
  * the Graph, but not of the elements that the vertices contain.
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
  * Internal use one, a constructor that takes a Collection of Nodes (vertices) and builds a graph
  * out of them of them,
  * @param nodes the nodes to be in the graph
  * @version 0.7.0
  * @since 0.2.0
  */
  protected Graph(Collection<Node<T>> nodes){
    nodes = getDeepCopyNodes(nodes);
    this.adjacencyList = new HashMap<T, Node<T>>((int)((nodes.size())/0.75)+1);
    this.numEdges = 0;
    for(Node<T> node : nodes){
      addNode(node);
    }
    this.graphFileName = "g.dimacs";
  }

  /**
  * Return the subset of the Graph containing the vertices with the elements provided.
  * @param elements a Collection of the elements specifying the subset
  * @return an UndirectedGraph containing every vertex containing one of the elements and all the
  * edges between these vertices.
  * @since 0.14.0
  */
  public Graph<T> subset(Collection<T> elements){
    LinkedList<Node<T>> nodes = new LinkedList<Node<T>>();
    for(T element : elements){
      if(this.contains(element)) {
        nodes.add(this.getNode(element));
      }
    }
    return new Graph<T>(nodes);
  }

  /**
  * Return the subset of the Graph containing the vertices with the elements provided.
  * @param elements a Collection of the elements specifying the subset
  * @return an UndirectedGraph containing every vertex containing one of the elements and all the
  * edges between these vertices.
  * @since 0.14.0
  */
  private Graph<T> subset(List<Node<T>> nodes){
    return new Graph<T>(nodes);
  }

  /**
  * Return a Graph of neighbors of the Node passed in.
  * <p>
  * The Neighbors Graph consists of the adjacent vertices and all the edges between these vertices.
  * @param element the element to get the neighbors of.
  * @return a graph of the neighbors. This is a deep copy of this subset of the total graph.
  * @since 0.14.0
  */
  public Graph<T> getNeighbors(T element){
    return subset(getNode(element).getNeighbors());
  }

  /**
  * Return the neighborhood from this Graph of the Node passed in.
  * <p>
  * The Neighborhood consists of the node, all of its neighbors, and the set of edges that are
  * between all of these nodes.
  * @param element The node to get the neighborhood around.
  * @return a graph of the neighborhood. This is a deep copy of this subset of the total graph.
  * @since 0.1.0
  */
  public Graph<T> getNeighborhood(T element){
    return subset(getNode(element).getNodeAndNeighbors());
  }

  /**
  * Return the neighborhood from this Graph of the collection of elements passed in.
  * <p>
  * The Neighborhood consists of those vertices, all their neighbors, and all the edges between
  * these vertices.
  * @param elements the elements of the Graph to get the neighborhood around.
  * @return a graph of the neighborhood. This is a deep copy of this subset of the total graph.
  * @since 0.14.0
  */
  public Graph<T> getNeighborhood(Collection<T> elements){
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
    return new Graph<T>(neighborhoodElements);
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
  * The filename to be used when writing the graph out to file. When a graph is created by reading
  * from a dimacs file, this method will return the name of that file.
  * @return the name of the file associated with this graph
  * @since 0.7.0
  */
  public String getGraphFileName(){
    return this.graphFileName;
  }

  /**
  * Return the complement of this graph.
  *
  * The complement is the graph containing all the nodes in the original graph, none
  * of the edges in the original graph, and all of the edges NOT in the original graph.
  *
  * @return the complement of the graph
  * @since 0.3.0
  */
  public Graph<T> getComplement(){
    return new Graph<T>(getComplementNodes());
  }

  /*
  * Return a deep copy of all the nodes in the graph.
  *
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
  * Return the nodes that would belong to the complement of this graph.
  *
  * These are a copy of all the Nodes with all the edges that are NOT in
  * this graph.
  *
  * @return a Collection of Nodes that would belong to the complement of this graph
  * @since 0.3.0
  */
  protected Collection<Node<T>> getComplementNodes(){
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
  * Return the number of Nodes in this graph
  * @return the number of Nodes in this graph
  * @since 0.1.0
  */
  public int size(){
    return this.adjacencyList.size();
  }

  /**
  * @return a {@code List<Node<T>>} of the nodes
  * @since 0.1.0
  */
  protected List<Node<T>> getNodes(){
    return new LinkedList<Node<T>>(this.adjacencyList.values());
  }

  /**
  * @return all the elements in this Graph
  * @since 0.14.0
  */
  public List<T> getElements(){
    LinkedList<T> elements = new LinkedList<T>();
    for(Node<T> node : this){
      elements.add(node.get());
    }
    return elements;
  }

  /**
  * Returns a set of all the edges in this Graph.
  *
  * @return a Collection of the edges in this Graph
  * @since 0.11.0
  */
  public Collection<? extends Edge<T>> getEdges(){
    HashSet<Edge<T>> edges = new HashSet<Edge<T>>((int)(numEdges()/0.75) + 1);
    for(Node<T> node : this) {
      for(Edge<T> e : node.getEdges()) {
        edges.add(e);
      }
    }
    return edges;
  }

  /*
  * Return a copy of the nodes without any edges.
  * returns a HashMap.
  */
  private HashMap<T,Node<T>> getCopyNodesNoEdges(){
    //Collection<Node<T>> originalNodes = getNodes();
    HashMap<T,Node<T>> copyNodes = new HashMap<T,Node<T>>((int)((this.size()+1)/0.75+1));
    for(Node<T> node : this){
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
  private void addNode(Node<T> n){
    if(!contains(n.get())){
      //System.out.println("Adding node: " + n.hashCode());
      adjacencyList.put(n.get() ,n);
      incrementNumEdges(n.numNeighbors());
    }
  }

  /**
  * Add an element to the graph. Only adds the element if it is not already in the graph.
  * @param e the element to add to the graph.
  * @since 0.14.0
  */
  public void addVertex(T e){
    addNode(new Node<T>(e));
  }

  /**
  * Returns the node from the graph that holds the given object.
  * @param element the object of the node to be retrieved
  * @return the node with that object or null if it is not in the graph.
  * @since 0.1.0
  * @throws IllegalArgumentException if there is no node containing obj.
  */
  protected Node<T> getNode(T element){
    if(!this.adjacencyList.containsKey(element)){
      throw new NoSuchElementException(String.format("Node %s not in graph.", element));
    }
    return this.adjacencyList.get(element);
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
    addEdge(start, end, 1.0);
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
    if(!contains(start)) {
      addVertex(start);
    }
    if(!contains(end)) {
      addVertex(end);
    }
    if(!hasEdge(start, end)) {
      getNode(start).addNeighbor(getNode(end), weight);
      incrementNumEdges();
    }
  }

  /**
  * Remove an edge from the graph.
  * @param start the node at the start of the edge
  * @param end the node at the end of the edge
  * @since 0.1.0
  */
  public void removeEdge(T start, T end){
    if(hasEdge(start, end)) {
      getNode(start).removeNeighbor(getNode(end));
      decrementNumEdges();
    }
  }

  /**
  * @param start the vertex at the start of the edge
  * @param end the vertex at the end of the edge
  * @return true if there is an edge from start to end, false otherwise
  * @since 0.14.0
  */
  public boolean hasEdge(T start, T end){
    if(this.contains(start) && this.contains(end)){
      if(this.getNode(start).hasNeighbor(this.getNode(end))){
        return true;
      }
    }
    return false;
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
  * Check if there is a vertec containing element in this graph.
  * @param element the element to check for.
  * @return true if one of the vertices in the graph contains element, false otherwise.
  * @since 0.1.0
  */
  public boolean contains(T element){
    return adjacencyList.containsKey(element);
  }

  /**
  * If this node containing this value is in the graph, remove it and all edges leading to or from
  * it from the graph. If the node is not in the graph, do nothing.
  * @param element the element to remove
  * @since 0.11.0
  */
  public void removeNode(T element){
    if(contains(element)){
      Node<T> node = this.getNode(element);
      // check all nodes in this graph to see if there is an edge from it to
      // this node
      for(Node<T> n : this) {
        if(n.hasNeighbor(node)){
          // if there is an edge from a node to nodeToBeRemoved,
          // remove that edge from this graph.
          this.removeEdge(n.get(), node.get());
        }
      }
      decrementNumEdges(node.numNeighbors());
      this.adjacencyList.remove(node.get());
    }
  }

  /**
  * Return the shortest path from source to target.
  * <p>
  * This method implements Dijkstra's algorithm with a priority queue to find the shortest
  * path between two vertices. The nodes that contain the source and target values are used
  * as the end points.
  * @param source the source to calculate the path from
  * @param target the target to calculate the path to
  * @return a List containing the path from source to target, or an empty list if no path exists.
  * @throws IllegalArgumentException if there are no nodes containing the source and target values.
  */
  public List<T> shortestPath(T source, T target){
    Node<T> sourceNode = getNode(source);
    Node<T> targetNode = getNode(target);

    // Create a HashMap large enough so that the default load factor (0.75) will not be
    // exceeded when all elements are added to it. This avoids any costly increases in
    // capacity since the map will never get too full.
    HashMap<Node<T>, Double> distToSource =
        new HashMap<Node<T>, Double>( (int)((this.size()+1)/0.75+1) );

    HashMap<Node<T>, Node<T>> prevNodeInPath =
        new HashMap<Node<T>, Node<T>>( (int)((this.size()+1)/0.75+1) );

    distToSource.put(sourceNode,0.0);
    prevNodeInPath.put(sourceNode,null);

    for(Node<T> node : this) {
      if(!node.equals(sourceNode)){
        distToSource.put(node, Double.MAX_VALUE);
        prevNodeInPath.put(node, null);
      }
    }

    PriorityQueue<Node<T>> unvisitedNodes =
        new PriorityQueue<Node<T>>( size(),
            new Comparator<Node<T>>(){
              public int compare(Node<T> a, Node<T> b){
                double diff = distToSource.get(a) - distToSource.get(b);
                if(diff < 0){
                  return -1;
                } else if (diff > 0){
                  return 1;
                }
                return 0;
              }
            }
        );

    unvisitedNodes.offer(sourceNode);

    while(!unvisitedNodes.isEmpty()){
      Node<T> min = unvisitedNodes.poll();
      //System.out.println("Visiting node: " + min.get());
      if(min.equals(targetNode)){
        break;
      }

      for(Node<T> neighbor : min.getNeighbors()){
        double distance = distToSource.get(min) + min.getEdgeWeight(neighbor);
        //System.out.printf("Neighbor %d distance to source is %.2f\n", neighbor.get(), distToSource.get(neighbor));
        //System.out.printf("Distance to neighbor %d is %.2f\n",neighbor.get(),distance);
        if(distance < distToSource.get(neighbor)){
          distToSource.put(neighbor,distance);
          prevNodeInPath.put(neighbor, min);
          if(!unvisitedNodes.contains(neighbor)){
            unvisitedNodes.offer(neighbor);
          } else {
            // update (decrease) the priority of neighbor. The only way to do this is to
            // remove the element from the queue and re-add it so that the comparator which
            // depends on distToSource is used (with the new value from distToSource) to add
            // neighbor to the right place in the queue.
            unvisitedNodes.remove(neighbor);
            unvisitedNodes.offer(neighbor);
          }
        }
      }
    }

    // build the list of nodes in the shortest path from source to target
    LinkedList<T> path = new LinkedList<T>();
    if(prevNodeInPath.get(targetNode) != null){
      path.add(targetNode.get());
    }
    Node<T> nodeInPath = targetNode;
    while(prevNodeInPath.get(nodeInPath) != null){
      path.add(0,prevNodeInPath.get(nodeInPath).get());
      nodeInPath = prevNodeInPath.get(nodeInPath);
    }
    return path;
  }

  /**
  * Return a list a list of the elements in this graph as traversed by a Depth First Search.
  * @param source the starting point for the Depth First Search
  * @return a list containing the elements as traversed by DFS
  * @throws NoSuchElementException if source is not in the graph
  * @since 0.13.0
  */
  public List<T> depthFirstSearch(T source){
    if(!contains(source)){
      throw new NoSuchElementException(String.format("source is not in graph."));
    }
    Stack<Node<T>> stack = new Stack<Node<T>>();
    HashSet<Node<T>> visited = new HashSet<Node<T>>();
    LinkedList<T> list = new LinkedList<T>();
    stack.push(getNode(source));
    while(!stack.empty()){
      Node<T> node = stack.pop();
      if(!visited.contains(node)){
        visited.add(node);
        list.add(node.get());
        for(Node<T> neighbor : node.getNeighbors()){
          stack.push(neighbor);
        }
      }
    }
    return list;
  }

  /**
  * Returns the density of the graph
  * @return the density of the graph
  * @since 0.3.0
  */
  public double density(){
    return ((double)getNumEdges())/(size()*(size()-1));
  }

  /**
  * {@inheritDoc}
  */
  @Override
  public Iterator<Node<T>> iterator(){
    return this.adjacencyList.values().iterator();
  }

  /**
  * @return a string representation of this graph in the form of an adjacency list.
  * @since 0.1.0
  */
  @Override
  public String toString(){
    String str = "";
    for(Node<T> node : this){
      str += node.toString() + "\n";// + " #neighbors: " + node.numNeighbors() + "\n";
    }
    return str;
  }

  @Override
  public int hashCode(){
    return getNodes().hashCode() + getEdges().hashCode();
  }

  /**
  * Two graphs are equal if they both contains the same set of Nodes and Edges.
  * @param obj the other graph
  * @return true if both this and other are graphs which contain the same sets of Nodes and
  *   Edges, false otherwise.
  */
  @Override
  @SuppressWarnings("unchecked")
  public boolean equals(Object obj){
    if(this.getClass().isInstance(obj)){
      Graph<T> other = this.getClass().cast(obj);

      boolean ret = true;
      // hash set's equals doesn't depend on order.
      HashSet<Node<T>> theseNodes = new HashSet<Node<T>>(this.getNodes());
      HashSet<Node<T>> otherNodes = new HashSet<Node<T>>(other.getNodes());
      ret = ret && theseNodes.equals(otherNodes);
      HashSet<Edge<T>> theseEdges = new HashSet<Edge<T>>(this.getEdges());
      HashSet<Edge<T>> otherEdges = new HashSet<Edge<T>>(other.getEdges());
      ret = ret && theseEdges.equals(otherEdges);
      return ret;
    }
    return false;
  }
}
