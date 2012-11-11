package com.saaranga.wikikannada;

import java.io.StringReader;
import java.net.URL;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

/**
 * Fetching and parsing of the feed is done here
 * Has two methods:
 *  1. get url > fetch feed > return RSSFeed object
 *  2. get feed in the form of a string > parse >return RSSFeed object 
 * 
 * @author supreeth
 *
 */
public class GetRssFeedClass {

	/**
	 * 
	 * @param feed_url
	 * @return RSSFeed object
	 */
	public RSSFeed getFeed(String feed_url) {

		System.out.println("getFeedMethod started");

		try{  
			//set up url
			URL url=new URL(feed_url);

			//create the factory 
			SAXParserFactory factory = SAXParserFactory.newInstance();

			//create the parser
			SAXParser parser= factory.newSAXParser();

			//create the reader (scanner)
			XMLReader xmlreader=parser.getXMLReader();

			//instantiate the our handler
			RSSHandler theRSSHandler=new RSSHandler();

			//assign our handler
			xmlreader.setContentHandler(theRSSHandler);

			//get our data through the url class
			InputSource is= new InputSource(url.openStream());

			//perform synchronous parse
			xmlreader.parse(is);

			//get the results - should be a fully populated RSS Feed instance, or null or an error
			//progress_status=1;
			return theRSSHandler.getFeed();
		}
		catch (Exception ee){
			//if there is any problem simply return null 
			ee.printStackTrace();
			return null;
		}
	}  

	/**
	 * 
	 * @param feed  -  string 
	 * @return RSSFeed object
	 */
	public RSSFeed parseFeed(String feed) {

		System.out.println("getFeedMethod started");

		try{

			//create the factory 
			SAXParserFactory factory = SAXParserFactory.newInstance();

			//	create the parser
			SAXParser parser= factory.newSAXParser();

			//create the reader (scanner)
			XMLReader xmlreader=parser.getXMLReader();

			//instantiate the our handler
			RSSHandler theRSSHandler=new RSSHandler();

			//assign our handler
			xmlreader.setContentHandler(theRSSHandler);

			//get our data through the url class
			InputSource is= new InputSource(new StringReader(feed));

			//perform synchronous parse
			xmlreader.parse(is);

			//get the results - should be a fully populated RSS Feed instance, or null or an error
			//progress_status=1;
			return theRSSHandler.getFeed();
		}
		catch (Exception ee){
			//if there is any problem simply return null 
			return null;
		}
	}

}
