package treewalker;

import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.Stack;
import java.util.TreeMap;
import java.util.TreeSet;

import diGraphlet.DiGraphlet;
import diGraphlet.DiGraphletFactory;
import equationgeneration.CommonsCounter;
import equationgeneration.Equation;
import graph.Graph;
import graph.Graphlet;
import graph.GraphletFactory;
import graphlets.AbstractGraphlet;
import graphs.AbstractGraph;
import tree.AddEdgeNode;
import tree.AddNodeNode;
import tree.ConditionNode;
import tree.GraphletTree;
import tree.TreeGenerator;
import tree.TreeNode;

public class SingleGraphletWalker<T extends AbstractGraphlet<U>, U extends Comparable<U>> extends TreeWalker<T, U> {

	private T graphlet;
	private List<String> path;
	private int index = 0;
	Set<List<Set<Integer>>> neighbours;
	int minus;

	private CommonsCounter<AbstractGraph<U>, U> commons;

	
	protected void solve() {
		for (String s : results.keySet()) {
			if (factors.containsKey(s) && factors.get(s) != 1) {
				results.put(s, results.get(s) / factors.get(s));
			}
		}
	}
	
	public SingleGraphletWalker(GraphletTree<T, U> tree, AbstractGraph<U> graph, T graphlet,
			Set<List<Set<Integer>>> neighbours, int minus) {
		super(tree, graph);
		this.graphlet = graphlet;
		this.neighbours = neighbours;
		analyseTree();
		commons = new CommonsCounter<>(graph, tree.getOrder() + 1);
		commons.recursiveCommons();
		this.minus = minus;
	}

	private void analyseTree() {
		path = new LinkedList<>();
		TreeNode<T, U> current;
		Deque<TreeNode<T, U>> queue = new LinkedList<>();
		queue.add(tree.getRoot());
		AddNodeNode<T, U> leaf = null;
		while (!queue.isEmpty()) {
			current = queue.pop();
			if (current instanceof AddNodeNode && current.getRepresentation().equals(graphlet.canonical())) {
				leaf = (AddNodeNode<T, U>) current;
				while (current != null) {
					path.add(0, current.getRepresentation());
					current = current.getParent();
				}
				break;
			} else if (current.getRepresentation().length() <= graphlet.canonical().length()) {
				for (TreeNode<T, U> tn : current.getChildren()) {
					queue.addFirst(tn);
				}
			}
		}
		List<Integer> permutation = leaf.getCanonicalAutomorphism();
		List<Integer> inversePermutation = new ArrayList<>();
		for (int i = 0; i < permutation.size(); i++) {
			inversePermutation.add(permutation.indexOf(i));
		}
		Set<List<Set<Integer>>> translation = new HashSet<>();
		for (List<Set<Integer>> l : neighbours) {
			List<Set<Integer>> translatedTerm = new ArrayList<>();
			for (Set<Integer> partTerm : l) {
				Set<Integer> translatedPart = new TreeSet<>();
				for (int i : partTerm) {
					translatedPart.add(permutation.get(i));
				}
				translatedTerm.add(translatedPart);
			}
			translation.add(translatedTerm);
		}
		neighbours = translation;
	}

	protected void register(AddNodeNode<T, U> treeNode) {
		String canonical = treeNode.getRepresentation();
		if (canonical.equals(graphlet.canonical())) {
			try {
				results.put(canonical, results.get(canonical) + 1);
			} catch (NullPointerException e) {
				results.put(canonical, 1L);
			}
			for (List<Set<Integer>> terms : neighbours) {
				List<SortedSet<Integer>> translation = new ArrayList<>();
				for (Set<Integer> term : terms) {
					SortedSet<Integer> translatedTerm = new TreeSet<>();
					for (int j : term) {
						translatedTerm.add(instance.get(j));
					}
					translation.add(translatedTerm);
				}
				String index = "";
				long plus = (long) (commons.getCommonNeighbours(translation) - minus);
				if (plus != 0) {
					try {
						results.put(index, results.get(index) + plus);
					} catch (NullPointerException e) {
						results.put(index, plus);
					}
				}
			}
		}
	}

	protected void action(TreeNode<T, U> currentNode) {
		if (index < path.size() && currentNode.getRepresentation().equals(path.get(index))) {
			index++;
			if (currentNode instanceof AddNodeNode) {
				addNodeAction((AddNodeNode<T, U>) currentNode);
			} else if (currentNode instanceof AddEdgeNode) {
				addEdgeAction((AddEdgeNode<T, U>) currentNode);
			} else if (currentNode instanceof ConditionNode) {
				conditionAction((ConditionNode<T, U>) currentNode);
			}
			index--;
		}

	}
}
