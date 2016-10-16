package com.cj.music_player.db;

import com.cj.music_player.info.ArtistInfo;

import android.database.sqlite.SQLiteDatabase;
import android.content.ContentValues;
import java.util.ArrayList;
import android.database.Cursor;
import android.content.Context;
import java.util.List;

public class ArtistInfoDB
{
    private static final String TABLE_ARTIST = "artist_info";
    private Context mContext;

    public ArtistInfoDB(Context context)
    {
        this.mContext = context;
    }

    public void saveArtistInfo(List<ArtistInfo> list)
    {
        SQLiteDatabase db = DatabaseHelper.getInstance(mContext);
        for (ArtistInfo info : list)
        {
            ContentValues cv = new ContentValues();
            cv.put(DatabaseHelper.ARTIST_TITLE, info.getTitle());
            cv.put(DatabaseHelper.ARTIST_NUMBER, info.getNumber());
            cv.put(DatabaseHelper.ARTIST_ARTIST_KEY_ID, info.getArtistKeyId());
            db.insert(TABLE_ARTIST, null, cv);
        }
    }

    public List<ArtistInfo> getArtistInfo()
    {
        SQLiteDatabase db = DatabaseHelper.getInstance(mContext);
        List<ArtistInfo> list = new ArrayList<ArtistInfo>();
        String sql = "select * from " + TABLE_ARTIST;
        Cursor cursor = db.rawQuery(sql, null);
        while (cursor.moveToNext())
        {
            ArtistInfo info = new ArtistInfo();
            
            String title = cursor.getString(cursor.getColumnIndex(DatabaseHelper.ARTIST_TITLE));
            String number = cursor.getString(cursor.getColumnIndex(DatabaseHelper.ARTIST_NUMBER));
            String artist_key_id = cursor.getString(cursor.getColumnIndex(DatabaseHelper.ARTIST_ARTIST_KEY_ID));
          
            info.setTitle(title);
            info.setNumber(number);
            info.setArtistKeyId(artist_key_id);
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
        String sql = "select count(*) from " + TABLE_ARTIST;
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
        String sql = "select count(*) from " + TABLE_ARTIST;
        Cursor cursor = db.rawQuery(sql, null);
        int count = 0;
        if (cursor.moveToFirst())
        {
            count = cursor.getInt(0);
        }
        return count;
	}
}
