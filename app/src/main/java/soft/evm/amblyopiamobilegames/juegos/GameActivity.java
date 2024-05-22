package soft.evm.amblyopiamobilegames.juegos;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabaseLockedException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.Purchase;
import com.yodo1.mas.Yodo1Mas;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import soft.evm.amblyopiamobilegames.BuildConfig;
import soft.evm.amblyopiamobilegames.MainActivity;
import soft.evm.amblyopiamobilegames.R;
import soft.evm.amblyopiamobilegames.SettingsActivity;
import soft.evm.amblyopiamobilegames.ads.MiAdMob;
import soft.evm.amblyopiamobilegames.ads.YodoAds;
import soft.evm.amblyopiamobilegames.analytics.MisMetricas;
import soft.evm.amblyopiamobilegames.billing.BillingManager;
import soft.evm.amblyopiamobilegames.billing.BillingProvider;
import soft.evm.amblyopiamobilegames.info.InfoActivity;
import soft.evm.amblyopiamobilegames.juegos.breaker.BreakerConfig;
import soft.evm.amblyopiamobilegames.juegos.breaker.game.BreakoutGame;
import soft.evm.amblyopiamobilegames.juegos.flappy.game.FlappyGame;
import soft.evm.amblyopiamobilegames.juegos.pong.game.Pong;
import soft.evm.amblyopiamobilegames.juegos.snake.SnakeConfig;
import soft.evm.amblyopiamobilegames.juegos.snake.game.SnakeGame;
import soft.evm.amblyopiamobilegames.juegos.space.SpaceConfig;
import soft.evm.amblyopiamobilegames.juegos.space.game.SpaceInvadersGame;
import soft.evm.amblyopiamobilegames.juegos.tetris.TetrisConfig;
import soft.evm.amblyopiamobilegames.juegos.tetris.game.TetrisGame;
import soft.evm.amblyopiamobilegames.layoutElements.Element;
import soft.evm.amblyopiamobilegames.table.Partidas;
import soft.evm.amblyopiamobilegames.table.TableViewRanking;
import soft.evm.amblyopiamobilegames.youtube.YouTubeConfig;

import static soft.evm.amblyopiamobilegames.MainActivity.N0;
import static soft.evm.amblyopiamobilegames.MainActivity.N1;
import static soft.evm.amblyopiamobilegames.MainActivity.N2;
import static soft.evm.amblyopiamobilegames.MainActivity.N3;
import static soft.evm.amblyopiamobilegames.MainActivity.no_registrado;
import static soft.evm.amblyopiamobilegames.MainActivity.si;


public class GameActivity extends Activity implements BillingProvider {

    SharedPreferences tiempo_demo_diario, user;

    public static volatile boolean gameOver = false;
    public static volatile boolean pause = false;

    public static String juegoSQL;
    static String nombre, texto;
    Class juego, config;

    TextView title;

    //final long demo_duration = 600000L;//10 minutos
    int tiempo_diario = 0;
    public static boolean dont_show_again = false;
    final int min_milisegundos = 600000;//600000

    //BBDD Mejores puntuaciones
    SharedPreferences highScores;

    //Youtube
    public static String youtube_link = YouTubeConfig.amblyo_mobile;

    //dias oferta
    public int dias = 0;

    //contamos las veces que jugamos para mostrar publicidad
    private static int jugados = 0;
    private static int flappy = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        gameOver = false;
        pause = false;

        PlayStore();

        // remove title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_game);
        Partidas.tabla = "juego";

        //hilo principal
        switchCase();
        interfaz();
        iniciarSharedDemo();
        comprobarTiempoDiario();
        iniciarDias();

