package pub.amitabha.util;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.Serializable;

/**
 * This class converts an object to a visible string, and can convert them back.
 * The object you convert must be serializable.
 * 
 * @author Ems
 *
 */
public class ObjectStringConverter {

	/**
	 * Convert a object to a base64 String, so that it can be to database as a
	 * string
	 * 
	 * @param obj
	 * @return
	 * @throws Exception
	 */
	public static String objectToString(Serializable obj) throws Exception {

		PipedInputStream pis = new PipedInputStream();
		PipedOutputStream pos = new PipedOutputStream(pis);

		ObjectOutputStream oos = new ObjectOutputStream(pos);
		oos.writeObject(obj);
		oos.close();

		int count = pis.available();
		byte[] buf = new byte[count];
		pis.read(buf);
		pis.close();
		pos.close();

		return Base64.getBase64(buf);
	}

	/**
	 * Restore an object from a base64 String.
	 * 
	 * @param str
	 * @return
	 * @throws Exception
	 */
	public static Object stringToObject(String str) throws Exception {
		byte[] buf = Base64.base64ToByte(str);

		PipedInputStream pis = new PipedInputStream();
		PipedOutputStream pos = new PipedOutputStream(pis);
		pos.write(buf);

		ObjectInputStream ois = new ObjectInputStream(pis);
		Object o = ois.readObject();
		ois.close();
		pis.close();
		pos.close();

		return o;
	}

	@SuppressWarnings("unchecked")
	public static <T> T stringToObject(String str, Class<T> T) {
		try {
			T obj = (T)stringToObject(str);
			return obj;
		} catch (Exception e) {
			return null;
		}
	}
}
