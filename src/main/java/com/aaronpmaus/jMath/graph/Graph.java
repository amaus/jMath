package com.aaronpmaus.jMath.graph;
import java.util.HashMap;
import java.util.Set;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.ArrayList;

/**
 * A general class for a Graph, by default a directed graph.
 * A Graph is made of Nodes. The generic type is for the Object that each node
 * holds. See the Node class for more information.
 * @author Aaron Maus aaron@aaronpmaus.com
 * @version 0.1.2
 * @since 0.1.0
*/
public class Graph<T>{
    private HashMap<T, Node<T>> adjacencyList;
    private int numEdges;

    /**
     * A default constructor for the graph.
    */
    public Graph(){
        this.adjacencyList = new HashMap<T, Node<T>>(100); // default load factor is 0.75
        this.numEdges = 0;
    }

    /**
     * Constructs a graph given the number of nodes that will go in the graph.
     * @param numNodes the number of nodes to go in the graph.
    */
    public Graph(int numNodes){
        this.adjacencyList = new HashMap<T, Node<T>>((int)((numNodes)/0.75)+1);
    }

    /**
     * A copy constructor for a Graph. Returns a deep copy of the Graph
     * passed in. The deep copy is of all the Nodes and Edges in the Graph,
     * but not of the Objects that the Nodes contains.
     * @param g the Graph to create a copy of
     * @since 0.1.1
    */
    public Graph(Graph<T> g){
        this(g.getNodes());
    }

    /**
     * Another copy constructor. This one takes a Collection of nodes and builds the graph
     * from a deep copy of these nodes and all edges in them that are between them.
     * @param originalNodes the nodes to be in the graph
     * @since 0.1.1
    */
    public Graph(Collection<Node<T>> originalNodes){
        this.adjacencyList = new HashMap<T, Node<T>>((int)((originalNodes.size())/0.75)+1);
        this.numEdges = 0;
        Collection<Node<T>> copyNodes = createDeepCopyOfNodes(originalNodes);
        for(Node<T> node : copyNodes){
            addNode(node);
        }
    }

    /*
     * A private helper method that creates a deep copy of the Collection of Nodes that is passed in
     * along with all edges in that collection of Nodes where both end points of the edge are in this
     * list of nodes.
    */
    private Collection<Node<T>> createDeepCopyOfNodes(Collection<Node<T>> originalNodes){
        HashMap<T,Node<T>> copyNodes = new HashMap<T,Node<T>>((int)((originalNodes.size()+1)/0.75+1));
        for(Node<T> node : originalNodes){
            Node<T> newNode = new Node<T>(node.getObject());
            copyNodes.put(node.getObject(), newNode);
        }

        for(Node<T> originalNode : originalNodes){
            Collection<Edge<T>> edges = originalNode.getEdges();
            Node<T> newNode = copyNodes.get(originalNode.getObject());
            for(Edge<T> edge : edges){
                Node<T> neighbor = edge.getEnd();
                if(copyNodes.containsKey(neighbor.getObject())){
                    newNode.addEdge(new Edge<T>(newNode, copyNodes.get(neighbor.getObject()), edge.getWeight()));
                }
            }
        }
        return copyNodes.values();
    }

    /**
     * Returns the number of Nodes in this graph
     * @return the number of Nodes in this graph
    */
    public int size(){
        return this.adjacencyList.size();
    }

    /**
     * @return a Collection&lt;Node&lt;T&gt;&gt; of the nodes
    */
    public Collection<Node<T>> getNodes(){
        return this.adjacencyList.values();
    }

    /**
     * Add a node to the graph. Only adds the node if it is not already in the graph.
     * @param n the node to add to the graph.
    */
    public void addNode(Node<T> n){
        if(!containsNode(n)){
            //System.out.println("Adding node: " + n.hashCode());
            adjacencyList.put(n.getObject() ,n);
            incrementNumEdges(n.numNeighbors());
        }
    }

    /**
     * Returns a node from the graph given the object it holds
     * @param obj the object of the node to be retrieved
     * @return the node with that object or null if it is not in the graph.
    */
    public Node<T> getNode(T obj){
        return this.adjacencyList.get(obj);
    }

    /**
     * Add an edge to the graph. Adds both nodes if they are not already in
     * the graph. Then adds an edge from start to end.
     * @param start the start node of the edge
     * @param end the end node of the edge
    */
    public void addEdge(Node<T> start, Node<T> end){
        addNode(start);
        addNode(end);
        getNode(start.getObject()).addNeighbor(getNode(end.getObject()));
        incrementNumEdges();
    }

