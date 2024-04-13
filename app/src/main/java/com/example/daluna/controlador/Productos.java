package com.example.daluna.controlador;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.daluna.R;

public class Productos extends AppCompatActivity {

//    private RecyclerView recyclerView;
//    private ProductoAdaptador adaptador;
//    private FirebaseManager firebaseManager;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_productos);
//
//        // Configurar EdgeToEdge
//        EdgeToEdge.enable(this);
//
//        // Inicializar FirebaseManager
//        firebaseManager = new FirebaseManager();
//
//        // Configurar el RecyclerView
//        recyclerView = findViewById(R.id.rvprod);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//
//        // Configurar adaptador
//        adaptador = new ProductoAdaptador(this, firebaseManager.getListaProductos());
//        recyclerView.setAdapter(adaptador);
//
//        // Leer los productos de la base de datos Firebase
//        firebaseManager.readProducts(adaptador);
//    }
}
