package userInterface;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;

import equationgeneration.Equation;
import equationgeneration.EquationGenerator;
import graphletgeneration.AbstractGraphletFactory;
import graphletgeneration.GraphletIterator;
import graphlets.AbstractGraph;
import graphlets.AbstractGraphlet;
import graphlets.GraphletIO;
import graphlets.diGraphlet.DiGraph;
import graphlets.diGraphlet.DiGraphlet;
import graphlets.diGraphlet.DiGraphletFactory;
import graphlets.simpleGraphlet.SimpleGraph;
import tree.GraphletTree;
import tree.TreeGenerator;
import treewalker.EquationWalker;
import treewalker.TreeWalker;

public class UI<T extends AbstractGraphlet<U>, U extends Comparable<U>> {

	private static Scanner reader;
	private AbstractGraph<U> graph;
	private AbstractGraphletFactory<T, U> factory;
	private Collection<Equation<T>> equations;
	private GraphletTree<T, U> tree;
	private int order;

	public UI(AbstractGraph<U> graph, AbstractGraphletFactory<T, U> factory, Collection<Equation<T>> equations,
			GraphletTree<T, U> tree, int order) {
		super();
		this.graph = graph;
		this.factory = factory;
		this.equations = equations;
		this.tree = tree;
		this.order = order;
	}

	//
	// public UI() {
	// }

	// private Map<String, String> getGraphTypes() {
	// Map<String, String> types = new HashMap<>();
	// File file = new File("data/graph_types");
	// try {
	// Scanner scanner = new Scanner(file);
	// while (scanner.hasNextLine()) {
	// String s = scanner.nextLine();
	// if (!s.startsWith("#")) {
	// String [] stukjes = s.split("\t");
	// types.put(stukjes[0], stukjes[1]);
	// }
	// }
	// scanner.close();
	// } catch (IOException e) {
	//
	// }
	// return types;
	// }

	private static List<List<String>> getGraphTypes() {
		List<List<String>> result = new ArrayList<>();
		File file = new File("data/graph_types");
		result.add(new ArrayList<>());
		result.add(new ArrayList<>());
		try {
			Scanner scanner = new Scanner(file);
			while (scanner.hasNextLine()) {
				String s = scanner.nextLine();
				if (!s.startsWith("#")) {
					String[] stukjes = s.split("\t");
					result.get(0).add(stukjes[0]);
					result.get(1).add(stukjes[1]);
				}
			}
			scanner.close();
		} catch (IOException e) {

		}
		return result;
	}

	public static <T extends AbstractGraphlet<U>, U extends Comparable<U>> void run() throws Exception {

		reader = new Scanner(System.in); // Reading from System.in
		System.out.println("     Welcome to Dingske");
		System.out.println("the general graphlet counter");
		System.out.println("           v0.0.1");
		System.out.println();
		System.out.println();
		System.out.println("Available graph types:");
		System.out.println();
		List<List<String>> types = getGraphTypes();
		for (int i = 0; i < types.get(0).size(); i++) {
			System.out.println("[" + (i + 1) + "]\t" + types.get(0).get(i));
		}
		System.out.println();
		System.out.println("Which graph type do you want to use?");
		String graphType = types.get(1).get(intLoop(1, types.get(0).size()) - 1);
		System.out.println(graphType);
		// TODO: read graph type
		System.out.println("Give the graph file:");
		String graphfile = reader.next();
		System.out.println("What is the minimum score for the edges?");
		double score = doubleLoop();
		System.out.println("Give the graphlet order");
		int order = intLoop(3, Integer.MAX_VALUE);
		System.out.println("Do you want to count graphlets or orbits?");
		String[] orbits = { "orbit", "orbits", "o" };
		String[] graphlets = { "graphlet", "graphlets", "g" };
		boolean useOrbits = booleanLoop(makeSet(orbits), makeSet(graphlets));
		System.out.println("Do you want to load a graphlet tree from file?");
		String[] yes = { "yes", "y" };
		String[] no = { "no", "n" };
		int loadTree = booleanLoop(makeSet(yes), makeSet(no)) ? 1 : 0;
		String treeFile = "";
		if (loadTree == 0) {
			System.out.println("Dingske will generate a tree.");
			System.out.println("Do you want to save the generated graphlet tree?");
			loadTree = booleanLoop(makeSet(yes), makeSet(no)) ? -1 : 0;
		}
		if (loadTree != 2) {
			System.out.println("Give the tree file:");
			treeFile = reader.next();
		}
		System.out.println("Do you want to load the equations from file?");
		int loadEquations = booleanLoop(makeSet(yes), makeSet(no)) ? 1 : 0;// 1 = load, -1 = save, 0 = neither
		String equationFile = "";
		if (loadEquations == 1) {
			System.out.println("Dingske will generate equations.");
			System.out.println("Do you want to save the generated equations?");
			loadEquations = booleanLoop(makeSet(yes), makeSet(no)) ? -1 : 0;
		}
		if (loadEquations != 2) {
			System.out.println("Give the equation file:");
			equationFile = reader.next();
		}
		System.out.println("Where do you want to save the results?");
		String resultsFile = reader.next();
		check( graphfile,
				 graphType, 0,  useOrbits,  score,  order,  loadTree,  treeFile,
				 loadEquations,  equationFile, resultsFile);
//		check(graphfile, graphType, score, useOrbits, loadTree, treeFile, order, loadEquations, equationFile,resultsFile);
		reader.close();
	}

