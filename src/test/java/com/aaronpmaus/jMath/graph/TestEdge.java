package com.aaronpmaus.jMath.graph;

import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.After;
import org.junit.AfterClass;
import com.aaronpmaus.jMath.graph.*;

// @Test flags a method as a test method.
// @Before indicates that a method will be run before every
//  test method is run.
// @BeforeClass indicates that a method will be run once before
//  any of the other methods in the test suite are run.
// @After indicates that a method will be run after every
//  test method is run.
// @AfterClass indicates that a method will be run once after
//  all the other methods in the test suite finish..

public class TestEdge{
    private Edge<Integer> edgeUnweighted;
    private Edge<Integer> edgeWeighted;
    private Node<Integer> start;
    private Node<Integer> end;

    @Before
    public void setUp(){
        start = new Node<Integer>(1);
        end = new Node<Integer>(2);
        edgeUnweighted = new Edge<Integer>(start, end);
        edgeWeighted = new Edge<Integer>(start, end, 5.0);
    }

    @Test
    public void testGetStart(){
        assertEquals(start,edgeUnweighted.getStart());
        assertEquals(start,edgeWeighted.getStart());
    }

    @Test
    public void testGetEnd(){
        assertEquals(end,edgeUnweighted.getEnd());
        assertEquals(end,edgeWeighted.getEnd());
    }

    @Test
    public void testGetWeight(){
        assertEquals(1.0,edgeUnweighted.getWeight(),0.0000001);
        assertEquals(5.0,edgeWeighted.getWeight(),0.0000001);
    }
}
