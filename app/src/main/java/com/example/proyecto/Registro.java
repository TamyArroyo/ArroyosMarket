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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Registro extends AppCompatActivity {
    EditText emailR, passR, nombreCompleto;
    Button registrar;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseDatabase database = FirebaseDatabase.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registro);

        emailR = findViewById(R.id.etEmailRegistro);
        passR = findViewById(R.id.etPasswordRegistro);
        nombreCompleto = findViewById(R.id.etENombreCompleto);
        registrar = findViewById(R.id.btnRegistrar);

        registrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email = emailR.getText().toString().trim();
                String password = passR.getText().toString().trim();
                String nombre = nombreCompleto.getText().toString().trim();

                if (email.isEmpty() || password.isEmpty() || nombre.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Por favor completa todos los campos", Toast.LENGTH_LONG).show();
                    return;
                }


                firebaseAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {

                                    Toast.makeText(getApplicationContext(), "Registro exitoso", Toast.LENGTH_LONG).show();


                                    String userId = firebaseAuth.getCurrentUser().getUid();
                                    DatabaseReference userRef = database.getReference("Usuarios").child(userId);


                                    userRef.child("nombreCompleto").setValue(nombre);
                                    userRef.child("email").setValue(email);

                                    Intent i = new Intent(Registro.this, MainActivity.class);
                                    startActivity(i);
                                    finish();
                                } else {
                                    Toast.makeText(getApplicationContext(), "Falló el Registro", Toast.LENGTH_LONG).show();
                                    emailR.setText("");
                                    passR.setText("");
                                    nombreCompleto.setText("");
                                }
                            }
                        });
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}
