package com.example.artistalbums;

public class ChildModel {
    private String artist, albumName, albumImage;

    public ChildModel(){}

    public String getArtist() {
        return artist;
    }

    public String getAlbumName() {
        return albumName;
    }

    public String getAlbumImage() {
        return albumImage;
    }


    public ChildModel(String artist, String albumName, String albumImage) {
        this.artist = artist;
        this.albumName = albumName;
        this.albumImage = albumImage;
    }
}