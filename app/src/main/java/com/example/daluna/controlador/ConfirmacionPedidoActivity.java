package com.example.daluna.controlador;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.daluna.R;
import com.example.daluna.adaptadores.ConfirmacionPedidoAdapter;
import com.example.daluna.modelo.Venta;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ConfirmacionPedidoActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ConfirmacionPedidoAdapter adapter;
    private List<Venta> pedidoList;
    private Spinner filtroSpinner;
    private String filtroEstado = "en espera"; // Estado predeterminado
    private Button exitButton;
    private FirebaseAuth firebaseAuth;
    private GoogleSignInClient googleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmacion_pedido);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        filtroSpinner = findViewById(R.id.filtro_spinner);
        exitButton = findViewById(R.id.button_exit);

        pedidoList = new ArrayList<>();
        adapter = new ConfirmacionPedidoAdapter(pedidoList, this);
        recyclerView.setAdapter(adapter);

        // Configurar el spinner para filtrar
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.estados_pedido, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        filtroSpinner.setAdapter(spinnerAdapter);

        filtroSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                filtroEstado = parent.getItemAtPosition(position).toString();
                fetchVentas();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // Initialize FirebaseAuth and GoogleSignInClient
        firebaseAuth = FirebaseAuth.getInstance();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);

        exitButton.setOnClickListener(v -> signOut());

        fetchVentas();
    }

    private void fetchVentas() {
        FirebaseDatabase.getInstance().getReference().child("ventas")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        pedidoList.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            Venta venta = dataSnapshot.getValue(Venta.class);
                            if (venta != null && venta.getEstado().equals(filtroEstado)) {
                                pedidoList.add(venta);
                                notifyNewOrder(venta); // Notificar nuevo pedido
                            }
                        }
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Manejar error
                    }
                });
    }

    private void notifyNewOrder(Venta venta) {
        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("new_order_channel",
                    "New Order Notifications",
                    NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("Notifications for new orders");
            channel.setSound(soundUri, null);
            notificationManager.createNotificationChannel(channel);
        }

        Notification notification = new NotificationCompat.Builder(this, "new_order_channel")
                .setContentTitle("Nuevo pedido")
                .setContentText("Pedido ID: " + venta.getNumeroPedido())
                .setSmallIcon(R.drawable.logo)
                .setSound(soundUri)
                .build();

        notificationManager.notify(1, notification);
    }

    private void signOut() {
        firebaseAuth.signOut();
        googleSignInClient.signOut().addOnCompleteListener(this,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(ConfirmacionPedidoActivity.this, "Sesión cerrada", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(ConfirmacionPedidoActivity.this, InicioSesion.class));
                        finish();
                    }
                });
    }
}
