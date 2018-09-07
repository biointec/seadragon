package graphletgeneration;

import java.util.ArrayList;
import java.util.List;

import coGraphlet.CoGraphletFactory;
import diGraphlet.DiGraphletFactory;
import graphlets.AbstractGraphlet;
import graphlets.GraphletIO;

public class GraphletGenerator {

	public static <T extends AbstractGraphlet<?>> List<T> generateGraphlets(int order,
			AbstractGraphletFactory<T> type) {
		// int order = 6;
		type.setOrder(order);
		List<T> g = new ArrayList<>();
		if (order == 1) {
			g.add(type.emptyGraphlet());
		} else {
			while (type.hasNext()) {
				T dg = type.next();
				// System.out.println(dg);
				if (dg.isConnected() && dg.permute()) {
					g.add(dg);
				}
			}
		}
		return g;

	}

	public static <T extends AbstractGraphlet<?>> List<List<T>> generateGraphlets(int minOrder, int maxOrder,
			AbstractGraphletFactory<T> type) {
		List<List<T>> result = new ArrayList<>();
		for (int order = minOrder; order < maxOrder + 1; order++) {
			result.add(generateGraphlets(order, type));
		}
		return result;
	}

}
