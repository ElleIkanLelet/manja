package com.contoh.ularmanja;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class Home extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
<<<<<<< HEAD
        //Fungsi Instance
=======
        # Fungsi Instance
>>>>>>> bcde57bf3a919658eb4b01e842f94772eb3ebd3f
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home2);

        Button btnMulai = findViewById(R.id.btnMulai);
        Button btnKeluar = findViewById(R.id.btnKeluar);

        btnMulai.setOnClickListener(v -> {

            Intent intent = new Intent(Home.this, Utama.class);
            startActivity(intent);

        });

        btnKeluar.setOnClickListener(v -> {
            finish();
        });
    }
}