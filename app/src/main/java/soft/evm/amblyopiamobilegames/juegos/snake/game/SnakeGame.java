package soft.evm.amblyopiamobilegames.juegos.snake.game;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import soft.evm.amblyopiamobilegames.MainActivity;
import soft.evm.amblyopiamobilegames.R;
import soft.evm.amblyopiamobilegames.ads.MiAdMob;
import soft.evm.amblyopiamobilegames.ads.YodoAds;
import soft.evm.amblyopiamobilegames.juegos.Chronometer;
import soft.evm.amblyopiamobilegames.juegos.Colores;
import soft.evm.amblyopiamobilegames.juegos.GameActivity;
import soft.evm.amblyopiamobilegames.juegos.Insertar;
import soft.evm.amblyopiamobilegames.juegos.Pausa;
import soft.evm.amblyopiamobilegames.juegos.flappy.game.FlappyGame;
import soft.evm.amblyopiamobilegames.juegos.snake.SnakeConfig;
import soft.evm.amblyopiamobilegames.juegos.snake.game.engine.GameEngine;
import soft.evm.amblyopiamobilegames.juegos.snake.game.enums.Direction;
import soft.evm.amblyopiamobilegames.juegos.snake.game.enums.GameState;
import soft.evm.amblyopiamobilegames.juegos.snake.game.views.SnakeView;

public class SnakeGame extends Activity implements View.OnTouchListener {

    private GameEngine gameEngine;
    private SnakeView snakeView;
    private final Handler handler = new Handler();
    private long updateDelay = 1200;

    private float prevX, prevY;

    SharedPreferences prefs, snake, user;

    private boolean auto;

    private MediaPlayer fin;
    private int velocidad;

    public static Chronometer chronometer;

    ProgressDialog progressDialog;

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

        setContentView(R.layout.activity_snake_game);

        snakeView = findViewById(R.id.snakeView);
        snakeView.setOnTouchListener(this);

        startUpdateHandler();

        prefs = getSharedPreferences("MisPreferencias", Context.MODE_PRIVATE);
        snake = getSharedPreferences("Snake", Context.MODE_PRIVATE);
        pintarFondo();
        ajustarJuego();
        velocidad = snake.getInt("velocidad",5);
        updateDelay = updateDelay - velocidad * 100;
        auto = snake.getBoolean("auto",false);
        fin = MediaPlayer.create(this, R.raw.finaljuego);

        gameEngine = new GameEngine(this, snake.getBoolean("muros",true), prefs.getBoolean("sonido",true), velocidad);
        gameEngine.initGame();
        chronometer = new Chronometer();
        new Thread(chronometer).start();
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
        YodoAds.showBannerAd(SnakeGame.this);
    }

    private void pintarFondo() {
        if(prefs.getInt("fondo", 0) == 0)
            getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        else
            getWindow().setBackgroundDrawable(new ColorDrawable(Color.BLACK));
    }

    private void ajustarJuego() {
        Colores.ajustarColores(prefs);
        snake.getBoolean("auto",false);
        snake.getBoolean("muros",true);
    }

    public void startUpdateHandler(){
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(GameActivity.pause)
                    gameEngine.setCurrentGameState(GameState.Ready);
                else
                    gameEngine.setCurrentGameState(GameState.Running);
                if(gameEngine.getCurrentGameState() == GameState.Ready && !GameActivity.gameOver){
                    handler.postDelayed(this, updateDelay);
                }
                else {
                    gameEngine.Update();
                    if (gameEngine.getCurrentGameState() == GameState.Running) {
                        if (auto && updateDelay > 200) {
                            handler.postDelayed(this, updateDelay--);
                            velocidad = (int) ((1200 - updateDelay) / 100);
                            gameEngine.setVelocidad(velocidad);
                        } else
                            handler.postDelayed(this, updateDelay);
                    }
                }
                snakeView.setSnakeViewMap(gameEngine.getMap());
                snakeView.invalidate();
                if (gameEngine.getCurrentGameState() == GameState.Lost) {
                    OnGameLost();
                    GameActivity.gameOver = true;
                }
                if(GameActivity.gameOver)
                    finish();
            }
        }, updateDelay);
    }

    private void OnGameLost(){
        if(gameEngine.isSonido())
            fin.start();
        Toast.makeText(this, getString(R.string.has_comido) + " " + gameEngine.getManzanas() + " " + getString(R.string.manzanas), Toast.LENGTH_SHORT).show();
        insertar();
    }

    private void insertar() {
        Insertar.insertar = true;
        user = getSharedPreferences("Usuario", Context.MODE_PRIVATE);
        Insertar.correo=user.getString("correo", "");
        Insertar.nombre=user.getString("nombre", "");
        Insertar.score1=gameEngine.getScore();
        Insertar.score2=gameEngine.getManzanas();
        Insertar.tiempo=chronometer.getMilis();
        if(GameEngine.muros)
            Insertar.nombre_juego="SNAKE (CON MUROS)";
        else
            Insertar.nombre_juego="SNAKE (SIN MUROS)";
        startProgressDialog();
    }

    public void startProgressDialog(){
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

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        switch (motionEvent.getAction()){
            case MotionEvent.ACTION_DOWN:
                prevX = motionEvent.getX();
                prevY = motionEvent.getY();
                break;
            case MotionEvent.ACTION_UP:
                float newX = motionEvent.getX();
                float newY = motionEvent.getY();

                //Calculate where ve swiped
                if(Math.abs(newX - prevX) > Math.abs(newY - prevY)){
                    // LEFT - RIGHT DIRECTION
                    if(newX > prevX){
                        // RIGHT
                        gameEngine.UpdateDirection(Direction.East);
                    }else{
                        // LEFT
                        gameEngine.UpdateDirection(Direction.West);
                    }
                }else{
                    // UP - DOWN DIRECTION
                    if(newY > prevY){
                        //DOWN
                        gameEngine.UpdateDirection(Direction.South);

                    }else{
                        // UP
                        gameEngine.UpdateDirection(Direction.North);
                    }
                }
                break;
        }
        return true;
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
