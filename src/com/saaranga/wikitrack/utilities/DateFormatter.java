package com.saaranga.wikitrack.utilities;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


/**
 * Utitlity class - format the date string from mm-dd-yy format to ಇಷ್ಟು ದಿನ ಇಷ್ಟು ಗಂಟೆ ಇಷ್ಟು ನಿಮಿಷಗಳ ನಂತರ
 *  
 * Usage: set the pattern for the SimpleDateformatter in constructor 
 * 
 * @author supreeth
 * @version 1.0 30-05-2012
 * 
 *          Copyright Saaranga Infotech
 */
public class DateFormatter {

	private static long yearDivisor = 31536000;
	private static long dayDivisor = 86400;
	private static long hourDivisor = 3600;
	private static long minDivisor = 60;

	private static SimpleDateFormat dateFormat;
	private static long diffInsec;

	public DateFormatter(String pattern) {
		dateFormat = new SimpleDateFormat(pattern);
	}

	/**
	 * Formats the date to the pattern provided
	 * TODO check if the parameter pattern is valid 
	 * @param unformatted
	 * @return
	 */
	public String getFormattedDate(String unformatted) {

		Date myDate = null;
		try {
			myDate = dateFormat.parse(unformatted);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		Date now = new Date();

		Calendar mycal = Calendar.getInstance();
		Calendar nowCal = Calendar.getInstance();

		mycal.setTime(myDate);
		nowCal.setTime(now);

		long milliseconds1 = mycal.getTimeInMillis();
		long milliseconds2 = nowCal.getTimeInMillis();
		long diff = milliseconds2 - milliseconds1;
		diffInsec = diff / 1000;

		String year = getYear();
		String day = getDay();
		String hour = getHour();
		String min = getMin();
		String sec = getSec();

		if (!year.equals("")) {
			return year + " ಹಿಂದೆ";
		} else {
			if (!day.equals("")) {
				return day + " ಹಿಂದೆ";
			} else {
				if (!hour.equals("")) {
					if (!min.equals("")) {
						return trimWord(hour) + "," + min + " ಹಿಂದೆ";
					} else {
						return hour + " ಹಿಂದೆ";
					}
				} else {
					if (!min.equals("")) {
						return min + " ಹಿಂದೆ";
					} else {
						return sec + " ಹಿಂದೆ";
					}
				}
			}
		}

	}

	private  String getYear() {
		long diffYear = diffInsec / yearDivisor;
		if (diffYear > 0) {
			if(diffYear>1) return "" + diffYear + " ವರ್ಷಗಳ"; else return "" + diffYear + " ವರ್ಷದ";  
		} else
			return "";
	}

	private  String getDay() {
		long diffDay = (diffInsec % yearDivisor) / dayDivisor;
		if (diffDay > 0) {
			if(diffDay>1) return "" + diffDay + " ದಿನಗಳ"; else return "" + diffDay + " ದಿನದ";
		} else
			return "";
	}

	private  String getHour() {
		long diffHr = ((diffInsec % yearDivisor) % dayDivisor) / hourDivisor;
		if (diffHr > 0) {
			if(diffHr>1) return "" + diffHr + " ಘಂಟೆಗಳ"; else return "" + diffHr + " ಘಂಟೆಯ";
		} else
			return "";
	}

	private  String getMin() {
		long diffMin = (((diffInsec % yearDivisor) % dayDivisor) % hourDivisor)
				/ minDivisor;
		if (diffMin > 0) {
			if(diffMin>1) return "" + diffMin + " ನಿಮಿಷಗಳ"; else return "" + diffMin + " ನಿಮಿಷದ";
		} else
			return "";
	}

	private  String getSec() {
		long diffSec = (((diffInsec % yearDivisor) % dayDivisor) % hourDivisor)
				% minDivisor;
		if (diffSec > 0) {
			if(diffSec>1) return "" + diffSec + " ಸೆಕೆಂಡುಗಳ" ; else return "" + diffSec + " ಸೆಕೆಂಡು";
		} else
			return "";
	}
	
	private String trimWord(String untrimmed) {
		int index = untrimmed.indexOf("ಗ");
		int index2 = untrimmed.indexOf("ದ");
		int index3 = untrimmed.indexOf("ಯ");
		if(index!=-1) {
			return untrimmed.substring(0, index);
		} else if(index2!=-1) {
			return untrimmed.substring(0, index2);
		}else if(index3!=-1) {
			return untrimmed.substring(0, index3);
		}else {
			return untrimmed;
		}
		
		
	}

}
