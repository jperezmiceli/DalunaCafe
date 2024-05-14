package com.example.daluna.controlador;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.daluna.R;

import com.example.daluna.modelo.Producto;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.util.ArrayList;
import java.util.List;

public class Productos extends AppCompatActivity implements ProductoAdaptador.OnItemClickListener {


    private TextView puntosUsuario;
    private LinearLayout linearCarrito;

    // RecyclerViews
    private RecyclerView recyclerViewCafe;
    private RecyclerView recyclerViewTeInfusiones;
    private RecyclerView recyclerViewDesayunos;
    private RecyclerView recyclerViewBrunch;
    private RecyclerView recyclerViewZumoSmuthie;
    private RecyclerView recyclerViewBebidas;
    private RecyclerView recyclerViewVinoCerveza;

    // Adaptadores
    private ProductoAdaptador adaptadorCafe;
    private ProductoAdaptador adaptadorTeInfusiones;
    private ProductoAdaptador adaptadorDesayunos;
    private ProductoAdaptador adaptadorBrunch;
    private ProductoAdaptador adaptadorZumoSmuthie;
    private ProductoAdaptador adaptadorBebidas;
    private ProductoAdaptador adaptadorVinoCerveza;

    // Listas de productos
    private List<Producto> listaProductos;
    private List<Producto> listaProductosCafe;
    private List<Producto> listaProductosTeInfusiones;
    private List<Producto> listaProductosDesayuno;
    private List<Producto> listaProductosBrunch;
    private List<Producto> listaProductosZumoSmuthie;
    private List<Producto> listaProductosBebidas;
    private List<Producto> listaProductosVinoCerveza;

    // Firebase

    private FirebaseManager firebaseManager;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private FirebaseUser firebaseUser;

    private ImageView iraperfil;

    // ID de usuario
    private String idUsuario = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_productos);

        // Inicializar la base de datos
