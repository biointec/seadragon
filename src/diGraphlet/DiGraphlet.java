package diGraphlet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import graphlets.AbstractGraphlet;
import graphlets.IllegalGraphActionException;

public class DiGraphlet extends AbstractGraphlet<Boolean> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7721854624631535947L;
	private List<SortedSet<Integer>> arcs;

	public DiGraphlet(String matrix, boolean isOrbitRep) {
		super(isOrbitRep);
		order = Math.round((1f + (float) Math.sqrt(1f + 4f * matrix.length())) / 2f);
		size = 0;
		arcs = new ArrayList<>();
		for (int i = 0; i < order; i++) {
			arcs.add(new TreeSet<>());
			for (int j = 0; j < order - 1; j++) {
				int jj = j >= i ? j + 1 : j;
				if (matrix.charAt(i * (order - 1) + j)!='0') {
					arcs.get(i).add(jj);
					++size;
				}
			}
		}
	}

//	public DiGraphlet(List<Boolean> rep, boolean isOrbitRep) {
//		this(repToMatrix(rep), isOrbitRep);
//	}

//	private static boolean[] repToMatrix(List<Boolean> s) {
//		boolean[] result = new boolean[s.length()];
//		for (int i = 0; i < s.length(); i++) {
//			result[i] = (s.charAt(i) == '1');
//		}
//		return result;
//	}

	public DiGraphlet copy() {
		DiGraphlet result = new DiGraphlet(representation(), isOrbitRep);
		result.representations = representations;
		result.automorphisms = automorphisms;
		result.canonical = canonical;
		result.order = order;
		result.size = size;
		result.orbits = orbits;
		return result;
	}

	@Override
	public void swap(int a, int b) {
		int aa = Math.min(a, b);
		int bb = Math.max(a, b);
		for (Set<Integer> l : arcs) {
			if (l.contains(aa)) {
				if (!l.contains(bb)) {
					l.remove(aa);
					// int i = l.size() - 1;
					// while (i >= 0 && l.get(i) > bb) {
					// i--;
					// }
					l.add(bb);
				}
			} else if (l.contains(bb)) {
				l.remove(bb);
				// int i = 0;
				// while (i < l.size() && l.get(i) < aa) {
				// i++;
				// }
				l.add(aa);
			}
		}
		SortedSet<Integer> reserve = arcs.get(a);
		arcs.set(a, arcs.get(b));
		arcs.set(b, reserve);

	}

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
	public boolean isConnected() {
		Set<Integer> connected = new HashSet<Integer>();
		Queue<Integer> connect = new LinkedList<>();
		connected.add(0);
		connect.add(0);
		while (!connect.isEmpty() && connected.size() != order) {
			int current = connect.poll();
			for (int i : arcs.get(current)) {
				if (connected.add(i)) {
					connect.add(i);
				}
			}
			for (int i = 0; i < arcs.size(); i++) {
				if (arcs.get(i).contains(current) && connected.add(i)) {
					connect.add(i);
				}
			}
		}
		return connected.size() == order;
	}

	@Override
	public StringBuilder toPS() {

		StringBuilder builder = new StringBuilder();
		builder.append(drawNodes());

		for (int i = 0; i < order; i++) {
			for (int j : arcs.get(i)) {
				builder.append(drawEdge(i, j));
				// double angle = 0;
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
		// return null;
	}

	@Override
	public String representation() {
		String result = "";
		for (int i = 0; i < order; i++) {
			int index = 0;
			for (int j : arcs.get(i)) {
				while (index < j) {
					if (index != i)
//						result.add(false);
						result+=0;
					index++;
				}
//				result.add(true);
				result+=1;
				index++;
			}
			while (index < order) {
				if (index != i)
//					result.add(false);
					result+=0;
				index++;
			}

		}
		return result;
	}

	@Override
	public void addNode() {
		ready = false;
		order++;
		arcs.add(new TreeSet<>());
	}

	@Override
	public void addEdge(int i, int j, Boolean status) throws IllegalGraphActionException {
		ready = false;
		checkNode(i);
		checkNode(j);
		if (i == j) {
			throw new IllegalGraphActionException("No self-loops allowed");
		} else if (status ? arcs.get(i).contains(j) : arcs.get(j).contains(i)) {
			throw new IllegalGraphActionException("No double edges allowed between nodes " + i + " and " + j);
		} else if (status) {
			arcs.get(i).add(j);
		} else {
			arcs.get(j).add(i);
		}
		size++;

	}

	// @Override
	// public boolean areConnected(int a, int b) {
	// return arcs.get(a).contains(b);
	// }
	//
	// @Override
	// public Set<Integer> getNeighbours(int node) {
	// // TODO Auto-generated method stub
	// return null;
	// }

	@Override
	public SortedSet<Boolean> edgeTypes() {
		SortedSet<Boolean> result = new TreeSet<>();
		result.add(false);
		result.add(true);
		return result;
	}

	@Override
	public List<SortedSet<Boolean>> validEdges() {
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
	public SortedMap<Integer, List<Boolean>> getNeighbours(int node) {
		SortedMap<Integer, List<Boolean>> result = new TreeMap<>();
		for (int i : arcs.get(node)) {
			List<Boolean> l = new ArrayList<Boolean>();
			l.add(true);
			result.put(i, l);
		}
		for (int i = 0; i < order; i++) {
			if (arcs.get(i).contains(node)) {
				List<Boolean> l = result.get(i);
				if (l == null) {
					l = new ArrayList<Boolean>();
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
	public void removeNode(int i) throws IllegalGraphActionException {
		ready = false;
		arcs.remove(i);
		order--;
		for (int j = 0; j < order; j++) {
			arcs.get(j).remove(i);
			for (int k = i + 1; k < order; k++) {
				if (arcs.get(j).remove(k)) {
					arcs.get(j).add(k - 1);
				}
			}
		}

	}

	@Override
	public void removeEdge(int i, int j) throws IllegalGraphActionException {
		ready = false;
		checkNode(i);
		checkNode(j);
		checkEdge(i, j);
		if (arcs.get(i).remove(j))
			size--;
		if (arcs.get(j).remove(i))
			size--;
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
	public void removeEdge(int i, int j, Boolean type) throws IllegalGraphActionException {
		checkNode(i);
		checkNode(j);
		if(!getEdges(i,j).contains(type)) {
			throw new IllegalGraphActionException("No edge present from "+(type?i:j) +" to "+(type?j:i));
		}
		
	}
	
	public boolean isComplete() {
		return size == order*(order-1);
	}

}