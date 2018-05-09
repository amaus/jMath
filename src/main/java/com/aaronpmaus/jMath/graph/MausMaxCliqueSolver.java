package com.aaronpmaus.jMath.graph;
import java.util.ArrayList;
import java.util.List;
import java.util.LinkedList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;

/**
* A class to find and return maximum cliques of Undirected Graphs.
* <p>
* IncMaxCliqueAdapter is a much faster implementation.
* <p>
* This implementation is my own for the Max Clique Problem
* @author Aaron Maus aaron@aaronpmaus.com
* @version 0.14.0
* @since 0.7.0
*/
public class MausMaxCliqueSolver extends MaxCliqueSolver<Integer> {
  public static long numRecursiveCalls = -1;
  private static int maxPrintLevel = -1;
  private boolean verbose = false;
  private int maxSatQuit;

  /**
  * Find and return a Maximum Clique of an UndirectedGraph.
  *
  * Calculates the maximum possible clique number K by looking at the number of edges per node. It
  * makes use of the fact that for a graph to have a clique of size k, there must be atleast k nodes
  * each with atleast k-1 neighbors. For example, for there to be a clique of size 4, there must be
  * atleast 4 nodes each with atlead 3 neighbors. It calculates that max possible number for which
  * this criteria is satisfied. After calculating the max possible clique number, it performs a
  * binary search on possible clique numbers. For each k, it runs findClique given a graph and k.
  * There are O(log(K)) calls to findClique.
  *
  * @param graph the graph to find the max clique in
  * @return an UndirectedGraph that is the Maximum Clique
  * @since 0.7.0
  */
  public UndirectedGraph<Integer> findMaxClique(UndirectedGraph<Integer> graph) {
    maxSatQuit = 0;
    long fullStartTime = new Date().getTime();
    // the plus 1 is necessary. Imagine a trivial example where
    // the max possible clique number is 5, and the actualy clique
    // number is 5.
    // If high were set to 5, then order of search would be:
    // low high : mid  Result
    //  0   5   :  2     T
    //  2   5   :  3     T
    //  3   5   :  4     T
    //  4   5   : FIN  w(g)==4
    // if maxPossibleCliqueNum is the size of the graph (and the whole
    // graph is a clique), ((N+1) + N)/2 == N. Searching for a clique of
    // size N will be the last search made.
    if(verbose) System.out.println("Original Graph Size: " + graph.size());

    //ArrayList<ArrayList<Node<Integer>>> indSets = indSetUB(graph.getNodes());
    //int indSetUB = indSetUB(graph.getNodes());
    int maxSatUB = new MaxSatUB(graph).estimateCardinality();
    int high = maxSatUB + 1;
    //int high = maxPossibleCliqueNum(graph) + 1;
    int low = 0;
    UndirectedGraph<Integer> clique = null;
    UndirectedGraph<Integer> maxClique = null;
    while(high - low > 1) {
      int k = (high + low) / 2;
      long startTime = new Date().getTime();
      if(verbose) System.out.println("******Searching for a clique of size: " + k + "******");
      if(numRecursiveCalls == -1) {
        numRecursiveCalls = 0;
      }
      // last parameter is a copy of the vertex ordering so that we don't have to
      // recalculate it every time we want to call findClique. the copy passed in
      // will be modified by findClique
      clique = findClique(new UndirectedGraph<Integer>(graph), k, 1);
      long endTime = new Date().getTime();
      if(clique != null) { // clique found
        if(verbose) System.out.println("##### Found a clique of size " + clique.size() +" #####");
        if(verbose) System.out.print(clique);
        if(verbose) {
          String cliqueStr = "CLIQUE: ";
          for(Node<Integer> node : clique) {
            cliqueStr += node.get() + " ";
          }
          System.out.println(cliqueStr);
        }
        maxClique = clique;
        // findClique can return a clique larger than k.
        // The first thing the method does is check if the graph passed in
        // is a clique. if it is, it returns it. This clique can be larger than
        // the k being searched for.
        // we'll want to bring our low up to the largest clique found so far.
        if(clique.size() > k) {
          k = clique.size();
        }
        low = k;
      } else { // NO clique of size k
        if(verbose) System.out.println("##### No clique found of size " + k + " #####");
        high = k;
      }
      if(verbose)
      System.out.println("Took " + (endTime - startTime) + " milliseconds to run findClique for k: " + k);
      if(verbose)
      System.out.println("using " + numRecursiveCalls + " recursive calls.");
    }
    long fullEndTime = new Date().getTime();
    //System.out.print("Maximum Clique\n"+maxClique);
    //System.out.println("size: " + maxClique.size());
    if(verbose) System.out.println("Total Time: " + (fullEndTime - fullStartTime) + " milliseconds");
    System.out.println("Num Times Max Sat Bounded a branch: " + maxSatQuit);
    return maxClique;
  }

