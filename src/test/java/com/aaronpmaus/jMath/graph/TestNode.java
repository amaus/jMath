package com.aaronpmaus.jMath.graph;

import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.After;
import org.junit.AfterClass;
import com.aaronpmaus.jMath.graph.*;
import java.util.Collection;

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
  private Node<Integer> n4;
  private Node<Integer> n5;
  private Node<Integer> n6;

  @Before
  public void setUp() {
    n1 = new Node<Integer>(1);
    n2 = new Node<Integer>(2);
    n3 = new Node<Integer>(3);
    n4 = new Node<Integer>(4);
    n5 = new Node<Integer>(5);
    n6 = new Node<Integer>(6);
    n4.addNeighbor(n1);
    n4.addNeighbor(n2);
    n4.addNeighbor(n3);
  }

  @Test
  public void testNodeConstruction() {
    n1 = new Node<Integer>(1);
    n2 = new Node<Integer>(2);
    n3 = new Node<Integer>(3);

    assertEquals(n1.numNeighbors(), 0);
    assertEquals(n2.numNeighbors(), 0);
    assertEquals(n3.numNeighbors(), 0);

    assertEquals(n1.get(), new Integer(1));
    assertEquals(n2.get(), new Integer(2));
    assertEquals(n3.get(), new Integer(3));

    assertFalse(n1.hasNeighbor(n2));
    assertFalse(n1.hasNeighbor(n3));

    Collection<Node<Integer>> nodes = n1.getNeighbors();
    assertEquals(nodes.size(), 0);
  }

  @Test
  public void testHashCode() {
    assertEquals(n1.hashCode(), 1);
    assertEquals(n2.hashCode(), 2);
    assertEquals(n3.hashCode(), 3);
  }

}
