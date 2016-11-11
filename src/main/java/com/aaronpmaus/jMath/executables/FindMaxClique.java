package com.aaronpmaus.jMath.executables;
import com.aaronpmaus.jMath.graph.*;
import java.util.ArrayList;
import java.util.Set;
import java.util.Date;
import java.util.Collections;

/**
 * <p>Usage: FindMaxClique dimacsFilename</p>
 * FindMaxClique finds a maximum clique in the graph. It performs a binary search on k (clique size),
 * searching for cliques of that size until it can verify that there is a clique of size k
 * and there are no cliques of size k+1.
*/
public class FindMaxClique{
    public static void main(String[] args){
        if(args.length != 1){
            System.out.println("Usage: FindMaxClique dimacsfilename");
            System.exit(1);
        }
        String filename = args[0];
        UndirectedGraph<Integer> graph = GraphIO.readFromDimacsFile(filename);
        System.out.println("Graph built from dimacs file: " + filename);
        System.out.println("Graph Density: " + graph.density());
        //System.out.println(graph);
        //neighborhood = graph.getNeighborhood(graph.getNode(new Integer(3)));
        //System.out.println("Neighborhood around 3:\n"+neighborhood);
        //System.out.println("Is Clique?: " + graph.isClique(neighborhood));
        UndirectedGraph<Integer> maxClique = graph.findMaxClique(graph);
        if(maxClique != null){
            String cliqueStr = "";
            ArrayList<Integer> nodeNums = new ArrayList<Integer>();
            for(Node<Integer> node : maxClique.getNodes()){
                nodeNums.add(node.get());
            }
            Collections.sort(nodeNums);
            for(Integer i : nodeNums){
                cliqueStr += i + " ";
            }
            System.out.println("MAXIMUM CLIQUE");
            System.out.print(maxClique);
            System.out.println("CLIQUE: "+cliqueStr);
            System.out.println(maxClique.size() + " nodes in clique");
        } else {
            System.out.println("Max clique not found. Umm.. somethings wrong");
        }
        System.out.println(UndirectedGraph.numRecursiveCalls + " RECURSIVE CALLS MADE.");
    }
}
