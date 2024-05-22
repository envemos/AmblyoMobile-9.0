package soft.evm.amblyopiamobilegames.juegos.space;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import soft.evm.amblyopiamobilegames.MainActivity;
import soft.evm.amblyopiamobilegames.R;
import soft.evm.amblyopiamobilegames.SettingsActivity;
import soft.evm.amblyopiamobilegames.ads.MiAdMob;
import soft.evm.amblyopiamobilegames.ads.YodoAds;
import soft.evm.amblyopiamobilegames.juegos.breaker.BreakerConfig;
import soft.evm.amblyopiamobilegames.juegos.flappy.game.FlappyGame;
import soft.evm.amblyopiamobilegames.layoutElements.Element;

public class SpaceConfig extends AppCompatActivity {

    TextView tvnivel;
    SeekBar velocidad;
    SharedPreferences space;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // remove title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_space_config);
        incializar();
        interfaz();

        //Admob
        MiAdMob.mostrarAds(this.getWindow().getDecorView().findViewById(android.R.id.content));
    }

    private void interfaz() {
        Element element = new Element(getApplicationContext());
        element.setTextLevel(tvnivel);
    }

    private void incializar() {
        space = getSharedPreferences("Space", Context.MODE_PRIVATE);
        editor = space.edit();
        velocidad = findViewById(R.id.seekBarSpaceConfigVelocidad);

        tvnivel = findViewById(R.id.textViewSpaceConfigNivel);

        velocidad.setProgress(space.getInt("level",1));
        tvnivel.setText(getString(R.string.nivel_) + String.valueOf(space.getInt("level",1)));
        iniciarBarra();
    }

    private void iniciarBarra() {
        velocidad.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                editor.putInt("level", progress);
                editor.commit();
                TextView t = findViewById(R.id.textViewSpaceConfigNivel);
                t.setText(getString(R.string.nivel_) + String.valueOf(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
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
        YodoAds.showBannerAd(SpaceConfig.this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        YodoAds.showInterstitialAd(SpaceConfig.this);
        YodoAds.mostrarAds(this.getWindow().getDecorView().findViewById(android.R.id.content));
    }

}
