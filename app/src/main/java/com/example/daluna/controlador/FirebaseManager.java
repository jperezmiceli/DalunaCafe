package com.example.daluna.controlador;

import androidx.appcompat.app.AppCompatActivity;

import com.example.daluna.modelo.Producto;
import com.example.daluna.modelo.Usuario;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FirebaseManager extends AppCompatActivity {
    private DatabaseReference mDatabase;
    public FirebaseManager(){
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    public void writeNewUser(Usuario usuario) {
        // Crear un nuevo objeto Usuario con los datos proporcionados


        // Guardar el usuario en la base de datos Firebase
        mDatabase.child("usuarios").child("hsuwsh99").setValue(usuario);
    }


}
