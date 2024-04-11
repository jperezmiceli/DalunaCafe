package com.example.daluna.controlador;

import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class FirebaseAuthManager {
    private static final String TAG = "FirebaseAuthManager";

    private FirebaseAuth mAuth;
    private String currentUserId;

    // Constructor
    public FirebaseAuthManager() {
        mAuth = FirebaseAuth.getInstance();
    }

    // Método para iniciar sesión con correo electrónico y contraseña
    public Task<AuthResult> signIn(String email, String password) {
        return mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "signInWithEmail:success");
                        // Obtener el usuario actualmente autenticado
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            // Guardar el ID del usuario en la variable de sesión
                            currentUserId = user.getUid();
                        }
                    } else {
                        Log.w(TAG, "signInWithEmail:failure", task.getException());
                    }
                });
    }

    // Método para registrar un nuevo usuario con correo electrónico y contraseña
    public Task<AuthResult> signUp(String email, String password) {
        return mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "signUpWithEmail:success");
                        // Obtener el usuario actualmente autenticado
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            // Guardar el ID del usuario en la variable de sesión
                            currentUserId = user.getUid();
                        }
                    } else {
                        Log.w(TAG, "signUpWithEmail:failure", task.getException());
                    }
                });
    }

    // Método para obtener el ID del usuario actual
    public String getCurrentUserId() {
        return currentUserId;
    }

    // Método para cerrar sesión
    public void signOut() {
        mAuth.signOut();
        // Limpiar el ID del usuario al cerrar sesión
        currentUserId = null;
    }

    // Otros métodos relacionados con la autenticación de Firebase se pueden agregar aquí según sea necesario
}
