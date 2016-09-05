package pub.amitabha.wechat;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pub.amitabha.Application;
import pub.amitabha.util.Encryption;

public class WeChat {

	private static final Logger log = LoggerFactory.getLogger(Application.class);

	public static boolean validate(String token, String signature, String timestamp, String nonce) {

		String[] array = new String[] { token, timestamp, nonce };
		Arrays.sort(array);
		String tmpStr = Encryption.SHA1(pub.amitabha.util.StringUtil.implode(array));

		log.info("Encryption result: " + tmpStr);

		if (tmpStr.equals(signature))
			return true;
		else
			return false;
	}
}
