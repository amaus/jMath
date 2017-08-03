package com.aaronpmaus.jMath.graph;

import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.After;
import org.junit.AfterClass;
import com.aaronpmaus.jMath.graph.*;
import java.util.Collection;
import java.util.Set;

// @Test flags a method as a test method.
// @Before indicates that a method will be run before every
//  test method is run.
// @BeforeClass indicates that a method will be run once before
//  any of the other methods in the test suite are run.
// @After indicates that a method will be run after every
//  test method is run.
// @AfterClass indicates that a method will be run once after
//  all the other methods in the test suite finish..

public class TestNode{
  private Node<Integer> n1;
  private Node<Integer> n2;
  private Node<Integer> n3;

  @Before
  public void setUp(){
    n1 = new Node<Integer>(1);
    n2 = new Node<Integer>(2);
    n3 = new Node<Integer>(3);
    System.out.println("Running setup method");
  }

  @Test
  public void testNodeConstruction(){
    assertEquals(n1.numNeighbors(), 0);
    assertEquals(n2.numNeighbors(), 0);
    assertEquals(n3.numNeighbors(), 0);

    assertEquals(n1.get(), new Integer(1));
    assertEquals(n2.get(), new Integer(2));
    assertEquals(n3.get(), new Integer(3));

    assertFalse(n1.hasNeighbor(n2));
    assertFalse(n1.hasNeighbor(n3));

    Set<Node<Integer>> nodes = n1.getNeighbors();
    assertEquals(nodes.size(), 0);

  }

  @Test
  public void TestHashCode(){
    assertEquals(n1.hashCode(), 1);
    assertEquals(n2.hashCode(), 2);
    assertEquals(n3.hashCode(), 3);
  }
}
