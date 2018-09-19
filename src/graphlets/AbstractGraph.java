package graphlets;

import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * General abstract class that represents a graph of any kind.
 * 
 * @author Ine Melckenbeeck
 *
 * @param <T>
 *            A datatype with which the AbstractGraph represents its edge types.
 */
public abstract class AbstractGraph<T extends Comparable<T>> {
	protected int order;
	protected int size;

	/**
	 * Checks whether this graph is connected.
	 * 
	 * @return <code>true</code> if the graph is connected.
	 */
	public boolean isConnected() {
		Set<Integer> used = new TreeSet<>();
		Deque<Integer> d = new LinkedList<>();
		d.add(0);
		used.add(0);
		while (!d.isEmpty()) {
			for (int i : getNeighbours(d.pop()).keySet()) {
				if (used.add(i)) {
					d.add(i);
//					System.out.println(used);
				}
			}
		}
		return used.size() == order;
	}

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
	 * @param node
	 *            The index of the node to be checked.
	 * @throws IllegalGraphActionException
	 *             if the given node is not present.
	 */
	protected void checkNode(int node) throws IllegalGraphActionException {
		if (node >= order) {
			throw new IllegalGraphActionException("Node " + node + " not present");
		}
	}

	/**
	 * Helper function that throws an exception if the given nodes are not
	 * connected.
	 * 
	 * @param i
	 *            first node
	 * @param j
	 *            second node
	 * @throws IllegalGraphActionException
	 *             if the given nodes are not connected.
	 */
	protected void checkEdge(int i, int j) throws IllegalGraphActionException {
		if (getEdges(i, j).isEmpty()) {
			throw new IllegalGraphActionException("Edge " + i + "," + j + " not present");
		}
	}

	/**
	 * Helper function that throws an exception if the given nodes are the same.
	 * Intended to prevent self-loops in graphs where those are not allowed.
	 * 
	 * @param i
	 *            first node
	 * @param j
	 *            second node
	 * @throws IllegalGraphActionException
	 *             if the given nodes are the same.
	 */
	protected void checkLoop(int i, int j) throws IllegalGraphActionException {
		if (i == j) {
			throw new IllegalGraphActionException("No self-loops allowed");
		}
	}

	/**
	 * Returns a SortedSet containing all edge types that are present between node i
	 * and node j.
	 * 
	 * @param i
	 *            first node
	 * @param j
	 *            second node
	 * @return a SortedSet containing all edge types that are present between node i
	 *         and node j.
	 * @throws IllegalGraphActionException
	 *             if either of the nodes does not exist.
	 */
	public abstract SortedSet<T> getEdges(int i, int j) throws IllegalGraphActionException;

	/**
	 * Returns a SortedSet containing all nodes to which there is an edge from the
	 * given node, of the given type.
	 * 
	 * @param node
	 *            the starting node for the edges.
	 * @param type
	 *            the edge type.
	 * @return a SortedSet containing all nodes to which there is an edge from the
	 *         given node, of the given type.
	 */
	public abstract SortedSet<Integer> getNeighbours(int node, T type);

	/**
	 * Returns a SortedSet containing all nodes from which there is an edge to the
	 * given node, of the given type.
	 * 
	 * @param node
	 *            the ending node for the edges.
	 * @param type
	 *            the edge type.
	 * @return a SortedSet containing all nodes from which there is an edge to the
	 *         given node, of the given type.
	 */
	public abstract SortedSet<Integer> getInvertedNeighbours(int node, T type);

	/**
	 * Returns a SortedMap that contains
	 * 
	 * @see #getEdges(int, int)
	 * @param node
	 * @return
	 */
	public abstract SortedMap<Integer, SortedSet<T>> getNeighbours(int node);

	/**
	 * Returns the graph's density, i.e. the number of edges in this graph divided
	 * by the maximum number of edges.
	 * 
	 * @return the graph's density.
	 */
	public abstract double density();

	public abstract List<T> edgeTypes();

	public abstract List<SortedSet<T>> edgeCombinations();

	/**
	 * Returns the graph's order, i.e. its number of nodes.
	 * 
	 * @return the graph's order.
	 */
	public int getOrder() {
		return order;
	}

	/**
	 * Returns the graph's size, i.e. its number of edges.
	 * 
	 * @return the graph's size.
	 */
	public int getSize() {
		return size;
	}

	// public void addNode(List<Integer> neighbours, List<T> edges) throws
	// IllegalGraphActionException {
	// if (neighbours.size() < edges.size()) {
	// throw new IllegalGraphActionException("Too many edge types.");
	// } else if (neighbours.size() > edges.size()) {
	// throw new IllegalGraphActionException("Not enough edge types.");
	// }
	// addNode();
	// for (int i = 0; i < neighbours.size(); i++) {
	// addEdge(order - 1, neighbours.get(i), edges.get(i));
	// }
	// }

	/**
	 * Returns <code>true</code> if this graph is complete, i.e. no more edges can
	 * be added.
	 * 
	 * @return <code>true</code> if this graph is complete.
	 */
	public abstract boolean isComplete();

}
