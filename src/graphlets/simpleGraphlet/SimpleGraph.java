package graphlets.simpleGraphlet;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import graphlets.AbstractGraph;
import graphlets.IllegalGraphActionException;

/**
 * Simple, undirected graph implementation of the AbstractGraph class. The graph
 * is implemented as an adjacency list.
 * 
 * @author Ine Melckenbeeck
 *
 */
public class SimpleGraph extends AbstractGraph<Boolean> {

	private List<SortedSet<Integer>> adjacency;

	/**
	 * Creates an empty simple graph. 0 nodes, 0 edges.
	 */
	public SimpleGraph() {
		adjacency = new ArrayList<>();
		order = 0;
		size = 0;
	}

	/**
	 * Read a graph in from file. I should program this somewhere else.
	 * 
	 * @param filename
	 * @return
	 */
	public static SimpleGraph readGraph(String filename) {
		File file = new File(filename);
		SimpleGraph result = new SimpleGraph();
		try {
			Scanner scanner = new Scanner(file);
			boolean started = false;
			while (scanner.hasNextLine()) {
				String s = scanner.nextLine();
				if (!s.startsWith("#")) {
					String[] namen = s.split(" ");
					if (namen.length >= 2) {
						int a = Integer.parseInt(namen[0]);
						int b = Integer.parseInt(namen[1]);
						if (!started) {
							for (int i = 0; i < a; i++) {
								result.addNode();
							}
							started = true;
						} else {
							try {
								result.addEdge(a, b, true);
							} catch (IllegalGraphActionException e) {
								e.printStackTrace();
							}
						}
					}
				}
			}
			scanner.close();
		} catch (FileNotFoundException e) {
			System.out.println("Invalid file name");
		}
		return result;
	}

	@Override
	public void addNodeInternal() {
		adjacency.add(new TreeSet<>());
	}

	@Override
	public void removeNodeInternal(int node) throws IllegalGraphActionException {
		checkNode(node);
		adjacency.remove(node);
		for (SortedSet<Integer> neighbours : adjacency) {
			neighbours.remove(node);
			for (int i : neighbours) {
				if (i > node) {
					neighbours.remove(i);
					neighbours.add(i - 1);
				}
			}
		}
	}

	@Override
	public void addEdgeInternal(int node1, int node2, Boolean type) throws IllegalGraphActionException {
		checkNode(node1);
		checkNode(node2);
		checkLoop(node1, node2);
		if (!adjacency.get(node1).add(node2) || !adjacency.get(node2).add(node1)) {
			throw new IllegalGraphActionException("No double edges allowed");
		}
	}

	@Override
	public void removeEdgeInternal(int node1, int node2) throws IllegalGraphActionException {
		checkNode(node1);
		checkNode(node2);
		checkEdge(node1, node2);
		adjacency.get(node1).remove(node2);
		adjacency.get(node2).remove(node1);
	}

	@Override
	public void removeEdgeInternal(int node1, int node2, Boolean type) throws IllegalGraphActionException {
		checkNode(node1);
		checkNode(node2);
		checkEdge(node1, node2);
		if (!adjacency.get(node1).remove(node2) || !adjacency.get(node2).remove(node1)) {
			throw new IllegalGraphActionException(
					"No arc from node " + (type ? node1 : node2) + " to node " + (type ? node2 : node1));
		}

	}

	@Override
	public SortedSet<Boolean> getEdges(int node1, int node2) throws IllegalGraphActionException {
		checkNode(node1);
		checkNode(node2);
		SortedSet<Boolean> result = new TreeSet<>();
		if (adjacency.get(node1).contains(node2)) {
			result.add(true);
		}
		return result;
	}

	@Override
	public SortedSet<Integer> getNeighbours(int node, Boolean condition) {
		return adjacency.get(node);
	}

	@Override
	public SortedMap<Integer, SortedSet<Boolean>> getNeighbours(int node) {
		SortedMap<Integer, SortedSet<Boolean>> result = new TreeMap<>();
		SortedSet<Boolean> edge = new TreeSet<>();
		edge.add(true);
		for (int neighbour : adjacency.get(node)) {
			result.put(neighbour, edge);
		}
		return result;
	}

	@Override
	public double density() {
		return size * 2. / (order - 1.) / order;
	}

	@Override
	public boolean isComplete() {
		return size * 2 == (order - 1) * order;
	}

	@Override
	public String toString() {
		return adjacency.toString();
	}

	@Override
	public SortedSet<Integer> getInvertedNeighbours(int node, Boolean condition) {
		return getNeighbours(node, condition);
	}
}
