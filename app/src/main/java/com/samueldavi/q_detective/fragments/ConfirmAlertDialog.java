package com.samueldavi.q_detective.fragments;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;

/**
 * Created by davic on 12/12/2017.
 */

public class ConfirmAlertDialog extends DialogFragment {

    private DialogConfirmListener listener;

    public interface DialogConfirmListener{
        public void onDialogSimClick(DialogFragment dialog);
        public void onDialogCancelarClick(DialogFragment dialog);
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Deseja mesmo remover essa denúncia?");
        builder.setPositiveButton("Sim", confirmaSim);
        builder.setNegativeButton("Cancelar", confirmaCancelar);
        listener = (DialogConfirmListener) getActivity();

        return builder.create();
    }

    DialogInterface.OnClickListener confirmaSim = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            listener.onDialogSimClick(ConfirmAlertDialog.this);
        }
    };

    DialogInterface.OnClickListener confirmaCancelar = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            listener.onDialogCancelarClick(ConfirmAlertDialog.this);
        }
    };


}
