package com.example.daluna.controlador;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
    private ImageView botonatras;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recuperacion_clave);

        // Inicializar Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance();

        //iniciar bton atras
        botonatras = findViewById(R.id.prodindatrasrec);

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

        botonatras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RecuperacionClave.this, InicioSesion.class);
                startActivity(intent);
                finish();
            }
        });

    }

    private void enviarCorreoRecuperacion() {
        String correo = correoRecuperacionEditText.getText().toString().trim();

        // Validar el campo de correo electrónico
        if (TextUtils.isEmpty(correo)) {
            correoRecuperacionEditText.setError("Por favor, ingrese su correo electrónico");
            correoRecuperacionEditText.requestFocus();
            return;
        }

        // Validar el formato del correo electrónico
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
            correoRecuperacionEditText.setError("Por favor, ingrese un correo electrónico válido");
            correoRecuperacionEditText.requestFocus();
            return;
        }

        // Enviar el correo de recuperación usando Firebase Authentication
        firebaseAuth.sendPasswordResetEmail(correo)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // Correo de recuperación enviado exitosamente
                            Toast.makeText(RecuperacionClave.this, "Correo de recuperación enviado", Toast.LENGTH_SHORT).show();
                            // Limpia el campo de correo electrónico
                            correoRecuperacionEditText.setText("");
                        } else {
                            // Error al enviar el correo de recuperación
                            Toast.makeText(RecuperacionClave.this, "Error al enviar el correo de recuperación. Por favor, inténtelo de nuevo.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
