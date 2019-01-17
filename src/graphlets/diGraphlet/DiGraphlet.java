package graphlets.diGraphlet;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import graphletgeneration.AbstractGraphletFactory;
import graphlets.AbstractGraphlet;
import graphlets.IllegalGraphActionException;

/**
 * Implementation of AbstractGraphlet for a simple, directed graphlet. In this
 * graphlet, two parallel edges may exist only if they have opposite direction.
 * The graphlets are internally represented as an adjacency list.
 * 
 * Booleans are used as edge type. If an edge between node1 and node2 has
 * <code>true</code> as edge type, it means the edge runs from node1 to node2.
 * Vice versa, if its edge type is <code>false</code>, the edge runs from node2
 * to node1.
 * 
 * @author Ine Melckenbeeck
 *
 */
public class DiGraphlet extends AbstractGraphlet<Boolean> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7721854624631535947L;
	private List<SortedSet<Integer>> arcs;

	/**
	 * Create a directed graphlet with the given representation.
	 * 
	 * @param representation
	 *            The representation of the new graphlet.
	 * @param isOrbitRep
	 */
	public DiGraphlet(String representation, boolean isOrbitRep) {
		super(isOrbitRep);
		order = Math.round((1f + (float) Math.sqrt(1f + 4f * representation.length())) / 2f);
		size = 0;
		arcs = new ArrayList<>();
		for (int i = 0; i < order; i++) {
			arcs.add(new TreeSet<>());
			for (int j = 0; j < order - 1; j++) {
				int jj = j >= i ? j + 1 : j;
				if (representation.charAt(i * (order - 1) + j) != '0') {
					arcs.get(i).add(jj);
					++size;
				}
			}
		}
	}

	@Override
	public void swap(int a, int b) {
		int aa = Math.min(a, b);
		int bb = Math.max(a, b);
		for (Set<Integer> l : arcs) {
			if (l.contains(aa)) {
				if (!l.contains(bb)) {
					l.remove(aa);
					l.add(bb);
				}
			} else if (l.contains(bb)) {
				l.remove(bb);
				l.add(aa);
			}
		}
		SortedSet<Integer> reserve = arcs.get(a);
		arcs.set(a, arcs.get(b));
		arcs.set(b, reserve);
	}

	@Override
	public String toString() {
		return arcs.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((arcs == null) ? 0 : arcs.hashCode());
		result = prime * result + order;
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
		DiGraphlet other = (DiGraphlet) obj;
		if (order != other.order)
			return false;
		if (canonical != null && other.canonical != null) {
			return true;
		}
		if (arcs == null) {
			if (other.arcs != null)
				return false;
		} else if (!arcs.equals(other.arcs))
			return false;
		return true;
	}

	@Override
	public StringBuilder toPS() {

		StringBuilder builder = new StringBuilder();
		builder.append(drawNodes());

		for (int i = 0; i < order; i++) {
			for (int j : arcs.get(i)) {
				builder.append(drawEdge(i, j));
				double angle = 180. / order * (j - i) + 360. / order * i + (i < j ? 180 : 0);
				builder.append(x(i, 0) + " " + y(i, 0) + " translate\n");
				builder.append((int) Math.round(angle) + " rotate\n");
				int arrowheight = 30;
				int arrowwidth = 15;
				builder.append("newpath\n");
				builder.append("0 0 moveto\n");
				builder.append(arrowwidth / 2 + " " + -arrowheight + " rlineto\n");
				builder.append(-arrowwidth + " 0 rlineto\n");
				builder.append("closepath\n");
				builder.append("gsave\n");
				builder.append("stroke\n");
				builder.append("grestore\n");
				builder.append("fill\n");
				builder.append(-(int) Math.round(angle) + " rotate\n");
				builder.append(-x(i, 0) + " " + -y(i, 0) + " translate\n");
			}
		}
		builder.append(drawOrbits());
		builder.append("showpage\n");
		return builder;
	}

	@Override
	public String representation() {
		String result = "";
		for (int i = 0; i < order; i++) {
			int index = 0;
			for (int j : arcs.get(i)) {
				while (index < j) {
					if (index != i)
						result += 0;
					index++;
				}
				result += 1;
				index++;
			}
			while (index < order) {
				if (index != i)
					result += 0;
				index++;
			}

		}
		return result;
	}

	@Override
	public void addNodeInternal() {
		arcs.add(new TreeSet<>());
	}

	@Override
	public double density() {
		return (double) size / (order * (order - 1.));
	}

	@Override
	public SortedSet<Integer> getNeighbours(int node, Boolean condition) {
		if (condition) {
			return arcs.get(node);
		} else {
			SortedSet<Integer> result = new TreeSet<>();
			for (int i = 0; i < order; i++) {
				if (arcs.get(i).contains(node)) {
					result.add(i);
				}
			}
			return result;
		}
	}

	@Override
	public SortedMap<Integer, SortedSet<Boolean>> getNeighbours(int node) {
		SortedMap<Integer, SortedSet<Boolean>> result = new TreeMap<>();
		for (int i : arcs.get(node)) {
			SortedSet<Boolean> l = new TreeSet<>();
			l.add(true);
			result.put(i, l);
		}
		for (int i = 0; i < order; i++) {
			if (arcs.get(i).contains(node)) {
				SortedSet<Boolean> l = result.get(i);
				if (l == null) {
					l = new TreeSet<>();
				}
				l.add(false);
				result.put(i, l);
			}
		}
		return result;
	}

	@Override
	public String name() {
		return "Di" + super.name();
	}

	@Override
	public void removeNodeInternal(int i) throws IllegalGraphActionException {
		checkNode(i);
		ready = false;
		size -= arcs.get(i).size();
		arcs.remove(i);
		// order--;
		for (int j = 0; j < order - 1; j++) {
			if (arcs.get(j).remove(i)) {
			}
			for (int k = i + 1; k < order - 1; k++) {
				if (arcs.get(j).remove(k)) {
					arcs.get(j).add(k - 1);
				}
			}
		}

	}

	@Override
	public void removeEdgeInternal(int i, int j) throws IllegalGraphActionException {
		checkNode(i);
		checkNode(j);
		checkEdge(i, j);
		arcs.get(i).remove(j);
		arcs.get(j).remove(i);
	}

	@Override
	public SortedSet<Boolean> getEdges(int i, int j) throws IllegalGraphActionException {
		checkNode(i);
		checkNode(j);
		SortedSet<Boolean> result = new TreeSet<>();
		if (arcs.get(i).contains(j))
			result.add(true);
		if (arcs.get(j).contains(i))
			result.add(false);
		return result;
	}

	@Override
	public void addEdgeInternal(int i, int j, Boolean edgeType) throws IllegalGraphActionException {
		checkNode(i);
		checkNode(j);
		if (i == j) {
			throw new IllegalGraphActionException("No self-loops allowed");
		} else if (edgeType ? arcs.get(i).contains(j) : arcs.get(j).contains(i)) {
			throw new IllegalGraphActionException("No double edges allowed between nodes " + i + " and " + j);
		} else if (edgeType) {
			arcs.get(i).add(j);
		} else {
			arcs.get(j).add(i);
		}
	}

	@Override
	public void removeEdgeInternal(int i, int j, Boolean type) throws IllegalGraphActionException {
		checkNode(i);
		checkNode(j);
		if (!getEdges(i, j).contains(type)) {
			throw new IllegalGraphActionException("No edge present from " + (type ? i : j) + " to " + (type ? j : i));
		} else if (type) {
			assert (arcs.get(i).remove(j));
		} else {
			assert (arcs.get(j).remove(i));
		}
	}

	@Override
	public boolean isComplete() {
		return size == order * (order - 1);
	}

	@Override
	public SortedSet<Integer> getInvertedNeighbours(int node, Boolean condition) {
		return getNeighbours(node, !condition);
	}
	
	@Override
	public AbstractGraphletFactory<? extends AbstractGraphlet<Boolean>, Boolean> getGraphletType(boolean useOrbits) {
		return new DiGraphletFactory( useOrbits);
	}
}
