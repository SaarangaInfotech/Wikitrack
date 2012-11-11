package com.saaranga.wikitrack.utilities;

import java.util.Calendar;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
import android.util.Log;
/**
 * Utitlity class - application constants are stored here
 * 
 * @author supreeth
 * @version 1.0 30-05-2012
 * 
 *          Copyright Saaranga Infotech
 */
public class Constants {
	
	public static final String FEEDTYPE= "FEEDTYPE";

	//URLS
	
	public static final String LINK_SAARANGA_FACEBOOK_PAGE = "https://www.facebook.com/pages/Saaranga-Infotech/176579575735262";
	public static final String LINK_SAARANGA_TWITTER_PAGE = "http://twitter.com/#%21/SaarangaTech";
	public static final String LINK_SAARANGA_WEBSITE = "http://www.saaranga.com";
	public static final String LINK_APPS_BY_SAARANGA = "market://search?q=pub:Saaranga Infotech";
	public static final String[] EMAIL_TO = { "hpn@saaranga.com" };
	
	public static final String RECENT_CHANGES_FEEDURL = "http://kn.wikipedia.org/w/index.php?title=%E0%B2%B5%E0%B2%BF%E0%B2%B6%E0%B3%87%E0%B2%B7:RecentChanges&feed=atom";
	public static final String MYWATCHLIST_URL = "http://kn.wikipedia.org/w/api.php?action=feedwatchlist&feedformat=atom";
	
	
	//constants for the feedtype
	public static final String RECENT_CHANGES = "recent_changes";
	public static final String MYWATCHLIST = "my_watchlist";
	public static final String MY_CONTRIBUTIONS = "my_contributions";
	
	//preferences constants
	
	public static final String RC_FEED_COUNT = "feed_count_recent_changes";
	public static final String WL_FEED_COUNT = "feed_count_watch_list";
	public static final String MC_FEED_COUNT = "feed_count_mycontributions";
	public static final String ERASE_PRIVATE_DATA= "erase_private_data";
	public static final String SELECT_FONT = "select_font";
	public static final String CLEAR_CACHE= "erase_cache_data";
	public static final String DELETE_DATABASE = "delete_database";
	public static final String ABOUT_DEVELOPER = "about_developer";
	public static final String ABOUT_APPLICATION = "about_application";

	public static final String FONT_KEDAGE = "Kedage-n.ttf";
	public static final String FONT_LOHIT = "lohit_kn.ttf";
	public static final String FONT_DEFAULT = "default";
	
	//MY_PREFS preference file
	public static final String USERNAME_PREFS = "MY_PREFS"; 	
	public static final String LOGIN_PASSWORD = "PASSWORD"; 
	public static final String LOGIN_USERNAME = "USERNAME"; 
	public static final String MYCONTRIBUTIONS_USERNAME = "MYCONTRIBUTIONS_USERNAME";
	
	//time formater
	public static long yearDivisor = 31536000;
	public static long dayDivisor = 86400;
	public static long hourDivisor = 3600;
	public static long minDivisor = 60;
	public static long secDivisor = 1;
	
	
	public static Typeface getTypeface(Context c){
		SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(c); 
		String font = prefs.getString(SELECT_FONT, "nothing"); mlog("font: "+font);
		
		if(font.equals(FONT_KEDAGE)) {
			return Typeface.createFromAsset(c.getAssets(), FONT_KEDAGE);
		} else if(font.equals(FONT_LOHIT)) {
			return Typeface.createFromAsset(c.getAssets(), FONT_LOHIT);
		} else {
			return null;
		}
	}
	
	
	public static String getFontName(Context c){
		SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(c); 
		String font = prefs.getString(SELECT_FONT, "nothing"); mlog("font: "+font);
		return font;
	}
	
	
	public static int getFeedLimit(String feedType, Context c){
	
		SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(c); 
		if(feedType.equals(RECENT_CHANGES))
		 return Integer.parseInt(prefs.getString(RC_FEED_COUNT, "50"));
		else if(feedType.equals(MY_CONTRIBUTIONS))
			return Integer.parseInt(prefs.getString(MC_FEED_COUNT, "50"));
		else if(feedType.equals(MYWATCHLIST))
			return Integer.parseInt(prefs.getString(WL_FEED_COUNT, "50"));
		
		else return 0;
	}
	
	public static String getFontFamily(String font) {
		if(font.equals(FONT_KEDAGE)) {
			return "kedage";
		} else if(font.equals(FONT_LOHIT)) {
			return "\"Lohit Kannada\"";
		} 
		return "none";
	}
	
	public static String formatDateStamp(String stampDate) {
		
		Calendar calendar1 = Calendar.getInstance();
		  Calendar calendar2 = Calendar.getInstance();
		  calendar1.set(2007, 01, 10);
		  calendar2.set(2007, 07, 01);
		
		return null;
	}
	
	

	private static String DEBUG_TAG = "Constants class";
	
	private static void mlog(String msg) {
		Log.d(DEBUG_TAG  , msg);
	}
}
