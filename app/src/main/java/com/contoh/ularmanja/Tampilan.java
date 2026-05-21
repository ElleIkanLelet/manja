package com.contoh.ularmanja;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import java.util.ArrayList;
import java.util.Random;
import android.widget.TextView;
import android.graphics.BitmapFactory;



public class Tampilan extends SurfaceView implements Runnable {

    private Thread utas = null;
    private SurfaceHolder wadahPermukaan;
    private volatile boolean sedangBermain;
    private Canvas kanvas;
    private Paint kuas;

    private int layarX, layarY;
    private int ukuranBlok;
    private final int JUMLAH_BLOK_LEBAR = 20;
    private int jumlahBlokTinggi;

    private ArrayList<Point> ular;
    private Point makanan;
    private Point makananBesar;
    private ArrayList<Point> rintangan;

    private enum Arah {ATAS, BAWAH, KIRI, KANAN}
    private Arah arahSaatIni = Arah.KANAN;

    private int skor;
    private int skorTertinggi;
    private long waktuFrameTerakhir;
    private int tunda;

    private boolean makananBesarAktif = false;
    private long waktuMulaiMakananBesar;
    private final long DURASI_MAKANAN_BESAR = 5000;

    private enum Status {MENU, BERMAIN, KALAH}
    private Status statusSekarang = Status.MENU;

    private float sentuhX, sentuhY;
    private SharedPreferences dataSimpanan;
    private SoundPool efekSuara;
    private TextView teksSkor;
    private int idSuaraMakan, idSuaraTabrak;
    private Bitmap bitmapBatu;
    private Bitmap bitmapKepalaAtas, bitmapKepalaBawah, bitmapKepalaKiri, bitmapKepalaKanan;
    private Bitmap bitmapBadan, bitmapEkor;

    public Tampilan(Context konteks, TextView scoreView) {
        super(konteks);
        this.teksSkor = scoreView;
        setZOrderOnTop(false);
        wadahPermukaan = getHolder();
        kuas = new Paint();
        ular = new ArrayList<>();
        rintangan = new ArrayList<>();

        dataSimpanan = konteks.getSharedPreferences("SkorUlar", Context.MODE_PRIVATE);
        skorTertinggi = dataSimpanan.getInt("tinggi", 0);

        AudioAttributes atribut = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();
        efekSuara = new SoundPool.Builder()
                .setMaxStreams(5)
                .setAudioAttributes(atribut)
                .build();

        idSuaraMakan = efekSuara.load(konteks, getResources().getIdentifier("makan", "raw", konteks.getPackageName()), 1);
        idSuaraTabrak = efekSuara.load(konteks, getResources().getIdentifier("tabrak", "raw", konteks.getPackageName()), 1);

        bitmapBatu = BitmapFactory.decodeResource(getResources(), R.drawable.batu_rintangan);
        bitmapKepalaAtas = BitmapFactory.decodeResource(getResources(), R.drawable.ular_kepala_atas);
        bitmapKepalaBawah = BitmapFactory.decodeResource(getResources(), R.drawable.ular_kepala_bawah);
        bitmapKepalaKiri = BitmapFactory.decodeResource(getResources(), R.drawable.ular_kepala_kiri);
        bitmapKepalaKanan = BitmapFactory.decodeResource(getResources(), R.drawable.ular_kepala_kanan);
        bitmapBadan = BitmapFactory.decodeResource(getResources(), R.drawable.ular_badan);
        bitmapEkor = BitmapFactory.decodeResource(getResources(), R.drawable.ular_ekor);

    }

    public void lanjutkan() {
        sedangBermain = true;
        utas = new Thread(this);
        utas.start();
    }

    public void diamkan() {
        sedangBermain = false;
        try {
            utas.join();
        } catch (InterruptedException e) {}
    }

    @Override
    public void run() {
        while (sedangBermain) {
            if (statusSekarang == Status.BERMAIN) {
                if (butuhPembaruan()) {
                    perbarui();
                }
            }
            gambar();
        }
    }

    private boolean butuhPembaruan() {
        if (System.currentTimeMillis() - waktuFrameTerakhir > tunda) {
            waktuFrameTerakhir = System.currentTimeMillis();
            return true;
        }
        return false;
    }

    private void mulaiPermainan() {
        skor = 0;
        tunda = 200;
        arahSaatIni = Arah.KANAN;
        statusSekarang = Status.BERMAIN;
        makananBesarAktif = false;

        ukuranBlok = layarX / JUMLAH_BLOK_LEBAR;
        jumlahBlokTinggi = layarY / ukuranBlok;

        ular.clear();
        ular.add(new Point(JUMLAH_BLOK_LEBAR / 2, jumlahBlokTinggi / 2));

        rintangan.clear();
        Random acak = new Random();
        for (int i = 0; i < 5; i++) {
            rintangan.add(new Point(acak.nextInt(JUMLAH_BLOK_LEBAR), acak.nextInt(jumlahBlokTinggi)));
        }

        munculkanMakanan();
    }

