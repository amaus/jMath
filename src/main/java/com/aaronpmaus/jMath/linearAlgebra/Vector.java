package com.aaronpmaus.jMath.linearAlgebra;
import com.aaronpmaus.jMath.CartesianCoordinates;
import java.lang.IllegalArgumentException;


/**
 * This class represents a general vector of any dimensions.
 * @author  Aaron Maus aaron@aaronpmaus.com
 * @version 0.1.0
 * @since 0.1.0
 */
public class Vector extends CartesianCoordinates{

    /**
     * Creates a Vector containing the values passed in as parameters.
     * @param vals the values to be added to this vector
     * @since 0.1.0
    */
    public Vector(double... vals){
        super(vals);
    }

    /**
     * The default constructor creates the a 3 dimensional vector at the origin.
     * @since 0.1.0
    */
    public Vector(){
        super();
    }

    /**
     * Returns the dot product of this vector and the parameter vector.
     * @param other the Vector by which to perform the dot product with.
     * @return  the value of the dot product
     * @throws IllegalArgumentException if the Vector passed in does not have the
     *          same dimensionality at this vector.
     * @since 0.1.0
    */
    public double dotProduct(Vector other){
        if(dimensionality() != other.dimensionality()){
            throw new IllegalArgumentException("Vector::dotProduct() " + buildIllegalArgumentExceptionString(other.getCoords()));
        }

        double product = 0;
        for(int i = 0; i < dimensionality(); i++){
            product += getCoordinate(i) * other.getCoordinate(i);
        }
        return product;
    }

    /**
     * Calcuates the angle (in degrees) between this vector and the other.
     * @param other the other vector to calculate the angle between
     * @return  the angle in degrees
     * @throws IllegalArgumentException thrown if the dimensions of the two vectors
     *          are not the same.
     * @since 0.1.0
    */
    public double angle(Vector other){
        if(dimensionality() != other.dimensionality()){
            throw new IllegalArgumentException("Vector::angle() " + buildIllegalArgumentExceptionString(other.getCoords()));
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

    @Override
    /**
     * An overridden implementation of Object.toString().
     * @return  A String representation of the coordinates of this vector.
     * @since 0.1.0
    */
    public String toString(){
        return super.toString();
    }

}// end of class Vector
