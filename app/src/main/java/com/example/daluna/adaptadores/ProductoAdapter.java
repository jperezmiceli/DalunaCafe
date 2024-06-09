package com.example.daluna.adaptadores;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.daluna.R;
import com.example.daluna.modelo.CarritoModelo;

import java.util.List;

public class ProductoAdapter extends RecyclerView.Adapter<ProductoAdapter.ProductoViewHolder> {

    private List<CarritoModelo> listaProductos;

    public ProductoAdapter(List<CarritoModelo> listaProductos) {
        this.listaProductos = listaProductos;
    }

    @NonNull
    @Override
    public ProductoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_lista_pedido, parent, false);
        return new ProductoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductoViewHolder holder, int position) {
        CarritoModelo producto = listaProductos.get(position);
        holder.nombreProducto.setText(producto.getNombreProducto());
        holder.cantidadProducto.setText(String.valueOf(producto.getCantidadProducto()) + " x");
        holder.precioProducto.setText(String.valueOf(producto.getPrecioProducto()) + " €");
    }

    @Override
    public int getItemCount() {
        return listaProductos.size();
    }

    static class ProductoViewHolder extends RecyclerView.ViewHolder {
        TextView nombreProducto, cantidadProducto, precioProducto;

        public ProductoViewHolder(@NonNull View itemView) {
            super(itemView);
            nombreProducto = itemView.findViewById(R.id.nombreProducto);
            cantidadProducto = itemView.findViewById(R.id.cantidadProducto);
            precioProducto = itemView.findViewById(R.id.precioProducto);
        }
    }
}
