package com.samueldavi.q_detective.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.VideoView;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.samueldavi.q_detective.R;
import com.samueldavi.q_detective.model.Categoria;
import com.samueldavi.q_detective.model.DAO.DenunciaDAO;
import com.samueldavi.q_detective.model.Denuncia;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URI;
import java.util.Calendar;
import java.util.Date;

public class DenunciaCadastroActivity extends AppCompatActivity{

    private EditText description;
    private Spinner category;
    private ImageView imgView;
    private VideoView videoView;
    private ImageButton btnPlay;
    private FloatingActionButton videoFab;
    private FloatingActionButton photoFab;
    private FloatingActionMenu fabMenu;
    private Uri uri;
    private boolean[] permissions = new boolean[3]; //0 is camera permission, 1 is permission to write on external storage, 2 is permission to read on external storage.
    private boolean hasSdCard;
    private LocationManager locationManager;
    private Location currentLocation;
    private DenunciaDAO denunciaDatabase;
    private Denuncia editingDenuncia;
    //private FusedLocationProviderClient mFusedLocationClient;
    private CustomLocationListener locationListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_denuncia_cadastro);

        denunciaDatabase = new DenunciaDAO(this);
        editingDenuncia = (Denuncia) getIntent().getSerializableExtra(getString(R.string.KEY_EDIT));
        //mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        findViews();
        setupView();

        manageFabButtons();
        setupSpinner();
    }

    private void findViews() {
        description = findViewById(R.id.editText_description);
        category = findViewById(R.id.spinner_category_cadastro);
        videoView = findViewById(R.id.videoView_cadastro);
        videoFab = findViewById(R.id.fab_addVideo_cadastro);
        photoFab = findViewById(R.id.fab_addPhoto_cadastro);
        fabMenu = findViewById(R.id.floating_menu_cadastro);
        imgView = findViewById(R.id.imageView_cadastro);
        btnPlay = findViewById(R.id.btn_play);

        videoFab.setImageDrawable(getResources().getDrawable(R.drawable.ic_add_video));
        photoFab.setImageDrawable(getResources().getDrawable(R.drawable.ic_image_add));
    }

    private void setupView() {
        if(editingDenuncia != null){
            description.setText(editingDenuncia.getDescricao());
            category.setSelection(editingDenuncia.getCategoria().getInt(editingDenuncia.getCategoria().getDescricao()));

            String mediaPath = Uri.parse(editingDenuncia.getUriMidia()).toString();

            String midiaFormat = mediaPath.substring(mediaPath.length() - 3, mediaPath.length());

            if(midiaFormat.equals("mp4")){
                imgView.setVisibility(View.GONE);
                videoView.setVisibility(View.VISIBLE);
                videoView.setVideoPath(mediaPath);

            }else{
                videoView.setVisibility(View.GONE);
                imgView.setVisibility(View.VISIBLE);
                try {
                    imgView.setImageBitmap(BitmapFactory.decodeStream(getContentResolver().openInputStream(Uri.parse(editingDenuncia.getUriMidia()))));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void setupSpinner() {
        ArrayAdapter<CharSequence> categoryAdapter = ArrayAdapter.createFromResource(this, R.array.spinner_array, R.layout.category_spinner_item);
        category.setAdapter(categoryAdapter);
    }

    private void manageFabButtons() {
        photoFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getPermissions();
                getLocationManager();
                startImageCapture();

            }
        });
        videoFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getPermissions();
                getLocationManager();
                startVideoRecorder();
            }
        });

        /*fabMenu.setOnMenuButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });*/
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
        Log.d("getPermissions: ", "SAIU");
    }

    private void startVideoRecorder() {
        if (permissions[0] && permissions[1] && permissions[2]) {
            Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
            intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 10);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, setMediaFile(true));
            try {
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(intent, getResources().getInteger(R.integer.REQUEST_VIDEO_CAPTURE));
                }
            } catch (Exception e) {
                e.printStackTrace();//Log.d("getPermissions: ", "Erro Camera");//Toast.makeText(this, "Erro ao iniciar a câmera.", Toast.LENGTH_LONG).show();
            }
        }
    }


    private void startImageCapture() {
        if (permissions[0] && permissions[1] && permissions[2]) {
            try {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, setMediaFile(false)); // isVideo parameter is false because we want an image.
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(intent, getResources().getInteger(R.integer.REQUEST_IMAGE_CAPTURE));
                }
            } catch (Exception e) {
                e.printStackTrace();//Log.d("getPermissions: ", "Erro Camera"); //Toast.makeText(this, "Erro ao iniciar a câmera.", Toast.LENGTH_LONG).show();
            }
        }
    }


    private Uri setMediaFile(boolean isVideo) {
        File diretorio;
        if (!hasSdCard) {
            diretorio = (isVideo) ? this.getExternalFilesDir(Environment.DIRECTORY_MOVIES) : this.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        }else{
            diretorio = (isVideo) ? Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES) : Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        }

        File pathMidia = (isVideo) ? new File(diretorio + "/" + System.currentTimeMillis() + ".mp4") : new File(diretorio + "/" + System.currentTimeMillis() + ".jpg");

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Log.d("BUILD VERSIO", "MAIOR QUE 6.0");
            String authority = this.getApplicationContext().getPackageName() + ".fileprovider";
            uri = FileProvider.getUriForFile(this, authority, pathMidia);
            return uri;
        } else {
            uri = Uri.fromFile(pathMidia);
            return uri;
        }

        // return Uri.fromFile(pathMidia);
    }

    private File getFileByUri(String path) {

        if (path.contains("/")) {
            System.out.println(path);
            int beginIndex = path.lastIndexOf("/") + 1;
            path = path.substring(beginIndex);
            System.out.println(path);
        }
        File diretorio = this.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return new File(diretorio, path);
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == getResources().getInteger(R.integer.REQUEST_VIDEO_CAPTURE)&& resultCode == RESULT_OK) {

            imgView.setVisibility(View.GONE);
            videoView.setVisibility(View.VISIBLE);
            btnPlay.setVisibility(View.VISIBLE);

            videoView.setVideoURI(uri);
            videoView.seekTo(100);
            setFabMenuToFinish();
            //saveVideo(videoUri.getPath());

        }

        else if(requestCode == getResources().getInteger(R.integer.REQUEST_IMAGE_CAPTURE)&& resultCode == RESULT_OK){
            // uri = intent.getData();
            videoView.setVisibility(View.GONE);
            imgView.setVisibility(View.VISIBLE);
            btnPlay.setVisibility(View.GONE);
            try {
                Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
                imgView.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            setFabMenuToFinish();
            if(imgView.getVisibility() ==  View.VISIBLE){
                Log.d("IMGVIEW VISIBILITY: ", "asdalksd");
            }

        }
    }

    private void setFabMenuToFinish(){
        //fabMenu.close(liste);
        fabMenu.getMenuIconView().setImageResource(R.drawable.ic_checked_rotated);
        fabMenu.setOnMenuButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String descricao = description.getText().toString();
                Date data = Calendar.getInstance().getTime();
                Double longitude;
                Double latitude;
                if(currentLocation != null) {
                    longitude = currentLocation.getLongitude();
                    latitude = currentLocation.getLatitude();
                }else{
                    longitude = 00.0000000;
                    latitude = 00.000000;
                }
                String uriMidia = uri.toString();
                String usuario = getIntent().getStringExtra(getString(R.string.KEY_USER_EXTRA));
                int categoria = category.getSelectedItemPosition();



                if(editingDenuncia == null) {
                    Denuncia denuncia = new Denuncia(descricao, data, longitude, latitude, uriMidia, usuario, Categoria.getCategoria(categoria));
                    denunciaDatabase.salvarDenuncia(denuncia);
                }else{
                    editingDenuncia.setDescricao(descricao);
                    editingDenuncia.setData(data);
                    editingDenuncia.setLatitude(latitude);
                    editingDenuncia.setLongitude(longitude);
                    editingDenuncia.setUriMidia(uriMidia);
                    editingDenuncia.setUsuario(usuario);
                    editingDenuncia.setCategoria(editingDenuncia.getCategoria().getCategoria(categoria));
                    denunciaDatabase.atualizarDenuncia(editingDenuncia);
                }


                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
//                finishActivity(RESULT_OK);

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
            Log.d("GETLOCATION: ", "SEM PERMISSÂO");
            return;
        }

        locationListener = new CustomLocationListener();
        locationManager = (LocationManager) DenunciaCadastroActivity.this.getSystemService(Context.LOCATION_SERVICE);
        long minTime = 0;
        float minDistance = 0;


