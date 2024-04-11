package com.example.daluna.modelo;

public class Producto {
    private String categoria;
    private String descripcion;
    private boolean estado;
    private String imagen;
    private String nombre;

    private double precio;

    public Producto() {
        // Constructor vacío requerido por Firebase
    }

    public Producto(String categoria, String descripcion, boolean estado, String imagen, String nombre,  double precio) {
        this.categoria = categoria;
        this.descripcion = descripcion;
        this.estado = estado;
        this.imagen = imagen;
        this.nombre = nombre;
        this.precio = precio;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public boolean isEstado() {
        return estado;
    }

    public void setEstado(boolean estado) {
        this.estado = estado;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }
}
