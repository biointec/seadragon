package graphlets.coGraphlet;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import graphletgeneration.AbstractGraphletFactory;

public class CoGraphletFactory extends AbstractGraphletFactory<CoGraphlet,Integer> {

	private int nColors;

	public CoGraphletFactory(int nColors, boolean isOrbitRep) {
		super(isOrbitRep);
		this.nColors = nColors;
	}


	@Override
	public CoGraphlet toGraphlet(String s) {
		return new CoGraphlet(s,isOrbitRep);
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
	protected int representationLength(int order) {
		return order*(order-1)/2;
	}


	@Override
	public List<Integer> getEdgeTypes() {
		List<Integer> result = new ArrayList<>();
		for (int i = 1; i < nColors+1; i++) {
			result.add(i);
		}
		return result;
	}

	@Override
	public List<SortedSet<Integer>> edgeCombinations() {
		List<SortedSet<Integer>> result = new ArrayList<>();
		for(int i:getEdgeTypes()) {
			SortedSet<Integer>edge = new TreeSet<>();
			edge.add(i);
			result.add(edge);
		}
//		System.out.println("boop");
//		System.out.println(result);
		return result;
	}


	@Override
	protected String namePrefix() {
		return "Co";
	}
}