	public static void main(String[] args) throws Exception {
//		check("test/test.txt", "graphlets.coGraphlet.CoGraph",3,false, 0, 4, 0, "", 0, "", "test/candida_cographlets.out");
		
		long time;
		System.out.println("Candida 4-cographlets");
		time = System.nanoTime();
		check("test/candida.txt", "graphlets.coGraphlet.CoGraph",12,false, 0, 4, 0, "", 0, "", "test/candida_cographlets.out");
		System.out.println((System.nanoTime()-time)*1e-9);
		System.out.println("Candida 4-coorbits");
		time = System.nanoTime();
		check("test/candida.txt", "graphlets.coGraphlet.CoGraph",12,true, 0, 4, 0, "", 0, "", "test/candida_coorbits.out");
		System.out.println((System.nanoTime()-time)*1e-9);
		time = System.nanoTime();
		System.out.println("Candida 4-graphlets");
		time = System.nanoTime();
		check("test/candida.txt", "graphlets.simpleGraphlet.SimpleGraph",0,false, 0, 4, 0, "", 0, "", "test/candida_graphlets.out");
		System.out.println((System.nanoTime()-time)*1e-9);
		System.out.println("Candida 4-orbits");
		time = System.nanoTime();
		check("test/candida.txt", "graphlets.simpleGraphlet.SimpleGraph",0,true, 0, 4, 0, "", 0, "", "test/candida_orbits.out");
		System.out.println((System.nanoTime()-time)*1e-9);
		System.out.println("Candida 4-digraphlets");
		time = System.nanoTime();
		check("test/candida.txt", "graphlets.diGraphlet.DiGraph",0,false, 0, 4, 0, "", 0, "", "test/candida_digraphlets.out");
		System.out.println((System.nanoTime()-time)*1e-9);
		System.out.println("Candida 4-diorbits");
		time = System.nanoTime();
		check("test/candida.txt", "graphlets.diGraphlet.DiGraph",0,true, 0, 4, 0, "", 0, "", "test/candida_diorbits.out");
		System.out.println((System.nanoTime()-time)*1e-9);
//		System.out.println("human 4-orbits 0.9");
//		time = System.nanoTime();
//		check(false, 0.9, 4, 0, "", 0, "", "test/human9.out");
//		System.out.println((System.nanoTime()-time)*1e-9);
//		System.out.println("human 4-orbits 0.8");
//		time = System.nanoTime();
//		check(false, 0.8, 4, 0, "", 0, "", "test/human8.out");
//		System.out.println((System.nanoTime()-time)*1e-9);
//		System.out.println("human 4-orbits 0.7");
//		time = System.nanoTime();
//		check(false, 0.7, 4, 0, "", 0, "", "test/human7.out");
//		System.out.println((System.nanoTime()-time)*1e-9);
//		System.out.println("human 4-orbits 0.6");
//		time = System.nanoTime();
//		check(false, 0.6, 4, 0, "", 0, "", "test/human6.out");
//		System.out.println((System.nanoTime()-time)*1e-9);
//		System.out.println("human 4-orbits 0.5");
//		time = System.nanoTime();
//		check(false, 0.5, 4, 0, "", 0, "", "test/human5.out");
//		System.out.println((System.nanoTime()-time)*1e-9);
//		System.out.println("human 4-orbits 0.4");
//		time = System.nanoTime();
//		check(false, 0.4, 4, 0, "", 0, "", "test/human4.out");
//		System.out.println((System.nanoTime()-time)*1e-9);
//		System.out.println("human 4-orbits 0.3");
//		time = System.nanoTime();
//		check(false, 0.3, 4, 0, "", 0, "", "test/human3.out");
//		System.out.println((System.nanoTime()-time)*1e-9);
//		System.out.println("human 4-orbits 0.2");
//		time = System.nanoTime();
//		check(false, 0.2, 4, 0, "", 0, "", "test/human2.out");
//		System.out.println((System.nanoTime()-time)*1e-9);
//		System.out.println("human 4-orbits 0.1");
//		time = System.nanoTime();
//		check(false, 0.1, 4, 0, "", 0, "", "test/human1.out");
//		System.out.println((System.nanoTime()-time)*1e-9);
//		 run("test/candida.txt","graphlets.diGraphlet.DiGraph", 0,false,0,"",4,0,"");
	}

//	private static <T extends AbstractGraphlet<U>, U extends Comparable<U>> void run(String graphfile, String graphType,
//			double score, boolean useOrbits, int loadTree, String treeFile, int order, int loadEquations,
//			String equationFile,String resultfile) throws ClassNotFoundException, FileNotFoundException {
//		AbstractGraph<U> graph = getGraph(graphfile, (Class<? extends AbstractGraph<U>>) Class.forName(graphType),
//				score);
//		// System.out.println(graph);
//		AbstractGraphletFactory<T, U> factory = (AbstractGraphletFactory<T, U>) graph.getGraphletType(useOrbits);
//		// GraphletTree<T, U> tree = getTree(loadTree, treeFile, factory, order-1);
//
//		GraphletTree<T, U> tree = new TreeGenerator<>(factory, order - 1).generateTree();
//		Collection<Equation<T>> equations = new EquationGenerator<T, U>(factory, order, tree).generateEquations();
//		// Collection<Equation<T>> equations = getEquations(loadEquations, equationFile,
//		// factory, tree,order);
//		TreeWalker<T, U> tw = new EquationWalker<>(tree, graph, equations);
//		tw.reset();
//		PrintStream ps = new PrintStream(new File (resultfile));
//		tw.run(ps);
////		System.out.println(graph.getOrder());
////		System.out.println(graph.getSize());
////		for (int i = 0; i < graph.getOrder(); i++) {
////			NavigableMap<String, Long> run = tw.run(i);
////			System.out.println(run);
////		}
//	}
	
