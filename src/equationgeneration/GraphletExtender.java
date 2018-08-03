package equationgeneration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import diGraphlet.DiGraphlet;
import graphlets.AbstractGraphlet;
import graphlets.IllegalGraphActionException;

public class GraphletExtender {

	public static <T extends AbstractGraphlet<F>,F extends Comparable<F>> Set<T> extendEdges(T g) {
		Set<T> result = new TreeSet<T>();
		List<SortedSet<F>> edges = g.validEdges();
		System.out.println(edges);
		int[] counter = new int[g.getOrder()];
		while (counter[0] == 0) {
			try {
				T copy = g.copy();
				for (int j = 0; j < g.getOrder() - 1; j++) {
					if (counter[j + 1] != 0) {
						for (F edge : edges.get(counter[j + 1] - 1)) {
							copy.addEdge(g.getOrder() - 1, j, edge);
						}
					}
				}
				result.add(copy);
			} catch (IllegalGraphActionException e) {
				// System.out.println(e.getMessage());
			}
			int i = g.getOrder() - 1;
			while (counter[i] == edges.size()) {
				counter[i--] = 0;
			}
			counter[i]++;
		}
		return result;
	}
	

	public static <T extends AbstractGraphlet<F>,F extends Comparable<F>> Set<T> extendNodes(T g) {
		Set<T> result = new TreeSet<T>();
		SortedSet<F> edges = g.edgeTypes();
		for (int i = 0; i < g.getOrder(); i++) {
			for (F edge : edges) {
				T copy = g.copy();
				copy.addNode();
				try {
					copy.addEdge(g.getOrder(), i, edge);
				} catch (IllegalGraphActionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				result.add(copy);
			}
		}
		return result;
	}
	
	public static <T extends AbstractGraphlet<F>,F extends Comparable<F>> Set<T> extendNodesWithEdges(T g){
		Set<T> result = new TreeSet<T>();
		g = g.copy();
		g.addNode();
		List<SortedSet<F>> edges = g.validEdges();
		System.out.println(edges);
		int[] counter = new int[g.getOrder()];
		counter[g.getOrder()-1] = 1;
		while (counter[0] == 0) {
			try {
				T copy = g.copy();
				for (int j = 0; j < g.getOrder() - 1; j++) {
					if (counter[j + 1] != 0) {
						for (F edge : edges.get(counter[j + 1] - 1)) {
							copy.addEdge(g.getOrder() - 1, j, edge);
						}
					}
				}
				result.add(copy);
			} catch (IllegalGraphActionException e) {
				// System.out.println(e.getMessage());
			}
			int i = g.getOrder() - 1;
			while (counter[i] == edges.size()) {
				counter[i--] = 0;
			}
			counter[i]++;
		}
		return result;
	}

	public static <T extends AbstractGraphlet<F>,F extends Comparable<F>> List<List<SortedSet<F>>> edgeCombinations(T g) {
		List<List<SortedSet<F>>> result = new ArrayList<>();
		List<SortedSet<F>> edges = g.validEdges();
		int[] counter = new int[g.getOrder()];
		while (counter[0] == 0) {
			List<SortedSet<F>> sub = new ArrayList<>();
			for (int j = 0; j < g.getOrder() - 1; j++) {
				if (counter[j + 1] != 0) {
					sub.add(edges.get(counter[j + 1]));
				} else {
					sub.add(new TreeSet<>());
				}
			}
			result.add(sub);

			int i = g.getOrder() - 1;
			while (counter[i] == edges.size() - 1) {
				counter[i--] = 0;
			}
			counter[i]++;
		}
		return result;
	}


	public static void main(String[] args) {
		DiGraphlet dg = new DiGraphlet("011000", true);
		System.out.println(dg);
		System.out.println(extendNodesWithEdges(dg));
	}
}
