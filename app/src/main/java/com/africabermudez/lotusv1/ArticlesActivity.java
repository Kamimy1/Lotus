package com.africabermudez.lotusv1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ArticlesActivity extends AppCompatActivity {

    /**
     * Activity que muestra los articulos de la base de datos
     *
     * @author Africa Maria Bermudez Mejias
     * @version 1
     */

    public ArrayList<Article> articleList;
    FirebaseDatabase firebaseDatabase;
    FirebaseAuth firebaseAuth;
    String userId;
    DatabaseReference usuarioRef;

    FloatingActionButton add_admin;

    DatabaseReference articulosRef;
    ListView listView;

    String categoria, titulo, UID, text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_articles);

        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        userId = firebaseAuth.getCurrentUser().getUid();
        usuarioRef = firebaseDatabase.getReference("users").child(userId);
        articulosRef = firebaseDatabase.getReference("articulos");

        add_admin = findViewById(R.id.fab);
        listView = findViewById(R.id.custom_list_view);

        /**
         * Configuracion del toolbar
         * */

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Artículos");

        botonInvisible();

        /**
         * Click que lleva a la activity de AgregarArticuloActivity
         * */

        add_admin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ArticlesActivity.this, AgregarArticuloActivity.class);
                finish();
                startActivity(i);
            }
        });

        /**
         * Obtiene los datos del articulo y compara las categorias
         * y dependiendo de la categoria se selecciona una imagen u otra para el articulo
         * Se crea un objeto de articulo y se aniade al arraylist de articulos para luego
         * agregar los objetos del arraylist a la listview
         * */

        articleList = new ArrayList<>();

        articulosRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot articleSnapshot : dataSnapshot.getChildren()) {
                    categoria = articleSnapshot.child("categoria").getValue(String.class);
                    titulo = articleSnapshot.child("titulo").getValue(String.class);
                    UID = articleSnapshot.child("UID").getValue(String.class);
                    text = articleSnapshot.child("texto").getValue(String.class);

                    if(categoria!=null){
                        if (categoria.equals("Ciclo y menstruacion")) {
                            int drawableId = R.drawable.ciclo_menstrual;
                            Article article = new Article(UID,drawableId, titulo, text, categoria);
                            articleList.add(article);
                        } else if (categoria.equals("Salud femenina")) {
                            int drawableId = R.drawable.salud_femenina;
                            Article article = new Article(UID,drawableId, titulo, text, categoria);
                            articleList.add(article);
                        } else if (categoria.equals("Sexual")) {
                            int drawableId = R.drawable.anticonceptivo;
                            Article article = new Article(UID,drawableId, titulo, text, categoria);
                            articleList.add(article);
                        } else if (categoria.equals("Medicina")) {
                            int drawableId = R.drawable.latido_del_corazon;
                            Article article = new Article(UID,drawableId, titulo, text, categoria);
                            articleList.add(article);
                        }
                    }

                }


                // Verifica si la lista es nula o vacía
                if (articleList != null && !articleList.isEmpty()) {
                    ListView listView = findViewById(R.id.custom_list_view);
                    MyAdapter adapter = new MyAdapter(getApplicationContext(), articleList);
                    listView.setAdapter(adapter);
                } else {
                    Toast.makeText(ArticlesActivity.this, "La lista esta vacia", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        /**
         * Click que recoge los datos del articulo y lleva a DetalleArticuloActivity
         * */

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Article selectedArticle = articleList.get(position);

                Intent intent = new Intent(ArticlesActivity.this, DetalleArticuloActivity.class);
                intent.putExtra("titulo", selectedArticle.getTitulo());
                intent.putExtra("imagen", selectedArticle.getImage());
                intent.putExtra("UID", selectedArticle.getUID());
                intent.putExtra("texto", selectedArticle.getTexto());
                intent.putExtra("categoria", selectedArticle.getCategoria());
                startActivity(intent);
            }
        });


        /**
         * Controla los eventos de navegacion del bottomnavigationbar
         * */

        BottomNavigationView bottomNavigationView = findViewById(R.id.BottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.articles);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.articles:
                    return true;
                case R.id.calendar:
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    finish();
                    return true;
                case R.id.settings:
                    startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    finish();
                    return true;
            }
            return false;
        });

    }

    /**
     * Identifica si el usuario es un administrador y si lo es ensenia elementos exclusivos
     * */

    public void botonInvisible() {

        usuarioRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    if (dataSnapshot.child("admin_code").exists()) {
                        add_admin.setVisibility(View.VISIBLE);
                    } else {
                        add_admin.setVisibility(View.GONE);
                    }
                } else {
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}