package soft.evm.amblyopiamobilegames.juegos;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import soft.evm.amblyopiamobilegames.MainActivity;
import soft.evm.amblyopiamobilegames.R;
import soft.evm.amblyopiamobilegames.SettingsActivity;
import soft.evm.amblyopiamobilegames.ads.MiAdMob;
import soft.evm.amblyopiamobilegames.ads.YodoAds;
import soft.evm.amblyopiamobilegames.juegos.breaker.BreakerConfig;
import soft.evm.amblyopiamobilegames.juegos.flappy.game.FlappyGame;
import soft.evm.amblyopiamobilegames.layoutElements.Element;

public class Reglas extends Activity {

    TextView nombre, reglas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // remove title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_reglas);
        iniciar();
        pintarFondo();

        //Admob
        MiAdMob.mostrarAds(this.getWindow().getDecorView().findViewById(android.R.id.content));
    }

    private void pintarFondo() {
        SharedPreferences prefs = getSharedPreferences("MisPreferencias", Context.MODE_PRIVATE);
        if(prefs.getInt("fondo", 0) == 0)
            getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        else
            getWindow().setBackgroundDrawable(new ColorDrawable(Color.BLACK));
    }

    private void iniciar() {
        Element element = new Element(getApplicationContext());
        nombre = findViewById(R.id.textViewActivityReglasNombreJuego);
        reglas = findViewById(R.id.textViewActivityReglaReglas);
        nombre.setText(GameActivity.nombre);
        reglas.setText(GameActivity.texto);
        element.setRulesName(nombre);
        element.setRulesText(reglas);
    }

    public void go_back(View view) {
        finish();
    }

    @Override
    public void finish() {
        super.finish();
        //MiAdMob.showInterstitialAd();
    }

    @Override
    protected void onStart() {
        super.onStart();
        YodoAds.showBannerAd(Reglas.this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        YodoAds.showInterstitialAd(Reglas.this);
        YodoAds.mostrarAds(this.getWindow().getDecorView().findViewById(android.R.id.content));
    }

}
