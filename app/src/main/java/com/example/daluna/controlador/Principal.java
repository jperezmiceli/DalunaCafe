package com.example.daluna.controlador;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.example.daluna.fragments.InicioFragment;
import com.example.daluna.R;
import com.example.daluna.fragments.CarritoFragment;
import com.example.daluna.fragments.PedidosFragment;
import com.example.daluna.fragments.ProductosFragment;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class Principal extends AppCompatActivity {

    private final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

    private BottomNavigationView bottomNavigationView;
    private ImageView perfilbotonproductos;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            EdgeToEdge.enable(this);
            setContentView(R.layout.activity_principal);

            // Inicializar vistas
            perfilbotonproductos = findViewById(R.id.perfilbotonproductos);
            bottomNavigationView = findViewById(R.id.bottom_navigation);

            // Configurar BottomNavigationView
            bottomNavigationView.setOnItemSelectedListener(navItemSelectedListener);

            // Cargar ProductosFragment por defecto
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainer, new ProductosFragment())
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .commit();

            perfilbotonproductos.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        if(firebaseUser!=null){
                            // Abrir la actividad del perfil de usuario
                            Intent intent = new Intent(Principal.this, PerfilUsuario.class);
                            startActivity(intent);
                        }else{
                            Intent intent = new Intent(Principal.this, InicioSesion.class);
                            startActivity(intent);
                            finish();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });


            // Botón atrás
            OnBackPressedCallback callback = new OnBackPressedCallback(true) {
                @Override
                public void handleOnBackPressed() {
                    onBackPressed();
                }
            };
            getOnBackPressedDispatcher().addCallback(this, callback);

            // Cargar la imagen del perfil del usuario utilizando Glide
            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            if (firebaseUser != null) {
                String uid = firebaseUser.getUid();
                StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("imgusuarios/" + uid + ".jpg");
                storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        if (!isFinishing() && !isDestroyed()) {
                            // Glide para cargar y mostrar la imagen de perfil circular
                            Glide.with(Principal.this)
                                    .load(uri)
                                    .placeholder(R.drawable.usuario)
                                    .error(R.drawable.usuario)
                                    .circleCrop()
                                    .into(perfilbotonproductos); // Cambiar a la referencia correcta del ImageView
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        e.printStackTrace();
                    }
                });
            } else {
                // No hay usuario autenticado, hacer algo en consecuencia
            }

        } catch (Exception e) {
            e.printStackTrace();
            // Mostrar un mensaje al usuario sobre el error
        }
    }


    private BottomNavigationView.OnItemSelectedListener navItemSelectedListener =
            new BottomNavigationView.OnItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(MenuItem item) {
                    Fragment selectedFragment = null;
                    int itemId = item.getItemId();

                    try {
                        if (itemId == R.id.action_home) {
                            // Cargar el fragmento correspondiente al inicio
                            selectedFragment = new InicioFragment();
                        } else if (itemId == R.id.action_productos) {
                            // Cargar el fragmento correspondiente a los productos
                            selectedFragment = new ProductosFragment();
                        } else if (itemId == R.id.action_pedidos) {
                            // Cargar el fragmento correspondiente a los pedidos
                            selectedFragment = new PedidosFragment();
                        } else if (itemId == R.id.action_carrito) {
                            // Cargar el fragmento correspondiente al carrito
                            selectedFragment = new CarritoFragment();
                        }

                        // Reemplazar el fragmento en el contenedor FrameLayout
                        if (selectedFragment != null) {
                            getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.fragmentContainer, selectedFragment)
                                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                                    .commit();
                            return true;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    return false;
                }
            };
}
