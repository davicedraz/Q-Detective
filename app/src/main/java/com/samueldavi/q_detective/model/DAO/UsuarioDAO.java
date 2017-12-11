package com.samueldavi.q_detective.model.DAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.samueldavi.q_detective.model.Usuario;
import com.samueldavi.q_detective.resources.DatabaseHelper;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by davicedraz on 10/12/2017.
 */

public class UsuarioDAO {
    private DatabaseHelper helper;
    private SQLiteDatabase db;
    private Cursor cursor;

    public UsuarioDAO (Context context) {
        this.helper = new DatabaseHelper(context);
        db = helper.getReadableDatabase();
        cursor = db.query(DatabaseHelper.UsuarioDB.TABELA,
                DatabaseHelper.UsuarioDB.COLUNAS_USUARIODB,
                null, null, null, null, null);
    }


    public List<Map<String, Object>> listarUsuarios() {

        List<Map<String, Object>> usuarios = new ArrayList<>();

        while (cursor.moveToNext()) {
            String nome = cursor.getString(cursor.getColumnIndex(DatabaseHelper.UsuarioDB.NOME));
            String email = cursor.getString(cursor.getColumnIndex(DatabaseHelper.UsuarioDB.EMAIL));
            String bairro = cursor.getString(cursor.getColumnIndex(DatabaseHelper.UsuarioDB.BAIRRO));
            String telefone = cursor.getString(cursor.getColumnIndex(DatabaseHelper.UsuarioDB.TELEFONE));

            Map<String, Object> usuario = new HashMap<>();

            usuario.put(DatabaseHelper.UsuarioDB.NOME, nome);
            usuario.put(DatabaseHelper.UsuarioDB.EMAIL, email);
            usuario.put(DatabaseHelper.UsuarioDB.BAIRRO, bairro);
            usuario.put(DatabaseHelper.UsuarioDB.TELEFONE, telefone);

            usuarios.add(usuario);
        }
        cursor.close();
        return usuarios;
    }


    public long inserirUsuario(Usuario usuario) {
        ContentValues values = new ContentValues();

            values.put(DatabaseHelper.UsuarioDB.NOME, usuario.getNome());
            values.put(DatabaseHelper.UsuarioDB.EMAIL, usuario.getEmail());
            values.put(DatabaseHelper.UsuarioDB.BAIRRO, usuario.getBairro());
            values.put(DatabaseHelper.UsuarioDB.TELEFONE, usuario.getTelefone());

            db = helper.getWritableDatabase();
            long qtdInseridos = db.insert(DatabaseHelper.UsuarioDB.TABELA, null, values);
            return qtdInseridos;
    }


    public Usuario buscarUsuario(String nome) {
        db = helper.getReadableDatabase();
        cursor = db.query(DatabaseHelper.UsuarioDB.TABELA,
                DatabaseHelper.UsuarioDB.COLUNAS_USUARIODB,
                DatabaseHelper.UsuarioDB.NOME + " = ?",
                new String[]{nome.toString()}, null, null, null);

        if (cursor.moveToNext()) {
            Usuario usuario = criarUsuario(cursor);
            cursor.close();
            return usuario;
        }
        return null;
    }


    public boolean removerUsuario(Integer id) {
        boolean flag = false;

        db = helper.getWritableDatabase();
        String where[] = new String[]{id.toString()};
        int removidos = db.delete(DatabaseHelper.UsuarioDB.TABELA, DatabaseHelper.UsuarioDB.NOME + " = ?", where);

        if(removidos > 0) {
            flag = true;
        }

        return flag;
    }

    public Usuario criarUsuario(Cursor cursor){
        String nome = cursor.getString(cursor.getColumnIndex(DatabaseHelper.UsuarioDB.NOME));
        String email = cursor.getString(cursor.getColumnIndex(DatabaseHelper.UsuarioDB.EMAIL));
        String bairro = cursor.getString(cursor.getColumnIndex(DatabaseHelper.UsuarioDB.BAIRRO));
        String telefone = cursor.getString(cursor.getColumnIndex(DatabaseHelper.UsuarioDB.TELEFONE));

        Usuario usuario = new Usuario(nome, email, bairro, telefone);
        return usuario;
    }

    public void close() {
        helper.close();
        db = null;
    }

}
