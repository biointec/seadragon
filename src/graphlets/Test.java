package graphlets;

import java.util.HashSet;
import java.util.Set;


public class Test {
	private class Edge {
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result + a;
			result = prime * result + b;
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null) {
				return false;}
			if (getClass() != obj.getClass()) {
				return false;}
			Edge other = (Edge) obj;
			if (!getOuterType().equals(other.getOuterType())) {
				return false;}
			if (a != other.a) {
				return false;}
			if (b != other.b) {
				return false;}
			return true;
		}

		int a, b;

		Edge(int a, int b) throws IllegalGraphActionException {
			if (a == b)
				throw new IllegalGraphActionException("No self-loops allowed");
			this.a = Math.min(a, b);
			this.b = Math.max(a, b);
		}

		private Test getOuterType() {
			return Test.this;
		}
		
		public String toString() {
			return"("+a+","+b+")";
		}

	}
	
	private Set<Edge> edges;
	private Edge test;
	private int order;
	private int size;


	public Test(String representation, boolean isOrbitRep) {
		edges = new HashSet<>();
		order = (1 + (int) Math.sqrt(1 + 8 * representation.length())) / 2;
		for (int i = 1; i < order; i++) {
			for (int j = 0; j < i; j++) {
				if (representation.charAt((i * (i - 1)) / 2)+j != '0') {
					try {
						addEdge(i,j,true);
//						edges.add(new Edge(i, j));
					} catch (IllegalGraphActionException e) {
					}
				}
			}
		}
		try {
			test = new Edge(0,1);
		} catch (IllegalGraphActionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		size = edges.size();
	}
	
	public void addEdge(int i, int j, Boolean status) throws IllegalGraphActionException {
		if(i==j) {
			throw new IllegalGraphActionException("No self-loops allowed");
		}
		if(!edges.add(new Edge(i,j))) {
			throw new IllegalGraphActionException("No multiple edges allowed");
		}
	}
	
	public static void main(String[] args) throws IllegalGraphActionException {
		Test a = new Test("111",true);
		System.out.println(a.edges);
		a.addEdge(1, 2, true);
		System.out.println(a.edges);
		for(Edge e:a.edges) {
			System.out.println(e.hashCode());
		}
	}
}
