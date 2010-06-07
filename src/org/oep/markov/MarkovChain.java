package org.oep.markov;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;
import java.util.Vector;

import org.oep.markov.MarkovChain.Node.Edge;

/**
 * A class for generating a Markov phrase out of some sort of
 * arbitrary comparable data.
 * @author pkilgo
 *
 * @param <T> the type of data you would like to generate phrases for (e.g., <code>java.lan
 */
public class MarkovChain<T> {
	
	/** HashMap to help us resolve data to the node that contains it */
	protected HashMap<Tuple, MarkovChain<T>.Node> mNodes =
		new HashMap<Tuple, MarkovChain<T>.Node>();
	
	/** Nodes use this to find the next node */
	private Random RNG = new Random();
	
	/** Purely for informational purposes. This keeps track of how many edges our graph has. */
	protected int mEdgeCount = 0;
	
	/** For keeping up with node IDs */
	protected int mNodeCount = 0;
	
	/** Node that marks the beginning of a phrase. All Markov phrases start here. */
	protected Node mHeader = makeNode();
	
	/** Node that signals the end of a phrase. This node should have no edges. */
	protected Node mTrailer = makeNode();
	
	/** Stores how long our tuple length is (how many data elements a node has) */
	protected int mTupleLength = 1;
	
	/** Pointer to the current node. Methods next() uses this */
	protected Node mCurrent;
	
	/** Index for which data element is next in our tuple */
	protected int mTupleIndex = 0;
	
	/** Keeps up with how long our gradual chain is */
	protected int mElements = 0;
	
	public MarkovChain(int n) {
		if(n <= 0) throw new IllegalArgumentException("Can't have MarkovChain with tuple length <= 0");
		
		mTupleLength = n;
	}
	
	/**
	 * Forget everything.
	 */
	public void clear() {
		mNodes.clear();
		mNodeCount = 0;
		mEdgeCount = 0;
		mHeader = makeNode();
		mTrailer = makeNode();
	}
	
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
	 * Re-initialize the chain pointer  and 
	 * tuple index to start from the top.
	 */
	public void reset() {
		mCurrent = null;
		mTupleIndex = 0;
	}
	
	/**
	 * Returns the next element in our gradual chain.
	 * Ignores maximum length.
	 * @return next data element
	 */
	public T next() {
		return next(false, 0);
	}
	
	/**
	 * Returns the next element and loops to the front of chain
	 * on termination.
	 * @return next element
	 */
	public T nextLoop() {
		return next(true, 0);
	}
	
	public T next(int maxLength) {
		return next(false, maxLength);
	}
	
	public T next(boolean loop) {
		return next(loop, 0);
	}
	
	
	/**
	 * Get next element pointed by our single-element.
	 * This will also update the data structure to get ready
	 * to serve the next data element.
	 * @param loop if you would like to loop
	 * @return data element at the current node tuple index
	 */
	public T next(boolean loop, int maxLength) {
		// In case mCurrent hasn't been initialized yet.
		if(mCurrent == null || mCurrent == mHeader) mCurrent = mHeader.next();

		// Handle behavior in case we're at the trailer at the start.
		if(mCurrent == mTrailer) {
			if(loop == true) {
				
				if(maxLength > 0 && mElements >= maxLength) mCurrent = mHeader.nextTerminal();
				else mCurrent = mHeader.next();
				
				mTupleIndex = 0;
			}
			// No more data for non-loopers
			else {
				return null;
			}
		}
		
		T returnValue = mCurrent.getData(mTupleIndex);
		
		mTupleIndex++;
		mElements++;
		
		// We've reached the end of this tuple.
		if(mTupleIndex >= mCurrent.size()) {
			
			if(maxLength > 0 && mElements >= maxLength) mCurrent = mCurrent.nextTerminal();
			else mCurrent = mCurrent.next();
			
			mTupleIndex = 0;
		}
		
		return returnValue;
	}
	
