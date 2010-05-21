package org.oep.markov;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Adds functionality to the MarkovChain class which 
 * tokenizes a String argument for use in the Markov graph.
 * @author OEP
 *
 */
public class MarkovSentence extends MarkovChain<String> {
	/** Buffer to use when parsing our input source */
	private ArrayList<String> mPhraseBuffer = new ArrayList<String>();
	
	/**
	 * Converts a String to a ByteArrayInputStream and parses.
	 * @param s String object to parse.
	 */
	public void parseSentence(String s) {
		byte buf[] = s.getBytes();
		parseSentence(new ByteArrayInputStream(buf));
	}
	
	/**
	 * Stream-safe method to parse an InputStream.
	 * @param is InputStream to parse
	 */
	public void parseSentence(InputStream is) {
		if(is == null) return;
		
		int read;
		try {
			
			// Check and see if we can read anything.
			while((read = is.available()) != 0) {
				
				// Allocate a buffer to read into
				byte buf[] = new byte[read];
				
				// Read bytes and 
				int actual = is.read(buf, 0, read);
				
				// We will push our tokens into this thingy.
				StringBuffer sb = new StringBuffer();
				
				// Loop through the bytes we just read.
				for(int i = 0; i < actual; i++) {
					
					// If it is whitespace, end the token that we've been making and push it into the
					// phrase buffer.
					if(Character.isWhitespace((char)buf[i])) {
						if(sb.length() > 0) {
							pushWord(sb.toString());
							sb = new StringBuffer();
						}
					}
					// If it is a printable character, push it into our token buffer.
					else if(32 <= buf[i] && buf[i] < 127) {
						sb.append((char)buf[i]);
					}
					// If it is nonprintable, end the token and push it.
					else  {
						if(sb.length() > 0) {
							pushWord(sb.toString());
							sb = new StringBuffer();
						}
					}
				}
			}
			
			// Add the sentence to our MarkovChain
			flushBuffer();
		} catch (IOException e) {
			// Intentionally blank.
		}
		finally {
			try { if(is != null) is.close();	}
			catch (IOException e) { /* intentionally blank*/ }
		}
	}
	
	/**
	 * Make our generated Markov phrase into a String
	 * object that is more versatile.
	 * @return String of our Markov phrase
	 */
	public String makeSentence() {
		// Get our phrase as an unwieldy ArrayList
		ArrayList<String> phrase = makePhrase();
		
		// Get our StringBuffer ready and calculate the size beforehand BECAUSE IT'S SO MUCH FASTER.
		StringBuffer sb = new StringBuffer();
		int sz = phrase.size();
		
		// Iterate over the ArrayList.
		for(int i = 0; i < sz; i++) {
			// Grab our word.
			String word = phrase.get(i);
			
			// Capitalize if this is the first word
			if(i == 0) word = word.substring(0, 1).toUpperCase() + word.substring(1);
			
			// Add a space if it isn't the last word.
			if(i != sz - 1) word = word + " ";
			
			sb.append(word);
		}
		
		return sb.toString();
	}
	
	/**
	 * Alias method to help us flush the buffer into the Markov engine.
	 */
	private void flushBuffer() {
		if(mPhraseBuffer.size() == 0) return;
		
		this.addPhrase(mPhraseBuffer);
		mPhraseBuffer.clear();
	}
	
	/**
	 * Alias method to add a word to the phrase buffer.
	 * @param word the word to add
	 */
	private void pushWord(String word) {
		mPhraseBuffer.add(word.toLowerCase());
	}
	
}