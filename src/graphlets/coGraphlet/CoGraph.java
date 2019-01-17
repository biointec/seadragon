package graphlets.coGraphlet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeSet;

import graphletgeneration.AbstractGraphletFactory;
import graphlets.AbstractGraph;
import graphlets.AbstractGraphlet;
import graphlets.IllegalGraphActionException;

public class CoGraph extends AbstractGraph<Integer> {

	List<List<Set<Integer>>> neighbours;
	Map<String, Integer> types;

	public CoGraph() {
		neighbours = new ArrayList<>();
		types = new HashMap<>();
	}

	@Override
	protected void addNodeInternal() {
		neighbours.add(new ArrayList<>());
	}

	@Override
	protected void removeNodeInternal(int node) throws IllegalGraphActionException {
		neighbours.remove(node);
		for (List<Set<Integer>> colour : neighbours) {
			for (Set<Integer> noc : colour) {
				noc.remove(node);
				for (int i = node + 1; i < getOrder(); i++) {
					if (noc.remove(i)) {
						noc.add(i - 1);
					}
				}
			}
		}

	}

	@Override
	public Integer getType(String pieces) {
		if (pieces == null || pieces.equals("")) {
			return null;
		}
		if (!types.containsKey(pieces)) {
			types.put(pieces, types.size());
			System.out.println(types.size() + ":" + pieces);
		}
		return types.get(pieces);
	}

	@Override
	protected void addEdgeInternal(int node1, int node2, Integer edgeType) throws IllegalGraphActionException {
		if (edgeType == null || edgeType <= 0) {
			throw new IllegalGraphActionException("Invalid edge type");
		}
		while (neighbours.get(node1).size() < edgeType) {
			neighbours.get(node1).add(new HashSet<>());
		}
		neighbours.get(node1).get(edgeType - 1);
		while (neighbours.get(node2).size() < edgeType) {
			neighbours.get(node2).add(new HashSet<>());
		}
		neighbours.get(node2).get(edgeType - 1);
	}

	@Override
	protected void removeEdgeInternal(int node1, int node2) throws IllegalGraphActionException {
		for (Set<Integer> color : neighbours.get(node1)) {
			if (!color.remove(node2))
				throw new IllegalGraphActionException("No such edge");
		}
		for (Set<Integer> color : neighbours.get(node2)) {
			color.remove(node1);
		}

	}

	@Override
	public void removeEdgeInternal(int node1, int node2, Integer edgeType) throws IllegalGraphActionException {
		if (!neighbours.get(node1).get(edgeType).remove(node2))
			throw new IllegalGraphActionException("No such edge");
		neighbours.get(node2).get(edgeType).remove(node1);

	}

	@Override
	public SortedSet<Integer> getEdges(int node1, int node2) throws IllegalGraphActionException {
		SortedSet<Integer> result = new TreeSet<>();
		for (int i = 0; i < neighbours.get(node1).size(); i++) {
			if (neighbours.get(node1).get(i).contains(node2))
				result.add(i);
		}
		return result;
	}

	@Override
	public SortedSet<Integer> getNeighbours(int node, Integer edgeType) {
		return new TreeSet<>(neighbours.get(node).get(edgeType));
	}

	@Override
	public SortedSet<Integer> getInvertedNeighbours(int node, Integer edgeType) {
		return new TreeSet<>(neighbours.get(node).get(edgeType));
	}

	@Override
	public SortedMap<Integer, SortedSet<Integer>> getNeighbours(int node) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isComplete() {
		return size == order * (order - 1) / 2;
	}

	@Override
	public double density() {
		return size * 2. / order / (order - 1);
	}

	@Override
	public AbstractGraphletFactory<? extends AbstractGraphlet<Integer>, Integer> getGraphletType(boolean useOrbits) {
		return new CoGraphletFactory(types.size(), useOrbits);
	}

}