  /**
  * Looks for cliques of size k in a graph.
  * If there is a clique of size k, it will return it. If it returns a clique
  * larger than k, then there is definitely a clique of size k and possibly
  * a clique larger than k. If there is not a clique of size k, then returns null.
  * If no clique of size k was found, then there are no cliques larger than k.
  * @param graph the graph to search for cliques in
  * @param k the size of the clique to search for
  * @param level track the level of recursion
  * @return an UndirectedGraph{@literal <Integer>} that is a clique or null if no clique
  * of size k exists.
  * @since 0.7.0
  */
  public UndirectedGraph<Integer> findClique(UndirectedGraph<Integer> graph, int k, int level) {
    int maxSatUB = new MaxSatUB(graph).estimateCardinality();
    if(maxSatUB < k) {
      //System.out.printf("QUITING BECAUSE MAXSATUB is less than k, %d < %d\n", maxSatUB, k);
      maxSatQuit++;
      return null;
    }
    while(graph.size() >= k) {
      if(graph.isClique()) {
        return new UndirectedGraph<Integer>(graph);
      }
      //if(maxPossibleCliqueNum(graph) < k) {
      //return null;
      //}
      if(level <= maxPrintLevel) {
        levelPrint(level, "------------------");
        levelPrint(level, "graph of size "+graph.size() + " - level " + level + " ");
        levelPrint(level, "density: " + graph.density());
      }
      ArrayList<Node<Integer>> nodes = new ArrayList<Node<Integer>>(graph.getNodes());
      //ArrayList<Node<Integer>> nodes = degeneracyOrdering(graph);
      Collections.sort(nodes); // O(N*log(N)) operation. faster if I let each
      // for loop go through every node? Then the
      // whole while loop is O(3N) looks at nodes.
      ArrayList<Node<Integer>> removedNodes = new ArrayList<Node<Integer>>();
      boolean nodesRemoved = false;
      for(Node<Integer> node : nodes) {
        if(node.numNeighbors() >= k-1) {
          break;
        }
        if(node.numNeighbors() < k-1) {
          if(level <= maxPrintLevel) {
            levelPrint(level, "case1 too few neighbors ("+node.numNeighbors()+") removing node: "+node.get());
          }
          graph.removeVertex(node.get());
          removedNodes.add(node);
          if(graph.size() < k) {
            if(level <= maxPrintLevel) {
              levelPrint(level, "Too few nodes left in graph (" + graph.size()
              + ") for a clique of size " + k+".");
              levelPrint(level, "RETURNING null");
            }
            return null;
          }
          nodesRemoved = true;
        }
      } // end of for loop for node with too few neighbors to be in clique
      for(Node<Integer> node : removedNodes) {
        nodes.remove(node);
      }
      if(nodesRemoved) {
        continue;
      }

      Node<Integer> node = nodes.get(0); // the node with the lowest # neighbors
      //System.out.println("Looking at neighbohood of Node: " + node.get());
      if(node.numNeighbors() == k-1) {
        //UndirectedGraph<Integer> neighborhood = null;
        //neighborhood = graph.getNeighborhood(node.get());
        if(isClique(node.getNodeAndNeighbors())) {
          return graph.getNeighborhood(node.get());
          //return neighborhood;
        } else {
          if(level <= maxPrintLevel) {
            levelPrint(level, "case2 isClique test failed removing node: "+node.get());
          }
          graph.removeVertex(node.get());
          nodes.remove(node);
          continue;
        }
      }

      // At this point, all nodes that are left have > k-1 neighbors.
      // Their neighborhood can not be a clique. Need to do a recursive
      // call to keep searching.
      node = nodes.get(0); // the first node in the list is the node with the lowest # neighbors.
      if(node.numNeighbors() > k-1) {
        //UndirectedGraph<Integer> neighbors = graph.getNeighborhood(node.get());
        List<Node<Integer>> neighbors = node.getNodeAndNeighbors();
        if(level <= maxPrintLevel) {
          levelPrint(level, "# looking for clique of size " + k);
          levelPrint(level, "# in node: "+node.get() +" 's neighborhood.");
          levelPrint(level, "# num neighbors: " + node.numNeighbors());
          //levelPrint(level, "# density of its neighborhood: " + neighborhood.density());
        }

        UndirectedGraph<Integer> clique = null;

        //int maxPosCliqueNum = indSetUB(neighborhood);
        //ArrayList<ArrayList<Node<Integer>>> indSets = indSetUB(neighbors.getNodes());
        //int maxSatUB = new MaxSatUB(neighbors, indSets).estimateCardinality();
        //int maxPosCliqueNum = Math.min(indSets.size()-1, maxSatUB);
        int maxPosCliqueNum = indSetUB(neighbors);
        //System.out.println("MAX POS CLIQUE NUM: " + maxPosCliqueNum);
        //neighbors = null;
        if(maxPosCliqueNum < k) {
          clique = null;
          if(level <= maxPrintLevel) {
            String message = "Max possible clique number of neighborhood : " + maxPosCliqueNum
            + " is less than " + k;
            levelPrint(level, message);
          }
        } else {
          numRecursiveCalls++;
          long start = 0;
          if(level <= maxPrintLevel) {
            start = new Date().getTime();
          }

          //System.out.println("before recursive call\n"+graph);
          clique = findClique(graph.getNeighborhood(node.get()), k, level+1);
          //clique = findClique(neighbors, k, level+1);

          if(level <= maxPrintLevel) {
            long end = new Date().getTime();
            String message = (end-start)/1000.0 + " seconds to evaluate node";
            levelPrint(level, message);
          }
        }
        if(clique == null) {
          if(level <= maxPrintLevel) {
            levelPrint(level,"case3 recursive call evaluated to null");
            levelPrint(level,"removing node: "+node.get() + " @ " +new Date());
          }
          graph.removeVertex(node.get());
          nodes.remove(node);
          continue;
        } else {
          return clique;
        }
      } else {
        // shouldn't be here. all nodes at this point should have greater than k-1 neighbors
        System.out.println("WHOA Buddy! Shouldn't be here");
        System.out.println("Node\n"+node+"has too few neighbors");
        System.out.println(node.numNeighbors());
        continue; // so that cases 1 or 2 can handle these nodes and the system won't break
        //System.exit(1);
      }
    }
    return null;
  }

