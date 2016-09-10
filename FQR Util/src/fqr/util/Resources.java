package fqr.util;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
public class Resources {
	private static Properties properties = null;
	public static Properties getResource() {
		if(properties != null)
			return properties;

		try {
			InputStream resourcesStream = Resources.class.getResourceAsStream("/config.properties");
			properties = new Properties();
			properties.load(resourcesStream);
		} catch (IOException e) {
		}
	
		return properties;
	}
	
	public static String getProperty(String key) {
		Properties properties = getResource();
		if(properties != null) {
			return properties.getProperty(key);
		} else {
			return null;
		}
	}

	public static InputStream getInputstream(String key) {
		Properties properties = getResource();
		if(properties != null) {
			InputStream resourcesStream = Resources.class.getResourceAsStream(properties.getProperty(key));
			return resourcesStream;
		} else {
			return null;
		}
	}
}
