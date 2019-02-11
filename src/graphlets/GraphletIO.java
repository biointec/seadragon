package graphlets;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import equationgeneration.Equation;
import graphlets.coGraphlet.CoGraph;
import graphlets.diGraphlet.DiGraph;
import graphlets.genGraphlet.GenGraph;
import graphlets.simpleGraphlet.SimpleGraph;
import tree.GraphletTree;

public class GraphletIO {

	public static void save(GraphletTree<?, ?> tree, String fileName) {
		try (FileOutputStream fos = new FileOutputStream(fileName);
				ObjectOutputStream oos = new ObjectOutputStream(fos);) {
			oos.writeObject(tree);
		} catch (FileNotFoundException e) {
			// Error in accessing the file
			e.printStackTrace();
		} catch (IOException e) {
			// Error in converting the Student
			e.printStackTrace();
		}
	}

	public static void save(List<AbstractGraphlet<?>> g, String fileName) {
		try (FileOutputStream fos = new FileOutputStream(fileName);
				ObjectOutputStream oos = new ObjectOutputStream(fos);) {
			oos.writeObject(g);
		} catch (FileNotFoundException e) {
			// Error in accessing the file
			e.printStackTrace();
		} catch (IOException e) {
			// Error in converting the Student
			e.printStackTrace();
		}
	}

	public static <T extends AbstractGraphlet<?>> void save(Collection<Equation<T>> eq, String fileName) {
		try (FileOutputStream fos = new FileOutputStream(fileName);
				ObjectOutputStream oos = new ObjectOutputStream(fos);) {
			oos.writeObject(eq);
		} catch (FileNotFoundException e) {
			// Error in accessing the file
			e.printStackTrace();
		} catch (IOException e) {
			// Error in converting the Student
			e.printStackTrace();
		}
	}

	public static void save(List<AbstractGraphlet<?>> l) {
		save(l, l.get(0).name() + "s-" + l.get(0).getOrder() + ".gpl");
	}

	public static void main(String[]args) {
		CoGraph cg = readGraph("test/hiv.txt",CoGraph.class,12);
		System.out.println(cg.getNColors());
	}
	
