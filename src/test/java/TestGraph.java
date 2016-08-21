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

public class TestGraph{
    private Graph<Integer> myGraph;

    @Before
    public void setUp(){
        myGraph = new Graph<Integer>();
    }

    @Test
    public void testAddNode(){
        assertEquals(0,myGraph.size());
        myGraph.addNode(new Node<Integer>(1));
        assertEquals(1,myGraph.size());
        //assertEquals(1,0);
    }
}
