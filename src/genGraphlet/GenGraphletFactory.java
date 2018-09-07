package genGraphlet;

import graphletgeneration.AbstractGraphletFactory;

public class GenGraphletFactory extends AbstractGraphletFactory<GenGraphlet> {
	
	public GenGraphletFactory(boolean isOrbitRep) {
		super(isOrbitRep);
	}

	private static final char[] validCharacters = {'+','-'};


	@Override
	protected char[] validCharacters() {
		return validCharacters;
	}

	@Override
	protected int representationLength() {
		return order*(order-1);
	}

	@Override
	public GenGraphlet toGraphlet(String s) {
		return new GenGraphlet(s,isOrbitRep);
	}

}
