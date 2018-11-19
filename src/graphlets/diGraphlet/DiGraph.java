package graphlets.diGraphlet;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import graphlets.AbstractGraph;
import graphlets.IllegalGraphActionException;

/**
 * Simple directed graph implementation of AbstractGraph. In this graph, double edges may exist only if they have opposite direction.
 * @author imelcken
 *
 */
public class DiGraph extends AbstractGraph<Boolean> {

	private List<SortedSet<Integer>> arcs;
	private List<SortedSet<Integer>> invertedArcs;
	
	/**
	 * 
	 */
	public DiGraph() {
		arcs = new ArrayList<>();
		invertedArcs = new ArrayList<>();
	}
	
	public String toString() {
		return arcs.toString()+"\n"+invertedArcs.toString();
	}

	public static DiGraph readGraph(String filename) {
		File file = new File(filename);
		DiGraph result = new DiGraph();
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
	
//	@Override
//	public boolean isConnected() {
//		Deque<Integer> queue = new LinkedList<>();
//		queue.add(0);
//		Set<Integer> check = new TreeSet<>();
//		check.add(0);
//		while (!queue.isEmpty()) {
//			int node = queue.poll();
//			for (int neighbour : arcs.get(node)) {
//				if (check.add(neighbour)) {
//					queue.addLast(neighbour);
//				}
//			}
//			for (int neighbour : invertedArcs.get(node)) {
//				if (check.add(neighbour)) {
//					queue.addLast(neighbour);
//				}
//			}
//		}
//		return check.size() == order;
//	}

	@Override
	public void addNodeInternal() {
//		order++;
		arcs.add(new TreeSet<Integer>());
		invertedArcs.add(new TreeSet<Integer>());

	}

	@Override
	public void removeNodeInternal(int node) throws IllegalGraphActionException {
		checkNode(node);
		arcs.remove(node);
		invertedArcs.remove(node);
		for (SortedSet<Integer> neighbours : arcs) {
			if (neighbours.remove(node)) {
//				size--;
			}
			for (int i : neighbours) {
				if (i > node) {
					neighbours.remove(i);
					neighbours.add(i - 1);
				}
			}
		}
		for (SortedSet<Integer> neighbours : invertedArcs) {
			if (neighbours.remove(node)) {
//				size--;
			}
			for (int i : neighbours) {
				if (i > node) {
					neighbours.remove(i);
					neighbours.add(i - 1);
				}
			}
		}
//		order--;

	}

	@Override
	public void addEdgeInternal(int node1, int node2, Boolean type) throws IllegalGraphActionException {
		checkNode(node1);
		checkNode(node2);
		checkLoop(node1, node2);
		if (!type) {
			int reserve = node2;
			node2 = node1;
			node1 = reserve;
		}
		if (!arcs.get(node1).add(node2) || !invertedArcs.get(node2).add(node1)) {
			throw new IllegalGraphActionException("No double edges allowed");
		}
//		size++;

	}

	@Override
	public void removeEdgeInternal(int node1, int node2) throws IllegalGraphActionException {
		checkNode(node1);
		checkNode(node2);
		checkEdge(node1, node2);
		arcs.get(node1).remove(node2);
		arcs.get(node2).remove(node1);
		invertedArcs.get(node1).remove(node2);
		invertedArcs.get(node2).remove(node1);
//		size--;
	}

	@Override
	public void removeEdgeInternal(int node1, int node2, Boolean type) throws IllegalGraphActionException {
		checkNode(node1);
		checkNode(node2);
		checkEdge(node1, node2);
		if (!type) {
			int reserve = node2;
			node2 = node1;
			node1 = reserve;
		}
		if (arcs.get(node1).remove(node2) && invertedArcs.get(node2).remove(node1)) {
//			size--;
		} else {
			throw new IllegalGraphActionException("No arc from node " + node1 + " to node " + node2);
		}

	}

	@Override
	public SortedSet<Boolean> getEdges(int node1, int node2) throws IllegalGraphActionException {
		checkNode(node1);
		checkNode(node2);
		SortedSet<Boolean> result = new TreeSet<>();
		if (arcs.get(node1).contains(node2)) {
			result.add(true);
		}
		if (invertedArcs.get(node1).contains(node2)) {
			result.add(false);
		}
		return result;
	}

	@Override
	public SortedSet<Integer> getNeighbours(int node, Boolean condition) {
		if (condition) {
			return arcs.get(node);
		} else {
			return invertedArcs.get(node);
		}
	}

	@Override
	public SortedMap<Integer, SortedSet<Boolean>> getNeighbours(int node) {
		SortedMap<Integer, SortedSet<Boolean>> result = new TreeMap<>();
		SortedSet<Boolean> edge = new TreeSet<>();
		edge.add(true);
		for (int neighbour : arcs.get(node)) {
			result.put(neighbour, edge);
		}
		edge = new TreeSet<>();
		edge.add(false);
		for (int neighbour : invertedArcs.get(node)) {
			result.put(neighbour, edge);
		}
		return result;
	}

	@Override
	public double density() {
		return (double) size / (order * (order - 1.));
	}

//	@Override
//	public List<Boolean> edgeTypes() {
//		List<Boolean> result = new ArrayList<>();
//		result.add(false);
//		result.add(true);
//		return result;
//	}
//
//	@Override
//	public List<SortedSet<Boolean>> edgeCombinations() {
//		List<SortedSet<Boolean>> result = new ArrayList<>();
//		SortedSet<Boolean> a = new TreeSet<Boolean>();
//		a.add(true);
//		result.add(a);
//		a = new TreeSet<Boolean>();
//		a.add(false);
//		result.add(a);
//		a = new TreeSet<Boolean>();
//		a.add(true);
//		a.add(false);
//		result.add(a);
//		return result;
//	}

	@Override
	public boolean isComplete() {
		return size == order * (order - 1);
	}
	@Override
	public SortedSet<Integer> getInvertedNeighbours(int node, Boolean condition) {
		return getNeighbours(node,!condition);
	}

}
