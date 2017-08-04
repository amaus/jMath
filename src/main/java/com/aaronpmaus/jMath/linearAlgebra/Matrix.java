package com.aaronpmaus.jMath.linearAlgebra;

import java.lang.IllegalArgumentException;
import java.math.BigDecimal;
import java.math.MathContext;

import java.text.DecimalFormat;

/**
* This class represents a 2-dimensional matrix.
* @author Aaron Maus aaron@aaronpmaus.com
* @version 0.10.0
* @since 0.1.0
*/
public class Matrix{
  private BigDecimal[][] matrix;
  private final int numRows;
  private final int numCols;

  /**
  * Construct a 2-dimensional Matrix holding the values in the 2D Array passed in.
  *
  * @param matrix A 2-dimensional array holding the values for the matrix. It must not
  * be null.
  * @throws IllegalArgumentException if the matrix is null or not rectangular
  * @since 0.1.0
  */
  public Matrix(Double[][] matrix){
    validateMatrixDimensions(matrix);
    this.numRows = matrix.length;
    this.numCols = matrix[0].length;

    this.matrix = new BigDecimal[numRows][numCols];
    for(int i = 0; i < numRows; i++){
      for(int j = 0; j < numCols; j++){
        if(matrix[i][j] == null){
          throw new IllegalArgumentException(
          String.format("matrix[%d][%d] is null.",i,j)
          + "The matrix must not contain any null values.");
        }
        this.matrix[i][j] = new BigDecimal(matrix[i][j], MathContext.DECIMAL128);
      }
    }
  }

  /**
  * Construct a 2-dimensional Matrix holding the values in the 2D Array passed in.
  *
  * @param matrix A 2-dimensional array holding the values for the matrix. It must not
  * be null.
  * @throws IllegalArgumentException if the matrix is null or not rectangular
  * @since 0.1.0
  */
  public Matrix(BigDecimal[][] matrix){
    validateMatrixDimensions(matrix);
    this.numRows = matrix.length;
    this.numCols = matrix[0].length;

    this.matrix = new BigDecimal[numRows][numCols];
    for(int i = 0; i < numRows; i++){
      for(int j = 0; j < numCols; j++){
        if(matrix[i][j] == null){
          throw new IllegalArgumentException(
              String.format("matrix[%d][%d] is null.",i,j)
              + "The matrix must not contain any null values.");
        }
        this.matrix[i][j] = matrix[i][j];
      }
    }
  }

  /**
  * Construct a 2D Matrix from a set of column vectors passed in.
  *
  * There must be atleast one column vector and all column vectors must have
  * the same number of rows.
  *
  * @param colVectors the column vectors to build this matrix from
  * @throws IllegalArgumentException if the preconditions aren't met.
  * @since 0.10.0
  * TODO (Aaron Maus): change this to take row vectors. Row vectors will make
  * constructor calls more intuitive.
  */
  public Matrix(Vector... colVectors){
    this.numCols = colVectors.length;
    if(this.numCols == 0){
      throw new IllegalArgumentException("Matrix must have atleast one column.");
    }
    this.numRows = colVectors[0].getNumDimensions();
    for(Vector v : colVectors){
      if(v.getNumDimensions() != this.numRows){
        throw new IllegalArgumentException("All Vectors must have the same number of rows.");
      }
    }
    this.matrix = new BigDecimal[this.numRows][this.numCols];
    for(int row = 0; row < this.numRows; row++){
      for(int col = 0; col < this.numCols; col++){
        this.matrix[row][col] = colVectors[col].getValue(row);
      }
    }
  }

  /**
  * @param numRows the number of rows
  * @param numCols the number of rows
  * @param fillValue the value to fill the matrix with
  */
  public Matrix(int numRows, int numCols, BigDecimal fillValue){
    if(numRows == 0 || numCols == 0){
      throw new IllegalArgumentException("The matrix must have rows and cols.");
    }

    this.numRows = numRows;
    this.numCols = numCols;
    this.matrix = new BigDecimal[numRows][numCols];
    for(int i = 0; i < numRows; i++){
      for(int j = 0; j < numCols; j++){
        this.matrix[i][j] = fillValue;
      }
    }
  }

  /**
  * Construct a 4 by 4 identity matrix
  *
  * @since 0.1.0
  */
  public Matrix(){
    this.numRows = 4;
    this.numCols = 4;
    this.matrix = new BigDecimal[numRows][numCols];
    for(int i = 0; i < numRows; i++){
      for(int j = 0; j < numCols; j++){
        if(i == j){
          this.matrix[i][j] = new BigDecimal("1.0", MathContext.DECIMAL128);
        } else {
          this.matrix[i][j] = new BigDecimal("0.0", MathContext.DECIMAL128);
        }
      }
    }
  }

  private void validateMatrixDimensions(Number[][] matrix){
    if(matrix == null || matrix.length == 0){
      throw new IllegalArgumentException("Matrix::Matrix() matrix must not be null or empty");
    }
    int numRows = matrix.length;
    int numCols = matrix[0].length;
    for(int i = 0; i < numRows; i++){
      if(matrix[i].length != numCols){
        throw new IllegalArgumentException("Matrix::Matrix() matrix must be rectangular.\n"
        + "matrix[0].length: " + numCols + "\n"
        + "matrix["+i+"].length: " + matrix[i].length);
      }
    }
  }

