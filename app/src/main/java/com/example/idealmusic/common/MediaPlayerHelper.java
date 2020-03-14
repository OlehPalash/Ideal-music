package com.example.idealmusic.common;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

import com.example.idealmusic.model.Track;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MediaPlayerHelper {

    public static List<Track> getTrackList(Context context) {
        return MediaPlayerHelper.getAllAudios(context);
    }

    private static List<Track> getAllAudios(Context context) { //функция, которая дает нам все аудиофайлы в File Object.
        List<Track> tracks = new ArrayList<>();
        List<File> files = new ArrayList<>();

        String[] projection = {MediaStore.Audio.AudioColumns.DATA, MediaStore.Audio.Media.DISPLAY_NAME};
        Cursor cursor = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, projection, null, null, null);
        if (cursor == null) {
            return tracks;
        }
        try {
            cursor.moveToFirst();
            do {
                files.add((new File(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)))));
            } while (cursor.moveToNext());

            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (File file : files) {
            tracks.add(new Track(file, file.getName(), file.getPath()));
        }
        return tracks;
    }
}