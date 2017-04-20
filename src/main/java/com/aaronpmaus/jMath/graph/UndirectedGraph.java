package com.aaronpmaus.jMath.graph;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.util.Collection;
import java.util.Collections;
import java.util.ArrayList;
import java.util.Date;

/**
 * A class for an UndirectedGraph. Extends Graph. A Graph is made of Nodes. Nodes
 * represent vertices and are wrapper for a generic Object. See the Node class for
 * more information. In this manner a graph can be built to represent anything.
 * @author Aaron Maus aaron@aaronpmaus.com
 * @version 0.1.5
 * @since 0.1.0
*/
public class UndirectedGraph<T> extends Graph<T>{
    public static long numRecursiveCalls = -1;
    private static int maxPrintLevel = 0;
    private boolean verbose = false;

    /**
     * A default constructor for the UndirectedGraph
    */
    public UndirectedGraph(){
        super();
    }

    /**
     * Constructs an undirected graph given the number of nodes it needs to hold.
     * @param numNodes the number of nodes the graph will hold.
    */
    public UndirectedGraph(int numNodes){
        super(numNodes);
    }

    /**
     * Builds an UndirectedGraph from a Graph object.
     * @param g the Graph to build from the UndirectedGraph
    */
    public UndirectedGraph(Graph<T> g){
        //super(g.size());
        super(g);
        //for(Node<T> node : g.getNodes()){
            //addNode(node);
        //}
        
        for(Node<T> node : g.getNodes()){
            for(Node<T> neighbor : node.getNeighbors()){
                // We know there is an edge from node to neighbor,
                // but if there is not an edge from the neighbor to this
                // node, then add it.
                if(!neighbor.hasNeighbor(node)){
                    super.addEdge(neighbor,node);
                }
            }
        }
    }

    /**
     * A copy constructor for an UndirectedGraph. Returns a deep copy of the
     * UndirectedGraph passed in. The deep copy is of all the Nodes and Edges
     * in the UndirectedGraph, but not of all the Objects that the Nodes
     * contain.
     * @param g the UndirectedGraph to Copy
     * @since 0.1.1
    */
    public UndirectedGraph(UndirectedGraph<T> g){
        super(g);
    }

    /**
     * A constructor that takes a Collection of nodes and builds an
     * UndirectedGraph out of them.
     * @param nodes the nodes to be in the graph
     * @since 0.1.3
    */
    public UndirectedGraph(Collection<Node<T>> nodes){
        super(nodes);
    }

    /**
     * Returns an UndirectedGraph of neighbors of the Node passed in.
     * The Neighbors Graph consists of the neighboring Nodes
     * and all the edges between all of these nodes.
     * @param node the Node to get the neighbors of.
     * @return an UndirectedGraph of the neighbors. This is a deep
     *         copy of this subset of the total graph.
     * @since 0.1.5
    */
    public UndirectedGraph<T> getNeighbors(Node<T> node){
        return new UndirectedGraph<T>(super.getNeighbors(node));
    }

    /**
     * Given a set of objects, check if the group of nodes containing those
     * objects form a clique in the graph.
     * @param objects the objects to check
     * @return true if the nodes containing these objects are a clique. false otherwise
     * @since 0.1.1
    */
    public boolean checkIfClique(Collection<T> objects){
        ArrayList<Node<T>> nodesInClique = new ArrayList<Node<T>>(objects.size());
        for(T obj : objects){
            nodesInClique.add(getNode(obj));
        }
        UndirectedGraph<T> clique = new UndirectedGraph<T>(nodesInClique);
        return clique.isClique( );
    }

    @Override
    /**
     * Add an edge to this Undirected graph.
     * @param n1 one of the end nodes of this edge
     * @param n2 the other end node of this edge
    */
    public void addEdge(Node<T> n1, Node<T> n2){
        super.addEdge(n1,n2);
        super.addEdge(n2,n1);
    }

    // this implementation is for an undirected graph.
    @Override
    /**
     * {@inheritDoc}
    */
    public void removeNodeFromGraph(Node<T> n){
        n = this.getNode(n.get());
        for(Node<T> neighbor : n.getNeighbors()){
            super.removeEdge(neighbor, n);
        }
        removeNode(n);
    }

    @Override
    /**
     * {@inheritDoc}
    */
    public UndirectedGraph<T> getNeighborhood(Node<T> root){
        Collection<Node<T>> copyNodes = getNeighborhoodNodes(root);
        return new UndirectedGraph<T>(copyNodes);
    }

    /*
     * @TODO overload getNeighborhood() to take a collection of nodes and
     *       pass it into getNeighborhoodNodes() --overloaded as well
     *  then I can use this to build a graph of all the nodes of a clique
     *  and their collective neighborhood. and then run max clique on that graph
     *  step 1: given a clique, get a collection of all the nodes in a graph with 
     *          the same IDs as those in the clique
     *  step 2: call getNeighborhood on that collection of nodes
     *          - will need to overload getNeighborhood here and
     *          - getNeighborhoodNodes in Graph to take a collection of nodes
     *  step 3: run max clique on that UndirectedGraph. it will return a clique
     *          containing the previous clique as a subset
    */
    @Override
    /**
     * {@inheritDoc}
    */
    public UndirectedGraph<T> getNeighborhood(Collection<Node<T>> nodes){
        // getNeighborhoodNodes will use the nodes from THIS graph
        // with the same objects as the nodes passed in. That way,
        // if the nodes passed in are a deep copy, it will find
        // the correct nodes in THIS graph.
        Collection<Node<T>> copyNodes = getNeighborhoodNodes(nodes);
        return new UndirectedGraph<T>(copyNodes);
    }
    

    @Override
    /**
     * {@inheritDoc}
    */
    public UndirectedGraph<T> getComplement(){
        return new UndirectedGraph<T>(getComplementNodes());
    }

    @Override
    /**
     * {@inheritDoc}
    */
    public double density(){
        // the implementation of the undirected graph includes both forward and back edges.
        // ie, double the edges. so we don't need to multiply by 2.
        return super.density(); 
    }
    
    /**
     * Returns a set of all the edges in this UndirectedGraph
     * @return a Collection of the edges in this UndirectedGraph
    */
    public Collection<UndirectedEdge<T>> getEdges(){
        HashSet<UndirectedEdge<T>> edges = new HashSet<UndirectedEdge<T>>((int)(numEdges()/0.75) + 1);
        for(Node<T> node : getNodes()){
            for(Edge<T> e : node.getEdges()){
                edges.add(new UndirectedEdge<T>(e));
            }
        }
        return edges;
    }

    /**
     * Returns the number of edges in this UndirectedGraph.
     * @return an int representing the number of edges in this UndirectedGraph
    */
    @Override
    public int numEdges(){
        return super.numEdges()/2;
    }

    /**
     * Checks if a graph is a clique. A graph is a clique if there are N*(N-1)/2
     * edges in it.
     * @return true if the graph is a clique, false otherwise
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