  private void levelPrint(int level, String message) {
    for(int i = 1; i < level; i++) {
      System.out.print("|   ");
    }
    System.out.println(message);
  }

  /**
  * Returns a first pass maximum possible clique number for an UndirectedGraph
  * This relies on the fact that in order to have a clique of size K, there must
  * be atleast K nodes all with atleast K-1 edges in the graph. For example, for
  * there to be a clique of size 4, there must be 4 nodes that all have atleast 3 edges.
  * @param graph the graph to find the max possible clique number of
  * @return the max possible clique number
  * @since 0.7.0
  * @deprecated indSetUB gives better bounds
  */
  @Deprecated
  private int maxPossibleCliqueNum(UndirectedGraph<Integer> graph) {
    int maxEdges = Collections.max(graph.getNodes()).numNeighbors();
    // if the node with the max edges has 3 edges, then those three
    // neighbors plus itself makes a subgraph of 4 nodes.
    int k = maxEdges + 1;
    boolean cont = true;
    while(cont) {
      int numPotentialMembers = 0;
      for(Node<Integer> n : graph) {
        if(n.numNeighbors() + 1 >= k) {
          numPotentialMembers++;
        }
      }
      if(numPotentialMembers >= k) {
        cont = false;
      } else {
        k--;
      }
    }
    return k;
  }

  /**
  * Returns a second pass maximum possible clique number for an UndirectedGraph
  * This relies on the fact that in order to have a clique of size K, there must
  * be atleast K nodes all with atleast K-1 edges in the graph. For example, for
  * there to be a clique of size 4, there must be 4 nodes that all have atleast 3 edges.
  * This method looks at the neighborhood of every node in the graph and calculates the
  * max possible clique number of that neighborhood
  * @param graph the graph to find the max possible clique number of
  * @return the max possible clique number
  * @since 0.7.0
  * @deprecated runtime too great
  */
  @Deprecated
  private int maxPossibleCliqueNumDeep(UndirectedGraph<Integer> graph) {
    int k = 0;
    for(Node<Integer> node : graph) {
      UndirectedGraph<Integer> neighborhood = graph.getNeighborhood(node.get());
      int maxEdges = Collections.max(neighborhood.getNodes()).numNeighbors();
      // if the node with the max edges has 3 edges, then those three
      // neighbors plus itself makes a subgraph of 4 nodes.
      int tempK = maxEdges + 1;
      boolean cont = true;
      while(cont) {
        int numPotentialMembers = 0;
        for(Node<Integer> n : neighborhood) {
          if(n.numNeighbors() + 1 >= tempK) {
            numPotentialMembers++;
          }
        }
        if(numPotentialMembers >= tempK) {
          cont = false;
        } else {
          tempK--;
        }
      }
      if(tempK > k) {
        k = tempK;
      }
    }
    return k;
  }

  private int indSetUB(UndirectedGraph<Integer> graph) {
    return indSetUB(graph.getNodes());
  }

