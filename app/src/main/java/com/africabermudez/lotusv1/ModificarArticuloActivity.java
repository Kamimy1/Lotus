package com.africabermudez.lotusv1;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ModificarArticuloActivity extends AppCompatActivity {

    /**
     * Activity que modifica el articulo seleccionado por el administrador
     *
     * @author Africa Maria Bermudez Mejias
     * @version 1
     */

    private EditText tituloEdittext;
    private EditText textoEdittext;

    Spinner categorias;
    String titulo, texto, id, categoria;

    Button aceptar;
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modificar_articulo);

        tituloEdittext = findViewById(R.id.tituloArt);
        textoEdittext = findViewById(R.id.textArt);
        categorias = findViewById(R.id.spinner);
        aceptar = findViewById(R.id.aceptar);

        /**
         * Configuracion del toolbar
         * */

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Modificar artículo");
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_24);
        }

        obtenerCategorias();

        /**
         * Recoge los datos del articulo a modificar de la actividad DetalleArticuloActivity y los
         * ensenia en los campos correspondientes
         */

        Intent intent = getIntent();
        if (intent != null) {
            titulo = intent.getStringExtra("titulo");
            texto = intent.getStringExtra("texto");
            id = intent.getStringExtra("UID");
            categoria = intent.getStringExtra("categoria");

            Article article = new Article(id,titulo, texto, categoria);
            article.setTexto(texto);

            tituloEdittext.setText(article.getTitulo());
            textoEdittext.setText(article.getTexto());


        }

        /**
         * Accion del boton que recoge y comprueba los datos escritos para el articulo y los modifica
         * en al base de datos
         */

        aceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!tituloEdittext.getText().toString().trim().isEmpty() && !textoEdittext.getText().toString().trim().isEmpty()){

                    DatabaseReference articulosRef = firebaseDatabase.getReference("articulos");
                    DatabaseReference nuevoArticuloRef = articulosRef.child(id);

                    final int position = categorias.getSelectedItemPosition();
                    categoria = categorias.getItemAtPosition(position).toString();
                    titulo = tituloEdittext.getText().toString();
                    texto = textoEdittext.getText().toString();


                    nuevoArticuloRef.child("categoria").setValue(categoria);
                    nuevoArticuloRef.child("texto").setValue(texto);
                    nuevoArticuloRef.child("titulo").setValue(titulo);

                    Toast.makeText(ModificarArticuloActivity.this, "Artículo modificado.", Toast.LENGTH_LONG).show();

                    Intent intent = new Intent(ModificarArticuloActivity.this, ArticlesActivity.class);
                    finish();
                    startActivity(intent);

                }else{
                    Toast.makeText(ModificarArticuloActivity.this, "Hay algún campo vacío", Toast.LENGTH_LONG).show();
                }

            }
        });

    }

    /**
     * Recoge las categorias disponibles de la base de datos y las aniade a un Spinner
     */
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

                // Configuracion el Spinner con las categorías obtenidas
                ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(ModificarArticuloActivity.this, android.R.layout.simple_spinner_item, categoriasList);
                spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                categorias.setAdapter(spinnerAdapter);

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
            Intent intent = new Intent(this, ArticlesActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}