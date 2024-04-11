package com.example.daluna.modelo;

import java.util.ArrayList;
import java.util.List;

public class Venta {
    private String idVenta;
    private String idUsuario;
    private List<Producto> productos;
    private double total;
    private String fecha;

    public Venta() {
        // Constructor vacío requerido para Firebase
    }

    public Venta(String idVenta, String idUsuario, List<Producto> productos, double total, String fecha) {
        this.idVenta = idVenta;
        this.idUsuario = idUsuario;
        this.productos = productos;
        this.total = total;
        this.fecha = fecha;
    }

    public String getIdVenta() {
        return idVenta;
    }

    public void setIdVenta(String idVenta) {
        this.idVenta = idVenta;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public List<Producto> getProductos() {
        return productos;
    }

    public void setProductos(List<Producto> productos) {
        this.productos = productos;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    // Método para agregar un producto a la venta
    public void agregarProducto(Producto producto) {
        if (productos == null) {
            productos = new ArrayList<>();
        }
        productos.add(producto);
    }
}
