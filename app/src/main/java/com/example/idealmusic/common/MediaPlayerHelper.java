package com.example.idealmusic.common;

import android.content.Context;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.SeekBar;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MediaPlayerHelper {
    private Context context;
    private static MediaPlayerHelper instance;
    private MediaPlayer player;

    private List songsList = new ArrayList<File>();

    private MediaPlayerHelper() {
    }

    public static MediaPlayerHelper getInstance() {
        if (instance == null) {
            instance = new MediaPlayerHelper();
        }
        return instance;
    }

    public List getTrackList(Context context) {
        songsList = getAllAudios(context);
        return songsList;
    }


    public void playByPosition(Context context, int position) {
        if (songsList.isEmpty()) return;

        if (player != null) { // окончание трека
            player.stop();
            player.release();
        }

        Uri u = Uri.parse(songsList.get(position).toString()); //Открытый конечный класс
        player = MediaPlayer.create(context, u);
        player.start();
    }

    public int getDuration() {
        if (player != null) {
            return player.getDuration();
        } else return 0;
    }

    public int getCurrentPosition() {
        return player.getCurrentPosition();
    }

    public void seekTo(int position) {
        player.seekTo(position);
    }

    public boolean isPlaying() {
        return player.isPlaying();
    }

    public void pause() {
        player.pause();
    }

    public void start() {
        player.start();
    }


    private List<File> getAllAudios(Context context) { //функция, которая дает нам все аудиофайлы в File Object.
        List<File> files = new ArrayList<>();
        String[] projection = {MediaStore.Audio.AudioColumns.DATA, MediaStore.Audio.Media.DISPLAY_NAME};
        Cursor cursor = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, projection, null, null, null);
        try {
            cursor.moveToFirst();
            do {
                files.add((new File(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)))));
            } while (cursor.moveToNext());

            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return files;
    }
}