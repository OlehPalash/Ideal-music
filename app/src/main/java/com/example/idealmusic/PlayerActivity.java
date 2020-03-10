package com.example.idealmusic;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.media.MediaPlayer;
import android.net.Uri;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

public class PlayerActivity extends AppCompatActivity {
    static MediaPlayer myMediaPlayer;

    Button btn_next, btn_previos, btn_pause;
    TextView songTextLabel;
    SeekBar songSeekbar;

    Thread updateseekBar;
    String sname;
    ArrayList<File> mySongs;

    int position;


    @SuppressLint("NewApi")// – это аннотация, используемая инструментом Android Lint.
    // Линт расскажет вам, когда что-то в вашем коде не является оптимальным или может упасть.
    // Пропуская NewApi , вы подавляете все предупреждения, которые расскажут вам,
    // используете ли вы какой-либо API, введенный после вашего minSdkVersion
    @Override
    protected void onCreate(Bundle savedInstanceState) { //задаёт начальную установку параметров при инициализации активности
        super.onCreate(savedInstanceState);// вызиваем версию метода из супер класса
        setContentView(R.layout.activity_main);// принимаем даные

        btn_next = (Button) findViewById(R.id.next);
        btn_previos = (Button) findViewById(R.id.previus);
        btn_pause = (Button) findViewById(R.id.pause);
        songTextLabel = (TextView) findViewById(R.id.songLabel);
        songSeekbar = (SeekBar) findViewById(R.id.seekBar);
        // Функцыонал кнопок + который есть ниже

        getSupportActionBar().setTitle("Now Playing");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowCustomEnabled(true);


        updateseekBar = new Thread() {
            @Override
            public void run() { //Функцыонал seekBar

                int totalDuration = myMediaPlayer.getDuration(); // продолжытельность песни
                int currentPosition = 0;

                while (currentPosition < totalDuration) {
                    try {
                        sleep(500);
                        currentPosition = myMediaPlayer.getCurrentPosition();//Поточна позиція
                        songSeekbar.setProgress(currentPosition);
                    } catch (InterruptedException e) {
                        e.printStackTrace();//закрашывание проиграного отреска
                    }
                }
            }
        };

        if (myMediaPlayer != null) { // окончание трека
            myMediaPlayer.stop();
            myMediaPlayer.release();
        }

        Intent i = getIntent();
        Bundle bundle = i.getExtras();

        mySongs = (ArrayList) bundle.getParcelableArrayList("songs");

        sname = mySongs.get(position).getName().toString();

        String songName = i.getStringExtra("songname");

        songTextLabel.setText(songName);

        songTextLabel.setSelected(true);

        position = bundle.getInt("pos", 0);

       /* Uri u = Uri.parse(mySongs.get(position).toString()); //Открытый конечный класс

        myMediaPlayer = MediaPlayer.create(getApplicationContext(), u);

        myMediaPlayer.start();*/

       /* songSeekbar.setMax(myMediaPlayer.getDuration());// Максемальная продолжительность песни

        updateseekBar.start();

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

                myMediaPlayer.seekTo(seekBar.getProgress());
            }
        });*/




        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                myMediaPlayer.stop();
                myMediaPlayer.release();
                position = ((position + 1) % mySongs.size());

                Uri u = Uri.parse(mySongs.get(position).toString());

                myMediaPlayer = MediaPlayer.create(getApplicationContext(), u);

                sname = mySongs.get(position).getName().toString();
                songTextLabel.setText(sname);

                myMediaPlayer.start();
            }
        });

        btn_previos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                myMediaPlayer.stop();
                myMediaPlayer.release();
                position = ((position + 1) < 0) ? (mySongs.size() - 1) : (position - 1);
                Uri u = Uri.parse(mySongs.get(position).toString());
                myMediaPlayer = MediaPlayer.create(getApplicationContext(), u);

                sname = mySongs.get(position).getName().toString();
                songTextLabel.setText(sname);

                myMediaPlayer.start();

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}
