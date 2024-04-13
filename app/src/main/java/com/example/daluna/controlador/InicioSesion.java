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
    import com.example.daluna.modelo.Usuario;
    import com.google.android.gms.tasks.OnCompleteListener;
    import com.google.android.gms.tasks.Task;
    import com.google.firebase.FirebaseApp;
    import com.google.firebase.auth.AuthResult;
    import com.google.firebase.auth.FirebaseAuth;

    public class InicioSesion extends AppCompatActivity {
        private Button registrar;
        private Button enviar;
        private EditText mail;
        private EditText clave;

        FirebaseAuth mAuth;
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);


            mAuth = FirebaseAuth.getInstance();
            enviar = findViewById(R.id.buttonregistrar);
            registrar = findViewById(R.id.registrate);
            mail = findViewById(R.id.nombreRegistro);
            clave = findViewById(R.id.claveDosRegistro);

            registrar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(InicioSesion.this, Registro.class);
                    startActivity(intent);
                    finish();
                }
            });

            enviar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String email = mail.getText().toString();
                    String password = clave.getText().toString();

                    if (!email.isEmpty() && !password.isEmpty()) {
                        // Iniciar sesión utilizando el FirebaseAuthManager
                        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Log.e(TAG, "Usuario registrado");
                                    showHome(email);
                                } else {
                                    Log.e(TAG, "Error al iniciar sesión: " + task.getException().getMessage());
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
            builder.setTitle("Error al iniciar sesión");
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
            Intent intent = new Intent(this, Productos.class);
            intent.putExtra("correo", correo);
            startActivity(intent);
            finish();
        }
    }

