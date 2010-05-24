package org.oep.markov.test;

import java.util.ArrayList;

import org.oep.markov.MarkovChain;

public class StringTest {
	public static void main(String [] args) {
		String phrase1[] = "she sells sea shells by the sea shore fool".split(" ");
		String phrase2[] = "a sea shell found by the beach sells for quite a bit".split(" ");
		String phrase3[] = "a sea monster sells sea shells underneath the beach house".split(" ");
		String phrase4[] = "sea shells underneath the cabinet are meant for shelly to sell sea shore".split(" ");
		
		MarkovChain<String> chain = new MarkovChain<String>(2);
		chain.addPhrase(phrase1);
		chain.addPhrase(phrase2);
		chain.addPhrase(phrase3);
		chain.addPhrase(phrase4);
		
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
