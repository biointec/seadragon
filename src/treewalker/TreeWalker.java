package treewalker;

import java.io.PrintStream;
import java.util.NavigableMap;
import java.util.SortedMap;
import java.util.Stack;
import java.util.TreeMap;

import graphlets.AbstractGraph;
import graphlets.AbstractGraphlet;
import graphlets.CanonicalComparator;
import tree.AddEdgeNode;
import tree.AddNodeNode;
import tree.ConditionNode;
import tree.GraphletTree;
import tree.TreeNode;

/**
 * 
 * @author Ine Melckenbeeck
 *
 * @param <T>
 * @param <U>
 */
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

	public void run(PrintStream ps) {
		for (int node = 0; node < graph.getOrder(); node++) {
			run(node);
//			ps.println();
			if (tree.isOrbitRep()) {
				ps.println(exportResults());
//				solve();
//				ps.println(printResults());
//				reset();
				// results = new TreeMap<>(new StringComparator());
			}
		}
		if (!tree.isOrbitRep()) {
			ps.println(exportResults());
//			solve();
//			ps.println(printResults());
//			reset();
		}
	}
	
	public NavigableMap<String,Long> exportResults(){
		solve();
		NavigableMap<String,Long> result= results;
		reset();
		return result;
	}
	

	public /*NavigableMap<String, Long>*/void run(int node) {
		instance.push(node);
		action(tree.getRoot());
		instance.pop();
//		return exportResults();
//		solve();
//		return results;
	}

	protected void solve() {
//		System.out.println(results);
		for (String s : results.keySet()) {
			if (factors.containsKey(s) && factors.get(s) != 1) {
				results.put(s, results.get(s) / factors.get(s));
			}
		}
	}

	public void reset() {
		instance = new Stack<>();
		results = new TreeMap<>(new CanonicalComparator());
	}

	public String printResults() {
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
		if (currentNode instanceof AddNodeNode) {
			addNodeAction((AddNodeNode<T, U>) currentNode);
		} else if (currentNode instanceof AddEdgeNode) {
			addEdgeAction((AddEdgeNode<T, U>) currentNode);
		} else if (currentNode instanceof ConditionNode) {
			conditionAction((ConditionNode<T, U>) currentNode);
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

	protected void register(AddNodeNode<T, U> treeNode) {
		String canonical = treeNode.getRepresentation();
		try {
			results.put(canonical, results.get(canonical) + 1);
		} catch (NullPointerException e) {
			factors.put(canonical, treeNode.getSymmetryFactor());
			results.put(canonical, 1L);
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
