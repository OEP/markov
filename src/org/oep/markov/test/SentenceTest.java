package org.oep.markov.test;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.oep.markov.MarkovSentence;

public class SentenceTest {
	
	public static void main(String [] args) {
		MarkovSentence ms = new MarkovSentence( 1 );
		
		URL url;
		try {
			url = new URL("http://students.mint.ua.edu/~pmkilgo/tmp/21597.txt");
			ms.parseSentence(url.openStream());
			
			System.out.println(ms.makeSentence());
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
}
