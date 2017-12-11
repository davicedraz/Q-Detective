package com.samueldavi.q_detective.model.DAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.samueldavi.q_detective.model.Denuncia;
import com.samueldavi.q_detective.model.Usuario;
import com.samueldavi.q_detective.resources.DatabaseHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by davicedraz on 10/12/2017.
 */

public class DenunciaDAO {
    private DatabaseHelper helper;
    private SQLiteDatabase db;
    private Cursor cursor;
    private SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy");

    public DenunciaDAO(Context context) {
        this.helper = new DatabaseHelper(context);
    }

    public List<Map<String, Object>> listarDenuncias() {
        db = helper.getReadableDatabase();
        cursor = db.query(DatabaseHelper.DenunciaDB.TABELA,
                DatabaseHelper.DenunciaDB.COLUNAS_DENUNCIADB,
                null, null, null, null, null);

        List<Map<String, Object>> denuncias = new ArrayList<>();

        while (cursor.moveToNext()) {
            Integer id = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.DenunciaDB._ID));
            String descricao = cursor.getString(cursor.getColumnIndex(DatabaseHelper.DenunciaDB.DESCRICAO));
            Date data = new Date(cursor.getLong(cursor.getColumnIndex(DatabaseHelper.DenunciaDB.DATA)));
            Double longitude = cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.DenunciaDB.LONGITUDE));
            Double latitude = cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.DenunciaDB.LATITUDE));
            String uriMidia = cursor.getString(cursor.getColumnIndex(DatabaseHelper.DenunciaDB.URIMIDIA));
            String usuario = cursor.getString(cursor.getColumnIndex(DatabaseHelper.DenunciaDB.USUARIO));
            int categoria = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.DenunciaDB.CATEGORIA));

            Map<String, Object> denuncia = new HashMap<>();

            denuncia.put(DatabaseHelper.DenunciaDB._ID, id);
            denuncia.put(DatabaseHelper.DenunciaDB.DESCRICAO, descricao);
            denuncia.put(DatabaseHelper.DenunciaDB.DATA, fmt.format(data));
            denuncia.put(DatabaseHelper.DenunciaDB.LONGITUDE, longitude);
            denuncia.put(DatabaseHelper.DenunciaDB.LATITUDE, latitude);
            denuncia.put(DatabaseHelper.DenunciaDB.URIMIDIA, uriMidia);
            denuncia.put(DatabaseHelper.DenunciaDB.USUARIO, usuario);
            denuncia.put(DatabaseHelper.DenunciaDB.CATEGORIA, categoria);

            denuncias.add(denuncia);
        }
        cursor.close();
        return denuncias;
    }

    public List<Denuncia> listar() {
        db = helper.getReadableDatabase();
        cursor = db.query(DatabaseHelper.DenunciaDB.TABELA,
                DatabaseHelper.DenunciaDB.COLUNAS_DENUNCIADB,
                null, null, null, null, null);

        List<Denuncia> contatos = new ArrayList<>();

        while (cursor.moveToNext()) {
            contatos.add(this.criarDenuncia(cursor));
        }
        cursor.close();
        return contatos;
    }

    public Denuncia buscarDenunciaPorID(Integer id) {
        db = helper.getReadableDatabase();
        cursor = db.query(DatabaseHelper.DenunciaDB.TABELA,
                DatabaseHelper.DenunciaDB.COLUNAS_DENUNCIADB,
                DatabaseHelper.DenunciaDB._ID + " = ?",
                new String[]{id.toString()}, null, null, null);

        if (cursor.moveToNext()) {
            Denuncia denuncia = criarDenuncia(cursor);
            cursor.close();
            return denuncia;
        }
        return null;
    }

    public long salvarDenuncia(Denuncia denuncia) {
        ContentValues values = new ContentValues();

            if (denuncia.getId() > 0) {
                values.put(DatabaseHelper.DenunciaDB._ID, denuncia.getId());
            }

            values.put(DatabaseHelper.DenunciaDB.DESCRICAO, denuncia.getDescricao());
            values.put(DatabaseHelper.DenunciaDB.DATA, denuncia.getData().getTime());
            values.put(DatabaseHelper.DenunciaDB.LONGITUDE, denuncia.getLongitude());
            values.put(DatabaseHelper.DenunciaDB.LATITUDE, denuncia.getLatitude());
            values.put(DatabaseHelper.DenunciaDB.URIMIDIA, denuncia.getUriMidia());
            values.put(DatabaseHelper.DenunciaDB.USUARIO, denuncia.getUsuario());
            values.put(DatabaseHelper.DenunciaDB.CATEGORIA, denuncia.getCategoria());

            db = helper.getWritableDatabase();

            long qtdInseridos = db.insert(DatabaseHelper.DenunciaDB.TABELA, null, values);
            return qtdInseridos;
    }

    public boolean atualizarDenuncia(Denuncia denuncia) {
        ContentValues values = new ContentValues();

        boolean flag = false;

        if (buscarDenunciaPorID(denuncia.getId()) != null) {

            flag = true;

            values.put(DatabaseHelper.DenunciaDB.DESCRICAO, denuncia.getDescricao());
            values.put(DatabaseHelper.DenunciaDB.DATA, denuncia.getData().getTime());
            values.put(DatabaseHelper.DenunciaDB.LONGITUDE, denuncia.getLongitude());
            values.put(DatabaseHelper.DenunciaDB.LATITUDE, denuncia.getLatitude());
            values.put(DatabaseHelper.DenunciaDB.URIMIDIA, denuncia.getUriMidia());
            values.put(DatabaseHelper.DenunciaDB.USUARIO, denuncia.getUsuario());
            values.put(DatabaseHelper.DenunciaDB.CATEGORIA, denuncia.getCategoria());

            db = helper.getWritableDatabase();
            db.update(DatabaseHelper.DenunciaDB.TABELA,
                    values,
                    DatabaseHelper.DenunciaDB._ID + " = ?",
                    new String[]{denuncia.getId().toString()});
            return flag;
        }
        else{
            return flag;
        }
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
