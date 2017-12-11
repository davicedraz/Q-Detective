package com.samueldavi.q_detective.activities;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_denuncia_cadastro);

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
}
