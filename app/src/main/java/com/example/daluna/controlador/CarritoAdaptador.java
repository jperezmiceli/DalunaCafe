package com.example.daluna.controlador;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.daluna.R;
import com.example.daluna.modelo.CarritoModelo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import java.util.List;

public class CarritoAdaptador extends RecyclerView.Adapter<CarritoAdaptador.ViewHolder> {

    private Context mContext;
    private List<CarritoModelo> mCarritoList;
    private FirebaseManager firebaseManager;

    public CarritoAdaptador(Context context, List<CarritoModelo> carritoList, FirebaseManager firebaseManager) {
        mContext = context;
        mCarritoList = carritoList;
        this.firebaseManager = firebaseManager;
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
        holder.txtPrecio.setText(String.valueOf(carrito.getPrecioTotalProducto() + " €"));

        // Cargar la imagen utilizando Glide
        Glide.with(mContext)
                .load(carrito.getImagen())
                .into(holder.imageViewProducto);

        // Cargar el comentario en el EditText
        String comentario = carrito.getComentario();
        if (comentario != null && !comentario.isEmpty()) {
            holder.editTextComment.setText(comentario);
        } else {
            holder.editTextComment.setText("");
        }

        // Manejar la lógica del botón guardar comentario
        holder.btnGuardarComentario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String comentario = holder.editTextComment.getText().toString();
                if (!comentario.isEmpty()) {
                    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                    if (currentUser != null) {
                        DatabaseReference comentarioRef = firebaseManager.getDatabaseReferenceUsuarios()
                                .child(currentUser.getUid())
                                .child("carrito")
                                .child(carrito.getProductoId())
                                .child("comentario");
                        comentarioRef.setValue(comentario).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(mContext, "Comentario guardado", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(mContext, "Error al guardar comentario", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                } else {
                    Toast.makeText(mContext, "El comentario no puede estar vacío", Toast.LENGTH_SHORT).show();
                }
            }
        });

        holder.btnPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int adapterPosition = holder.getAdapterPosition();
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    CarritoModelo carrito = mCarritoList.get(adapterPosition);
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
                    firebaseManager.eliminarProductoDelCarrito(carrito.getProductoId(), mContext);
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
        Button btnPlus, btnMinus, btnDelete, btnGuardarComentario;
        ImageView imageViewProducto;
        EditText editTextComment;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNombreProducto = itemView.findViewById(R.id.textViewProductName);
            txtCantidad = itemView.findViewById(R.id.textViewQuantity);
            txtPrecio = itemView.findViewById(R.id.textViewProductPrice);
            btnPlus = itemView.findViewById(R.id.btnPlus);
            btnMinus = itemView.findViewById(R.id.btnMinus);
            btnDelete = itemView.findViewById(R.id.btnEliminar);
            imageViewProducto = itemView.findViewById(R.id.imgViewProductoCarrito);
            btnGuardarComentario = itemView.findViewById(R.id.btnGuardarComentario);
            editTextComment = itemView.findViewById(R.id.editTextComment);
        }
    }
}
