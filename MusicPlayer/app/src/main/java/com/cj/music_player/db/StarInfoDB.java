package com.cj.music_player.db;

import com.cj.music_player.info.StarInfo;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.content.ContentValues;
import java.util.List;
import java.util.ArrayList;
import android.database.Cursor;

public class StarInfoDB
{
    private static final String TABLE_STAR = "star_info";
    private static Context mContext;

    public StarInfoDB(Context context)
    {
        this.mContext = context;
    }

    public static void saveStarInfo(String id, String star, String title, String artist, String album,
                             String album_art, String album_image_path)
    {
        SQLiteDatabase db = DatabaseHelper.getInstance(mContext);

        ContentValues cv = new ContentValues();
        cv.put(DatabaseHelper.STAR_ID, id);
        cv.put(DatabaseHelper.STAR_NUMBER, star);
        cv.put(DatabaseHelper.STAR_TITLE, title);
        cv.put(DatabaseHelper.STAR_ALBUM, album);
        cv.put(DatabaseHelper.STAR_ARTIST, artist);
        cv.put(DatabaseHelper.STAR_ART, album_art);
        cv.put(DatabaseHelper.STAR_ALBUM_IMAGE_PATH, album_image_path);
        db.insert(TABLE_STAR, null, cv);
    }
    
    public List<StarInfo> getStarInfo()
    {
        SQLiteDatabase db = DatabaseHelper.getInstance(mContext);
        List<StarInfo> list = new ArrayList<StarInfo>();
        String sql = "select * from " + TABLE_STAR;
        Cursor cursor = db.rawQuery(sql, null);
        while (cursor.moveToNext())
        {
            StarInfo info = new StarInfo();

            String title = cursor.getString(cursor.getColumnIndex(DatabaseHelper.STAR_TITLE));
            String star = cursor.getString(cursor.getColumnIndex(DatabaseHelper.STAR_NUMBER));
            String artist = cursor.getString(cursor.getColumnIndex(DatabaseHelper.STAR_ARTIST));
            String album_art = cursor.getString(cursor.getColumnIndex(DatabaseHelper.STAR_ART));
            String id = cursor.getString(cursor.getColumnIndex(DatabaseHelper.STAR_ID));
            String album = cursor.getString(cursor.getColumnIndex(DatabaseHelper.STAR_ALBUM));
            String album_image_path = cursor.getString(cursor.getColumnIndex(DatabaseHelper.STAR_ALBUM_IMAGE_PATH));

            info.setTitle(title);
            info.setArtist(artist);
            info.setId(id);
            info.setStar(star);
            info.setAlbum(album);
            info.setAlbumArt(album_art);
            info.setAlbumImagePath(album_image_path);
            
            list.add(info);
        }
        cursor.close();
        return list;
    }
    
    public static boolean queryExist(String title)
    {
        boolean isExist = false;
        SQLiteDatabase db = DatabaseHelper.getInstance(mContext);
        
        Cursor cursor = db.query(DatabaseHelper.TABLE_STAR, null, DatabaseHelper.STAR_TITLE + " like ?", new String[] { "%" + title + "%"}, null, null, null);        
        if (cursor.getCount() > 0)
        {
            isExist = true;
        }
        cursor.close();
        return isExist;
    }
    
    public static void delete(String title)
    {
        SQLiteDatabase db = DatabaseHelper.getInstance(mContext);
        db.delete(DatabaseHelper.TABLE_STAR, DatabaseHelper.STAR_TITLE + "='" + title + "'", null);
    }
    
    public static void update(String title, String star)
    {
        SQLiteDatabase db = DatabaseHelper.getInstance(mContext);
        ContentValues values = new ContentValues();
		values.put(DatabaseHelper.STAR_NUMBER, star);
        db.update(DatabaseHelper.TABLE_STAR, values, DatabaseHelper.STAR_TITLE + "=?", new String[] { title });
    }
}
