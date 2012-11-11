package com.saaranga.wikikannada;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.saaranga.wikitrack.utilities.Constants;

/**
 * Database adapter class
 * 
 * PUPOSE: create database tables and provide methods to access the database
 * 
 * @author supreeth
 * @version 1.0 30-05-2012
 * 
 *          Copyright Saaranga Infotech
 */
public class FeedDBAdapter {

	private String DEBUG_TAG = "DBAdapter";

	//-------------- Database column constants-----//
	public static final String KEY_ROWID = "_id";
	public static final String KEY_TITLE = "title";
	public static final String KEY_LINK = "link";
	public static final String KEY_SUMMARY = "summary";
	public static final String KEY_PUBDATE = "pubdate";
	public static final String KEY_AUTHOR = "author";
	public static final String KEY_FAVORITE = "favorite";
	public static final String KEY_MARKREAD = "markread";
	public static final String KEY_MARKPRESSED = "markpressed"; // to mark the
																// background
																// gray in list
	public static final String KEY_LATESTINDEX = "latest_index";

	//-------------- Database column constant ENDs-----//
	
	private static final String tag = "DBAdapter";

	private static final String DATABASE_NAME = "Feed_Database";
	private static final int DATABASE_VERSION = 1;

	private static final String DBPATH = "/data/data/com.saaranga.wikitrackkannada/databases/";

	// Database tables- create commands
	private static final String DATABASE_CREATE_RECENTCHANGES = "create table recent_changes"
			+ " (_id integer PRIMARY KEY AUTOINCREMENT, "
			+ "title text, "
			+ "link text , "
			+ "summary text,"
			+ "pubdate text, "
			+ "author text,"
			+ "favorite integer, "
			+ "markread integer, "
			+ "markpressed integer, " + "latest_index integer);";
	private static final String DATABASE_CREATE_MY_CONTRIBUTIONS = "create table my_contributions"
			+ " (_id integer PRIMARY KEY AUTOINCREMENT, "
			+ "title text, "
			+ "link text ,"
			+ " summary text,"
			+ "pubdate text, "
			+ "author text,"
			+ "favorite integer, "
			+ "markread integer, "
			+ "markpressed integer," + "latest_index integer);";
	private static final String DATABASE_CREATE_WATCHLIST = "create table my_watchlist"
			+ " (_id integer PRIMARY KEY AUTOINCREMENT, "
			+ "title text,"
			+ " link text , "
			+ "summary text,"
			+ "pubdate text, "
			+ "author text,"
			+ "favorite integer,"
			+ " markread integer, "
			+ "markpressed integer, " + "latest_index integer);";

	private final Context context; // removed final , initial

	private DatabaseHelper DBHelper = new DatabaseHelper(null);

	public SQLiteDatabase db;

	/*
	 * public FeedDBAdapter() {
	 * 
	 * }
	 */
	public FeedDBAdapter(Context ctx) {
		this.context = ctx;
		DBHelper = new DatabaseHelper(context);
	}

	private static class DatabaseHelper extends SQLiteOpenHelper {
		DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			//Log.i(tag, "Entered create Database method ");

			try {
				db.execSQL(DATABASE_CREATE_RECENTCHANGES);
				db.execSQL(DATABASE_CREATE_MY_CONTRIBUTIONS);
				db.execSQL(DATABASE_CREATE_WATCHLIST);

				Log.d(tag, "Three database tables are created");

			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w(tag, "upgrading database from version " + oldVersion + "to"
					+ newVersion + ", which will destroy all old data");
			db.execSQL("DROP TABLE IF EXISTS feed_items");

			onCreate(db);
		}
	}

	/*
	 * This method belongs to class FeedDBAdapter & is used to open database
	 */
	public FeedDBAdapter open() throws SQLException {
		//Log.i(tag, "Database is opened");
		db = DBHelper.getWritableDatabase();
		return this;
	}

	/*
	 * --closes the database--
	 */

	public void close() {
		//Log.i(tag, "Database is closed");
		DBHelper.close();
	}

	/**
	 * Check if the database already exist to avoid re-copying the file each
	 * time you open the application.
	 * 
	 * @return true if it exists, false if it doesn't
	 */
	public boolean checkDataBase() {

		SQLiteDatabase checkDB = null;

		try {
			String myPath = DBPATH + DATABASE_NAME;
			checkDB = SQLiteDatabase.openDatabase(myPath, null,
					SQLiteDatabase.OPEN_READONLY);

		} catch (SQLiteException e) {

			// database does't exist yet.
			//Log.i("DB: checkDatabase", "DB doest exist ");
		}

		if (checkDB != null) {

			checkDB.close();

		}

		return checkDB != null ? true : false;
	}

