package org.oep.markov;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;

import org.oep.markov.MarkovChain.Node.Edge;

/**
 * Adds functionality to the MarkovChain class which 
 * tokenizes a String argument for use in the Markov graph.
 * @author OEP
 *
 */
public class MarkovSentence extends MarkovChain<String> {
	/** Buffer to use when parsing our input source */
	private ArrayList<String> mPhraseBuffer = new ArrayList<String>();
	
	private String mTokenChars = null;
	private String mTerminateChars = null;
	
	public MarkovSentence(int tupleLength) {
		super(tupleLength);
	}
	
	public void setTokenChars(String chars) {
		mTokenChars = chars;
	}
	
	public void setTerminateChars(String chars) {
		mTerminateChars = chars;
	}
	
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
			// We will push our tokens into this thingy.
			StringBuffer sb = new StringBuffer();
			
			// Check and see if we can read anything.
			while((read = is.available()) != 0) {
				
				// Allocate a buffer to read into
				byte buf[] = new byte[read];
				
				// Read bytes and 
				int actual = is.read(buf, 0, read);
				
				
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
					else if(isEmpty(mTokenChars) && 32 <= buf[i] && buf[i] < 127) {
						sb.append((char)buf[i]);
					}
					else if(!isEmpty(mTokenChars) && mTokenChars.indexOf(buf[i]) >= 0) {
						sb.append((char) buf[i]);
					}
					else if(!isEmpty(mTerminateChars) && mTerminateChars.indexOf(buf[i]) >= 0
							&& mPhraseBuffer.size() > 0) {
						sb.append((char)buf[i]);
						pushWord(sb.toString());
//						System.out.printf("Pushing '%s' do to punc. mark '%c'\n", sb.toString(), buf[i]);
						
						sb = new StringBuffer();
						flushBuffer();
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
			
			// Before we flush, make sure there's nothing left in the buffer
			if(sb.length() > 0) {
				pushWord(sb.toString());
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
	
	private boolean isEmpty(String s) {
		return s == null || s.length() == 0;
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
	
	public void export(String filename) throws FileNotFoundException {
		File outfile = new File(filename);
		OutputStream os = new FileOutputStream(outfile);
		PrintStream p = new PrintStream(os);
		
		p.append("<markov>\n");
		
		p.append("\t<header>\n");
		writeEdges(p,mHeader);
		p.append("\t</header>\n");

		ArrayList<Node> nodes = new ArrayList<Node>(mNodes.values());
		
		for(Node n : nodes) {
			p.append(String.format("\t<node name='%s'>\n", n.data));
			writeEdges(p,n);
			p.append("\t</node>\n");
		}
		
		p.append("</markov>\n");
	}
	
	/**
	 * Alias to write the edges of a node.
	 * @param p
	 */
	private void writeEdges(PrintStream p, Node n) {
		for(int i = 0; i < n.mEdges.size(); i++) {
			Node.Edge e = n.mEdges.get(i);
			
			if(e.node.data != null)
				p.append(String.format("\t\t<edge name='%s' />\n", e.node.data));
			else {
				p.append("\t\t<edge trailer='true' />\n");
			}
		}
	}
	
	/**
	 * Alias method to help us flush the buffer into the Markov engine.
	 */
	private void flushBuffer() {
		if(mPhraseBuffer.size() == 0) return;
//		System.out.println("Adding: " + mPhraseBuffer.toString());
		this.addPhrase(mPhraseBuffer);
		mPhraseBuffer.clear();
	}
	
	/**
	 * Alias method to add a word to the phrase buffer.
	 * @param word the word to add
	 */
	private void pushWord(String word) {
		if(word == null || word.length() == 0) return;
		mPhraseBuffer.add(word.toLowerCase());
	}
	
	
	public static final String ALPHANUMERIC = "ABCDEFGHIJKLMNOPQRUSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
	public static final String PUNCTUATION = ";.?!";
}