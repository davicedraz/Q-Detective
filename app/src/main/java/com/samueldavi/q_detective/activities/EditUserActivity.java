package com.samueldavi.q_detective.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.samueldavi.q_detective.R;
import com.samueldavi.q_detective.model.DAO.UsuarioDAO;
import com.samueldavi.q_detective.model.Usuario;

import java.util.List;

public class EditUserActivity extends AppCompatActivity {

    private EditText nome;
    private EditText email;
    private EditText endereco;
    private UsuarioDAO usuarioDAO;
    private Usuario user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user);

        findViews();
        getUserFromDatabase();
        setText();

    }

    private void setText() {
        nome.setText(user.getNome());
        endereco.setText(user.getBairro());
        email.setText(user.getEmail());
    }

    private void getUserFromDatabase() {
        usuarioDAO = new UsuarioDAO(this);
        int userIndex = usuarioDAO.listar().size()-1;
        user = usuarioDAO.listar().get(userIndex);
    }

    private void findViews() {
        nome = findViewById(R.id.edit_nome_user);
        email = findViewById(R.id.edit_email_user);
        endereco = findViewById(R.id.edit_bairro_user);
    }

    public void saveUser(View view) {
        user.setBairro(endereco.getText().toString());
        user.setEmail(email.getText().toString());
        user.setNome(nome.getText().toString());

        usuarioDAO.inserirUsuario(user);

        finish();
    }
}
