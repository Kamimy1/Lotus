package com.africabermudez.lotusv1;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

public class RecordatoriosActivity extends AppCompatActivity {

    /**
     * Activity que programa una alarma
     *
     * @author Africa Maria Bermudez Mejias
     * @version 1
     */

    private TimePicker timePicker;
    private Button btnSetAlarm;
    private TextView txtSelectedTime;

    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    String userId = firebaseAuth.getCurrentUser().getUid();
    DatabaseReference usuarioRef = firebaseDatabase.getReference("users").child(userId);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recordatorios);

        btnSetAlarm = findViewById(R.id.anticonceptivos_button);
        txtSelectedTime = findViewById(R.id.txtSelectedTime);
        timePicker = findViewById(R.id.timePicker);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Recordatorios");
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_24);
        }

        //Busca si existe el campo recordatorio
        usuarioRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("recordatorio")) {
                    String recodatorioValue = dataSnapshot.child("recordatorio").getValue(String.class);
                    txtSelectedTime.setText("Hora seleccionada: "+recodatorioValue);
                } else {
                    txtSelectedTime.setText("No hay hora seleccionada.");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        btnSetAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                programarRecordatorio();
            }
        });
    }

    /**
     * Obitiene la hora seleccionada por el usuario y programa la alarma
     */
    private void programarRecordatorio() {
        // Se obtiene la hora seleccionada por el usuario
        int hora = timePicker.getCurrentHour();
        int minuto = timePicker.getCurrentMinute();

        // Crear un calendario con la hora seleccionada
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hora);
        calendar.set(Calendar.MINUTE, minuto);
        calendar.set(Calendar.SECOND, 0);

        // Verifica si la hora seleccionada ya ha pasado
        Calendar ahora = Calendar.getInstance();

        String seleccionada = hora + ":" + minuto;

        txtSelectedTime.setText("Hora seleccionada: "+seleccionada);

        usuarioRef.child("recordatorio").setValue(seleccionada, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError != null) {

                } else {
                }
            }
        });

        if (calendar.before(ahora)) {
            calendar.add(Calendar.DATE, 1); // Sumar un día si la hora ya paso
        }

        // Crea un intent para la notificacion
        Intent intent = new Intent(RecordatoriosActivity.this, RecordatorioBroadcastReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(RecordatoriosActivity.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        // Se obtiene el administrador de alarmas
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        // Cancela la alarma anterior (si existe)
        alarmManager.cancel(pendingIntent);

        // Programa la nueva alarma agregandio permisos
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        } else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        }

        Toast.makeText(this, "Recordatorio programado", Toast.LENGTH_SHORT).show();
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