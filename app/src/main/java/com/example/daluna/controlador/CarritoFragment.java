package com.example.daluna.controlador;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

public class CarritoFragment extends Fragment {

    private RecyclerView recyclerView;
    private CarritoAdaptador adaptador;
    private FirebaseManager firebaseManager;
    private TextView precioTotal;
    private TextView emptyCarritoMessage;
    private ValueEventListener valueEventListener;
    private Button btnRevisarPedido;
    private EditText editTextComentario;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_carrito, container, false);

        precioTotal = view.findViewById(R.id.textViewTotal);
        emptyCarritoMessage = view.findViewById(R.id.emptyCarritoMessage);
        btnRevisarPedido = view.findViewById(R.id.btnCheckout);
        firebaseManager = new FirebaseManager();
        recyclerView = view.findViewById(R.id.recyclerViewCart);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

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
                        String comentario = carritoSnapshot.child("comentario").getValue(String.class);
                        Integer cantidad = carritoSnapshot.child("cantidad").getValue(Integer.class);
                        Double precio = carritoSnapshot.child("precio").getValue(Double.class);
                        Double precioTotal = carritoSnapshot.child("precioTotal").getValue(Double.class);

                        if (nombre != null && cantidad != null && precio != null && precioTotal != null) {
                            CarritoModelo carrito = new CarritoModelo(productoId, nombre, categoria, imagen, cantidad, precio, precioTotal, comentario);
                            carritoList.add(carrito);
                        }
                    }

                    if (carritoList.isEmpty()) {
                        emptyCarritoMessage.setVisibility(View.VISIBLE);
                        btnRevisarPedido.setEnabled(false);
                        precioTotal.setText("0.00");
                    } else {
                        emptyCarritoMessage.setVisibility(View.GONE);
                        adaptador = new CarritoAdaptador(getActivity(), carritoList, firebaseManager);
                        recyclerView.setAdapter(adaptador);
                        btnRevisarPedido.setEnabled(true);
                        double precioTotalCarrito = calcularPrecioTotal(carritoList);
                        precioTotal.setText(String.format("%.2f", precioTotalCarrito));

                        btnRevisarPedido.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                firebaseManager.obtenerUsuarioActual(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if (dataSnapshot != null) {
                                            Usuario usuario = dataSnapshot.getValue(Usuario.class);
                                            if (usuario != null) {
                                                String clienteId = usuario.getCorreo(); // Usar correo como ID del cliente
                                                String direccionEntrega = usuario.getUbicacion();
                                                String numeroPedido = carritoUsuarioRef.push().getKey(); // Generar un número de pedido único

                                                Venta venta = new Venta(numeroPedido, clienteId, carritoList, precioTotalCarrito, direccionEntrega);
                                                // Guardar la venta en la base de datos
                                                DatabaseReference ventasRef = FirebaseDatabase.getInstance().getReference("ventas");
                                                ventasRef.child(numeroPedido).setValue(venta);

                                                // Mostrar el fragmento de confirmación de pedido
                                                mostrarConfirmacionPedidoFragment(numeroPedido);
                                            }
                                        } else {
                                            Toast.makeText(getActivity(), "Error al obtener datos del usuario", Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                        Toast.makeText(getActivity(), "Error al obtener datos del usuario", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(getActivity(), "Error al obtener datos del carrito", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(getActivity(), "Usuario no autenticado", Toast.LENGTH_SHORT).show();
        }

        return view;
    }

    private double calcularPrecioTotal(List<CarritoModelo> carritoList) {
        double total = 0;
        for (CarritoModelo carrito : carritoList) {
            total += carrito.getPrecioTotalProducto();
        }
        return total;
    }

    private void mostrarConfirmacionPedidoFragment(String numeroPedido) {
        ConfirmacionPedidoFragment confirmacionPedidoFragment = new ConfirmacionPedidoFragment();
        Bundle bundle = new Bundle();
        bundle.putString("numeroPedido", numeroPedido);
        confirmacionPedidoFragment.setArguments(bundle);

        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, confirmacionPedidoFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
