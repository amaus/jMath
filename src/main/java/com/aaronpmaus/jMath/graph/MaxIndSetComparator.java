package com.aaronpmaus.jMath.graph;
import java.util.Comparator;
import java.util.ArrayList;

public class MaxIndSetComparator<T> implements Comparator<Node<T>> {
    private ArrayList<UndirectedGraph<T>> indSetPartition;

    public MaxIndSetComparator(ArrayList<UndirectedGraph<T>> partition){
        this.indSetPartition = partition;
    }

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
    
    public int getIndSetPartitionIndex(Node<T> n){
        for(int i = 0; i < indSetPartition.size(); i++){
            UndirectedGraph<T> g = indSetPartition.get(i);
            if(g.containsNode(n)){
                return i;
            }
        }
        return -1;
    }
}
