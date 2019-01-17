package graphlets;

import java.util.Collection;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeSet;

import graphletgeneration.AbstractGraphletFactory;

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
	 * Adds a node to the graph.
	 */
	public void addNode() {
		addNodeInternal();
		order++;
	}

	/**
	 * Internal function to add a node. Do not call this method, call
	 * {@link #addNode()}, which in turn calls this method, instead.
	 * 
	 */
	protected abstract void addNodeInternal();

	/**
	 * Remove the given node from the graph.
	 * 
	 * @param node
	 *            The index of the node to be removed.
	 * @throws IllegalGraphActionException
	 *             if there is no node with index <code> node </code>, i.e. when the
	 *             graph's order is <code> node </code> or less.
	 */
	public void removeNode(int node) throws IllegalGraphActionException {
		int degree = 0;
		SortedMap<Integer, SortedSet<T>> neighbours = getNeighbours(node);
		for (int neighbour : neighbours.keySet()) {
			degree += neighbours.get(neighbour).size();
		}
		removeNodeInternal(node);
		order--;
		size -= degree;
	}

	/**
	 * Internal function to remove a node. Do not call this method, call
	 * {@link #removeNode(int)}, which in turn calls this method, instead.
	 * 
	 * @see #removeNode(int)
	 * @throws IllegalGraphActionException
	 *             if
	 */
	protected abstract void removeNodeInternal(int node) throws IllegalGraphActionException;

	/**
	 * Adds an edge of the given type between the nodes with index the given nodes.
	 * 
	 * @param node1
	 *            First node of the edge.
	 * @param node2
	 *            Second node of the edge.
	 * @param edgeType
	 *            The type of the edge.
	 * @throws IllegalGraphActionException
	 *             if node i or j does not exist, or if an edge of the given type
	 *             can not be added between these nodes.
	 */
	public void addEdge(int node1, int node2, T edgeType) throws IllegalGraphActionException {
		addEdgeInternal(node1, node2, edgeType);
		size++;
	}
	
	public abstract T getType(String pieces); 
	
	/**
	 * Internal function to add an edge. Do not call this method, call
	 * {@link #addEdge(int,int,T)}, which in turn calls this method, instead.
	 * 
	 */
	protected abstract void addEdgeInternal(int node1, int node2, T edgeType) throws IllegalGraphActionException;

	/**
	 * Removes all edges between the given nodes.
	 * 
	 * @param node1
	 *            First node of the edges.
	 * @param node2
	 *            Second node of the edges.
	 * @throws IllegalGraphActionException
	 *             if node i or j does not exist, or if there is no edge between
	 *             these nodes.
	 */
	public void removeEdge(int node1, int node2) throws IllegalGraphActionException {
		removeEdgeInternal(node1, node2);
		size--;
	}

	/**
	 * Internal function to remove an edge. Do not call this method, call
	 * {@link #removeEdge(int,int)}, which in turn calls this method, instead.
	 * 
	 */
	protected abstract void removeEdgeInternal(int node1, int node2) throws IllegalGraphActionException;

	/**
	 * Removes an edge of the given type between nodes i and j.
	 * 
	 * @param node1
	 *            First node of the edge.
	 * @param node2
	 *            Second node of the edge.
	 * @param edgeType
	 *            Type of the edge.
	 * @throws IllegalGraphActionException
	 *             if node i or j does not exist, or if there is no edge of the
	 *             given type between these nodes.
	 */
	public void removeEdge(int node1, int node2, T edgeType) throws IllegalGraphActionException {
		removeEdgeInternal(node1, node2, edgeType);
		size--;
	}

	/**
	 * Internal function to remove an edge. Do not call this method, call
	 * {@link #removeEdge(int,int,T)}, which in turn calls this method, instead.
	 * 
	 */
	public abstract void removeEdgeInternal(int node1, int node2, T edgeType) throws IllegalGraphActionException;

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
				}
			}
		}
		return used.size() == order;
	}

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
	 * @param node1
	 *            first node
	 * @param node2
	 *            second node
	 * @throws IllegalGraphActionException
	 *             if the given nodes are not connected.
	 */
	protected void checkEdge(int node1, int node2) throws IllegalGraphActionException {
		if (getEdges(node1, node2).isEmpty()) {
			throw new IllegalGraphActionException("Edge " + node1 + "," + node2 + " not present");
		}
	}

	/**
	 * Helper function that throws an exception if the given nodes are the same.
	 * Intended to prevent self-loops in graphs where those are not allowed.
	 * 
	 * @param node1
	 *            first node
	 * @param node2
	 *            second node
	 * @throws IllegalGraphActionException
	 *             if the given nodes are the same.
	 */
	protected void checkLoop(int node1, int node2) throws IllegalGraphActionException {
		if (node1 == node2) {
			throw new IllegalGraphActionException("No self-loops allowed");
		}
	}

	/**
	 * Returns a SortedSet containing all edge types that are present between node i
	 * and node j.
	 * 
	 * @param node1
	 *            first node
	 * @param node2
	 *            second node
	 * @return a SortedSet containing all edge types that are present between node i
	 *         and node j.
	 * @throws IllegalGraphActionException
	 *             if either of the nodes does not exist.
	 */
	public abstract SortedSet<T> getEdges(int node1, int node2) throws IllegalGraphActionException;

	/**
	 * Returns a SortedSet containing all nodes to which there is an edge from the
	 * given node, of the given type.
	 * 
	 * @param node
	 *            the starting node for the edges.
	 * @param edgeType
	 *            the edge type.
	 * @return a SortedSet containing all nodes to which there is an edge from the
	 *         given node, of the given type.
	 */
	public abstract SortedSet<Integer> getNeighbours(int node, T edgeType);

	/**
	 * Returns a SortedSet containing all nodes from which there is an edge to the
	 * given node, of the given type.
	 * 
	 * @param node
	 *            the ending node for the edges.
	 * @param edgeType
	 *            the edge type.
	 * @return a SortedSet containing all nodes from which there is an edge to the
	 *         given node, of the given type.
	 */
	public abstract SortedSet<Integer> getInvertedNeighbours(int node, T edgeType);

	/**
	 * Returns a SortedMap that contains all outgoing edges from this node, sorted
	 * by their end node.
	 * 
	 * @see #getEdges(int, int)
	 * @param node
	 *            The index of the node whose edges are needed.
	 * @return
	 */
	public abstract SortedMap<Integer, SortedSet<T>> getNeighbours(int node);

	/**
	 * Returns the indices of the nodes that are connected to the given node by the
	 * given edge types.
	 * 
	 * @param node
	 *            The node whose neighbours need to be returned.
	 * @param edgeTypes
	 *            The edge types from the given node that need to be present.
	 * @return the indices of the nodes that are connected to the given node by the
	 *         given edge types.
	 */
	public SortedSet<Integer> getNeighbours(int node, Collection<T> edgeTypes) {
		Iterator<T> it = edgeTypes.iterator();
		if (it.hasNext()) {
			SortedSet<Integer> neighbours = new TreeSet<>(getNeighbours(node, it.next()));
			while (it.hasNext() && !neighbours.isEmpty()) {
				neighbours.retainAll(getNeighbours(node, it.next()));
			}
			return neighbours;
		} else {
			return new TreeSet<>();
		}
	}

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

	/**
	 * Returns <code>true</code> if this graph is complete, i.e. no more edges can
	 * be added.
	 * 
	 * @return <code>true</code> if this graph is complete.
	 */
	public abstract boolean isComplete();

	/**
	 * Returns the graph's density, i.e. the number of edges in this graph divided
	 * by the maximum number of edges.
	 * 
	 * @return the graph's density.
	 */
	public abstract double density();
	
	public abstract AbstractGraphletFactory<? extends AbstractGraphlet<T>, T> getGraphletType(boolean useOrbits);
}
