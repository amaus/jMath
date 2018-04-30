package com.aaronpmaus.jMath.executables;

import com.aaronpmaus.jMath.graph.*;
import com.aaronpmaus.jMath.io.GraphIO;
import com.aaronpmaus.jMath.io.CommandLineParser;

import java.util.List;
import java.util.Set;
import java.util.Date;
import java.util.Collections;
import java.util.Scanner;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
* <pre>
* <code>
* {@literal Usage: FindMaxClique [<options>] <graph fname>}
*
*   FindMaxClique takes the file name of a DIMACS file specifying a graph
*   and solves and prints the MAX CLIQUE in that graph. The user can
*   specify which algorithm to use.
*
*   The inc-adapter algorithm is an adapter to Li, Fang, and Xu's
*   Incremental Max Clique program (Combining MaxSAT Reasoning and
*   Incremental Upper Bound for the Maximum Clique Problem, 2013 IEEE
*   International Conference on Tools  with Artificial Intelligence). The
*   inc-solver algorithm is this library's implementation of that
*   algorithm. This implementation is in progress. The maus algorithm is
*   an original algorithm for this library. It is not as efficient the
*   Incremental Max Clique algorithms.
*
*   options :
*       -h
*           Display the usage file.
*       --graph
*           The file name of a DIMACS file containing the graph to find
*           a MAX CLIQUE in.
*       --inc-adapter
*           Use the IncMaxCliqueAdapter to find the max clique
*       --inc-solver
*           Use the IncMaxCliqueSolver to find the max clique
*       --maus
*           Use the MausMaxCliqueSolver to find the max clique
*
* </code>
* </pre>
* @version 0.13.1
* @since 0.4.0
*/
public class FindMaxClique{
  private static boolean graphFileProvided = false;
  private static boolean runIncAdapter = false;
  private static boolean runIncSolver = false;
  private static boolean runMaus = false;
  private static String graphFileName;
  private static UndirectedGraph<Integer> graph;

  public static void main(String[] arguments) {
    CommandLineParser args = new CommandLineParser(arguments);
    if(arguments.length == 0 || args.contains("-h")) {
      printUsage();
      System.exit(1);
    } else {
      if(args.contains("--graph")) {
        graphFileName = args.getValue("--graph");
        graphFileProvided = true;
      }
      if(args.contains("--inc-adapter")) {
        runIncAdapter = true;
      }
      if(args.contains("--inc-solver")) {
        runIncSolver = true;
      }
      if(args.contains("--maus")) {
        runMaus = true;
      }
      if(!graphFileProvided) {
        System.out.println("You must provide a graph DIMACS file.");
        System.out.println();
        printUsage();
        System.exit(1);
      } else {
        // READ IN GRAPH FILE
        try {

          long graphBuildStart = new Date().getTime();
          graph = GraphIO.readFromDimacsFile(graphFileName);
          graph.setGraphFileName("g.dimacs");
          long graphBuildEnd = new Date().getTime();
          System.out.println("Graph built from dimacs file " + graphFileName + " in "
              + (graphBuildEnd-graphBuildStart) + " milliseconds.");
          System.out.println("Graph Density: " + graph.density());
        } catch (FileNotFoundException e) {
          System.out.println(graphFileName + " not found. input proper filename or check file");
          System.exit(1);
        }
      }

      if(runIncAdapter) {
        System.out.println("########################### IncMaxCliqueAdapter ###########################");
        MaxCliqueSolver<Integer> maxCliqueTool = new IncMaxCliqueAdapter();
        long cliqueStart = new Date().getTime();
        UndirectedGraph<Integer> maxClique = maxCliqueTool.findMaxClique(graph);
        long cliqueEnd = new Date().getTime();
        printCliqueResults(maxClique, (cliqueEnd - cliqueStart));
      }

      if(runIncSolver) {
        System.out.println("########################### IncMaxCliqueSolver ############################");
        MaxCliqueSolver<Integer> maxCliqueTool = new IncMaxCliqueSolver<Integer>();
        long cliqueStart = new Date().getTime();
        UndirectedGraph<Integer> maxClique = maxCliqueTool.findMaxClique(graph);
        long cliqueEnd = new Date().getTime();
        printCliqueResults(maxClique, (cliqueEnd - cliqueStart));
      }

      if(runMaus) {
        System.out.println("############################## Maus Solver ################################");
        MaxCliqueSolver<Integer> maxCliqueTool = new MausMaxCliqueSolver<Integer>();
        long cliqueStart = new Date().getTime();
        UndirectedGraph<Integer> maxClique = maxCliqueTool.findMaxClique(graph);
        long cliqueEnd = new Date().getTime();
        printCliqueResults(maxClique, (cliqueEnd - cliqueStart));
      }
    }
  }

  private static void printCliqueResults(UndirectedGraph<Integer> clique, long runTime) {
    if(clique != null) {
      String cliqueStr = "";
      List<Integer> nodeNums = clique.getElements();
      Collections.sort(nodeNums);
      for(Integer i : nodeNums) {
        cliqueStr += i + " ";
      }
      System.out.println("MAXIMUM CLIQUE");
      System.out.print(clique);
      System.out.println("CLIQUE: "+cliqueStr);
      System.out.println("Clique Found in "
      + runTime + " milliseconds.");
      System.out.println(clique.size() + " nodes in clique");
    } else {
      System.out.println("Max clique not found. Umm.. somethings wrong");
    }
  }

  private static void printUsage() {
    InputStream stream = FindMaxClique.class.getResourceAsStream("FindMaxCliqueUsage.txt");
    Scanner in = new Scanner(stream);
    while(in.hasNextLine()) {
      System.out.println(in.nextLine());
    }
  }
}
