package com.africabermudez.lotusv1;

public class Article {

    /**
     * Clase que proporciona métodos y propiedades para administrar información relacionada con un articulo.
     *
     * @author Africa Maria Bermudez Mejias
     * @version 1
     */
    private String titulo, texto, UID, categoria;
    private int image;

    public Article() {
        // Constructor vacío requerido para Firebase
    }

    public Article(int image, String titulo) {
        this.titulo = titulo;
        this.image = image;
    }

    public Article(String UID, int image, String titulo, String texto, String categoria) {
        this.UID = UID;
        this.titulo = titulo;
        this.image = image;
        this.texto = texto;
        this.categoria = categoria;
    }

    public Article(String UID, String titulo, String texto, String categoria) {
        this.UID = UID;
        this.titulo = titulo;
        this.texto = texto;
        this.categoria = categoria;
    }


    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }


    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }
}

