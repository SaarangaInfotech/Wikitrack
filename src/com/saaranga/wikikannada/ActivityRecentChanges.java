package com.saaranga.wikikannada;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.SQLException;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.saaranga.wikitrack.utilities.Constants;

/**
 * activity class - <br>
 * PURPOSE: This activity is not loaded as of now, recent changes list is loaded directly
 *  <br>
 * 
 *  
 * TODO option for advanced query settings can be provided in future releases
 * 
 * @author supreeth
 * @version 1.0 30-05-2012
 * 
 *          Copyright Saaranga Infotech
 */
public class ActivityRecentChanges extends Activity {
	
	private static final String tag = "Activity recent changes";
	
//	private String DEBUG_TAG = "Activity recent changes";

	private TextView noFeedText;
	  
	
	 @Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.activity_recent_changes);
	        
	        setUpUI();
	        
	        //fetches the feed data and loads the listview
	        new getRecentChangesFeedTask().execute("");  
	       
	    }

	 /**
	  * create the views for the activity
	  */	
	private void setUpUI() {
		noFeedText = (TextView)findViewById(R.id.rc_no_feed_text);
		
		Typeface tf = Constants.getTypeface(this);
		if(tf!=null) {
			noFeedText.setTypeface(tf);	
		}
		
	}

/**
 * Asynctask to get the recent changes feed items and store them in database
 * 
 * @author supreeth
 *
 */
	public class getRecentChangesFeedTask extends AsyncTask<String,Void,Void> {
		ProgressDialog dialog;
		private RSSFeed rssFeed = new RSSFeed();
		private GetRssFeedClass getRssFeedClass;
		private FeedDBAdapter feedDBAdapter = new FeedDBAdapter(ActivityRecentChanges.this);
		private boolean isError = false;
		
		private List<RSSItem> _itemList;
		
		/*
		 * Constructor
		 */
		public getRecentChangesFeedTask() {
			
		}
		
		/**
		 * A progress dialog is displayed till the feed is fetched from internet
		 * 
		 */
		@Override
		protected void onPreExecute() {
			dialog = new ProgressDialog(ActivityRecentChanges.this);
			dialog.setCancelable(false);
			dialog.setButton(DialogInterface.BUTTON_NEGATIVE,"cancel", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});
			dialog.setIcon(android.R.drawable.ic_dialog_info);
			dialog.setTitle(getString(R.string.kn_pls_wait));
			dialog.setMessage(getString(R.string.kn_loading));
			dialog.show();
		}
		
		@Override
		protected Void doInBackground(String... params) {
			try {
				getRssFeedClass = new GetRssFeedClass();
				rssFeed = getRssFeedClass.getFeed(Constants.RECENT_CHANGES_FEEDURL);
				_itemList = new ArrayList<RSSItem>();
				
				if(rssFeed!=null) {
					Log.d(tag, "RssFeed is not null");
					_itemList = rssFeed.getAllItems();
					//release the object
					rssFeed = null;
				} else {
					Log.d(tag, "RssFeed is null");
				}
			} catch (Exception e1) {
				e1.printStackTrace();
				isError = true;
			} 
			
			try {
				feedDBAdapter.updateDatabaseTable(Constants.RECENT_CHANGES);
				feedDBAdapter.open();
				for(int i=0;i<_itemList.size();i++) {
					feedDBAdapter.insertFeedItem(Constants.RECENT_CHANGES, _itemList.get(i).getTitle(),
							_itemList.get(i).getLink(), _itemList.get(i).getSummary(), _itemList.get(i).getUpdated(),_itemList.get(i).getAuthor(), i);
				}
				feedDBAdapter.close();
				
				//trim to the database length - always be true
				feedDBAdapter.trimDatabaseToFeedItemLimit(Constants.RECENT_CHANGES);
				
				//release itemlist
				_itemList.clear();
				
			} catch (SQLException e) {
				e.printStackTrace();
				isError= true;
			}
			
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			if(isError) {
				Log.e(tag, "Error in fetching feed");
			}
			dialog.dismiss();
			
			Intent recentChangesDefaultFeedIntent = new Intent(ActivityRecentChanges.this, ActivityLists.class);
			recentChangesDefaultFeedIntent.putExtra(Constants.FEEDTYPE, Constants.RECENT_CHANGES);
			ActivityRecentChanges.this.startActivity(recentChangesDefaultFeedIntent);
			ActivityRecentChanges.this.finish();
		}
		
	}
	
}