	/*
	 * Method is used to enter a contact in to the database
	 */
	public long insertFeedItem(String database_table, String title,
			String link, String description, String pubdate, String author,
			int index) {
		//Log.i(tag, "Item insert begins ");
		ContentValues initialValues = new ContentValues();

		initialValues.put(KEY_TITLE, title);
		initialValues.put(KEY_LINK, link);
		initialValues.put(KEY_SUMMARY, description);
		initialValues.put(KEY_PUBDATE, pubdate);
		initialValues.put(KEY_AUTHOR, author);
		initialValues.put(KEY_FAVORITE, 0);
		initialValues.put(KEY_MARKREAD, 0);

		initialValues.put(KEY_MARKPRESSED, 0);
		initialValues.put(KEY_LATESTINDEX, index);
		// initialValues.put(KEY_FEEDTYPE, feedType);
		return db.insert(database_table, null, initialValues);
	}

	/*
	 * Retrives all the items
	 */

	public Cursor getFeedItems(String database_table) {

		//Log.i(tag, "getAllFeedItems method entered");
		Cursor mnCursor = db.query(database_table, new String[] { KEY_ROWID,
				KEY_TITLE, KEY_LINK, KEY_SUMMARY, KEY_AUTHOR, KEY_PUBDATE,
				KEY_FAVORITE, KEY_MARKREAD, KEY_MARKPRESSED, KEY_LATESTINDEX },
				null, null, null, null, KEY_LATESTINDEX + " ASC");
		;

		return mnCursor;
	}

	/*
	 * retrieves a particular feed item
	 * 
	 * 
	 * 
	 * public Cursor getfeedItem(long rowId) throws SQLException { Cursor
	 * mCursor = db.query(true, database_table, new String[]
	 * {KEY_ROWID,KEY_TITLE, KEY_LINK, KEY_SUMMARY, KEY_PUBDATE}, KEY_ROWID,
	 * null, null, null, null, null); if(mCursor != null) {
	 * mCursor.moveToFirst(); } return mCursor; }
	 */
	/*
	 * update a contact
	 */

	public boolean updatefeed(long rowId, String title, String link,
			String description, String pubdate, String database_table) {
		ContentValues args = new ContentValues();
		args.put(KEY_TITLE, title);
		args.put(KEY_LINK, link);
		args.put(KEY_SUMMARY, description);
		args.put(KEY_PUBDATE, pubdate);
		// args.put (KEY_CATEGORY, category);
		return db.update(database_table, args, KEY_ROWID + "=" + rowId, null) > 0;
	}

	public void UpgradeDatabase() {
		DBHelper.onUpgrade(db, 1, 2);

	}

	// get feed methods

	private ArrayList<String> titleList;
	private ArrayList<String> authorList;
	private ArrayList<String> pubDateList;
	private ArrayList<String> linkList;
	private ArrayList<String> summaryList;

	/**
	 * 
	 * @param feedType
	 * @return
	 */

	public ArrayList<String> getTitleList(String table_name) {

		//Log.i(tag, "getFeedTitle is called ");
		open();
		Cursor cur = getFeedItems(table_name);
		titleList = new ArrayList<String>();
		int titlecolumn = cur.getColumnIndex(KEY_TITLE);
		try {
			if (cur != null) {
				if (cur.moveToFirst()) {
					//Log.i(tag, "cursor is in first row");
					do {

						String title = cur.getString(titlecolumn);
						// ////Log.i(tag, "Title read is : "+title);
						titleList.add(title);
					} while (cur.moveToNext());
				}
			}
			cur.close();
		} catch (Exception e) {

			Log.e(tag, "Error in retreiving data");
		} finally {
			// if(db!=null)
			// db.execSQL("DELETE FROM "+ DATABASE_TABLE);
			close();
		}
		return titleList;

	}

	public ArrayList<String> getAuthorList(String table_name) {
		//Log.i(tag, "getAuthorList is called ");
		open();
		Cursor cur = getFeedItems(table_name);
		authorList = new ArrayList<String>();
		int titlecolumn = cur.getColumnIndex(KEY_AUTHOR);
		try {
			if (cur != null) {
				if (cur.moveToFirst()) {
					do {
						String title = cur.getString(titlecolumn);
						// ////Log.i(tag, "Title read is : "+title);
						authorList.add(title);
					} while (cur.moveToNext());
				}
			}
			cur.close();
			//Log.i(tag, "cursor closed");
		} catch (Exception e) {

			Log.e(tag, "Error in retreiving data");
		} finally {
			// if(db!=null)
			// db.execSQL("DELETE FROM "+ DATABASE_TABLE);
			close();
		}
		return authorList;

	}

