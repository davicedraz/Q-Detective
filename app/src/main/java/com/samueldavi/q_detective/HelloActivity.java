package com.samueldavi.q_detective;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.samueldavi.q_detective.model.DAO.UsuarioDAO;
import com.samueldavi.q_detective.model.Usuario;

public class HelloActivity extends AppCompatActivity {

    private UsuarioDAO usuarioDAO;
    private EditText nome_usuario;
    private EditText email_usuario;
    private EditText bairro_usurio;
    private EditText telefone_usuario;

    private Button buttonContinuar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hello);

        usuarioDAO = new UsuarioDAO(this);

       nome_usuario = (EditText) findViewById(R.id.editTextNome);
       email_usuario = (EditText) findViewById(R.id.editTextEmail);
       bairro_usurio = (EditText) findViewById(R.id.editTextBairro);
       telefone_usuario = (EditText) findViewById(R.id.editTextTelefone);

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
                String telefone = telefone_usuario.getText().toString();

                Usuario usuario = new Usuario(nome, email, bairro, telefone);

                usuarioDAO.inserirUsuario(usuario);

               Log.d("NADA", usuarioDAO.buscarUsuario(usuario.getNome()).getNome());

            }
        });

    }
}
