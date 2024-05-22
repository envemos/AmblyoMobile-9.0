package soft.evm.amblyopiamobilegames.table;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import soft.evm.amblyopiamobilegames.MainActivity;
import soft.evm.amblyopiamobilegames.R;
import soft.evm.amblyopiamobilegames.SettingsActivity;
import soft.evm.amblyopiamobilegames.ads.MiAdMob;
import soft.evm.amblyopiamobilegames.ads.YodoAds;
import soft.evm.amblyopiamobilegames.juegos.GameActivity;
import soft.evm.amblyopiamobilegames.layoutElements.Element;
import soft.evm.amblyopiamobilegames.model.PartidaContract;

import static soft.evm.amblyopiamobilegames.MainActivity.no_registrado;
import static soft.evm.amblyopiamobilegames.MainActivity.updateURLPartidas;
import static soft.evm.amblyopiamobilegames.model.PartidaContract.Partida.TABLE_NAME_RANKING;

public class TableViewRanking extends AppCompatActivity {

    private TableLayout mTableLayout;
    private ProgressDialog progressDialog;
    AlertDialog alertDialog;

    private String guardarConsulta, ultimaConsulta = "";

    Partidas partidas = new Partidas();
    PartidaData[] data;

    Element element;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // remove title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.table_view);

        element = new Element(getApplicationContext());

        progressDialog = new ProgressDialog(this);
        alertDialog = new AlertDialog.Builder(this).create();

        // setup the table
        mTableLayout = (TableLayout) findViewById(R.id.tableInvoices);

        mTableLayout.setStretchAllColumns(true);

