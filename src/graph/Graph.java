package graph;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import graphs.AbstractGraph;
import graphs.IllegalGraphActionException;
import tree.GraphletTree;
import tree.TreeGenerator;
import treewalker.TreeWalker;

public class Graph extends AbstractGraph<Boolean> {

	private List<SortedSet<Integer>> adjacency;

	public Graph() {
		adjacency = new ArrayList<>();
	}

	public static void main(String[] args) {
		// Graph graph = readGraph("test/randomgraph-100-1000.txt");
		Graph graph = readGraph("test/example.in");
//		System.out.println(graph);
		GraphletTree<Graphlet, Boolean> tree = new TreeGenerator<Graphlet, Boolean>(new GraphletFactory( true), 5)
				.generateTree();
		TreeWalker<Graphlet, Boolean> walker = new TreeWalker<>(tree, graph);
		walker.run(System.out);
	}

	public static Graph readGraph(String filename) {
		File file = new File(filename);
		Graph result = new Graph();
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
	public boolean isConnected() {
		Deque<Integer> queue = new LinkedList<>();
		queue.add(0);
		Set<Integer> check = new TreeSet<>();
		check.add(0);
		while (!queue.isEmpty()) {
			int node = queue.poll();
			for (int neighbour : adjacency.get(node)) {
				if (check.add(neighbour)) {
					queue.addLast(neighbour);
				}
			}
		}
		return check.size() == order;
	}

	@Override
	public void addNode() {
		order++;
		adjacency.add(new TreeSet<>());
	}

	@Override
	public void removeNode(int node) throws IllegalGraphActionException {
		checkNode(node);
		adjacency.remove(node);
		for (SortedSet<Integer> neighbours : adjacency) {
			if (neighbours.remove(node)) {
				size--;
			}
			for (int i : neighbours) {
				if (i > node) {
					neighbours.remove(i);
					neighbours.add(i - 1);
				}
			}
		}
		order--;

	}

	@Override
	public void addEdge(int node1, int node2, Boolean type) throws IllegalGraphActionException {
		checkNode(node1);
		checkNode(node2);
		checkLoop(node1, node2);
		if (!adjacency.get(node1).add(node2) || !adjacency.get(node2).add(node1)) {
			throw new IllegalGraphActionException("No double edges allowed");
		}
		size++;

	}

	@Override
	public void removeEdge(int node1, int node2) throws IllegalGraphActionException {
		checkNode(node1);
		checkNode(node2);
		checkEdge(node1, node2);
		if (adjacency.get(node1).remove(node2) && adjacency.get(node2).remove(node1)) {
			size--;
		}

	}

	@Override
	public void removeEdge(int node1, int node2, Boolean type) throws IllegalGraphActionException {

		checkNode(node1);
		checkNode(node2);
		checkEdge(node1, node2);
		if (adjacency.get(node1).remove(node2) && adjacency.get(node2).remove(node1)) {
			size--;
		}else {
			throw new IllegalGraphActionException("No arc from node "+(type?node1:node2)+" to node "+ (type?node2:node1));
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
	public List<Boolean> edgeTypes() {
		List<Boolean> result = new ArrayList<>();
		result.add(true);
		return result;
	}

	@Override
	public List<SortedSet<Boolean>> validEdges() {
		List<SortedSet<Boolean>> result = new ArrayList<>();
		result.add(new TreeSet<>(edgeTypes()));
		return result;
	}

	@Override
	public boolean isComplete() {
		return size * 2 == (order - 1) * order;
	}

	public String toString() {
		return adjacency.toString();
	}

	@Override
	public SortedSet<Integer> getInvertedNeighbours(int node, Boolean condition) {
		return getNeighbours(node,condition);
	}
}
