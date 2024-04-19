package com.example.daluna.controlador;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.daluna.R;
import com.example.daluna.modelo.Producto;
import com.example.daluna.modelo.Usuario;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Productos extends AppCompatActivity {

    private TextView puntosUsuario;
    private RecyclerView recyclerViewcafe;
    private RecyclerView recyclerViewtes;
    private ProductoAdaptador adaptadorTeInfusiones;
    private ProductoAdaptador adaptadorCafe;
    private List<Producto> listaProductosCafe;
    private List<Producto> listaProductos;
    private List<Producto> listaProductosTeInfusiones;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private FirebaseUser firebaseUser;
    private String idUsuario = "";

    public Productos() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_productos);
        idUsuario = getIntent().getStringExtra("id");

        puntosUsuario = findViewById(R.id.tuspuntos);
        //firebase
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("usuarios");

        databaseReference.child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String uid = ""+snapshot.child("uid").getValue();
                    String nombre = ""+snapshot.child("nombre").getValue();
                    String apellidos = ""+snapshot.child("apellidos").getValue();
                    String correo = ""+snapshot.child("correo").getValue();
                    String puntos = ""+snapshot.child("puntos").getValue();
                    puntosUsuario.setText(puntos);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        // Inicializar la lista de productos
        listaProductosCafe = new ArrayList<>();
        listaProductos = new ArrayList<>();
        listaProductosTeInfusiones = new ArrayList<>();

        // Configurar el RecyclerView
        recyclerViewcafe = findViewById(R.id.rvprodcafes);
        recyclerViewtes = findViewById(R.id.rvprodtes);
        recyclerViewcafe.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerViewtes.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        // Configurar adaptador
        adaptadorCafe = new ProductoAdaptador(this, listaProductosCafe);
        adaptadorTeInfusiones = new ProductoAdaptador(this, listaProductosTeInfusiones);
        recyclerViewcafe.setAdapter(adaptadorCafe);
        recyclerViewtes.setAdapter(adaptadorTeInfusiones);

        // Obtener referencia a la base de datos Firebase y la ubicación de los productos
        DatabaseReference productosRef = FirebaseDatabase.getInstance().getReference().child("productos");

        // Agregar un ValueEventListener para leer los datos de los productos
        productosRef.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listaProductosCafe.clear(); // Limpiar la lista de productos de café antes de agregar nuevos
                listaProductosTeInfusiones.clear(); // Limpiar la lista de productos de té/infusiones antes de agregar nuevos
                for (DataSnapshot productoSnapshot : dataSnapshot.getChildren()) {
                    Producto producto = productoSnapshot.getValue(Producto.class);
                    listaProductos.add(producto);
                }
                listaProductosCafe.addAll(filtrarProductosPorCategoria(listaProductos,"cafe")); // Agregar productos de café filtrados
                listaProductosTeInfusiones.addAll(filtrarProductosPorCategoria(listaProductos,"TeInfusiones")); // Agregar productos de té/infusiones filtrados
                adaptadorCafe.notifyDataSetChanged(); // Notificar al adaptador de café que los datos han cambiado
                adaptadorTeInfusiones.notifyDataSetChanged(); // Notificar al adaptador de té/infusiones que los datos han cambiado
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(Productos.this, "Error al cargar los productos", Toast.LENGTH_SHORT).show();
            }
        });
    }
    // Método para filtrar productos por categoría
    private List<Producto> filtrarProductosPorCategoria(List<Producto> listaProductos, String categoria) {
        List<Producto> productosFiltrados = new ArrayList<>();
        for (Producto producto : listaProductos) {
            if (producto.getCategoria().equals(categoria)) {
                productosFiltrados.add(producto);
            }
        }
        return productosFiltrados;
    }
}
