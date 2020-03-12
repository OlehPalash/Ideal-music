package com.example.idealmusic;

import android.Manifest;
import android.graphics.PorterDuff;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
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
    Button btn_next, btn_previous, btn_pause;
    TextView songTextLabel;
    SeekBar songSeekBar;
    LinearLayout playerControlBar;

    SeekBarThread seekBarThread;
    String songTitleLabel;
    private List<File> songsList = new ArrayList<File>();
    private int position = 0;
    private MediaPlayer player;

    MediaPlayerHelper playerHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) { //задаёт начальную установку параметров при инициализации активности
        super.onCreate(savedInstanceState); // вызиваем версию метода из супер класса
        setContentView(R.layout.activity_main); // получаем даные

        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setTitle("Now Playing");
            supportActionBar.setDisplayHomeAsUpEnabled(false);
            supportActionBar.setDisplayShowCustomEnabled(true);
        }

        myListViewForSong = findViewById(R.id.mySongListView);
        songTextLabel = findViewById(R.id.songLabel);

        playerControlBar = findViewById(R.id.player_control_layout);
        btn_next = findViewById(R.id.next);
        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                position = ((position + 1) % songsList.size());
                playSong(position);
            }
        });
        btn_previous = findViewById(R.id.previous);
        btn_previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                position = ((position + 1) < 0) ? (songsList.size() - 1) : (position - 1);
                playSong(position);

            }
        });
        btn_pause = findViewById(R.id.pause);
        btn_pause.setBackgroundResource(R.drawable.icon_play);
        btn_pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (player == null) return;

                songSeekBar.setMax(player.getDuration());
                if (player.isPlaying()) {
                    btn_pause.setBackgroundResource(R.drawable.icon_play);
                    player.pause();
                } else {
                    btn_pause.setBackgroundResource(R.drawable.icon_pause);
                    player.start();
                }
            }
        });
        songSeekBar = findViewById(R.id.seekBar);
        songSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                player.seekTo(seekBar.getProgress());
            }
        });

        lockUI(true);
        playerHelper = MediaPlayerHelper.getInstance();
        checkPermission(); // Этот класс для полномочий времени выполнения
    }

    void runPlayer() {
        seekBarThread = new SeekBarThread(player, songSeekBar);
        seekBarThread.start();
        songsList = playerHelper.getTrackList(this);
        if (!songsList.isEmpty()) {   // prepare play on load
            songTitleLabel = songsList.get(0).getName();
            songTextLabel.setText(songTitleLabel);
            songSeekBar.getProgressDrawable().setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.MULTIPLY);
            songSeekBar.getThumb().setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
            songSeekBar.setProgress(0);
            seekBarThread.setCurrentSeekBarPosition(0);
            Uri u = Uri.parse(songsList.get(position).toString());  //Открытый конечный класс
            player = MediaPlayer.create(this, u);

            songSeekBar.setMax(player.getDuration()); // Максимальная продолжительность песни
        }
        ArrayAdapter myAdapter =
                new ArrayAdapter<File>(this, android.R.layout.simple_list_item_1, songsList);
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
        if (player != null && !songsList.isEmpty()) {
            songTitleLabel = songsList.get(position).getName();
            songTextLabel.setText(songTitleLabel);
            songSeekBar.getProgressDrawable().setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.MULTIPLY);
            songSeekBar.getThumb().setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
            songSeekBar.setMax(player.getDuration()); // Максимальная продолжительность песни
            songSeekBar.setProgress(0);
            seekBarThread.setCurrentSeekBarPosition(0);
            player.stop();
            player.release();
        }
        btn_pause.setBackgroundResource(R.drawable.icon_pause);

        Uri u = Uri.parse(songsList.get(position).toString());  //Открытый конечный класс
        player = MediaPlayer.create(this, u);
        player.start();
    }

    private void lockUI(boolean lockUI) {
        if (lockUI) {
            playerControlBar.setVisibility(View.INVISIBLE);
        } else {
            playerControlBar.setVisibility(View.VISIBLE);
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
                        songsList = playerHelper.getTrackList(MainActivity.this);
                        if (songsList.isEmpty()) {
                            lockUI(true);
                        } else {
                            lockUI(false);
                            MainActivity.this.runPlayer();
                        }
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
}