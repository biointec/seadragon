package graphlets;

import java.util.List;
import java.util.SortedMap;
import java.util.SortedSet;

/**
 * General abstract class that represents a graph of any kind.
 * 
 * @author Ine Melckenbeeck
 *
 * @param <T>
 *            A datatype with which the Graph represents its edge types.
 */
public abstract class Graph<T extends Comparable<T>> {
	protected int order;
	protected int size;

	/**
	 * Checks whether this graph is connected.
	 * 
	 * @return true if the graph is connected.
	 */
	public abstract boolean isConnected();

	/**
	 * Adds a node to the graph. Implementations must increase the graph's order.
	 */
	public abstract void addNode();

	/**
	 * Remove the given node from the graph. Implementations must decrease the
	 * graph's order.
	 * 
	 * @param node
	 *            The index of the node to be removed.
	 * @throws IllegalGraphActionException
	 *             if there is no node with index i, i.e. when the graph's order is
	 *             i or less.
	 */
	public abstract void removeNode(int node) throws IllegalGraphActionException;

	/**
	 * Adds an edge of the given type between the nodes with index the given nodes.
	 * Implementations must increase the graph's size.
	 * 
	 * @param node1
	 *            First node of the edge.
	 * @param node2
	 *            Second node of the edge.
	 * @param type
	 *            The type of the edge.
	 * @throws IllegalGraphActionException
	 *             if node i or j does not exist, or if an edge of the given type
	 *             can not be added between these nodes.
	 */
	public abstract void addEdge(int node1, int node2, T type) throws IllegalGraphActionException;

	/**
	 * Removes all edges between the given nodes. Implementations must decrease the
	 * graph's size.
	 * 
	 * @param node1
	 *            First node of the edges.
	 * @param node2
	 *            Second node of the edges.
	 * @throws IllegalGraphActionException
	 *             if node i or j does not exist, or if there is no edge between
	 *             these nodes.
	 */
	public abstract void removeEdge(int node1, int node2) throws IllegalGraphActionException;

	/**
	 * Removes an edge of the given type between nodes i and j. Implementations must
	 * decrease the graph's size.
	 * 
	 * @param node1
	 *            First node of the edge.
	 * @param node2
	 *            Second node of the edge.
	 * @param type
	 *            Type of the edge.
	 * @throws IllegalGraphActionException
	 *             if node i or j does not exist, or if there is no edge of the
	 *             given type between these nodes.
	 */
	public abstract void removeEdge(int node1, int node2, T type) throws IllegalGraphActionException;

	/**
	 * Helper function that throws an exception if the given node is not present,
	 * i.e. if node is greater than or equal to the graph's order.
	 * 
	 * @param node The index of the node to be checked.
	 * @throws IllegalGraphActionException if the given node is not present.
	 */
	protected void checkNode(int node) throws IllegalGraphActionException {
		if (node >= order) {
			throw new IllegalGraphActionException("Node " + node + " not present");
		}
	}

	/**
	 * Helper function that throws an exception if the given nodes are not connected.
	 * @param i
	 * @param j
	 * @throws IllegalGraphActionException
	 */
	protected void checkEdge(int i, int j) throws IllegalGraphActionException {
		if (getEdges(i, j).isEmpty()) {
			throw new IllegalGraphActionException("Edge " + i + "," + j + " not present");
		}
	}

	public abstract SortedSet<T> getEdges(int i, int j) throws IllegalGraphActionException;

	public abstract SortedSet<Integer> getNeighbours(int node, T condition);

	public abstract SortedMap<Integer, SortedSet<T>> getNeighbours(int node);

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
