package equationgeneration;

import java.util.Collection;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import graphlets.AbstractGraphlet;

public class EquationSelecter {

	public EquationSelecter() {
		// TODO Auto-generated constructor stub
	}

	public static <T extends AbstractGraphlet<U>, U extends Comparable<U>> SortedSet<Equation<T, U>> selectEquations(Collection<Equation<T, U>> starting) {
		SortedMap<String, Equation<T, U>> result = new TreeMap<>();
		for (Equation<T, U> equation : starting) {
			if (result.get(equation.getLhs().firstKey()) == null ||
					(result.get(equation.getLhs().firstKey()))
					.getRhsGraphlet().compareTo(equation.getRhsGraphlet()) > 0) {
				result.put(equation.getLhs().firstKey(), equation);
			}
		}
		SortedSet<Equation<T,U>> resultList = new TreeSet<>();
		for(Equation<T,U> e:result.values()) {
			resultList.add(e);
		}
		return resultList;
	}
}
