package coGraphlet;

import java.util.Arrays;

import graphletgeneration.GraphletFactory;
import graphlets.AbstractGraphlet;

public class CoGraphletFactory extends GraphletFactory<CoGraphlet> {

	private int nColors;
	private int[] matrix;
	private int counter;

	public CoGraphletFactory(int nColors, boolean isOrbitRep) {
		super(isOrbitRep);
		this.nColors = nColors;
		
	}

	
	public void setOrder(int order) {
		super.setOrder(order);
		matrix = new int[(order * (order - 1)) / 2];
		counter=0;
	}

	@Override
	public boolean hasNext() {
//		System.out.println(counter+" "+(Math.pow( matrix.length,order)));
		return counter < Math.pow(nColors+1, matrix.length)-1;
	}

	@Override
	public CoGraphlet next() {
		++counter;
		int i = matrix.length - 1;
		while (matrix[i] == nColors ) {
//			System.out.println(i);
			matrix[i] = 0;
			i--;
		}
		matrix[i]++;
//		System.out.println(Arrays.toString(matrix));
		return new CoGraphlet(matrix,nColors,isOrbitRep);
	}

	@Override
	public String name() {
		return nColors + "-Co"+super.name();
	}

}
