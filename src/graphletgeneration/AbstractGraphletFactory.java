package graphletgeneration;

import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;

import diGraphlet.DiGraphlet;
import graphlets.AbstractGraphlet;
import graphlets.CanonicalComparator;

public abstract class AbstractGraphletFactory<T extends AbstractGraphlet<? extends Comparable<?>>> implements Iterator<T>{
	
	protected int order;
	protected boolean isOrbitRep;
	private int count;
	private int[] rep;
	protected CanonicalComparator comparator;
	
	public AbstractGraphletFactory(boolean isOrbitRep) {
		this.isOrbitRep=isOrbitRep;
	}
	
	public void reset() {
		count = 1;
		rep=new int[representationLength()];
		for(int i=0;i<representationLength();i++) {
			rep[i]=-1;
		}
	}
	
	public void setOrder(int order) {
		this.order=order;
		reset();
	}
	
	public boolean hasNext() {
		return count < Math.pow( validCharacters().length+1,representationLength());
	}
	
	public T next() {
		int i = rep.length - 1;
		while (rep[i]==validCharacters().length-1) {
			rep[i] = -1;
			--i;
		}
		rep[i] ++;
		++count;
		return toGraphlet(matrixToString());
	}
	
	public T canonicalVersion(T graphlet) {
		return toGraphlet(graphlet.canonical());
	}
	
	private String matrixToString() {
		StringBuilder sb = new StringBuilder();
		for(int i: rep) {
			if(i<0) {
				sb.append('0');
			}else {
				sb.append(validCharacters()[i]);
			}
		}
		return sb.toString();
	}
	
	protected abstract char[] validCharacters();
	
	protected abstract int representationLength();
	
	public abstract T toGraphlet(String s);
	
	public T emptyGraphlet() {
		return toGraphlet("");
	}
	
	public T copy(T graphlet) {
		return toGraphlet(graphlet.representation());
	}
	
	public CanonicalComparator comparator() {
		return comparator;
	}

}
