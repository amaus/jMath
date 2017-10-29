package com.aaronpmaus.jMath.linearAlgebra;

import com.aaronpmaus.jMath.transformations.Transformable;
import com.aaronpmaus.jMath.transformations.Transformation;
import com.aaronpmaus.jMath.transformations.TransformationMatrix;

import java.lang.IllegalArgumentException;
import java.math.BigDecimal;
import java.math.MathContext;

/**
 * A Vector in 3D space, Transformable.
 * <p>
 * Creating a Vector3D and Transforming it:
 * <p>
 * {@code Transformation t = new Transformation();} <br>
 * {@code t.addRotationAboutX(90);}<br>
 * {@code t.addTranslation(new Vector(0.0, 0.0, 41.0));}<br>
 * <p>
 * {@code Vector3D vector = new Vector3D(0.0, 1.0, 0.0); // Vector3D implements Transformable} <br>
 * {@code vector.applyTransformation(t)}<br>
 * {@code System.out.println(Vector); // (0.00, 0.00, 42.00)} <br>
 * <p>
 * {@code // Undo the transformation by getting the inverse and applying it}<br>
 * {@code Transformation inverse = t.inverse();} <br>
 * {@code vector.applyTransformation(inverse)}<br>
 * {@code System.out.println(vector); // (0.00, 1.00, 0.00)} <br>
 * @author  Aaron Maus aaron@aaronpmaus.com
 * @version 0.12.0
 * @since 0.12.0
 */
 public class Vector3D extends Vector implements Transformable{

   /**
   * @param x the coordinate in the first dimension, x
   * @param y the coordinate in the second dimension, y
   * @param z the coordinate in the third dimension, z
   * @since 0.1.0
   */
   public Vector3D(Double x, Double y, Double z){
     super(x, y, z);
   }

   /**
   * @param x the coordinate in the first dimension, x
   * @param y the coordinate in the second dimension, y
   * @param z the coordinate in the third dimension, z
   * @since 0.1.0
   */
   public Vector3D(BigDecimal x, BigDecimal y, BigDecimal z){
     super(x, y, z);
   }

   /**
   * Copy Constructor
   * @param vec the vector to create a copy of
   */
   public Vector3D(Vector3D vec){
     super(vec.getX(), vec.getY(), vec.getZ());
   }

   /**
   * The default constructor creates a Vector of 3 dimensions at (0,0,0).
   * @since 0.1.0
   */
   public Vector3D(){
     super(0.0, 0.0, 0.0);
   }

   /**
   * By common convention, returns the 0th coordinate of this Vector. Useful
   * if the Vector is 3 dimensional.
   * @return The 0th coordinate of this Vector.
   * @since 0.1.0
   */
   public BigDecimal getX(){
     return getValue(0);
   }

   /**
   * By common convention, returns the 1th coordinate of this Vector. Useful
   * if the Vector is 3 dimensional.
   * @return The 1th coordinate of this Vector.
   * @since 0.1.0
   */
   public BigDecimal getY(){
     return getValue(1);
   }

   /**
   * By common convention, returns the 2th coordinate of this Vector. Useful
   * if the Vector is 3 dimensional.
   * @return The 2th coordinate of this Vector.
   * @since 0.1.0
   */
   public BigDecimal getZ(){
     return getValue(2);
   }

   /**
   * Return the cross product of this vector and the other.
   * <p>
   * Both vectors must be three dimensional.
   * @param other a Vector3D
   * @return a three dimensional vector that is the cross product of this and other
   * @since 0.12.2
   */
   public Vector3D crossProduct(Vector3D other){
     BigDecimal u1 = getX();
     BigDecimal u2 = getY();
     BigDecimal u3 = getZ();
     BigDecimal v1 = other.getX();
     BigDecimal v2 = other.getY();
     BigDecimal v3 = other.getZ();
     BigDecimal x = u2.multiply(v3).subtract(u3.multiply(v2));
     BigDecimal y = u3.multiply(v1).subtract(u1.multiply(v3));
     BigDecimal z = u1.multiply(v2).subtract(u2.multiply(v1));
     return new Vector3D(x,y,z);
   }

   /**
    * Add two Vectors together and return a new Vector3D with the result.
    *
    * @param other The other Vector to add to this one
    * @return a new Vector containg the result of the addition
    * @since 0.12.2
   */
   public Vector3D add(Vector3D other){
     BigDecimal x = this.getX().add(other.getX());
     BigDecimal y = this.getY().add(other.getY());
     BigDecimal z = this.getZ().add(other.getZ());
     return new Vector3D(x,y,z);
   }

   /**
    * Subtract two Vectors together and return a new Vector3D with the result.
    *
    * @param other The other Vector to subtract to this one
    * @return a new Vector containg the result of the subtraction
    * @since 0.12.2
   */
   public Vector3D subtract(Vector3D other){
     BigDecimal x = this.getX().subtract(other.getX());
     BigDecimal y = this.getY().subtract(other.getY());
     BigDecimal z = this.getZ().subtract(other.getZ());
     return new Vector3D(x,y,z);
   }

   /**
    * Multiply this Vector3D by a scalar and return a new vector containing the
    * result
    *
    * When constructing a BigDecimal for this method, the preferred constructor
    * is {@link java.math.BigDecimal#BigDecimal(String val)}. See
    * {@link java.math.BigDecimal#BigDecimal(double val)} for the details
    * on why using a double as the argument is unpredictable and using a
    * String is preferred.
    *
    * @param scalar the value to multiply this Vector by
    * @return a new Vector3D containg the result of the multiplication
    * @since 0.12.2
    * @see java.math.BigDecimal
   */
   public Vector3D multiply(BigDecimal scalar){
     BigDecimal x = this.getX().multiply(scalar);
     BigDecimal y = this.getY().multiply(scalar);
     BigDecimal z = this.getZ().multiply(scalar);
     return new Vector3D(x,y,z);
   }

   /**
   * @return a unit vector pointing in this same direction as this vector
   */
   public Vector3D toUnitVector(){
     return this.multiply(new BigDecimal(1.0/this.magnitude(), MathContext.DECIMAL128));
   }

   @Override
   public void applyTransformation(Transformation t){
     System.out.println("Vector3D::applyTransformation()");
     Vector3D vec = t.applyTransformationTo(this);
     System.out.println("Vector3D::applyTransformation(), Transformation apply complete.");
     this.setValue(0, vec.getValue(0));
     this.setValue(1, vec.getValue(1));
     this.setValue(2, vec.getValue(2));
   }

 }// end of class Vector3D
