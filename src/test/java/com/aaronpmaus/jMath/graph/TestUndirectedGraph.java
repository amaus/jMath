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

import java.util.Collection;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.io.FileNotFoundException;
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

public class TestUndirectedGraph{
  private UndirectedGraph<Integer> myGraph;
  private UndirectedGraph<Integer> example;
  private Node<Integer> one;
  private Node<Integer> two;
  private Node<Integer> three;
  private Node<Integer> four;
  private Node<Integer> five;
  private Node<Integer> six;
  private Node<Integer> seven;


  @Rule
  public final ExpectedException exception = ExpectedException.none();

  @Before
  public void setUp(){
    myGraph = new UndirectedGraph<Integer>();
    String fileName = "example.dimacs";
    InputStream stream = TestUndirectedGraph.class.getResourceAsStream(fileName);
    example = GraphIO.readFromDimacsFile(stream, fileName);
    one = example.getNode(1);
    two = example.getNode(2);
    three = example.getNode(3);
    four = example.getNode(4);
    five = example.getNode(5);
    six = example.getNode(6);
    seven = example.getNode(7);

  }

  @Test
  public void testGraphConstruction(){
    assertEquals(7, example.size());
    assertEquals(15, example.numEdges());

    assertEquals(4,one.numNeighbors());
    assertEquals(4,two.numNeighbors());
    assertEquals(4,three.numNeighbors());
    assertEquals(5,four.numNeighbors());
    assertEquals(5,five.numNeighbors());
    assertEquals(4,six.numNeighbors());
    assertEquals(4,seven.numNeighbors());

    assertTrue(one.hasNeighbor(two));
    assertTrue(one.hasNeighbor(three));
    assertTrue(one.hasNeighbor(four));
    assertTrue(one.hasNeighbor(six));
    assertFalse(one.hasNeighbor(five));
    assertFalse(one.hasNeighbor(seven));

    assertTrue(two.hasNeighbor(one));
    assertTrue(two.hasNeighbor(three));
    assertTrue(two.hasNeighbor(five));
    assertTrue(two.hasNeighbor(seven));
    assertFalse(two.hasNeighbor(four));
    assertFalse(two.hasNeighbor(six));

    assertTrue(three.hasNeighbor(one));
    assertTrue(three.hasNeighbor(two));
    assertTrue(three.hasNeighbor(four));
    assertTrue(three.hasNeighbor(five));
    assertFalse(three.hasNeighbor(six));
    assertFalse(three.hasNeighbor(seven));

    assertTrue(four.hasNeighbor(one));
    assertTrue(four.hasNeighbor(three));
    assertTrue(four.hasNeighbor(five));
    assertTrue(four.hasNeighbor(six));
    assertTrue(four.hasNeighbor(seven));
    assertFalse(four.hasNeighbor(two));

    assertTrue(five.hasNeighbor(two));
    assertTrue(five.hasNeighbor(three));
    assertTrue(five.hasNeighbor(four));
    assertTrue(five.hasNeighbor(six));
    assertTrue(five.hasNeighbor(seven));
    assertFalse(five.hasNeighbor(one));

    assertTrue(six.hasNeighbor(one));
    assertTrue(six.hasNeighbor(four));
    assertTrue(six.hasNeighbor(five));
    assertTrue(six.hasNeighbor(seven));
    assertFalse(six.hasNeighbor(two));
    assertFalse(six.hasNeighbor(three));

    assertTrue(seven.hasNeighbor(two));
    assertTrue(seven.hasNeighbor(four));
    assertTrue(seven.hasNeighbor(five));
    assertTrue(seven.hasNeighbor(six));
    assertFalse(seven.hasNeighbor(one));
    assertFalse(seven.hasNeighbor(three));
  }

  @Test
  public void testAddNode(){
    assertEquals(0,myGraph.size());
    myGraph.addNode(new Node<Integer>(1));
    assertEquals(1,myGraph.size());
    //assertEquals(1,0);
  }

