package equationgeneration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.Stack;
import java.util.TreeMap;
import java.util.TreeSet;

import graphletgeneration.AbstractGraphletFactory;
import graphlets.AbstractGraph;

/**
 * Common neighbour counter for a given graph.
 * 
 * @author Ine Melckenbeeck
 *
 * @param <U>
 *            The edge type of the given graph.
 */
public class CommonsCounter<U extends Comparable<U>> {

	private AbstractGraph<U> graph;
	private Map<List<SortedSet<Integer>>, Integer> commons;
	private List<U> types;
	private List<SortedSet<U>> typeCombos;
	private int order;
	private int graphorder;

	/**
	 * Creates a new Commonscounter for the given graph and order. Immediately
	 * counts all common neighbours of up to <code>order</code> nodes within that
	 * graph.
	 * 
	 * @param graph
	 *            The graph in which the common neighbours are counted.
	 * @param order
	 *            The maximal number of nodes of which the common neighbours are
	 *            counted.
	 * @param factory
	 *            The AbstractGraphletFactory which contains all valid edges and
	 *            edge combinations for the common neighbours.
	 */
	public CommonsCounter(AbstractGraph<U> graph, int order, AbstractGraphletFactory<?, U> factory) {
		this.graph = graph;
		commons = new HashMap<>();
		types = factory.getEdgeTypes();
		typeCombos = factory.edgeCombinations();
		this.order = order;
		graphorder = graph.getOrder();
		iterativeCommons();
	}

	/**
	 * Returns the common neighbours of the given set of nodes and edge types.
	 * 
	 * @param key
	 *            The nodes whose common neighbours need to be returned, sorted by
	 *            the edges they need to have. More specifically, if the
	 * @return the number of common neighbours.
	 */
	public int getCommonNeighbours(List<SortedSet<Integer>> key) {
		if (commons.containsKey(key)) {
			return commons.get(key);
		} else {
			return 0;
		}
	}

	/**
	 * Calculate the common neighbours.
	 */
	private void iterativeCommons() {
		int[] counter = new int[order];
		for (int i = 1; i < order; i++)
			counter[i] = -1;
		int i = 0;
		Stack<Set<Integer>> common = new Stack<>();// for backtracking purposes
		int size = typeCombos.size();
		while (counter[0] < graphorder * size) {
			if (i == 0) {
				common.push(graph.getNeighbours(counter[i] / size, typeCombos.get(counter[i] % size)));
			} else {
				common.push(new TreeSet<>(common.peek()));
				common.peek().retainAll(graph.getNeighbours(counter[i] / size, typeCombos.get(counter[i] % size)));
			}
			if (common.peek().size() > 0) { // first as a map, then as a list...
				Map<U, SortedSet<Integer>> map = new TreeMap<>();
				for (U type : types) {
					map.put(type, new TreeSet<>());
				}
				for (int j = 0; j <= i; j++) {
					for (U type : typeCombos.get(counter[j] % size)) {
						map.get(type).add(counter[j] / size);
					}
				}
				List<SortedSet<Integer>> key = new ArrayList<>();
				for (int j = 0; j < types.size(); j++) {
					key.add(map.get(types.get(j)));
				}
				commons.put(key, common.peek().size());
			}
			if (i == order - 1 || common.peek().isEmpty()) {
				common.pop();
			} else {
				i++;
				counter[i] = counter[i - 1];
			}
			while (i > 0 && counter[i] == graphorder * size - 1) {
				counter[i--] = -1;
				common.pop();
			}
			counter[i]++;
		}
	}

	// private Map<List<SortedSet<Integer>>, Integer> recursiveCommons() {
	// // commons = new HashMap<>();
	// List<SortedSet<Integer>> start = new ArrayList<SortedSet<Integer>>();
	// for (int i = 0; i < types.size(); i++) {
	// start.add(new TreeSet<>());
	// }
	// recursiveCommons(start, new TreeSet<Integer>(), new Stack<Integer>(), 0);
	// // System.out.println(commons);
	// return commons;
	// }
	//
	// private static List<SortedSet<Integer>> deepCopy(List<SortedSet<Integer>>
	// original) {
	// List<SortedSet<Integer>> result = new ArrayList<>();
	// for (SortedSet<Integer> element : original) {
	// result.add(new TreeSet<Integer>(element));
	// }
	// return result;
	// }
	//
	// private void recursiveCommons(List<SortedSet<Integer>> key,
	// SortedSet<Integer> current, Stack<Integer> instance,
	// int edge) {
	// if (!instance.isEmpty())
	// for (int i = edge + 1; i < types.size(); i++) {
	// SortedSet<Integer> neighbours = new TreeSet<Integer>(
	// graph.getNeighbours(instance.peek(), types.get(i)));
	// neighbours.retainAll(current);
	// if (!neighbours.isEmpty()) {
	// List<SortedSet<Integer>> newkey = deepCopy(key);
	// newkey.get(i).add(instance.peek());
	// commons.put(newkey, neighbours.size());
	// recursiveCommons(newkey, neighbours, instance, i);
	// }
	// }
	// if (instance.size() != order) {
	// int start = instance.isEmpty() ? 0 : instance.peek() + 1;
	// for (int i = start; i < graphorder; i++) {
	// for (int j = 0; j < types.size(); j++) {
	// SortedSet<Integer> neighbours = new TreeSet<Integer>(graph.getNeighbours(i,
	// types.get(j)));
	// if (!instance.isEmpty())
	// neighbours.retainAll(current);
	// if (!neighbours.isEmpty()) {
	// List<SortedSet<Integer>> newkey = deepCopy(key);
	// newkey.get(j).add(i);
	// commons.put(newkey, neighbours.size());
	// instance.push(i);
	// recursiveCommons(newkey, neighbours, instance, j);
	// instance.pop();
	// }
	// }
	// }
	// }
	// }

}