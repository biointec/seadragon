package diGraphlet;

import graphletgeneration.GraphletFactory;

public class DiGraphletFactory extends GraphletFactory<DiGraphlet> {

	public DiGraphletFactory(boolean isOrbitRep) {
		super(isOrbitRep);
	}

	private boolean[] matrix;
	private int count;

	public void setOrder(int order) {
		super.setOrder(order);
		matrix = new boolean[order * (order - 1)];
		count = 0;
	}

	@Override
	public boolean hasNext() {
		return count != Math.pow(2, order * (order - 1)) - 1;
	}

	@Override
	public DiGraphlet next() {
		int i = matrix.length - 1;
		while (matrix[i]) {
			matrix[i] = false;
			--i;
		}
		matrix[i] = true;
		++count;
		return new DiGraphlet(matrix, isOrbitRep);
	}

	
}
