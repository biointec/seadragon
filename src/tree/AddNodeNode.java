package tree;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import graphlets.AbstractGraphlet;

public class AddNodeNode<T extends AbstractGraphlet<U>, U extends Comparable<U>> extends TreeNode<T, U> {


	private SortedMap<Integer, SortedMap<U, TreeNode<T, U>>> children;
	private Map <TreeNode<T,U>, U> reverseChildren1;
	private Map<TreeNode<T,U>,Integer> reverseChildren2;
	private int symmetryFactor;
	private List<Integer> canonicalAutomorphism;
	
	public int getSymmetryFactor() {
		return symmetryFactor;
	}

	void setSymmetryFactor(int symmetryFactor) {
		this.symmetryFactor = symmetryFactor;
	}

	public AddNodeNode(TreeNode<T,U> parent,/*String representation*/ T graphlet) {
		super(parent, graphlet.canonical());
		symmetryFactor = graphlet.getSymmetry();
		children = new TreeMap<>(Comparator.nullsFirst(Comparator.naturalOrder()));
		reverseChildren1=new HashMap<>();
		reverseChildren2 = new HashMap<>();
		canonicalAutomorphism = graphlet.getCanonicalAutomorphism();
	}
	
	
	public List<Integer> getCanonicalAutomorphism() {
		return canonicalAutomorphism;
	}

	public SortedMap<Integer, SortedMap<U, TreeNode<T, U>>> getChildrenMap(){
		return children;
	}
	
	
//	public AddNodeNode(T root) {
//		super(new GraphletTree<>(root,order));
//		children = new TreeMap<>(Comparator.nullsFirst(Comparator.naturalOrder()));
//		reverseChildren1=new HashMap<>();
//		reverseChildren2 = new HashMap<>();
//	}
	
	public void print(String spaces) {
		System.out.println(this);
		for (Integer i : children.keySet()) {
			System.out.println(spaces + " " + i);
			for (U type : children.get(i).keySet()) {
				System.out.print(spaces + "  " + type + " ");
				children.get(i).get(type).print(spaces + "  ");
			}
		}
	}
	
	@Override
	public String toString() {
		return "AddNodeNode [" + representation + "]";
	}

	public boolean isLeaf() {
		return children.isEmpty();
	}

	public void addChild(Integer node, U type, TreeNode<T, U> tn) {
		SortedMap<U, TreeNode<T, U>> m = children.get(node);
		if (m == null) {
			m = new TreeMap<>(Comparator.nullsFirst(Comparator.naturalOrder()));
			children.put(node, m);
		}
		m.put(type, tn);
		reverseChildren1.put(tn, type);
		reverseChildren2.put(tn, node);
	}

	public void removeChild(TreeNode<T,U> child) {
		children.get(reverseChildren2.get(child)).remove(reverseChildren1.get(child));
		if(children.get(reverseChildren2.get(child)).isEmpty()) {
			children.remove(reverseChildren2.get(child));
		}
		reverseChildren2.remove(child);
		reverseChildren1.remove(child);
	}

	@Override
	public Collection<TreeNode<T, U>> getChildren() {
		return reverseChildren1.keySet();
	}

	@Override
	public void replaceChild(TreeNode<T, U> original, TreeNode<T, U> replacement) {
		children.get(reverseChildren2.get(original)).put(reverseChildren1.get(original),replacement);
		reverseChildren2.put(replacement, reverseChildren2.get(original));
		reverseChildren2.remove(original);
		reverseChildren1.put(replacement, reverseChildren1.get(original));
		reverseChildren1.remove(original);
	}

	@Override
	public int numberOfNodes() {
		int result = 1;
		for(TreeNode<T,U> tn:reverseChildren1.keySet()) {
			result+=tn.numberOfNodes();
		}
		return result;
	}

}
