package soft.evm.amblyopiamobilegames;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.widget.SwitchCompat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Locale;

import soft.evm.amblyopiamobilegames.ads.MiAdMob;
import soft.evm.amblyopiamobilegames.ads.YodoAds;
import soft.evm.amblyopiamobilegames.analytics.MisMetricas;
import soft.evm.amblyopiamobilegames.juegos.flappy.game.FlappyGame;
import soft.evm.amblyopiamobilegames.layoutElements.Element;
import soft.evm.amblyopiamobilegames.remindMe.AlarmReceiver;
import soft.evm.amblyopiamobilegames.remindMe.NotificationScheduler;

import static soft.evm.amblyopiamobilegames.BuildConfig.URL_UPDATE;
import static soft.evm.amblyopiamobilegames.MainActivity.no;
import static soft.evm.amblyopiamobilegames.MainActivity.no_registrado;
import static soft.evm.amblyopiamobilegames.MainActivity.si;
import static soft.evm.amblyopiamobilegames.analytics.MisMetricas.FIRST_MAIN;
import static soft.evm.amblyopiamobilegames.analytics.MisMetricas.FIRST_SESION;

public class SesionActivity extends Activity {

    //DATOS
    private TextView TVemail;
    private static TextView TVNacimiento;
    private TextView TVNacimientotext;
    private static EditText ETusername;
    private DatePickerDialog picker;
    private static Spinner SPcountryList;
    private static RadioButton RbMale;
    private static RadioButton RbFemale;

    Element element;

    //SharedPreferences
    static SharedPreferences user;

    //Poder acceder al adapter desde la clase BackgroundWorker
    static ArrayAdapter<String> adapter;

    public static final String updateURL = URL_UPDATE;

    public static boolean actualizar = false;

    SharedPreferences.Editor editor;

    SwitchCompat switch_emailList1, switch_emailList2;

    //OVERRIDE
    @Override
    public void onBackPressed() {
        //solo podra salir si ya actualizó con anterioridad
        if(!user.getString("nombre", no_registrado).equals(no_registrado)) {
            actualizar = false;
            finish();
        }
    }

    @Override
    public void finish() {
        if(actualizar) {
            String correo = user.getString("correo", no_registrado);
            if (correo == null)
                correo = no_registrado;
            if (correo.equals(no_registrado)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(getString(R.string.necesitasemail))
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //do things
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            } else {
                //hay que guardar en el sharedpreferences la informacion por defecto que no se ha registrado
                SharedPreferences.Editor editor = user.edit();
                String u = user.getString("nombre", no_registrado);
                if (u == null)
                    u = no_registrado;
                if (u.equals(no_registrado)) {
                    u = correo.substring(0, correo.indexOf("@"));
                    editor.putString("nombre", u);
                }
                String n = user.getString("nacimiento", no_registrado);
                if (n == null)
                    n = no_registrado;
                if (n.equals(no_registrado)) {
                    n = TVNacimientotext.getText().toString();
                    editor.putString("nacimiento", n);
                }
                String g = user.getString("genero", no_registrado);
                if (g == null)
                    g = no_registrado;
                if (g.equals(no_registrado)) {
                    g = "M";
                    editor.putString("genero", g);
                }
                String p = user.getString("pais", no_registrado);
                if (p == null)
                    p = no_registrado;
                if (p.equals(no_registrado)) {
                    p = "null";
                    editor.putString("pais", p);
                }
                editor.apply();

                //metricas
                MisMetricas.firstUserExperienceGeneral(this, FIRST_MAIN);

            }
        }
        super.finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //metricas
        MisMetricas.firstUserExperienceGeneral(this, FIRST_SESION);

        // remove title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_sesion);

        //INICIAR
        user = getSharedPreferences("Usuario", Context.MODE_PRIVATE);
        element = new Element(getApplicationContext());
        inicializarComponentes();
        String c = user.getString("correo", no_registrado);

        //Admob
        MiAdMob.mostrarAds(this.getWindow().getDecorView().findViewById(android.R.id.content));
    }

    @Override
    protected void onResume() {
        super.onResume();
        YodoAds.mostrarAds(this.getWindow().getDecorView().findViewById(android.R.id.content));
    }

    @Override
    protected void onStart() {
        super.onStart();
        YodoAds.showBannerAd(SesionActivity.this);
    }

    //INICIAR
    private void inicializarComponentes() {
        TextView TVmicuenta = findViewById(R.id.TextViewActivitySesionMicuenta);
        element.setSesionTextTitle(TVmicuenta);
        incializarEmail();
        inicializarUsername();
        inicializarFecha();
        inicializarPaises();
        inicializarGeneros();
        inicializarDemasElementos();
        //TextView pri = findViewById(R.id.TextViewActivitySesionPrivacidad);
        //element.setSettingsText(pri);
    }

