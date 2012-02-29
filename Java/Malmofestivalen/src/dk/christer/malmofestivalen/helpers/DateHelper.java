package dk.christer.malmofestivalen.helpers;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.text.format.DateUtils;

public class DateHelper {

	static DateFormat iso8601Format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	static DateFormat simpleHourFormat = new SimpleDateFormat("HH:mm");
	static DateFormat shortday = new SimpleDateFormat("d");
	
	public static String createShortDateResume(String startDateString, String endDateString) 
	{
		Calendar cal = Calendar.getInstance();
		
		Date startDate = new Date();
		Date endDate = new Date();
		try {
			startDate = iso8601Format.parse(startDateString);
			endDate = iso8601Format.parse(endDateString);
		}
		catch (Exception ex) {
		}
		cal.setTime(startDate);
		int dayNbrStart = cal.get(cal.DAY_OF_WEEK);
		String dayOfWeek = DateUtils.getDayOfWeekString(dayNbrStart, DateUtils.LENGTH_LONG);
		
		
		String dateString = simpleHourFormat.format(startDate) + " - " + simpleHourFormat.format(endDate) + ", "+ dayOfWeek + " d. " + shortday.format(startDate);
		return dateString;
	}
}