  @Test
  public void testEdges(){
    assertEquals(15,example.numEdges());
    assertEquals(4,example.getNode(1).numNeighbors());
    assertEquals(4,example.getNode(2).numNeighbors());
    assertEquals(4,example.getNode(3).numNeighbors());
    assertEquals(5,example.getNode(4).numNeighbors());
    assertEquals(5,example.getNode(5).numNeighbors());
    assertEquals(4,example.getNode(6).numNeighbors());
    assertEquals(4,example.getNode(7).numNeighbors());
  }

  @Test
  public void testGetNeighborhoodNodes(){
    Collection<Node<Integer>> neighborhood = example.getNeighborhoodNodes(4);
    assertEquals(6,neighborhood.size());
    ArrayList<Integer> values = new ArrayList<Integer>();
    for(Node<Integer> n : neighborhood){
      values.add(n.get());
    }
    assertTrue(values.contains(1));
    assertTrue(values.contains(3));
    assertTrue(values.contains(4));
    assertTrue(values.contains(5));
    assertTrue(values.contains(6));
    assertTrue(values.contains(7));
  }

  @Test
  public void testGetNeighborhood(){
    UndirectedGraph<Integer> neighborhood = example.getNeighborhood(example.getNode(4));
    assertEquals(6,neighborhood.size());
    ArrayList<Integer> values = new ArrayList<Integer>();
    for(Node<Integer> n : neighborhood){
      values.add(n.get());
    }
    assertTrue(values.contains(1));
    assertTrue(values.contains(3));
    assertTrue(values.contains(4));
    assertTrue(values.contains(5));
    assertTrue(values.contains(6));
    assertTrue(values.contains(7));

    one = neighborhood.getNode(1);
    three = neighborhood.getNode(3);
    four = neighborhood.getNode(4);
    five = neighborhood.getNode(5);
    six = neighborhood.getNode(6);
    seven = neighborhood.getNode(7);

    two = example.getNode(2);

    assertEquals(3,one.numNeighbors());
    assertEquals(3,three.numNeighbors());
    assertEquals(5,four.numNeighbors());
    assertEquals(4,five.numNeighbors());
    assertEquals(4,six.numNeighbors());
    assertEquals(3,seven.numNeighbors());

    assertTrue(one.hasNeighbor(three));
    assertTrue(one.hasNeighbor(four));
    assertTrue(one.hasNeighbor(six));

    assertTrue(three.hasNeighbor(one));
    assertTrue(three.hasNeighbor(four));
    assertTrue(three.hasNeighbor(five));

    assertTrue(four.hasNeighbor(one));
    assertTrue(four.hasNeighbor(three));
    assertTrue(four.hasNeighbor(five));
    assertTrue(four.hasNeighbor(six));
    assertTrue(four.hasNeighbor(seven));

    assertTrue(five.hasNeighbor(three));
    assertTrue(five.hasNeighbor(four));
    assertTrue(five.hasNeighbor(six));
    assertTrue(five.hasNeighbor(seven));

    assertTrue(six.hasNeighbor(one));
    assertTrue(six.hasNeighbor(four));
    assertTrue(six.hasNeighbor(five));
    assertTrue(six.hasNeighbor(seven));

    assertTrue(seven.hasNeighbor(four));
    assertTrue(seven.hasNeighbor(five));
    assertTrue(seven.hasNeighbor(six));

    assertFalse(one.hasNeighbor(two));
    assertFalse(three.hasNeighbor(two));
    assertFalse(five.hasNeighbor(two));
    assertFalse(seven.hasNeighbor(two));

  }

  @Test
  public void testGetNeighborhoodException(){
    Node<Integer> two = example.getNode(2);
    example.removeNodeFromGraph(two);
    exception.expect(NoSuchElementException.class);
    example.getNeighborhood(two);
  }