        startLoadData();

    }

    public void startLoadData() {
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getString(R.string.cargando));
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();
        new LoadDataTask().execute(0);

    }

    public void loadData() throws NullPointerException{

        int leftRowMargin=0;
        int topRowMargin=0;
        int rightRowMargin=0;
        int bottomRowMargin=0;
        int textSize = 0, smallTextSize =0, mediumTextSize = 0;

        textSize = (int) getResources().getDimension(R.dimen.Table_font_size_verysmall);
        smallTextSize = (int) getResources().getDimension(R.dimen.Table_font_size_small);
        mediumTextSize = (int) getResources().getDimension(R.dimen.Table_font_size_medium);

        //SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/YYYY/hh:mm:ss");
        //SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/YYYY");
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy\nHH:mm:ss", java.util.Locale.getDefault());

        int rows = data.length;

        getSupportActionBar().setTitle(MainActivity.juego + " (" + String.valueOf(rows) + " " + getString(R.string.resultados_));

        TextView textSpacer = null;

        mTableLayout.removeAllViews();

        SharedPreferences user = getSharedPreferences("Usuario", Context.MODE_PRIVATE);

        int rank = 0;
        //para el snake
        int muros = 0, sinMuros = 0;
        // -1 means heading row
        for(int i = -1; i < rows; i ++) {
            PartidaData row = null;
            if (i > -1)
                row = data[i];
            else {
                textSpacer = new TextView(this);
                textSpacer.setText("");

            }

            // data columns
            final TextView tv0 = new TextView(this);
            tv0.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                    TableRow.LayoutParams.MATCH_PARENT));

            tv0.setGravity(Gravity.START);

            tv0.setPadding(20, 20, 20, 20);
            tv0.setBackgroundColor(Color.WHITE);
            tv0.setTextColor(getResources().getColor(R.color.Blanco));
            if (i != -1) {
                tv0.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                        TableRow.LayoutParams.MATCH_PARENT));
                tv0.setBackgroundColor(Color.YELLOW);
                tv0.setTextColor(getResources().getColor(R.color.Negro));
                tv0.setText(String.valueOf(++rank));
                tv0.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
                if(row.correo.equals(user.getString("correo", "")))
                    tv0.setTextColor(Color.BLUE);
                element.setTableContent(tv0);
            }


            final TextView tv = new TextView(this);
            tv.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                    TableRow.LayoutParams.WRAP_CONTENT));

            tv.setGravity(Gravity.START);

            tv.setPadding(20, 20, 20, 20);
            tv.setBackgroundColor(getResources().getColor(R.color.Negro));
            tv.setTextColor(getResources().getColor(R.color.Blanco));
            if (i == -1) {
                tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, mediumTextSize);
                tv.setText(R.string.nombre);
                element.setTableTitle(tv);
            } else {
                tv.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                        TableRow.LayoutParams.MATCH_PARENT));
                tv.setBackgroundColor(Color.parseColor("#f0f0f0"));
                tv.setTextColor(getResources().getColor(R.color.Negro));
                tv.setText(row.nombre);
                if(row.correo.equals(user.getString("correo", "")))
                    tv.setTextColor(Color.BLUE);
                tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
                element.setTableContent(tv);
            }



            final LinearLayout layCustomer = new LinearLayout(this);
            layCustomer.setOrientation(LinearLayout.VERTICAL);
            //layCustomer.setPadding(0, 10, 0, 10);
            layCustomer.setBackgroundColor(Color.parseColor("#f8f8f8"));

            final TextView tv2 = new TextView(this);
            if (i == -1) {
                tv2.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                        TableRow.LayoutParams.MATCH_PARENT));
                //tv2.setPadding(5, 5, 0, 5);
                tv2.setTextSize(TypedValue.COMPLEX_UNIT_PX, smallTextSize);
            } else {
                tv2.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                        TableRow.LayoutParams.MATCH_PARENT));
                //tv2.setPadding(5, 0, 0, 5);
                tv2.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
            }

            tv2.setGravity(Gravity.TOP);

            tv2.setPadding(20, 20, 20, 20);


            if (i == -1) {
                tv2.setPadding(20, 20, 20, 20);
                tv2.setText(R.string.puntuacion);
                tv2.setBackgroundColor(Color.parseColor("#f0f0f0"));
                tv2.setBackgroundColor(getResources().getColor(R.color.colorVerde));
                tv2.setTextColor(getResources().getColor(R.color.Blanco));
                tv2.setTextSize(TypedValue.COMPLEX_UNIT_PX, mediumTextSize);
                element.setTableTitle(tv2);
            } else {
                tv2.setPadding(10, 10, 0, 10);
                tv2.setBackgroundColor(Color.parseColor("#f8f8f8"));
                tv2.setTextColor(Color.parseColor("#000000"));
                tv2.setTextSize(TypedValue.COMPLEX_UNIT_PX, smallTextSize);
                tv2.setText(String.valueOf(row.s1));
                element.setTableContent(tv2);
            }
            layCustomer.addView(tv2);


            if (i > -1) {
                final TextView tv2b = new TextView(this);
                tv2b.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                        TableRow.LayoutParams.WRAP_CONTENT));

                tv2b.setGravity(Gravity.RIGHT);
                tv2b.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
                tv2b.setPadding(10, 10, 0, 10);
                tv2b.setTextColor(Color.parseColor("#aaaaaa"));
                tv2b.setBackgroundColor(Color.parseColor("#f8f8f8"));
                //para los colores
                SpannableString mensaje;
                ForegroundColorSpan colorSpan;
                switch (row.juego) {
                    case "SNAKE (SIN MUROS)":
                        mensaje = new SpannableString(getString(R.string.comidos_) + " " + String.valueOf(row.s2) + "\n" + getString(R.string.muros) + ": " +getString(R.string.no));
                        colorSpan = new ForegroundColorSpan(Color.RED);// Puedes usar tambien .. new ForegroundColorSpan(Color.RED); Color.parseColor("#FF0040")
                        mensaje.setSpan(colorSpan, mensaje.length()-2, mensaje.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                        tv2b.setText(mensaje);
                        tv0.setText(String.valueOf(++sinMuros));
                        break;
                    case "SNAKE (CON MUROS)":
                        mensaje = new SpannableString(getString(R.string.comidos_) + " " + String.valueOf(row.s2) + "\n" + getString(R.string.muros) + ": " + getString(R.string.si));
                        colorSpan = new ForegroundColorSpan(Color.GREEN);// Puedes usar tambien .. new ForegroundColorSpan(Color.RED); Color.parseColor("#FF0040")
                        mensaje.setSpan(colorSpan, mensaje.length()-2, mensaje.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                        tv2b.setText(mensaje);
                        tv0.setText(String.valueOf(++muros));
                        tv0.setBackgroundColor(Color.BLUE);
                        tv0.setTextColor(Color.WHITE);
                        if(row.correo.equals(user.getString("correo", ""))) {
                            tv0.setTextColor(Color.RED);
                            tv.setTextColor(Color.RED);
                        }
                        //case "SNAKE":
                        //tv3b.setText(getString(R.string.comidos_) + " " + String.valueOf(row.s2));
                        break;
                    case "TETRIS":
                        tv2b.setText(getString(R.string.lineas_) + " " + String.valueOf(row.s2));
                        break;
                    case "FLAPPY":
                        tv2b.setText(getString(R.string.saltos_) + " " + String.valueOf(row.s2));
                        break;
                    case "BREAKER":
                        tv2b.setText(getString(R.string.levels_) + " " + String.valueOf(row.s2));
                        break;

                    case "SPACE":
                        tv2b.setText(getString(R.string.oleadas) + " " + String.valueOf(row.s2));
                        break;
                    case "PONG":
                        String dificultad;
                        if(String.valueOf(row.s2).equals("0"))
                            dificultad=getString(R.string.normal);
                        else
                            dificultad=getString(R.string.imposible);
                        tv2b.setText(dificultad);
                        break;
                    case "PACMAN":
                        tv2b.setText(getString(R.string.levels_) + " " + String.valueOf(row.s2));
                        break;
                    case "PINBALL":
                        tv2b.setText(getString(R.string.levels_) + " " + String.valueOf(row.s2));
                        break;
                }

                layCustomer.addView(tv2b);
                element.setTableContent(tv2b);
            }


            final TextView tv3 = new TextView(this);
            if (i == -1) {
                tv3.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                        TableRow.LayoutParams.WRAP_CONTENT));
                tv3.setTextSize(TypedValue.COMPLEX_UNIT_PX, smallTextSize);
            } else {
                tv3.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                        TableRow.LayoutParams.MATCH_PARENT));
                tv3.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
            }
            tv3.setPadding(20, 20, 20, 20);

            if (i == -1) {
                tv3.setBackgroundColor(getResources().getColor(R.color.colorRojo));
                tv3.setTextColor(getResources().getColor(R.color.Blanco));
                tv3.setTextSize(TypedValue.COMPLEX_UNIT_PX, mediumTextSize);
                tv3.setText(R.string.fecha);
                element.setTableTitle(tv3);
            }else {
                tv3.setBackgroundColor(Color.parseColor("#ffffff"));
                tv3.setTextColor(Color.parseColor("#000000"));
                //Date d=new Date(row.fecha+28800000);
                //tv2.setText(dateFormat.format(d));
                tv3.setText(dateFormat.format(row.fecha));
                element.setTableContent(tv3);
            }


            final LinearLayout layAmounts = new LinearLayout(this);
            layAmounts.setOrientation(LinearLayout.VERTICAL);
            layAmounts.setGravity(Gravity.RIGHT);
            //layAmounts.setPadding(0, 10, 0, 10);
            layAmounts.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                    TableRow.LayoutParams.MATCH_PARENT));



            final TextView tv4 = new TextView(this);
            if (i == -1) {
                tv4.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                        TableRow.LayoutParams.MATCH_PARENT));
                //tv4.setPadding(5, 5, 1, 5);
                layAmounts.setBackgroundColor(Color.parseColor("#f7f7f7"));
            } else {
                tv4.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                        TableRow.LayoutParams.WRAP_CONTENT));
                //tv4.setPadding(5, 0, 1, 5);
                layAmounts.setBackgroundColor(Color.parseColor("#ffffff"));
            }

            tv4.setGravity(Gravity.LEFT);

            if (i == -1) {
                tv4.setText(R.string.tiempo);
                tv4.setPadding(20, 20, 20, 20);
                tv4.setBackgroundColor(Color.parseColor("#f7f7f7"));
                tv4.setTextSize(TypedValue.COMPLEX_UNIT_PX, smallTextSize);
                tv4.setBackgroundColor(getResources().getColor(R.color.colorAzul));
                tv4.setTextColor(getResources().getColor(R.color.Blanco));
                tv4.setTextSize(TypedValue.COMPLEX_UNIT_PX, mediumTextSize);
                element.setTableTitle(tv4);
            } else {
                tv4.setPadding(10, 10, 0, 10);
                tv4.setBackgroundColor(Color.parseColor("#ffffff"));
                tv4.setTextColor(Color.parseColor("#000000"));
                tv4.setText(formatearMilis(row.tiempo));
                tv4.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
                element.setTableContent(tv4);
            }

            layAmounts.addView(tv4);

            // add table row
            final TableRow tr = new TableRow(this);
            tr.setId(i + 1);
            TableLayout.LayoutParams trParams = new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,
                    TableLayout.LayoutParams.WRAP_CONTENT);
            trParams.setMargins(leftRowMargin, topRowMargin, rightRowMargin, bottomRowMargin);
            tr.setPadding(0,0,0,0);
            tr.setLayoutParams(trParams);


            tr.addView(tv0);
            tr.addView(tv);
            tr.addView(layCustomer);
            tr.addView(tv3);
            tr.addView(layAmounts);

            mTableLayout.addView(tr, trParams);

            if (i > -1) {

                // add separator row
                final TableRow trSep = new TableRow(this);
                TableLayout.LayoutParams trParamsSep = new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,
                        TableLayout.LayoutParams.WRAP_CONTENT);
                trParamsSep.setMargins(leftRowMargin, topRowMargin, rightRowMargin, bottomRowMargin);

                trSep.setLayoutParams(trParamsSep);
                TextView tvSep = new TextView(this);
                TableRow.LayoutParams tvSepLay = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                        TableRow.LayoutParams.WRAP_CONTENT);
                tvSepLay.span = 4;
                tvSep.setLayoutParams(tvSepLay);
                tvSep.setBackgroundColor(Color.parseColor("#d9d9d9"));
                tvSep.setHeight(1);

                trSep.addView(tvSep);
                mTableLayout.addView(trSep, trParamsSep);
            }


        }
    }

    //////////////////////////////////////////////////////////////////////////////

    //
    // The params are dummy and not used
    //
    class LoadDataTask extends AsyncTask<Integer, Integer, String> {
        @Override
        protected String doInBackground(Integer... params) {

            String result = "";

            // comprobar conexión a internet
            /*ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if (networkInfo == null || !networkInfo.isConnected()){
                result = "noInternet";
                return result;
            }*/

            // 1 comunicarse con el servidor y devolver el ranking actualizado
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
                SharedPreferences user = getSharedPreferences("Usuario", Context.MODE_PRIVATE);
                String correo = user.getString("correo", no_registrado);
                post_data = URLEncoder.encode("correo", "UTF-8") + "=" + URLEncoder.encode(correo, "UTF-8")
                        + "&"
                        + URLEncoder.encode("tipo", "UTF-8") + "=" + URLEncoder.encode("ranking", "UTF-8")
                        + "&"
                        + URLEncoder.encode("juego", "UTF-8") + "=" + URLEncoder.encode(GameActivity.juegoSQL, "UTF-8");
                bufferedWriter.write(post_data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, iso_8859_1));
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
                SQLiteDatabase db = MainActivity.partidasDBHelper_ranking.getWritableDatabase();
                if (db.isOpen()) {
                    db.delete(TABLE_NAME_RANKING, "SUBIDO = 1", null);
                    JSONObject myJsonjObject = new JSONObject(result);
                    JSONArray myJsonArray = myJsonjObject.getJSONArray("partidas");
                    for (int i = 0; i < myJsonArray.length(); i++) {
                        JSONObject oneObject = myJsonArray.getJSONObject(i);
                        correo = oneObject.getString("correo");
                        String usuario = oneObject.getString("usuario");
                        String myDate = oneObject.getString("fecha");
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault());
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

                        db.insert(TABLE_NAME_RANKING, null, values);
                    }
                }

                result = "correcto";
                db.close();

            } catch (Exception e){
                result = "error";
                e.printStackTrace();
                Log.e("38", "Error");
            }
            // 2 devolver la tabla ranking
            data = partidas.getPartidasRanking();
            String sql = Partidas.sql_ranking;
            return result;
        }
        @Override
        protected void onPostExecute(String result) {
            progressDialog.hide();
            switch (result) {
                case "correcto":
                    try {
                        loadData();
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                        Log.e("39", "Error");
                    } catch (IllegalStateException e) {
                        e.printStackTrace();
                        //error 58
                    }
                    break;
                case "noInternet":
                case "error":
                    alertDialog.setTitle(getString(R.string.amblyomobile));
                    alertDialog.setMessage(getString(R.string.no_internet));
                    alertDialog.show();
                    break;

                //No se ha podido establecer
            }
        }
        @Override
        protected void onPreExecute() {
        }
        @Override
        protected void onProgressUpdate(Integer... values) {

        }
    }

    public String formatearMilis(long milis) {
        String formato = "%02d:%02d:%02d";
        long minutosReales = TimeUnit.MILLISECONDS.toMinutes(milis);
        long segundosReales = TimeUnit.MILLISECONDS.toSeconds(milis) - minutosReales * 60;
        long decimasReales = (TimeUnit.MILLISECONDS.toMillis(milis) - TimeUnit.MINUTES.toMillis(minutosReales) - TimeUnit.SECONDS.toMillis(segundosReales))%1000/10;
        return String.format(formato, minutosReales, segundosReales, decimasReales);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        if ( progressDialog !=null && progressDialog.isShowing() ){
            progressDialog.cancel();//cancelamos el asyntask al salir de la actividad y evitamos la excepción
        }
    }

    @Override
    public void finish() {
        super.finish();
        //MiAdMob.showInterstitialAd();
    }

}