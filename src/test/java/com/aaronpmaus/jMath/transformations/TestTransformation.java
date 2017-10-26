package com.aaronpmaus.jMath.transformations;

import com.aaronpmaus.jMath.linearAlgebra.Matrix;
import com.aaronpmaus.jMath.linearAlgebra.Vector;
import com.aaronpmaus.jMath.linearAlgebra.Point3D;

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

public class TestTransformation {
  @Test
  public void testRotationAboutX(){
    Transformation t = new Transformation();
    t.addRotationAboutX(90);
    Double[][] rot90 = {{ 1.0,  0.0,  0.0,  0.0},
                        { 0.0,  0.0, -1.0,  0.0},
                        { 0.0,  1.0,  0.0,  0.0},
                        { 0.0,  0.0,  0.0,  1.0}};
    Matrix expected = new Matrix(rot90);
    assertEquals(t.getMatrix(), expected);

    Point3D coor = new Point3D(0.0, 1.0, 0.0);
    coor.applyTransformation(t);
    expected = new Point3D(0.0,  0.0, 1.0);
    assertEquals(coor, expected);
  }

  @Test
  public void testRotationAboutY(){
    Transformation t = new Transformation();
    t.addRotationAboutY(90);
    Double[][] rot90 = {{ 0.0,  0.0,  1.0,  0.0},
                        { 0.0,  1.0,  0.0,  0.0},
                        {-1.0,  0.0,  0.0,  0.0},
                        { 0.0,  0.0,  0.0,  1.0}};
    Matrix expected = new Matrix(rot90);
    assertEquals(t.getMatrix(), expected);

    Point3D coor = new Point3D(0.0, 0.0, 1.0);
    coor.applyTransformation(t);
    expected = new Point3D(1.0, 0.0, 0.0);
    assertEquals(coor, expected);
  }

  @Test
  public void testRotationAboutZ(){
    Transformation t = new Transformation();
    t.addRotationAboutZ(90);
    Double[][] rot90 = {{ 0.0, -1.0,  0.0,  0.0},
                        { 1.0,  0.0,  0.0,  0.0},
                        { 0.0,  0.0,  1.0,  0.0},
                        { 0.0,  0.0,  0.0,  1.0}};
    Matrix expected = new Matrix(rot90);
    assertEquals(t.getMatrix(), expected);

    Point3D coor = new Point3D(1.0, 0.0, 0.0);
    coor.applyTransformation(t);
    expected = new Point3D(0.0, 1.0, 0.0);
    assertEquals(coor, expected);
  }

  @Test
  public void testRotationAboutAxis(){
    // Rotate about the X axis
    Transformation t = new Transformation();
    t.addRotationAboutAxis(
        new Point3D(1.0, 0.0, 0.0), 90);
    Double[][] rot90X = {{ 1.0,  0.0,  0.0,  0.0},
                        { 0.0,  0.0, -1.0,  0.0},
                        { 0.0,  1.0,  0.0,  0.0},
                        { 0.0,  0.0,  0.0,  1.0}};
    Matrix expected = new Matrix(rot90X);
    assertEquals(t.getMatrix(), expected);

    Point3D coor = new Point3D(0.0, 0.0, 1.0);
    coor.applyTransformation(t);
    expected = new Point3D(0.0, -1.0, 0.0);
    assertEquals(coor, expected);

    // Rotate about the Y axis
    t = new Transformation();
    t.addRotationAboutAxis(
        new Point3D(0.0, 1.0, 0.0), 90);
    Double[][] rot90Y = {{ 0.0,  0.0,  1.0,  0.0},
                        { 0.0,  1.0,  0.0,  0.0},
                        {-1.0,  0.0,  0.0,  0.0},
                        { 0.0,  0.0,  0.0,  1.0}};
    expected = new Matrix(rot90Y);
    assertEquals(t.getMatrix(), expected);

    coor = new Point3D(0.0, 0.0, 1.0);
    coor.applyTransformation(t);
    expected = new Point3D(1.0, 0.0, 0.0);
    assertEquals(coor, expected);

    // Rotate about the Z axis
    t = new Transformation();
    t.addRotationAboutAxis(
        new Point3D(0.0, 0.0, 1.0), 90);
    Double[][] rot90Z = {{ 0.0, -1.0,  0.0,  0.0},
                        { 1.0,  0.0,  0.0,  0.0},
                        { 0.0,  0.0,  1.0,  0.0},
                        { 0.0,  0.0,  0.0,  1.0}};
    expected = new Matrix(rot90Z);
    assertEquals(t.getMatrix(), expected);

    coor = new Point3D(1.0, 0.0, 0.0);
    coor.applyTransformation(t);
    expected = new Point3D(0.0, 1.0, 0.0);
    assertEquals(coor, expected);
  }