	/**
	 * Interpret an ArrayList of data as a possible phrase.
	 * @param phrase to learn
	 */
	public void addPhrase(ArrayList<T> phrase) {
		if(phrase == null || phrase.size() == 0) return;
		
		// All phrases start at the header.
		Node current = mHeader;
		
		// Make temporary lists to help us resolve nodes.
		Tuple tuple = new Tuple();
		
		// Find or create each node, add to its weight for the current node
		// and iterate to the next node.
		for(T data : phrase) {
			int sz = tuple.size();
			
			if(sz < mTupleLength) {
				tuple.add(data);
			}
			else {
				Node n = findOrCreate(tuple);
				current.promote(n);
				current = n;
				tuple = new Tuple();
				tuple.add(data);
			}
		}
		
		// Add any incomplete tuples if needed.
		if(tuple.size() > 0) {
			Node n = findOrCreate(tuple);
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
		
		// Empty tuple structure to work with
		Tuple tuple = new Tuple();
		
		// Find or create each node, add to its weight for the current node
		// and iterate to the next node.
		for(int i = 0; i < phrase.length; i++) {
			T data = phrase[i];
			int sz = tuple.size();
			
			if(sz < mTupleLength) {
				tuple.add(data);
			}
			else {
				Node n = findOrCreate(tuple);
				current.promote(n);
				current = n;
				tuple = new Tuple();
				tuple.add(data);
			}
		}
		
		// Add any incomplete tuples if needed.
		if(tuple.size() > 0) {
			Node n = findOrCreate(tuple);
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
			// Iterate over the data tuple in the node and add stuff to the phrase
			// if it is non-null
			for(int i = 0; i < current.data.size(); i++) {
				T data = current.data.get(i);
				
				if(data != null)
					phrase.add(data);
			}
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
	private Node findOrCreate(Tuple data) {
		if(data.size() > mTupleLength) {
			throw new IllegalArgumentException(
					String.format("Invalid tuple length %d. This structure: %d", data.size(), mTupleLength)
					);
		}
		Node n = mNodes.get(data);
		
		if(n == null) {
			n = makeNode(data);
			mNodes.put(data, n);
		}
		
		return n;
	}
	
	private Node makeNode() {
		return makeNode(null);
	}
	
	private Node makeNode(Tuple data) {
		Node n = new Node(data);
		n.id = mNodeCount;
		mNodeCount++;
		return n;
	}
	
	public class Tuple {
		protected ArrayList<T> mElements = new ArrayList<T>();
		
		public void putAll(Collection <? extends T> datas) {
			mElements.addAll(datas);
		}
		
		public void add(T data) {
			mElements.add(data);
		}
		
		public String toString() {
			return mElements.toString();
		}
		
		public T get(int n) {
			return mElements.get(n);
		}
		
		public int hashCode() {
			if(mElements.size() == 0) return 0;
			int hashCode = get(0).hashCode();
			for(int i = 1; i < size(); i++) {
				hashCode ^= get(i).hashCode();
			}
			
			return hashCode;
		}
		
		public boolean equals(Object o) {
			try {
				Tuple other = (Tuple) o;
				if(other.size() != size()) return false;
				
				for(int i = 0; i < size(); i++) {
					T mine = mElements.get(i);
					T theirs = other.mElements.get(i);

					if(mine.equals(theirs) == false) {
						return false;
					}
				}
				return true;
			}
			catch(Exception e) {
				return false;
			}
		}
		
		public int size() {
			return mElements.size();
		}
	}
	
	/**
	 * This is our Markov phrase node. It contains the data
	 * that this node represents as well as a list of edges to
	 * possible nodes elsewhere in the graph.
	 * @author pkilgo
	 *
	 */
	public class Node {
		/** The data this node represents */
		public Tuple data = new Tuple();
		
		/** ID used for crawling the data tree */
		public int id;
		
		/** A list of edges to other nodes */
		protected ArrayList<Edge> mEdges = new ArrayList<Edge>();
		
		/**
		 * Blank constructor for data-less nodes (the header or trailer)
		 */
		public Node() {
		}
		
		/**
		 * Constructor for node which will contain data.
		 * @param d the data this node should represent
		 */
		public Node(Tuple d) {
			if(d != null) data = d;
		}

		/**
		 * Get the data from the tuple at given position
		 * @param i the index of the data
		 * @return data at index
		 */
		public T getData(int i) {
			return data.get(i);
		}
		
		public int getTerminalPathLength() {
			boolean visits[] = new boolean [mNodeCount];
			return doGetTerminalPathLength(visits);
		}
		
		private int doGetTerminalPathLength(boolean visits[]) {
			// The path length is 0 if this is a terminal node.
			if(isTerminal()) return 0;
			
			// We have visited the node we are currently in
			visits[id] = true;
			
			// Make this variable exist outside the scope of following loop
			Edge e = null;
			int i = 0;
			
			// First let's iterate to find the first node we haven't visited
			for(i = 0; i < mEdges.size(); i++) {
				e = mEdges.get(i);
				if(visits[e.node.id] == false) break;
			}
			
			// If we never found one, this path does not terminate
			if(visits[e.node.id] == true) {
				visits[id] = false;
				return Integer.MAX_VALUE;
			}
			
			// Set the terminal path length of this first node as the minimum
			int min = e.node.doGetTerminalPathLength(visits);
			
			for(i++; i < mEdges.size(); i++) {
				e = mEdges.get(i);
				
				// Skip this guy if we have already visited
				if(visits[e.node.id] == true) continue;
				
				// Decide which is smaller
				int pathLength = e.node.doGetTerminalPathLength(visits);
				min = Math.min(min, pathLength);
			}
			
			// Set this guy to unvisited and return the path length
			visits[id] = false;
			return (min == Integer.MAX_VALUE) ? min : min + 1;
		}
		
		public boolean isTerminal() {
			return data == null || data.size() == 0;
		}
		
		/**
		 * Returns this node's tuple's size.
		 * @return size of tuple represented by this node
		 */
		public int size() {
			return data.size();
		}
		
		/**
		 * Add more weight to the given node
		 * or create an edge to that node if we didn't
		 * already have one.
		 * @param n node to add more weight to
		 */
		public void promote(Node n) {
			// Iterate through the edges and see if we can find that node.
			for(Edge e : mEdges) {
				if(e.node.equals(n)) {
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
			Edge choice = chooseEdge(mEdges);
			return choice.node;
		}
		
		protected Node nextTerminal() {
			if(mEdges.size() == 0) return null;
			
			ArrayList<Edge> candidates = new ArrayList<Edge>();
			Edge e = mEdges.get(0);
			candidates.add(e);
			int min = e.node.getTerminalPathLength();
			
			for(int i = 1; i < mEdges.size(); i++) {
				e = mEdges.get(i);
				int pathLength = e.node.getTerminalPathLength();
				if(pathLength == min) {
					candidates.add(e);
				}
				else if(pathLength < min) {
					candidates.clear();
					candidates.add(e);
					min = pathLength;
				}
			}
			
			if(min == Integer.MAX_VALUE) return null;
			Edge choice = chooseEdge(candidates);
//			System.out.printf("Terminal path: %d\n", min);
//			System.out.printf("%s --> %s\n", data.toString(), choice.node.data.toString());
			return choice.node;
		}
		
		private Edge chooseEdge(ArrayList<Edge> edges) {
			// First things first: count up the entirety of all the weight.
			int totalScore = 0;
			for(int i = 0; i < edges.size(); i++) totalScore += edges.get(i).weight;
			
			// Choose a random number that is less than or equal to that weight
			int r = RNG.nextInt(totalScore);
			
			// This variable contains how much weight we have "seen" in our loop.
			int current = 0;
			
			// Iterate through the edges and find out where our generated number landed.
			for(int i = 0; i < edges.size(); i++) {
				Edge e = edges.get(i);
				
				// Is it between the weight we've seen and the weight of this node?
				if(r >= current && r < current + e.weight) {
					return e;
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
		
	}
	
	public void exportXML(File file) throws FileNotFoundException {
		exportXML(new FileOutputStream(file));
	}
	
	public void exportXML(OutputStream os) {
		exportXML(new PrintStream(os));
	}
	
	public void exportXML(PrintStream os) {
		os.println("<chain>");
		printNode("header", os, mHeader);
		
		Iterator<Node> it = mNodes.values().iterator();
		
		while(it.hasNext()) {
			Node n = it.next();
			printNode("node", os, n);
		}
		
		printNode("trailer", os, mTrailer);
		os.println("</chain>");
		
		
	}
	
	private void printNode(String name, PrintStream os, Node n) {
		os.printf("\t<%s id='%d'>\n", name, n.id);
		if(n.data != null) {
			os.printf("\t%s\n",n.data.toString());
		}
		printEdges(os,n);
		os.printf("\t</%s>\n", name);
	}
	
	private void printEdges(PrintStream os, Node n) {
		for(Node.Edge e : n.mEdges) {
			os.printf("\t\t<edge id='%d' />\n",e.node.id);
		}
	}

	public static void main(String args[]) {
		MarkovChain<String> chain = new MarkovChain<String>(1);
		
		String phrases[] = {
          "foo foo foo foo foo foo foo foo foo foo foo foo foo foo foo foo foo foo foo foo foo foo foo foo foo foo foo foo foo foo foo foo foo foo foo foo foo foo foo foo foo foo foo foo foo foo foo foo foo foo foo foo foo foo bar",
          "foo doo hoo koo loo yoo oo too"
		};

		
		for(int i = 0; i < phrases.length; i++) {
			chain.addPhrase(phrases[i].split(" "));
		}
		
		String word;
		String phrase = new String();
		int i = 0;
		while((word = chain.next(10)) != null) {
			phrase += word + " ";
			i++;
		}
		System.out.println(phrase);
	}
}
