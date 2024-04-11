package com.example.daluna.modelo;

public class Usuarios {
    private String nombre;
    private String apellidos;
    private String numero;
    private String correo;
    private boolean estado;
    private int puntos;
    private String ubicacion;

    public Usuarios() {}

    public Usuarios(String nombre, String apellidos, String numero, String correo, boolean estado, int puntos, String ubicacion) {
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.numero = numero;
        this.correo = correo;
        this.estado = estado;
        this.puntos = puntos;
        this.ubicacion = ubicacion;
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

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public boolean isEstado() {
        return estado;
    }

    public void setEstado(boolean estado) {
        this.estado = estado;
    }

    public int getPuntos() {
        return puntos;
    }

    public void setPuntos(int puntos) {
        this.puntos = puntos;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }
}

