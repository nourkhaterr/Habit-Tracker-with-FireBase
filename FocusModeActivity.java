package com.example.habitotracker;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class FocusModeActivity extends AppCompatActivity {
    private TextView studyTimeTextView;
    private Button startPauseButton, stopButton, musicOption1Button, musicOption2Button, stopMusicButton;

    private CountDownTimer countDownTimer;
    private long timeLeftInMillis = 1500000; // 25 minutes in milliseconds
    private boolean isTimerRunning = false;

    private MediaPlayer mediaPlayer;
    private boolean isMusicPlaying = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activtiy_focus_mode); // Ensure this is the correct layout name

        // Initialize views
        studyTimeTextView = findViewById(R.id.studyTimeTextView);
        startPauseButton = findViewById(R.id.startPauseButton);
        stopButton = findViewById(R.id.stopButton);
        musicOption1Button = findViewById(R.id.musicOption1Button);
        musicOption2Button = findViewById(R.id.musicOption2Button);
        stopMusicButton = findViewById(R.id.stopMusicButton);

        updateStudyTimeTextView();

        // Start or Pause study session
        startPauseButton.setOnClickListener(v -> {
            if (isTimerRunning) {
                pauseStudySession();
            } else {
                startStudySession();
            }
        });

        // Stop and Reset timer
        stopButton.setOnClickListener(v -> stopStudySession());

        // Play focus music options
        musicOption1Button.setOnClickListener(v -> playFocusMusic(1)); // Forest sound
        musicOption2Button.setOnClickListener(v -> playFocusMusic(2)); // Rain sound

        // Stop music
        stopMusicButton.setOnClickListener(v -> stopMusic());
    }

    // Start the study session
    private void startStudySession() {
        startPauseButton.setText("Pause");
        isTimerRunning = true;

        countDownTimer = new CountDownTimer(timeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                updateStudyTimeTextView();
            }

            @Override
            public void onFinish() {
                Toast.makeText(FocusModeActivity.this, "Study session finished!", Toast.LENGTH_SHORT).show();
                resetTimer();
            }
        }.start();
    }

    // Pause the study session
    private void pauseStudySession() {
        countDownTimer.cancel();
        isTimerRunning = false;
        startPauseButton.setText("Resume");
    }

    // Stop the study session and reset timer
    private void stopStudySession() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        resetTimer();
    }

    // Reset timer to default 25 minutes
    private void resetTimer() {
        timeLeftInMillis = 1500000;
        isTimerRunning = false;
        startPauseButton.setText("Start");
        updateStudyTimeTextView();
    }

    // Update the study time display
    private void updateStudyTimeTextView() {
        int minutes = (int) (timeLeftInMillis / 1000) / 60;
        int seconds = (int) (timeLeftInMillis / 1000) % 60;
        studyTimeTextView.setText(String.format("Study Time: %02d:%02d", minutes, seconds));
    }

    // Play focus music
    private void playFocusMusic(int musicOption) {
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }

        if (musicOption == 1) {
            mediaPlayer = MediaPlayer.create(this, R.raw.forest);
            Toast.makeText(this, "Playing Forest Sound", Toast.LENGTH_SHORT).show();
        } else {
            mediaPlayer = MediaPlayer.create(this, R.raw.rain);
            Toast.makeText(this, "Playing Rain Sound", Toast.LENGTH_SHORT).show();
        }

        if (mediaPlayer != null) {
            mediaPlayer.setLooping(true);
            mediaPlayer.start();
            isMusicPlaying = true;
            stopMusicButton.setEnabled(true);
        }
    }

    // Stop music
    private void stopMusic() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
            isMusicPlaying = false;
            Toast.makeText(this, "Music Stopped", Toast.LENGTH_SHORT).show();
        }
    }

    // Release media resources when the activity stops
    @Override
    protected void onStop() {
        super.onStop();
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }
}
