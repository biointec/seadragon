package graphlets.coGraphlet;

import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import graphletgeneration.AbstractGraphletFactory;
import graphlets.AbstractGraphlet;
import graphlets.IllegalGraphActionException;
import graphlets.genGraphlet.GenGraphletFactory;

/**
 * Graphlet with coloured, undirected edges. Up to 9 edge colours can be used,
 * and only a single edge of any colour can be present between any pair of
 * nodes.
 * 
 * The colours are represented by integers. Since they also must correspond to a
 * single character (to work with the string representation), only the integers
 * <code>1</code> to <code>9</code> can be used.
 * 
 * @author Ine Melckenbeeck
 *
 */
public class CoGraphlet extends AbstractGraphlet<Integer> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1887726067135000527L;
	int nColors;
	private int[][] matrix;
//	private float[][] colors;

	public CoGraphlet(String representation, boolean isOrbitRep) {
		super(isOrbitRep);
		order = (1 + (int) Math.sqrt(1 + 8 * representation.length())) / 2;
		matrix = new int[order][];
		for (int i = 0; i < order; i++) {
			matrix[i] = new int[i];
			for (int j = 0; j < i; j++) {
				matrix[i][j] = representation.charAt((i * (i - 1)) / 2 + j) - '0';
				if (matrix[i][j] != 0)
					size++;
			}
		}
	}

	@Override
	public void swap(int a, int b) {
		int aa = Math.min(a, b);
		int bb = Math.max(a, b);
		for (int i = 0; i < aa; i++) {
			int reserve = matrix[bb][i];
			matrix[bb][i] = matrix[aa][i];
			matrix[aa][i] = reserve;
		}
		for (int i = aa + 1; i < bb; i++) {
			int reserve = matrix[bb][i];
			matrix[bb][i] = matrix[i][aa];
			matrix[i][aa] = reserve;
		}
		for (int i = bb + 1; i < order; i++) {
			int reserve = matrix[i][bb];
			matrix[i][bb] = matrix[i][aa];
			matrix[i][aa] = reserve;
		}
	}

	@Override
	public String representation() {
		String s = "";
		for (int i = 0; i < order; i++) {
			for (int j = 0; j < i; j++) {
				s += matrix[i][j];
			}
		}
		return s;
	}

	@Override
	public StringBuilder toPS() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void addNodeInternal() {
		int[][] newmatrix = new int[order + 1][];
		for (int i = 0; i < order; i++) {
			newmatrix[i] = new int[i];
			for (int j = 0; j < i; j++) {
				newmatrix[i][j] = matrix[i][j];
			}
		}
		newmatrix[order] = new int[order];
		matrix = newmatrix;
	}

	@Override
	protected void removeNodeInternal(int node) throws IllegalGraphActionException {
		int[][] newmatrix = new int[order - 1][];
		for (int i = 0; i < order - 1; i++) {
			newmatrix[i] = new int[i];
			for (int j = 0; j < i; j++) {
				newmatrix[i][j] = matrix[i][j];
			}
		}
		matrix = newmatrix;
	}

	@Override
	protected void addEdgeInternal(int node1, int node2, Integer type) throws IllegalGraphActionException {
		checkNode(node1);
		checkNode(node2);
		int a = Math.min(node1, node2);
		int b = Math.max(node2, node1);
		if (matrix[b][a] != 0) {
			throw new IllegalGraphActionException("No double edges allowed.");
		} else {
			matrix[b][a] = type;
		}

	}

	@Override
	protected void removeEdgeInternal(int node1, int node2) throws IllegalGraphActionException {
		checkNode(node1);
		checkNode(node2);
		checkEdge(node1, node2);
		int a = Math.min(node1, node2);
		int b = Math.max(node2, node1);
		matrix[b][a] = 0;
	}

	@Override
	public void removeEdgeInternal(int node1, int node2, Integer type) throws IllegalGraphActionException {
		checkNode(node1);
		checkNode(node2);
		int a = Math.min(node1, node2);
		int b = Math.max(node2, node1);
		if (matrix[b][a] != type) {
			throw new IllegalGraphActionException(
					"No edge of type " + type + " present between nodes " + node1 + " and " + node2);
		} else {
			matrix[b][a] = 0;
		}
	}

	@Override
	public SortedSet<Integer> getEdges(int i, int j) throws IllegalGraphActionException {
		checkNode(i);
		checkNode(j);
		SortedSet<Integer> result = new TreeSet<>();
		int a = Math.min(i, j);
		int b = Math.max(i, j);
		if (matrix[b][a] != 0) {
			result.add(matrix[b][a]);
		}
		return result;
	}

	@Override
	public SortedSet<Integer> getNeighbours(int node, Integer type) {
		SortedSet<Integer> result = new TreeSet<>();
		for (int i = 0; i < node; i++) {
			if (matrix[node][i] == type) {
				result.add(i);
			}
		}
		for (int i = node + 1; i < order; i++) {
			if (matrix[i][node] == type) {
				result.add(i);
			}
		}
		return result;
	}

	@Override
	public SortedSet<Integer> getInvertedNeighbours(int node, Integer type) {
		return getNeighbours(node, type);
	}

	@Override
	public SortedMap<Integer, SortedSet<Integer>> getNeighbours(int node) {
		// System.out.println(Arrays.deepToString(matrix));
		SortedMap<Integer, SortedSet<Integer>> result = new TreeMap<>();
		for (int i = 0; i < node; i++) {
			if (matrix[node][i] != 0) {
				if (!result.containsKey(i)) {
					result.put(i, new TreeSet<>());
				}
				result.get(i).add(matrix[node][i]);
			}
		}
		for (int i = node + 1; i < order; i++) {
			if (matrix[i][node] != 0) {
				if (!result.containsKey(i)) {
					result.put(i, new TreeSet<>());
				}
				result.get(i).add(matrix[i][node]);
			}
		}
		return result;
	}

	@Override
	public double density() {
		return 2. * size / order / (order - 1.);
	}

	@Override
	public boolean isComplete() {
		return (2 * size == order * (order - 1));
	}

	// @Override
	// public StringBuilder toPS() {
	// StringBuilder result = drawNodes();
	// for (int i = 1; i < order; i++) {
	// for (int j = 0; j < i; j++) {
	// if (matrix[i][j] != 0) {
	// result.append(colors[matrix[i][j] - 1][0]);
	// result.append(" ");
	// result.append(colors[matrix[i][j] - 1][1]);
	// result.append(" ");
	// result.append(colors[matrix[i][j] - 1][2]);
	// result.append(" setrgbcolor\n");
	// result.append(drawEdge(i, j));
	// }
	// }
	// }
	// result.append("0 0 0 setrgbcolor\n");
	// result.append(drawOrbits());
	// result.append("showpage\n");
	// return result;
	// }
	//
	

	@Override
	public AbstractGraphletFactory<? extends AbstractGraphlet<Integer>, Integer> getGraphletType(boolean useOrbits) {
		return new CoGraphletFactory(nColors,useOrbits);
	}
}
