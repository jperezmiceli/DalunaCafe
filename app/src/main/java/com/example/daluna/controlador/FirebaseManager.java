package com.example.daluna.controlador;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

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

import android.content.Context;
import android.widget.Toast;

public class FirebaseManager {
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private DatabaseReference databaseReferenceUsuarios;
    private DatabaseReference databaseReferenceProductos;
    private StorageReference storageReference;


    public FirebaseManager() {
        try {
            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
            databaseReferenceUsuarios = firebaseDatabase.getReference().child("usuarios");
            databaseReferenceProductos = firebaseDatabase.getReference().child("productos");
            firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            storageReference = FirebaseStorage.getInstance().getReference();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void uploadImage(Uri imageUri, final UploadCallback callback) {
        try {
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
        } catch (Exception e) {
            callback.onFailure(e.getMessage());
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
        try {
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            if (currentUser != null) {
                return databaseReferenceUsuarios.child(currentUser.getUid()).child("carrito");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void obtenerUsuarioActual(ValueEventListener listener) {
        try {
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
        } catch (Exception e) {
            e.printStackTrace();
            listener.onCancelled(DatabaseError.fromException(e));
        }
    }

    public void obtenerDatosUsuarioAutenticado(ValueEventListener listener) {
        try {
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
        } catch (Exception e) {
            e.printStackTrace();
            listener.onCancelled(DatabaseError.fromException(e));
        }
    }

    public void agregarProductoAlCarrito(Producto producto) {
        try {
            DatabaseReference carritoUsuarioRef = getDatabaseReferenceCarritoUsuarioActual();
            if (carritoUsuarioRef != null) {
                DatabaseReference productoRef = carritoUsuarioRef.child(producto.getId());
                productoRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            int cantidadActual = dataSnapshot.child("cantidad").getValue(Integer.class);
                            double precioUnitario = dataSnapshot.child("precio").getValue(Double.class);
                            if (cantidadActual < 10) {
                                cantidadActual += 1;
                                double precioTotalActual = cantidadActual * precioUnitario;
                                productoRef.child("cantidad").setValue(cantidadActual);
                                productoRef.child("precioTotal").setValue(precioTotalActual);
                            } else {
                                System.out.println("No se pueden agregar más de 10 unidades");
                            }
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
                    public void onCancelled(DatabaseError databaseError) {
                        // Manejo de errores
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }




    public void obtenerCantidadProductoEnCarrito(Producto producto, ObtenerCantidadProductoCallback callback) {
        try {
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            if (currentUser != null) {
                String userId = currentUser.getUid();
                DatabaseReference carritoUsuarioRef = getDatabaseReferenceUsuarios().child(userId).child("carrito").child(producto.getId()).child("cantidad");
                carritoUsuarioRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            int cantidad = dataSnapshot.getValue(Integer.class);
                            callback.onSuccess(cantidad);
                        } else {
                            // Si no existe la cantidad en el carrito, se considera como 0
                            callback.onSuccess(0);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        callback.onFailure(databaseError.getMessage());
                    }
                });
            } else {
                callback.onFailure("User not logged in");
            }
        } catch (Exception e) {
            callback.onFailure(e.getMessage());
        }
    }

    public interface ObtenerCantidadProductoCallback {
        void onSuccess(int cantidad);
        void onFailure(String errorMessage);
    }

    public String obtenerCorreoUsuarioAutenticado() {
        String correoUsuario = null;
        try {
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            if (currentUser != null) {
                correoUsuario = currentUser.getEmail();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return correoUsuario;
    }



    public void leerCarritoUsuarioActual(ValueEventListener listener) {
        try {
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

                            CarritoModelo carrito = new CarritoModelo(productoId, nombre, categoria, imagen, cantidad, precio, precioTotal, comentario);
                        }

                        listener.onDataChange(dataSnapshot);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        listener.onCancelled(databaseError);
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            listener.onCancelled(DatabaseError.fromException(e));
        }
    }

    public boolean agregarCantidadAlCarrito(String productoId, CantidadAgregadaCallback errorAlAgregarCantidad) {
        final boolean[] cantidadAgregada = {false};
        try {
            DatabaseReference carritoUsuarioRef = getDatabaseReferenceCarritoUsuarioActual();
            if (carritoUsuarioRef != null) {
                DatabaseReference productoRef = carritoUsuarioRef.child(productoId);
                productoRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            int cantidadActual = dataSnapshot.child("cantidad").getValue(Integer.class);
                            double precioUnitario = dataSnapshot.child("precio").getValue(Double.class);
                            if (cantidadActual < 10) {
                                cantidadActual++;
                                double precioTotalActual = cantidadActual * precioUnitario;
                                productoRef.child("cantidad").setValue(cantidadActual);
                                productoRef.child("precioTotal").setValue(precioTotalActual);
                                cantidadAgregada[0] = true;
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Manejo de errores
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cantidadAgregada[0];
    }
    public interface CantidadAgregadaCallback {
        void onSuccess(boolean cantidadAgregada);

        void onFailure(Exception e);
    }



    public void quitarCantidadAlCarrito(String productoId) {
        try {
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void eliminarProductoDelCarrito(String productoId, Context context) {
        try {
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void obtenerPedidos(ValueEventListener listener) {
        try {
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            if (currentUser != null) {
                DatabaseReference pedidosRef = databaseReferenceUsuarios.child(currentUser.getUid()).child("ventas");
                pedidosRef.addListenerForSingleValueEvent(listener);
            }
        } catch (Exception e) {
            e.printStackTrace();
            listener.onCancelled(DatabaseError.fromException(e));
        }
    }

    public void obtenerUrlsDeImagenes(String carpeta, final ObtenerUrlsCallback callback) {
        try {
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
        } catch (Exception e) {
            e.printStackTrace();
            callback.onFailure(e.getMessage());
        }
    }

    public interface ObtenerUrlsCallback {
        void onSuccess(List<String> urls);

        void onFailure(String errorMessage);
    }
    public void esAdmin(final EsAdminCallback callback) {
        try {
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            if (currentUser != null) {
                String userId = currentUser.getUid();
                DatabaseReference usuarioRef = databaseReferenceUsuarios.child(userId).child("role");
                usuarioRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            String role = dataSnapshot.getValue(String.class);
                            if ("admin".equals(role)) {
                                callback.onResult(true);
                            } else {
                                callback.onResult(false);
                            }
                        } else {
                            callback.onResult(false);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        callback.onResult(false);
                    }
                });
            } else {
                callback.onResult(false);
            }
        } catch (Exception e) {
            e.printStackTrace();
            callback.onResult(false);
        }
    }

    public interface EsAdminCallback {
        void onResult(boolean isAdmin);
    }
    public void eliminarVentaGlobal(String ventaId) {
        try {
            DatabaseReference ventaGlobalRef = FirebaseDatabase.getInstance().getReference().child("ventas").child(ventaId);
            ventaGlobalRef.removeValue();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void eliminarVentaDeUsuario(String userId, String ventaId) {
        try {
            DatabaseReference ventaUsuarioRef = FirebaseDatabase.getInstance().getReference().child("usuarios").child(userId).child("ventas").child(ventaId);
            ventaUsuarioRef.removeValue();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
