package carpus.util;

import java.util.GregorianCalendar;

public class DateFunctions {

	public static String getSQLDateStr(GregorianCalendar tmpDate) {
		String result = "";
		if (tmpDate == null)
			return null;
		result += "" + tmpDate.get(GregorianCalendar.YEAR) + "-";
		if (tmpDate.get(GregorianCalendar.MONTH) + 1 < 10)
			result += "0";
		result += "" + (tmpDate.get(GregorianCalendar.MONTH) + 1) + "-";
		if (tmpDate.get(GregorianCalendar.DATE) < 10)
			result += "0";
		result += "" + tmpDate.get(GregorianCalendar.DATE) + " ";
		return result.trim();
	}
	public static String getAccessDateStr(GregorianCalendar tmpDate) {
		String result = "";
		if (tmpDate == null)
			return null;
		result += "" + (tmpDate.get(GregorianCalendar.MONTH) + 1) + "/";
		result += "" + tmpDate.get(GregorianCalendar.DATE) + "/";
		result += "" + tmpDate.get(GregorianCalendar.YEAR);
		return result.trim();
	}
	public static String getSQLDateStr(java.util.Date tmpDate) {
		return getSQLDateStr(gregorianFromJavaDate(tmpDate));
	}

	public static String getSQLTimeStr(GregorianCalendar tmpDate) {
		String result = "";
		try {
			result += "" + tmpDate.get(GregorianCalendar.HOUR_OF_DAY) + ":";
			result += "" + tmpDate.get(GregorianCalendar.MINUTE) + ":";
			result += ""
				+ ((tmpDate.get(GregorianCalendar.SECOND) < 10) ? "0" : "")
				+ tmpDate.get(GregorianCalendar.SECOND)
				+ " ";
			//  	result +=  "" + ((tmpDate.get(GregorianCalendar.AM_PM) == GregorianCalendar.AM) ? "AM": "PM");		
			return result.trim();
		} catch (Exception e) {
			return "Err extracting time from " + tmpDate;
		}
	}

	public static String getSQLDateTimeStr(GregorianCalendar tmpDate) {
		String results;
		results = getSQLDateStr(tmpDate) + " " + getSQLTimeStr(tmpDate);
		return results.trim();
	}

	public static java.util.Date javaDateFromGregorian(
		GregorianCalendar tmpDate) {
		if (tmpDate == null)
			return null;
		return new java.util.Date(tmpDate.getTime().getTime());
	}
	public static java.util.GregorianCalendar gregorianFromString(String str) {
		if (str == null || str.length()==0)
			return null;
		GregorianCalendar results = new GregorianCalendar();
		java.util.Date date = null;

		java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat();
		try {
			date = formatter.parse(str);
			results.setTimeInMillis(date.getTime());
			return results;
		} catch (Exception e) {		}

		formatter = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			date = formatter.parse(str);
			results.setTimeInMillis(date.getTime());
			return results;
		} catch (Exception e) {		}

		formatter = new java.text.SimpleDateFormat("yyyy-MM-dd");
		try {
			date = formatter.parse(str);
			results.setTimeInMillis(date.getTime());
			return results;
		} catch (Exception e) {		}

		formatter = new java.text.SimpleDateFormat("MM/dd/yy");
		try {
			date = formatter.parse(str);
			results.setTimeInMillis(date.getTime());
			return results;
		} catch (Exception e) {		}

		formatter = new java.text.SimpleDateFormat("MMddyy");
		try {
			date = formatter.parse(str);
			results.setTimeInMillis(date.getTime());
			return results;
		} catch (Exception e) {		}

		return null;
	}

	public static GregorianCalendar gregorianFromJavaDate(
		java.util.Date tmpDate) {
		GregorianCalendar results;
		if (tmpDate == null)
			return null;
		results = new GregorianCalendar();
		results.setTime(tmpDate);
		return results;
	}

	public static boolean isCurrentFiscalYear(GregorianCalendar date) {
		GregorianCalendar aYearAgo = new GregorianCalendar();

		aYearAgo.add(GregorianCalendar.YEAR, -1);
		aYearAgo.set(GregorianCalendar.MONTH, GregorianCalendar.OCTOBER);
		aYearAgo.set(GregorianCalendar.DATE, 1);
		aYearAgo.add(GregorianCalendar.DATE, -1);

		return date.after(aYearAgo);
	}

	public static void main(String args[]) {
		//  	GregorianCalendar date1 = (GregorianCalendar) carpus.Billing.BudgetReport.fyStart.clone();;
		//        GregorianCalendar date2 = new GregorianCalendar(2001, 10, 22);
		//        System.out.println(gregorianFromString("2002-01-08 07:48:52-05"));
		// 	GregorianCalendar tmpDate = gregorianFromString("2002-01-08 07:48:52-05");
		//        System.out.println(getSQLDateTimeStr(tmpDate));
		//       System.out.println(getSQLTimeStr(tmpDate));
		//        System.out.println(gregorianFromString("1985-06-26 00:00:00"));
		System.out.println(gregorianFromString("12/6/03"));

		//    	GregorianCalendar test1 = new GregorianCalendar(2000, 10, 10);
		//  	java.util.Date test2=test1.getTime();

		//  	System.out.println(getSQLDateStr(DateFunctions.gregorianFromString("10/10/2001")));
	}

	public static int dateDifference(
		GregorianCalendar date1,
		GregorianCalendar date2) {
		int results = 0;
		if (date1 == null && date2 == null)
			return 0;
		results =
			date2.get(GregorianCalendar.DAY_OF_YEAR)
				- date1.get(GregorianCalendar.DAY_OF_YEAR);
		if (date1.get(GregorianCalendar.YEAR)
			!= date2.get(GregorianCalendar.YEAR))
			results += 365
				* (date2.get(GregorianCalendar.YEAR)
					- date1.get(GregorianCalendar.YEAR));

		return results;
	}
	public static String monthStr(int month) {
		String dates[] =
			{
				"January",
				"February",
				"March",
				"April",
				"May",
				"June",
				"July",
				"August",
				"September",
				"October",
				"November",
				"December" };
		if (month >= 0 && month < 12)
			return dates[month];
		return "Invalid Month";
	}
}
