package com.contoh.ularmanja;
import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

public class Utama extends Activity {

    private Tampilan tampilanGame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        tampilanGame = new Tampilan(this);
        setContentView(tampilanGame);
    }

    @Override
    protected void onResume() {
        super.onResume();
        tampilanGame.lanjutkan();
    }

    @Override
    protected void onPause() {
        super.onPause();
        tampilanGame.diamkan();
    }
}