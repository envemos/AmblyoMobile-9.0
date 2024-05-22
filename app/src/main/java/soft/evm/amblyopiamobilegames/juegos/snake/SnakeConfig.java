package soft.evm.amblyopiamobilegames.juegos.snake;

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
import soft.evm.amblyopiamobilegames.juegos.breaker.BreakerConfig;
import soft.evm.amblyopiamobilegames.juegos.flappy.game.FlappyGame;
import soft.evm.amblyopiamobilegames.layoutElements.Element;


public class SnakeConfig extends Activity implements AdapterView.OnItemSelectedListener {

    TextView tVelocidad;
    CheckBox autoInc, muros;
    SeekBar velocidad;
    SharedPreferences snake;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        // remove title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_snake_config);
        incializar();
        interfaz();

        //Admob
        MiAdMob.mostrarAds(this.getWindow().getDecorView().findViewById(android.R.id.content));
    }

    private void interfaz() {
        Element element = new Element(getApplicationContext());
        element.setTextLevel(tVelocidad);
        element.setAutoText(autoInc);
        element.setAutoText(muros);
    }

    private void incializar() {
        snake = getSharedPreferences("Snake", Context.MODE_PRIVATE);
        editor = snake.edit();
        autoInc = findViewById(R.id.checkBoxSnakeConfigVelocidad);
        velocidad = findViewById(R.id.seekBarSnakeConfigVelocidad);
        muros = findViewById(R.id.checkBoxSnakeConfigMuros);

        tVelocidad = findViewById(R.id.textViewSnakeConfigVelocidad);

        autoInc.setChecked(snake.getBoolean("auto",false));
        muros.setChecked(snake.getBoolean("muros",true));
        velocidad.setProgress(snake.getInt("velocidad",5));
        tVelocidad.setText(getString(R.string.velocidad_) + String.valueOf(snake.getInt("velocidad",5)*10) + "%");
        iniciarBarra();
    }

    private void iniciarBarra() {
        velocidad.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                editor.putInt("velocidad", progress);
                editor.commit();
                TextView t = findViewById(R.id.textViewSnakeConfigVelocidad);
                t.setText(getString(R.string.velocidad_) + String.valueOf(progress*10) + "%");
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

    public void onCheckboxClicked(View view) {
        // Is the view now checked?
        boolean checked = ((CheckBox) view).isChecked();

        // Check which checkbox was clicked
        switch(view.getId()) {
            case R.id.checkBoxSnakeConfigVelocidad:
                if (checked)
                    editor.putBoolean("auto",true);
                else
                    editor.putBoolean("auto",false);
                editor.commit();
                break;
            case R.id.checkBoxSnakeConfigMuros:
                if (checked)
                    editor.putBoolean("muros",true);
                else
                    editor.putBoolean("muros",false);
                editor.commit();
                break;
        }
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
        YodoAds.showBannerAd(SnakeConfig.this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        YodoAds.showInterstitialAd(SnakeConfig.this);
        YodoAds.mostrarAds(this.getWindow().getDecorView().findViewById(android.R.id.content));
    }

}