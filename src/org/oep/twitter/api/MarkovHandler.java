package org.oep.twitter.api;

import java.util.Stack;

import org.oep.markov.MarkovSentence;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

public class MarkovHandler extends DefaultHandler {
	private MarkovSentence mMarkov;
	
	private Stack<String> mTextStack = new Stack<String>();
	private String mWorkingText = new String();
	
	public void setMarkovObject(MarkovSentence markov) {
		mMarkov = markov;
	}
	
	public void startDocument() {
		mTextStack.clear();
		mWorkingText = new String();
	}
	
	public void endDocument() {
		
	}
	
	public void startElement(String uri, String qname, String lname, Attributes atts) {
		mTextStack.push(mWorkingText);
		mWorkingText = new String();
	}
	
	public void endElement(String uri, String qname, String lname) {
		if(mMarkov != null && lname.compareTo("text") == 0) {
			mMarkov.parseSentence(mWorkingText);
		}
		
		mWorkingText = mTextStack.pop();
	}
	
	public void characters( char ch[], int start, int length ) {
		mWorkingText += new String(ch,start,length);
	}
}
