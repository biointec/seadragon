package treewalker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import equationgeneration.CommonsCounter;
import equationgeneration.Equation;
import equationgeneration.EquationSelecter;
import graphlets.AbstractGraphlet;
import graphlets.CanonicalComparator;
import graphs.AbstractGraph;
import tree.AddNodeNode;
import tree.ConditionNode;
import tree.GraphletTree;

public class EquationWalker<T extends AbstractGraphlet<U>, U extends Comparable<U>>
		extends TreeWalker<T, U> {

	private SortedSet<Equation<T, U>> equations;
	private SortedMap<String, List<Equation<T, U>>> equationsByRHS;
	private SortedSet<String> largerGraphlets;
	SortedMap<String, Integer> minus;
	private SortedMap<String, List<String>> lhs;
	private SortedMap<String, List<Integer>> lhsFactors;
	private SortedMap<String, Map<List<Set<Integer>>, String>> rhs;
	private CommonsCounter<AbstractGraph<U>, U> commons;
	private boolean saving = true;

	public EquationWalker(GraphletTree<T, U> tree, AbstractGraph<U> graph, SortedSet<Equation<T, U>> equations) {
		super(tree, graph);
		this.equations = EquationSelecter.selectEquations(equations);
		analyseEquations();
		commons = new CommonsCounter<>(graph, tree.getOrder() + 1);
		commons.recursiveCommons();
	}
	
	private void analyseTree() {
		rhs = new TreeMap<>();
		for (AddNodeNode<T, U> leaf : tree.getLeaves()) {
			String rhsGraphlet = leaf.getRepresentation();
			List<Integer> permutation = leaf.getCanonicalAutomorphism();
			List<Integer> inversePermutation = new ArrayList<>();
			for (int i = 0; i < permutation.size(); i++) {
				inversePermutation.add(permutation.indexOf(i));
			}
			if (equationsByRHS.containsKey(rhsGraphlet)) {
				Map<List<Set<Integer>>, String> commons = new HashMap<>();
				for (Equation<T, U> equation : equationsByRHS.get(rhsGraphlet)) {
					for (List<Set<Integer>> term : equation.getCommons()) {
						List<Set<Integer>> translatedTerm = new ArrayList<>();
						for (Set<Integer> partTerm : term) {
							Set<Integer> translatedPart = new TreeSet<>();
							for (int i : partTerm) {
								translatedPart.add(permutation.get(i));
							}
							translatedTerm.add(translatedPart);
						}
						commons.put(translatedTerm, equation.getLhs().firstKey());
					}
				}
				rhs.put(rhsGraphlet, commons);
			}
		}
	}

	private void analyseEquations() {
		largerGraphlets = new TreeSet<>(Collections.reverseOrder(new CanonicalComparator()));
		equationsByRHS = new TreeMap<>();
		lhs = new TreeMap<>();
		lhsFactors = new TreeMap<>();
		minus = new TreeMap<>();
		for (Equation<T, U> equation : equations) {
			Iterator<String> lhsIterator = equation.getLhs().keySet().iterator();
			String lhskey = lhsIterator.next();
			List<String> terms = new ArrayList<>();
			List<Integer> factors = new ArrayList<>();
			factors.add(equation.getLhs().get(lhskey));
			while (lhsIterator.hasNext()) {
				String graphlet = lhsIterator.next();
				terms.add(graphlet);
				factors.add(equation.getLhs().get(graphlet));
			}
			lhs.put(lhskey, terms);
			lhsFactors.put(lhskey, factors);
			minus.put(lhskey, equation.getMinus());
			String key = equation.getRhsGraphlet();
			List<Equation<T, U>> thingy = equationsByRHS.get(key);
			if (thingy == null) {
				thingy = new ArrayList<>();
				equationsByRHS.put(key, thingy);
			}
			thingy.add(equation);
			largerGraphlets.add(lhskey);
		}
		analyseTree();
	}

	public void reset() {
		saving = true;
		super.reset();
	}

	protected void conditionAction(ConditionNode<T, U> treenode) {
		if (instance.get(treenode.getFirst()) < instance.get(treenode.getSecond())) {
			saving = false;
		}
		action(treenode.getChild());
	}

	protected void register(AddNodeNode<T, U> treeNode) {
		String canonical = treeNode.getRepresentation();
		if (saving) {
			super.register(treeNode);
			if (equationsByRHS.containsKey(canonical)) {
				for (List<Set<Integer>> l : rhs.get(canonical).keySet()) {
					List<SortedSet<Integer>> translation = new ArrayList<>();
					for (Set<Integer> term : l) {
						SortedSet<Integer> translatedTerm = new TreeSet<>();
						for (int j : term) {
							translatedTerm.add(instance.get(j));
						}
						translation.add(translatedTerm);
					}
					String index = rhs.get(canonical).get(l);
					long plus = (long) (commons.getCommonNeighbours(translation) - minus.get(index));
					if (plus != 0) {
						try {
							results.put(index, results.get(index) + plus);
						} catch (NullPointerException e) {
							results.put(index, plus);
						}
					}
				}
			}
		} else {
			saving = true;
		}
	}

	protected void solve() {
		for (String s : largerGraphlets) {
			if (results.containsKey(s)) {
				long a = results.get(s);
				for (int i = 0; i < lhs.get(s).size(); i++) {
					if (results.containsKey(lhs.get(s).get(i))) {
						if (lhsFactors.containsKey(s)) {
							a -= results.get(lhs.get(s).get(i)) * lhsFactors.get(s).get(i + 1);
						} else {
							a -= results.get(lhs.get(s).get(i));
						}
					}
				}
				if (a != 0) {
					a /= lhsFactors.get(s).get(0);
					results.put(s, a);
				} else {
					results.remove(s);
				}
			}
		}
		for (String s : results.keySet()) {
			if (factors.containsKey(s) && factors.get(s) != 1) {
				results.put(s, results.get(s) / factors.get(s));
			}
		}
	}
}
