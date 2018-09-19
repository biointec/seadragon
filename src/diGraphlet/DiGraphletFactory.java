package diGraphlet;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import graphletgeneration.AbstractGraphletFactory;

public class DiGraphletFactory extends AbstractGraphletFactory<DiGraphlet,Boolean> {

	public DiGraphletFactory(boolean isOrbitRep) {
		super(isOrbitRep);
	}

	private static final char[] validCharacters = {'1'};

	@Override
	public DiGraphlet toGraphlet(String s) {
		return new DiGraphlet(s, isOrbitRep);
	}

	@Override
	protected char[] validCharacters() {
		return validCharacters;
	}

	@Override
	protected int representationLength(int order) {
		return order*(order-1);
	}

	@Override
	public List<Boolean> edgeTypes() {
		List<Boolean> result = new ArrayList<>();
		result.add(false);
		result.add(true);
		return result;
	}

	@Override
	public List<SortedSet<Boolean>> edgeCombinations() {
		List<SortedSet<Boolean>> result = new ArrayList<>();
		SortedSet<Boolean> a = new TreeSet<Boolean>();
		a.add(true);
		result.add(a);
		a = new TreeSet<Boolean>();
		a.add(false);
		result.add(a);
		a = new TreeSet<Boolean>();
		a.add(true);
		a.add(false);
		result.add(a);
		return result;

	}
}
