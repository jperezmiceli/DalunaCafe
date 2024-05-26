package com.example.daluna;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.daluna.controlador.PedidosFragment;
import com.example.daluna.controlador.PerfilUsuario;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import com.example.daluna.controlador.HomeFragment;
import com.example.daluna.controlador.ProductosFragment;
import com.example.daluna.controlador.CarritoFragment;

public class Principal extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private ImageView perfilbotonproductos;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_principal);

        //boton perfil
        perfilbotonproductos = findViewById(R.id.perfilbotonproductos);

        // Configurar BottomNavigationView
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(navItemSelectedListener);

        // Cargar ProductosFragment por defecto
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainer, new ProductosFragment())
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit();
        perfilbotonproductos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Principal.this, PerfilUsuario.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private BottomNavigationView.OnItemSelectedListener navItemSelectedListener =
            new BottomNavigationView.OnItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(MenuItem item) {
                    Fragment selectedFragment = null;
                    int itemId = item.getItemId();

                    if (itemId == R.id.action_home) {
                        // Cargar el fragmento correspondiente al inicio
                        selectedFragment = new Inicio();
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

                    return false;
                }

            };

//    private View.OnClickListener profileClickListener = new View.OnClickListener() {
//        @Override
//        public void onClick(View v) {
//            // Cargar el fragmento de PerfilUsuario
//            Fragment selectedFragment = new PerfilUsuario();
//
//            // Reemplazar el fragmento en el contenedor FrameLayout
//            getSupportFragmentManager().beginTransaction()
//                    .replace(R.id.fragmentContainer, selectedFragment)
//                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
//                    .commit();
//        }
//    };
}
