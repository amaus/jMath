package com.aaronpmaus.jMath.transformations;

import com.aaronpmaus.jMath.linearAlgebra.Vector3D;
import com.aaronpmaus.jMath.linearAlgebra.Matrix;

import java.math.BigDecimal;
import java.math.MathContext;

import java.util.LinkedList;
import java.util.Iterator;
import java.util.Collection;

/**
* A Transformation is a Matrix that can be applied to a 3D Vector to get a new Vector that has been
* rotated and/or translated in space. All Transformations start as the Identity Matrix. Then
* rotations and translations are added to them. In this way, a Transformation can be built up
* that is composed of multiple rotations and/or translations.
* <p>
* Every Transformation keeps a history of all the Transformations (rotations and translations) that
* have been applied to it.
* Usage:
* <p>
* {@code Transformation t = new Transformation();} <br>
* {@code t.addRotationAboutX(90);}<br>
* {@code t.addTranslation(new Vector(0.0, 0.0, 41.0));}<br>
* <p>
* {@code Vector vec = new Vector(0.0, 1.0, 0.0);} <br>
* {@code Vector transformed = t.applyTransformationTo(vec)}<br>
* {@code System.out.println(transformed); // (0.00, 0.00, 42.00)} <br>
* <p>
* {@code // Undo the transformation by getting the inverse and applying it}<br>
* {@code Transformation inverse = t.inverse();} <br>
* {@code transformed = inverse.applyTransformationTo(transformed);} <br>
* {@code System.out.println(transformed); // (0.00, 1.00, 0.00)} <br>
* <br>
* <p>
* This class is intended to be used in conjunction with
* {@link com.aaronpmaus.jMath.transformations.Transformable Transformable}. In that case, any object
* that implements Transformable can have Transformations applied directly to them.
* <p>
* For Example:
* <p>
* {@code Transformation t = new Transformation();} <br>
* {@code t.addRotationAboutX(90);}<br>
* {@code t.addTranslation(new Vector(0.0, 0.0, 41.0));}<br>
* <p>
* {@code Point3D point = new Point3D(0.0, 1.0, 0.0); // Point3D implements Transformable} <br>
* {@code point.applyTransformation(t)}<br>
* {@code System.out.println(point); // (0.00, 0.00, 42.00)} <br>
* <p>
* {@code // Undo the transformation by getting the inverse and applying it}<br>
* {@code Transformation inverse = t.inverse();} <br>
* {@code point.applyTransformation(inverse)}<br>
* {@code System.out.println(point); // (0.00, 1.00, 0.00)} <br>
*/
public final class Transformation extends TransformationMatrix {
  private static final Vector3D ZERO = new Vector3D(0.0, 0.0, 0.0);
  private static final Matrix IDENTITY = new Matrix(4);
  private LinkedList<TransformationMatrix> history;

  /**
  * Construct a Transformation. All Transformations start as a [4 by 4] identity matrix.
  * <p>
  * Rotations and Translations can then be added to this Transformation.
  */
  public Transformation(){
    history = new LinkedList<TransformationMatrix>();
  }

  /**
  * Include Transformation t in this transformation as one of its components.
  * <p>
  * A Transformation can be composed of multiple rotations and translations.
  * @param t a transformation
  */
  public final void addTransformation(TransformationMatrix t){
    addTransformation(t.getMatrix());
    // Object of type TransformationMatrix can either be of type Transformation, Rotation, or
    // Translation.
    // If t is a Transformation, then it can be composed of multiple components (all three types
    // of TransformationMatrices). Add each component to the history.
    // Otherwise, if t is a Rotation or Translation, it is not a composition. Add it directly to
    // the history.
    if (t instanceof Transformation){
      Transformation composedTransformation = (Transformation)t;
      for(TransformationMatrix component: composedTransformation.getHistory()){
        addToHistory(component);
      }
    } else if(t instanceof Rotation || t instanceof Translation){
      addToHistory(t);
    }
  }

  private void addToHistory(TransformationMatrix t){
    this.history.add(t);
  }

  public Collection<TransformationMatrix> getHistory(){
    return this.history;
  }

  /**
  * @return a new Transformation, the inverse of this Transformation
  */
  public Transformation inverse(){
    // To calculate the inverse of a Transformation, iterate over its history in reverse order,
    // building up the inverse from the inverses of the components.  The inverse of a rotation is
    // its transpose. The inverse of a Translation is a Translation of the negative translation
    // vector. The inverse of a Transformation is the matrix multiplication of the inverses of its
    // components. If one of the components is of type Transformation itself, the process recurses
    // to build its inverse and include it in this inverse.
    Iterator<TransformationMatrix> reverseIterator = history.descendingIterator();
    // history is guaranteed to have atleast one transformation in it, this transformation itself
    Transformation inverse = new Transformation();
    inverse.addTransformation(reverseIterator.next().inverse());
    while(reverseIterator.hasNext()){
      inverse.addTransformation(reverseIterator.next().inverse());
    }
    return inverse;
  }

