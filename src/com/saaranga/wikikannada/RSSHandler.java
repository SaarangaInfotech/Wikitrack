package com.saaranga.wikikannada;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.util.Log;

/*This class is used to handle the data parsed from the getFeed() method in main activity, 
 * You can change the structure of RSS xml document here to adapt to different type of RSS service like
 * Feed burner etc
 */
public class RSSHandler extends DefaultHandler {
	private String tag = "RSSHandler";
	
	public RSSFeed _feed;
	private RSSItem _item;
	//These are constants
	private final int RSS_TITLE=1;
	private final int RSS_LINK=2;
	//final int RSS_CATEGORY=4;
	private final int RSS_FEED_PUBDATE=5;
	private final int CHANNEL_TITLE=6;
	private final int RSS_ITEM_PUBDATE = 7;
	
	//fields to assist for extracting text with complex characters
	private boolean is_desc_started = false;
	private boolean is_channel_title_started= false;
	private boolean is_item_title_started = false;
	private boolean is_feed_subtitle = false; 
	private boolean is_author = false;
	
	//fields to build string for text having special characters
	private StringBuilder desc_string = new StringBuilder();
	private StringBuilder desc_string2 = new StringBuilder();
	private StringBuilder desc_string3 = new StringBuilder();
	private StringBuilder full_channel_title = new StringBuilder();
	private StringBuilder full_item_title = new StringBuilder();
	private StringBuilder full_subtitle;
	private StringBuilder full_authorName;
	
	//to flag that title is of item
	private boolean is_item = false;
	//to flag that title is of channel
	private boolean is_channel = false;
	
	private int depth=0;
	private int currentState=0;
	
	private String full_description;
	
	//constructor
	
	public RSSHandler(){
		
	}
	
	//getFeed() returns our feed when all of the parsing is completed
	
	public RSSFeed getFeed(){
		return _feed;
	}
	
	@Override
	public void startDocument() throws SAXException {
		//initialising the RSSFeed object this will hold the parsed contents from the xml stream
		
		_feed= new RSSFeed();
		//////Log.i(tag, "Sax started reading Document");
		//initialising the RSSItem object- this is used to pick items from the xml stream as both channel
		//and items have same elements, we use _item as a crutch to catch the content-later we need to pass
		//it on to Feed 
		
		_item=new RSSItem();
		
	}
	@Override
	public void endDocument() throws SAXException{
		//Do nothing
		
	}
	
	//this method recognises various elements of the rss feed 
	@Override
	public void startElement(String namespaceURI,String localname, String qname,Attributes atts) throws SAXException{
			Log.d(tag, "tag: "+localname);
			depth++;
			
			if(localname.equals("feed")){ //changed from channel
				////Log.i(tag, "tag read :feed");
				is_channel=true;
				currentState=0;
				return;
			}
		
			//read the subtitle for the feed
			if(localname.equals("subtitle")) {
			////Log.i(tag, "tag read: subtitle");
			is_feed_subtitle = true; 
			return;
			}
		
			//read the author for the items
			if(localname.equals("author")) {
			////Log.i(tag, "tag read: author");
			is_author = true;
			}
		
		
		if(localname.equals("entry")){ // changed from item
			////Log.i(tag, "tag read: entry");
			//flag to check the element is inside item
			is_item=true;
			_item= new RSSItem();
			return;
		}
		
		if(localname.equals("title")){
			////Log.i(tag, "tag read: title");			
			//This is to ensure that the title having special charcters is read properly- added on 18-10-11
			
			if (is_channel==true) {
				is_channel_title_started = true;
				currentState=CHANNEL_TITLE;
				
				//flag to uncheck the element is inside channel
				is_channel=false;
				
			} else if(is_item==true) {
				
				is_item_title_started = true;
				
				//flag unchecked to indicate its out of item
				currentState=RSS_TITLE;
				is_item = false;
				
			}
			return;
		}
		
		if(localname.equals("link")){
			//Log.i(tag, "tag read :link");
			//both feed and entry have links
			if(depth==3) {
				//currentState=RSS_LINK;	
				String href = atts.getValue("href");
				_item.setLink(href);
				//Log.i(tag, "tag read : item link"+href);
			}

			return;
		}
		
		if(localname.equals("summary")){ //changed from description
			//as feed has no summary field no need to bother about depth
			////Log.i(tag, "tag read : summary");
				is_desc_started = true;
			return;
		}
		
		/*
		if(localname.equals("category")){
			currentState=RSS_CATEGORY;
			return;
		}	
			*/
		if(localname.equals("updated")){ //changed from pubDate
			////Log.i(tag, "tag read :updated");
			//////Log.i(tag, "Item Pub Date is encountered ");
			if(depth==2) {
				currentState=RSS_FEED_PUBDATE;	
			} else if(depth==3) {
				currentState = RSS_ITEM_PUBDATE;
			}
			
			return;
		}
		
		currentState=0;
		
	}
	
