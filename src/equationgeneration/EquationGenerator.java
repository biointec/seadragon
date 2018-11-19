package equationgeneration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import graphletgeneration.AbstractGraphletFactory;
import graphletgeneration.GraphletIterator;
import graphlets.AbstractGraphlet;
import graphlets.CanonicalComparator;
import graphlets.IllegalGraphActionException;
import tree.GraphletTree;
import tree.TreeGenerator;
import treewalker.SingleGraphletWalker;

/**
 * Class used to generate Equations for counting graphlets of type T of one
 * higher order than those that were actually found.
 * 
 * @author Ine Melckenbeeck
 *
 * @param <T>
 *            The graphlet type.
 * @param <U>
 *            The graphlet type's edge type.
 */
public class EquationGenerator<T extends AbstractGraphlet<U>, U extends Comparable<U>> {

	Iterator<T> start;
	Collection<Equation<T>> result;
	List<U> edgeTypes;
	List<SortedSet<Integer>> edgeCombinations;
	AbstractGraphletFactory<T, U> factory;
	GraphletTree<T, U> tree;

	/**
	 * Creates a new EquationGenerator that will generate equations to count
	 * graphlets of the given type and order. The given tree is used to generate the
	 * equations, alleviating the need to generate a separate tree.
	 * 
	 * @param type
	 *            An AbstractGraphletFactory for the required graphlet type.
	 * @param order
	 *            The order of the counted graphlets.
	 * @param tree
	 *            The used graphlet tree. This tree should be a GraphletTree used to
	 *            find graphlets with <code>(order-1)</code> nodes.
	 */
	public EquationGenerator(AbstractGraphletFactory<T, U> type, int order, GraphletTree<T, U> tree) {
		this.start = new GraphletIterator<>(type, order - 1);
		assert (tree.getOrder() == order - 1);
		edgeTypes = type.getEdgeTypes();
		edgeCombinations = new ArrayList<>();
		for (SortedSet<U> s : type.edgeCombinations()) {
			SortedSet<Integer> t = new TreeSet<>();
			for (U u : s) {
				t.add(edgeTypes.indexOf(u));
			}
			edgeCombinations.add(t);
		}
		factory = type;
		this.tree = tree;
	}

	/**
	 * <p>
	 * Creates a new EquationGenerator that will generate equations to count
	 * graphlets of the given type and order. A tree structure to find graphlets
	 * with <code>(order-1)</code> nodes is generated.
	 * </p>
	 * 
	 * <p>
	 * If such a GraphletTree is used outside of this constructor,
	 * {@link #EquationGenerator(AbstractGraphletFactory, int, GraphletTree)} should
	 * be used instead, so the tree is not generated twice.
	 * </p>
	 * 
	 * @param type
	 *            An AbstractGraphletFactory for the required graphlet type.
	 * @param order
	 *            The order of the counted graphlets.
	 */
	public EquationGenerator(AbstractGraphletFactory<T, U> type, int order) {
		this(type, order, new TreeGenerator<>(type, order - 1).generateTree());
	}

	/**
	 * Generates a Collection of Equations with the parameters given at creation of
	 * this EquationGenerator. This Collection may contain multiple equations to
	 * count the same graphlet, but every graphlet (that can be created by adding a
	 * node and its edges to a graphlet with <code>(order-1)</code> nodes) will have
	 * at least one equation.
	 * 
	 * @return a Collection of Equations.
	 */
	public Collection<Equation<T>> generateEquations() {
		result = new TreeSet<>();
		while (start.hasNext()) {
			T graphlet = start.next();
			Set<Set<List<Set<Integer>>>> rhses = rhsTerms(graphlet);
			for (Set<List<Set<Integer>>> rhs : rhses) {
				int minus = minus(graphlet, rhs.iterator().next());
				SortedMap<String, Integer> lhs = lhsTerms(graphlet, rhs, minus);
				if (lhs != null) {
					Equation<T> equation = new Equation<T>(lhs, graphlet, rhs, minus, edgeTypes);
					result.add(equation);
				}
			}
		}
		return result;
	}

