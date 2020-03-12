package com.example.idealmusic.common;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MediaPlayerHelper {
    private Context context;
    private static MediaPlayerHelper instance;
    private android.media.MediaPlayer player;

    private List songsList = new ArrayList<File>();

    private MediaPlayerHelper() {
    }

    public static MediaPlayerHelper getInstance() {
        if (instance == null) {
            instance = new MediaPlayerHelper();
        }
        return instance;
    }

    public void playByPosition(Context context, int position) {
        if (songsList.isEmpty()) return;

        if (player != null) { // окончание трека
            player.stop();
            player.release();
        }

        Uri u = Uri.parse(songsList.get(position).toString()); //Открытый конечный класс
        player = android.media.MediaPlayer.create(context, u);
        player.start();
    }

    public List getTrackList(Context context) {
        return getAllAudios(context);
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

    public ArrayList<File> findSong(File file) {  //Делаем список масивов чтобы найти песню
        ArrayList<File> arrayList = new ArrayList<>();
        File[] files = file.listFiles();
        for (File singleFile : files) { // Логічна функція
            if (singleFile.isDirectory()/* && !singleFile.isHidden()*/) {
                arrayList.addAll(findSong(singleFile));
            } else {
                if (singleFile.getName().endsWith(".mp3") ||
                        singleFile.getName().endsWith(".m4a")) {
                    arrayList.add(singleFile);
                }
            }
        }
        return arrayList;
    }
}