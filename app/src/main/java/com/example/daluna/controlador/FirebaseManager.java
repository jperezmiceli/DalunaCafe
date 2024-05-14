package com.example.daluna.controlador;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.daluna.modelo.CarritoModelo;
import com.example.daluna.modelo.Producto;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FirebaseManager {
    private DatabaseReference databaseReferenceUsuarios;
    private DatabaseReference databaseReferenceProductos;

    public FirebaseManager() {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReferenceUsuarios = firebaseDatabase.getReference().child("usuarios");
        databaseReferenceProductos = firebaseDatabase.getReference().child("productos");
    }

    public DatabaseReference getDatabaseReferenceUsuarios() {
        return databaseReferenceUsuarios;
    }

    public DatabaseReference getDatabaseReferenceProductos() {
        return databaseReferenceProductos;
    }

    public DatabaseReference getDatabaseReferenceCarritoUsuarioActual() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            return databaseReferenceUsuarios.child(currentUser.getUid()).child("carrito");
        }
        return null;
    }

    public void agregarProductoAlCarrito(Producto producto) {
        DatabaseReference carritoUsuarioRef = getDatabaseReferenceCarritoUsuarioActual();
        if (carritoUsuarioRef != null) {
            DatabaseReference productoRef = carritoUsuarioRef.child(producto.getId());
            productoRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        int cantidadActual = dataSnapshot.child("cantidad").getValue(Integer.class);
                        double precioUnitario = dataSnapshot.child("precio").getValue(Double.class);
                        cantidadActual += 1;
                        double precioTotalActual = cantidadActual * precioUnitario;
                        productoRef.child("cantidad").setValue(cantidadActual);
                        productoRef.child("precioTotal").setValue(precioTotalActual);
                    } else {
                        double precioUnitario = producto.getPrecio();
                        productoRef.child("nombre").setValue(producto.getNombre());
                        productoRef.child("categoria").setValue(producto.getCategoria());
                        productoRef.child("imagen").setValue(producto.getImagen());
                        productoRef.child("cantidad").setValue(1);
                        productoRef.child("precio").setValue(precioUnitario);
                        productoRef.child("precioTotal").setValue(precioUnitario);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Manejo de errores
                }
            });
        }
    }

    public void leerCarritoUsuarioActual(ValueEventListener listener) {
        DatabaseReference carritoUsuarioRef = getDatabaseReferenceCarritoUsuarioActual();
        if (carritoUsuarioRef != null) {
            carritoUsuarioRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot carritoSnapshot : dataSnapshot.getChildren()) {
                        String productoId = carritoSnapshot.getKey();
                        String nombre = carritoSnapshot.child("nombre").getValue(String.class);
                        String categoria = carritoSnapshot.child("categoria").getValue(String.class);
                        String imagen = carritoSnapshot.child("imagen").getValue(String.class);
                        int cantidad = carritoSnapshot.child("cantidad").getValue(Integer.class);
                        double precio = carritoSnapshot.child("precio").getValue(Double.class);
                        double precioTotal = carritoSnapshot.child("precioTotal").getValue(Double.class);

                        CarritoModelo carrito = new CarritoModelo(productoId, nombre, categoria, imagen, cantidad, precio, precioTotal);
                    }

                    listener.onDataChange(dataSnapshot);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    listener.onCancelled(databaseError);
                }
            });
        }
    }

    public void agregarCantidadAlCarrito(String productoId) {
        DatabaseReference carritoUsuarioRef = getDatabaseReferenceCarritoUsuarioActual();
        if (carritoUsuarioRef != null) {
            DatabaseReference productoRef = carritoUsuarioRef.child(productoId);
            productoRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        int cantidadActual = dataSnapshot.child("cantidad").getValue(Integer.class);
                        double precioUnitario = dataSnapshot.child("precio").getValue(Double.class);
                        cantidadActual++;
                        double precioTotalActual = cantidadActual * precioUnitario;
                        productoRef.child("cantidad").setValue(cantidadActual);
                        productoRef.child("precioTotal").setValue(precioTotalActual);
                    } else {
                        // El producto no existe en el carrito
                        // Aquí podrías manejar esta situación si lo consideras necesario
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Manejo de errores
                }
            });
        }
    }

    public void quitarCantidadAlCarrito(String productoId) {
        DatabaseReference carritoUsuarioRef = getDatabaseReferenceCarritoUsuarioActual();
        if (carritoUsuarioRef != null) {
            DatabaseReference productoRef = carritoUsuarioRef.child(productoId);
            productoRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        int cantidadActual = dataSnapshot.child("cantidad").getValue(Integer.class);
                        double precioUnitario = dataSnapshot.child("precio").getValue(Double.class);
                        // Verificar que la cantidad actual no sea menor o igual a cero
                        if (cantidadActual > 1) {
                            cantidadActual--;
                            double precioTotalActual = cantidadActual * precioUnitario;
                            productoRef.child("cantidad").setValue(cantidadActual);
                            productoRef.child("precioTotal").setValue(precioTotalActual);
                        } else {
                            // La cantidad actual ya es cero, no se puede quitar más
                            // Aquí podrías manejar esta situación si lo consideras necesario
                        }
                    } else {
                        // El producto no existe en el carrito
                        // Aquí podrías manejar esta situación si lo consideras necesario
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Manejo de errores
                }
            });
        }
    }

    public void eliminarProductoDelCarrito(String productoId, Context context) {
        DatabaseReference carritoUsuarioRef = getDatabaseReferenceCarritoUsuarioActual();
        if (carritoUsuarioRef != null) {
            DatabaseReference productoRef = carritoUsuarioRef.child(productoId);
            productoRef.removeValue(new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                    if (databaseError != null) {
                        // Manejo de error al eliminar el producto del carrito
                    } else {
                        // Producto eliminado exitosamente del carrito
                        carritoUsuarioRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (!dataSnapshot.exists()) {
                                    // El carrito está vacío, redirigir a la actividad anterior
                                    Intent intent = new Intent(context, Productos.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    context.startActivity(intent);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                // Manejo de errores
                            }
                        });
                    }
                }
            });
        }
    }
}
