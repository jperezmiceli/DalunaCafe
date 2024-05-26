package com.example.daluna;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.example.daluna.controlador.FirebaseManager;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.List;

import static android.app.Activity.RESULT_OK;

public class Inicio extends Fragment {

    private static final int PICK_IMAGE_REQUEST = 1;
    private int currentImageIndex = 0;
    private Uri imageUri;
    private StorageReference storageReference;
    private FirebaseAuth auth;
    private ImageView imageView;
    private Button uploadButton;
    private ImageView repositorioDeImagenes;
    private Handler handler = new Handler();
    private List<String> imageUrls;
    private FirebaseManager firebaseManager = new FirebaseManager();  // Crear instancia de FirebaseManager

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_inicio, container, false);

        // Inicializa Firebase Auth y Storage
        auth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        imageView = view.findViewById(R.id.imageView);
        repositorioDeImagenes = view.findViewById(R.id.repositoriodeimagenes);
        uploadButton = view.findViewById(R.id.uploadButton);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadFile();
            }
        });

        ImageButton whatsappButton = view.findViewById(R.id.whatsappButton);
        ImageButton emailButton = view.findViewById(R.id.emailButton);
        ImageButton instagramButton = view.findViewById(R.id.instagramButton);
        ImageButton phoneButton = view.findViewById(R.id.phoneButton);

        whatsappButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://whatsapp.com/channel/0029VaXNVNGEAKWMiG45Gq3z";
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                startActivity(intent);
            }
        });

        emailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:info@daluna.com")); // Replace with your email
                startActivity(intent);
            }
        });

        instagramButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://www.instagram.com/dalunacafe";
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                startActivity(intent);
            }
        });

        phoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:+34636914387"));
                startActivity(intent);
            }
        });

        // Obtén las URLs de las imágenes desde Firebase Storage y comienza el ciclo de cambio de imagen
        firebaseManager.obtenerUrlsDeImagenes("imagenesrepositorio", new FirebaseManager.ObtenerUrlsCallback() {
            @Override
            public void onSuccess(List<String> urls) {
                imageUrls = urls;
                startImageCycle();  // Inicia el ciclo de imágenes una vez obtenidas las URLs
            }

            @Override
            public void onFailure(String errorMessage) {
                Toast.makeText(getActivity(), "Error al obtener imágenes: " + errorMessage, Toast.LENGTH_LONG).show();
            }
        });

        return view;
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            imageView.setImageURI(imageUri);
        }
    }

    private void uploadFile() {
        if (imageUri != null) {
            FirebaseUser user = auth.getCurrentUser();
            if (user != null) {
                String userId = user.getUid();
                StorageReference fileReference = storageReference.child("images/" + userId + "/" + System.currentTimeMillis() + ".jpg");

                fileReference.putFile(imageUri)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                Toast.makeText(getActivity(), "Upload successful", Toast.LENGTH_LONG).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getActivity(), "Upload failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
            }
        } else {
            Toast.makeText(getActivity(), "No file selected", Toast.LENGTH_SHORT).show();
        }
    }

    private void startImageCycle() {
        final long duration = 1000;  // Duración de la animación de transición (1 segundo)
        final long delay = 3000;     // Tiempo de espera entre cambios de imagen (5 segundos)

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (getActivity() != null && imageUrls != null && !imageUrls.isEmpty()) {
                    currentImageIndex = (currentImageIndex + 1) % imageUrls.size();

                    // Establecer la animación de desvanecimiento
                    repositorioDeImagenes.animate()
                            .alpha(0f)  // Establecer la transparencia a 0 (desvanecimiento)
                            .setDuration(duration)
                            .withEndAction(new Runnable() {
                                @Override
                                public void run() {
                                    // Cambiar la imagen cuando se completa el desvanecimiento
                                    Glide.with(getActivity())
                                            .load(imageUrls.get(currentImageIndex))
                                            .centerCrop()
                                            .placeholder(R.drawable.dalunga240)
                                            .into(repositorioDeImagenes);

                                    // Establecer la animación de aparición
                                    repositorioDeImagenes.animate()
                                            .alpha(1f)  // Establecer la transparencia a 1 (aparecer)
                                            .setDuration(duration)
                                            .start();
                                }
                            })
                            .start();

                    handler.postDelayed(this, delay); // Cambia la imagen cada 5 segundos
                }
            }
        }, delay);
    }


}
