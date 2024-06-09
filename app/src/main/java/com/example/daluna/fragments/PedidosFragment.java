package com.example.daluna.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.daluna.R;
import com.example.daluna.adaptadores.PedidoAdapter;
import com.example.daluna.controlador.FirebaseManager;
import com.example.daluna.modelo.Venta;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class PedidosFragment extends Fragment {

    private List<Venta> listaPedidos;
    private List<Venta> listaPedidosFiltrada;
    private PedidoAdapter pedidoAdapter;
    private FirebaseManager firebaseManager;
    private String[] estadosPedido;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_pedidos, container, false);

        // Inicializa las listas de pedidos
        listaPedidos = new ArrayList<>();
        listaPedidosFiltrada = new ArrayList<>();

        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewPedidos);
        EditText searchField = view.findViewById(R.id.searchField);
        Spinner filterSpinner = view.findViewById(R.id.filterSpinner);

        pedidoAdapter = new PedidoAdapter(listaPedidosFiltrada);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(pedidoAdapter);

        firebaseManager = new FirebaseManager();

        // Obtiene el array de estados desde resources
        estadosPedido = getResources().getStringArray(R.array.estados_pedido);

        // Configura el spinner con el array de estados
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, estadosPedido);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        filterSpinner.setAdapter(adapter);

        // Obtiene la lista de pedidos desde Firebase
        firebaseManager.obtenerPedidos(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listaPedidos.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Venta venta = snapshot.getValue(Venta.class);
                    if (venta != null) {
                        listaPedidos.add(venta);
                    }
                }
                filtrarPedidos(searchField.getText().toString(), filterSpinner.getSelectedItem().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), "Error al obtener datos de Firebase", Toast.LENGTH_SHORT).show();
            }
        });

        // Configura el campo de búsqueda
        searchField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filtrarPedidos(s.toString(), filterSpinner.getSelectedItem().toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Configura el spinner de filtro
        filterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                filtrarPedidos(searchField.getText().toString(), parent.getItemAtPosition(position).toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        return view;
    }

    private void filtrarPedidos(String textoBusqueda, String filtro) {
        listaPedidosFiltrada.clear();
        for (Venta venta : listaPedidos) {
            if ((venta.getNumeroPedido().toLowerCase().contains(textoBusqueda.toLowerCase()) ||
                    venta.getEstado().toLowerCase().contains(textoBusqueda.toLowerCase())) &&
                    (filtro.equals("Todos") || venta.getEstado().equalsIgnoreCase(filtro))) {
                listaPedidosFiltrada.add(venta);
            }
        }
        pedidoAdapter.notifyDataSetChanged();
    }
}

