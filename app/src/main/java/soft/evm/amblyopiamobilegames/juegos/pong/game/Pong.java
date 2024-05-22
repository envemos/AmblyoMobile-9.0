package soft.evm.amblyopiamobilegames.juegos.pong.game;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import soft.evm.amblyopiamobilegames.MainActivity;
import soft.evm.amblyopiamobilegames.R;
import soft.evm.amblyopiamobilegames.ads.MiAdMob;
import soft.evm.amblyopiamobilegames.juegos.Colores;
import soft.evm.amblyopiamobilegames.juegos.GameActivity;
import soft.evm.amblyopiamobilegames.juegos.Pausa;

/**
 * Main activity of Pong game.
 */
public class Pong extends Activity {

    public static int screenX, screenY;

    private void iniciarScreen()
    {
        // Get a Display object to access screen details
        Display display = getWindowManager().getDefaultDisplay();
        // Load the resolution into a Point object
        Point size = new Point();
        display.getSize(size);
        screenX = size.x;
        screenY = size.y;
    }

    private static final int MENU_NEW_GAME = 1;
    private static final int MENU_RESUME = 2;
    private static final int MENU_EXIT = 3;

    private PongThread mGameThread;

    SharedPreferences prefs;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        prefs = getSharedPreferences("MisPreferencias", Context.MODE_PRIVATE);

        Colores.ajustarColores(prefs);

        iniciarScreen();

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_pong_game);

        final PongView mPongView = (PongView) findViewById(R.id.main);
        mPongView.setStatusView((TextView) findViewById(R.id.status));
        mPongView.setScoreView((TextView) findViewById(R.id.score));

        TextView textViewStatus = findViewById(R.id.status);
        textViewStatus.setTextColor(Colores.getOjo_vago());

        mGameThread = mPongView.getGameThread();
        if (savedInstanceState == null) {
            mGameThread.setState(PongThread.STATE_READY);
        } else {
            mGameThread.restoreState(savedInstanceState);
        }

        //handler
        startUpdateHandler();

    }

    private final Handler handler = new Handler();

    private long updateDelay = 1000;

    public void startUpdateHandler(){
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(GameActivity.gameOver) {
                    finish();
                }
                else
                    handler.postDelayed(this, updateDelay);
            }
        }, updateDelay);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mGameThread.pause();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mGameThread.saveState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(0, MENU_NEW_GAME, 0, R.string.menu_new_game);
        menu.add(0, MENU_RESUME, 0, R.string.menu_resume);
        menu.add(0, MENU_EXIT, 0, R.string.menu_exit);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case MENU_NEW_GAME:
                mGameThread.startNewGame();
                return true;
            case MENU_EXIT:
                finish();
                return true;
            case MENU_RESUME:
                mGameThread.unPause();
                return true;
        }
        return false;
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