  @Test
  public void testTranslation(){
    Transformation t = new Transformation();
    t.addTranslation(new Vector(3.0,4.0,5.0));
    Double[][] rot90 = {{ 1.0,  0.0,  0.0,  3.0},
                        { 0.0,  1.0,  0.0,  4.0},
                        { 0.0,  0.0,  1.0,  5.0},
                        { 0.0,  0.0,  0.0,  1.0}};
    Matrix expected = new Matrix(rot90);
    assertEquals(t.getMatrix(), expected);

    Point3D coor = new Point3D(1.0, 1.0, 1.0);
    coor.applyTransformation(t);
    expected = new Point3D(4.0, 5.0, 6.0);
    assertEquals(coor, expected);
  }

  @Test
  public void testComposedTransformation(){
    Transformation t = new Transformation();
    //System.out.println("INITIAL TRANSFORMATION");
    //System.out.println(t.getMatrix());
    //System.out.println("Adding 90 degree Rotation about Y");
    t.addRotationAboutY(90);
    //System.out.println(t.getMatrix());
    //System.out.println("Adding Translation (6.0, 7.0, 8.0)");
    t.addTranslation(new Vector(6.0,7.0,8.0));
    //System.out.println(t.getMatrix());
    Double[][] rot90 = {{ 0.0,  0.0,  1.0,  6.0},
                        { 0.0,  1.0,  0.0,  7.0},
                        {-1.0,  0.0,  0.0,  8.0},
                        { 0.0,  0.0,  0.0,  1.0}};
    Matrix expected = new Matrix(rot90);
    //System.out.println(expected);
    assertEquals(t.getMatrix(), expected);

    Point3D coor = new Point3D(0.0, 0.0, 1.0);
    coor.applyTransformation(t);
    //System.out.println(coor);
    //System.out.println(expected);
    expected = new Point3D(7.0,7.0,8.0);
    assertEquals(coor, expected);
  }

  @Test
  public void testSingleInverse(){
    Transformation t = new Transformation();
    t.addRotationAboutY(90);

    Point3D coor = new Point3D(0.0, 0.0, 1.0);
    coor.applyTransformation(t);

    Matrix expected = new Point3D(1.0, 0.0, 0.0);
    assertEquals(coor, expected);

    Transformation inverse = t.inverse();

    coor.applyTransformation(inverse);
    expected = new Point3D(0.0, 0.0, 1.0);
    assertEquals(coor, expected);

    t.addTransformation(inverse);
    assertEquals(t.getMatrix(), new Matrix(4));
  }

  @Test
  public void testComposedInverse(){
    Point3D coor = new Point3D(0.0, 0.0, 1.0);

    Transformation t = new Transformation();
    t.addRotationAboutX(90); // After rotation: (0.0, -1.0, 0.0)
    t.addTranslation(new Vector(43.0, 43.0, 43.0)); // After translation: (43.0, 42.0, 43.0)

    // Apply the composed transformation and ensure the result is correct
    coor.applyTransformation(t);
    Matrix expected = new Point3D(43.0, 42.0, 43.0);
    assertEquals(coor, expected);

    Transformation inverse = t.inverse();

    // Apply the inverse and ensure the result is the original coordinates
    coor.applyTransformation(inverse);
    expected = new Point3D(0.0, 0.0, 1.0);
    assertEquals(coor, expected);

    // Apply the inverse to the original transformation and ensure the result is the identity
    t.addTransformation(inverse);
    assertEquals(t.getMatrix(), new Matrix(4));

    // A more rigorous test of the inverse
    // Take the inverse of the current transformation (currently the identity)
    // Ensure that this inverse is still the identity
    inverse = t.inverse();
    assertEquals(inverse.getMatrix(), new Matrix(4));

    // Apply the inverse to t
    // Ensure that the result remains the identity
    t.addTransformation(inverse);
    assertEquals(t.getMatrix(), new Matrix(4));
  }

  @Test
  public void testRotateOntoVector(){
    Point3D ref = new Point3D(1.0, 2.0, 3.0);
    Point3D mobile = new Point3D(1.0, 1.0, 1.0);

    Transformation t = new Transformation();
    t.addRotationOntoVector(ref, mobile);

    mobile.applyTransformation(t);
    assertEquals(mobile.toUnitVector(), ref.toUnitVector());
  }
}
