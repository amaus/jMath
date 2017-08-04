package com.aaronpmaus.jMath.linearAlgebra;

import java.lang.IllegalArgumentException;
import java.util.Arrays;
import java.math.BigDecimal;
import java.math.MathContext;
import java.text.DecimalFormat;

/**
 * This class represents a general vector of any dimensions.
 * @author Aaron Maus aaron@aaronpmaus.com
 * @version 0.10.0
 * @since 0.1.0
 */
public class Vector{
  private BigDecimal[] values;
  private final int numDimensions;

  /**
   * Construct a Vector containing the numeric values passed in as Strings.
   *
   * This is the preferred constuctor. It will build BigDecimals at a
   * desired precision.
   *
   * @param vals the values to be added to this vector
   * @since 0.1.0
  */
  public Vector(String... vals){
    this.numDimensions = vals.length;
    if(this.numDimensions == 0){
      throw new IllegalArgumentException("Vector must contain atleast one value.");
    }
    this.values = new BigDecimal[vals.length];
    int i = 0;
    for(String val : vals){
      this.values[i] = new BigDecimal(val, MathContext.DECIMAL128);
      i++;
    }
  }

  /**
   * Construct a vector from a set of BigDecimals.
   *
   * It is preferred that one of the other constructors is used, but if you
   * use this one, it is recommended that you build the BigDecimals using
   * {@link java.math.MathContext#DECIMAL64}
   *
   * @param vals the BigDecimals to build the vector from
  */
  public Vector(BigDecimal... vals){
    this.numDimensions = vals.length;
    if(this.numDimensions == 0){
      throw new IllegalArgumentException("Vector must contain atleast one value.");
    }
    this.values = vals;
  }

  /**
   * Construct a 3 dimensional vector at the origin.
   * @since 0.1.0
  */
  public Vector(){
    this("0.0","0.0","0.0");
  }

  /**
   * Return the value of the vector at the given dimension.
   * @param dimension The dimension of the coorinate to be returned. The first
   *        value is at the 0th dimension.
   * @return The value of the vector at the given dimension.
   * @throws IllegalArgumentException Thrown if dimension is {@code >= getNumDimensions()}.
   * @since 0.10.0
  */
  public BigDecimal getValue(int dimension){
    if(dimension >= 0 && dimension < getNumDimensions()){
      return this.values[dimension];
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
  public BigDecimal[] getValues(){
    final BigDecimal[] coordinates = new BigDecimal[getNumDimensions()];
    for(int i = 0; i < getNumDimensions(); i++){
      coordinates[i] = getValue(i);
    }
    return coordinates;
  }

  /**
   * Returns the number of dimensions of this point.
   * @return The number of dimensions of this point.
   * @since 0.9.0
  */
  public int getNumDimensions(){
    return this.numDimensions;
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
      product += getValue(i).multiply(other.getValue(i)).doubleValue();
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
    double angle = this.dotProduct(other) / (this.magnitude() * other.magnitude());
    angle = Math.acos(angle);
    return Math.toDegrees(angle);
  }

  /**
   * Returns the magnitude (aka length) of this Vector.
   * @return the magnitude
   * @since 0.1.0
  */
  public double magnitude(){
    return Math.sqrt(this.dotProduct(this));
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
      BigDecimal[] vals = new BigDecimal[this.getNumDimensions()];
      for(int i = 0; i < this.getNumDimensions(); i++){
        vals[i] = this.getValue(i).add(other.getValue(i));
      }
      ans = new Vector(vals);
      return ans;
    } else {
      String exceptionString = "Vector::add - " + buildIllegalArgumentExceptionString(values);
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
      BigDecimal[] vals = new BigDecimal[this.getNumDimensions()];
      for(int i = 0; i < this.getNumDimensions(); i++){
        vals[i] = this.getValue(i).subtract(other.getValue(i));
      }
      ans = new Vector(vals);
      return ans;
    } else {
      String exceptionString = "Vector::subtract - " + buildIllegalArgumentExceptionString(values);
      throw new IllegalArgumentException(exceptionString);
    }
  }

  /**
   * Multiply this Vector by a scalar and return a new vector containing the
   * result
   *
   * When constructing a BigDecimal for this method, the preferred constructor
   * is {@link java.math.BigDecimal#BigDecimal(String val)}. See
   * {@link java.math.BigDecimal#BigDecimal(double val)} for the details
   * on why using a double as the argument is unpredictable and using a
   * String is preferred.
   *
   * @param scalar the value to multiply this Vector by
   * @return a new Vector containg the result of the multiplication
   * @since 0.10.0
   * @see java.math.BigDecimal
  */
  public Vector multiply(BigDecimal scalar){
    Vector ans = null;
    BigDecimal[] vals = new BigDecimal[this.getNumDimensions()];
    for(int i = 0; i < this.getNumDimensions(); i++){
      vals[i] = this.getValue(i).multiply(scalar);
    }
    ans = new Vector(vals);
    return ans;
  }

  /**
   * @param values The values to build a String out of.
   * @return The String representation of the values.
   * @since 0.9.0
  */
  public static String buildVectorString(BigDecimal[] values){
    String str = "(";
    for(int i = 0; i < values.length - 1; i++){
      str += String.format("%s, ", new DecimalFormat("0.00").format(values[i]));
    }
    str += String.format("%s)", new DecimalFormat("0.00").format(values[values.length-1]));
    return str;
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
        distance += getValue(i).subtract(otherVector.getValue(i))
            .pow(2)
            .doubleValue();
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
   * Returns the hash code for this vector.
   *
   * Relies on {@link java.util.Arrays#hashCode(Object[] a)} to
   * calculate the hashCode.
   *
   * @see java.util.Arrays#hashCode(Object[] a)
   * @see java.math.BigDecimal#hashCode()
   * @since 0.10.0
  */
  @Override
  public int hashCode(){
    return Arrays.hashCode(this.getValues());
  }

  /**
   * Returns true if both Vectors contain the same values.
   *
   * @since 0.10.0
  */
  @Override
  public boolean equals(Object obj){
    if(obj instanceof Vector){
      Vector other = (Vector)obj;
      if(this.getNumDimensions() != other.getNumDimensions()){
        return false;
      }
      for(int i = 0; i < this.getNumDimensions(); i++){
        BigDecimal num1 = this.getValue(i).setScale(15,BigDecimal.ROUND_HALF_EVEN);
        BigDecimal num2 = other.getValue(i).setScale(15,BigDecimal.ROUND_HALF_EVEN);
        if(!num1.equals(num2)){
          return false;
        }
      }
      return Arrays.equals(this.getValues(), other.getValues());
    }
    return false;
  }

  /**
   * This is a helper method to be used when a vector passed in to another
   * method do not have the same dimension at this point.
   * @param values The values that were passed into the other method.
   * @return A String stating that the Num Dimensions of the Vector passed in
   *     do not match this.getNumDimensions().
   * @since 0.9.0
  */
  protected String buildIllegalArgumentExceptionString(BigDecimal[] values){
      String exceptionString = "Must pass a vector with the correct number of dimensions. \n"
          + "Num Dimensions: " + getNumDimensions() + "\n"
          + "Requires " + getNumDimensions() + " arguments.\n"
          + "Given " + values.length + " arguments: " + buildVectorString(values);
      return exceptionString;
  }

  @Override
  /**
   * An overridden implementation of Object.toString().
   * @returnA String representation of this vector.
   * @since 0.1.0
  */
  public String toString(){
    return buildVectorString(getValues());
  }
}// end of class Vector
