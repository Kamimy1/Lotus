package com.africabermudez.lotusv1;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

public class ConfInicialActivity extends AppCompatActivity {

    /**
     * Activity que agrega a la base de datos los datos iniciales del ciclo del usuario
     *
     * @author Africa Maria Bermudez Mejias
     * @version 1
     */

    Button calendario, aceptar;
    String selectedDate;

    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    String userId = firebaseAuth.getCurrentUser().getUid();
    DatabaseReference usuarioRef = firebaseDatabase.getReference("users").child(userId);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conf_inicial);

        calendario = findViewById(R.id.conf_calendario);
        aceptar = findViewById(R.id.aceptar);

        selectedDate= "";

        /**
         * Configuracion del toolbar
         * */

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Tu último periodo");
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_24);
        }

        /**
         * Accion del boton que muestra un DatePickerDialog
         */

        calendario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

                // Creacion el DatePickerDialog
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        ConfInicialActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                selectedDate = dayOfMonth + "-" + (month + 1) + "-" + year;
                            }
                        }, year, month, dayOfMonth
                );

                datePickerDialog.show();
            }
        });

        /**
         * Accion del boton que recoge y comprueba los datos y los aniade a la base de datos y
         * lleva al usuario al Main
         * Notifica si ha habido fallos
         */
        aceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EditText ETdiasCiclo = findViewById(R.id.duracion_ciclo);
                EditText ETduracionPeriodo = findViewById(R.id.duracion_periodo);

                String diasCiclo = ETdiasCiclo.getText().toString().trim();
                String duracionPeriodo = ETduracionPeriodo.getText().toString().trim();

                if(!TextUtils.isEmpty(diasCiclo) && !TextUtils.isEmpty(duracionPeriodo) && !selectedDate.isEmpty()){

                    if (TextUtils.isDigitsOnly(diasCiclo) && TextUtils.isDigitsOnly(duracionPeriodo)){

                        int ndiasCiclo = Integer.parseInt(diasCiclo);
                        int nduracionPeriodo = Integer.parseInt(duracionPeriodo);

                        DatabaseReference primerDiaReglaRef = usuarioRef.child("primerDiaRegla");
                        primerDiaReglaRef.setValue(selectedDate);

                        DatabaseReference duracionCicloRef = usuarioRef.child("duracionCiclo");
                        duracionCicloRef.setValue(ndiasCiclo);

                        DatabaseReference duracionPeriodoRef = usuarioRef.child("duracionPeriodo");
                        duracionPeriodoRef.setValue(nduracionPeriodo);

                        Intent i = new Intent(ConfInicialActivity.this, MainActivity.class);
                        startActivity(i);
                        finish();

                    }else {
                        Toast.makeText(ConfInicialActivity.this, "Formato no válido.", Toast.LENGTH_LONG).show();
                    }
                }else if(TextUtils.isEmpty(diasCiclo) && TextUtils.isEmpty(duracionPeriodo)){
                    Toast.makeText(ConfInicialActivity.this, "El campo o los campos no pueden vacios.", Toast.LENGTH_LONG).show();
                } else if (selectedDate.isEmpty()) {
                    Toast.makeText(ConfInicialActivity.this, "Debes seleccionar una fecha.", Toast.LENGTH_LONG).show();
                } else if ((TextUtils.isEmpty(diasCiclo) || TextUtils.isEmpty(duracionPeriodo)) && !selectedDate.isEmpty()) {
                    Toast.makeText(ConfInicialActivity.this, "Seleccina una fecha y hay algun campo vacío.", Toast.LENGTH_LONG).show();
                }else if(TextUtils.isEmpty(diasCiclo) || TextUtils.isEmpty(duracionPeriodo)){
                    Toast.makeText(ConfInicialActivity.this, "Hay un campo vacío", Toast.LENGTH_LONG).show();
                }

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