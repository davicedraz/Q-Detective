package com.samueldavi.q_detective.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.samueldavi.q_detective.R;
import com.samueldavi.q_detective.model.DAO.UsuarioDAO;
import com.samueldavi.q_detective.model.Usuario;

public class HelloActivity extends AppCompatActivity {

    private UsuarioDAO usuarioDAO;
    private EditText nome_usuario;
    private EditText email_usuario;
    private EditText bairro_usurio;

    private Button buttonContinuar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hello);

        usuarioDAO = new UsuarioDAO(this);

//        if (usuarioDAO.listarUsuarios().size() > 0){
//            redirect();
//        }

       nome_usuario = (EditText) findViewById(R.id.editTextNome);
       email_usuario = (EditText) findViewById(R.id.editTextEmail);
       bairro_usurio = (EditText) findViewById(R.id.editTextBairro);

       buttonContinuar = (Button) findViewById(R.id.buttonContinuar);

       novoUsuario();

    }

    public void novoUsuario(){
        buttonContinuar.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                String nome = nome_usuario.getText().toString();
                String email = email_usuario.getText().toString();
                String bairro = bairro_usurio.getText().toString();

                Usuario usuario = new Usuario(nome, email, bairro);

                if(usuarioDAO.inserirUsuario(usuario) > 0){
                    redirect();
                }
            }
        });
    }


    public void redirect(){
        Intent intent = new Intent(this, DenunciaActivity.class);
        startActivity(intent);
    }
}
