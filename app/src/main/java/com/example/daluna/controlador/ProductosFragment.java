package com.example.daluna.controlador;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
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

public class ProductosFragment extends Fragment implements ProductoAdaptador.OnItemClickListener {

    private RecyclerView recyclerViewCafe;
    private RecyclerView recyclerViewTeInfusiones;
    private RecyclerView recyclerViewDesayunos;
    private RecyclerView recyclerViewBrunch;
    private RecyclerView recyclerViewZumoSmuthie;
    private RecyclerView recyclerViewBebidas;
    private RecyclerView recyclerViewVinoCerveza;

    private List<Producto> listaProductosCafe;
    private List<Producto> listaProductosTeInfusiones;
    private List<Producto> listaProductosDesayuno;
    private List<Producto> listaProductosBrunch;
    private List<Producto> listaProductosZumoSmuthie;
    private List<Producto> listaProductosBebidas;
    private List<Producto> listaProductosVinoCerveza;

    private FirebaseManager firebaseManager;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private FirebaseUser firebaseUser;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.activity_productos, container, false);

        // Inicialización de Firebase
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("usuarios");

        recyclerViewCafe = root.findViewById(R.id.rvprodcafes);
        recyclerViewTeInfusiones = root.findViewById(R.id.rvprodtes);
        recyclerViewDesayunos = root.findViewById(R.id.rvprodDesayunos);
        recyclerViewBrunch = root.findViewById(R.id.rvprodBrunch);
        recyclerViewZumoSmuthie = root.findViewById(R.id.rvprodZumoSmuthie);
        recyclerViewBebidas = root.findViewById(R.id.rvprodBebidas);
        recyclerViewVinoCerveza = root.findViewById(R.id.rvprodVinoCerveza);

        recyclerViewCafe.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerViewTeInfusiones.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerViewDesayunos.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerViewBrunch.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerViewZumoSmuthie.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerViewBebidas.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerViewVinoCerveza.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        listaProductosCafe = new ArrayList<>();
        listaProductosTeInfusiones = new ArrayList<>();
        listaProductosDesayuno = new ArrayList<>();
        listaProductosBrunch = new ArrayList<>();
        listaProductosZumoSmuthie = new ArrayList<>();
        listaProductosBebidas = new ArrayList<>();
        listaProductosVinoCerveza = new ArrayList<>();

        ProductoAdaptador adaptadorCafe = new ProductoAdaptador(getContext(), listaProductosCafe);
        ProductoAdaptador adaptadorTeInfusiones = new ProductoAdaptador(getContext(), listaProductosTeInfusiones);
        ProductoAdaptador adaptadorDesayunos = new ProductoAdaptador(getContext(), listaProductosDesayuno);
        ProductoAdaptador adaptadorBrunch = new ProductoAdaptador(getContext(), listaProductosBrunch);
        ProductoAdaptador adaptadorZumoSmuthie = new ProductoAdaptador(getContext(), listaProductosZumoSmuthie);
        ProductoAdaptador adaptadorBebidas = new ProductoAdaptador(getContext(), listaProductosBebidas);
        ProductoAdaptador adaptadorVinoCerveza = new ProductoAdaptador(getContext(), listaProductosVinoCerveza);

        recyclerViewCafe.setAdapter(adaptadorCafe);
        recyclerViewTeInfusiones.setAdapter(adaptadorTeInfusiones);
        recyclerViewDesayunos.setAdapter(adaptadorDesayunos);
        recyclerViewBrunch.setAdapter(adaptadorBrunch);
        recyclerViewZumoSmuthie.setAdapter(adaptadorZumoSmuthie);
        recyclerViewBebidas.setAdapter(adaptadorBebidas);
        recyclerViewVinoCerveza.setAdapter(adaptadorVinoCerveza);

        adaptadorCafe.setOnItemClickListener(this);
        adaptadorTeInfusiones.setOnItemClickListener(this);
        adaptadorDesayunos.setOnItemClickListener(this);
        adaptadorBrunch.setOnItemClickListener(this);
        adaptadorZumoSmuthie.setOnItemClickListener(this);
        adaptadorBebidas.setOnItemClickListener(this);
        adaptadorVinoCerveza.setOnItemClickListener(this);

        // Obtención de la referencia a la base de datos Firebase y la ubicación de los productos
        DatabaseReference productosRef = FirebaseDatabase.getInstance().getReference().child("productos");

        // Agregar un ValueEventListener para leer los datos de los productos
        productosRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listaProductosCafe.clear();
                listaProductosTeInfusiones.clear();
                listaProductosDesayuno.clear();
                listaProductosBrunch.clear();
                listaProductosZumoSmuthie.clear();
                listaProductosBebidas.clear();
                listaProductosVinoCerveza.clear();

                for (DataSnapshot productoSnapshot : dataSnapshot.getChildren()) {
                    Producto producto = productoSnapshot.getValue(Producto.class);
                    producto.setId(productoSnapshot.getKey());
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
                // Manejo de errores
            }
        });

        return root;
    }

    @Override
    public void onItemClick(Producto producto) {
        // Aquí puedes iniciar la nueva actividad y pasar los datos del producto
        Intent intent = new Intent(getContext(), producto_individual.class);
        intent.putExtra("producto_imagen", producto.getImagen());
        intent.putExtra("producto_nombre", producto.getNombre());
        intent.putExtra("producto_precio", producto.getPrecio());
        intent.putExtra("producto_descripcion", producto.getDescripcion());
        // Agrega más datos si es necesario
        startActivity(intent);
    }
    // Supongamos que este método se llama cuando el usuario hace clic en un botón para ir al carrito


}