    private void munculkanMakanan() {
        Random acak = new Random();
        makanan = new Point(acak.nextInt(JUMLAH_BLOK_LEBAR), acak.nextInt(jumlahBlokTinggi));
    }

    private void munculkanMakananBesar() {
        Random acak = new Random();
        makananBesar = new Point(acak.nextInt(JUMLAH_BLOK_LEBAR), acak.nextInt(jumlahBlokTinggi));
        makananBesarAktif = true;
        waktuMulaiMakananBesar = System.currentTimeMillis();
    }

    private void perbarui() {
        Point kepala = new Point(ular.get(0));

        // Menerjemahkan arah swipe menjadi angka untuk diolah C++
        float inputX = 0;
        float inputY = 0;

        switch (arahSaatIni) {
            case ATAS: inputY = -1; break;
            case BAWAH: inputY = 1; break;
            case KIRI: inputX = -1; break;
            case KANAN: inputX = 1; break;
        }

        // --- MENGGUNAKAN MESIN C++ UNTUK MENGHITUNG PERGERAKAN ---
        Utama aktivitasUtama = (Utama) getContext();
        float[] posisiBaru = aktivitasUtama.updateLogikaUlar(inputX, inputY, 1.0f);

        kepala.x += Math.round(posisiBaru[0]);
        kepala.y += Math.round(posisiBaru[1]);
        // ---------------------------------------------------------

        if (kepala.x < 0 || kepala.x >= JUMLAH_BLOK_LEBAR || kepala.y < 0 || kepala.y >= jumlahBlokTinggi) {
            akhiriPermainan();
            return;
        }

        for (Point bagian : ular) {
            if (kepala.equals(bagian)) {
                akhiriPermainan();
                return;
            }
        }

        for (Point blokir : rintangan) {
            if (kepala.equals(blokir)) {
                akhiriPermainan();
                return;
            }
        }

        ular.add(0, kepala);

        if (kepala.equals(makanan)) {
            skor += 10;
            teksSkor.post(() -> {
                teksSkor.setText("SCORE : " + skor);
            });
            efekSuara.play(idSuaraMakan, 1, 1, 0, 0, 1);
            munculkanMakanan();
            if (new Random().nextInt(10) > 7 && !makananBesarAktif) {
                munculkanMakananBesar();
            }
            if (tunda > 60) tunda -= 3;
        } else if (makananBesarAktif && kepala.equals(makananBesar)) {
            skor += 30;
            teksSkor.post(() -> {
                teksSkor.setText("SCORE : " + skor);
            });
            efekSuara.play(idSuaraMakan, 1, 1, 0, 0, 1);
            makananBesarAktif = false;
            ular.add(new Point(ular.get(ular.size() - 1)));
            ular.add(new Point(ular.get(ular.size() - 1)));
            if (tunda > 60) tunda -= 7;
        } else {
            ular.remove(ular.size() - 1);
        }

        if (makananBesarAktif && System.currentTimeMillis() - waktuMulaiMakananBesar > DURASI_MAKANAN_BESAR) {
            makananBesarAktif = false;
        }
    }

    private void akhiriPermainan() {

        efekSuara.play(idSuaraTabrak, 1, 1, 0, 0, 1);

        android.content.Intent intent =
                new android.content.Intent(getContext(), GameOver.class);

        getContext().startActivity(intent);
    }

