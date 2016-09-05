package pub.amitabha.util;

import java.util.Comparator;

public class StringComparator implements Comparator<String> {
	public int compare(String s1, String s2) {
		return ((String) s1).compareTo((String) s2);
	}
}
