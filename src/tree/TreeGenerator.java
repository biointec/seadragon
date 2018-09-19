package tree;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.Stack;
import java.util.TreeSet;

import genGraphlet.GenGraphletFactory;
import graphletgeneration.AbstractGraphletFactory;
import graphlets.AbstractGraphlet;
import graphlets.IllegalGraphActionException;

/**
 * Class that contains all data and algorithms needed to generate a
 * GraphletTree. This information is kept separate from the GraphletTree itself
 * so any TreeGenerator object can be discarded after use.
 * 
 * Every tree needs to be generated only once, after which it should be saved
 * and loaded using the GraphletIO class.
 * 
 * @author Ine Melckenbeeck
 *
 * @see GraphletTree
 * @param <T>
 *            The graphlet type for which the tree must be generated.
 * @param <U>
 *            The data type corresponding to T's edges.
 */
public class TreeGenerator<T extends AbstractGraphlet<U>, U extends Comparable<U>> {

	private GraphletTree<T, U> tree;
	private List<U> edgeTypes;
	private Stack<AddNodeNode<T, U>> currentNodes;
	private Stack<AddEdgeNode<T, U>> currentEdges;
	private Stack<T> graphlets;
	private int order;
	private SortedSet<String> usedGraphlets;
	private Set<TreeNode<T, U>> toPrune;
	private AbstractGraphletFactory<T,U> factory;

	/**
	 * Creates a new TreeGenerator with the
	 * 
	 * @param root
	 *            The root graphlet of the tree.
	 * @param order
	 *            The maximal order of graphlets within the tree.
	 */
	public TreeGenerator(AbstractGraphletFactory<T,U> f, int order) {
		factory = f;
		tree = new GraphletTree<T, U>(f, order);
		AddNodeNode<T, U> rootNode = tree.getRoot();
		currentNodes = new Stack<>();
		currentNodes.add(rootNode);
		currentEdges = new Stack<>();
		graphlets = new Stack<T>();
		graphlets.push(f.emptyGraphlet());
		edgeTypes = tree.getEdgeTypes();
		usedGraphlets = new TreeSet<>();
		this.order = order;
		toPrune = new HashSet<>();
	}

	/**
	 * Generates a new GraphletTree.
	 * 
	 * @return
	 */
	public GraphletTree<T, U> generateTree() {
		expandNode();
		for (TreeNode<T, U> tn : toPrune) {
			tn.prune();
		}
		return tree;
	}
	
	public static void main(String[]args) {
		new TreeGenerator<>(new GenGraphletFactory(true),3).generateTree().print();
	}

