package soft.evm.amblyopiamobilegames.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import soft.evm.amblyopiamobilegames.MainActivity;
import soft.evm.amblyopiamobilegames.R;


public class RateDialogFragment2 extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(MainActivity.mensaje)
                .setPositiveButton(R.string.si__, new DialogInterface.OnClickListener() {


                    public void onClick(DialogInterface dialog, int id) {


                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            Uri uri = null;
                            uri = Uri.parse("market://details?id=" + getContext().getPackageName());
                            Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                            // To count with Play market backstack, After pressing back button,
                            // to taken back to our application, we need to add following flags to intent.
                            goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                                    Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                                    Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                            try {
                                startActivity(goToMarket);
                            } catch (ActivityNotFoundException e) {
                                startActivity(new Intent(Intent.ACTION_VIEW,
                                        Uri.parse("http://play.google.com/store/apps/details?id=" + getContext().getPackageName())));
                            }
                        }
                        else{
                            Uri uri = Uri.parse("https://play.google.com/store/apps/details?id=soft.evm.amblyopiamobilegames");
                            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                            startActivity(intent);
                        }
                        MainActivity.isRate = true;
                    }
                })
                .setNegativeButton(R.string.no__, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {


                        MainActivity.veces = 50;
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }
}
