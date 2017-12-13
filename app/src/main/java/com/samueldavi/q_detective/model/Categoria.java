package com.samueldavi.q_detective.model;

import java.util.Calendar;

public enum Categoria {

        VIAS_PUBLICAS("Vias públicas de Acesso"),
        EQUIPAMENTOS_COMUNICATARIOS("Equipamentos Comunitários"),
        LIMPEZA_URBANA("Limpeza Urbana e Saneamento");

        private String descricao;

        private Categoria(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return this.descricao;
        }

        public int getInt(String descricao){
            if("Vias públicas de Acesso".equals(descricao)){
                return 0;
            }
            else if("Equipamentos Comunitários".equals(descricao)){
                return 1;
            }
            else if("Limpeza Urbana e Saneamento".equals(descricao)){
                return 2;
            }else{
                return -1;
            }
        }
        public static Categoria getCategoria(int categoria){
            Categoria cat;

            if(categoria == 0){
                cat = Categoria.VIAS_PUBLICAS;
            }else if(categoria == 1){
                cat = Categoria.EQUIPAMENTOS_COMUNICATARIOS;
            }
            else if(categoria == 2){
                cat = Categoria.LIMPEZA_URBANA;
            }else{
                cat = null;
            }
            return cat;
        }



}
