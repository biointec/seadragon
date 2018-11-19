package graphlets;

import java.util.Comparator;

/**
 * Comparator for graphlets' canonical string representation. This comparator
 * functions as a lexicographical comparator, with the exception that
 * <code>'0'</code>, representing the absence of an edge in a representation, is
 * the lowest possible value for a character.
 * 
 * @author Ine Melckenbeeck
 *
 */
public class CanonicalComparator implements Comparator<String> {

	@Override
	public int compare(String o1, String o2) {
		if (o1.length() != o2.length()) {
			return o1.length() - o2.length();
		} else {
			for (int i = 0; i < o1.length(); i++) {
				if (o1.charAt(i) != o2.charAt(i)) {
					if (o1.charAt(i) == '0') {
						return -1;
					} else if (o2.charAt(i) == '0') {
						return +1;
					} else {
						return o1.charAt(i) < o2.charAt(i) ? -1 : 1;
					}
				}
			}
			return 0;
		}
	}
}