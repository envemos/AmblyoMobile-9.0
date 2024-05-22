package soft.evm.amblyopiamobilegames;

import android.accounts.AccountManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.Purchase;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.common.AccountPicker;
import com.yodo1.mas.Yodo1Mas;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import soft.evm.amblyopiamobilegames.ads.MiAdMob;
import soft.evm.amblyopiamobilegames.ads.YodoAds;
import soft.evm.amblyopiamobilegames.analytics.MisMetricas;
import soft.evm.amblyopiamobilegames.billing.BillingManager;
import soft.evm.amblyopiamobilegames.billing.BillingProvider;
import soft.evm.amblyopiamobilegames.dal.PartidasDBHelperHistorial;
import soft.evm.amblyopiamobilegames.dal.PartidasDBHelperRanking;
import soft.evm.amblyopiamobilegames.dialog.RateDialogFragment;
import soft.evm.amblyopiamobilegames.info.InfoActivity;
import soft.evm.amblyopiamobilegames.juegos.Colores;
import soft.evm.amblyopiamobilegames.juegos.GameActivity;
import soft.evm.amblyopiamobilegames.juegos.Insertar;
import soft.evm.amblyopiamobilegames.juegos.flappy.game.FlappyGame;
import soft.evm.amblyopiamobilegames.layoutElements.Element;
import soft.evm.amblyopiamobilegames.model.PartidaContract;
import soft.evm.amblyopiamobilegames.rate.AppRater;
import soft.evm.amblyopiamobilegames.remindMe.AlarmReceiver;
import soft.evm.amblyopiamobilegames.remindMe.LocalData;
import soft.evm.amblyopiamobilegames.remindMe.NotificationScheduler;
import soft.evm.amblyopiamobilegames.utils.GifImageView;
import soft.evm.amblyopiamobilegames.webview.WebViewActivity;

import static soft.evm.amblyopiamobilegames.BuildConfig.URL_PARTIDAS;
import static soft.evm.amblyopiamobilegames.BuildConfig.URL_UPDATE;
import static soft.evm.amblyopiamobilegames.model.PartidaContract.Partida.TABLE_NAME_HISTORIAL;

public class MainActivity extends Activity implements BillingProvider {

    //Variables string nombre de ususario; sesion y datos de usuario
    public final static String no_registrado = "";
    public static final int REQUEST_CODE_EMAIL = 1;
    public static final int REQUEST_CODE_SESION = 2;
    public static final int REQUEST_CODE_GAMEACTIVITY = 3;
    public static final int N0 = 0;
    public static final int N1 = 2;
    public static final int N2 = 4;
    public static final int N3 = 6;

    //clase que define el modelo SQLite de las partidas
    public static PartidasDBHelperHistorial partidasDBHelper_historial;
    public static PartidasDBHelperRanking partidasDBHelper_ranking;

    //BBDD ShareedPref de configuracion
    SharedPreferences prefs, aviso;

    //BBDD ShareedPref de usuario
    static SharedPreferences user;

    //interfaces visuales
    //TextView title;
    //static TextView bienvenido;

    // ATTENTION: This was auto-generated to handle app links.
    /*Intent appLinkIntent = getIntent();
    String appLinkAction = appLinkIntent.getAction();
    Uri appLinkData = appLinkIntent.getData();*/

    //botones
    Button jugar;
    ImageView IVsesion, buttonSesion, buttonActividad;
    GifImageView actividadCargando, sesionCargando;

    //********************** MENU JUEGO


    public static String juego = "";

    //SharedPreferences prefs;
    SharedPreferences.Editor editor;

    //Rate
    public static boolean isRate;
    public static int mensaje;
    public static int veces = 0;

    //Interfaz grafica
    Element element;
    //TextView titulo;//, premium;
    Button buttonSnake, buttonTetris, buttonFlappy, buttonBreake, buttonSpace, buttonPong;

    //**********************

    public static final String updateURLUsuarios = URL_UPDATE;
    public static final String updateURLPartidas = URL_PARTIDAS;

    public static final String no = "NO";
    public static final String si = "SI";

    public final String MAIN = "main";
    public final String SESION = "sesion";

    ProgressDialog progressDialog;

    public static boolean accountPicker = false;

    ////

    private int n_params_sesion = 3;

    //

    /**
     * Aquí nos comunicamos con el play store
     */

    // Debug tag, for logging
    private static final String TAG = "GameActivity";

    private BillingManager mBillingManager;

    public static boolean mIsPremium = false;

    private boolean onBillingClientSetupFinished = false;

    final String PremiumSKU = "premium";
    final String PremiumSKU2 = "premium2";
    final String PremiumSKU3 = "premium3";
    final String PremiumSKU4 = "premium4";

    private String premiumKey = "";

    //////////////////////////////////////////////////
    /////////////////