	public static void check(boolean useOrbits, double score, int order, int loadTree, String treeFile,
			int loadEquations, String equationFile,String resultfile) throws Exception {
		DiGraph graph =GraphletIO.readMatrix((float) score);
		AbstractGraphletFactory<DiGraphlet, Boolean> f = (AbstractGraphletFactory<DiGraphlet, Boolean>) graph.getGraphletType(useOrbits);
		// GraphletIterator<T> gi = new GraphletIterator<>(f,order);
		GraphletTree<DiGraphlet, Boolean> tree = getTree(loadTree, treeFile, f, order - 1);
//		tree.print();
		Collection<Equation<DiGraphlet>> equations = new EquationGenerator<DiGraphlet, Boolean>(f, order, tree).generateEquations();
//		Collection<Equation<T>> equations = getEquations(loadEquations, equationFile, f, tree, order);
//		System.out.println();
//		System.out.println(equations);
		// while(gi.hasNext()) {
		// T next = gi.next();
		// System.out.println(next.representation());
		System.out.println("Calculating common neighbours...");
		
		TreeWalker<DiGraphlet, Boolean> tw = new EquationWalker<>(tree, graph, equations);
		tw.reset();
		PrintStream ps = new PrintStream(new File (resultfile));
		System.out.println("Running...");
		tw.run(ps);
		System.out.println("Finished");
//		for (int i = 0; i < graph.getOrder(); i++) {
//			NavigableMap<String, Long> run = tw.run(i);
//			System.out.println(run);
//			Iterator<String> it = ((NavigableSet<String>) run.keySet()).descendingIterator();
//			String last = it.next();
//			String secondLast = it.next();
//			// if (i == 0 && !last.equals(next.representation()) || run.get(last) != 1
//			// || secondLast.length() >= last.length()) {
//			// throw new Exception("Impossible!");
//			// }
//			tw.reset();
			// }
//		}
	}

