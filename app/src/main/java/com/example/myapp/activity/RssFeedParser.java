package com.example.myapp.activity;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;

import android.util.Log;
import android.util.Xml;

public class RssFeedParser {

	static final String TITLE = "title";
	static final String ITEM = "item";
	static final String PUB_DATE = "pubDate";
	static final String DESCRIPTION = "description";
	static final String LINK = "link";

	static final String CHANNEL = "channel";

	final URL url;

	public RssFeedParser(String feedUrl) {
		try {
			this.url = new URL(feedUrl);
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}

	public List<Message> parse() {
		List<Message> messages = null;
		XmlPullParser parser = Xml.newPullParser();
		try {
			// auto-detect the encoding from the stream
			parser.setInput(this.getInputStream(), null);
			int eventType = parser.getEventType();
			Message currentMessage = null;
			boolean done = false;
			while (eventType != XmlPullParser.END_DOCUMENT && !done){
				String name = null;
				switch (eventType){
				case XmlPullParser.START_DOCUMENT:
					messages = new ArrayList<Message>();
					break;
				case XmlPullParser.START_TAG:
					name = parser.getName();
					if (name.equalsIgnoreCase(ITEM)){
						currentMessage = new Message();
					} else if (currentMessage != null){
						if (name.equalsIgnoreCase(LINK)){
							currentMessage.setLink(parser.nextText());
						} else if (name.equalsIgnoreCase(DESCRIPTION)){
							currentMessage.setDescription(parser.nextText());
						} 
						else if (name.equalsIgnoreCase(PUB_DATE)){
							currentMessage.setDate(parser.nextText());
						} 
						else if (name.equalsIgnoreCase(TITLE)){
							currentMessage.setTitle(parser.nextText());
						}	
					}
					break;
				case XmlPullParser.END_TAG:
					name = parser.getName();
					if (name.equalsIgnoreCase(ITEM) && currentMessage != null){
						messages.add(currentMessage);
					} else if (name.equalsIgnoreCase(CHANNEL)){
						done = true;
					}
					break;
				}
				eventType = parser.next();
			}
		} catch (Exception e) {
			Log.e("Exception Occured", e.getMessage(), e);
			throw new RuntimeException(e);
		}
		return messages;
	}

	protected InputStream getInputStream() {
		try {
			return url.openConnection().getInputStream();
		}
		catch (IOException ioe) {
			throw new RuntimeException(ioe);
		} catch (Exception e) {
			Log.e("Exception Occured", e.getMessage(), e);
			throw new RuntimeException(e);
		}
	}
}
