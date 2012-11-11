package com.saaranga.wikikannada;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.SQLException;
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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.saaranga.wikitrack.utilities.Constants;

/**
 * activity class - <br>
 * PURPOSE: If no user name is present in the preference, displays the form to
 * enter the username <br>
 * TODO option for advanced query settings can be provided in future releases
 * 
 * @author supreeth
 * @version 1.0 30-05-2012
 * 
 *          Copyright Saaranga Infotech
 */
public class ActivityMyContributions extends Activity implements
		OnClickListener {

	private static final String tag = "Activity my contributions";
	private EditText usernameBox;

	private SharedPreferences settings;

	private String usernameString = null;
	private Button defaultMC;
	private String DEBUG_TAG = "Activity my contributions";
	private TextView tv5;
	private TextView tv4;
	private TextView tv3;
	private TextView tv2;
	private TextView tv1;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_contributions);

		setUpUI();

	}

	/**
	 * create the views for the activity
	 */
	private void setUpUI() {
		usernameBox = (EditText) findViewById(R.id.username_box);
		defaultMC = (Button) findViewById(R.id.activity_my_contributions_btn_default);
		tv1 = (TextView) findViewById(R.id.mc_tv1);
		tv2 = (TextView) findViewById(R.id.mc_tv2);
		tv3 = (TextView) findViewById(R.id.mc_tv3);
		tv4 = (TextView) findViewById(R.id.mc_tv4);
		tv5 = (TextView) findViewById(R.id.mc_tv5);

		Typeface tf = Constants.getTypeface(this);
		if (tf != null) {
			usernameBox.setTypeface(tf);
			defaultMC.setTypeface(tf);
			tv1.setTypeface(tf);
			tv2.setTypeface(tf);
			tv3.setTypeface(tf);
			tv4.setTypeface(tf);
			tv5.setTypeface(tf);
		}

		defaultMC.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {

		// close the soft keyboard if open
		InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		inputMethodManager.hideSoftInputFromWindow(getCurrentFocus()
				.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		switch (v.getId()) {
		case R.id.activity_my_contributions_btn_default:

			usernameString = usernameBox.getText().toString();
			setUsername(usernameString);
			if (usernameString.equals("*\\W")) {
				mlog("username caught.username:" + usernameString + "|");
			} else {
				mlog("username escaped. username:" + usernameString + "|");
			}

			if (!usernameString.equals("")) {
				new getMyContributionsFeedTask(usernameString).execute("");
			} else {
				Toast.makeText(this, "Please Enter valid username",
						Toast.LENGTH_LONG).show();
			}

			break;
		}
	}

	private void setUsername(String uname) {
		usernameString = uname;

		// set the username in preferences
		settings = getSharedPreferences(Constants.USERNAME_PREFS, MODE_PRIVATE);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString(Constants.MYCONTRIBUTIONS_USERNAME, usernameString);
		editor.commit();
	}

	/**
	 * Async task that fetches the feed for my contributions in the background
	 * 
	 * @author supreeth
	 * 
	 */
	private class getMyContributionsFeedTask extends
			AsyncTask<String, Void, Void> {

		private ProgressDialog dialog;
		private RSSFeed rssFeed = new RSSFeed();
		private GetRssFeedClass getRssFeedClass;
		private FeedDBAdapter feedDBAdapter = new FeedDBAdapter(
				ActivityMyContributions.this);
		private boolean isError = false;
		private String usernameString;

		private List<RSSItem> _itemList;
		private boolean isFeedNull;
		private boolean isEmptyFeed;

		public getMyContributionsFeedTask(String usernameString) {
			this.usernameString = usernameString;
		}

		/**
		 * A progress dialog is displayed till the feed is fetched from internet
		 * 
		 */
		@Override
		protected void onPreExecute() {
			dialog = new ProgressDialog(ActivityMyContributions.this);
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
			try {
				getRssFeedClass = new GetRssFeedClass();

				// prepare the rss feed url to fetch my contributions feed
				StringBuilder stringBuilder = new StringBuilder();
				stringBuilder
						.append("http://kn.wikipedia.org/w/api.php?action=feedcontributions&user=");
				stringBuilder.append(this.usernameString);
				stringBuilder.append("&feedformat=atom");

				rssFeed = getRssFeedClass.getFeed(stringBuilder.toString());
				_itemList = new ArrayList<RSSItem>();

				if (rssFeed != null) { // get the feed items to a list item
					Log.d(tag, "RssFeed is not null");
					_itemList = rssFeed.getAllItems();

				} else {
					Log.d(tag, "RssFeed is null");
				}

				mlog("count: " + rssFeed.getAllItems().size());
			} catch (Exception e1) {
				e1.printStackTrace();
				isError = true;
			}

			// Handle the errors in fetching the feed here

			if (rssFeed == null) {
				// rss feed is null check if the internet connection is not
				// there or the username is not valid
				mlog("rss feed is null");
				isFeedNull = true;

			} else {

				if (rssFeed.getAllItems().size() == 0) {
					isEmptyFeed = true; // feed is returned but no items present
										// - incase of invalid username
				}

				try {
					feedDBAdapter
							.updateDatabaseTable(Constants.MY_CONTRIBUTIONS);
					feedDBAdapter.open();
					for (int i = 0; i < _itemList.size(); i++) {
						feedDBAdapter.insertFeedItem(
								Constants.MY_CONTRIBUTIONS, _itemList.get(i)
										.getTitle(),
								_itemList.get(i).getLink(), _itemList.get(i)
										.getSummary(), _itemList.get(i)
										.getUpdated(), _itemList.get(i)
										.getAuthor(), i);
					}
					feedDBAdapter.close();

					// trim to the database length - always be true
					feedDBAdapter
							.trimDatabaseToFeedItemLimit(Constants.MY_CONTRIBUTIONS);
				} catch (SQLException e) {
					e.printStackTrace();
					isError = true;
				}
			}
			// release rssFeed item
			rssFeed = null;

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			if (isError) {
				Log.e(tag, "Error in fetching feed");
			}
			dialog.dismiss();

			if (isFeedNull || isEmptyFeed) {
				// if either the feed is null or url returned empty feed, handle
				// them
				handleNullFeed();

			} else {
				Intent recentChangesDefaultFeedIntent = new Intent(
						ActivityMyContributions.this, ActivityLists.class);
				recentChangesDefaultFeedIntent.putExtra(Constants.FEEDTYPE,
						Constants.MY_CONTRIBUTIONS);
				ActivityMyContributions.this
						.startActivity(recentChangesDefaultFeedIntent);
				ActivityMyContributions.this.finish();
			}

		}

		/**
		 * handle the null and empty feed cases
		 * 
		 */
		private void handleNullFeed() {

			if (!isNetworkAvailable() && isFeedNull) {

				Toast.makeText(ActivityMyContributions.this,
						"Unable to connect. Check internet connectivity",
						Toast.LENGTH_LONG).show();

			} else if (isEmptyFeed) {
				// network is available but the username is not valid - or feed
				// returned is empty (occurs in case of my watchlist - if the
				// number
				// of items is zero
				Toast.makeText(ActivityMyContributions.this,
						"It seems username doesn't exist or no feed returned",
						Toast.LENGTH_LONG).show();
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
