package com.example.daluna.controlador;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.daluna.Principal;
import com.example.daluna.modelo.CarritoModelo;
import com.example.daluna.modelo.Producto;
import com.example.daluna.modelo.Usuario;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;

public class FirebaseManager {
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private DatabaseReference databaseReferenceUsuarios;
    private DatabaseReference databaseReferenceProductos;
    private StorageReference storageReference;

    public FirebaseManager() {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReferenceUsuarios = firebaseDatabase.getReference().child("usuarios");
        databaseReferenceProductos = firebaseDatabase.getReference().child("productos");
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        storageReference = FirebaseStorage.getInstance().getReference();
    }

    public void uploadImage(Uri imageUri, final UploadCallback callback) {
        if (imageUri != null) {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                String userId = user.getUid();
                StorageReference fileReference = storageReference.child("images/" + userId + "/" + System.currentTimeMillis() + ".jpg");

                fileReference.putFile(imageUri)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                callback.onSuccess();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                callback.onFailure(e.getMessage());
                            }
                        });
            } else {
                callback.onFailure("User not logged in");
            }
        } else {
            callback.onFailure("No file selected");
        }
    }


    public interface UploadCallback {
        void onSuccess();
        void onFailure(String errorMessage);
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
    public void obtenerUsuarioActual(ValueEventListener listener) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            DatabaseReference usuarioRef = databaseReferenceUsuarios.child(currentUser.getUid());
            usuarioRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        Usuario usuario = dataSnapshot.getValue(Usuario.class);
                        listener.onDataChange(dataSnapshot);
                    } else {
                        // El usuario no existe en la base de datos
                        listener.onDataChange(null);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    listener.onCancelled(databaseError);
                }
            });
        } else {
            // El usuario no está autenticado
            listener.onDataChange(null);
        }
    }
    public void obtenerDatosUsuarioAutenticado(ValueEventListener listener) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            DatabaseReference usuarioRef = databaseReferenceUsuarios.child(userId);
            usuarioRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        Usuario usuario = dataSnapshot.getValue(Usuario.class);
                        listener.onDataChange(dataSnapshot);
                    } else {
                        // El usuario no existe en la base de datos
                        listener.onDataChange(null);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    listener.onCancelled(databaseError);
                }
            });
        } else {
            // El usuario no está autenticado
            listener.onDataChange(null);
        }
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
                        productoRef.child("comentario");
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
                        String comentario = carritoSnapshot.child("comentario").getValue(String.class);

                        CarritoModelo carrito = new CarritoModelo(productoId, nombre, categoria, imagen, cantidad, precio, precioTotal,comentario);
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
                                    Intent intent = new Intent(context, Principal.class);
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
    public void obtenerUrlsDeImagenes(String carpeta, final ObtenerUrlsCallback callback) {
        StorageReference carpetaRef = storageReference.child(carpeta);
        carpetaRef.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult) {
                List<String> urls = new ArrayList<>();
                for (StorageReference item : listResult.getItems()) {
                    item.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            urls.add(uri.toString());
                            if (urls.size() == listResult.getItems().size()) {
                                callback.onSuccess(urls);
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            callback.onFailure(e.getMessage());
                        }
                    });
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                callback.onFailure(e.getMessage());
            }
        });
    }

    public interface ObtenerUrlsCallback {
        void onSuccess(List<String> urls);
        void onFailure(String errorMessage);
    }

}
