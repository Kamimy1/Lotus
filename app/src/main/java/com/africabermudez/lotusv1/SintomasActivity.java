package com.africabermudez.lotusv1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Locale;


public class SintomasActivity extends AppCompatActivity {

    /**
     * Activity que inserta, lee y elimina los sintomas elegidos por el usuario mediante botones
     *
     * @author Africa Maria Bermudez Mejias
     * @version 1
     */

    ImageButton boton_1, boton_2, boton_3, boton_4, boton_5, boton_6, boton_7, boton_8, boton_9, boton_10, boton_11, boton_12,
    boton_13, boton_14, boton_15, boton_16, boton_17, boton_18, boton_19, boton_20;

    private int day;
    private int month;
    private int year;

    CheckBox primerDia;

    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    String userId = firebaseAuth.getCurrentUser().getUid();
    DatabaseReference usuarioRef = firebaseDatabase.getReference("users").child(userId);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sintomas_activity);

        primerDia = findViewById(R.id.switchPrimerDia);

        /**
         * Configuracion del toolbar
         * */

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Síntomas");
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_24);
        }

        declaracionBotones();

        /**
         * Se recoge el dia seleccionado por el usuario desde DiaActivity
         */
        day = getIntent().getIntExtra("day", 0);
        month = getIntent().getIntExtra("month", 0);
        year = getIntent().getIntExtra("year", 0);

        cargarDatosDesdeFirebase();

        comprobarCheckbox();

        /**
         * Accion del checbox que si es seleccionado inserta o modifica el "primerDiaRegla"
         */
        primerDia.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    String fecha = day + "-" + month + "-" + year;
                    DatabaseReference primerDiaReglaRef = usuarioRef.child("primerDiaRegla");
                    primerDiaReglaRef.setValue(fecha);
                } else {
                }
            }
        });

    }

    /**
     * Comprueba en la base de datos si el dia recogido es el mismo dia que "primerDiaRegla" y marca o no
     * el checkbox
     */
    private void comprobarCheckbox(){

        usuarioRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String primerDiaRegla = dataSnapshot.child("primerDiaRegla").getValue(String.class);

                    String fecha = day + "-" + month + "-" + year;

                    if (primerDiaRegla.toLowerCase().equals(fecha.toLowerCase(Locale.ROOT))) {
                        primerDia.setChecked(true);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
            }
        });
    }

    /**
     * Declaracion de los botones de los sintomas
     */
    private void declaracionBotones(){
        boton_1 = findViewById(R.id.boton_1);
        boton_2 = findViewById(R.id.boton_2);
        boton_3 = findViewById(R.id.boton_3);
        boton_4 = findViewById(R.id.boton_4);
        boton_5 = findViewById(R.id.boton_5);
        boton_6 = findViewById(R.id.boton_6);
        boton_7 = findViewById(R.id.boton_7);
        boton_8 = findViewById(R.id.boton_8);
        boton_9 = findViewById(R.id.boton_9);
        boton_10 = findViewById(R.id.boton_10);
        boton_11 = findViewById(R.id.boton_11);
        boton_12 = findViewById(R.id.boton_12);
        boton_13 = findViewById(R.id.boton_13);
        boton_14 = findViewById(R.id.boton_14);
        boton_15 = findViewById(R.id.boton_15);
        boton_16 = findViewById(R.id.boton_16);
        boton_17 = findViewById(R.id.boton_17);
        boton_18 = findViewById(R.id.boton_18);
        boton_19 = findViewById(R.id.boton_19);
        boton_20 = findViewById(R.id.boton_20);

        boton_1.setOnClickListener(onClickListener);
        boton_2.setOnClickListener(onClickListener);
        boton_3.setOnClickListener(onClickListener);
        boton_4.setOnClickListener(onClickListener);
        boton_5.setOnClickListener(onClickListener);
        boton_6.setOnClickListener(onClickListener);
        boton_7.setOnClickListener(onClickListener);
        boton_8.setOnClickListener(onClickListener);
        boton_9.setOnClickListener(onClickListener);
        boton_10.setOnClickListener(onClickListener);
        boton_11.setOnClickListener(onClickListener);
        boton_12.setOnClickListener(onClickListener);
        boton_13.setOnClickListener(onClickListener);
        boton_14.setOnClickListener(onClickListener);
        boton_15.setOnClickListener(onClickListener);
        boton_16.setOnClickListener(onClickListener);
        boton_17.setOnClickListener(onClickListener);
        boton_18.setOnClickListener(onClickListener);
        boton_19.setOnClickListener(onClickListener);
        boton_20.setOnClickListener(onClickListener);



    }

    /**
     * Click que dependiendo del boton seleccionado y si se cumplen algunas condiciones, se realiza
     * una accion u otra como insertar o eliminar datos en la base de datos
     */
    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.boton_1:
                    if (boton_1.isSelected()) {
                        boton_1.setSelected(false);
                        eliminarDatosDeFirebase("1");
                    } else {
                        boton_1.setSelected(true);
                        insertarDatosEnFirebase("1");
                        if(boton_2.isSelected()){
                            boton_2.setSelected(false);
                            eliminarDatosDeFirebase("2");
                        }else if(boton_3.isSelected()) {
                            boton_3.setSelected(false);
                            eliminarDatosDeFirebase("3");
                        }
                    }
                    break;
                case R.id.boton_2:
                    if (boton_2.isSelected()) {
                        boton_2.setSelected(false);
                        eliminarDatosDeFirebase("2");
                    } else {
                        boton_2.setSelected(true);
                        insertarDatosEnFirebase("2");
                        if(boton_1.isSelected()){
                            boton_1.setSelected(false);
                            eliminarDatosDeFirebase("1");
                        }else if(boton_3.isSelected()) {
                            boton_3.setSelected(false);
                            eliminarDatosDeFirebase("3");
                        }
                    }
                    break;
                case R.id.boton_3:
                    if (boton_3.isSelected()) {
                        boton_3.setSelected(false);
                        eliminarDatosDeFirebase("3");
                    } else {
                        boton_3.setSelected(true);
                        insertarDatosEnFirebase("3");
                        if(boton_2.isSelected()){
                            boton_2.setSelected(false);
                            eliminarDatosDeFirebase("2");
                        }else if(boton_1.isSelected()) {
                            boton_1.setSelected(false);
                            eliminarDatosDeFirebase("1");
                        }
                    }
                    break;
                case R.id.boton_4:

                    if(boton_4.isSelected()){
                        eliminarDatosDeFirebase("4");
                        boton_4.setSelected(false);
                    }else {
                        insertarDatosEnFirebase("4");
                        boton_4.setSelected(true);
                    }

                    break;
                case R.id.boton_5:
                    if(boton_5.isSelected()){
                        eliminarDatosDeFirebase("5");
                        boton_5.setSelected(false);
                    }else {
                        insertarDatosEnFirebase("5");
                        boton_5.setSelected(true);
                    }
                    break;
                case R.id.boton_6:
                    if(boton_6.isSelected()){
                        eliminarDatosDeFirebase("6");
                        boton_6.setSelected(false);
                    }else {
                        insertarDatosEnFirebase("6");
                        boton_6.setSelected(true);
                    }
                    break;

                case R.id.boton_7:
                    if(boton_7.isSelected()){
                        eliminarDatosDeFirebase("7");
                        boton_7.setSelected(false);
                    }else {
                        insertarDatosEnFirebase("7");
                        boton_7.setSelected(true);
                    }
                    break;

                case R.id.boton_8:
                    if(boton_8.isSelected()){
                        eliminarDatosDeFirebase("8");
                        boton_8.setSelected(false);
                    }else {
                        insertarDatosEnFirebase("8");
                        boton_8.setSelected(true);
                    }
                    break;

                case R.id.boton_9:
                    if(boton_9.isSelected()){
                        eliminarDatosDeFirebase("9");
                        boton_9.setSelected(false);
                    }else {
                        insertarDatosEnFirebase("9");
                        boton_9.setSelected(true);
                    }
                    break;

                case R.id.boton_10:
                    if(boton_10.isSelected()){
                        eliminarDatosDeFirebase("10");
                        boton_10.setSelected(false);
                    }else {
                        insertarDatosEnFirebase("10");
                        boton_10.setSelected(true);
                    }
                    break;

                case R.id.boton_11:
                    if(boton_11.isSelected()){
                        eliminarDatosDeFirebase("11");
                        boton_11.setSelected(false);
                    }else {
                        insertarDatosEnFirebase("11");
                        boton_11.setSelected(true);
                    }
                    break;

                case R.id.boton_12:
                    if(boton_12.isSelected()){
                        eliminarDatosDeFirebase("12");
                        boton_12.setSelected(false);
                    }else {
                        insertarDatosEnFirebase("12");
                        boton_12.setSelected(true);
                    }
                    break;

                case R.id.boton_13:
                    if(boton_13.isSelected()){
                        eliminarDatosDeFirebase("13");
                        boton_13.setSelected(false);
                    }else {
                        insertarDatosEnFirebase("13");
                        boton_13.setSelected(true);
                    }
                    break;

                case R.id.boton_14:
                    if(boton_14.isSelected()){
                        eliminarDatosDeFirebase("14");
                        boton_14.setSelected(false);
                    }else {
                        insertarDatosEnFirebase("14");
                        boton_14.setSelected(true);
                    }
                    break;

                case R.id.boton_15:
                    if(boton_15.isSelected()){
                        eliminarDatosDeFirebase("15");
                        boton_15.setSelected(false);
                    }else {
                        insertarDatosEnFirebase("15");
                        boton_15.setSelected(true);
                    }
                    break;

                case R.id.boton_16:
                    if(boton_16.isSelected()){
                        eliminarDatosDeFirebase("16");
                        boton_16.setSelected(false);
                    }else {
                        insertarDatosEnFirebase("16");
                        boton_16.setSelected(true);
                    }
                    break;

                case R.id.boton_17:
                    if(boton_17.isSelected()){
                        eliminarDatosDeFirebase("17");
                        boton_17.setSelected(false);
                    }else {
                        insertarDatosEnFirebase("17");
                        boton_17.setSelected(true);
                    }
                    break;

                case R.id.boton_18:
                    if(boton_18.isSelected()){
                        eliminarDatosDeFirebase("18");
                        boton_18.setSelected(false);
                    }else {
                        insertarDatosEnFirebase("18");
                        boton_18.setSelected(true);
                    }
                    break;

                case R.id.boton_19:
                    if(boton_19.isSelected()){
                        eliminarDatosDeFirebase("19");
                        boton_19.setSelected(false);
                    }else {
                        insertarDatosEnFirebase("19");
                        boton_19.setSelected(true);
                    }
                    break;

                case R.id.boton_20:
                    if(boton_20.isSelected()){
                        eliminarDatosDeFirebase("20");
                        boton_20.setSelected(false);
                    }else {
                        insertarDatosEnFirebase("20");
                        boton_20.setSelected(true);
                    }
                    break;


            }
        }
    };

    /**
     * Inserta en la base de datos el id del sintomas seleccionado
     * @param id
     */
    private void insertarDatosEnFirebase(final String id) {
        final String fecha = day + "-" + month + "-" + year;

        // Crea una referencia al nodo de fecha seleccionada
        DatabaseReference fechaRef = usuarioRef.child(fecha);

        // Verifica si ya existe un nodo con la misma fecha
        fechaRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Si el nodo ya existe, se obtiene la referencia a sintomas dentro del nodo existente
                    DatabaseReference sintomasRef = dataSnapshot.child("sintomas").getRef();
                    // Agrega el nuevo valor sin borrar lo que ya existe
                    sintomasRef.push().setValue(id);
                    Toast.makeText(SintomasActivity.this, "Datos guardados correctamente", Toast.LENGTH_SHORT).show();
                } else {
                    // Si el nodo no existe, se crea un nuevo nodo con la fecha y se inserta el valor id
                    fechaRef.child("id").setValue(fecha);
                    DatabaseReference sintomasRef = fechaRef.child("sintomas");
                    sintomasRef.push().setValue(id);
                    Toast.makeText(SintomasActivity.this, "Datos guardados correctamente", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(SintomasActivity.this, "Error al verificar los datos", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Elimina en la base de datos el id del sintomas deseleccionado
     * @param id
     */
    private void eliminarDatosDeFirebase(final String id) {
        // Se obitene una referencia al nodo del usuario actual en la base de datos
        String fecha = day + "-" + month + "-" + year;

        // Elimina el nodo correspondiente a la fecha seleccionada
        final DatabaseReference fechaRef = usuarioRef.child(fecha);
        final DatabaseReference sintomasRef = fechaRef.child("sintomas");
        Query query = sintomasRef.orderByValue().equalTo(id);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    snapshot.getRef().removeValue();
                }

                // Verifica si no quedan sintomas en el nodo de sintomas
                sintomasRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (!dataSnapshot.exists()) {
                            // Si no quedan sintomas, se elimina el nodo fechaRef
                            fechaRef.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(SintomasActivity.this, "Datos eliminados correctamente", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(SintomasActivity.this, "Error al eliminar datos", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        } else {
                            Toast.makeText(SintomasActivity.this, "Datos eliminados correctamente", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(SintomasActivity.this, "Error al eliminar datos", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(SintomasActivity.this, "Error al eliminar datos", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Recoge los sintomas seleccionados de un dia concreto y selecciona los botones correspondientes
     */
    private void cargarDatosDesdeFirebase() {

        String fecha = day + "-" + month + "-" + year;

        // Se obitene la referencia al nodo correspondiente a la fecha actual
        DatabaseReference fechaRef = usuarioRef.child(fecha);

        fechaRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Si existe el nodo de la fecha, se obtienen los datos de sintomas
                    DataSnapshot sintomasSnapshot = dataSnapshot.child("sintomas");

                    for (DataSnapshot childSnapshot : sintomasSnapshot.getChildren()) {
                        String id = childSnapshot.getValue(String.class);

                        // Selecciona el botón segun el ID obtenido
                        switch (id) {
                            case "1":
                                boton_1.setSelected(true);
                                break;
                            case "2":
                                boton_2.setSelected(true);
                                break;
                            case "3":
                                boton_3.setSelected(true);
                                break;
                            case "4":
                                boton_4.setSelected(true);
                                break;
                            case "5":
                                boton_5.setSelected(true);
                                break;
                            case "6":
                                boton_6.setSelected(true);
                                break;
                            case "7":
                                boton_7.setSelected(true);
                                break;
                            case "8":
                                boton_8.setSelected(true);
                                break;
                            case "9":
                                boton_9.setSelected(true);
                                break;
                            case "10":
                                boton_10.setSelected(true);
                                break;
                            case "11":
                                boton_11.setSelected(true);
                                break;
                            case "12":
                                boton_12.setSelected(true);
                                break;
                            case "13":
                                boton_13.setSelected(true);
                                break;
                            case "14":
                                boton_14.setSelected(true);
                                break;
                            case "15":
                                boton_15.setSelected(true);
                                break;
                            case "16":
                                boton_16.setSelected(true);
                                break;
                            case "17":
                                boton_17.setSelected(true);
                                break;
                            case "18":
                                boton_18.setSelected(true);
                                break;
                            case "19":
                                boton_19.setSelected(true);
                                break;
                            case "20":
                                boton_20.setSelected(true);
                                break;

                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Intent intent = new Intent(this, DiaActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("day", day);
            intent.putExtra("month", month);
            intent.putExtra("year", year);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }




}