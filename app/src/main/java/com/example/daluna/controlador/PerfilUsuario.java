package com.example.daluna.controlador;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.daluna.Principal;
import com.example.daluna.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class PerfilUsuario extends AppCompatActivity {

    private EditText nombreEditText, apellidosEditText, correoEditText, numeroEditText, calleEditText, numeroCalleEditText, portalEditText, pisoEditText;
    private Spinner ciudadSpinner, puebloSpinner, tipoResidenciaSpinner;
    private Button guardarCambiosButton, buttonCerrarSesion;
    private ImageView botonatras, editarFotoImageView;
    private TextView textViewPueblo, textViewPortal, textViewPiso;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private FirebaseUser firebaseUser;
    private GoogleSignInClient googleSignInClient;
    private Uri imageUri;
    private ImageView imagenPerfil;

    private ArrayAdapter<CharSequence> adapterCiudades;
    private ArrayAdapter<CharSequence> adapterTipoResidencia;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_usuario);

        // Inicializar Firebase Auth y la base de datos
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("usuarios");

        // Configurar el cliente de Google SignIn
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);

        // Referencias a los elementos de la interfaz de usuario
        nombreEditText = findViewById(R.id.nombreEditText);
        apellidosEditText = findViewById(R.id.apellidosEditText);
        correoEditText = findViewById(R.id.correoEditText);
        numeroEditText = findViewById(R.id.numeroEditText);
        calleEditText = findViewById(R.id.calleEditText);
        numeroCalleEditText = findViewById(R.id.numeroCalleEditText);
        portalEditText = findViewById(R.id.portalEditText);
        pisoEditText = findViewById(R.id.pisoEditText);

        ciudadSpinner = findViewById(R.id.ciudadSpinner);
        puebloSpinner = findViewById(R.id.puebloSpinner);
        tipoResidenciaSpinner = findViewById(R.id.tipoResidenciaSpinner);

        guardarCambiosButton = findViewById(R.id.guardarCambiosButton);
        buttonCerrarSesion = findViewById(R.id.cerrarSesion);
        botonatras = findViewById(R.id.prodindatrasperfilusuario);
        editarFotoImageView = findViewById(R.id.editarFotoImageView);
        imagenPerfil = findViewById(R.id.fotoPerfilImageView);

        // Configurar Spinners y sus adapters
        adapterCiudades = ArrayAdapter.createFromResource(this, R.array.ciudades, android.R.layout.simple_spinner_item);
        adapterCiudades.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ciudadSpinner.setAdapter(adapterCiudades);

        adapterTipoResidencia = ArrayAdapter.createFromResource(this, R.array.tipo_residencia, android.R.layout.simple_spinner_item);
        adapterTipoResidencia.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        tipoResidenciaSpinner.setAdapter(adapterTipoResidencia);

        // Eventos de los Spinners
        ciudadSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if (position == 1) { // Si la ciudad es Madrid
                    puebloSpinner.setVisibility(View.VISIBLE);
                } else {
                    puebloSpinner.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Do nothing here
            }
        });

        tipoResidenciaSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if (position == 1) { // Si el tipo de residencia es "Piso"
                    portalEditText.setVisibility(View.VISIBLE);
                    pisoEditText.setVisibility(View.VISIBLE);
                } else {
                    portalEditText.setVisibility(View.GONE);
                    pisoEditText.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Do nothing here
            }
        });

        // Obtener y mostrar la información del usuario
        if (firebaseUser != null) {
            cargarDatosUsuario();
            String uid = firebaseUser.getUid();
            StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("imgusuarios/" + uid + ".jpg");
            storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Picasso.get().load(uri).into(imagenPerfil);
                }
            });
        }

        // Guardar cambios en la información del usuario
        guardarCambiosButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guardarDatosUsuario();
            }
        });

        // Editar foto de perfil
        editarFotoImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seleccionarFoto();
            }
        });

        // Cerrar sesión
        buttonCerrarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth.signOut();
                googleSignInClient.signOut().addOnCompleteListener(PerfilUsuario.this,
                        new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(PerfilUsuario.this, "Sesión cerrada", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(PerfilUsuario.this, Principal.class));
                                finish();
                            }
                        });
            }
        });

        // Botón atrás
        botonatras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void cargarDatosUsuario() {
        String uid = firebaseUser.getUid();
        databaseReference.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String nombre = snapshot.child("nombre").getValue(String.class);
                    String apellidos = snapshot.child("apellidos").getValue(String.class);
                    String correo = snapshot.child("correo").getValue(String.class);
                    String numero = snapshot.child("numero").getValue(String.class);
                    String calle = snapshot.child("calle").getValue(String.class);
                    String numeroCalle = snapshot.child("numeroCalle").getValue(String.class);
                    String portal = snapshot.child("portal").getValue(String.class);
                    String piso = snapshot.child("piso").getValue(String.class);
                    String ciudad = snapshot.child("ciudad").getValue(String.class);
                    String pueblo = snapshot.child("pueblo").getValue(String.class);
                    String tipoResidencia = snapshot.child("tipoResidencia").getValue(String.class);

                    nombreEditText.setText(nombre);
                    apellidosEditText.setText(apellidos);
                    correoEditText.setText(correo);
                    numeroEditText.setText(numero);
                    calleEditText.setText(calle);
                    numeroCalleEditText.setText(numeroCalle);
                    portalEditText.setText(portal);
                    pisoEditText.setText(piso);

                    if (ciudad != null) {
                        int ciudadPosition = adapterCiudades.getPosition(ciudad);
                        ciudadSpinner.setSelection(ciudadPosition);
                    }

                    if (ciudad != null && ciudad.equals("Madrid") && pueblo != null) {
                        ArrayAdapter<CharSequence> adapterPueblos = ArrayAdapter.createFromResource(PerfilUsuario.this, R.array.pueblos_madrid, android.R.layout.simple_spinner_item);
                        adapterPueblos.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        puebloSpinner.setAdapter(adapterPueblos);
                        int puebloPosition = adapterPueblos.getPosition(pueblo);
                        puebloSpinner.setSelection(puebloPosition);
                    }

                    if (tipoResidencia != null) {
                        int tipoResidenciaPosition = adapterTipoResidencia.getPosition(tipoResidencia);
                        tipoResidenciaSpinner.setSelection(tipoResidenciaPosition);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(PerfilUsuario.this, "Error al cargar los datos", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void guardarDatosUsuario() {
        String uid = firebaseUser.getUid();

        String nombre = nombreEditText.getText().toString().trim();
        String apellidos = apellidosEditText.getText().toString().trim();
        String correo = correoEditText.getText().toString().trim();
        String numero = numeroEditText.getText().toString().trim();
        String calle = calleEditText.getText().toString().trim();
        String numeroCalle = numeroCalleEditText.getText().toString().trim();
        String portal = portalEditText.getText().toString().trim();
        String piso = pisoEditText.getText().toString().trim();
        String ciudad = ciudadSpinner.getSelectedItem().toString();
        String pueblo = ciudad.equals("Madrid") ? puebloSpinner.getSelectedItem().toString() : "";
        String tipoResidencia = tipoResidenciaSpinner.getSelectedItem().toString();

        databaseReference.child(uid).child("nombre").setValue(nombre);
        databaseReference.child(uid).child("apellidos").setValue(apellidos);
        databaseReference.child(uid).child("correo").setValue(correo);
        databaseReference.child(uid).child("numero").setValue(numero);
        databaseReference.child(uid).child("calle").setValue(calle);
        databaseReference.child(uid).child("numeroCalle").setValue(numeroCalle);
        databaseReference.child(uid).child("portal").setValue(portal);
        databaseReference.child(uid).child("piso").setValue(piso);
        databaseReference.child(uid).child("ciudad").setValue(ciudad);
        databaseReference.child(uid).child("pueblo").setValue(pueblo);
        databaseReference.child(uid).child("tipoResidencia").setValue(tipoResidencia);

        if (imageUri != null) {
            StorageReference fileRef = FirebaseStorage.getInstance().getReference().child("imgusuarios/" + uid + ".jpg");
            fileRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            databaseReference.child(uid).child("fotoPerfil").setValue(uri.toString());
                            Toast.makeText(PerfilUsuario.this, "Foto de perfil actualizada", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(PerfilUsuario.this, "Error al subir la foto", Toast.LENGTH_SHORT).show();
                }
            });
        }

        Toast.makeText(PerfilUsuario.this, "Datos actualizados", Toast.LENGTH_SHORT).show();
    }

    private void seleccionarFoto() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            imagenPerfil.setImageURI(imageUri);
        }
    }
}
