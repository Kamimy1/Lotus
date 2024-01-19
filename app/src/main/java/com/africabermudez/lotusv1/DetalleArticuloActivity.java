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
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DetalleArticuloActivity extends AppCompatActivity {

    /**
     * Activity que agrega a la base de datos los datos iniciales del ciclo del usuario
     *
     * @author Africa Maria Bermudez Mejias
     * @version 1
     */

    private TextView tituloTextView;
    private ImageView imagenImageView;
    private TextView textoTextView;

    FloatingActionButton modify_admin, delete_admin;

    String titulo, texto, id, categoria;
    int imagen;

    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    String userId = firebaseAuth.getCurrentUser().getUid();
    DatabaseReference usuarioRef = firebaseDatabase.getReference("users").child(userId);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_articulo);

        tituloTextView = findViewById(R.id.tituloArt);
        imagenImageView = findViewById(R.id.imagenArt);
        textoTextView = findViewById(R.id.textArt);
        modify_admin = findViewById(R.id.fabModificar);
        delete_admin = findViewById(R.id.fabEliminar);

        /**
         * Recoge de ArticleActivity los datos del articulo seleccionado
         * y los muestra en sus campos correspondientes
         */
        Intent intent = getIntent();
        if (intent != null) {
            titulo = intent.getStringExtra("titulo");
            imagen = intent.getIntExtra("imagen", 0);
            texto = intent.getStringExtra("texto");
            id = intent.getStringExtra("UID");
            categoria = intent.getStringExtra("categoria");

            Article article = new Article(imagen, titulo);
            article.setTexto(texto);

            tituloTextView.setText(article.getTitulo());
            imagenImageView.setImageResource(article.getImage());
            textoTextView.setText(article.getTexto());
        }

        botonInvisible();

        /**
         * Accion del boton que obtiene los valores del articulo y los lleva a otra actividad
         */

        modify_admin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nuevoTitulo = tituloTextView.getText().toString();
                String nuevoTexto = textoTextView.getText().toString();

                Intent intent = new Intent(DetalleArticuloActivity.this, ModificarArticuloActivity.class);

                intent.putExtra("titulo", nuevoTitulo);
                intent.putExtra("texto", nuevoTexto);
                intent.putExtra("categoria", categoria);
                intent.putExtra("UID", id);

                startActivity(intent);
            }
        });

        /**
         * Accion del boton que muestra un dialogo para confirmar si el administrador quiere borrar
         * el articulo seleccionado y en caso de que quiera, este es borrado de la base de datos
         */

        delete_admin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(DetalleArticuloActivity.this);
                View dialogView = getLayoutInflater().inflate(R.layout.dialog_borrar, null);

                builder.setView(dialogView);
                AlertDialog dialog = builder.create();

                dialogView.findViewById(R.id.btnOk).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(DetalleArticuloActivity.this, ArticlesActivity.class);

                        DatabaseReference articuloRef = firebaseDatabase.getReference("articulos").child(id);

                        articuloRef.removeValue()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(DetalleArticuloActivity.this, "El articulo fue borrado correctamente.", Toast.LENGTH_SHORT).show();
                                        finish();
                                        startActivity(intent);
                                        dialog.dismiss();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(DetalleArticuloActivity.this, "El articulo no pude ser borrado correctamente.", Toast.LENGTH_SHORT).show();
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

        /**
         * Configuracion del toolbar
         * */

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(titulo);
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_24);
        }
    }

    /**
     * Recoge si existe o no "admin_code" dentro del usuario y dependiendo de si existe o no,
     * muestra o no los botonoes de modificar y borrar
     */

    private void botonInvisible() {

        usuarioRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    if (dataSnapshot.child("admin_code").exists()) {

                        modify_admin.setVisibility(View.VISIBLE);
                        delete_admin.setVisibility(View.VISIBLE);
                    } else {
                        modify_admin.setVisibility(View.GONE);
                        delete_admin.setVisibility(View.GONE);
                    }
                } else {

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
            Intent intent = new Intent(this, ArticlesActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}