package graphlets;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import graphletgeneration.Permutator;
import graphlets.diGraphlet.DiGraphlet;

/**
 * Abstract class representing graphlets of any kind.
 * 
 * @author Ine Melckenbeeck
 *
 * @param <T>
 *            The type that is used to identify the different edge types that
 *            can be added to graphlets of this type.
 */
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
	protected boolean isOrbitRep;
	protected List<List<Integer>> automorphisms;
	protected List<Integer> canonicalAutomorphism;
	protected String canonical;
	protected List<SortedSet<Integer>> orbits;
	protected List<SortedSet<Integer>> cosetreps;

	public AbstractGraphlet(boolean isOrbitRep) {
		this.isOrbitRep = isOrbitRep;
	}

	@Override
	public void addNode() {
		super.addNode();
		ready = false;
	}

	@Override
	public void removeNode(int node) throws IllegalGraphActionException {
		super.removeNode(node);
		ready = false;
	}

	@Override
	public void addEdge(int node1, int node2, T edgeTypes) throws IllegalGraphActionException {
		super.addEdge(node1, node2, edgeTypes);
		ready = false;
	}

	@Override
	public void removeEdge(int node1, int node2) throws IllegalGraphActionException {
		super.removeEdge(node1, node2);
		ready = false;
	}

	@Override
	public void removeEdge(int node1, int node2, T edgeTypes) throws IllegalGraphActionException {
		super.removeEdge(node1, node2, edgeTypes);
		ready = false;
	}

	/**
	 * <p>
	 * Swaps the indices of the two given nodes. All edges to or from a, go to or
	 * from b after the swap and vice versa.
	 * </p>
	 * <p>
	 * Used in the permutation of the nodes during symmetry calculation.
	 * 
	 * @param node1
	 *            The first node to be swapped.
	 * @param node2
	 *            The second node to be swapped.
	 */
	public abstract void swap(int node1, int node2);

	public List<Integer> getCanonicalAutomorphism() {
		if (canonicalAutomorphism == null) {
			permute();
		}
		return canonicalAutomorphism;
	}

	/**
	 * Transforms the graphlet in a string representation that can be used to
	 * reconstruct the graphlet. This representation should be a listing of the
	 * graphlet's edges in some predetermined order, with the character '0'
	 * referring to the absence of an edge.
	 * 
	 * @return This graphlet's string representation.
	 */
	public abstract String representation();

	/**
	 * Returns the graphlet's canonical representation, i.e. the lowest possible
	 * representation of this graphlet.
	 * 
	 * @return
	 */
	public String canonical() {
		if (!ready) {
			permute();
		}
		return canonical;
	}

	/**
	 * Returns <code>true</code> if the graphlet is in its canonical form, i.e. its
	 * representation is the canonical representation.
	 * 
	 * @return <code>true</code> if the graphlet is in its canonical form.
	 */
	public boolean isCanonical() {
		if (!ready) {
			return permute();
		} else {
			return canonical.equals(representation());
		}
	}

	/**
	 * Write the graphlet in PostScript format for automatic visualisation.
	 * 
	 * @return a StringBuilder containing the graphlet in PS format.
	 */
	public abstract StringBuilder toPS();

	/**
	 * Returns the graphlet's symmetry factor, i.e. its number of automorphisms.
	 * 
	 * @return the graphlet's symmetry factor.
	 */
	public int getSymmetry() {
		if (!ready) {
			permute();
		}
		return automorphisms.size();
	}

	/**
	 * Names this graphlet type, for use in export files.
	 * 
	 * @return The graphlet type's name.
	 */
	public String name() {
		return (isOrbitRep ? "OrbitRep" : "Graphlet");
	}

	/**
	 * Returns <code>true<\code> if this graphlet is an orbit representative. Orbit
	 * representatives' 0th node is never permuted, making each orbit representative
	 * equivalent with one orbit of its base graphlet.
	 * 
	 * @return <code>true<\code> if this graphlet is an orbit representative.
	 */
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
		@SuppressWarnings("unchecked")
		AbstractGraphlet<T> other = (AbstractGraphlet<T>) obj;
		if (isOrbitRep != other.isOrbitRep)
			return false;
		if (order != other.order)
			return false;
		if (size != other.size)
			return false;
		if (canonical == null)
			permute();
		if (!canonical.equals(other.canonical))
			return false;
		return true;
	}

	/**
	 * Permute the graphlet's nodes, calculating its automorphisms, orbits and
	 * canonical form.
	 * 
	 * @return <code>true</code> if this graphlet is canonical.
	 */
	public boolean permute() {
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
		canonicalAutomorphism = new ArrayList<>(current);
		automorphisms.add(new ArrayList<>(current));
		int o = (isOrbitRep ? 1 : 0);
		if (order - o > 1) {
			Permutator p = new Permutator(order - o);
			String rep = canonical;
			int index = p.next();
			while (index >= 0) {
				swap(index + o, index + o + 1);
				int reserve = current.get(index + o);
				current.set(index + o, current.get(index + o + 1));
				current.set(index + o + 1, reserve);
				String s = this.representation();
				if (s.equals(rep)) {
					for (int i = 0; i < order; i++) {
						orbits.get(i).add(current.get(i));
					}
					automorphisms.add(new ArrayList<>(current));
				}
				if (new CanonicalComparator().compare(canonical, s) > 0) {
					canonical = s;
					result = false;
					canonicalAutomorphism = new ArrayList<>(current);
				}
				index = p.next();
			}
			swap(o, o + 1);
		}
		return result;
	}

	/**
	 * Returns a Set containing the graphlet's automorphism orbits. These are the
	 * sets of nodes that can be mapped onto each other by any of the graphlet's
	 * automorphisms.
	 * 
	 * @return the graphlet's orbits.
	 */
	public Set<SortedSet<Integer>> getOrbits() {
		if (!ready)
			permute();
		Set<SortedSet<Integer>> result = new HashSet<>();
		result.addAll(orbits);
		return result;
	}

	public Set<List<Set<Integer>>> getOrbitOf(List<Set<Integer>> nodes) {
		if (!ready)
			permute();
		Set<List<Set<Integer>>> result = new HashSet<>();
		for (List<Integer> auto : automorphisms) {
			List<Set<Integer>> sub = new ArrayList<>();
			for (Set<Integer> set : nodes) {
				Set<Integer> orbit = new TreeSet<>();
				for (int i : set) {
					orbit.add(auto.get(i));
				}
				sub.add(orbit);
			}
			result.add(sub);
		}
		return result;
	}

	/**
	 * Returns the orbit of the given node.
	 * 
	 * @param node
	 *            the node whose orbit is given.
	 * @return the orbit of the given node.
	 */
	public SortedSet<Integer> getOrbitOf(int node) {
		if (!ready)
			permute();
		return orbits.get(node);
	}

	@Override
	public int compareTo(AbstractGraphlet<T> graphlet) {
		if (canonical == null)
			permute();
		if (graphlet.canonical == null)
			graphlet.permute();
		return new CanonicalComparator().compare(canonical, graphlet.canonical);
	}

