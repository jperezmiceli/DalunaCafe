package com.example.daluna.modelo;

public class Usuario {
    private String nombre;
    private String apellidos;
    private String numero;
    private String mail;
    private String id;
    private String ubicacion;
    public Usuario(){

    }

    public Usuario(String nombre, String apellidos, String numero, String mail, String id, String ubicacion) {
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.numero = numero;
        this.mail = mail;
        this.id = id;
        this.ubicacion = ubicacion;
    }
    public Usuario(String nombre,String numero, String mail){
        this.nombre = nombre;
        this.numero = numero;
        this.mail = mail;
        this.id = mail;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }
}
