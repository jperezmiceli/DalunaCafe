package com.example.daluna.modelo;

public class CarritoModelo {

    private String productoId; // Cambiado a String para almacenar el ID del producto en lugar del objeto Producto
    private String nombreProducto; // Nombre del producto
    private String categoria; // Categoría del producto
    private String imagen; // URL de la imagen del producto
    private int cantidadProducto;
    private double precioProducto; // Precio del producto
    private double precioTotalProducto;
    private String comentario; // Comentario del producto

    public CarritoModelo() {
        // Constructor vacío requerido por Firebase
    }

    public CarritoModelo(String productoId, String nombreProducto, String categoria, String imagen, int cantidadProducto, double precioProducto, double precioTotalProducto, String comentario) {
        this.productoId = productoId;
        this.nombreProducto = nombreProducto;
        this.categoria = categoria;
        this.imagen = imagen;
        this.cantidadProducto = cantidadProducto;
        this.precioProducto = precioProducto;
        this.precioTotalProducto = precioTotalProducto;
        this.comentario = comentario;
    }

    public String getProductoId() {
        return productoId;
    }

    public void setProductoId(String productoId) {
        this.productoId = productoId;
    }

    public String getNombreProducto() {
        return nombreProducto;
    }

    public void setNombreProducto(String nombreProducto) {
        this.nombreProducto = nombreProducto;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public int getCantidadProducto() {
        return cantidadProducto;
    }

    public void setCantidadProducto(int cantidadProducto) {
        this.cantidadProducto = cantidadProducto;
    }

    public double getPrecioProducto() {
        return precioProducto;
    }

    public void setPrecioProducto(double precioProducto) {
        this.precioProducto = precioProducto;
    }

    public double getPrecioTotalProducto() {
        return precioTotalProducto;
    }

    public void setPrecioTotalProducto(double precioTotalProducto) {
        this.precioTotalProducto = precioTotalProducto;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }
}
