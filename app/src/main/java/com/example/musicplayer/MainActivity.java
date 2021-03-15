package com.example.musicplayer;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.appwidget.AppWidgetProvider;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity  {
    public static final String  EXTRA_MESSAGE =  "1";
    int STORAGE_PERMISSION_CODE = 1 ;
    public static int song_position = -1 ;
    private ListView music_list ;
    public boolean hasPermission = false ;
    public  static List<AudioModel> Audio_list  ;
    public  List<String> Audio_Names = new ArrayList<String>();
    public static  MediaPlayer mp = new MediaPlayer();
    public static  boolean isPlaying ;
    public static NotificationCompat.Builder builder ;
    public static NotificationManagerCompat notificationManager ;
    public static  RemoteViews notificationLayout ;
    public static  RemoteViews notificationLayoutExpanded ;
    public  static  RemoteViews empty_notification ;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        createNotificationChannel();
        setRemoteview();
        build_Notification();
        notificationManager = NotificationManagerCompat.from(this);
        setup();
        show_notification();
        setVaribles();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getAudio(hasPermission);
        }
        setAudio_Name();
        setList();
        music_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                song_position = position ;
                playSong(position);

            }
        });

    }

    private  void setVaribles() {
        notificationLayoutExpanded.setImageViewResource(R.id.noti_p, R.drawable.ic_previous);
        notificationLayoutExpanded.setImageViewResource(R.id.noti_pl, R.drawable.ic_pause);
        notificationLayoutExpanded.setImageViewResource(R.id.noti_n, R.drawable.ic_next);
    }

    private void setRemoteview() {
        String PACKAGE_NAME = getApplicationContext().getPackageName();
        empty_notification = new RemoteViews(PACKAGE_NAME , R.layout.empty);
        notificationLayout = new RemoteViews(PACKAGE_NAME , R.layout.notification_small);
        notificationLayoutExpanded = new RemoteViews(PACKAGE_NAME , R.layout.notification_large);
    }

    public  static  void show_notification() {

        if(isPlaying){
            builder.setNotificationSilent() ;
            notificationLayoutExpanded.setTextViewText(R.id.song_name_title,Audio_list.get(song_position).getaName());
            notificationLayout.setTextViewText(R.id.notification_title,Audio_list.get(song_position).getaName());
            builder.setContentTitle(Audio_list.get(song_position).getaName());
            builder.setCustomContentView(notificationLayout)
                    .setCustomBigContentView(notificationLayoutExpanded) ;
        }
        else {
       //     builder.setContentTitle("No Song is Playing");
            builder.setNotificationSilent();
            builder.setCustomContentView(empty_notification)
                    .setCustomBigContentView(empty_notification) ;
        }
        notificationManager.notify(1, builder.build());
    }




    private void build_Notification() {
         builder = new NotificationCompat.Builder(getApplicationContext(), "1")
                 .setContentTitle("No Song is Playing")
                .setSmallIcon(R.drawable.ic_notification)
                .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                .setCustomContentView(notificationLayout)
                .setCustomBigContentView(notificationLayoutExpanded) ;
               //s .build();
    }


    private void playSong(int position) {
        Intent intent = new Intent(this , Player.class );
        String message = String.valueOf(position);
        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);

    }

    private void setAudio_Name() {
        for ( AudioModel am : Audio_list){
            Audio_Names.add(am.getaName());
        }
    }

    private void setList() {
        ArrayAdapter<String> myadaptor = new ArrayAdapter<String>(this , R.layout.support_simple_spinner_dropdown_item , Audio_Names);
        music_list.setAdapter(  myadaptor    );
      

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void getAudio(boolean hasPermission) {

        if(hasPermission){
            Audio_list =  getData.getAllAudioFromDevice(getApplicationContext());
        }
    }


    private void setup() {
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            hasPermission = true ;


        } else {
            requestStoragePermission();
        }

        music_list = (ListView) findViewById(R.id.id_musiclist);
        isPlaying = false ;
    }

    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(this,
                new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == STORAGE_PERMISSION_CODE)  {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission GRANTED", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permission DENIED", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Log.i("======", "createNotificationChannel: ");
            CharSequence name = "Chanel_1";
            String description = "TO show Music ";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("1", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
            notificationLayout = new RemoteViews(getPackageName(), R.layout.notification_small);
            notificationLayoutExpanded = new RemoteViews(getPackageName(), R.layout.notification_large);
        }
    }

}
