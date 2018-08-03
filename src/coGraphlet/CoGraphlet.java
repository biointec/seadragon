package coGraphlet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import diGraphlet.DiGraphlet;
import graphlets.AbstractGraphlet;
import graphlets.IllegalGraphActionException;

public class CoGraphlet extends AbstractGraphlet<Integer> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1887726067135000527L;
	int nColors;
	private int[][] matrix;
	private float[][] colors;

	// Werkt niet met meer dan 9 kleuren!
	public CoGraphlet(int[] list, int nColors, boolean isOrbitRep) {
		super(isOrbitRep);
		this.nColors = nColors;
		order = (1 + (int) Math.round(Math.sqrt(1 + 8 * list.length))) / 2;
		size = 0;
		matrix = new int[order][];
		for (int i = 0; i < order; i++) {
			matrix[i] = new int[i];
			for (int j = 0; j < i; j++) {
				matrix[i][j] = list[(i * (i - 1)) / 2 + j];
				if (matrix[i][j] != 0) {
					size++;
				}
			}
		}
		colors = new float[nColors][3];
		for (int i = 0; i < nColors; i += 1) {
			float c = i / (float) nColors;
			if (c < 1 / 3.) {
				colors[i][0] = 1 - (c * c) * 9f;
			}
			if (c < 2 / 3.) {
				colors[i][1] = 1 - (c - 1 / 3f) * (c - 1 / 3f) * 9f;
			}
			if (c > 1 / 3.) {
				colors[i][2] = 1 - (c - 2 / 3f) * (c - 2 / 3f) * 9;
			}
			if (c > 2 / 3.) {
				colors[i][0] = 1 - (c - 1f) * (c - 1f) * 9f;
			}
		}
		// this.nColors=nColors;
	}

	public CoGraphlet(String rep, int nColors, boolean isOrbitRep) {
		this(repToMatrix(rep), nColors, isOrbitRep);
	}

	private static int[] repToMatrix(String s) {
		int[] result = new int[s.length()];
		for (int i = 0; i < s.length(); i++) {
			result[i] = s.charAt(i) - '0';
		}
		return result;
	}

	@Override
	public void swap(int a, int b) {
		int aa = Math.min(a, b);
		int bb = Math.max(a, b);
		for (int k = 0; k < aa; k++) {
			int reserve = matrix[aa][k];
			matrix[aa][k] = matrix[bb][k];
			matrix[bb][k] = reserve;
		}
		for (int k = aa + 1; k < bb; k++) {
			int reserve = matrix[k][aa];
			matrix[k][aa] = matrix[bb][k];
			matrix[bb][k] = reserve;
		}

		for (int k = bb + 1; k < order; k++) {
			//
			int reserve = matrix[k][bb];
			matrix[k][bb] = matrix[k][aa];
			matrix[k][aa] = reserve;
		}

	}

	@Override
	public boolean isConnected() {
		List<Integer> l = new ArrayList<>();
		l.add(0);
		int i = 0;
		while (i < l.size() && l.size() < order) {
			for (int j = 0; j < l.get(i); j++) {
				if (matrix[l.get(i)][j] != 0 && !l.contains(j)) {
					l.add(j);
				}
			}
			for (int j = l.get(i) + 1; j < order; j++) {
				if (matrix[j][l.get(i)] != 0 && !l.contains(j)) {
					l.add(j);
				}
			}
			i++;
		}
		return l.size() == order;
	}

	@Override
	public String representation() {
		String s = "";
		for (int i = 1; i < order; i++) {
			for (int j = 0; j < i; j++) {
				s += matrix[i][j];
			}
		}
		return s;
	}

	@Override
	public StringBuilder toPS() {
		StringBuilder result = drawNodes();
		for (int i = 1; i < order; i++) {
			for (int j = 0; j < i; j++) {
				if (matrix[i][j] != 0) {
					result.append(colors[matrix[i][j] - 1][0]);
					result.append(" ");
					result.append(colors[matrix[i][j] - 1][1]);
					result.append(" ");
					result.append(colors[matrix[i][j] - 1][2]);
					result.append(" setrgbcolor\n");
					result.append(drawEdge(i, j));
				}
			}
		}
		result.append("0 0 0 setrgbcolor\n");
		result.append(drawOrbits());
		result.append("showpage\n");
		return result;
	}

	public String toString() {
		String s = "";
		for (int i = 1; i < order; i++) {
			for (int j = 0; j < i; j++) {
				s += matrix[i][j];
			}
			s += "\n";
		}
		return s;
	}

	@Override
	public CoGraphlet copy() {
		CoGraphlet result = new CoGraphlet(representation(), nColors, isOrbitRep);
		result.representations = representations;
		result.automorphisms = automorphisms;
		result.canonical = canonical;
		result.order = order;
		result.size = size;
		result.orbits = orbits;
		return result;

	}

	@Override
	public void addNode() {
		order++;
		int[][] matrixcopy = matrix;
		matrix = new int[order][];
		for (int i = 0; i < order - 1; i++) {
			matrix[i] = new int[i];
			for (int j = 0; j < i; j++) {
				matrix[i][j] = matrixcopy[i][j];
			}
		}
		matrix[order - 1] = new int[order - 1];
	}

	@Override
	public void addEdge(int i, int j, Integer status) throws IllegalGraphActionException {
		if (i == j) {
			throw new IllegalGraphActionException("No self-loops allowed");
		} else if (matrix[i][j] != 0) {
			throw new IllegalGraphActionException("No double edges allowed");
		} else if (i > j) {
			matrix[i][j] = status;
		} else {
			matrix[j][i] = status;
		}
		size++;
	}

	@Override
	public boolean areConnected(int a, int b) {
		return a<b?matrix[b][a]!=0:matrix[a][b]!=0;
	}

}
