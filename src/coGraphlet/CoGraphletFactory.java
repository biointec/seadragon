package coGraphlet;

import java.util.Arrays;

import graphletgeneration.AbstractGraphletFactory;
import graphlets.AbstractGraphlet;

public class CoGraphletFactory extends AbstractGraphletFactory<CoGraphlet> {

	private int nColors;

	public CoGraphletFactory(int nColors, boolean isOrbitRep) {
		super(isOrbitRep);
		this.nColors = nColors;
	}

	public void setOrder(int order) {
		super.setOrder(order);
	}

	@Override
	public CoGraphlet toGraphlet(String s) {
		return new CoGraphlet(s,nColors,isOrbitRep);
	}

	@Override
	protected char[] validCharacters() {
		char[] result = new char[nColors];
		for(int i=0;i<result.length;i++) {
			result[i]= (char) ('1'+i);
		}
		return result;
	}

	@Override
	protected int representationLength() {
		return order*(order-1)/2;
	}
}
