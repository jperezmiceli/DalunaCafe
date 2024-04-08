package com.example.daluna.modelo;

public class Producto {
    private String nombreProducto;
    private String idProducto;
    private double precioProducto;
    private int puntosProducto;
    private String categoriaProducto;

    public Producto(String nombreProducto, String idProducto, double precioProducto, String categoriaProducto) {
        this.nombreProducto = nombreProducto;
        this.idProducto = idProducto;
        this.precioProducto = precioProducto;
        this.categoriaProducto = categoriaProducto;
        this.puntosProducto = (int) Math.floor(precioProducto * 10);
    }

    public String getNombreProducto() {
        return nombreProducto;
    }

    public void setNombreProducto(String nombreProducto) {
        this.nombreProducto = nombreProducto;
    }

    public String getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(String idProducto) {
        this.idProducto = idProducto;
    }

    public double getPrecioProducto() {
        return precioProducto;
    }

    public void setPrecioProducto(int precioProducto) {
        this.precioProducto = precioProducto;
    }

    public int getPuntosProducto() {
        return puntosProducto;
    }

    public void setPuntosProducto(int puntosProducto) {
        this.puntosProducto = puntosProducto;
    }

    public String getCategoriaProducto() {
        return categoriaProducto;
    }

    public void setCategoriaProducto(String categoriaProducto) {
        this.categoriaProducto = categoriaProducto;
    }
}
