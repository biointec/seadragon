package graphlets;

import java.util.Comparator;
import java.util.List;

public class ListComparator<T extends Comparable<T>> implements Comparator<List<T>> {

	@Override
	public int compare(List<T> arg0, List<T> arg1) {
		if(arg0.size()!=arg1.size()) {
			return (arg0.size()-arg1.size());
		}else {
			int i=0;
			while(i<arg0.size()&& arg0.get(i).compareTo(arg1.get(i))==0) {
				i++;
			}
			if(i<arg0.size()) {
				return arg0.get(i).compareTo(arg1.get(i));
			}else {
				return 0;
			}
		}
		
	}

}
