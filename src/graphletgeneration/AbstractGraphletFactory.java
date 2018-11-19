package graphletgeneration;

import java.util.List;
import java.util.SortedSet;

import graphlets.AbstractGraphlet;

/**
 * Abstract factory class for graphlets of any type. This class mainly concerns
 * translation of string representations into graphlets. This is used to
 * generate and copy graphlets while providing type security.
 * 
 * @author Ine Melckenbeeck
 *
 * @param <T>
 *            The used graphlet type.
 * @param <U>
 *            The graphlet type's edge type.
 */
public abstract class AbstractGraphletFactory<T extends AbstractGraphlet<U>, U extends Comparable<U>> {
	protected boolean isOrbitRep;

	/**
	 * Create a graphlet factory that generates either normal graphlets or orbit
	 * representatives. Orbit representatives' node 0 does not participate in any
	 * permutations, which means they represent a single orbit in the graphlet in
	 * which all nodes are permuted.
	 * 
	 * @param isOrbitRep
	 *            A boolean specifying whether this graphlet factory generates orbit
	 *            representatives (<code>true</code>) or normal graphlets
	 *            (<code>false</code>).
	 */
	public AbstractGraphletFactory(boolean isOrbitRep) {
		this.isOrbitRep = isOrbitRep;
	}

	/**
	 * Copy the given graphlet in its canonical permutation.
	 * 
	 * @param graphlet
	 *            The graphlet to be copied.
	 * @return A copy of the given graphlet in its canonical permutation.
	 */
	public T canonicalVersion(T graphlet) {
		return toGraphlet(graphlet.canonical());
	}

	/**
	 * Returns an array containing all valid characters that represent an edge in
	 * this graphlet type's representations. The character <code>'0'</code> is not
	 * included in here, because this is the character that represents the absence
	 * of an edge in any graphlet type.
	 * 
	 * @return
	 */
	protected abstract char[] validCharacters();

	/**
	 * Returns the length of a representation of a graphlet of the given order.
	 * 
	 * @param order
	 *            The order of the graphlets.
	 * @return The length of a representation of a graphlet of the given order.
	 */
	protected abstract int representationLength(int order);

	/**
	 * Construct the graphlet with the given representation.
	 * 
	 * @param representation
	 *            The representation of the graphlet to be created.
	 * @return The graphlet with the given representation.
	 */
	public abstract T toGraphlet(String representation);

	/**
	 * Construct the minimal graphlet of this type. It has only one node and no
	 * edges. For graphlets that have a representation with a length more than 0 for
	 * this graphlet for any reason, this method needs to be overridden.
	 * 
	 * @return The minimal graphlet of this type.
	 */
	public T oneNodeGraphlet() {
		return toGraphlet("");
	}

	/**
	 * Copies the given graphlet.
	 * 
	 * @param graphlet
	 *            The original graphlet.
	 * @return a copy of the given graphlet.
	 */
	public T copy(T graphlet) {
		return toGraphlet(graphlet.representation());
	}

	/**
	 * Return the valid edge types for this graphlet type.
	 * 
	 * @return the valid edge types for this graphlet type.
	 */
	public abstract List<U> getEdgeTypes();

	/**
	 * Return the valid combinations of edge types that may simultaneously be
	 * present between any pair of nodes in this graphlet type.
	 * 
	 * @return the valid edge type combinations between a pair of nodes in this
	 *         graphlet type.
	 */
	public abstract List<SortedSet<U>> edgeCombinations();

	/**
	 * Return the name of this graphlet type.
	 * 
	 * @return the name of this graphlet type.
	 */
	public String name() {
		return namePrefix() + (isOrbitRep ? "OrbitRep" : "Graphlet");
	}

	/**
	 * Return the prefix of this graphlet type's name (the part that comes before
	 * -graphlet). This is mainly used for input and output.
	 * 
	 * @return the prefix of this graphlet type's name.
	 */
	protected abstract String namePrefix();

}
