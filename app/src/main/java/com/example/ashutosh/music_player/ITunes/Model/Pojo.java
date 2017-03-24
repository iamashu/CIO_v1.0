package com.example.ashutosh.music_player.ITunes.Model;

/**
 * Created by ashutosh on 25/3/17.
 */
public class Pojo {

    private String artistName = null;
    private String collectionName = null;
    private String trackName = null;
    private String imageView = null ;

    public Pojo(String artistName, String collectionName, String trackName, String imageView) {

        this.artistName = artistName;
        this.collectionName = collectionName;
        this.trackName = trackName;
        this.imageView = imageView ;
    }

    @Override
    public String toString() {
        return "Pojo{" +
                "artistName='" + artistName + '\'' +
                ", collectionName='" + collectionName + '\'' +
                ", trackName='" + trackName + '\'' +
                ", imageView='" + imageView + '\'' +
                '}';
    }

    public Pojo(){

    }

    public String getCollectionName() {
        return collectionName;
    }

    public void setCollectionName(String collectionName) {
        this.collectionName = collectionName;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public String getTrackName() {
        return trackName;
    }

    public void setTrackName(String trackName) {
        this.trackName = trackName;
    }

    public String getImageView() { return imageView; }

    public void setImageView(String imageView) {
        this.imageView = imageView ;
    }


}