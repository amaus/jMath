package com.aaronpmaus.jMath.io;

import java.util.ArrayList;
import java.security.InvalidParameterException;

/**
* Provides a set of methods to parse command line arguments for
* executables. Arguments to set values are in the form {@code -flag val}
* where flag is something like {@code -d} or {@code --distance}, and val is the
* value to be used.
* @since 0.13.1
*/
public class CommandLineParser{
  private ArrayList<String> args;

  /**
  * Constructor requires the array of command line arguments
  * @param arguments the array of command line args
  * @since 0.3.0
  */
  public CommandLineParser(String[] arguments){
    this.args = new ArrayList<String>( );
    for(String arg : arguments){
      this.args.add(arg);
    }
  }

  /**
  * Checks to see if the command line arguments contains the given
  * flag.
  * @param flag the flag to check for
  * @return true if found, false otherwise.
  * @since 0.6.0
  */
  public boolean contains(String flag){
    for(String arg : args){ // for every cmd argument
      if(arg.equals(flag)){ // if it contains the flag
        return true; // return true
      }
    }
    // if none of the args contained the flag, return false
    return false;
  }

  /*
  * private helper method to get the index of the flag in the parameter list
  */
  private int getIndex(String flag){
    boolean found = false;
    int index = -1;
    for(int i = 0; i < args.size(); i++){
      String[] pair = args.get(i).split(":");
      if(pair[0].equals(flag)){
        found = true;
        index = i;
      }
    }
    if(!found){
      throw new InvalidParameterException("Flag " + flag +
      "not found in command line arguments.");
    }
    return index;
  }

  /**
  * Return the value for the given flag.
  * @param flag the flag to get the value of
  * @return the trimmed value for that flag, that is, excluding all white space before
  *   and after the value.
  * @throws InvalidParameterException if the flag is not in the cmd line args
  * @since 0.3.0
  */
  public String getValue(String flag) throws InvalidParameterException {
    return args.get( getIndex(flag)+1 ).trim();
  }
}
