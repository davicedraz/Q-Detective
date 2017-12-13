package com.samueldavi.q_detective.activities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import com.samueldavi.q_detective.R;
import com.samueldavi.q_detective.model.DAO.DenunciaDAO;
import com.samueldavi.q_detective.model.Denuncia;

import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class DetalhesActivity extends AppCompatActivity {

    private ImageView iconeCategoria;
    private TextView textViewCategoria;
    private TextView textViewData;
    private TextView textViewUsuarioDenuncia;
    private TextView textViewDescricao;

    private ImageView imageViewMidia;
    private VideoView videoViewMidia;
    private ImageButton btn_playMidia;

    private WebView webViewLocalizacao;

    private Denuncia denuncia;
    private DenunciaDAO denunciaDAO;

    private SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy");

    private String urlBase = "http://maps.googleapis.com/maps/api/staticmap?size=400x400&sensor=true&markers=color:red|%s,%s";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhes);

        iconeCategoria = (ImageView) findViewById(R.id.iconeCategoria);
        textViewCategoria = (TextView) findViewById(R.id.textViewCategoria);
        textViewData = (TextView) findViewById(R.id.textViewData);
        textViewUsuarioDenuncia = (TextView) findViewById(R.id.textViewUsuarioDenuncia);
        textViewDescricao = (TextView) findViewById(R.id.textViewDescricao);

        imageViewMidia = (ImageView) findViewById(R.id.imageViewMidia);
        videoViewMidia = (VideoView) findViewById(R.id.videoViewMidia);
        btn_playMidia = (ImageButton) findViewById(R.id.btn_playMidia);

        webViewLocalizacao = (WebView) findViewById(R.id.webViewLocalizacao);

        //settings webView
        webViewLocalizacao.getSettings().setJavaScriptEnabled(true);
        webViewLocalizacao.getSettings().setSupportZoom(true);
        webViewLocalizacao.getSettings().setBuiltInZoomControls(false);
        webViewLocalizacao.setBackgroundColor(Color.parseColor("#FFFFFF"));
        webViewLocalizacao.getSettings().setUseWideViewPort(true);
        webViewLocalizacao.getSettings().setLoadWithOverviewMode(false);
        webViewLocalizacao.setInitialScale(15);

        int position = getIntent().getIntExtra("position", 0);
        denunciaDAO = new DenunciaDAO(this);

        denuncia = denunciaDAO.listar().get(position);

        preencherCampos();
    }

    public void preencherCampos(){
        //De acordo com o tipo da categoria
        if (denuncia.getCategoria() == 0){
            iconeCategoria.setImageResource(R.drawable.ic_road);
            textViewCategoria.setText("Vias públicas de acesso");
        }
        else if(denuncia.getCategoria() == 1){
            iconeCategoria.setImageResource(R.drawable.ic_park);
            textViewCategoria.setText("Equipamentos comunitários");
        }
        else if(denuncia.getCategoria() == 2){
            iconeCategoria.setImageResource(R.drawable.ic_sewer);
            textViewCategoria.setText("Equipamentos comunitários");
        }

        //data da denuncia
        String data = fmt.format(denuncia.getData());
        textViewData.setText("Registrada em: " + data);

        //usuario da denuncia
        textViewUsuarioDenuncia.setText("Autor: " + denuncia.getUsuario());
        textViewDescricao.setText(denuncia.getDescricao());

        //localizacao da denuncia
        String url = String.format(urlBase, denuncia.getLatitude(), denuncia.getLongitude());
        webViewLocalizacao.loadUrl(url);

         setupMidia();
    }

    public void setupMidia(){
        Uri uri = Uri.parse(denuncia.getUriMidia());
        String path = uri.toString();

        String midiaFormat = path.substring(path.length() - 3, path.length());

        if(midiaFormat.equals("mp4")){
            videoViewMidia.setVisibility(View.VISIBLE);
            videoViewMidia.setVideoPath(path);
        }
        else if(midiaFormat.equals("jpg")){
            imageViewMidia.setVisibility(View.VISIBLE);

            Bitmap bitmap = null;
            try {
                bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            imageViewMidia.setImageBitmap(bitmap);
        }
    }


    public void playVideo(View view) {
        if(videoViewMidia.isPlaying()) {
            btn_playMidia.setVisibility(View.VISIBLE);
            btn_playMidia.setImageAlpha(180);
            videoViewMidia.pause();
            videoViewMidia.seekTo(100);
        }
        else{
            videoViewMidia.start();

            btn_playMidia.setImageAlpha(0);

            videoViewMidia.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                public void onCompletion(MediaPlayer mp) {
                    btn_playMidia.setImageAlpha(180);
                    videoViewMidia.seekTo(100);
                }
            });
        }

        //btn_playMidia.setVisibility(View.INVISIBLE);
    }


}
