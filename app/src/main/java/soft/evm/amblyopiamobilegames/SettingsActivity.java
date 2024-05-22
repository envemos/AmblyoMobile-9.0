package soft.evm.amblyopiamobilegames;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.ColorInt;

import com.pes.androidmaterialcolorpickerdialog.ColorPicker;
import com.pes.androidmaterialcolorpickerdialog.ColorPickerCallback;

import soft.evm.amblyopiamobilegames.ads.MiAdMob;
import soft.evm.amblyopiamobilegames.ads.YodoAds;
import soft.evm.amblyopiamobilegames.juegos.Colores;
import soft.evm.amblyopiamobilegames.juegos.flappy.game.FlappyGame;
import soft.evm.amblyopiamobilegames.layoutElements.Element;
import soft.evm.amblyopiamobilegames.table.TableViewHistorial;

public class SettingsActivity extends Activity implements AdapterView.OnItemSelectedListener {

    TextView left_eye, right_eye, lazy_eye, background_color, nota;
    Spinner ojo_L, ojo_R, ojo_vago, fondo;
    CheckBox sonido;
    SharedPreferences prefs;
    SharedPreferences.Editor editor;

    private ColorPicker cpL, cpR;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        // remove title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_settings);
        prefs = getSharedPreferences("MisPreferencias", Context.MODE_PRIVATE);
        colorPicker();
        incializar();
        pintarInterfaces();

        //Admob
        MiAdMob.mostrarAds(this.getWindow().getDecorView().findViewById(android.R.id.content));
    }

    private void colorPicker() {
        //color personalizado
        int colorR = prefs.getInt("customColorR", 0);
        int colorL = prefs.getInt("customColorL", 0);

        int defaultAlphaValue = Color.alpha(colorR);
        int defaultColorR = Color.red(colorR);
        int defaultColorG = Color.green(colorR);
        int defaultColorB = Color.blue(colorR);

        cpR = new ColorPicker(SettingsActivity.this, defaultAlphaValue, defaultColorR, defaultColorG, defaultColorB);

        defaultAlphaValue = Color.alpha(colorL);
        defaultColorR = Color.red(colorL);
        defaultColorG = Color.green(colorL);
        defaultColorB = Color.blue(colorL);

        cpL = new ColorPicker(SettingsActivity.this, defaultAlphaValue, defaultColorR, defaultColorG, defaultColorB);

        /* Set a new Listener called when user click "select" */
        cpR.setCallback(new ColorPickerCallback() {
            @Override
            public void onColorChosen(@ColorInt int color) {
                /* Or the android RGB Color (see the android Color class reference) */

                Colores.setCustomR(color);

                SharedPreferences.Editor editor = prefs.edit();
                editor.putInt("customColorR", color);
                editor.apply();

                // If the auto-dismiss option is not enable (disabled as default) you have to manually dimiss the dialog
                // cp.dismiss();
            }
        });

        cpL.setCallback(new ColorPickerCallback() {
            @Override
            public void onColorChosen(@ColorInt int color) {
                /* Or the android RGB Color (see the android Color class reference) */

                Colores.setCustomL(color);

                SharedPreferences.Editor editor = prefs.edit();
                editor.putInt("customColorL", color);
                editor.apply();

                // If the auto-dismiss option is not enable (disabled as default) you have to manually dimiss the dialog
                // cp.dismiss();
            }
        });
    }

    private void pintarInterfaces() {
        Element element = new Element(getApplicationContext());
        nota = findViewById(R.id.textViewActivityANota);
        left_eye = findViewById(R.id.textViewActivitySettingsOjoIzquierdo);
        right_eye = findViewById(R.id.textViewActivitySettingsOjoDerecho);
        lazy_eye = findViewById(R.id.textViewActivitySettingsOjoVago);
        background_color = findViewById(R.id.textViewActivitySettingsFondoColor);
        element.setActivityText(nota);
        element.setSettingsText(left_eye);
        element.setSettingsText(right_eye);
        element.setSettingsText(lazy_eye);
        element.setSettingsText(background_color);
        element.setSettingsCheckBox(sonido);
    }


    private void incializar() {
        editor = prefs.edit();
        editor.apply();
        ojo_L = findViewById(R.id.left_eye);
        ojo_R = findViewById(R.id.right_eye);
        ojo_vago = findViewById(R.id.weak_eye);
        fondo = findViewById(R.id.fondo_color);
        sonido = findViewById(R.id.checkBoxActivitySettingsSonido);

        ArrayAdapter adapter = new ArrayAdapter<>(this, R.layout.spinner_text, getResources().getStringArray(R.array.colors));
        ojo_L.setAdapter(adapter);
        ojo_R.setAdapter(adapter);
        adapter = new ArrayAdapter<>(this, R.layout.spinner_text, getResources().getStringArray(R.array.ojo_selec));
        ojo_vago.setAdapter(adapter);
        adapter = new ArrayAdapter<>(this, R.layout.spinner_text, getResources().getStringArray(R.array.blanco_negro));
        fondo.setAdapter(adapter);

        ojo_L.setOnItemSelectedListener(this);
        ojo_R.setOnItemSelectedListener(this);
        ojo_vago.setOnItemSelectedListener(this);
        fondo.setOnItemSelectedListener(this);

        ojo_L.setSelection(prefs.getInt("ojo_izquierdo", 0));
        ojo_R.setSelection(prefs.getInt("ojo_derecho", 1));
        ojo_vago.setSelection(prefs.getInt("ojo_vago", 1));
        fondo.setSelection(prefs.getInt("fondo", 1));
        sonido.setChecked(prefs.getBoolean("sonido",true));
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        //Obteniendo el id del Spinner que recibió el evento
        int idSpinner = parent.getId();
        switch(idSpinner) {

            case R.id.left_eye:
                editor.putInt("ojo_izquierdo", ojo_L.getSelectedItemPosition());
                if (ojo_L.getSelectedItemPosition() == 6){
                    /* Show color picker dialog */
                    cpL.show();
                    cpL.enableAutoClose(); // Enable auto-dismiss for the dialog
                }
                editor.commit();
                break;

            case R.id.right_eye:
                editor.putInt("ojo_derecho", ojo_R.getSelectedItemPosition());
                if(ojo_R.getSelectedItemPosition() == 6){
                    /* Show color picker dialog */
                    cpR.show();
                    cpR.enableAutoClose(); // Enable auto-dismiss for the dialog
                }
                editor.commit();
                break;

            case R.id.weak_eye:
                editor.putInt("ojo_vago", ojo_vago.getSelectedItemPosition());
                editor.commit();
                break;

            case R.id.fondo_color:
                editor.putInt("fondo", fondo.getSelectedItemPosition());
                editor.commit();
                break;

        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void onCheckboxClicked(View view) {
        // Is the view now checked?
        boolean checked = ((CheckBox) view).isChecked();

        if (checked)
            editor.putBoolean("sonido",true);
        else
            editor.putBoolean("sonido",false);
        editor.commit();
    }

    public void save(View view) {
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
        YodoAds.showBannerAd(SettingsActivity.this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        YodoAds.showInterstitialAd(SettingsActivity.this);
        YodoAds.mostrarAds(this.getWindow().getDecorView().findViewById(android.R.id.content));
    }
}

