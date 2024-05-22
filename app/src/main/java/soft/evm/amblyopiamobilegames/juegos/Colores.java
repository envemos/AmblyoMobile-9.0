package soft.evm.amblyopiamobilegames.juegos;

import android.content.SharedPreferences;
import android.graphics.Color;

public class Colores {

    private static int ojo_sano = 0;
    private static int ojo_vago = 0;
    private static int fondo = 0;
    private static int customR = 0;
    private static int customL = 0;

    public static int getOjo_sano() {
        return ojo_sano;
    }

    public static void setOjo_sano(int ojo_sano) {
        Colores.ojo_sano = ojo_sano;
    }

    public static int getOjo_vago() {
        return ojo_vago;
    }

    public static void setOjo_vago(int ojo_vago) {
        Colores.ojo_vago = ojo_vago;
    }

    public static void setCustomR(int customR) {
        Colores.customR = customR;
    }

    public static void setCustomL(int customL) {
        Colores.customL = customL;
    }

    public static int getFondo() {
        return fondo;
    }

    public static void setFondo(int fondo) {
        Colores.fondo = fondo;
    }

    public static int toColor(int n) {
        int color = 0;
        //if fondo == balno||negro
        if(fondo == Color.BLACK) {
            switch (n) {
                case 0:
                    color = Color.BLUE;//ROJO -> BLUE
                    break;
                case 1:
                    color = Color.RED;//AZUL -> RED
                    break;
                case 2:
                    color = Color.BLUE;//AMARILLO -> AZUL
                    break;
                case 3:
                    color = Color.RED;//CIAN -> ROJO
                    break;
                case 4:
                    color = Color.GREEN;//MAGENTA -> VERDE
                    break;
                case 5:
                    color = Color.RED;//VERDE -> ROJO
                    break;
                case 6:
                    color = customR;
                    break;
                case 7:
                    color = customL;
                    break;
            }
        }
        else{//color blanco
            switch (n) {
                case 0:
                    color = Color.YELLOW;//ROJO -> AMARILLO
                    break;
                case 1:
                    color = Color.CYAN;//AZUL -> CYAN
                    break;
                case 2:
                    color = Color.YELLOW;//AMARILLO -> AMARILLO
                    break;
                case 3:
                    color = Color.CYAN;//CYAN -> CYAN
                    break;
                case 4:
                    color = Color.MAGENTA;//MAGENTA -> MAGENTA
                    break;
                case 5:
                    color = Color.CYAN;//VERDE -> CYAN
                    break;
                case 6:
                    color = customR;
                    break;
                case 7:
                    color = customL;
                    break;
            }
        }
        return color;
    }

    public static void ajustarColores (SharedPreferences prefs){
        int izquierdo = prefs.getInt("ojo_izquierdo", 0);
        if(izquierdo == 6)
            izquierdo = 7;
        int derecho = prefs.getInt("ojo_derecho", 1);
        int lado = prefs.getInt("ojo_vago", 1);
        Colores.customL = prefs.getInt("customColorL", 1);
        Colores.customR = prefs.getInt("customColorR", 1);
        if(lado == 1) {
            Colores.setOjo_vago(toColor(izquierdo));
            Colores.setOjo_sano(toColor(derecho));
        }
        else {
            Colores.setOjo_vago(toColor(derecho));
            Colores.setOjo_sano(toColor(izquierdo));
        }
        if(prefs.getInt("fondo", 1) == 0)
            Colores.setFondo(Color.WHITE);
        else
            Colores.setFondo(Color.BLACK);
    }

}
