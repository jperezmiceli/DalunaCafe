package com.example.daluna.fragments;

import android.os.Bundle;
import android.util.Log;
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
import com.example.daluna.adaptadores.CarritoAdaptador;
import com.example.daluna.controlador.FirebaseManager;
import com.example.daluna.modelo.CarritoModelo;
import com.example.daluna.modelo.Usuario;
import com.example.daluna.modelo.Venta;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
    private Button btnRevisarPedido;
    private EditText editTextComentario;
    private List<CarritoModelo> carritoList;
    private double precioTotalCarrito;
    private FirebaseUser firebaseUser;
    private FirebaseAuth firebaseAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_carrito, container, false);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        precioTotal = view.findViewById(R.id.textViewTotal);
        emptyCarritoMessage = view.findViewById(R.id.emptyCarritoMessage);
        btnRevisarPedido = view.findViewById(R.id.btnCheckout);
        firebaseManager = new FirebaseManager();
        recyclerView = view.findViewById(R.id.recyclerViewCart);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        carritoList = new ArrayList<>(); // Inicializar carritoList aquí

        // Configurar el adaptador antes de asignar el ValueEventListener
        adaptador = new CarritoAdaptador(getActivity(), carritoList, firebaseManager);
        recyclerView.setAdapter(adaptador);

        DatabaseReference carritoUsuarioRef = firebaseManager.getDatabaseReferenceCarritoUsuarioActual();


        if (carritoUsuarioRef != null) {
            carritoUsuarioRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    carritoList.clear(); // Limpiar la lista antes de añadir nuevos elementos
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
                        adaptador.notifyDataSetChanged(); // Notificar cambios al adaptador
                        btnRevisarPedido.setEnabled(true);
                        precioTotalCarrito = calcularPrecioTotal(carritoList);
                        precioTotal.setText(String.format("%.2f", precioTotalCarrito));
                        Log.d("CarritoFragment", "Botón Revisar Pedido habilitado: " + btnRevisarPedido.isEnabled());
                        Log.d("CarritoFragment", "Tamaño de carritoList: " + carritoList.size());
                        Log.d("CarritoFragment", "Precio total del carrito: " + precioTotalCarrito);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(getActivity(), "Error al obtener datos del carrito", Toast.LENGTH_SHORT).show();
                }
            });

            btnRevisarPedido.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    firebaseManager.obtenerDatosUsuarioAutenticado(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                Usuario usuario = dataSnapshot.getValue(Usuario.class);
                                if (usuario != null) {
                                    if (usuario.getCalle() != null && !usuario.getCalle().isEmpty() &&
                                            usuario.getNumeroCalle() != null && !usuario.getNumeroCalle().isEmpty() &&
                                            usuario.getPueblo() != null && !usuario.getPueblo().isEmpty() &&
                                            usuario.getCiudad() != null && !usuario.getCiudad().isEmpty()) {

                                        String domicilio = usuario.getCalle() + " " + usuario.getNumeroCalle() + ", " + usuario.getPiso() + ", " + usuario.getPueblo() + ", " + usuario.getCiudad();
                                        Log.d("Domicilio", domicilio);
                                        String direccionEntrega = domicilio;

                                        // Generar un número de pedido de tres dígitos aleatorio
                                        int numeroPedidoInt = (int) (Math.random() * 900) + 100;
                                        String numeroPedido = String.valueOf(numeroPedidoInt);

                                        Venta venta = new Venta(numeroPedido, firebaseUser.getUid(), carritoList, precioTotalCarrito, direccionEntrega);
                                        // Guardar la venta en la base de datos
                                        DatabaseReference ventasRef = FirebaseDatabase.getInstance().getReference("ventas");
                                        DatabaseReference usuarioVentasRef = FirebaseDatabase.getInstance().getReference("usuarios").child(venta.getClienteId()).child("ventas");
                                        ventasRef.child(numeroPedido).setValue(venta);
                                        usuarioVentasRef.child(numeroPedido).setValue(venta);

                                        // Vaciar el carrito
                                        carritoUsuarioRef.removeValue();

                                        // Mostrar Toast con mensaje
                                        Toast.makeText(getActivity(), "Pedido añadido a mis pedidos", Toast.LENGTH_SHORT).show();

                                        // Iniciar el PedidosFragment
                                        Fragment pedidosFragment = new PedidosFragment();
                                        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                                        transaction.replace(R.id.fragment_container, pedidosFragment);
                                        transaction.addToBackStack(null);
                                        transaction.commit();

                                    } else {
                                        Toast.makeText(getActivity(), "Por favor, complete su dirección en el perfil", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toast.makeText(getActivity(), "Error al obtener datos del usuario", Toast.LENGTH_SHORT).show();
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

        return view;
    }


    private double calcularPrecioTotal(List<CarritoModelo> carritoList) {
        double total = 0.0;
        for (CarritoModelo carrito : carritoList) {
            total += carrito.getPrecioTotalProducto();
        }
        return total;
    }
}
