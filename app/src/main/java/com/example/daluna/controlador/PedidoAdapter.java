package com.example.daluna.controlador;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.daluna.R;
import com.example.daluna.modelo.Venta;

import java.util.List;

public class PedidoAdapter extends RecyclerView.Adapter<PedidoAdapter.PedidoViewHolder> {

    private List<Venta> listaPedidos;

    public PedidoAdapter(List<Venta> listaPedidos) {
        this.listaPedidos = listaPedidos;
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
        holder.numeroPedido.setText(venta.getNumeroPedido());
        holder.fechaVenta.setText(venta.getFechaVenta().toString());
        holder.estado.setText(venta.getEstado());
        holder.totalVenta.setText("Total Venta: $" + venta.getTotalVenta());
        holder.direccionEntrega.setText("Dirección: " + venta.getDireccionEntrega());
        holder.tiempoEstimadoEntrega.setText("Entrega Estimada: " + venta.getTiempoEstimadoEntrega());

        holder.expandButton.setOnClickListener(v -> {
            if (holder.expandableSection.getVisibility() == View.GONE) {
                holder.expandableSection.setVisibility(View.VISIBLE);
                holder.expandButton.setText("Ver Menos");
            } else {
                holder.expandableSection.setVisibility(View.GONE);
                holder.expandButton.setText("Ver Más");
            }
        });
    }

    @Override
    public int getItemCount() {
        return listaPedidos.size();
    }

    public void actualizarPedidos(List<Venta> nuevasVentas) {
        listaPedidos = nuevasVentas;
        notifyDataSetChanged();
    }

    static class PedidoViewHolder extends RecyclerView.ViewHolder {
        TextView numeroPedido, fechaVenta, estado, totalVenta, direccionEntrega, tiempoEstimadoEntrega;
        Button expandButton;
        View expandableSection;

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
        }
    }
}
