package com.samueldavi.q_detective.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.VideoView;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.samueldavi.q_detective.R;
import com.samueldavi.q_detective.model.DAO.DenunciaDAO;
import com.samueldavi.q_detective.model.Denuncia;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Calendar;
import java.util.Date;

public class DenunciaCadastroActivity extends AppCompatActivity implements LocationListener{

    private EditText description;
    private Spinner category;
    private ImageView imgView;
    private VideoView videoView;
    private FloatingActionButton videoFab;
    private FloatingActionButton photoFab;
    private FloatingActionMenu fabMenu;
    private Uri uri;
    private boolean[] permissions; //0 is camera permission, 1 is permission to write on external storage, 2 is permission to read on external storage.
    private boolean hasSdCard;
    private LocationManager locationManager;
    private Location location;
    DenunciaDAO denunciaDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_denuncia_cadastro);
        denunciaDatabase = new DenunciaDAO(this);

        manageFabButtons();
        setupSpinner();
    }

    private void setupSpinner() {
        ArrayAdapter<CharSequence> categoryAdapter = ArrayAdapter.createFromResource(this, R.array.spinner_array, R.layout.category_spinner_item);
        category.setAdapter(categoryAdapter);
    }

    private void manageFabButtons() {
        photoFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startImageCapture();
            }
        });
        videoFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startVideoRecorder();
            }
        });

        fabMenu.setOnMenuButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getPermissions();
            }
        });
    }

    private void getPermissions() {
        String CAMERA = Manifest.permission.CAMERA;
        String WRITE_EXTERNAL_STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE;
        String READ_EXTERNAL_STORAGE = Manifest.permission.READ_EXTERNAL_STORAGE;
        int PERMISSION_GRANTED = PackageManager.PERMISSION_GRANTED;

        hasSdCard = Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);

        permissions[0] = ActivityCompat.checkSelfPermission(this, CAMERA) == PERMISSION_GRANTED;
        permissions[1] = ActivityCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE) == PERMISSION_GRANTED;
        permissions[2] = ActivityCompat.checkSelfPermission(this, READ_EXTERNAL_STORAGE) == PERMISSION_GRANTED;

        if (!(permissions[0] && permissions[1] && permissions[2])) {
            ActivityCompat.requestPermissions(this, new String[]{CAMERA, WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE}, getResources().getInteger(R.integer.REQUEST_PERMISSIONS));
        }
    }

    private void startVideoRecorder() {
        try {

            Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
            intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 10);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(setMediaFile(true)));

            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(intent, getResources().getInteger(R.integer.REQUEST_VIDEO_CAPTURE));
            }
        } catch (Exception e) {
            Toast.makeText(this, "Erro ao iniciar a câmera.", Toast.LENGTH_LONG).show();
        }
    }

    private void startImageCapture() {
        try {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(setMediaFile(false))); // isVideo parameter is false because we want an image.
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(intent, getResources().getInteger(R.integer.REQUEST_IMAGE_CAPTURE));
            }
        } catch (Exception e) {
            Toast.makeText(this, "Erro ao iniciar a câmera.", Toast.LENGTH_LONG).show();
        }
    }


    private File setMediaFile(boolean isVideo) {
        File diretorio;
        if (!hasSdCard) {
            diretorio = (isVideo) ? this.getExternalFilesDir(Environment.DIRECTORY_MOVIES) : this.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        }else{
            diretorio = (isVideo) ? Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES) : Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        }

        File pathMidia = (isVideo) ? new File(diretorio + "/" + System.currentTimeMillis() + ".mp4") : new File(diretorio + "/" + System.currentTimeMillis() + ".jpg");
        return pathMidia;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == getResources().getInteger(R.integer.REQUEST_VIDEO_CAPTURE)&& resultCode == RESULT_OK) {
            imgView.setVisibility(View.GONE);

            uri = intent.getData();
            videoView.setVideoURI(uri);
            videoView.pause();
            videoView.seekTo(100);
            setFabMenuToFinish();
            //saveVideo(videoUri.getPath());
        }

        else if(requestCode == getResources().getInteger(R.integer.REQUEST_IMAGE_CAPTURE)&& resultCode == RESULT_OK){
            uri = intent.getData();
            videoView.setVisibility(View.GONE);
            try {
               Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
                imgView.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            setFabMenuToFinish();
        }
    }

    private void setFabMenuToFinish(){
        fabMenu.getMenuIconView().setImageResource(R.drawable.ic_checked);
        fabMenu.setOnMenuButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getLocationManager();

                String descricao = description.getText().toString();
                Date data = Calendar.getInstance().getTime();
                Double longitude = location.getLongitude();
                Double latitude = location.getLatitude();
                String uriMidia = uri.toString();
                String usuario = getIntent().getStringExtra(getString(R.string.KEY_USER_EXTRA));;
                int categoria = category.getSelectedItemPosition();


                Denuncia denuncia = new Denuncia(descricao, data, longitude, latitude, uriMidia, usuario, categoria);
                denunciaDatabase.salvarDenuncia(denuncia);

                finish();
            }
        });
    }

    private void getLocationManager() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.INTERNET},
                    getResources().getInteger(R.integer.REQUEST_PERMISSIONS));
            return;
        }

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        long minTime = 0;
        float minDistance = 0;
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, minTime, minDistance, this);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTime, minDistance, this);
    }


    @Override
    public void onLocationChanged(Location location) {
        this.location = location;
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                        grantResults[1] == PackageManager.PERMISSION_GRANTED &&
                        grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                    getLocationManager();
                } else {
                    Toast.makeText(this, "Sem permissão para uso de gps ou rede.", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }
}
