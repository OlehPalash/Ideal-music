package com.example.idealmusic;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Environment;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    ListView myListViewForSong; // создаем лист
    String[] items;            // создаем новий елемент в масиве String

    public static   ArrayList<File> getAllAudios(Context c) { //функция, которая дает нам все аудиофайлы в File Object.
        ArrayList<File> files = new ArrayList<>();
        String[] projection = { MediaStore.Audio.AudioColumns.DATA ,MediaStore.Audio.Media.DISPLAY_NAME};
        Cursor cursor = c.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, projection, null, null, null);
        try {
            cursor.moveToFirst();
            do{
                files.add((new File(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)))));
            }while(cursor.moveToNext());

            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return files;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) { //задаёт начальную установку параметров при инициализации активности
        super.onCreate(savedInstanceState);// вызиваем версию метода из супер класса
        setContentView(R.layout.activity_main);// получаем даные

        myListViewForSong=(ListView) findViewById(R.id.mySongListView);

        runtimePermission(); // Этот класс для полномочий времени выполнения
    }

    public void runtimePermission (){

        Dexter.withActivity(this)     // библеатека Dexter
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) { display();}
                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {}
                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest(); // открываем стандарт
                    }
                }).check();

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


    void display(){
        final ArrayList<File> mySongs = getAllAudios(this);

        items = new String[mySongs.size()];// создаём пункт

        for (int i=0;i<mySongs.size();i++){


            items[i]= mySongs.get(i).getName().toString().replace(".mp3", "").replace(".m4a","" );

        }

            ArrayAdapter<String>myAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,items);
        myListViewForSong.setAdapter(myAdapter);

        myListViewForSong.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long id ) {

                String songName = myListViewForSong.getItemAtPosition(i).toString();

                startActivity(new Intent(getApplicationContext(),PlayerActivity.class)
                .putExtra("songs",mySongs).putExtra("songname",songName) //кратчайший способ получения данных
                .putExtra("pos",i));


            }
        });

        }
    }