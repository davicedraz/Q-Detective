package com.samueldavi.q_detective.model;

/**
 * Created by davicedraz on 10/12/2017.
 */

public class Usuario {

    private String nome;
    private String email;
    private String bairro;

    public Usuario(String nome, String email, String bairro) {
        this.nome = nome;
        this.email = email;
        this.bairro = bairro;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }
}
