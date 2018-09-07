package genGraphlet;

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

import diGraphlet.DiGraph;
import graph.Graph;
import graph.Graphlet;
import graphs.AbstractGraph;
import graphs.IllegalGraphActionException;
import tree.GraphletTree;
import tree.TreeGenerator;
import treewalker.TreeWalker;

public class GenGraph extends AbstractGraph<Byte> {
	private DiGraph plus;
	private DiGraph minus;

	public GenGraph() {
		plus = new DiGraph();
		minus = new DiGraph();
	}

	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append("+\n");
		result.append(plus);
		result.append("\n-\n");
		result.append(minus);
		return result.toString();
	}
	
	
	public static void main(String[]args) throws IllegalGraphActionException {
		GenGraph graph = new GenGraph();
		System.out.println(graph);
		graph.addNode();
		graph.addNode();
		System.out.println(graph);
		graph.addEdge(0, 1, (byte)'+');
		graph.addEdge(1, 0, (byte)2);
		System.out.println(graph);
		System.out.println(graph.getEdges(0, 1));
		System.out.println(graph.validEdges());
		System.out.println(graph.edgeTypes());
//		
		
//				GenGraph graph = readGraph("test/test.txt");
//				System.out.println(graph);
//				GraphletTree<GenGraphlet, Byte> tree = new TreeGenerator<>(new GenGraphlet("", true), 3)
//						.generateTree();
//				tree.print();
//				TreeWalker<GenGraphlet, Byte> walker = new TreeWalker<>(tree, graph);
//				walker.run(System.out);
	}

	public static GenGraph readGraph(String filename) {
		File file = new File(filename);
		GenGraph result = new GenGraph();
		try {
			Scanner scanner = new Scanner(file);
			boolean started = false;
			while (scanner.hasNextLine()) {
				String s = scanner.nextLine();
				if (!s.startsWith("#")) {
					String[] namen = s.split(" ");
					if (namen.length >= 3) {
						int a = Integer.parseInt(namen[0]);
						int b = Integer.parseInt(namen[1]);
						
						
						if (!started) {
//							System.out.println("ping");
							for (int i = 0; i < a; i++) {
								result.addNode();
							}
							started = true;
						} else {
							byte t;
							try {
								t = Byte.parseByte(namen[2]);
							} catch (NumberFormatException e) {
								t = (byte) namen[2].charAt(0);
							}
							try {
								result.addEdge(a, b, t);
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
			for (int neighbour : getNeighbours(node).keySet()) {
				if (check.add(neighbour)) {
					queue.addLast(neighbour);
				}
			}
		}
		return check.size() == order;
	}

	@Override
	public void addNode() {
		plus.addNode();
		minus.addNode();
		order++;
	}

	@Override
	public void removeNode(int node) throws IllegalGraphActionException {
		plus.removeNode(node);
		minus.removeNode(node);
		order--;
	}

	@Override
	public void addEdge(int node1, int node2, Byte type) throws IllegalGraphActionException {
		switch (type) {
		case '+':
		case 1:
			plus.addEdge(node1, node2, true);
			break;
		case '-':
		case 2:
			minus.addEdge(node1, node2, true);
			break;
		case -1:
			plus.addEdge(node1, node2, false);
			break;
		case -2:
			minus.addEdge(node1, node2, false);
			break;
		}
		size++;

	}

	@Override
	public void removeEdge(int node1, int node2) throws IllegalGraphActionException {
		try {
			plus.removeEdge(node1, node2);
			size--;
			try {
				minus.removeEdge(node1, node2);
				size--;
			} catch (IllegalGraphActionException e) {
			}
		} catch (IllegalGraphActionException e) {
			minus.removeEdge(node1, node2);
			size--;
		}

	}

	@Override
	public void removeEdge(int node1, int node2, Byte type) throws IllegalGraphActionException {
		switch (type) {
		case '+':
		case 1:
			plus.removeEdge(node1, node2, true);
			break;
		case '-':
		case 2:
			minus.removeEdge(node1, node2, true);
			break;
		case -1:
			plus.removeEdge(node1, node2, false);
			break;
		case -2:
			minus.removeEdge(node1, node2, false);
			break;
		}
		size--;
	}

	@Override
	public SortedSet<Byte> getEdges(int i, int j) throws IllegalGraphActionException {
		SortedSet<Byte> result = new TreeSet<>();
		for (boolean b : plus.getEdges(i, j)) {
			result.add(b ? (byte) 1 : -1);
		}
		for (boolean b : minus.getEdges(i, j)) {
			result.add(b ? (byte) 2 : -2);
		}
		return result;
	}

	@Override
	public SortedSet<Integer> getNeighbours(int node, Byte condition) {
		switch (condition) {
		case '+':
		case 1:
			return plus.getNeighbours(node, true);
		case '-':
		case 2:
			return minus.getNeighbours(node, true);
		case -1:
			return plus.getNeighbours(node, false);
		case -2:
			return minus.getNeighbours(node, false);
		}
		return null;
	}

	@Override
	public SortedMap<Integer, SortedSet<Byte>> getNeighbours(int node) {
		SortedMap<Integer, SortedSet<Byte>> result = new TreeMap<>();
		SortedMap<Integer, SortedSet<Boolean>> neighbours = plus.getNeighbours(node);
		for (int neighbour : neighbours.keySet()) {
			SortedSet<Boolean> a = neighbours.get(neighbour);
			SortedSet<Byte> translation = new TreeSet<>();
			for (boolean b : a) {
				translation.add(b ? (byte) 1 : -1);
			}
			result.put(neighbour, translation);
		}
		neighbours = minus.getNeighbours(node);
		for (int neighbour : neighbours.keySet()) {
			SortedSet<Boolean> a = neighbours.get(neighbour);
			SortedSet<Byte> translation = result.get(neighbour);
			if (translation == null) {
				translation = new TreeSet<>();
			}
			for (boolean b : a) {
				translation.add(b ? (byte) 2 : -2);
			}
			result.put(neighbour, translation);
		}
		return result;
	}

	@Override
	public double density() {
		return (plus.density() + minus.density()) / 2;
	}

	@Override
	public List<Byte> edgeTypes() {
		List<Byte> result = new ArrayList<>();
		result.add((byte) 1);
		result.add((byte) -1);
		result.add((byte) 2);
		result.add((byte) -2);
		return result;
	}

	@Override
	public List<SortedSet<Byte>> validEdges() {
		List<SortedSet<Byte>> result = new ArrayList<>();
		byte[][] options = { { 0, -1, -2 }, { 0, 1, 2 } };
		for (int i = 1; i < 9; i++) {
			int a = i % 3;
			int b = i / 3;
			SortedSet<Byte> piece = new TreeSet<>();
			if (options[0][a] != 0) {
				piece.add(options[0][a]);
			}
			if (options[1][b] != 0) {
				piece.add(options[1][b]);
			}
			result.add(piece);
		}
		return result;
	}

	@Override
	public boolean isComplete() {
		return plus.isComplete() && minus.isComplete();
	}

	@Override
	public SortedSet<Integer> getInvertedNeighbours(int node, Byte condition) {
		return getNeighbours(node,(byte) -condition);
	}
}
