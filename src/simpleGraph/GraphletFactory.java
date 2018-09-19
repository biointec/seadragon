package simpleGraph;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

public class GraphletFactory extends graphletgeneration.AbstractGraphletFactory<Graphlet,Boolean> {

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
	protected int representationLength(int order) {
		return order*(order-1)/2;
	}
	
	@Override
	public List<Boolean> edgeTypes() {
		List<Boolean> result = new ArrayList<>();
		result.add(true);
		return result;
	}

	@Override
	public List<SortedSet<Boolean>> edgeCombinations() {
		List<SortedSet<Boolean>> result = new ArrayList<>(1);
		result.add(new TreeSet<>(edgeTypes()));
		return result;
	}

}
