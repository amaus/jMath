package com.aaronpmaus.jMath;
import java.lang.IllegalArgumentException;

/**
 * This class represents a general set of cartesian coordinates in any number of dimensions.
 * @author  Aaron Maus aaron@aaronpmaus.com
 * @version 0.1.0
 * @since 0.1.0
 */
public class CartesianCoordinates{
    private double[] coords;
    private final int dimensionality;

    /**
     * @param coords  The set of values that define this point. Can create a point of any dimension.
     * @since 0.1.0
    */
    public CartesianCoordinates(double... coords){
        this.coords = coords;
        this.dimensionality = coords.length;
    }

    /**
     * The default constructor creates a point of 3 dimensions at (0,0,0).
     * @since 0.1.0
    */
    public CartesianCoordinates(){
        this(0.0, 0.0, 0.0);
    }

    /**
     * Returns an array holding the values of the coordinates.
     * @return An array holding the coordinates of this point. This array is
     * a deep copy of the coordinates. Any changes made to this copy do not
     * affect the original coordinates.
     * @since 0.1.0
    */
    public double[] getCoords(){
        double[] coordinates = new double[dimensionality()];
        for(int i = 0; i < dimensionality(); i++){
            coordinates[i] = getCoordinate(i);
        }
        return coordinates;
    }

    /**
     * Returns the number of dimensions of this point.
     * @return The number of dimensions of this point.
     * @since 0.1.0
    */
    public int dimensionality(){
        return this.dimensionality;
    }

    /**
     * By common convention, returns the 0th coordinate of this point. Useful if the point is 3 dimensional.
     * @return The 0th coordinate of this point.
     * @since 0.1.0
    */
    public double getX(){
        return getCoordinate(0);
    }

    /**
     * By common convention, returns the 1th coordinate of this point. Useful if the point is 3 dimensional.
     * @return The 1th coordinate of this point.
     * @since 0.1.0
    */
    public double getY(){
        return getCoordinate(1);
    }

    /**
     * By common convention, returns the 2th coordinate of this point. Useful if the point is 3 dimensional.
     * @return The 2th coordinate of this point.
     * @since 0.1.0
    */
    public double getZ(){
        return getCoordinate(2);
    }

    /**
     * Returns the value of the coordinate at the given dimension.
     * @param dimension The dimension of the coorinate to be returned. The first coordinate is at the 0th dimension.
     * @return The value of the coordinate at the given dimension.
     * @throws IllegalArgumentException Thrown if dimension is {@code >= dimensionality()}.
     * @since 0.1.0
    */
    public double getCoordinate(int dimension){
        if(dimension < dimensionality()){
            return this.coords[dimension];
        } else {
            throw new IllegalArgumentException("dimension must be less than the dimensionality of this point." +
                                "The first coordinate is at the 0th dimension." +
                                "Dimensionality: " + dimensionality() + ", given dimension: " +dimension);
        }
    }

    /**
     * @param coords     The coordinates for this point to be moved to.
     * @throws IllegalArgumentException     Thrown if coords.length is != dimensionality().
     * @since 0.1.0
    */
    public void moveTo(double... coords){
        if(coords.length == dimensionality()){
            this.coords = coords;
        } else {
            String exceptionString = "CartesianCoordinates::moveTo - " + buildIllegalArgumentExceptionString(coords);
            throw new IllegalArgumentException(exceptionString);
        }
    }

    /**
     * @param vector    The vector by which to move our point.
     * @throws IllegalArgumentException     Thrown if vector.length is != dimensionality().
     * @since 0.1.0
    */
    public void moveBy(double... vector){
        if(vector.length == dimensionality()){
            for(int i = 0; i < vector.length; i++){
                this.coords[i] += vector[i];
            }
        } else {
            String exceptionString = "CartesianCoordinates::moveBy() - " + buildIllegalArgumentExceptionString(vector);
            throw new IllegalArgumentException(exceptionString);
        }
    }

    /**
     * This is a helper method to be used when coordinates passed in to another
     * method do not have the same dimension at this point.
     * @param coords The coords that were passed into the other method.
     * @return A String stating that the dimensionality of the passed in
     *         coordinates do not match dimensionality().
     * @since 0.1.0
    */
    protected String buildIllegalArgumentExceptionString(double[] coords){
            String exceptionString = "Must pass correct number of coordinates. "
                                        + "Dimensionality: " + dimensionality() + ", requires " + dimensionality()
                                        + " arguments. Given " + coords.length + " arguments: ";
            exceptionString += buildCoordsString(coords);
            return exceptionString;
    }

    @Override
    /**
     * An overridden implementation of Object.toString().
     * @return  A String representation of the coordinates of this point.
     * @since 0.1.0
    */
    public String toString(){
        return buildCoordsString(getCoords());
    }

    /**
     * @param coords    The coordinates to build a String out of.
     * @return          The String representation of the coords.
     * @since 0.1.0
    */
    public static String buildCoordsString(double[] coords){
        String str = "(";
        for(int i = 0; i < coords.length - 1; i++){
            str += String.format("%.2f, ", coords[i]);
        }
        str += String.format("%.2f)", coords[coords.length-1]);
        return str;
    }

    /**
     * Calculates the Euclidean Distance between this CartesianCoordinates and the CartesianCoordinates passed in.
     * @param otherCartesianCoordinates    The other CartesianCoordinates to calculate the distance too.
     * @return              The Euclidean Distance between this CartesianCoordinates and otherCartesianCoordinates.
     * @throws IllegalArgumentException Thrown if otherCartesianCoordinates.dimensionality() != dimensionality
     * @since 0.1.0
    */
    public double distance(CartesianCoordinates otherCartesianCoordinates){
        double distance = 0.0;
        if(otherCartesianCoordinates.dimensionality() == dimensionality()){
            for(int i = 0; i < dimensionality(); i++){
                distance += Math.pow(getCoordinate(i) - otherCartesianCoordinates.getCoordinate(i), 2);
            }
            distance = Math.sqrt(distance);
        } else {
            String exceptionString = "CartesianCoordinates::distance() - CartesianCoordinates must have same dimensionality."
                + " this.dimensionality(): " + dimensionality()
                + " otherCartesianCoordinates.dimensionality() " + otherCartesianCoordinates.dimensionality();
            throw new IllegalArgumentException(exceptionString);
        }
        return distance;
    }
}// end of class CartesianCoordinates
