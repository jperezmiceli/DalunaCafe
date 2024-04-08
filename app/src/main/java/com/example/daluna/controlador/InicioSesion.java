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
import com.example.daluna.modelo.Producto;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class InicioSesion extends AppCompatActivity {
    private Button registrar;
    private Button enviar;
    private EditText mail;
    private EditText clave;
    private Producto producto;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        setTheme(R.style.SplashTheme);
        enviar = findViewById(R.id.buttonregistrar);
        registrar = findViewById(R.id.registrate);
        mail = findViewById(R.id.nombreRegistro);
        clave = findViewById(R.id.claveDosRegistro);
        producto = new Producto("pan","1",3.56679,"desayuno");
        System.out.println(producto.getPuntosProducto());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        registrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InicioSesion.this, Registro.class);


                // Iniciar la nueva actividad
                startActivity(intent);

                // Opcional: Cerrar la actividad actual si ya no es necesaria
                finish();


            }
        });
        enviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mail.getText().toString();
                String password = clave.getText().toString();

                if (!email.isEmpty() && !password.isEmpty()) {
                    FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        Log.e(TAG, "Usuario registrado");
                                        showHome(email);
                                        // El usuario se creó exitosamente
                                        // Aquí pAuedes manejar el éxito, por ejemplo, iniciar sesión automáticamente o mostrar un mensaje de éxito
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
