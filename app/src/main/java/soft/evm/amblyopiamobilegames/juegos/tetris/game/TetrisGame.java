package soft.evm.amblyopiamobilegames.juegos.tetris.game;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.TimeUnit;

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

public class TetrisGame extends Activity implements View.OnTouchListener {

    public static final int boardWidth = 10, boardHeight = 16;

    private int[][] board = new int[boardHeight][boardWidth];

    private Shape[] shapes = new Shape[7];

    private Shape currentShape;

    private boolean primeraVez = true;

    Chronometer chronometer;

    SharedPreferences prefs, tetris, user;

    MediaPlayer put;

    ProgressDialog progressDialog;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        progressDialog.dismiss();
    }

    public int[][] getBoard(){
        return board;
    }

    @Override
    public void finish() {
        super.finish();
        GameActivity.gameOver = true;
        //MiAdMob.showInterstitialAd(); -> en el GameActivity
    }

    private void insertar() {
        Insertar.insertar = true;
        user = getSharedPreferences("Usuario", Context.MODE_PRIVATE);
        Insertar.correo=user.getString("correo", "");
        Insertar.nombre=user.getString("nombre", "");
        Insertar.nombre_juego="TETRIS";
        Insertar.score1= Shape.score;
        Insertar.score2= Shape.derribadas;
        Insertar.tiempo=chronometer.getMilis();
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

    public void setNextShape(){
        if(prefs.getBoolean("sonido",true) && !primeraVez)
            put.start();
        primeraVez = false;

        int index = (int)(Math.random()*shapes.length);

        currentShape = new Shape(shapes[index].getCoords(),
                this, shapes[index].getColor(), Shape.level);


        for(int row = 0; row < currentShape.getCoords().length; row++)
            for(int col = 0; col < currentShape.getCoords()[row].length; col++)
                if (currentShape.getCoords()[row][col] != 0)
                    if (board[row][col + 3] != 0)
                        GameActivity.gameOver = true;
    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // remove title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_tetris_game);
        tetrisView = findViewById(R.id.TetrisView);
        tetrisView.setOnTouchListener(this);
        score = findViewById(R.id.ScoreView);
        time = findViewById(R.id.TimeView);
        level = findViewById(R.id.LevelView);
        prefs = getSharedPreferences("MisPreferencias", Context.MODE_PRIVATE);
        tetris = getSharedPreferences("Tetris", Context.MODE_PRIVATE);
        ajustarJuego();
        Shape.score = 0;
        startUpdateHandler();
        put = MediaPlayer.create(this, R.raw.come);
        incicializarfiguras();
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
        YodoAds.showBannerAd(TetrisGame.this);
    }

    private void ajustarJuego() {
        Colores.ajustarColores(prefs);
        Shape.level = tetris.getInt("level", 1);
        Shape.auto = tetris.getBoolean("auto",false);
    }

    private void incicializarfiguras() {
        shapes[0] = new Shape(new int[][]{
                {1, 1, 1, 1} // IShape
        }, this, 2, Shape.level);

        shapes[1] = new Shape(new int[][]{
                {1, 1, 0},
                {0, 1, 1}   // ZShape
        }, this, 2, Shape.level);

        shapes[2] = new Shape(new int[][]{
                {0, 1, 1},
                {1, 1, 0}   // S-Shape
        }, this, 2, Shape.level);

        shapes[3] = new Shape(new int[][]{
                {1, 1, 1},
                {0, 0, 1}   // J-Shape
        }, this, 2, Shape.level);

        shapes[4] = new Shape(new int[][]{
                {1, 1, 1},
                {1, 0, 0}   // L-Shape
        }, this, 2, Shape.level);

        shapes[5] = new Shape(new int[][]{
                {1, 1, 1},
                {0, 1, 0}   // T-Shape
        }, this, 2, Shape.level);

        shapes[6] = new Shape(new int[][]{
                {1, 1},
                {1, 1}   // O-Shape
        }, this, 2, Shape.level);

        setNextShape();
    }

    private final Handler handler = new Handler();

    private long updateDelay = 100;

    private TETRISView tetrisView;

    private TextView score, time, level;

    public void OnGameLost(){
        MediaPlayer fin = MediaPlayer.create(this, R.raw.finaljuego);
        if(prefs.getBoolean("sonido",true))
            fin.start();
        Toast.makeText(this, getString(R.string.lose) + " " + Shape.score, Toast.LENGTH_SHORT).show();
        insertar();
        Shape.score = 0;
        finish();
    }

    public void startUpdateHandler(){
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(GameActivity.gameOver && GameActivity.pause)
                    finish();
                else if(!GameActivity.pause) {
                    currentShape.update();
                    if (!GameActivity.gameOver) {
                        handler.postDelayed(this, updateDelay);
                        tetrisView.setTetrisViewBoard(board, currentShape);
                        tetrisView.invalidate();
                        score.setText(getString(R.string.score_) + String.valueOf(0 + Shape.score));
                        time.setText(getString(R.string.time_) + formatearMilis(chronometer.getMilis()));
                        level.setText(getString(R.string.LEVEL_) + String.valueOf(0 + Shape.level));
                    } else
                        OnGameLost();
                }
                else
                    handler.postDelayed(this, updateDelay);
            }
        }, updateDelay);
    }

    private float prevX, prevY;

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
                        currentShape.setDeltaX(1);
                    }else{
                        // LEFT
                        currentShape.setDeltaX(-1);
                    }
                }else{
                    // UP - DOWN DIRECTION
                    if(newY > prevY){
                        //DOWN
                        currentShape.speedDown();
                        //currentShape.normalSpeed();
                    }else{
                        // UP
                        currentShape.rotate();
                    }
                }
                break;
        }
        return true;
    }

    public String formatearMilis(long milis) {
        String formato = "%02d:%02d:%02d";
        long minutosReales = TimeUnit.MILLISECONDS.toMinutes(milis);
        long segundosReales = TimeUnit.MILLISECONDS.toSeconds(milis) - TimeUnit.MILLISECONDS.toSeconds(minutosReales);
        long decimasReales = (TimeUnit.MILLISECONDS.toMillis(milis) - TimeUnit.MILLISECONDS.toMillis(minutosReales) - TimeUnit.MILLISECONDS.toSeconds(segundosReales))%1000/10;
        return String.format(formato, minutosReales, segundosReales, decimasReales);
    }

    @Override
    public void onBackPressed() {
        Pausa.context = this;
        Pausa.pausa();
    }

}
