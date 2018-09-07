package genGraphlet;

import graphlets.CanonicalComparator;

public class GenComparator extends CanonicalComparator {

	@Override
	public int compare(String o1, String o2) {
		if (o1.length() != o2.length()) {
			return o1.length() - o2.length();
		} else {
			for(int i=0;i<o1.length();i++) {
				if(o1.charAt(i)!=o2.charAt(i)) {
					if(o1.charAt(i)=='0') {
						return -1;
					}else if(o2.charAt(i)=='0') {
						return +1;
					}else if(o1.charAt(i)=='-') {
						return -1;
					}else {
						return 1;
					}
				}
			}
			return 0;
		}
	}

}
