package com.aaronpmaus.jMath.executables;

import com.aaronpmaus.jMath.graph.*;
import com.aaronpmaus.jMath.io.GraphIO;

import java.util.List;
import java.util.Collections;
import java.util.Date;
import java.io.FileNotFoundException;

/**
* Usage: FindClique dimacsFilename cliqueSize
*
* FindClique looks for a clique of a given size in the graph. If a clique is
* found, it prints it out. Otherwise it states that there are no cliques of
* that size in the graph.
* @author Aaron Maus aaron@aaronpmaus.com
# @version 0.7.0
* @since 0.4.0
*/
public class FindClique {
  public static void main(String[] args) {
    if(args.length != 2) {
      System.out.println("Usage: FindClique dimacsfilename cliqueSize");
      System.exit(1);
    }
    String filename = args[0];
    int k = Integer.parseInt(args[1]);
    try {

      // Read in the graph from the DIMACS file and print the
      // max possible clique number
      long graphBuildStart = new Date().getTime();
      UndirectedGraph<Integer> graph = GraphIO.readFromDimacsFile(filename);
      long graphBuildEnd = new Date().getTime();
      System.out.println("Graph built from dimacs file " + filename + " in "
      + (graphBuildEnd-graphBuildStart) + " milliseconds.");

      //System.out.println(graph);
      //neighborhood = graph.getNeighborhood(graph.getNode(new Integer(3)));
      //System.out.println("Neighborhood around 3:\n"+neighborhood);
      //System.out.println("Is Clique?: " + graph.isClique(neighborhood));

      // run the find clique algorithm while clocking it to know how much time it took
      MausMaxCliqueSolver<Integer> maxCliqueTool = new MausMaxCliqueSolver<Integer>();
      long startTime = new Date().getTime();
      UndirectedGraph<Integer> clique = maxCliqueTool.findClique(graph, k, 1);
      long endTime = new Date().getTime();
      System.out.println("Runtime: " + (endTime-startTime) + " milliseconds");
      String cliqueStr = "";
      if(clique != null) {
        List<Integer> nodeNums = clique.getElements();
        Collections.sort(nodeNums);
        for(Integer i : nodeNums) {
          cliqueStr += i + " ";
        }

        System.out.println(cliqueStr);
        System.out.println(clique.size() + " nodes in clique");
        System.out.print(clique);
      } else {
        System.out.println("There are no cliques of size " + k);
      }
    } catch (FileNotFoundException e) {
      System.out.println(filename + " not found. input proper filename or check file");
    }
  }
}
