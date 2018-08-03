package graphletgeneration;

import java.util.ArrayList;
import java.util.List;

import coGraphlet.CoGraphletFactory;
import diGraphlet.DiGraphletFactory;
import graphlets.AbstractGraphlet;
import graphlets.GraphletIO;

public class GraphletGenerator {

	public static <T extends AbstractGraphlet<?>> void generateGraphlets(int maxOrder, GraphletFactory<T> type) {
		generateGraphlets(2,maxOrder,type);
	}
	
	public static <T extends AbstractGraphlet<?>> void generateGraphlets(int minOrder, int maxOrder, GraphletFactory<T> type) {
		for (int order = minOrder; order < maxOrder + 1; order++) {
			// int order = 6;
			type.setOrder(order);
			List<AbstractGraphlet<?>> g = new ArrayList<>();
			while (type.hasNext()) {
				AbstractGraphlet<?> dg =  type.next();
//				System.out.println(dg);
				if (dg.isConnected() && dg.permute()) {
					g.add(dg);
				}
			}
			System.out.println(g);
			GraphletIO.save(g, "dump/" + g.get(0).name() + "s-" + order + ".gpl");
			GraphletIO.draw(g, "dump/" + g.get(0).name() + "s-" + order + ".ps");
		}
	}

	public static void main(String[] args) {
		generateGraphlets(3, new DiGraphletFactory(true));
	}
}
