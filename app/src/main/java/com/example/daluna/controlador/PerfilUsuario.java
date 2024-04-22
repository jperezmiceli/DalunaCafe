package com.example.daluna.controlador;

import android.content.Intent;
import android.os.Bundle;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class PerfilUsuario extends AppCompatActivity {

    private EditText nombreEditText, apellidosEditText, correoEditText, numeroEditText, ubicacionEditText;
    private Button guardarCambiosButton;
    private Button buttonCerrarSesion;
    private ImageView botonatras;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_usuario);

        botonatras = findViewById(R.id.prodindatrasperfilusuario);
        buttonCerrarSesion = findViewById(R.id.cerrarSesion);

        // Inicializar Firebase Auth y la base de datos
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("usuarios");

        // Referencias a los elementos de la interfaz de usuario
        nombreEditText = findViewById(R.id.nombreEditText);
        apellidosEditText = findViewById(R.id.apellidosEditText);
        correoEditText = findViewById(R.id.correoEditText);
        numeroEditText = findViewById(R.id.numeroEditText);
        ubicacionEditText = findViewById(R.id.ubicacionEditText);
        guardarCambiosButton = findViewById(R.id.guardarCambiosButton);

        // Obtener y mostrar los datos del usuario actual
        obtenerDatosUsuario();

        // Configurar el listener de clic para el botón de guardar cambios
        guardarCambiosButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guardarCambios();
            }
        });
        buttonCerrarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth.signOut();
                // Creamos un Intent para abrir la actividad de inicio de sesión
                Intent intent = new Intent(PerfilUsuario.this, InicioSesion.class);
                // Limpiamos todas las actividades anteriores y agregamos la nueva actividad al inicio
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                // Finalizamos la actividad actual (PerfilUsuario)
                finish();
            }
        });

        botonatras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PerfilUsuario.this, Productos.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void obtenerDatosUsuario() {
        // Verificar si el usuario está autenticado
        if (firebaseUser != null) {
            // Obtener la referencia al nodo del usuario en la base de datos
            DatabaseReference usuarioRef = databaseReference.child(firebaseUser.getUid());

            // Agregar un ValueEventListener para leer los datos del usuario
            usuarioRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        // Obtener los datos del usuario
                        String nombre = dataSnapshot.child("nombre").getValue(String.class);
                        String apellidos = dataSnapshot.child("apellidos").getValue(String.class);
                        String correo = dataSnapshot.child("correo").getValue(String.class);
                        String numero = dataSnapshot.child("numero").getValue(String.class);
                        String ubicacion = dataSnapshot.child("ubicacion").getValue(String.class);

                        // Mostrar los datos del usuario en los EditText
                        nombreEditText.setText(nombre);
                        apellidosEditText.setText(apellidos);
                        correoEditText.setText(correo);
                        numeroEditText.setText(numero);
                        ubicacionEditText.setText(ubicacion);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(PerfilUsuario.this, "Error al obtener datos del usuario", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            // El usuario no está autenticado, no se pueden obtener sus datos
            Toast.makeText(this, "Usuario no autenticado", Toast.LENGTH_SHORT).show();
        }
    }

    private void guardarCambios() {
        // Obtener los nuevos valores de los EditText
        String nombre = nombreEditText.getText().toString().trim();
        String apellidos = apellidosEditText.getText().toString().trim();
        String correo = correoEditText.getText().toString().trim();
        String numero = numeroEditText.getText().toString().trim();
        String ubicacion = ubicacionEditText.getText().toString().trim();

        // Verificar si el usuario está autenticado
        if (firebaseUser != null) {
            // Obtener la referencia al nodo del usuario en la base de datos
            DatabaseReference usuarioRef = databaseReference.child(firebaseUser.getUid());

            // Actualizar los datos del usuario en la base de datos
            usuarioRef.child("nombre").setValue(nombre);
            usuarioRef.child("apellidos").setValue(apellidos);
            usuarioRef.child("correo").setValue(correo);
            usuarioRef.child("numero").setValue(numero);
            usuarioRef.child("ubicacion").setValue(ubicacion)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                // Los cambios se guardaron exitosamente
                                Toast.makeText(PerfilUsuario.this, "Cambios guardados", Toast.LENGTH_SHORT).show();
                            } else {
                                // Error al guardar los cambios
                                Toast.makeText(PerfilUsuario.this, "Error al guardar los cambios", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } else {
            // El usuario no está autenticado, no se pueden guardar los cambios
            Toast.makeText(this, "Usuario no autenticado", Toast.LENGTH_SHORT).show();
        }
    }
}
