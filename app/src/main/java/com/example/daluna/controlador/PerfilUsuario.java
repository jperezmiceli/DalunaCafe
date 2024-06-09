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

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
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
    private ArrayAdapter<CharSequence> adapterPueblos; // Adapter para el spinner de pueblos

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_usuario);

        try {
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

            // Configurar adapter para el spinner de pueblos
            adapterPueblos = ArrayAdapter.createFromResource(this, R.array.pueblos_madrid, android.R.layout.simple_spinner_item);
            adapterPueblos.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            puebloSpinner.setAdapter(adapterPueblos);

            OnBackPressedCallback callback = new OnBackPressedCallback(true) {
                @Override
                public void handleOnBackPressed() {
                    try {
                        // Acción a realizar cuando se presiona el botón de retroceso
                        Intent intent = new Intent(PerfilUsuario.this, Principal.class);
                        startActivity(intent);
                        finish();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
            this.getOnBackPressedDispatcher().addCallback(this, callback);

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
                        // Ensure the context is valid before calling Glide
                        if (!isFinishing() && !isDestroyed()) {
                            Glide.with(PerfilUsuario.this).load(uri).into(imagenPerfil);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        e.printStackTrace();
                    }
                });
            }

            // Guardar cambios en la información del usuario
            guardarCambiosButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        guardarDatosUsuario();
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(PerfilUsuario.this, "Error al guardar los datos", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            // Editar foto de perfil
            editarFotoImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        seleccionarFoto();
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(PerfilUsuario.this, "Error al seleccionar la foto", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            // Cerrar sesión
            buttonCerrarSesion.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        firebaseAuth.signOut();
                        googleSignInClient.signOut().addOnCompleteListener(PerfilUsuario.this,
                                new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Toast.makeText(PerfilUsuario.this, "Sesión cerrada", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(PerfilUsuario.this, InicioSesion.class));
                                        finish();
                                    }
                                });
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(PerfilUsuario.this, "Error al cerrar sesión", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            // Botón atrás
            botonatras.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Intent intent = new Intent(PerfilUsuario.this, Principal.class);
                        startActivity(intent);
                        finish();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void cargarDatosUsuario() {
        try {
            String userId = firebaseUser.getUid();
            databaseReference.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String nombre = snapshot.child("nombre").getValue(String.class);
                        String apellidos = snapshot.child("apellidos").getValue(String.class);
                        String correo = snapshot.child("correo").getValue(String.class);
                        String numero = snapshot.child("numero").getValue(String.class);
                        String calle = snapshot.child("calle").getValue(String.class);
                        String numeroCalle = snapshot.child("numeroCalle").getValue(String.class);
                        String ciudad = snapshot.child("ciudad").getValue(String.class);
                        String pueblo = snapshot.child("pueblo").getValue(String.class); // Obtener valor de pueblo
                        String tipoResidencia = snapshot.child("tipoResidencia").getValue(String.class);
                        String portal = snapshot.child("portal").getValue(String.class);
                        String piso = snapshot.child("piso").getValue(String.class);

                        nombreEditText.setText(nombre);
                        apellidosEditText.setText(apellidos);
                        correoEditText.setText(correo);
                        numeroEditText.setText(numero);
                        calleEditText.setText(calle);
                        numeroCalleEditText.setText(numeroCalle);

                        // Seleccionar valor de ciudad en el Spinner
                        if (ciudad != null) {
                            int position = adapterCiudades.getPosition(ciudad);
                            ciudadSpinner.setSelection(position);
                        }

                        // Seleccionar valor de pueblo en el Spinner
                        if (pueblo != null) {
                            int position = adapterPueblos.getPosition(pueblo);
                            puebloSpinner.setSelection(position);
                        }

                        // Seleccionar valor de tipoResidencia en el Spinner
                        if (tipoResidencia != null) {
                            int position = adapterTipoResidencia.getPosition(tipoResidencia);
                            tipoResidenciaSpinner.setSelection(position);

                            if (position == 1) { // Si el tipo de residencia es "Piso"
                                portalEditText.setVisibility(View.VISIBLE);
                                pisoEditText.setVisibility(View.VISIBLE);
                                portalEditText.setText(portal);
                                pisoEditText.setText(piso);
                            } else {
                                portalEditText.setVisibility(View.GONE);
                                pisoEditText.setVisibility(View.GONE);
                            }
                        }

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(PerfilUsuario.this, "Error al cargar los datos", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void guardarDatosUsuario() {
        try {
            String userId = firebaseUser.getUid();
            String nombre = nombreEditText.getText().toString();
            String apellidos = apellidosEditText.getText().toString();
            String correo = correoEditText.getText().toString();
            String numero = numeroEditText.getText().toString();
            String calle = calleEditText.getText().toString();
            String numeroCalle = numeroCalleEditText.getText().toString();
            String ciudad = ciudadSpinner.getSelectedItem().toString();
            String pueblo = puebloSpinner.getSelectedItem().toString(); // Obtener valor del Spinner de pueblo
            String tipoResidencia = tipoResidenciaSpinner.getSelectedItem().toString();
            String portal = portalEditText.getText().toString();
            String piso = pisoEditText.getText().toString();

            // Actualizar datos en la base de datos
            databaseReference.child(userId).child("nombre").setValue(nombre);
            databaseReference.child(userId).child("apellidos").setValue(apellidos);
            databaseReference.child(userId).child("correo").setValue(correo);
            databaseReference.child(userId).child("numero").setValue(numero);
            databaseReference.child(userId).child("calle").setValue(calle);
            databaseReference.child(userId).child("numeroCalle").setValue(numeroCalle);
            databaseReference.child(userId).child("ciudad").setValue(ciudad);
            databaseReference.child(userId).child("pueblo").setValue(pueblo); // Guardar valor del Spinner de pueblo
            databaseReference.child(userId).child("tipoResidencia").setValue(tipoResidencia);

            if (tipoResidencia.equals("Piso")) {
                databaseReference.child(userId).child("portal").setValue(portal);
                databaseReference.child(userId).child("piso").setValue(piso);
            } else {
                databaseReference.child(userId).child("portal").removeValue();
                databaseReference.child(userId).child("piso").removeValue();
            }

            Toast.makeText(this, "Datos guardados correctamente", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error al guardar los datos", Toast.LENGTH_SHORT).show();
        }
    }

    private void seleccionarFoto() {
        try {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, 1);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error al seleccionar la foto", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            imagenPerfil.setImageURI(imageUri);
            subirFoto();
        }
    }

    private void subirFoto() {
        try {
            String uid = firebaseUser.getUid();
            StorageReference fileRef = FirebaseStorage.getInstance().getReference().child("imgusuarios/" + uid + ".jpg");
            fileRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(PerfilUsuario.this, "Foto subida correctamente", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    e.printStackTrace();
                    Toast.makeText(PerfilUsuario.this, "Error al subir la foto", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error al subir la foto", Toast.LENGTH_SHORT).show();
        }
    }
}
