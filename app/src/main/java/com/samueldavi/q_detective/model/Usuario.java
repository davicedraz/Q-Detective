package com.samueldavi.q_detective.model;

/**
 * Created by davicedraz on 10/12/2017.
 */

public class Usuario {

    private String nome;
    private String email;
    private String bairro;
    private String telefone;

    public Usuario(String nome, String email, String bairro, String telefone) {
        this.nome = nome;
        this.email = email;
        this.bairro = bairro;
        this.telefone = telefone;
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

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }
}
