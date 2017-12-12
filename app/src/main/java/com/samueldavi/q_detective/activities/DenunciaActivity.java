package com.samueldavi.q_detective.activities;

import android.Manifest;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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
import android.widget.Toast;

import com.samueldavi.q_detective.adapter.DenunciaListViewAdapter;
import com.samueldavi.q_detective.R;
import com.samueldavi.q_detective.fragments.ConfirmAlertDialog;
import com.samueldavi.q_detective.fragments.MenuAlertDialog;
import com.samueldavi.q_detective.model.DAO.DenunciaDAO;
import com.samueldavi.q_detective.model.DAO.UsuarioDAO;
import com.samueldavi.q_detective.model.Denuncia;
import com.samueldavi.q_detective.model.Usuario;
import com.samueldavi.q_detective.resources.WebServiceUtils;

import java.io.File;
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

    private boolean permisaoInternet = false;
    private final String url = " http://35.226.50.35/QDetective/";
    private ProgressDialog load;

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

        setPreferences(this);
        getUserFromDatabase();
        getDenunciasFromDatabase();
        setupDenucias();

        denunciasListview = (ListView) findViewById(R.id.listview_denuncias);
        DenunciaListViewAdapter adapter = new DenunciaListViewAdapter(denuncias, (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE));

        denunciasListview.setAdapter(adapter);

        //abre o menu_denuncias_activity de contexto ao clicar em uma denuncia
        denunciasListview.setOnItemClickListener(this);

        manageFloatingButton();
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
                startActivityForResult(intent, 1);
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
        getPermissaoDaInternet();

        if(permisaoInternet){
            Denuncia denuncia = denuncias.get(position);
            UploadDenuncia upload = new UploadDenuncia();
            upload.execute(denuncia);
        }
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


    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    private void getPermissaoDaInternet() {
        boolean internet = ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED;
        boolean redeStatus = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NETWORK_STATE) == PackageManager.PERMISSION_GRANTED;
        if (internet && redeStatus) {
            if (isOnline()) {
                permisaoInternet = true;
                return;
            } else {
                permisaoInternet = false;
                Toast.makeText(this, "Sem conexão de Internet.", Toast.LENGTH_LONG).show();
            }
        } else {
            ActivityCompat.requestPermissions(this, new String[]{
                            Manifest.permission.INTERNET,
                            Manifest.permission.ACCESS_NETWORK_STATE},
                    1);
        }
    }

    class UploadDenuncia extends AsyncTask<Denuncia, Void, WebServiceUtils> {

        @Override
        protected void onPreExecute() {
            load = ProgressDialog.show(DenunciaActivity.this, "Por favor Aguarde ...", "Recuperando Informações do Servidor...");
        }

        @Override
        protected WebServiceUtils doInBackground(Denuncia... denuncias) {
            WebServiceUtils webService = new WebServiceUtils();
            Denuncia denuncia = denuncias[0];
            String urlDados = url + "denuncias";
            if (webService.sendDenunciaJson(urlDados, denuncia)) {
                urlDados = url + "arquivos/postFotoBase64";
                webService.uploadImagemBase64(urlDados, new File(denuncia.getUriMidia()));
            }
            return webService;
        }

        @Override
        protected void onPostExecute(WebServiceUtils webService) {
            Toast.makeText(getApplicationContext(),
                    webService.getRespostaServidor(),
                    Toast.LENGTH_LONG).show();
            load.dismiss();
        }
    }

}