	public ArrayList<String> getPubDateList(String table_name) {

		//Log.i(tag, "getPubdate  is called ");
		open();
		Cursor cur = getFeedItems(table_name);
		pubDateList = new ArrayList<String>();
		int titlecolumn = cur.getColumnIndex(KEY_PUBDATE);
		try {
			if (cur != null) {
				if (cur.moveToFirst()) {
					do {
						String title = cur.getString(titlecolumn);
						// ////Log.i(tag, "Title read is : "+title);
						pubDateList.add(title);
					} while (cur.moveToNext());
				}
			}
			cur.close();
			;
		} catch (Exception e) {

			Log.e(tag, "Error in retreiving pubdate data");
		} finally {
			// if(db!=null)
			// db.execSQL("DELETE FROM "+ DATABASE_TABLE);
			close();
		}
		return pubDateList;
	}

	public ArrayList<String> getLinkList(String table_name) {

		//Log.i(tag, "getlink list is called ");
		open();
		Cursor cur = getFeedItems(table_name);
		//Log.i(tag, "Items in cursor: ");
		linkList = new ArrayList<String>();
		int titlecolumn = cur.getColumnIndex(KEY_LINK);
		try {
			if (cur != null) {
				//Log.i(tag, "cursor not empty");
				if (cur.moveToFirst()) {
					//Log.i(tag, "cursor is in first row");
					do {

						//Log.i(tag, "entered do while");

						String title = cur.getString(titlecolumn);
						// ////Log.i(tag, "Title read is : "+title);
						linkList.add(title);
						//Log.i(tag, "Feed list size is : " + linkList.size());
					} while (cur.moveToNext());
					//Log.i(tag, "came out of do while");
				}
				//Log.i(tag, "cursor is not in first place");
			}
			cur.close();
			//Log.i(tag, "cursor closed");
		} catch (Exception e) {

			Log.e(tag, "Error in retreiving data");
		} finally {
			// if(db!=null)
			// db.execSQL("DELETE FROM "+ DATABASE_TABLE);
			close();
		}
		return linkList;
	}

	public ArrayList<String> getSummaryList(String table_name) {
		//Log.i(tag, "getsummary is called ");
		open();
		Cursor cur = getFeedItems(table_name);
		//Log.i(tag, "Items in cursor: ");
		summaryList = new ArrayList<String>();
		int titlecolumn = cur.getColumnIndex(KEY_SUMMARY);
		try {
			if (cur != null) {
				//Log.i(tag, "cursor not empty");
				if (cur.moveToFirst()) {
					//Log.i(tag, "cursor is in first row");
					do {

						//Log.i(tag, "entered do while");

						String title = cur.getString(titlecolumn);
						// ////Log.i(tag, "Title read is : "+title);
						summaryList.add(title);
						////Log.i(tag, "Feed list size is : " +
						// summaryList.size());
					} while (cur.moveToNext());
					//Log.i(tag, "came out of do while");
				}
				//Log.i(tag, "cursor is not in first place");
			}
			cur.close();
			//Log.i(tag, "cursor closed");
		} catch (Exception e) {

			Log.e(tag, "Error in retreiving data");
		} finally {
			// if(db!=null)
			// db.execSQL("DELETE FROM "+ DATABASE_TABLE);
			close();
		}
		cur.close();
		return summaryList;
	}

	public ArrayList<Integer> getFavoriteList(String table_name) {
		//Log.i(tag, "getfavorite is called ");
		open();
		Cursor cur = getFeedItems(table_name);
		//Log.i(tag, "Items in cursor: ");
		ArrayList<Integer> favoriteList = new ArrayList<Integer>();
		int titlecolumn = cur.getColumnIndex(KEY_FAVORITE);
		try {
			if (cur != null) {
				//Log.i(tag, "cursor not empty");
				if (cur.moveToFirst()) {
					//Log.i(tag, "cursor is in first row");
					do {

						//Log.i(tag, "entered do while");

						int title = cur.getInt(titlecolumn);
						// ////Log.i(tag, "Title read is : "+title);
						favoriteList.add(title);
						////Log.i(tag, "Feed list size is : " +
						// favoriteList.size());
					} while (cur.moveToNext());
					//Log.i(tag, "came out of do while");
				}
				//Log.i(tag, "cursor is not in first place");
			}
			cur.close();
			//Log.i(tag, "cursor closed");
		} catch (Exception e) {

			Log.e(tag, "Error in retreiving favorite data");
		} finally {
			// if(db!=null)
			// db.execSQL("DELETE FROM "+ DATABASE_TABLE);
			close();
		}
		cur.close();
		return favoriteList;
	}

