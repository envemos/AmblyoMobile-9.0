package soft.evm.amblyopiamobilegames.juegos;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import soft.evm.amblyopiamobilegames.R;

public class Pausa {

    public static Context context;

    public static void pausa() {

        GameActivity.pause = true;
        new AlertDialog.Builder(context)
                //set icon
                .setIcon(android.R.drawable.ic_dialog_info)
                //set title
                .setTitle(R.string.menu_pausa)
                //set message
                .setMessage(R.string.quieres_salir)
                //set positive button
                .setPositiveButton(R.string.reanudar, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        GameActivity.pause = false;

                        //breakoutView.resume();
                    }
                })
                //set negative button
                .setNegativeButton(R.string.salir, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        seguro();
                    }
                })
                .show();
    }

    public static void seguro() {
        new AlertDialog.Builder(context)
                //set icon
                .setIcon(android.R.drawable.ic_dialog_alert)
                //set title
                .setTitle(R.string.seguro)
                //set message
                .setMessage(R.string.no_se_guarda)
                //set positive button
                .setPositiveButton(R.string.si, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Insertar.correo="NO";
                        GameActivity.gameOver = true;
                    }
                })
                //set negative button
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        pausa();
                    }
                })
                .show();
    }

}
