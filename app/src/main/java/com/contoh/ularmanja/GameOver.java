package com.contoh.ularmanja;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

public class GameOver extends Activity {

    private MediaPlayer backsound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gameover);

        // Mainkan musik game over
        backsound = MediaPlayer.create(this, R.raw.tabrak);
        backsound.setLooping(true);
        backsound.start();

        Button btnUlangi = findViewById(R.id.btnUlangi);
        Button btnKeluar = findViewById(R.id.btnKeluar);

        TextView tvScore = findViewById(R.id.tvScore);
        TextView tvHighScore = findViewById(R.id.tvHighScore);

        int score = getIntent().getIntExtra("score", 0);

        SharedPreferences dataSimpan =
                getSharedPreferences("SkorUlar", MODE_PRIVATE);

        int highScore = dataSimpan.getInt("highscore", 0);

        if (score > highScore) {
            highScore = score;

            SharedPreferences.Editor editor = dataSimpan.edit();
            editor.putInt("highscore", highScore);
            editor.apply();
        }

        tvScore.setText("Skor Kamu: " + score);
        tvHighScore.setText("High Score: " + highScore);

        btnUlangi.setOnClickListener(v -> {
            stopLagu();

            Intent intent = new Intent(GameOver.this, Utama.class);
            startActivity(intent);
            finish();
        });

        btnKeluar.setOnClickListener(v -> {
            stopLagu();

            Intent intent = new Intent(GameOver.this, Home.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            startActivity(intent);
            finish();
        });
    }

    private void stopLagu() {
        if (backsound != null) {
            if (backsound.isPlaying()) {
                backsound.stop();
            }

            backsound.release();
            backsound = null;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLagu();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopLagu();
    }
}