	public ArrayList<Integer> getMarkReadList(String table_name) {
		//Log.i(tag, "getfavorite is called ");
		open();
		Cursor cur = getFeedItems(table_name);
		//Log.i(tag, "Items in cursor: ");
		ArrayList<Integer> markReadList = new ArrayList<Integer>();
		int titlecolumn = cur.getColumnIndex(KEY_MARKREAD);
		try {
			if (cur != null) {
				//Log.i(tag, "cursor not empty");
				if (cur.moveToFirst()) {
					//Log.i(tag, "cursor is in first row");
					do {

						//Log.i(tag, "entered do while");

						int title = cur.getInt(titlecolumn);
						// ////Log.i(tag, "Title read is : "+title);
						markReadList.add(title);
						////Log.i(tag, "Feed list size is : " +
						// markReadList.size());
					} while (cur.moveToNext());
					//Log.i(tag, "came out of do while");
				}
				//Log.i(tag, "cursor is not in first place");
			}
			cur.close();
			//Log.i(tag, "cursor closed");
		} catch (Exception e) {

			Log.e(tag, "Error in retreiving favorite data");
		} finally {
			// if(db!=null)
			// db.execSQL("DELETE FROM "+ DATABASE_TABLE);
			close();
		}
		cur.close();
		return markReadList;
	}

	public ArrayList<Integer> getMarkPressedList(String table_name) {
		//Log.i(tag, "getfavorite is called ");
		open();
		Cursor cur = getFeedItems(table_name);
		//Log.i(tag, "Items in cursor: ");
		ArrayList<Integer> markPresseddList = new ArrayList<Integer>();
		int titlecolumn = cur.getColumnIndex(KEY_MARKPRESSED);
		try {
			if (cur != null) {
				//Log.i(tag, "cursor not empty");
				if (cur.moveToFirst()) {
					//Log.i(tag, "cursor is in first row");
					do {

						//Log.i(tag, "entered do while");

						int title = cur.getInt(titlecolumn);
						// ////Log.i(tag, "Title read is : "+title);
						markPresseddList.add(title);
					} while (cur.moveToNext());
					//Log.i(tag, "came out of do while");
				}
				//Log.i(tag, "cursor is not in first place");
			}
			cur.close();
			//Log.i(tag, "cursor closed");
		} catch (Exception e) {

			Log.e(tag, "Error in retreiving favorite data");
		} finally {
			// if(db!=null)
			// db.execSQL("DELETE FROM "+ DATABASE_TABLE);
			close();
		}
		cur.close();
		return markPresseddList;
	}

	// *********************************getters for individual
	// items********************************//
	
	public String getTitle(String table_name, int index) {
		//Log.i(tag, "getAuthorList is called ");
		String author = "null";
		open();
		Cursor cur = db.rawQuery("SELECT " + KEY_TITLE+ " FROM " + table_name
				+ " WHERE " + KEY_LATESTINDEX + " = " + index, null);
		int titlecolumn = cur.getColumnIndex(KEY_TITLE);
		try {
			if (cur != null) {
				if (cur.moveToFirst()) {
					do {
						author = cur.getString(titlecolumn);
					} while (cur.moveToNext());
				}
			}
			cur.close();
			//Log.i(tag, "cursor closed");
		} catch (Exception e) {

			Log.e(tag, "Error in retreiving data");
		} finally {
			close();
		}
		return author;

	}
	
	
	public String getAuthor(String table_name, int index) {
		//Log.i(tag, "getAuthorList is called ");
		String author = "null";
		open();
		Cursor cur = db.rawQuery("SELECT " + KEY_AUTHOR + " FROM " + table_name
				+ " WHERE " + KEY_LATESTINDEX + " = " + index, null);
		int titlecolumn = cur.getColumnIndex(KEY_AUTHOR);
		try {
			if (cur != null) {
				if (cur.moveToFirst()) {
					do {
						author = cur.getString(titlecolumn);
					} while (cur.moveToNext());
				}
			}
			cur.close();
			//Log.i(tag, "cursor closed");
		} catch (Exception e) {

			Log.e(tag, "Error in retreiving data");
		} finally {
			// if(db!=null)
			// db.execSQL("DELETE FROM "+ DATABASE_TABLE);
			close();
		}
		return author;

	}

