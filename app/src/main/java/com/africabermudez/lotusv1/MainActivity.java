package com.africabermudez.lotusv1;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.EventDay;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    /**
     * Activity que calcula los dias de menstruacion y ovulacion y esenia en el calendario
     * los sintomas aniadidos por el usuario
     *
     * @author Africa Maria Bermudez Mejias
     * @version 1
     */

    Button dia, conf_inicial;

    int day, month, year;

    CalendarView calendarView;

    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    String userId = firebaseAuth.getCurrentUser().getUid();
    DatabaseReference usuarioRef = firebaseDatabase.getReference("users").child(userId);

    private List<EventDay> events = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dia = findViewById(R.id.dia);

        calendarView = findViewById(R.id.calendario);

        conf_inicial = findViewById(R.id.inicial);

        botonInvisible();

        obtenerFecha();

        cargarDatosDesdeFirebase();

        calcularRegla();

        /**
         * Accion del boton que lleva al usuario a la ventana de ConfInicialActivity
         */
        conf_inicial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ConfInicialActivity.class);
                startActivity(intent);
                finish();

            }
        });

        /**
         * Accion del boton que lleva al usuario a la ventana de DiaActivity pasando la fecha
         * que esta seleccionada en el calendario
         */
        dia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Calendar selectedDate = calendarView.getSelectedDate();
                int day = selectedDate.get(Calendar.DAY_OF_MONTH);
                int month = selectedDate.get(Calendar.MONTH) + 1; // Agrega 1, ya que los meses en Calendar empiezan desde 0
                int year = selectedDate.get(Calendar.YEAR);

                Intent intent = new Intent(MainActivity.this, DiaActivity.class);
                intent.putExtra("day", day);
                intent.putExtra("month", month);
                intent.putExtra("year", year);
                startActivity(intent);
                finish();
            }
        });

        /**
         * Configuracion del toolbar
         * */

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Calendario");

        /**
         * Configuracion del BottomNavigationView
         */

        BottomNavigationView bottomNavigationView = findViewById(R.id.BottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.calendar);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.calendar:
                    return true;
                case R.id.articles:
                    startActivity(new Intent(getApplicationContext(), ArticlesActivity.class));
                    finish();
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    return true;
                case R.id.settings:
                    startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
                    finish();
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    return true;
            }
            return false;
        });

    }

    /**
     * Recoge de la base de datos si existen o no los datos "primerDiaRegla", "duracionCiclo" y "duracionPeriodo".
     * Dependiendo de si existen o no, se muestra un boton u otro
     */
    private void botonInvisible() {

        usuarioRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    if (dataSnapshot.child("primerDiaRegla").exists() && dataSnapshot.child("duracionCiclo").exists() && dataSnapshot.child("duracionPeriodo").exists()) {
                        conf_inicial.setVisibility(View.GONE);
                    }

                    if (!dataSnapshot.child("primerDiaRegla").exists()){
                        dia.setVisibility(View.GONE);
                    }
                } else {

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    /**
     * Metodo que recoge la fecha seleccionada del calendario
     */
    public void obtenerFecha() {
        Calendar selectedDate = calendarView.getSelectedDate();

        day = selectedDate.get(Calendar.DAY_OF_MONTH);
        month = selectedDate.get(Calendar.MONTH) + 1;
        year = selectedDate.get(Calendar.YEAR);

    }

    /**
     * Metodo que inserta en el calendario eventos relacionado con los sintomas
     * agregados por el usuario y calcula la ovulacion
     */
    private void cargarDatosDesdeFirebase() {

        events.clear();

        // Se obtiene el número de días en el mes actual
        Calendar calendar = Calendar.getInstance();
        int numDiasMes = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        for (int dia = 1; dia <= numDiasMes; dia++) {
            Calendar eventoCalendar = Calendar.getInstance();
            eventoCalendar.set(Calendar.DAY_OF_MONTH, dia);

            String fecha = eventoCalendar.get(Calendar.DAY_OF_MONTH) + "-" + (eventoCalendar.get(Calendar.MONTH) + 1) + "-" + eventoCalendar.get(Calendar.YEAR);

            // Se obtiene la referencia al nodo correspondiente a la fecha actual
            DatabaseReference fechaRef = usuarioRef.child(fecha);

            /**
             * Escucha un evento de cambio de valor en Firebase y realiza acciones basadas en los datos recibidos.
             * Crea y aniade objetos EventDay a una lista llamada events, dependiendo de los valores encontrados en la base de datos.
             */

            fechaRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists() && dataSnapshot.hasChildren()) {
                        DataSnapshot sintomasSnapshot = dataSnapshot.child("sintomas");

                        ArrayList<Integer> numeros = new ArrayList<>();

                        if (!sintomasSnapshot.hasChildren()) {

                            events.add(new EventDay(eventoCalendar, R.drawable.img_transparente));
                        }else{
                            for (DataSnapshot childSnapshot : sintomasSnapshot.getChildren()) {
                                String idString = childSnapshot.getValue(String.class);

                                int id = Integer.parseInt(idString);
                                numeros.add(id);
                            }

                            if (numeros.size() > 1 && numeros.contains(1)) {
                                events.add(new EventDay(eventoCalendar, R.drawable.img_1plus));
                            } else if (numeros.size() > 1 && numeros.contains(2)) {
                                events.add(new EventDay(eventoCalendar, R.drawable.img_2plus));
                            } else if (numeros.size() > 1 && numeros.contains(3)) {
                                events.add(new EventDay(eventoCalendar, R.drawable.img_3plus));
                            }else if(numeros.size()==1 && numeros.contains(1)){
                                events.add(new EventDay(eventoCalendar, R.drawable.img_1));
                            }else if(numeros.size()==1 && numeros.contains(2)){
                                events.add(new EventDay(eventoCalendar, R.drawable.img_2));
                            }else if(numeros.size()==1 && numeros.contains(3)){
                                events.add(new EventDay(eventoCalendar, R.drawable.img_3));
                            }else{
                                events.add(new EventDay(eventoCalendar, R.drawable.plus));
                            }

                        }

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        /**
         * Recoge los datos si existen necesarios para la calcula la ovulacion
         */

        usuarioRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    if (dataSnapshot.child("primerDiaRegla").exists()
                            && dataSnapshot.child("duracionCiclo").exists()
                            && dataSnapshot.child("duracionPeriodo").exists()) {
                        String primerDiaRegla = dataSnapshot.child("primerDiaRegla").getValue(String.class);
                        int duracionCiclo = dataSnapshot.child("duracionCiclo").getValue(Integer.class);
                        int duracionRegla = dataSnapshot.child("duracionPeriodo").getValue(Integer.class);

                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                        Calendar calendar = Calendar.getInstance();

                        try {
                            Date startDate = dateFormat.parse(primerDiaRegla);
                            calendar.setTime(startDate);


                            // Ajuste para tener en cuenta la duración del periodo
                            calendar.add(Calendar.DAY_OF_MONTH, duracionRegla);

                            /**
                             * Se calcula la ovulacion de este mes y los 6 siguientes. Por cada
                             * dia calculado se aniade un evento
                             */

                            for (int i = 0; i < 7; i++) {
                                calendar.add(Calendar.DAY_OF_MONTH, duracionCiclo - duracionRegla - 14);

                                Calendar dosDiaAntesCalendario = (Calendar) calendar.clone();
                                dosDiaAntesCalendario.add(Calendar.DAY_OF_MONTH, -2);
                                events.add(new EventDay(dosDiaAntesCalendario, R.drawable.chupete));

                                Calendar diaAntesCalendario = (Calendar) calendar.clone();
                                diaAntesCalendario.add(Calendar.DAY_OF_MONTH, -1);
                                events.add(new EventDay(diaAntesCalendario, R.drawable.chupete));

                                Calendar ovulacion = (Calendar) calendar.clone();
                                events.add(new EventDay(ovulacion, R.drawable.bebe));

                                Calendar diaDespuesCalendario = (Calendar) calendar.clone();
                                diaDespuesCalendario.add(Calendar.DAY_OF_MONTH, 1);
                                events.add(new EventDay(diaDespuesCalendario, R.drawable.chupete));

                                Calendar dosDiasDespuesCalendario = (Calendar) calendar.clone();
                                dosDiasDespuesCalendario.add(Calendar.DAY_OF_MONTH, 2);
                                events.add(new EventDay(dosDiasDespuesCalendario, R.drawable.chupete));

                                calendar.add(Calendar.DAY_OF_MONTH, 14);
                                calendar.add(Calendar.DAY_OF_MONTH, duracionRegla);
                            }

                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        calendarView.setEvents(events);
    }


    /**
     * Esta funcion realiza un cálculo basado en los datos obtenidos de Firebase.
     * Se verifica si los datos recibidos existen y se obtienen los valores deseados de los hijos
     * para calcular los dias en el que el usuario deberia menstruar en el mes actual y los 6 meses siguientes.
     * Por cada dia calculado se resalta de rojo
     */

    private void calcularRegla() {

        usuarioRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    if (dataSnapshot.child("primerDiaRegla").exists()
                            && dataSnapshot.child("duracionCiclo").exists()
                            && dataSnapshot.child("duracionPeriodo").exists()) {
                        String primerDiaRegla = dataSnapshot.child("primerDiaRegla").getValue(String.class);
                        int duracionCiclo = dataSnapshot.child("duracionCiclo").getValue(Integer.class);
                        int duracionRegla = dataSnapshot.child("duracionPeriodo").getValue(Integer.class);

                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                        Calendar calendar = Calendar.getInstance();

                        try {
                            Date fechaRegla = dateFormat.parse(primerDiaRegla);
                            calendar.setTime(fechaRegla);

                            List<Calendar> highlightedCalendars = new ArrayList<>();

                            for (int i = 0; i < 7; i++) {
                                Calendar inicioReglaCalendar = Calendar.getInstance();
                                inicioReglaCalendar.setTime(calendar.getTime());
                                highlightedCalendars.add(inicioReglaCalendar);

                                calendar.add(Calendar.DAY_OF_MONTH, duracionRegla - 1);

                                Calendar finReglaCalendar = Calendar.getInstance();
                                finReglaCalendar.setTime(calendar.getTime());
                                highlightedCalendars.add(finReglaCalendar);

                                Calendar tempCalendar = (Calendar) inicioReglaCalendar.clone();
                                tempCalendar.add(Calendar.DAY_OF_MONTH, 1);

                                while (tempCalendar.before(finReglaCalendar)) {
                                    highlightedCalendars.add((Calendar) tempCalendar.clone());
                                    tempCalendar.add(Calendar.DAY_OF_MONTH, 1);
                                }

                                calendar.add(Calendar.DAY_OF_MONTH, duracionCiclo - duracionRegla + 1);

                            }

                            calendarView.setHighlightedDays(highlightedCalendars);


                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


}