    private void gambar() {
        if (wadahPermukaan.getSurface().isValid()) {
            kanvas = wadahPermukaan.lockCanvas();
            kanvas.drawColor(Color.parseColor("#050505"));
            kuas.setColor(Color.parseColor("#112211"));

            for (int x = 0; x < layarX; x += ukuranBlok) {
                kanvas.drawLine(x, 0, x, layarY, kuas);
            }

            for (int y = 0; y < layarY; y += ukuranBlok) {
                kanvas.drawLine(0, y, layarX, y, kuas);
            }

            if (statusSekarang == Status.BERMAIN) {
                kuas.setColor(Color.DKGRAY);
                for (Point p : rintangan) {

                    Bitmap batuResize = Bitmap.createScaledBitmap(bitmapBatu, ukuranBlok, ukuranBlok, false);
                    kanvas.drawBitmap(batuResize, p.x * ukuranBlok, p.y * ukuranBlok, null);
                    kanvas.drawRect(p.x * ukuranBlok, p.y * ukuranBlok,
                            (p.x * ukuranBlok) + ukuranBlok, (p.y * ukuranBlok) + ukuranBlok, kuas);

                }

                kuas.setColor(Color.parseColor("#00FF66"));
                for (int i = 0; i < ular.size(); i++) {

                    Point p = ular.get(i);
                    Bitmap ularResize;

                    if (i == 0) {
                        // Logika Kepala
                        Bitmap kepalaPilihan;
                        if (arahSaatIni == Arah.BAWAH) kepalaPilihan = bitmapKepalaBawah;
                        else if (arahSaatIni == Arah.KIRI) kepalaPilihan = bitmapKepalaKiri;
                        else if (arahSaatIni == Arah.KANAN) kepalaPilihan = bitmapKepalaKanan;
                        else kepalaPilihan = bitmapKepalaAtas;

                        ularResize = Bitmap.createScaledBitmap(kepalaPilihan, ukuranBlok, ukuranBlok, false);
                    } else if (i == ular.size() - 1) {
                        // Logika Ekor
                        ularResize = Bitmap.createScaledBitmap(bitmapEkor, ukuranBlok, ukuranBlok, false);
                    } else {
                        // Logika Badan
                        ularResize = Bitmap.createScaledBitmap(bitmapBadan, ukuranBlok, ukuranBlok, false);
                    }

                    // Gambar ke kanvas
                    kanvas.drawBitmap(ularResize, p.x * ukuranBlok, p.y * ukuranBlok, null);


                    Point p = ular.get(i);

                    if (i == 0) {
                        kuas.setColor(Color.parseColor("#D4FF00"));
                    } else {
                        kuas.setColor(Color.parseColor("#00FF66"));
                    }

                    kanvas.drawRect(
                            p.x * ukuranBlok,
                            p.y * ukuranBlok,
                            (p.x * ukuranBlok) + ukuranBlok,
                            (p.y * ukuranBlok) + ukuranBlok,
                            kuas
                    );
                }

                kuas.setColor(Color.parseColor("#FFD000"));
                kanvas.drawRect(makanan.x * ukuranBlok, makanan.y * ukuranBlok,
                        (makanan.x * ukuranBlok) + ukuranBlok, (makanan.y * ukuranBlok) + ukuranBlok, kuas);

                if (makananBesarAktif) {
                    kuas.setColor(Color.YELLOW);
                    kanvas.drawCircle((makananBesar.x * ukuranBlok) + (ukuranBlok / 2),
                            (makananBesar.y * ukuranBlok) + (ukuranBlok / 2), ukuranBlok / 2, kuas);
                }

                kuas.setColor(Color.parseColor("#00FF66"));
                kuas.setTextSize(65);
                kuas.setFakeBoldText(true);

                // kanvas.drawText("SKOR : " + skor, 40, 80, kuas);

            } /* else if (statusSekarang == Status.MENU) {
                kuas.setColor(Color.parseColor("#D4FF00"));
                kuas.setTextSize(130);
                kuas.setFakeBoldText(true);

                kanvas.drawText("SNAKE.EXE", layarX / 6, layarY / 3, kuas);

                kuas.setColor(Color.parseColor("#00FF66"));
                kuas.setTextSize(55);

                kanvas.drawText("TAP TO START", layarX / 4, layarY / 2, kuas);

                kuas.setColor(Color.parseColor("#FFD000"));

                kanvas.drawText("HIGH SCORE : " + skorTertinggi,
                        layarX / 5,
                        layarY / 2 + 140,
                        kuas);

            }*/ else {

                kuas.setColor(Color.parseColor("#FF4444"));
                kuas.setTextSize(130);
                kuas.setFakeBoldText(true);

                kanvas.drawText("GAME OVER", layarX / 8, layarY / 3, kuas);

                kuas.setColor(Color.WHITE);
                kuas.setTextSize(60);

                kanvas.drawText("FINAL SCORE : " + skor,
                        layarX / 4,
                        layarY / 2,
                        kuas);

                kuas.setColor(Color.parseColor("#00FF66"));

                kanvas.drawText("TAP TO RESTART",
                        layarX / 5,
                        layarY / 2 + 150,
                        kuas);
            }

            wadahPermukaan.unlockCanvasAndPost(kanvas);
        }
    }

    @Override
    protected void onSizeChanged(int l, int t, int lamaL, int lamaT) {
        super.onSizeChanged(l, t, lamaL, lamaT);
        layarX = l;
        layarY = t;
    }

    @Override
    public boolean onTouchEvent(MotionEvent peristiwa) {
        switch (peristiwa.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (statusSekarang != Status.BERMAIN) {
                    mulaiPermainan();
                }
                sentuhX = peristiwa.getX();
                sentuhY = peristiwa.getY();
                break;
            case MotionEvent.ACTION_UP:
                float selisihX = peristiwa.getX() - sentuhX;
                float selisihY = peristiwa.getY() - sentuhY;

                if (Math.abs(selisihX) > Math.abs(selisihY)) {
                    if (selisihX > 0 && arahSaatIni != Arah.KIRI) arahSaatIni = Arah.KANAN;
                    else if (selisihX < 0 && arahSaatIni != Arah.KANAN) arahSaatIni = Arah.KIRI;
                } else {
                    if (selisihY > 0 && arahSaatIni != Arah.ATAS) arahSaatIni = Arah.BAWAH;
                    else if (selisihY < 0 && arahSaatIni != Arah.BAWAH) arahSaatIni = Arah.ATAS;
                }
                break;
        }
        return true;
    }
    public void mulaiGame() {
        mulaiPermainan();
    }

    public void pauseGame() {
        statusSekarang = Status.MENU;
    }

    public void restartGame() {
        mulaiPermainan();
    }
}