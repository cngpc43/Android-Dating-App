package com.example.mymessengerapp;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class AudioPlayerActivity extends AppCompatActivity {

    private MediaPlayer mediaPlayer;
    private ImageButton playButton;
    private TextView durationTextView;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_player);

        playButton = findViewById(R.id.play_audio_button);
        durationTextView = findViewById(R.id.audio_duration);

        mediaPlayer = new MediaPlayer();
        try {
            String audioUrl = getIntent().getStringExtra("audioUrl");
            mediaPlayer.setDataSource(audioUrl);
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer.isPlaying()) {
                    pauseAudio();
                } else {
                    playAudio();
                }
            }
        });
    }

    private void playAudio() {
        mediaPlayer.start();
        playButton.setImageResource(android.R.drawable.ic_media_pause);
        updateAudioDuration();
    }

    private void pauseAudio() {
        if (mediaPlayer != null) {
            mediaPlayer.pause();
            playButton.setImageResource(android.R.drawable.ic_media_play);
        }
    }

    private void updateAudioDuration() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    long minutes = TimeUnit.MILLISECONDS.toMinutes(mediaPlayer.getCurrentPosition());
                    long seconds = TimeUnit.MILLISECONDS.toSeconds(mediaPlayer.getCurrentPosition())
                            - TimeUnit.MINUTES.toSeconds(minutes);
                    durationTextView.setText(String.format("%02d:%02d", minutes, seconds));
                    updateAudioDuration();
                }
            }, 1000);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.release();
            mediaPlayer = null;
        }
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
    }
}
