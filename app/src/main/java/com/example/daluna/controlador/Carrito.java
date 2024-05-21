package com.example.daluna.controlador;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.daluna.R;
import com.example.daluna.modelo.CarritoModelo;
import com.example.daluna.modelo.Usuario;
import com.example.daluna.modelo.Venta;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Carrito extends AppCompatActivity  {

//    private RecyclerView recyclerView;
//    private CarritoAdaptador adaptador;
//    private FirebaseManager firebaseManager;
//    private TextView precioTotal;
//    private ValueEventListener valueEventListener;
//    private boolean hasDisplayedEmptyToast = false;
//    private Button btnRevisarPedido;
//    private EditText editTextComentario;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_carrito);
//        precioTotal = findViewById(R.id.textViewTotal);
//        btnRevisarPedido = findViewById(R.id.btnCheckout);
//        firebaseManager = new FirebaseManager();
//        recyclerView = findViewById(R.id.recyclerViewCart);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//
//        DatabaseReference carritoUsuarioRef = firebaseManager.getDatabaseReferenceCarritoUsuarioActual();
//
//        if (carritoUsuarioRef != null) {
//            carritoUsuarioRef.addValueEventListener(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                    List<CarritoModelo> carritoList = new ArrayList<>();
//                    for (DataSnapshot carritoSnapshot : dataSnapshot.getChildren()) {
//                        String productoId = carritoSnapshot.getKey();
//                        String nombre = carritoSnapshot.child("nombre").getValue(String.class);
//                        String categoria = carritoSnapshot.child("categoria").getValue(String.class);
//                        String imagen = carritoSnapshot.child("imagen").getValue(String.class);
//                        String comentario = carritoSnapshot.child("comentario").getValue(String.class);
//                        Integer cantidad = carritoSnapshot.child("cantidad").getValue(Integer.class);
//                        Double precio = carritoSnapshot.child("precio").getValue(Double.class);
//                        Double precioTotal = carritoSnapshot.child("precioTotal").getValue(Double.class);
//
//                        if (nombre != null && cantidad != null && precio != null && precioTotal != null) {
//                            CarritoModelo carrito = new CarritoModelo(productoId, nombre, categoria, imagen, cantidad, precio, precioTotal, comentario);
//                            carritoList.add(carrito);
//                        }
//                    }
//
//                    if (carritoList.isEmpty()) {
//                        if (!hasDisplayedEmptyToast) {
//                            Toast.makeText(Carrito.this, "No hay nada en el carrito", Toast.LENGTH_SHORT).show();
//                            hasDisplayedEmptyToast = true;
//                        }
//                    } else {
//                        adaptador = new CarritoAdaptador(Carrito.this, carritoList, firebaseManager);
//                        recyclerView.setAdapter(adaptador);
//                        btnRevisarPedido.setVisibility(View.VISIBLE);
//                        double precioTotalCarrito = calcularPrecioTotal(carritoList);
//                        precioTotal.setText(String.format("%.2f", precioTotalCarrito));
//                        hasDisplayedEmptyToast = false;
//
//                        btnRevisarPedido.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                firebaseManager.obtenerUsuarioActual(new ValueEventListener() {
//                                    @Override
//                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                                        if (dataSnapshot != null) {
//                                            Usuario usuario = dataSnapshot.getValue(Usuario.class);
//                                            if (usuario != null) {
//                                                String clienteId = usuario.getCorreo(); // Usar correo como ID del cliente
//                                                String direccionEntrega = usuario.getUbicacion();
//                                                String numeroPedido = carritoUsuarioRef.push().getKey(); // Generar un número de pedido único
//
//                                                Venta venta = new Venta(numeroPedido, clienteId, carritoList, precioTotalCarrito, direccionEntrega);
//                                                // Guardar la venta en la base de datos
//                                                DatabaseReference ventasRef = FirebaseDatabase.getInstance().getReference("ventas");
//                                                ventasRef.child(numeroPedido).setValue(venta);
//
//                                                // Mostrar la confirmación de pedido en un fragmento
//                                                mostrarConfirmacionPedidoFragment(numeroPedido);
//                                            }
//                                        } else {
//                                            Toast.makeText(Carrito.this, "Error al obtener datos del usuario", Toast.LENGTH_SHORT).show();
//                                        }
//                                    }
//
//                                    @Override
//                                    public void onCancelled(@NonNull DatabaseError databaseError) {
//                                        Toast.makeText(Carrito.this, "Error al obtener datos del usuario", Toast.LENGTH_SHORT).show();
//                                    }
//                                });
//                            }
//                        });
//                    }
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError databaseError) {
//                    Toast.makeText(Carrito.this, "Error al obtener datos del carrito", Toast.LENGTH_SHORT).show();
//                }
//            });
//        } else {
//            Toast.makeText(this, "Usuario no autenticado", Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    private double calcularPrecioTotal(List<CarritoModelo> carritoList) {
//        double total = 0;
//        for (CarritoModelo carrito : carritoList) {
//            total += carrito.getPrecioTotalProducto();
//        }
//        return total;
//    }
//
//    private void mostrarConfirmacionPedidoFragment(String numeroPedido) {
//        Fragment confirmacionPedidoFragment = new ConfirmacionPedidoFragment();
//        Bundle bundle = new Bundle();
//        bundle.putString("numeroPedido", numeroPedido);
//        confirmacionPedidoFragment.setArguments(bundle);
//
//        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//        transaction.replace(R.id.fragment_container, confirmacionPedidoFragment);
//        transaction.addToBackStack(null);
//        transaction.commit();
//
//        // Ocultar el carrito y mostrar el fragmento de confirmación de pedido
//        findViewById(R.id.recyclerViewCart).setVisibility(View.GONE);
//        findViewById(R.id.textViewTotal).setVisibility(View.GONE);
//        findViewById(R.id.btnCheckout).setVisibility(View.GONE);
//        findViewById(R.id.fragment_container).setVisibility(View.VISIBLE);
//    }
//
//    @Override
//    public void onBackPressed() {
//        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
//            getSupportFragmentManager().popBackStack();
//            // Mostrar el carrito de nuevo
//            findViewById(R.id.recyclerViewCart).setVisibility(View.VISIBLE);
//            findViewById(R.id.textViewTotal).setVisibility(View.VISIBLE);
//            findViewById(R.id.btnCheckout).setVisibility(View.VISIBLE);
//            findViewById(R.id.fragment_container).setVisibility(View.GONE);
//        } else {
//            super.onBackPressed();
//            Intent intent = new Intent(this, Productos.class);
//            startActivity(intent);
//        }
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        DatabaseReference carritoUsuarioRef = firebaseManager.getDatabaseReferenceCarritoUsuarioActual();
//        if (carritoUsuarioRef != null) {
//            carritoUsuarioRef.removeEventListener(valueEventListener);
//        }
//    }
}
