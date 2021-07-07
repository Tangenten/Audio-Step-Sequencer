package com.example.javasynth;

public class Note {

    long mPosition;
    double mLength;

    float mVolume;
    double mFrequency;

    int mADSRAttack;
    int mADSRDecay;
    float mADSRSustain;
    int mADSRRelease;

    float mLFOVolume;
    float mHz;

    public Note(long position, double frequency, double length) {
        mPosition = position;
        mFrequency = frequency;
        mLength = length;
    }

    public boolean inPlayRange(long currentSample) {
        if (currentSample >= mPosition && currentSample <= (long) (mPosition + mLength)) {
            return true;
        } else {
            return false;
        }
    }
}
