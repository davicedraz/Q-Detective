package com.samueldavi.q_detective.activities;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.VideoView;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.samueldavi.q_detective.R;

public class DenunciaCadastroActivity extends AppCompatActivity {

    private EditText description;
    private Spinner category;
    private ImageView imgView;
    private VideoView videoView;
    private FloatingActionButton videoFab;
    private FloatingActionButton photoFab;
    private FloatingActionMenu fabMenu;

    private LocationManager locationManager;
    private String provedor;
    private Double latitude;
    private Double longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_denuncia_cadastro);

        getLocationManager();

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
                //Tirar Foto
            }
        });
        videoFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Gravar Video
            }
        });
    }


    private void getLocationManager() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.INTERNET},
                    1);
            return;
        }

        Listener listener = new Listener();
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        long tempoAtualizacao = 0;
        float distancia = 0;
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, tempoAtualizacao, distancia, listener);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, tempoAtualizacao, distancia, listener);

    }


    private class Listener implements LocationListener {

        @Override
        public void onLocationChanged(Location location) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            provedor = (location.getProvider());
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {    }
        @Override
        public void onProviderEnabled(String provider) {  }
        @Override
        public void onProviderDisabled(String provider) { }
    }

}



