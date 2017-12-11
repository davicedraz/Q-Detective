package com.samueldavi.q_detective.activities;

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
import android.widget.ListView;

import com.samueldavi.q_detective.DenunciaListViewAdapter;
import com.samueldavi.q_detective.R;
import com.samueldavi.q_detective.model.DAO.DenunciaDAO;
import com.samueldavi.q_detective.model.Denuncia;

import java.util.List;

public class DenunciaActivity extends AppCompatActivity {

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
}
