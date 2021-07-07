package com.example.javasynth;

import android.content.Context;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioManager;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Settings {

    static int BIT_DEPTH = AudioFormat.ENCODING_PCM_16BIT;
    static int SAMPLE_RATE;
    static int BUFFER_SIZE;
    static android.media.AudioManager mAudioManager;
    static int MAX_AMPLITUDE = 32767;
    static float BUFFER_LATENCY_MS;
    static int DEVICE_LATENCY_MS;

    static double TWO_PI = (2 * Math.PI);

    private final double silenceThreshold = 70; // db

    static void setup(Context context) {

        mAudioManager = (AudioManager) context.getApplicationContext().getSystemService(Context.AUDIO_SERVICE);

        boolean hasLowLatencyFeature = context.getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_AUDIO_LOW_LATENCY);
        boolean hasProFeature = context.getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_AUDIO_PRO);

        //String sampleRateStr = mAudioManager.getProperty(AudioManager.PROPERTY_OUTPUT_SAMPLE_RATE);
        //SAMPLE_RATE = Integer.parseInt(sampleRateStr);
        SAMPLE_RATE = android.media.AudioTrack.getNativeOutputSampleRate(android.media.AudioTrack.MODE_STREAM);

        //BUFFER_SIZE = AudioTrack.getMinBufferSize(SAMPLE_RATE, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT);
        //String framesPerBuffer = mAudioManager.getProperty(AudioManager.PROPERTY_OUTPUT_FRAMES_PER_BUFFER);
        //BUFFER_SIZE = Integer.parseInt(framesPerBuffer);
        BUFFER_SIZE = 1024;

        BUFFER_LATENCY_MS = Float.valueOf((BUFFER_SIZE / SAMPLE_RATE) * 1000);

        Method m;
        {
            try {
                m = mAudioManager.getClass().getMethod("getOutputLatency", int.class);
                {
                    try {
                        DEVICE_LATENCY_MS = (Integer) m.invoke(mAudioManager, AudioManager.STREAM_MUSIC);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
        }
    }

    public static double calculateRMS(float[] floatBuffer) {
        double rms = 0.0;
        for (int i = 0; i < floatBuffer.length; i++) {
            rms += floatBuffer[i] * floatBuffer[i];
        }
        rms = rms / Double.valueOf(floatBuffer.length);
        rms = Math.sqrt(rms);
        return rms;
    }

    private double soundPressureLevel(final float[] buffer) {
        double value = Math.pow(localEnergy(buffer), 0.5);
        value = value / buffer.length;
        return linearToDecibel(value);
    }

    private double localEnergy(final float[] buffer) {
        double power = 0.0D;
        for (float element : buffer) {
            power += element * element;
        }
        return power;
    }

    public boolean isSilence(final float[] buffer) {
        double currentSPL = soundPressureLevel(buffer);
        return currentSPL < silenceThreshold;
    }

    private double linearToDecibel(final double value) {
        return 20.0 * Math.log10(value);
    }

}
