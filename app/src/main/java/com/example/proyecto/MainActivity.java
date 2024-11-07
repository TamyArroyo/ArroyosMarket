package com.example.proyecto;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    EditText email, pass;
    Button ingresar;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        email = findViewById(R.id.etEmail);
        pass = findViewById(R.id.etPassword);
        ingresar = findViewById(R.id.btnIngresar);

        ingresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userEmail = email.getText().toString().trim();
                String userPassword = pass.getText().toString().trim();

                // Validar si los campos de correo y contraseña no están vacíos
                if (userEmail.isEmpty() || userPassword.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Por favor ingresa tus credenciales", Toast.LENGTH_LONG).show();
                    return;
                }

                // Si es el usuario admin
                if (userEmail.equals("admin@admin.cl") && userPassword.equals("12345678")) {
                    // Redirigir al layout de Admin (Ingresado)
                    Intent i = new Intent(MainActivity.this, Ingresado.class);
                    startActivity(i);
                    finish();
                } else {
                    firebaseAuth.signInWithEmailAndPassword(userEmail, userPassword)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(getApplicationContext(), "Login Correcto", Toast.LENGTH_LONG).show();

                                        // Aquí es donde pasamos el ID del Modelo al cliente activity
                                        // **Este ID debe ser dinámico dependiendo de tu lógica de la app**
                                        int idModelo = 901;  // Por ejemplo, pasamos el ID 901 de un producto

                                        Intent i = new Intent(MainActivity.this, cliente.class);
                                        i.putExtra("ID_MODELO", idModelo);  // Aquí pasas el ID del Modelo
                                        startActivity(i);
                                        finish();
                                    } else {
                                        Toast.makeText(getApplicationContext(), "Autenticación Falló", Toast.LENGTH_LONG).show();
                                        Intent i = new Intent(MainActivity.this, Registro.class);
                                        startActivity(i);
                                        finish();
                                    }
                                }
                            });
                }
            }
        });

        // Manejo de padding para dispositivos con bordes
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}
