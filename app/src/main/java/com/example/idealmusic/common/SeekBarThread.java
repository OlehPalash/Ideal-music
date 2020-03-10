package com.example.idealmusic.common;

import android.widget.SeekBar;

public class SeekBarThread extends Thread {
    private boolean isStarted = false;
    private MediaPlayerHelper playerHelper;
    private SeekBar songSeekbar;
    private int currentPosition = 0;

    public SeekBarThread(MediaPlayerHelper playerHelper, SeekBar songSeekbar) {
        this.playerHelper = playerHelper;
        this.songSeekbar = songSeekbar;
    }

    @Override
    public void run() { //Функционал seekBar
        int totalDuration = playerHelper.getDuration(); // продолжытельность песни

        while ((currentPosition < totalDuration) && (isStarted)) {
            try {
                sleep(500);
                currentPosition = playerHelper.getCurrentPosition();//Поточна позиція
                songSeekbar.setProgress(currentPosition);
            } catch (InterruptedException e) {
                e.printStackTrace(); //закрашывание проиграного отреска
            }
        }
    }

    public boolean isStarted() {
        return isStarted;
    }

    public void setStarted(boolean started) {
        isStarted = started;
    }

    public void setCurrentPosition(int position) {
        currentPosition = position;
    }
}