	/**
	 * Generates the positive right-hand side terms of all equations that have the
	 * given graphlet in the RHS sum. These are simply all valid combinations of
	 * edges to a new node in the graphlet, sorted into groups that are equivalent
	 * to each other under automorphisms of the original graphlet.
	 * 
	 * @param graphlet
	 *            The graphlet in the equation's RHS sum.
	 * @return The rhs terms of all equations with the given graphlet in the RHS
	 *         sum.
	 */
	private Set<Set<List<Set<Integer>>>> rhsTerms(T graphlet) {
		int[] counter = new int[graphlet.getOrder()];
		counter[counter.length - 1] = 1;
		Set<Set<List<Set<Integer>>>> rhses = new HashSet<>();
		while (counter[0] <= edgeCombinations.size()) {
			List<Set<Integer>> orbitcheck = new ArrayList<>();
			for (int j = 0; j < edgeTypes.size(); j++) {
				orbitcheck.add(new TreeSet<>());
			}
			for (int j = 0; j < counter.length; j++) {
				if (counter[j] != 0) {
					for (int k : edgeCombinations.get(counter[j] - 1)) {
						orbitcheck.get(k).add(j);
					}
				}
			}
			Set<List<Set<Integer>>> rhs = graphlet.getOrbitOf(orbitcheck);
			rhses.add(rhs);
			int i = counter.length - 1;
			while (i > 0 && counter[i] == edgeCombinations.size()) {
				counter[i--] = 0;
			}
			counter[i]++;
		}
		return rhses;
	}

	/**
	 * Generate the negative terms for the given right-hand side.
	 * 
	 * @param graphlet
	 *            The RHS graphlet for the equation.
	 * @param edges
	 *            One positive RHS term for the equation.
	 * @return The negative terms in the equation's RHS.
	 */
	private int minus(T graphlet, List<Set<Integer>> edges) {
		SortedSet<Integer> result = new TreeSet<>();
		for (int j = 0; j < graphlet.getOrder(); j++) {
			result.add(j);
		}
		for (int i = 0; i < edges.size(); i++) {
			for (int node : edges.get(i)) {
				result.retainAll(graphlet.getNeighbours(node, this.edgeTypes.get(i)));
			}
		}
		return result.size();
	}

	/**
	 * Generates the left-hand side terms of the equation with the given RHS. These
	 * are graphlet counts multiplied by symmetry factors. The graphlets are all
	 * graphlets that can be formed by adding edges to the graphlet described by the
	 * right-hand side.
	 * 
	 * @param graphlet
	 *            The RHS graphlet of the equation.
	 * @param edges
	 *            The positive RHS terms of the equation.
	 * @param minus
	 *            The negative terms of the equation.
	 * @return The LHS of the equation.
	 */
	private SortedMap<String, Integer> lhsTerms(T graphlet, Set<List<Set<Integer>>> edges, int minus) {
		SortedMap<String, Integer> lhs = new TreeMap<>(new CanonicalComparator());
		T oldgraphlet = factory.canonicalVersion(graphlet);
		assert (graphlet.representation().equals(oldgraphlet.representation()));
		graphlet.addNode();
		List<Set<Integer>> rhsTerm = edges.iterator().next();
		for (int i = 0; i < this.edgeTypes.size(); i++) {
			for (int j : rhsTerm.get(i)) {
				try {
					graphlet.addEdge(j, graphlet.getOrder() - 1, this.edgeTypes.get(i));
				} catch (IllegalGraphActionException e) {
					e.printStackTrace();
				}
			}
		}
		if (graphlet.isComplete()) {
			try {
				graphlet.removeNode(graphlet.getOrder() - 1);
			} catch (IllegalGraphActionException e) {
				e.printStackTrace();
			}
			return null;
		} else {
			int[] counter = new int[graphlet.getOrder()];
			while (counter[0] == 0) {
				try {
					T copy = factory.copy(graphlet);
					for (int j = 1; j < copy.getOrder(); j++) {
						if (counter[j] != 0) {
							SortedSet<Integer> edgelist = edgeCombinations.get(counter[j] - 1);
							for (int edge : edgelist) {
								copy.addEdge(j - 1, copy.getOrder() - 1, this.edgeTypes.get(edge));
							}
						}
					}
					copy = factory.canonicalVersion(copy);
					SortedMap<String, Long> run = new SingleGraphletWalker<>(tree, copy, oldgraphlet, edges, minus)
							.run(0);
					Long long1 = run.get("");
					assert (long1 != null);
					lhs.put(copy.canonical(), Math.toIntExact(long1));
				} catch (IllegalGraphActionException e) {
				}
				int i = graphlet.getOrder() - 1;
				while (counter[i] == this.edgeCombinations.size()) {
					counter[i--] = 0;
				}
				counter[i]++;
			}
			try {
				graphlet.removeNode(graphlet.getOrder() - 1);
				assert (graphlet.representation().equals(oldgraphlet.representation()));
			} catch (IllegalGraphActionException e) {
			}
			return lhs;
		}
	}
}
