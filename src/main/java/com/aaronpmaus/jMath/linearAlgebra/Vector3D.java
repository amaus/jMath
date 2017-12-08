package com.aaronpmaus.jMath.linearAlgebra;

import com.aaronpmaus.jMath.transformations.Transformable;
import com.aaronpmaus.jMath.transformations.Transformation;
import com.aaronpmaus.jMath.transformations.TransformationMatrix;

import java.lang.IllegalArgumentException;

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
 * @version 0.12.2
 * @since 0.12.2
 */
 public class Vector3D extends Vector implements Transformable{

   /**
   * @param x the coordinate in the first dimension, x
   * @param y the coordinate in the second dimension, y
   * @param z the coordinate in the third dimension, z
   */
   public Vector3D(Double x, Double y, Double z){
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
   */
   public Vector3D(){
     super(0.0, 0.0, 0.0);
   }

   /**
   * By common convention, returns the 0th coordinate of this Vector. Useful
   * if the Vector is 3 dimensional.
   * @return The 0th coordinate of this Vector.
   */
   public Double getX(){
     return getValue(0);
   }

   /**
   * By common convention, returns the 1th coordinate of this Vector. Useful
   * if the Vector is 3 dimensional.
   * @return The 1th coordinate of this Vector.
   */
   public Double getY(){
     return getValue(1);
   }

   /**
   * By common convention, returns the 2th coordinate of this Vector. Useful
   * if the Vector is 3 dimensional.
   * @return The 2th coordinate of this Vector.
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
    * @param scalar the value to multiply this Vector by
    * @return a new Vector3D containg the result of the multiplication
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

   /**
   * Calculate the dihedral angle between 2 planes defined by the points ABC, and BCD respectively.
   * A, B, C, and D must all be distinct points.
   * @param a a point in the plane ABC
   * @param b a point in both planes ABC and BCD
   * @param c a point in both planes ABC and BCD
   * @param d a point in the plane BCD
   * @return the angle between the two planes, in degrees
   */
   public static double calculateDihedralAngle(Vector3D a, Vector3D b, Vector3D c, Vector3D d){
     // Calculate Normal of ABC plane
    Vector3D norm1 = (a.subtract(b).crossProduct(c.subtract(b)));
    // Calculate Normal of BCD plane
    Vector3D norm2 = (b.subtract(c).crossProduct(d.subtract(c)));
    double angle = norm1.angle(norm2);

    // if the dot product of 2 vectors is positive, then the angle between them is between [0,90) or
    // (270,360]. If negative, the angle is between (90,270). Use this fact to determine if the
    // dihedral angle should be negative or positive.

    // One way to think about this is that we want to know if C->D falls to the left or right of the
    // B->A vector. Take the ABC plane. If we orient that plane so that B is at the origin, A falls
    // on the positive Y-axis, and the plane coincides with the X=0 plane, then norms fall on the
    // X-axis. Take the norm that falls on the negative X-axis. If the dot product of that norm and
    // the C->D vector is positive, then C->D points to the left of the ABC plane, that is, it falls
    // to the left of the B->A vector. Otherwise, C->D points to the right from the ABC plane, or
    // falls to the right of the B->A vector.
    double signTest = norm1.dotProduct(d.subtract(c));
    // if the signTest is 0, then the norm and CD are orthogonal, implying that the dihedral
    // angle is 180. The ambiguity around 0 (imprecision leading to -/+ 0) will result in the
    // angle being reported at either 180 or -180, both of which are the same. For consistency,
    // if the signTest is less than the double precision limit (1*10^-14), return the positive
    // angle. 
    if(Math.abs(signTest) < 0.00000000000001){
      return angle;
    }
    if(signTest < 0){
      angle *= -1.0;
    }
    return angle;

   }

   @Override
   public void applyTransformation(Transformation t){
     Vector3D vec = t.applyTransformationTo(this);
     this.setValue(0, vec.getValue(0));
     this.setValue(1, vec.getValue(1));
     this.setValue(2, vec.getValue(2));
   }

 }// end of class Vector3D
