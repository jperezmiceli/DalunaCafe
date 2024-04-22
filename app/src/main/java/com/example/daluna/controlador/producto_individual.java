package com.example.daluna.controlador;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import com.example.daluna.R;

public class producto_individual extends AppCompatActivity {

    private ImageView imageView;
    private TextView tituloTextView;
    private TextView precioTextView;
    private TextView descripcionTextView;
    private TextView alergenosTextView;
    private ImageView imgAtras;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_producto_individual);
        setTitle(""); // Elimina el título de la barra de acción

        // Inicialización de las vistas
        imageView = findViewById(R.id.imageView2);
        imgAtras = findViewById(R.id.prodindatras);
        tituloTextView = findViewById(R.id.prdindtitulo);
        precioTextView = findViewById(R.id.prodindprecio);
        descripcionTextView = findViewById(R.id.prodinddescripcion);
        alergenosTextView = findViewById(R.id.prodindalergenos);

        // Obtener los datos del intent
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String imageUrl = extras.getString("producto_imagen");
            String titulo = extras.getString("producto_nombre");
            String descripcion = extras.getString("producto_descripcion");
            String alergenos = extras.getString("producto_alergenos");
            double precio = extras.getDouble("producto_precio");

            // Rellenar las vistas con los datos
            tituloTextView.setText(titulo);
            descripcionTextView.setText(descripcion);
            alergenosTextView.setText(alergenos);
            precioTextView.setText(String.valueOf(precio));

            // Cargar la imagen utilizando Glide
            RequestOptions requestOptions = new RequestOptions()
                    .diskCacheStrategy(DiskCacheStrategy.ALL);

            Glide.with(this)
                    .load(imageUrl)
                    .apply(requestOptions)
                    .into(imageView);
        }
        imgAtras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish(); // Cierra la actividad actual y vuelve a la actividad anterior
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
