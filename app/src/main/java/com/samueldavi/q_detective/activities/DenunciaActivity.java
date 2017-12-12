package com.samueldavi.q_detective.activities;

import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.samueldavi.q_detective.DenunciaListViewAdapter;
import com.samueldavi.q_detective.R;
import com.samueldavi.q_detective.fragments.ConfirmAlertDialog;
import com.samueldavi.q_detective.fragments.MenuAlertDialog;
import com.samueldavi.q_detective.model.DAO.DenunciaDAO;
import com.samueldavi.q_detective.model.Denuncia;

import java.util.List;

public class DenunciaActivity extends AppCompatActivity implements MenuAlertDialog.DialogListener,
        AdapterView.OnItemClickListener, ConfirmAlertDialog.DialogConfirmListener {

    private ListView denunciasListview;
    private DenunciaDAO denunciasDatabase;
    private List<Denuncia> denuncias;
    private boolean isFirstTimeEver;

    private void setPreferences(Context context){
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            isFirstTimeEver = preferences.getBoolean(getString(R.string.is_first_time_ever), true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_denuncia);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setPreferences(this);

        denunciasDatabase = new DenunciaDAO(this);
        setupDenucias();
        getDenunciasFromDatabase();

        denunciasListview = (ListView) findViewById(R.id.listview_denuncias);
        DenunciaListViewAdapter adapter = new DenunciaListViewAdapter(denuncias, (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE));

        denunciasListview.setAdapter(adapter);

        //abre o menu de contexto ao clicar em uma denuncia
        denunciasListview.setOnItemClickListener(this);

        manageFloatingButton();
    }

    private void setupDenucias() {
        getDenunciasFromDatabase();

        if (denuncias.isEmpty()){
            if(isFirstTimeEver){
                //denunciasListview.setEmptyView(findViewById(R.id.txt_first_time_empty_listview));
            }else{
                //denunciasListview.setEmptyView(findViewById(R.id.txt_first_time_empty_listview));
            }
        }
    }

    private void manageFloatingButton(){
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.floating_btn_add_denuncia);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Click action
            }
        });
    }

    private void getDenunciasFromDatabase(){
        denuncias = denunciasDatabase.listar();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        MenuAlertDialog fragmentDialog = new MenuAlertDialog();

        Bundle bundle = new Bundle();
        bundle.putInt("position", position);
        fragmentDialog.setArguments(bundle);

        fragmentDialog.show(this.getFragmentManager(), "menu");
    }


    @Override
    public void onDialogDetalhesClick(int position) {
        Intent intent = new Intent(this, DetalhesActivity.class);
        intent.putExtra("position", position);
        startActivity(intent);
    }

    @Override
    public void onDialogEditarClick(int position) {

    }


    @Override
    public void ondDialogRemoverClick(int position) {
        DialogFragment confirmDialogFragment = new ConfirmAlertDialog();

        Bundle bundle = new Bundle();
        bundle.putInt("position", position);
        confirmDialogFragment.setArguments(bundle);

        confirmDialogFragment.show(this.getFragmentManager(), "confirma");
    }

    @Override
    public void onDialogSimClick(DialogFragment dialog) {
        int position = dialog.getArguments().getInt("position");
        denunciasDatabase.removerDenuncia(denunciasDatabase.listar().get(position).getId());


        //não sei fazer aquele negocio lá pra voltar pra activity depois de excluir kkkkk
        finish();
        startActivity(this.getIntent());
    }

    @Override
    public void onDialogCancelarClick(DialogFragment dialog) {
        //do nothing
    }


}
