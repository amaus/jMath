package com.aaronpmaus.jMath.graph;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Collections;

/**
 * A class that implements IncMaxCliqueSolver from
 * Combining MaxSAT Reasoning and Incremental Upper Bound for the Maximum Clique Problem
 * Li, Fang, Xu 2013
*/
public class IncMaxCliqueSolver<T>{
    private ArrayList<Node<T>> vertexOrdering;
    private ArrayList<Integer> vertexUB;

    public IncMaxCliqueSolver(){

    }

    /**
     * Finds the maximum clique in g
     * @param graph the graph to search for a max clique in
     * @return An {@code UndirectedGraph<T>} that is a max clique in graph
     */
    public UndirectedGraph<T> findMaxClique(UndirectedGraph<T> graph){
        // create a deep copy of the graph
        UndirectedGraph<T> g = new UndirectedGraph<T>(graph);
        vertexOrdering = vertexOrdering(g);
        // initialize vertexUB
        vertexUB = new ArrayList<Integer>(g.size());
        for(int i = vertexUB.size()-1; i >= 0; i--){
            incUB(i, g);
        }
        UndirectedGraph<T> clique = incMaxClique(g, new UndirectedGraph<T>(), new UndirectedGraph<T>());       
        UndirectedGraph<T> cliqueInOriginal = new UndirectedGraph<T>();
        for(Node<T> n : clique.getNodes()){
            cliqueInOriginal.addNode(graph.getNode(n.get()));
        }
        return cliqueInOriginal;
    }

    // take in a reference to a graph
    // this will be the graph to use to establish neighbors
    // when calculating UB values
    // @param index is the index of the vertex in the vertexOrdering
    // @param g is the graph to use to establish neighbors
    private void incUB(int index, UndirectedGraph<T> g){
        Node<T> vertex = g.getNode(vertexOrdering.get(index).get());
        if(vertex == null){
            throw new NullPointerException("IncMaxCliqueSolver::incUB() vertex at index in vertexOrdering not in g");
        }
        for(int i = index+1; i < vertexUB.size(); i++){
            // if v_i and v_j are neighbors
            Node<T> neighbor = g.getNode(vertexOrdering.get(i).get());
            if(neighbor != null && vertex.hasNeighbor(neighbor)){
                // set vertexUB[i] = vertexUB[j] + 1
                vertexUB.set(index, vertexUB.get(i)+1);
                return; // done
            }
        }
        // if v_i has no neighbors after it in vertexOrdering, set vertexUB[i] = 1
        vertexUB.set(index, 1);
    }

    /*
     * @param g the UndirectedGraph to look for a max clique in
     * @param c the clique being built
     * @param gMax the max clique found so far 
    */
    private UndirectedGraph<T> incMaxClique(UndirectedGraph<T> g, UndirectedGraph<T> c, UndirectedGraph<T> cMax){
        if(g.size() == 0){
            return c;
        }
        Node<T> smallestVertex = getSmallestVertex(g);
        int smallestVertexIndex = vertexOrdering.indexOf(smallestVertex);
        UndirectedGraph<T> gWithoutSmallestVertex = new UndirectedGraph<T>(g);
        gWithoutSmallestVertex.removeNodeFromGraph(gWithoutSmallestVertex.getNode(smallestVertex.get()));
        UndirectedGraph<T> c1 = incMaxClique(gWithoutSmallestVertex, c, cMax);
        if(c1.size() > cMax.size()){
            cMax = c1;
        }
        // update vertexUB, so far only incUB. TODO include UBindSet
        vertexUB.set(smallestVertexIndex, Math.min(vertexUB.get(smallestVertexIndex), incUB(smallestVertexIndex, g)));
        
        if(cMax.size() >= (vertexUB.get(smallestVertexIndex) + c.size())){
            return cMax;
        }
        // save all vertexUB values (really, the only ones that are important are the neighbors of
        // smallestVertex
        ArrayList<Integer> vertexUB_bkup = new ArrayList<Integer>();
        for(Integer i : vertexUB){
            vertexUB_bkup.add(new Integer(i));
        }

       
        UndirectedGraph<T> c2 = incMaxClique(gWithoutSmallestVertex, c, cMax);
        
        return null;
    }

    private Node<T> getSmallestVertex(UndirectedGraph<T> g){
        for(int i = 0; i < vertexUB.size(); i++){
            if(g.containsNode(vertexOrdering.get(i))){
                return vertexOrdering.get(i);
            }
        }
        throw new NoSuchElementException("Smallest Vertex Not found!");
    }

    // Combining MaxSAT Reasoning and Incremental Upper Bound for the Maximum Clique Problem
    // Li, Fang, Xu 2013
    private ArrayList<Node<T>> vertexOrdering(UndirectedGraph<T> g){
        ArrayList<Node<T>> vertexOrdering = new ArrayList<Node<T>>(g.size());
        // Build the Degeneracy Vertex Ordering (
        UndirectedGraph<T> temp = new UndirectedGraph<T>(g);
        while(temp.size() > 0){
            // get the node with the smallest degree in temp
            Node<T> theSmallestNodeTemp = Collections.min(temp.getNodes());
            // get a reference to that node in the original graph
            Node<T> theSmallestNodeOriginal = g.getNode(theSmallestNodeTemp.get());
            // add the original node reference to vertexUB
            vertexOrdering.add(theSmallestNodeOriginal);
            // remove the node from temp
            temp.removeNodeFromGraph(theSmallestNodeTemp);
        }
        return vertexOrdering;
    }
}
