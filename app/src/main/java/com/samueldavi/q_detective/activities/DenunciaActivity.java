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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.samueldavi.q_detective.adapter.DenunciaListViewAdapter;
import com.samueldavi.q_detective.R;
import com.samueldavi.q_detective.fragments.ConfirmAlertDialog;
import com.samueldavi.q_detective.fragments.MenuAlertDialog;
import com.samueldavi.q_detective.model.DAO.DenunciaDAO;
import com.samueldavi.q_detective.model.DAO.UsuarioDAO;
import com.samueldavi.q_detective.model.Denuncia;
import com.samueldavi.q_detective.model.Usuario;

import java.util.List;

public class DenunciaActivity extends AppCompatActivity implements MenuAlertDialog.DialogListener,
        AdapterView.OnItemClickListener, ConfirmAlertDialog.DialogConfirmListener {

    private ListView denunciasListview;
    private DenunciaDAO denunciasDatabase;
    private List<Denuncia> denuncias;
    private boolean isFirstTimeEver;
    private TextView noItensTextTop;
    private TextView noItensTextBottom;
    private ImageView noItensImage;
    private UsuarioDAO userDatabase;
    private Usuario user;

    private void setPreferences(Context context){
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            isFirstTimeEver = preferences.getBoolean(getString(R.string.is_first_time_ever), true);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_denuncias_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.request_list) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_denuncia);

        userDatabase = new UsuarioDAO(this);
        denunciasDatabase = new DenunciaDAO(this);

        findViews();
        setPreferences(this);
        getUserFromDatabase();
        getDenunciasFromDatabase();
        setupDenucias();

        DenunciaListViewAdapter adapter = new DenunciaListViewAdapter(denuncias, (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE));

        denunciasListview.setAdapter(adapter);

        //abre o menu_denuncias_activity de contexto ao clicar em uma denuncia
        denunciasListview.setOnItemClickListener(this);

        manageFloatingButton();
    }

    private void findViews() {
        noItensTextTop = findViewById(R.id.text_top_empty_denuncia_activity);
        noItensTextBottom = findViewById(R.id.text_bottom_empty_denuncia_activity);
        noItensImage = findViewById(R.id.img_empty_denuncia_activity);
        denunciasListview =  findViewById(R.id.listview_denuncias);
    }

    private void getUserFromDatabase() {
        List<Usuario> users = userDatabase.listar();
        int lastUser = users.size();
        user = users.get(lastUser);
    }

    private void setupDenucias() {

        if (denuncias.isEmpty()){
            if(isFirstTimeEver){
                noItensTextTop.setVisibility(View.GONE);
                noItensTextBottom.setBackground(null);
                noItensTextBottom.setText(getString(R.string.no_item_first_time));
                noItensImage.setImageResource(R.drawable.ic_warning);

            }else{
                noItensImage.setImageResource(R.drawable.ic_detective);
                noItensTextBottom.setText(R.string.no_item_bottom_text);
                noItensTextBottom.setBackground(getResources().getDrawable(R.drawable.no_item_text_bottom_background));//denunciasListview.setEmptyView(findViewById(R.id.txt_first_time_empty_listview));
            }
        }else {
            noItensTextTop.setVisibility(View.GONE);
            noItensTextBottom.setVisibility(View.GONE);
            noItensImage.setVisibility(View.GONE);
        }
    }

    private void manageFloatingButton(){
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.floating_btn_add_denuncia);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DenunciaActivity.this, DenunciaCadastroActivity.class);
                intent.putExtra(getString(R.string.KEY_USER_EXTRA), user.getNome());
                startActivityForResult(intent, getResources().getInteger(R.integer.ACTIVITY_CADASTRO));
            }
        });
    }

    private void getDenunciasFromDatabase(){
        denuncias = denunciasDatabase.listar();
    }










    //DIALOG SHIT


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
    public void onDialogEnviarWebserviceClick(int position) {

    }

    @Override
    public void onDialogEditarClick(int position) {
        Intent intent = new Intent(DenunciaActivity.this, DenunciaCadastroActivity.class);
        intent.putExtra(getString(R.string.KEY_USER_EXTRA), user.getNome());
        intent.putExtra(getString(R.string.KEY_EDIT), denuncias.get(position));
        startActivityForResult(intent, getResources().getInteger(R.integer.ACTIVITY_CADASTRO_EDIT));
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
