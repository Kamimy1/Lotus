package com.africabermudez.lotusv1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class DiaActivity extends AppCompatActivity {

    /**
     * Activity que muestra los sintomas aniadidos por el usuario en el dia seleccionado
     *
     * @author Africa Maria Bermudez Mejias
     * @version 1
     */

    Button sintomas;
    private int day;
    private int month;
    private int year;

    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    String userId = firebaseAuth.getCurrentUser().getUid();
    DatabaseReference usuarioRef = firebaseDatabase.getReference("users").child(userId);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dia);

        /**
         * Configuracion del toolbar
         * */

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Dia");
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_24);
        }

        sintomas = findViewById(R.id.sintomas);

        /**
         * Se obtiene la fecha seleccionado por el usuario desde el MainActivity
         */

        day = getIntent().getIntExtra("day", 0);
        month = getIntent().getIntExtra("month", 0);
        year = getIntent().getIntExtra("year", 0);

        actionBar.setTitle("Fecha seleccionada: " + day + "/" + (month) + "/" + year);

        cargarDatosDesdeFirebase();

        sintomas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(DiaActivity.this, SintomasActivity.class);
                i.putExtra("day", day);
                i.putExtra("month", month);
                i.putExtra("year", year);
                finish();
                startActivity(i);
            }
        });
    }


    /**
     * Recoge los sintomas aniadidos por el usuario y ensenia su nombre en un ListView
     */
    private void cargarDatosDesdeFirebase() {

        ArrayList<String> listaElementos = new ArrayList<>();

        String fecha = day + "-" + month + "-" + year;

        DatabaseReference fechaRef = usuarioRef.child(fecha);

        fechaRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    DataSnapshot sintomasSnapshot = dataSnapshot.child("sintomas");

                    // Se recorre los hijos de sintomas
                    for (DataSnapshot childSnapshot : sintomasSnapshot.getChildren()) {
                        String id = childSnapshot.getValue(String.class);

                        switch (id) {
                            case "1":
                                listaElementos.add("Regla ligera.");
                                break;
                            case "2":
                                listaElementos.add("Regla normal.");
                                break;
                            case "3":
                                listaElementos.add("Regla abundante.");
                                break;
                            case "4":
                                listaElementos.add("Relacciones con protección.");
                                break;
                            case "5":
                                listaElementos.add("Relacciones sin protección.");
                                break;
                            case "6":
                                listaElementos.add("No he practicado sexo.");
                                break;
                            case "7":
                                listaElementos.add("Alto deseo sexual.");
                                break;
                            case "8":
                                listaElementos.add("Dolor de cabeza.");
                                break;
                            case "9":
                                listaElementos.add("Acné.");
                                break;
                            case "10":
                                listaElementos.add("Mareos.");
                                break;
                            case "11":
                                listaElementos.add("Dolor en el pecho.");
                                break;
                            case "12":
                                listaElementos.add("Cólicos.");
                                break;
                            case "13":
                                listaElementos.add("Dolor de espalda.");
                                break;
                            case "14":
                                listaElementos.add("Feliz.");
                                break;
                            case "15":
                                listaElementos.add("Con energía.");
                                break;
                            case "16":
                                listaElementos.add("Activa.");
                                break;
                            case "17":
                                listaElementos.add("Calmada.");
                                break;
                            case "18":
                                listaElementos.add("Irritada.");
                                break;
                            case "19":
                                listaElementos.add("Desanimada.");
                                break;
                            case "20":
                                listaElementos.add("Triste.");
                                break;
                        }
                    }

                    if (listaElementos.isEmpty()) {
                        listaElementos.add("No hay datos");
                    }


                }else{
                    listaElementos.add("No hay datos");
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(DiaActivity.this, R.layout.list_item_dia, listaElementos) {
                    @Override
                    public View getView(int position, View convertView, ViewGroup parent) {
                        if (convertView == null) {
                            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_dia, parent, false);
                        }

                        String item = getItem(position);

                        TextView textViewTitle = convertView.findViewById(R.id.textViewTitle);

                        textViewTitle.setText(item);

                        return convertView;
                    }
                };

                ListView listView = findViewById(R.id.listViewSintomas);
                listView.setAdapter(adapter);

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
            Intent intent = new Intent(this, MainActivity.class);
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