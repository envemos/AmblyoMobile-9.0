package soft.evm.amblyopiamobilegames.juegos.breaker;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.formats.NativeAdOptions;
import com.google.android.gms.ads.formats.UnifiedNativeAd;

import soft.evm.amblyopiamobilegames.MainActivity;
import soft.evm.amblyopiamobilegames.R;
import soft.evm.amblyopiamobilegames.SettingsActivity;
import soft.evm.amblyopiamobilegames.ads.MiAdMob;
import soft.evm.amblyopiamobilegames.ads.YodoAds;
import soft.evm.amblyopiamobilegames.juegos.flappy.game.FlappyGame;
import soft.evm.amblyopiamobilegames.layoutElements.Element;


public class BreakerConfig extends Activity implements AdapterView.OnItemSelectedListener {

    SeekBar SKnivel;
    SharedPreferences breaker;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // remove title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_breaker_config);
        incializar();

        //Admob
        MiAdMob.mostrarAds(this.getWindow().getDecorView().findViewById(android.R.id.content));
    }

    @Override
    protected void onResume() {
        super.onResume();
        YodoAds.showInterstitialAd(BreakerConfig.this);
        YodoAds.mostrarAds(this.getWindow().getDecorView().findViewById(android.R.id.content));
    }

    private void incializar() {
        breaker = getSharedPreferences("Breaker", Context.MODE_PRIVATE);
        editor = breaker.edit();
        SKnivel = findViewById(R.id.seekBarBreakerConfigVelocidad);
        TextView t = findViewById(R.id.textViewBreakerConfigNivel);
        SKnivel.setProgress(breaker.getInt("level",1));
        t.setText("Nivel inicial: " + String.valueOf(breaker.getInt("level",1)));
        new Element(getApplicationContext()).setTextLevel(t);
        iniciarBarra();
    }

    private void iniciarBarra() {
        SKnivel.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                editor.putInt("level", progress);
                editor.commit();
                TextView t = findViewById(R.id.textViewBreakerConfigNivel);
                t.setText("Nivel inicial: " + String.valueOf(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void finish(View view) {
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
        YodoAds.showBannerAd(BreakerConfig.this);
    }

}
