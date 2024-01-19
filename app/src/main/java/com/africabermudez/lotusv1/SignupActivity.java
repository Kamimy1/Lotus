package com.africabermudez.lotusv1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SignupActivity extends AppCompatActivity {

    /**
     * Activity que registra a un usuario
     *
     * @author Africa Maria Bermudez Mejias
     * @version 1
     */
    EditText signupName, signupEmail, signupUsername, signupPassword;
    TextView loginRedirectText, adminSignup;
    Button signupButton;
    private FirebaseDatabase database;
    private DatabaseReference reference;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        signupName = findViewById(R.id.signup_name);
        signupEmail = findViewById(R.id.signup_email);
        signupUsername = findViewById(R.id.signup_username);
        signupPassword = findViewById(R.id.signup_password);
        loginRedirectText = findViewById(R.id.loginRedirectText);
        signupButton = findViewById(R.id.signup_button);
        auth = FirebaseAuth.getInstance();
        adminSignup = findViewById(R.id.adminText);

        FirebaseUser currentUser = auth.getCurrentUser();

        /**
         * Comprueba si existe una sesion abierta
         */
        if (currentUser != null) {
            Intent i = new Intent(SignupActivity.this, MainActivity.class);
            startActivity(i);
            finish();
        }else{
            registro(auth);
        }

        /**
         * Accion que enviar al usuario a LoginActivity
         */
        loginRedirectText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        /**
         * Accion que enviar al usuario a SignupAdminActivity
         */
        adminSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignupActivity.this, SignupAdminActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }

    /**
     * Recoge y comprueba si existen en la base de datos los datos introducidos por el usuario
     * Si todo es correcto, el usuario es registrado
     * @param auth
     */
    public void registro(FirebaseAuth auth){

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = signupName.getText().toString().trim();
                String username = signupUsername.getText().toString().trim();
                String email =  signupEmail.getText().toString().trim();
                String password = signupPassword.getText().toString().trim();
                database = FirebaseDatabase.getInstance();
                reference = database.getReference("users");

                ValueEventListener valueEventListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        boolean nombreDeUsuarioExiste = false;
                        boolean emailExiste = false;

                        // Se obtienen los datos de los usuarios
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            String usuarioNombreDeUsuario = snapshot.child("username").getValue(String.class);
                            String usuarioEmail = snapshot.child("email").getValue(String.class);

                            // Comprueba si el nombre de usuario y el email existen
                            if (usuarioNombreDeUsuario != null && usuarioNombreDeUsuario.equalsIgnoreCase(username)) {
                                nombreDeUsuarioExiste = true;
                            }
                            if (usuarioEmail != null && usuarioEmail.equals(email)) {
                                emailExiste = true;
                            }
                        }

                        if (nombreDeUsuarioExiste && emailExiste) {
                            Snackbar.make(v, "El usuario y la contraseña ya existen", Snackbar.LENGTH_LONG).show();
                        } else if (nombreDeUsuarioExiste) {
                            Snackbar.make(v, "El nombre de usuario ya existe", Snackbar.LENGTH_LONG).show();
                        } else if (emailExiste) {
                            Snackbar.make(v, "El email ya existe", Snackbar.LENGTH_LONG).show();
                        } else {
                            if(email.isEmpty()){
                                signupEmail.setError("El email no puede estar vacío.");
                            }

                            if(name.isEmpty()){
                                signupName.setError("El nombre no puede estar vacío");
                            }

                            if(username.isEmpty()){
                                signupUsername.setError("El nombre de usuario no puede estar vacío");
                            }

                            if(password.isEmpty()){
                                signupPassword.setError("La contraseña no puede estar vacía.");
                            }

                            if(!name.isEmpty() && !username.isEmpty() && !email.isEmpty() && !password.isEmpty()){

                                if (password.length() < 8) {
                                    signupPassword.setError("La contraseña debe tener al menos 8 caracteres.");
                                } else if (!password.matches(".*\\d.*")) {
                                    signupPassword.setError("La contraseña debe contener al menos un número.");
                                } else if (!password.matches(".*[A-Z].*")) {
                                    signupPassword.setError("La contraseña debe contener al menos una letra mayúscula.");
                                } else if (!password.matches(".*[a-z].*")) {
                                    signupPassword.setError("La contraseña debe contener al menos una letra minúscula.");
                                } else if (!password.matches(".*[!@#$%^&*()-+_].*")) {
                                    signupPassword.setError("La contraseña debe contener al menos un carácter especial.");
                                } else {
                                    auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            if(task.isSuccessful()){
                                                FirebaseUser currentUser = auth.getCurrentUser();
                                                String UID = currentUser.getUid();
                                                HelperClass helperclass = new HelperClass(UID, name, email, username);
                                                reference.child(UID).setValue(helperclass);
                                                Toast.makeText(SignupActivity.this, "Registro Exitoso", Toast.LENGTH_LONG).show();
                                                startActivity(new Intent(SignupActivity.this, LoginActivity.class));
                                                finish();
                                            }else{
                                                Toast.makeText(SignupActivity.this, "Registro Fallido"+task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });
                                }

                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                };

                reference.addValueEventListener(valueEventListener);

            }
        });

    }


}