package com.example.daluna.controlador;

import android.util.Log;

import com.example.daluna.modelo.Usuarios;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FirebaseManager {
    private static final String TAG = "FirebaseManager";
    private DatabaseReference mDatabase;

    public FirebaseManager() {
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    public void writeNewUser(String userId, Usuarios usuario) {
        // Guardar el usuario en la base de datos Firebase
        mDatabase.child("usuarios").child(userId).setValue(usuario);
    }

    public void readUser(String userId) {
        // Leer un usuario de la base de datos Firebase
        mDatabase.child("usuarios").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Este método se llama una vez con el valor inicial y nuevamente
                // cada vez que se actualizan los datos en esta ubicación.
                Usuarios usuario = dataSnapshot.getValue(Usuarios.class);
                Log.d(TAG, "Usuario leído: " + usuario);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Error al leer el valor
                Log.w(TAG, "Error al leer el valor.", error.toException());
            }
        });
    }
}
