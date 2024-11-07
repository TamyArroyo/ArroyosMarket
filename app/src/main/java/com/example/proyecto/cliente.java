package com.example.proyecto;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class cliente extends AppCompatActivity {
    private TextView txtProductoNombre, txtProductoPrecio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cliente);

        txtProductoNombre = findViewById(R.id.txtProductoNombre);
        txtProductoPrecio = findViewById(R.id.txtProductoPrecio);

        // Obtener el ID del Modelo del Intent
        int idModelo = getIntent().getIntExtra("ID_MODELO", -1);  // Usamos "ID_MODELO" como clave

        if (idModelo == -1) {
            Toast.makeText(this, "Error: No se encontró el ID del modelo", Toast.LENGTH_SHORT).show();
            finish(); // Finalizar la actividad si no se encuentra el ID
            return;
        }

        // Conectar a la base de datos de Firebase
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference dbref = db.getReference("Modelo");  // Asegúrate de que "Modelo" es el nodo correcto en tu base de datos

        // Escuchar los datos de la base de datos
        dbref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                boolean found = false;
                for (DataSnapshot productSnapshot : snapshot.getChildren()) {
                    // Accede al campo "id" en cada hijo del nodo "Modelo"
                    int modeloId = productSnapshot.child("id").getValue(Integer.class);

                    // Verificar si el ID del modelo coincide con el ID recibido
                    if (modeloId == idModelo) {
                        String nombre = productSnapshot.child("nombre").getValue(String.class);
                        Double precio = productSnapshot.child("precio").getValue(Double.class);

                        // Mostrar los datos del Modelo en los TextViews
                        txtProductoNombre.setText(nombre);
                        txtProductoPrecio.setText(String.format("Precio: $%.2f", precio));
                        found = true;
                        break;
                    }
                }

                if (!found) {
                    Toast.makeText(cliente.this, "Modelo no encontrado", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(cliente.this, "Error al acceder a la base de datos", Toast.LENGTH_SHORT).show();
            }
        });

        // Ajuste de padding para barras del sistema
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}