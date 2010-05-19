package org.oep.markov.test;

import java.util.ArrayList;

import org.oep.markov.MarkovChain;

public class StringTest {
	public static void main(String [] args) {
		String phrase1[] = "She sells sea shells by the sea shore".split(" ");
		String phrase2[] = "A sea shell found by the beach sells for quite a bit".split(" ");
		
		MarkovChain<String> chain = new MarkovChain<String>();
		chain.addPhrase(phrase1);
		chain.addPhrase(phrase2);
		
		long start = System.currentTimeMillis();
		ArrayList<String> phrase = chain.makePhrase();
		long dt = System.currentTimeMillis() - start;
		
		for(String word : phrase) {
			System.out.print(word + " ");
		}
		System.out.println();
		
		System.out.println(dt + " ms");
	}
}
