package com.example.daluna.controlador;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.ComponentActivity;
import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.daluna.R;

public class PedidosFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_pedidos, container, false);

        // Llamada correcta para habilitar EdgeToEdge en la actividad contenedora del fragmento
        ComponentActivity activity = (ComponentActivity) requireActivity();
        EdgeToEdge.enable(activity);

        return view;
    }
}
