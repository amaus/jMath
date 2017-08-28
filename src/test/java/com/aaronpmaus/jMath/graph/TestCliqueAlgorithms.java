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

import java.io.InputStream;

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
  private UndirectedGraph<Integer> clique;
  private MaxCliqueSolver<Integer> cliqueTool;

  @Before
  public void setUp(){
    String fileName = "example.dimacs";
    InputStream stream = TestUndirectedGraph.class.getResourceAsStream(fileName);
    example = GraphIO.readFromDimacsFile(stream, fileName);
  }

  @Test
  public void testIncMaxCliqueAdapter(){
    cliqueTool = new IncMaxCliqueAdapter();
    UndirectedGraph<Integer> clique = cliqueTool.findMaxClique(example);
    verifyClique(clique);
  }

  @Test
  public void testIncMaxCliqueSolver(){
    cliqueTool = new IncMaxCliqueSolver<Integer>();
    UndirectedGraph<Integer> clique = cliqueTool.findMaxClique(example);
    verifyClique(clique);
  }

  @Test
  public void testMausMaxCliqueSolver(){
    cliqueTool = new MausMaxCliqueSolver<Integer>();
    UndirectedGraph<Integer> clique = cliqueTool.findMaxClique(example);
    verifyClique(clique);
  }

  private void verifyClique(UndirectedGraph<Integer> clique){
    assertTrue(clique.contains(4));
    assertTrue(clique.contains(5));
    assertTrue(clique.contains(6));
    assertTrue(clique.contains(7));
    assertFalse(clique.contains(1));
    assertFalse(clique.contains(2));
    assertFalse(clique.contains(3));
  }
}
