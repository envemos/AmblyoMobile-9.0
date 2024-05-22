package soft.evm.amblyopiamobilegames.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import soft.evm.amblyopiamobilegames.MainActivity;
import soft.evm.amblyopiamobilegames.R;


public class RateDialogFragment extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.enjoy)
                .setPositiveButton(R.string.si_, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {


                        MainActivity.mensaje = R.string.howabout;
                        DialogFragment r = new RateDialogFragment2();
                        r.show(getFragmentManager(), "dialog");
                    }
                })
                .setNegativeButton(R.string.no_, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {


                        MainActivity.mensaje = R.string.givingus;
                        DialogFragment r = new RateDialogFragment2();
                        r.show(getFragmentManager(), "dialog");
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }
}
