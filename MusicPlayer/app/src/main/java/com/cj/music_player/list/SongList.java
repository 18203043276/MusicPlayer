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
import com.cj.music_player.db.SettingSharedUtils;

import java.util.List;
import android.content.Context;
import java.util.ArrayList;
import java.util.Collections;

public class SongList
{
    public static List<MusicInfo> getMusicList(Context context)
    {
        MusicInfoDB db = new MusicInfoDB(context);
        List<MusicInfo> list= new ArrayList<MusicInfo>();
        list = db.getMusicInfo();
        if (SettingSharedUtils.getBoolean(context, "music_list_sort", true) == false)
        {
            Collections.reverse(list);
        }
        return list;
    }
    
    public List<FolderInfo> getFolderList(Context context)
    {
        FolderInfoDB db = new FolderInfoDB(context);
        List<FolderInfo> list = new ArrayList<FolderInfo>();
        list = db.getFolderInfo();
        if (SettingSharedUtils.getBoolean(context, "folder_list_sort", true) == false)
        {
            Collections.reverse(list);
        }
        return list;
    }
    
    public List<AlbumInfo> getAlbumList(Context context)
    {
        AlbumInfoDB db = new AlbumInfoDB(context);
        List<AlbumInfo> list = new ArrayList<AlbumInfo>();
        list = db.getAlbumInfo();
        if (SettingSharedUtils.getBoolean(context, "album_list_sort", true) == false)
        {
            Collections.reverse(list);
        }
        return list;
    }
    
    public List<ArtistInfo> getArtistList(Context context)
    {
        ArtistInfoDB db = new ArtistInfoDB(context);
        List<ArtistInfo> list = new ArrayList<ArtistInfo>();
        list = db.getArtistInfo();
        if (SettingSharedUtils.getBoolean(context, "artist_list_sort", true) == false)
        {
            Collections.reverse(list);
        }
        return list;
    }
    
    public List<StarInfo> getStarList(Context context)
    {
        StarInfoDB db = new StarInfoDB(context);
        List<StarInfo> list = new ArrayList<StarInfo>();
        list = db.getStarInfo();
        if (SettingSharedUtils.getBoolean(context, "star_list_sort", true) == false)
        {
            Collections.reverse(list);
        }
        return list;
    }
}
