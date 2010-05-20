package org.oep.markov.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.InvalidPropertiesFormatException;
import java.util.Properties;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.oep.markov.MarkovSentence;
import org.oep.twitter.api.MarkovHandler;
import org.xml.sax.SAXException;

public class TwitterTest {
	static Properties properties = new Properties();
	public static void main(String [] args) {
		File prefs = new File("prefs.xml");
		final MarkovSentence mk = new MarkovSentence();
		
		try {
			properties.loadFromXML(new FileInputStream(prefs));
		} catch (InvalidPropertiesFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		String username = ((properties.containsKey("username")) ? properties.getProperty("username") : "");
		String password = ((properties.containsKey("password")) ? properties.getProperty("password") : "");
		
		String url = String.format("http://%s:%s@stream.twitter.com/1/statuses/sample.xml",username,password);
		url = "http://twitter.com/statuses/public_timeline.xml";
		
		System.out.println("Waiting a tick...");
		try { Thread.sleep(600000);}
		catch(InterruptedException e) { }
		
		try {
			final URL apiURL = new URL(url);
			Thread t = new Thread() {
				public void run() {
					MarkovHandler mh = new MarkovHandler();
					mh.setMarkovObject(mk);
					SAXParserFactory factory = SAXParserFactory.newInstance();
					SAXParser parser;
					while(true) {
						try {
								parser = factory.newSAXParser();
								parser.parse(apiURL.openStream(), mh);
						} catch (ParserConfigurationException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (SAXException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
						try { Thread.sleep(10000); }
						catch(InterruptedException e) { }
					}
				}
			};
			t.start();
			
			while(true) {
				try {
					Thread.sleep(60000);
				} catch (InterruptedException e) {
					break;
				}
				System.out.println("Out: " + mk.makeSentence());
			}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
