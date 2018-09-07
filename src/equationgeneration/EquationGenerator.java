package equationgeneration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import diGraphlet.DiGraphlet;
import diGraphlet.DiGraphletFactory;
import graph.Graphlet;
import graphletgeneration.AbstractGraphletFactory;
import graphletgeneration.GraphletGenerator;
import graphlets.AbstractGraphlet;
import graphlets.CanonicalComparator;
import graphs.IllegalGraphActionException;
import tree.GraphletTree;
import tree.TreeGenerator;
import treewalker.SingleGraphletWalker;
import treewalker.TreeWalker;

public class EquationGenerator<T extends AbstractGraphlet<U>, U extends Comparable<U>> {

	List<T> start;
	SortedSet<Equation<T, U>> result;
	List<U> edges;
	List<SortedSet<Integer>> edgeCombinations;
	AbstractGraphletFactory<T> factory;
	GraphletTree<T, U> tree;

	public EquationGenerator(List<T> start) {
		this.start = start;
		edges = new ArrayList<>(start.get(0).edgeTypes());
		edgeCombinations = new ArrayList<>();
		for (SortedSet<U> s : start.get(0).validEdges()) {
			SortedSet<Integer> t = new TreeSet<>();
			for (U u : s) {
				t.add(edges.indexOf(u));
			}
			edgeCombinations.add(t);
		}
	}

	public EquationGenerator(AbstractGraphletFactory<T> type, int order) {
		this(GraphletGenerator.generateGraphlets(order - 1, type));
		factory = type;
		tree = new TreeGenerator<>(type, order - 1).generateTree();
	}

	public SortedSet<Equation<T, U>> generateEquations() {
		result = new TreeSet<>();
		for (T graphlet : start) {
			Set<Set<List<Set<Integer>>>> rhses = rhsTerms(graphlet);
			for (Set<List<Set<Integer>>> rhs : rhses) {
				int minus = minus(graphlet, rhs.iterator().next());
				SortedMap<String, Integer> lhs = lhsTerms(graphlet, rhs, minus);
				if (lhs!=null) {
					Equation<T, U> equation = new Equation<T, U>(lhs, graphlet, rhs, minus);
					result.add(equation);
				}
			}
		}
		return result;
	}


	private SortedMap<String, Integer> lhsTerms(T graphlet, Set<List<Set<Integer>>> edges, int minus) {
		SortedMap<String, Integer> lhs = new TreeMap<>(new CanonicalComparator());
		T oldgraphlet = factory.canonicalVersion(graphlet);
		assert (graphlet.representation().equals(oldgraphlet.representation()));
		graphlet.addNode();
		List<Set<Integer>> rhsTerm = edges.iterator().next();
		for (int i = 0; i < this.edges.size(); i++) {
			for (int j : rhsTerm.get(i)) {
				try {
					graphlet.addEdge(j, graphlet.getOrder() - 1, this.edges.get(i));
				} catch (IllegalGraphActionException e) {
					e.printStackTrace();
				}
			}
		}
		if (graphlet.isComplete()) {
			try {
				graphlet.removeNode(graphlet.getOrder()-1);
			} catch (IllegalGraphActionException e) {
				e.printStackTrace();
			}
			return null;
		} else {
			int[] counter = new int[graphlet.getOrder()];
			while (counter[0] == 0) {
				try {
					T copy = factory.copy(graphlet);
					for (int j = 1; j < copy.getOrder(); j++) {
						if (counter[j] != 0) {
							SortedSet<Integer> edgelist = edgeCombinations.get(counter[j] - 1);
							for (int edge : edgelist) {
								copy.addEdge(j - 1, copy.getOrder() - 1, this.edges.get(edge));
							}
						}
					}
					copy = factory.canonicalVersion(copy);
					SortedMap<String, Long> run = new SingleGraphletWalker<>(tree, copy, oldgraphlet, edges, minus)
							.run(0);
					Long long1 = run.get("");
					assert (long1 != null);
					lhs.put(copy.canonical(), Math.toIntExact(long1));
				} catch (IllegalGraphActionException e) {
				}
				int i = graphlet.getOrder() - 1;
				while (counter[i] == this.edgeCombinations.size()) {
					counter[i--] = 0;
				}
				counter[i]++;
			}
			try {
				graphlet.removeNode(graphlet.getOrder() - 1);
				assert (graphlet.representation().equals(oldgraphlet.representation()));
			} catch (IllegalGraphActionException e) {
			}
			return lhs;
		}
	}

	private int minus(T graphlet, List<Set<Integer>> edges) {
		SortedSet<Integer> result = new TreeSet<>();
		for (int j = 0; j < graphlet.getOrder(); j++) {
			result.add(j);
		}
		for (int i = 0; i < edges.size(); i++) {
			for (int node : edges.get(i)) {
				result.retainAll(graphlet.getNeighbours(node, this.edges.get(i)));
			}
		}
		return result.size();
	}

	private Set<Set<List<Set<Integer>>>> rhsTerms(T graphlet) {
		int[] counter = new int[graphlet.getOrder()];
		int ticktock = 1;
		counter[counter.length - 1] = 1;
		Set<Set<List<Set<Integer>>>> rhses = new HashSet<>();
		while (ticktock < Math.pow(edgeCombinations.size() + 1, graphlet.getOrder()) - 1) {
			ticktock++;
			List<Set<Integer>> orbitcheck = new ArrayList<>();
			for (int j = 0; j < edges.size(); j++) {
				orbitcheck.add(new TreeSet<>());
			}
			for (int j = 0; j < counter.length; j++) {
				if (counter[j] != 0) {
					for (int k : edgeCombinations.get(counter[j] - 1)) {
						orbitcheck.get(k).add(j);
					}
				}
			}
			Set<List<Set<Integer>>> rhs = graphlet.getOrbitOf(orbitcheck);
			rhses.add(rhs);
			int i = counter.length - 1;
			while (counter[i] == edgeCombinations.size()) {
				counter[i--] = 0;
			}
			counter[i]++;
		}
		return rhses;
	}
}
