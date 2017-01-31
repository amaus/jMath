package com.aaronpmaus.jMath.linearAlgebra;
import java.lang.IllegalArgumentException;

/**
 * This class represents a 2-dimensional matrix.
 * @author  Aaron Maus aaron@aaronpmaus.com
 * @version 0.1.4
 * @since 0.1.0
 */
public class Matrix{
    private double[][] matrix;
    private final int numRows;
    private final int numCols;

    /**
     * @param matrix A 2-dimensional array holding the values for the matrix. It must not
     * be null. It must be rectangular, that is every row has the same number of columns.
    */
    public Matrix(double[][] matrix){
        this.matrix = matrix;
        if(this.matrix == null){
            throw new IllegalArgumentException("Matrix::Matrix() matrix must not be null");
        }
        boolean sameNumColsPerRow = true;
        numRows = matrix.length;
        numCols = matrix[0].length;
        for(int i = 0; i < matrix.length; i++){
            if(matrix[i].length != numCols){
                throw new IllegalArgumentException("Matrix::Matrix() matrix must be rectangular. matrix[0].length: " +
                    numCols + "matrix["+i+"].length: " + matrix[i].length +"\n");
            }
        }
    }

    /**
     * The default constructor creates a 4 by 4 identity matrix
    */
    public Matrix(){
        this.matrix = new double[4][4];
        this.matrix[0][0] = 1;
        this.matrix[1][1] = 1;
        this.matrix[2][2] = 1;
        this.matrix[3][3] = 1;
        this.numRows = 4;
        this.numCols = 4;
    }

    /**
     * @return The number of rows of this matrix.
    */
    public int getNumRows(){
        return this.numRows;
    }

    /**
     * @return The number of cols of this matrix.
    */
    public int getNumCols(){
        return this.numCols;
    }

