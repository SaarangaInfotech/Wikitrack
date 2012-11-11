package com.saaranga.wikikannada;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

import com.saaranga.wikitrack.utilities.ClearApplicationCache;
import com.saaranga.wikitrack.utilities.Constants;

/**
 * Preference activity class
 * <br>
 * PURPOSE: provides interface for the default application preferences.
 * 
 * <br>
 * This has the following preferences
 * <ul>
 * <li>
 * Set the feed count to be stored locally in the app database
 * </li>
 * <li>Change the application font </li>
 * <li>Delete the private data</li>
 * <li>Delete database </li>
 * <li>Clear web cache </li>
 * <li>About developer</li>
 * <li>About the application </li>
 * </ul>
 * 
 * @author supreeth
 *@version 1.0
 *
 *Copyright Saaranga Infotech
 */
public class ActivityApplicationPreferenceList extends PreferenceActivity
		implements OnSharedPreferenceChangeListener, OnPreferenceClickListener {

	private String DEBUG_TAG = "Activity application preference list";
	private SharedPreferences customPreference;
	private Builder builder;

	private Preference clearCache;
	private Preference deleteAppDatabase;
	private final int dialogIcon = R.drawable.ic_menu_info_details;
	private boolean deleteRC = false, deleteMC = false, deleteWL = false;
	private Preference aboutDev;
	private Preference aboutApp;
	private Preference eraseData;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mlog("on create 1");
		addPreferencesFromResource(R.xml.custom_preferences);
		mlog("on create 2");

		// get the preference
		customPreference = PreferenceManager.getDefaultSharedPreferences(this);
		eraseData.setOnPreferenceClickListener(this);

		// clear cache
		clearCache = (Preference) getPreferenceScreen().findPreference(
				Constants.CLEAR_CACHE);
		clearCache.setOnPreferenceClickListener(this);

		// Delete database
		deleteAppDatabase = (Preference) getPreferenceScreen().findPreference(
				Constants.DELETE_DATABASE);
		deleteAppDatabase.setOnPreferenceClickListener(this);
		
		//about developer
		aboutDev = (Preference)getPreferenceScreen().findPreference(Constants.ABOUT_DEVELOPER);
		aboutDev.setOnPreferenceClickListener(this);
		
		//about application
		aboutApp = (Preference)getPreferenceScreen().findPreference(Constants.ABOUT_APPLICATION);
		
		aboutApp.setOnPreferenceClickListener(this);

	}

	@Override
	protected void onResume() {
		super.onResume();
		mlog("in onresume");

		customPreference.registerOnSharedPreferenceChangeListener(this);

	}

	@Override
	protected void onPause() {
		mlog("in onpause");
		customPreference.unregisterOnSharedPreferenceChangeListener(this);
		super.onResume();
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		mlog("in on shared preference changed method");
		// font changed - reload the activity
		if (key.equals(Constants.SELECT_FONT)) {
			mlog("font changed");
			reload();
		}
		
	}

	private void reload() {
		startActivity(getIntent());
		this.finish();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {

			setResult(RESULT_OK);
			finish();
			onBackPressed();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private void mlog(String msg) {
		Log.d(DEBUG_TAG, msg);
	}

	@Override
	public boolean onPreferenceClick(Preference preference) {
		mlog("preferene clicked");
		if (preference.getKey().equals(Constants.ERASE_PRIVATE_DATA)) {
			mlog("erase data");
			erasePrivateDataDialog();
		}

		if (preference.getKey().equals(Constants.CLEAR_CACHE)) {
			mlog("erase cache");
			clearChacheDialog();
		}

		if (preference.getKey().equals(Constants.DELETE_DATABASE)) {
			mlog("delete database");
			deleteAppDatabaseDialog();
		}
		
		if (preference.getKey().equals(Constants.ABOUT_DEVELOPER)) {
			mlog("about dev");
			showAboutDevDialog();
		}
		if (preference.getKey().equals(Constants.ABOUT_APPLICATION)) {
			mlog("about app");
			showAboudAppDialog();
		}

		return false;
	}

	private void showAboudAppDialog() {
		builder = new AlertDialog.Builder(this);
		builder.setTitle("About application")
				.setIcon(dialogIcon)
				.setMessage(
						"About text for this app. Help option menu item has more info on how to use.");
		builder.create().show();
	}

	private void showAboutDevDialog() {
		builder = new AlertDialog.Builder(this);
		builder.setTitle("About developer")
				.setIcon(dialogIcon)
				.setMessage(
						"About Saaranga infotech and the developer of this app.");
		builder.create().show();
	}

	private void deleteAppDatabaseDialog() {
		builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.kn_pref_deletedb_title)
				.setIcon(dialogIcon)
//				.setMessage(
//						"ನಿಮ್ಮ ಅಪ್ಲಿಕೇಶನ್ ಡೇಟಾಬೇಸ್ ಅಳಿಸಲಾಗುವುದು. ನಿಮ್ಮ ನೆಚ್ಚಿನ ಪುಟಗಳು, ಇತರೆ ಆಯ್ಕೆಗಳು ಸಹ ಅಳಿಸಲ್ಪಡುವವು. ಎಚ್ಚರಿಕೆಯಿಂದ ಮುಂದುವರೆಯಿರಿ.")
				.setMultiChoiceItems(
						new CharSequence[] {
								getString(R.string.kn_recent_changes),
								getString(R.string.kn_my_contributions),
								getString(R.string.kn_my_watchlist) },
						new boolean[] { false, false, false }, new DialogInterface.OnMultiChoiceClickListener() {
							
							@Override
							public void onClick(DialogInterface dialog, int which, boolean isChecked) {
								mlog("item: "+which);
								//set the flags to delete the database tables
								switch (which) {
								case 0:
									deleteRC = isChecked;
									break;
								case 1:
									deleteMC = isChecked;
									break;
								case 2:
									deleteWL = isChecked;
									break;

								}
							}
						})
				.setPositiveButton(R.string.kn_erase,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								//delete database
								FeedDBAdapter dbAdapter = new FeedDBAdapter(ActivityApplicationPreferenceList.this);
								if(deleteRC) {
									dbAdapter.updateDatabaseTable(Constants.RECENT_CHANGES);
								}
								if(deleteMC) dbAdapter.updateDatabaseTable(Constants.MY_CONTRIBUTIONS);
								if(deleteWL) dbAdapter.updateDatabaseTable(Constants.MYWATCHLIST);
								
								
								// disable the preference after deleting the
								// preference
								clearCache.setEnabled(false);
								
								Toast.makeText(
										ActivityApplicationPreferenceList.this,
										"Database deleted", Toast.LENGTH_SHORT)
										.show();
							}
						})
				.setNegativeButton(R.string.kn_return,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// do nothing
								return;
							}
						});

		builder.create().show();
	}

	private void clearChacheDialog() {
		builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.kn_clear_cache)
				.setIcon(dialogIcon)
				.setMessage(
						R.string.kn_clear_cache_message)
				.setPositiveButton(R.string.kn_erase,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// clear all webview cache
								ClearApplicationCache.clearCache(
										ActivityApplicationPreferenceList.this,
										0); // 0 means deletes all the cache

								// disable the preference after deleting the
								// preference
								clearCache.setEnabled(false);

								Toast.makeText(
										ActivityApplicationPreferenceList.this,
										"Cleared cache", Toast.LENGTH_SHORT)
										.show();

							}
						})
				.setNegativeButton(R.string.kn_return,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// do nothing
								return;
							}
						});

		builder.show();
	}

	private void erasePrivateDataDialog() {
		builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.kn_erase_private_data_title)
				.setIcon(dialogIcon)
				.setMessage(
						R.string.kn_erase_private_data_message)
				.setPositiveButton(R.string.kn_erase,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// this preference seems tobe different from the
								// application preference
								SharedPreferences settings = getSharedPreferences(
										"MY_PREFS", MODE_PRIVATE);
								SharedPreferences.Editor editor = settings
										.edit();
								editor.clear();
								editor.commit();

								// disable the preference after deleting the
								// preference
								eraseData.setEnabled(false);
							}
						})
				.setNegativeButton(R.string.kn_return,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// do nothing
								return;
							}
						});

		builder.show();
	}

}
