package com.aaronpmaus.jMath.graph;
import java.util.LinkedHashMap;
import java.util.Collection;
import java.util.Set;
import java.util.ArrayList;
import java.lang.ClassCastException;

/**
 * A Node for a graph. A Node represents a vertex and is a wrapper 
 * for an object. In addition to its object, it also has Edges
 * to other nodes.
 * The Object can be anything, and the generic type allows for
 * there to be a graph of any type of Object.
 * @author Aaron Maus aaron@aaronpmaus.com
 * @version 0.1.4
 * @since 0.1.0
*/
public class Node<T> implements Comparable<Node<T>>{
    private T obj;
    // the key is the node at the end of the edge
    private LinkedHashMap<Node<T>, Edge<T>> neighbors;
    
    /**
     * Constructor for a Node.
     * @param obj the object that this node holds
    */
    public Node(T obj){
        this.obj = obj;
        this.neighbors = new LinkedHashMap<Node<T>,Edge<T>>();
    }

    /**
     * @return the number of neighbors of this node.
    */
    public int numNeighbors(){
        return this.neighbors.size();
    }

    /**
     * Returns the object that is Node wraps.
     * @return the Object that this Node wraps.
    */
    public T get(){
        return this.obj;
    }

    /**
     * add an edge to this node
     * @param e the edge to add to this node
    */
    public void addEdge(Edge<T> e){
        this.neighbors.put(e.getEnd(), e);
    }

    /**
     * Add a neighbor to this node. Accomplished the same thing
     * as @see Node:addEdge(Edge&lt;T&gt; e)
     * @param n the node to add as a neighbor
    */
    public void addNeighbor(Node<T> n){
        this.neighbors.put(n, new Edge<T>(this, n));
    }

    /**
     * Add a neighbor to this node. Accomplished the same thing
     * as @see Node:addEdge(Edge&lt;T&gt; e)
     * @param n the node to add as a neighbor
     * @param weight the weight of the edge from this node to n
    */
    public void addNeighbor(Node<T> n, double weight){
        this.neighbors.put(n, new Edge<T>(this, n, weight));
    }

    /**
     * Removes the node as a neighbor of this node. Deletes the edge
     * between it and this node.
     * @param node the node to remove
    */
    public void removeNeighbor(Node<T> node){
        this.neighbors.remove(node);
    }

    /**
     * Checks if the node is a neighbor of this node
     * @param node the node to check
     * @return true if there is an edge to the node, false otherwise
    */
    public boolean hasNeighbor(Node<T> node){
        return neighbors.containsKey(node);
    }

    /**
     * Get all the edges that leave this node
     * @return a Collection&lt;Edge&lt;T&gt;&gt; of edges
    */
    public Collection<Edge<T>> getEdges(){
        return neighbors.values();
    }

    /**
     * Get all the neighbors of this node
     * @return a Set&lt;Node&lt;T&gt;&gt; of nodes that are connected
     *         to this node by an edge going from this node
     *         to them.
    */
    public Set<Node<T>> getNeighbors(){
        return neighbors.keySet();
        //System.out.println(keys.getClass().getName());
        //System.out.println("Yes");
    }

    /**
     * Get ArrayList of Node and all its neighbors
     * @return the ArrayList&lt;Node&lt;T&gt;&gt; containing this node
     *         and all of its neighbors.
     * @since 0.1.1
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
    */
    public int hashCode(){
        return obj.hashCode();
    }

    @Override
    /**
     * Returns the toString of this node's Object followed
     * by the toStrings of all the neighbor node's Objects.
     * @return the string representing this Node and its neighbors
    */
    public String toString(){
        String str = this.obj.toString() + ": ";
        Set<Node<T>> neighbors = getNeighbors();
        for(Node<T> node : neighbors){
            str += node.get().toString() + " ";
        }
        //str += "\n";
        return str;
    }

    @Override
    /**
     * Overwritten compareTo from Comparable.
     * @param n the node to compare to
     * @return this.numNeighbors() - n.numNeighbors()
    */
    public int compareTo(Node<T> n){
        return numNeighbors() - n.numNeighbors();
    }

    /**
     * Two nodes are equals if this.get().equals(n.get())
     * @param obj the other node to compare to.
     * @return true if this.get().equals(obj.get())
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
