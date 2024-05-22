package soft.evm.amblyopiamobilegames;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.SeekBar;
import android.widget.TextView;

import soft.evm.amblyopiamobilegames.ads.MiAdMob;
import soft.evm.amblyopiamobilegames.ads.YodoAds;
import soft.evm.amblyopiamobilegames.juegos.breaker.BreakerConfig;
import soft.evm.amblyopiamobilegames.juegos.flappy.game.FlappyGame;
import soft.evm.amblyopiamobilegames.layoutElements.Element;

public class DailyTimeActivity extends Activity {

    TextView title, minutes, desccription;
    SeekBar seekBar;
    SharedPreferences tiempo_demo_diario;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // remove title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_daily_time);

        incializarComponentes();
        setFont();
        setValores();

        //Admob
        MiAdMob.mostrarAds(this.getWindow().getDecorView().findViewById(android.R.id.content));
    }

    private void setValores() {
        tiempo_demo_diario = getSharedPreferences("TiempoDemoDiario", Context.MODE_PRIVATE);
        editor = tiempo_demo_diario.edit();
        int minutos = tiempo_demo_diario.getInt("tiempo_diario", 0);// 0 * 10 minutos
        minutes.setText(minutos*10 + " " + getString(R.string.minutos));
        seekBar.setProgress(minutos);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                editor.putInt("tiempo_diario", progress);
                editor.commit();
                minutes.setText(progress*10 + " " + getString(R.string.minutos));
                seekBar.setProgress(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void setFont() {
        Element element = new Element(this);
        element.setActivityText(title);
        element.setActivityText(minutes);
        element.setActivityText(desccription);
    }

    private void incializarComponentes() {
        title = findViewById(R.id.tv_tiempo_diario);
        minutes = findViewById(R.id.tv_daily_time_minutos);
        desccription = findViewById(R.id.tv_daily_time_description);
        seekBar = findViewById(R.id.seekBarTiempoDiario);
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
        YodoAds.showBannerAd(DailyTimeActivity.this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        YodoAds.showInterstitialAd(DailyTimeActivity.this);
        YodoAds.mostrarAds(this.getWindow().getDecorView().findViewById(android.R.id.content));
    }
}
