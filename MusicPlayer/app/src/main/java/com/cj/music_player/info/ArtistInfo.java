package com.cj.music_player.info;

public class ArtistInfo
{
    private String title;
    private String number;
    private String artist_key_id;
    
    
    public String getArtistKeyId()
    {
        return artist_key_id;
    }
    public void setArtistKeyId(String artist_key_id)
    {
        this.artist_key_id = artist_key_id;
    }
    
    public String getTitle()
    {
        return title;
    }
    public void setTitle(String title)
    {
        this.title = title;
    }
    
    public String getNumber()
    {
        return number;
    }
    public void setNumber(String number)
    {
        this.number = number;
    }
}
