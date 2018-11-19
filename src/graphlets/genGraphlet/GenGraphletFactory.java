package graphlets.genGraphlet;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import graphletgeneration.AbstractGraphletFactory;

public class GenGraphletFactory extends AbstractGraphletFactory<GenGraphlet,Byte> {
	
	public GenGraphletFactory(boolean isOrbitRep) {
		super(isOrbitRep);
	}

	private static final char[] validCharacters = {'+','-'};


	@Override
	protected char[] validCharacters() {
		return validCharacters;
	}

	@Override
	protected int representationLength(int order) {
		return order*(order-1);
	}

	@Override
	public GenGraphlet toGraphlet(String s) {
		return new GenGraphlet(s,isOrbitRep);
	}

	@Override
	public List<Byte> getEdgeTypes() {
		List<Byte> result = new ArrayList<>();
		result.add((byte) 1);
		result.add((byte) -1);
		result.add((byte) 2);
		result.add((byte) -2);
		return result;
	}

	@Override
	public List<SortedSet<Byte>> edgeCombinations() {
		List<SortedSet<Byte>> result = new ArrayList<>();
		byte[][] options = { { 0, -1, -2 }, { 0, 1, 2 } };
		for (int i = 1; i < 9; i++) {
			int a = i % 3;
			int b = i / 3;
			SortedSet<Byte> piece = new TreeSet<>();
			if (options[0][a] != 0) {
				piece.add(options[0][a]);
			}
			if (options[1][b] != 0) {
				piece.add(options[1][b]);
			}
			result.add(piece);
		}
		return result;
	}

	@Override
	protected String namePrefix() {
		return "Gen";
	}
}
