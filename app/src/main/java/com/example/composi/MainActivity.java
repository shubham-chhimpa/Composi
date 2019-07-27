package com.example.composi;

import android.content.Context;
import android.media.MediaPlayer;


import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;


import android.util.Log;
import android.view.View;

import android.widget.*;

import java.io.*;
import java.util.concurrent.TimeUnit;


public class MainActivity extends AppCompatActivity {
    private TextView timeElapsed, ttime,progressText;
    private EditText inputNotes;
    static ImageButton playBtn;
    static Button fetchBtn;
    static byte[] audioBytes;
    public static Context ctx;
    private MediaPlayer mediaPlayer = new MediaPlayer();
    boolean isPaused = false;
    int mediaPlayerCurrentPostion = 0;
    private SeekBar musicSeekbar;
    Handler mHandler = new Handler();
    private ProgressBar progressBar;
    private ConstraintLayout playerLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.ctx = getApplication();
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        playerLayout = findViewById(R.id.playerlayout);
        progressBar = findViewById(R.id.progressBar);
        progressText = findViewById(R.id.progresstext);
        timeElapsed = findViewById(R.id.timeElapsed);
        ttime = findViewById(R.id.ttime);
        musicSeekbar = findViewById(R.id.musicSeekbar);
        inputNotes = findViewById(R.id.notes_input);
        fetchBtn = findViewById(R.id.fetch_btn);
        playBtn = findViewById(R.id.playBtn);
        //  playBtn.setVisibility(View.INVISIBLE);

        fetchBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                playBtn.setVisibility(View.INVISIBLE);
                fetchBtn.setVisibility(View.INVISIBLE);
                playerLayout.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
                progressText.setVisibility(View.VISIBLE);

                FetchAudioTask fetchAudioTask = new FetchAudioTask(inputNotes.getText().toString(), new onTaskCompleted() {
                    @Override
                    public void onTaskCompleted() {
                        Log.i("MAINACTIVITY", "TASK COMPLETED");
                        runOnUiThread(new Runnable() {
                            public void run() {
                                loadMp3(audioBytes);
                                mediaPlayerCurrentPostion = 0;
                                musicSeekbar.setProgress(0);
                                timeElapsed.setText("0:0");
                                progressBar.setVisibility(View.GONE);
                                progressText.setVisibility(View.GONE);
                                playerLayout.setVisibility(View.VISIBLE);

                            }
                        });

                    }

                    @Override
                    public void onTaskFailed() {
                        runOnUiThread(new Runnable() {
                            public void run() {
                                fetchBtn.setVisibility(View.VISIBLE);
                                progressBar.setVisibility(View.GONE);
                                progressText.setVisibility(View.GONE);
                            }
                        });


                    }
                });
                fetchAudioTask.execute();


            }
        });
        playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (audioBytes != null) {

                    if (mediaPlayer.isPlaying()) {
                        mediaPlayer.pause();
                        mediaPlayerCurrentPostion = mediaPlayer.getCurrentPosition();
                        playBtn.setImageResource(R.drawable.ic_pause_black_24dp);
                        isPaused = true;

                    } else {
                        if (isPaused) {
                            mediaPlayer.seekTo(mediaPlayerCurrentPostion);
                            playBtn.setImageResource(R.drawable.ic_play_arrow_black_36dp);
                            mediaPlayer.start();
                        } else {
                            System.out.println(audioBytes);
                            mediaPlayer.start();

                        }
                    }
                }
            }
        });


        musicSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (mediaPlayer != null && fromUser) {
                    System.out.println(progress);

                    System.out.println(progress);

                    mediaPlayer.seekTo(progress * 1000);
                    mediaPlayerCurrentPostion = mediaPlayer.getCurrentPosition();


                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
//Make sure you update Seekbar on UI thread
        MainActivity.this.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    int mcurrentpos = mediaPlayer.getCurrentPosition() / 1000;
                    System.out.println("media current pos " + mcurrentpos);
                    musicSeekbar.setProgress(mcurrentpos);
                    updateTimeElapsed();
                }
                if (mediaPlayer != null) {


                }
                mHandler.postDelayed(this, 1000);
            }
        });

    }

    private void updateTimeElapsed() {

        timeElapsed.setText(String.format("%d:%d",
                TimeUnit.MILLISECONDS.toMinutes((long) mediaPlayer.getCurrentPosition()),
                TimeUnit.MILLISECONDS.toSeconds((long) mediaPlayer.getCurrentPosition()) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.
                                toMinutes((long) mediaPlayer.getCurrentPosition())))
        );
    }

    @Override
    protected void onStart() {
        super.onStart();


    }


    // music player code start

    public void loadMp3(byte[] mp3SoundByteArray) {
        try {
            // create temp file that will hold byte array
            Log.i("MAINACTIVITY", "mp3SoundByteArray " + mp3SoundByteArray);
            File tempMp3 = File.createTempFile("kurchina", "mp3", getCacheDir());
            tempMp3.deleteOnExit();
            FileOutputStream fos = new FileOutputStream(tempMp3);
            fos.write(mp3SoundByteArray);
            fos.close();

            // resetting mediaplayer instance to evade problems
            mediaPlayer.reset();

            // In case you run into issues with threading consider new instance like:
            // MediaPlayer mediaPlayer = new MediaPlayer();

            // Tried passing path directly, but kept getting
            // "Prepare failed.: status=0x1"
            // so using file descriptor instead
            FileInputStream fis = new FileInputStream(tempMp3);
            mediaPlayer.setDataSource(fis.getFD());

            mediaPlayer.prepare();
            // mediaPlayer.start();
            ttime.setText(String.format("%d:%d",
                    TimeUnit.MILLISECONDS.toMinutes((long) mediaPlayer.getDuration()),
                    TimeUnit.MILLISECONDS.toSeconds((long) mediaPlayer.getDuration()) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.
                                    toMinutes((long) mediaPlayer.getDuration())))
            );
            musicSeekbar.setMax(mediaPlayer.getDuration() / 1000);

            System.out.println("total duration" + (mediaPlayer.getDuration() / 1000));

        } catch (IOException ex) {
            String s = ex.toString();
            ex.printStackTrace();
        }
    }


    //music player code end

    @Override
    protected void onDestroy() {
        super.onDestroy();
        clearMediaPlayer();
    }

    private void clearMediaPlayer() {
        mediaPlayer.stop();
        mediaPlayer.release();
        mediaPlayer = null;
    }


}

