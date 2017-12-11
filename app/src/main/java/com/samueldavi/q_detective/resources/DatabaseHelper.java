package com.samueldavi.q_detective.resources;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by davicedraz on 10/12/2017.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String BANCO_DADOS = "qdetective";
    private static final int VERSAO = 1;

    public static class UsuarioDB {
        public static final String TABELA = "usuario";
        public static final String NOME = "nome";
        public static final String EMAIL = "email";
        public static final String BAIRRO = "bairro";
        public static final String TELEFONE = "telefone";

        public static final String[] COLUNAS_USUARIODB =
                new String[]{NOME, EMAIL, BAIRRO, TELEFONE};
    }
    public static class DenunciaDB {
        public static final String TABELA= "denuncia";
        public static final String _ID = "id";
        public static final String DESCRICAO = "descricao";
        public static final String DATA = "data";
        public static final String LONGITUDE = "longitude";
        public static final String LATITUDE = "latitude";
        public static final String URIMIDIA = "uriMidia";
        public static final String USUARIO = "usuario";
        public static final String CATEGORIA = "categoria";

        public static final String[] COLUNAS_DENUNCIADB =
                new String[]{_ID, DESCRICAO, DATA, LONGITUDE, LATITUDE, URIMIDIA, USUARIO, CATEGORIA};

    }

    public DatabaseHelper(Context context) {
        super(context, BANCO_DADOS, null, VERSAO);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + UsuarioDB.TABELA +
                "(" + UsuarioDB.NOME + " TEXT," +
                UsuarioDB.EMAIL + " TEXT," +
                UsuarioDB.BAIRRO + " TEXT," +
                UsuarioDB.TELEFONE + " TEXT);");

        db.execSQL("CREATE TABLE " + DenunciaDB.TABELA +
                "(" + DenunciaDB._ID + " INTEGER PRIMARY KEY," +
                DenunciaDB.DESCRICAO + " TEXT," +
                DenunciaDB.DATA + " DATE," +
                DenunciaDB.LONGITUDE + " DOUBLE," +
                DenunciaDB.LATITUDE + " DOUBLE," +
                DenunciaDB.URIMIDIA + " TEXT," +
                DenunciaDB.USUARIO + " TEXT," +
                DenunciaDB.CATEGORIA + " TEXT);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE " + UsuarioDB.TABELA + ";");
        db.execSQL("DROP TABLE " + DenunciaDB.TABELA + ";");
        onCreate(db);
    }
}