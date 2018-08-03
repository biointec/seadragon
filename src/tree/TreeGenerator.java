package tree;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.Set;
import java.util.SortedSet;
import java.util.Stack;
import java.util.TreeSet;

import diGraphlet.DiGraphlet;
import graphlet.Graphlet;
import graphlets.AbstractGraphlet;
import graphlets.IllegalGraphActionException;
import graphlets.ListComparator;

public class TreeGenerator<T extends AbstractGraphlet<U>, U extends Comparable<U>> {

	private GraphletTree<T, U> tree;
	private List<U> edgeTypes;
	private Stack<AddNodeNode<T, U>> currentNodes;
	private Stack<AddEdgeNode<T, U>> currentEdges;
	private Stack<T> graphlets;
	private int order;
	private SortedSet<String> usedGraphlets;
	private Set<TreeNode<T, U>> toPrune;

	public TreeGenerator(T root, int order) {
		tree = new GraphletTree<T, U>(root);
		AddNodeNode<T, U> rootNode = tree.getRoot();
		currentNodes = new Stack<>();
		currentNodes.add(rootNode);
		currentEdges = new Stack<>();
		graphlets = new Stack<T>();
		graphlets.push(root);
		edgeTypes = tree.getEdgeTypes();
		usedGraphlets = new TreeSet<>();
		this.order = order;
		toPrune = new HashSet<>();
	}

	public GraphletTree<T, U> generateTree() {
		expandNode();
		for (TreeNode<T, U> tn : toPrune) {
			tn.prune();
		}
		return tree;
	}

	public static void main(String[] args) {
		TreeGenerator<Graphlet, Boolean> tn = new TreeGenerator<>(new Graphlet("", true),6);
		System.out.println("generating...");
		GraphletTree<Graphlet,Boolean> gt = tn.generateTree();
		gt.print();
		System.out.println(gt.getLeaves().size());
		System.out.println(gt.getRoot().numberOfNodes()-2);
	}

	private void breakSymmetry() {
		List<SortedSet<Integer>> cosetreps = graphlets.peek().cosetreps();
		for (int i = 0; i < order; i++) {
			for (int j : cosetreps.get(i)) {
				if (i != j) {
					ConditionNode<T, U> condition = new ConditionNode<T, U>(currentNodes.peek().parent, i, j);
					condition.setChild(currentNodes.peek());
					currentNodes.peek().parent.replaceChild(currentNodes.peek(), condition);
					currentNodes.peek().parent = condition;
				}
			}
		}
	}

	private void expandNode() {
		try {
			T graphlet = graphlets.peek().copy();
			if (!usedGraphlets.add(graphlet.canonical())) {
				toPrune.add(currentNodes.peek());
				return;
			}
			if (graphlet.getOrder() == order) {
//				breakSymmetry();
				tree.addLeaf(currentNodes.peek());
				return;
			}
			graphlet.addNode();
			for (SortedSet<Integer> orbit : graphlet.getOrbits()) {
				int i = orbit.first();
				if (i != graphlet.getOrder() - 1) {
					graphlets.push(graphlet);
					for (int j = 0; j < edgeTypes.size(); j++) {
						U edge = edgeTypes.get(j);
						graphlet.addEdge(graphlet.getOrder() - 1, i, edge);
						expandEdges(i, j);
						graphlet.removeEdge(graphlet.getOrder() - 1, i);
					}
					graphlets.pop();
				}
			}
		} catch (IllegalGraphActionException e) {
			e.printStackTrace();
		}
	}

	private void expandEdges(int currentGray, int currentColour) {
		if (currentColour > 0) {
			negativeEdge(currentGray, currentColour);
		} else if (currentGray > 0) {
			negativeEdge(currentGray, currentColour);
		} else if (edgeTypes.size() == 1 && graphlets.peek().getOrder() == 2) {
			AddNodeNode<T, U> child = new AddNodeNode<>(currentNodes.peek(), graphlets.peek().canonical());
			currentNodes.peek().addChild(0, edgeTypes.get(0), child);
			currentNodes.push(child);
			expandNode();
			currentNodes.pop();

		} else {
			currentEdges.push(new AddEdgeNode<T, U>(currentNodes.peek(), graphlets.peek().canonical(),
					edgeTypes.size() == 1 ? 1 : 0, edgeTypes.size() == 1 ? 0 : 1));
			currentNodes.peek().addChild(0, edgeTypes.get(0), currentEdges.peek());
			expandEdge();
			currentEdges.pop();
		}

	}

	private void negativeEdge(int lastGray, int colour) {
		T graphlet = graphlets.peek();
		currentEdges.push(new AddEdgeNode<T, U>(currentNodes.peek(), graphlet.canonical(), 0, 0));
		currentNodes.peek().addChild(lastGray, edgeTypes.get(colour), currentEdges.peek());
		for (int i = 1; i < lastGray * edgeTypes.size() + colour; i++) {
			AddEdgeNode<T, U> copy = currentEdges.pop();
			currentEdges.push(
					new AddEdgeNode<T, U>(copy, graphlet.canonical(), i / edgeTypes.size(), (i % edgeTypes.size())));
			copy.addChild(false, currentEdges.peek());
		}
		if (lastGray == graphlets.peek().getOrder() - 2 && colour == edgeTypes.size() - 1) {
			AddEdgeNode<T, U> copy = currentEdges.pop();
			currentNodes.push(new AddNodeNode<T, U>(copy, graphlet.canonical()));
			copy.addChild(false, currentNodes.peek());
			expandNode();
			currentNodes.pop();
		} else {
			AddEdgeNode<T, U> copy = currentEdges.pop();
			currentEdges.push(new AddEdgeNode<T, U>(copy, graphlet.canonical(),
					lastGray + (colour + 1) / edgeTypes.size(), ((colour + 1) % edgeTypes.size())));
			copy.addChild(false, currentEdges.peek());
			expandEdge();
			currentEdges.pop();
		}
	}

