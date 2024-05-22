package soft.evm.amblyopiamobilegames.juegos.breaker.game;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Display;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import soft.evm.amblyopiamobilegames.MainActivity;
import soft.evm.amblyopiamobilegames.R;
import soft.evm.amblyopiamobilegames.ads.MiAdMob;
import soft.evm.amblyopiamobilegames.juegos.Chronometer;
import soft.evm.amblyopiamobilegames.juegos.Colores;
import soft.evm.amblyopiamobilegames.juegos.GameActivity;
import soft.evm.amblyopiamobilegames.juegos.Insertar;
import soft.evm.amblyopiamobilegames.juegos.Pausa;

public class BreakoutGame extends Activity {

    SharedPreferences prefs, breaker, user;

    Chronometer chronometer;

    BreakoutView breakoutView;

    ProgressDialog progressDialog;

    static boolean sonido = true;

    // The score
    static int score = 0;

    // Lives
    static int lives = 10;

    //Levels
    static int level = 1;

    // Get a Display object to access screen details
    static Display display;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        progressDialog.dismiss();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // remove title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Initialize gameView and set it as the view
        score = 0;
        // Lives
        lives = 10;
        display = getWindowManager().getDefaultDisplay();
        breaker = getSharedPreferences("Breaker", Context.MODE_PRIVATE);
        level = breaker.getInt("level",1);
        prefs = getSharedPreferences("MisPreferencias", Context.MODE_PRIVATE);
        Colores.ajustarColores(prefs);
        sonido = prefs.getBoolean("sonido",true);
        breakoutView = new BreakoutView(this);
        setContentView(breakoutView);
        chronometer = new Chronometer();
        progressDialog = new ProgressDialog(this);

        //iniciamos hilos
        startUpdateHandler();
        new Thread(chronometer).start();
        new Thread(breakoutView).start();
    }

    private Handler handler = new Handler();
    int updateDelay = 1000;

    public void startUpdateHandler() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (GameActivity.gameOver)
                    if(!GameActivity.pause)
                        onGameLost();
                    else
                        finish();
                else
                    handler.postDelayed(this, updateDelay);
            }
        }, updateDelay);
    }

    private void onGameLost() {
        if(sonido) {
            MediaPlayer fin = MediaPlayer.create(this, R.raw.finaljuego);
            fin.start();
        }
        Toast.makeText(this, "Score " + score, Toast.LENGTH_SHORT).show();
        insertar();
        finish();
    }

    private void insertar() {
        Insertar.insertar = true;
        user = getSharedPreferences("Usuario", Context.MODE_PRIVATE);
        Insertar.correo=user.getString("correo", "");
        Insertar.nombre=user.getString("nombre", "");
        Insertar.nombre_juego="BREAKER";
        Insertar.score1=score;
        Insertar.score2=level;
        Insertar.tiempo=chronometer.getMilis();
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

    // This method executes when the player starts the game
    /*@Override
    protected void onResume() {
        super.onResume();

        // Tell the gameView resume method to execute
        breakoutView.resume();
    }

    // This method executes when the player quits the game
    @Override
    protected void onPause() {
        super.onPause();

        // Tell the gameView pause method to execute
        breakoutView.pause();
    }*/

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
// This is the end of the BreakoutGame class