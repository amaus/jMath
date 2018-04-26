package com.aaronpmaus.jMath.graph;
import java.util.ArrayList;
import java.util.Collection;

/**
* This is the top of an inheritance hierarch for various max clique Algorithms. It specifies that a
* subclass must implement findMaxClique(UndirectedGraph g). Given that implementation, it provides
* methods to get a clique covering, find a min vertex covering, find the max independent set, and
* get an independent set partition.
* <p>
* Subclasses include MausMaxCliqueSolver which uses Aaron Maus' algorithm, IncMaxCliqueSolver - an
* implementation of Li et al. 2013 algorithm, and IncMaxCliqueAdapter - a wrapper for Li et al. c
* source code.
* @version 0.7.0
* @since 0.7.0
*/
public abstract class MaxCliqueSolver<T extends Comparable<? super T>>{

  /**
  * Finds the maximum clique in g
  * @param graph the graph to search for a max clique in
  * @return An {@code UndirectedGraph<T>} that is a max clique in graph
  * @since 0.7.0
  */
  public abstract UndirectedGraph<T> findMaxClique(UndirectedGraph<T> graph);

  /**
  * Returns a clique covering (or partition) of a Graph.
  * A clique covering is a set of cliques that are disjoint and
  * cover the graph.
  * @param g the graph to get the Clique Covering of
  * @return a {@code ArrayList<UndirectedGraph<T>>} where each graph is a clique
  * in the partition. The Nodes are deep copies of those in the original graph.
  * @since 0.7.0
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
      ArrayList<T> nodesFromOrigGraph = new ArrayList<T>(clique.size());
      for(Node<T> node : clique){
        // need to pass in a code from the original graph, not
        // one from the clique
        theGraph.removeNode(node.get());
        nodesFromOrigGraph.add(node.get());
      }
      theCovering.add(g.subset(nodesFromOrigGraph));
    } while(theGraph.size() > 0);
    return theCovering;
  }

  /**
  * Returns the min vertex cover of the graph. Calculates this by finding the max
  * clique in the complement of this graph and returning all the nodes in the
  * graph except those nodes.
  * @param graph the graph to get the min Vertex Cover of
  * @return the min vertex cover of this graph if exists
  * @since 0.7.0
  */
  public UndirectedGraph<T> findMinVertexCoverViaClique(UndirectedGraph<T> graph){
    UndirectedGraph<T> independentSet = findMaxIndependentSetViaClique(graph);
    Collection<T> nodes = graph.getElements();
    for(T independentSetNode : independentSet.getElements()){
      nodes.remove(independentSetNode);
    }
    return graph.subset(nodes);
  }

  /**
  * Returns the max independent set of size k of a graph. Calculates this by finding the
  * max clique in the complement of this graph and returning those nodes.
  * @param graph the graph to get the Max Independent Set of
  * @return the max independent set in this graph
  * @since 0.7.0
  */
  public UndirectedGraph<T> findMaxIndependentSetViaClique(UndirectedGraph<T> graph){

    UndirectedGraph<T> complement = graph.getComplement();
    UndirectedGraph<T> clique = findMaxClique(complement);
    UndirectedGraph<T> independentSet = null;
    if(clique != null){
      ArrayList<T> nodes = new ArrayList<T>(clique.size());
      for(T node : clique.getElements()){
        nodes.add(node);
      }
      independentSet = graph.subset(nodes);
    }
    return independentSet;
  }

  /**
  * Returns an Independent Set partition of the graph.
  * This partition is NOT guaranteed to be optimal. It is built
  * via a greedy algorithm. At every step, find the largest
  * Independent Set in the graph without any nodes from
  * previous Independent Sets
  * @param g the graph to get the Independent Set Partition of
  * @return a {@code ArrayList<UndirectedGraph<T>>} where each graph is an
  * Independent Set in the Partition.
  * @since 0.7.0
  */
  public ArrayList<UndirectedGraph<T>> getIndependentSetPartition(UndirectedGraph<T> g){
    ArrayList<UndirectedGraph<T>> independentSetPartition = new ArrayList<UndirectedGraph<T>>();
    UndirectedGraph<T> theGraph = new UndirectedGraph<T>(g);
    do {
      UndirectedGraph<T> independentSet = findMaxIndependentSetViaClique(theGraph);
      ArrayList<T> nodesFromOrigGraph = new ArrayList<T>();
      for(Node<T> node : independentSet){
        // need to pass in a code from the original graph, not
        // one from the clique
        theGraph.removeNode(node.get());
        nodesFromOrigGraph.add(node.get());
      }
      independentSetPartition.add(g.subset(nodesFromOrigGraph));
    } while(theGraph.size() > 0);
    return independentSetPartition;
  }
}
