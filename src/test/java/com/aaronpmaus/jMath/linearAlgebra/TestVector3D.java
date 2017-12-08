package com.aaronpmaus.jMath.linearAlgebra;

import com.aaronpmaus.jMath.linearAlgebra.*;
import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Rule;
import org.junit.rules.ExpectedException;

import java.math.BigDecimal;
import java.math.MathContext;
import java.text.DecimalFormat;
/*
* @Test flags a method as a test method.
* @Before indicates that a method will be run before every
*  test method is run.
* @BeforeClass indicates that a method will be run once before
*  any of the other methods in the test suite are run.
* @After indicates that a method will be run after every
*  test method is run.
* @AfterClass indicates that a method will be run once after
*  all the other methods in the test suite finish..
*/

public class TestVector3D{

  @Test
  public void testCalculateDihedralAngle(){
    Vector3D a = new Vector3D( 0.0, 1.0, 0.0);
    Vector3D b = new Vector3D( 0.0, 0.0, 0.0);
    Vector3D c = new Vector3D( 0.0, 0.0,-1.0);
    Vector3D d = new Vector3D( -1.4142, 1.4142,-1.0);

    double angle = Vector3D.calculateDihedralAngle(a,b,c,d);
    double expected = 45.0;
    assertTrue(Math.abs(angle - expected) < 0.0001);

    d = new Vector3D( -1.4142, -1.4142,-1.0);
    angle = Vector3D.calculateDihedralAngle(a,b,c,d);
    expected = 135.0;
    assertTrue(Math.abs(angle - expected) < 0.0001);

    d = new Vector3D( 1.4142, -1.4142,-1.0);
    angle = Vector3D.calculateDihedralAngle(a,b,c,d);
    expected = -135.0;
    assertTrue(Math.abs(angle - expected) < 0.0001);

    d = new Vector3D( 1.4142, 1.4142,-1.0);
    angle = Vector3D.calculateDihedralAngle(a,b,c,d);
    expected = -45.0;
    assertTrue(Math.abs(angle - expected) < 0.0001);

    d = new Vector3D(-1.0, 0.0, -1.0);
    angle = Vector3D.calculateDihedralAngle(a,b,c,d);
    expected = 90.0;
    assertTrue(Math.abs(angle - expected) < 0.0001);

    d = new Vector3D(1.0, 0.0, -1.0);
    angle = Vector3D.calculateDihedralAngle(a,b,c,d);
    expected = -90.0;
    
    assertTrue(Math.abs(angle - expected) < 0.0001);
  }
}