//	public static void main(String[]args) {
//		System.out.println(new DiGraphlet("001100",false).getCosetReps());
//	}
	
	/**
	 * Returns the coset representatives of this graphlet's automorphisms. These are
	 * used to impose symmetry-breaking constraints on the graphlet's nodes, which
	 * is used to avoid counting one instance of a graphlet multiple times.
	 * 
	 * @return the coset representatives of this graphlet's automorphisms.
	 */
	public List<SortedSet<Integer>> getCosetReps() {
		if (!ready) {
			permute();
			cosetreps = null;}
		if (getSymmetry() == 1) {
			return orbits;
		} else if (cosetreps == null) {
			List<List<Integer>> automorphismCopy = new ArrayList<>(automorphisms);
			cosetreps = new ArrayList<>();
			for (int i = 0; i < order; i++) {
				cosetreps.add(new TreeSet<>());
				for (int j = automorphismCopy.size()-1; j >= 0; j--) {
					cosetreps.get(i).add(automorphismCopy.get(j).get(i));
					if (automorphismCopy.get(j).get(i) != i) {
						automorphismCopy.remove(j);
//						j--;
					}
				}
			}
		}
		return cosetreps;
	}

	/**
	 * Returns the automorphisms of this graphlet.
	 * 
	 * @return the automorphisms of this graphlet.
	 */
	public List<List<Integer>> getAutomorphisms() {
		if (!ready)
			permute();
		return automorphisms;
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

}