  /**
  * Returns the transpose of this Matrix.
  * @return a new Matrix holding the transpose of this matrix.
  * @since 0.1.0
  */
  public Matrix transpose(){
    BigDecimal[][] newMat = new BigDecimal[getNumCols()][getNumRows()];
    for(int i = 0; i < getNumRows(); i++){
      for(int j = 0; j < getNumCols(); j++){
        newMat[j][i] = this.getElement(i,j);
      }
    }
    return new Matrix(newMat);
  }

  /**
  * @return The number of rows of this matrix.
  * @since 0.1.0
  */
  public int getNumRows(){
    return this.numRows;
  }

  /**
  * @return The number of cols of this matrix.
  * @since 0.1.0
  */
  public int getNumCols(){
    return this.numCols;
  }

  /**
  * Performs matrix multiplication. The multiplication is THIS*OTHER.
  * @param other the other Matrix by which to multiply this one. The multiplication is
  *    this * other.
  * @return a Matrix of dimensions this.getNumRows() by other.getNumCols() containing the results
  *    of the multiplication.
  * @throws IllegalArgumentException thrown if the Matrices dimensions are incompatible for matrix
  *    multiplication. this.getNumCols() must be equal to other.getNumRows().
  * @since 0.1.0
  */
  public Matrix multiply(Matrix other) throws IllegalArgumentException{
    if(getNumCols() != other.getNumRows()){
      throw new IllegalArgumentException("Matrix::multiply() this.getNumCols()"
      + " must equals other.getNumRows()");
    }

    BigDecimal[][] newMat = new BigDecimal[getNumRows()][other.getNumCols()];
    for(int i = 0; i < getNumRows(); i++){
      for(int j = 0; j < other.getNumCols(); j++){
        BigDecimal dotProduct = new BigDecimal("0.0", MathContext.DECIMAL128);
        for(int k = 0; k < getNumCols(); k++){
          dotProduct = dotProduct.add(
              (this.getElement(i,k).multiply(other.getElement(k,j))) );
        }
        newMat[i][j] = dotProduct;
        // It seems more elegant to get the row and col vector and
        // perform the dot product, but efficiency is key here.
        // Avoid instantiating new objects and creating copies of
        // data already in memory. Hence the logic above as opposed
        // to below.
        //Vector aVec = getRowVector(this, i);
        //Vector bVec = getColVector(other, j);
        //newMat[i][j] = aVec.dotProduct(bVec);
      }
    }
    return new Matrix(newMat);
  }

  /**
  * Perform scalar matrix multiplication.
  *
  * @param scalar the scalar to multiply this Matrix by.
  * @return  a new Matrix of the same dimensions with the multiplied values.
  * @since 0.1.0
  */
  public Matrix multiply(BigDecimal scalar){
    BigDecimal[][] newMat = new BigDecimal[getNumRows()][getNumCols()];
    for(int rowIndex = 0; rowIndex < getNumRows(); rowIndex++){
      for(int colIndex = 0; colIndex < getNumCols(); colIndex++){
        newMat[rowIndex][colIndex] = this.getElement(rowIndex,colIndex)
            .multiply(scalar);
      }
    }
    return new Matrix(newMat);
  }

  /**
  * Perform matrix addition.
  *
  * @param other the Matrix to add to this one. It must be of the same dimensions as this Matrix.
  * @return  a new Matrix of the same dimensions holding the sum of the two matrices.
  * @throws IllegalArgumentException thrown if the dimensions of the two matrices are not the same.
  * @since 0.1.0
  */
  public Matrix add(Matrix other) throws IllegalArgumentException{
    if(this.getNumRows() != other.getNumRows() || this.getNumCols() != other.getNumCols()){
      throw new IllegalArgumentException("Matrix::add(Matrix other), "
      + "other must have same dimensions as this");
    }
    BigDecimal[][] newMat = new BigDecimal[getNumRows()][getNumCols()];
    for(int rowIndex = 0; rowIndex < getNumRows(); rowIndex++){
      for(int colIndex = 0; colIndex < getNumCols(); colIndex++){
        newMat[rowIndex][colIndex] = this.getElement(rowIndex,colIndex)
            .add(other.getElement(rowIndex,colIndex));
      }
    }
    return new Matrix(newMat);
  }

  /**
  * Performs matrix subtraction.
  *
  * @param other the Matrix to subtract from this one. It must be of the same dimensions
  *    as this Matrix.
  * @return  a new Matrix of the same dimensions holding the difference of the two matrices.
  * @throws IllegalArgumentException thrown if the dimensions of the two matrices are not the same.
  * @since 0.5.0
  */
  public Matrix subtract(Matrix other) throws IllegalArgumentException{
    Matrix negativeOther = other.multiply(new BigDecimal("-1.0", MathContext.DECIMAL128));
    return add(negativeOther);
  }