	/**
	 * Breaks the symmetry of the current graphlet and inserts appropriate
	 * ConditionNodes before the current AddNodeNode.
	 */
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
		currentNodes.peek().setSymmetryFactor(1);
	}

	/**
	 * Expands the current AddNodeNode by adding a new node with all possible edges
	 * to the graphlet's existing nodes.
	 */
	private void expandNode() {
		try {
			T graphlet = factory.copy(graphlets.peek());
			if (!usedGraphlets.add(graphlet.canonical())) {
				// This graphlet has been found already, prune the current node.
				toPrune.add(currentNodes.peek());
				return;
			}
			if (graphlet.getOrder() == order + 1) {
				if (graphlet.isComplete()) {
					breakSymmetry();
				} else {
					toPrune.add(currentNodes.peek());
				}
				return;
			}
			if (graphlet.getOrder() == order) {
				// The maximum order is reached, no further nodes must be added.
				breakSymmetry();
				tree.addLeaf(currentNodes.peek());
				if (!graphlet.isComplete()) {
					return;
				}
			}
			// Add a new node to the graphlet connected to the first node of every orbit,
			// with every possible edge type.
			graphlet.addNode();
			for (SortedSet<Integer> orbit : graphlet.getOrbits()) {
				int i = orbit.first();
				if (i != graphlet.getOrder() - 1) {
					graphlets.push(graphlet);
					for (int j = 0; j < edgeTypes.size(); j++) {
						U edge = edgeTypes.get(j);
						graphlet.addEdge(i, graphlet.getOrder() - 1, edge);
						// Now try to add more edges.
						expandEdges(i, j);
						graphlet.removeEdge(i, graphlet.getOrder() - 1);
					}
					graphlets.pop();
				}
			}
		} catch (IllegalGraphActionException e) {
			// Shouldn't happen.
			e.printStackTrace();
		}
	}

	/**
	 * Create the descendants of the current AddNodeNode in the branch where the
	 * newly added node is connected with the given existing node and edge type.
	 * 
	 * @param currentGray
	 *            The node to which the last added node is connected.
	 * @param currentColour
	 *            The index of the edge type of the last added node's connection.
	 */
	private void expandEdges(int currentGray, int currentColour) {
		if (currentColour > 0 || currentGray > 0) {
			// make sure the edges before the last one are not present.
			negativeEdge(currentGray, currentColour);
		} else if (edgeTypes.size() == 1 && graphlets.peek().getOrder() == 2) {
			// there are no edges before the last one, nor after the last one
			// no extra edge can be added, add a new node instead
			AddNodeNode<T, U> child = new AddNodeNode<>(currentNodes.peek(), graphlets.peek());
			currentNodes.peek().addChild(0, edgeTypes.get(0), child);
			currentNodes.push(child);
			expandNode();
			currentNodes.pop();

		} else {
			// there are no edges before the last one, but there are possible next edges
			// add the next possible edge:
			// if there's more than 1 edge type, use the second one
			// else use the next node
			currentEdges.push(new AddEdgeNode<T, U>(currentNodes.peek(), graphlets.peek().canonical(),
					edgeTypes.size() == 1 ? 1 : 0, edgeTypes.size() == 1 ? 0 : 1));
			currentNodes.peek().addChild(0, edgeTypes.get(0), currentEdges.peek());
			checkEdge();
			currentEdges.pop();
		}

	}

	/**
	 * Adds AddEdgeNodes to check that the edges before the one of the last added
	 * node are not actually present.
	 * 
	 * @param lastGray
	 *            The node to which the last added node is connected.
	 * @param colour
	 *            The index of the edge type of the last added node's connection.
	 */
	private void negativeEdge(int lastGray, int colour) {
		T graphlet = graphlets.peek();
		TreeNode<T, U> parent = currentNodes.peek();
		AddEdgeNode<T, U> first = null;
		for (int i = 0; i < lastGray * edgeTypes.size() + colour; i++) {
			try {
				// try to add the current edge
				// TODO: use the list of allowed edge combinations instead?
				factory.copy(graphlet).addEdge(i / edgeTypes.size(), graphlet.getOrder() - 1,
						edgeTypes.get(i % edgeTypes.size()));
				// if we get here, the next edge could be present, so we need to make sure it
				// isn't
				AddEdgeNode<T, U> copy = new AddEdgeNode<T, U>(parent, graphlet.canonical(), i / edgeTypes.size(),
						(i % edgeTypes.size()));
				if (first == null) {// the first AddEdgeNode will later be added as a child of the current
									// AddNodeNode
					first = copy;
				} else {
					((AddEdgeNode<T, U>) parent).addChild(false, copy);
				}
				parent = copy;
			} catch (IllegalGraphActionException e) {
				// the next edge can't be added, so there's no need to do check whether it is
				// present
			}
		}
		if (first != null) {
			// We managed to add an edge before the one of the last node, yay!
			currentNodes.peek().addChild(lastGray, edgeTypes.get(colour), first);
			if (lastGray == graphlets.peek().getOrder() - 2 && colour == edgeTypes.size() - 1) {
				// There are no unchecked edges left, go add a new node instead
				currentNodes.push(new AddNodeNode<T, U>(parent, graphlet));
				((AddEdgeNode<T, U>) parent).addChild(false, currentNodes.peek());
				expandNode();
				currentNodes.pop();
			} else {
				// There is still an unchecked edge after the current one, go check it
				currentEdges.push(new AddEdgeNode<T, U>(parent, graphlet.canonical(),
						lastGray + (colour + 1) / edgeTypes.size(), ((colour + 1) % edgeTypes.size())));
				((AddEdgeNode<T, U>) parent).addChild(false, currentEdges.peek());
				checkEdge();
				currentEdges.pop();
			}
		} else {
			// All edges before the one of the last node couldn't exist anyway
			if (lastGray == graphlets.peek().getOrder() - 2 && colour == edgeTypes.size() - 1) {
				// and there are none to check after it either, so go add a new node to the
				// current AddNodeNode
				AddNodeNode<T, U> copy = currentNodes.peek();
				currentNodes.push(new AddNodeNode<T, U>(copy, graphlet));
				copy.addChild(lastGray, edgeTypes.get(colour), currentNodes.peek());
				expandNode();
				currentNodes.pop();
			} else {
				// There are still edges to check, so go check them
				AddNodeNode<T, U> copy = currentNodes.peek();
				currentEdges.push(new AddEdgeNode<T, U>(copy, graphlet.canonical(),
						lastGray + (colour + 1) / edgeTypes.size(), ((colour + 1) % edgeTypes.size())));
				copy.addChild(lastGray, edgeTypes.get(colour), currentEdges.peek());
				checkEdge();
				currentEdges.pop();
			}
		}

	}

	/**
	 * Expands the graphlet tree by checking the current AddEdgeNode, adding
	 * appropriate children when the edge is absent or present.
	 */
	private void checkEdge() {

		int node = currentEdges.peek().getNode() + (currentEdges.peek().getTypeIndex() + 1) / edgeTypes.size();
		int edge = (currentEdges.peek().getTypeIndex() + 1) % edgeTypes.size();

		if (node < graphlets.peek().getOrder() - 1) {

			// There is still an unchecked edge left
			try {
				// Current edge is added, check the next edge
				T graphlet = factory.copy(graphlets.peek());
				graphlet.addEdge(currentEdges.peek().getNode(), graphlet.getOrder() - 1,
						edgeTypes.get(currentEdges.peek().getTypeIndex()));
				graphlets.push(graphlet);
				AddEdgeNode<T, U> child = new AddEdgeNode<T, U>(currentEdges.peek(), graphlet.canonical(), node, edge);
				currentEdges.peek().addChild(true, child);
				currentEdges.push(child);
				checkEdge();
				graphlets.pop();
				currentEdges.pop();
				// Current edge is not added, check the next edge
				child = new AddEdgeNode<T, U>(currentEdges.peek(), graphlets.peek().canonical(), node, edge);
				currentEdges.peek().addChild(false, child);
				currentEdges.push(child);
				checkEdge();
				currentEdges.pop();
			} catch (IllegalGraphActionException e) {
				// Current edge can't exist, try again with the next edge
				currentEdges.peek().setNode(node);
				currentEdges.peek().setType(edge);
				checkEdge();

			}
		} else {
			// There are no unchecked edges left
			try {

				// Current edge is added, add a new node
				T graphlet = factory.copy(graphlets.peek());
				graphlet.addEdge(graphlet.getOrder() - 2, graphlet.getOrder() - 1, edgeTypes.get(edgeTypes.size() - 1));
				graphlets.push(graphlet);
				AddNodeNode<T, U> child = new AddNodeNode<>(currentEdges.peek(), graphlet);
				currentEdges.peek().addChild(true, child);
				currentNodes.push(child);
				expandNode();
				graphlets.pop();
				currentNodes.pop();
				// Current edge is not added, add a new node
				child = new AddNodeNode<>(currentEdges.peek(), graphlets.peek());
				currentEdges.peek().addChild(false, child);
				currentNodes.push(child);
				expandNode();
				currentNodes.pop();
			} catch (IllegalGraphActionException e) {
				// Current edge can't exist and there is no next edge, replace the current
				// AddEdgeNode by an AddNodeNode

				AddNodeNode<T, U> replacement = new AddNodeNode<T, U>(currentEdges.peek().parent, graphlets.peek());
				currentEdges.peek().replace(replacement);
				currentNodes.push(replacement);
				expandNode();
				currentNodes.pop();
			}
		}

	}

}
