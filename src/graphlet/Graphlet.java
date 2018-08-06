package graphlet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.Predicate;

import graphlets.AbstractGraphlet;
import graphlets.IllegalGraphActionException;

public class Graphlet extends AbstractGraphlet<Boolean>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3049809919286778062L;

	private class Edge {
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
//			if (!getOuterType().equals(other.getOuterType())) {
//				return false;
//			}
			if (a != other.a) {
				return false;
			}
			if (b != other.b) {
				return false;
			}
			return true;
		}

		int a, b;

		Edge(int a, int b) throws IllegalGraphActionException {
			if (a == b)
				throw new IllegalGraphActionException("No self-loops allowed");
			this.a = Math.min(a, b);
			this.b = Math.max(a, b);
		}

		private Graphlet getOuterType() {
			return Graphlet.this;
		}

		void swap(int a, int b) {
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

		public String toString() {
			return "(" + a + "," + b + ")";
		}

	}
	
	private class NodeInEdge implements Predicate<Edge>{
		private int node;
		NodeInEdge(int node){
			this.node = node;
		}

		@Override
		public boolean test(Edge arg0) {
			return arg0.a==node||arg0.b==node;
//			return false;
		}
		
	}

	private Set<Edge> edges;

	public Graphlet(boolean isOrbitRep) {
		super(isOrbitRep);
		edges = new HashSet<>();
	}

	public Graphlet(String representation, boolean isOrbitRep) {
		super(isOrbitRep);
		edges = new HashSet<>();
		order = (1 + (int) Math.sqrt(1 + 8 * representation.length())) / 2;
		for (int i = 1; i < order; i++) {
			for (int j = 0; j < i; j++) {
//				System.out.println(((i * (i - 1)) / 2) + j );
				if (representation.charAt((i * (i - 1)) / 2 + j) !='0') {
					try {
//						addEdge(i, j, true);
						 edges.add(new Edge(i, j));
						 size++;
					} catch (IllegalGraphActionException e) {
					}
				}
			}
		}
//		System.out.println(representation);
//		System.out.println(edges);
		
//		size = edges.size();
	}

	@Override
	public Graphlet copy() {
		return new Graphlet(this.representation(), this.isOrbitRep);
	}

	@Override
	public void swap(int a, int b) {
		// System.out.println(edges);
		for (Edge e : edges) {
			e.swap(a, b);
		}
		// System.out.println(edges);
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
		String result ="";
		for (int i = 0; i < order; i++) {
			for (int j = 0; j < i; j++) {
				// System.out.println(matrix[i][j]);
				result+=matrix[i][j];
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
	public boolean isConnected() {
		List<Set<Integer>> components = new ArrayList<>();
		for (int i = 0; i < order; i++) {
			Set<Integer> s = new TreeSet<>();
			s.add(i);
			components.add(s);
		}
		for (Edge e : edges) {
			components.get(e.a).addAll(components.get(e.b));
			components.get(e.b).addAll(components.get(e.a));
		}
		return order == 0 || components.get(0).size() == order;
	}

	@Override
	public void addNode() {
		++order;
		ready = false;
	}

	public static void main(String[] args) throws IllegalGraphActionException {
		Graphlet g = new Graphlet("1100010100",true);
		System.out.println(g);
		System.out.println(g.representation());
		System.out.println(g.canonical());
		System.out.println(g.copy());
	}

	@Override
	public void addEdge(int i, int j, Boolean status) throws IllegalGraphActionException {
		checkNode(i);
		checkNode(j);
		if (i == j) {
			throw new IllegalGraphActionException("No self-loops allowed");
		}
		if (!edges.add(new Edge(i, j))) {
			throw new IllegalGraphActionException("No multiple edges allowed");
		}else {
			size++;
			ready = false;
		}
	}

	@Override
	public void removeEdge(int i, int j) throws IllegalGraphActionException {
		if (!edges.remove(new Edge(i, j))) {
			throw new IllegalGraphActionException("Edge " + i + "," + j + " not present");
		}else {
			size--;
			ready = false;
		}
	}

	@Override
	public void removeEdge(int i, int j, Boolean type) throws IllegalGraphActionException {
		removeEdge(i, j);
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
	public SortedSet<Boolean> edgeTypes() {
		SortedSet<Boolean> result = new TreeSet<>();
		result.add(true);
		return result;
	}

	@Override
	public List<SortedSet<Boolean>> validEdges() {
		List<SortedSet<Boolean>> result = new ArrayList<>(1);
		result.add(edgeTypes());
		return result;
	}

	@Override
	public void removeNode(int i) throws IllegalGraphActionException {
		checkNode(i);
		edges.removeIf(new NodeInEdge(i));
		order--;
		
	}
	
	public String toString() {
		return edges.toString();
	}

	public boolean isComplete() {
		return size == (order*(order-1))/2;
	}

}
