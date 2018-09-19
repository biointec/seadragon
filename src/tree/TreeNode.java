package tree;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import graphlets.AbstractGraphlet;

public abstract class TreeNode<T extends AbstractGraphlet<U>, U extends Comparable<U>> {

	protected String representation;

	public String getRepresentation() {
		return representation;
	}

	protected TreeNode<T, U> parent;
	protected GraphletTree<T, U> tree;
	protected List<TreeNode<T, U>> temporaryParents;

//	public TreeNode(GraphletTree<T, U> tree) {
//		this.representation = "";
//		this.parent = null;
//		temporaryParents = new ArrayList<>();
//		this.tree = tree;
//	}

	public TreeNode(TreeNode<T, U> parent, String representation) {
		this.representation = representation;
		this.parent = parent;
		temporaryParents = new ArrayList<>();
		temporaryParents.add(parent);
		if(parent!=null)
		this.tree = parent.tree;
	}

	public void print() {
		print("");
	}

	public abstract void print(String spaces);

	public boolean isLeaf() {
		return getChildren().isEmpty();
	}

	public abstract Collection<TreeNode<T, U>> getChildren();

//	public int countLeaves() {
//		if (isLeaf()) {
//			return 1;
//		}
//		int result = 0;
//		for (TreeNode<T, U> child : getChildren()) {
//			result += child.countLeaves();
//		}
//		return result;
//	}

	public abstract void removeChild(TreeNode<T, U> child);

	public boolean prune() {
		if (isLeaf()) {
			parent.removeChild(this);
			parent.prune();
		}
		return (isLeaf());
	}

//	public int depth() {
//		if (parent == null) {
//			return 0;
//		} else {
//			return parent.depth() + 1;
//		}
//	}
//
//	public int heigth() {
//
//		int max = 0;
//		for (TreeNode<T, U> child : getChildren()) {
//			int childheight = child.heigth();
//			if (childheight >= max) {
//				max = childheight + 1;
//			}
//		}
//		return max;
//	}
	
	public void replace(TreeNode<T,U> replacement) {
		parent.replaceChild(this, replacement);
	}

	public abstract void replaceChild(TreeNode<T, U> original, TreeNode<T, U> replacement);

	public TreeNode<T,U> getParent() {
		return parent;
	}

}
