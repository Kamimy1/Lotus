package com.africabermudez.lotusv1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AgregarArticuloActivity extends AppCompatActivity {

    /**
     * Activity en la cual los admins pueden agregar un artículo
     *
     * @author Africa Maria Bermudez Mejias
     * @version 1
     */

    Button aceptar;

    Spinner spinner;

    EditText tituloET, textoET;

    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_articulo);

        aceptar = findViewById(R.id.aceptar);
        spinner = findViewById(R.id.spinner);
        tituloET = findViewById(R.id.tituloArt);
        textoET = findViewById(R.id.textArt);

        /**
         * Configuracion del toolbar
         * */

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Agregar artículo");
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_24);
        }

        obtenerCategorias();

        /**
         * Click que recoge los datos de la activity y comprueba si estan vacios, si se
         * cumples las condiciones para que se pueda agregarse la base de datos, se agrega y cambia
         * a la activity de los articulos
         * */

        aceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = UUID.randomUUID().toString();
                final int position = spinner.getSelectedItemPosition();
                String categoria = spinner.getItemAtPosition(position).toString();
                String titulo = tituloET.getText().toString();
                String texto = textoET.getText().toString();

                if(!titulo.isEmpty() && !texto.isEmpty()){

                    DatabaseReference articulosRef = firebaseDatabase.getReference("articulos");
                    DatabaseReference nuevoArticuloRef = articulosRef.child(id);
                    nuevoArticuloRef.child("UID").setValue(id);
                    nuevoArticuloRef.child("categoria").setValue(categoria);
                    nuevoArticuloRef.child("texto").setValue(texto);
                    nuevoArticuloRef.child("titulo").setValue(titulo);

                    Toast.makeText(AgregarArticuloActivity.this, "Artículo añadido", Toast.LENGTH_LONG).show();

                    Intent intent = new Intent(AgregarArticuloActivity.this, ArticlesActivity.class);
                    finish();
                    startActivity(intent);

                }else{
                    Toast.makeText(AgregarArticuloActivity.this, "Hay campos vacíos", Toast.LENGTH_LONG).show();
                }


            }
        });


    }

    /**
     * Obtiene las categorías de la base de datos y las guarda en un spinner
     * */
    private void obtenerCategorias(){

        DatabaseReference categoriasRef = firebaseDatabase.getReference("categorias");

        categoriasRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<String> categoriasList = new ArrayList<>();
                for (DataSnapshot categoriaSnapshot : dataSnapshot.getChildren()) {
                    String categoria = categoriaSnapshot.getValue(String.class);
                    categoriasList.add(categoria);
                }

                ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(AgregarArticuloActivity.this, android.R.layout.simple_spinner_item, categoriasList);
                spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(spinnerAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

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
            Intent intent = new Intent(this, ArticlesActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}