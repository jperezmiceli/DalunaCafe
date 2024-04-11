package com.example.daluna.controlador;

import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.daluna.R;
import com.example.daluna.modelo.Producto;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Productos extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProductoAdaptador adaptador;
    private List<Producto> listaProductos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_productos);

        // Configurar EdgeToEdge
        EdgeToEdge.enable(this);

        // Configurar el RecyclerView
        recyclerView = findViewById(R.id.rvprod);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Inicializar la lista de productos
        listaProductos = new ArrayList<>();

        // Configurar adaptador
        adaptador = new ProductoAdaptador(this, listaProductos);
        recyclerView.setAdapter(adaptador);

        // Obtener referencia a la base de datos Firebase
        DatabaseReference productosRef = FirebaseDatabase.getInstance().getReference("productos");

        // Leer los productos de Firebase Database
        productosRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listaProductos.clear(); // Limpiar la lista antes de cargar nuevos productos
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Producto producto = snapshot.getValue(Producto.class);
                    listaProductos.add(producto);
                }
                adaptador.notifyDataSetChanged(); // Notificar al adaptador que los datos han cambiado
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Productos", "Error al leer productos de Firebase Database: " + databaseError.getMessage());
            }
        });
    }
}
