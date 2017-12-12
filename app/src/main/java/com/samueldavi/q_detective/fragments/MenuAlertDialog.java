package com.samueldavi.q_detective.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

/**
 * Created by davicedraz on 11/12/2017.
 */

public class MenuAlertDialog extends DialogFragment{

    public interface DialogListener{
        public void onDialogDetalhesClick(int position);
        public void onDialogEnviarWebserviceClick(int position);
        public void onDialogEditarClick(int position);
        public void ondDialogRemoverClick(int position);
    }

    private DialogListener listener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        final CharSequence[] items = {"Mais detalhes", "Enviar para webservice", "Editar", "Remover"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Opções").setItems(items, itemClick);
        listener = (DialogListener) getActivity();
        return builder.create();
    }

    DialogInterface.OnClickListener itemClick = new DialogInterface.OnClickListener(){
        @Override
        public void onClick(DialogInterface dialog, int which) {
            int position = MenuAlertDialog.this.getArguments().getInt("position");

            switch (which){
                case 0:
                    listener.onDialogDetalhesClick(position);
                    break;
                case 1:
                    listener.onDialogEnviarWebserviceClick(position);
                    break;
                case 2:
                    listener.onDialogEditarClick(position);
                    break;
                case 3:
                    listener.ondDialogRemoverClick(position);
                    break;
            }
        }
    };


}
