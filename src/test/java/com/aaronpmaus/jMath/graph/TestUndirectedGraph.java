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

import java.util.Collection;
import java.util.ArrayList;
import java.util.List;
import java.util.LinkedList;
import java.util.NoSuchElementException;
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
  private UndirectedGraph<Integer> graph;
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
    graph = new UndirectedGraph<Integer>();
    String fileName = "example.dimacs"; // clique: 4 5 6 7
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
    assertEquals(0,graph.size());
    graph.addVertex(1);
    assertEquals(1,graph.size());
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
  public void testGetNeighborhood(){
    UndirectedGraph<Integer> neighborhood = example.getNeighborhood(4);
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
    example.removeNode(2);
    exception.expect(NoSuchElementException.class);
    example.getNeighborhood(2);
  }

  @Test
  public void testGetNeighborhoodFromCollectionException(){
    Node<Integer> one = example.getNode(1);
    Node<Integer> two = example.getNode(2);
    ArrayList<Integer> list = new ArrayList<Integer>();
    list.add(1);
    list.add(2);
    example.removeNode(2);
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
  public void testGetComplementNoEdgesBoundary(){
    example.addEdge(4, 2);

    UndirectedGraph<Integer> complement = example.getComplement();
    one = complement.getNode(1);
    two = complement.getNode(2);
    three = complement.getNode(3);
    four = complement.getNode(4);
    five = complement.getNode(5);
    six = complement.getNode(6);
    seven = complement.getNode(7);

    assertEquals(7, complement.size());
    assertEquals(5, complement.numEdges());

    assertEquals(2, one.numNeighbors());
    assertEquals(1, two.numNeighbors());
    assertEquals(2, three.numNeighbors());
    assertEquals(0, four.numNeighbors());
    assertEquals(1, five.numNeighbors());
    assertEquals(2, six.numNeighbors());
    assertEquals(2, seven.numNeighbors());
  }

  @Test
  public void testRemoveNode(){
    example.removeNode(2);
    example.removeNode(2);

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

  /**
  * The method getNodes should return a Collection of nodes and if that Collection is
  * modified by adding or removing elements, the graph should not be changed.
  */
  @Test
  public void testGetNodesGraphSafety(){
    Collection<Node<Integer>> values = example.getNodes();
    assertEquals(7, example.size());

    values.remove(one);
    assertEquals(7, example.size());

    values.add(new Node<Integer>(8));
    values.add(new Node<Integer>(9));
    assertEquals(7, example.size());
  }

  @Test
  public void testShortestPath(){
    Node<Integer> n1 = new Node<Integer>(1);
    Node<Integer> n2 = new Node<Integer>(2);
    Node<Integer> n3 = new Node<Integer>(3);
    Node<Integer> n4 = new Node<Integer>(4);
    Node<Integer> n5 = new Node<Integer>(5);
    Node<Integer> n6 = new Node<Integer>(6);
    Node<Integer> n7 = new Node<Integer>(7);
    Node<Integer> n8 = new Node<Integer>(8);
    Node<Integer> n9 = new Node<Integer>(9);
    graph.addEdge(1,2);
    graph.addEdge(2,3);
    graph.addEdge(3,7);
    graph.addEdge(1,4);
    graph.addEdge(4,5);
    graph.addEdge(5,6);
    graph.addEdge(6,7);
    graph.addEdge(8,9);
    assertTrue(graph.hasEdge(1, 2));
    assertTrue(graph.hasEdge(2, 1));
    List<Integer> path = graph.shortestPath(1,2);
    assertEquals(2, path.size());
    assertEquals(new Integer(1), path.get(0));
    assertEquals(new Integer(2), path.get(1));

    path = graph.shortestPath(1,7);
    assertEquals(4, path.size());
    assertEquals(new Integer(1), path.get(0));
    assertEquals(new Integer(2), path.get(1));
    assertEquals(new Integer(3), path.get(2));
    assertEquals(new Integer(7), path.get(3));

    path = graph.shortestPath(1,8);
    assertEquals(0, path.size());
  }

  @Test
  public void testDepthFirstSearch(){
    UndirectedGraph<String> graph = new UndirectedGraph<String>();
    graph.addEdge("A","B");
    graph.addEdge("A","C");
    graph.addEdge("A","E");
    graph.addEdge("B","D");
    graph.addEdge("B","F");
    graph.addEdge("C","G");
    graph.addEdge("F","E");

    List<String> traversal = graph.depthFirstSearch("A");
    assertEquals(traversal.size(), 7);
    //for(String e : traversal){
      //System.out.println(e);
    //}
    // There are multiple correct traversals for the example graph.
    // this is one of them. The one traversed depends on the order
    // that a node's neighbors are returned when visiting it.
    assertEquals(traversal.get(0), "A");
    assertEquals(traversal.get(1), "E");
    assertEquals(traversal.get(2), "F");
    assertEquals(traversal.get(3), "B");
    assertEquals(traversal.get(4), "D");
    assertEquals(traversal.get(5), "C");
    assertEquals(traversal.get(6), "G");
  }

  @Test
  public void testSubset(){
    LinkedList<Integer> list = new LinkedList<Integer>();
    list.add(3);
    list.add(4);
    list.add(5);
    list.add(6);
    UndirectedGraph<Integer> subset = example.subset(list);
    assertEquals(subset.size(), 4);
    assertEquals(subset.numEdges(), 5);
    assertTrue(subset.hasEdge(3,4));
    assertTrue(subset.hasEdge(3,5));
    assertTrue(subset.hasEdge(4,5));
    assertTrue(subset.hasEdge(4,6));
    assertTrue(subset.hasEdge(5,6));

    assertFalse(subset.hasEdge(3,6));
    assertFalse(subset.hasEdge(5,7));

    assertFalse(subset.contains(1));
    assertFalse(subset.contains(2));
    assertFalse(subset.contains(7));
  }
}
