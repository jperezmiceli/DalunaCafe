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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Registro extends AppCompatActivity {
    private Button registrar;
    private EditText mail;
    private EditText nombre;
    private EditText numero;
    private EditText claveUno;
    private EditText claveDos;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registro);

        mail = findViewById(R.id.mailRegistro);
        claveUno = findViewById(R.id.claveUnoRegistro);
        claveDos = findViewById(R.id.claveDosRegistro);
        nombre = findViewById(R.id.nombreRegistro);
        numero = findViewById(R.id.movilRegistro);

        registrar = findViewById(R.id.buttonregistrar);

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

                if (!email.isEmpty() && !password.isEmpty() && claveUno.getText().toString().equals(claveDos.getText().toString())) {
                    FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        Usuario usuario = new Usuario(nombre.getText().toString(), numero.getText().toString(),mail.getText().toString());
                                        Log.e(TAG, "Usuario registrado");
                                        showHome(email);
                                        // El usuario se creó exitosamente
                                        // Aquí puedes manejar el éxito, por ejemplo, iniciar sesión automáticamente o mostrar un mensaje de éxito
                                    } else {
                                        // Hubo un error al crear el usuario
                                        // Aquí puedes manejar el error, por ejemplo, mostrar un mensaje de error al usuario
                                        Log.e(TAG, "Error al crear usuario: " + task.getException().getMessage());
                                        // Mostrar un diálogo de alerta con el mensaje de error
                                        showErrorDialog(task.getException().getMessage());
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

    private void showHome(String correo) {
        // Crear un Intent para iniciar la nueva actividad
        Intent intent = new Intent(this, Principal.class);

        // Agregar el correo electrónico y el proveedor como extras en el intent
        intent.putExtra("correo", correo);

        // Iniciar la nueva actividad
        startActivity(intent);

        // Opcional: Cerrar la actividad actual si ya no es necesaria
        finish();
    }
}
