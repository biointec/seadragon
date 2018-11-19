package graphlets.coGraphlet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import graphlets.AbstractGraphlet;
import graphlets.IllegalGraphActionException;

public class CoGraphlet extends AbstractGraphlet<Integer> {

	public CoGraphlet(String representation, boolean isOrbitRep) {
		super(isOrbitRep);
//		System.out.println(representation);
		order = (1+(int)Math.sqrt(1+8*representation.length()))/2;
//		System.out.println(order);
		matrix = new int[order][];
		for(int i=0;i<order;i++) {
			matrix[i]=new int[i];
			for(int j=0;j<i;j++) {
				matrix[i][j]= representation.charAt((i*(i-1))/2+j)-'0';
				if(matrix[i][j]!=0)size++;
			}
		}
		// TODO Auto-generated constructor stub
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1887726067135000527L;
	int nColors;
	private int[][] matrix;
	private float[][] colors;

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
//		System.out.println(Arrays.deepToString(matrix));
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
		newmatrix[order]=new int[order];
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
		if(matrix[b][a]!=type) {
			throw new IllegalGraphActionException("No edge of type "+type+" present between nodes "+node1+" and "+node2);
		}else {
			matrix[b][a]=0;
		}
	}

	@Override
	public SortedSet<Integer> getEdges(int i, int j) throws IllegalGraphActionException {
		checkNode(i);
		checkNode(j);
		SortedSet<Integer> result = new TreeSet<>();
		int a = Math.min(i, j);
		int b = Math.max(i, j);
		if(matrix[b][a]!=0) {
			result.add(matrix[b][a]);
		}
		return result;
	}

	@Override
	public SortedSet<Integer> getNeighbours(int node, Integer type) {
		SortedSet<Integer>result = new TreeSet<>();
		for(int i=0;i<node;i++) {
			if(matrix[node][i]==type) {
				result.add(i);
			}
		}
		for(int i=node+1;i<order;i++) {
			if(matrix[i][node]==type) {
				result.add(i);
			}
		}
		return result;
	}

	@Override
	public SortedSet<Integer> getInvertedNeighbours(int node, Integer type) {
		return getNeighbours(node,type);
	}

	@Override
	public SortedMap<Integer, SortedSet<Integer>> getNeighbours(int node) {
//		System.out.println(Arrays.deepToString(matrix));
		SortedMap<Integer,SortedSet<Integer>>result = new TreeMap<>();
		for(int i=0;i<node;i++) {
			if(matrix[node][i]!=0) {
				if(!result.containsKey(i)) {
					result.put(i, new TreeSet<>());
				}
				result.get(i).add(matrix[node][i]);
			}
		}
		for(int i=node+1;i<order;i++) {
			if(matrix[i][node]!=0) {
				if(!result.containsKey(i)) {
					result.put(i, new TreeSet<>());
				}
				result.get(i).add(matrix[i][node]);
			}
		}
//		System.out.println(node+" "+result);
		return result;
	}

	@Override
	public double density() {
		return 2.*size/order/(order-1.);
	}

