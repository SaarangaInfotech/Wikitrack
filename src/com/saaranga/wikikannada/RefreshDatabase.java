package com.saaranga.wikikannada;

import java.net.URL;
import java.util.ArrayList;

import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.saaranga.wikitrack.utilities.Constants;

/**
 * Utitlity class - refresh the database tables
 * 
 * USAGE: create the refreshDatabase object, call the update method supplying
 * the feedType variable, the feed is downloaded and compared with the database
 * if updates are present, the will be added to the database
 * 
 * @author supreeth
 * @version 1.0 30-05-2012
 * 
 *          Copyright Saaranga Infotech
 */
public class RefreshDatabase {

	private static final String tag = "Update Database Class";

	private static final String DEBUG_TAG = "Update Database Class";

	private Context mContext;
	private boolean isDBUpdated = false;

	public static boolean isErrorInFetchingFeed = false;
	public static int NUM_OF_ITEMS_ADDED = 0;

	private RSSFeed feed = new RSSFeed();
	private FeedDBAdapter feedDBAdapter;

	SharedPreferences settings;

	public RefreshDatabase(Context c) {
		mContext = c;
	}

	/*
	 * this method is called from outside when refresh button is pressed
	 * 
	 * @param - feedType - which feed is to be updated
	 */
	public boolean update(String feedType) {

		if (feedType.equals("recent_changes")) {
			updateDatabase(feedType, Constants.RECENT_CHANGES_FEEDURL);

		} else if (feedType.equals("my_contributions")) {
			settings = mContext.getSharedPreferences("MY_PREFS",
					Context.MODE_PRIVATE);
			if (settings.contains("MYCONTRIBUTIONS_USERNAME")) {
				// username is present
				String usernameString = settings.getString(
						"MYCONTRIBUTIONS_USERNAME", "");
				StringBuilder stringBuilder = new StringBuilder();
				stringBuilder
						.append("http://kn.wikipedia.org/w/api.php?action=feedcontributions&user=");
				stringBuilder.append(usernameString);
				stringBuilder.append("&feedformat=atom");
				String myContributionsUrl = stringBuilder.toString();
				updateDatabase(feedType, myContributionsUrl);
			}

		}

		return isDBUpdated;
	}

	/*
	 * this method gets the parsed feed compares the date of the last feed in
	 * the database with the, latest item in the feed. If they match, feed is
	 * not updated. If they dont match, the item is inserted in to the database
	 * till the match is found. If the match is not found, all the items in the
	 * feed are inserted in to the database
	 * 
	 * PS: for shravya feed insert feed has different paramaters
	 * 
	 * @param feedType, Url of the feed
	 */
	private void updateDatabase(String feedType, String URL) {

		// fetch the Rss Feed from the given url
		feed = getFeed(URL);

		// check of the database is up to date by comparing published date of
		// the latest item in the feed
		if (feed != null) {
			// itemCount will be same as size
			int newItemsCount = feed.getItemCount();
			mlog("Fresh feed count: " + newItemsCount);

			ArrayList<String> pubDateList = new ArrayList<String>(newItemsCount);
			for (int i = 0; i < newItemsCount; i++) {
				String newPubDate = feed.getAllItems().get(i).getUpdated();
				try {
					pubDateList.add(newPubDate);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			String latestDateInFeed = pubDateList.get(0);
			mlog("Date of latest feed: " + latestDateInFeed);

			// compare that with the pubdate of the latest item in the db
			String tableName = "";
			if (feedType.equals("recent_changes")) {
				tableName = Constants.RECENT_CHANGES;
			} else if (feedType.equals("my_contributions")) {
				tableName = Constants.MY_CONTRIBUTIONS;
			}

			feedDBAdapter = new FeedDBAdapter(mContext);
			String latestDateInDatabase = feedDBAdapter.getPubDateList(
					tableName).get(0);

			mlog("data base date: " + latestDateInDatabase);

			// if not check the new feed and inser them in db
			int numOfNewItems = 0;
			for (int j = 0; j < newItemsCount; j++) {
				mlog("is " + pubDateList.get(j) + "==" + latestDateInDatabase);
				if (!pubDateList.get(j).equals(latestDateInDatabase)) {
					mlog("false");
					numOfNewItems++;
				} else {
					mlog("true");
					break;
				}

			}

			mlog("fresh Items: " + numOfNewItems);
			// index of the old items will be incremented
			mlog(""
					+ feedDBAdapter.updateTheIndexOfRows(tableName,
							numOfNewItems));

			// after incrementing trim the database
			// trim to the database length - always be true
			feedDBAdapter.trimDatabaseToFeedItemLimit(feedType);

			NUM_OF_ITEMS_ADDED = numOfNewItems;

			for (int k = 0; k < numOfNewItems; k++) {
				mlog("inside update loop");
				String title, author, link, pubdate, description;

				mlog("getting unicode data");
				title = feed.getAllItems().get(k).getTitle();
				author = feed.getAllItems().get(k).getAuthor();
				link = feed.getAllItems().get(k).getLink();
				pubdate = feed.getAllItems().get(k).getUpdated();
				description = feed.getAllItems().get(k).getSummary();

				// insert the item to database and update the count
				feedDBAdapter.open();

				feedDBAdapter.insertFeedItem(feedType, title, link,
						description, pubdate, author, k); // starts with zero
				feedDBAdapter.close();

				isDBUpdated = true;

			}
			// release
			feed = null;
		}
	}

	/*
	 * starndard feed fetcher and parser method, called from updateDatabase()
	 * method
	 * 
	 * @ params - url to RSS feed
	 */
	private RSSFeed getFeed(String urltoRSSFeed) {

		Log.i(tag, "get Feed method started");

		try {

			URL url = new URL(urltoRSSFeed);
			// create the factory
			SAXParserFactory factory = SAXParserFactory.newInstance();

			// create the parser
			SAXParser parser = factory.newSAXParser();

			// create the reader (scanner)
			XMLReader xmlreader = parser.getXMLReader();

			// instantiate the our handler
			RSSHandler theRSSHandler = new RSSHandler();

			// assign our handler
			xmlreader.setContentHandler(theRSSHandler);

			// get our data through the url class
			InputSource is = new InputSource(url.openStream());

			// perform synchronous parse
			xmlreader.parse(is);

			// get the results - should be a fully populated RSS Feed instance,
			// or null or an error
			return theRSSHandler.getFeed();

		} catch (FactoryConfigurationError e) {
			e.printStackTrace();
			return null;
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
			return null;
		} catch (SAXException e) {
			e.printStackTrace();
			return null;
		} catch (Exception ee) {
			// if there is any problem simply return null
			ee.printStackTrace();
			Log.i(tag, "error in parsing feed");
			return null;
		}
	}

	private void mlog(String msg) {
		Log.d(DEBUG_TAG, msg);
	}

}
