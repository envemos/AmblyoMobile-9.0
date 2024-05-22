package soft.evm.amblyopiamobilegames.table;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.concurrent.TimeUnit;

import soft.evm.amblyopiamobilegames.MainActivity;
import soft.evm.amblyopiamobilegames.R;
import soft.evm.amblyopiamobilegames.SettingsActivity;
import soft.evm.amblyopiamobilegames.ads.MiAdMob;
import soft.evm.amblyopiamobilegames.ads.YodoAds;
import soft.evm.amblyopiamobilegames.layoutElements.Element;
import soft.evm.amblyopiamobilegames.model.PartidaContract;

import static soft.evm.amblyopiamobilegames.ActividadActivity.m;
import static soft.evm.amblyopiamobilegames.ActividadActivity.milis_dia;
import static soft.evm.amblyopiamobilegames.MainActivity.no_registrado;

public class TableViewHistorial extends AppCompatActivity {

    private TableLayout mTableLayout;
    private ProgressDialog progressDialog;

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

    public void loadData() {
        data = partidas.getPartidasHistorial();

        int leftRowMargin=0;
        int topRowMargin=0;
        int rightRowMargin=0;
        int bottomRowMargin = 0;
        int textSize = 0, smallTextSize =0, mediumTextSize = 0;

        textSize = (int) getResources().getDimension(R.dimen.Table_font_size_verysmall);
        smallTextSize = (int) getResources().getDimension(R.dimen.Table_font_size_small);
        mediumTextSize = (int) getResources().getDimension(R.dimen.Table_font_size_medium);

        //SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/YYYY/hh:mm:ss");
        //SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/YYYY");
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy\nHH:mm:ss", java.util.Locale.getDefault());

        int rows = data.length;

        final SharedPreferences user = getSharedPreferences("Usuario", Context.MODE_PRIVATE);
        String username = user.getString("nombre", "(not register)");
        getSupportActionBar().setTitle(username + " (" + String.valueOf(rows) + " " + getString(R.string.resultados_));

        TextView textSpacer = null;

        mTableLayout.removeAllViews();

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
            final TextView tv = new TextView(this);
            if (i == -1) {
                tv.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                        TableRow.LayoutParams.WRAP_CONTENT));
                tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, smallTextSize);
            } else {
                tv.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                        TableRow.LayoutParams.MATCH_PARENT));
                tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
            }
            tv.setPadding(20, 20, 20, 20);

            if (i == -1) {
                tv.setBackgroundColor(getResources().getColor(R.color.colorRojo));
                tv.setTextColor(getResources().getColor(R.color.Blanco));
                tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, mediumTextSize);
                tv.setText(R.string.fecha);
                tv.setOnClickListener(new View.OnClickListener() {//ORDENARLOS POR FECHA

                    @Override
                    public void onClick(View v) {


                        Partidas partidas = new Partidas();
                        guardarConsulta=Partidas.sql_historial;
                        Partidas.sql_historial = "SELECT * FROM " + PartidaContract.Partida.TABLE_NAME_HISTORIAL + " WHERE DIA BETWEEN " + m + " AND " + (m + milis_dia) + " AND CORREO = '" + user.getString("correo", no_registrado) + "'";
                        if(ultimaConsulta.contains(" ORDER BY DIA DESC"))
                            Partidas.sql_historial+=" ORDER BY DIA";
                        else
                            Partidas.sql_historial+=" ORDER BY DIA DESC";
                        data = partidas.getPartidasHistorial();
                        loadData();
                        ultimaConsulta = Partidas.sql_historial;
                        Partidas.sql_historial=guardarConsulta;
                    }
                });
                element.setTableTitle(tv);
            }else {
                tv.setBackgroundColor(Color.parseColor("#ffffff"));
                tv.setTextColor(Color.parseColor("#000000"));
                //Date d=new Date(row.fecha+28800000);
                //tv2.setText(dateFormat.format(d));
                tv.setText(dateFormat.format(row.fecha));
                element.setTableContent(tv);
            }

            final TextView tv2 = new TextView(this);
            tv2.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                    TableRow.LayoutParams.WRAP_CONTENT));

            tv2.setGravity(Gravity.LEFT);

            tv2.setPadding(20, 20, 20, 20);
            tv2.setBackgroundColor(getResources().getColor(R.color.Negro));
            tv2.setTextColor(getResources().getColor(R.color.Blanco));
            tv2.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                    TableRow.LayoutParams.MATCH_PARENT));
            tv2.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);

            if (i == -1) {
                tv2.setTextSize(TypedValue.COMPLEX_UNIT_PX, mediumTextSize);
                tv2.setText(R.string.juego);
                tv2.setOnClickListener(new View.OnClickListener() {//ORDENARLOS POR JUEGO

                    @Override
                    public void onClick(View v) {

                        Partidas partidas = new Partidas();
                        guardarConsulta=Partidas.sql_historial;
                        Partidas.sql_historial = "SELECT * FROM " + PartidaContract.Partida.TABLE_NAME_HISTORIAL + " WHERE DIA BETWEEN " + m + " AND " + (m + milis_dia) + " AND CORREO = '" + user.getString("correo", no_registrado) + "'";;
                        if(ultimaConsulta.contains(" ORDER BY JUEGO DESC"))
                            Partidas.sql_historial+=" ORDER BY JUEGO";
                        else
                            Partidas.sql_historial+=" ORDER BY JUEGO DESC";
                        data = partidas.getPartidasHistorial();
                        loadData();
                        ultimaConsulta = Partidas.sql_historial;
                        Partidas.sql_historial=guardarConsulta;
                    }
                });
                element.setTableTitle(tv2);
            } else {
                tv2.setBackgroundColor(Color.parseColor("#f0f0f0"));
                tv2.setTextColor(getResources().getColor(R.color.Negro));
                if(row.juego.contains("SNAKE"))//PARA QUE APAREZCA 'SNAKE' SOLO. LOS MUROS SE MUESTRAN EN OTRA VARIABLE
                    tv2.setText("SNAKE");
                else
                    tv2.setText(row.juego);
                tv2.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
                element.setTableContent(tv2);
            }

            final LinearLayout layCustomer = new LinearLayout(this);
            layCustomer.setOrientation(LinearLayout.VERTICAL);
            //layCustomer.setPadding(0, 10, 0, 10);
            layCustomer.setBackgroundColor(Color.parseColor("#f8f8f8"));

            final TextView tv3 = new TextView(this);
            if (i == -1) {
                tv3.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                        TableRow.LayoutParams.MATCH_PARENT));
                //tv3.setPadding(5, 5, 0, 5);
                tv3.setTextSize(TypedValue.COMPLEX_UNIT_PX, smallTextSize);
            } else {
                tv3.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                        TableRow.LayoutParams.MATCH_PARENT));
                //tv3.setPadding(5, 0, 0, 5);
                tv3.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
            }

            tv3.setGravity(Gravity.TOP);

            tv3.setPadding(20, 20, 20, 20);


            if (i == -1) {
                tv3.setPadding(20, 20, 20, 20);
                tv3.setText(R.string.puntuacion);
                tv3.setBackgroundColor(Color.parseColor("#f0f0f0"));
                tv3.setBackgroundColor(getResources().getColor(R.color.colorVerde));
                tv3.setTextColor(getResources().getColor(R.color.Blanco));
                tv3.setTextSize(TypedValue.COMPLEX_UNIT_PX, mediumTextSize);
                tv3.setOnClickListener(new View.OnClickListener() {//ORDENARLOS POR PUNTUACIÓN

                    @Override
                    public void onClick(View v) {

                        Partidas partidas = new Partidas();
                        guardarConsulta=Partidas.sql_historial;
                        Partidas.sql_historial = "SELECT * FROM " + PartidaContract.Partida.TABLE_NAME_HISTORIAL + " WHERE DIA BETWEEN " + m + " AND " + (m + milis_dia) + " AND CORREO = '" + user.getString("correo", no_registrado) + "'";
                        if(ultimaConsulta.contains(" ORDER BY S1 DESC"))
                            Partidas.sql_historial+=" ORDER BY S1";
                        else if(ultimaConsulta.contains(" ORDER BY S1"))
                            Partidas.sql_historial+=" ORDER BY S2 DESC";
                        else if(ultimaConsulta.contains(" ORDER BY S2 DESC"))
                            Partidas.sql_historial+=" ORDER BY S2";
                        else
                            Partidas.sql_historial+=" ORDER BY S1 DESC";
                        data = partidas.getPartidasHistorial();
                        loadData();
                        ultimaConsulta = Partidas.sql_historial;
                        Partidas.sql_historial=guardarConsulta;
                    }
                });
                element.setTableTitle(tv3);
            } else {
                tv3.setPadding(10, 10, 0, 10);
                tv3.setBackgroundColor(Color.parseColor("#f8f8f8"));
                tv3.setTextColor(Color.parseColor("#000000"));
                tv3.setTextSize(TypedValue.COMPLEX_UNIT_PX, smallTextSize);
                tv3.setText(String.valueOf(row.s1));
                element.setTableContent(tv3);
            }
            layCustomer.addView(tv3);


            if (i > -1) {
                final TextView tv3b = new TextView(this);
                tv3b.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                        TableRow.LayoutParams.WRAP_CONTENT));

                tv3b.setGravity(Gravity.RIGHT);
                tv3b.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
                tv3b.setPadding(10, 10, 0, 10);
                tv3b.setTextColor(Color.parseColor("#aaaaaa"));
                tv3b.setBackgroundColor(Color.parseColor("#f8f8f8"));
                switch (row.juego) {
                    case "SNAKE (SIN MUROS)":
                        tv3b.setText(getString(R.string.comidos_) + " " + String.valueOf(row.s2) + "\n" + getString(R.string.muros) + ": " + getString(R.string.no));
                        break;
                    case "SNAKE (CON MUROS)":
                        tv3b.setText(getString(R.string.comidos_) + " " + String.valueOf(row.s2) + "\n" + getString(R.string.muros) + ": " + getString(R.string.si));
                    //case "SNAKE":
                        //tv3b.setText(getString(R.string.comidos_) + " " + String.valueOf(row.s2));
                        break;
                    case "TETRIS":
                        tv3b.setText(getString(R.string.lineas_) + " " + String.valueOf(row.s2));
                        break;
                    case "FLAPPY":
                        tv3b.setText(getString(R.string.saltos_) + " " + String.valueOf(row.s2));
                        break;
                    case "BREAKER":
                        tv3b.setText(getString(R.string.levels_) + " " + String.valueOf(row.s2));
                        break;

                    case "SPACE":
                        tv3b.setText(getString(R.string.oleadas) + " " + String.valueOf(row.s2));
                        break;
                    case "PONG":
                        String dificultad;
                        if(String.valueOf(row.s2).equals("0"))
                            dificultad="normal";
                        else
                            dificultad="imposible";
                        tv3b.setText(dificultad);
                        break;
                    case "PACMAN":
                        tv3b.setText(getString(R.string.levels_) + " " + String.valueOf(row.s2));
                        break;
                    case "PINBALL":
                        tv3b.setText(getString(R.string.levels_) + " " + String.valueOf(row.s2));
                        break;
                }

                layCustomer.addView(tv3b);
                element.setTableContent(tv3b);
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

            tv4.setPadding(20, 20, 20, 20);

            tv4.setGravity(Gravity.LEFT);

            if (i == -1) {
                tv4.setText(R.string.tiempo);
                tv4.setPadding(20, 20, 20, 20);
                tv4.setBackgroundColor(Color.parseColor("#f7f7f7"));
                tv4.setTextSize(TypedValue.COMPLEX_UNIT_PX, smallTextSize);
                tv4.setBackgroundColor(getResources().getColor(R.color.colorAzul));
                tv4.setTextColor(getResources().getColor(R.color.Blanco));
                tv4.setTextSize(TypedValue.COMPLEX_UNIT_PX, mediumTextSize);
                tv4.setOnClickListener(new View.OnClickListener() {//ORDENARLOS POR PUNTUACIÓN

                    @Override
                    public void onClick(View v) {

                        Partidas partidas = new Partidas();
                        guardarConsulta=Partidas.sql_historial;
                        Partidas.sql_historial = "SELECT * FROM PARTIDAS WHERE DIA BETWEEN " + m + " AND " + (m + milis_dia) + " AND CORREO = '" + user.getString("correo", no_registrado) + "'";
                        if(ultimaConsulta.contains(" ORDER BY TIEMPO DESC"))
                            Partidas.sql_historial+=" ORDER BY TIEMPO";
                        else
                            Partidas.sql_historial+=" ORDER BY TIEMPO DESC";
                        data = partidas.getPartidasHistorial();
                        loadData();
                        ultimaConsulta = Partidas.sql_historial;
                        Partidas.sql_historial=guardarConsulta;
                    }
                });
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



            tr.addView(tv);
            tr.addView(tv2);
            tr.addView(layCustomer);
            tr.addView(layAmounts);

            if (i > -1) {

                tr.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        TableRow tr = (TableRow) v;
                        //do whatever action is needed

                    }
                });


            }
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

            try {
                Thread.sleep(200);//es necesario para poder activar activar el ProgressBar, necesario para largas cargas
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            return "Task Completed.";
        }
        @Override
        protected void onPostExecute(String result) {
            progressDialog.hide();
            loadData();
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
        return String.format(java.util.Locale.getDefault(), formato, minutosReales, segundosReales, decimasReales);
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
