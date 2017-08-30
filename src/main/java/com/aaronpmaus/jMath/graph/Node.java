package com.aaronpmaus.jMath.graph;
import java.util.LinkedHashMap;
import java.util.Collection;
import java.util.ArrayList;
import java.lang.ClassCastException;

/**
* A Node for a graph. A Node represents a vertex and is a wrapper
* for an object. In addition to its object, it also has Edges
* to other nodes.
* The Object can be anything, and the generic type allows for
* there to be a graph of any type of Object.
* @author Aaron Maus aaron@aaronpmaus.com
* @version 0.11.0
* @since 0.1.0
*/
public class Node<T extends Comparable<T>> implements Comparable<Node<T>>{
  private T obj;
  // the key is the node at the end of the edge
  private LinkedHashMap<Node<T>, Edge<T>> neighbors;

  /**
  * Constructor for a Node.
  * @param obj the object that this node holds
  * @since 0.1.0
  */
  public Node(T obj){
    this.obj = obj;
    this.neighbors = new LinkedHashMap<Node<T>,Edge<T>>();
  }

  /**
  * @return the number of neighbors of this node.
  * @since 0.1.0
  */
  public int numNeighbors(){
    return this.neighbors.size();
  }

  /**
  * Returns the object that is Node wraps.
  * @return the Object that this Node wraps.
  * @since 0.4.0
  */
  public T get(){
    return this.obj;
  }

  /**
  * Change the object that this node holds.
  * @param obj the new object for this node to hold
  * @since 0.7.0
  */
  public void set(T obj) {
    this.obj = obj;
  }

  /**
  * add an edge to this node
  * @param e the edge to add to this node
  * @since 0.1.0
  */
  public void addEdge(Edge<T> e){
    this.neighbors.put(e.getEnd(), e);
  }

  /**
  * Add a neighbor to this node. Accomplished the same thing
  * as @see Node:addEdge(Edge&lt;T&gt; e)
  * @param n the node to add as a neighbor
  * @since 0.1.0
  */
  public void addNeighbor(Node<T> n){
    this.neighbors.put(n, new Edge<T>(this, n));
  }

  /**
  * Add a neighbor to this node. Accomplished the same thing
  * as @see Node:addEdge(Edge&lt;T&gt; e)
  * @param n the node to add as a neighbor
  * @param weight the weight of the edge from this node to n
  * @since 0.1.0
  */
  public void addNeighbor(Node<T> n, double weight){
    this.neighbors.put(n, new Edge<T>(this, n, weight));
  }

  /**
  * Removes the node as a neighbor of this node. Deletes the edge
  * between it and this node.
  * @param node the node to remove
  * @since 0.1.0
  */
  public void removeNeighbor(Node<T> node){
    this.neighbors.remove(node);
  }

  /**
  * Checks if the node is a neighbor of this node
  * @param node the node to check
  * @return true if there is an edge to the node, false otherwise
  * @since 0.1.0
  */
  public boolean hasNeighbor(Node<T> node){
    return neighbors.containsKey(node);
  }

  /**
  * Return the Edge from this node to neighbor
  *
  * @param neighbor a node that is connected to this node by an edge
  * @return the Edge between this and neighbor
  * @throws IllegalArgumentException if neighbor is not a neighbor
  * @since 0.11.0
  */
  public Edge<T> getEdge(Node<T> neighbor){
    if(!hasNeighbor(neighbor)){
      throw new IllegalArgumentException("Node passed to Node::getEdge() is not a neighbor.");
    }
    return this.neighbors.get(neighbor);
  }

  /**
  * Get all the edges that leave this node
  * @return a {@code Collection<Edge<T>>} of edges
  * @since 0.1.0
  */
  public Collection<Edge<T>> getEdges(){
    return neighbors.values();
  }

  /**
  * Return the neighbors of this node.
  *
  * Any changes to the collection (such as adding or removing nodes) will not affect this node.
  * Any changes to the nodes in the collection however will affect the graph.
  *
  * @return a {@code Collection<Node<T>>} of all nodes that are connected
  *         to this node by an edge
  * @since 0.11.0
  */
  public Collection<Node<T>> getNeighbors(){
    return new ArrayList<Node<T>>(neighbors.keySet());
  }

  /**
  * Return the weight of the edge to the neighbor.
  *
  * @param neighbor a node that is connected to this node by an edge
  * @return the weight of the edge
  * @throws IllegalArgumentException if neighbor is not a neighbor
  * @since 0.11.0
  */
  public double getEdgeWeight(Node<T> neighbor){
    if(!hasNeighbor(neighbor)){
      throw new IllegalArgumentException("Node passed to Node::getWeight() is not a neighbor.");
    }
    return getEdge(neighbor).getWeight();
  }

  /**
  * Get ArrayList of Node and all its neighbors
  * @return the ArrayList&lt;Node&lt;T&gt;&gt; containing this node
  *         and all of its neighbors.
  * @since 0.2.0
  */
  public ArrayList<Node<T>> getNodeAndNeighbors(){
    ArrayList<Node<T>> list = new ArrayList<Node<T>>(numNeighbors()+1);
    list.add(this);
    for(Node<T> node : getNeighbors()){
      list.add(node);
    }
    return list;
  }

  @Override
  /**
  * The hashCode of this node is the HashCode of the Object
  * it holds. There should only be one Node per Object in the graph
  * @return the hashcode
  * @since 0.1.0
  */
  public int hashCode(){
    return obj.hashCode();
  }

  @Override
  /**
  * Returns the toString of this node's Object followed
  * by the toStrings of all the neighbor node's Objects.
  * @return the string representing this Node and its neighbors
  * @since 0.1.0
  */
  public String toString(){
    String str = this.obj.toString() + ": ";
    for(Node<T> node : getNeighbors()){
      str += node.get().toString() + " ";
    }
    //str += "\n";
    return str;
  }

  @Override
  /**
  * Overwritten compareTo from Comparable compares on number
  * of neighbors.
  * @param n the node to compare to
  * @return this.numNeighbors() - n.numNeighbors()
  * @since 0.1.0
  */
  public int compareTo(Node<T> n){
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
  public boolean equals(Object obj){
    Node<T> n;
    if(this.getClass().isInstance(obj)){
      try{
        n = this.getClass().cast(obj);
        return get().equals(n.get());
      }catch(ClassCastException e){
        e.printStackTrace();
      }
    }
    return false;
  }

}
