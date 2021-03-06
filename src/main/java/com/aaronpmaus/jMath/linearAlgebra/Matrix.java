package com.aaronpmaus.jMath.linearAlgebra;

import java.lang.IllegalArgumentException;
import java.math.BigDecimal;
import java.math.MathContext;

import java.text.DecimalFormat;

/**
* A 2-dimensional matrix. From the public perspective (1), Matrices are immutable. All operations
* return a new Matrix rather than modify itself. Matrices support addition, subtraction, and
* multiplication by scalars and other matrices.
* <p>
* (1) Only Matrix and subclasses can directly modify the values. This allows Points and Vectors to
* be transformed in 3D space directly.
* @see com.aaronpmaus.jMath.transformations.Transformation
* @see com.aaronpmaus.jMath.transformations.Transformable
* @author Aaron Maus aaron@aaronpmaus.com
* @version 0.12.0
* @since 0.1.0
*/
public class Matrix{
  private Double[][] matrix;
  private int numRows;
  private int numCols;

  /**
  * Construct a 2-dimensional Matrix holding the values in the 2D Array passed in.
  *
  * @param matrix A 2-dimensional array holding the values for the matrix. It must not
  * be null.
  * @throws IllegalArgumentException if the matrix is null or not rectangular
  * @since 0.1.0
  */
  public Matrix(Double[][] matrix){
    initializeMatrix(matrix);
  }

  /**
  * Construct a column vector from the values, a matrix with values.length num rows and 1 column.
  * @param values the values to insert
  */
  protected Matrix(Double... values){
    Double[][] matrix = new Double[values.length][1];
    for(int i = 0; i < values.length; i++){
      matrix[i][0] = values[i];
    }

    initializeMatrix(matrix);
  }

  /**
  * Copy Constructor
  * @param matrix a matrix to create a new copy of
  * @since 0.12.0
  */
  public Matrix(Matrix matrix){
    this(matrix.toArray());
  }

