package org.oep.overmind;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Random;
import java.util.Scanner;

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
	
	public static final boolean DEBUG = true;
	
	private ArrayList<MarkovSentence> mChains = new ArrayList<MarkovSentence>();
	
	private String mUsername;
	private String mPassword;
	private String mTerminateChars, mTokenChars;
	private final int mOrder;
	private final int mCount;
	
	private Twitter mAccount;
	
	private Thread mLearnThread = null;
	
	String regex_url = "https?://([-\\w\\.]+)+(:\\d+)?(/([\\w/_\\.]*(\\?\\S+)?)?)?";
	String regex_mentions = "[@][A-Za-z0-9]+";
	
	public TweetOvermind(String user, String password, int order, int chains,
			String tokenChars, String terminateChars) {
		mUsername = user;
		mPassword = password;
		mAccount = new Twitter(mUsername, mPassword);
		mOrder = Math.max(1, order);
		mCount = Math.max(1, chains);
		mTokenChars = tokenChars;
		mTerminateChars = terminateChars;
		cycleChains();
	}
	
	public static void main(String [] args) {
		int order = 2;
		int chains = 10;
		String tokenChars = ".?;!" + TWITTER_ALPHANUMERIC;
		TweetOvermind overmind =
			TweetOvermind.makeOvermind(new File("prefs.xml"), order, chains, tokenChars, null);
		overmind.startSample();
		Scanner keyboard = new Scanner(System.in);
		
		while(true) {
			System.out.print("You said: ");
			
			while(keyboard.hasNextLine() == false) { }
			
			String line = keyboard.nextLine();
			String lowercase = line.toLowerCase();
			
			if(lowercase.compareTo("!next") == 0) {
				overmind.cycleChains();
				System.out.println("Cycled chains.");
			}
			else {
//				overmind.parseSentence(line);
				System.out.printf("%s says: ", overmind.toString());
				System.out.println(overmind.makeTweet());
			}
		}
	}
	
	public void parseSentence(String tweet) {
		for(MarkovSentence chain : mChains) {
			chain.parseSentence(tweet);
		}
	}
	
	public void cycleChains() {
		if(mChains.size() >= mCount) {
			System.out.println("Removed a chain!");
			mChains.remove(0);
		}
		MarkovSentence chain = new MarkovSentence(mOrder);
		chain.setTokenChars(mTokenChars);
		chain.setTerminateChars(mTerminateChars);
		mChains.add(chain);
	}
	
	public String getUsername() {
		return mUsername;
	}
	
	public String makeTweet() {
		MarkovSentence chain = mChains.get(0);
		String sentence = chain.makeSentence();
		int i = 0;
		
		while(i != Integer.MAX_VALUE && sentence.length() > 140) {
			sentence = chain.makeSentence();
			i++;
		}
		
		if(i > 0) System.out.println(i + " failures.");
		
		return sentence;
	}
	
	public int getNodeCount() {
		MarkovSentence chain = mChains.get(0);
		
		if(chain == null) return 0;
		
		return chain.getNodeCount();
	}
	
	public int getEdgeCount() {
		MarkovSentence chain = mChains.get(0);
		return chain.getEdgeCount();
	}
	
	public void setTerminateChars(String chars) {
		mTerminateChars = chars;
	}
	
	public void setTokenChars(String chars) {
		mTokenChars = chars;
	}
	
	public boolean isLearning() {
		return mLearnThread != null && mLearnThread.isAlive();
	}
	
	public boolean startSample() {
		TwitterStreamConfiguration tws = new TwitterStreamConfiguration(mUsername, mPassword);
		TwitterStream ts = TweetRiver.sample(tws, this);
		
		if(isLearning() == false) {
			mLearnThread = new Thread(ts);
			mLearnThread.start();
			return true;
		}
		
		return false;
	}
	
	public boolean stopLearning() {
		if(isLearning()) {
			mLearnThread.interrupt();
			return true;
		}
		return false;
	}
	
	public void clear() {
		mChains.clear();
		cycleChains();
	}
	
	public void updateStatus(String tweet) throws TwitterException {
		if(tweet.length() > 140) {
			tweet = tweet.substring(0, 140);
		}
		mAccount.updateStatus(tweet);
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
//		tweet = tweet.replaceAll(regex_mentions, "");
		parseSentence(tweet);
	}
	
	public static TweetOvermind makeOvermind(File xml, int order, int chains, String tokenChars, String terminateChars) {
		Properties prefs = new Properties();
		
		// Load up the preferences
		try { prefs.loadFromXML(new FileInputStream(new File("prefs.xml"))); }
		catch(Exception e) { return null; }
		
		
		// Grab up the username/password
		String username = (prefs.containsKey("username")) ? prefs.getProperty("username") : "";
		String password = (prefs.containsKey("password")) ? prefs.getProperty("password") : "";
		
		return new TweetOvermind(username, password, order, chains, tokenChars, terminateChars);
	}
	
	public String toString() {
		return String.format("Overmind<%d,%d>(%d)", mOrder, mCount, getNodeCount());
	}
		
	
	public static final String TWITTER_ALPHANUMERIC = "@#ABCDEFGHIJKLMNOPQRUSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
}
