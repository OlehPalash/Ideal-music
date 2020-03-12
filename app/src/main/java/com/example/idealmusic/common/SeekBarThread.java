package com.example.idealmusic.common;

import android.media.MediaPlayer;
import android.widget.SeekBar;

public class SeekBarThread extends Thread {
    private boolean isStarted = false;
    private MediaPlayer player;
    private SeekBar songSeekBar;
    private int currentPosition = 0;

    public SeekBarThread(MediaPlayer player, SeekBar songSeekBar) {
        this.player = player;
        this.songSeekBar = songSeekBar;
    }

    @Override
    public void run() { //Функционал seekBar
        int totalDuration = 0;
        if (player != null) {
            totalDuration = player.getDuration();
        }
        ; // продолжытельность песни

        while ((currentPosition < totalDuration) && (isStarted)) {
            try {
                sleep(500);
                currentPosition = player.getCurrentPosition();//Поточна позиція
                songSeekBar.setProgress(currentPosition);
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

    public void setCurrentSeekBarPosition(int position) {
        currentPosition = position;
    }
}
