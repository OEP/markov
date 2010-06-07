package org.oep.overmind;

import java.io.File;
import java.util.Random;

import twitter4j.TwitterException;

public class OvermindMain {
	/** Randomized multiplier for pausing. Multiply by "SECONDS" to get the maximum time between tweets. */
	public static final int MULTIPLIER = 5;
	
	/** Multiply by MULTIPLIER to get maximum time between tweets */
	public static final int SECONDS = 1440;
	
	/** Size of our tuple in our MarkovChain */
	public static final int ORDER = 2;
	
	/** Number of chains we want TweetOvermind to alternate between. */
	public static final int CHAINS = 10;
	
	/** Which letters TweetOvermind is allowed to learn */
	public static final String TOKEN_CHARS = "_&'?.;!:" + TweetOvermind.TWITTER_ALPHANUMERIC;
	
	public static void main(String args[]) {
		if(args.length == 0) {
			printUsage();
			System.exit(1);
		}
		
		
		TweetOvermind overmind = 
			TweetOvermind.makeOvermind(new File(args[0]), ORDER, CHAINS, TOKEN_CHARS, null);
		
		System.out.printf("Created instance of TweetOvermind using username '%s'\n", overmind.getUsername());
		
		Random rng = new Random();
		overmind.startSample();
		
		while(true) {
			long millis = 0;
			
			
			// Calculate how long to pause.
			for(int i = 0; i < MULTIPLIER; i++) {
				millis += rng.nextInt(SECONDS * 1000);
			}
			
			// Pause to learn a while.
			System.out.printf("Learning for %d minutes, %d seconds.\n", (millis/(60 * 1000)), ((millis / 1000) % 60));
			try { Thread.sleep(millis);	}
			catch (InterruptedException e) { System.exit(1); }
			
			// Try to post a tweet.
			try {
				overmind.updateStatus( overmind.makeTweet() );
			} catch (TwitterException e) {
				e.printStackTrace();
			}
			
			// Cycle our chains.
			overmind.cycleChains();
		}
		
	}
	
	public static void printUsage() {
		System.out.println("Usage: java OvermindMain [prefs-file]");
	}
}