/*        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        Log.d("getLastLocation: ", " Entrou");
                        if (location != null) {
                            currentLocation = location;
                            Log.d("getLastLocation: ", " "+ location.getLongitude());
                        }
                    }
                });*/

        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, minTime, minDistance, locationListener);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3600000, 1000, locationListener);




        Log.d("getPermissions: ", "SAIU");
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        Log.d("RESULTS_LENGTH:", " " + grantResults.length);
        if(grantResults.length != 0) {
            switch (requestCode) {
                case 1: {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                            grantResults[1] == PackageManager.PERMISSION_GRANTED &&
                            grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                    } else {
                        Toast.makeText(this, "Sem permissão para uso de gps ou rede.", Toast.LENGTH_LONG).show();
                    }
                    return;
                }
            }
        }
    }

    public void playVideo(View view) {
        if(videoView.isPlaying()) {
            btnPlay.setVisibility(View.VISIBLE);
            btnPlay.setImageAlpha(180);
            videoView.pause();
            videoView.seekTo(100);
        }
        else {
            videoView.start();
            btnPlay.setImageAlpha(0);

            videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                public void onCompletion(MediaPlayer mp) {
                    btnPlay.setImageAlpha(180);
                    videoView.seekTo(100);
                }
            });

        }


        //btnPlay.setVisibility(View.INVISIBLE);

    }


    private class CustomLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location location) {
            currentLocation = location;
            Log.d("LOCATION", "Latitude: " + location.getLatitude() + "     Longitude: " + location.getLongitude());
            locationManager.removeUpdates(locationListener);


            /*String latitudeStr = String.valueOf(location.getLatitude());
            String longitudeStr = String.valueOf(location.getLongitude());

            provedor.setText(location.getProvider());
            latitude.setText(latitudeStr);
            longitude.setText(longitudeStr);

            String url = String.format(urlBase, latitudeStr, longitudeStr);
            mWebView.loadUrl(url);*/
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {    }
        @Override
        public void onProviderEnabled(String provider) {  }
        @Override
        public void onProviderDisabled(String provider) { }
    }

}
