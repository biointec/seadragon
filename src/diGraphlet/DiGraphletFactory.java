package diGraphlet;

import java.util.List;

import graphletgeneration.AbstractGraphletFactory;

public class DiGraphletFactory extends AbstractGraphletFactory<DiGraphlet> {

	public DiGraphletFactory(boolean isOrbitRep) {
		super(isOrbitRep);
	}

	private static final char[] validCharacters = {'1'};

	public void setOrder(int order) {
		super.setOrder(order);
	}

	@Override
	public DiGraphlet toGraphlet(String s) {
		return new DiGraphlet(s, isOrbitRep);
	}

	@Override
	protected char[] validCharacters() {
		return validCharacters;
	}

	@Override
	protected int representationLength() {
		return order*(order-1);
	}

}
