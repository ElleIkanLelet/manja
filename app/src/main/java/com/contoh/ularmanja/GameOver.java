package com.contoh.ularmanja;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class GameOver extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gameover);

        Button btnUlangi = findViewById(R.id.btnUlangi);
        Button btnKeluar = findViewById(R.id.btnKeluar);

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