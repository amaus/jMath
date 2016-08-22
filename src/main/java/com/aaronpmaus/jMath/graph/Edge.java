package com.aaronpmaus.jMath.graph;

/**
 * The Edge of a graph. Consists of two nodes (start and end) and the
 * weight of the edge between them. Represents a directed edge.
 * @author Aaron Maus aaron@aaronpmaus.com
 * @version 0.1.0
 * @since 0.1.0
*/
public class Edge<T>{
    private final Node<T> start;
    private final Node<T> end;
    private double weight;

    /**
     * Constructs and edge for a graph with a default weight of 1.0
     * @param start the "from" node of the edge
     * @param end the "to" node of the edge
    */
    public Edge(Node<T> start, Node<T> end){
        this(start,end,1.0);
    }

    /**
     * Constructs and edge for a graph
     * @param start the "from" node of the edge
     * @param end the "to" node of the edge
     * @param weight the weight of this edge
    */
    public Edge(Node<T> start, Node<T> end, double weight){
        this.start = start;
        this.end = end;
        this.weight = weight;
    }

    /**
     * Returns the Node&lt;T&gt; at the start position of this edge
     * @return the start Node&lt;T&gt;
    */
    public Node<T> getStart(){
        return this.start;
    }

    /**
     * Returns the Node&lt;T&gt; at the end position of this edge
     * @return the end Node&lt;T&gt;
    */
    public Node<T> getEnd(){
        return this.end;
    }

    /**
     * Returns the weight of this edge
     * @return the weight
    */
    public double getWeight(){
        return this.weight;
    }
}
