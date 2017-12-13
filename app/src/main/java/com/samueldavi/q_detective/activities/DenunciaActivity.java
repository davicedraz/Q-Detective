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
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
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
import com.samueldavi.q_detective.resources.DatabaseHelper;
import com.samueldavi.q_detective.resources.WebServiceUtils;

import java.io.File;
import java.util.List;
import java.util.Map;

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

    private List<Map<String, Object>> mapList;
    private boolean permisaoInternet = false;
    private final String url = "http://35.226.50.35/QDetective/";
    private ProgressDialog load;

    private void setPreferences(Context context){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        isFirstTimeEver = preferences.getBoolean(getString(R.string.is_first_time_ever), true);
    }
    private boolean getPreferences(){
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        return sharedPref.getBoolean(getString(R.string.is_first_time_ever), false);
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
        switch (id){
            case R.id.request_list:
                Log.d("ItemSelected", "request_list");
                break;
            case R.id.user_settings:
                Intent intent = new Intent(this, EditUserActivity.class);
                startActivity(intent);
                Log.d("ItemSelected", "user_settings");
                break;
            default:
                return super.onOptionsItemSelected(item);
        }

        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_denuncia);
        isFirstTimeEver = getPreferences();

        userDatabase = new UsuarioDAO(this);
        denunciasDatabase = new DenunciaDAO(this);

        findViews();
        getUserFromDatabase();
        getDenunciasFromDatabase();
        setupDenucias();

        DenunciaListViewAdapter adapter = new DenunciaListViewAdapter(denuncias, this);

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
        int lastUser = users.size()-1;
        user = users.get(lastUser);
    }

    private void setupDenucias() {
        if (denuncias.isEmpty()){
            if(isFirstTimeEver){
                noItensTextTop.setVisibility(View.GONE);
                noItensTextBottom.setVisibility(View.VISIBLE);
                noItensImage.setVisibility(View.VISIBLE);

                noItensTextBottom.setBackground(null);
                noItensTextBottom.setText(getString(R.string.no_item_first_time));
                noItensImage.setImageResource(R.drawable.ic_warning);
                noItensTextBottom.setTextColor(getResources().getColor(R.color.colorText_noItems));

            }else{
                noItensTextTop.setVisibility(View.VISIBLE);
                noItensTextBottom.setVisibility(View.VISIBLE);
                noItensImage.setVisibility(View.VISIBLE);
                noItensImage.setImageResource(R.drawable.ic_detective);
                noItensTextBottom.setText(R.string.no_item_bottom_text);
                noItensTextBottom.setBackground(getResources().getDrawable(R.drawable.no_item_text_bottom_background));
                noItensTextBottom.setTextColor(getResources().getColor(R.color.colorText_noItems));
                noItensTextBottom.setTextColor(getResources().getColor(R.color.colorText_Bottom_empty_text));//denunciasListview.setEmptyView(findViewById(R.id.txt_first_time_empty_listview));
            }
        }else {
            isFirstTimeEver = false;

            noItensTextTop.setVisibility(View.GONE);
            noItensTextBottom.setVisibility(View.GONE);
            noItensImage.setVisibility(View.GONE);
        }
        setPreferences(this);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(" ", "entrou no metodo");
        if(requestCode == getResources().getInteger(R.integer.ACTIVITY_CADASTRO) && requestCode == RESULT_OK){
            getDenunciasFromDatabase();
            ((BaseAdapter)denunciasListview.getAdapter()).notifyDataSetChanged();

        }
    }

    @Override
    protected void onResume() {
        updateListview();
        Log.d("onResume", "entrou no metodo");
        super.onResume();
    }

    private void updateListview() {
        getUserFromDatabase();
        getDenunciasFromDatabase();
        ((DenunciaListViewAdapter) denunciasListview.getAdapter()).updateDenunciasList(denuncias);
        //((BaseAdapter) denunciasListview.getAdapter()).notifyDataSetChanged();
        setupDenucias();
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

            denunciasDatabase.removerDenuncia(denuncia.getId());
            updateListview();
        }
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

        updateListview();
    }

    @Override
    public void onDialogCancelarClick(DialogFragment dialog) {
        //do nothing
    }


    // WEBSERVICE SHIT

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

    private class DownloadDenuncias extends AsyncTask<Long, Void, WebServiceUtils> {
        @Override
        protected void onPreExecute() {
            load = ProgressDialog.show(DenunciaActivity.this, "Por favor Aguarde ...", "Recuperando Informações do Servidor...");
        }

        @Override
        protected WebServiceUtils doInBackground(Long... ids) {
            WebServiceUtils webService = new WebServiceUtils();
            String id = (ids != null && ids.length == 1) ? ids[0].toString() : "";
            List<Denuncia> denuncias = webService.getListaDenunciasJson(url, "denuncias", id);
            for (Denuncia denuncia : denuncias) {
                String path = getDiretorioDeSalvamento(denuncia.getUriMidia()).getPath();
                webService.downloadImagemBase64(url + "arquivos", path, denuncia.getId());
                denuncia.setUriMidia(path);
            }
            return webService;
        }

        @Override
        protected void onPostExecute(WebServiceUtils webService) {
            for (Denuncia contato : webService.getDenuncias()) {
                Denuncia denuncia = denunciasDatabase.buscarDenunciaPorID(contato.getId());
                if (denuncia != null) {
                    denunciasDatabase.atualizarDenuncia(denuncia);
                } else {
                    denunciasDatabase.salvarDenuncia(denuncia);
                }
            }
            load.dismiss();
            Toast.makeText(getApplicationContext(), webService.getRespostaServidor(), Toast.LENGTH_LONG).show();
        }
    }

    private File getDiretorioDeSalvamento(String nomeArquivo) {
        if (nomeArquivo.contains("/")) {
            int beginIndex = nomeArquivo.lastIndexOf("/") + 1;
            nomeArquivo = nomeArquivo.substring(beginIndex);
        }
        File diretorio = this.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File pathDaImagem = new File(diretorio, nomeArquivo);
        return pathDaImagem;
    }

    public void iniciarDownload(View view) {
        getPermissaoDaInternet();
        if (permisaoInternet) {
            DownloadDenuncias downloadDenuncias = new DownloadDenuncias();
            downloadDenuncias.execute();
        }
    }


}
