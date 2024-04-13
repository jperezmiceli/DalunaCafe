package com.example.daluna.controlador;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.daluna.R;
import com.example.daluna.modelo.Producto;
import com.google.firebase.database.DatabaseReference;

import java.util.List;

public class ProductoAdaptador extends RecyclerView.Adapter<ProductoAdaptador.ProductoViewHolder> {
    private Context context;
    private List<Producto> listaProductos;



    public ProductoAdaptador(Context context, List<Producto> listaProductos) {
        this.context = context;
        this.listaProductos = listaProductos;
    }

    @NonNull
    @Override
    public ProductoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_producto, parent, false);
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
        private TextView textViewDescripcion;
        private TextView textViewPrecio;
        private Button buttonAddToCart;

        public ProductoViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewProducto = itemView.findViewById(R.id.imageViewProduct);
            textViewNombre = itemView.findViewById(R.id.textViewProductName);
            textViewDescripcion = itemView.findViewById(R.id.textViewProductDescription);
            textViewPrecio = itemView.findViewById(R.id.textViewProductPrice);
            buttonAddToCart = itemView.findViewById(R.id.buttonAddToCart);
        }

        public void bind(final Producto producto) {
            Glide.with(context).load(producto.getImagen()).into(imageViewProducto);
            textViewNombre.setText(producto.getNombre());
            textViewDescripcion.setText(producto.getDescripcion());
            textViewPrecio.setText(context.getString(R.string.price_format, producto.getPrecio()));

            buttonAddToCart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Aquí puedes implementar la lógica para añadir el producto al carrito
                    // Por ejemplo, mostrar un mensaje de confirmación
                    Toast.makeText(context, "Producto " + producto.getNombre() + " añadido al carrito", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