    private final String enlace_amazon = "https://www.amazon.es/gp/product/B012CQKZAE/ref=as_li_tl?ie=UTF8&camp=3638&creative=24630&creativeASIN=B012CQKZAE&linkCode=as2&tag=envemos-21&linkId=e4143a96b89d0580318480d49a2e3a2d";

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_EMAIL && resultCode == RESULT_OK) {
            SharedPreferences.Editor editor;
            editor = user.edit();
            String correo = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
            editor.putString("correo", correo);
            long ahora = System.currentTimeMillis();
            Date date = new Date(ahora);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.getDefault());
            String dateText = sdf.format(date);
            editor.putString("registro", dateText);
            editor.putBoolean(SESION, true);
            editor.apply();
            progressDialog = new ProgressDialog(this);
            startLoadBar();
            updateMysql();// en segundo plano sincronizamos las partidas
            Insertar.correo = user.getString("correo", no_registrado);
            Insertar.nombre = correo.substring(0,correo.indexOf("@"));
            Insertar.insertar(getBaseContext());// insertar partida pendiente (si hay)
            updateUser(SESION);
        }else if(requestCode == REQUEST_CODE_SESION) {
            if (SesionActivity.actualizar)
                updateBBDDSessionActivity(this);
        }else if(requestCode == REQUEST_CODE_GAMEACTIVITY && accountPicker){
            accountPicker = false;
            selectAccount();
        }

    }

    //METODOS OVERRRIDE
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

        pintarBotones();

        // no mostrar anuncios en el main activity
        //YodoAds.showInterstitialAd(MainActivity.this);
        //YodoAds.mostrarAds(this.getWindow().getDecorView().findViewById(android.R.id.content));
    }

    @Override
    protected void onStart() {
        super.onStart();
        //YodoAds.showBannerAd(MainActivity.this);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        pintarBotones();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PlayStore();

        // remove title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        hilo();

    }

    private void hilo() {
        //inicializamos SharedPreferences para iniciar la concurrencia
        inicializarSharedPreferences();//no ejecutar en thread porque debe inicializarse antes que los threads

        tareasEnSegundoPlano();
        tareasEnHiloPrincipal();

    }

    private void tareasEnSegundoPlano() {

        new Thread(){
            public void run(){

                //Interfaz
                pintarBotonesThread();

                //inicializamos SQLite
                inicializarSQLiteThread();

                //Sesion
                sesionThread();

                //inicializar alarma
                alarmaThread();

            }

            //////////////////////////////////////////

            private void pintarBotonesThread() {
                new Thread() {
                    public void run() {
                        pintarBotones();
                    }
                }.start();
            }

            private void inicializarSQLiteThread() {
                new Thread() {
                    public void run() {
                        inicializarSQLite();
                    }
                }.start();
            }

            private void sesionThread() {
                new Thread() {
                    public void run() {
                        iniciarBotonesSesion();
                        if(haySesion())
                            hiloSesion();
                    }
                }.start();
            }

            private void alarmaThread() {
                new Thread() {
                    public void run() {
                        alarma();
                    }
                }.start();
            }

            ///////////////////


        }.start();

    }

    private void tareasEnHiloPrincipal() {

        //Admob -> no ejecutar en Thread; sino en hilo principal
        //MiAdMob.iniciarAdMob(getBaseContext());
        //MiAdMob.iniInterstitialAd(getBaseContext());

        //Yodo ads init SDK
        Yodo1Mas.getInstance().setCOPPA(false);
        Yodo1Mas.getInstance().setCCPA(false);
        Yodo1Mas.getInstance().init(this, YodoAds.AppKey);

        //metricas
        MisMetricas.iniciar(this, getApplication());
        MisMetricas.firstUserExperienceGeneral(this, MisMetricas.FIRST_JUEGO);

        //rate -> no ejecutar en Thread; sino en hilo principal
        rate();

        //Ofertar -> no ejecutar en Thread; sino en hilo principal
        iniciarDias();
        mostrarOferta();
    }

    private void alarma() {
        new Thread() {
            public void run() {
                iniAlarma();
            }
        }.start();
    }

    private void abrirSesion(String tipo){
        if(tipo.equals(SESION))
            finishLoadBar();
        Intent i = new Intent(this, SesionActivity.class);//si no existe, vamos al menu de sesion
        startActivityForResult(i, REQUEST_CODE_SESION);
    }

    private boolean haySesion() {
        String c = user.getString("correo", no_registrado);
        String nm = user.getString("nombre", no_registrado);
        String nc = user.getString("nacimiento", no_registrado);
        String p = user.getString("pais", no_registrado);
        String g = user.getString("genero", no_registrado);
        if (c.equals(no_registrado) || //¿usuario existe?
                nm.equals(no_registrado) ||
                nc.equals(no_registrado) ||
                p.equals(no_registrado) ||
                g.equals(no_registrado)) {
            return false;
        }
        return true;
    }

    private void hiloSesion() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            new Thread() {
                public void run() {
                    sesion();
                }
            }.start();
        } // no mostrar ningun dialog porque no estamos en el thread principal
    }

    private void iniciarBotonesSesion(){
        buttonSesion = findViewById(R.id.buttonActivityMainSesion);
        buttonActividad = findViewById(R.id.buttonActivityVerActividad);
        if(haySesion())
            caragarBotonesSesion();
    }

    private void caragarBotonesSesion() {
        actividadCargando = (GifImageView) findViewById(R.id.cargandoActividad);
        actividadCargando.setGifImageResource(R.mipmap.loading);
        sesionCargando = (GifImageView) findViewById(R.id.cargandoSesion);
        sesionCargando.setGifImageResource(R.mipmap.loading);
    }

    private void iniAlarma() {
        LocalData localData = new LocalData(this);
        localData.getReminderStatus();
        localData.get_hour();
        localData.get_min();
        NotificationScheduler.setReminder(this, AlarmReceiver.class, localData.get_hour(), localData.get_min());
    }


    //1 BBDD
    private void inicializarSharedPreferences() {
        prefs = getSharedPreferences("MisPreferencias", Context.MODE_PRIVATE);
        user = getSharedPreferences("Usuario", Context.MODE_PRIVATE);
        aviso = getSharedPreferences("Aviso", Context.MODE_PRIVATE);
        inicializarSharedPreferencesPrefs();
        inicializarSharedPreferencesUsuario();
        iniciarSharedPreferencesAviso();
    }

    private void inicializarSQLite() {
        partidasDBHelper_historial = new PartidasDBHelperHistorial(this);
        partidasDBHelper_ranking = new PartidasDBHelperRanking(this);
    }

    private void inicializarSharedPreferencesPrefs() {
        //Valores por defecto al iniciar el programa por primera vez
        prefs.getInt("ojo_izquierdo", 0);
        prefs.getInt("ojo_derecho", 0);
        prefs.getInt("ojo_vago", 1);
        prefs.getInt("fondo", 0);
    }

    private void inicializarSharedPreferencesUsuario() {
        //visibles
        user.getString("correo", no_registrado);
        user.getString("nombre", no_registrado);
        user.getString("nacimiento", no_registrado);
        user.getString("pais", no_registrado);
        user.getString("genero", no_registrado);
        //no visible
        user.getString("ultima modificacion", no_registrado);
        user.getString("registro", no_registrado);
        user.getString("premium", no);
        user.getString("mail_list", si);
        user.getString("marketing", si);
    }

    private void iniciarSharedPreferencesAviso() {


        /**
         *
         * estrabismo
         *
         * sesion
         * informacion
         * configuracion
         * actividad
         * alarma
         * recordatorio
         *
         *
         */


        String tipo = "estrabismo";
        if(aviso.getBoolean(tipo, true))
            mostrarAlertDialog(tipo);

        tipo = "GDPR";
        Yodo1Mas.getInstance().setGDPR(false);
        if(aviso.getBoolean(tipo, true)) {
            new AlertDialog.Builder(this)
                    //set icon
                    .setIcon(android.R.drawable.ic_dialog_info)
                    //set title
                    .setTitle("GDPR")
                    //set message
                    .setMessage("We collect player data and distribute it to third parties to personalize the game experience. This personalization leads to a more engaging, and exciting, game play experience. If you are under the age of 16, or do not consent to having your data shared, you may opt out.")
                    //set positive button
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Yodo1Mas.getInstance().setGDPR(true);
                        }
                    })
                    //set negative button
                    .setNegativeButton("deny", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Yodo1Mas.getInstance().setGDPR(false);
                        }
                    })
                    .show();
        }
        SharedPreferences.Editor editor = aviso.edit();
        editor.putBoolean(tipo, false);
        editor.apply();
    }

    private void mostrarAlertDialog(String tipo) {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainActivity.this);
        View mView = getLayoutInflater().inflate(R.layout.alert_dialog_menu, null);
        TextView textView = mView.findViewById(R.id.textViewMenu);
        CheckBox mCheckBox = mView.findViewById(R.id.checkBoxDontShowAgain);

        String titulo = "";
        String texto = "";
        switch (tipo){
            case "estrabismo":
                titulo = getString(R.string.titulo_estrabismo);
                texto = getString(R.string.aviso_estrabismo);
                break;
            case "sesion":
                titulo = getString(R.string.titulo_sesion);
                texto = getString(R.string.aviso_sesion);
                break;
            case "informacion":
                titulo = getString(R.string.titulo_informacion);
                texto = getString(R.string.aviso_informacion);
                break;
            case "configuracion":
                titulo = getString(R.string.titulo_configuracion);
                texto = getString(R.string.aviso_configuracion);
                break;
            case "actividad":
                titulo = getString(R.string.titulo_actividad);
                texto = getString(R.string.aviso_actividad);
                break;
            case "alarma":
                titulo = getString(R.string.titulo_alarma);
                texto = getString(R.string.aviso_alarma);
                break;
            case "recordatorio":
                titulo = getString(R.string.titulo_recordatorio);
                texto = getString(R.string.aviso_recordatorio);
                break;
        }

        mBuilder.setTitle(titulo);
        textView.setText(texto);

        mBuilder.setView(mView);
        mBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                Intent e = null;
                switch (tipo){
                    case "estrabismo":
                        break;
                    case "sesion":
                        if (haySesion()){
                            e = new Intent(getApplicationContext(), SesionActivity.class);
                            startActivity(e);
                        }
                        else
                            selectAccount();
                        break;
                    case "informacion":
                        e = new Intent(getApplicationContext(), InfoActivity.class);
                        startActivity(e);
                        break;
                    case "configuracion":
                        e = new Intent(getApplicationContext(), SettingsActivity.class);
                        startActivity(e);
                        break;
                    case "actividad":
                        e = new Intent(getApplicationContext(), ActividadActivity.class);
                        startActivity(e);
                        break;
                    case "alarma":
                        e = new Intent(getApplicationContext(), DailyTimeActivity.class);
                        startActivity(e);
                        break;
                    case "recordatorio":
                        e = new Intent(getApplicationContext(), RemindMeActivity.class);
                        startActivity(e);
                        break;
                }
            }
        });

        AlertDialog mDialog = mBuilder.create();
        mDialog.show();
        mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                SharedPreferences.Editor editor = aviso.edit();
                if(compoundButton.isChecked())
                    editor.putBoolean(tipo, false);
                else
                    editor.putBoolean(tipo, true);
                editor.apply();
            }
        });

        mDialog.show();
    }


    //2 INTERFAZ
    private void pintarBotones() {
        pintarSesionGenero();
        pintarBotonesJuego();
    }

    private void pintarSesionGenero() {
        IVsesion = findViewById(R.id.buttonActivityMainSesion);
        String g = user.getString("genero", no_registrado);
        if (g.equals("F"))
            IVsesion.setImageDrawable(getDrawable(R.mipmap.female));
        else
            IVsesion.setImageDrawable(getDrawable(R.mipmap.male));
    }

    //3 SESION
    private void sesion() {
        if(haySesion())
            actualizarSesion();
    }

    private void actualizarSesion() {
        //select ultima vez de cuenta de gmail
        //A -> mysql > shared -> Actualizar en Shared
        //B -> mysql = shared -> Nada
        //C -> mysql < shared -> Actualizar en Mysql

        new Thread() {
            public void run() {
                updateUser(MAIN);
            }
        }.start();
        new Thread() {
            public void run() {
                noSubidas();
            }
        }.start();
        new Thread() {
            public void run() {
                updateMysql();
            }
        }.start();
    }

    private void updateUser(String tipo) {//actualizar los campos de la base de datos mysql
        if(tipo.equals(MAIN)) {
            String c = user.getString("correo", no_registrado);
            if (c.equals(""))
                c = no_registrado;
            if (!c.equals(no_registrado)) {//solo autorizamos la operacion si el correo de usuario esta debidamente registrado
                String[] params = new String[10];
                user = getSharedPreferences("Usuario", Context.MODE_PRIVATE);
                params[1] = user.getString("correo", no_registrado);
                params[3] = user.getString("nombre", "Username");
                params[4] = user.getString("nacimiento", no_registrado);
                params[5] = user.getString("genero", "M");
                params[6] = user.getString("pais", "null");
                params[7] = user.getString("premium", no);
                params[8] = user.getString("mail_list", si);
                params[9] = user.getString("marketing", si);
                params[0] = tipo;
                //NO actualizamos la ultima modificacion
                params[2] = user.getString("ultima modificacion", "1970-01-15 00:00:00");
                new ActualizarUsuario(params).execute();
            }
        } else if(tipo.equals(SESION)) {
            ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()) {
                String[] params = new String[n_params_sesion];
                params[0] = tipo;
                params[1] = user.getString("correo", no_registrado);
                params[2] = user.getString("registro", "1970-01-15 00:00:00");
                new ActualizarUsuario(params).execute();
            } else {
                AlertDialog alertDialog = new AlertDialog.Builder(this).create();
                alertDialog.setTitle(getString(R.string.amblyomobile));
                alertDialog.setMessage(getString(R.string.no_internet));
                alertDialog.show();
                progressDialog.hide();
            }
        }
    }

    private void noSubidas() {
        //Abrimos en modo escritura porque se va a insertar una fila
        SQLiteDatabase db = MainActivity.partidasDBHelper_historial.getWritableDatabase();
        //volver a intentar subir partidas no subidas
        Cursor c;
        try {
            c = db.rawQuery("SELECT * FROM " + TABLE_NAME_HISTORIAL + " WHERE SUBIDO = 0", null);
        }catch (SQLiteException e){
            //e.printStackTrace();
            Log.e("39", "Error");
            return;
        }
        if (c.moveToFirst()){
            String json = "[";
            StringBuilder sb = new StringBuilder(json);
            do {
                sb.append("{");
                // Passing values
                sb.append("\"CORREO\":\"");
                sb.append(c.getString(1));
                sb.append("\",");
                sb.append("\"USUARIO\":\"");
                sb.append(c.getString(2));
                sb.append("\",");
                SimpleDateFormat format = new SimpleDateFormat("yyyy:MM:dd hh:mm:ss", Locale.getDefault());
                String fecha = format.format(c.getLong(3));
                sb.append("\"DIA\":\"");
                sb.append(fecha);
                sb.append("\",");
                sb.append("\"JUEGO\":\"");
                sb.append(c.getString(4));
                sb.append("\",");
                sb.append("\"S1\":\"");
                sb.append(c.getString(5));
                sb.append("\",");
                sb.append("\"S2\":\"");
                sb.append(c.getString(6));
                sb.append("\",");
                sb.append("\"TIEMPO\":\"");
                sb.append(c.getString(7));
                sb.append("\",");
                sb.append("\"DISPOSITIVO\":\"");
                sb.append(c.getString(8));
                sb.append("\"");
                // Do something Here with values
                sb.append("},");
            } while(c.moveToNext());
            sb.deleteCharAt(sb.lastIndexOf(","));
            sb.append("]");
            json=sb.toString();
            new PartidasNoSubidas(json).start();
        }
        c.close();
        db.close();
    }

    private void updateMysql() {
        String c = user.getString("correo", no_registrado);
        if(c.equals(""))
            c = no_registrado;
        if(!c.equals(no_registrado))
            new UpdatesBBDD(c).execute();
    }


    //BOTONES DEL MENU

    public void verOpciones(View view) {
        String tipo = "configuracion";
        if(aviso.getBoolean(tipo, true))
            mostrarAlertDialog(tipo);
        else {
            Intent i = new Intent(this, SettingsActivity.class);
            startActivity(i);
        }
    }

    public void verActividad(View view) {
        String tipo = "actividad";
        if(aviso.getBoolean(tipo, true))
            mostrarAlertDialog(tipo);
        else {
            Intent i = new Intent(this, ActividadActivity.class);
            startActivity(i);
        }
    }

    public void verSesion(View view) {
        String tipo = "sesion";
        if(aviso.getBoolean(tipo, true))
            mostrarAlertDialog(tipo);
        else {
            if (haySesion())
                abrirSesion(MAIN);
            else
                selectAccount();
        }
    }

    private void selectAccount() {

        try {
            Intent intent = AccountPicker.newChooseAccountIntent(null, null,
                    new String[] { GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE }, false, null, null, null, null);
            startActivityForResult(intent, REQUEST_CODE_EMAIL);
        } catch (ActivityNotFoundException e) {
            // TODO
        }

        // ...
    }

    public void verGafas(View view) {
        WebViewActivity.url = enlace_amazon;
        Intent i = new Intent(this, WebViewActivity.class);
        startActivity(i);
    }

    /*public void verSitioWeb(View view) {
        Uri uri = Uri.parse(        "https://www.amblyopiaonlinegames.com");
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }*/


