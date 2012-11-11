package com.saaranga.wikikannada;

import android.util.Log;


/*
 *Individual items of the RSS feed are constructed here 
 */
public class RSSItem {

	private String tag = "RSSItem class";
	
	private String _title=null;
	private String _summary=null;
	private String _updated=null;
	private String _link=null;
	private String _author=null;
	int i=0;
	
	/**
	 * constructor withour params
	 */
	RSSItem(){
		
	}
	
	/*
	 * Setters
	 */
	
		//set the title of the item - podcast
		void setTitle(String title){
			_title=title;
			Log.i(tag, "Item title: "+title);
		}
	
	void setSummary(String description){
		_summary=description;
		Log.i(tag, "Item description: "+description);
		
	}
	
	void setAuthor(String ath) {
		_author = ath;
		Log.i(tag, "Item author: "+_author);
	}
	
	//Date of publication of the item not the date of interview conducted
	void setUpdated(String pubdate){
		_updated=pubdate;
		Log.i(tag, "Item pubdate: "+_updated);
	}
	
	//set link for the podcast
	void setLink(String link){
		_link=link;
		Log.i(tag, "Item link: "+_link);
		}
	
	
	
	/*
	 * Getters
	 */
	
	String getTitle(){
		return _title;
	}
	
	
	String getSummary(){
		return _summary;
	}
	
	String getAuthor()
	{
		return _author;
	}
	
	String getUpdated(){
		return _updated;
	}
	
	String getLink(){
		return _link;
	}
	
		
	@Override
	public String toString(){
		
		if(_title.length()>42){
			return _title.substring(0, 42)+"...";
		}
		return _title;
	}
	
}
