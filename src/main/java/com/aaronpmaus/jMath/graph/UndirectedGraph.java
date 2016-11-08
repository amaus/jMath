package com.aaronpmaus.jMath.graph;
import java.util.HashMap;
import java.util.Set;
import java.util.Collection;
import java.util.Collections;
import java.util.ArrayList;
import java.util.Date;

/**
 * A class for an UndirectedGraph. Extends Graph. A Graph is made of Nodes. Nodes
 * represent vertices and are wrapper for a generic Object. See the Node class for
 * more information. In this manner a graph can be built to represent anything.
 * @author Aaron Maus aaron@aaronpmaus.com
 * @version 0.1.4
 * @since 0.1.0
*/
public class UndirectedGraph<T> extends Graph<T>{
    public static long numRecursiveCalls = 0;
    private static int maxPrintLevel = 0;

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
        super(g.size());
        for(Node<T> node : g.getNodes()){
            addNode(node);
        }
        
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
        return isClique(clique);
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

    /*
     * Finds and returns the Maximum Clique of an UndirectedGraph.
     * @param graph the graph to find the max clique in
     * @return an UndirectedGraph that is the Maximum Clique
     * TODO: write this to use binary search rather than linear to determine the max clique
    public UndirectedGraph<T> findMaxClique(UndirectedGraph<T> graph){
        long startTime = new Date().getTime();
        UndirectedGraph<T> clique = null;
        System.out.println("Original Graph Size: " + graph.size());
        int k = maxPossibleCliqueNum(graph);
        System.out.println("Max Possible Clique Number: " + k);
        while(k > 0){
            System.out.println("***SEARCHING FOR CLIQUES OF SIZE: " + k+"***");
            clique = findClique(new UndirectedGraph<T>(graph), k);
            System.out.println(numRecursiveCalls + " RECURSIVE CALLS SO FAR");
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
    */

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
        System.out.println("Original Graph Size: " + graph.size());
        //System.out.println(graph);
        int high = maxPossibleCliqueNumDeep(graph) + 1;
        System.out.println("Max Possible Clique Number: " + (high-1));
        int low = 0;
        UndirectedGraph<T> clique = null;
        UndirectedGraph<T> maxClique = null;
        while(high - low > 1){
            int k = (high + low) / 2;
            long startTime = new Date().getTime();
            System.out.println("******Searching for a clique of size: " + k + "******");
            numRecursiveCalls = 0;
            clique = findClique(new UndirectedGraph<T>(graph), k, 1);
            long endTime = new Date().getTime();
            if(clique != null){ // clique found
                System.out.println("##### Found a clique of size " + clique.size() +" #####");
                System.out.print(clique);
                String cliqueStr = "CLIQUE: ";
                for(Node<T> node : clique.getNodes()){
                    cliqueStr += node.get() + " ";
                }
                System.out.println(cliqueStr);
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
                System.out.println("##### No clique found of size " + k + " #####");
                high = k;
            }
            System.out.println("Took " + (endTime - startTime) + " milliseconds to run findClique for k: " + k);
            System.out.println("using " + numRecursiveCalls + " recursive calls.");
        }
        long fullEndTime = new Date().getTime();
        //System.out.print("Maximum Clique\n"+maxClique);
        //System.out.println("size: " + maxClique.size());
        System.out.println("Total Time: " + (fullEndTime - fullStartTime) + " milliseconds");
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
     * @return an UndirectedGraph&lt;T&gt; that is a clique or null if no clique
     * of size k exists.
     * @since 0.1.2
    */
    public UndirectedGraph<T> findClique(UndirectedGraph<T> graph, int k, int level){
        while(graph.size() >= k){
            if(isClique(graph)){
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
            if(nodesRemoved){
                continue;
            }

            Node<T> node = nodes.get(0); // the node with the lowest # neighbors
            //System.out.println("Looking at neighbohood of Node: " + node.get());
            if(node.numNeighbors() == k-1){
                UndirectedGraph<T> neighborhood = graph.getNeighborhood(node);
                if(isClique(neighborhood)){
                    return neighborhood;
                } else {
                    if(level <= maxPrintLevel){
                        levelPrint(level, "case2 isClique test failed removing node: "+node.get());
                    }
                    graph.removeNodeFromGraph(node);
                    continue;
                }
            }
            
            // At this point, all nodes that are left have > k-1 neighbors.
            // Their neighborhood can not be a clique. Need to do a recursive
            // call to keep searching.
            node = nodes.get(0); // the first node in the list is the node with the lowest # neighbors.
            if(node.numNeighbors() > k-1){
                UndirectedGraph<T> neighborhood = graph.getNeighborhood(node);
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
                int maxPosCliqueNum = maxPossibleCliqueNum(neighborhood);
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
                    
                    for(Node<T> nodeToRemove : nodesWithNeighborsOnlyInNeighborhood){
                        if(level <= maxPrintLevel){
                            levelPrint(level,"removing node: "+nodeToRemove.get() + " @ " +new Date());
                        }
                        graph.removeNodeFromGraph(nodeToRemove);
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
     * @since 0.1.3
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
    */
    public UndirectedGraph<T> findIndependentSetViaClique(UndirectedGraph<T> graph, int k){
        UndirectedGraph<T> complement = graph.getComplement();
        UndirectedGraph<T> clique = findClique(new UndirectedGraph<T>(complement),k,1);
        UndirectedGraph<T> independentSet = null;
        if(clique != null){
            ArrayList<Node<T>> nodes = new ArrayList<Node<T>>(clique.size());
            for(Node<T> node : clique.getNodes()){
                nodes.add(getNode(node.get()));
            }
            independentSet = new UndirectedGraph<T>(nodes);
        }
        return independentSet;
    }

    /**
     * Returns the max independent set of size k of a graph. Calculates this by finding the
     * max clique in the complement of this graph and returning those nodes.
     * @param graph the graph to look for the independent set in
     * @return the max independent set
    */
    public UndirectedGraph<T> findMaxIndependentSetViaClique(UndirectedGraph<T> graph){
        UndirectedGraph<T> complement = getComplement();
        UndirectedGraph<T> clique = findMaxClique(complement);
        UndirectedGraph<T> independentSet = null;
        if(clique != null){
            ArrayList<Node<T>> nodes = new ArrayList<Node<T>>(clique.size());
            for(Node<T> node : clique.getNodes()){
                nodes.add(getNode(node.get()));
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
    */
    public UndirectedGraph<T> findVertexCoverViaClique(UndirectedGraph<T> graph, int k){
        UndirectedGraph<T> independentSet = findIndependentSetViaClique(graph, size() - k);
        UndirectedGraph<T> vertexCover = null;
        if(independentSet != null){
            Collection<Node<T>> nodes = getNodes();
            for(Node<T> independentSetNode : independentSet.getNodes()){
                nodes.remove(independentSetNode);
            }
            vertexCover = new UndirectedGraph<T>(nodes);
        }
        return vertexCover;
    }

    /**
     * Returns the min vertex cover of the graph. Calculates this by finding  the max
     * clique in the complement of this graph and returning all the nodes in the
     * graph except those nodes.
     * @param graph the graph to look for the vertext cover in
     * @return the min vertex cover if exists
    */
    public UndirectedGraph<T> findMinVertexCoverViaClique(UndirectedGraph<T> graph){
        UndirectedGraph<T> independentSet = findMaxIndependentSetViaClique(graph);
        Collection<Node<T>> nodes = getNodes();
        for(Node<T> independentSetNode : independentSet.getNodes()){
            nodes.remove(independentSetNode);
        }
        return new UndirectedGraph<T>(nodes);
    }

    /**
     * Checks if a graph is a clique. A graph is a clique if there are N*(N-1)/2
     * edges in it.
     * @param graph the graph to test.
     * @return true if the graph is a clique, false otherwise
    */
    public boolean isClique(UndirectedGraph<T> graph){
        int numEdges = graph.numEdges();
        int numNodes = graph.size();
        int edgesRequired = numNodes * (numNodes - 1);
        if(numEdges == edgesRequired){
            return true;
        }
        return false;
    }
}