  private int indSetUB(Collection<Node<Integer>> nodes) {
    //System.out.println("## Calculating indSetUB");
    // get a list of nodes and sort them in descending order by degree
    List<Node<Integer>> descendingDegreeNodes = new ArrayList<Node<Integer>>(nodes);
    Collections.sort(descendingDegreeNodes, Collections.reverseOrder());
    int maxColorNumber = 0;
    // initialize color sets
    // The index of the outer ArrayList is k and the inner arraylists hold all the nodes that belong
    // to that color k.
    ArrayList<ArrayList<Node<Integer>>> colorSets = new ArrayList<ArrayList<Node<Integer>>>();
    // initialize the first two color sets (k = 0,1)
    colorSets.add(new ArrayList<Node<Integer>>());
    colorSets.add(new ArrayList<Node<Integer>>());
    for(Node<Integer> node : descendingDegreeNodes) {
      int k = 0;
      // find the lowest k where neighbors and the set of nodes in colorSets[k] share no nodes
      // in common
      while(!Collections.disjoint(node.getNeighbors(), colorSets.get(k))) {
        k++;
      }
      if(k > maxColorNumber) {
        maxColorNumber = k;
        // initialize and add the next color set
        colorSets.add(new ArrayList<Node<Integer>>());
      }
      colorSets.get(k).add(node);

    }
    //System.out.println(colorSets.size()-1 + ", " + (maxColorNumber+1));
    return maxColorNumber + 1;
    //return colorSets;
  }

  /**
  * Returns an independent set of size k of a graph. Calculated this by finding a
  * clique of size k in the complement of this graph and returning those nodes.
  * @param graph the graph to look for the independent set in
  * @param k the size of the independent set to look for
  * @return the independent set if exists, null otherwise
  * @since 0.7.0
  */
  public UndirectedGraph<Integer> findIndependentSetViaClique(UndirectedGraph<Integer> graph, int k) {
    UndirectedGraph<Integer> complement = graph.getComplement();
    UndirectedGraph<Integer> clique = findClique(new UndirectedGraph<Integer>(complement),k,1);
    UndirectedGraph<Integer> independentSet = null;
    if(clique != null) {
      independentSet = graph.subset(clique.getElements()); //new UndirectedGraph<Integer>(nodes);
    }
    return independentSet;
  }

  /**
  * Returns a vertex cover of size k of a graph if it exists. Calculates this by finding a
  * clique of size k in the complement of this graph and returning all the nodes in the
  * graph except those nodes.
  * @param graph the graph to look for the vertext cover in
  * @param k the size of the vertex cover to look for
  * @return the vertex cover if exists, null otherwise
  * @since 0.7.0
  */
  public UndirectedGraph<Integer> findVertexCoverViaClique(UndirectedGraph<Integer> graph, int k) {
    UndirectedGraph<Integer> independentSet = findIndependentSetViaClique(graph, graph.size() - k);
    UndirectedGraph<Integer> vertexCover = null;
    if(independentSet != null) {
      List<Integer> cliqueElements = graph.getElements();
      for(Integer independentSetElement : independentSet.getElements()) {
        cliqueElements.remove(independentSetElement);
      }
      vertexCover = graph.subset(cliqueElements); //new UndirectedGraph<Integer>(nodes);
    }
    return vertexCover;
  }

  private ArrayList<Node<Integer>> degeneracyOrdering(UndirectedGraph<Integer> graph) {
    ArrayList<Node<Integer>> vertexOrdering = new ArrayList<Node<Integer>>(graph.size());
    // Build the Degeneracy Vertex Ordering
    UndirectedGraph<Integer> temp = new UndirectedGraph<Integer>(graph);
    while(temp.size() > 0) {
      // get the node with the smallest degree in temp
      Node<Integer> theSmallestVertex = Collections.min(temp.getNodes());
      // add the original node reference to vertexOrdering
      vertexOrdering.add(theSmallestVertex);
      // remove the node from temp
      temp.removeVertex(theSmallestVertex.get());
    }
    return vertexOrdering;
  }

  private LinkedList<Node<Integer>> intersection(Collection<Node<Integer>> a, Collection<Node<Integer>> b) {
    LinkedList<Node<Integer>> intersection = new LinkedList<Node<Integer>>();
    for(Node<Integer> node : a) {
      if(b.contains(node)) {
        intersection.add(node);
      }
    }
    return intersection;
  }

  private boolean isClique(List<Node<Integer>> nodes) {
    for(Node<Integer> a : nodes) {
      for(Node<Integer> b : nodes) {
        if(a.equals(b)) {
          continue;
        }
        if(!a.hasNeighbor(b)) {
          return false;
        }
      }
    }
    return true;
  }

}