        //Admob
        MiAdMob.mostrarAds(this.getWindow().getDecorView().findViewById(android.R.id.content));
    }

    @Override
    protected void onStart() {
        super.onStart();
        YodoAds.showBannerAd(GameActivity.this);
    }

    private void iniciarDias() {
        user = getSharedPreferences("Usuario", Context.MODE_PRIVATE);
        String registro = user.getString("registro", no_registrado);
        if(registro.equals(no_registrado))
            dias=0;
        else{
            try {
                long ahora = System.currentTimeMillis();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.getDefault());
                Date date = null;
                date = sdf.parse(registro);
                long registro_milis = date.getTime();
                long diferencia_milis = ahora - registro_milis;
                dias = (int) (diferencia_milis / (1000*60*60*24));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    private void comprobarTiempoDiario() {
        if(tiempo_demo_diario.getLong("tiempo", 0L) > tiempo_diario && tiempo_diario != 0)
            mostrarAlertDialog();
    }

    private void mostrarAlertDialog() throws WindowManager.BadTokenException {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(GameActivity.this);
        View mView = getLayoutInflater().inflate(R.layout.alert_dialog_tiempo_diario, null);
        CheckBox mCheckBox = mView.findViewById(R.id.checkBoxDontShowAgain);
        mBuilder.setTitle(getString(R.string.objetivo_cumplido));
        mBuilder.setMessage(getString(R.string.enhorabuena_finalizado));
        mBuilder.setView(mView);
        mBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        AlertDialog mDialog = mBuilder.create();
        mDialog.show();
        mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(compoundButton.isChecked()){
                    dont_show_again = true;
                }else{
                    dont_show_again = false;
                }
            }
        });

        if(dont_show_again){
            mDialog.hide();
        }else{
            mDialog.show();
        }
    }


    private void iniciarSharedDemo() {
        tiempo_demo_diario = getSharedPreferences("TiempoDemoDiario", Context.MODE_PRIVATE);
        long ahora = System.currentTimeMillis();
        long ultimavez = tiempo_demo_diario.getLong("fecha de hoy", System.currentTimeMillis());
        tiempo_demo_diario.getLong("tiempo", 0L);

        Calendar calendar_ahora = Calendar.getInstance();
        calendar_ahora.setTimeInMillis(ahora);
        int mYear_ahora = calendar_ahora.get(Calendar.YEAR);
        int mMonth_ahora = calendar_ahora.get(Calendar.MONTH);
        int mDay_ahora = calendar_ahora.get(Calendar.DAY_OF_MONTH);

        Calendar calendar_ultimavez = Calendar.getInstance();
        calendar_ultimavez.setTimeInMillis(ultimavez);
        int mYear_ultimavez = calendar_ultimavez.get(Calendar.YEAR);
        int mMonth_ultimavez = calendar_ultimavez.get(Calendar.MONTH);
        int mDay_ultimavez = calendar_ultimavez.get(Calendar.DAY_OF_MONTH);

        if(mYear_ahora != mYear_ultimavez || mMonth_ahora != mMonth_ultimavez || mDay_ahora != mDay_ultimavez){
            SharedPreferences.Editor editor = tiempo_demo_diario.edit();
            editor.putLong("fecha de hoy", ahora);
            editor.putLong("tiempo", 0);
            editor.apply();
        }

        tiempo_diario = tiempo_demo_diario.getInt("tiempo_diario", 0) * min_milisegundos;

    }

    private void switchCase() {
        switch (MainActivity.juego){
            case "Snake":
                nombre = getResources().getString(R.string.snake);
                juegoSQL = "Snake";
                texto = getResources().getString(R.string.snakeR);
                SharedPreferences snake = getSharedPreferences("Snake", Context.MODE_PRIVATE);
                snake.getInt("velocidad",50);
                snake.getBoolean("auto",false);
                snake.getBoolean("muros",true);
                juego= SnakeGame.class;
                config= SnakeConfig.class;
                youtube_link = YouTubeConfig.snake;
                break;
            case "Tetris":
                nombre = getResources().getString(R.string.tetris);
                juegoSQL = "Tetris";
                texto = getResources().getString(R.string.tetrisR);
                juego= TetrisGame.class;
                config= TetrisConfig.class;
                youtube_link = YouTubeConfig.tetris;
                break;
            case "Flappy":
                nombre = getResources().getString(R.string.flappy);
                juegoSQL = "Flappy";
                texto = getResources().getString(R.string.flappyR);
                juego= FlappyGame.class;
                findViewById(R.id.buttonActivityConfiguracion).setVisibility(View.GONE);
                youtube_link = YouTubeConfig.flappy;
                break;
            case "Breaker":
                nombre = getResources().getString(R.string.breaker);
                juegoSQL = "Breaker";
                texto = getResources().getString(R.string.breakeR);
                juego= BreakoutGame.class;
                config= BreakerConfig.class;
                youtube_link = YouTubeConfig.breaker;
                break;
            case "Space Invaders":
                nombre = getResources().getString(R.string.space);
                juegoSQL = "Space";
                texto = getResources().getString(R.string.spaceR);
                juego = SpaceInvadersGame.class;
                config = SpaceConfig.class;
                youtube_link = YouTubeConfig.space;
                break;
            case "Pong":
                nombre = getResources().getString(R.string.pong);
                juegoSQL = "Pong";
                texto = getResources().getString(R.string.pongR);
                juego= Pong.class;
                //config= PongConfig.class;
                findViewById(R.id.buttonActivityConfiguracion).setVisibility(View.GONE);
                youtube_link = YouTubeConfig.pong;
                break;
        }
    }

    private void interfaz() {
        titulo();
    }

    private void titulo() {
        title = findViewById(R.id.textViewActivityGameTitle);
        title.setText(MainActivity.juego);
        new Element(getApplicationContext()).setTtileGameName(title);
    }

    public void iniciarJuego(View view) {
        try {
            if (onBillingClientSetupFinished)
                if (isPremiumPurchased() && premiumKey.equals(BuildConfig.PREMIUM_KEY))
                    jugar();
                else if(esPrimeraVez())
                    jugar();
                else if(siEsFlappyCadaDosVeces())
                    jugar();
                else
                    openDialogPremium();
        }catch (Exception e){
            e.printStackTrace();
            Log.e("38", "Error");
        }
    }

    private boolean esPrimeraVez() {
        if(dias > N0)
            return false;
        else if(jugados > 0)
            return false;
        else
            return true;
    }

    private boolean siEsFlappyCadaDosVeces() {
        // no simplificar el condicional (warning de android studio)
        if(nombre.equals(getResources().getString(R.string.flappy)) && flappy%2==0)
            return true;
        else
            return false;
    }

    public void iniciarConfig(View view) {
        try {
            iniConfig();
        } catch (NullPointerException e){
            //version 85
        }
    }

    public void openDialogPremium() {
        final Dialog dialog = new Dialog(this); // Context, this, etc.
        dialog.setContentView(R.layout.dialog_premium);
        dialog.setTitle(R.string.dialog_title);
        dialog.show();
        iniciarBotonesDialog(dialog);
    }

    public void jugar() {
        jugados++;
        if(nombre.equals(getResources().getString(R.string.flappy)))
            flappy++;

        //metricas
        MisMetricas.firstUserExperienceGeneral(this, MisMetricas.FIRST_PLAY);


        gameOver = false;
        Intent i = new Intent(this, juego);
        startActivity(i);
    }

    private void iniConfig() {
        Intent i = new Intent(this, config);
        startActivity(i);
    }

    public void purchase() {
        getBillingManager().initiatePurchaseFlow(PremiumSKU, BillingClient.SkuType.INAPP);
    }

    public void purchase2() {
        getBillingManager().initiatePurchaseFlow(PremiumSKU2, BillingClient.SkuType.INAPP);
    }

    public void purchase3() {
        getBillingManager().initiatePurchaseFlow(PremiumSKU3, BillingClient.SkuType.INAPP);
    }

    public void purchase4() {
        getBillingManager().initiatePurchaseFlow(PremiumSKU4, BillingClient.SkuType.INAPP);
    }

    private void iniciarBotonesDialog(final Dialog dialog) {
        ImageButton purchase = (ImageButton) dialog.findViewById(R.id.dialog_purchase);
        ImageButton watch_video = (ImageButton) dialog.findViewById(R.id.dialog_video);
        purchase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if(dias < N1)
                    purchase();
                else if(dias < N2)
                    purchase2();
                else if(dias < N3)
                    purchase3();
                else
                    purchase4();
            }
        });
        watch_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                boolean hasAds = Yodo1Mas.getInstance().isRewardedAdLoaded();
                if (hasAds) {
                    Yodo1Mas.getInstance().showRewardedAd(GameActivity.this);
                    jugar();
                    /*, new VideoCallback() {
                        @Override
                        public void onVideoClosed(boolean isFinished) {
                            if(isFinished){
                                jugar();
                            }
                        }

                        @Override
                        public void onVideoShow() {

                        }

                        @Override
                        public void onVideoShowFailed(AdErrorCode errorCode) {

                        }

                        @Override
                        public void onVideoClicked() {

                        }
                    });
                }*/
                /*try {
                    MiAdMob.mRewardedVideoAd.setRewardedVideoAdListener(new RewardedVideoAdListener() {
                        @Override
                        public void onRewardedVideoAdLoaded() {

                        }

                        @Override
                        public void onRewardedVideoAdOpened() {

                        }

                        @Override
                        public void onRewardedVideoStarted() {

                        }

                        @Override
                        public void onRewardedVideoAdClosed() {
                            // Load the next rewarded video ad.
                            MiAdMob.mRewardedVideoAd.loadAd(MiAdMob.mRewardedVideoAd_ID, new AdRequest.Builder().build());
                        }

                        @Override
                        public void onRewarded(RewardItem rewardItem) {

                        }

                        @Override
                        public void onRewardedVideoAdLeftApplication() {

                        }

                        @Override
                        public void onRewardedVideoAdFailedToLoad(int i) {

                        }

                        @Override
                        public void onRewardedVideoCompleted() {
                            jugar();
                        }

                    });
                    MiAdMob.showRewardedVideoAd();
                }catch(NullPointerException e){
                    e.printStackTrace();
                    //69 -> 70
                }*/
                }
            }
        });
    }

    public void iniciarReglas(View view) {
        Intent i = new Intent(this, Reglas.class);
        startActivity(i);
    }

    public void iniciarRanking(View view) {
        Intent i = new Intent(this, TableViewRanking.class);
        startActivity(i);
    }

    public void go_back(View view) {
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();

        //BillingManager
        // Note: We query purchases in onResume() to handle purchases completed while the activity
        // is inactive. For example, this can happen if the activity is destroyed during the
        // purchase flow. This ensures that when the activity is resumed it reflects the user's
        // current purchases.
        if (mBillingManager != null
                && mBillingManager.getBillingClientResponseCode() == BillingClient.BillingResponseCode.OK) {
            mBillingManager.queryPurchases();
        }

        //Insertar partida
        insertar();
        new Thread() {
            public void run() {
                if (!(!gameOver && !pause)) {
                    ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                    Insertar.internet = networkInfo != null && networkInfo.isConnected();
                    try {
                        Insertar.insertar(getApplicationContext());
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    } catch (SQLiteDatabaseLockedException e) {
                        e.printStackTrace();
                        //error 58
                    }
                }
            }
        }.start();

        //mostrar anuncio
        if(gameOver || pause)
            if(!isPremiumPurchased()) {
                //MiAdMob.showInterstitialAd();
                YodoAds.showInterstitialAd(GameActivity.this);
            }

        gameOver = false;
        pause = false;

        comprobarTiempoDiario();

        YodoAds.mostrarAds(this.getWindow().getDecorView().findViewById(android.R.id.content));
    }

    public void insertar(){
        if (gameOver && !pause) {
            if (!user.getString("correo", no_registrado).equals(no_registrado))
                new Thread() {
                    public void run() {
                        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                        Insertar.internet = networkInfo != null && networkInfo.isConnected();
                        try {
                            Insertar.insertar(getApplicationContext());
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        } catch (SQLiteDatabaseLockedException e) {
                            e.printStackTrace();
                            //error 58
                        }
                    }
                }.start();
            else
                guardarPartidaDialog();
        }
    }

    public void guardarPartidaDialog(){
        new AlertDialog.Builder(this)
                //set icon
                .setIcon(android.R.drawable.ic_dialog_info)
                //set title
                .setTitle(R.string.menu_guardar)
                //set message
                .setMessage(R.string.quieres_guardar)
                //set positive button
                .setPositiveButton(R.string.guardar, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        MainActivity.accountPicker = true;
                        finish();
                    }
                })
                //set negative button
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        seguro();
                    }
                })
                .show();
    }

    public void seguro() {
        new AlertDialog.Builder(this)
                //set icon
                .setIcon(android.R.drawable.ic_dialog_alert)
                //set title
                .setTitle(R.string.seguro)
                //set message
                .setMessage(R.string.no_se_guarda)
                //set positive button
                .setPositiveButton(R.string.si, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                })
                //set negative button
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        guardarPartidaDialog();
                    }
                })
                .show();
    }

    public void verOpciones(View view) {
        Intent i = new Intent(this, SettingsActivity.class);
        startActivity(i);
    }

    /**
     * Aquí nos comunicamos con el play store
     */

    // Debug tag, for logging
    private static final String TAG = "GameActivity";

    private BillingManager mBillingManager;

    public static boolean mIsPremium;

    private boolean onBillingClientSetupFinished = false;

    final String PremiumSKU = "premium";
    final String PremiumSKU2 = "premium2";
    final String PremiumSKU3 = "premium3";
    final String PremiumSKU4 = "premium4";

    private String premiumKey;

    private void PlayStore() {

        // Create and initialize BillingManager which talks to BillingLibrary
        mBillingManager = new BillingManager(this, new UpdateListener());

    }

    public void verInfo(View view) {
        Intent i = new Intent(this, InfoActivity.class);
        startActivity(i);
    }

    /**
     * UpdateListener
     */

    private class UpdateListener implements BillingManager.BillingUpdatesListener {


        @Override
        public void onBillingClientSetupFinished() {
            onBillingClientSetupFinished = true;
        }

        @Override
        public void onConsumeFinished(String token, int result) {

        }

        @Override
        public void onPurchasesUpdated(List<Purchase> purchases) {
            mIsPremium = false;

            for (Purchase purchase : purchases) {
                if (PremiumSKU.equals(purchase.getSku())) {
                    Log.d(TAG, "You are premium now! Congratulations!!!");
                    mIsPremium = true;
                    premiumKey = BuildConfig.PREMIUM_KEY;
                }
                if (PremiumSKU2.equals(purchase.getSku())) {
                    Log.d(TAG, "You are premium now! Congratulations!!!");
                    mIsPremium = true;
                    premiumKey = BuildConfig.PREMIUM_KEY;
                }
                if (PremiumSKU3.equals(purchase.getSku())) {
                    Log.d(TAG, "You are premium now! Congratulations!!!");
                    mIsPremium = true;
                    premiumKey = BuildConfig.PREMIUM_KEY;
                }
                if (PremiumSKU4.equals(purchase.getSku())) {
                    Log.d(TAG, "You are premium now! Congratulations!!!");
                    mIsPremium = true;
                    premiumKey = BuildConfig.PREMIUM_KEY;
                }
            }

            if(mIsPremium){
                user = getSharedPreferences("Usuario", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = user.edit();
                editor.putString("premium", si);
                editor.apply();
            }
        }
    }

    /**
     * implements BillingProvider
     */

    @Override
    public BillingManager getBillingManager() {
        return mBillingManager;
    }

    @Override
    public boolean isPremiumPurchased() {
        return mIsPremium;
    }


    /**
     * override methods
     */

    //ESTE MÉTODO ON RESUME ENCARGADO PARA BILLING MANAGER. NO BORRAR PARA TENERLO CÓMO BORRADOR
    /*@Override
    protected void onResume() {
        super.onResume();
        // Note: We query purchases in onResume() to handle purchases completed while the activity
        // is inactive. For example, this can happen if the activity is destroyed during the
        // purchase flow. This ensures that when the activity is resumed it reflects the user's
        // current purchases.
        if (mBillingManager != null
                && mBillingManager.getBillingClientResponseCode() == BillingClient.BillingResponse.OK) {
            mBillingManager.queryPurchases();
        }
    }*/

    @Override
    public void onDestroy() {
        Log.d(TAG, "Destroying helper.");
        if (mBillingManager != null) {
            mBillingManager.destroy();
        }
        super.onDestroy();
    }

    ////////////
    //Youtube
    public void reproducirVideo(View view) {
        Intent intent = new Intent(this, YoutubeActivity.class);
        startActivity(intent);
    }

    @Override
    public void finish() {
        super.finish();
        //MiAdMob.showInterstitialAd(); -> Se debe emplazar como máximo un anuncio de este tipo por cada dos acciones que se realicen dentro de la aplicación. Tenga en cuenta que este requisito también se aplica cuando el usuario hace clic en el botón Atrás dentro de ella.
    }

}

