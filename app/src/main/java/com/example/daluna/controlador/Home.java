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

        String correo = getIntent().getStringExtra("correo");
        TextView editTextCorreo = findViewById(R.id.editTetxtCorreo);
        editTextCorreo.setText(correo);
    }
}