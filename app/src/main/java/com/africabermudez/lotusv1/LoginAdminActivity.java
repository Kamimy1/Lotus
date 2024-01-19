package com.africabermudez.lotusv1;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.developer.gbuttons.GoogleSignInButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginAdminActivity extends AppCompatActivity {

    /**
     * Activity en la que un administrador puede iniciar sesion y recuperar su contrasenia
     *
     * @author Africa Maria Bermudez Mejias
     * @version 1
     */

    private FirebaseAuth auth;
    private EditText loginEmail, loginPassword;
    private Button loginButton;
    private TextView signupRedirectText, forgotPassword, noAdmin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_admin);

        auth = FirebaseAuth.getInstance();
        loginEmail = findViewById(R.id.login_email);
        loginPassword = findViewById(R.id.login_password);
        loginButton = findViewById(R.id.login_button);
        signupRedirectText = findViewById(R.id.signupRedirectText);
        forgotPassword = findViewById(R.id.forgotPassword);
        noAdmin = findViewById(R.id.noLoginAdmin);

        /**
         * Recoge los datos escritos por el administrador y los comprueba, si todo es correcto el usuario
         * inicia sesion
         */
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email =  loginEmail.getText().toString().trim();
                String password = loginPassword.getText().toString().trim();

                if(!email.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    if(!password.isEmpty()){
                        auth.signInWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                Toast.makeText(LoginAdminActivity.this, "Inicio de sesión exitoso", Toast.LENGTH_LONG).show();
                                startActivity(new Intent(LoginAdminActivity.this, MainActivity.class));
                                finish();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(LoginAdminActivity.this, "Inicio de sesión fallido", Toast.LENGTH_LONG).show();

                            }
                        });
                    }else{
                        loginPassword.setError("La contraseña no puede estar vacía.");
                    }
                }else if(email.isEmpty()){
                    loginEmail.setError("El email no puede estar vacío.");
                }else{
                    loginEmail.setError("Porfavor, inserte un email válido.");
                }
            }
        });

        /**
         * Accion que lleva al iniciar sesion de los usuarios
         */

        noAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginAdminActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        /**
         * Accion que lleva al registro de administradores
         */

        signupRedirectText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginAdminActivity.this, SignupAdminActivity.class);
                startActivity(intent);
                finish();
            }
        });

        /**
         * Accion que muestra un dialogo donde el administrador debera escribir su email para recuperar
         * la contrasenia
         */
        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(LoginAdminActivity.this);
                View dialogView = getLayoutInflater().inflate(R.layout.dialog_forgot, null);
                EditText email = dialogView.findViewById(R.id.email);

                builder.setView(dialogView);
                AlertDialog dialog = builder.create();

                dialogView.findViewById(R.id.btnOk).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String Email = email.getText().toString();

                        if(TextUtils.isEmpty(Email) && !Patterns.EMAIL_ADDRESS.matcher(Email).matches()){
                            Toast.makeText(LoginAdminActivity.this, "Introduze tu email válido", Toast.LENGTH_LONG).show();
                            return;
                        }
                        auth.sendPasswordResetEmail(Email).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    Toast.makeText(LoginAdminActivity.this, "Revisa tu correo electrónico", Toast.LENGTH_LONG).show();
                                    dialog.dismiss();
                                }else {
                                    Toast.makeText(LoginAdminActivity.this, "No se pudo enviar el correo", Toast.LENGTH_LONG).show();

                                }
                            }
                        });
                    }
                });

                dialogView.findViewById(R.id.btnCancel).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                if(dialog.getWindow() != null){
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                }
                dialog.show();
            }
        });


    }
}