package com.example.idealmusic;

import android.Manifest;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.idealmusic.common.MediaPlayerHelper;
import com.example.idealmusic.common.SeekBarThread;
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

    Button btn_next, btn_previos, btn_pause;
    TextView songTextLabel;
    SeekBar songSeekbar;

    SeekBarThread updateseekBar;
    String sname;
    List<File> songsList;

    MediaPlayerHelper playerHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) { //задаёт начальную установку параметров при инициализации активности
        super.onCreate(savedInstanceState); // вызиваем версию метода из супер класса
        setContentView(R.layout.activity_main); // получаем даные

        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setTitle("Now Playing");
            supportActionBar.setDisplayHomeAsUpEnabled(true);
            supportActionBar.setDisplayShowCustomEnabled(true);
        }

        myListViewForSong = findViewById(R.id.mySongListView);
        btn_next = findViewById(R.id.next);
        btn_previos = findViewById(R.id.previus);
        btn_pause = findViewById(R.id.pause);
        btn_pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                songSeekbar.setMax(playerHelper.getDuration());
                if (playerHelper.isPlaying()) {
                    btn_pause.setBackgroundResource(R.drawable.icon_play);
                    playerHelper.pause();
                } else {
                    btn_pause.setBackgroundResource(R.drawable.icon_pause);
                    playerHelper.start();
                }
            }

        });

        songTextLabel = findViewById(R.id.songLabel);
        songSeekbar = findViewById(R.id.seekBar);

        playerHelper = MediaPlayerHelper.getInstance();

        checkPermission(); // Этот класс для полномочий времени выполнения
    }


    void onPermissionGranted() {
        updateseekBar = new SeekBarThread(playerHelper, songSeekbar);
        updateseekBar.start();
        List songsFileList = playerHelper.getTrackList(this);
        ArrayAdapter myAdapter =
                new ArrayAdapter<File>(this, android.R.layout.simple_list_item_1, songsFileList);
        myListViewForSong.setAdapter(myAdapter);
        myListViewForSong.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long id) {
                playSong(i);
            }
        });

        lockUI(false);
    }

    private void playSong(int position) {
        MediaPlayerHelper.getInstance().playByPosition(MainActivity.this, position);
        songSeekbar.setMax(playerHelper.getDuration());// Максемальная продолжительность песни
        songSeekbar.setProgress(0);
        updateseekBar.setCurrentPosition(0);

        songSeekbar.getProgressDrawable().setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.MULTIPLY);
        songSeekbar.getThumb().setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
        songSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                playerHelper.seekTo(seekBar.getProgress());
            }
        });

    }

    private void lockUI(boolean lockUI) {
        if (lockUI) {

        } else {

        }
    }

    private void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    private void checkPermission() {
        Dexter.withActivity(this)     // библеатека Dexter
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        MainActivity.this.onPermissionGranted();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                    }

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
}