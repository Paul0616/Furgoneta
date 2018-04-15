package ro.duoline.furgoneta.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;
import java.util.Set;

import ro.duoline.furgoneta.LoginActivity;
import ro.duoline.furgoneta.R;
import ro.duoline.furgoneta.Utils.SaveSharedPreferences;
import ro.duoline.furgoneta.sofer.AllDocumentsSoferActivity;

/**
 * Created by Paul on 13/04/2018.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private String CHANNEL_ID = "DriversMessages";
    private NotificationManager notificationManager;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        //super.onMessageReceived(remoteMessage);
        String role = SaveSharedPreferences.getRol(getApplicationContext());
        if(role.equals("sofer")){
            Set<String> set = SaveSharedPreferences.getAssociatedlocations(getApplicationContext());
            Map<String, String> data = remoteMessage.getData();
            String fromLocation = data.get("location").toString();
            if(set.contains(fromLocation)) {
                sndNotification(remoteMessage);
            }
        }


    }
    private void sndNotification(RemoteMessage remoteMessage) {

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            setupChannels();
        }

        RemoteMessage.Notification notification = remoteMessage.getNotification();
        Intent intent = new Intent(this, LoginActivity.class); //TODO: poate ar trebui schimbat????
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notficationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(notification.getTitle())
                .setContentText(notification.getBody())
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(remoteMessage.getNotification().getBody()))
                .setPriority(Notification.PRIORITY_MAX);

        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notficationBuilder.build());
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setupChannels() {
        CharSequence driversChannelName = getString(R.string.notifications_drivers_channel_name);
        String driversChannelDescription = getString(R.string.notifications_drivers_channel_description);
        NotificationChannel driversChannel;
        driversChannel = new NotificationChannel(CHANNEL_ID, driversChannelName, NotificationManager.IMPORTANCE_DEFAULT);
        driversChannel.setDescription(driversChannelDescription);
        driversChannel.enableLights(true);
        driversChannel.setLightColor(Color.RED);
        driversChannel.enableVibration(true);
        if (notificationManager != null) {
            notificationManager.createNotificationChannel(driversChannel);
        }
    }
}
