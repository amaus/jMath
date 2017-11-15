package com.aaronpmaus.jMath.linearAlgebra;

import java.lang.IllegalArgumentException;
import java.util.Arrays;
import java.math.BigDecimal;
import java.math.MathContext;
import java.text.DecimalFormat;

/**
 * A Vector can have any number of dimensions and supports addition, subtraction, multiplication
 * by a scalar, and dot product.
 * @author Aaron Maus aaron@aaronpmaus.com
 * @version 0.12.0
 * @since 0.1.0
 */
public class Vector extends Matrix{
  private boolean isColVector;

  /**
   * Construct a Vector containing the values passed in as Doubles.
   *
   * @param vals the values to be added to this vector
   * @since 0.1.0
  */
  public Vector(Double... vals){
    super(vals);
    this.isColVector = true;
  }

  /**
   * Construct a 3 dimensional vector at the origin.
   * @since 0.1.0
  */
  public Vector(){
    this(0.0,0.0,0.0);
  }

  private Vector(Double[][] vals){
    super(vals);
    int numRows = vals.length;
    int numCols = vals[0].length;
    // Determine if this vector is a row or column vector based on the dimensions of vals.
    // Note that by default, a Vector of a single value is a column vector.
    if(numCols == 1) { // a single col, therefore a col vector
      this.isColVector = true;
    } else if(numRows == 1){ // a single row, therefore a row vector
      this.isColVector = false;
    } else { // if neither have a dimension of 1, then throw an IllegalArgumentException
      throw new IllegalArgumentException("Vector must be initialized with either "
          + "a single row or column");
    }
  }

  /**
  * @return true if this vector is a column vector, false if this vector is a row vector
  * @since 0.12.0
  */
  public boolean isColVector(){
    return this.isColVector;
  }

  /**
  * Transpose this vector. If it was a column vector, it is now a row vector and vice versa.
  * @since 0.12.0
  */
  public Vector transpose(){
    if(isColVector){
      // construct and return a row vector
      return new Vector(buildRowMatrix(getValues()));
    } else {
      // construct and return a column vector
      return new Vector(getValues());
    }
  }

  /*
  * Helper method for initializing a column Vector. From an array of values, build a 2D array where
  * those values go down the first column.
  */
  private Double[][] buildRowMatrix(Double[] vals){
    Double[][] matrix = new Double[1][vals.length];
    for(int i = 0; i < vals.length; i++){
      matrix[0][i] = vals[i];
    }
    return matrix;
  }

  /**
   * Return the value of the vector at the given dimension.
   * @param dimension The dimension of the coorinate to be returned. The first
   *        value is at the 0th dimension.
   * @param value The value of the vector at the given dimension.
   * @throws IllegalArgumentException Thrown if dimension is {@code >= getNumDimensions()}.
   * @since 0.10.0
  */
  protected void setValue(int dimension, double value){
    if(dimension >= 0 && dimension < getNumDimensions()){
      if(isColVector){
        setElement(dimension,0,value);
      } else {
        setElement(0,dimension,value);
      }
    } else {
      throw new IllegalArgumentException("dimension must be less than the Num Dimensions\n"
          + "of this point. The first coordinate is at the 0th\n"
          + "dimension.\n"
          + "Num Dimensions: " + getNumDimensions() + "\n"
          + "Given Dimension: " + dimension);
    }
  }


  /**
   * Return the value of the vector at the given dimension.
   * @param dimension The dimension of the coorinate to be returned. The first
   *        value is at the 0th dimension.
   * @return The value of the vector at the given dimension.
   * @throws IllegalArgumentException Thrown if dimension is {@code >= getNumDimensions()}.
   * @since 0.10.0
  */
  public Double getValue(int dimension){
    if(dimension >= 0 && dimension < getNumDimensions()){
      if(isColVector){
        return getElement(dimension,0);
      } else {
        return getElement(0,dimension);
      }
    } else {
      throw new IllegalArgumentException("dimension must be less than the Num Dimensions\n"
          + "of this point. The first coordinate is at the 0th\n"
          + "dimension.\n"
          + "Num Dimensions: " + getNumDimensions() + "\n"
          + "Given Dimension: " + dimension);
    }
  }

