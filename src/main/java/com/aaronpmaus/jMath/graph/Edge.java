package com.aaronpmaus.jMath.graph;

/**
* The Edge of a graph. Consists of two nodes (start and end) and the weight of the edge between
* them. Represents a directed edge.
* <p>
* Edges can be sorted. The natural ordering is ascending order by the ordering of the end points.
* Ties between the start points are broken with the ending points.
* <p>
* EX: <br>
* (1,2) <br>
* (1,4) <br>
* (1,5) <br>
* (2,3) <br>
* (2,4) <br>
* (3,4) <br>
* (3,5) <br>
* (4,5) <br>
* @version 0.12.0
* @since 0.1.0
*/
public class Edge<T extends Comparable<? super T>> implements Comparable<UndirectedEdge<T>>{

  private final Node<T> start;
  private final Node<T> end;
  private double weight;

  /**
  * Constructs an edge for a graph with a default weight of 1.0
  * @param start the "from" node of the edge
  * @param end the "to" node of the edge
  * @since 0.1.0
  */
  public Edge(Node<T> start, Node<T> end){
    this(start,end,1.0);
  }

  /**
  * Constructs an edge for a graph
  * @param start the "from" node of the edge
  * @param end the "to" node of the edge
  * @param weight the weight of this edge
  * @since 0.1.0
  */
  public Edge(Node<T> start, Node<T> end, double weight){
    this.start = start;
    this.end = end;
    this.weight = weight;
  }

  /**
  * Returns the Node{@literal <T>} at the start position of this edge
  * @return the start Node{@literal <T>}
  * @since 0.1.0
  */
  public Node<T> getStart(){
    return this.start;
  }

  /**
  * Returns the Node{@literal <T>} at the end position of this edge
  * @return the end Node{@literal <T>}
  * @since 0.1.0
  */
  public Node<T> getEnd(){
    return this.end;
  }

  /**
  * Returns the weight of this edge
  * @return the weight
  * @since 0.1.0
  */
  public double getWeight(){
    return this.weight;
  }

  @Override
  public String toString(){
    return String.format("(%s, %s)", getStart().get(), getEnd().get());
  }

  /**
  * Order Edges by their endpoints. Ascending order, first by start point, then by
  * end point if tied.
  * @since 0.12.0
  */
  @Override
  public int compareTo(UndirectedEdge<T> other) {
    int comparison = this.getStart().get().compareTo(other.getStart().get());
    if(comparison == 0) {
      return this.getEnd().get().compareTo(other.getEnd().get());
    }
    return comparison;
  }

  /**
  * Returns the hashCode for this edge. The hashCode for an edge
  * is the concatenation of the hashCodes of nodes of it's end
  * points
  * @return an int representing the hashCode of this edge
  * @since 0.6.0
  */
  @Override
  public int hashCode(){
    String str = String.format("%d%d",getStart().hashCode(),getEnd().hashCode());
    return Integer.parseInt(str);
  }

  /**
  * Overridden equals() method.
  * Two edges are equals if their starting points are equals
  * and their ending points are equal.
  * @param obj the other edge to compare to
  * @return true if {@code this.getStart().equals(obj.getStart()) && this.getEnd().equals(obj.getEnd())}
  * @since 0.6.0
  */
  @Override
  @SuppressWarnings("unchecked")
  public boolean equals(Object obj){
    Edge<T> other;
    if(this.getClass().isInstance(obj)){
      other = this.getClass().cast(obj);
      if(getStart().equals(other.getStart())
      && getEnd().equals(other.getEnd())){
        return true;
      }
    }
    return false;
  }
}