	public String getPubDate(String table_name, int index) {
		//Log.i(tag, "getAuthorList is called ");
		String pubDate = "null";
		open();
		Cursor cur = db.rawQuery("SELECT " + KEY_PUBDATE + " FROM "
				+ table_name + " WHERE " + KEY_LATESTINDEX + " = " + index,
				null);
		int titlecolumn = cur.getColumnIndex(KEY_PUBDATE);
		try {
			if (cur != null) {
				if (cur.moveToFirst()) {
					do {
						pubDate = cur.getString(titlecolumn);
					} while (cur.moveToNext());
				}
			}
			cur.close();
			//Log.i(tag, "cursor closed");
		} catch (Exception e) {

			Log.e(tag, "Error in retreiving data");
		} finally {
			// if(db!=null)
			// db.execSQL("DELETE FROM "+ DATABASE_TABLE);
			close();
		}
		return pubDate;
	}

	public String getLink(String table_name, int index) {
		//Log.i(tag, "getAuthorList is called ");
		String link = "null";
		open();
		Cursor cur = db.rawQuery("SELECT " + KEY_LINK + " FROM " + table_name
				+ " WHERE " + KEY_LATESTINDEX + " = " + index, null);
		int titlecolumn = cur.getColumnIndex(KEY_LINK);
		try {
			if (cur != null) {
				if (cur.moveToFirst()) {
					do {
						link = cur.getString(titlecolumn);
					} while (cur.moveToNext());
				}
			}
			cur.close();
			//Log.i(tag, "cursor closed");
		} catch (Exception e) {

			Log.e(tag, "Error in retreiving data");
		} finally {
			// if(db!=null)
			// db.execSQL("DELETE FROM "+ DATABASE_TABLE);
			close();
		}
		return link;
	}

	public String getDescription(String table_name, int index) {
		//Log.i(tag, "getAuthorList is called ");
		String description = "null";
		open();
		Cursor cur = db.rawQuery("SELECT " + KEY_SUMMARY + " FROM "
				+ table_name + " WHERE " + KEY_LATESTINDEX + " = " + index,
				null);
		int titlecolumn = cur.getColumnIndex(KEY_SUMMARY);
		try {
			if (cur != null) {
				if (cur.moveToFirst()) {
					do {
						description = cur.getString(titlecolumn);
					} while (cur.moveToNext());
				}
			}
			cur.close();
			//Log.i(tag, "cursor closed");
		} catch (Exception e) {

			Log.e(tag, "Error in retreiving data");
		} finally {
			// if(db!=null)
			// db.execSQL("DELETE FROM "+ DATABASE_TABLE);
			close();
		}
		return description;
	}

	public int getReadFlag(String table_name, int index) {
		//Log.i(tag, "getAuthorList is called ");
		int readFlag = 0;
		open();
		Cursor cur = db.rawQuery("SELECT " + KEY_MARKREAD + " FROM "
				+ table_name + " WHERE " + KEY_LATESTINDEX + " = " + index,
				null);
		int titlecolumn = cur.getColumnIndex(KEY_MARKREAD);
		try {
			if (cur != null) {
				if (cur.moveToFirst()) {
					do {
						readFlag = cur.getInt(titlecolumn);
					} while (cur.moveToNext());
				}
			}
			cur.close();
			//Log.i(tag, "cursor closed");
		} catch (Exception e) {

			Log.e(tag, "Error in retreiving data");
		} finally {
			// if(db!=null)
			// db.execSQL("DELETE FROM "+ DATABASE_TABLE);
			close();
		}
		return readFlag;
	}

	public int getFavoriteFlag(String table_name, int index) {
		//Log.i(tag, "getAuthorList is called ");
		int readFlag = 0;
		open();
		Cursor cur = db.rawQuery("SELECT " + KEY_FAVORITE + " FROM "
				+ table_name + " WHERE " + KEY_LATESTINDEX + " = " + index,
				null);
		int titlecolumn = cur.getColumnIndex(KEY_FAVORITE);
		try {
			if (cur != null) {
				if (cur.moveToFirst()) {
					do {
						readFlag = cur.getInt(titlecolumn);
					} while (cur.moveToNext());
				}
			}
			cur.close();
			//Log.i(tag, "cursor closed");
		} catch (Exception e) {

			Log.e(tag, "Error in retreiving data");
		} finally {
			// if(db!=null)
			// db.execSQL("DELETE FROM "+ DATABASE_TABLE);
			close();
		}
		return readFlag;
	}

