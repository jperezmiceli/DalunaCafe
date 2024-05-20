package com.example.daluna.modelo;



import java.util.Date;
import java.util.List;

public class Venta {
    private String numeroPedido;
    private Date fechaVenta;
    private String clienteId;
    private List<CarritoModelo> carritoList; // Lista de productos en el carrito
    private double totalVenta;
    private String metodoPago;
    private String direccionEntrega;
    private String tiempoEstimadoEntrega; // Tiempo estimado de entrega
    private String estado; // Por ejemplo: pendiente, en camino, entregado, cancelado

    public Venta() {
        // Constructor vacío requerido por Firebase
    }

    public Venta(String numeroPedido, String clienteId, List<CarritoModelo> carritoList,
                 double totalVenta, String direccionEntrega) {
        this.numeroPedido = numeroPedido;
        this.fechaVenta = new Date();
        this.clienteId = clienteId;
        this.carritoList = carritoList;
        this.totalVenta = totalVenta;
        this.metodoPago = "";
        this.direccionEntrega = direccionEntrega;
        this.estado = "espera";
        // Calculamos el tiempo estimado de entrega automáticamente
        this.tiempoEstimadoEntrega = calcularTiempoEstimadoEntrega();
    }

    private String calcularTiempoEstimadoEntrega() {
        // Lógica para calcular el tiempo estimado de entrega basado en los productos en el carrito,
        // la dirección de entrega, etc.
        // Aquí puedes implementar tu lógica para calcular el tiempo estimado de entrega de manera inteligente.
        return "1 hora"; // Ejemplo: se establece un tiempo fijo para la demostración
    }

    // Getters y setters

    public String getNumeroPedido() {
        return numeroPedido;
    }

    public void setNumeroPedido(String numeroPedido) {
        this.numeroPedido = numeroPedido;
    }

    public Date getFechaVenta() {
        return fechaVenta;
    }

    public void setFechaVenta(Date fechaVenta) {
        this.fechaVenta = fechaVenta;
    }

    public String getClienteId() {
        return clienteId;
    }

    public void setClienteId(String clienteId) {
        this.clienteId = clienteId;
    }

    public List<CarritoModelo> getCarritoList() {
        return carritoList;
    }

    public void setCarritoList(List<CarritoModelo> carritoList) {
        this.carritoList = carritoList;
    }

    public double getTotalVenta() {
        return totalVenta;
    }

    public void setTotalVenta(double totalVenta) {
        this.totalVenta = totalVenta;
    }

    public String getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(String metodoPago) {
        this.metodoPago = metodoPago;
    }

    public String getDireccionEntrega() {
        return direccionEntrega;
    }

    public void setDireccionEntrega(String direccionEntrega) {
        this.direccionEntrega = direccionEntrega;
    }

    public String getTiempoEstimadoEntrega() {
        return tiempoEstimadoEntrega;
    }

    public void setTiempoEstimadoEntrega(String tiempoEstimadoEntrega) {
        this.tiempoEstimadoEntrega = tiempoEstimadoEntrega;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}

