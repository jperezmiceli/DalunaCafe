package com.example.daluna.controlador;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.daluna.R;

public class Principal extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_principal);

        // Obtener el correo electrónico pasado como extra del Intent
        String correo = getIntent().getStringExtra("correo");

        // Encontrar el EditText en tu layout por su id
        TextView editTextCorreo = findViewById(R.id.editTetxtCorreo);

        // Establecer el texto del EditText al correo electrónico obtenido
        editTextCorreo.setText(correo);
    }
}