    /**
     * Add an edge to the graph. Adds both nodes if they are not already in
     * the graph. Then adds an edge from start to end.
     * @param start the start node of the edge
     * @param end the end node of the edge
     * @param weight the weight of the edge
    */
    public void addEdge(Node<T> start, Node<T> end, double weight){
        addNode(start);
        addNode(end);
        getNode(start.getObject()).addEdge(new Edge<T>(getNode(start.getObject()),
                                                        getNode(end.getObject()),
                                                        weight));
        incrementNumEdges();
    }

    /**
     * Remove an edge from the graph.
     * @param start the node at the start of the edge
     * @param end the node at the end of the edge
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
    */
    public int numEdges(){
        return this.numEdges;
    }

    /**
     * checks if a node is in the graph.
     * @param n the node to check for
     * @return true if the graph contains this node, false otherwise.
    */
    public boolean containsNode(Node<T> n){
        T obj = n.getObject();
        return adjacencyList.containsKey(obj);
    }

    // remove the node from the graph and all edges that
    // go to this node.
    /**
     * Removes this node and all edges leading to or from it from the graph.
     * @param n the node to remove
    */
    public void removeNodeFromGraph(Node<T> n){
        for(Node<T> node : this.adjacencyList.values()){
            if(node.hasNeighbor(n)){
                node.removeNeighbor(n);
                decrementNumEdges();
            }
        }
        removeNode(n);
    }

    // this is a hack so that the subclass
    // can remove from the adjacency list. It only removes
    // from the adjacency list. It does not remove any other
    // edges from the graph
    /**
     * Removes this node from the graph. Does not remove edges leading to this node.
     * This method is intended as a helper method for this class and subclasses. Do
     * not call this method, rather call removeNodeFromGraph(Node&lt;T&gt; n), it will 
     * properly maintain all edges of the graph. This method does not guarantee that.
     * I wish that java had an access modifier to restrict access to only subclasses.
     * Also, I know this is not the best way of going about this. I'm still trying to
     * figure out a better way. Any suggestions?
     * @param n the node to remove.
    */
    protected void removeNode(Node<T> n){
        this.adjacencyList.remove(n.getObject());
        decrementNumEdges(n.numNeighbors());
    }

    /**
     * Returns the density of the graph
     * @return the density of the graph
     * @since 0.1.2
    */
    public double density(){
        return ((double)getNumEdges())/(size()*(size()-1));
    }

    /**
     * Returns the neighborhood of the Node passed in.
     * The Neighborhood consists of the node and all of its neighbors
     * and the set of edges that are between all of these nodes.
     * @param root The node to get the neighborhood around.
     * @return a graph of the neighborhood. This is a deep copy of this subset
     *         of the total graph.
    */
    public Graph<T> getNeighborhood(Node<T> root){
        // first get a list of these nodes
        // store as a hashmap for constant time lookups
        // about the parameter. The default load factor is 0.75. So we want to instantiate the HashMap
        // with an initialCapacity large enough so that we never increase the capacity. We know
        // exactly how many nodes will be added to this HashMap, it's the number of neighbors+1 (for the root). 
        // Setting the initial capacity to (numNeighbors+1)/0.75 + 1 will do the trick.
        ArrayList<Node<T>> originalNodes = root.getNodeAndNeighbors();
        Collection<Node<T>> copyNodes = createDeepCopyOfNodes(originalNodes);
        Graph<T> neighborhood = new Graph<T>(originalNodes.size());
        for(Node<T> node : copyNodes){
            neighborhood.addNode(node);
        }

        return neighborhood;
/*
        // create a copy of all the nodes, HashMap for constant time on gets.
        HashMap<T,Node<T>> copyNodes = new HashMap<T,Node<T>>((int)((root.numNeighbors()+1)/0.75+1));
        for(Node<T> originalNode : originalNodes){
            Node<T> newNode = new Node<T>(originalNode.getObject());
            copyNodes.put(newNode.getObject(), newNode);
        }
        // and add all the edges where both end points are in the HashMap of nodes
        for(Node<T> originalNode : originalNodes){
            Collection<Edge<T>> edges = originalNode.getEdges();
            Node<T> newNode = copyNodes.get(originalNode.getObject());
            for(Edge<T> edge : edges){
                Node<T> neighbor = edge.getEnd();
                if(copyNodes.containsKey(neighbor.getObject())){
                    newNode.addEdge(new Edge<T>(newNode, copyNodes.get(neighbor.getObject()), edge.getWeight()));
                }
            }
        }
        
        Graph<T> neighborhood = new Graph<T>(originalNodes.size());
        for(Node<T> node : copyNodes.values()){
            neighborhood.addNode(node);
        }

        return neighborhood;
*/
    }

    @Override
    /**
     * @return a string representation of this graph in the form of an adjacency list.
    */
    public String toString(){
        String str = "";
        for(Node<T> node : this.adjacencyList.values()){
            str += node.toString() + "\n";// + " #neighbors: " + node.numNeighbors() + "\n";
        }
        return str;
    }
}
