package pub.amitabha.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Reflection {
	/**
	 * Calls the getter methods to get the field by field name.
	 * 
	 * @param fieldName
	 * @return
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 */
	public static Object getAttribute(Object o, String fieldName) throws IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		StringBuilder sb = new StringBuilder("get");
		sb.append(fieldName.substring(0, 1).toUpperCase()).append(fieldName.substring(1));

		Method m = o.getClass().getDeclaredMethod(sb.toString());
		return m.invoke(o);
	}
}
