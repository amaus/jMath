package com.aaronpmaus.jMath.linearAlgebra;
import java.lang.IllegalArgumentException;

/**
 * This class represents a general set of cartesian coordinates in any number
 * of dimensions.
 * @author  Aaron Maus aaron@aaronpmaus.com
 * @version 0.1.0
 * @since 0.1.0
 */
public class CartesianCoordinates extends Vector{

    /**
     * @param coords The set of values that define this point. Can create a
     *               point of any dimension.
     * @since 0.1.0
    */
    public CartesianCoordinates(double... coords){
        super(coords);
    }

    /**
     * The default constructor creates a point of 3 dimensions at (0,0,0).
     * @since 0.1.0
    */
    public CartesianCoordinates(){
        super(0.0, 0.0, 0.0);
    }

    /**
     * By common convention, returns the 0th coordinate of this point. Useful
     * if the point is 3 dimensional.
     * @return The 0th coordinate of this point.
     * @since 0.1.0
    */
    public double getX(){
        return getValue(0);
    }

    /**
     * By common convention, returns the 1th coordinate of this point. Useful
     * if the point is 3 dimensional.
     * @return The 1th coordinate of this point.
     * @since 0.1.0
    */
    public double getY(){
        return getValue(1);
    }

    /**
     * By common convention, returns the 2th coordinate of this point. Useful
     * if the point is 3 dimensional.
     * @return The 2th coordinate of this point.
     * @since 0.1.0
    */
    public double getZ(){
        return getValue(2);
    }

}// end of class CartesianCoordinates
