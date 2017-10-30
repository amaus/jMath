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
   public Double getX(){
     return getValue(0);
   }

   /**
   * By common convention, returns the 1th coordinate of this Vector. Useful
   * if the Vector is 3 dimensional.
   * @return The 1th coordinate of this Vector.
   * @since 0.1.0
   */
   public Double getY(){
     return getValue(1);
   }

   /**
   * By common convention, returns the 2th coordinate of this Vector. Useful
   * if the Vector is 3 dimensional.
   * @return The 2th coordinate of this Vector.
   * @since 0.1.0
   */
   public Double getZ(){
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
     Double u1 = getX();
     Double u2 = getY();
     Double u3 = getZ();
     Double v1 = other.getX();
     Double v2 = other.getY();
     Double v3 = other.getZ();
     Double x = (u2 * v3) - (u3 * v2);
     Double y = (u3 * v1) - (u1 * v3);
     Double z = (u1 * v2) - (u2 * v1);
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
     Double x = this.getX() + other.getX();
     Double y = this.getY() + other.getY();
     Double z = this.getZ() + other.getZ();
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
     Double x = this.getX() - other.getX();
     Double y = this.getY() - other.getY();
     Double z = this.getZ() - other.getZ();
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
   public Vector3D multiply(double scalar){
     Double x = this.getX() * scalar;
     Double y = this.getY() * scalar;
     Double z = this.getZ() * scalar;
     return new Vector3D(x,y,z);
   }

   /**
   * @return a unit vector pointing in this same direction as this vector
   */
   public Vector3D toUnitVector(){
     return this.multiply(1.0/this.magnitude());
   }

   @Override
   public void applyTransformation(Transformation t){
     Vector3D vec = t.applyTransformationTo(this);
     this.setValue(0, vec.getValue(0));
     this.setValue(1, vec.getValue(1));
     this.setValue(2, vec.getValue(2));
   }

 }// end of class Vector3D
