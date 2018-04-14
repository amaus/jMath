package com.aaronpmaus.jMath.graph;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Date;

/**
* A class that uses IncMaxCliqueSolver from Combining MaxSAT Reasoning and Incremental Upper Bound
* for the Maximum Clique Problem Li, Fang, Xu 2013 to find the maximum clique in UndirectedGraphs.
* Credit to the authors for the c source used by this program.
* @version 0.7.0
* @since 0.7.0
*/
public class IncMaxCliqueAdapter extends MaxCliqueSolver<Integer>{

  /**
  * {@inheritDoc}
  * @since 0.7.0
  */
  public UndirectedGraph<Integer> findMaxClique(UndirectedGraph<Integer> graph) {
    // create a deep copy of the graph so that the client's Object is not
    // modified
    graph = new UndirectedGraph<Integer>(graph);
    if(graph.size() <= 1){
      return graph;
    }
    HashMap<Integer, Integer> nodeIDMapping = new HashMap<Integer, Integer>();
    int nodeID = 1;
    for(Node<Integer> n : graph.getNodes()){
      nodeIDMapping.put(nodeID, n.get());
      n.set(nodeID);
      nodeID++;
    }
    // need to rebuild the graph for consistency now that the nodes have changed
    UndirectedGraph<Integer> g = new UndirectedGraph<Integer>(graph);
    //long time = new Date().getTime();
    //String fname = "" + time + "_" + ProcessHandle.current().getPID() + ".dimacs";
    //g.setGraphFileName(fname);
    try {
      GraphIO.writeDimacsFile(g, g.getGraphFileName());
    } catch (IOException e) {
      System.err.println("Could not write out Dimacs file: " + g.getGraphFileName());
      e.printStackTrace();
    }
    UndirectedGraph<Integer> clique = findMaxClique(g.getGraphFileName(), g);
    for(int seqID = 1; seqID < nodeID; seqID++){
      if(clique.contains(seqID)) {
        clique.getNode(seqID).set(nodeIDMapping.get(seqID));
      }
    }
    // need to rebuild the graph for consistency now that the nodes have changed
    clique = new UndirectedGraph<Integer>(clique);
    try {
      Files.deleteIfExists(Paths.get(g.getGraphFileName()));
    } catch(IOException e) {
      System.out.println("could not remove " + g.getGraphFileName());
    }
    return clique;
  }

  /*
  * Find a MAX CLIQUE in g from the dimacs file filename.
  * @param filename the name of the DIMACS file for this graph.
  * @param g the graph to search for max clique in
  * @return a MAX CLIQUE in the graph g
  */
  private UndirectedGraph<Integer> findMaxClique(String filename, UndirectedGraph<Integer> g) {
    // store compiled versions of IncMaxClique in project as resources.
    // find out which OS this is running on.
    // call appropriate executable
    //System.out.println("USING INCMAXCLIQUE");
    String clique = null;
    Process process = null;
    try {
      String os = System.getProperty("os.name").trim().toLowerCase();
      if(os.equals("mac os x")){
        process = new ProcessBuilder("IncMaxCliqueMac", filename).start();
      } else if (os.equals("linux")) {
        process = new ProcessBuilder("IncMaxCliqueLinux", filename).start();
      } else {
        System.out.println("OS not yet supported. Finding max clique IncMaxCliqueAdapter");
        System.out.println("only supported on Linux and Mac.");
      }
      InputStream is = process.getInputStream();
      InputStreamReader isr = new InputStreamReader(is);
      BufferedReader br = new BufferedReader(isr);
      String line;
      ArrayList<String> lines = new ArrayList<String>();
      int i = 0;
      while((line = br.readLine()) != null) {
        lines.add(line);
        i++;
      }
      clique = lines.get(lines.size()-3);
    } catch (IOException e) {
      System.out.println("Running IncMaxClique process IO has failed");
      e.printStackTrace();
    }
    ArrayList<Node<Integer>> nodes = new ArrayList<Node<Integer>>();
    //System.out.println("PRINTING OUT GRAPH");
    //System.out.println(g);
    for(String nodeID : clique.split(" ")) {
      Node<Integer> node = g.getNode(new Integer(nodeID));
      if(node == null) {
        System.out.println("Trying to add null to list of nodes in clique");
        System.out.println("NodeNum: " + new Integer(nodeID));
        System.out.println("nodeID: " + nodeID);
      }
      nodes.add(node);
    }
    return new UndirectedGraph<Integer>(nodes);
  }
}
