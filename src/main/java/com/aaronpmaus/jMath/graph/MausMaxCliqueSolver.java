package com.aaronpmaus.jMath.graph;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;

/**
 * A class to find and return maximum cliques of Undirected Graphs.
 * IncMaxCliqueAdapter is a much faster implementation.
 * This implementation is my own for the Max Clique Problem
 * @author Aaron Maus aaron@aaronpmaus.com
 * @version 0.7.0
 * @since 0.7.0
*/
public class MausMaxCliqueSolver<T extends Comparable<T>> extends MaxCliqueSolver<T> {
    public static long numRecursiveCalls = -1;
    private static int maxPrintLevel = 0;
    private boolean verbose = false;

    /**
     * Finds and returns the Maximum Clique of an UndirectedGraph. Calculates the maximum
     * possible clique number by looking at the number of edges per node. It makes use of the
     * fact that for a graph to have a clique of size k, there must be atleast k nodes each with
     * atleast k-1 neighbors. For example, for there to be a clique of size 4, there must be atleast
     * 4 nodes each with atlead 3 neighbors. It calculates that max possible number for which this
     * criteria is satisfied. After calculating the max possible clique number, it
     * performs a binary search on possible clique numbers. For each k, it runs findClique given
     * a graph and k. There are O(log(N)) calls to findClique.
     * @param graph the graph to find the max clique in
     * @return an UndirectedGraph that is the Maximum Clique
     * @since 0.7.0
    */
    public UndirectedGraph<T> findMaxClique(UndirectedGraph<T> graph){
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
        //System.out.println(graph);
        //int high = maxPossibleCliqueNumDeep(graph) + 1;
        ArrayList<UndirectedGraph<T>> independentSets = null;
        //int high = graph.size();
        //if(density() > 0.90){
            // use IndependentSetPartition to determine bound
            //independentSets = graph.getIndependentSetPartition();
            //high = independentSets.size() + 1;
            //if(verbose) System.out.println("Max Possible Clique Number (Ind Sets): " + (high-1));
        //} else {
            // determine it via graph edges
            //high = maxPossibleCliqueNum(graph) + 1;
            //if(verbose) System.out.println("Max Possible Clique Number: " + (high-1));
        //}
        int high = maxPossibleCliqueNum(graph) + 1;
        int low = 0;
        UndirectedGraph<T> clique = null;
        UndirectedGraph<T> maxClique = null;
        while(high - low > 1){
            int k = (high + low) / 2;
            long startTime = new Date().getTime();
            if(verbose) System.out.println("******Searching for a clique of size: " + k + "******");
            if(numRecursiveCalls == -1){
                numRecursiveCalls = 0;
            }
            // last parameter is a copy of the vertex ordering so that we don't have to
            // recalculate it every time we want to call findClique. the copy passed in
            // will be modified by findClique
            clique = findClique(new UndirectedGraph<T>(graph), k, 1);
            long endTime = new Date().getTime();
            if(clique != null){ // clique found
                if(verbose) System.out.println("##### Found a clique of size " + clique.size() +" #####");
                if(verbose) System.out.print(clique);
                String cliqueStr = "CLIQUE: ";
                for(Node<T> node : clique.getNodes()){
                    cliqueStr += node.get() + " ";
                }
                if(verbose) System.out.println(cliqueStr);
                maxClique = clique;
                // findClique can return a clique larger than k.
                // The first thing the method does is check if the graph passed in
                // is a clique. if it is, it returns it. This clique can be larger than
                // the k being searched for.
                // we'll want to bring our low up to the largest clique found so far.
                if(clique.size() > k){
                    k = clique.size();
                }
                low = k;
            } else { // NO clique of size k
                if(verbose) System.out.println("##### No clique found of size " + k + " #####");
                high = k;
            }
            if(verbose) System.out.println("Took " + (endTime - startTime) + " milliseconds to run findClique for k: " + k);
            if(verbose) System.out.println("using " + numRecursiveCalls + " recursive calls.");
        }
        long fullEndTime = new Date().getTime();
        //System.out.print("Maximum Clique\n"+maxClique);
        //System.out.println("size: " + maxClique.size());
        if(verbose) System.out.println("Total Time: " + (fullEndTime - fullStartTime) + " milliseconds");
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
     * @return an UndirectedGraph{@literal <T>} that is a clique or null if no clique
     * of size k exists.
     * @since 0.7.0
    */
    public UndirectedGraph<T> findClique(UndirectedGraph<T> graph, int k, int level){
        while(graph.size() >= k){
            if(graph.isClique()){
                return graph;
            }
            //if(maxPossibleCliqueNum(graph) < k){
                //return null;
            //}
            if(level <= maxPrintLevel){
                levelPrint(level, "------------------");
                levelPrint(level, "graph of size "+graph.size() + " - level " + level + " ");
                levelPrint(level, "density: " + graph.density());
            }
            ArrayList<Node<T>> nodes = new ArrayList<Node<T>>(graph.getNodes());
            Collections.sort(nodes); // O(N*log(N)) operation. faster if I let each
                                     // for loop go through every node? Then the
                                     // whole while loop is O(3N) looks at nodes.
            ArrayList<Node<T>> removedNodes = new ArrayList<Node<T>>();
            boolean nodesRemoved = false;
            for(Node<T> node : nodes){
                if(node.numNeighbors() >= k-1){
                    break;
                }
                if(node.numNeighbors() < k-1){
                    if(level <= maxPrintLevel){
                        levelPrint(level, "case1 too few neighbors ("+node.numNeighbors()+") removing node: "+node.get());
                    }
                    graph.removeNodeFromGraph(node);
                    removedNodes.add(node);
                    if(graph.size() < k){
                        if(level <= maxPrintLevel){
                            levelPrint(level, "Too few nodes left in graph (" + graph.size()
                                                + ") for a clique of size " + k+".");
                            levelPrint(level, "RETURNING null");
                        }
                        return null;
                    }
                    nodesRemoved = true;
                }
            } // end of for loop for node with too few neighbors to be in clique
            for(Node<T> node : removedNodes){
                nodes.remove(node);
            }
            if(nodesRemoved){
                continue;
            }

            Node<T> node = nodes.get(0); // the node with the lowest # neighbors
            //System.out.println("Looking at neighbohood of Node: " + node.get());
            if(node.numNeighbors() == k-1){
                UndirectedGraph<T> neighborhood = null;
                try{
                    neighborhood = graph.getNeighborhood(node);
                } catch (NullPointerException e) {
                    e.printStackTrace();
                    System.out.println("Searching neighborhood of node: " + node.get());
                    System.out.println("Nodes in Graph");
                    int i = 1;
                    for(Node<T> n : graph.getNodes()){
                        System.out.println(i + ": " + n.get());
                        i++;
                    }
                    System.out.println("Nodes in Ordering");
                    i = 1;
                    for(Node<T> n : nodes){
                        System.out.println(i + ": " + n.get());
                        i++;
                    }
                    System.exit(1);
                }
                if(neighborhood.isClique()){
                    return neighborhood;
                } else {
                    if(level <= maxPrintLevel){
                        levelPrint(level, "case2 isClique test failed removing node: "+node.get());
                    }
                    graph.removeNodeFromGraph(node);
                    nodes.remove(node);
                    continue;
                }
            }

            // At this point, all nodes that are left have > k-1 neighbors.
            // Their neighborhood can not be a clique. Need to do a recursive
            // call to keep searching.
            node = nodes.get(0); // the first node in the list is the node with the lowest # neighbors.
            if(node.numNeighbors() > k-1){
                UndirectedGraph<T> neighborhood = graph.getNeighborhood(graph.getNode(node.get()));
                if(level <= maxPrintLevel){
                    levelPrint(level, "# looking for clique of size " + k);
                    levelPrint(level, "# in node: "+node.get() +" 's neighborhood.");
                    levelPrint(level, "# num neighbors: " + node.numNeighbors());
                    levelPrint(level, "# density of its neighborhood: " + neighborhood.density());
                }
                ArrayList<Node<T>> nodesWithNeighborsOnlyInNeighborhood = new ArrayList<Node<T>>();
                for(Node<T> neighborhoodNode : neighborhood.getNodes()){
                    // if the number of neighbors of this node in the
                    // graph is the same as the number of neighbors of this
                    // node in the neighborhood, then remember this node.
                    // this means that all of its neighbors are in the neighborhood
                    // if we find that there is no Max Clique in this neighborhood
                    // then we can remove these nodes from the graph as well.
                    if(!neighborhoodNode.equals(node)){
                        Node<T> nodeInGraph = graph.getNode(neighborhoodNode.get());
                        try{
                            if(nodeInGraph.numNeighbors() == neighborhoodNode.numNeighbors()){
                                //System.out.println("Adding node to list: \n " + neighborhoodNode);
                                // it must hold a reference to this node in the graph, not the
                                // node from the neighborhood. The neighborhood is a deep copy
                                // and in order to properly remove this node if necessary, we need
                                // to have the original with all the references to the other nodes
                                // in the graph
                                nodesWithNeighborsOnlyInNeighborhood.add(nodeInGraph);
                            }
                        } catch(NullPointerException e){
                            e.printStackTrace();
                            System.out.println("NodeInGraph:\n"+nodeInGraph);
                            System.out.println("neighborhoodNode:\n"+neighborhoodNode);
                            System.out.println("looking at neighborhood of:\n"+node);
                            int i = 0;
                            System.out.println("Nodes in Graph");
                            for(Node<T> n : graph.getNodes()){
                                System.out.println(i + ": " + n.get());
                                i++;
                            }
                            System.out.println("Nodes in Neighborhood");
                            ArrayList<Integer> ns = new ArrayList<Integer>();
                            for(Node<T> n : neighborhood.getNodes()){
                                T obj = n.get();
                                if(obj instanceof Integer){
                                    ns.add((Integer)obj);
                                }
                            }
                            Collections.sort(ns);
                            i = 0;
                            for(Integer o : ns){
                                System.out.println(i + ": " + o);
                                i++;
                            }
                            System.out.println(neighborhoodNode);
                            System.out.println(nodeInGraph);
                            System.out.println(numRecursiveCalls);
                            System.exit(1);
                        }
                    }
                }
                UndirectedGraph<T> clique = null;
                int maxPosCliqueNum = neighborhood.size();
                if(neighborhood.density() > 0.70){
                    //System.out.println("calculating max clique size in neighborhood");
                    //ArrayList<UndirectedGraph<T>> indSets = IncMaxCliqueSolver.getIndependentSetPartition(neighborhood);
                    ArrayList<UndirectedGraph<T>> indSets = getIndependentSetPartition(neighborhood);
                    //System.out.println("num ind sets: "+ indSets.size());
                    //System.out.println("max clique num by edges: " + maxPossibleCliqueNum(neighborhood));
                    maxPosCliqueNum = indSets.size();
                } else {
                    maxPosCliqueNum = maxPossibleCliqueNum(neighborhood);
                }
                //int maxPosCliqueNum = maxPossibleCliqueNum(neighborhood);
                if(maxPosCliqueNum < k){
                    clique = null;
                    if(level <= maxPrintLevel){
                        String message = "Max possible clique number of neighborhood : " + maxPosCliqueNum
                            + " is less than " + k;
                        levelPrint(level, message);
                    }
                } else {
                    numRecursiveCalls++;
                    long start = 0;
                    if(level <= maxPrintLevel){
                        start = new Date().getTime();
                    }
                    clique = findClique(neighborhood, k, level+1);
                    if(level <= maxPrintLevel){
                        long end = new Date().getTime();
                        String message = (end-start)/1000.0 + " seconds to evaluate node";
                        levelPrint(level, message);
                    }
                }
                if(clique == null){
                    // at this point, can we remove any of the other nodes
                    // in the neighborhood from the graph?
                    // yes if and only if all of its neighbors are in the neighborhood
                    // if we know that there is not a max clique in this subset
                    // of the graph and that the node has no neighbors outside
                    // of this subset, then it cannot be a part of the max clique.
                    // can this happen? yes if it is neighbors with everything
                    // in the neighborhood. Otherwise it would have less neighbors
                    // than the root node and we would have checked its neighborhood
                    // first.
                    // Can't check here though because neighborhood has been modified by
                    // the recursive call. Would have to store all nodes with the
                    // same numNeighbors in the neighborhood and graph before the recursive
                    // call.
                    if(level <= maxPrintLevel){
                        levelPrint(level,"case3 recursive call evaluated to null");
                        levelPrint(level,"removing node: "+node.get() + " @ " +new Date());
                    }
                    graph.removeNodeFromGraph(node);
                    nodes.remove(node);

                    for(Node<T> nodeToRemove : nodesWithNeighborsOnlyInNeighborhood){
                        if(level <= maxPrintLevel){
                            levelPrint(level,"removing node: "+nodeToRemove.get() + " @ " +new Date());
                        }
                        graph.removeNodeFromGraph(nodeToRemove);
                        nodes.remove(nodeToRemove);
                    }
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

    private void levelPrint(int level, String message){
        for(int i = 1; i < level; i++){
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
    */
    public int maxPossibleCliqueNum(UndirectedGraph<T> graph){
        int maxEdges = Collections.max(graph.getNodes()).numNeighbors();
        // if the node with the max edges has 3 edges, then those three
        // neighbors plus itself makes a subgraph of 4 nodes.
        int k = maxEdges + 1;
        boolean cont = true;
        while(cont){
            int numPotentialMembers = 0;
            for(Node<T> n : graph.getNodes()){
                if(n.numNeighbors() + 1 >= k){
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
     * Returns a second pass maximum possible clique number for an UndirectedGraph
     * This relies on the fact that in order to have a clique of size K, there must
     * be atleast K nodes all with atleast K-1 edges in the graph. For example, for
     * there to be a clique of size 4, there must be 4 nodes that all have atleast 3 edges.
     * This method looks at the neighborhood of every node in the graph and calculates the
     * max possible clique number of that neighborhood
     * @param graph the graph to find the max possible clique number of
     * @return the max possible clique number
     * @since 0.7.0
    */
    public int maxPossibleCliqueNumDeep(UndirectedGraph<T> graph){
        int k = 0;
        for(Node<T> node : graph.getNodes()){
            UndirectedGraph<T> neighborhood = graph.getNeighborhood(node);
            int maxEdges = Collections.max(neighborhood.getNodes()).numNeighbors();
            // if the node with the max edges has 3 edges, then those three
            // neighbors plus itself makes a subgraph of 4 nodes.
            int tempK = maxEdges + 1;
            boolean cont = true;
            while(cont){
                int numPotentialMembers = 0;
                for(Node<T> n : neighborhood.getNodes()){
                    if(n.numNeighbors() + 1 >= tempK){
                        numPotentialMembers++;
                    }
                }
                if(numPotentialMembers >= tempK){
                    cont = false;
                } else {
                    tempK--;
                }
            }
            if(tempK > k){
                k = tempK;
            }
        }
        return k;
    }

    /**
     * Returns an independent set of size k of a graph. Calculated this by finding a
     * clique of size k in the complement of this graph and returning those nodes.
     * @param graph the graph to look for the independent set in
     * @param k the size of the independent set to look for
     * @return the independent set if exists, null otherwise
     * @since 0.7.0
    */
    public UndirectedGraph<T> findIndependentSetViaClique(UndirectedGraph<T> graph, int k){
        UndirectedGraph<T> complement = graph.getComplement();
        UndirectedGraph<T> clique = findClique(new UndirectedGraph<T>(complement),k,1);
        UndirectedGraph<T> independentSet = null;
        if(clique != null){
            ArrayList<Node<T>> nodes = new ArrayList<Node<T>>(clique.size());
            for(Node<T> node : clique.getNodes()){
                nodes.add(graph.getNode(node.get()));
            }
            independentSet = new UndirectedGraph<T>(nodes);
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
    public UndirectedGraph<T> findVertexCoverViaClique(UndirectedGraph<T> graph, int k){
        UndirectedGraph<T> independentSet = findIndependentSetViaClique(graph, graph.size() - k);
        UndirectedGraph<T> vertexCover = null;
        if(independentSet != null){
            Collection<Node<T>> nodes = graph.getNodes();
            for(Node<T> independentSetNode : independentSet.getNodes()){
                nodes.remove(independentSetNode);
            }
            vertexCover = new UndirectedGraph<T>(nodes);
        }
        return vertexCover;
    }
}