	public static <T extends AbstractGraph<U>, U extends Comparable<U>> T readGraph(String filename, Class<T> base,
			int column) {
		File file = new File(filename);
		Map<String, Integer> legend = new HashMap<>();
		int counter = 0;
		T result = null;
		try {
			result = base.newInstance();
			Scanner scanner = new Scanner(file);
			boolean started = false;
			int linenr = 0;
			int ignored = 0;
			while (scanner.hasNextLine()) {
				linenr++;
				String s = scanner.nextLine();
				// System.out.println(s);
				if (!s.startsWith("#")) {
					String[] namen = s.split("\t");
					// System.out.println(Arrays.toString(namen));
					if (namen.length >= column) {
						String node1 = namen[0];
						if (!legend.containsKey(node1)) {
							legend.put(node1, counter++);
							result.addNode();
						}
						int a = legend.get(node1);
						String node2 = namen[1];
						if (!legend.containsKey(node2)) {
							legend.put(node2, counter++);
							result.addNode();
						}
						// System.out.println(result.order);
						int b = legend.get(node2);
						try {
							// System.out.println(a+" "+b+" "+result.getType(namen));
							try {
								result.addEdge(a, b, result.getType(namen[column - 1]));
							} catch (ArrayIndexOutOfBoundsException e) {
								result.addEdge(a, b, result.getType(""));
							}
						} catch (IllegalGraphActionException e) {
//							 System.out.println("Ignoring line " + linenr + ": " + e.getMessage());
							ignored++;
						}
					}
				}
			}
//			System.out.println("Ignored " + ignored + " of " + linenr + " lines");
			scanner.close();
		} catch (FileNotFoundException e) {
			System.out.println("Invalid file name");
		} catch (InstantiationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IllegalAccessException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return result;
	}

	public static <T extends AbstractGraph<U>, U extends Comparable<U>> T readGraph(String filename, Class<T> base,
			int column, double confidence) {
		File file = new File(filename);
		Map<String, Integer> legend = new HashMap<>();
		int counter = 0;
		T result = null;
		try {
			result = base.newInstance();
			Scanner scanner = new Scanner(file);
			boolean started = false;
			int linenr = 0;
			int ignored = 0;
			while (scanner.hasNextLine()) {
				linenr++;
				String s = scanner.nextLine();
				// System.out.println(s);
				if (!s.startsWith("#")) {
					String[] namen = s.split("\t");
					// System.out.println(Arrays.toString(namen));
					if (namen.length >= 3) {
						String c = namen[namen.length - 1];
						if (namen[namen.length - 1].toLowerCase().startsWith("score:")) {
							c = namen[namen.length - 1].substring(6);
						}
						try {
							float score = Float.parseFloat(c);
							if (score >= confidence) {

								String node1 = namen[0];
								if (!legend.containsKey(node1)) {
									legend.put(node1, counter++);
									result.addNode();
								}
								int a = legend.get(node1);
								String node2 = namen[1];
								if (!legend.containsKey(node2)) {
									legend.put(node2, counter++);
									result.addNode();
								}
								// System.out.println(result.order);
								int b = legend.get(node2);
								try {
									// System.out.println(a+" "+b+" "+result.getType(namen));
									try {
										result.addEdge(a, b, result.getType(namen[column - 1]));
									} catch (ArrayIndexOutOfBoundsException e) {
										result.addEdge(a, b, result.getType(""));
									}
								} catch (IllegalGraphActionException e) {
//									 System.out.println("Ignoring line " + linenr + ": " + e.getMessage());
									ignored++;
								}
								// result.addEdge(a, b, result.getType(namen[column - 1]));
							} else {
//								 System.out.println("Ignoring line " + linenr + ": " + "too low confidence");
								ignored++;
							}
						} catch (NumberFormatException e) {
//							 System.out.println("Ignoring line " + linenr + ": " + e.getMessage());
							ignored++;
						}
					}
				}
			}
			scanner.close();
		} catch (FileNotFoundException e) {
			System.out.println("Invalid file name");
		} catch (InstantiationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IllegalAccessException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return result;
	}

	public static void draw(List<AbstractGraphlet<?>> g, String fileName) {
		try {
			PrintWriter pw = new PrintWriter(fileName);
			pw.print("%!PS\n/Times-Roman findfont\n10 scalefont\nsetfont\n");
			for (AbstractGraphlet<?> graphlet : g) {
				pw.println(graphlet.toPS());
			}
			pw.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static DiGraph readMatrix(float threshold) {
		DiGraph graph = new DiGraph();
		for(int i=0;i<3172;i++) {
			graph.addNode();
		}
		float[][]matrix = pdat();
		for(int i=0;i<3172;i++) {
			for(int j=0;j<3172;j++) {
//				System.out.print(matrix[i][j]+" ");
				if(matrix[i][j]>threshold) {
					try {
						graph.addEdge(i, j, true);
					} catch (IllegalGraphActionException e) {
						// TODO Auto-generated catch block
//						e.printStackTrace();
					}
				}
			}
//			System.out.println();
		}
		System.out.println(graph.order+", "+graph.size);
		return graph;
	}
	
	private static float[][] pdat() {
		int dim = 3172;
		float[][] matrix = new float[dim][dim];
		try {
			DataInputStream in = new DataInputStream(
					new BufferedInputStream(new FileInputStream("test/findr_geuvadis_ptrans_colorder.dat")));
			for (int i = 0; i < dim; i++) {
				for (int j = 0; j < dim; j++) {
					matrix[i][j] = Float.intBitsToFloat(readInt(in));
				}
			}
			in.close();
		} catch (Exception e) {
		}
		return matrix;
	}

	private static int readInt(DataInputStream in) throws IOException {
		int val = 0;
		for (int i = 0; i < 4; i++) {
			val = (int) (val | (((int) in.readUnsignedByte()) << (i * 8)));
		}
		return val;
	}

	@SuppressWarnings("unchecked")
	public static List<AbstractGraphlet<?>> read(String fileName) {
		List<AbstractGraphlet<?>> g = null;
		if (fileName.substring(fileName.length() - 4).equals(".gpl"))
			try (FileInputStream fis = new FileInputStream(fileName);
					ObjectInputStream ois = new ObjectInputStream(fis);) {
				g = (List<AbstractGraphlet<?>>) ois.readObject();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		return g;
	}

	// public static List<AbstractGraphlet<?>> read(String type, int order){
	// return read(type+ "-")
	// }
	//
	// public static List<Graphlet<?>> getGraphlets(String type, int order){
	//
	// }

}
