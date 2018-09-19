package graphletgeneration;

import java.util.Iterator;

import graphlets.AbstractGraphlet;

public class GraphletIterator<T extends AbstractGraphlet<?>> implements Iterator<T> {

	protected int order;
	protected boolean isOrbitRep;
	private int count;
	private int[] rep;
	private AbstractGraphletFactory<T,?> factory;
	private boolean inCaseOfOneNode;

	public GraphletIterator(AbstractGraphletFactory<T,?> factory) {
		this.factory=factory;
	}
	public GraphletIterator(AbstractGraphletFactory<T,?> factory, int order) {
		this.factory=factory;
		setOrder(order);
	}

	public void reset() {
		count = 1;
		rep = new int[factory.representationLength(order)];
		for (int i = 0; i < factory.representationLength(order); i++) {
			rep[i] = -1;
		}
		inCaseOfOneNode = true;
	}

	public void setOrder(int order) {
		this.order = order;
		reset();
	}

	public boolean hasNext() {
		if (order == 1)
			return inCaseOfOneNode;
		return count < Math.pow(factory.validCharacters().length + 1, factory.representationLength(order));
	}

	public T next() {
		if (order == 1) {
			inCaseOfOneNode = false;
			return factory.emptyGraphlet();
		} else {
			T graphlet;
			do {
			int i = rep.length - 1;
			while (rep[i] == factory.validCharacters().length - 1) {
				rep[i] = -1;
				--i;
			}
			rep[i]++;
			++count;
			graphlet = factory.toGraphlet(matrixToString());}
			while(!graphlet.isConnected()||!graphlet.permute()) ;
				
			
			return graphlet;
		}
	}

	private String matrixToString() {
		StringBuilder sb = new StringBuilder();
		for (int i : rep) {
			if (i < 0) {
				sb.append('0');
			} else {
				sb.append(factory.validCharacters()[i]);
			}
		}
		return sb.toString();
	}

}
