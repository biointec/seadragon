package graphlets.simpleGraphlet;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.Predicate;

import graphletgeneration.AbstractGraphletFactory;
import graphlets.AbstractGraphlet;
import graphlets.IllegalGraphActionException;

/**
 * AbstractGraphlet implementation for classical graphlets: simple, undirected
 * graphlets. This implementation works with a list of Edge objects.
 * 
 * @author Ine Melckenbeeck
 *
 */
public class SimpleGraphlet extends AbstractGraphlet<Boolean> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3049809919286778062L;

	private Set<Edge> edges;

	/**
	 * Creates a simple graphlet with one node and no edges.
	 * @param isOrbitRep
	 */
	public SimpleGraphlet(boolean isOrbitRep) {
		super(isOrbitRep);
		order = 1;
		edges = new HashSet<>();
	}

	/**
	 * Creates a simple graphlet with the given representation.
	 * @param representation The string representation of the graphlet.
	 * @param isOrbitRep The status of the graphlet: graphlet or orbit representative.
	 */
	public SimpleGraphlet(String representation, boolean isOrbitRep) {
		super(isOrbitRep);
		edges = new HashSet<>();
		order = (1 + (int) Math.sqrt(1 + 8 * representation.length())) / 2;
		for (int i = 1; i < order; i++) {
			for (int j = 0; j < i; j++) {
				if (representation.charAt((i * (i - 1)) / 2 + j) != '0') {
					try {
						edges.add(new Edge(i, j));
						size++;
					} catch (IllegalGraphActionException e) {
					}
				}
			}
		}
	}

	@Override
	public void swap(int a, int b) {
		for (Edge e : edges) {
			e.swap(a, b);
		}
	}

	@Override
	public String representation() {
		int[][] matrix = new int[order][];
		for (int i = 0; i < order; i++) {
			matrix[i] = new int[i];
		}
		for (Edge e : edges) {
			matrix[e.b][e.a] = 1;
		}
		String result = "";
		for (int i = 0; i < order; i++) {
			for (int j = 0; j < i; j++) {
				result += matrix[i][j];
			}
		}
		return result;
	}

	@Override
	public StringBuilder toPS() {
		StringBuilder result = new StringBuilder();
		result.append(drawNodes());
		for (Edge e : edges) {
			result.append(drawEdge(e.a, e.b));
		}
		return result;
	}

	@Override
	public void addNodeInternal() {

	}

	@Override
	public void addEdgeInternal(int i, int j, Boolean status) throws IllegalGraphActionException {
		checkNode(i);
		checkNode(j);
		if (i == j) {
			throw new IllegalGraphActionException("No self-loops allowed");
		}
		if (!edges.add(new Edge(i, j))) {
			throw new IllegalGraphActionException("No multiple edges allowed");
		}
	}

	@Override
	public void removeEdgeInternal(int i, int j) throws IllegalGraphActionException {
		if (!edges.remove(new Edge(i, j))) {
			throw new IllegalGraphActionException("Edge " + i + "," + j + " not present");
		}
	}

	@Override
	public void removeEdgeInternal(int i, int j, Boolean type) throws IllegalGraphActionException {
		removeEdgeInternal(i, j);
	}

	@Override
	public SortedSet<Boolean> getEdges(int i, int j) throws IllegalGraphActionException {
		SortedSet<Boolean> result = new TreeSet<>();
		if (edges.contains(new Edge(i, j))) {
			result.add(true);
		}
		return result;
	}

	@Override
	public SortedSet<Integer> getNeighbours(int node, Boolean condition) {
		SortedSet<Integer> result = new TreeSet<>();
		List<Boolean> value = new ArrayList<>();
		value.add(true);
		for (Edge e : edges) {
			if (e.a == node) {
				result.add(e.b);
			} else if (e.b == node) {
				result.add(e.a);
			}
		}
		return result;
	}

	@Override
	public SortedMap<Integer, SortedSet<Boolean>> getNeighbours(int node) {
		SortedMap<Integer, SortedSet<Boolean>> result = new TreeMap<>();
		SortedSet<Boolean> value = new TreeSet<>();
		value.add(true);
		for (Edge e : edges) {
			if (e.a == node) {
				result.put(e.b, value);
			} else if (e.b == node) {
				result.put(e.a, value);
			}
		}
		return result;
	}

	@Override
	public double density() {
		return size * 2. / order / (order - 1.);
	}

	@Override
	public void removeNodeInternal(int i) throws IllegalGraphActionException {
		checkNode(i);
		edges.removeIf(new NodeInEdge(i));
		size = edges.size();

	}

	@Override
	public String toString() {
		return edges.toString();
	}

	@Override
	public boolean isComplete() {
		return size == (order * (order - 1)) / 2;
	}

	@Override
	public SortedSet<Integer> getInvertedNeighbours(int node, Boolean condition) {
		return getNeighbours(node, condition);
	}
	

	/**
	 * Internal class for edges: ordered sets of exactly two nodes.
	 * 
	 * @author Ine Melckenbeeck
	 *
	 */
	private class Edge implements Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = 7298572042071159020L;

		private int a, b;

		private Edge(int a, int b) throws IllegalGraphActionException {
			if (a == b)
				throw new IllegalGraphActionException("No self-loops allowed");
			this.a = Math.min(a, b);
			this.b = Math.max(a, b);
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + a;
			result = prime * result + b;
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null) {
				return false;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}
			Edge other = (Edge) obj;
			if (a != other.a) {
				return false;
			}
			if (b != other.b) {
				return false;
			}
			return true;
		}

		/**
		 * Swaps the node indices for node a and b, if any are present in this edge.
		 * 
		 * @param a
		 *            the first node index.
		 * @param b
		 *            the second node index.
		 */
		private void swap(int a, int b) {
			if (this.a == a) {
				this.a = b;
			} else if (this.a == b) {
				this.a = a;
			}
			if (this.b == a) {
				this.b = b;
			} else if (this.b == b) {
				this.b = a;
			}
			if (this.a > this.b) {
				int reserve = this.a;
				this.a = this.b;
				this.b = reserve;
			}
		}

		@Override
		public String toString() {
			return "(" + a + "," + b + ")";
		}

	}

	/**
	 * Predicate implementation to filter edges based on whether or not a certain
	 * node is present within them.
	 * 
	 * @author Ine Melckenbeeck
	 *
	 */
	private class NodeInEdge implements Predicate<Edge> {
		private int node;

		private NodeInEdge(int node) {
			this.node = node;
		}

		@Override
		public boolean test(Edge arg0) {
			return arg0.a == node || arg0.b == node;
		}

	}
	

	@Override
	public AbstractGraphletFactory<? extends AbstractGraphlet<Boolean>, Boolean> getGraphletType(boolean useOrbits) {
		return new SimpleGraphletFactory( useOrbits);
	}
}
