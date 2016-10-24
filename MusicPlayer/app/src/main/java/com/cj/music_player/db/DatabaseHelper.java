package com.cj.music_player.db;

import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;
import android.content.Context;
import android.database.Cursor;

public class DatabaseHelper extends SQLiteOpenHelper
{
    private static SQLiteDatabase Db;
    private static DatabaseHelper Helper;
    private static final int DB_VERSION = 2;
    private static final String DB_NAME = "MusicInfo.db";
    
    public static final String TABLE_MUSIC = "music_info";
    public final static String TITLE = "title";
    public final static String ARTIST = "artist";
    public final static String TIME = "time";
    public final static String DATA = "data";
    public final static String NAME = "name";
    public final static String ID = "id";
    public final static String ALBUM_ID = "album_id";
    public final static String ART = "album_art";
    public final static String ALBUM_IMAGE_PATH = "album_image_path";
    public final static String ALBUM = "album";
    public final static String ALBUM_ARTIST = "album_artist";
    public final static String ALBUM_KEY_ID = "album_key_id";
    public final static String ARTIST_KEY_ID = "artist_key_id";
    public final static String SIZE = "size";
    public final static String FAVORITE ="favorite";
    
    public static final String TABLE_FOLDER = "folder_info";
    public final static String FOLDER_TITLE = "title";
    public final static String FOLDER_PATH = "path";
    
    public static final String TABLE_ALBUM = "album_info";
    public final static String ALBUM_TITLE = "album";
    public final static String ALBUM_NUMBER = "number";
    public final static String ALBUM_ALBUM_ARTIST = "album_artist";
    public final static String ALBUM_ART = "album_art";
    public final static String ALBUM_ALBUM_KEY_ID = "album_key_id";
    
    public static final String TABLE_ARTIST = "artist_info";
    public final static String ARTIST_TITLE = "artist";
    public final static String ARTIST_NUMBER = "number";
    public final static String ARTIST_ARTIST_KEY_ID = "artist_key_id";
    
    public static final String TABLE_STAR = "star_info";
    public static final String STAR_NUMBER = "star";
    public static final String STAR_TITLE = "title";
    public static final String STAR_ID = "id";
    public static final String STAR_ARTIST = "artist";
    public static final String STAR_ALBUM = "album";
    public static final String STAR_ART = "album_art";
    public final static String STAR_ALBUM_IMAGE_PATH = "album_image_path";
    public static final String STAR_PATH = "path";
    
    
    public static SQLiteDatabase getInstance(Context context)
    {
        if (Db == null)
        {
            Db = getHelper(context).getWritableDatabase();
        }
        return Db;
    }

    public static DatabaseHelper getHelper(Context context)
    {
        if (Helper == null)
        {
            Helper = new DatabaseHelper(context);
        }
        return Helper;
    }

    public DatabaseHelper(Context context)
    {
        super(context, DB_NAME, null, DB_VERSION);
    }

    public DatabaseHelper(Context context, String name, CursorFactory factory, int version)
    {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_MUSIC
                   + " (_id INTEGER PRIMARY KEY AUTOINCREMENT, " 
                   + ID + " char, " + ALBUM_ID + " char, " + TITLE + " char, " 
                   + ARTIST + " char, " + ALBUM  + " char, " + ALBUM_ARTIST + " char, "
                   + TIME + " char, " + SIZE + " char, "  + NAME + " char, " 
                   + FAVORITE  + " char, " + DATA + " char, " + ART + " char," + ALBUM_IMAGE_PATH + " char, "
                   + ALBUM_KEY_ID  + " char, " + ARTIST_KEY_ID + " char)");
                   
                   
       db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_FOLDER
                   + " (_id INTEGER PRIMARY KEY AUTOINCREMENT, " 
                   + FOLDER_TITLE + " char, " + FOLDER_PATH + " char)");
	
                   
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_ALBUM
                   + " (_id INTEGER PRIMARY KEY AUTOINCREMENT, " 
                   + ALBUM_TITLE + " char, " + ALBUM_ALBUM_ARTIST + " char, " + ALBUM_NUMBER + " char, " 
                   + ALBUM_ART + " char, " + ALBUM_ALBUM_KEY_ID  + " char)");
                   
                   
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_ARTIST
                   + " (_id INTEGER PRIMARY KEY AUTOINCREMENT, " 
                   + ARTIST_TITLE + " char, " + ARTIST_NUMBER + " char, " + ARTIST_ARTIST_KEY_ID + " char)");
	
                   
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_STAR
                   + " (_id INTEGER PRIMARY KEY AUTOINCREMENT, " 
                   + STAR_ID + " char, " + STAR_NUMBER + " char, " + STAR_TITLE + " char, " 
                   + STAR_ARTIST + " char, " + STAR_ALBUM  + " char, " + STAR_ART + " char, " + STAR_PATH + " char, " + STAR_ALBUM_IMAGE_PATH + " char)");
	}

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        if (newVersion > oldVersion)
        {         
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_MUSIC);    
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_FOLDER);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_ALBUM);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_ARTIST);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_STAR);
            onCreate(db);
        }
    }

    public void deleteTables(Context context)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_FOLDER, null, null);
        db.delete(TABLE_MUSIC, null, null);
        db.delete(TABLE_ALBUM, null, null);
        db.delete(TABLE_ARTIST, null, null);
    }

}
