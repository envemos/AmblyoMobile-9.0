package soft.evm.amblyopiamobilegames.juegos.tetris;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.TextView;

import soft.evm.amblyopiamobilegames.MainActivity;
import soft.evm.amblyopiamobilegames.R;
import soft.evm.amblyopiamobilegames.SettingsActivity;
import soft.evm.amblyopiamobilegames.ads.MiAdMob;
import soft.evm.amblyopiamobilegames.ads.YodoAds;
import soft.evm.amblyopiamobilegames.juegos.flappy.game.FlappyGame;
import soft.evm.amblyopiamobilegames.layoutElements.Element;


public class TetrisConfig extends Activity implements AdapterView.OnItemSelectedListener {

    TextView tvnivel;
    CheckBox autoInc;
    SeekBar velocidad;
    SharedPreferences tetris;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // remove title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_tetris_config);
        incializar();
        interfaz();

        //Admob
        MiAdMob.mostrarAds(this.getWindow().getDecorView().findViewById(android.R.id.content));
    }

    private void interfaz() {
        Element element = new Element(getApplicationContext());
        element.setTextLevel(tvnivel);
        element.setAutoText(autoInc);
    }

    private void incializar() {
        tetris = getSharedPreferences("Tetris", Context.MODE_PRIVATE);
        editor = tetris.edit();
        autoInc = findViewById(R.id.checkBoxTetrisConfigVelocidad);
        velocidad = findViewById(R.id.seekBarTetrisConfigVelocidad);

        tvnivel = findViewById(R.id.textViewTetrisConfigNivel);

        autoInc.setChecked(tetris.getBoolean("auto",false));
        velocidad.setProgress(tetris.getInt("level",5));
        tvnivel.setText(getString(R.string.nivel_) + String.valueOf(tetris.getInt("level",5)));
        iniciarBarra();
    }

    private void iniciarBarra() {
        velocidad.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                editor.putInt("level", progress);
                editor.commit();
                TextView t = findViewById(R.id.textViewTetrisConfigNivel);
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

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    public void onCheckboxClicked(View view) {
        // Is the view now checked?
        boolean checked = ((CheckBox) view).isChecked();

        if (checked)
            editor.putBoolean("auto",true);
        else
            editor.putBoolean("auto",false);
        editor.commit();
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
        YodoAds.showBannerAd(TetrisConfig.this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        YodoAds.showInterstitialAd(TetrisConfig.this);
        YodoAds.mostrarAds(this.getWindow().getDecorView().findViewById(android.R.id.content));
    }

}
