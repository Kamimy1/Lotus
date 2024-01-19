package com.africabermudez.lotusv1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.MenuItem;
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

import java.util.HashMap;

public class AccountSettingsActivity extends AppCompatActivity {

    /**
     * Activity para mostrar y modificar datos del usuario
     *
     * @author Africa Maria Bermudez Mejias
     * @version 1
     */

    Button confirmarCambios;

    EditText newName, newUsername;

    TextView changePassword;


    /**
     *  Variables necesarias de conexion con firebase y realtime database
     *  para leer y modificar datos del usuario
     * */

    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser user = mAuth.getCurrentUser();
    String uid = user.getUid();
    String currentUserId = mAuth.getCurrentUser().getUid();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference ref = database.getReference("users");
    DatabaseReference userRef = database.getReference("users").child(uid);

    String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_settings);

        confirmarCambios = findViewById(R.id.confirm_button);

        newName= findViewById(R.id.nameChange);
        newUsername= findViewById(R.id.usernameChange);

        changePassword = findViewById(R.id.changePassword);

        /**
         * Configuracion del toolbar
         * */

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Tu cuenta");
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_24);
        }



        /**
         * Click que recoge los datos y comprueba si estan vacios o si son repetidos
         * en la base de datos, si se cumplen las condiciones necesarias, se actualizan los
         * datos en la base de datos
         * */


        confirmarCambios.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = String.valueOf(newName.getText());
                String username = String.valueOf(newUsername.getText());


                // Verifica que el UID del usuario que esta intentando actualizar sus datos
                // coincida con el UID del usuario actualmente autenticado en la aplicacion
                if (!uid.equals(currentUserId)) {
                    // El usuario actual no tiene permiso para actualizar los datos de este usuario
                    return;
                }

                HashMap<String, Object> updates = new HashMap<>();

                //Comprueba si hay campos vacios
                if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(username)) {

                    ValueEventListener valueEventListener = new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            boolean usuarioExiste = false;

                            // Obtener los datos de los usuarios
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                String usuario = snapshot.child("username").getValue(String.class);


                                // Comprueba si el nombre de usuario existen
                                if (usuario != null && usuario.equalsIgnoreCase(username)) {
                                    usuarioExiste = true;
                                }
                            }

                            if (usuarioExiste) {
                                Toast.makeText(AccountSettingsActivity.this, "El nombre de usuario ya existe", Toast.LENGTH_LONG).show();
                            }else{

                                //Actualizado de datos

                                updates.put("name", name);
                                updates.put("username", username);

                                userRef.updateChildren(updates);

                                newName.setText("");
                                newUsername.setText("");
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    };

                    ref.addValueEventListener(valueEventListener);


                }else{
                    Toast.makeText(AccountSettingsActivity.this, "Hay algún campo vacio.", Toast.LENGTH_LONG).show();
                }


            }
        });

        /**
         * Click que recoge los datos y los inserta en los campos deseados
         * */


        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Recoger el email y el username del usuario
                email = dataSnapshot.child("email").getValue(String.class);
                String username = dataSnapshot.child("username").getValue(String.class);
                String name = dataSnapshot.child("name").getValue(String.class);


                TextView textViewEmail = findViewById(R.id.emailText);
                TextView textViewUsername = findViewById(R.id.usernameText);
                TextView textViewName = findViewById(R.id.nameText);
                textViewEmail.setText(email);
                textViewUsername.setText(username);
                textViewName.setText(name);

                email = email.trim();

                newName.setText(name);
                newUsername.setText(username);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        /**
         * Envia un correo electronico con el cambio de contraseña
         */
        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(AccountSettingsActivity.this, "Revisa tu correo electrónico", Toast.LENGTH_LONG).show();
                        }else {
                            Toast.makeText(AccountSettingsActivity.this, "No se pudo enviar el correo", Toast.LENGTH_LONG).show();

                        }
                    }
                });
            }
        });



    }

    /**
     * Maneja eventos de seleccion de elementos del menu de opciones de la actividad,
     * en este caso maneja la flecha para volver atras
     * */

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            Intent intent = new Intent(this, SettingsActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}