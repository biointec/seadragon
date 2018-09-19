package graphletgeneration;

import java.util.List;
import java.util.SortedSet;

import graphlets.AbstractGraphlet;

public abstract class AbstractGraphletFactory<T extends AbstractGraphlet<U> , U extends Comparable<U>>{
	protected boolean isOrbitRep;
	
	public AbstractGraphletFactory(boolean isOrbitRep) {
		this.isOrbitRep=isOrbitRep;
	}
	
	public T canonicalVersion(T graphlet) {
		return toGraphlet(graphlet.canonical());
	}
	
	protected abstract char[] validCharacters();
	
	protected abstract int representationLength(int order);
	
	public abstract T toGraphlet(String s);
	
	public T emptyGraphlet() {
		return toGraphlet("");
	}
	
	public T copy(T graphlet) {
		return toGraphlet(graphlet.representation());
	}
	
	public abstract List<U > edgeTypes();
	
	public abstract List<SortedSet<U >> edgeCombinations();
}
