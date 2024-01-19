package com.africabermudez.lotusv1;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class EnviarBugsActivity extends AppCompatActivity {

    /**
     * Activity que envia un correo con los datos escritos con el usuario
     *
     * @author Africa Maria Bermudez Mejias
     * @version 1
     */

    private Button aceptar;

    EditText asunto, cuerpo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enviar_bugs);

        asunto = findViewById(R.id.asunto);
        cuerpo = findViewById(R.id.cuerpo);
        aceptar = findViewById(R.id.aceptar);

        /**
         * Configuracion del toolbar
         * */

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Informar de errores");
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_24);
        }

        /**
         * Accion del boton que lanza el cliente con los datos escritos por el usuario
         */
        aceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String enviarcorreo = "lotus.soporte@gmail.com";
                String enviarasunto = asunto.getText().toString();
                String enviarmensaje = cuerpo.getText().toString();

                if(enviarasunto.isEmpty() || enviarmensaje.isEmpty()){
                    Toast.makeText(EnviarBugsActivity.this, "Hay campos vacios", Toast.LENGTH_LONG).show();
                }else{

                    Intent intent = new Intent(Intent.ACTION_SEND);

                    intent.putExtra(Intent.EXTRA_EMAIL, new String[] { enviarcorreo });
                    intent.putExtra(Intent.EXTRA_SUBJECT, enviarasunto);
                    intent.putExtra(Intent.EXTRA_TEXT, enviarmensaje);

                    intent.setType("message/rfc822");

                    // Se lanza el cliente de correo
                    startActivity(Intent.createChooser(intent,"Elige un cliente de Correo:"));
                }
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