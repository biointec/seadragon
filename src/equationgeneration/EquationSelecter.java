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

	public static <T extends AbstractGraphlet<?>> SortedSet<Equation<T>> selectEquations(Collection<Equation<T>> starting) {
		SortedMap<String, Equation<T>> result = new TreeMap<>();
		for (Equation<T> equation : starting) {
			if (result.get(equation.getLhs().firstKey()) == null ||
					(result.get(equation.getLhs().firstKey()))
					.getRhsGraphlet().compareTo(equation.getRhsGraphlet()) > 0) {
				result.put(equation.getLhs().firstKey(), equation);
			}
		}
		SortedSet<Equation<T>> resultList = new TreeSet<>();
		for(Equation<T> e:result.values()) {
			resultList.add(e);
		}
		return resultList;
	}
}
