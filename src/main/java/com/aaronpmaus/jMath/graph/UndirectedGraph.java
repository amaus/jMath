package com.aaronpmaus.jMath.graph;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.util.Collection;
import java.util.Collections;
import java.util.ArrayList;
import java.util.Date;
import java.util.Comparator;

/**
 * A class for an UndirectedGraph. Extends Graph. A Graph is made of Nodes. Nodes
 * represent vertices and are wrapper for a generic Object. See the Node class for
 * more information. In this manner a graph can be built to represent anything.
 * @author Aaron Maus aaron@aaronpmaus.com
 * @version 0.2.0
 * @since 0.1.0
*/
public class UndirectedGraph<T extends Comparable<T>> extends Graph<T>{
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
    public void removeNodeFromGraph(Node<T> nodeToBeRemoved){
        nodeToBeRemoved = this.getNode(nodeToBeRemoved.get());
        // for ever neighbor of nodeToBeRemoved,
        for(Node<T> neighbor : nodeToBeRemoved.getNeighbors()){
            super.removeEdge(neighbor, nodeToBeRemoved);
        }
        removeNodeFromAdjacencyList(nodeToBeRemoved);
    }

    @Override
    /**
     * {@inheritDoc}
    */
    public UndirectedGraph<T> getNeighborhood(Node<T> root){
        Collection<Node<T>> copyNodes = getNeighborhoodNodes(root);
        return new UndirectedGraph<T>(copyNodes);
    }

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

    /**
     * A method to return the degeneracy ordering of a graph
     * @return an ArrayList of Nodes representing the degeneracy ordering.
     *         the smallest vertex is at the 0th index
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
            temp.removeNodeFromGraph(theSmallestNodeTemp);
        }
        return vertexOrdering;
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
     * Returns a set of all the edges in this UndirectedGraph.
     * @return a Collection of the edges in this UndirectedGraph
     * @since 0.2.0
    */
    Collection<UndirectedEdge<T>> getEdges(){
        HashSet<UndirectedEdge<T>> edges = new HashSet<UndirectedEdge<T>>((int)(numEdges()/0.75) + 1);
        for(Node<T> node : getNodes()){
            for(Edge<T> e : node.getEdges()){
                edges.add(new UndirectedEdge<T>(e));
            }
        }
        //return edges;
        ArrayList<UndirectedEdge<T>> edgesSorted = new ArrayList<UndirectedEdge<T>>(edges);
        Collections.sort(edgesSorted, new Comparator<UndirectedEdge<T>>() {
            public int compare(UndirectedEdge<T> e1, UndirectedEdge<T> e2) {
                int comparison = e1.getStart().get().compareTo(e2.getStart().get());
                if(comparison == 0) {
                    return e1.getEnd().get().compareTo(e2.getEnd().get());
                }
                return comparison;
            }
        });
        return edgesSorted;
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
