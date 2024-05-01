package com.example.daluna.controlador;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.daluna.R;
import com.example.daluna.modelo.Producto;

import java.util.List;

public class ProductoAdaptador extends RecyclerView.Adapter<ProductoAdaptador.ProductoViewHolder> {
    private Context context;
    private List<Producto> listaProductos;
    private LayoutInflater inflater;
    private OnItemClickListener listener;

    public ProductoAdaptador(Context context, List<Producto> listaProductos) {
        this.context = context;
        this.listaProductos = listaProductos;
        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ProductoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_producto, parent, false);
        return new ProductoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductoViewHolder holder, int position) {
        Producto producto = listaProductos.get(position);
        holder.bind(producto);
    }

    @Override
    public int getItemCount() {
        return listaProductos.size();
    }

    public class ProductoViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageViewProducto;
        private TextView textViewNombre;
        private TextView textViewProductPrice;
        private ImageView imageViewAddToCart;
        private ConstraintLayout constraintLayout;

        public ProductoViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewProducto = itemView.findViewById(R.id.imageViewProduct);
            textViewNombre = itemView.findViewById(R.id.textViewProductName);
            textViewProductPrice = itemView.findViewById(R.id.textViewProductPrice);
            imageViewAddToCart = itemView.findViewById(R.id.imageViewAddToCart);
            constraintLayout = itemView.findViewById(R.id.constraintBotonAddCarrito);
        }

        public void bind(final Producto producto) {
            if (producto.getImagen().isEmpty()) {
                producto.setImagen("https://firebasestorage.googleapis.com/v0/b/daluna-22b7a.appspot.com/o/imgDefecto.jpeg?alt=media&token=c2250b4a-df39-4b32-8cde-ee84470480a6");
            }

            Glide.with(context)
                    .load(producto.getImagen())
                    .into(imageViewProducto);

            textViewNombre.setText(producto.getNombre());
            textViewProductPrice.setText(context.getString(R.string.price_format, producto.getPrecio()));

            constraintLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Aquí puedes implementar la lógica para añadir el producto al carrito
                    // Por ejemplo, mostrar un mensaje de confirmación
                    Toast.makeText(context, "Producto " + producto.getNombre() + " añadido al carrito", Toast.LENGTH_SHORT).show();
                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onItemClick(producto);
                    }
                }
            });
        }
    }

    public void actualizarLista(List<Producto> nuevosProductos) {
        listaProductos.clear();
        listaProductos.addAll(nuevosProductos);
        notifyDataSetChanged();
    }

    public interface OnItemClickListener {
        void onItemClick(Producto producto);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}
