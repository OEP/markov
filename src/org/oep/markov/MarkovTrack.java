package org.oep.markov;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiFileFormat;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import javax.sound.midi.Track;

public class MarkovTrack extends MarkovChain<MarkovTrack.MidiMessageWrapper> {

	private MarkovChain<Long> mLengthChain;
	
	public MarkovTrack(int n) {
		super(n);
		mLengthChain = new MarkovChain<Long>(n);
	}
	
	public void importMIDI(File file) throws InvalidMidiDataException, IOException {
		Sequence s = MidiSystem.getSequence(file);
		learnSequence(s, MidiSystem.getMidiFileFormat(file));
	}
	
	public void learnSequence(Sequence s, MidiFileFormat fmt) {
		Track tracks[] = s.getTracks();
		for(int i = 0; i < tracks.length; i++) {
			learnTrack(tracks[i], fmt);
		}
		
	}
	
	public void learnTrack(Track t, MidiFileFormat fmt) {
		if(t.size() == 0) return;
		ArrayList<MidiMessageWrapper> msgs = new ArrayList<MidiMessageWrapper>();
		ArrayList<Long> times = new ArrayList<Long>();

		MidiEvent event = t.get(0);
		long lastTick = event.getTick();
		MidiMessage msg = event.getMessage();
		MidiMessageWrapper wrap = new MidiMessageWrapper(msg);
		times.add(lastTick);
		
		// TODO: Assumed Sequence is PPM here
		int resolution = fmt.getResolution();
		long beats = event.getTick() / resolution;
		int adds = 0;
		
		for(int i = 1; i < t.size(); i++) {
			event = t.get(i);
			msg = event.getMessage();
			wrap = new MidiMessageWrapper(msg);
			times.add(event.getTick() - lastTick);
			lastTick = event.getTick();
			msgs.add(wrap);
		}
		
		
		if(msgs.size() > 0) {
			adds++;
			this.addPhrase(msgs);
		}
		
		mLengthChain.addPhrase(times);
		
	}
	
	public void exportTrack(String filename, float divisionType, int resolution, int fileType)
	throws InvalidMidiDataException, IOException {
		exportTrack(new File(filename), divisionType, resolution, fileType, 0);
	}
	
	public void exportTrack(String filename, float divisionType, int resolution, int fileType, int maxLength)
	throws InvalidMidiDataException, IOException {
		exportTrack(new File(filename), divisionType, resolution, fileType, maxLength);
	}
	
	public void exportTrack(File file, float divisionType, int resolution, int fileType, int maxLength)
	throws InvalidMidiDataException, IOException {
		Sequence s = new Sequence(divisionType, resolution, 1);
		Track t = s.createTrack();
		reset();
		
		int ticks = 0;
		MidiMessageWrapper wrpmsg;
		System.out.println("Max length: " + maxLength);
		while((wrpmsg = next(maxLength)) != null) {
			MidiMessage msg = wrpmsg.getMessage();
			
			long dt = mLengthChain.nextLoop();
			ticks += dt;
			MidiEvent event = new MidiEvent(msg, ticks);
			
			if(t.add(event) == false) { }
		}
		
		MidiSystem.write(s, fileType, file);
	}

	public class MidiMessageWrapper implements Comparable<MidiMessageWrapper> {
		private MidiMessage mMessage;
		
		public MidiMessageWrapper(MidiMessage msg) {
			mMessage = msg;
		}
		
		public MidiMessage getMessage() {
			return mMessage;
		}
		
		public int hashCode() {
			if(mMessage == null || mMessage.getLength() == 0) return 0;
			String str = toString();
			return str.hashCode();
		}
		
		@Override
		public int compareTo(MidiMessageWrapper other) {
			byte mymsg[] = mMessage.getMessage();
			byte theirmsg[] = mMessage.getMessage();
			

			for(int i = 0; i < mymsg.length && i < theirmsg.length; i++) {
				if(mymsg[i] > theirmsg[i]) {
					return 1;
				}
				else if(theirmsg[i] > mymsg[i]) return -1;
			}
			
			if(mymsg.length > theirmsg.length){
				return 1;
			}
			else if(mymsg.length < theirmsg.length) return -1;
			else  {
				return 0;
			}
		}
		
		public boolean equals(Object o) {
			try {
				MidiMessageWrapper other = (MidiMessageWrapper) o;
				byte mine[] = mMessage.getMessage();
				byte theirs[] = other.getMessage().getMessage();
				
				if(mine.length != theirs.length) return false;
				
				for(int i = 0; i < mine.length; i++) {
					if(mine[i] != theirs[i]) {
						return false;
					}
				}
				
				return true;
			}
			catch(Exception e) {
				return false;
			}
		}
		
		public String toString() {
			String out = new String();
			for(int i = 0; i < mMessage.getLength(); i++) {
				out += String.format("%x", mMessage.getMessage()[i]);
				if(i < mMessage.getLength() - 1) out += " ";
			}
			return out;
		}
		
	}
}
