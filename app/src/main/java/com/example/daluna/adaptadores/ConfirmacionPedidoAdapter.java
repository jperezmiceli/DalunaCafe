package com.example.daluna.adaptadores;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.daluna.R;
import com.example.daluna.modelo.Venta;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class ConfirmacionPedidoAdapter extends RecyclerView.Adapter<ConfirmacionPedidoAdapter.PedidoViewHolder> {
    private List<Venta> ventaList;
    private Context context;

    public ConfirmacionPedidoAdapter(List<Venta> ventaList, Context context) {
        this.ventaList = ventaList;
        this.context = context;
    }

    @NonNull
    @Override
    public PedidoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_venta, parent, false);
        return new PedidoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PedidoViewHolder holder, int position) {
        Venta venta = ventaList.get(position);
        holder.textViewId.setText(venta.getNumeroPedido());
        holder.textViewEstado.setText(venta.getEstado());
        holder.editTextTiempoEntrega.setText(venta.getTiempoEstimadoEntrega());
        holder.textViewFechaVenta.setText(venta.getFechaVentaEntera());

        // Configurar el Spinner con los estados del pedido
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(context, R.array.estados_pedido, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        holder.spinnerEstado.setAdapter(adapter);
        if (venta.getEstado() != null) {
            int spinnerPosition = adapter.getPosition(venta.getEstado());
            holder.spinnerEstado.setSelection(spinnerPosition);
        }

        // Configurar el Spinner con los tiempos de espera
        ArrayAdapter<CharSequence> tiempoAdapter = ArrayAdapter.createFromResource(context, R.array.tiempos_espera, android.R.layout.simple_spinner_item);
        tiempoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        holder.spinnerTiempo.setAdapter(tiempoAdapter);
        if (venta.getTiempoEstimadoEntrega() != null) {
            int spinnerPosition = tiempoAdapter.getPosition(venta.getTiempoEstimadoEntrega());
            holder.spinnerTiempo.setSelection(spinnerPosition);
        }

        // Configurar el RecyclerView de productos
        ProductoAdapter productoAdapter = new ProductoAdapter(venta.getCarritoList());
        holder.recyclerViewProductos.setLayoutManager(new LinearLayoutManager(context));
        holder.recyclerViewProductos.setAdapter(productoAdapter);

        holder.buttonGuardar.setOnClickListener(v -> {
            String nuevoEstado = holder.spinnerEstado.getSelectedItem().toString();
            String nuevoTiempoEntrega = holder.spinnerTiempo.getSelectedItem().toString();

            // Guardar cambios en Firebase para la venta general
            DatabaseReference pedidoRef = FirebaseDatabase.getInstance().getReference().child("ventas").child(venta.getNumeroPedido());
            pedidoRef.child("estado").setValue(nuevoEstado);
            pedidoRef.child("tiempoEstimadoEntrega").setValue(nuevoTiempoEntrega);

            // Actualizar la venta del usuario correspondiente
            DatabaseReference usuarioPedidoRef = FirebaseDatabase.getInstance().getReference().child("usuarios").child(venta.getClienteId()).child("ventas").child(venta.getNumeroPedido());
            usuarioPedidoRef.child("estado").setValue(nuevoEstado);
            usuarioPedidoRef.child("tiempoEstimadoEntrega").setValue(nuevoTiempoEntrega);
        });

        holder.buttonEliminar.setOnClickListener(v -> {
            // Cambiar el estado del pedido en lugar de eliminarlo
            DatabaseReference pedidoRef = FirebaseDatabase.getInstance().getReference().child("ventas").child(venta.getNumeroPedido());
            pedidoRef.child("estado").setValue("Cancelado");
            venta.setEstado("Cancelado");
            notifyItemChanged(position);

            // Cambiar el estado del pedido para el usuario correspondiente
            DatabaseReference usuarioPedidoRef = FirebaseDatabase.getInstance().getReference().child("usuarios").child(venta.getClienteId()).child("ventas").child(venta.getNumeroPedido());
            usuarioPedidoRef.child("estado").setValue("Cancelado");
        });
    }

    @Override
    public int getItemCount() {
        return ventaList.size();
    }

    static class PedidoViewHolder extends RecyclerView.ViewHolder {
        TextView textViewId;
        TextView textViewEstado;
        Spinner spinnerEstado;
        Spinner spinnerTiempo;
        EditText editTextTiempoEntrega;
        Button buttonGuardar;
        Button buttonEliminar;
        TextView textViewFechaVenta;
        RecyclerView recyclerViewProductos; // RecyclerView para productos

        public PedidoViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewId = itemView.findViewById(R.id.text_view_id);
            textViewEstado = itemView.findViewById(R.id.text_view_estado);
            spinnerEstado = itemView.findViewById(R.id.spinner_estado);
            spinnerTiempo = itemView.findViewById(R.id.spinner_tiempo);
            editTextTiempoEntrega = itemView.findViewById(R.id.edit_text_tiempo_entrega);
            buttonGuardar = itemView.findViewById(R.id.button_guardar);
            buttonEliminar = itemView.findViewById(R.id.button_eliminar);
            textViewFechaVenta = itemView.findViewById(R.id.text_view_fecha_venta);
            recyclerViewProductos = itemView.findViewById(R.id.recycler_view_productos); // Inicializar el RecyclerView
        }
    }
}
