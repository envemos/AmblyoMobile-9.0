package soft.evm.amblyopiamobilegames.juegos.space.game;

import android.app.Activity;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.view.Display;

import soft.evm.amblyopiamobilegames.MainActivity;
import soft.evm.amblyopiamobilegames.ads.MiAdMob;
import soft.evm.amblyopiamobilegames.juegos.GameActivity;
import soft.evm.amblyopiamobilegames.juegos.Pausa;

public class SpaceInvadersGame extends Activity {

    // spaceInvadersView will be the view of the game
    // It will also hold the logic of the game
    // and respond to screen touches as well
    SpaceInvadersView spaceInvadersView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //DEJAR ASÍ PARA UN BUEN REINICIO DEL JUEGO
        GameActivity.pause=true;
        GameActivity.gameOver=false;

        // Get a Display object to access screen details
        Display display = getWindowManager().getDefaultDisplay();
        // Load the resolution into a Point object
        Point size = new Point();
        display.getSize(size);

        // Initialize gameView and set it as the view
        spaceInvadersView = new SpaceInvadersView(this, size.x, size.y);
        setContentView(spaceInvadersView);

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
                    spaceInvadersView.postInvalidate();
                    spaceInvadersView.clearAnimation();
                    spaceInvadersView.destroyDrawingCache();
                    finish();
                }
                else
                    handler.postDelayed(this, updateDelay);
            }
        }, updateDelay);
    }

    // This method executes when the player starts the game
    @Override
    protected void onResume() {
        super.onResume();

        // Tell the gameView resume method to execute
        spaceInvadersView.resume();
    }

    // This method executes when the player quits the game
    @Override
    protected void onPause() {
        super.onPause();

        // Tell the gameView pause method to execute
        spaceInvadersView.pause();
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
