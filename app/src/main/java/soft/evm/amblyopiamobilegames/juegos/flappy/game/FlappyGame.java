package soft.evm.amblyopiamobilegames.juegos.flappy.game;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import java.util.ArrayList;

import soft.evm.amblyopiamobilegames.MainActivity;
import soft.evm.amblyopiamobilegames.R;
import soft.evm.amblyopiamobilegames.ads.MiAdMob;
import soft.evm.amblyopiamobilegames.ads.YodoAds;
import soft.evm.amblyopiamobilegames.juegos.Chronometer;
import soft.evm.amblyopiamobilegames.juegos.Colores;
import soft.evm.amblyopiamobilegames.juegos.GameActivity;
import soft.evm.amblyopiamobilegames.juegos.Insertar;
import soft.evm.amblyopiamobilegames.juegos.Pausa;

public class FlappyGame extends Activity implements View.OnTouchListener {

    FlappyView flappyView;
    Bird bird;
    private ArrayList<Rect> rects;
    private int scroll;
    public static int saltos = 0;
    private int jumps = 0;

    //miliseconds
    private long tiempo = 0;

    public static int width, height;
    public int ancho, alto;

    Chronometer chronometer;

    SharedPreferences prefs, flappy, user;

    MediaPlayer salto;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // remove title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Display display = getWindowManager().getDefaultDisplay();
        width = display.getWidth();
        height = display.getHeight();
        alto = height/6;
        ancho = width/9;
        setContentView(R.layout.activity_flappy_game);
        flappyView = findViewById(R.id.flappyView);
        flappyView.setOnTouchListener(this);
        bird = new Bird(width);
        rects = new ArrayList<>();
        prefs = getSharedPreferences("MisPreferencias", Context.MODE_PRIVATE);
        flappy = getSharedPreferences("Flappy", Context.MODE_PRIVATE);
        Colores.ajustarColores(prefs);
        saltos = 0;
        chronometer = new Chronometer();
        new Thread(chronometer).start();
        startUpdateHandler();
        salto = MediaPlayer.create(this, R.raw.come);
        progressDialog = new ProgressDialog(this);

        //Admob
        MiAdMob.mostrarAds(this.getWindow().getDecorView().findViewById(android.R.id.content));
    }

    @Override
    protected void onResume() {
        super.onResume();
        YodoAds.mostrarAds(this.getWindow().getDecorView().findViewById(android.R.id.content));
    }

    @Override
    protected void onStart() {
        super.onStart();
        YodoAds.showBannerAd(FlappyGame.this);
    }

    private Handler handler = new Handler();
    int updateDelay = 200;//para cargar el juego, algunos dispositivos necesitan menos carga

    public void startUpdateHandler(){
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(GameActivity.gameOver && GameActivity.pause)
                    finish();
                else if (GameActivity.gameOver){
                    tiempo = chronometer.getMilis();
                    saltos /= 2;
                    mostrar();
                    insertar();
                    saltos = 0;
                    finish();
                }
                else if(!GameActivity.pause) {
                    update();
                    handler.postDelayed(this, updateDelay);
                    updateDelay = 1;//poner a 1
                    flappyView.setBird(bird, rects);
                    flappyView.invalidate();
                }
                else
                    handler.postDelayed(this, updateDelay);
            }
        }, updateDelay);
    }

    private void insertar() {
        Insertar.insertar = true;
        user = getSharedPreferences("Usuario", Context.MODE_PRIVATE);
        Insertar.correo=user.getString("correo", "");
        Insertar.nombre=user.getString("nombre", "");
        Insertar.nombre_juego="FLAPPY";
        Insertar.score1=saltos;
        Insertar.score2=jumps/2;
        Insertar.tiempo=tiempo;
        startProgressDialog();
    }

    public void startProgressDialog() throws WindowManager.BadTokenException {
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getString(R.string.cargando));
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        try {
            progressDialog.show();
        }catch (WindowManager.BadTokenException e){
            e.printStackTrace();
            Log.e("39", "Error");
        }
    }


    private void mostrar() {
        MediaPlayer fin = MediaPlayer.create(this, R.raw.finaljuego);
        if(prefs.getBoolean("sonido",true))
            fin.start();
        Toast.makeText(this, getString(R.string.lose) + " " + saltos, Toast.LENGTH_SHORT).show();
    }

    private void update() {
        bird.physics();
        if(bird.y > height || bird.y+bird.radio < 0) {
            GameActivity.gameOver = true;
            return;
        }
        if(scroll % 90 == 0) {
            Rect r = new Rect();
            int h2 = (int) ((Math.random()*height)/5f + (0.2f)*height);
            r.set(width-ancho,0,width, h2);
            Rect r2 = new Rect();
            r2.set(width-ancho,h2 + alto, width, height);

            rects.add(r);
            rects.add(r2);
        }
        ArrayList<Rect> toRemove = new ArrayList<>();
        for (Rect r : rects) {
            r.left -= width/175;
            r.right -= width/175;
            if (r.right + r.width() <= 0) {
                toRemove.add(r);
            }
            if (r.contains((int)bird.x, (int)bird.y)) {
                GameActivity.gameOver = true;
            }
            else if(bird.x == r.right){
                saltos++;
                /*if((saltos/2)%10==0 && saltos > 0)
                    //updateDelay--;//autoincrementar
                    velocidad+=5;*/
            }
        }
        rects.removeAll(toRemove);
        scroll++;
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if(prefs.getBoolean("sonido",true))
            salto.start();
        jumps++;
        bird.jump();
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        progressDialog.dismiss();
    }

    @Override
    public void onBackPressed() {
        Pausa.context = this;
        Pausa.pausa();
    }

    @Override
    public void finish() {
        super.finish();
        //MiAdMob.showInterstitialAd(); -> en el GameActivity
    }

}