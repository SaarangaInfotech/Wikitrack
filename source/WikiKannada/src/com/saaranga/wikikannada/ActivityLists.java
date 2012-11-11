package com.saaranga.wikikannada;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.saaranga.wikitrack.utilities.Constants;

/**
 * activity class - <br>
 * PURPOSE: Displays list for recent changes feed, my contributions as well as
 * my watchlist feed <br>
 * 
 * Has options menu with the following items: *
 * <ul>
 * <li>Refresh feed</li>
 * <li>batch mode</li> TODO
 * <li>preferences</li>
 * <li>username page</li>
 * </ul>
 * 
 * @author supreeth
 * @version 1.0
 * 
 *          Copyright Saaranga Infotech
 */
public class ActivityLists extends Activity implements OnItemClickListener,
		OnClickListener {

	private final String DEBUG_TAG = "Lists Activity";

	private String feedType;
	private GridView listItemsGrid;
	private ProgressDialog dialog;

	private ImageView listview_home;

	private TextView menu;

	private TextView footer_title;

	private FeedDBAdapter dbAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_listview);

		feedType = getIntent().getExtras().getString(Constants.FEEDTYPE);
		listItemsGrid = (GridView) findViewById(R.id.listlayout_gridview);

		// declare the UI
		setUpUI();

		getAdapter();

	}

	/**
	 * load the adapter for the list in a seperate thread and display the
	 * progress bar till it completes
	 * 
	 */
	private void getAdapter() {
		final Handler handler = new Handler();

		dialog = ProgressDialog.show(this, getString(R.string.kn_pls_wait),
				getString(R.string.kn_loading));

		Thread thread = new Thread(new Runnable() {

			@Override
			public void run() {

				final BoxListAdapter adapter = new BoxListAdapter(
						ActivityLists.this, feedType);
				handler.post(new Runnable() {

					@Override
					public void run() {
						listItemsGrid.setAdapter(adapter);
						listItemsGrid
								.setOnItemClickListener(ActivityLists.this);
						dialog.dismiss();
					}
				});
			}
		});
		thread.start();
	}

	/**
	 * Create the views for the activity
	 * 
	 */
	private void setUpUI() {
		listview_home = (ImageView) findViewById(R.id.listview_home);
		menu = (TextView) findViewById(R.id.lists_menu);

		footer_title = (TextView) findViewById(R.id.footer_text);
		if (feedType.equals(Constants.RECENT_CHANGES)) {
			footer_title.setText(R.string.kn_recent_changes);
		} else if (feedType.equals(Constants.MY_CONTRIBUTIONS)) {
			footer_title.setText(R.string.kn_my_contributions);
		} else if (feedType.equals(Constants.MYWATCHLIST)) {
			footer_title.setText(R.string.kn_my_watchlist);
		}

		// set typeace
		Typeface tf = Constants.getTypeface(this);
		if (tf != null) {
			// font preference present
			mlog("tf is not null");
			// menu.setTypeface(tf);
			footer_title.setTypeface(tf);
		} else {
			mlog("tf is null");
			// dont set typeface
		}

		// set onclick listeners to buttons
		listview_home.setOnClickListener(this);
		menu.setOnClickListener(this);
	}

	@Override
	public void onItemClick(AdapterView<?> view, View arg1, int position,
			long arg3) {
		switch (view.getId()) {
		case R.id.listlayout_gridview:

			dbAdapter = new FeedDBAdapter(this);
			// set the pressed state of the item selected
			dbAdapter.setPressed(feedType, position);
			Log.d("test", "");

			Intent listItemIntent = new Intent(ActivityLists.this,
					ActivityArticleView.class);
			// put intent information : feedType, position of the clicked item
			listItemIntent.putExtra("FEEDTYPE", feedType);
			listItemIntent.putExtra("POSITION", position);

			this.startActivity(listItemIntent);
			break;
		}
	}

	/**
	 * create menu with the following items:
	 * <ul>
	 * <li>Refresh feed</li>
	 * <li>batch mode</li> TODO
	 * <li>preferences</li>
	 * <li>username page</li>
	 * </ul>
	 * 
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.listview_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.list_menu_refresh:
			// refresh the database
			refreshFeedDatabase();

			return true;

		case R.id.list_menu_batchmode:
			// TODO batch mode
			Toast.makeText(this, "coming soon", Toast.LENGTH_SHORT).show();
			return true;

		case R.id.list_menu_preferences:
			// start the preference activity
			Intent prefIntent = new Intent(this,
					ActivityApplicationPreferenceList.class);
			startActivityForResult(prefIntent, 1);
			return true;

		case R.id.list_menu_username:
			// This option is used by user to access the username page of my
			// contributions and username, password page for my watchlist
			if (feedType.equals(Constants.MY_CONTRIBUTIONS)) {
				Intent myContributionsIntent = new Intent(ActivityLists.this,
						ActivityMyContributions.class);
				startActivity(myContributionsIntent);
			} else if (feedType.equals(Constants.MYWATCHLIST)) {
				Intent mywatchListIntent = new Intent(ActivityLists.this,
						ActivityMyWatchList.class);
				startActivity(mywatchListIntent);
			}

			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/**
	 * Method to handle the refresh feed request It instantiates the database
	 * update method. recieves flags stating whether the database is updated or
	 * not - if the database is refreshed, the view is refreshed with new data
	 * it also checks the static flag isErrorInFetchingFeed after the calling
	 * function returns - and throws error toast message based on the network
	 * availability
	 * 
	 */
	private void refreshFeedDatabase() {
		final Handler handler2 = new Handler();
		dialog = ProgressDialog.show(ActivityLists.this, "Updating Database",
				"Please wait...");
		new Thread(new Runnable() {

			@Override
			public void run() {

				RefreshDatabase updateDB = new RefreshDatabase(
						ActivityLists.this);
				final boolean isUpdatePresent = updateDB.update(feedType);
				Log.i(DEBUG_TAG, "updated: " + isUpdatePresent);
				handler2.post(new Runnable() {

					@Override
					public void run() {

						if (RefreshDatabase.isErrorInFetchingFeed) {
							// Error in database update- check if internet
							// connection is there
							if (isNetworkAvailable()) {
								dialog.dismiss();
								mlog("Error: but network available");
								Toast.makeText(
										ActivityLists.this,
										getString(R.string.error_msg_in_fetching_feed),
										Toast.LENGTH_LONG).show();
							} else {
								dialog.dismiss();
								mlog("Error: network un available");
								Toast.makeText(
										ActivityLists.this,
										getString(R.string.error_msg_internet_unavailable),
										Toast.LENGTH_LONG).show();
							}

						} else {

							if (isUpdatePresent) {

								listItemsGrid.setAdapter(new BoxListAdapter(
										ActivityLists.this, feedType));
								listItemsGrid
										.setOnItemClickListener(ActivityLists.this);

								dialog.dismiss();
								Toast.makeText(
										ActivityLists.this,
										"Added "
												+ RefreshDatabase.NUM_OF_ITEMS_ADDED
												+ " item(s) to database",
										Toast.LENGTH_LONG).show();
							} else {
								dialog.dismiss();
								Toast.makeText(ActivityLists.this,
										"Database is up to date",
										Toast.LENGTH_LONG).show();
							}
						}
					}

				});

			}

		}).start();

	}

	/**
	 * After returning from the preference sub activity, on create is called
	 * with null saved instance state because if we reload the activity we need
	 * to supply the intents. Since only the views are going to be changed, on
	 * create is called
	 * 
	 */
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		mlog("returned from preference activity");
		if (requestCode == 1) {
			mlog("here");
			// returned from preference activity
			this.onCreate(null);
		}

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.listview_home:
			Intent home = new Intent(ActivityLists.this, ActivityShowCase.class);
			this.startActivity(home);

			break;

		case R.id.lists_menu:

			openOptionsMenu();
			break;

		}

	}

	/**
	 * 
	 * check if network is available
	 * 
	 * @return true if network is available
	 */
	private boolean isNetworkAvailable() {
		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager
				.getActiveNetworkInfo();
		return activeNetworkInfo != null;
	}

	private void mlog(String msg) {
		Log.d(DEBUG_TAG, msg);
	}

}
