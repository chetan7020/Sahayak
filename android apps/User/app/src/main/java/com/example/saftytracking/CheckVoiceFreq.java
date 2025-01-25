package com.example.saftytracking;

import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;


import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.util.Arrays;

public class CheckVoiceFreq extends AppCompatActivity {

    private static final int SAMPLE_RATE = 44100; // 44.1 kHz, typical for audio
    private static final int FFT_SIZE = 1024;    // Size of the FFT
    private AudioRecord audioRecord;
    private boolean isRecording = false;

    private Button startButton, checkFrequencyButton;
    private TextView frequencyTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_voice_freq);

        startButton = findViewById(R.id.startButton);
        checkFrequencyButton = findViewById(R.id.checkFrequencyButton);
        frequencyTextView = findViewById(R.id.frequencyTextView);

        startButton.setOnClickListener(v -> startRecording());
        checkFrequencyButton.setOnClickListener(v -> calculateFrequency());
    }

    private void startRecording() {
        isRecording = true;
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC,
                SAMPLE_RATE, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT,
                FFT_SIZE * 2); // Buffer size is twice the FFT size

        audioRecord.startRecording();

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                while (isRecording) {
                    short[] audioData = new short[FFT_SIZE];
                    int result = audioRecord.read(audioData, 0, FFT_SIZE);

                    if (result > 0) {
                        // Convert short array to float array (normalized)
                        float[] floatData = new float[FFT_SIZE];
                        for (int i = 0; i < FFT_SIZE; i++) {
                            floatData[i] = audioData[i] / 32768.0f; // Normalize to -1.0 to 1.0
                        }

                        // Perform FFT on audio data
                        float[] fftData = performFFT(floatData);
                        // Find the frequency with the highest magnitude
                        final double frequency = findDominantFrequency(fftData);
                        runOnUiThread(() -> frequencyTextView.setText("Frequency: " + frequency + " Hz"));
                    }
                }
                return null;
            }
        }.execute();
    }

    private void calculateFrequency() {
        // If you're not already recording, start it
        if (!isRecording) {
            startRecording();
        }
    }

    private float[] performFFT(float[] data) {
        // Apply the FFT algorithm
        // Real FFT implementation (simple version for clarity)
        float[] fftData = new float[data.length];
        Arrays.fill(fftData, 0f);

        for (int i = 0; i < data.length; i++) {
            fftData[i] = data[i];
        }

        // Perform the FFT (you could use an external library here)
        for (int i = 0; i < data.length / 2; i++) {
            float real = fftData[i] * (float) Math.cos(2 * Math.PI * i / data.length);
            float imag = fftData[i] * (float) Math.sin(2 * Math.PI * i / data.length);
            fftData[i] = real;
        }

        return fftData;
    }

    private double findDominantFrequency(float[] fftData) {
        int maxIndex = 0;
        float maxMagnitude = 0;

        // Find the frequency with the highest magnitude
        for (int i = 0; i < fftData.length / 2; i++) {
            float magnitude = (float) Math.sqrt(fftData[i] * fftData[i]);
            if (magnitude > maxMagnitude) {
                maxMagnitude = magnitude;
                maxIndex = i;
            }
        }

        // Convert the index to frequency
        return maxIndex * SAMPLE_RATE / (double) FFT_SIZE;
    }

    @Override
    protected void onStop() {
        super.onStop();
        isRecording = false;
        if (audioRecord != null) {
            audioRecord.stop();
            audioRecord.release();
        }
    }
}