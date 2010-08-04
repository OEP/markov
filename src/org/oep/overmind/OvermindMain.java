package org.oep.overmind;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.InvalidPropertiesFormatException;
import java.util.Random;

import twitter4j.TwitterException;

public class OvermindMain {
	/** Which letters TweetOvermind is allowed to learn */
	public static final String TOKEN_CHARS = "_&'?.;!:" + TweetOvermind.TWITTER_ALPHANUMERIC;
	
	public static void main(String args[]) {
		if(args.length < 5 || args.length > 6) {
			printUsage();
			System.exit(1);
		}
		
		int order = Integer.parseInt(args[1]);
		int chains = Integer.parseInt(args[2]);
		int multiplier = Math.max(1, Integer.parseInt(args[3]));
		int seconds = Math.max(1, Integer.parseInt(args[4]));
		
		int iterations = 0;
		
		try {
			iterations = (args.length >= 6) ? Integer.parseInt(args[5]) : 0;
		}
		catch(NumberFormatException e) {
			System.err.println(args[5] + " doesn't look like an integer. Looping infinitely...");
			iterations = 0;
		}
		
		TweetOvermind overmind;
		try {
			overmind = TweetOvermind.makeOvermind(new File(args[0]), order, chains, TOKEN_CHARS, null);
		} catch (Exception e) {
			System.err.println("Error making a TweetOvermind: " + e.getMessage());
			return;
		}
		
		System.out.printf("Created instance of TweetOvermind using username '%s'\n", overmind.getUsername());
		
		Random rng = new Random();
		overmind.startSample();
		
		System.out.println((iterations > 0) ? "Looping for " + iterations + " iterations"
				: "Looping infinitely...");
		
		int count = 0;
		while(iterations <= 0 || count < iterations) {
			long millis = 0;
			
			
			// Calculate how long to pause.
			for(int i = 0; i < multiplier; i++) {
				millis += rng.nextInt(seconds * 1000);
			}
			
			// Respawn the thread if needed.
			if(overmind.isLearning() == false) {
				overmind.startSample();
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
			
			count++;
		}
		
		overmind.stopLearning();
		System.out.println("Finished");
		System.exit(0);
	}
	
	public static void printUsage() {
		System.out.println("Usage: java OvermindMain prefs-file order chains multiplier seconds [iterations]");
	}
}
