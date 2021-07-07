package com.example.javasynth;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Settings mSettings = new Settings();
        mSettings.setup(this);

        Sequencer mSequencer = new Sequencer();
        mSequencer.addTrack();
        mSequencer.addNoteToTrack(0, 0, 440, 48000);
        mSequencer.addNoteToTrack(0, 48000, 880, 48000);

        mSequencer.addNoteToTrack(1, 24000, 660, 24000);
        mSequencer.addNoteToTrack(1, 48000, 1760, 48000);

        //mSequencer.addNoteToTrack(1, 60000, 1600, 48000);

        mSequencer.togglePlayPause();
        RealtimeAudioWriter mWriter = new RealtimeAudioWriter();
        mWriter.setup(mSequencer);
        mWriter.startWrite();
    }
}