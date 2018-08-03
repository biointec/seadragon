package graphlets;

import java.util.List;
import java.util.SortedMap;
import java.util.SortedSet;

public abstract class Graph<T extends Comparable<T>> {
	protected int order;
	protected int size;

	public abstract boolean isConnected();

	public abstract void addNode();

	public abstract void removeNode(int i)throws IllegalGraphActionException;

	public abstract void addEdge(int i, int j, T status) throws IllegalGraphActionException;

	public abstract void removeEdge(int i, int j) throws IllegalGraphActionException;
	
	public abstract void removeEdge(int i, int j, T type) throws IllegalGraphActionException;
	
	protected void checkNode(int i) throws IllegalGraphActionException {
		if(i>=order) {
			throw new IllegalGraphActionException("Node "+i+" not present");
		}
	}
	
	protected void checkEdge(int i, int j) throws IllegalGraphActionException {
		if(getEdges(i,j).isEmpty()) {
			throw new IllegalGraphActionException("Edge "+i+","+j +" not present");
		}
	}

	public abstract SortedSet<T> getEdges(int i, int j) throws IllegalGraphActionException;

	public abstract SortedSet<Integer> getNeighbours(int node, T condition);

	public abstract SortedMap<Integer, List<T>> getNeighbours(int node);

	public abstract double density();

	public abstract SortedSet<T> edgeTypes();

	public abstract List<SortedSet<T>> validEdges();

	public int getOrder() {
		return order;
	}

	public int getSize() {
		return size;
	}

	public void addNode(List<Integer> neighbours, List<T> edges) throws IllegalGraphActionException {
		if (neighbours.size() < edges.size()) {
			throw new IllegalGraphActionException("Too many edge types.");
		} else if (neighbours.size() > edges.size()) {
			throw new IllegalGraphActionException("Not enough edge types.");
		}
		addNode();
		for (int i = 0; i < neighbours.size(); i++) {
			addEdge(order - 1, neighbours.get(i), edges.get(i));
		}
	}
	
	public abstract boolean isComplete();

}
