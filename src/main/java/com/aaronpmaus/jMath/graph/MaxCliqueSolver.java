package com.aaronpmaus.jMath.graph;
import java.util.ArrayList;

public abstract class MaxCliqueSolver<T extends Comparable<T>>{

    /**
     * Finds the maximum clique in g
     * @param graph the graph to search for a max clique in
     * @return An {@code UndirectedGraph<T>} that is a max clique in graph
     */
    public abstract UndirectedGraph<T> findMaxClique(UndirectedGraph<T> graph);

    /**
     * Returns a clique covering (or partition) of a Graph.
     * A clique covering is a set of cliques that are disjoint and
     * cover the graph.
     * @param g the graph to get the Clique Covering of
     * @return a {@code ArrayList<UndirectedGraph<T>>} where each graph is a clique
     * in the partition. The Nodes are deep copies of those in the original graph.
     * @since 0.1.5
    */
    public ArrayList<UndirectedGraph<T>> getCliqueCovering(UndirectedGraph<T> g ){
        ArrayList<UndirectedGraph<T>> theCovering = new ArrayList<UndirectedGraph<T>>();
        UndirectedGraph<T> theGraph = new UndirectedGraph<T>(g);
        do {
            UndirectedGraph<T>  clique;
            if(theGraph.size() == 1) {
                clique = theGraph;
            } else {
                clique = findMaxClique(theGraph);
            }
            ArrayList<Node<T>> nodesFromOrigGraph = new ArrayList<Node<T>>();
            for(Node<T> node : clique.getNodes()){
                // need to pass in a code from the original graph, not
                // one from the clique
                theGraph.removeNodeFromGraph(theGraph.getNode(node.get()));
                nodesFromOrigGraph.add(g.getNode(node.get()));
            }
            theCovering.add(new UndirectedGraph<T>(nodesFromOrigGraph));
        } while(theGraph.size() > 0);
        return theCovering;
    }
}
