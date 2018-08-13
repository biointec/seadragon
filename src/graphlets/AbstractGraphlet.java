package graphlets;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeSet;

import diGraphlet.DiGraphlet;
import graph.Graphlet;
import graphletgeneration.Permutator;
import graphs.AbstractGraph;

public abstract class AbstractGraphlet<T extends Comparable<T>> extends AbstractGraph<T>
		implements Serializable, Comparable<AbstractGraphlet<T>> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6053595474377934655L;
	protected static final int drawr = 100;
	protected static final int drawsize = 20;
	private static final int drawwidth = 595;
	private static final int drawheight = 841;

	protected boolean ready = false;

	protected Set<String> representations;
	protected List<List<Integer>> automorphisms;

	protected String canonical;
	protected List<SortedSet<Integer>> orbits;
	protected boolean isOrbitRep;
	protected List<SortedSet<Integer>> cosetreps;

	public AbstractGraphlet(boolean isOrbitRep) {
		this.isOrbitRep = isOrbitRep;
	}

	public abstract <U extends AbstractGraphlet<T>> U copy();

	public abstract void swap(int a, int b);

	public abstract String representation();

	public abstract StringBuilder toPS();

	public int getSymmetry() {
		if (!ready) {
			permute();
		}
		return automorphisms.size();
	}

	public String canonical() {
		if (!ready) {
			permute();
		}
		return canonical;
	}

	public String name() {
		return (isOrbitRep ? "OrbitRep" : "Graphlet");
	}
	
	public boolean isOrbitRep() {
		return isOrbitRep;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((canonical == null) ? 0 : canonical.hashCode());
		result = prime * result + (isOrbitRep ? 1231 : 1237);
		result = prime * result + order;
		result = prime * result + size;
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
		AbstractGraphlet other = (AbstractGraphlet) obj;
		if (isOrbitRep != other.isOrbitRep)
			return false;
		if (order != other.order)
			return false;
		if (size != other.size)
			return false;
		if (canonical == null) {
			if (other.canonical != null)
				return false;
		} else if (!canonical.equals(other.canonical))
			return false;
		return true;
	}

	public boolean permute() {
		ready = true;
		cosetreps = null;
		if (isOrbitRep) {
			return permuteExcept(0);
		}
		return permuteExcept(new TreeSet<Integer>());
	}

	public boolean permuteExcept(int i) {
		SortedSet<Integer> l = new TreeSet<>();
		l.add(i);
		return permuteExcept(l);
	}

	public boolean permuteExcept(Collection<Integer> nodes) {
		automorphisms = new ArrayList<>();
		canonical = this.representation();
		boolean result = true;
		orbits = new ArrayList<>();
		List<Integer> current = new ArrayList<Integer>();
		for (int i = 0; i < order; i++) {
			orbits.add(new TreeSet<>());
			orbits.get(i).add(i);
			current.add(i);
		}
		automorphisms.add(new ArrayList<>(current));
		representations = new TreeSet<>();
		representations.add(canonical);
		if (order - nodes.size() > 1) {
			Permutator p = new Permutator(order - nodes.size());
			List<Integer> translation = new ArrayList<Integer>(order - nodes.size());
			for (int i = 0; i < order; i++) {
				if (!nodes.contains(i))
					translation.add(i);
			}
			String rep = canonical;
			int index = p.next();
			while (index >= 0) {
				swap(translation.get(index), translation.get(index + 1));
				int reserve = current.get(translation.get(index));
				current.set(translation.get(index), current.get(translation.get(index + 1)));
				current.set(translation.get(index + 1), reserve);
				String s = this.representation();
				representations.add(s);
				if (s.equals(rep)) {
					for (int i = 0; i < order; i++) {
						orbits.get(i).add(current.get(i));
					}
					automorphisms.add(new ArrayList<>(current));
				}
				if (canonical.compareTo( s) > 0) {
					canonical = s;
					result = false;
				}
				index = p.next();
			}
			swap(translation.get(0), translation.get(1));
		}
		return result;
	}

	public Set<SortedSet<Integer>> getOrbits() {
		if (!ready)
			permute();
		Set<SortedSet<Integer>> result = new HashSet<>();
		result.addAll(orbits);
		return result;
	}

	public Set<List<Integer>> getOrbitOf(List<Integer> nodes) {
		if (!ready)
			permute();
		Set<List<Integer>> result = new HashSet<>();
		for (List<Integer> auto : automorphisms) {
			List<Integer> orbit = new ArrayList<>();
			for (int i : nodes) {
				orbit.add(auto.get(i));
			}
			result.add(orbit);
		}
		return result;
	}

	public SortedSet<Integer> getOrbitOf(int node) {
		if (!ready)
			permute();
		return orbits.get(node);
	}

	protected StringBuilder drawNodes() {
		StringBuilder builder = new StringBuilder();

		for (int i = 0; i < order; i++) {
			builder.append("newpath\n");
			builder.append(x(i, 0) + " " + y(i, 0) + " " + 5 + " " + 0 + " " + 360 + " arc\n");
			builder.append("gsave\n");
			builder.append("stroke\n");
			if (!isOrbitRep || i != 0) {
				builder.append("grestore\n");
				builder.append("fill\n");
			}

			builder.append(x(i, drawsize) + " " + y(i, drawsize) + " moveto\n");
			builder.append("(" + i + ") show\n");
		}
		return builder;
	}

	protected int x(int i, int offset) {
		return (drawwidth / 2 + (int) Math.round((drawr + offset) * Math.cos(i * 2 * Math.PI / order)));
	}

	protected int y(int i, int offset) {
		return (drawheight / 2 + (int) Math.round((drawr + offset) * Math.sin(i * 2 * Math.PI / order)));
	}

	protected StringBuilder drawEdge(int i, int j) {
		StringBuilder builder = new StringBuilder();
		builder.append("newpath\n");
		builder.append(x(i, 0) + " " + y(i, 0) + " moveto\n");
		builder.append(x(j, 0) + " " + y(j, 0) + " lineto\n");
		builder.append("stroke\n");
		return builder;
	}

	protected StringBuilder drawOrbits() {
		StringBuilder builder = new StringBuilder();
		SortedSet<String> orbitSpec = new TreeSet<String>();
		for (SortedSet<Integer> orbit : getOrbits()) {
			orbitSpec.add(orbit.toString());
		}
		builder.append(10 + " " + 10 + " moveto\n");
		builder.append("(" + orbitSpec.toString() + ") show\n");
		return builder;
	}

	@Override
	public int compareTo(AbstractGraphlet<T> g) {
		if (canonical == null)
			permute();
		if (g.canonical == null)
			g.permute();
		return canonical.compareTo(g.canonical);
	}

	public List<SortedSet<Integer>> cosetreps() {
		if (!ready)
			permute();
		if (getSymmetry() == 1) {
			return orbits;
		} else if (cosetreps == null) {
			List<List<Integer>> automorphismCopy = new ArrayList<>(automorphisms);
			cosetreps = new ArrayList<>();
			for (int i = 0; i < order; i++) {
				cosetreps.add(new TreeSet<>());
				for (int j = 0; j < automorphismCopy.size(); j++) {
					cosetreps.get(i).add(automorphismCopy.get(j).get(i));
					if (automorphismCopy.get(j).get(i) != i) {
						automorphismCopy.remove(j);
						j--;
					}
				}
			}
		}
		return cosetreps;
	}

	public List<List<Integer>> getAutomorphisms() {
		return automorphisms;
	}
}
