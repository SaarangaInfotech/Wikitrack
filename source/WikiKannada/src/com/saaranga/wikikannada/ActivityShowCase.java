package com.saaranga.wikikannada;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.SlidingDrawer;
import android.widget.SlidingDrawer.OnDrawerCloseListener;
import android.widget.SlidingDrawer.OnDrawerOpenListener;
import android.widget.TextView;

import com.saaranga.wikitrack.utilities.Constants;

/**
 * Activity class - <br>
 * PURPOSE: This activity is showcase activity for the app
 *  <br>
 * It displays the following items
 * <ul> 
 * <li> Recent changes label </li>
 * <li> My contributions </li>
 * <li> my watch list </li>
 * <li> More apps from saaranga </li>
 * </ul>
 * It also has header label to preference activity, A drawer for reach us buttons with options to 
 * <ul> 
 * <li> Mail developer</li>
 * <li> Follow saaranga on twitter </li>
 * <li> like saaranga facebook page </li>
 * <li> Visiit saaranga website </li>
 * </ul>
 *  
 * 
 * @author supreeth
 * @version 1.0 30-05-2012
 * 
 *          Copyright Saaranga Infotech
 */
public class ActivityShowCase extends Activity implements OnClickListener {

	private static final String tag = "showcase activity";

	private FeedDBAdapter dbAdapter = new FeedDBAdapter(this);
	private TextView recentChanges, myWatchList, myContributions, moreApps;
	private TextView settings;
	private String DEBUG_TAG = "showcase activity";
	private TextView slidingButton;
	private SlidingDrawer slidingDrawer;
	private TextView mail;
	private TextView follow;
	private TextView like;
	private TextView site;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mlog("on create");
		setContentView(R.layout.activity_showcase);
		setUpUI();

	}

	/**
	 * create the views for the activity
	 */
	private void setUpUI() {
		recentChanges = (TextView) findViewById(R.id.recent_changes);
		myWatchList = (TextView) findViewById(R.id.my_watchlist);
		myContributions = (TextView) findViewById(R.id.my_contributions);
		// favoritePages = (TextView)findViewById(R.id.showcase_favorites);
		moreApps = (TextView) findViewById(R.id.more_apps);
		settings = (TextView) findViewById(R.id.showcase_preferences);

		// sliding drawer
		slidingButton = (TextView) findViewById(R.id.handle_button);
		slidingDrawer = (SlidingDrawer) findViewById(R.id.slidingDrawer1);
		mail = (TextView) findViewById(R.id.drawer_mail);
		follow = (TextView) findViewById(R.id.drawer_follow);
		like = (TextView) findViewById(R.id.drawer_like);
		site = (TextView) findViewById(R.id.drawer_site);

		// set typeace
		Typeface tf = Constants.getTypeface(this);
		if (tf != null) {
			// font preference present
			mlog("tf is not null");
			recentChanges.setTypeface(tf);
			myWatchList.setTypeface(tf);
			myContributions.setTypeface(tf);
			// favoritePages.setTypeface(tf);
			moreApps.setTypeface(tf);

		} else {
			mlog("tf is null");
			// dont set typeface
		}

	}

	/**
	 * Release the objects in onpause
	 * 
	 */
	@Override
	protected void onPause() {
		mail.setOnClickListener(null);
		recentChanges.setOnClickListener(null);
		myContributions.setOnClickListener(null);
		myWatchList.setOnClickListener(null);
		moreApps.setOnClickListener(null);
		// favoritePages.setOnClickListener(null);
		settings.setOnClickListener(null);
		follow.setOnClickListener(null);
		like.setOnClickListener(null);
		site.setOnClickListener(null);

		slidingDrawer.setOnDrawerOpenListener(null);

		slidingDrawer.setOnDrawerCloseListener(null);
		super.onPause();
	}

	/**
	 * Release the objects in onResume
	 * 
	 */
	@Override
	protected void onResume() {
		super.onResume();
		mail.setOnClickListener(this);
		recentChanges.setOnClickListener(this);
		myContributions.setOnClickListener(this);
		myWatchList.setOnClickListener(this);
		moreApps.setOnClickListener(this);
		// favoritePages.setOnClickListener(this);
		settings.setOnClickListener(this);
		follow.setOnClickListener(this);
		like.setOnClickListener(this);
		site.setOnClickListener(this);

		slidingDrawer.setOnDrawerOpenListener(new OnDrawerOpenListener() {

			@Override
			public void onDrawerOpened() {
				slidingButton.setCompoundDrawablesWithIntrinsicBounds(0, 0,
						R.drawable.expander_ic_minimized, 0);
			}
		});

		slidingDrawer.setOnDrawerCloseListener(new OnDrawerCloseListener() {

			@Override
			public void onDrawerClosed() {
				slidingButton.setCompoundDrawablesWithIntrinsicBounds(0, 0,
						R.drawable.expander_ic_maximized, 0);
			}
		});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.recent_changes:
			// If the previous database is present load it directly and give
			// refresh button
			Intent rcIntent;
			if (dbAdapter.isTableExists(Constants.RECENT_CHANGES, true)) {
				rcIntent = new Intent(ActivityShowCase.this,
						ActivityLists.class);
				rcIntent.putExtra("FEEDTYPE", Constants.RECENT_CHANGES);
			} else {

				rcIntent = new Intent(ActivityShowCase.this,
						ActivityRecentChanges.class);
			}
			this.startActivity(rcIntent);
			break;

		case R.id.my_contributions:
			// If the previous database is present load it directly and give
			// refresh button

			Intent mycIntent;
			if (dbAdapter.isTableExists(Constants.MY_CONTRIBUTIONS, true)) {
				mycIntent = new Intent(ActivityShowCase.this,
						ActivityLists.class);
				mycIntent.putExtra(Constants.FEEDTYPE,
						Constants.MY_CONTRIBUTIONS);
			} else {
				mycIntent = new Intent(ActivityShowCase.this,
						ActivityMyContributions.class);
			}
			this.startActivity(mycIntent);

			break;

		case R.id.my_watchlist:
			// If the previous database is present load it directly and give
			// refresh button
			Intent wlIntent;
			if (dbAdapter.isTableExists(Constants.MYWATCHLIST, true)) {
				Log.i(tag, "table exists");
				wlIntent = new Intent(ActivityShowCase.this,
						ActivityLists.class);
				wlIntent.putExtra(Constants.FEEDTYPE, Constants.MYWATCHLIST);
			} else {
				wlIntent = new Intent(ActivityShowCase.this,
						ActivityMyWatchList.class);
			}
			this.startActivity(wlIntent);

			break;


		case R.id.more_apps:
			Intent moreappsIntent = new Intent(Intent.ACTION_VIEW,
					Uri.parse(Constants.LINK_APPS_BY_SAARANGA));
			moreappsIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			this.startActivity(moreappsIntent);

			break;

		case R.id.showcase_preferences:
			Intent prefIntent = new Intent(this,
					ActivityApplicationPreferenceList.class);
			startActivityForResult(prefIntent, 0);

			break;

		case R.id.drawer_mail:
			mlog("mail clicked");
			// send email to developer
			Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
			emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, Constants.EMAIL_TO);
			emailIntent.setType("plain/text");
			this.startActivity(emailIntent);

			break;

		case R.id.drawer_follow:
			Intent twitterIntent = new Intent(Intent.ACTION_VIEW,
					Uri.parse(Constants.LINK_SAARANGA_TWITTER_PAGE));
			twitterIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			this.startActivity(twitterIntent);

			break;

		case R.id.drawer_like:
			Intent facebookIntent = new Intent(
					Intent.ACTION_VIEW,
					Uri.parse(Constants.LINK_SAARANGA_FACEBOOK_PAGE));
			facebookIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			this.startActivity(facebookIntent);

			break;

		case R.id.drawer_site:

			Intent saarangaIntent = new Intent(Intent.ACTION_VIEW,
					Uri.parse(Constants.LINK_SAARANGA_WEBSITE));
			saarangaIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			this.startActivity(saarangaIntent);
			break;
		default:
			break;
		}

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
		if (requestCode == 0) {
			mlog("here");
			// returned from preference activity
			this.onCreate(null);
		}

	}

	private void mlog(String msg) {
		Log.d(DEBUG_TAG, msg);
	}
}