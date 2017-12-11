package com.samueldavi.q_detective.model.DAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.samueldavi.q_detective.model.Usuario;
import com.samueldavi.q_detective.resources.DatabaseHelper;

import java.util.Date;

/**
 * Created by davicedraz on 10/12/2017.
 */

public class UsuarioDAO {
    private DatabaseHelper helper;
    private SQLiteDatabase db;
    private Cursor cursor;

    public UsuarioDAO (Context context) {
        this.helper = new DatabaseHelper(context);
    }


    public boolean inserirContato(Usuario usuario) {
        ContentValues values = new ContentValues();
        boolean flag = false;

        if(buscarUsuario(usuario.getNome()) != null){
            values.put(DatabaseHelper.UsuarioDB.NOME, usuario.getNome());
            values.put(DatabaseHelper.UsuarioDB.EMAIL, usuario.getEmail());
            values.put(DatabaseHelper.UsuarioDB.BAIRRO, usuario.getBairro());
            values.put(DatabaseHelper.UsuarioDB.TELEFONE, usuario.getTelefone());

            db = helper.getWritableDatabase();
            db.insert(DatabaseHelper.UsuarioDB.TABELA, null, values);
            flag = true;
        }
        else{
            flag = false;
        }
        return flag;
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