  /**
  * Include in this Transformation a rotation about the X axis. This rotation obeys the right hand
  * rule.
  * @param degrees an angle, in degrees
  */
  public void addRotationAboutX(double degrees){
    degrees = Math.toRadians(degrees);
    double cos = Math.cos(degrees);
    double sin = Math.sin(degrees);
    Double[][] mat = {{1.0,   0.0,   0.0,    0.0},
                      {0.0,   cos, -1.0*sin, 0.0},
                      {0.0,   sin,   cos,    0.0},
                      {0.0,   0.0,   0.0,    1.0}};
    addTransformation(new Rotation(new Matrix(mat)));
  }

  /**
  * Include in this Transformation a rotation about the Y axis. This rotation obeys the right hand
  * rule.
  * @param degrees an angle, in degrees
  */
  public void addRotationAboutY(double degrees){
    degrees = Math.toRadians(degrees);
    double cos = Math.cos(degrees);
    double sin = Math.sin(degrees);
    Double[][] mat = {{  cos,    0.0,   sin,    0.0},
                      {  0.0,    1.0,   0.0,    0.0},
                      {-1.0*sin, 0.0,   cos,    0.0},
                      {  0.0,    0.0,   0.0,    1.0}};
    addTransformation(new Rotation(new Matrix(mat)));
  }

  /**
  * Include in this Transformation a rotation about the Z axis. This rotation obeys the right hand
  * rule.
  * @param degrees an angle, in degrees
  */
  public void addRotationAboutZ(double degrees){
    degrees = Math.toRadians(degrees);
    double cos = Math.cos(degrees);
    double sin = Math.sin(degrees);
    Double[][] mat = {{cos, -1.0*sin, 0.0,    0.0},
                      {sin,   cos,    0.0,    0.0},
                      {0.0,   0.0,    1.0,    0.0},
                      {0.0,   0.0,    0.0,    1.0}};
    addTransformation(new Rotation(new Matrix(mat)));
  }

  /**
  * Include in this Transformation a rotation about an axis. This axis is a vector pointing from
  * the origin in the direction of vec. This rotation obeys the right hand rule.
  * @param vec the vector to rotate about, a unit vector
  * @param degrees an angle, in degrees
  */
  public void addRotationAboutAxis(Vector3D vec, double degrees){
    vec = vec.toUnitVector();
    degrees = Math.toRadians(degrees);
    double cos = Math.cos(degrees);
    double sin = Math.sin(degrees);
    double x = vec.getValue(0);
    double y = vec.getValue(1);
    double z = vec.getValue(2);
    double oneMinusCos = 1 - cos;
    double xy = x*y;
    double xz = x*z;
    double yz = y*z;
    double xsin = x*sin;
    double ysin = y*sin;
    double zsin = z*sin;
    Double[][] mat = {{cos + x*x*oneMinusCos, xy*oneMinusCos - z*sin, xz*oneMinusCos + ysin, 0.0},
                    {xy*oneMinusCos + zsin, cos + y*y*oneMinusCos,  yz*oneMinusCos - xsin, 0.0},
                    {xz*oneMinusCos - ysin, yz*oneMinusCos + xsin,  cos + z*z*oneMinusCos, 0.0},
                    {         0.0,                  0.0,                    0.0,           1.0}};
    addTransformation(new Rotation(new Matrix(mat)));
  }

  /**
  * Include in this Transformation a rotation about an axis. This axis is a vector pointing from
  * start to end. This rotation obeys the right hand rule.
  * @param start the start point of the axis to rotate about
  * @param direction a vector pointing in the direction of the vector to rotate about
  * @param degrees an angle, in degrees
  */
  public void addRotationAboutAxis(Vector3D start, Vector3D direction, double degrees){
    Transformation composedRotation = new Transformation();
    composedRotation.addTranslation(start.multiply(-1.0));
    composedRotation.addRotationAboutAxis(direction, degrees);
    composedRotation.addTranslation(start);
    addTransformation(composedRotation);
  }

