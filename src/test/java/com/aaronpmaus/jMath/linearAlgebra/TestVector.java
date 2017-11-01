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

public class TestVector{
  Vector zeros;
  Vector zerosTwo;
  Vector ones;
  Vector homogenous;
  Vector pi;
  Vector x;
  Vector y;
  Vector three;
  Vector four;
  Vector origin;

  @Rule
  public final ExpectedException exception = ExpectedException.none();

  @Before
  public void setup(){
    zeros = new Vector(0.0,0.0,0.0);
    zerosTwo = new Vector(0.0,0.0,0.0);
    ones = new Vector(1.0,1.0,1.0);
    pi = new Vector(3.14,3.14,3.14);
    homogenous = new Vector(0.0,0.0,0.0,1.0);
    x = new Vector(1.0,0.0);
    y = new Vector(0.0,1.0);
    origin = new Vector(0.0,0.0);
    three = new Vector(3.0,0.0);
    four = new Vector(3.0,4.0);
  }

  @Test
  public void testEquals(){

    assertTrue(zeros.equals(zerosTwo));
    assertFalse(zeros.equals(ones));

    Double a = new Double(106838.81);
    Double b = new Double(263970.96);
    Double c = new Double(879.35);
    Double d = new Double(366790.80);
    Double total = new Double(0.0);
    total = total + a;
    total = total + b;
    total = total + c;
    total = total + d;

    Vector v3 = new Vector(total);
    Vector v4 = new Vector(738479.92);
    assertTrue(v3.equals(v4));

    Vector one = new Vector(0.1);
    Vector doubleOne = new Vector(0.1);
    //System.out.printf("%s\n",
    //    new DecimalFormat("0.0000000000000000000000000000000000000000").format(new BigDecimal(0.1)));
    assertTrue(one.equals(doubleOne));

    Vector v5 = new Vector(3141592.653589793);
    Vector v6 = new Vector(3141592.653589799);
    assertEquals(v5,v6);
  }

  @Test
  public void testAdd(){
    Vector sum = zeros.add(ones);
    assertFalse(zeros.equals(sum));
    assertTrue(ones.equals(sum));

    exception.expect(IllegalArgumentException.class);
    sum = ones.add(homogenous);
  }

  @Test
  public void testSubtract(){
    Vector diff = ones.subtract(ones);
    assertFalse(ones.equals(diff));
    assertTrue(zeros.equals(diff));

    exception.expect(IllegalArgumentException.class);
    diff = ones.subtract(homogenous);
  }

  @Test
  public void testMultiply(){
    Vector multiple = ones.multiply(3.14);
    assertFalse(ones.equals(multiple));
    assertTrue(multiple.equals(pi));
  }

  @Test
  public void testGetValue(){
    Double piScalar = 3.14;
    assertTrue(piScalar.equals(pi.getValue(0)));
    assertTrue(piScalar.equals(pi.getValue(1)));
    assertTrue(piScalar.equals(pi.getValue(2)));
  }

  @Test
  public void testDotProduct(){
    double product = ones.dotProduct(pi);
    assertTrue(Math.abs(product - 9.42) < 0.001);
  }

  @Test
  public void testAngle(){
    double angle = x.angle(y);
    assertTrue(Math.abs(angle - 90) < 0.001);

    // 3-4-5 triangle, expected angle 53.13
    angle = three.angle(four);
    assertTrue(Math.abs(angle - 53.13) < 0.001);
  }

  @Test
  public void testMagnitude(){
    double mag = x.magnitude();
    assertTrue(Math.abs(mag-1) < 0.001);

    mag = three.magnitude();
    assertTrue(Math.abs(mag-3) < 0.001);

    mag = four.magnitude();
    assertTrue(Math.abs(mag-5) < 0.001);

    Vector negOne = x.multiply(-1.0); //new BigDecimal("-1.0"));
    mag = negOne.magnitude();
    assertTrue(Math.abs(mag-1) < 0.001);
  }

  @Test
  public void testDistance(){
    double dist = origin.distance(four);
    assertTrue(Math.abs(dist-5) < 0.001);

    dist = origin.distance(three);
    assertTrue(Math.abs(dist-3) < 0.001);

    dist = zeros.distance(pi);
    assertTrue(Math.abs(dist-5.43864) < 0.000001);

    exception.expect(IllegalArgumentException.class);
    pi.distance(homogenous);
  }

  @Test
  public void testUnitVector(){
    Vector vec = new Vector(1.0, 1.0);
    vec = vec.toUnitVector();
    Vector expected = new Vector(0.5*Math.sqrt(2), 0.5*Math.sqrt(2));
    assertEquals(vec, expected);
  }

  @Test
  public void testValueThrowsExceptionForLowIndex(){
    exception.expect(IllegalArgumentException.class);
    pi.getValue(-1);
  }

  @Test
  public void testValueThrowsExceptionForHighIndex(){
    exception.expect(IllegalArgumentException.class);
    pi.getValue(3);
  }

  @Test
  public void testTranspose(){
    Vector vec = new Vector(0.0, 1.0, 2.0);
    assertTrue(vec.isColVector());
    assertTrue(vec.getNumDimensions() == 3);

    Vector transposed = vec.transpose();
    assertFalse(transposed.isColVector());
    assertTrue(transposed.getNumDimensions() == 3);
  }
}
