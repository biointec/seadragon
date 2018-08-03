package tree;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;

import graphlets.AbstractGraphlet;

public class GraphletTree<T extends AbstractGraphlet<U>, U extends Comparable<U>> implements Serializable{


	/**
	 * 
	 */
	private static final long serialVersionUID = 971133459606523849L;
	private SortedSet<U> edgeTypes;
	private AddNodeNode<T,U> root;
	private List<TreeNode<T,U>> leaves;
	
	public GraphletTree(T root) {
		edgeTypes = root.edgeTypes();
		leaves = new ArrayList<>();
		System.out.println(root);
		this.root=new AddNodeNode<T,U>(null,root.representation());
		this.root.tree=this;
	}
	
	void addLeaf(TreeNode<T,U>leaf) {
		leaves.add(leaf);
	}
	
	public List<TreeNode<T,U>> getLeaves(){
		return leaves;
	}
	
	public List<U> getEdgeTypes() {
		ArrayList<U> result = new ArrayList<>(edgeTypes.size());
		result.addAll(edgeTypes);
		return result;
	}

	public AddNodeNode<T, U> getRoot() {
		return root;
	}
	
	public void print(){
		root.print();
	}

	
}
