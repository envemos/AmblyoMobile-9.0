package soft.evm.amblyopiamobilegames;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.EventDay;
import com.applandeo.materialcalendarview.listeners.OnDayClickListener;
import com.applandeo.materialcalendarview.listeners.OnSelectDateListener;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import soft.evm.amblyopiamobilegames.ads.MiAdMob;
import soft.evm.amblyopiamobilegames.ads.YodoAds;
import soft.evm.amblyopiamobilegames.juegos.flappy.game.FlappyGame;
import soft.evm.amblyopiamobilegames.juegos.tetris.TetrisConfig;
import soft.evm.amblyopiamobilegames.layoutElements.Element;
import soft.evm.amblyopiamobilegames.model.PartidaContract;
import soft.evm.amblyopiamobilegames.sesion.Estadisticas;
import soft.evm.amblyopiamobilegames.table.Partidas;
import soft.evm.amblyopiamobilegames.table.TableViewHistorial;

import static soft.evm.amblyopiamobilegames.MainActivity.no_registrado;

public class ActividadActivity extends AppCompatActivity implements OnSelectDateListener {

    static TextView TVmiActividad, TVtotal, TVmedio, TVjugadas, TVtotalTiempo, TVmedioTiempo, TVjugadasTiempo;
    //Button BThistorial;
    static SharedPreferences user, estad;

    public final static int milis_dia = 86400000;

    public static long m = 0;

    CalendarView calendarView;

    List<Calendar> selectedDays;

    public static void pintar() {
        //pintar
        TVtotalTiempo.setText(estad.getString("total","00:00:00"));
        TVmedioTiempo.setText(estad.getString("medio","00:00:00"));
        TVjugadasTiempo.setText(String.valueOf(estad.getLong("jugadas",0)));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // remove title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_actividad);
        inicializarSharedPreferences();
        inicializarComponentes();
        inicializarEstadisticas();
        try {
            inicializarCalendario();
        }catch (SQLiteException e ){
            e.printStackTrace();
            //58 error
        } catch (RuntimeException e){
            e.printStackTrace();
            //58 error
        }

        //Admob
        MiAdMob.mostrarAds(this.getWindow().getDecorView().findViewById(android.R.id.content));
    }

    private void inicializarSharedPreferences() {
        estad = getSharedPreferences("Estadisticas", Context.MODE_PRIVATE);
        user = getSharedPreferences("Usuario", Context.MODE_PRIVATE);
    }

    private void inicializarComponentes() {
        Element element = new Element(getApplicationContext());
        TVmiActividad = findViewById(R.id.textViewActivityActividadActividad);
        element.setActivityTitle(TVmiActividad);
        TVtotal = findViewById(R.id.textViewActivityActividadTotal);
        element.setActivityText(TVtotal);
        TVmedio = findViewById(R.id.textViewActivityActividadMedio);
        element.setActivityText(TVmedio);
        TVjugadas = findViewById(R.id.textViewActivityActividadJugadas);
        element.setActivityText(TVjugadas);
        TVtotalTiempo = findViewById(R.id.textViewActivityActividadTiempoTotal);
        element.setActivityTextContent(TVtotalTiempo);
        TVmedioTiempo = findViewById(R.id.textViewActivityActividadTiempoMedio);
        element.setActivityTextContent(TVmedioTiempo);
        TVjugadasTiempo = findViewById(R.id.textViewActivityActividadPartidasJugadas);
        element.setActivityTextContent(TVjugadasTiempo);
    }


    private void inicializarEstadisticas() {
        try {
            Estadisticas.update(estad, user);
        } catch (SQLiteException e) {
            e.printStackTrace();
            //Bloqueo 49-> 50
        }
    }

    private void inicializarCalendario() {
        SQLiteDatabase db = MainActivity.partidasDBHelper_historial.getReadableDatabase();
        String correo = user.getString("correo", no_registrado);
        Cursor cursor = db.rawQuery("SELECT DIA FROM " + PartidaContract.Partida.TABLE_NAME_HISTORIAL + " WHERE CORREO=? ORDER BY DIA", new String[] {correo});
        selectedDays = new ArrayList<>();
        if(cursor.moveToFirst()){
            do{
                long f = cursor.getLong(0);
                Calendar c = new GregorianCalendar();
                c.setTimeInMillis(f);
                int ano = c.get(Calendar.YEAR);
                int mes = c.get(Calendar.MONTH) + 1;
                int dia = c.get(Calendar.DAY_OF_MONTH);
                Calendar calendar = Calendar.getInstance();
                calendar.set(ano, mes-1, dia);
                selectedDays.add(calendar);
            }while(cursor.moveToNext());
        }
        cursor.close();
        db.close();

        calendarView = findViewById(R.id.calendarView);
        calendarView.setSelectedDates(selectedDays);

        calendarView.setOnDayClickListener(new OnDayClickListener() {
            @Override
            public void onDayClick(EventDay eventDay) {
                Calendar clickedDayCalendar = eventDay.getCalendar();
                if(calendarView.getSelectedDates().contains(clickedDayCalendar)) {
                    m = clickedDayCalendar.getTimeInMillis();
                    Partidas.sql_historial = "SELECT * FROM " + PartidaContract.Partida.TABLE_NAME_HISTORIAL + " WHERE DIA BETWEEN " + m + " AND " + (m + milis_dia) + " AND CORREO = '" + user.getString("correo", no_registrado) + "' ORDER BY DIA DESC";

                    //Partidas.sql = "SELECT * FROM PARTIDAS ORDER BY DIA DESC";

                    iniciarHistorial();
                }
            }
        });

    }

    public void iniciarHistorial() {
        user = getSharedPreferences("Usuario", Context.MODE_PRIVATE);
        String correo = user.getString("correo", no_registrado);
        if(!correo.equals(no_registrado)){
            Partidas.tabla = "usuario";
            Intent i = new Intent(this, TableViewHistorial.class);
            startActivity(i);
        }
    }

    public void go_back(View view) {
        finish();
    }


    @Override
    public void onSelect(List<Calendar> calendar) {
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        //calendarView.setSelectedDates(selectedDays);
    }

    @Override
    public void finish() {
        super.finish();
        //MiAdMob.showInterstitialAd();
    }

    @Override
    protected void onStart() {
        super.onStart();
        YodoAds.showBannerAd(ActividadActivity.this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        YodoAds.showInterstitialAd(ActividadActivity.this);
        YodoAds.mostrarAds(this.getWindow().getDecorView().findViewById(android.R.id.content));
    }
}