  @Test
  public void testGetNeighborhoodFromCollectionException(){
    Node<Integer> one = example.getNode(1);
    Node<Integer> two = example.getNode(2);
    ArrayList<Node<Integer>> list = new ArrayList<Node<Integer>>();
    list.add(one);
    list.add(two);
    example.removeNodeFromGraph(two);
    exception.expect(NoSuchElementException.class);
    example.getNeighborhood(list);
  }

  @Test
  public void testGetComplement(){
    UndirectedGraph<Integer> complement = example.getComplement();
    one = complement.getNode(1);
    two = complement.getNode(2);
    three = complement.getNode(3);
    four = complement.getNode(4);
    five = complement.getNode(5);
    six = complement.getNode(6);
    seven = complement.getNode(7);

    assertEquals(7, complement.size());
    assertEquals(6, complement.numEdges());

    assertEquals(2,one.numNeighbors());
    assertEquals(2,two.numNeighbors());
    assertEquals(2,three.numNeighbors());
    assertEquals(1,four.numNeighbors());
    assertEquals(1,five.numNeighbors());
    assertEquals(2,six.numNeighbors());
    assertEquals(2,seven.numNeighbors());

    assertFalse(one.hasNeighbor(two));
    assertFalse(one.hasNeighbor(three));
    assertFalse(one.hasNeighbor(four));
    assertFalse(one.hasNeighbor(six));
    assertTrue(one.hasNeighbor(five));
    assertTrue(one.hasNeighbor(seven));

    assertFalse(two.hasNeighbor(one));
    assertFalse(two.hasNeighbor(three));
    assertFalse(two.hasNeighbor(five));
    assertFalse(two.hasNeighbor(seven));
    assertTrue(two.hasNeighbor(four));
    assertTrue(two.hasNeighbor(six));

    assertFalse(three.hasNeighbor(one));
    assertFalse(three.hasNeighbor(two));
    assertFalse(three.hasNeighbor(four));
    assertFalse(three.hasNeighbor(five));
    assertTrue(three.hasNeighbor(six));
    assertTrue(three.hasNeighbor(seven));

    assertFalse(four.hasNeighbor(one));
    assertFalse(four.hasNeighbor(three));
    assertFalse(four.hasNeighbor(five));
    assertFalse(four.hasNeighbor(six));
    assertFalse(four.hasNeighbor(seven));
    assertTrue(four.hasNeighbor(two));

    assertFalse(five.hasNeighbor(two));
    assertFalse(five.hasNeighbor(three));
    assertFalse(five.hasNeighbor(four));
    assertFalse(five.hasNeighbor(six));
    assertFalse(five.hasNeighbor(seven));
    assertTrue(five.hasNeighbor(one));

    assertFalse(six.hasNeighbor(one));
    assertFalse(six.hasNeighbor(four));
    assertFalse(six.hasNeighbor(five));
    assertFalse(six.hasNeighbor(seven));
    assertTrue(six.hasNeighbor(two));
    assertTrue(six.hasNeighbor(three));

    assertFalse(seven.hasNeighbor(two));
    assertFalse(seven.hasNeighbor(four));
    assertFalse(seven.hasNeighbor(five));
    assertFalse(seven.hasNeighbor(six));
    assertTrue(seven.hasNeighbor(one));
    assertTrue(seven.hasNeighbor(three));

  }

  @Test
  public void testRemoveNodeFromGraph(){
    example.removeNodeFromGraph(two);

    assertEquals(6, example.size());
    assertEquals(11, example.numEdges());

    assertFalse(one.hasNeighbor(two));
    assertFalse(three.hasNeighbor(two));
    assertFalse(five.hasNeighbor(two));
    assertFalse(seven.hasNeighbor(two));

    assertTrue(four.hasNeighbor(one));
    assertTrue(four.hasNeighbor(three));
    assertTrue(four.hasNeighbor(five));
    assertTrue(four.hasNeighbor(six));
    assertTrue(four.hasNeighbor(seven));

    assertFalse(four.hasNeighbor(two));
    assertFalse(six.hasNeighbor(two));
  }
}
