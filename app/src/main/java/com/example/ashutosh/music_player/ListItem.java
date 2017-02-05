package com.example.ashutosh.music_player;

/**
 * Created by ashutosh on 2/2/17.
 */

public class ListItem
{
    private String head ;
    private String description ;
    private String imageURL ;

    public String getImageURL() {
        return imageURL;
    }

    public String getHead() {
        return head;
    }

    public String getDescription() {
        return description;
    }

    public ListItem(String head, String description, String imageURL) {
        this.head = head;
        this.description = description;
        this.imageURL = imageURL ;
    }
}
