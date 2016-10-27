package com.cj.music_player.db;

import com.cj.music_player.info.MusicInfo;

import android.content.Context;
import java.util.List;
import android.database.sqlite.SQLiteDatabase;
import android.content.ContentValues;
import android.database.Cursor;
import java.util.ArrayList;

public class MusicInfoDB
{
    private final String TABLE_MUSIC = "music_info";
    private Context mContext;

    public MusicInfoDB(Context context)
    {
        this.mContext = context;
    }

    public void saveMusicInfo(List<MusicInfo> list)
    {
        SQLiteDatabase db = DatabaseHelper.getInstance(mContext);
        for (MusicInfo music : list)
        {
            ContentValues cv = new ContentValues();

            cv.put(DatabaseHelper.TITLE, music.getTitle());
            cv.put(DatabaseHelper.ARTIST, music.getArtist());
            cv.put(DatabaseHelper.TIME, music.getTime());
            cv.put(DatabaseHelper.ID, music.getId());
            cv.put(DatabaseHelper.ALBUM_ID, music.getAlbumId());
            cv.put(DatabaseHelper.DATA, music.getPath());
            cv.put(DatabaseHelper.NAME, music.getName());
            cv.put(DatabaseHelper.ALBUM, music.getAlbum());
            cv.put(DatabaseHelper.SIZE, music.getSize());
            cv.put(DatabaseHelper.FAVORITE, music.getFavorite());
            cv.put(DatabaseHelper.ART, music.getAlbumArt());
            cv.put(DatabaseHelper.ALBUM_KEY_ID, music.getAlbumKeyId());
            cv.put(DatabaseHelper.ARTIST_KEY_ID, music.getArtistKeyId());
            cv.put(DatabaseHelper.ALBUM_ARTIST, music.getAlbumArtist());
            cv.put(DatabaseHelper.ALBUM_IMAGE_PATH, music.getAlbumImagePath());
            db.insert(TABLE_MUSIC, null, cv);
        }
    }

    public List<MusicInfo> getMusicInfo()
    {
        SQLiteDatabase db = DatabaseHelper.getInstance(mContext);
        List<MusicInfo> list = new ArrayList<MusicInfo>();
        String sql = "select * from " + TABLE_MUSIC;
        Cursor cursor = db.rawQuery(sql, null);
        while (cursor.moveToNext())
        {
            MusicInfo music = new MusicInfo();

            String title = cursor.getString(cursor.getColumnIndex(DatabaseHelper.TITLE));
            String artist = cursor.getString(cursor.getColumnIndex(DatabaseHelper.ARTIST));
            String data = cursor.getString(cursor.getColumnIndex(DatabaseHelper.DATA));
            String time = cursor.getString(cursor.getColumnIndex(DatabaseHelper.TIME));
            String album_id = cursor.getString(cursor.getColumnIndex(DatabaseHelper.ALBUM_ID));
            String id = cursor.getString(cursor.getColumnIndex(DatabaseHelper.ID));
            String name = cursor.getString(cursor.getColumnIndex(DatabaseHelper.NAME));
            String album = cursor.getString(cursor.getColumnIndex(DatabaseHelper.ALBUM));
            String size = cursor.getString(cursor.getColumnIndex(DatabaseHelper.SIZE));
            String favorite = cursor.getString(cursor.getColumnIndex(DatabaseHelper.FAVORITE));
            String album_art = cursor.getString(cursor.getColumnIndex(DatabaseHelper.ART));
            String album_key_id = cursor.getString(cursor.getColumnIndex(DatabaseHelper.ALBUM_KEY_ID));
            String artist_key_id = cursor.getString(cursor.getColumnIndex(DatabaseHelper.ARTIST_KEY_ID));
            String album_artist = cursor.getString(cursor.getColumnIndex(DatabaseHelper.ALBUM_ARTIST));
            String album_image_path = cursor.getString(cursor.getColumnIndex(DatabaseHelper.ALBUM_IMAGE_PATH));

            music.setTitle(title);
            music.setArtist(artist);
            music.setPath(data);
            music.setTime(time);
            music.setAlbumId(album_id);
            music.setId(id);
            music.setName(name);
            music.setAlbum(album);
            music.setSize(size);
            music.setFavorite(favorite);
            music.setAlbumArt(album_art);
            music.setAlbumKeyId(album_key_id);
            music.setArtistKeyId(artist_key_id);
            music.setAlbumArtist(album_artist);
            music.setAlbumImagePath(album_image_path);

            list.add(music);
        }
        cursor.close();
        return list;
    }



    public void setFavoriteStateById(int id, int favorite)
    {
        SQLiteDatabase db = DatabaseHelper.getInstance(mContext);
        String sql = "update " + TABLE_MUSIC + " set favorite = " + favorite + " where _id = " + id;
        db.execSQL(sql);
    }

    public boolean queryExist(String path)
    {
        boolean isExist = false;
        SQLiteDatabase db = DatabaseHelper.getInstance(mContext);

        Cursor cursor = db.query(DatabaseHelper.TABLE_MUSIC, null, DatabaseHelper.DATA + " like ?", new String[] { "%" + path + "%"}, null, null, null);        
        if (cursor.getCount() > 0)
        {
            isExist = true;
        }
        cursor.close();
        return isExist;
    }

    public void delete(String path)
    {
        SQLiteDatabase db = DatabaseHelper.getInstance(mContext);
        db.delete(DatabaseHelper.TABLE_MUSIC, DatabaseHelper.DATA + "='" + path + "'", null);
    }

    /**
     * 数据库中是否有数据
     * @return
     */
    public boolean hasData()
    {
        SQLiteDatabase db = DatabaseHelper.getInstance(mContext);
        String sql = "select count(*) from " + TABLE_MUSIC;
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
        String sql = "select count(*) from " + TABLE_MUSIC;
        Cursor cursor = db.rawQuery(sql, null);
        int count = 0;
        if (cursor.moveToFirst())
        {
            count = cursor.getInt(0);
        }
        return count;
    }

    public void deleteTables(Context context)
    {
        SQLiteDatabase db = DatabaseHelper.getInstance(mContext);
        db.delete(DatabaseHelper.TABLE_MUSIC, null, null);
        db.delete(DatabaseHelper.TABLE_FOLDER, null, null);
        db.delete(DatabaseHelper.TABLE_ALBUM, null, null);
        db.delete(DatabaseHelper.TABLE_ARTIST, null, null);
    }

    public Cursor query(String queryTable, String key_id, String[] value)
    {
        // TODO: Implement this method
        DatabaseHelper helper = new DatabaseHelper(mContext);
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.query(DatabaseHelper.TABLE_MUSIC, value, queryTable + " like ?", new String[] { "%" + key_id + "%"}, null, null, null);
        return cursor;
    }
    
}
