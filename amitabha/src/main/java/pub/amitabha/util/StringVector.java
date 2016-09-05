package pub.amitabha.util;

import java.util.Vector;

public class StringVector extends Vector<String> {
	private static final long serialVersionUID = 406134286588359415L;

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (String str : this) {
			sb.append(str);
		}

		return sb.toString();
	}
}
