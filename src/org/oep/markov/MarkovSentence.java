package org.oep.markov;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Adds functionality to the MarkovChain class which 
 * parses a String argument according to the punctuation
 * rules of normal English.
 * @author OEP
 *
 */
public class MarkovSentence extends MarkovChain<String> {
	private ArrayList<String> mPhraseBuffer = new ArrayList<String>();
	
	public void parseSentence(String s) {
		byte buf[] = s.getBytes();
		parseSentence(new ByteArrayInputStream(buf));
	}
	
	public void parseSentence(InputStream is) {
		if(is == null) return;
		
		int read;
		try {
			while((read = is.available()) != 0) {
				byte buf[] = new byte[read];
				int actual = is.read(buf, 0, read);
				
				StringBuffer sb = new StringBuffer();
				for(int i = 0; i < actual; i++) {
					if(Character.isWhitespace((char)buf[i])) {
						if(sb.length() > 0) {
							pushWord(sb.toString());
							sb = new StringBuffer();
						}
					}
//					else if(buf[i] == '!' || buf[i] == '.' || buf[i] == '?' || buf[i] == ';') {
//						if(sb.length() > 0) {
//							pushWord(sb.toString());
//							sb = new StringBuffer();
//						}
//						flushBuffer();
//					}
					else if(32 <= buf[i] && buf[i] < 127) {
						sb.append((char)buf[i]);
					}
					else  {
						if(sb.length() > 0) {
							pushWord(sb.toString());
							sb = new StringBuffer();
						}
						flushBuffer();
					}
				}
			}
			
			flushBuffer();
		} catch (IOException e) {
		}
		finally {
			try {
				is.close();
			} catch (IOException e) {
			}
			
		}
	}
	
	public String makeSentence() {
		ArrayList<String> phrase = makePhrase();
		StringBuffer sb = new StringBuffer();
		int sz = phrase.size();
		for(int i = 0; i < sz; i++) {
			String word = phrase.get(i);
			
			// Capitalize!
			if(i == 0) word = word.substring(0, 1).toUpperCase() + word.substring(1);
			if(i != sz - 1) word = word + " ";
			
			sb.append(word);
		}
		
		return sb.toString();
	}
	
	private void flushBuffer() {
		if(mPhraseBuffer.size() == 0) return;
		
		this.addPhrase(mPhraseBuffer);
		mPhraseBuffer.clear();
	}
	
	private void pushWord(String word) {
		mPhraseBuffer.add(word.toLowerCase());
	}
	
}