package tree;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import graphletgeneration.AbstractGraphletFactory;
import graphlets.AbstractGraphlet;

public class GraphletTree<T extends AbstractGraphlet<U>, U extends Comparable<U>> implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 971133459606523849L;
//	private List<U> edgeTypes;
	private AddNodeNode<T,U> root;
	private List<AddNodeNode<T,U>> leaves;
	private int order;
	private boolean isOrbitRep;
	private AbstractGraphletFactory<T,U> factory;

	
	public GraphletTree(AbstractGraphletFactory<T,U> gf, int order) {
		T root = gf.oneNodeGraphlet();
//		edgeTypes = gf.edgeTypes();
		leaves = new ArrayList<>();
//		System.out.println(root);
		this.root=new AddNodeNode<T,U>(null,root);
		this.root.tree=this;
		this.order=order;
		isOrbitRep = root.isOrbitRep();
		factory = gf;
	}
	
	public boolean isOrbitRep() {
		return isOrbitRep;
	}
	
	public int getOrder() {
		return order;
	}
	void addLeaf(AddNodeNode<T,U>leaf) {
		leaves.add(leaf);
	}
	
	public List<AddNodeNode<T,U>> getLeaves(){
		return leaves;
	}
	
	public AbstractGraphletFactory<T,U> getFactory(){
		return factory;
	}
	
	public List<U> getEdgeTypes() {
		ArrayList<U> result = new ArrayList<>(factory.getEdgeTypes());
		return result;
	}

	public AddNodeNode<T, U> getRoot() {
		return root;
	}
	
	public void print(){
		root.print();
	}

	
}
