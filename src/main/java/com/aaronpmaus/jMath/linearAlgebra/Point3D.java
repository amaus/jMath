package com.aaronpmaus.jMath.linearAlgebra;

import com.aaronpmaus.jMath.transformations.Transformable;
import com.aaronpmaus.jMath.transformations.Transformation;
import com.aaronpmaus.jMath.transformations.TransformationMatrix;

import java.lang.IllegalArgumentException;
import java.math.BigDecimal;

/**
 * A point in 3D space, Transformable.
 * @author  Aaron Maus aaron@aaronpmaus.com
 * @version 0.12.0
 * @since 0.12.0
 */
 public class Point3D extends Vector implements Transformable{

   /**
   * @param x the coordinate in the first dimension, x
   * @param y the coordinate in the second dimension, y
   * @param z the coordinate in the third dimension, z
   * @since 0.1.0
   */
   public Point3D(Double x, Double y, Double z){
     super(x, y, z);
   }

   /**
   * The default constructor creates a point of 3 dimensions at (0,0,0).
   * @since 0.1.0
   */
   public Point3D(){
     super(0.0, 0.0, 0.0);
   }

   /**
   * By common convention, returns the 0th coordinate of this point. Useful
   * if the point is 3 dimensional.
   * @return The 0th coordinate of this point.
   * @since 0.1.0
   */
   public BigDecimal getX(){
     return getValue(0);
   }

   /**
   * By common convention, returns the 1th coordinate of this point. Useful
   * if the point is 3 dimensional.
   * @return The 1th coordinate of this point.
   * @since 0.1.0
   */
   public BigDecimal getY(){
     return getValue(1);
   }

   /**
   * By common convention, returns the 2th coordinate of this point. Useful
   * if the point is 3 dimensional.
   * @return The 2th coordinate of this point.
   * @since 0.1.0
   */
   public BigDecimal getZ(){
     return getValue(2);
   }

   @Override
   public void applyTransformation(TransformationMatrix t){
     Vector homogeneous = new Vector(getX(), getY(), getZ(), BigDecimal.ONE);
     Vector vec = t.applyTransformationTo(homogeneous);
     this.setValue(0, vec.getValue(0));
     this.setValue(1, vec.getValue(1));
     this.setValue(2, vec.getValue(2));
   }

 }// end of class Point3D
