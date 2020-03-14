package com.example.idealmusic;

import android.Manifest;
import android.graphics.PorterDuff;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
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
import com.example.idealmusic.model.Track;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class MainActivity extends AppCompatActivity {
    private final String TAG = this.getClass().getSimpleName();

    ListView myListViewForSong; // создаем лист
    Button btn_next, btn_previous, btn_pause;
    TextView songTextLabel;
    SeekBar songSeekBar;
    LinearLayout playerControlBar;

    String songTitleLabel;
    private List<Track> songsList = new ArrayList<>();
    private int position = 0;
    private MediaPlayer player;

    private Handler mHandler = new Handler();

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
                next();
            }
        });

        btn_previous = findViewById(R.id.previous);
        btn_previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                previous();
            }
        });

        btn_pause = findViewById(R.id.pause);
        btn_pause.setBackgroundResource(R.drawable.icon_play);
        btn_pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (player == null) return;

                if (player.isPlaying()) {
                    stop();
                } else {
                    resume();
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
                if (player.getCurrentPosition() >= seekBar.getProgress()) {
                    next();
                } else {
                    player.seekTo(seekBar.getProgress());
                }
                if (!player.isPlaying()) {
                    player.start();
                }
            }
        });

        lockUI(true);
        checkPermission(); // Этот класс для полномочий времени выполнения
    }

    private void stop() {
        player.stop();
        updatePlayButton();
        songSeekBar.setProgress(0);
    }

    private void previous() {
        position = ((position - 1) < 0) ? (songsList.size() - 1) : (position - 1);
        play(position);
    }

    private void next() {
        position = ((position + 1) % songsList.size());
        play(position);
    }

    private void resume() {
        player.start();
        updatePlayButton();
    }

    private void play(int position) {
        if (player != null && !songsList.isEmpty() && position >= 0 && position < songsList.size()) {
            Track track = songsList.get(position);
            songTitleLabel = track.getTitle().isEmpty() ? track.getDescription().isEmpty() ? track.getFile().getName() : "" : "";
            songTextLabel.setText(songTitleLabel);
            songSeekBar.setMax(player.getDuration()); // Максимальная продолжительность песни
            songSeekBar.setProgress(0);
            player.stop();
            player.release();
        }

        Uri u = Uri.parse(songsList.get(position).getFile().toString());  //Открытый конечный класс
        player = MediaPlayer.create(this, u);
        player.start();
        updatePlayButton();
        MainActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if ((player != null) && (player.isPlaying())) {
                    songSeekBar.setProgress(player.getCurrentPosition());
                    mHandler.postDelayed(this, 1000);
                }
            }
        });
    }

    void runPlayer() {
        songsList = MediaPlayerHelper.getTrackList(this);
        if (songsList.isEmpty()) {
            lockUI(true);
        } else {
            lockUI(false);

            songTitleLabel = songsList.get(0).getTitle();
            songTextLabel.setText(songTitleLabel);

            Uri u = Uri.parse(songsList.get(position).getFile().toString());  //Открытый конечный класс
            player = MediaPlayer.create(this, u);
            songSeekBar.setProgress(0);
            songSeekBar.setMax(player.getDuration()); // Максимальная продолжительность песни
            updatePlayButton();

            List<String> trackName = new ArrayList<>();
            for (int i = 0; i < songsList.size(); i++) {
                Track f = songsList.get(i);
                trackName.add(!f.getTitle().isEmpty() ? f.getTitle() : !f.getDescription().isEmpty() ? f.getDescription() : "Track " + i);
            }

            ArrayAdapter myAdapter = new ArrayAdapter<Track>(this, android.R.layout.simple_list_item_1, songsList);
            myListViewForSong.setAdapter(myAdapter);
            myListViewForSong.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long id) {
                    play(i);
                }
            });
        }
    }

    private void lockUI(boolean lockUI) {
        if (lockUI) {
            playerControlBar.setVisibility(View.INVISIBLE);
        } else {
            playerControlBar.setVisibility(View.VISIBLE);
        }
    }

    private void updatePlayButton() {
        int colorRes;
        if (player.isPlaying()) {
            btn_pause.setBackgroundResource(R.drawable.icon_pause);
            colorRes = R.color.colorPrimary;
        } else {
            btn_pause.setBackgroundResource(R.drawable.icon_play);
            colorRes = R.color.colorPrimaryDark;
        }
        songSeekBar.getProgressDrawable().setColorFilter(getResources().getColor(colorRes), PorterDuff.Mode.MULTIPLY);
        songSeekBar.getThumb().setColorFilter(getResources().getColor(colorRes), PorterDuff.Mode.SRC_IN);
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
                        songsList = MediaPlayerHelper.getTrackList(MainActivity.this);
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