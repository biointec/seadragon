package genGraphlet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import graphlets.AbstractGraphlet;
import graphs.IllegalGraphActionException;

public class GenGraphlet extends AbstractGraphlet<Byte> {

	byte[][] matrix;

	public GenGraphlet(String representation,boolean isOrbitRep) {
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

	public String toString() {
		String result = "";
		for (byte[] line : matrix) {
			for (byte b : line) {
				if(b==1) {
					result+="+";
				}else if(b==2) {
					result+="-";
				}else if(b==0) {
					result+="0";
				}
			}
			result += "\n";
		}
		return result;
		// return result.substring(0,result.length()-1);
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
				} else if (matrix[i][j] == '-') {
					builder.append(drawEdge(i, j));
					// double angle = 0;
					double angle = 180. / order * (j - i) + 360. / order * i + (i < j ? 180 : 0);
					builder.append(x(i, 0) + " " + y(i, 0) + " translate\n");
					builder.append((int) Math.round(angle) + " rotate\n");
					int arrowheight = 30;
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
	public boolean isConnected() {
		List<Integer> result = new ArrayList<>();
		result.add(0);
		for (int i = 0; i < result.size(); i++) {
			for (int j = 0; j < order; j++) {
				if ((matrix[result.get(i)][j] != 0|| matrix[j][result.get(i)] != 0)&& !result.contains(j)) {
					result.add(j);
				}
			}
		}
		return result.size() == order;
	}

	@Override
	public void addNode() {
		byte[][] oldMatrix = matrix;
		order++;
		matrix = new byte[order][order];
		for (int i = 0; i < order - 1; i++) {
			for (int j = 0; j < order - 1; j++) {
				matrix[i][j] = oldMatrix[i][j];
			}
			matrix[i][order - 1] = 0;
			matrix[order - 1][i] = 0;
		}
		matrix[order - 1][order - 1] = 0;
		ready=false;
	}

	@Override
	public void removeNode(int i) throws IllegalGraphActionException {
		checkNode(i);
		byte[][] oldMatrix = matrix;
		for(int j=0;j<order;j++) {
			if(oldMatrix[i][j]!=0) {
				size--;
			}
			if(oldMatrix[j][i]!=0) {
				size--;
			}
		}
		order--;
		matrix = new byte[order][order];
		for (int k = 0; k < order; k++) {
			for (int j = 0; j < order; j++) {
				matrix[k][j] = oldMatrix[k + (k >= i ? 1 : 0)][j + (j >= i ? 1 : 0)];
			}
		}

		ready=false;
	}

	@Override
	public void addEdge(int i, int j, Byte status) throws IllegalGraphActionException {
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
				size++;
				break;
			case 2:
			case '-':
				matrix[i][j] = 2;
				size++;
				break;
			default:
				throw new IllegalGraphActionException("Invalid edge type.");
			}
		}
		ready=false;

	}

	@Override
	public void removeEdge(int i, int j) throws IllegalGraphActionException {
		checkNode(i);
		checkNode(j);
		checkEdge(i, j);
		if (matrix[i][j] != 0) {
			matrix[i][j] = 0;
			size--;
		}
		if (matrix[j][i] != 0) {
			matrix[j][i] = 0;
			size--;
		}
		ready=false;
	}

	@Override
	public void removeEdge(int i, int j, Byte type) throws IllegalGraphActionException {
		checkNode(i);
		checkNode(j);

		if (type < 0) {
			type = (byte) (-type);
			int reserve = i;
			i = j;
			j = reserve;
		}
		if (matrix[i][j] == type) {
			matrix[i][j] = 0;
			size--;
		} else {
			throw new IllegalGraphActionException(
					"No edge of type " + type + " present between node " + i + " and " + j);
		}

		ready=false;
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
				part.add((byte)-matrix[node][i]);
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
	public List<Byte> edgeTypes() {
		List<Byte> result = new ArrayList<>();
		result.add((byte) 1);
		result.add((byte) -1);
		result.add((byte) 2);
		result.add((byte) -2);
		return result;
	}

	@Override
	public List<SortedSet<Byte>> validEdges() {
		List<SortedSet<Byte>> result = new ArrayList<>();
		byte[][] options = { { 0, -1, -2 }, { 0, 1, 2 } };
		for (int i = 1; i < 9; i++) {
			int a = i % 3;
			int b = i / 3;
			SortedSet<Byte> piece = new TreeSet<>();
			if (options[0][a] != 0) {
				piece.add(options[0][a]);
			}
			if (options[1][b] != 0) {
				piece.add(options[1][b]);
			}
			result.add(piece);
		}
		return result;
	}

	@Override
	public boolean isComplete() {
		return size == order * (order - 1);
	}
	
	@Override
	public SortedSet<Integer> getInvertedNeighbours(int node, Byte condition) {
		return getNeighbours(node,(byte) -condition);
	}

}
