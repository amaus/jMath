package com.aaronpmaus.jMath.graph;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.IOException;

public class IncMaxCliqueAdapter<T> implements MaxCliqueSolver<T>{

    public UndirectedGraph<T> findMaxClique(String filename) throws FileNotFoundException{
        // store compiled versions of IncMaxClique in project as resources.
        // find out which OS this is running on.
        // call appropriate executable
        try {
            Process process = new ProcessBuilder("IncMaxClique",filename).start();
            InputStream is = process.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String line;

            while((line = br.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException e) {
            System.out.println("Running IncMaxClique process IO has failed");
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Finds the maximum clique in g
     * @param graph the graph to search for a max clique in
     * @return An {@code UndirectedGraph<T>} that is a max clique in graph
     */
    public UndirectedGraph<T> findMaxClique(UndirectedGraph<T> graph) {  return null; }
}
