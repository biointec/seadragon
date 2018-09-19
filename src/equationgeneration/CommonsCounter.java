package equationgeneration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.Stack;
import java.util.TreeSet;

import graphlets.AbstractGraph;

public class CommonsCounter<T extends AbstractGraph<U>, U extends Comparable<U>> {

	private T graph;
	private Map<List<SortedSet<Integer>>, Integer> alternateCommons;
	List<U> types;
	List<SortedSet<U>> combinations;
	int order;
	int graphorder;

	public CommonsCounter(T graph, int order) {
		this.graph = graph;
		alternateCommons = new HashMap<>();
		types = new ArrayList<>(graph.edgeTypes());
		this.order = order;
		graphorder = graph.getOrder();
		combinations = graph.edgeCombinations();
		// combinations.add(new TreeSet<U>());
		// System.out.println(combinations);
	}

	

	public int getCommonNeighbours(List<SortedSet<Integer>> key) {
		if (alternateCommons.containsKey(key)) {
			return alternateCommons.get(key);
		} else {
			return 0;
		}
	}

	

	public Map<List<SortedSet<Integer>>,Integer> recursiveCommons() {
		List<SortedSet<Integer>> start = new ArrayList<SortedSet<Integer>>();
		for (int i = 0; i < types.size(); i++) {
			start.add(new TreeSet<>());
		}
//		System.out.println(types);
		recursiveCommons(start, new TreeSet<Integer>(), new Stack<Integer>(), 0);
		// recursiveCommons(new TreeMap<U, SortedSet<Integer>>(), new
		// TreeSet<Integer>(), new Stack<Integer>(), 0);
		// System.out.println(commons);
//		System.out.println(alternateCommons);
		return alternateCommons;
	}


	public static List<SortedSet<Integer>> deepCopy(List<SortedSet<Integer>> original) {
		List<SortedSet<Integer>> result = new ArrayList<>();
		for (SortedSet<Integer> element : original) {
			result.add(new TreeSet<Integer>(element));
		}
		return result;
	}

	public void recursiveCommons(List<SortedSet<Integer>> key, SortedSet<Integer> current, Stack<Integer> instance,
			int edge) {

		if (!instance.isEmpty())
			for (int i = edge + 1; i < types.size(); i++) {
				SortedSet<Integer> neighbours = new TreeSet<Integer>(
						// graph.getInvertedNeighbours(instance.peek(), types.get(i)));
						graph.getNeighbours(instance.peek(), types.get(i)));
				neighbours.retainAll(current);
				if (!neighbours.isEmpty()) {
					List<SortedSet<Integer>> newkey = deepCopy(key);
					newkey.get(i).add(instance.peek());
					// SortedMap<U, SortedSet<Integer>> newkey = deepCopy(key);
					// addNode(newkey, instance.peek(), types.get(i));
					alternateCommons.put(newkey, neighbours.size());
					recursiveCommons(newkey, neighbours, instance, i);
				}
			}
		if (instance.size() != order) {
			int start = instance.isEmpty() ? 0 : instance.peek() + 1;
			for (int i = start; i < graphorder; i++) {
				for (int j = 0; j < types.size(); j++) {
					// SortedSet<Integer> neighbours = new
					// TreeSet<Integer>(graph.getInvertedNeighbours(i, types.get(j)));
					SortedSet<Integer> neighbours = new TreeSet<Integer>(graph.getNeighbours(i, types.get(j)));
					if (!instance.isEmpty())
						neighbours.retainAll(current);
					if (!neighbours.isEmpty()) {
						// System.out.println(key);
						List<SortedSet<Integer>> newkey = deepCopy(key);
						// System.out.println(newkey);
						// System.out.println(i+" "+j);
						newkey.get(j).add(i);
						// SortedMap<U, SortedSet<Integer>> newkey = deepCopy(key);
						// addNode(newkey, i, types.get(j));
						alternateCommons.put(newkey, neighbours.size());
						instance.push(i);
						recursiveCommons(newkey, neighbours, instance, j);
						instance.pop();
					}
				}
			}
		}
	}

	
}