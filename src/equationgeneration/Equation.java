package equationgeneration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;

import graphlets.AbstractGraphlet;
import graphlets.CanonicalComparator;

public class Equation<E extends AbstractGraphlet<V>, V extends Comparable<V>> implements Comparable<Equation<E, V>> {

	private SortedMap<String, Integer> lhs;
	private String rhsGraphlet;
	private Set<List<Set<Integer>>> commons;
	private int minus;
	private List<V> edgeTypes;

	public Equation(SortedMap<String, Integer> lhs, E rhsGraphlet, Set<List<Set<Integer>>> rhs, int minus) {
		this.lhs = lhs;
		this.rhsGraphlet = rhsGraphlet.canonical();
		this.commons = rhs;
		this.minus = minus;
		edgeTypes = new ArrayList<>();
		edgeTypes.addAll(rhsGraphlet.edgeTypes());
	}

	@Override
	public int compareTo(Equation<E, V> arg0) {
		String e1 = lhs.firstKey();
		String e2 = arg0.lhs.firstKey();
		return new CanonicalComparator().compare(e1,e2);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((commons == null) ? 0 : commons.hashCode());
		result = prime * result + ((lhs == null) ? 0 : lhs.hashCode());
		result = prime * result + minus;
		result = prime * result + ((rhsGraphlet == null) ? 0 : rhsGraphlet.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Equation<E,V> other = (Equation<E,V>) obj;
		if (commons == null) {
			if (other.commons != null)
				return false;
		} else if (!commons.equals(other.commons))
			return false;
		if (lhs == null) {
			if (other.lhs != null)
				return false;
		} else if (!lhs.equals(other.lhs))
			return false;
		if (minus != other.minus)
			return false;
		if (rhsGraphlet == null) {
			if (other.rhsGraphlet != null)
				return false;
		} else if (!rhsGraphlet.equals(other.rhsGraphlet))
			return false;
		return true;
	}

	public SortedMap<String, Integer> getLhs() {
		return lhs;
	}

	public String getRhsGraphlet() {
		return rhsGraphlet;
	}

	public Set<List<Set<Integer>>> getCommons() {
		return commons;
	}

	public int getMinus() {
		return minus;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (String graphlet : lhs.keySet()) {
			if (lhs.get(graphlet) != 1) {
				sb.append(lhs.get(graphlet));
				sb.append(" * ");
			}
			sb.append(graphlet);
			sb.append(" + ");
		}
		sb.replace(sb.length() - 2, sb.length() - 1, "=");
		sb.append("Sum over ");
		sb.append(rhsGraphlet);
		sb.append(":(");
		for (List<Set<Integer>> term : commons) {
			sb.append("c(");
			for (int i=0;i<edgeTypes.size();i++) {
				if (!term.get(i).isEmpty()) {
					sb.append(edgeTypes.get(i));
					sb.append(":");
					sb.append(term.get(i));
					sb.append(";");
				}
			}
			sb.replace(sb.length() - 1, sb.length(), ")");
			if (minus != 0) {
				sb.append(" - ");
				sb.append(minus);
			}
			sb.append(" + ");

		}
		sb.replace(sb.length() - 3, sb.length(), ")\n");
		return sb.toString();
	}

}
