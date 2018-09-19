package tree;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import graphlets.AbstractGraphlet;

public class AddEdgeNode<T extends AbstractGraphlet<U>, U extends Comparable<U>> extends TreeNode<T, U> {

	// public AddEdgeNode(String representation) {
	// this(representation);
	// // TODO Auto-generated constructor stub
	// }
	private int node;
	private int type;
	private List<TreeNode<T, U>> children;

	public AddEdgeNode(TreeNode<T, U> parent, String representation, int node, int condition) {
		super(parent, representation);
		this.node = node;
		this.type = condition;
		children = new ArrayList<>();
		children.add(null);
		children.add(null);
	}

	@Override
	public void print(String spaces) {
		System.out.println(this);
//		System.out.println(spaces +" "+ node + " " + type);
		for (int i = 0; i < 2; i++) {
			if(children.get(i)!=null) {
			System.out.print(spaces+" " + (i == 1)+" ");
			children.get(i).print(spaces + "  ");}
		}

	}

	public int getNode() {
		return node;
	}

	public int getTypeIndex() {
		return type;
	}
	
	public U getType() {
		return tree.getEdgeTypes().get(type);
	}

	@Override
	public String toString() {
		return "AddEdgeNode [" + representation + " node=" + node + ", type=" + tree.getEdgeTypes().get(type) + "]";
	}

	public void setNode(int node) {
		this.node = node;
	}

	public void setType(int type) {
		this.type = type;
	}
	
	public TreeNode<T,U> getChild(boolean which){
		return children.get(which?1:0);
	}

	@Override
	public Collection<TreeNode<T, U>> getChildren() {
		List<TreeNode<T, U>> result = new ArrayList<TreeNode<T, U>>();
		if(children.get(0)!= null)
		result.add(children.get(0));
		if(children.get(1)!= null)
		result.add(children.get(1));
		return result;
	}

	@Override
	public void removeChild(TreeNode<T, U> child) {
		for(int i=0;i<2;i++) {
			if(children.get(i)==child) children.set(i, null);
		}
	}
	
	public void addChild(boolean condition, TreeNode<T,U> child) {
		children.set(condition?1:0,child);
	}

	@Override
	public void replaceChild(TreeNode<T, U> original, TreeNode<T, U> replacement) {
		for(int i=0;i<2;i++) {
//			System.out.println(children);
			if(original.equals(children.get(i)))children.set(i, replacement);
		}	
	}

//	@Override
//	public int numberOfNodes() {
//		int result = 0;
//		for(int i=0;i<2;i++) {
//			if(children.get(i)!=null) {
//				result+=children.get(i).numberOfNodes();
//			}
//		}
//		return result;
//	}

}
