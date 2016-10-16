package com.cj.music_player.db;

import com.cj.music_player.info.FolderInfo;

import android.content.Context;
import java.util.List;
import android.database.sqlite.SQLiteDatabase;
import android.content.ContentValues;
import java.util.ArrayList;
import android.database.Cursor;

public class FolderInfoDB
{
    private static final String TABLE_FOLDER = "folder_info";
    private Context mContext;

    public FolderInfoDB(Context context)
    {
        this.mContext = context;
    }

    public void saveFolderInfo(List<FolderInfo> list)
    {
        SQLiteDatabase db = DatabaseHelper.getInstance(mContext);
        for (FolderInfo info : list)
        {
            ContentValues cv = new ContentValues();
            cv.put(DatabaseHelper.FOLDER_TITLE, info.getTitle());
            cv.put(DatabaseHelper.FOLDER_PATH, info.getPath());
            db.insert(TABLE_FOLDER, null, cv);
        }
    }

    public List<FolderInfo> getFolderInfo()
    {
        SQLiteDatabase db = DatabaseHelper.getInstance(mContext);
        List<FolderInfo> list = new ArrayList<FolderInfo>();
        String sql = "select * from " + TABLE_FOLDER;
        Cursor cursor = db.rawQuery(sql, null);
        while (cursor.moveToNext())
        {
            FolderInfo info = new FolderInfo();
            
            String title = cursor.getString(cursor.getColumnIndex(DatabaseHelper.FOLDER_TITLE));
            String path = cursor.getString(cursor.getColumnIndex(DatabaseHelper.FOLDER_PATH));
            
            info.setTitle(title);
            info.setPath(path);
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
        String sql = "select count(*) from " + TABLE_FOLDER;
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
        String sql = "select count(*) from " + TABLE_FOLDER;
        Cursor cursor = db.rawQuery(sql, null);
        int count = 0;
        if (cursor.moveToFirst())
        {
            count = cursor.getInt(0);
        }
        return count;
	}
}
