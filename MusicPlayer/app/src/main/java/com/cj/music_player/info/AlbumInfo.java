package com.cj.music_player.info;

public class AlbumInfo
{
    private String title;
    private String number;
    private String album_artist;
    private String album_art;
    private String album_key_id;

    
    public String getAlbumKeyId()
    {
        return album_key_id;
    }
    public void setAlbumKeyId(String album_key_id)
    {
        this.album_key_id = album_key_id;
    }
    
    public String getAlbumArt()
    {
        return album_art;
    }
    public void setAlbumArt(String album_art)
    {
        this.album_art = album_art;
    }
    
    public String getAlbumArtist()
    {
        return album_artist;
    }
    public void setAlbumArtist(String album_artist)
    {
        this.album_artist = album_artist;
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
