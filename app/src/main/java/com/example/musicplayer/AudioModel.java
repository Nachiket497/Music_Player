package com.example.musicplayer;


import android.graphics.Bitmap;

public class AudioModel {

    String aPath;
    String aName;
    String aAlbum;
    String aArtist;
    Bitmap aCover ;

    public String getaPath() {
        return aPath;
    }
    public void setaPath(String aPath) {
        this.aPath = aPath;
    }
    public String getaName() {
        return aName;
    }
    public void setaName(String aName) {
        this.aName = aName;
    }
    public String getaAlbum() {
        return aAlbum;
    }
    public void setaAlbum(String aAlbum) {
        this.aAlbum = aAlbum;
    }
    public String getaArtist() {
        return aArtist;
    }
    public void setaArtist(String aArtist) {
        this.aArtist = aArtist;
    }
    public Bitmap getaCover(){
        return  aCover ;
    }
    public  void setaCover( Bitmap  aCover){
        this.aCover = aCover ;
    }

}