//******************************

    /*public static void setBienvenido() {
        bienvenido.setText(user.getString("nombre", null));
    }*/

    public void verInfo(View view) {
        String tipo = "informacion";
        if(aviso.getBoolean(tipo, true))
            mostrarAlertDialog(tipo);
        else {
            Intent i = new Intent(this, InfoActivity.class);
            startActivity(i);
        }
    }

    public void sharedApp(View view) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=soft.evm.amblyopiamobilegames");
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }


    //********** MENU JUEGOS

    private void pintarBotonesJuego() {
        element =  new Element(getApplicationContext());
        prefs = getSharedPreferences("MisPreferencias", Context.MODE_PRIVATE);
        Colores.ajustarColores(prefs);
        buttonSnake = findViewById(R.id.ButtonActivityMenuSnake);
        element.setButtonGameMenu(buttonSnake, Colores.getOjo_sano());
        buttonTetris = findViewById(R.id.ButtonActivityMenuTetris);
        element.setButtonGameMenu(buttonTetris, Colores.getOjo_vago());
        buttonFlappy = findViewById(R.id.ButtonActivityMenuFlappy);
        element.setButtonGameMenu(buttonFlappy, Colores.getOjo_vago());
        buttonBreake = findViewById(R.id.ButtonActivityMenuBreake);
        element.setButtonGameMenu(buttonBreake, Colores.getOjo_sano());
        buttonSpace = findViewById(R.id.ButtonActivityMenuSpace);
        element.setButtonGameMenu(buttonSpace, Colores.getOjo_sano());
        buttonPong = findViewById(R.id.ButtonActivityMenuPong);
        element.setButtonGameMenu(buttonPong, Colores.getOjo_vago());
    }

    public void Snake(View view) {
        juego = "Snake";
        Intent i = new Intent(this, GameActivity.class);
        startActivityForResult(i, REQUEST_CODE_GAMEACTIVITY);
    }

    public void Tetris(View view) {
        juego = "Tetris";
        Intent i = new Intent(this, GameActivity.class);
        startActivityForResult(i, REQUEST_CODE_GAMEACTIVITY);
    }

    public void Flappy(View view) {
        juego = "Flappy";
        Intent i = new Intent(this, GameActivity.class);
        startActivityForResult(i, REQUEST_CODE_GAMEACTIVITY);
    }

    public void Breaker(View view) {
        juego = "Breaker";
        Intent i = new Intent(this, GameActivity.class);
        startActivityForResult(i, REQUEST_CODE_GAMEACTIVITY);
    }


    public void Space(View view) {
        juego = "Space Invaders";
        Intent i = new Intent(this, GameActivity.class);
        startActivityForResult(i, REQUEST_CODE_GAMEACTIVITY);
    }

    public void Pong(View view) {
        juego = "Pong";
        Intent i = new Intent(this, GameActivity.class);
        startActivityForResult(i, REQUEST_CODE_GAMEACTIVITY);
    }

    ////////////////////////////////////////////////

    private void rate() {

        //RATE 1
        AppRater.app_launched(this);

        //RATE 2 -> se muestra una vez haya finalizado la fase de RATE 1
        //rate question
        SharedPreferences apprater = getSharedPreferences("apprater", 0);
        editor = apprater.edit();
        if (apprater.getBoolean("dontshowagain", false)) {
            if (apprater.getLong("launch_count", 0) > 20 && apprater.getLong("launch_count", 0) < 30) {
                DialogFragment r = new RateDialogFragment();
                r.show(getFragmentManager(), "dialog");
                editor.putLong("launch_count", veces);
                editor.apply();
            }
        }
    }

    //****************************


    public void verRemidMe(View view) {
        String tipo = "recordatorio";
        if(aviso.getBoolean(tipo, true))
            mostrarAlertDialog(tipo);
        else {
            Intent i = new Intent(this, RemindMeActivity.class);
            startActivity(i);
        }
    }

    public void verDailyTime(View view) {
        String tipo = "alarma";
        if(aviso.getBoolean(tipo, true))
            mostrarAlertDialog(tipo);
        else{
            Intent i = new Intent(this, DailyTimeActivity.class);
            startActivity(i);
        }
    }


    //**********


    private class ActualizarUsuario extends AsyncTask<Void, Void, String>{

        String[] params;

        ActualizarUsuario(String[] params) {
            this.params = params;
            this.tipo = params[0];
        }

        String nombre = "";
        String edad = "";
        String genero = "";
        String pais = "";
        String correo = "";
        String ultimaModificacion = "";
        String premium = "";
        String mail_list = "";
        String marketing = "";
        String registro = "";
        String tipo = "";

        @Override
        protected String doInBackground(Void... voids) {
            Charset UTF_8;
            Charset iso_8859_1;
            //standar parametros
            UTF_8 = StandardCharsets.UTF_8;
            iso_8859_1 = StandardCharsets.ISO_8859_1;
            try {
                this.correo = params[1];

                if (params.length > n_params_sesion) {
                    this.ultimaModificacion = params[2];
                    this.nombre = params[3];
                    this.edad = params[4];
                    this.genero = params[5];
                    this.pais = params[6];
                    this.premium = params[7];
                    this.mail_list = params[8];
                    this.marketing = params[9];

                    if(this.nombre.equals("") || this.nombre.equalsIgnoreCase("null") || this.nombre.isEmpty())
                        this.nombre=this.correo.substring(0,this.correo.indexOf("@"));
                }
                else{
                    this.registro = params[2];
                    this.nombre = "";//nombre nulo
                }
                URL url = new URL(updateURLUsuarios);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, UTF_8));
                String post_data;
                if (params.length > n_params_sesion) {
                    post_data = URLEncoder.encode("correo", "UTF-8") + "=" + URLEncoder.encode(this.correo, "UTF-8")
                            + "&"
                            + URLEncoder.encode("ultima", "UTF-8") + "=" + URLEncoder.encode(this.ultimaModificacion, "UTF-8")
                            + "&"
                            + URLEncoder.encode("nombre", "UTF-8") + "=" + URLEncoder.encode(this.nombre, "UTF-8")
                            + "&"
                            + URLEncoder.encode("edad", "UTF-8") + "=" + URLEncoder.encode(this.edad, "UTF-8")
                            + "&"
                            + URLEncoder.encode("genero", "UTF-8") + "=" + URLEncoder.encode(this.genero, "UTF-8")
                            + "&"
                            + URLEncoder.encode("pais", "UTF-8") + "=" + URLEncoder.encode(this.pais, "UTF-8")
                            + "&"
                            + URLEncoder.encode("premium", "UTF-8") + "=" + URLEncoder.encode(this.premium, "UTF-8")
                            + "&"
                            + URLEncoder.encode("mail_list", "UTF-8") + "=" + URLEncoder.encode(this.mail_list, "UTF-8")
                            + "&"
                            + URLEncoder.encode("marketing", "UTF-8") + "=" + URLEncoder.encode(this.marketing, "UTF-8")
                            + "&"
                            + URLEncoder.encode("idioma", "UTF-8") + "=" + URLEncoder.encode(Locale.getDefault().getLanguage(), "UTF-8");
                } else {
                    post_data = URLEncoder.encode("correo", "UTF-8") + "=" + URLEncoder.encode(this.correo, "UTF-8")
                            + "&"
                            + URLEncoder.encode("nombre", "UTF-8") + "=" + URLEncoder.encode(this.nombre, "UTF-8")
                            + "&"
                            + URLEncoder.encode("idioma", "UTF-8") + "=" + URLEncoder.encode(Locale.getDefault().getLanguage(), "UTF-8")
                            + "&"
                            + URLEncoder.encode("registro", "UTF-8") + "=" + URLEncoder.encode(this.registro, "UTF-8");
                }
                bufferedWriter.write(post_data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, iso_8859_1));
                String result = "";
                String line;
                StringBuilder sb = new StringBuilder(result);
                while ((line = bufferedReader.readLine()) != null) {
                    sb.append(line);
                }
                result = sb.toString();
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();
                return result;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e){
                e.printStackTrace();
                Log.e("38", "Error");
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if(tipo.equals(MAIN))
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            insertar(result);
                            sesionCargando.setVisibility(View.GONE);
                            buttonSesion.setEnabled(true);
                            buttonSesion.setVisibility(View.VISIBLE);
                        }catch (NullPointerException e){
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            else if(tipo.equals(SESION)) {
                try {
                    if(result != null)
                        insertar(result);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                abrirSesion(SESION);
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if(tipo.equals(MAIN)){
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try{
                            buttonSesion.setEnabled(false);
                            buttonSesion.setVisibility(View.GONE);
                        }catch (NullPointerException e){
                            e.printStackTrace();
                        }
                    }
                });
            }
        }

        private void insertar(String result) throws JSONException {
            if (!result.equals("") && !result.equals("[]")) {
                JSONObject myJsonjObject = new JSONObject(result);
                String myJsonString = myJsonjObject.getString("success");
                if (myJsonString.equals("2")) {// entonces el array es de un usuario
                    JSONArray myJsonArray = myJsonjObject.getJSONArray("usuario");
                    JSONObject oneObject = myJsonArray.getJSONObject(0); // Aquí accederías de la misma forma que se muestra arriba ya que es otro objeto
                    this.correo = oneObject.getString("correo");
                    this.nombre = oneObject.getString("nombre");
                    this.edad = oneObject.getString("edad");
                    String[] strings = this.edad.split("-");
                    try {
                        this.edad = strings[2] + "/" + strings[1] + "/" + strings[0];
                    } catch (ArrayIndexOutOfBoundsException e) {
                        this.edad = "1/1/1900";
                    }
                    this.genero = oneObject.getString("genero");
                    this.pais = oneObject.getString("pais");
                    this.ultimaModificacion = oneObject.getString("ultima");

                    this.premium = oneObject.getString("premium");
                    this.mail_list = oneObject.getString("mail_list");
                    this.marketing = oneObject.getString("marketing");

                    this.registro = oneObject.getString("registro");

                    SharedPreferences.Editor editor = user.edit();
                    editor.putString("correo", this.correo);
                    editor.putString("nombre", this.nombre);
                    editor.putString("nacimiento", this.edad);
                    editor.putString("genero", this.genero);
                    editor.putString("pais", this.pais);
                    editor.putString("premium", this.premium);
                    editor.putString("mail_list", this.mail_list);
                    editor.putString("marketing", this.marketing);
                    editor.putString("ultima modificacion", this.ultimaModificacion);
                    editor.putString("registro", this.registro);
                    editor.apply();

                }

            }
        }

    }

    public void startLoadBar() {
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getString(R.string.cargando));
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();
    }

    public void finishLoadBar() {
        progressDialog.hide();
    }

    private class PartidasNoSubidas extends Thread{

        String json;

        PartidasNoSubidas(String json) {
            this.json = json;
        }

        public void run() {
            Charset UTF_8;
            Charset iso_8859_1;
            //standar parametros
            UTF_8 = StandardCharsets.UTF_8;
            iso_8859_1 = StandardCharsets.ISO_8859_1;
            try {
                URL url = new URL(updateURLPartidas);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, UTF_8));
                String post_data = URLEncoder.encode("partidas", "UTF-8") + "=" + URLEncoder.encode(json, "UTF-8");
                bufferedWriter.write(post_data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, iso_8859_1));
                String result = "";
                String line;
                StringBuilder sb = new StringBuilder(result);
                while ((line = bufferedReader.readLine()) != null) {
                    sb.append(line);
                }
                result = sb.toString();
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();

                if (!result.equals("") && !result.equals("[]")) {
                    JSONObject myJsonjObject = new JSONObject(result);
                    String myJsonString = myJsonjObject.getString("success");
                    if (myJsonString.equals("1")) {
                        //Abrimos en modo escritura porque se va a insertar una fila
                        SQLiteDatabase db = MainActivity.partidasDBHelper_historial.getWritableDatabase();
                        ContentValues cv = new ContentValues();
                        cv.put("SUBIDO", 1);
                        db.update(TABLE_NAME_HISTORIAL, cv, "SUBIDO = 0", null);
                        cv.clear();
                        db.close();
                    }
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e){
                e.printStackTrace();
                Log.e("38", "Error");
            }
        }
    }

    private class UpdatesBBDD extends AsyncTask<Void, Void, Void> {

        String correo;

        UpdatesBBDD(String correo){
            this.correo = correo;
        }


        @Override
        protected Void doInBackground(Void... voids) {

            try {

                Charset UTF_8;
                Charset iso_8859_1;
                //standar parametros
                UTF_8 = StandardCharsets.UTF_8;
                iso_8859_1 = StandardCharsets.ISO_8859_1;
                URL url = new URL(updateURLPartidas);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, UTF_8));
                String post_data;
                post_data = URLEncoder.encode("correo", "UTF-8") + "=" + URLEncoder.encode(correo, "UTF-8")
                        + "&"
                        + URLEncoder.encode("tipo", "UTF-8") + "=" + URLEncoder.encode("historial", "UTF-8");
                bufferedWriter.write(post_data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, iso_8859_1));
                String result = "";
                String line;
                StringBuilder sb = new StringBuilder(result);
                while ((line = bufferedReader.readLine()) != null) {
                    sb.append(line);
                }
                result = sb.toString();
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();

                //Abrimos en modo escritura porque se va a insertar una fila
                SQLiteDatabase db = MainActivity.partidasDBHelper_historial.getWritableDatabase();
                if(db.isOpen()) {
                    db.delete(TABLE_NAME_HISTORIAL, "SUBIDO = 1", null);
                    JSONObject myJsonjObject = new JSONObject(result);
                    JSONArray myJsonArray = myJsonjObject.getJSONArray("partidas");
                    for (int i = 0; i < myJsonArray.length(); i++) {
                        JSONObject oneObject = myJsonArray.getJSONObject(i);
                        String correo = oneObject.getString("correo");
                        String usuario = oneObject.getString("usuario");
                        String myDate = oneObject.getString("fecha");
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                        Date date = sdf.parse(myDate);
                        long fecha = date.getTime();
                        String juego = oneObject.getString("juego");
                        int score1 = oneObject.getInt("score1");
                        int score2 = oneObject.getInt("score2");
                        long tiempo = oneObject.getLong("tiempo");
                        String dispositivo = oneObject.getString("dispositivo");

                        //Creamos mapa de valores. Los nombres de las coumnas son las keys
                        ContentValues values = new ContentValues();

                        values.put(PartidaContract.Partida.COLUMN_NAME_CORREO, correo);
                        values.put(PartidaContract.Partida.COLUMN_NAME_NOMBRE, usuario);
                        values.put(PartidaContract.Partida.COLUMN_NAME_DIA, fecha);
                        values.put(PartidaContract.Partida.COLUMN_NAME_JUEGO, juego);
                        values.put(PartidaContract.Partida.COLUMN_NAME_S1, score1);
                        values.put(PartidaContract.Partida.COLUMN_NAME_S2, score2);
                        values.put(PartidaContract.Partida.COLUMN_NAME_TIEMPO, tiempo);
                        values.put(PartidaContract.Partida.COLUMN_NAME_DEVICE, dispositivo);
                        values.put(PartidaContract.Partida.COLUMN_NAME_SUBIDO, 1);

                        db.insert(TABLE_NAME_HISTORIAL, null, values);
                    }
                }

                db.close();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e){
                e.printStackTrace();
                Log.e("38", "Error");
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    buttonActividad.setEnabled(true);
                    buttonActividad.setVisibility(View.VISIBLE);
                }

            });
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    buttonActividad.setEnabled(false);
                    buttonActividad.setVisibility(View.GONE);
                }
            });
        }
    }

    /**
     * Aquí nos comunicamos con el play store
     */


    private void PlayStore() {
        // Create and initialize BillingManager which talks to BillingLibrary
        mBillingManager = new BillingManager(this, new UpdateListener());
        mBillingManager.queryPurchases();
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

            try {
                mostrarOferta();
            }catch(NullPointerException e){
                e.printStackTrace();
                //69->70
            }

            mostrarAnuncios();
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
        super.onDestroy();
        Log.d(TAG, "Destroying helper.");
        if (mBillingManager != null) {
            mBillingManager.destroy();
        }
    }

    /*
     *
     * Ofertar:
     *
     *   Si no es premium:
     *
     *       - N0 día : oferta 0
     *
     *       - N1 día : oferta 1
     *
     *       - N2 día : oferta 2
     *
     *       - N3 día : oferta 3
     *
     *   Para ello debemos
     *
     *       - Calcular días
     *
     *       - Modificar interfaz
     *
     *
     * */

    //dias oferta
    public int dias = 0;

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

    private void mostrarOferta() {
        ImageButton offer = findViewById(R.id.imageButtonMainOffer);
        offer.setVisibility(View.GONE);

        if (!(isPremiumPurchased() && premiumKey.equals(BuildConfig.PREMIUM_KEY))) {

            offer.setVisibility(View.VISIBLE);

            //
            //dias = 10;
            //

            if (dias > N0) {
                if (dias < N1)
                    offer.setImageResource(R.drawable.offer1);//en drawable-xxxhdpi
                else if (dias < N2)
                    offer.setImageResource(R.drawable.offer2);//en drawable-xxxhdpi
                else if (dias < N3)
                    offer.setImageResource(R.drawable.offer3);//en drawable-xxxhdpi
                else
                    offer.setImageResource(R.drawable.offer4);//en drawable-xxxhdpi
            }

        }
        else if(premiumKey.equals(BuildConfig.PREMIUM_KEY)){
            offer.setVisibility(View.VISIBLE);
            offer.setImageResource(R.drawable.premium);//en drawable-xxxhdpi
        }

    }

    private void mostrarAnuncios() {
        MiAdMob.mostrarAds(this.getWindow().getDecorView().findViewById(android.R.id.content));
    }

    /*
     *
     * purchases
     *
     * */

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

    public void buy(View view) {
        if(!mIsPremium) {
            if (dias < N1)
                purchase();
            else if (dias < N2)
                purchase2();
            else if (dias < N3)
                purchase3();
            else
                purchase4();
        }
    }

    //COPIA Y PEGA DEL ACTUALIZAR DEL SESION ACTIVITY updateMysql
    public void updateBBDDSessionActivity(Context context) {
        String[] params = new String[9];
        user =  context.getSharedPreferences("Usuario", Context.MODE_PRIVATE);
        //visibles
        params[0]=user.getString("correo", no_registrado);
        params[2]=user.getString("nombre", "");
        params[3]=user.getString("nacimiento", no_registrado);
        params[4]=user.getString("genero", no_registrado);
        params[5]=user.getString("pais", no_registrado);
        params[6] = user.getString("premium", no);
        params[7] = user.getString("mail_list", si);
        params[8] = user.getString("marketing", si);
        //no visible
        long ahora = System.currentTimeMillis();
        Date date = new Date(ahora);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.getDefault());
        String dateText = sdf.format(date);
        SharedPreferences.Editor editor;
        editor = user.edit();
        editor.putString("ultima modificacion", dateText);
        editor.apply();
        params[1]=user.getString("ultima modificacion", no_registrado);
        //
        try {
            new ActualizarThread(params).start();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("38", "Error");
        }
    }

    public static class ActualizarThread extends Thread{

        String[] params;

        ActualizarThread(String[] params){
            this.params = params;
        }

        String correo = "";
        String nombre = "";
        String edad = "";
        String genero = "";
        String pais = "";
        String ultimaModificacion = "";
        String premium = "";
        String mail_list = "";
        String marketing = "";

        public void run(){
            Charset UTF_8;
            Charset iso_8859_1;
            //standar parametros
            UTF_8 = StandardCharsets.UTF_8;
            iso_8859_1 = StandardCharsets.ISO_8859_1;
            try {
                correo = params[0];
                ultimaModificacion = params[1];
                nombre = params[2];
                edad = params[3];
                genero = params[4];
                pais = params[5];
                premium = params[6];
                mail_list = params[7];
                marketing = params[8];
                if(nombre.equals("") || nombre.equalsIgnoreCase("null") || nombre.isEmpty())
                    nombre=correo.substring(0,correo.indexOf("@"));
                URL url = new URL("https://amblyomobile.app/data/usuarios_actualizar.php");
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, UTF_8));
                String post_data;
                post_data = URLEncoder.encode("correo", "UTF-8") + "=" + URLEncoder.encode(correo, "UTF-8")
                        + "&"
                        + URLEncoder.encode("ultima", "UTF-8") + "=" + URLEncoder.encode(ultimaModificacion, "UTF-8")
                        + "&"
                        + URLEncoder.encode("nombre", "UTF-8") + "=" + URLEncoder.encode(nombre, "UTF-8")
                        + "&"
                        + URLEncoder.encode("edad", "UTF-8") + "=" + URLEncoder.encode(edad, "UTF-8")
                        + "&"
                        + URLEncoder.encode("genero", "UTF-8") + "=" + URLEncoder.encode(genero, "UTF-8")
                        + "&"
                        + URLEncoder.encode("pais", "UTF-8") + "=" + URLEncoder.encode(pais, "UTF-8")
                        + "&"
                        + URLEncoder.encode("premium", "UTF-8") + "=" + URLEncoder.encode(this.premium, "UTF-8")
                        + "&"
                        + URLEncoder.encode("mail_list", "UTF-8") + "=" + URLEncoder.encode(this.mail_list, "UTF-8")
                        + "&"
                        + URLEncoder.encode("marketing", "UTF-8") + "=" + URLEncoder.encode(this.marketing, "UTF-8")
                        + "&"
                        + URLEncoder.encode("idioma", "UTF-8") + "=" + URLEncoder.encode(Locale.getDefault().getLanguage(), "UTF-8");
                bufferedWriter.write(post_data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, iso_8859_1));
                String result = "";
                String line;
                StringBuilder sb = new StringBuilder(result);
                while ((line = bufferedReader.readLine()) != null) {
                    sb.append(line);
                }
                result = sb.toString();
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();

                if (result.equals("") || result.equals("[]"))
                    return;
                JSONObject myJsonjObject = new JSONObject(result);
                String myJsonString = myJsonjObject.getString("success");

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e){
                e.printStackTrace();
                Log.e("38", "Error");
            }

        }

    }

    @Override
    public void finish() {
        super.finish();
        //MiAdMob.showInterstitialAd(); -> No coloque anuncios intersticiales al cargar la aplicación o al salir de ella, ya que solo deben ubicarse entre las páginas del contenido.
    }


}