	public static <T extends AbstractGraphlet<U>, U extends Comparable<U>> void check(String graphfile,
			String graphType, int column, boolean useOrbits, double score, int order, int loadTree, String treeFile,
			int loadEquations, String equationFile,String resultfile) throws Exception {
		AbstractGraph<U> graph = getGraph(graphfile, (Class<? extends AbstractGraph<U>>) Class.forName(graphType), score,
				column);
		AbstractGraphletFactory<T, U> f = (AbstractGraphletFactory<T, U>) graph.getGraphletType(useOrbits);
		// GraphletIterator<T> gi = new GraphletIterator<>(f,order);
		GraphletTree<T, U> tree = getTree(loadTree, treeFile, f, order - 1);
//		tree.print();
//		Collection<Equation<T>> equations = new EquationGenerator<T, U>(f, order, tree).generateEquations();
		Collection<Equation<T>> equations = getEquations(loadEquations, equationFile, f, tree, order);
//		System.out.println();
//		System.out.println(equations);
		// while(gi.hasNext()) {
		// T next = gi.next();
		// System.out.println(next.representation());
		System.out.println("Calculating common neighbours...");
		long time = System.nanoTime();
		TreeWalker<T, U> tw = new EquationWalker<>(tree, graph, equations);
		System.out.println((System.nanoTime()-time)/1e9+" s");
		tw.reset();
		PrintStream ps = new PrintStream(new File (resultfile));
		System.out.println("Running...");
		time = System.nanoTime();
		tw.run(ps);
		System.out.println((System.nanoTime()-time)/1e9+" s");
		System.out.println("Finished");
//		for (int i = 0; i < graph.getOrder(); i++) {
//			NavigableMap<String, Long> run = tw.run(i);
//			System.out.println(run);
//			Iterator<String> it = ((NavigableSet<String>) run.keySet()).descendingIterator();
//			String last = it.next();
//			String secondLast = it.next();
//			// if (i == 0 && !last.equals(next.representation()) || run.get(last) != 1
//			// || secondLast.length() >= last.length()) {
//			// throw new Exception("Impossible!");
//			// }
//			tw.reset();
			// }
//		}
	}

	private static <U extends Comparable<U>> AbstractGraph<U> getGraph(String graphfile,
			Class<? extends AbstractGraph<U>> type, double score, int column) {
		AbstractGraph<U> graph;
		System.out.println("Loading graph...");
		if (score == 0) {
			graph = GraphletIO.readGraph(graphfile, (type),column);
		} else {
			graph = GraphletIO.readGraph(graphfile, type, column, score);
		}
		System.out.println(graph.getOrder()+" nodes, "+graph.getSize()+" edges");
		return graph;
	}

	// public static <T extends AbstractGraphlet<U>, U extends Comparable<U>> void
	// check(AbstractGraph<U> graph,
	// AbstractGraphletFactory<T, U> f, int order) throws Exception {
	//
	// // GraphletIterator<T> gi = new GraphletIterator<>(f, order);
	// // GraphletTree<T, U> tree = new TreeGenerator<>(f, order -
	// 1).generateTree();
	// // Collection<Equation<T>> equations = new EquationGenerator<>(f, order,
	// // tree).generateEquations();
	// // tree.print();
	// // System.out.println();
	// // System.out.println(equations);
	// // while (gi.hasNext()) {
	// // T next = gi.next();
	// // System.out.println(next.representation());
	// TreeWalker<T, U> tw = new EquationWalker<>(tree, graph, equations);
	// tw.reset();
	// for (int i = 0; i < graph.getOrder(); i++) {
	// NavigableMap<String, Long> run = tw.run(i);
	// System.out.println(run);
	// Iterator<String> it = ((NavigableSet<String>)
	// run.keySet()).descendingIterator();
	// String last = it.next();
	// String secondLast = it.next();
	// //
	// tw.reset();
	// }
	// }

