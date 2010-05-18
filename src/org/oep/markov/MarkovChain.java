package org.oep.markov;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Vector;

public class MarkovChain<T extends Comparable<T>> {
	private HashMap<T, MarkovChain<T>.Node> mNodes =
		new HashMap<T, MarkovChain<T>.Node>();
	private Random RNG = new Random();
	
	private Node mHeader = new Node();
	private Node mTrailer = new Node();
	
	public void addPhrase(T phrase[]) {
		if(phrase == null || phrase.length == 0) return;
		
		Node current = mHeader;
		for(int i = 0; i < phrase.length; i++) {
			Node n = findOrCreate(phrase[i]);
			current.promote(n);
			current = n;
		}
		current.promote(mTrailer);
	}
	
	public T[] makePhrase() {
		Node current = mHeader.next();
		
		ArrayList<T> phrase = new ArrayList<T>();
		
		while(current != null && current != mTrailer) {
			phrase.add(current.data);
			current = current.next();
		}
		
		return (T[]) phrase.toArray();
	}
	
	
	private Node findOrCreate(T data) {
		Node n = mNodes.get(data);
		
		if(n == null) {
			n = new Node(data);
			mNodes.put(data, n);
		}
		
		return n;
	}
	
	public class Node implements Comparable<Node> {
		public T data;
		private Vector<Edge> mEdges = new Vector<Edge>();
		
		public Node() {
			
		}
		
		public Node(T d) {
			data = d;
		}
		
		public void promote(Node n) {
			for(int i = 0; i < mEdges.size(); i++) {
				Edge e = mEdges.elementAt(i);
				if(e.node.compareTo(n) == 0) {
					e.weight++;
					return;
				}
			}
			
			mEdges.add(new Edge(n));
		}
		
		public Node next() {
			if(mEdges.size() == 0) return null;
			
			int totalScore = 0;
			for(int i = 0; i < mEdges.size(); i++) totalScore += mEdges.get(i).weight;
			
			int r = RNG.nextInt(totalScore);
			int current = 0;
			
			for(int i = 0; i < mEdges.size(); i++) {
				Edge e = mEdges.get(i);
				if(r >= current && r < current + e.weight) {
					return e.node;
				}
			}
			
			throw new IllegalArgumentException("Something terrible happened.");
		}
		
		private class Edge {
			public Edge(Node n) {
				node = n;
				weight = 1;
			}
			
			Node node;
			int weight = 1;
		}
		
		public int compareTo(Node other) {
			return data.compareTo(other.data);
		}
	}
}
