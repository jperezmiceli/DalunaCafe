package com.example.daluna.controlador;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.daluna.R;
import com.example.daluna.modelo.CarritoModelo;

import java.util.List;

public class CarritoAdaptador extends RecyclerView.Adapter<CarritoAdaptador.ViewHolder> {

    private Context mContext;
    private List<CarritoModelo> mCarritoList;
    private FirebaseManager firebaseManager;


    public CarritoAdaptador(Context context, List<CarritoModelo> carritoList, FirebaseManager firebaseManager) {
        mContext = context;
        mCarritoList = carritoList;
        this.firebaseManager = firebaseManager; // Corrección aquí
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.activity_item_carrito, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CarritoModelo carrito = mCarritoList.get(position);
        holder.txtNombreProducto.setText(carrito.getNombreProducto());
        holder.txtCantidad.setText(String.valueOf(carrito.getCantidadProducto()));
        holder.txtPrecio.setText(String.valueOf(carrito.getPrecioTotalProducto()  + " €"));

        holder.btnPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int adapterPosition = holder.getAdapterPosition();
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    CarritoModelo carrito = mCarritoList.get(adapterPosition);
                    // Aquí puedes realizar la acción deseada, como aumentar la cantidad en el carrito
                    firebaseManager.agregarCantidadAlCarrito(carrito.getProductoId());
                }
            }
        });
        holder.btnMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int adapterPosition = holder.getAdapterPosition();
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    CarritoModelo carrito = mCarritoList.get(adapterPosition);
                    // Aquí puedes realizar la acción deseada, como aumentar la cantidad en el carrito
                    firebaseManager.quitarCantidadAlCarrito(carrito.getProductoId());
                }
            }
        });
        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int adapterPosition = holder.getAdapterPosition();
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    CarritoModelo carrito = mCarritoList.get(adapterPosition);
                    // Obtener el contexto desde el botón
                    Context context = v.getContext();
                    // Llamar a eliminarProductoDelCarrito pasando el contexto correcto
                    firebaseManager.eliminarProductoDelCarrito(carrito.getProductoId(), context);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return mCarritoList != null ? mCarritoList.size() : 0;
    }

    public void actualizarCarritos(List<CarritoModelo> carritoList) {
        mCarritoList = carritoList;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtNombreProducto, txtCantidad, txtPrecio;
        Button btnPlus;
        Button btnMinus;
        Button btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNombreProducto = itemView.findViewById(R.id.textViewProductName);
            txtCantidad = itemView.findViewById(R.id.textViewQuantity);
            txtPrecio = itemView.findViewById(R.id.textViewProductPrice);
            btnPlus = itemView.findViewById(R.id.btnPlus);
            btnMinus = itemView.findViewById(R.id.btnMinus);
            btnDelete = itemView.findViewById(R.id.btnEliminar);

        }
    }

}
