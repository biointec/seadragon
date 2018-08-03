package equationgeneration;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;

import graphlets.AbstractGraphlet;

public class Equation<E extends AbstractGraphlet<V>, V extends Comparable<V>> implements Comparable<Equation<E, V>> {

	private SortedMap<E, Integer> lhs;
	private E rhsGraphlet;
	private Set<Map<V, SortedSet<Integer>>> commons;
	private int minus;

	public Equation(SortedMap<E, Integer> lhs, E rhsGraphlet, Set<Map<V, SortedSet<Integer>>> rhs, int minus) {
		this.lhs = lhs;
		this.rhsGraphlet = rhsGraphlet;
		this.commons = rhs;
		this.minus = minus;
	}

	@Override
	public int compareTo(Equation<E, V> arg0) {
		E e1 = lhs.firstKey();
		E e2 = arg0.lhs.firstKey();
		return e1.compareTo(e2);
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
		Equation other = (Equation) obj;
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

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (E graphlet : lhs.keySet()) {
			if (lhs.get(graphlet) != 1) {
				sb.append(lhs.get(graphlet));
				sb.append(" * ");
			}
			sb.append(graphlet.representation());
			sb.append(" + ");
		}
		sb.replace(sb.length() - 2, sb.length() - 1, "=");
		sb.append("Sum over ");
		sb.append(rhsGraphlet.representation());
		sb.append(":(");
		for (Map<V, SortedSet<Integer>> term : commons) {
			sb.append("c(");
			for (V key : term.keySet()) {
				if (!term.get(key).isEmpty()) {
					sb.append(key);
					sb.append(":");
					sb.append(term.get(key));
					sb.append(";");
				}
			}
			sb.replace(sb.length() - 1, sb.length(), ")");
			if (minus != 0) {
				sb.append(" - ");
				sb.append(minus);
			}

		}
		sb.append(")");
		return sb.toString();
	}

}
