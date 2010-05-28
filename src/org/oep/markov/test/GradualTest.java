package org.oep.markov.test;

import org.oep.markov.MarkovChain;

/**
 * Tests the gradual chaining methods of the data structure.
 * @author OEP
 *
 */
public class GradualTest {
	public static void main(String [] args) {
		String shortPhrase1[] = "foo bar bing bang".split(" ");
		String shortPhrase2[] = "foo eat bar bang foo".split(" ");
		
		String longPhrase[] = "a b c d e f g h i j k l m n o p q r s t u v w x y z".split(" ");
		String ragamuffin[] = "i a q a v a z a d a".split(" ");
		
		MarkovChain<String> longChain = new MarkovChain<String>(1);
		MarkovChain<String> shortChain = new MarkovChain<String>(1);
		
		longChain.addPhrase(longPhrase);
		longChain.addPhrase(ragamuffin);
		shortChain.addPhrase(shortPhrase1);
		shortChain.addPhrase(shortPhrase2);
		
		String shorty, longy;
		while((longy = longChain.next()) != null) {
			shorty = shortChain.nextLoop();
			System.out.printf("%s (%s)\n", longy, shorty);
		}
		
		longChain.reset();
		
		while((longy = longChain.next()) != null) {
			System.out.printf("%s", longy);
		}
		System.out.println();
	}
}
