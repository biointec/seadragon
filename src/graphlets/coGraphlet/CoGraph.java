package graphlets.coGraphlet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import equationgeneration.CommonsCounter;
import graphletgeneration.AbstractGraphletFactory;
import graphletgeneration.GraphletIterator;
import graphlets.AbstractGraph;
import graphlets.AbstractGraphlet;
import graphlets.GraphletIO;
import graphlets.IllegalGraphActionException;
import graphlets.simpleGraphlet.SimpleGraph;
import graphlets.simpleGraphlet.SimpleGraphlet;

public class CoGraph extends AbstractGraph<Integer> {

//	List<List<Set<Integer>>> neighbours;
	private List<SortedMap<Integer,Integer>> neighbours;
	private Map<String, Integer> types;
	private List<String> inverseTypes;
	private int largestEdge = 0;

	public CoGraph() {
		neighbours = new ArrayList<>();
		types = new HashMap<>();
		inverseTypes = new ArrayList<>();
	}

	@Override
	protected void addNodeInternal() {
//		neighbours.add(new ArrayList<>());
		neighbours.add(new TreeMap<>());
	}
	
	
	@Override
	protected void removeNodeInternal(int node) throws IllegalGraphActionException {
		neighbours.remove(node);
		for(SortedMap<Integer,Integer> m:neighbours) {
			m.remove(node);
			for (int i = node + 1; i < getOrder(); i++) {
				Integer remove = m.remove(i);
				if (remove != null) {
					m.put(i - 1,remove);
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
			types.put(pieces, types.size()+1);
			inverseTypes.add(pieces);
		}
		return types.get(pieces);
	}

	@Override
	protected void addEdgeInternal(int node1, int node2, Integer edgeType) throws IllegalGraphActionException {
		checkNode(node1);
		checkNode(node2);
		if(largestEdge<edgeType) {
			largestEdge=edgeType;
		}
		if (edgeType == null || edgeType < 0) {
			throw new IllegalGraphActionException("Invalid edge type "+ edgeType);
		}
		if(node1==node2) {
			throw new IllegalGraphActionException("No self-loops allowed");
		}
		if(neighbours.get(node1).containsKey(node2)) {
			throw new IllegalGraphActionException("No double edges allowed");
		}else {
			neighbours.get(node1).put(node2, edgeType);
			neighbours.get(node2).put(node1, edgeType);
		}
	}

	@Override
	protected void removeEdgeInternal(int node1, int node2) throws IllegalGraphActionException {
		checkNode(node1);
		checkNode(node2);
		if(neighbours.get(node1).remove(node2) != null) {
			throw new IllegalGraphActionException("No such edge");
		}
		neighbours.get(node2).remove(node1);
	}

	@Override
	public void removeEdgeInternal(int node1, int node2, Integer edgeType) throws IllegalGraphActionException {
		checkNode(node1);
		checkNode(node2);
		if(neighbours.get(node1).remove(node2,edgeType) ) {
			throw new IllegalGraphActionException("No such edge");
		}
		neighbours.get(node2).remove(node1,edgeType);

	}

	@Override
	public SortedSet<Integer> getEdges(int node1, int node2) throws IllegalGraphActionException {
		checkNode(node1);
		checkNode(node2);
		SortedSet<Integer> result = new TreeSet<>();
		Integer integer = neighbours.get(node1).get(node2);
		if(integer!=null) {
			result.add(integer);
		}
		return result;
	}

	@Override
	public SortedSet<Integer> getNeighbours(int node, Integer edgeType) {
		SortedSet<Integer>result = new TreeSet<>();
		for(int i:neighbours.get(node).keySet()) {
			if(neighbours.get(node).get(i)==edgeType) {
				result.add(i);
			}
		}
		return result;
	}

	@Override
	public SortedSet<Integer> getInvertedNeighbours(int node, Integer edgeType) {
		return getNeighbours(node,edgeType);
	}

	@Override
	public SortedMap<Integer, SortedSet<Integer>> getNeighbours(int node) {
		SortedMap<Integer,SortedSet<Integer>> result = new TreeMap<>();
		for(int i:neighbours.get(node).keySet()) {
			SortedSet<Integer>s = new TreeSet<>();
			s.add(neighbours.get(node).get(i));
			result.put(i, s);
		}
		return result;
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
		return new CoGraphletFactory(largestEdge, useOrbits);
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for(int i=0;i<order;i++) {
//			System.out.println(i);
//			System.out.println(neighbours.get(i));
			sb.append(neighbours.get(i));
//			for(int j=0;j<neighbours.get(i).size();j++) {
//				System.out.println(j);
//				if(!neighbours.get(i).get(j).isEmpty()) {
//					sb.append(inverseTypes.get(j));
//					sb.append(":");
//					sb.append(neighbours.get(i).get(j));
//				}
//			}
			sb.append("\n");
		}
		return sb.toString();
	}

	public int getNColors() {
		return largestEdge;
	}
}
