package com.africabermudez.lotusv1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ConfCicloActivity extends AppCompatActivity {

    /**
     * Activity que modifica a la base de datos los datos del ciclo del usuario
     *
     * @author Africa Maria Bermudez Mejias
     * @version 1
     */

    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    String userId = firebaseAuth.getCurrentUser().getUid();
    DatabaseReference usuarioRef = firebaseDatabase.getReference("users").child(userId);

    EditText ciclo, periodo;

    Button aceptar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conf_ciclo);

        ciclo = findViewById(R.id.duracion_ciclo);
        periodo = findViewById(R.id.duracion_periodo);
        aceptar = findViewById(R.id.aceptar);

        /**
         * Configuracion del toolbar
         * */

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Ciclo");
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_24);
        }

        obtenerDatos();

        /**
         * Recoge y comprueba los datos necesarios y los añade la base de datos
         * Notifica si ha habido fallos
         */

        aceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EditText ETdiasCiclo = findViewById(R.id.duracion_ciclo);
                EditText ETduracionPeriodo = findViewById(R.id.duracion_periodo);

                String diasCiclo = ETdiasCiclo.getText().toString().trim();
                String duracionPeriodo = ETduracionPeriodo.getText().toString().trim();

                if(!TextUtils.isEmpty(diasCiclo) && !TextUtils.isEmpty(duracionPeriodo)){

                    if (TextUtils.isDigitsOnly(diasCiclo) && TextUtils.isDigitsOnly(duracionPeriodo)) {

                        int ndiasCiclo = Integer.parseInt(diasCiclo);
                        int nduracionPeriodo = Integer.parseInt(duracionPeriodo);

                        DatabaseReference duracionCicloRef = usuarioRef.child("duracionCiclo");
                        duracionCicloRef.setValue(ndiasCiclo);

                        DatabaseReference duracionPeriodoRef = usuarioRef.child("duracionPeriodo");
                        duracionPeriodoRef.setValue(nduracionPeriodo);

                        obtenerDatos();

                        Toast.makeText(ConfCicloActivity.this, "Datos cambiados.", Toast.LENGTH_SHORT).show();


                    } else {
                        Toast.makeText(ConfCicloActivity.this, "Formato no válido", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(ConfCicloActivity.this, "Un campo o los campos no pueden estar vacios.", Toast.LENGTH_SHORT).show();
                }

            }
        });


    }

    /**
     * Obtiene los datos del ciclo del usuario de la base de datos y los ensenia en la interfaz
     */
    private void obtenerDatos(){

        usuarioRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                try{
                    int duracionCiclo = dataSnapshot.child("duracionCiclo").getValue(Integer.class);
                    int duracionRegla = dataSnapshot.child("duracionPeriodo").getValue(Integer.class);

                    ciclo.setText(String.valueOf(duracionCiclo));
                    periodo.setText(String.valueOf(duracionRegla));

                }catch(Exception e){
                    Toast.makeText(ConfCicloActivity.this, "No hay datos que mostrar", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

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