package com.saaranga.wikitrack.utilities;

import java.io.File;
import java.util.Date;

import android.content.Context;
import android.text.format.DateUtils;
import android.util.Log;

/**
 * Utitlity class - clear the application cache
 * 
 * USAGE: call the clearCache() method
 * 
 * @author supreeth
 * @version 1.0 30-05-2012
 * 
 *          Copyright Saaranga Infotech
 */
public class ClearApplicationCache {
	
	private static String TAG = "clear application cache";

	//helper method for clearCache() , recursive
	//returns number of deleted files
	private static int clearCacheFolder(final File dir, final int numDays) {

	    int deletedFiles = 0;
	    if (dir!= null && dir.isDirectory()) {
	        try {
	            for (File child:dir.listFiles()) {

	                //first delete subdirectories recursively
	                if (child.isDirectory()) {
	                    deletedFiles += clearCacheFolder(child, numDays);
	                }

	                //then delete the files and subdirectories in this dir
	                //only empty directories can be deleted, so subdirs have been done first
	                if (child.lastModified() < new Date().getTime() - numDays * DateUtils.DAY_IN_MILLIS) {
	                    if (child.delete()) {
	                        deletedFiles++;
	                    }
	                }
	            }
	        }
	        catch(Exception e) {
	            Log.e(TAG , String.format("Failed to clean the cache, error %s", e.getMessage()));
	        }
	    }
	    return deletedFiles;
	}

	/*
	 * Delete the files older than numDays days from the application cache
	 * 0 means all files.
	 */
	public static void clearCache(final Context context, final int numDays) {
	    Log.i(TAG, String.format("Starting cache prune, deleting files older than %d days", numDays));
	    int numDeletedFiles = clearCacheFolder(context.getCacheDir(), numDays);
	    Log.i(TAG, String.format("Cache pruning completed, %d files deleted", numDeletedFiles));
	}


}
