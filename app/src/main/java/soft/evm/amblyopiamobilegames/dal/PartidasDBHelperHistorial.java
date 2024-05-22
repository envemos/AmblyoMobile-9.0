package soft.evm.amblyopiamobilegames.dal;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import soft.evm.amblyopiamobilegames.model.PartidaContract;

import static soft.evm.amblyopiamobilegames.model.PartidaContract.Partida.TABLE_NAME_HISTORIAL;


public class PartidasDBHelperHistorial extends SQLiteOpenHelper {

    private static final String TEXT_TYPE = " TEXT";
    private static final String INTEGER_TYPE = " INTEGER";
    private static final String COMMA_SEPARATOR = ", ";
    private static final String DEFAULT_0 = " DEFAULT 0";


    //Instrucciones básicas de nuestra TABLA de BBDD
    private static final   String SQL_CREATE_PARTIDAS =
            "CREATE TABLE " + TABLE_NAME_HISTORIAL + " (" +
                    PartidaContract.Partida._ID + " INTEGER PRIMARY KEY, " +
                    PartidaContract.Partida.COLUMN_NAME_CORREO + TEXT_TYPE + COMMA_SEPARATOR +
                    PartidaContract.Partida.COLUMN_NAME_NOMBRE + TEXT_TYPE + COMMA_SEPARATOR +
                    PartidaContract.Partida.COLUMN_NAME_DIA + INTEGER_TYPE + COMMA_SEPARATOR +
                    PartidaContract.Partida.COLUMN_NAME_JUEGO + TEXT_TYPE + COMMA_SEPARATOR +
                    PartidaContract.Partida.COLUMN_NAME_S1 + INTEGER_TYPE + COMMA_SEPARATOR +
                    PartidaContract.Partida.COLUMN_NAME_S2 + INTEGER_TYPE + COMMA_SEPARATOR +
                    PartidaContract.Partida.COLUMN_NAME_TIEMPO + INTEGER_TYPE + COMMA_SEPARATOR +
                    PartidaContract.Partida.COLUMN_NAME_DEVICE + TEXT_TYPE + COMMA_SEPARATOR +
                    PartidaContract.Partida.COLUMN_NAME_SUBIDO + INTEGER_TYPE + DEFAULT_0 + ")";

    private static final   String SQL_DELETE_PARTIDAS =
            "DROP TABLE IF EXISTS " + TABLE_NAME_HISTORIAL;


    //Propiedades de la BBDD
    public static final String DATABASE_NAME = "Historial.db";
    public static final int DATABASE_VERSION = 1;

    public PartidasDBHelperHistorial(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(SQL_CREATE_PARTIDAS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL(SQL_DELETE_PARTIDAS);
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion){
        onUpgrade(db, oldVersion, newVersion);
    }

    public void eliminarTablaPartidas(SQLiteDatabase db){

        db.execSQL(SQL_DELETE_PARTIDAS);
    }

}
