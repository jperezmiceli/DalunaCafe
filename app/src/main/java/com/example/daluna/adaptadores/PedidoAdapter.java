package com.example.daluna.adaptadores;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.daluna.R;
import com.example.daluna.modelo.Venta;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class PedidoAdapter extends RecyclerView.Adapter<PedidoAdapter.PedidoViewHolder> {

    private List<Venta> listaPedidos;

    public PedidoAdapter(List<Venta> listaPedidos) {
        this.listaPedidos = listaPedidos;
        ordenarPedidosPorFecha();
    }

    @NonNull
    @Override
    public PedidoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pedido, parent, false);
        return new PedidoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PedidoViewHolder holder, int position) {
        Venta venta = listaPedidos.get(position);

        holder.numeroPedido.setText(venta.getNumeroPedido() != null ? venta.getNumeroPedido() : "Número no disponible");
        holder.fechaVenta.setText(venta.getFechaVentaEntera() != null ? venta.getFechaVentaEntera().toString() : "Fecha no disponible");
        holder.estado.setText(venta.getEstado() != null ? venta.getEstado() : "Estado no disponible");
        holder.totalVenta.setText("Total Venta: " + venta.getTotalVenta() + "€ ");
        holder.direccionEntrega.setText(venta.getDireccionEntrega() != null ? "Dirección: " + venta.getDireccionEntrega() : "Dirección no disponible");
        holder.tiempoEstimadoEntrega.setText(venta.getTiempoEstimadoEntrega() != null ? "Entrega Estimada: " + venta.getTiempoEstimadoEntrega() : "Tiempo estimado no disponible");

        // Mostrar botón y texto solo si el pedido está aceptado
        if ("Aceptado".equalsIgnoreCase(venta.getEstado())) {
            holder.textView2.setVisibility(View.VISIBLE);
            holder.pagarBoton.setVisibility(View.VISIBLE);
        } else {
            holder.textView2.setVisibility(View.GONE);
            holder.pagarBoton.setVisibility(View.GONE);
        }

        // Configurar el RecyclerView de productos
        ProductoAdapter productoAdapter = new ProductoAdapter(venta.getCarritoList());
        holder.recyclerViewProductos.setLayoutManager(new LinearLayoutManager(holder.itemView.getContext()));
        holder.recyclerViewProductos.setAdapter(productoAdapter);

        holder.expandButton.setOnClickListener(v -> {
            if (holder.expandableSection.getVisibility() == View.GONE) {
                holder.expandableSection.setVisibility(View.VISIBLE);
                holder.expandButton.setText("Ver Menos");
            } else {
                holder.expandableSection.setVisibility(View.GONE);
                holder.expandButton.setText("Ver Más");
            }
        });

        // Manejar el evento de clic del botón pagar
        holder.pagarBoton.setOnClickListener(v -> {
            // Actualizar el estado del pedido a "Pagado"
            venta.setEstado("pagado");
            DatabaseReference pedidoRef = FirebaseDatabase.getInstance().getReference().child("ventas").child(venta.getNumeroPedido());
            pedidoRef.child("estado").setValue("pagado");

            // Actualizar el estado del pedido para el usuario correspondiente
            DatabaseReference usuarioPedidoRef = FirebaseDatabase.getInstance().getReference().child("usuarios").child(venta.getClienteId()).child("ventas").child(venta.getNumeroPedido());
            usuarioPedidoRef.child("estado").setValue("pagado");

            // Ocultar el botón y el texto después de pagar
            holder.textView2.setVisibility(View.GONE);
            holder.pagarBoton.setVisibility(View.GONE);

            notifyDataSetChanged();
        });
    }

    @Override
    public int getItemCount() {
        return listaPedidos.size();
    }

    public void actualizarPedidos(List<Venta> nuevasVentas) {
        listaPedidos.addAll(0, nuevasVentas);
        ordenarPedidosPorFecha();
        notifyDataSetChanged();
    }

    private void ordenarPedidosPorFecha() {
        Collections.sort(listaPedidos, Comparator.comparing(Venta::getFechaVentaEntera).reversed());
    }

    static class PedidoViewHolder extends RecyclerView.ViewHolder {
        TextView numeroPedido, fechaVenta, estado, totalVenta, direccionEntrega, tiempoEstimadoEntrega, textView2;
        Button expandButton, pagarBoton;
        View expandableSection;
        RecyclerView recyclerViewProductos;

        public PedidoViewHolder(@NonNull View itemView) {
            super(itemView);
            numeroPedido = itemView.findViewById(R.id.numeroPedido);
            fechaVenta = itemView.findViewById(R.id.fechaVenta);
            estado = itemView.findViewById(R.id.estado);
            totalVenta = itemView.findViewById(R.id.totalVenta);
            direccionEntrega = itemView.findViewById(R.id.direccionEntrega);
            tiempoEstimadoEntrega = itemView.findViewById(R.id.tiempoEstimadoEntrega);
            expandButton = itemView.findViewById(R.id.expandButton);
            expandableSection = itemView.findViewById(R.id.expandableSection);
            recyclerViewProductos = itemView.findViewById(R.id.recyclerViewProductos);
            textView2 = itemView.findViewById(R.id.textView2);
            pagarBoton = itemView.findViewById(R.id.pagarboton);
        }
    }
}
