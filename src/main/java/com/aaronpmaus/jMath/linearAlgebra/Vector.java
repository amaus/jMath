package com.aaronpmaus.jMath.linearAlgebra;
import java.lang.IllegalArgumentException;


/**
 * This class represents a general vector of any dimensions.
 * @author  Aaron Maus aaron@aaronpmaus.com
 * @version 0.9.0
 * @since 0.1.0
 */
public class Vector{
    private double[] values;
    private final int numDimensions;
    /**
     * Creates a Vector containing the values passed in as parameters.
     * @param vals the values to be added to this vector
     * @since 0.1.0
    */
    public Vector(double... vals){
        this.values = vals;
        this.numDimensions = vals.length;
    }

    /**
     * The default constructor creates the a 3 dimensional vector at the origin.
     * @since 0.1.0
    */
    public Vector(){
        this(0.0,0.0,0.0);
    }

    /**
     * Returns an array holding the values of the Vector.
     * @return An array holding the values of this Vector. This array is
     * a deep copy of the values. Any changes made to this copy do not
     * affect the original Vector.
     * @since 0.9.0
    */
    public double[] getValues(){
        double[] coordinates = new double[getNumDimensions()];
        for(int i = 0; i < getNumDimensions(); i++){
            coordinates[i] = getValue(i);
        }
        return coordinates;
    }

    /**
     * Returns the number of dimensions of this point.
     * @return The number of dimensions of this point.
     * @since 0.9.0
    */
    public int getNumDimensions(){
        return this.numDimensions;
    }

    /**
     * Returns the dot product of this vector and the parameter vector.
     * @param other the Vector by which to perform the dot product with.
     * @return the value of the dot product
     * @throws IllegalArgumentException if the Vector passed in does not have the
     *         same Num Dimensions at this vector.
     * @since 0.1.0
    */
    public double dotProduct(Vector other){
        if(getNumDimensions() != other.getNumDimensions()){
            throw new IllegalArgumentException("Vector::dotProduct() " + buildIllegalArgumentExceptionString(other.getValues()));
        }

        double product = 0;
        for(int i = 0; i < getNumDimensions(); i++){
            product += getValue(i) * other.getValue(i);
        }
        return product;
    }

    /**
     * Calcuates the angle (in degrees) between this vector and the other.
     * @param other the other vector to calculate the angle between
     * @return the angle in degrees
     * @throws IllegalArgumentException thrown if the dimensions of the two vectors
     *          are not the same.
     * @since 0.1.0
    */
    public double angle(Vector other){
        if(getNumDimensions() != other.getNumDimensions()){
            throw new IllegalArgumentException("Vector::angle() " + buildIllegalArgumentExceptionString(other.getValues()));
        }
        double angle = this.dotProduct(other) / (this.magnitude() * other.magnitude());
        angle = Math.acos(angle);
        return Math.toDegrees(angle);
    }

    /**
     * Returns the magnitude (aka length) of this Vector.
     * @return the magnitude
     * @since 0.1.0
    */
    public double magnitude(){
        return Math.sqrt(this.dotProduct(this));
    }

    /**
     * Returns the value of the coordinate at the given dimension.
     * @param dimension The dimension of the coorinate to be returned. The first
     *                  coordinate is at the 0th dimension.
     * @return The value of the coordinate at the given dimension.
     * @throws IllegalArgumentException Thrown if dimension is {@code >= getNumDimensions()}.
     * @since 0.9.0
    */
    public double getValue(int dimension){
        if(dimension < getNumDimensions()){
            return this.values[dimension];
        } else {
            throw new IllegalArgumentException("dimension must be less than the Num Dimensions of this point." +
                                "The first coordinate is at the 0th dimension." +
                                "Num Dimensions: " + getNumDimensions() + ", given dimension: " +dimension);
        }
    }

    /**
     * @param values The coordinates for this point to be moved to.
     * @throws IllegalArgumentException Thrown if values.length is != getNumDimensions().
     * @since 0.9.0
    */
    public void moveTo(double... values){
        if(values.length == getNumDimensions()){
            this.values = values;
        } else {
            String exceptionString = "Vector::moveTo - " + buildIllegalArgumentExceptionString(values);
            throw new IllegalArgumentException(exceptionString);
        }
    }

    /**
     * @param vector The vector by which to move our point.
     * @throws IllegalArgumentException Thrown if vector.length is != getNumDimensions().
     * @since 0.9.0
    */
    public void moveBy(double... vector){
        if(vector.length == getNumDimensions()){
            for(int i = 0; i < vector.length; i++){
                this.values[i] += vector[i];
            }
        } else {
            String exceptionString = "Vector::moveBy() - " + buildIllegalArgumentExceptionString(vector);
            throw new IllegalArgumentException(exceptionString);
        }
    }

    /**
     * This is a helper method to be used when coordinates passed in to another
     * method do not have the same dimension at this point.
     * @param values The values that were passed into the other method.
     * @return A String stating that the Num Dimensions of the Vector passed in
     *         do not match this.getNumDimensions().
     * @since 0.9.0
    */
    protected String buildIllegalArgumentExceptionString(double[] values){
            String exceptionString = "Must pass correct number of coordinates. "
                                        + "Num Dimensions: " + getNumDimensions() + ", requires " + getNumDimensions()
                                        + " arguments. Given " + values.length + " arguments: ";
            exceptionString += buildVectorString(values);
            return exceptionString;
    }

    @Override
    /**
     * An overridden implementation of Object.toString().
     * @return  A String representation of the coordinates of this point.
     * @since 0.1.0
    */
    public String toString(){
        return buildVectorString(getValues());
    }

    /**
     * @param values The coordinates to build a String out of.
     * @return The String representation of the values.
     * @since 0.9.0
    */
    public static String buildVectorString(double[] values){
        String str = "(";
        for(int i = 0; i < values.length - 1; i++){
            str += String.format("%.2f, ", values[i]);
        }
        str += String.format("%.2f)", values[values.length-1]);
        return str;
    }

    /**
     * Calculates the Euclidean Distance between this Vector and the Vector passed in.
     * @param otherVector The other Vector to calculate the distance too.
     * @return The Euclidean Distance between this Vector and otherVector.
     * @throws IllegalArgumentException Thrown if otherVector.getNumDimensions() != this.getNumDimensions()
     * @since 0.9.0
    */
    public double distance(Vector otherVector){
        double distance = 0.0;
        if(otherVector.getNumDimensions() == getNumDimensions()){
            for(int i = 0; i < getNumDimensions(); i++){
                distance += Math.pow(getValue(i) - otherVector.getValue(i), 2);
            }
            distance = Math.sqrt(distance);
        } else {
            String exceptionString = "Vector::distance() - Vector must have same Num Dimensions."
                + " this.getNumDimensions(): " + getNumDimensions()
                + " otherVector.getNumDimensions() " + otherVector.getNumDimensions();
            throw new IllegalArgumentException(exceptionString);
        }
        return distance;
    }

}// end of class Vector
