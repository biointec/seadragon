package graphlets.genGraphlet;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import graphletgeneration.AbstractGraphletFactory;
import graphlets.AbstractGraph;
import graphlets.AbstractGraphlet;
import graphlets.IllegalGraphActionException;
import graphlets.diGraphlet.DiGraph;

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
	public void addNodeInternal() {
		plus.addNode();
		minus.addNode();
	}

	@Override
	public void removeNodeInternal(int node) throws IllegalGraphActionException {
		plus.removeNode(node);
		minus.removeNode(node);
	}

	@Override
	public void addEdgeInternal(int node1, int node2, Byte type) throws IllegalGraphActionException {
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
		default:
			throw new IllegalGraphActionException("Invalid edge type: "+type);
		}
	}

	@Override
	public void removeEdgeInternal(int node1, int node2) throws IllegalGraphActionException {
		try {
			plus.removeEdge(node1, node2);
			try {
				minus.removeEdge(node1, node2);
			} catch (IllegalGraphActionException e) {
			}
		} catch (IllegalGraphActionException e) {
			minus.removeEdge(node1, node2);
		}

	}

	@Override
	public void removeEdgeInternal(int node1, int node2, Byte type) throws IllegalGraphActionException {
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
	public boolean isComplete() {
		return plus.isComplete() && minus.isComplete();
	}

	@Override
	public SortedSet<Integer> getInvertedNeighbours(int node, Byte condition) {
		return getNeighbours(node,(byte) -condition);
	}
	@Override
	public AbstractGraphletFactory<? extends AbstractGraphlet<Byte>, Byte> getGraphletType(boolean useOrbits) {
		return new GenGraphletFactory( useOrbits);
	}

	@Override
	public Byte getType(String pieces) {
		
			return (byte) pieces.charAt(0);
		
	}
}
