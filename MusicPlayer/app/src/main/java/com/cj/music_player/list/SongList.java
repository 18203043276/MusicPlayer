package com.cj.music_player.list;

import com.cj.music_player.db.MusicInfoDB;
import com.cj.music_player.db.FolderInfoDB;
import com.cj.music_player.db.AlbumInfoDB;
import com.cj.music_player.db.ArtistInfoDB;
import com.cj.music_player.db.StarInfoDB;
import com.cj.music_player.info.MusicInfo;
import com.cj.music_player.info.FolderInfo;
import com.cj.music_player.info.AlbumInfo;
import com.cj.music_player.info.ArtistInfo;
import com.cj.music_player.info.StarInfo;

import java.util.List;
import android.content.Context;
import java.util.ArrayList;

public class SongList
{
    public static List<MusicInfo> getMusicList(Context context)
    {
        MusicInfoDB db = new MusicInfoDB(context);
        List<MusicInfo> list= new ArrayList<MusicInfo>();
        list = db.getMusicInfo();
        return list;
    }
    
    public static List<FolderInfo> getFolderList(Context context)
    {
        FolderInfoDB db = new FolderInfoDB(context);
        List<FolderInfo> list = new ArrayList<FolderInfo>();
        list = db.getFolderInfo();
        return list;
    }
    
    public static List<AlbumInfo> getAlbumList(Context context)
    {
        AlbumInfoDB db = new AlbumInfoDB(context);
        List<AlbumInfo> list = new ArrayList<AlbumInfo>();
        list = db.getAlbumInfo();
        return list;
    }
    
    public static List<ArtistInfo> getArtistList(Context context)
    {
        ArtistInfoDB db = new ArtistInfoDB(context);
        List<ArtistInfo> list = new ArrayList<ArtistInfo>();
        list = db.getArtistInfo();
        return list;
    }
    
    public static List<StarInfo> getStarList(Context context)
    {
        StarInfoDB db = new StarInfoDB(context);
        List<StarInfo> list = new ArrayList<StarInfo>();
        list = db.getStarInfo();
        return list;
    }
}