  /**
  * Return the element from the matrix specified by row and col.
  *
  * @param row the row index. must be less than getNumRows().
  * @param col the col index. Must be less than getNumCols().
  * @return the element at the given indices.
  * TODO(Aaron Maus): write a MatrixInvalidIndicesException and throw it if necessary
  * @since 0.1.0
  */
  public BigDecimal getElement(int row, int col){
    return this.matrix[row][col];
  }

  /**
  * Return a 2-Dimensional array representing the matrix.
  *
  * The first dimension is rows and the second is columns.
  * Modifying this array will not change the Matrix.
  * @return the 2-D double array that represents this matrix
  * @since 0.1.0
  */
  public BigDecimal[][] toArray(){
    BigDecimal[][] ret = new BigDecimal[getNumRows()][getNumCols()];
    for(int i = 0; i < getNumRows(); i++){
      for(int j = 0; j < getNumCols(); j++){
        ret[i][j] = getElement(i,j);
      }
    }
    return ret;
  }

  /**
  * Build and returns a Vector holding the values of one of the rows of the matrix.
  *
  * @param rowIndex the index of the row to be retrieved. @require rowIndex &#62; getNumRows()
  * @return a Vector object holding the values of the row.
  * TODO(Aaron Maus): write a MatrixInvalidIndicesException and throw it if necessary
  * @since 0.1.0
  */
  public Vector getRowVector(int rowIndex){
    BigDecimal[] row = new BigDecimal[getNumCols()];
    for(int colIndex = 0; colIndex < getNumCols(); colIndex++){
      row[colIndex] = getElement(rowIndex,colIndex);
    }
    return new Vector(row);
  }

  /**
  * Return Vector holding the values of one of the columns of the matrix.
  *
  * @param colIndex the index of the column to be retrieved. @require colIndex &#62; getNumcols()
  * @return a Vector object holding the values of the column.
  * TODO(Aaron Maus): write a MatrixInvalidIndicesException and throw it if necessary
  * @since 0.1.0
  */
  public Vector getColVector(int colIndex){
    BigDecimal[] col = new BigDecimal[getNumRows()];
    for(int rowIndex = 0; rowIndex < getNumRows(); rowIndex++){
      col[rowIndex] = getElement(rowIndex,colIndex);
    }
    return new Vector(col);
  }

  /**
  * Calculate and return the hashCode for this matrix.
  *
  * @return the hashCode calculated as {@link java.util.List#hashCode()} does
  */
  public int hashCode(){
    int hashCode = 1;
    for(int i = 0; i < getNumRows(); i++){
      for(int j = 0; j < getNumCols(); j++){
        hashCode = 31*hashCode + (getElement(i,j)==null ? 0 : getElement(i,j).hashCode());
      }
    }
    return hashCode;
  }

  /**
  * Perform equality check by checking if all the values contained
  * in the two matrices are equal.
  *
  * Two values are equal if their values are the same out to 15
  * decimal places.
  *
  * {@inheritDoc}
  */
  @Override
  public boolean equals(Object obj){
    if(obj instanceof Matrix){
      Matrix other = (Matrix)obj;
      if(this.getNumRows() != other.getNumRows()){
        return false;
      }
      if(this.getNumCols() != other.getNumCols()){
        return false;
      }
      for(int i = 0; i < getNumRows(); i++){
        for(int j = 0; j < getNumCols(); j++){
          try{
            BigDecimal num1 = this.getElement(i,j).setScale(15,BigDecimal.ROUND_HALF_EVEN);
            BigDecimal num2 = other.getElement(i,j).setScale(15,BigDecimal.ROUND_HALF_EVEN);
            if(! (num1.equals(num2))){
              //System.out.printf("%s\n",
              //    new DecimalFormat("0.0000000000000000000000000000000000000000").format(num1));
              //System.out.printf("%s\n",
              //    new DecimalFormat("0.0000000000000000000000000000000000000000").format(num2));
              return false;
            }
          } catch(ArithmeticException e){
            e.printStackTrace();
            System.exit(1);
          }
        }
      }
      return true;
    }
    return false;
  }

  /**
  * An overridden implementation of Object.toString().
  * @return A String representation of this matrix.
  * @since 0.1.0
  */
  @Override
  public String toString(){
    String str = "\n";
    //str = "numRows: " + getNumRows() + "\n";
    //str += "numCols: " + getNumCols() + "\n";
    for(int i = 0; i < getNumRows(); i++){
      str += "|";
      for(int j = 0; j < getNumCols(); j++){
        //str += String.format("%6.2f",getElement(i,j).doubleValue());
        str += String.format("%6s", new DecimalFormat("0.00").format(getElement(i,j)));
      }
      if(i == getNumRows()-1){
        str += "  |";
      } else {
        str += "  |\n";
        int numSpaces = (getNumCols() * 6) + 2;// + (getNumCols()+1)*2;
        str += "|";
        for(int counter = 0; counter < numSpaces; counter++){
          str += " ";
        }
        str += "|\n";
      }
    }
    str += "\n";
    return str;
  }

}// end of class Matrix