  /**
   * Return an array holding the values of the Vector.
   *
   * @return An array holding the values of this Vector. This array is
   * a deep copy of the values. Any changes made to this copy do not
   * affect the original Vector.
   * @since 0.10.0
  */
  public Double[] getValues(){
    if(isColVector){
      return getColValues(0);
    } else {
      return getRowValues(0);
    }
  }

  /**
   * Returns the number of dimensions of this point.
   * @return The number of dimensions of this point.
   * @since 0.9.0
  */
  public int getNumDimensions(){
    if(isColVector){
      return getNumRows();
    } else {
      return getNumCols();
    }
  }

  /**
   * Returns the dot product of this vector and the parameter vector.
   * @param other the Vector by which to perform the dot product with.
   * @return the value of the dot product
   * @throws IllegalArgumentException if the Vector passed in does not have the
   *     same Num Dimensions at this vector.
   * @since 0.1.0
  */
  public double dotProduct(Vector other){
    if(getNumDimensions() != other.getNumDimensions()){
      throw new IllegalArgumentException("Vector::dotProduct() "
          + buildIllegalArgumentExceptionString(other.getValues()));
    }

    double product = 0.0;
    for(int i = 0; i < getNumDimensions(); i++){
      product += getValue(i) * other.getValue(i);
    }
    return product;
  }


  /**
   * Calcuates the angle (in degrees) between this vector and the other.
   * @param other the other vector to calculate the angle between
   * @return the angle in degrees
   * @throws IllegalArgumentException thrown if the dimensions of the two vectors
   *          are not the same.
   * @since 0.1.0
  */
  public double angle(Vector other){
    if(getNumDimensions() != other.getNumDimensions()){
      throw new IllegalArgumentException("Vector::angle() "
          + buildIllegalArgumentExceptionString(other.getValues()));
    }
    double angle = this.dotProduct(other) / Math.sqrt(this.magnitudeSquared() * other.magnitudeSquared());
    // special cases if the cos(angle) is -1 or +1. Due to double precision, the value could be
    // slightly less than or greater than -1 or +1 respectively. In either case, Math.acos() returns
    // NaN. Check if cos(angle) is -1 or +1 within tolerance and return either 180 or 0 depending
    // on which.
    if(Math.abs(angle - -1.0) < 0.0000000001){
      return 180.0;
    } else if(Math.abs(angle - 1.0) < 0.0000000001){
      return 0.0;
    } else {
      angle = Math.acos(angle);
    }
    return Math.toDegrees(angle);
  }

  /**
  * @return a unit vector pointing in this same direction as this vector
  */
  public Vector toUnitVector(){
    return this.multiply(1.0/this.magnitude());
  }

  /**
   * @return the magnitude of this vector
   * @since 0.1.0
  */
  public double magnitude(){
    return Math.sqrt(magnitudeSquared());
  }

  /**
  * @return the square of the magnitude of this vector
  * @since 0.11.0
  */
  public double magnitudeSquared(){
    return this.dotProduct(this);
  }

  /**
   * Add two Vectors together and return a new Vector with the result.
   *
   * @param other The other Vector to add to this one
   * @return a new Vector containg the result of the addition
   * @throws IllegalArgumentException Thrown if other.getNumDimensions is != getNumDimensions().
   * @since 0.10.0
  */
  public Vector add(Vector other){
    if(other.getNumDimensions() == getNumDimensions()){
      Vector ans = null;
      Double[] vals = new Double[this.getNumDimensions()];
      for(int i = 0; i < this.getNumDimensions(); i++){
        vals[i] = this.getValue(i) + other.getValue(i);
      }
      ans = new Vector(vals);
      return ans;
    } else {
      String exceptionString = "Vector::add - "
          + buildIllegalArgumentExceptionString(this.getValues());
      throw new IllegalArgumentException(exceptionString);
    }
  }

