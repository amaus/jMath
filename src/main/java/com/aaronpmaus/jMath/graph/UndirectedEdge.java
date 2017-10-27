package com.aaronpmaus.jMath.graph;

/**
* An UndirectedEdge is an edge where the order of the end points does not matter.
* @since 0.6.0
*/
public class UndirectedEdge<T extends Comparable<? super T>> extends Edge<T> {

  /**
  * Constructs an UndirectedEdge for a graph with a default weight of 1.0
  * @param point1 the first endpoint of this edge
  * @param point2 the second endpoint of this edge
  * @since 0.6.0
  */
  public UndirectedEdge(Node<T> point1, Node<T> point2){
    this(point1,point2,1.0);
  }

  /**
  * Constructs an UndirectedEdge for a graph.
  * @param point1 the first endpoint of this edge
  * @param point2 the second endpoint of this edge
  * @param weight the weight of this edge
  * @since 0.6.0
  */
  public UndirectedEdge(Node<T> point1, Node<T> point2, double weight){
    super(point1,point2,weight);
  }

  /**
  * A constructor for an UndirectedEdge that takes an Edge
  * @param e the edge to use as this UndirectedEdge
  * @since 0.6.0
  */
  public UndirectedEdge(Edge<T> e){
    this(e.getStart(), e.getEnd(), e.getWeight());
  }

  /**
  * Return the hashCode for this edge. The hashCode for an edge is the multiplication of the
  * hashCodes of nodes of it's end points
  * @return an int representing the hashCode of this edge
  * @since 0.6.0
  */
  @Override
  public int hashCode(){
    return getStart().hashCode()*getEnd().hashCode();
  }

  /**
  * Overridden equals() method.
  * Two edges are equals if they have the same ending points
  * @param obj the other UndirectedEdge to compare to
  * @return a boolean representing whether the UndirectedEdges are equal
  * @since 0.6.0
  */
  @Override
  @SuppressWarnings("unchecked")
  public boolean equals(Object obj){
    if(obj instanceof UndirectedEdge){
      UndirectedEdge<T> other  = (UndirectedEdge<T>) obj;
      if(getStart().equals(other.getStart())
      && getEnd().equals(other.getEnd())){
        return true;
      }
      if(getStart().equals(other.getEnd())
      && getEnd().equals(other.getStart())){
        return true;
      }

    }
    return false;
  }
}
