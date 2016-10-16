package com.cj.music_player.info;

public class StarInfo
{
    private String id;
    private String star;
    private String title;
    private String artist;
    private String album;
    private String album_art;
    private String album_image_path;


    public String getAlbumImagePath()
    {
        return album_image_path;
    }
    public void setAlbumImagePath(String album_image_path)
    {
        this.album_image_path = album_image_path;
    }
    
    public String getId()
    {
        return id;
    }
    public void setId(String id)
    {
        this.id = id;
    }
    
    public String getStar()
    {
        return star;
    }
    public void setStar(String star)
    {
        this.star = star;
    }
    
    public String getTitle()
    {
        return title;
    }
    public void setTitle(String title)
    {
        this.title = title;
    }
    
    public String getArtist()
    {
        return artist;
    }
    public void setArtist(String artist)
    {
        this.artist = artist;
    }
    
    public String getAlbum()
    {
        return album;
    }
    public void setAlbum(String album)
    {
        this.album = album;
    }
    
    public String getAlbumArt()
    {
        return album_art;
    }
    public void setAlbumArt(String album_art)
    {
        this.album_art = album_art;
    }
}
