package com.aaronpmaus.jMath.graph;
import java.util.LinkedHashMap;
import java.util.Collection;
import java.util.List;
import java.util.LinkedList;
import java.util.ArrayList;
import java.lang.ClassCastException;

/**
* A Node for a graph. A Node represents a vertex and is a wrapper for an object. In addition to its
* object, it also has Edges to other nodes.
* <p>
* The Object can be anything, and the generic type allows for graphs to be constructed containing
* any type of Object, as long as it is comparable.
* @author Aaron Maus aaron@aaronpmaus.com
* @version 0.11.0
* @since 0.1.0
*/
public class Node<T extends Comparable<? super T>> implements Comparable<Node<T>>{
  private T element;
  // the key is the node at the end of the edge
  private LinkedHashMap<Node<T>, Edge<T>> edges;

  /**
  * Constructor for a Node.
  * @param element the element that this node holds
  * @since 0.1.0
  */
  public Node(T element) {
    this.element = element;
    this.edges = new LinkedHashMap<Node<T>,Edge<T>>();
  }

  protected Node(Node<T> other) {
    this(other.get());
    for(Node<T> neighbor : other.getNeighbors()) {
      this.addNeighbor(neighbor, other.getEdgeWeight(neighbor));
    }
  }

  /**
  * @return the number of neighbors of this node.
  * @since 0.1.0
  */
  public int numNeighbors() {
    return this.edges.size();
  }

  /**
  * Returns the object that is Node wraps.
  * @return the Object that this Node wraps.
  * @since 0.4.0
  */
  public T get() {
    return this.element;
  }

  /**
  * Change the element that this node holds.
  * @param element the new element for this node to hold
  * @since 0.7.0
  */
  public void set(T element) {
    this.element = element;
  }

  /**
  * add an edge to this node
  * @param e the edge to add to this node
  * @since 0.1.0
  */
  public void addEdge(Edge<T> e) {
    this.edges.put(e.getEnd(), e);
  }

  /**
  * Add a neighbor to this node. Accomplished the same thing
  * as @see Node:addEdge(Edge&lt;T&gt; e)
  * @param n the node to add as a neighbor
  * @since 0.1.0
  */
  public void addNeighbor(Node<T> n) {
    this.edges.put(n, new Edge<T>(this, n));
  }

  /**
  * Add a neighbor to this node. Accomplished the same thing
  * as @see Node:addEdge(Edge&lt;T&gt; e)
  * @param n the node to add as a neighbor
  * @param weight the weight of the edge from this node to n
  * @since 0.1.0
  */
  public void addNeighbor(Node<T> n, double weight) {
    this.edges.put(n, new Edge<T>(this, n, weight));
  }

  /**
  * Removes the node as a neighbor of this node. Deletes the edge
  * between it and this node.
  * @param node the node to remove
  * @since 0.1.0
  */
  public void removeNeighbor(Node<T> node) {
    this.edges.remove(node);
  }

  /**
  * Checks if the node is a neighbor of this node
  * @param node the node to check
  * @return true if there is an edge to the node, false otherwise
  * @since 0.1.0
  */
  public boolean hasNeighbor(Node<T> node) {
    return edges.containsKey(node);
  }

  /**
  * Return the Edge from this node to neighbor
  * @param neighbor a node that is connected to this node by an edge
  * @return the Edge between this and neighbor
  * @throws IllegalArgumentException if neighbor is not a neighbor
  * @since 0.11.0
  */
  public Edge<T> getEdge(Node<T> neighbor) {
    if(!hasNeighbor(neighbor)) {
      throw new IllegalArgumentException("Node passed to Node::getEdge() is not a neighbor.");
    }
    return this.edges.get(neighbor);
  }

  /**
  * Get all the edges that leave this node
  * @return a {@code Collection<Edge<T>>} of edges
  * @since 0.1.0
  */
  protected Collection<Edge<T>> getEdges() {
    return edges.values();
  }

  /**
  * Return the neighbors of this node.
  * <p>
  * Modification of this Collection is UNSAFE. It will be reflected back on this Node.
  * @return a {@code Collection<Node<T>>} of all nodes that are connected
  *         to this node by an edge
  * @since 0.11.0
  */
  protected Collection<Node<T>> getNeighbors() {
    //return new LinkedList<Node<T>>(edges.keySet());
    return edges.keySet();
  }

  /**
  * Return the weight of the edge to the neighbor.
  * @param neighbor a node that is connected to this node by an edge
  * @return the weight of the edge
  * @throws IllegalArgumentException if neighbor is not a neighbor
  * @since 0.11.0
  */
  public double getEdgeWeight(Node<T> neighbor) {
    if(!hasNeighbor(neighbor)) {
      throw new IllegalArgumentException("Node passed to Node::getWeight() is not a neighbor.");
    }
    return getEdge(neighbor).getWeight();
  }

  /**
  * Get a list of this Node and all its neighbors
  * @return the {@literal List<Node<T>>} containing this node and all of its neighbors.
  * @version 0.14.0
  * @since 0.2.0
  */
  public List<Node<T>> getNodeAndNeighbors() {
    ArrayList<Node<T>> list = new ArrayList<Node<T>>(numNeighbors()+1);
    list.add(this);
    list.addAll(edges.keySet());
    return list;
  }

  /**
  * The hashCode of this node is the HashCode of the Object
  * it holds. There should only be one Node per Object in the graph
  * @return the hashcode
  * @since 0.1.0
  */
  @Override
  public int hashCode() {
    return element.hashCode();
  }

  /**
  * Returns the toString of this node's Object followed
  * by the toStrings of all the neighbor node's Objects.
  * @return the string representing this Node and its neighbors
  * @since 0.1.0
  */
  @Override
  public String toString() {
    String str = this.get() + ": ";
    for(Node<T> node : getNeighbors()) {
      str += node.get() + " ";
    }
    return str;
  }

  /**
  * Overwritten compareTo from Comparable compares on number
  * of neighbors.
  * @param n the node to compare to
  * @return this.numNeighbors() - n.numNeighbors()
  * @since 0.1.0
  */
  @Override
  public int compareTo(Node<T> n) {
    return numNeighbors() - n.numNeighbors();
  }

  /**
  * Two nodes are equals if this.get().equals(n.get()),
  * that is, if the objects they both contain are equal.
  * @param obj the other node to compare to.
  * @return true if this.get().equals(obj.get())
  * @since 0.1.0
  */
  @Override
  @SuppressWarnings("unchecked")
  public boolean equals(Object obj) {
    if(obj instanceof Node) {
      Node<T> n = (Node<T>) obj;
      return this.get().equals(n.get());
    }
    return false;
  }

}
