package com.example.musicplayer;


import android.app.IntentService;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;

import android.util.Log;
import android.view.View;

import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;



import java.util.Random;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class Player extends AppCompatActivity {
    static int positon;
    private static final String MyOnClick_p = "myOnClickTag_p", MyOnClick_pl = "myOnClickTag_pl", MyOnClick_n = "myOnClickTag_n";
    public static TextView song_name;
    public static TextView song_album;
    public static TextView song_artist;
    public static TextView durection;
    public static TextView current;
    public static SeekBar seek_bar;
    public  static ImageView cover , bg_cover ;
    public static ImageButton pause;
    public ImageButton back;
    public ImageButton next;
    public ImageButton previous;
    public ImageButton loop;
    public ImageButton shuffle;
    public static boolean is_pause = false;
    public static boolean is_shuffle = false;
    public boolean is_loop = false;

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.music_player);
        setup();
        play_Song();
        MainActivity.notificationLayoutExpanded.setOnClickPendingIntent(R.id.noti_p,
                getPendingSelfIntent(this, MyOnClick_p));
        MainActivity.notificationLayoutExpanded.setOnClickPendingIntent(R.id.noti_pl,
                getPendingSelfIntent(this, MyOnClick_pl));
        MainActivity.notificationLayoutExpanded.setOnClickPendingIntent(R.id.noti_n,
                getPendingSelfIntent(this, MyOnClick_n));
        MainActivity.show_notification();
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (is_shuffle) {
                    MainActivity.mp.stop();
                    Random rand = new Random();
                    positon = rand.nextInt(MainActivity.Audio_list.size());
                    MainActivity.song_position = positon;
                    is_pause = false;
                    pause.setBackgroundResource(R.drawable.ic_pause);
                    MainActivity.notificationLayoutExpanded.setImageViewResource(R.id.noti_pl, R.drawable.ic_pause);
                    play_Song();
                } else {
                    MainActivity.mp.stop();
                    positon++;
                    MainActivity.song_position = positon;
                    is_pause = false;
                    pause.setBackgroundResource(R.drawable.ic_pause);
                    MainActivity.notificationLayoutExpanded.setImageViewResource(R.id.noti_pl, R.drawable.ic_pause);
                    play_Song();
                }
            }
        });
        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.mp.stop();
                positon--;
                MainActivity.song_position = positon;
                is_pause = false;
                pause.setBackgroundResource(R.drawable.ic_pause);
                MainActivity.notificationLayoutExpanded.setImageViewResource(R.id.noti_pl, R.drawable.ic_pause);
                play_Song();
            }
        });
        seek_bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    MainActivity.mp.seekTo(progress * MainActivity.mp.getDuration() / 100);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (is_pause) {
                    MainActivity.mp.start();
                    is_pause = false;
                    pause.setBackgroundResource(R.drawable.ic_pause);
                    MainActivity.notificationLayoutExpanded.setImageViewResource(R.id.noti_pl, R.drawable.ic_pause);
                    MainActivity.show_notification();
                } else {
                    MainActivity.mp.pause();
                    is_pause = true;
                    pause.setBackgroundResource(R.drawable.ic_resume);
                    MainActivity.notificationLayoutExpanded.setImageViewResource(R.id.noti_pl, R.drawable.ic_resume);
                    MainActivity.show_notification();
                }
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        final Handler hd = new Handler();
        hd.post(new Runnable() {
            @Override
            public void run() {
                int currentPosition = MainActivity.mp.getCurrentPosition() / 1000;
                int total = MainActivity.mp.getDuration() / 1000;
                current.setText(currentPosition / 60 + ":" + currentPosition % 60);
                seek_bar.setProgress(currentPosition * 100 / total);
                hd.postDelayed(this, 10);
            }
        });
        MainActivity.mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if (is_loop) {
                    MainActivity.mp.stop();
                    is_pause = false;
                    pause.setBackgroundResource(R.drawable.ic_pause);
                    play_Song();
                } else if (is_shuffle) {
                    MainActivity.mp.stop();
                    Random rand = new Random();
                    positon = rand.nextInt(MainActivity.Audio_list.size());
                    MainActivity.song_position = positon;
                    is_pause = false;
                    pause.setBackgroundResource(R.drawable.ic_pause);
                    play_Song();
                } else {
                    MainActivity.mp.stop();
                    positon++;
                    MainActivity.song_position = positon;
                    is_pause = false;
                    pause.setBackgroundResource(R.drawable.ic_pause);
                    play_Song();
                }
            }
        });

        loop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (is_loop) {
                    is_loop = false;
                    loop.setBackgroundResource(R.drawable.ic_not);
                } else {
                    is_loop = true;
                    loop.setBackgroundResource(0);
                }
            }
        });
        shuffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (is_shuffle) {
                    is_shuffle = false;
                    shuffle.setBackgroundResource(R.drawable.ic_not);
                } else {
                    is_shuffle = true;
                    shuffle.setBackgroundResource(0);
                }
            }
        });
    }


    protected PendingIntent getPendingSelfIntent(Context context, String action) {
        Intent intent = new Intent(context, NotificationHandeler.class );
     //   Intent intent = new Intent("Button_clicked");
        intent.setAction(action);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private void setup() {
        song_album = findViewById(R.id.txt_album);
        song_name = findViewById(R.id.txt_name);
        song_artist = findViewById(R.id.txt_artist);
        cover = findViewById(R.id.cover);
        bg_cover = findViewById(R.id.bg_cover);
        pause = findViewById(R.id.btn_pause);
        back = findViewById(R.id.btn_back);
        seek_bar = findViewById(R.id.seek_bar);
        durection = findViewById(R.id.txt_durection);
        seek_bar = findViewById(R.id.seek_bar);
        current = findViewById(R.id.txt_current);
        next = findViewById(R.id.btn_next);
        previous = findViewById(R.id.btn_previous);
        loop = findViewById(R.id.btn_loop);
        shuffle = findViewById(R.id.btn_shuffle);
        icon_setup();
        positon = MainActivity.song_position ;
    }

    private void icon_setup() {
        pause.setBackgroundResource(R.drawable.ic_pause);
        next.setBackgroundResource(R.drawable.ic_next);
        previous.setBackgroundResource(R.drawable.ic_previous);
        back.setBackgroundResource(R.drawable.ic_back);
        loop.setBackgroundResource(R.drawable.ic_not);
        shuffle.setBackgroundResource(R.drawable.ic_not);
    }


    private static void play_Song() {

        if (MainActivity.mp != null && MainActivity.isPlaying) {
            MainActivity.mp.reset();
        }
        setVaribles();

        try {
            Log.i("-------------", "play_Song: -------------------- ");
            Log.i("---------", "play_Song: "+positon);
            MainActivity.mp.setDataSource(MainActivity.Audio_list.get(positon).getaPath());
            MainActivity.mp.prepare();
            MainActivity.mp.start();
            player_setup();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void player_setup() {
        MainActivity.isPlaying = true;
        int time = MainActivity.mp.getDuration() / 1000;
        durection.setText(time / 60 + ":" + time % 60);
        current.setText("0:00");
        seek_bar.setProgress(0);
        setup_show_notification();
    }

    private static void setup_show_notification() {
        MainActivity.builder.setContentTitle(MainActivity.Audio_list.get(positon).getaName());
        MainActivity.notificationLayoutExpanded.setImageViewResource(R.id.noti_pl, R.drawable.ic_pause);
        MainActivity.show_notification();
    }


    private static void setVaribles() {
        song_album.setText(MainActivity.Audio_list.get(positon).getaAlbum());
        song_name.setText(MainActivity.Audio_list.get(positon).getaName());
        song_artist.setText(MainActivity.Audio_list.get(positon).getaArtist());
        MainActivity.notificationLayoutExpanded.setTextViewText(R.id.song_name_title, MainActivity.Audio_list.get(MainActivity.song_position).getaName());
        MainActivity.notificationLayout.setTextViewText(R.id.notification_title, MainActivity.Audio_list.get(MainActivity.song_position).getaName());
        if (MainActivity.Audio_list.get(positon).getaCover() == null ){
            cover.setImageResource(R.drawable.music_player);
            bg_cover.setImageResource(R.drawable.music_player);
            MainActivity.notificationLayout.setImageViewResource(R.id.noti_s_cover , R.drawable.music_player);
            MainActivity.notificationLayoutExpanded.setImageViewResource(R.id.noti_l_cover , R.drawable.music_player);
        }else {
            cover.setImageBitmap(MainActivity.Audio_list.get(MainActivity.song_position).getaCover());
            bg_cover.setImageBitmap(MainActivity.Audio_list.get(MainActivity.song_position).getaCover());
            MainActivity.notificationLayout.setImageViewBitmap(R.id.noti_s_cover ,MainActivity.Audio_list.get(MainActivity.song_position).getaCover());
            MainActivity.notificationLayoutExpanded.setImageViewBitmap(R.id.noti_l_cover ,MainActivity.Audio_list.get(MainActivity.song_position).getaCover());
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }

//notification reciver class

    public static class NotificationHandeler extends BroadcastReceiver {

        public  NotificationHandeler(){

        }

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i("click on click listener", "onReceive: 111111111111111 ");
            if (MyOnClick_p.equals(intent.getAction())) {
                MainActivity.mp.stop();
                positon--;
                MainActivity.song_position = positon;
                is_pause = false;
                pause.setBackgroundResource(R.drawable.ic_pause);
                MainActivity.notificationLayoutExpanded.setImageViewResource(R.id.noti_pl, R.drawable.ic_pause);
                play_Song();
            } else if (MyOnClick_pl.equals(intent.getAction())) {
                Log.i("click on click listener", "onReceive: ");
                if (is_pause) {
                    MainActivity.mp.start();
                    is_pause = false;
                    pause.setBackgroundResource(R.drawable.ic_pause);
                    MainActivity.notificationLayoutExpanded.setImageViewResource(R.id.noti_pl, R.drawable.ic_pause);
                    MainActivity.show_notification();
                } else {
                    MainActivity.mp.pause();
                    is_pause = true;
                    pause.setBackgroundResource(R.drawable.ic_resume);
                    MainActivity.notificationLayoutExpanded.setImageViewResource(R.id.noti_pl, R.drawable.ic_resume);
                    MainActivity.show_notification();

                }
            } else if (MyOnClick_n.equals(intent.getAction())) {
                if (is_shuffle) {
                    MainActivity.mp.stop();
                    Random rand = new Random();
                    positon = rand.nextInt(MainActivity.Audio_list.size());
                    is_pause = false;
                    pause.setBackgroundResource(R.drawable.ic_pause);
                    MainActivity.notificationLayoutExpanded.setImageViewResource(R.id.noti_pl, R.drawable.ic_pause);
                    play_Song();
                } else {
                    MainActivity.mp.stop();
                    positon++;
                    MainActivity.song_position++;
                    is_pause = false;
                    pause.setBackgroundResource(R.drawable.ic_pause);
                    play_Song();
                }
            }

        }


    }

}


