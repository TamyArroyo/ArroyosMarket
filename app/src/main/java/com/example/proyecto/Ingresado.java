package com.example.proyecto;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Ingresado extends AppCompatActivity {
    private EditText txtId, txtNombre, txtPrecio;
    private Button btnBuscar, btnAgregar, btnEliminar, btnModificar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_ingresado);
        txtId = findViewById(R.id.etIdProducto);
        txtNombre = findViewById(R.id.etNombreProducto);
        txtPrecio = findViewById(R.id.etPrecio);
        btnBuscar = findViewById(R.id.btnBuscar);
        btnAgregar = findViewById(R.id.btnAgregar);
        btnEliminar = findViewById(R.id.btnEliminar);
        btnModificar = findViewById(R.id.btnModificar);


        Buscar();
        Modificar();
        Eliminar();
        Agregar();


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void Agregar() {
        btnAgregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Validación de campos vacíos
                if (txtId.getText().toString().trim().isEmpty() || txtNombre.getText().toString().trim().isEmpty() || txtPrecio.getText().toString().trim().isEmpty()) {
                    ocultarTeclado();
                    Toast.makeText(Ingresado.this, "Complete los campos faltantes", Toast.LENGTH_LONG).show();
                } else {
                    // Obtener los datos del formulario
                    int id = Integer.parseInt(txtId.getText().toString());
                    String nombre = txtNombre.getText().toString();
                    double precio = Double.parseDouble(txtPrecio.getText().toString());

                    // Conectar con Firebase
                    FirebaseDatabase db = FirebaseDatabase.getInstance();
                    DatabaseReference dbref = db.getReference(Modelo.class.getSimpleName());

                    // Obtenemos todos los datos en Firebase para validar duplicados
                    dbref.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            // Validar que el ID no esté duplicado
                            boolean idDuplicado = false;
                            for (DataSnapshot x : snapshot.getChildren()) {
                                if (x.child("id").getValue().toString().equalsIgnoreCase(String.valueOf(id))) {
                                    idDuplicado = true;
                                    break;
                                }
                            }

                            // Si el ID está duplicado, mostrar mensaje y salir
                            if (idDuplicado) {
                                ocultarTeclado();
                                Toast.makeText(Ingresado.this, "Este ID " + id + " ya existe!!", Toast.LENGTH_LONG).show();
                                return; // Salimos del método sin continuar
                            }

                            // Validar que el nombre no esté duplicado
                            boolean nombreDuplicado = false;
                            for (DataSnapshot x : snapshot.getChildren()) {
                                if (x.child("nombre").getValue().toString().equalsIgnoreCase(nombre)) {
                                    nombreDuplicado = true;
                                    break;
                                }
                            }

                            if (nombreDuplicado) {
                                ocultarTeclado();
                                Toast.makeText(Ingresado.this, "Este Nombre " + nombre + " ya existe!!", Toast.LENGTH_LONG).show();
                                return;
                            }

                            // Crear el objeto Modelo con precio
                            Modelo modelo = new Modelo(id, nombre, precio);
                            dbref.push().setValue(modelo);
                            ocultarTeclado();
                            Toast.makeText(Ingresado.this, "Modelo agregado!!", Toast.LENGTH_LONG).show();

                            txtId.setText("");
                            txtNombre.setText("");
                            txtPrecio.setText(""); // Limpiar el campo de precio
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(Ingresado.this, "Error al acceder a la base de datos", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    private void ocultarTeclado() {
        // Aquí puedes agregar lógica para ocultar el teclado si lo deseas
    }


    private void Buscar() {
        btnBuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (txtId.getText().toString().trim().isEmpty()) {
                    ocultarTeclado();
                    Toast.makeText(Ingresado.this, "Ingrese el Id a buscar", Toast.LENGTH_LONG).show();
                } else {
                    int id = Integer.parseInt(txtId.getText().toString());
                    FirebaseDatabase db = FirebaseDatabase.getInstance();
                    DatabaseReference dbref = db.getReference(Modelo.class.getSimpleName());

                    dbref.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String strId = Integer.toString(id);
                            boolean res = false;
                            for (DataSnapshot x : snapshot.getChildren()) {
                                if (strId.equalsIgnoreCase(x.child("id").getValue().toString())) {
                                    res = true;
                                    ocultarTeclado();
                                    txtNombre.setText(x.child("nombre").getValue().toString());
                                    double precio = Double.parseDouble(x.child("precio").getValue().toString());
                                    txtPrecio.setText(String.valueOf(precio)); // Mostrar el precio

                                    // Redirigir a ProductoActivity con el ID
                                    Intent intent = new Intent(Ingresado.this, cliente.class);
                                    intent.putExtra("ID_PRODUCTO", id);
                                    startActivity(intent); // Iniciar la nueva actividad
                                    break;
                                }
                            }
                            if (!res) {
                                ocultarTeclado();
                                Toast.makeText(Ingresado.this, "Id (" + strId + ") no encontrado", Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {}
                    });
                }
            }
        });
    }


    private void Modificar() {
        btnModificar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (txtId.getText().toString().trim().isEmpty()
                        || txtNombre.getText().toString().trim().isEmpty()
                        || txtPrecio.getText().toString().trim().isEmpty()) {
                    ocultarTeclado();
                    Toast.makeText(Ingresado.this, "Complete los campos faltantes", Toast.LENGTH_LONG).show();
                } else {
                    int id = Integer.parseInt(txtId.getText().toString());
                    String nombre = txtNombre.getText().toString();
                    double precio = Double.parseDouble(txtPrecio.getText().toString());

                    FirebaseDatabase db = FirebaseDatabase.getInstance();
                    DatabaseReference dbref = db.getReference(Modelo.class.getSimpleName());

                    dbref.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String strId = Integer.toString(id);
                            boolean res = false;
                            for (DataSnapshot x : snapshot.getChildren()) {
                                if (x.child("id").getValue().toString().equalsIgnoreCase(strId)) {
                                    res = true;
                                    ocultarTeclado();
                                    x.getRef().child("nombre").setValue(nombre);
                                    x.getRef().child("precio").setValue(precio); // Actualizar el precio
                                    txtId.setText("");
                                    txtNombre.setText("");
                                    txtPrecio.setText(""); // Limpiar el campo
                                    break;
                                }
                            }
                            if (!res) {
                                ocultarTeclado();
                                Toast.makeText(Ingresado.this, "Id (" + strId + ") no encontrado, no se puede modificar", Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {}
                    });
                }
            }
        });
    }

    private void Eliminar() {
        btnEliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (txtId.getText().toString().trim().isEmpty()) {
                    ocultarTeclado();
                    Toast.makeText(Ingresado.this, "Ingrese el ID a Eliminar", Toast.LENGTH_LONG).show();
                } else {
                    int id = Integer.parseInt(txtId.getText().toString());
                    FirebaseDatabase db = FirebaseDatabase.getInstance();
                    DatabaseReference dbref = db.getReference(Modelo.class.getSimpleName());

                    dbref.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String strId = Integer.toString(id);
                            final boolean[] res = {false};

                            for (DataSnapshot x : snapshot.getChildren()) {
                                if (strId.equalsIgnoreCase(x.child("id").getValue().toString())) {
                                    AlertDialog.Builder a = new AlertDialog.Builder(Ingresado.this);
                                    a.setCancelable(false);
                                    a.setTitle("Consulta");
                                    a.setMessage("¿Está seguro de eliminar este modelo? (ID: " + strId + ")");
                                    a.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                        }
                                    });
                                    a.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            res[0] = true;
                                            ocultarTeclado();
                                            x.getRef().removeValue();
                                            Toast.makeText(Ingresado.this, "Modelo eliminado correctamente.", Toast.LENGTH_LONG).show();
                                        }
                                    });
                                    a.show();
                                    break;
                                }
                            }
                            if (res[0] == false) {
                                ocultarTeclado();
                                Toast.makeText(Ingresado.this, "Id (" + strId + ") no encontrado", Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(Ingresado.this, "Error al acceder a la base de datos", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }
}