  private void initializeMatrix(Double[][] matrix){
    validateMatrixDimensions(matrix);
    this.numRows = matrix.length;
    this.numCols = matrix[0].length;

    this.matrix = new Double[numRows][numCols];
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
  * <p>
  * There must be atleast one column vector and all column vectors must have the same number of
  * rows.
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
    this.matrix = new Double[this.numRows][this.numCols];
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
  public Matrix(int numRows, int numCols, double fillValue){
    if(numRows == 0 || numCols == 0){
      throw new IllegalArgumentException("The matrix must have rows and cols.");
    }

    this.numRows = numRows;
    this.numCols = numCols;
    this.matrix = new Double[numRows][numCols];
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
    this(4);
  }

  /**
  * Construct an m by m identity matrix.
  * @param m the square dimension of the Matrix
  * @since 0.12.0
  */
  public Matrix(int m){
    this.numRows = m;
    this.numCols = m;
    this.matrix = new Double[numRows][numCols];
    for(int i = 0; i < numRows; i++){
      for(int j = 0; j < numCols; j++){
        if(i == j){
          this.matrix[i][j] = 1.0;
        } else {
          this.matrix[i][j] = 0.0;
        }
      }
    }
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
  public Double getElement(int row, int col){
    return this.matrix[row][col];
  }

  /**
  * @param row the row index, must be in range [0,numRows()-1]
  * @param col the col index, must be in range [0,numRows()-1]
  * @param value the value to set at matrix[row][col]
  */
  protected void setElement(int row, int col, double value){
    this.matrix[row][col] = value;
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
  * @return the transpose of this matrix.
  * @since 0.11.1
  */
  public Matrix transpose(){
    Double[][] newMat = new Double[getNumCols()][getNumRows()];
    for(int i = 0; i < getNumRows(); i++){
      for(int j = 0; j < getNumCols(); j++){
        newMat[j][i] = this.getElement(i,j);
      }
    }
    //this.matrix = newMat;
    //this.numRows = this.matrix.length;
    //this.numCols = this.matrix[0].length;
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

    Double[][] newMat = new Double[getNumRows()][other.getNumCols()];
    for(int i = 0; i < getNumRows(); i++){
      for(int j = 0; j < other.getNumCols(); j++){
        double dotProduct = 0.0;
        for(int k = 0; k < getNumCols(); k++){
          dotProduct += this.getElement(i,k) * other.getElement(k,j);
        }
        newMat[i][j] = dotProduct;

        // It seems more elegant to get the row and col vector and perform the dot product, but
        // efficiency is key here. Avoid instantiating new objects and creating copies of data
        // already in memory. So we'll go through the rigamarole above instead of the simplicity
        // below.
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
  public Matrix multiply(double scalar){
    Double[][] newMat = new Double[getNumRows()][getNumCols()];
    for(int rowIndex = 0; rowIndex < getNumRows(); rowIndex++){
      for(int colIndex = 0; colIndex < getNumCols(); colIndex++){
        newMat[rowIndex][colIndex] = this.getElement(rowIndex,colIndex) * scalar;
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
    Double[][] newMat = new Double[getNumRows()][getNumCols()];
    for(int rowIndex = 0; rowIndex < getNumRows(); rowIndex++){
      for(int colIndex = 0; colIndex < getNumCols(); colIndex++){
        newMat[rowIndex][colIndex] = this.getElement(rowIndex,colIndex)
            + other.getElement(rowIndex,colIndex);
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
    Matrix negativeOther = other.multiply(-1.0);
    return add(negativeOther);
  }

  /**
  * Return a 2-Dimensional array representing the matrix.
  * <p>
  * The first dimension is rows and the second is columns. Modifying this array will not change the
  * Matrix.
  * @return the 2-D double array that represents this matrix
  * @since 0.1.0
  */
  public Double[][] toArray(){
    Double[][] ret = new Double[getNumRows()][getNumCols()];
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
    return new Vector(getRowValues(rowIndex));
  }

  /**
  * Return the values of a row of this Matrix
  * @param rowIndex the rowIndex, starting from 0
  * @return an array of Doubles, the values of this row
  */
  public Double[] getRowValues(int rowIndex){
    Double[] row = new Double[getNumCols()];
    for(int colIndex = 0; colIndex < getNumCols(); colIndex++){
      row[colIndex] = getElement(rowIndex,colIndex);
    }
    return row;
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
    return new Vector(getColValues(colIndex));
  }

  /**
  * Return the values of a column of this Matrix
  * @param colIndex the colIndex, starting from 0
  * @return an array of Doubles, the values of this column
  */
  public Double[] getColValues(int colIndex){
    Double[] col = new Double[getNumRows()];
    for(int rowIndex = 0; rowIndex < getNumRows(); rowIndex++){
      col[rowIndex] = getElement(rowIndex,colIndex);
    }
    return col;
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
  * <p>
  * Two values are equal if their values are the same out to 15
  * decimal places.
  * <p>
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
            BigDecimal num1 = getBigDecimal(this.getElement(i,j)); //new BigDecimal(this.getElement(i,j), MathContext.DECIMAL128).setScale(9,BigDecimal.ROUND_HALF_EVEN);
            BigDecimal num2 = getBigDecimal(other.getElement(i,j)); //, MathContext.DECIMAL128).setScale(9,BigDecimal.ROUND_HALF_EVEN);
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

  private BigDecimal getBigDecimal(Double value){
    String valueStr = String.format("%f", value);
    String[] parts = valueStr.split("\\.");
    // the scale is the number of digits to preserve to the right of the decimal.
    // Double are precise to 15 digits, including to the left and right of the decimal.
    // PI*10^6:    3  1  4  1  5  9  2 . 6  5  3  5  8  9  7  9  3
    //             |  |  |  |  |  |  |   |  |  |  |  |  |  |  |  |
    // digit       1  2  3  4  5  6  7   8  9  10 11 12 13 14 15 16
    // only digits 1-15 are reliable. the 16th digit (3) may be inaccurate.
    // use the 15th digit for rounding.
    // create a BigDecimal with a scale of 8 with rounding mode ROUNDING_HALF_UP
    // 7 = 14 - 7
    int scale = 14 - parts[0].length();
    return new BigDecimal(value, MathContext.DECIMAL128).setScale(scale, BigDecimal.ROUND_HALF_EVEN);
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
