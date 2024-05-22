package soft.evm.amblyopiamobilegames.model;

import android.provider.BaseColumns;

public class PartidaContract {
    private PartidaContract(){}

    public static class Partida implements BaseColumns {

        public static final String TABLE_NAME_HISTORIAL = "HISTORIAL";
        public static final String TABLE_NAME_RANKING = "RANKING";

        public static final String COLUMN_NAME_CORREO ="CORREO";
        public static final String COLUMN_NAME_NOMBRE ="USUARIO";
        public static final String COLUMN_NAME_DIA = "DIA";
        public static final String COLUMN_NAME_JUEGO = "JUEGO";
        public static final String COLUMN_NAME_S1 = "S1";
        public static final String COLUMN_NAME_S2 = "S2";
        public static final String COLUMN_NAME_TIEMPO = "TIEMPO";
        public static final String COLUMN_NAME_DEVICE = "DISPOSITIVO";
        public static final String COLUMN_NAME_SUBIDO = "SUBIDO";


    }
}