	@Override
	public void endElement(String namespaceURI,String localname,String qname) throws SAXException {
		depth--;
		
		////////Log.i(tag, "element ended");
		if(localname.equals("entry")){
			//add entry to the list
			_feed.addItem(_item);
			//////Log.i(tag, "<Item added with Item count: "+_feed.getItemCount()+">");
			return;
		}
	
		if(localname.equals("summary")){
			////////Log.i(tag, "End of descritpion element");
			is_desc_started= false;
			full_description = desc_string.toString();
			_item.setSummary(full_description);
			desc_string.delete(0, desc_string.length());//To empty the description variable after one iteration
			return;
		}
		
		//to append the read title with special characters 
		if(localname.equals("title")) {
			
			
			//for channel title - depth is 2 because the depth is decremented at the beginning
			if(depth == 1){
			
			is_channel_title_started = false;
			
			//string builder produces the full title
			_feed.setTitle(full_channel_title.toString());
			
			
			//to empty the string builder to read new title
			full_channel_title.delete(0, full_channel_title.length());
			
			
			return;
			
			} else if(depth ==2){  //for item title - depth is 3 because it is decremented at the beginning 
				
								
				is_item_title_started = false;
				
				//string builder produces the full title
				_item.setTitle(full_item_title.toString());
				
				
				//to empty the string builder to read new title
				full_item_title.delete(0, full_item_title.length());
				return;

			} else return;
		}
		
		if(localname.equals("subtitle")) {
			is_feed_subtitle = false;
			_feed.setSubtitle(full_subtitle.toString());
		}
		
		if(localname.equals("author")) {
			is_author = false;
			_item.setAuthor(full_authorName.toString());
		}
	}
	
	
	/*
	 * this method takes the parsed content of each elements in the feed in the form of characters - (non-Javadoc)
	 * @see org.xml.sax.helpers.DefaultHandler#characters(char[], int, int)
	 */
	
	@Override
	public void characters(char ch[],int start,int length){
		
		String theString=new String(ch,start,length);
		 
		////////Log.i(tag,"characters ["+theString+"]");
		/*
		 * This condition is added to make sure that the description is read even though it has other tags which result in sax parser 
		 * terminating
		 */
		if(is_desc_started){
			if(desc_string.toString().length() < Integer.MAX_VALUE) {
				desc_string.append(new String(ch,start,length));
			
			} else if(desc_string2.toString().length() < Integer.MAX_VALUE) {
				//////Log.i("RSS handler", "desc buffer2");
				desc_string2.append(ch, start, length);
			} else {
				//////Log.i("RSS handler", "desc buffer3");
				desc_string3.append(ch, start, length);
			}
			
			////////Log.i(tag, "The updated descritpion is: " +desc_string);
		}
		
		//builds the channel title - 18-10-2011
		if(is_channel_title_started) {
			
			full_channel_title.append(new String(ch,start,length));
		}
		
		//builds the item title - 18-10-2011
		
		if(is_item_title_started) {
			
			full_item_title.append(new String(ch,start,length));
		}
		
		if(is_feed_subtitle) {
			full_subtitle = new StringBuilder();
			full_subtitle.append(new String(ch,start,length));
		}
		
		if(is_author) {
			full_authorName = new StringBuilder();
			full_authorName.append(new String(ch,start,length));
		}
		
		
		switch(currentState){
		
		 case CHANNEL_TITLE:
		
			 //commented out as full title is read in end element method 
			//_feed.setTitle(theString);
				////////Log.i(tag, "Channel title set: "+ _feed.getTitle());
				currentState=0;
				break;
					
		case RSS_TITLE:
			
			/*
			 * depth limit changed from 4 
			 */
			if(depth <=4) { 
				//commented out as full title is read in end element method 18-10-2011
				//_item.setTitle(theString); 
			////////Log.i(tag, " Item title read : "+_item.getTitle());
			currentState=0;			
			break;} else {
			
				
			////////Log.i(tag, " title read is not of item");
			currentState=0;
			break;
			}
			
		case RSS_LINK:
			_item.setLink(theString);
			////////Log.i(tag, " Item link read : "+_item.getLink());
			currentState=0;
			break;
		
	/*	case RSS_CATEGORY:
			_item.setCategory(theString);
			//////Log.i(tag, " Item category read : "+_item.getCategory());
			currentState=0;
			break;
		*/
		/*	
		case RSS_DESCRIPTION:
			//_item.setDescription(theString);
			
			currentState=0;
			break;
			*/
		case RSS_FEED_PUBDATE:
			_feed.setPubDate(theString);
			//////Log.i(tag, " Item PubDate read : "+_item.getPubDate());
			currentState=0;
			break;
			
		case RSS_ITEM_PUBDATE:
			_item.setUpdated(theString);
			currentState = 0;
			break;
		}
		}
		
	}
	

