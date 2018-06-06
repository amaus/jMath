package com.aaronpmaus.jMath.graph;

import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Rule;
import org.junit.rules.ExpectedException;

import com.aaronpmaus.jMath.graph.*;
import com.aaronpmaus.jMath.io.GraphIO;

import java.io.InputStream;
import java.util.ArrayList;

// @Test flags a method as a test method.
// @Before indicates that a method will be run before every
//  test method is run.
// @BeforeClass indicates that a method will be run once before
//  any of the other methods in the test suite are run.
// @After indicates that a method will be run after every
//  test method is run.
// @AfterClass indicates that a method will be run once after
//  all the other methods in the test suite finish..

public class TestCliqueAlgorithms {
  private UndirectedGraph<Integer> example;
  private UndirectedGraph<Integer> maxSatGraph;
  private UndirectedGraph<Integer> clique;
  private MaxCliqueSolver<Integer> cliqueTool;

  @Before
  public void setUp() {
    String fileName = "example.dimacs";
    InputStream stream = TestUndirectedGraph.class.getResourceAsStream(fileName);
    example = GraphIO.readFromDimacsFile(stream, fileName);
  }

  @Test
  public void testIncMaxCliqueAdapter() {
    cliqueTool = new IncMaxCliqueAdapter();
    UndirectedGraph<Integer> clique = cliqueTool.findMaxClique(example);
    verifyClique(clique);
  }

  @Test
  public void testIncMaxCliqueSolver() {
    cliqueTool = new IncMaxCliqueSolver();
    UndirectedGraph<Integer> clique = cliqueTool.findMaxClique(example);
    verifyClique(clique);
  }

  @Test
  public void testMausMaxCliqueSolver() {
    cliqueTool = new MausMaxCliqueSolver();
    UndirectedGraph<Integer> clique = cliqueTool.findMaxClique(example);
    verifyClique(clique);
  }

  @Test
  public void testIncMaxCliqueAdapterOnGraphOfSizeOne() {
    UndirectedGraph<Integer> graph = new UndirectedGraph<Integer>();
    graph.addVertex(new Integer(1));
    cliqueTool = new IncMaxCliqueAdapter();
    UndirectedGraph<Integer> clique = cliqueTool.findMaxClique(graph);
    assertTrue(clique.size() == 1);
    assertTrue(clique.contains(1));
  }

  @Test
  public void testIncMaxCliqueAdapterOnEmptyGraph() {
    UndirectedGraph<Integer> graph = new UndirectedGraph<Integer>();
    cliqueTool = new IncMaxCliqueAdapter();
    UndirectedGraph<Integer> clique = cliqueTool.findMaxClique(graph);
    assertTrue(clique.size() == 0);
  }

  @Test
  public void testMaxSatUB() {
    String fileName = "maxSatGraph.dimacs";
    InputStream stream = TestUndirectedGraph.class.getResourceAsStream(fileName);
    maxSatGraph = GraphIO.readFromDimacsFile(stream, fileName);
    ArrayList<ArrayList<Node<Integer>>> colorSets = new ArrayList<ArrayList<Node<Integer>>>();
    ArrayList<Node<Integer>> set = new ArrayList<Node<Integer>>();
    set.add(maxSatGraph.getNode(5));
    colorSets.add(set);
    set = new ArrayList<Node<Integer>>();
    set.add(maxSatGraph.getNode(2));
    set.add(maxSatGraph.getNode(3));
    colorSets.add(set);
    set = new ArrayList<Node<Integer>>();
    set.add(maxSatGraph.getNode(1));
    set.add(maxSatGraph.getNode(4));
    set.add(maxSatGraph.getNode(6));
    colorSets.add(set);
    int maxSatUB = new MaxSatUB(maxSatGraph).estimateCardinality(maxSatGraph);

  }

  private void verifyClique(UndirectedGraph<Integer> clique) {
    assertTrue(clique.contains(4));
    assertTrue(clique.contains(5));
    assertTrue(clique.contains(6));
    assertTrue(clique.contains(7));
    assertFalse(clique.contains(1));
    assertFalse(clique.contains(2));
    assertFalse(clique.contains(3));
  }
}
