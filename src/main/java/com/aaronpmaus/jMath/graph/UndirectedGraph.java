package com.aaronpmaus.jMath.graph;
import java.util.HashMap;
import java.util.Set;
import java.util.Collection;
import java.util.Collections;
import java.util.ArrayList;
import java.util.Date;

/**
 * A class for an UndirectedGraph. Extends Graph. The generic type is for the object that the
 * Nodes of the graph hold.
 * @author Aaron Maus aaron@aaronpmaus.com
 * @version 0.1.0
 * @since 0.1.0
*/
public class UndirectedGraph<T> extends Graph<T>{

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
     * @param g the Graph to build the UndirectedGraph from.
    */
    public UndirectedGraph(Graph<T> g){
        super(g.size());
        for(Node<T> node : g.getNodes()){
            addNode(node);
        }
        /*
        for(Node<T> node : g.getNodes()){
            for(Node<T> neighbor : node.getNeighbors()){
                // We know there is an edge from node to neighbor,
                // but if there is not an edge from the neighbor to this
                // node, then add it.
                if(!neighbor.hasNeighbor(node)){
                    super.addEdge(neighbor,node);
                }
            }
        }*/
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
        return new UndirectedGraph<T>(super.getNeighborhood(root));
    }

    /**
     * Finds and returns the Maximum Clique of an UndirectedGraph.
     * @param graph the graph to find the max clique in
     * @return an UndirectedGraph that is the Maximum Clique
    */
    public UndirectedGraph<T> findMaxClique(UndirectedGraph<T> graph){
        long startTime = new Date().getTime();
        UndirectedGraph<T> clique = null;
        System.out.println("Original Graph Size: " + graph.size());
        int k = maxPossibleCliqueNum(graph);
        System.out.println("Max Possible Clique Number: " + k);
        while(k > 0){
            clique = findMaxClique(graph, k);
            if(clique != null){
                long endTime = new Date().getTime();
                System.out.println("Maximum Clique Finder runtime: " + (endTime-startTime) + " milliseconds");
                System.out.println("Clique Number: " + clique.size());
                return clique;
            } else {
                k--;
            }
        }
        return clique;
    }

    private UndirectedGraph<T> findMaxClique(UndirectedGraph<T> graph, int k){
        while(graph.size() >= k){
            ArrayList<Node<T>> nodes = new ArrayList<Node<T>>(graph.getNodes());
            Collections.sort(nodes);
            boolean nodesRemoved = false;
            for(Node<T> node : nodes){
                if(node.numNeighbors() >= k-1){
                    break;
                }
                if(node.numNeighbors() < k-1){
                    graph.removeNodeFromGraph(node);
                    if(graph.size() < k){
                        return null;
                    }
                    nodesRemoved = true;
                }
            }
            if(nodesRemoved){
                continue;
            }

            for(Node<T> node : nodes){
                if(node.numNeighbors() > k-1){
                    break;
                }
                if(node.numNeighbors() == k-1){
                    UndirectedGraph<T> neighborhood = graph.getNeighborhood(node);
                    if(isClique(neighborhood)){
                        return neighborhood;
                    } else {
                        graph.removeNodeFromGraph(node);
                        nodesRemoved = true;
                        break;
                    }
                }
            }
            if(nodesRemoved){
                continue;
            }
            
            // At this point, all nodes that are left have > k-1 neighbors.
            // Their neighborhood can not be a clique. Need to do a recursive
            // call to keep searching.
            for(Node<T> node : nodes){
                UndirectedGraph<T> clique = findMaxClique(graph.getNeighborhood(node), k);
                if(clique == null){
                    graph.removeNodeFromGraph(node);
                    break;
                } else {
                    return clique;
                }
            }
        }
        return null;    
    }
    
    /**
     * Returns a first pass maximum possible clique number for an UndirectedGraph
     * This relies on the fact that in order to have a clique of size K, there must
     * be atleast K nodes all with atleast K-1 edges in the graph. For example, for
     * there to be a clique of size 4, there must be 4 nodes that all have atleast 3 edges.
     * @param graph the graph to find the max possible clique number of
     * @return the max possible clique number
    */
    public int maxPossibleCliqueNum(UndirectedGraph<T> graph){
        int maxEdges = Collections.max(graph.getNodes()).numNeighbors();
        // if the node with the max edges has 3 edges, then those three
        // neighbors plus itself makes a subgraph of 4 nodes.
        int k = maxEdges + 1; 
        boolean cont = true;
        while(cont){
            int numPotentialMembers = 0;
            for(Node<T> node : graph.getNodes()){
                if(node.numNeighbors() + 1 >= k){
                    numPotentialMembers++;
                }
            }
            if(numPotentialMembers >= k){
                cont = false;
            } else {
                k--;
            }
        }
        return k;
    }

    /**
     * Checks if a graph is a clique. A graph is a clique if there are N*(N-1)/2
     * edges in it.
     * @param graph the graph to test.
     * @return true if the graph is a clique, false otherwise
    */
    public boolean isClique(UndirectedGraph<T> graph){
        int numEdges = graph.getNumEdges();
        int numNodes = graph.size();
        int edgesRequired = numNodes * (numNodes - 1);
        if(numEdges == edgesRequired){
            return true;
        }
        return false;
    }
}
