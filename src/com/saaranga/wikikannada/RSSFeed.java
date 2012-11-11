package com.saaranga.wikikannada;

import java.util.List;
import java.util.Vector;

/**
 * Utitlity class - RSSFeed class
 * 
 * RSSItems are added to this class. call get all items to get the list of all
 * RSS items
 * 
 * @author supreeth
 * @version 1.0 30-05-2012
 * 
 *          Copyright Saaranga Infotech
 */
public class RSSFeed {
	private String _title = null;
	private String _pubdate = null;
	private String _subtitle = null;
	private int _itemcount = 0;
	private List<RSSItem> _itemlist;

	RSSFeed() {
		_itemlist = new Vector<RSSItem>(0);
	}

	/**
	 * RSS items are added to the list
	 * 
	 */
	void addItem(RSSItem item) {
		_itemlist.add(item);
		_itemcount++;
		// Log.i(tag, "Item  added count is: " +_itemcount);

	}

	/*
	 * Setters
	 */
	// title of the channel
	void setTitle(String name) {
		_title = name;

		// Log.i(tag, "Feed Title is set: "+_title);
	}

	// published date of the channel
	void setPubDate(String date) {
		_pubdate = date;
		// Log.i(tag, "Feed updated is set: "+_pubdate);
	}

	// set the subtitle of the feed
	void setSubtitle(String st) {
		_subtitle = st;
		// Log.i(tag, "Feed subtitle is set: "+_subtitle);
	}

	/*
	 * Getters
	 */

	RSSItem getItem(int location) {
		return _itemlist.get(location);
	}

	List<RSSItem> getAllItems() {
		return _itemlist;
	}

	int getItemCount() {
		return _itemcount;
	}

	String getTitle() {
		return _title;
	}

	String getPubDate() {
		return _pubdate;
	}

	String getSubtitle() {
		return _subtitle;
	}

}