	public int getPressedFlag(String table_name, int index) {
		//Log.i(tag, "getAuthorList is called ");
		int readFlag = 0;
		open();
		Cursor cur = db.rawQuery("SELECT " + KEY_MARKPRESSED + " FROM "
				+ table_name + " WHERE " + KEY_LATESTINDEX + " = " + index,
				null);
		int titlecolumn = cur.getColumnIndex(KEY_MARKPRESSED);
		try {
			if (cur != null) {
				if (cur.moveToFirst()) {
					do {
						readFlag = cur.getInt(titlecolumn);
					} while (cur.moveToNext());
				}
			}
			cur.close();
			//Log.i(tag, "cursor closed");
		} catch (Exception e) {

			Log.e(tag, "Error in retreiving data");
		} finally {
			// if(db!=null)
			// db.execSQL("DELETE FROM "+ DATABASE_TABLE);
			close();
		}
		return readFlag;
	}
	
	
	public int getFavoriteCount(String table_name) {
		//Log.i(tag, "getAuthorList is called ");
		int count = 0;
		open();
		Cursor cur = db.rawQuery("SELECT * FROM " + table_name
				+ " WHERE " + KEY_FAVORITE+ " = " + "1", null);
		
		try {
			if (cur != null) {
				count = cur.getCount();
			}
			cur.close();
			//Log.i(tag, "cursor closed");
		} catch (Exception e) {

			Log.e(tag, "Error in retreiving data");
		} finally {
			close();
		}
		return count;

	}

	public boolean setPressed(String tableName, int position) {

		open();
		try {
			db.execSQL("UPDATE " + tableName + " SET markpressed=1 WHERE latest_index="
					+ position);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			close();
		}

		return true;
	}
	
	public boolean markRead(String tableName, int position) {

	
		open();
		try {
			db.execSQL("UPDATE " + tableName + " SET markread=1 WHERE latest_index ="
					+ position);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			close();
		}

		return true;
	}
	
	public boolean markFavorite(String tableName, int position) {

		open();
		try {
			db.execSQL("UPDATE " + tableName + " SET favorite =1 WHERE latest_index="
					+ position);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			close();
		}

		return true;
	}
	
	public boolean markUnFavorite(String tableName, int position) {

		open();
		try {
			db.execSQL("UPDATE " + tableName + " SET favorite =0 WHERE latest_index="
					+ position);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			close();
		}

		return true;
	}
	

	// check if database table exists
	public boolean isTableExists(String table_name, boolean openDb) {
		open();
		Cursor cur = getFeedItems(table_name);
		if (cur != null) {
			if (cur.getCount() > 0) {
				close();
				return true;
			}
			cur.close();
		}
		close();

		return false;
	}

	/*
	 * This method deletes the database table before building new data base
	 */
	public void updateDatabaseTable(String database_table) {
		//Log.i(tag, "database being updated");
		open();
		if (db != null) {
			//Log.i(tag, "older database is present it is deleted");
			db.execSQL("DELETE FROM " + database_table);
		}
		close();
	}

	public boolean trimDatabaseToFeedItemLimit(String tableName) {
		mlog("trim called");
		int limit = Constants.getFeedLimit(tableName, context) - 2; // since the
																	// latest_index
																	// starts
																	// from 0
																	// and we
																	// are using
																	// > sign,
		// reduce two from count
		
		mlog("Trim limit: "+(limit+2));
		open();
		if (db != null && limit != -2) {
			try {
				db.execSQL("DELETE FROM " + tableName
						+ " WHERE latest_index > " + limit);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		close();
		return true;
	}

	// private void getTableName(String feedType) {
	// if(feedType.equals(Constants.RECENT_CHANGES))
	// }

	public boolean updateTheIndexOfRows(String table, int count) {
		boolean isUpdated = true;

		open();
		try {
			db.execSQL("UPDATE " + table + " SET latest_index = latest_index+"
					+ count);
		} catch (SQLException e) {
			isUpdated = false;
			e.printStackTrace();
		}

		close();

		return isUpdated;

	}

	private void mlog(String msg) {
		Log.d(DEBUG_TAG, msg);
	}

}

// FeedType and Database Table names are same for this app
