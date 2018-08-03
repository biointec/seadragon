package genGraphlet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeSet;

import graphlets.AbstractGraphlet;
import graphlets.IllegalGraphActionException;

public class GenGraphlet extends AbstractGraphlet<Byte> {

	byte[][] matrix;

	public GenGraphlet(boolean isOrbitRep, String representation) {
		super(isOrbitRep);
		order = (1 + (int) Math.sqrt(1 + 4 * representation.length())) / 2;
		matrix = new byte[order][order];
		for (int i = 0; i < representation.length(); i++) {
			switch(representation.charAt(i)):
				case '1
			matrix[i / (order - 1)][i % (order - 1) + ((i % (order - 1) < i / (order - 1)) ? 0 : 1)] = (representation
					.charAt(i)='0'?0;
			if (representation.charAt(i) != 0) {
				size++;
			}
		}
		for (int i = 0; i < order; i++) {
			matrix[i][i] = '0';
		}
	}

	public String toString() {
		String result = "";
		for (char[] line : matrix) {
			result += Arrays.toString(line);
			result += "\n";
		}
		return result;
		// return result.substring(0,result.length()-1);
	}

	@Override
	public GenGraphlet copy() {
		return new GenGraphlet(isOrbitRep, representation());
	}

	@Override
	public void swap(int a, int b) {
		for (int i = 0; i < order; i++) {
			char reserve = matrix[a][i];
			matrix[a][i] = matrix[b][i];
			matrix[b][i] = reserve;
		}
		for (int i = 0; i < order; i++) {
			char reserve = matrix[i][a];
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
					result += matrix[i][j];
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
				if (matrix[result.get(i)][j] != '0' && !result.contains(j)) {
					result.add(j);
				}
				if (matrix[j][result.get(i)] != '0' && !result.contains(j)) {
					result.add(j);
				}
			}
		}
		return result.size() == order;
	}

	@Override
	public void addNode() {
		char[][] oldMatrix = matrix;
		order++;
		matrix = new char[order][order];
		for (int i = 0; i < order - 1; i++) {
			for (int j = 0; j < order - 1; j++) {
				matrix[i][j] = oldMatrix[i][j];
			}
			matrix[i][order-1]='0';
			matrix[order-1][i]='0';
		}
		matrix[order-1][order-1]='0';

	}
	

	@Override
	public void removeNode(int i) throws IllegalGraphActionException {
		checkNode(i);
		char[][] oldMatrix = matrix;
		order--;
		matrix = new char[order][order];
		for (int k = 0; k < order; k++) {
			for (int j = 0; j < order; j++) {
				matrix[k][j] = oldMatrix[k+(k>=i?1:0)][j+(j>=i?1:0)];
			}
		}

	}

	public static void main(String[] args) throws IllegalGraphActionException {
		GenGraphlet gg = new GenGraphlet(true, "++-000");
		System.out.println(gg);
		gg.removeNode(2);
		System.out.println(gg);
	}
	@Override
	public void addEdge(int i, int j, Character status) throws IllegalGraphActionException {
		if(matrix)

	}

	@Override
	public void removeEdge(int i, int j) throws IllegalGraphActionException {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeEdge(int i, int j, Character type) throws IllegalGraphActionException {
		// TODO Auto-generated method stub

	}

	@Override
	public SortedSet<Character> getEdges(int i, int j) throws IllegalGraphActionException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SortedSet<Integer> getNeighbours(int node, Character condition) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SortedMap<Integer, List<Character>> getNeighbours(int node) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double density() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public SortedSet<Character> edgeTypes() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<SortedSet<Character>> validEdges() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isComplete() {
		// TODO Auto-generated method stub
		return false;
	}

}
