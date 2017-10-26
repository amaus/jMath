package com.aaronpmaus.jMath.transformations;

/**
* This interface allows classes to be transformable, that is, they can be rotated and translated in
* 3D space.
* <p>
* Any class that implements this interface must implement the
* {@code applyTransformation(TransformationMatrix t)} method, applying the transformation
* represented by the TransformationMatrix to itself.
* <p>
* Example Usage:
* <p>
* {@code Transformation t = new Transformation(); // extends TransformationMatrix} <br>
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
* @since 0.12.0
*/
public interface Transformable{
  /**
  * Apply the TransformationMatrix to this object.
  * <p>
  * @param t the TransformationMatrix to apply to this Transformable
  * @see com.aaronpmaus.jMath.transformations.Transformation
  */
  void applyTransformation(TransformationMatrix t);
}