  /**
   * Subtracts another Vector from this Vector and return a new Vector with the result.
   *
   * @param other The other Vector to subtract from this one
   * @return a new Vector containg the result of the subtraction
   * @throws IllegalArgumentException Thrown if other.getNumDimensions is != getNumDimensions().
   * @since 0.10.0
  */
  public Vector subtract(Vector other){
    if(other.getNumDimensions() == getNumDimensions()){
      Vector ans = null;
      Double[] vals = new Double[this.getNumDimensions()];
      for(int i = 0; i < this.getNumDimensions(); i++){
        vals[i] = this.getValue(i) - other.getValue(i);
      }
      ans = new Vector(vals);
      return ans;
    } else {
      String exceptionString = "Vector::subtract - "
          + buildIllegalArgumentExceptionString(this.getValues());
      throw new IllegalArgumentException(exceptionString);
    }
  }

  /**
   * Multiply this Vector by a scalar and return a new vector containing the
   * result
   *
   * @param scalar the value to multiply this Vector by
   * @return a new Vector containg the result of the multiplication
   * @since 0.10.0
  */
  public Vector multiply(double scalar){
    Double[] vals = new Double[this.getNumDimensions()];
    for(int i = 0; i < this.getNumDimensions(); i++){
      vals[i] = this.getValue(i) * scalar;
    }
    return new Vector(vals);
  }

  /**
   * @param values The values to build a String out of.
   * @return The String representation of the values.
   * @since 0.9.0
  */
  public String buildVectorString(Double[] values){
    if(!isColVector()){
      String str = "| ";
      for(int i = 0; i < values.length - 1; i++){
        str += String.format("%s, ", new DecimalFormat("0.00").format(values[i]));
      }
      str += String.format("%s |", new DecimalFormat("0.00").format(values[values.length-1]));
      return str;
    } else {
      String str = "";
      for(int i = 0; i < values.length; i++){
        str += String.format("| %s |\n", new DecimalFormat("0.00").format(values[i]));
      }
      return str;
    }
  }

  /**
   * Calculates the Euclidean Distance between this Vector and the Vector passed in.
   * @param otherVector The other Vector to calculate the distance too.
   * @return The Euclidean Distance between this Vector and otherVector.
   * @throws IllegalArgumentException Thrown if
   *    otherVector.getNumDimensions() != this.getNumDimensions()
   * @since 0.9.0
  */
  public double distance(Vector otherVector){
    double distance = 0.0;
    if(otherVector.getNumDimensions() == getNumDimensions()){
      for(int i = 0; i < getNumDimensions(); i++){
        distance += Math.pow(getValue(i) - otherVector.getValue(i), 2);
      }
      distance = Math.sqrt(distance);
    } else {
      String exceptionString = "Vector::distance() - Vector must have same Num Dimensions."
        + " this.getNumDimensions(): " + getNumDimensions()
        + " otherVector.getNumDimensions() " + otherVector.getNumDimensions();
      throw new IllegalArgumentException(exceptionString);
    }
    return distance;
  }

  /**
   * This is a helper method to be used when a vector passed in to another
   * method do not have the same dimension at this point.
   * @param values The values that were passed into the other method.
   * @return A String stating that the Num Dimensions of the Vector passed in
   *     do not match this.getNumDimensions().
   * @since 0.9.0
  */
  protected String buildIllegalArgumentExceptionString(Double[] values){
      String exceptionString = "Must pass a vector with the correct number of dimensions. \n"
          + "Num Dimensions: " + getNumDimensions() + "\n"
          + "Requires " + getNumDimensions() + " arguments.\n"
          + "Given " + values.length + " arguments: " + buildVectorString(values);
      return exceptionString;
  }

  /**
   * @return a String representation of this vector in the form "(v0, v1, ..., vn)"
   * @since 0.1.0
  */
  @Override
  public String toString(){
    return buildVectorString(getValues());
  }
}// end of class Vector
