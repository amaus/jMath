package com.aaronpmaus.jMath.graph;

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
import java.util.Random;
/*
 * @Test flags a method as a test method.
 * @Before indicates that a method will be run before every
 *test method is run.
 * @BeforeClass indicates that a method will be run once before
 *  any of the other methods in the test suite are run.
 * @After indicates that a method will be run after every
 *  test method is run.
 * @AfterClass indicates that a method will be run once after
 *  all the other methods in the test suite finish..
*/

public class TestMatrix{
  Matrix zeros;
  Matrix zerosTwo;
  Matrix ones;
  Matrix twos;
  Matrix identity;
  Matrix columnVec;
  Matrix rowVec;
  Matrix colTimesRow;

  @Rule
  public final ExpectedException exception = ExpectedException.none();

  @Before
  public void setup(){
    zeros = new Matrix(4, 4, new BigDecimal("0.0", MathContext.DECIMAL64));
    zerosTwo = new Matrix(4, 4, new BigDecimal("0.0", MathContext.DECIMAL64));
    ones = new Matrix(4, 4, new BigDecimal("1.0", MathContext.DECIMAL64));
    twos = new Matrix(4, 4, new BigDecimal("2.0", MathContext.DECIMAL64));
    columnVec = new Matrix(new Vector("1.0","2.0","3.0"));
    rowVec = new Matrix(new Vector("4.0","5.0","6.0")).transpose();
    colTimesRow = new Matrix(new Vector("4.0","8.0","12.0"),
        new Vector("5.0","10.0","15.0"),
        new Vector("6.0","12.0","18.0"));
    identity = new Matrix();
  }

  //@Test
  public void testPrint(){
    System.out.println(zeros);
    System.out.println(zerosTwo);
    System.out.println(ones);
    System.out.println(identity);
  }

  @Test
  public void testEquals(){

    assertTrue(zeros.equals(zerosTwo));
    assertFalse(zeros.equals(ones));

  }

  @Test
  public void testAdd(){
    Matrix sum = zeros.add(ones);
    assertFalse(zeros.equals(sum));
    assertTrue(ones.equals(sum));

    sum = ones.add(ones);
    assertTrue(sum.equals(twos));

    exception.expect(IllegalArgumentException.class);
    ones.add(columnVec);
  }

  @Test
  public void testSubtract(){
    Matrix diff = ones.subtract(ones);
    assertFalse(ones.equals(diff));
    assertTrue(zeros.equals(diff));

    diff = twos.subtract(ones);
    assertTrue(diff.equals(ones));

    exception.expect(IllegalArgumentException.class);
    ones.subtract(columnVec);
  }

  @Test
  public void testMultiply(){
    Matrix multiple = columnVec.multiply(rowVec);
    assertTrue(multiple.equals(colTimesRow));

    exception.expect(IllegalArgumentException.class);
    // 3x1 * 3x3 should have #cols != #rows
    columnVec.multiply(colTimesRow);
  }

  @Test
  public void testDistributiveProperty(){
    Matrix a = generateMatrix(3, 3, 1);
    Matrix b = generateMatrix(3, 3, 2);
    Matrix c = generateMatrix(3, 3, 3);
    // test whether A(B+C) = AB + AC
    Matrix leftHandSide = a.multiply( b.add(c) );
    Matrix rightHandSide = ( a.multiply(b) ).add( a.multiply(c) );
    assertTrue(leftHandSide.equals(rightHandSide));
  }

  private Matrix generateMatrix(int numRows, int numCols, int seed){
    Random gen = new Random(seed);
    Double[][] values = new Double[numRows][numCols];
    for(int i = 0; i < numRows; i++){
      for(int j = 0; j < numCols; j++){
        values[i][j] = gen.nextDouble();
      }
    }
    return new Matrix(values);
  }
}
