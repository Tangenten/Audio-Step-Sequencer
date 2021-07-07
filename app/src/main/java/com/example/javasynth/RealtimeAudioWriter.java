package com.example.javasynth;

import android.media.AudioAttributes;
import android.media.AudioFormat;
import android.media.AudioTrack;
import android.os.Process;
import android.util.Log;

import static com.example.javasynth.Settings.BIT_DEPTH;
import static com.example.javasynth.Settings.BUFFER_SIZE;
import static com.example.javasynth.Settings.SAMPLE_RATE;


public final class RealtimeAudioWriter implements AudioTrack.OnPlaybackPositionUpdateListener {

    static android.media.AudioTrack mAudioTrack;

    static short samplesToWrite[] = new short[BUFFER_SIZE];
    static Sequencer mSequencer;
    static Runnable myRunnable;
    private static short[] silentBuffer = new short[BUFFER_SIZE];

    static void startWrite() {
        mAudioTrack.reloadStaticData();
        mAudioTrack.play();

        myRunnable = new Runnable() {
            public void run() {
                android.os.Process.setThreadPriority(Process.THREAD_PRIORITY_URGENT_AUDIO);

                while (true) {
                    System.arraycopy(Mixer.samples, 0, samplesToWrite, 0, BUFFER_SIZE);
                    //long startTime2 = System.nanoTime();
                    //long endTime2 = System.nanoTime();
                    //long duration2 = (endTime2 - startTime2) / 1000000;
                    //Log.d("Generate: ", String.valueOf(duration2));

                    //long startTime = System.nanoTime();
                    mSequencer.updateState();
                    mAudioTrack.write(samplesToWrite, 0, BUFFER_SIZE);
                    //long endTime = System.nanoTime();
                    //long duration = (endTime - startTime) / 1000000;
                    //Log.d("Write: ", String.valueOf(duration));
                }

            }
        };


        //mAudioTrack.write(silentBuffer, 0, BUFFER_SIZE);
        //mAudioTrack.write(silentBuffer, 0, BUFFER_SIZE);
        myRunnable.run();

    }

    void setup(Sequencer sequencer) {

        for (int i = 0; i < BUFFER_SIZE; i++) {
            silentBuffer[i] = 0;
        }

        mAudioTrack = new android.media.AudioTrack.Builder()
                .setPerformanceMode(android.media.AudioTrack.PERFORMANCE_MODE_LOW_LATENCY)
                .setAudioFormat(new AudioFormat.Builder()
                        .setEncoding(BIT_DEPTH)
                        .setSampleRate(SAMPLE_RATE)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build())
                .setBufferSizeInBytes(BUFFER_SIZE / 4)
                .setTransferMode(AudioTrack.MODE_STREAM)
                .setAudioAttributes(new AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_GAME).build())
                .build();

        //mAudioTrack.setPositionNotificationPeriod(BUFFER_SIZE);
        //mAudioTrack.setPlaybackPositionUpdateListener(this);
        mSequencer = sequencer;


    }

    @Override
    public void onMarkerReached(final AudioTrack track) {
        Log.d("onMarker", "onMarkerReached: Should Never be Reached");
    }

    @Override
    public void onPeriodicNotification(final AudioTrack track) {
        //Log.d("OnPeriod", "onPeriodicNotification: Test");
        //mSequencer.updateState();
        //System.arraycopy(Mixer.samples,0,samplesToWrite,0,BUFFER_SIZE);
        //mAudioTrack.write(samplesToWrite, 0, BUFFER_SIZE);
        new Thread(myRunnable).start();
    }
}
