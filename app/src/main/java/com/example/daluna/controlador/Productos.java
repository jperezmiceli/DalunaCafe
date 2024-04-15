package com.example.daluna.controlador;

import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
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

        // Inicializar la lista de productos
        listaProductos = new ArrayList<>();

        // Configurar el RecyclerView
        recyclerView = findViewById(R.id.rvprod);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Configurar adaptador
        adaptador = new ProductoAdaptador(this, listaProductos);
        recyclerView.setAdapter(adaptador);

        // Obtener referencia a la base de datos Firebase y la ubicación de los productos
        DatabaseReference productosRef = FirebaseDatabase.getInstance().getReference().child("productos");

        // Agregar un ValueEventListener para leer los datos de los productos
        productosRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listaProductos.clear(); // Limpiar la lista de productos antes de agregar nuevos
                for (DataSnapshot productoSnapshot : dataSnapshot.getChildren()) {
                    Producto producto = productoSnapshot.getValue(Producto.class);
                    listaProductos.add(producto);
                }
                adaptador.notifyDataSetChanged(); // Notificar al adaptador que los datos han cambiado
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(Productos.this, "Error al cargar los productos", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
