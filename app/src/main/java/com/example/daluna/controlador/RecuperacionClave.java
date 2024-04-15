package com.example.daluna.controlador;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.daluna.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class RecuperacionClave extends AppCompatActivity {

    private EditText correoRecuperacionEditText;
    private Button enviarCorreoButton;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recuperacion_clave);

        // Inicializar Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance();

        // Inicializar los elementos de la interfaz de usuario
        correoRecuperacionEditText = findViewById(R.id.correorecu);
        enviarCorreoButton = findViewById(R.id.buttonrecuperar);

        // Configurar el listener de clic para el botón de enviar correo
        enviarCorreoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enviarCorreoRecuperacion();
            }
        });
    }

    private void enviarCorreoRecuperacion() {
        String correo = correoRecuperacionEditText.getText().toString().trim();

        // Validar el campo de correo electrónico
        if (correo.isEmpty()) {
            correoRecuperacionEditText.setError("Por favor, ingrese su correo electrónico");
            correoRecuperacionEditText.requestFocus();
            return;
        }

        // Mostrar un ProgressDialog mientras se envía el correo
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Enviando correo de recuperación...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        // Enviar el correo de recuperación usando Firebase Authentication
        firebaseAuth.sendPasswordResetEmail(correo)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        progressDialog.dismiss(); // Ocultar el ProgressDialog

                        if (task.isSuccessful()) {
                            // Correo de recuperación enviado exitosamente
                            Toast.makeText(RecuperacionClave.this, "Correo de recuperación enviado", Toast.LENGTH_SHORT).show();
                        } else {
                            // Error al enviar el correo de recuperación
                            Toast.makeText(RecuperacionClave.this, "Error al enviar el correo de recuperación", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
