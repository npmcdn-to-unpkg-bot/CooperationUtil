package pub.amitabha.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class DateToTimestamp {
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
