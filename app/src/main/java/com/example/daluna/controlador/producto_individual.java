package com.example.daluna.controlador;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.daluna.R;
import com.example.daluna.modelo.Producto;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class producto_individual extends AppCompatActivity {

    private ImageView imageView;
    private TextView tituloTextView;
    private TextView precioTextView;
    private TextView descripcionTextView;
    private ImageView imgAtras;
    private Button btnAgregarAlCarrito;

    private FirebaseManager firebaseManager;
    private FirebaseUser firebaseUser;

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
        btnAgregarAlCarrito = findViewById(R.id.prodindaddcarro);

        firebaseManager = new FirebaseManager();

        // Obtener los datos del intent
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String imageUrl = extras.getString("producto_imagen");
            String titulo = extras.getString("producto_nombre");
            String descripcion = extras.getString("producto_descripcion");
            double precio = extras.getDouble("producto_precio", 0.0);
            String productoId = extras.getString("producto_id");
            String categoria = extras.getString("producto_categoria");
            boolean estado = extras.getBoolean("producto_estado");

            // Crear el objeto Producto
            Producto producto = new Producto(productoId, titulo, descripcion, precio, imageUrl, categoria, estado);

            // Rellenar las vistas con los datos
            if (titulo != null) {
                tituloTextView.setText(titulo);
            }

            if (descripcion != null) {
                descripcionTextView.setText(descripcion);
            }
            precioTextView.setText(String.format("%.2f €", precio));

            // Cargar la imagen utilizando Glide
            RequestOptions requestOptions = new RequestOptions()
                    .diskCacheStrategy(DiskCacheStrategy.ALL);

            if (imageUrl != null) {
                Glide.with(this)
                        .load(imageUrl)
                        .apply(requestOptions)
                        .into(imageView);
            }

            // Configurar el botón para agregar al carrito
            btnAgregarAlCarrito.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Aquí puedes implementar la lógica para añadir el producto al carrito
                    // Por ejemplo, mostrar un mensaje de confirmación

                    // Realizar la consulta a Firebase para obtener la cantidad actual del producto en el carrito
                    firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                    if(firebaseUser!=null){
                        firebaseManager.obtenerCantidadProductoEnCarrito(producto, new FirebaseManager.ObtenerCantidadProductoCallback() {
                            @Override
                            public void onSuccess(int cantidad) {

                                if (cantidad < 10) {
                                    // Si la cantidad actual es menor que 10, permitir agregar una más al carrito
                                    firebaseManager.agregarProductoAlCarrito(producto);
                                    Toast.makeText(producto_individual.this, "Producto " + producto.getNombre() + " añadido al carrito", Toast.LENGTH_SHORT).show();
                                } else {
                                    // Si la cantidad actual es 10 o más, mostrar un mensaje de error
                                    Toast.makeText(producto_individual.this, "No es posible agregar más de 10 unidades de " + producto.getNombre() + " al carrito", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(String errorMessage) {
                                // Manejar el error
                                Toast.makeText(producto_individual.this, "Error al obtener cantidad del producto en el carrito: " + errorMessage, Toast.LENGTH_SHORT).show();
                            }
                        });

                    }else{
                        Toast.makeText(producto_individual.this, "Usuario no regsitrado", Toast.LENGTH_SHORT).show();
                    }

                }
            });
        }

        imgAtras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

}
