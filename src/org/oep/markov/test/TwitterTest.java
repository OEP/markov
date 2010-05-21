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

import com.crepezzi.tweetstream4j.TweetRiver;
import com.crepezzi.tweetstream4j.TwitterStream;
import com.crepezzi.tweetstream4j.TwitterStreamConfiguration;
import com.crepezzi.tweetstream4j.TwitterStreamHandler;
import com.crepezzi.tweetstream4j.types.SDeletion;
import com.crepezzi.tweetstream4j.types.SLimit;
import com.crepezzi.tweetstream4j.types.STweet;

public class TwitterTest implements TwitterStreamHandler {
	static Properties properties = new Properties();
	final MarkovSentence markov = new MarkovSentence();
	
	String regex_url = "https?://([-\\w\\.]+)+(:\\d+)?(/([\\w/_\\.]*(\\?\\S+)?)?)?";
	
	public static void main(String [] args) {
		File prefs = new File("prefs.xml");
		
		try { properties.loadFromXML(new FileInputStream(prefs));	}
		catch (Exception e) { System.err.println("Error loading prefs..."); }
		
		String username = ((properties.containsKey("username")) ? properties.getProperty("username") : "");
		String password = ((properties.containsKey("password")) ? properties.getProperty("password") : "");
	
		TwitterTest ttest = new TwitterTest();
		TwitterStreamConfiguration tws = new TwitterStreamConfiguration(username, password);
		TwitterStream ts = TweetRiver.sample(tws, ttest);
		
		(new Thread(ts)).start();
		
		System.out.println("Collecting tweets...");
		try {Thread.sleep(10000); }
		catch(Exception e) {  };
		
		System.out.println("Outputting: " + ttest.markov.makeSentence());
		
		System.out.println("Exporting brain");
		try {
			ttest.markov.export("brain.xml");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public void addDeletion(SDeletion d) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void addLimit(SLimit l) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void addTweet(STweet t) {
//		System.out.println(String.format("%s (by %s)", t.getText(), t.getUser().getScreenName()));
		String text = t.getText();
		String filtered = text.replaceAll(regex_url, "");
//		System.out.println(text + " --> " + filtered);
		markov.parseSentence(filtered);
	}
}
