package com.africabermudez.lotusv1;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SettingsActivity extends AppCompatActivity {

    /**
     * Activity que muestra datos del usuario y un menu de opciones
     *
     * @author Africa Maria Bermudez Mejias
     * @version 1
     */
    TextView accountSettings, requestFeatures, reportBugs, recordatorios, conf_ciclo, logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);

        accountSettings = findViewById(R.id.account_settings);
        requestFeatures = findViewById(R.id.request_features);
        reportBugs = findViewById(R.id.report_bugs);
        recordatorios = findViewById(R.id.recordatorios);
        conf_ciclo = findViewById(R.id.conf_ciclo);
        logout = findViewById(R.id.logout);

        /**
         * Configuracion del toolbar
         * */

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Ajustes");
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_24);
        }

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        String uid = user.getUid();

        /**
         * Recoge datos del usuario y los muestra en sus campos correspondientes
         */

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users").child(uid);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Recupera el email y el username del usuario
                String email = dataSnapshot.child("email").getValue(String.class);
                String username = dataSnapshot.child("username").getValue(String.class);


                TextView textViewEmail = findViewById(R.id.emailText);
                TextView textViewUsername = findViewById(R.id.usernameText);
                textViewEmail.setText(email);
                textViewUsername.setText(username);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        /**
         * Accion que lleva al usuario a la vista de AccountSettingsActivity
         */
        accountSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SettingsActivity.this, AccountSettingsActivity.class);
                startActivity(i);
                finish();
            }
        });

        /**
         * Accion que lleva al usuario a la vista de RecordatoriosActivity
         */
        recordatorios.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SettingsActivity.this, RecordatoriosActivity.class);
                startActivity(i);
                finish();
            }
        });

        /**
         * Accion que lleva al usuario a la vista de ConfCicloActivity
         */
        conf_ciclo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SettingsActivity.this, ConfCicloActivity.class);
                startActivity(i);
                finish();
            }
        });

        /**
         * Accion que lleva al usuario a la vista de EnviarSugerenciasActivity
         */
        requestFeatures.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SettingsActivity.this, EnviarSugerenciasActivity.class);
                startActivity(i);
                finish();
            }
        });

        /**
         * Accion que lleva al usuario a la vista de EnviarBugsActivity
         */
        reportBugs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SettingsActivity.this, EnviarBugsActivity.class);
                startActivity(i);
                finish();
            }
        });

        /**
         * Accion del boton que elimina la sesion del usuario y lo devuelve a LoginActivity
         */
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent i = new Intent(SettingsActivity.this, LoginActivity.class);
                startActivity(i);
                finish();

            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}