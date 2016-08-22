package com.aaronpmaus.jMath.graph;
import java.util.Scanner;
import java.io.FileNotFoundException;
import java.io.File;

/**
 * A class with static methods to read in graphs from files with difference formats
 * @author Aaron Maus aaron@aaronpmaus.com
 * @version 0.1.0
 * @since 0.1.0
*/
public class GraphIO{

    /**
     * Reads in and build and UndirectedGraph&lt;Integer&gt; from a DIMACS file.
     * @param filename the name of the file to read from
     * @return the UndirectedGraph&lt;Integer&gt; from that file
    */
    public static UndirectedGraph<Integer> readFromDimacsFile(String filename){
        try{
            Scanner fileReader = new Scanner(new File(filename));
            int numNodes = 0;
            int numEdges = 0;
            UndirectedGraph<Integer> graph;// = new UndirectedGraph<Integer>();
            while(fileReader.hasNextLine()){
                String line = fileReader.nextLine();
                String[] tokens = line.split(" ");
                if(tokens[0].equals("p")){
                    numNodes = Integer.parseInt(tokens[2]);
                    numEdges = Integer.parseInt(tokens[3]);
                    break;
                }
            }
            graph = new UndirectedGraph<Integer>(numNodes);
            while(fileReader.hasNextLine()){
                String line = fileReader.nextLine();
                String[] tokens = line.split(" ");
                if(tokens[0].equals("e")){
                    //System.out.println("Adding Edge between " + tokens[1] + " and " + tokens[2]);
                    graph.addEdge(new Node<Integer>(new Integer(tokens[1])), 
                                    new Node<Integer>(new Integer(tokens[2])));
                }
            }
            return graph;
        } catch(FileNotFoundException e){
            e.printStackTrace();
        }
        return null;
    }

}
