package soft.evm.amblyopiamobilegames.sesion;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteStatement;

import java.text.SimpleDateFormat;
import java.util.concurrent.TimeUnit;

import soft.evm.amblyopiamobilegames.ActividadActivity;

import static soft.evm.amblyopiamobilegames.MainActivity.partidasDBHelper_historial;
import static soft.evm.amblyopiamobilegames.model.PartidaContract.Partida.TABLE_NAME_HISTORIAL;

public class Estadisticas {


    public static void update(SharedPreferences estad, SharedPreferences user) throws SQLiteException {

        //Abrimos BBDD en modo lectura
        SQLiteDatabase db = partidasDBHelper_historial.getReadableDatabase();

        long count = 0;
        int total = 0;
        int dias = 1;
        if(db.isOpen()) {

            //1.-obtener nº de partidas jugadas
            String sql = "SELECT COUNT(*) FROM " + TABLE_NAME_HISTORIAL + " WHERE CORREO = '" + user.getString("correo", "") + "'";
            SQLiteStatement s = db.compileStatement(sql);
            count = s.simpleQueryForLong();

            //2.-obtener tiempo total de juego
            sql = "SELECT SUM(TIEMPO) AS TOTAL FROM " + TABLE_NAME_HISTORIAL + " WHERE CORREO = '" + user.getString("correo", "") + "'";
            Cursor cursor = db.rawQuery(sql, null);

            if (cursor.moveToFirst())
                total = cursor.getInt(cursor.getColumnIndex("TOTAL"));
            cursor.close();

            //3.-obtener tiempo medio de juego
            sql = "SELECT DIA FROM " + TABLE_NAME_HISTORIAL + " WHERE CORREO = '" + user.getString("correo", "") + "'";
            cursor = db.rawQuery(sql, null);

            if (cursor.moveToFirst()) {
                //Recorremos el cursor hasta que no haya más registros
                int i = 0;
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault());
                String diaAnterior = "";
                do {
                    long fecha = cursor.getLong(0);
                    String dia = dateFormat.format(fecha);
                    if (!dia.equals(diaAnterior)) {
                        i++;
                        diaAnterior = dia;
                    }
                } while (cursor.moveToNext());
                dias = i;
            }
            cursor.close();
            db.close();
        }

        long medio = total/dias;

        //actualizar estadisticas
        String totalTime = formatearMilis(total, 1);
        String medioTime = formatearMilis(medio, 2);

        SharedPreferences.Editor editor = estad.edit();
        editor.putString("total", totalTime);
        editor.putString("medio", medioTime);
        editor.putLong("jugadas", count);
        editor.apply();

        ActividadActivity.pintar();
    }

    public static String formatearMilis(long milis, int i) {
        String formato;
        if(i==1)
            formato = "%03d:%02d:%02d";
        else
            formato = "%02d:%02d:%02d";
        long horasReales = TimeUnit.MILLISECONDS.toHours(milis);
        long minutosReales = TimeUnit.MILLISECONDS.toMinutes(milis) - horasReales * 60;
        long segundosReales = TimeUnit.MILLISECONDS.toSeconds(milis) - minutosReales * 60 - horasReales * 3600;
        return String.format(java.util.Locale.getDefault(), formato, horasReales, minutosReales, segundosReales);
    }
}
