package com.samueldavi.q_detective.model.DAO;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.samueldavi.q_detective.model.Denuncia;
import com.samueldavi.q_detective.model.Usuario;
import com.samueldavi.q_detective.resources.DatabaseHelper;

import java.util.Date;

/**
 * Created by davicedraz on 10/12/2017.
 */

public class DenunciaDAO {
    private DatabaseHelper helper;
    private SQLiteDatabase db;
    private Cursor cursor;

    public DenunciaDAO (Context context) {
        this.helper = new DatabaseHelper(context);
    }



    public boolean removerDenuncia(Integer id) {
        db = helper.getWritableDatabase();
        String where[] = new String[]{id.toString()};
        int removidos = db.delete(DatabaseHelper.DenunciaDB.TABELA, "_id = ?", where);
        return removidos > 0;
    }

    public Denuncia criarDenuncia (Cursor cursor){
        Integer id = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.DenunciaDB._ID));
        String descricao = cursor.getString(cursor.getColumnIndex(DatabaseHelper.DenunciaDB.DESCRICAO));
        Date data = new Date(cursor.getLong(cursor.getColumnIndex(DatabaseHelper.DenunciaDB.DATA)));
        Double longitude = cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.DenunciaDB.LONGITUDE));
        Double latitude = cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.DenunciaDB.LATITUDE));
        String uriMidia = cursor.getString(cursor.getColumnIndex(DatabaseHelper.DenunciaDB.URIMIDIA));
        String usuario = cursor.getString(cursor.getColumnIndex(DatabaseHelper.DenunciaDB.USUARIO));
        int categoria = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.DenunciaDB.CATEGORIA));

        Denuncia denuncia = new Denuncia(id, descricao, data, longitude, latitude, uriMidia, usuario, categoria);
        return denuncia;
    }

    public void close() {
        helper.close();
        db = null;
    }

}
