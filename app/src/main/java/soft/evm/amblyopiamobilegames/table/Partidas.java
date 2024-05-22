package soft.evm.amblyopiamobilegames.table;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import soft.evm.amblyopiamobilegames.MainActivity;
import soft.evm.amblyopiamobilegames.model.PartidaContract;

/**
 * Created by amit on 1/5/16.
 */
public class Partidas {

    public static String sql_historial = "SELECT * FROM " + PartidaContract.Partida.TABLE_NAME_HISTORIAL;
    public static String sql_ranking = "SELECT * FROM " + PartidaContract.Partida.TABLE_NAME_RANKING + ";";
    public static String tabla = "usuario";

    Cursor cursorBBDD;

    public PartidaData[] getPartidasHistorial() throws NullPointerException{

        //Abrimos BBDD en modo lectura
        SQLiteDatabase db = MainActivity.partidasDBHelper_historial.getReadableDatabase();

        cursorBBDD = db.rawQuery(sql_historial, null);

        PartidaData[] data = new PartidaData[cursorBBDD.getCount()];

        if (cursorBBDD.moveToFirst()) {

            //Recorremos el cursor hasta que no haya más registros
            int i = 0;
            do {
                PartidaData row = new PartidaData();
                row.id = cursorBBDD.getInt(0);
                row.correo = cursorBBDD.getString(1);
                row.nombre = cursorBBDD.getString(2);
                row.fecha = cursorBBDD.getLong(3);
                row.juego = cursorBBDD.getString(4);
                row.s1 = cursorBBDD.getInt(5);
                row.s2 = cursorBBDD.getInt(6);
                row.tiempo = cursorBBDD.getLong(7);
                data[i] = row;
                i++;
            } while(cursorBBDD.moveToNext());

        }

        return data;

    }

    public PartidaData[] getPartidasRanking() throws NullPointerException{

        //Abrimos BBDD en modo lectura
        SQLiteDatabase db = MainActivity.partidasDBHelper_ranking.getReadableDatabase();

        cursorBBDD = db.rawQuery(sql_ranking, null);

        PartidaData[] data = new PartidaData[cursorBBDD.getCount()];

        if (cursorBBDD.moveToFirst()) {

            //Recorremos el cursor hasta que no haya más registros
            int i = 0;
            do {
                PartidaData row = new PartidaData();
                row.id = cursorBBDD.getInt(0);
                row.correo = cursorBBDD.getString(1);
                row.nombre = cursorBBDD.getString(2);
                row.fecha = cursorBBDD.getLong(3);
                row.juego = cursorBBDD.getString(4);
                row.s1 = cursorBBDD.getInt(5);
                row.s2 = cursorBBDD.getInt(6);
                row.tiempo = cursorBBDD.getLong(7);
                data[i] = row;
                i++;
            } while(cursorBBDD.moveToNext());

        }

        return data;

    }

}
