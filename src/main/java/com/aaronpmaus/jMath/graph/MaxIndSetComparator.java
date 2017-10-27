package com.aaronpmaus.jMath.graph;
import java.util.Comparator;
import java.util.ArrayList;

/**
* MaxIndSetComparator is a Comparator that allows nodes to be ordered based on the size of their
* maximum independent sets.
* @author Aaron Maus aaron@aaronpmaus.com
* @version 0.7.0
* @since 0.7.0
*/
public class MaxIndSetComparator<T extends Comparable<? super T>> implements Comparator<Node<T>> {
  private ArrayList<UndirectedGraph<T>> indSetPartition;

  /**
  * This constructor takes in an ArrayList of the Independent Sets.
  * @param partition the list of Independent Sets in the IND Set partition of a graph
  * @since 0.7.0
  */
  public MaxIndSetComparator(ArrayList<UndirectedGraph<T>> partition){
    this.indSetPartition = partition;
  }

  /**
  * Returns -1 if the size of n1's partition is smaller than n2's
  *          0 if their sizes are the same
  *          1 otherwise
  * @param n1 the first node
  * @param n2 the second node
  * @return {@literal -1 if n1 < n2, 0 if same, 1 if n1 > n2}
  * @since 0.7.0
  */
  public int compare(Node<T> n1, Node<T> n2){
    int n1_index = getIndSetPartitionIndex(n1);
    int n2_index = getIndSetPartitionIndex(n2);
    if(n1_index > n2_index){
      return -1;
    } else if(n1_index == n2_index) {
      if(n1.numNeighbors() < n2.numNeighbors()){
        return -1;
      } else if(n1.numNeighbors() == n2.numNeighbors()){
        return 0;
      } else {
        return 1;
      }
    } else {
      return 1;
    }
  }

  private int getIndSetPartitionIndex(Node<T> n){
    for(int i = 0; i < indSetPartition.size(); i++){
      UndirectedGraph<T> g = indSetPartition.get(i);
      if(g.contains(n)){
        return i;
      }
    }
    return -1;
  }
}
