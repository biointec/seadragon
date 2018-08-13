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
import graphlets.AbstractGraphlet;
import graphs.IllegalGraphActionException;

public class EquationGenerator<T extends AbstractGraphlet<U>, U extends Comparable<U>> {

	List<T> start;
	SortedSet<Equation<T, U>> result;

	public EquationGenerator() {
		// TODO Auto-generated constructor stub
	}

	public SortedSet<Equation<T, U>> generateEquations(int order) {
		result = new TreeSet<>();
		for (T graphlet : start) {
			List<Set<Map<U, SortedSet<Integer>>>> rhses = rhsTerms(graphlet);
			for (Set<Map<U, SortedSet<Integer>>> rhs : rhses) {
				SortedSet<T> lhsTerms = lhsTerms(graphlet, rhs);
				SortedMap<T, Integer> lhs = new TreeMap<>();
				for (T term : lhsTerms) {
					lhs.put(term, lhsFactor(term, rhs.iterator().next()));
				}
				int minus = minus(graphlet, rhs.iterator().next());
				// System.out.println(lhs);
				// System.out.println(graphlet);
				// System.out.println(rhs);
				// System.out.println(minus);
				Equation<T, U> equation = new Equation<T, U>(lhs, graphlet, rhs, minus);
				// System.out.println(equation);
				// System.out.println();
				result.add(equation);
			}
		}
		return result;
	}

	public int lhsFactor(T newGraphlet, Map<U, SortedSet<Integer>> edges) {
		int factor = newGraphlet.getOrbitOf(newGraphlet.getOrder() - 1).size();
		List<Integer> neighbours = new ArrayList<>();
		for (U type : edges.keySet()) {
			for (int i : edges.get(type)) {
				neighbours.add(i);
			}
		}
		factor *= newGraphlet.getOrbitOf(neighbours).size();
		return factor;
	}

	public SortedSet<T> lhsTerms(T graphlet, Set<Map<U, SortedSet<Integer>>> edges) {
		Map<U, SortedSet<Integer>> rhsTerm = edges.iterator().next();
		T copy = graphlet.copy();
		copy.addNode();
		for (U key : rhsTerm.keySet()) {
			for (int i : rhsTerm.get(key)) {
				try {
					copy.addEdge(graphlet.getOrder(), i, key);
				} catch (IllegalGraphActionException e) {
					e.printStackTrace();
				}
			}
		}
		SortedSet<T> lhs = new TreeSet<>();
		List<SortedSet<U>> edgeTypes = copy.validEdges();
		int[] counter = new int[graphlet.getOrder() + 1];
		while (counter[0] == 0) {
			try {
				T newCopy = copy.copy();
				for (int j = 0; j < graphlet.getOrder(); j++) {
					if (counter[j + 1] != 0) {
						SortedSet<U> edgelist = edgeTypes.get(counter[j + 1] - 1);
						for (U edge : edgelist) {
							newCopy.addEdge(graphlet.getOrder(), j, edge);
						}
					}
				}
				newCopy.permute();
				lhs.add(newCopy);
			} catch (IllegalGraphActionException e) {
			}

			int i = graphlet.getOrder();
			while (counter[i] == edgeTypes.size()) {
				counter[i--] = 0;
			}
			counter[i]++;
		}
		return lhs;

	}

	public int minus(T graphlet, Map<U, SortedSet<Integer>> edges) {
		SortedSet<Integer> result = new TreeSet<>();
		for (int j = 0; j < graphlet.getOrder(); j++) {
			result.add(j);
		}
		for (U type : edges.keySet()) {
			for (int node : edges.get(type)) {
				result.retainAll(graphlet.getNeighbours(node, type));
			}
		}
		return result.size();
	}

	public List<Set<Map<U, SortedSet<Integer>>>> rhsTerms(T graphlet) {

		List<SortedSet<U>> edges = graphlet.validEdges();
		int[] counter = new int[graphlet.getOrder() + 1];
		counter[graphlet.getOrder()] = 1;
		List<Set<Map<U, SortedSet<Integer>>>> equations = new ArrayList<>();
		while (counter[0] == 0) {
			List<Integer> orbitcheck = new ArrayList<>();
			List<Integer> edgetype = new ArrayList<>();
			for (int j = 0; j < graphlet.getOrder(); j++) {
				if (counter[j + 1] != 0) {
					orbitcheck.add(j);
					edgetype.add(counter[j + 1] - 1);
				}
			}
			Set<List<Integer>> orbit = graphlet.getOrbitOf(orbitcheck);
			Set<Map<U, SortedSet<Integer>>> rhs = new HashSet<>();
			for (List<Integer> swap : orbit) {
				Map<U, SortedSet<Integer>> commons = new TreeMap<>();
				for (U condition : graphlet.edgeTypes()) {
					commons.put(condition, new TreeSet<>());
				}
				for (int i = 0; i < orbitcheck.size(); i++) {
					for (U thingy : edges.get(edgetype.get(i))) {
						commons.get(thingy).add(swap.get(i));
					}
				}
				rhs.add(commons);
			}
			equations.add(rhs);
			int i = graphlet.getOrder();
			while (counter[i] == edges.size()) {
				counter[i--] = 0;
			}
			counter[i]++;
		}
		return equations;
	}

	public static void main(String[] args) throws IllegalGraphActionException {
		EquationGenerator<DiGraphlet, Boolean> eg = new EquationGenerator<>();
		eg.start = new ArrayList<>();
		DiGraphlet dg = new DiGraphlet("11", true);
		dg.permute();
		eg.start.add(dg);
		dg = new DiGraphlet("10", true);
		dg.permute();
		eg.start.add(dg);
		dg = new DiGraphlet("01", true);
		dg.permute();
		eg.start.add(dg);
		for (Equation<DiGraphlet, Boolean> e : eg.generateEquations(3)) {
			System.out.println(e);
		}
		// List<Set<Map<Boolean, SortedSet<Integer>>>> rhs = eg.rhsTerms(dg);
		// System.out.println(rhs);
		// SortedSet<DiGraphlet>lhs = eg.lhsTerms(dg, rhs.get(0));
		// for(DiGraphlet d:lhs) {
		// System.out.println(eg.lhsFactor(d,rhs.get(0).iterator().next()));
		// }

	}

}
