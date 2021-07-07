package com.example.javasynth;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.example.javasynth.Settings.BUFFER_SIZE;

public class Sequencer {

    private boolean mPlayPause;
    private boolean mStop;
    private boolean mRepeat;

    private int mNumerator; // Beats per Bar
    private int mDenominator; // Whole Notes per Beat

    private long mPlayhead; // Position of the playhead in samples

    private int mBpm; // Tempo of Sequencer
    private int mOctave; // Notes per Octave
    private int mTuning; // Pitch Reference for Note A4

    private String[] mNoteNames = {"A", "A#", "B", "C", "C#", "D", "D#", "E", "F", "F#", "G", "G#"};
    private Map<String, Double> mNoteTable = new HashMap<String, Double>(); // Key-Value pairs of Note Pitch names and Frequency's
    private String[] mRhythmNames = {"Whole", "Half", "Quarter", "Eighth", "Sixteenth"};
    private Map<String, Double> mRhythmTable = new HashMap<String, Double>(); // Key-Value pairs of Note Length's names and Milliseconds

    private Mixer mMixer;
    private ArrayList<Track> mTracks = new ArrayList<>();

    public Sequencer() {
        mPlayPause = false;
        mStop = false;
        mRepeat = false;

        mNumerator = 4;
        mDenominator = 4;

        mPlayhead = 0;

        mBpm = 128;
        mOctave = 12;
        mTuning = 440;

        genNoteTable();
        genRhythmTable();

        mMixer = new Mixer();
        mTracks.add(new Track());
    }

    public void updateState() {
        short buffer[];
        mPlayhead += BUFFER_SIZE;
        for (Track track : mTracks) {
            buffer = track.updateState(mPlayhead);
            mMixer.addBuffer(buffer);
        }
        mMixer.mix();
    }

    public void genNoteTable() {
        double rootOfOctave = Math.pow(2, (1 / (double) mOctave));
        int octaveNr = 0;

        for (int i = -4; i <= 4; i++) {
            for (int j = 0; j < mOctave; j++) {
                double currentNote = (double) ((mOctave * i) + j);
                Double noteFrequency = mTuning * Math.pow(rootOfOctave, currentNote);
                String noteName = mNoteNames[j].concat(String.valueOf(octaveNr));
                mNoteTable.put(noteName, noteFrequency);

                if (mNoteNames[j] == "C") {
                    octaveNr++;
                }
            }
        }
    }

    public void genRhythmTable() {
        double quarterNoteMs = 60000 / (double) mBpm;

        mNoteTable.put("Whole", quarterNoteMs * 4);
        mNoteTable.put("Half", quarterNoteMs * 2);
        mNoteTable.put("Quarter", quarterNoteMs * 1);
        mNoteTable.put("Eighth", quarterNoteMs * 0.5);
        mNoteTable.put("Sixteenth", quarterNoteMs * 0.25);
    }

    public void addTrack() {
        mTracks.add(new Track());
    }

    public void removeTrack(int trackIndex) {
        mTracks.remove(trackIndex);
    }

    public void addNoteToTrack(int trackIndex, long position, double frequency, double length) {
        mTracks.get(trackIndex).addNote(position, frequency, length);
    }

    public void setPlayhead(long position) {
        mPlayhead = position;
    }

    public void togglePlayPause() {
        if (mPlayPause) {
            mPlayPause = false;
            mStop = false;
        } else {
            mPlayPause = true;
            mStop = false;
        }
    }

    public void stop() {
        mStop = true;
        mPlayPause = false;
    }

    public void toggleRepeat() {
        if (mRepeat) {
            mRepeat = false;
        } else {
            mRepeat = true;
        }
    }

    public void incPlayhead(int sampleLength) {
        if (mPlayPause) {
            mPlayhead += sampleLength;
        }
    }
}