    /**
     * Performs matrix multiplication. The multiplication is THIS*OTHER.
     * @param other the other Matrix by which to multiply this one. The multiplication is
     *              this * other.
     * @return      a Matrix of dimensions this.getNumRows() by other.getNumCols() containing the results
     *              of the multiplication.
     * @throws IllegalArgumentException thrown if the Matrices dimensions are incompatible for matrix
     *              multiplication. this.getNumCols() must be equal to other.getNumRows().
    */
    public Matrix multiply(Matrix other) throws IllegalArgumentException{
        if(getNumCols() != other.getNumRows()){
            throw new IllegalArgumentException("Matrix::multiply() this.getNumCols() must equal other.getNumRows()");
        }

        double[][] newMat = new double[getNumRows()][other.getNumCols()];
        for(int i = 0; i < getNumRows(); i++){
            for(int j = 0; j < other.getNumCols(); j++){
                double dotProduct = 0.0;
                for(int k = 0; k < getNumCols(); k++){
                    dotProduct += this.getElement(i,k) * other.getElement(k,j);
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
     * Performs scalar matrix multiplication.
     * @param scalar the scalar to multiply this Matrix by. 
     * @return      a new Matrix of the same dimensions with the multiplied values.
    */
    public Matrix multiply(double scalar){
        double[][] newMat = new double[getNumRows()][getNumCols()];
        for(int rowIndex = 0; rowIndex < getNumRows(); rowIndex++){
            for(int colIndex = 0; colIndex < getNumCols(); colIndex++){
                newMat[rowIndex][colIndex] = this.getElement(rowIndex,colIndex)*scalar;
            }
        }
        return new Matrix(newMat);
    }

    /**
     * Performs matrix addition.
     * @param other the Matrix to add to this one. It must be of the same dimensions as this Matrix.
     * @return      a new Matrix of the same dimensions holding the sum of the two matrices.
     * @throws IllegalArgumentException thrown if the dimensions of the two matrices are not the same.
    */
    public Matrix add(Matrix other) throws IllegalArgumentException{
        if(this.getNumRows() != other.getNumRows() || this.getNumCols() != other.getNumCols()){
            throw new IllegalArgumentException("Matrix::add(Matrix other) other must have same dimensions as this");
        }
        double[][] newMat = new double[getNumRows()][getNumCols()];
        for(int rowIndex = 0; rowIndex < getNumRows(); rowIndex++){
            for(int colIndex = 0; colIndex < getNumCols(); colIndex++){
                newMat[rowIndex][colIndex] = this.getElement(rowIndex,colIndex) + other.getElement(rowIndex,colIndex);
            }
        }
        return new Matrix(newMat);
    }

    /**
     * Performs matrix subtraction.
     * @param other the Matrix to subtract from this one. It must be of the same dimensions as this Matrix.
     * @return      a new Matrix of the same dimensions holding the difference of the two matrices.
     * @throws IllegalArgumentException thrown if the dimensions of the two matrices are not the same.
     * @since 0.1.4
    */
    public Matrix subtract(Matrix other) throws IllegalArgumentException{
        Matrix negativeOther = other.multiply(-1);
        return add(negativeOther);
    }
    /**
     * Returns the transpose of this Matrix.
     * @return a new Matrix holding the transpose of this matrix.
    */
    public Matrix transpose(){
        double[][] newMat = new double[getNumCols()][getNumRows()];
        for(int i = 0; i < getNumRows(); i++){
            for(int j = 0; j < getNumCols(); j++){
                newMat[j][i] = this.getElement(i,j);
            }
        }
        return new Matrix(newMat);
    }

    /**
     * Given a row and col index, returns that element from the matrix
     * @param row the row index. must be less than getNumRows().
     * @param col the col index. Must be less than getNumCols().
     * @return the element at the given indices.
     * TODO: write a MatrixInvalidIndicesException and throw it if necessary
    */
    public double getElement(int row, int col){
        return this.matrix[row][col];
    }

    /**
     * Returns a 2-Dimensional array representing the matrix. The first dimension is rows.
     * The second is columns. Modifying this array will not change the Matrix.
     * @return the 2-D double array that represents this matrix
    */
    public double[][] getArray(){
        double[][] ret = new double[getNumRows()][getNumCols()];
        for(int i = 0; i < getNumRows(); i++){
            for(int j = 0; j < getNumCols(); j++){
                ret[i][j] = getElement(i,j);
            }
        }
        return ret;
    }

    /**
     * Builds and returns a Vector holding the values of one of the rows of the matrix.
     * @param rowIndex  the index of the row to be retrieved. @require rowIndex &#62; getNumRows()
     * @return a Vector object holding the values of the row.
     * TODO: write a MatrixInvalidIndicesException and throw it if necessary
    */
    public Vector getRowVector(int rowIndex){
        double[] row = new double[getNumCols()];
        for(int colIndex = 0; colIndex < getNumCols(); colIndex++){
            row[colIndex] = getElement(rowIndex,colIndex);
        }
        return new Vector(row);
    }

    /**
     * Builds and returns a Vector holding the values of one of the columns of the matrix.
     * @param colIndex  the index of the column to be retrieved. @require colIndex &#62; getNumcols()
     * @return a Vector object holding the values of the column.
     * TODO: write a MatrixInvalidIndicesException and throw it if necessary
    */
    public Vector getColVector(int colIndex){
        double[] col = new double[getNumRows()];
        for(int rowIndex = 0; rowIndex < getNumRows(); rowIndex++){
            col[rowIndex] = getElement(rowIndex,colIndex);
        }
        return new Vector(col);
    }

    @Override
    /**
     * An overridden implementation of Object.toString().
     * @return  A String representation of this matrix.
    */
    public String toString(){
        String str = "";
        //str = "numRows: " + getNumRows() + "\n";
        //str += "numCols: " + getNumCols() + "\n";
        for(int i = 0; i < getNumRows(); i++){
            str += "|  ";
            for(int j = 0; j < getNumCols(); j++){
                str += String.format("%6.2f  ",this.matrix[i][j]);
            }
            if(i == getNumRows()-1){
                str += "|";
            } else {
                str += "|\n";
                int numSpaces = getNumCols() * 6 + (getNumCols()+1)*2;
                str += "|";
                for(int counter = 0; counter < numSpaces; counter++){
                    str += " ";
                }
                str += "|\n";
            }
        }
        return str;
    }

}// end of class Matrix
