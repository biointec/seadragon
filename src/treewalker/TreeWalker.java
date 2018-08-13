package treewalker;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.SortedMap;
import java.util.Stack;
import java.util.TreeMap;

import diGraphlet.DiGraph;
import diGraphlet.DiGraphlet;
import genGraphlet.GenGraph;
import genGraphlet.GenGraphlet;
import graphlets.AbstractGraphlet;
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
	protected SortedMap<String, Long> results;
	protected AbstractGraph<U> graph;
	protected SortedMap<String, Integer> factors;
	// protected boolean orbitrep;
	// private TreeNode<T, U> currentNode;

	protected class StringComparator implements Comparator<String> {

		@Override
		public int compare(String o1, String o2) {
			if (o1.length() != o2.length()) {
				return o1.length() - o2.length();
			} else {
				return o1.compareTo(o2);
			}
		}

	}

	public TreeWalker(GraphletTree<T, U> tree, AbstractGraph<U> graph) {
		this.tree = tree;
		instance = new Stack<>();
		results = new TreeMap<>(new StringComparator());
		this.graph = graph;
		factors = new TreeMap<>();
	}

	public void run(PrintStream ps) {
		
//		{	int node =0;
//		tree.print();
		for (int node = 0; node < graph.getOrder(); node++) {
			instance.push(node);
			action(tree.getRoot());
			instance.pop();
			if (tree.isOrbitRep()) {
				ps.println(printResults());
				results = new TreeMap<>(new StringComparator());
			}
		}
		if (!tree.isOrbitRep()) {
			ps.println(printResults());
		}
	}
	

	public static void main(String[] args) {
		// Graph graph = readGraph("test/randomgraph-100-1000.txt");
//		DiGraph graph = DiGraph.readGraph("test/test.txt");
//		System.out.println(graph);
//		System.out.println();
//		GraphletTree<DiGraphlet, Boolean> tree = new TreeGenerator<DiGraphlet, Boolean>(new DiGraphlet("", true), 5)
//				.generateTree();
//		tree.print();
//		TreeWalker<DiGraphlet, Boolean> walker = new TreeWalker<>(tree, graph);
//		walker.run(System.out);
		GenGraph graph = GenGraph.readGraph("test/test.txt");
		System.out.println(graph);
		GraphletTree<GenGraphlet, Byte> tree = new TreeGenerator<>(new GenGraphlet("", true), 5)
				.generateTree();
		tree.print();
		TreeWalker<GenGraphlet, Byte> walker = new TreeWalker<>(tree, graph);
		walker.run(System.out);
	}

	protected String printResults() {
		StringBuilder sb = new StringBuilder();
		for (String s : results.keySet()) {
			sb.append(s);
			sb.append(":");
			// sb.append(results.get(s));
			sb.append(results.get(s) / factors.get(s));
			sb.append(", ");
		}
		return sb.substring(0, sb.length() - 2);
	}

	protected void action(TreeNode<T, U> currentNode) {
		if (currentNode instanceof AddNodeNode) {
			addNodeAction((AddNodeNode<T, U>) currentNode);
		} else if (currentNode instanceof AddEdgeNode) {
			addEdgeAction((AddEdgeNode<T, U>) currentNode);
		} else if (currentNode instanceof ConditionNode) {
			conditionAction((ConditionNode<T, U>) currentNode);
		}
	}

	protected void addNodeAction(AddNodeNode<T, U> treeNode) {
		String canonical = treeNode.getRepresentation();
		try {
			results.put(canonical, results.get(canonical) + 1);
		} catch (NullPointerException e) {
			factors.put(canonical, treeNode.getSymmetryFactor());
			results.put(canonical, 1L);
		}
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
