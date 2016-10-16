package com.cj.music_player.util;

import com.cj.music_player.tools.MusicTools;
import com.cj.music_player.db.MusicInfoDB;
import com.cj.music_player.db.FolderInfoDB;
import com.cj.music_player.db.AlbumInfoDB;
import com.cj.music_player.db.ArtistInfoDB;
import com.cj.music_player.info.MusicInfo;
import com.cj.music_player.info.FolderInfo;
import com.cj.music_player.info.AlbumInfo;
import com.cj.music_player.info.ArtistInfo;
import com.cj.music_player.MusicApplication;

import android.provider.MediaStore;
import android.provider.MediaStore.Audio.Media;
import android.provider.MediaStore.Audio.Albums;
import android.provider.MediaStore.Files.FileColumns;
import android.database.Cursor;
import java.util.List;
import java.util.ArrayList;
import android.content.Context;
import java.util.Collections;
import android.net.Uri;
import java.io.File;

public class MusicUtils
{
    private MusicTools tool = new MusicTools();

    private SortUtils sort = new SortUtils();

    public List<MusicInfo> queryMusic(Context context)
    {
        MusicInfoDB music_db = new MusicInfoDB(context);
        //扫描->游标
        Cursor cursor = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null, null);
        List<MusicInfo> MusicList = new ArrayList<MusicInfo>();
        if (cursor != null)
        {
            while (cursor.moveToNext())
            {
                MusicInfo music_info = new MusicInfo();

                String name = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));
                int time1=cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
                //  String sbr = name.substring(name.length() - 3, name.length());
                /*if (sbr.equals("mp3") && (time1 >= 1000 && time1 <= 900000))
                 {*/
                String title=cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                String artist=cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                if ("<unknown>".equals(artist))
                {
                    artist = "未知歌手";
                }
                String path=cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                String time = tool.TimeConversion(time1);
                String album_id=cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));
                String album_art  = tool.getAlbumArt(context, Integer.valueOf(album_id));
                String album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
                if ("<unknown>".equals(album))
                {
                    album = "未知专辑";
                }
                String album_key_id = String.valueOf(album_id) + album;
                String id = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media._ID));
                int size1 = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.SIZE));
                String size = tool.SizeCoversion(size1);
                String artist_key_id = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST_ID)) + artist;
                String album_artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums.ARTIST));
                if ("<unknown>".equals(album_artist))
                {
                    album_artist = "未知专辑歌手";
                }
                String album_image_path = MusicApplication.album_art_path + name + "-image";


                music_info.setId(id);
                music_info.setTitle(title);
                music_info.setArtist(artist);
                music_info.setPath(path);
                music_info.setTime(time);
                music_info.setAlbumId(album_id);
                music_info.setName(name);
                music_info.setAlbum(album);
                music_info.setSize(size);
                music_info.setFavorite("0");
                music_info.setAlbumArt(album_art);
                music_info.setAlbumKeyId(album_key_id);
                music_info.setArtistKeyId(artist_key_id);
                music_info.setAlbumArtist(album_artist);
                music_info.setAlbumImagePath(album_image_path);

                MusicList.add(music_info);
                Collections.sort(MusicList, sort.new SortMusicList());//排序  
            }
            if (music_db.hasData())
            {
                return music_db.getMusicInfo();
            }
            else
            {
                music_db.saveMusicInfo(MusicList);
            }
        }
        cursor.close();
        return MusicList;
    }
    


    public List<FolderInfo> queryFolder(Context context)
    {
        FolderInfoDB folder_db = new FolderInfoDB(context);

        StringBuilder selection = new StringBuilder(FileColumns.MEDIA_TYPE
                                                    + " = " + FileColumns.MEDIA_TYPE_AUDIO);// + " and " + "("
        // + FileColumns.DATA + " like'%.mp3' or " + Media.DATA
        // + " like'%.wma')");
        selection.append(") group by ( " + FileColumns.PARENT);

        Cursor cursor = context.getContentResolver().query(MediaStore.Files.getContentUri("external"), null, selection.toString(), null, null);
        List<FolderInfo> FolderList = new ArrayList<FolderInfo>();
        while (cursor.moveToNext())
        {
            FolderInfo info = new FolderInfo();

            String filePath = cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.DATA));
            String path = filePath.substring(0, filePath.lastIndexOf(File.separator));
            String title = path.substring(path.lastIndexOf(File.separator) + 1);

            info.setTitle(title);
            info.setPath(path);
            FolderList.add(info);
            Collections.sort(FolderList, sort.new SortFolderList());//排序
        }
        if (folder_db.hasData())
        {
            return folder_db.getFolderInfo();
        }
        else
        {
            folder_db.saveFolderInfo(FolderList);
        }
        cursor.close();
        return FolderList;
    }

    public List<AlbumInfo> queryAlbum(Context context)
    {
        AlbumInfoDB album_db = new AlbumInfoDB(context);

        Cursor cursor = context.getContentResolver().query(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI, null, null, null, null);
        List<AlbumInfo> AlbumList = new ArrayList<AlbumInfo>();
        while (cursor.moveToNext())
        {
            AlbumInfo info = new AlbumInfo();

            String title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM));
            if ("<unknown>".equals(title))
            {
                title = "未知专辑";
            }
            String  number = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums.NUMBER_OF_SONGS));
            String album_artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums.ARTIST));
            if ("<unknown>".equals(album_artist))
            {
                album_artist = "未知专辑歌手";
            }
            String album_art = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ART));
            String album_key_id = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums._ID)) + title;

            info.setTitle(title);
            info.setNumber(number);
            info.setAlbumArtist(album_artist);
            info.setAlbumArt(album_art);
            info.setAlbumKeyId(album_key_id);
            
            AlbumList.add(info);
            Collections.sort(AlbumList, sort.new SortAlbumList());//排序
        }
        if (album_db.hasData())
        {
            return album_db.getAlbumInfo();
        }
        else
        {
            album_db.saveAlbumInfo(AlbumList);
        }
        cursor.close();
		return AlbumList;
    }


    public List<ArtistInfo> queryArtist(Context context)
    {

        ArtistInfoDB artist_db = new ArtistInfoDB(context);

        Cursor cursor = context.getContentResolver().query(MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI, null, null, null, null);
        List<ArtistInfo> ArtistList = new ArrayList<ArtistInfo>();
        while (cursor.moveToNext())
        {
            ArtistInfo info = new ArtistInfo();

            String title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Artists.ARTIST));
            if ("<unknown>".equals(title))
            {
                title = "未知歌手";
            }
            String number = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Artists.NUMBER_OF_TRACKS));
            String artist_key_id = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Artists._ID)) + title;
         
            info.setTitle(title);
            info.setNumber(number);
            info.setArtistKeyId(artist_key_id);
            ArtistList.add(info);
            Collections.sort(ArtistList, sort.new SortArtistList());//排序
        }
        if (artist_db.hasData())
        {
            return artist_db.getArtistInfo();
        }
        else
        {
            artist_db.saveArtistInfo(ArtistList);
        }
        cursor.close();
		return ArtistList;
    }

}
