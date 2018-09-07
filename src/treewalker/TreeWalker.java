package treewalker;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.NavigableMap;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.Stack;
import java.util.TreeMap;

import diGraphlet.DiGraph;
import diGraphlet.DiGraphlet;
import diGraphlet.DiGraphletFactory;
import equationgeneration.Equation;
import equationgeneration.EquationGenerator;
import genGraphlet.GenGraph;
import genGraphlet.GenGraphlet;
import genGraphlet.GenGraphletFactory;
import graph.Graph;
import graphlets.AbstractGraphlet;
import graphlets.CanonicalComparator;
import graphs.AbstractGraph;
import tree.AddEdgeNode;
import tree.AddNodeNode;
import tree.ConditionNode;
import tree.GraphletTree;
import tree.TreeGenerator;
import tree.TreeNode;

public class TreeWalker<T extends AbstractGraphlet<U>, U extends Comparable<U>> {

	protected GraphletTree<T, U> tree;
	protected Stack<Integer> instance;
	protected NavigableMap<String, Long> results;
	protected AbstractGraph<U> graph;
	protected SortedMap<String, Integer> factors;

	public TreeWalker(GraphletTree<T, U> tree, AbstractGraph<U> graph) {
		this.tree = tree;
		instance = new Stack<>();
		results = new TreeMap<>(new CanonicalComparator());
		this.graph = graph;
		factors = new TreeMap<>();
	}

	public NavigableMap<String, Long> run(int node) {
		instance.push(node);
		action(tree.getRoot());
		instance.pop();
		solve();
		return results;
	}

	public void reset() {
		instance = new Stack<>();
		results = new TreeMap<>(new CanonicalComparator());
	}

	public void run(PrintStream ps) {
		for (int node = 0; node < graph.getOrder(); node++) {
			run(node);
			if (tree.isOrbitRep()) {
				// solve();
				ps.println(printResults());
				reset();
				// results = new TreeMap<>(new StringComparator());
			}
		}
		if (!tree.isOrbitRep()) {
			solve();
			ps.println(printResults());
		}
	}

	// public static void main(String[] args) {
	// GenGraphletFactory gf = new GenGraphletFactory(true);
	// int order = 2;
	// GraphletTree<GenGraphlet, Byte> tree = new TreeGenerator<>(gf,
	// order).generateTree();
	// GenGraph graph = GenGraph.readGraph("test/test.txt");
	// tree.print();
	// TreeWalker<GenGraphlet, Byte> ew = new TreeWalker<>(tree, graph);
	// ew.run(System.out);
	// }

	protected void solve() {
		for (String s : results.keySet()) {
			if (factors.containsKey(s) && factors.get(s) != 1) {
				results.put(s, results.get(s) / factors.get(s));
			}
		}
	}

	protected String printResults() {
		StringBuilder sb = new StringBuilder();
		if (results.isEmpty())
			return "";
		for (String s : results.keySet()) {
			sb.append(s);
			sb.append(":");
			// sb.append(results.get(s));
			sb.append(results.get(s));
			sb.append(", ");
		}
		return sb.substring(0, sb.length() - 2);
	}

	protected void action(TreeNode<T, U> currentNode) {
		// System.out.println(currentNode);
		// System.out.println(instance);
		if (currentNode instanceof AddNodeNode) {
			addNodeAction((AddNodeNode<T, U>) currentNode);
		} else if (currentNode instanceof AddEdgeNode) {
			addEdgeAction((AddEdgeNode<T, U>) currentNode);
		} else if (currentNode instanceof ConditionNode) {
			conditionAction((ConditionNode<T, U>) currentNode);
		}
	}

	protected void register(AddNodeNode<T, U> treeNode) {
		String canonical = treeNode.getRepresentation();
		try {
			results.put(canonical, results.get(canonical) + 1);
		} catch (NullPointerException e) {
			factors.put(canonical, treeNode.getSymmetryFactor());
			results.put(canonical, 1L);
		}
	}

	protected void addNodeAction(AddNodeNode<T, U> treeNode) {
		register(treeNode);
		for (int i : treeNode.getChildrenMap().keySet()) {
			for (U type : treeNode.getChildrenMap().get(i).keySet()) {
				for (int graphnode : graph.getNeighbours(instance.get(i), type)) {
					if (!instance.contains(graphnode)) {
						instance.push(graphnode);
						action(treeNode.getChildrenMap().get(i).get(type));
						instance.pop();
					}
				}
			}
		}
	}

	protected void addEdgeAction(AddEdgeNode<T, U> treenode) {
		int graphletnode = treenode.getNode();
		U edgetype = treenode.getType();
		TreeNode<T, U> currentNode = treenode
				.getChild(graph.getNeighbours(instance.get(graphletnode), edgetype).contains(instance.peek()));
		if (currentNode != null) {
			action(currentNode);
		}
	}

	protected void conditionAction(ConditionNode<T, U> treenode) {
		if (instance.get(treenode.getFirst()) < instance.get(treenode.getSecond())) {
			action(treenode.getChild());
		}
	}
}
