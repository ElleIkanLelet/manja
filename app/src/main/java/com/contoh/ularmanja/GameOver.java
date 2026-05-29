package com.contoh.ularmanja;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.content.SharedPreferences;
import android.widget.TextView;

public class GameOver extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gameover);

        Button btnUlangi = findViewById(R.id.btnUlangi);
        Button btnKeluar = findViewById(R.id.btnKeluar);

        TextView tvScore = findViewById(R.id.tvScore);
        TextView tvHighScore = findViewById(R.id.tvHighScore);

        // Ambil skor
        int score = getIntent().getIntExtra("score", 0);

        // SharedPreferences High Score
        SharedPreferences dataSimpan =
                getSharedPreferences("SkorUlar", MODE_PRIVATE);

        int highScore = dataSimpan.getInt("highscore", 0);

        // Update high score kalau score sekarang lebih tinggi
        if (score > highScore) {

            highScore = score;

            SharedPreferences.Editor editor = dataSimpan.edit();
            editor.putInt("highscore", highScore);
            editor.apply();
        }

        // Tampilkan score
        tvScore.setText("Skor Kamu: " + score);

        // Tampilkan high score
        tvHighScore.setText("High Score: " + highScore);

        // ULANGI
        btnUlangi.setOnClickListener(v -> {

            Intent intent = new Intent(GameOver.this, Utama.class);
            startActivity(intent);

            finish();

        });

        // KELUAR
        btnKeluar.setOnClickListener(v -> {

            Intent intent = new Intent(GameOver.this, Home.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            startActivity(intent);

            finish();

        });
    }
}