	private void expandEdge() {
//		if (currentEdges.peek().getNode() == graphlets.peek().getOrder() - 2
//				&& currentEdges.peek().getType() == (edgeTypes.size() - 1)) {

//			AddNodeNode<T, U> child = new AddNodeNode<T, U>(currentEdges.peek(), graphlets.peek().canonical());
//			currentEdges.peek().addChild(false, child);
//			currentNodes.push(child);
//			expandNode();
//			currentNodes.pop();
//			try {
//				T graphlet = graphlets.peek().copy();
//				graphlet.addEdge(graphlet.getOrder() - 1, graphlet.getOrder() - 2, edgeTypes.get(edgeTypes.size() - 1));
//				graphlets.push(graphlet);
//				child = new AddNodeNode<T, U>(currentEdges.peek(), graphlet.canonical());
//				currentEdges.peek().addChild(true, child);
//				currentNodes.push(child);
//				expandNode();
//				graphlets.pop();
//				currentNodes.pop();
//			} catch (IllegalGraphActionException e) {
//				// e.printStackTrace();
//			}
//		} else {
			int node = currentEdges.peek().getNode() + (currentEdges.peek().getType() + 1) / edgeTypes.size();
			int edge = (currentEdges.peek().getType() + 1) % edgeTypes.size();
			while(node<graphlets.peek().getOrder()-1) {
//			while (node < graphlets.peek().getOrder() - 2 || currentEdges.peek().getType() < (edgeTypes.size() - 1)) {
				try {
					T graphlet = graphlets.peek().copy();
					graphlet.addEdge(graphlet.getOrder() - 1, currentEdges.peek().getNode(),
							edgeTypes.get(currentEdges.peek().getType()));
					graphlets.push(graphlet);
					AddEdgeNode<T, U> child = new AddEdgeNode<T, U>(currentEdges.peek(), graphlet.canonical(), node,
							edge);
					currentEdges.peek().addChild(true, child);
					currentEdges.push(child);
					expandEdge();
					graphlets.pop();
					currentEdges.pop();
					child = new AddEdgeNode<T, U>(currentEdges.peek(), graphlets.peek().canonical(), node, edge);
					currentEdges.peek().addChild(false, child);
					currentEdges.push(child);
					expandEdge();
					currentEdges.pop();
					return;
				} catch (IllegalGraphActionException e) {
					node = node+ (edge + 1) / edgeTypes.size();
					edge = (edge + 1) % edgeTypes.size();
					System.out.println(node+" "+edge+ " "+edgeTypes.size());

				}
			}
//			System.out.println(node+" "+edge+ " "+edgeTypes.size());
//			System.out.println(currentEdges);
//			System.out.println(graphlets.peek().getOrder());
//			System.out.println(node+" "+edge+ " "+edgeTypes.size());
			AddNodeNode<T, U> child = new AddNodeNode<T, U>(currentEdges.peek(), graphlets.peek().canonical());
			currentEdges.peek().addChild(false, child);
			currentNodes.push(child);
			expandNode();
			currentNodes.pop();
			try {
				T graphlet = graphlets.peek().copy();
				graphlet.addEdge(graphlet.getOrder() - 1, graphlet.getOrder() - 2, edgeTypes.get(edgeTypes.size() - 1));
				graphlets.push(graphlet);
				child = new AddNodeNode<T, U>(currentEdges.peek(), graphlet.canonical());
				currentEdges.peek().addChild(true, child);
				currentNodes.push(child);
				expandNode();
				graphlets.pop();
				currentNodes.pop();
			} catch (IllegalGraphActionException e) {
				 e.printStackTrace();
			}
			
			
//			AddEdgeNode<T, U> child = new AddEdgeNode<T, U>(currentEdges.peek(), graphlets.peek().canonical(), node,
//					edge);
//			currentEdges.peek().addChild(false, child);
//			currentEdges.push(child);
//			expandEdge();
//			currentEdges.pop();
//			try {
//				T graphlet = graphlets.peek().copy();
//				graphlet.addEdge(graphlet.getOrder() - 1, currentEdges.peek().getNode(),
//						edgeTypes.get(currentEdges.peek().getType()));
//				graphlets.push(graphlet);
//				child = new AddEdgeNode<T, U>(currentEdges.peek(), graphlet.canonical(), node, edge);
//				currentEdges.peek().addChild(true, child);
//				currentEdges.push(child);
//				expandEdge();
//				graphlets.pop();
//				currentEdges.pop();
//			} catch (IllegalGraphActionException e) {
//				// e.printStackTrace();
//			}

//		}

	}

}
