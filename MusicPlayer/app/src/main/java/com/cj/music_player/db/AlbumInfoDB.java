package com.cj.music_player.db;

import com.cj.music_player.info.AlbumInfo;

import android.database.sqlite.SQLiteDatabase;
import android.content.ContentValues;
import java.util.ArrayList;
import android.database.Cursor;
import android.content.Context;
import java.util.List;

public class AlbumInfoDB
{
    private static final String TABLE_ALBUM = "album_info";
    private Context mContext;

    public AlbumInfoDB(Context context)
    {
        this.mContext = context;
    }

    public void saveAlbumInfo(List<AlbumInfo> list)
    {
        SQLiteDatabase db = DatabaseHelper.getInstance(mContext);
        for (AlbumInfo info : list)
        {
            ContentValues cv = new ContentValues();
            cv.put(DatabaseHelper.ALBUM_TITLE, info.getTitle());
            cv.put(DatabaseHelper.ALBUM_NUMBER, info.getNumber());
            cv.put(DatabaseHelper.ALBUM_ALBUM_ARTIST, info.getAlbumArtist());
            cv.put(DatabaseHelper.ALBUM_ART, info.getAlbumArt());
            cv.put(DatabaseHelper.ALBUM_ALBUM_KEY_ID, info.getAlbumKeyId());
            db.insert(TABLE_ALBUM, null, cv);
        }
    }

    public List<AlbumInfo> getAlbumInfo()
    {
        SQLiteDatabase db = DatabaseHelper.getInstance(mContext);
        List<AlbumInfo> list = new ArrayList<AlbumInfo>();
        String sql = "select * from " + TABLE_ALBUM;
        Cursor cursor = db.rawQuery(sql, null);
        while (cursor.moveToNext())
        {
            AlbumInfo info = new AlbumInfo();
            
            String title = cursor.getString(cursor.getColumnIndex(DatabaseHelper.ALBUM_TITLE));
            String number = cursor.getString(cursor.getColumnIndex(DatabaseHelper.ALBUM_NUMBER));
            String album_artist = cursor.getString(cursor.getColumnIndex(DatabaseHelper.ALBUM_ALBUM_ARTIST));
            String album_art = cursor.getString(cursor.getColumnIndex(DatabaseHelper.ALBUM_ART));
            String album_key_id = cursor.getString(cursor.getColumnIndex(DatabaseHelper.ALBUM_ALBUM_KEY_ID));
         
            info.setTitle(title);
            info.setNumber(number);
            info.setAlbumArtist(album_artist);
            info.setAlbumArt(album_art);
            info.setAlbumKeyId(album_key_id);
            
            list.add(info);
        }
        cursor.close();
        return list;
    }

    /**
     * 数据库中是否有数据
     * @return
     */
    public boolean hasData()
    {
        SQLiteDatabase db = DatabaseHelper.getInstance(mContext);
        String sql = "select count(*) from " + TABLE_ALBUM;
        Cursor cursor = db.rawQuery(sql, null);
        boolean has = false;
        if (cursor.moveToFirst())
        {
            int count = cursor.getInt(0);
            if (count > 0)
            {
                has = true;
            }
        }
        cursor.close();
        return has;
    }

    public int getDataCount()
    {
        SQLiteDatabase db = DatabaseHelper.getInstance(mContext);
        String sql = "select count(*) from " + TABLE_ALBUM;
        Cursor cursor = db.rawQuery(sql, null);
        int count = 0;
        if (cursor.moveToFirst())
        {
            count = cursor.getInt(0);
        }
        return count;
	}
}
