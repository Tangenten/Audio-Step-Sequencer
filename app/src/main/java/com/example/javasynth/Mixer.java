package com.example.javasynth;

import java.util.ArrayList;

import static com.example.javasynth.Settings.BUFFER_SIZE;

public class Mixer {

    public static short[] samples = new short[BUFFER_SIZE];
    private ArrayList<short[]> bufferList = new ArrayList<>();

    public Mixer() {

    }

    public void addBuffer(short[] buffer) {
        bufferList.add(buffer);
    }

    public void mix() {
        short[] summedBuffer = new short[BUFFER_SIZE];
        int size = bufferList.size();
        for (short[] buffer : bufferList) {
            for (int i = 0; i < BUFFER_SIZE; i++) {
                summedBuffer[i] += (buffer[i] / size);
            }
        }
        samples = summedBuffer;
        bufferList.clear();
    }
}