  /**
  * Include in this Transformation a Transformation representing a rotation from the mobile vector
  * onto the reference vector.
  * @param reference the reference vector, has three dimensions
  * @param mobile the mobile vector, has three dimensions
  */
  public void addRotationOntoVector(Vector3D reference, Vector3D mobile){
    reference = reference.toUnitVector();
    mobile = mobile.toUnitVector();
    Vector3D cross = mobile.crossProduct(reference);
    double cosineOfAngle = mobile.dotProduct(reference);
    // if the cross product is zero, the two vectors are already aligned, return the IDENTITY
    if(cross.equals(ZERO)){
      addTransformation(new Rotation(IDENTITY));
    }
    // if the cosine of the angle between them is -1.0, the vectors point in opposite directions,
    // return the negative IDENTITY
    if(Math.abs(cosineOfAngle + 1.0) < 0.000000000001){
      addTransformation(new Rotation(IDENTITY.multiply(-1.0)));
    }
    // Otherwise, calculate the rotation matrix to rotate mobile onto reference
    double v1 = cross.getValue(0);
    double v2 = cross.getValue(1);
    double v3 = cross.getValue(2);
    double onePlusCos = cosineOfAngle + 1.0;
    double v1v2 = (v1*v2)/onePlusCos;
    double v1v3 = (v1*v3)/onePlusCos;
    double v2v3 = (v2*v3)/onePlusCos;
    double negV1Squared = -1.0*v1*v1;
    double negV2Squared = -1.0*v2*v2;
    double negV3Squared = -1.0*v3*v3;
    double diagOne = ((negV3Squared + negV2Squared)/onePlusCos)+1.0;
    double diagTwo = ((negV3Squared + negV1Squared)/onePlusCos)+1.0;
    double diagThree = ((negV2Squared + negV1Squared)/onePlusCos)+1.0;
    Double[][] mat = {{diagOne,    v1v2 - v3,  v1v3 + v2, 0.0},
                      {v1v2 + v3,   diagTwo,   v2v3 - v1, 0.0},
                      {v1v3 - v2,  v2v3 + v1,  diagThree, 0.0},
                      {  0.0,         0.0,        0.0,    1.0}};
    addTransformation(new Rotation(new Matrix(mat)));
  }

  /**
  * Include in this Transformation a translation specified by the given Vector.
  * @param vec a 3D vector, the amount to translate, (deltaX, deltaY, deltaZ)
  */
  public void addTranslation(Vector3D vec){
    if(vec.getNumDimensions() !=3){
      throw new IllegalArgumentException(
          "Transformation::addTranslation(): vec must have 3 dimensions, has "
          + vec.getNumDimensions());
    }
    BigDecimal one = BigDecimal.ONE;
    BigDecimal zero = BigDecimal.ZERO;
    Double[][] mat = {{1.0, 0.0, 0.0,  vec.getValue(0)},
                          {0.0, 1.0, 0.0,  vec.getValue(1)},
                          {0.0, 0.0, 1.0,  vec.getValue(2)},
                          {0.0, 0.0, 0.0,   1.0 }};
    addTransformation(new Translation(new Matrix(mat)));
  }

  @Override
  public int hashCode(){
    return getMatrix().hashCode();
  }

  @Override
  public boolean equals(Object obj){
    if(obj instanceof Transformation){
      Transformation other = (Transformation)obj;
      return getMatrix().equals(other.getMatrix());
    }
    return false;
  }

  @Override
  public String toString(){
    return getMatrix().toString();
  }

  /*
  * Private Inner Class Rotation
  *
  * Allows for the rules of inheritance to be used for the inverse method. Taking the inverse of a
  * Rotation can be accomplished by transposing the Matrix. For Translation, negating the delta
  * values. For SuperClass Transformation, by building up a new Transformation from the inverses
  * of all component transformations.
  */
  private class Rotation extends TransformationMatrix {

    private Rotation(Matrix t){
      super();
      super.addTransformation(t);
      //super.addToHistory(this);
    }

    /*
    * The inverse of a Rotation matrix is its transpose.
    */
    public Rotation inverse(){
      //System.out.println("In Rotation::inverse()");
      // create a copy of this matrix, transpose it, construct and return a Rotation containing it.
      Matrix mat = new Matrix(getMatrix());
      mat = mat.transpose();
      Rotation inverse = new Rotation(mat);
      //System.out.println("rotation inverse history size: " + inverse.getHistorySize());
      return inverse;
    }
  }

  /*
  * Private Inner Class Translation
  */
  private class Translation extends TransformationMatrix {

    private Translation(Matrix t){
      super();
      super.addTransformation(t);
      //super.addToHistory(this);
    }

    /*
    * The inverse of a Translation matrix is the negation of the translation vector.
    */
    public Translation inverse(){
      // Create a copy of the matrix, negate the values. Return a new Translation containing it.
      Matrix mat = new Matrix(getMatrix());
      mat = mat.multiply(-1.0);
      mat = mat.add(IDENTITY.multiply(2.0));
      return new Translation(mat);
    }
  }
}