    private void incializarEmail() {
        TVemail = findViewById(R.id.editTextActivitySesionCuentaGoogle);
        TVemail.setText(user.getString("correo", no_registrado));
        element.setSesionText(TVemail);
    }


    private static final int REQUEST_CODE_EMAIL = 1;

    private void inicializarUsername() {
        ETusername = findViewById(R.id.editTextActivitySesionNombreUsuario);
        String u = user.getString("nombre", no_registrado);
        if(u==null)
            u = no_registrado;
        if(u.equals(no_registrado))
            u="";
        if(u.equalsIgnoreCase("null")) {
            String correo = user.getString("correo", no_registrado);
            u = correo.substring(0, correo.indexOf("@"));
        }
        ETusername.setText(u);
        element.setSesionText(ETusername);
    }

    private void inicializarFecha() {
        TVNacimiento = findViewById(R.id.editTextActivitySesionFechaNacimiento);
        final Calendar cldr = Calendar.getInstance();
        int day = cldr.get(Calendar.DAY_OF_MONTH);
        int month = cldr.get(Calendar.MONTH);
        int year = cldr.get(Calendar.YEAR);
        String f = day + "/" + (month+1) + "/" + year;
        String t = user.getString("nacimiento", f);
        TVNacimiento.setText(t);
        TVNacimiento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar cldr = Calendar.getInstance();
                int day = cldr.get(Calendar.DAY_OF_MONTH);
                int month = cldr.get(Calendar.MONTH);
                int year = cldr.get(Calendar.YEAR);
                // date picker dialog
                picker = new DatePickerDialog(SesionActivity.this,
                        android.R.style.Theme_Holo_Light_Panel,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                String f = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                                TVNacimiento.setText(f);
                                element.setSesionText(TVNacimiento);
                            }
                        }, year, month, day);

                picker.show();
            }
        });
        element.setSesionText(TVNacimiento);
    }

    private void inicializarPaises() {
        SPcountryList = findViewById(R.id.SpinnerActivitySesionPaises);
        inicializarlista();
    }

    private void inicializarlista() {
        Locale[] locale = Locale.getAvailableLocales();
        ArrayList<String> countries = new ArrayList<>();
        String country;
        for(Locale loc : locale){
            country=loc.getDisplayCountry();
            if(country.length() > 0 && !countries.contains((country)))
                countries.add(country);
        }
        Collections.sort(countries, String.CASE_INSENSITIVE_ORDER);

        adapter = new ArrayAdapter<>(this, R.layout.spinner_text,countries);
        SPcountryList.setAdapter(adapter);
        int spinnerPosition = adapter.getPosition(user.getString("pais", no_registrado));
        SPcountryList.setSelection(spinnerPosition);
        SharedPreferences.Editor editor = user.edit();
        editor.putString("pais", user.getString("pais", no_registrado));
        editor.apply();
        SPcountryList = findViewById(R.id.SpinnerActivitySesionPaises);
    }

    private void inicializarGeneros() {
        //RadioGroup generos = findViewById(R.id.radioGroupActivitySesionGeneros);
        RbMale = findViewById(R.id.radioButtonActivitySesionM);
        RbFemale = findViewById(R.id.radioButtonActivitySesionF);
        element.setRadioButtonM(RbMale);
        element.setRadioButtonF(RbFemale);
        String g = user.getString("genero", no_registrado);
        if(g==null)
            g = "M";
        if(g.equals("F"))
            RbFemale.setChecked(true);
        SharedPreferences.Editor editor = user.edit();
        editor.putString("genero", user.getString("genero", "M"));
        editor.apply();
    }

    private void inicializarDemasElementos() {
        TextView TVmiCuenta = findViewById(R.id.TextViewActivitySesionMicuenta);
        element.setSesionText(TVmiCuenta);
        //TVemailtext = findViewById(R.id.TextViewActivitySesionGooglecuenta);
        //element.setSesionText(TVemailtext);
        TextView TVmicuentatext = findViewById(R.id.editTextActivitySesionUsername);
        element.setSesionText(TVmicuentatext);
        TVNacimientotext = findViewById(R.id.textViewActivitySesionFecha);
        element.setSesionText(TVNacimientotext);
        TextView TVPaisText = findViewById(R.id.textViewActivitySesionPais);
        element.setSesionText(TVPaisText);
        TextView TVgenero = findViewById(R.id.textViewActivitySesionGenero);
        element.setSesionText(TVgenero);
        Button update = findViewById(R.id.buttonActivitySesionActualizar);
        element.setSesionText(update);
    }

    public void update(View view) {


        View focusView = null;
        boolean cancel = false;

        //String pattern = "[A-Za-zÀ-ÖØ-öø-ÿ0-9-出油(\\\\d+)吨 ]{1,32}";
        String no_permitidos = "'*()=" + "'";

        if (TextUtils.isEmpty(ETusername.getText().toString())) {
            ETusername.setError(getString(R.string.error_field_required));
            focusView = ETusername;
            cancel = true;
        }
        /*else if (!ETusername.getText().toString().matches(pattern)) {
            String t = ETusername.getText().toString();
            ETusername.setError("Only letters and numbers. No alphanumeric.");
            focusView = ETusername;
            cancel = true;
        }*/
        else if (ETusername.getText().toString().contains("*") || ETusername.getText().toString().contains("(") || ETusername.getText().toString().contains(")") || ETusername.getText().toString().contains("=") || ETusername.getText().toString().contains("'") || ETusername.getText().toString().contains("\"") ) {
            String t = ETusername.getText().toString();
            ETusername.setError("Only letters and numbers. No alphanumeric.");
            focusView = ETusername;
            cancel = true;
        }
        else if (TextUtils.isEmpty(TVemail.getText().toString())) {
            TVemail.setError(getString(R.string.error_field_required));
            focusView = TVemail;
            cancel = true;
        }
        else if (TVemail.length() < 4 || !TVemail.getText().toString().contains("@")) {
            TVemail.setError(getString(R.string.error_invalid_email));
            focusView = TVemail;
            cancel = true;
        }
        if (cancel)
            focusView.requestFocus();
        else {
            SharedPreferences.Editor editor = user.edit();
            editor.putString("correo", TVemail.getText().toString());
            editor.putString("nombre", ETusername.getText().toString());
            editor.putString("nacimiento", TVNacimiento.getText().toString());
            try {
                editor.putString("pais", SPcountryList.getSelectedItem().toString());
            }catch (NullPointerException e){
                e.printStackTrace();
                Log.e("39", "Error");
            }
            if (RbFemale.isChecked())
                editor.putString("genero", "F");
            else
                editor.putString("genero", "M");
            editor.apply();

            ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

            actualizar = true;

            finish();

        }
    }

    //*****************************************************************************************************************************

    public static void insertar(){
        ETusername.setText(user.getString("nombre", null));
        TVNacimiento.setText(user.getString("nacimiento", null));
        String g = user.getString("genero", null);
        if(g==null)
            g = "M";
        if (g.equals("F"))
            RbFemale.setChecked(true);
        else
            RbMale.setChecked(true);
        int spinnerPosition = adapter.getPosition(user.getString("pais", null));
        SPcountryList.setSelection(spinnerPosition);
    }

    //**********************


    /////////////////////////////////////////////////////////////////////

    public void cerrarSesion(View view) {
        SharedPreferences.Editor editor = user.edit();
        editor.clear();
        editor.apply();
        editor = getSharedPreferences("MisPreferencias", Context.MODE_PRIVATE).edit();
        editor.clear();
        editor.apply();
        NotificationScheduler.cancelReminder(this, AlarmReceiver.class);
        editor = getSharedPreferences("Estadisticas", Context.MODE_PRIVATE).edit();
        editor.clear();
        editor.apply();
        editor = getSharedPreferences("TiempoDemoDiario", Context.MODE_PRIVATE).edit();
        editor.clear();
        editor.apply();
        editor = getSharedPreferences("RemindMePref", Context.MODE_PRIVATE).edit();
        editor.clear();
        editor.apply();
        editor = getSharedPreferences("apprater", Context.MODE_PRIVATE).edit();
        editor.clear();
        editor.apply();
        editor = getSharedPreferences("TiempoDemoDiario", Context.MODE_PRIVATE).edit();
        editor.clear();
        editor.apply();
        editor = getSharedPreferences("Snake", Context.MODE_PRIVATE).edit();
        editor.clear();
        editor.apply();
        editor = getSharedPreferences("FirstUserExperienceGeneral", Context.MODE_PRIVATE).edit();
        editor.clear();
        editor.apply();
        editor = getSharedPreferences("Breaker", Context.MODE_PRIVATE).edit();
        editor.clear();
        editor.apply();
        editor = getSharedPreferences("Flappy", Context.MODE_PRIVATE).edit();
        editor.clear();
        editor.apply();
        editor = getSharedPreferences("Pacman", Context.MODE_PRIVATE).edit();
        editor.clear();
        editor.apply();
        editor = getSharedPreferences("info", Context.MODE_PRIVATE).edit();
        editor.clear();
        editor.apply();
        editor = getSharedPreferences("Space", Context.MODE_PRIVATE).edit();
        editor.clear();
        editor.apply();
        editor = getSharedPreferences("Aviso", Context.MODE_PRIVATE).edit();
        editor.clear();
        editor.apply();
        actualizar = false;
        finish();
    }

}
