package virtualindex.virtualindexcore.sqlrequests;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.logging.Logger;

import virtualindex.virtualindexcore.Principal;

public class DateProcessing {

	private static String year;
	private static String month;
	private static String day;
	static final Logger logger = Logger.getLogger(Principal.class.getName());

	public DateProcessing(String year, String month, String day) {
		DateProcessing.year = year;
		DateProcessing.month = month;
		DateProcessing.day = day;
	}

	public DateProcessing() {
	}

	public static String getDate() {
		String result = year + "-" + month + "-" + day;
		return result;
	}

	public Calendar sqlToCalendar(String sqlDate, String format) throws ParseException {
		Calendar calendarDate = new GregorianCalendar();
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
		calendarDate.setTime(simpleDateFormat.parse(sqlDate));
		return calendarDate;
	}

	public String calendarToSql2(Calendar cal) // Deprecated
	{
		Date date;
		date = cal.getTime();
		@SuppressWarnings("deprecation")
		int test = date.getMonth() + 1;
		return cal.get(Calendar.YEAR) + "-" + test + "-" + cal.get(Calendar.DAY_OF_MONTH);
	}

	public static String calendarToSql(Calendar cal) {
		Date date;
		date = cal.getTime();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		return sdf.format(date);
	}

	public String dateToSql(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		return sdf.format(date);
	}

	public static String todaySqlDate() {
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		return sdf.format(date).toString();
	}

	public synchronized static String todaySqlTime() {
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("kk:mm:ss");
		return sdf.format(date).toString();
	}

	public synchronized static String offsetSqlDate(int offset, String date) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Calendar todayCal = new GregorianCalendar();
		try {
			todayCal.setTime(sdf.parse(date));
		} catch (Exception e) {
			logger.severe(e.toString());
		}
		Calendar offsetCal = todayCal;
		offsetCal.add(Calendar.DATE, offset);
		Date offsetDate = offsetCal.getTime();
		String offsetString = sdf.format(offsetDate);
		return offsetString;
	}

	public static Long stringToEpoch(String sqlDate) throws ParseException {
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		date = sdf.parse(sqlDate);
		long epoch = date.getTime();
		return epoch;
	}

}
