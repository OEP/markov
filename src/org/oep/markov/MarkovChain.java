package org.oep.markov;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Vector;

/**
 * A class for generating a Markov phrase out of some sort of
 * arbitrary comparable data.
 * @author pkilgo
 *
 * @param <T> the type of data you would like to generate phrases for (e.g., <code>java.lan
 */
public class MarkovChain<T extends Comparable<T>> {
	
	/** HashMap to help us resolve data to the node that contains it */
	protected HashMap<T, MarkovChain<T>.Node> mNodes =
		new HashMap<T, MarkovChain<T>.Node>();
	
	/** Nodes use this to find the next node */
	private Random RNG = new Random();
	
	/** Node that marks the beginning of a phrase. All Markov phrases start here. */
	protected Node mHeader = new Node();
	
	/** Node that signals the end of a phrase. This node should have no edges. */
	private Node mTrailer = new Node();
	
	/** Purely for informational purposes. This keeps track of how many edges our graph has. */
	private int mEdgeCount = 0;
	
	/**
	 * Get the number of edges in this graph.
	 * @return number of edges
	 */
	public int getEdgeCount() {
		return mEdgeCount;
	}
	
	/**
	 * Get the number of nodes in this graph.
	 * @return number of nodes
	 */
	public int getNodeCount() {
		return mNodes.size();
	}
	
	/**
	 * Interpret an ArrayList of data as a possible phrase.
	 * @param phrase to learn
	 */
	public void addPhrase(ArrayList<T> phrase) {
		if(phrase == null || phrase.size() == 0) return;
		
		// All phrases start at the header.
		Node current = mHeader;
		
		// Find or create each node, add to its weight for the current node
		// and interate to the next node.
		for(T data : phrase) {
			Node n = findOrCreate(data);
			current.promote(n);
			current = n;
		}
		
		// We've reached the end of the phrase, add an edge to the trailer node.
		current.promote(mTrailer);
	}
	
	/**
	 * Interpret an array of data as a valid phrase.
	 * @param phrase to interpret
	 */
	public void addPhrase(T phrase[]) {
		if(phrase == null || phrase.length == 0) return;
		
		// All phrases start at the header.
		Node current = mHeader;
		
		// Find or create each node, add to its weight for the current node
		// and interate to the next node.
		for(int i = 0; i < phrase.length; i++) {
			Node n = findOrCreate(phrase[i]);
			current.promote(n);
			current = n;
		}
		
		// Promote the trailer.
		current.promote(mTrailer);
	}
	
	/**
	 * Use our graph to randomly generate a possibly valid phrase
	 * from our data structure.
	 * @return generated phrase
	 */
	public ArrayList<T> makePhrase() {
		// Go ahead and choose our first node
		Node current = mHeader.next();
		
		// We will put our generated phrase in here.
		ArrayList<T> phrase = new ArrayList<T>();
		
		// As a safety, check for nulls
		// Iterate til we get to the trailer
		while(current != null && current != mTrailer) {
			phrase.add(current.data);
			current = current.next();
		}
		
		// Out pops pure genius
		return phrase;
	}
	
	
	/**
	 * This method is an alias to find a node if it
	 * exists or create it if it doesn't.
	 * @param data to find a node for
	 * @return the newly created node, or resolved node
	 */
	private Node findOrCreate(T data) {
		Node n = mNodes.get(data);
		
		if(n == null) {
			n = new Node(data);
			mNodes.put(data, n);
		}
		
		return n;
	}
	
	/**
	 * This is our Markov phrase node. It contains the data
	 * that this node represents as well as a list of edges to
	 * possible nodes elsewhere in the graph.
	 * @author pkilgo
	 *
	 */
	public class Node implements Comparable<Node> {
		/** The data this node represents */
		public T data;
		
		/** A list of edges to other nodes */
		protected Vector<Edge> mEdges = new Vector<Edge>();
		
		/**
		 * Blank constructor for data-less nodes (the header or trailer)
		 */
		public Node() {
			
		}
		
		/**
		 * Constructor for node which will contain data.
		 * @param d the data this node should represent
		 */
		public Node(T d) {
			data = d;
		}
		
		/**
		 * Add more weight to the given node
		 * or create an edge to that node if we didn't
		 * already have one.
		 * @param n node to add more weight to
		 */
		public void promote(Node n) {
			// Iterate through the edges and see if we can find that node.
			for(int i = 0; i < mEdges.size(); i++) {
				Edge e = mEdges.elementAt(i);
				if(e.node.compareTo(n) == 0) {
					e.weight++;
					return;
				}
			}
			
			// Elsewise, create an edge.
			mEdges.add(new Edge(n));
			MarkovChain.this.mEdgeCount++;
		}
		
		/**
		 * Randomly choose which is the next node to go to, or
		 * return null if there are no edges.
		 * @return next node, or null if we could not choose a next node
		 */
		protected Node next() {
			if(mEdges.size() == 0) return null;
			
			// First things first: count up the entirety of all the weight.
			int totalScore = 0;
			for(int i = 0; i < mEdges.size(); i++) totalScore += mEdges.get(i).weight;
			
			// Choose a random number that is less than or equal to that weight
			int r = RNG.nextInt(totalScore);
			
			// This variable contains how much weight we have "seen" in our loop.
			int current = 0;
			
			// Iterate through the edges and find out where our generated number landed.
			for(int i = 0; i < mEdges.size(); i++) {
				Edge e = mEdges.get(i);
				
				// Is it between the weight we've seen and the weight of this node?
				if(r >= current && r < current + e.weight) {
					return e.node;
				}
				
				// Add the weight we've seen
				current += e.weight;
			}
			
			// In theory, this shouldn't happen, but we should want to know if it does.
			throw new IllegalArgumentException("Something terrible happened.");
		}
		
		/**
		 * Simple container class that holds the node this edge represents and the weight
		 * of the edge.
		 * @author pkilgo
		 *
		 */
		protected class Edge {
			public Edge(Node n) {
				node = n;
				weight = 1;
			}
			
			Node node;
			int weight = 1;
		}
		
		/**
		 * For comparisons between node data.
		 * Null cases added for when the header or trailer is compared with something.
		 */
		public int compareTo(Node other) {
			if(other.data == null && data == null) return 0;
			else if(other.data == null && data != null) return 1;
			else if(other.data != null && data == null) return -1;
			else return data.compareTo(other.data);
		}
	}
}
