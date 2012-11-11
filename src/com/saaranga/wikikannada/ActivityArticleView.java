package com.saaranga.wikikannada;

import java.util.ArrayList;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.saaranga.wikitrack.utilities.Constants;
import com.saaranga.wikitrack.utilities.DateFormatter;
import com.saaranga.wikitrack.utilities.KannadaWebViewSupport;

/**
 * activity class - <br>
 * PURPOSE: Displays the article - uses an extended webview <br>
 * Has options menu with the following items: *
 * <ul>
 * <li>Mark as read</li>
 * <li>Mark as favorite</li>
 * <li>Help</li>
 * <li>Share article</li>
 * </ul>
 * 
 * @author supreeth
 * @version 1.0
 * 
 *          Copyright Saaranga Infotech
 */
public class ActivityArticleView extends Activity implements OnClickListener,
		OnPageChangeListener {

	private static final int OFF_SCREEN_LIMIT = 2;
	private final String tag = "Article View Activity";
	private FeedDBAdapter feedDBAdapter;

	private ViewPager articleViewPager;
	private PagerAdapter mPageAdapter;
	private ProgressDialog dialog;
	private static int NUM_VIEWS;

	private String feedType;
	private int positionOfItemClicked;
	int positionOfArticle = 0;

	ArrayList<String> titleList;
	private ImageView home;
	private TextView menu;
	private String DEBUG_TAG = "Article View Activity";
	private Dialog help_dialog;
	private ToggleButton readFlag;
	private ToggleButton favoriteFlag;
	private TextView pageCount;
	static ArrayList<String> titleList1 = new ArrayList<String>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_articleview);

		getStartingIntents();

		feedDBAdapter = new FeedDBAdapter(ActivityArticleView.this);
	}

	@Override
	protected void onPause() {
		mlog("in onpause");

		setUpUIRelease();

		// release objects
		feedDBAdapter = null;

		if (dialog.isShowing()) {
			dialog.dismiss();
		}
		super.onPause();
	}

	@Override
	protected void onResume() {
		mlog("in on resume");
		super.onResume();

		// recreate released objects

		setUpUI();

		loadAdapter();

	}

	/**
	 * set the adapter for the view pager
	 * 
	 */
	private void loadAdapter() {
		// get data

		titleList = feedDBAdapter.getTitleList(feedType);
		// get the number of pages to be added to the pager adapter
		NUM_VIEWS = titleList.size();

		final Handler handler = new Handler();
		dialog = ProgressDialog.show(this, "Loading..", "Please wait");

		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {

				Log.i(tag, "number of views:" + NUM_VIEWS);
				mPageAdapter = new pagerAdapter(titleList,
						positionOfItemClicked, feedType);

				handler.post(new Runnable() {

					@Override
					public void run() {
						articleViewPager = (ViewPager) findViewById(R.id.articleview_pager);
						articleViewPager.setAdapter(mPageAdapter);
						// number of pages to be cached in the view pagers
						articleViewPager
								.setOffscreenPageLimit(OFF_SCREEN_LIMIT);
						articleViewPager
								.setOnPageChangeListener(ActivityArticleView.this);
						// go to the respective item in the view pager, smooth
						// scroll = true
						articleViewPager.setCurrentItem(positionOfItemClicked,
								true);

						dialog.dismiss();
					}
				});
			}
		});

		thread.start();
	}

	/**
	 * creates the views for this activity
	 * 
	 */
	private void setUpUI() {
		// Declaring the views
		home = (ImageView) findViewById(R.id.articleview_home);
		menu = (TextView) findViewById(R.id.articleview_menu);
		readFlag = (ToggleButton) findViewById(R.id.articleview_read_flag);
		favoriteFlag = (ToggleButton) findViewById(R.id.articleview_favorite_flag);
		pageCount = (TextView) findViewById(R.id.articleview_pagecount);

		// set click listeners
		home.setOnClickListener(this);
		menu.setOnClickListener(this);
	}

	/**
	 * The UI elements are released
	 * 
	 */
	private void setUpUIRelease() {
		// Declaring the views

		// set click listeners
		home.setOnClickListener(null);
		menu.setOnClickListener(null);
	}

	/**
	 * Get the starting intents
	 * 
	 */
	private void getStartingIntents() {
		feedType = getIntent().getExtras().getString("FEEDTYPE");
		positionOfItemClicked = getIntent().getExtras().getInt("POSITION");
		Log.i(tag, "position of item:" + positionOfItemClicked);
	}

	/**
	 * Inner class for pager adapter
	 * 
	 * @author supreeth
	 * 
	 */
	private class pagerAdapter extends PagerAdapter {
		private ArrayList<String> titleListForAdapter;
		private String fontFacestyle;
		private String font;
		private KannadaWebViewSupport kannadaWebViewSupport;
		private DateFormatter df;
		private LayoutInflater inflater;

		public pagerAdapter(ArrayList<String> titleList, int pos,
				String feedType) {
			titleListForAdapter = titleList;
			font = Constants.getFontName(ActivityArticleView.this);

			// set the font face for the webview
			if (!font.equals("nothing")) {
				fontFacestyle = "@font-face {font-family: '"
						+ Constants.getFontFamily(font)
						+ "'; src: url('file:/"
						+ ActivityArticleView.this.getFilesDir()
								.getAbsolutePath() + "/" + font + "');}";
			} else {
				fontFacestyle = "";
			}

			mlog("fontface: " + fontFacestyle);

			kannadaWebViewSupport = new KannadaWebViewSupport();
			kannadaWebViewSupport.copyFile(ActivityArticleView.this, font);

			// Formatter the pubdate string
			df = new DateFormatter("yyyy-MM-dd hh:mm:ss");

			inflater = (LayoutInflater) getLayoutInflater();
		}

		@Override
		public int getCount() {
			return NUM_VIEWS;
		}

		@Override
		public Object instantiateItem(View collection, int p) {
			Log.i(tag, "instantiate item called and position: " + p);

			View pagerView = inflater.inflate(R.layout.articleview_layout,
					articleViewPager, false);

			// prepare the text to display
			String title = titleListForAdapter.get(p);
			String author = feedDBAdapter.getAuthor(feedType, p);
			String unformattedDate = feedDBAdapter.getPubDate(feedType, p);
			String description = feedDBAdapter.getDescription(feedType, p);
			mlog("desc: " + description);

			String pubdate = unformattedDate;
			try {
				// replcing T and Z present in the feed of wikipedia
				pubdate = df
						.getFormattedDate(unformattedDate.replace("T", " "))
						.replace("Z", "");
			} catch (Exception e) {
				e.printStackTrace();
			}
			mlog("unfomrttd date: " + unformattedDate + " formatted: "
					+ pubdate);

			String htmlHead = "<head> <style> " + fontFacestyle
					+ "body {font-family: '" + Constants.getFontFamily(font)
					+ "';}" + ".feed_body { border-top: 1px solid #999 }"
					+ ".feed_title { font-size : 18pt;color : #800000; } "
					+ ".feed_author { font-size : 12pt;color : #999;} "
					+ ".feed_date{ font-size : 10pt;color : #999;}"
					+ "</style></head>";
			String htmlBody = "<body>" + "<div class=\"feed_title\">" + title
					+ "</div><div class=\"feed_author\"> Author: " + author
					+ "</div><div class=\"feed_date\">" + pubdate + "</div>"
					+ "<div class=\"feed_body\">" + description
					+ "</div></body>";

			String htmlData = htmlHead + htmlBody;
			com.saaranga.wikikannada.ExtendedWebView descView = (com.saaranga.wikikannada.ExtendedWebView) pagerView
					.findViewById(R.id.articleView_desc);
			descView.getSettings().setBuiltInZoomControls(true);

			descView.loadDataWithBaseURL(null, htmlData, "text/html", "utf-8",
					null);
			// release all strings
			htmlBody = "";
			htmlHead = "";
			htmlData = "";
			title = "";
			author = "";
			description = "";
			pubdate = "";
			unformattedDate = "";

			((ViewPager) collection).addView(pagerView, 0);
			return pagerView;
		}

		@Override
		public void destroyItem(View collection, int position, Object view) {
			((ViewPager) collection).removeView((View) view);
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == ((View) object);
		}

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.articleview_home:
			Intent home = new Intent(ActivityArticleView.this,
					ActivityShowCase.class);
			this.startActivity(home);

			break;

		case R.id.articleview_menu:
			this.openOptionsMenu();
			break;

		}

	}

	/**
	 * create options menu. It has the following menu items
	 * <ul>
	 * <li>Mark as read</li>
	 * <li>Mark as favorite</li>
	 * <li>Help</li>
	 * <li>Share article</li>
	 * </ul>
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.articleview_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		int currentPosition = articleViewPager.getCurrentItem();
		mlog("pos: " + currentPosition);// starts with
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.articleview_menu_share:
			Intent sharingIntent = new Intent(Intent.ACTION_SEND);
			sharingIntent.setType("text/plain");
			sharingIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
			sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Wikitrack Kannada");
			sharingIntent.putExtra(
					Intent.EXTRA_TEXT,
					getString(R.string.kn_article_share_message)
							+ feedDBAdapter.getTitle(feedType,
									articleViewPager.getCurrentItem())
							+ "\\n"
							+ "[Link: "
							+ feedDBAdapter.getLink(feedType,
									articleViewPager.getCurrentItem()) + " ]");
			startActivity(Intent
					.createChooser(sharingIntent, "Share this Item"));

			return true;

		case R.id.articleview_menu_mark_read:
			int read_flag = feedDBAdapter
					.getReadFlag(feedType, currentPosition);
			if (read_flag == 0) {
				feedDBAdapter.markRead(feedType, currentPosition);
				readFlag.setChecked(true);
				Toast.makeText(ActivityArticleView.this, "Marked as read",
						Toast.LENGTH_LONG).show();
			} else {
				Toast.makeText(ActivityArticleView.this,
						"Already marked as read", Toast.LENGTH_LONG).show();
			}
			return true;

		case R.id.articleview_menu_mark_favorite:
			int fav_flag = feedDBAdapter.getFavoriteFlag(feedType,
					currentPosition);
			if (fav_flag == 0) {
				feedDBAdapter.markFavorite(feedType, currentPosition);
				favoriteFlag.setChecked(true);
				Toast.makeText(ActivityArticleView.this, "Marked as favorite",
						Toast.LENGTH_LONG).show();
			} else {
				feedDBAdapter.markUnFavorite(feedType, currentPosition);
				Toast.makeText(ActivityArticleView.this,
						"Already marked as favorite ", Toast.LENGTH_LONG)
						.show();
			}
			return true;

		case R.id.articleview_menu_help:

			help_dialog = new Dialog(ActivityArticleView.this);
			help_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			help_dialog.setContentView(R.layout.dialog_help);
			help_dialog.getWindow().setLayout(LayoutParams.FILL_PARENT,
					LayoutParams.FILL_PARENT);
			help_dialog.getWindow().setBackgroundDrawable(
					new ColorDrawable(R.color.transperant_help_dialog));
			help_dialog.getWindow().clearFlags(
					WindowManager.LayoutParams.FLAG_DIM_BEHIND);

			help_dialog.show();
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void mlog(String msg) {
		Log.d(DEBUG_TAG, msg);
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
	}

	/**
	 * This method can be used to change the page specific information such as
	 * getting the current item of the pager - because the state of the flags
	 * cannot be manipulated from inside the instantiate item method
	 * 
	 */
	@Override
	public void onPageSelected(int pageIndex) {
		int read_falg = feedDBAdapter.getReadFlag(feedType, pageIndex);
		int favorite_flag = feedDBAdapter.getFavoriteFlag(feedType, pageIndex);

		if (read_falg == 1) {
			readFlag.setChecked(true);
		} else {
			readFlag.setChecked(false);
		}
		if (favorite_flag == 1) {
			favoriteFlag.setChecked(true);
		} else {
			favoriteFlag.setChecked(false);
		}

		// set the page count
		String pageCountText = "" + (pageIndex + 1) + "/" + NUM_VIEWS; // since
																		// the
																		// index
																		// of
																		// first
																		// page
																		// will
																		// be
																		// zero
		pageCount.setText(pageCountText);
	}

}
