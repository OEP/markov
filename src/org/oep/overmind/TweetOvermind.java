package org.oep.overmind;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import org.oep.markov.MarkovSentence;
import org.oep.markov.test.TwitterTest;

import twitter4j.Twitter;
import twitter4j.TwitterException;

import com.crepezzi.tweetstream4j.TweetRiver;
import com.crepezzi.tweetstream4j.TwitterStream;
import com.crepezzi.tweetstream4j.TwitterStreamConfiguration;
import com.crepezzi.tweetstream4j.TwitterStreamHandler;
import com.crepezzi.tweetstream4j.types.SDeletion;
import com.crepezzi.tweetstream4j.types.SLimit;
import com.crepezzi.tweetstream4j.types.STweet;

public class TweetOvermind implements TwitterStreamHandler {
	
	private MarkovSentence mMarkov = new MarkovSentence();
	
	String regex_url = "https?://([-\\w\\.]+)+(:\\d+)?(/([\\w/_\\.]*(\\?\\S+)?)?)?";
	String regex_mentions = "[@][A-Za-z0-9]+";
	
	public static void main(String [] args) {
		Properties prefs = new Properties();
		
		// Load up the preferences
		try { prefs.loadFromXML(new FileInputStream(new File("prefs.xml"))); }
		catch(Exception e) {
			System.err.println("ERROR: " + e.getMessage());
		}
		
		// Grab up the username/password
		String username = (prefs.containsKey("username")) ? prefs.getProperty("username") : "";
		String password = (prefs.containsKey("password")) ? prefs.getProperty("password") : "";
		
		TweetOvermind overmind = new TweetOvermind();
		TwitterStreamConfiguration tws = new TwitterStreamConfiguration(username, password);
		TwitterStream ts = TweetRiver.sample(tws, overmind);
		Twitter twitter = new Twitter(username, password);
		
		
		
		(new Thread(ts)).start();
		
		while(true) {
			try {Thread.sleep(60 * 60 * 1000); }
			catch(Exception e) { break; };
			
			String tweet;
			int i = 0;
			
			do {
				tweet = overmind.mMarkov.makeSentence();
				i++;
			} while(tweet.length() <= 140 && i < 10);
			
			
			tweet = tweet.substring(0, Math.min(140, tweet.length()));
			
			System.out.println("Tweeting: " + overmind.mMarkov.makeSentence());
			System.out.println("\tnode count: " + overmind.mMarkov.getNodeCount());
			System.out.println("\tedge count: " + overmind.mMarkov.getEdgeCount());
			
			try {
				twitter.updateStatus(tweet);
			} catch (TwitterException e) {
				System.err.println("Error posting tweet: " + e.getMessage());
			}
			
		}
	}

	@Override
	public void addDeletion(SDeletion d) {
		// Intentionally blank
	}

	@Override
	public void addLimit(SLimit l) {
		// Intentionally blank
	}

	@Override
	public void addTweet(STweet t) {
		String tweet = t.getText();
		tweet = tweet.replaceAll(regex_url, "");
		tweet = tweet.replaceAll(regex_mentions, "");
		mMarkov.parseSentence(tweet);
	}
}
