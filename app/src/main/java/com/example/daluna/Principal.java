package com.example.daluna;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.daluna.controlador.Carrito;
import com.example.daluna.controlador.PerfilUsuario;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class Principal extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_principal);

        // Configurar BottomNavigationView
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(navItemSelectedListener);

        // Configurar OnClickListener para el perfil
        findViewById(R.id.perfilbotonproductos).setOnClickListener(profileClickListener);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navItemSelectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(MenuItem item) {
                    Fragment selectedFragment = null;

                    switch (item.getItemId()) {
                        case R.id.action_home:
                            // Cargar el fragmento correspondiente al inicio
                            selectedFragment = new HomeFragment();
                            break;
                        case R.id.action_productos:
                            // Cargar el fragmento correspondiente a los productos
                            selectedFragment = new ProductosFragment();
                            break;
                        case R.id.action_pedidos:
                            // Cargar el fragmento correspondiente a los pedidos
                            selectedFragment = new PedidosFragment();
                            break;
                        case R.id.action_carrito:
                            selectedFragment = new Carrito();
                            break;

                    }

                    // Reemplazar el fragmento en el contenedor FrameLayout
                    if (selectedFragment != null) {
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragmentContainer, selectedFragment)
                                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                                .commit();
                    }

                    return true;
                }
            };

    private View.OnClickListener profileClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // Cargar el fragmento de PerfilUsuario
            Fragment selectedFragment = new Fragment();

            // Reemplazar el fragmento en el contenedor FrameLayout
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainer, selectedFragment)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .commit();
        }
    };
}
