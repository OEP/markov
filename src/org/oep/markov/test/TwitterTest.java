package org.oep.markov.test;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.InvalidPropertiesFormatException;
import java.util.Properties;
import java.util.Random;

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
	final MarkovSentence markov = new MarkovSentence(2);
	
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
		
		Thread bg = new Thread(ts);
		bg.start();
		
		System.out.println("Collecting tweets...");

		Random r = new Random();
		long millis = 0;
		
		// Pause between 0 and 60 minutes, more centric around 30.
		for(int i = 0; i < 10; i++) {
			millis += r.nextInt(360 * 1000);
		}
		
		System.out.printf("Pausing for approximately %d minutes,  %d seconds\n", millis / 60000, (millis % 60000) / 1000);
		
		try {Thread.sleep(millis); }
		catch(Exception e) {  };
		
		System.out.println("Outputting: " + ttest.markov.makeSentence());
		bg.interrupt();
		
		System.out.println("Exporting brain");
		try {
			ttest.markov.export("brain.xml");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		System.exit(0);
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
