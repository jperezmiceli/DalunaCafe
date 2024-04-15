package com.example.daluna.controlador;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.daluna.R;
import com.example.daluna.modelo.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Registro extends AppCompatActivity {
    private Button registrar;
    private EditText mail;
    private EditText nombre;
    private EditText numero;
    private EditText apellidos;
    private EditText claveUno;
    private EditText claveDos;
     FirebaseAuth mAuth;
     DatabaseReference databaseReference;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registro);
        FirebaseApp.initializeApp(this);
        mail = findViewById(R.id.mailRegistro);
        claveUno = findViewById(R.id.claveUnoRegistro);
        claveDos = findViewById(R.id.claveDosRegistro);
        nombre = findViewById(R.id.nombreRegistro);
        apellidos = findViewById(R.id.apellidoRegistro);
        numero = findViewById(R.id.movilRegistro);
        registrar = findViewById(R.id.buttonregistrar);
        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        registrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mail.getText().toString();
                String password = claveDos.getText().toString();


                if (!email.isEmpty() && !password.isEmpty() && claveUno.getText().toString().equals(claveDos.getText().toString()) & apellidos != null & nombre != null ) {
                    mAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(Registro.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information
                                        Log.d(TAG, "createUserWithEmail:success");
                                        FirebaseUser user = mAuth.getCurrentUser();
                                        updateUI(user);
                                    } else {
                                        // If sign in fails, display a message to the user.
                                        Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                        Toast.makeText(Registro.this, "Authentication failed.",
                                                Toast.LENGTH_SHORT).show();
                                        updateUI(null);
                                    }
                                }
                            });
                } else {
                    Log.e(TAG, "error");
                }
            }
        });

    }

    private void showErrorDialog(String errorMessage) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Error al registrar usuario");
        builder.setMessage(errorMessage);
        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Aquí puedes agregar cualquier lógica adicional que necesites después de que el usuario acepte el mensaje de error
            }
        });
        builder.show();
    }


    // Dentro del método updateUI()

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            // El usuario se registró correctamente, mostrar un mensaje de bienvenida
            Toast.makeText(this, "¡Registro exitoso! Bienvenido, " + user.getEmail(), Toast.LENGTH_SHORT).show();

            // Obtener la referencia a la ubicación "usuarios" en la base de datos Firebase
            DatabaseReference usuariosRef = databaseReference.child("usuarios");

            // Obtener los datos del usuario para guardarlos en Firebase
            String userId = user.getUid();
            String userEmail = user.getEmail();
            String userNombre = nombre.getText().toString();
            String userApellidos = apellidos.getText().toString();
            String userNumero = numero.getText().toString();
            String userUbicacion = "Ubicación del usuario"; // Aquí debes obtener la ubicación del usuario si es necesario

            // Crear un objeto Usuario con los datos del usuario
            Usuario nuevoUsuario = new Usuario(userNombre, userApellidos, userNumero, userEmail, userUbicacion);

            // Guardar el nuevo usuario en la base de datos Firebase
            usuariosRef.child(userId).setValue(nuevoUsuario)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                // Los datos del usuario se guardaron exitosamente en Firebase
                                Log.d(TAG, "Datos del usuario guardados en Firebase correctamente");
                            } else {
                                // Hubo un error al guardar los datos del usuario en Firebase
                                Log.e(TAG, "Error al guardar los datos del usuario en Firebase", task.getException());
                            }
                        }
                    });

            // También puedes redirigir al usuario a otra actividad o realizar otras acciones según sea necesario
            Intent intent = new Intent(this, Home.class);
            intent.putExtra("correo", mail.getText().toString());
            intent.putExtra("id", userId);
            startActivity(intent);
            finish();
        } else {
            // Hubo un problema al registrar al usuario, mostrar un mensaje de error
            Toast.makeText(this, "¡Hubo un error al registrar al usuario!", Toast.LENGTH_SHORT).show();
        }
    }


}
