package com.contoh.ularmanja;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

public class Utama extends Activity {

    static {
        System.loadLibrary("native-lib");
    }

    public native float[] updateLogikaUlar(float inputX, float inputY, float speed);

    private Tampilan tampilanGame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        );

        setContentView(R.layout.activity_main);

        TextView scoreText = findViewById(R.id.scoreText);

        tampilanGame = new Tampilan(this, scoreText);

        FrameLayout gameContainer = findViewById(R.id.gameContainer);

        gameContainer.addView(tampilanGame);

        Button startButton = findViewById(R.id.startButton);
        Button pauseButton = findViewById(R.id.pauseButton);
        Button restartButton = findViewById(R.id.restartButton);

        startButton.setOnClickListener(v -> {
            tampilanGame.mulaiGame();
        });

        pauseButton.setOnClickListener(v -> {
            tampilanGame.pauseGame();
        });

        restartButton.setOnClickListener(v -> {
            tampilanGame.restartGame();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (tampilanGame != null) {
            tampilanGame.lanjutkan();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (tampilanGame != null) {
            tampilanGame.diamkan();
        }
    }
}