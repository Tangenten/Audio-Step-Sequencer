package com.example.javasynth;

import static com.example.javasynth.Settings.BUFFER_SIZE;
import static com.example.javasynth.Settings.MAX_AMPLITUDE;
import static com.example.javasynth.Settings.SAMPLE_RATE;
import static com.example.javasynth.Settings.TWO_PI;

public class Synth {

    private double mFrequency;
    private int mAmplitude;
    private double mPhase;
    private double phaseIncrement;

    private OscillatorType mOscType;

    Synth() {
        mFrequency = 440;
        mAmplitude = 1;
        mPhase = 0;
        phaseIncrement = (8. * Math.atan(1.)) * mFrequency / SAMPLE_RATE;
        mOscType = OscillatorType.SQUARE;
    }

    public short[] render(Note note) {
        setFrequency(note.mFrequency);

        return generateWave();
    }

    private void setFrequency(double frequency) {
        mFrequency = frequency;
        updatePhaseIncrement();
    }

    private void setAmplitude(int amplitude) {
        mAmplitude = amplitude;
    }

    private void updatePhaseIncrement() {
        phaseIncrement = TWO_PI * mFrequency / SAMPLE_RATE;
    }

    private void setOscType(OscillatorType oscType) {
        mOscType = oscType;
    }

    private short[] generateWave() {

        short[] buffer = new short[BUFFER_SIZE];

        switch (mOscType) {
            case SINE:
                for (int i = 0; i < BUFFER_SIZE; i++) {
                    buffer[i] = (short) ((MAX_AMPLITUDE * Math.sin(mPhase)) * mAmplitude);
                    mPhase += phaseIncrement;
                    while (mPhase >= TWO_PI) {
                        mPhase -= TWO_PI;
                    }
                }
                break;
            case TRIANGLE:
                for (int i = 0; i < BUFFER_SIZE; i++) {
                    double value = (MAX_AMPLITUDE * (-1.0 + (2.0 * mPhase / TWO_PI)));
                    buffer[i] = (short) ((2 * (Math.abs(value) - (MAX_AMPLITUDE / 2))) * mAmplitude);
                    mPhase += phaseIncrement;
                    while (mPhase >= TWO_PI) {
                        mPhase -= TWO_PI;
                    }
                }
                break;
            case SQUARE:
                for (int i = 0; i < BUFFER_SIZE; i++) {
                    double value = (short) (MAX_AMPLITUDE * Math.sin(mPhase));
                    if (value >= 0) {
                        buffer[i] = (short) (MAX_AMPLITUDE * mAmplitude);
                    } else {
                        buffer[i] = (short) (-MAX_AMPLITUDE * mAmplitude);
                    }
                    mPhase += phaseIncrement;
                    while (mPhase >= TWO_PI) {
                        mPhase -= TWO_PI;
                    }
                }
                break;
            case PULSE:
                for (int i = 0; i < BUFFER_SIZE; i++) {
                    double value = (short) (MAX_AMPLITUDE * Math.sin(mPhase));
                    if (value >= ((-MAX_AMPLITUDE) + (MAX_AMPLITUDE * 0.25))) {
                        buffer[i] = (short) (MAX_AMPLITUDE * mAmplitude);
                    } else {
                        buffer[i] = (short) (-MAX_AMPLITUDE * mAmplitude);
                    }
                    mPhase += phaseIncrement;
                    while (mPhase >= TWO_PI) {
                        mPhase -= TWO_PI;
                    }
                }
                break;
            case SAW:
                for (int i = 0; i < BUFFER_SIZE; i++) {
                    buffer[i] = (short) ((MAX_AMPLITUDE * (1.0 - 2.0 * mPhase / TWO_PI)) * mAmplitude);
                    mPhase += phaseIncrement;
                    while (mPhase >= TWO_PI) {
                        mPhase -= TWO_PI;
                    }
                }
                break;
            case NOISE:
                for (int i = 0; i < BUFFER_SIZE; i++) {
                    buffer[i] = (short) ((MAX_AMPLITUDE * Math.random()) * mAmplitude);
                }
                break;
        }
        return buffer;

    }

    private void noteOn() {

    }

    private void noteOff() {

    }

    private enum OscillatorType {
        SINE,
        TRIANGLE,
        SQUARE,
        PULSE,
        SAW,
        NOISE,
    }
}
