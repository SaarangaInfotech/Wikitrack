package com.saaranga.wikikannada;

import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.saaranga.wikitrack.utilities.Constants;

/**
 * activity class - <br>
 * PURPOSE: If user name and passwords are not stored, displays the form to
 * enter the username and password <br>
 * 
 * It actually calls the class {@link LoginHandler} to login to the user account
 * and post request to get my watchlist
 * 
 * TODO option for advanced query settings can be provided in future releases
 * 
 * @author supreeth
 * @version 1.0 30-05-2012
 * 
 *          Copyright Saaranga Infotech
 */
public class ActivityMyWatchList extends Activity implements OnClickListener {

	private static final String tag = "My watchlist activity";

	private EditText usernameBox, passwordBox;
	private String username, password;

	private SharedPreferences settings;
	private CheckBox savepswd;

	private String DEBUG_TAG = "Activity my Watchlist";
	private ImageView home;
	private Button defaultwl;
	private TextView tv1;
	private TextView tv2;
	private TextView tv3;
	private TextView tv4;
	private TextView tv5;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i(tag, "here 0");
		setContentView(R.layout.activity_my_watchlist);

		setUpUI();

	}

	/**
	 * create the views for the activity
	 */
	private void setUpUI() {
		home = (ImageView) findViewById(R.id.wl_home);
		usernameBox = (EditText) findViewById(R.id.my_watchlist_edittext_uname);
		passwordBox = (EditText) findViewById(R.id.my_watchlist_edittext_pswd);
		savepswd = (CheckBox) findViewById(R.id.wl_save_pswd);
		defaultwl = (Button) findViewById(R.id.my_watchlist_btn_default);
		tv1 = (TextView) findViewById(R.id.wltv1);
		tv2 = (TextView) findViewById(R.id.wltv2);
		tv3 = (TextView) findViewById(R.id.wltv3);
		tv4 = (TextView) findViewById(R.id.wltv4);
		tv5 = (TextView) findViewById(R.id.wltv5);

		settings = getSharedPreferences(Constants.USERNAME_PREFS, MODE_PRIVATE);

		if (settings.contains(Constants.LOGIN_PASSWORD)) {
			// username passwords are stored - show the username in the username
			// field and mask password
			Log.i(tag, "password present");
			usernameBox.setText(settings.getString(Constants.LOGIN_USERNAME,
					"none"));
			passwordBox.setText("******");
			// hide the save password option
			savepswd.setVisibility(View.GONE);
		}

		Log.i(tag, "here 1");

		home.setOnClickListener(this);
		defaultwl.setOnClickListener(this);

		Typeface tf = Constants.getTypeface(this);

		if (tf != null) {
			usernameBox.setTypeface(tf);
			passwordBox.setTypeface(tf);
			savepswd.setTypeface(tf);
			defaultwl.setTypeface(tf);
			tv1.setTypeface(tf);
			tv2.setTypeface(tf);
			tv3.setTypeface(tf);
			tv4.setTypeface(tf);
			tv5.setTypeface(tf);

		}
	}

	@Override
	public void onClick(View v) {
		Log.i(tag, "button clicked");
		// close the keyboard if open
		closeSoftKeyBoard();

		switch (v.getId()) {

		case R.id.wl_home:
			Intent home = new Intent(ActivityMyWatchList.this,
					ActivityShowCase.class);
			this.startActivity(home);
			finish();
			break;

		case R.id.my_watchlist_btn_default:

			// get username and password
			getUsernameAndPasswd();

			// check username and password for validity - basic authentication
			boolean isValid = basicAuthentication();
			mlog("isuname and pswd valid? " + isValid);

			if (!isValid) {
				Toast.makeText(this,
						getString(R.string.en_enter_valid_uname_pswd),
						Toast.LENGTH_LONG).show();
			} else {
				// if save is set, save username and password
				if (savepswd.isChecked())
					saveUsernameAndPswd();

				// run the background task
				new getMyWatchlistTask(username, password).execute("");
			}

			break;

		}
	}

	/**
	 * Close the softkeyboard if open.
	 */
	private void closeSoftKeyBoard() {
		InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		inputMethodManager.hideSoftInputFromWindow(getCurrentFocus()
				.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
	}

	/**
	 * Save the username and password to the sharedpreferences
	 * 
	 */
	private void saveUsernameAndPswd() {
		try {

			// save password
			Log.i(tag, "password saved");
			SharedPreferences.Editor editor = settings.edit();

			editor.putString(Constants.LOGIN_PASSWORD, password);
			editor.putString(Constants.LOGIN_USERNAME, username);
			editor.commit();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Do a basic validation of the text entered. Since we use specialized
	 * editText views, no need of authenticating further
	 * 
	 * @return true if the text entered is authentic
	 */
	private boolean basicAuthentication() {
		if (username.equals("") && password.equals(""))
			return false;

		if ((username.equals("none") && password.equals("*****")))
			return false;
		return true;
	}

	/**
	 * sets the username and passeord to the variables if present in shared
	 * preferences. If not present sets username to "none" and password "****"
	 * 
	 */
	private void getUsernameAndPasswd() {
		// get the username and password from preferences if saved else from the
		// edit boxes

		if (settings.contains(Constants.LOGIN_PASSWORD)) {
			// password is present in the preferences
			username = settings.getString(Constants.LOGIN_USERNAME, "none");
			password = settings.getString(Constants.LOGIN_PASSWORD, "****");
		} else {
			// not present get from the edit boxes
			username = usernameBox.getText().toString();
			password = passwordBox.getText().toString();
		}
	}

	/**
	 * 
	 * Asynctask to get the mywatchlist data and store it in database Using the
	 * user credentials, LoginHandler class is used to login to user account,
	 * the String obtained is parsed and returned by Login Handler
	 * 
	 * @author supreeth
	 * 
	 */
	private class getMyWatchlistTask extends AsyncTask<String, Void, Void> {

		ProgressDialog dialog;
		private RSSFeed rssFeed;
		private LoginHandler loginHandler;
		private GetRssFeedClass getRssFeedClass;
		private FeedDBAdapter feedDBAdapter = new FeedDBAdapter(
				ActivityMyWatchList.this);
		private boolean isError = false,isFeedEmpty = false;
		private String usernameString, passwordString;

		List<RSSItem> _itemList;
		private String errorMessage;

		public getMyWatchlistTask(String usernameString, String passwordString) {
			this.usernameString = usernameString;
			this.passwordString = passwordString;
		}

		@Override
		protected void onPreExecute() {

			dialog = new ProgressDialog(ActivityMyWatchList.this);
			dialog.setCancelable(false);
			dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "cancel",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					});
			dialog.setIcon(android.R.drawable.ic_dialog_info);
			dialog.setTitle("Fetching feed");
			dialog.setMessage("Loading");
			dialog.show();
		}

		@Override
		protected Void doInBackground(String... params) {

			getRssFeedClass = new GetRssFeedClass();
			// get the LoginHandler object
			loginHandler = new LoginHandler();

			// feed string can be null if there is error in login
			String feedString = null;
			try {
				feedString = loginHandler.loginToWikiAccount(usernameString,
						passwordString);
			} catch (Exception e) {
				e.printStackTrace();
			}

			if (LoginHandler.isError || feedString == null) {
				mlog("isError: " + isError + " feedString : " + feedString);

				// error in login
				isError = true;
				errorMessage = loginHandler.getErrorMessage();
			} else {
				// parse the feed data
				rssFeed = getRssFeedClass.parseFeed(feedString);

				if(rssFeed!=null) {
					Log.d(tag, "RssFeed is not null");
					_itemList = rssFeed.getAllItems();
				} else {
					Log.d(tag, "RssFeed is null");
				}
				
				if (_itemList.size() == 0) {
					// empty feed
					isFeedEmpty = true;
				} else {
					// insert to database
					insertItemsToDatabse();
				}

			}
			// release objects
			rssFeed = null;
			_itemList.clear();
			return null;
		}

		/**
		 * Insert thefeed items to Database
		 */
		private void insertItemsToDatabse() {
			feedDBAdapter.updateDatabaseTable(Constants.MYWATCHLIST);
			feedDBAdapter.open();
			for (int i = 0; i < _itemList.size(); i++) {
				feedDBAdapter.insertFeedItem(Constants.MYWATCHLIST, _itemList
						.get(i).getTitle(), _itemList.get(i).getLink(),
						_itemList.get(i).getSummary(), _itemList.get(i)
								.getUpdated(), _itemList.get(i).getAuthor(), i);
			}
			feedDBAdapter.close();

			// trim to the database length - always be true
			feedDBAdapter.trimDatabaseToFeedItemLimit(Constants.MYWATCHLIST);
		}

		@Override
		protected void onPostExecute(Void result) {
			dialog.dismiss();
			if (isError) {
				handleError();
			} else if (isFeedEmpty) {
				Toast.makeText(ActivityMyWatchList.this,
						"No items in the feed", Toast.LENGTH_LONG).show();

			} else {
				Intent recentChangesDefaultFeedIntent = new Intent(
						ActivityMyWatchList.this, ActivityLists.class);
				recentChangesDefaultFeedIntent.putExtra(Constants.FEEDTYPE,
						Constants.MYWATCHLIST);
				ActivityMyWatchList.this
						.startActivity(recentChangesDefaultFeedIntent);
				ActivityMyWatchList.this.finish();
			}
		}

		/**
		 * Handle error in fetching my watchlist feed
		 */
		private void handleError() {
			if (!isNetworkAvailable()) {

				Toast.makeText(ActivityMyWatchList.this,
						"Unable to connect. Check internet connectivity",
						Toast.LENGTH_LONG).show();

			} else {
				// network is available but the username is not valid - or feed
				// returned is empty (occurs in case of my watchlist - if the
				// number
				// of items is zero
				Toast.makeText(ActivityMyWatchList.this,
						"Error: " + errorMessage, Toast.LENGTH_LONG).show();
			}

		}

		/**
		 * check if network is available
		 * 
		 * @return true is available
		 */
		private boolean isNetworkAvailable() {
			ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo activeNetworkInfo = connectivityManager
					.getActiveNetworkInfo();
			return activeNetworkInfo != null;
		}

	}

	private void mlog(String msg) {
		Log.d(DEBUG_TAG, msg);
	}

}
