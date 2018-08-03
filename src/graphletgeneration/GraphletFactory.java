package graphletgeneration;

import java.util.Iterator;

import graphlets.AbstractGraphlet;

public abstract class GraphletFactory<T extends AbstractGraphlet<?>> implements Iterator<T>{
	
	protected int order;
	protected boolean isOrbitRep;
	
	public GraphletFactory(boolean isOrbitRep) {
		this.isOrbitRep=isOrbitRep;
	}
	
	public void setOrder(int order) {
		this.order=order;
	}

}
