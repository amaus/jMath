package com.aaronpmaus.jMath.graph;

public interface MaxCliqueSolver<T>{

    /**
     * Finds the maximum clique in g
     * @param graph the graph to search for a max clique in
     * @return An {@code UndirectedGraph<T>} that is a max clique in graph
     */
    public UndirectedGraph<T> findMaxClique(UndirectedGraph<T> graph);
}
