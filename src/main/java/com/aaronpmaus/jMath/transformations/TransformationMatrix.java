package com.aaronpmaus.jMath.transformations;

import com.aaronpmaus.jMath.linearAlgebra.Matrix;
import com.aaronpmaus.jMath.linearAlgebra.Vector;

/**
* A TransformationMatrix is an augmented matrix that can applied to a vector to transform that
* vector in 3D space.
* <p>
* Any subclass of TransformationMatrix must implement the inverse() method.
* @see com.aaronpmaus.jMath.transformations.Transformation
* @see com.aaronpmaus.jMath.transformations.Transformable
*
* @since 0.12.0
*/
public abstract class TransformationMatrix {
  private Matrix transformation;

  /**
  * Instantiate this TransformationMatrix as the identity matrix.
  */
  public TransformationMatrix(){
    this.transformation = new Matrix(4);
  }

  /**
  * Multiply this TransformationMatrix by t. Heavily restriced on purpose. ONLY to be used by
  * subclasses. Anything that is a subclass will want to maintain bookeeping whenever the
  * TransformationMatrix is modified. For example, keeping a history of all Rotations and
  * Translations applied to this matrix makes it easy to calculate the inverse.
  * <p>
  * This method performs no bookeeping and therefore should not be used by any clients to
  * modify this TransformationMatrix. Use methods provided by subclasses to do so.
  * @param t the matrix to multiply this TransformationMatrix by
  */
  void addTransformation(Matrix t){
    this.transformation = t.multiply(this.transformation);
  }

  /**
  * Return this transformation matrix as a Matrix.
  * @return this transformation matrix, a [4 by 4] matrix
  */
  public Matrix getMatrix(){
    return this.transformation;
  }

  /**
  * Apply this TransformationMatrix to a Vector.
  * <p>
  * @param vec a Vector with 4 dimensions, the first three are coordinates in 3D space, and the
  *   last is the homogeneous coordinate.
  * @return a new Vector, vec transformed. This Vector is returned as a column vector.
  */
  public Vector applyTransformationTo(Vector vec){
    Vector transformed;
    if(vec.isColVector()){
      transformed = getMatrix().multiply(vec).getColVector(0);
    } else {
      vec = vec.transpose();
      transformed = getMatrix().multiply(vec).getColVector(0);
    }
    return transformed;
  }

  /**
  * Return the inverse of this TransformationMatrix, a matrix that when applied to a Vector
  * would perform the opposite Transformation that this matrix would. In other words, the inverse
  * of a TransformationMatrix can undo the effects of that Transformation Matrix.
  * @return the inverse of this TransformationMatrix
  */
  public abstract TransformationMatrix inverse();
}
