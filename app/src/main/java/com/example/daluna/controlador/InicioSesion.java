package com.example.daluna.controlador;
import static android.content.ContentValues.TAG;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.daluna.modelo.Usuario;
import com.example.daluna.Principal;
import com.example.daluna.R;
import com.example.daluna.controlador.RecuperacionClave;
import com.example.daluna.controlador.Registro;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class InicioSesion extends AppCompatActivity {
    private static final int RC_SIGN_IN = 9001;
    private Button registrar;
    private Button enviar;
    private Button googlebutton;
    private EditText mail;
    private EditText clave;
    private TextView iniciosincuenta;
    private TextView recuperarclave;
    private FirebaseAuth mAuth;
    private FirebaseUser firebaseUser;
    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        googlebutton = findViewById(R.id.googlebutton);
        mAuth = FirebaseAuth.getInstance();
        enviar = findViewById(R.id.buttonregistrar);
        registrar = findViewById(R.id.registrate);
        mail = findViewById(R.id.nombreRegistro);
        clave = findViewById(R.id.claveDosRegistro);
        iniciosincuenta = findViewById(R.id.iniciodesesion);
        recuperarclave = findViewById(R.id.recuperarclave);

        // Configuración de Google Sign-In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        googlebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInWithGoogle();
            }
        });

        // Verificar si el usuario ya está autenticado
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            showHome(currentUser.getUid());
            finish(); // No es necesario continuar con el resto del método onCreate()
        }

        registrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InicioSesion.this, Registro.class);
                startActivity(intent);
                finish();
            }
        });
        iniciosincuenta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InicioSesion.this, Principal.class);
                startActivity(intent);
                finish();
            }
        });
        recuperarclave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InicioSesion.this, RecuperacionClave.class);
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
                    mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Log.e(TAG, "Usuario registrado");
                                firebaseUser = mAuth.getCurrentUser();
                                showHome(mAuth.getUid());
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

    private void signInWithGoogle() {
        // Cerrar sesión antes de iniciar sesión con Google para asegurarse de que se pregunte al usuario
        mAuth.signOut();

        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Resultado devuelto al iniciar la intención desde GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // El inicio de sesión de Google fue exitoso, autentícate con Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // El inicio de sesión de Google falló
                Log.w(TAG, "Google sign in failed", e);
                showErrorDialog("Error al iniciar sesión con Google: " + e.getMessage());
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            // Crear o actualizar un usuario en la base de datos Firebase Realtime Database
                            checkAndCreateFirebaseUser(user);
                            showHome(user.getUid());
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            showErrorDialog("Error al iniciar sesión con Google: " + task.getException().getMessage());
                        }
                    }
                });
    }

    private void checkAndCreateFirebaseUser(FirebaseUser firebaseUser) {
        if (firebaseUser != null) {
            String userId = firebaseUser.getUid();
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("usuarios");

            databaseReference.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        // El usuario ya existe, puedes actualizar la información si es necesario
                        Log.d(TAG, "Usuario ya existe en la base de datos.");
                    } else {
                        // El usuario no existe, crear un nuevo usuario
                        String userName = firebaseUser.getDisplayName();
                        String userEmail = firebaseUser.getEmail();
                        Usuario usuario = new Usuario(userName, userEmail, "", userEmail);
                        databaseReference.child(userId).setValue(usuario);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.w(TAG, "checkAndCreateFirebaseUser:onCancelled", error.toException());
                }
            });
        }
    }

    private void showHome(String userId) {
        Intent intent = new Intent(InicioSesion.this, Principal.class);
        intent.putExtra("USER_ID", userId);
        startActivity(intent);
        finish();
    }

    private void showErrorDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Error");
        builder.setMessage(message);
        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
