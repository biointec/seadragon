package graph;

public class GraphletFactory extends graphletgeneration.AbstractGraphletFactory<Graphlet> {

	private static final char[] characters = {'1'};
	
	public GraphletFactory(boolean isOrbitRep) {
		super(isOrbitRep);
		// TODO Auto-generated constructor stub
	}

	@Override
	public Graphlet toGraphlet(String s) {
		return new Graphlet(s, isOrbitRep);
	}

	@Override
	protected char[] validCharacters() {
		return characters;
	}

	@Override
	protected int representationLength() {
		return order*(order-1)/2;
	}

}
