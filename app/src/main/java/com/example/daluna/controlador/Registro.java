package com.example.daluna.controlador;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.daluna.Principal;
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

import java.util.regex.Pattern;

public class Registro extends AppCompatActivity {
    private ImageView atras;
    private Button registrar;
    private EditText mail, nombre, numero, apellidos, claveUno, claveDos;
    private ImageView showPasswordButton1, showPasswordButton2;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);
        FirebaseApp.initializeApp(this);

        // Initialize views
        atras = findViewById(R.id.prodindatrasregistro);
        mail = findViewById(R.id.mailRegistro);
        claveUno = findViewById(R.id.claveUnoRegistro);
        claveDos = findViewById(R.id.claveDosRegistro);
        nombre = findViewById(R.id.nombreRegistro);
        apellidos = findViewById(R.id.apellidoRegistro);
        numero = findViewById(R.id.movilRegistro);
        registrar = findViewById(R.id.buttonregistrar);
        showPasswordButton1 = findViewById(R.id.showPasswordButton1);
        showPasswordButton2 = findViewById(R.id.showPasswordButton2);

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        // Restrict the "numero" field to only accept numbers
        numero.setInputType(InputType.TYPE_CLASS_PHONE);

        atras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Registro.this, InicioSesion.class);
                startActivity(intent);
                finish();
            }
        });

        registrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mail.getText().toString();
                String password = claveDos.getText().toString();

                if (validateInputs() && isValidPassword(password) && claveUno.getText().toString().equals(claveDos.getText().toString())) {
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
                                        Toast.makeText(Registro.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                                        updateUI(null);
                                    }
                                }
                            });
                } else {
                    showErrorDialog("Please ensure all fields are filled correctly. Make sure the password meets the requirements.");
                }
            }
        });

        showPasswordButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                togglePasswordVisibility(claveUno, showPasswordButton1);
            }
        });

        showPasswordButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                togglePasswordVisibility(claveDos, showPasswordButton2);
            }
        });
    }

    private boolean validateInputs() {
        boolean isValid = true;

        // Reset background to default
        Drawable defaultBackground = ContextCompat.getDrawable(this, R.drawable.fondoiniciosesion);

        if (nombre.getText().toString().isEmpty()) {
            nombre.setBackground(ContextCompat.getDrawable(this, R.drawable.error_background));
            isValid = false;
        } else {
            nombre.setBackground(defaultBackground);
        }

        if (apellidos.getText().toString().isEmpty()) {
            apellidos.setBackground(ContextCompat.getDrawable(this, R.drawable.error_background));
            isValid = false;
        } else {
            apellidos.setBackground(defaultBackground);
        }

        if (numero.getText().toString().isEmpty()) {
            numero.setBackground(ContextCompat.getDrawable(this, R.drawable.error_background));
            isValid = false;
        } else {
            numero.setBackground(defaultBackground);
        }

        if (mail.getText().toString().isEmpty()) {
            mail.setBackground(ContextCompat.getDrawable(this, R.drawable.error_background));
            isValid = false;
        } else {
            mail.setBackground(defaultBackground);
        }

        if (claveUno.getText().toString().isEmpty()) {
            claveUno.setBackground(ContextCompat.getDrawable(this, R.drawable.error_background));
            isValid = false;
        } else {
            claveUno.setBackground(defaultBackground);
        }

        if (claveDos.getText().toString().isEmpty()) {
            claveDos.setBackground(ContextCompat.getDrawable(this, R.drawable.error_background));
            isValid = false;
        } else {
            claveDos.setBackground(defaultBackground);
        }

        return isValid;
    }

    private boolean isValidPassword(String password) {
        // Al menos 6 caracteres, una letra mayúscula, un número y un carácter especial (incluyendo '.')
        Pattern pattern = Pattern.compile("^(?=.*[A-Z])(?=.*[0-9])(?=.*[@#$%^&+=.!]).{6,}$");
        return pattern.matcher(password).matches();
    }


    private void togglePasswordVisibility(EditText passwordField, ImageView toggleButton) {
        if (passwordField.getInputType() == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {
            passwordField.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            toggleButton.setImageResource(R.drawable.ic_show_password);
        } else {
            passwordField.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            toggleButton.setImageResource(R.drawable.ic_hide_password);
        }
        passwordField.setSelection(passwordField.getText().length());
    }

    private void showErrorDialog(String errorMessage) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Error al registrar usuario");
        builder.setMessage(errorMessage);
        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Additional logic after the user acknowledges the error message
            }
        });
        builder.show();
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            Toast.makeText(this, "¡Registro exitoso! Bienvenido, " + user.getEmail(), Toast.LENGTH_SHORT).show();

            DatabaseReference usuariosRef = databaseReference.child("usuarios");

            String userId = user.getUid();
            String userEmail = user.getEmail();
            String userNombre = nombre.getText().toString();
            String userApellidos = apellidos.getText().toString();
            String userNumero = numero.getText().toString();

            Usuario nuevoUsuario = new Usuario(userNombre, userApellidos, userNumero, userEmail);

            usuariosRef.child(userId).setValue(nuevoUsuario)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "Datos del usuario guardados en Firebase correctamente");
                            } else {
                                Log.e(TAG, "Error al guardar los datos del usuario en Firebase", task.getException());
                            }
                        }
                    });

            Intent intent = new Intent(Registro.this, Principal.class);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "¡Hubo un error al registrar al usuario!", Toast.LENGTH_SHORT).show();
        }
    }
}