	private static <T extends AbstractGraphlet<U>, U extends Comparable<U>> Collection<Equation<T>> getEquations(
			int loadEquations, String fileName, AbstractGraphletFactory<T, U> factory, GraphletTree<T, U> tree,
			int order) {
		if (loadEquations == 1) {
			// GraphletTree<?, ?> tree = null;
			if (fileName.substring(fileName.length() - 5).equals(".equ"))
				try (FileInputStream fis = new FileInputStream(fileName);
						ObjectInputStream ois = new ObjectInputStream(fis);) {
					System.out.println("Loading equations...");
					Collection<Equation<T>> equations = (List<Equation<T>>) ois.readObject();
					return equations;
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			return null;
		} else {
			System.out.println("Generating equations...");
			long time = System.nanoTime();
			Collection<Equation<T>> result = new EquationGenerator<T, U>(factory, order, tree).generateEquations();
			System.out.println((System.nanoTime()-time)/1e9+" s");
			if (loadEquations == -1) {
				System.out.println("Saving equations...");
				GraphletIO.save(result, fileName);
			}
			return result;
		}
	}

	private static <T extends AbstractGraphlet<U>, U extends Comparable<U>> GraphletTree<T, U> getTree(int loadTree,
			String fileName, AbstractGraphletFactory<T, U> factory, int order) {
		if (loadTree == 1) {
			GraphletTree<T, U> tree = null;
			if (fileName.substring(fileName.length() - 5).equals(".tree"))
				System.out.println("Loading tree...");
			try (FileInputStream fis = new FileInputStream(fileName);
					ObjectInputStream ois = new ObjectInputStream(fis);) {
				tree = (GraphletTree<T, U>) ois.readObject();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			return tree;
		} else {
			System.out.println("Generating tree...");
			long time = System.nanoTime();
			GraphletTree<T, U> tree = new TreeGenerator<>(factory, order).generateTree();
			System.out.println((System.nanoTime()-time)/1e9+" s");
			if (loadTree == -1) {
				System.out.println("Saving tree...");
				GraphletIO.save(tree, fileName);
			}
//			tree.print();
			return tree;
		}
	}

	// private static Set<String> ORBITS;

	// private static void makeSets() {
	// ORBITS = new TreeSet<>();
	// String[]orbits = {"orbit","orbits","o"};
	// makeSet(ORBITS,orbits);
	// }

	private static Set<String> makeSet(String[] array) {
		Set<String> set = new TreeSet<>();
		for (String s : array) {
			set.add(s);
		}
		return set;
	}

	private static int intLoop(int min, int max) {
		while (true) {
			try {
				int i = reader.nextInt();
				if (i < min || i > max) {
					System.out.print("Please enter a number");
					if (min == Integer.MIN_VALUE) {
						if (max != Integer.MAX_VALUE) {
							System.out.println(" under " + max);
						} else {
							System.out.println();
						}
					} else {
						if (max == Integer.MAX_VALUE) {
							System.out.println(" over " + min);
						} else {
							System.out.println(" between " + min + " and " + max);
						}
					}
				} else {
					return i;
				}

			} catch (InputMismatchException e) {
				reader.next();
				System.out.print("Please enter a number");
				if (min == Integer.MIN_VALUE) {
					if (max != Integer.MAX_VALUE) {
						System.out.println(" under " + max);
					} else {
						System.out.println();
					}
				} else {
					if (max == Integer.MAX_VALUE) {
						System.out.println(" over " + min);
					} else {
						System.out.println(" between " + min + " and " + max);
					}
				}
			}
		}
	}

	private static double doubleLoop() {
		while (true) {
			try {
				double i = reader.nextDouble();
				if (i < 0 || i > 1) {
					System.out.print("Please enter a number between 0 and 1");
				} else {
					return i;
				}

			} catch (InputMismatchException e) {
				reader.next();
				System.out.print("Please enter a number between 0 and 1");
			}
		}
	}

	private static boolean booleanLoop(Collection<String> inputTrue, Collection<String> inputFalse) {
		while (true) {
			String check = reader.next();
			if (inputTrue.contains(check.toLowerCase())) {
				return true;
			}
			if (inputFalse.contains(check.toLowerCase())) {
				return false;
			}
			System.out.println("Please enter either ");
			Iterator<String> it = inputTrue.iterator();
			System.out.print(it.next());
			while (it.hasNext()) {
				System.out.print(" / ");
				System.out.print(it.next());
			}
			System.out.println("\nor");
			it = inputFalse.iterator();
			System.out.print(it.next());
			while (it.hasNext()) {
				System.out.print(" / ");
				System.out.print(it.next());
			}
			System.out.println();

		}
	}

}
