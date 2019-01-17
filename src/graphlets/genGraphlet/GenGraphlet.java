package graphlets.genGraphlet;

import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import graphletgeneration.AbstractGraphletFactory;
import graphlets.AbstractGraphlet;
import graphlets.IllegalGraphActionException;
import graphlets.diGraphlet.DiGraphletFactory;

/**
 * Genetic Graphlet, i.e. a signed, directed graphlet. Edges in this graphlet
 * are directed, and carry either a positive (+) or negative (-) sign.
 * Antiparallel edges may exist, regardless of their respective signs, but
 * parallel ones may not.
 * 
 * Bytes are used to represent the different edge types.
 * 
 * @author Ine Melckenbeeck
 *
 */
public class GenGraphlet extends AbstractGraphlet<Byte> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5796007812371156641L;
	byte[][] matrix;

	public GenGraphlet(String representation, boolean isOrbitRep) {
		super(isOrbitRep);
		order = (1 + (int) Math.sqrt(1 + 4 * representation.length())) / 2;
		matrix = new byte[order][order];
		for (int i = 0; i < representation.length(); i++) {
			int x = i / (order - 1);
			int y = i % (order - 1) + ((i % (order - 1) >= x) ? 1 : 0);
			switch (representation.charAt(i)) {
			case '+':
			case '1':
				matrix[x][y] = 1;
				size++;
				break;
			case '-':
			case '2':
				matrix[x][y] = 2;
				size++;
				break;
			default:
				matrix[x][y] = 0;
			}
		}
		for (int i = 0; i < order; i++) {
			matrix[i][i] = 0;
		}
	}

	@Override
	public String toString() {
		String result = "";
		for (byte[] line : matrix) {
			for (byte b : line) {
				if (b == 1) {
					result += "+";
				} else if (b == 2) {
					result += "-";
				} else if (b == 0) {
					result += "0";
				}
			}
			result += "\n";
		}
		return result;
	}

	@Override
	public void swap(int a, int b) {
		for (int i = 0; i < order; i++) {
			byte reserve = matrix[a][i];
			matrix[a][i] = matrix[b][i];
			matrix[b][i] = reserve;
		}
		for (int i = 0; i < order; i++) {
			byte reserve = matrix[i][a];
			matrix[i][a] = matrix[i][b];
			matrix[i][b] = reserve;
		}

	}

	@Override
	public String representation() {
		String result = "";
		for (int i = 0; i < order; i++) {
			for (int j = 0; j < order; j++) {
				if (i != j) {
					switch (matrix[i][j]) {
					case 1:
						result += '+';
						break;
					case 2:
						result += '-';
						break;
					case 0:
						result += '0';
					}
				}
			}
		}
		return result;
	}

	@Override
	public StringBuilder toPS() {

		StringBuilder builder = new StringBuilder();
		builder.append(drawNodes());

		for (int i = 0; i < order; i++) {
			for (int j = 0; j < order; j++) {
				if (matrix[i][j] == '+') {
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
				} else if (matrix[i][j] == '-') {
					builder.append(drawEdge(i, j));
					double angle = 180. / order * (j - i) + 360. / order * i + (i < j ? 180 : 0);
					builder.append(x(i, 0) + " " + y(i, 0) + " translate\n");
					builder.append((int) Math.round(angle) + " rotate\n");
					int arrowwidth = 15;
					builder.append("newpath\n-");
					builder.append(arrowwidth);
					builder.append(" ");
					builder.append(drawr);
					builder.append("moveto\n");
					builder.append(arrowwidth);
					builder.append(" ");
					builder.append(drawr);
					builder.append("lineto\n");
					builder.append("stroke\n");
					builder.append(-(int) Math.round(angle) + " rotate\n");
					builder.append(-x(i, 0) + " " + -y(i, 0) + " translate\n");
				}
			}
		}
		builder.append(drawOrbits());
		builder.append("showpage\n");
		return builder;
	}

	@Override
	public void addNodeInternal() {
		byte[][] oldMatrix = matrix;
		matrix = new byte[order + 1][order + 1];
		for (int i = 0; i < order; i++) {
			for (int j = 0; j < order; j++) {
				matrix[i][j] = oldMatrix[i][j];
			}
			matrix[i][order] = 0;
			matrix[order][i] = 0;
		}
		matrix[order][order] = 0;
	}

	@Override
	public void removeNodeInternal(int i) throws IllegalGraphActionException {
		checkNode(i);
		byte[][] oldMatrix = matrix;
		matrix = new byte[order - 1][order - 1];
		for (int k = 0; k < order - 1; k++) {
			for (int j = 0; j < order - 1; j++) {
				matrix[k][j] = oldMatrix[k + (k >= i ? 1 : 0)][j + (j >= i ? 1 : 0)];
			}
		}
	}

	@Override
	public void addEdgeInternal(int i, int j, Byte status) throws IllegalGraphActionException {
		checkNode(i);
		checkNode(j);
		if (status < 0) {
			status = (byte) (-status);
			int reserve = i;
			i = j;
			j = reserve;
		}
		if (matrix[i][j] != 0) {
			throw new IllegalGraphActionException("No parallel edges allowed.");
		} else {
			switch (status) {
			case 1:
			case '+':
				matrix[i][j] = 1;
				break;
			case 2:
			case '-':
				matrix[i][j] = 2;
				break;
			default:
				throw new IllegalGraphActionException("Invalid edge type.");
			}
		}
	}

	@Override
	public void removeEdgeInternal(int i, int j) throws IllegalGraphActionException {
		checkNode(i);
		checkNode(j);
		checkEdge(i, j);
		if (matrix[i][j] != 0) {
			matrix[i][j] = 0;
		}
		if (matrix[j][i] != 0) {
			matrix[j][i] = 0;
		}
	}

	@Override
	public void removeEdgeInternal(int i, int j, Byte type) throws IllegalGraphActionException {
		if (type < 0) {
			type = (byte) (-type);
			int reserve = i;
			i = j;
			j = reserve;
		}
		if (matrix[i][j] == type) {
			matrix[i][j] = 0;
		} else {
			throw new IllegalGraphActionException(
					"No edge of type " + type + " present between node " + i + " and " + j);
		}
	}

	@Override
	public SortedSet<Byte> getEdges(int i, int j) throws IllegalGraphActionException {
		checkNode(i);
		checkNode(j);
		SortedSet<Byte> result = new TreeSet<>();
		if (matrix[i][j] != 0) {
			result.add(matrix[i][j]);
		}
		if (matrix[j][i] != 0) {
			result.add((byte) -matrix[j][i]);
		}
		return result;
	}

	@Override
	public SortedSet<Integer> getNeighbours(int node, Byte condition) {
		SortedSet<Integer> result = new TreeSet<>();
		if (condition < 0) {
			for (int i = 0; i < order; i++) {
				if (matrix[i][node] == (byte) -condition) {
					result.add(i);
				}
			}

		} else {
			for (int i = 0; i < order; i++) {
				if (matrix[node][i] == condition) {
					result.add(i);
				}
			}
		}
		return result;
	}

	@Override
	public SortedMap<Integer, SortedSet<Byte>> getNeighbours(int node) {
		SortedMap<Integer, SortedSet<Byte>> result = new TreeMap<>();
		for (int i = 0; i < order; i++) {
			SortedSet<Byte> part = new TreeSet<>();
			if (matrix[i][node] != 0) {
				part.add(matrix[i][node]);
			}
			if (matrix[node][i] != 0) {
				part.add((byte) -matrix[node][i]);
			}
			if (part.size() != 0) {
				result.put(i, part);
			}
		}
		return result;
	}

	@Override
	public double density() {
		return size / (order - 1.) / order;
	}

	@Override
	public boolean isComplete() {
		return size == order * (order - 1);
	}

	@Override
	public SortedSet<Integer> getInvertedNeighbours(int node, Byte condition) {
		return getNeighbours(node, (byte) -condition);
	}

	@Override
	public AbstractGraphletFactory<? extends AbstractGraphlet<Byte>, Byte> getGraphletType(boolean useOrbits) {
		return new GenGraphletFactory( useOrbits);
	}
}
