package com.example.daluna.controlador;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.daluna.R;
import com.example.daluna.modelo.CarritoModelo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Carrito extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CarritoAdaptador adaptador;
    private FirebaseManager firebaseManager;
    private TextView precioTotal;
    private ValueEventListener valueEventListener;
    private boolean hasDisplayedEmptyToast = false;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carrito);
        precioTotal = findViewById(R.id.textViewTotal);
        firebaseManager = new FirebaseManager();

        recyclerView = findViewById(R.id.recyclerViewCart);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        DatabaseReference carritoUsuarioRef = firebaseManager.getDatabaseReferenceCarritoUsuarioActual();

        if (carritoUsuarioRef != null) {
            carritoUsuarioRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    List<CarritoModelo> carritoList = new ArrayList<>();
                    for (DataSnapshot carritoSnapshot : dataSnapshot.getChildren()) {
                        String productoId = carritoSnapshot.getKey();
                        String nombre = carritoSnapshot.child("nombre").getValue(String.class);
                        String categoria = carritoSnapshot.child("categoria").getValue(String.class);
                        String imagen = carritoSnapshot.child("imagen").getValue(String.class);
                        Integer cantidad = carritoSnapshot.child("cantidad").getValue(Integer.class);
                        Double precio = carritoSnapshot.child("precio").getValue(Double.class);
                        Double precioTotal = carritoSnapshot.child("precioTotal").getValue(Double.class);

                        if (nombre != null && cantidad != null && precio != null && precioTotal != null) {
                            CarritoModelo carrito = new CarritoModelo(productoId, nombre, categoria, imagen, cantidad, precio, precioTotal);
                            carritoList.add(carrito);
                        }
                    }


                    if (carritoList.isEmpty()) {
                        if (!hasDisplayedEmptyToast) {
                            Toast.makeText(Carrito.this, "No hay nada en el carrito", Toast.LENGTH_SHORT).show();
                            hasDisplayedEmptyToast = true;
                        }
                    } else {
                        adaptador = new CarritoAdaptador(Carrito.this, carritoList, firebaseManager);
                        recyclerView.setAdapter(adaptador);

                        double precioTotalCarrito = calcularPrecioTotal(carritoList);
                        precioTotal.setText(String.format("%.2f", precioTotalCarrito));
                        hasDisplayedEmptyToast = false;
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(Carrito.this, "Error al obtener datos del carrito", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, "Usuario no autenticado", Toast.LENGTH_SHORT).show();
        }
    }

    private double calcularPrecioTotal(List<CarritoModelo> carritoList) {
        double total = 0;
        for (CarritoModelo carrito : carritoList) {
            total += carrito.getPrecioTotalProducto();
        }
        return total;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, Productos.class);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DatabaseReference carritoUsuarioRef = firebaseManager.getDatabaseReferenceCarritoUsuarioActual();
        if (carritoUsuarioRef != null) {
            carritoUsuarioRef.removeEventListener(valueEventListener);
        }
    }

}
