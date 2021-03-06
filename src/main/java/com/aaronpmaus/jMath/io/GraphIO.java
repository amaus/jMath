package com.aaronpmaus.jMath.io;

import java.util.Scanner;
import java.io.FileNotFoundException;
import java.io.File;
import java.io.IOException;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.FileInputStream;

import com.aaronpmaus.jMath.graph.UndirectedGraph;
import com.aaronpmaus.jMath.graph.Edge;

/**
* This class provides static methods to read and write graphs to and from DIMACS files.
* @version 0.10.0
* @since 0.1.0
*/
public class GraphIO{

  /**
  * Read in and build and UndirectedGraph{@literal <Integer>}from a DIMACS file.
  * @param inputStream the InputStream to read from
  * @param graphFileName the filename of the graph
  * @return the UndirectedGraph{@literal <Integer>} from that file
  * @since 0.10.0
  */
  public static UndirectedGraph<Integer> readFromDimacsFile(InputStream inputStream, String graphFileName) {
    Scanner fileReader = new Scanner(inputStream);
    int numNodes = 0;
    int numEdges = 0;
    UndirectedGraph<Integer> graph;
    while(fileReader.hasNextLine()) {
      String line = fileReader.nextLine();
      String[] tokens = line.split(" ");
      if(tokens[0].equals("p")) {
        numNodes = Integer.parseInt(tokens[2]);
        numEdges = Integer.parseInt(tokens[3]);
        break;
      }
    }
    graph = new UndirectedGraph<Integer>(numNodes);
    graph.setGraphFileName(graphFileName);
    while(fileReader.hasNextLine()) {
      String line = fileReader.nextLine();
      String[] tokens = line.split(" ");
      tokens[0] = tokens[0].toLowerCase();
      // e for edge and a for arc. both can be used to indicate an edge in the graph
      if(tokens[0].equals("e") || tokens[0].equals("a")) {
        if(tokens.length == 3) {
          graph.addEdge(new Integer(tokens[1]), new Integer(tokens[2]));
        } else if (tokens.length == 4) {
          graph.addEdge(new Integer(tokens[1]), new Integer(tokens[2]), new Double(tokens[3]));
        }
      }
    }
    return graph;
  }

  /**
  * Reads in and build and UndirectedGraph{@literal <Integer>}from a DIMACS file.
  * @param filename the name of the file to read from
  * @return the UndirectedGraph{@literal <Integer>} from that file
  * @throws FileNotFoundException if reading in the file fails
  * @since 0.1.0
  */
  public static UndirectedGraph<Integer> readFromDimacsFile(String filename) throws FileNotFoundException{
    FileInputStream in = new FileInputStream(new File(filename));
    return readFromDimacsFile(in, filename);
  }

  /**
  * Given an UndirectedGraph{@literal <Integer>}, writes out a DIMACs file.
  * @param theGraph the UndirectedGraph{@literal <Integer>} to write as a DIMACS file.
  * @param fileName the name of the file to write out
  * @throws IOException if file creation fails
  * @since 0.6.0
  */
  public static void writeDimacsFile(UndirectedGraph<Integer> theGraph, String fileName) throws IOException {
    BufferedWriter out = new BufferedWriter(new FileWriter(new File(fileName)));
    out.write(String.format("c FILE:  %s\n",fileName));
    out.write(String.format("c\n"));
    out.write(String.format("c SOURCE: Written by jMath, a program written by Aaron Maus\n"));
    out.write(String.format("c\n"));
    out.write(String.format("c G(n,p) graph\n"));
    out.write(String.format("c\n"));
    out.write(String.format("c           Graph Stats\n"));
    out.write(String.format("c number of vertices : %d\n", theGraph.size()));
    out.write(String.format("c number of edges    : %d\n", theGraph.numEdges()));
    out.write(String.format("c edges density      : %.6f\n", theGraph.density()));
    out.write(String.format("p col %d %d\n", theGraph.size(), theGraph.numEdges()));

    for(Edge<Integer> e : theGraph.getEdges()) {
      out.write(String.format("e %d %d\n",e.getStart().get(), e.getEnd().get()));
    }
    out.close();
  }
}
