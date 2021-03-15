package com.example.musicplayer;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Size;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.RequiresApi;

public class getData {

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static List<AudioModel> getAllAudioFromDevice(final Context context) {
        final List<AudioModel> tempAudioList = new ArrayList<>();


        ContentResolver cr =  context.getContentResolver() ;
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        byte[] rawArt;
        Bitmap art = null;
        BitmapFactory.Options bfo=new BitmapFactory.Options();
        Cursor c =  cr.query(uri,null,null , null ,null);
        if (c != null && c.moveToFirst()) {

            int song_Name = c.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int song_Artist = c.getColumnIndex(MediaStore.Audio.Media.ARTIST);
            int song_Album = c.getColumnIndex(MediaStore.Audio.Media.ALBUM);
            int song_path = c.getColumnIndex(MediaStore.Audio.Media.DATA);

            Log.i("here", "getAllAudioFromDevice: ===============");
            do {

                mmr.setDataSource(c.getString(song_path));

                rawArt = mmr.getEmbeddedPicture();
                if (null != rawArt)
                    art = BitmapFactory.decodeByteArray(rawArt, 0, rawArt.length, bfo);
                else {
                    art = null ;
                }
                AudioModel temp = new AudioModel();
                temp.setaAlbum(c.getString(song_Album));
                temp.setaName(c.getString(song_Name));
                temp.setaArtist(c.getString(song_Artist));
                temp.setaPath(c.getString(song_path));
                temp.setaCover(art);
                tempAudioList.add(temp);

            }while(c.moveToNext());

        }

        return tempAudioList;
    }
}
