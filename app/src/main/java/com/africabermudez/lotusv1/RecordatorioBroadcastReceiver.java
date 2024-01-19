package com.africabermudez.lotusv1;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class RecordatorioBroadcastReceiver extends BroadcastReceiver {

    /**
     * Clase que configura la alarma
     *
     * @author Africa Maria Bermudez Mejias
     * @version 1
     */

    private static final String CHANNEL_ID = "recordatorio_channel";
    private static final int NOTIFICATION_ID = 123;

    @Override
    public void onReceive(Context context, Intent intent) {
        crearCanalNotificacion(context);
        mostrarNotificacion(context);
    }

    private void crearCanalNotificacion(Context context) {

        // Crea un canal de notificacion
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence nombre = "Canal de recordatorio";
            String descripcion = "Canal para recordatorios diarios";
            int importancia = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, nombre, importancia);
            channel.setDescription(descripcion);

            // Registra el canal en el sistema
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void mostrarNotificacion(Context context) {

        // Verifica permisos para mostrar notificaciones
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Lotus", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel);
        }

        // Crea un intent para abrir la actividad principal al hacer click en la notificacion
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        // Crea la notificacion
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.lotus)
                .setContentTitle("Recordatorio de Lotus")
                .setContentText("¡Es hora de tomar la pastilla AO!")
                .setContentIntent(pendingIntent)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        // Muestra la notificacion
        try {
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
            notificationManager.notify(NOTIFICATION_ID, builder.build());
        } catch (SecurityException e) {

        }
    }

}