package fqr.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * DateTime Utility Program for Time Zone GMT+8.
 * 
 * @author Ems
 *
 */
public class DateTimeUtil {
	public static String timeZone = "GMT+8";

	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
	private static final SimpleDateFormat dateFmt = new SimpleDateFormat("yyyy-MM-dd");

	public static String getTimestamp(Date date) {
		sdf.setTimeZone(TimeZone.getTimeZone(timeZone));
		return sdf.format(date);
	}

	public static String getTimestamp(long time) {
		sdf.setTimeZone(TimeZone.getTimeZone(timeZone));
		return sdf.format(new Date(time));
	}
	
	/**
	 * Get the mili-seconds by parsing a timestamp.
	 * @param timestamp
	 * @return
	 */
	public static long parseTimestamp(String timestamp) {
		long timeMillis = -1;

		try {
			sdf.setTimeZone(TimeZone.getTimeZone(timeZone));
			Date date = sdf.parse(timestamp);
			timeMillis = date.getTime();
		} catch (Exception e) {}
		return timeMillis;
	}

	public static String getDate(Date date) {
		dateFmt.setTimeZone(TimeZone.getTimeZone(timeZone));
		return dateFmt.format(date);
	}

	public static String getDate() {
		dateFmt.setTimeZone(TimeZone.getTimeZone(timeZone));
		Date date = new Date(System.currentTimeMillis());
		return getDate(date);
	}
}
