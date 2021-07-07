package com.example.javasynth;

import java.util.ArrayList;

import static com.example.javasynth.Settings.BUFFER_SIZE;

public class Track {

    private ArrayList<Note> mNotes = new ArrayList<Note>();
    private Synth mSynth;
    private short[] silentBuffer = new short[BUFFER_SIZE];

    private float mVolume;

    private boolean mNoteOn;

    public Track() {
        mVolume = 1;

        for (int i = 0; i < BUFFER_SIZE; i++) {
            silentBuffer[i] = 0;
        }
        mSynth = new Synth();
    }

    public short[] updateState(long currentSample) {
        return render(currentSample);
    }

    public void addNote(long position, double frequency, double length) {
        Note note = new Note(position, frequency, length);
        mNotes.add(note);
    }

    public short[] render(long currentSample) {
        short[] buffer = new short[BUFFER_SIZE];
        Note currentNote = getCurrentNote(currentSample);
        if (currentNote != null) {
            buffer = mSynth.render(getCurrentNote(currentSample));
            return buffer;
        }
        return silentBuffer;
    }

    public Note getCurrentNote(long currentSample) {
        for (Note note : mNotes) {
            if (note.inPlayRange(currentSample)) {
                return note;
            }
        }
        return null;
    }

    public void setNoteOn() {
        mNoteOn = true;
    }

    public void setNoteOff() {
        mNoteOn = false;
    }
}