//        DatabaseHelper dbHelper = new DatabaseHelper(this);
//        SQLiteDatabase db = dbHelper.getWritableDatabase();



        linearCarrito = findViewById(R.id.linearCarrito);

        iraperfil = findViewById(R.id.perfilbotonproductos);
        idUsuario = getIntent().getStringExtra("id");
        puntosUsuario = findViewById(R.id.tuspuntos);

        // Inicialización de Firebase
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("usuarios");

        // Referencias a los RecyclerViews
        recyclerViewCafe = findViewById(R.id.rvprodcafes);
        recyclerViewTeInfusiones = findViewById(R.id.rvprodtes);
        recyclerViewDesayunos = findViewById(R.id.rvprodDesayunos);
        recyclerViewBrunch = findViewById(R.id.rvprodBrunch);
        recyclerViewZumoSmuthie = findViewById(R.id.rvprodZumoSmuthie);
        recyclerViewBebidas = findViewById(R.id.rvprodBebidas);
        recyclerViewVinoCerveza = findViewById(R.id.rvprodVinoCerveza);

        // Configuración de los RecyclerViews
        recyclerViewCafe.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerViewTeInfusiones.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerViewDesayunos.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerViewBrunch.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerViewZumoSmuthie.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerViewBebidas.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerViewVinoCerveza.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        // Inicialización de las listas de productos
        listaProductos = new ArrayList<>();
        listaProductosCafe = new ArrayList<>();
        listaProductosTeInfusiones = new ArrayList<>();
        listaProductosDesayuno = new ArrayList<>();
        listaProductosBrunch = new ArrayList<>();
        listaProductosZumoSmuthie = new ArrayList<>();
        listaProductosBebidas = new ArrayList<>();
        listaProductosVinoCerveza = new ArrayList<>();

        // Configuración de los adaptadores
        adaptadorCafe = new ProductoAdaptador(this, listaProductosCafe);
        adaptadorTeInfusiones = new ProductoAdaptador(this, listaProductosTeInfusiones);
        adaptadorDesayunos = new ProductoAdaptador(this, listaProductosDesayuno);
        adaptadorBrunch = new ProductoAdaptador(this, listaProductosBrunch);
        adaptadorZumoSmuthie = new ProductoAdaptador(this, listaProductosZumoSmuthie);
        adaptadorBebidas = new ProductoAdaptador(this, listaProductosBebidas);
        adaptadorVinoCerveza = new ProductoAdaptador(this, listaProductosVinoCerveza);

        recyclerViewCafe.setAdapter(adaptadorCafe);
        recyclerViewTeInfusiones.setAdapter(adaptadorTeInfusiones);
        recyclerViewDesayunos.setAdapter(adaptadorDesayunos);
        recyclerViewBrunch.setAdapter(adaptadorBrunch);
        recyclerViewZumoSmuthie.setAdapter(adaptadorZumoSmuthie);
        recyclerViewBebidas.setAdapter(adaptadorBebidas);
        recyclerViewVinoCerveza.setAdapter(adaptadorVinoCerveza);

        linearCarrito.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Productos.this, Carrito.class);
                startActivity(intent);
                finish();
            }
        });


        // Obtención de la referencia a la base de datos Firebase y la ubicación de los productos
        DatabaseReference productosRef = FirebaseDatabase.getInstance().getReference().child("productos");

        // Agregar un ValueEventListener para leer los datos de los productos
        productosRef.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listaProductos.clear(); // Limpiar la lista de productos
                for (DataSnapshot productoSnapshot : dataSnapshot.getChildren()) {
                    Producto producto = productoSnapshot.getValue(Producto.class);
                    producto.setId(productoSnapshot.getKey()); // Establecer el ID del producto
                    listaProductos.add(producto);

                }

                // Agregar productos a las listas correspondientes
                listaProductosCafe.clear();
                listaProductosTeInfusiones.clear();
                listaProductosDesayuno.clear();
                listaProductosBrunch.clear();
                listaProductosZumoSmuthie.clear();
                listaProductosBebidas.clear();
                listaProductosVinoCerveza.clear();

                for (Producto producto : listaProductos) {
                    switch (producto.getCategoria()) {
                        case "cafe":
                            listaProductosCafe.add(producto);
                            break;
                        case "TeInfusiones":
                            listaProductosTeInfusiones.add(producto);
                            break;
                        case "Desayunos":
                            listaProductosDesayuno.add(producto);
                            break;
                        case "Brunch":
                            listaProductosBrunch.add(producto);
                            break;
                        case "ZumoSmuthie":
                            listaProductosZumoSmuthie.add(producto);
                            break;
                        case "Bebidas":
                            listaProductosBebidas.add(producto);
                            break;
                        case "VinoCerveza":
                            listaProductosVinoCerveza.add(producto);
                            break;
                        default:
                            break;
                    }
                }

                // Notificar cambios en los adaptadores
                adaptadorCafe.notifyDataSetChanged();
                adaptadorTeInfusiones.notifyDataSetChanged();
                adaptadorDesayunos.notifyDataSetChanged();
                adaptadorBrunch.notifyDataSetChanged();
                adaptadorZumoSmuthie.notifyDataSetChanged();
                adaptadorBebidas.notifyDataSetChanged();
                adaptadorVinoCerveza.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(Productos.this, "Error al cargar los productos", Toast.LENGTH_SHORT).show();
            }
        });

        iraperfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (firebaseUser != null) {
                    Intent intent = new Intent(Productos.this, PerfilUsuario.class);
                    startActivity(intent);
                    finish();
                } else {
                    Intent intent = new Intent(Productos.this, InicioSesion.class);
                    startActivity(intent);
                    finish();
                }
            }
        });

        // Configurar ValueEventListener para obtener datos de usuario y mostrar puntos
        if (firebaseUser != null) {

            // Configurar ValueEventListener para obtener datos de usuario y mostrar puntos
            databaseReference.child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String uid = snapshot.getKey();
                        String nombre = "" + snapshot.child("nombre").getValue();
                        String apellidos = "" + snapshot.child("apellidos").getValue();
                        String correo = "" + snapshot.child("correo").getValue();
                        String puntos = "" + snapshot.child("puntos").getValue();
                        puntosUsuario.setText(puntos);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(Productos.this, "Error al obtener puntos de usuario", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            // El usuario no está autenticado, no intentar obtener sus puntos
            Toast.makeText(this, "Usuario no autenticado", Toast.LENGTH_SHORT).show();
        }


        // Configurar el listener en el adaptador
        adaptadorCafe.setOnItemClickListener(this);
        adaptadorTeInfusiones.setOnItemClickListener(this);
        adaptadorDesayunos.setOnItemClickListener(this);
        adaptadorBrunch.setOnItemClickListener(this);
        adaptadorZumoSmuthie.setOnItemClickListener(this);
        adaptadorBebidas.setOnItemClickListener(this);
        adaptadorVinoCerveza.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(Producto producto) {
        // Aquí puedes iniciar la nueva actividad y pasar los datos del producto
        Intent intent = new Intent(this, producto_individual.class);
        intent.putExtra("producto_imagen", producto.getImagen());
        intent.putExtra("producto_nombre", producto.getNombre());
        intent.putExtra("producto_precio", producto.getPrecio());
        intent.putExtra("producto_descripcion", producto.getDescripcion());
        // Agrega más datos si es necesario
        startActivity(intent);
    }
    // Supongamos que este método se llama cuando el usuario hace clic en un botón para ir al carrito


}
