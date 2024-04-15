package com.example.daluna.controlador;

import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.daluna.R;

public class Home extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_principal);



        // Obtener el correo electrónico de la intención
        String correo = getIntent().getStringExtra("correo");
        String id = getIntent().getStringExtra("id");


        // Concatenar el correo electrónico y el ID de usuario (si es necesario) para mostrarlos en el TextView
        String textoCorreo = correo + " - ID: " + id;

        // Configurar el texto en un TextView
        TextView editTextCorreo = findViewById(R.id.editTetxtCorreo);
        editTextCorreo.setText(textoCorreo);
    }
}
