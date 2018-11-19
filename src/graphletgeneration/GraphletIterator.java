package graphletgeneration;

import java.util.Iterator;

import graphlets.AbstractGraphlet;

/**
 * Iterator that iterates over all possible graphlets of a given type and order.
 * 
 * @author Ine Melckenbeeck
 *
 * @param <T>
 *            Type of the used graphlets.
 */
public class GraphletIterator<T extends AbstractGraphlet<?>> implements Iterator<T> {

	protected int order;
	protected boolean isOrbitRep;
	private int count;
	private int[] rep;
	private AbstractGraphletFactory<T, ?> factory;
	private boolean inCaseOfOneNode;

	/**
	 * Creates a new GraphletIterator from the given type and order.
	 * 
	 * @param factory
	 *            AbstractGraphletFactory for the used type.
	 * @param order
	 *            Order of the generated graphlets.
	 */
	public GraphletIterator(AbstractGraphletFactory<T, ?> factory, int order) {
		this.factory = factory;
		this.order = order;
		count = 1;
		rep = new int[factory.representationLength(order)];
		for (int i = 0; i < factory.representationLength(order); i++) {
			rep[i] = -1;
		}
		inCaseOfOneNode = true;
	}

	@Override
	public boolean hasNext() {
		if (order == 1)
			return inCaseOfOneNode;
		return count < Math.pow(factory.validCharacters().length + 1, factory.representationLength(order));
	}

	@Override
	public T next() {
		if (order == 1) {
			inCaseOfOneNode = false;
			return factory.oneNodeGraphlet();
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
				graphlet = factory.toGraphlet(matrixToString());
			} while (!graphlet.isConnected() || !graphlet.isCanonical());
			return graphlet;
		}
	}

	/**
	 * Changes the internal matrix representation, which is an array of integers, to
	 * a valid string representation for the given graphlet type.
	 * 
	 * @return the string representation of the current graphlet.
	 */
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