	@Override
	public boolean isComplete() {
//		System.out.println(order+" "+size);
//		System.out.println(2*size==order*(order-1));
		return (2*size==order*(order-1));
	}

//	public static void main(String[]args) {
//		System.out.println(new CoGraphlet("333", true).isComplete());
//	}
	// Werkt niet met meer dan 9 kleuren!
	// public CoGraphlet(int[] list, int nColors, boolean isOrbitRep) {
	// super(isOrbitRep);
	// this.nColors = nColors;
	// order = (1 + (int) Math.round(Math.sqrt(1 + 8 * list.length))) / 2;
	// size = 0;
	// matrix = new int[order][];
	// for (int i = 0; i < order; i++) {
	// matrix[i] = new int[i];
	// for (int j = 0; j < i; j++) {
	// matrix[i][j] = list[(i * (i - 1)) / 2 + j];
	// if (matrix[i][j] != 0) {
	// size++;
	// }
	// }
	// }
	// colors = new float[nColors][3];
	// for (int i = 0; i < nColors; i += 1) {
	// float c = i / (float) nColors;
	// if (c < 1 / 3.) {
	// colors[i][0] = 1 - (c * c) * 9f;
	// }
	// if (c < 2 / 3.) {
	// colors[i][1] = 1 - (c - 1 / 3f) * (c - 1 / 3f) * 9f;
	// }
	// if (c > 1 / 3.) {
	// colors[i][2] = 1 - (c - 2 / 3f) * (c - 2 / 3f) * 9;
	// }
	// if (c > 2 / 3.) {
	// colors[i][0] = 1 - (c - 1f) * (c - 1f) * 9f;
	// }
	// }
	// // this.nColors=nColors;
	// }
	//
	// public CoGraphlet(String rep, int nColors, boolean isOrbitRep) {
	// this(repToMatrix(rep), nColors, isOrbitRep);
	// }
	//
	// private static int[] repToMatrix(String s) {
	// int[] result = new int[s.length()];
	// for (int i = 0; i < s.length(); i++) {
	// result[i] = s.charAt(i) - '0';
	// }
	// return result;
	// }
	//
	//
	// @Override
	// public void swap(int a, int b) {
	// int aa = Math.min(a, b);
	// int bb = Math.max(a, b);
	// for (int k = 0; k < aa; k++) {
	// int reserve = matrix[aa][k];
	// matrix[aa][k] = matrix[bb][k];
	// matrix[bb][k] = reserve;
	// }
	// for (int k = aa + 1; k < bb; k++) {
	// int reserve = matrix[k][aa];
	// matrix[k][aa] = matrix[bb][k];
	// matrix[bb][k] = reserve;
	// }
	//
	// for (int k = bb + 1; k < order; k++) {
	// //
	// int reserve = matrix[k][bb];
	// matrix[k][bb] = matrix[k][aa];
	// matrix[k][aa] = reserve;
	// }
	//
	// }
	//
	//// @Override
	//// public boolean isConnected() {
	//// List<Integer> l = new ArrayList<>();
	//// l.add(0);
	//// int i = 0;
	//// while (i < l.size() && l.size() < order) {
	//// for (int j = 0; j < l.get(i); j++) {
	//// if (matrix[l.get(i)][j] != 0 && !l.contains(j)) {
	//// l.add(j);
	//// }
	//// }
	//// for (int j = l.get(i) + 1; j < order; j++) {
	//// if (matrix[j][l.get(i)] != 0 && !l.contains(j)) {
	//// l.add(j);
	//// }
	//// }
	//// i++;
	//// }
	//// return l.size() == order;
	//// }
	//
	// @Override
	// public String representation() {
	// String s = "";
	// for (int i = 1; i < order; i++) {
	// for (int j = 0; j < i; j++) {
	// s += matrix[i][j];
	// }
	// }
	// return s;
	// }
	//
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
	// public String toString() {
	// String s = "";
	// for (int i = 1; i < order; i++) {
	// for (int j = 0; j < i; j++) {
	// s += matrix[i][j];
	// }
	// s += "\n";
	// }
	// return s;
	// }
	//
	//
	// @Override
	// public void addNode() {
	// order++;
	// int[][] matrixcopy = matrix;
	// matrix = new int[order][];
	// for (int i = 0; i < order - 1; i++) {
	// matrix[i] = new int[i];
	// for (int j = 0; j < i; j++) {
	// matrix[i][j] = matrixcopy[i][j];
	// }
	// }
	// matrix[order - 1] = new int[order - 1];
	// ready = false;
	// }
	//
	// @Override
	// public void addEdge(int i, int j, Integer status) throws
	// IllegalGraphActionException {
	// checkNode(i);
	// checkNode(j);
	// if (i == j) {
	// throw new IllegalGraphActionException("No self-loops allowed");
	// } else if (matrix[i][j] != 0) {
	// throw new IllegalGraphActionException("No double edges allowed");
	// }else if(status<=0 || status >nColors){
	// throw new IllegalGraphActionException(status+" is not a valid edge type");
	// } else if (i > j) {
	// matrix[i][j] = status;
	// } else {
	// matrix[j][i] = status;
	// }
	//// size++;
	// ready = false;
	// }
	//
	// @Override
	// public void removeNode(int i) throws IllegalGraphActionException {
	// checkNode(i);
	// int[][] oldMatrix = matrix;
	// order--;
	// matrix = new int[order][order];
	// for (int k = 0; k < order; k++) {
	// for (int j = 0; j < k; j++) {
	// matrix[k][j] = oldMatrix[k+(k>=i?1:0)][j+(j>=i?1:0)];
	// }
	// }
	// ready=false;
	// }
	//
	// @Override
	// public void removeEdge(int i, int j) throws IllegalGraphActionException {
	// checkNode(i);
	// checkNode(j);
	// checkEdge(i,j);
	// matrix[Math.max(i, j)][Math.min(i, j)]=0;
	// ready=false;
	//
	// }
	// @Override
	// public void removeEdge(int i, int j, Integer type) throws
	// IllegalGraphActionException {
	// checkNode(i);
	// checkNode(j);
	// if(matrix[Math.max(i, j)][Math.min(i, j)]!=type) {
	// throw new IllegalGraphActionException("No edge of type "+ type+" present
	// between nodes "+i+" and "+j);
	// }
	// matrix[Math.max(i, j)][Math.min(i, j)]=0;
	// ready=false;
	// }
	//
	// @Override
	// public SortedSet<Integer> getEdges(int i, int j) throws
	// IllegalGraphActionException {
	// int b=Math.min(i,j);
	// int a = Math.max(i, j);
	// SortedSet<Integer> result = new TreeSet<>();
	// if(matrix[a][b]!=0) {
	// result.add(matrix[a][b]);
	// }
	// return result;
	// }
	//
	// @Override
	// public SortedSet<Integer> getNeighbours(int node, Integer condition) {
	// SortedSet<Integer> result = new TreeSet<>();
	// for(int i=0;i<node;i++) {
	// if(matrix[node][i]==condition) {
	// result.add(i);
	// }
	// }
	// for(int i=node+1;i<order;i++) {
	// if(matrix[i][node]==condition) {
	// result.add(i);
	// }
	// }
	// return result;
	// }
	//
	// @Override
	// public SortedMap<Integer, SortedSet<Integer>> getNeighbours(int node) {
	// SortedMap<Integer, SortedSet<Integer>> result = new TreeMap<>();
	// for(int i=0;i<node;i++) {
	// if(matrix[node][i]!=0) {
	// SortedSet<Integer> edge = new TreeSet<>();
	// edge.add(matrix[node][i]);
	// result.put(i,edge);
	// }
	// }
	// for(int i=node+1;i<order;i++) {
	// if(matrix[i][node]!=0) {
	// SortedSet<Integer> edge = new TreeSet<>();
	// edge.add(matrix[i][node]);
	// result.put(i,edge);
	// }
	// }
	// return result;
	// }
	//
	// @Override
	// public double density() {
	// return size / (order * (order - 1.)) * nColors;
	// }
	//
	//// @Override
	//// public List<Integer> edgeTypes() {
	//// List<Integer> result = new ArrayList<>();
	//// for (int i = 1; i < nColors+1; i++) {
	//// result.add(i);
	//// }
	//// return result;
	//// }
	////
	//// @Override
	//// public List<SortedSet<Integer>> edgeCombinations() {
	//// List<SortedSet<Integer>> result = new ArrayList<>();
	//// for(int i:edgeTypes()) {
	//// SortedSet<Integer>edge = new TreeSet<>();
	//// edge.add(i);
	//// result.add(edge);
	//// }
	//// return result;
	//// }
	//
	// @Override
	// public boolean isComplete() {
	// return size == (order * (order - 1)) * nColors;
	// }
	//
	// public static void main(String[]args) {
	// CoGraphlet cg = new CoGraphlet("123456",6,true);
	// System.out.println(cg.representation());
	// System.out.println(cg.canonical());
	// cg.addNode();
	// System.out.println(cg.representation());
	// System.out.println(cg.canonical());
	// }
	//
	// @Override
	// public SortedSet<Integer> getInvertedNeighbours(int node, Integer condition)
	// {
	// return getNeighbours(node,condition);
	// }

}