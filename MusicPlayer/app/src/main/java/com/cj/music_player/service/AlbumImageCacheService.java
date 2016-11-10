package com.cj.music_player.service;

import com.cj.music_player.MusicApplication;
import com.cj.music_player.list.SongList;
import com.cj.music_player.info.MusicInfo;
import com.cj.music_player.tools.AlbumBitmap;
import com.cj.music_player.tools.BitmapTools;
import com.cj.music_player.db.SharedUtils;

import android.app.IntentService;
import android.content.Intent;
import java.util.List;
import java.util.ArrayList;
import android.graphics.Bitmap;
import java.io.File;

public class AlbumImageCacheService extends IntentService
{
    private List<MusicInfo> list = new ArrayList<MusicInfo>();
    
    public AlbumImageCacheService()
    {
        super("AlbumImageCacheService");
    }

    @Override
    public void onCreate()
    {
        // TODO: Implement this method
        super.onCreate();
        
        list = SongList.getMusicList(this);
    }

    @Override
    protected void onHandleIntent(Intent p1)
    {
        // TODO: Implement this method
        File f = new File(MusicApplication.getAlbumImagePath());
        BitmapTools.deleteFile(f);
        for (int n = 0; n < list.size(); n ++)
        {
            Bitmap bitmap = null;
            bitmap = AlbumBitmap.AlbumImage(list.get(n).getPath());
            if (bitmap == null)
            {
                bitmap = AlbumBitmap.getAlbumImage(this, n, Integer.valueOf(list.get(n).getAlbumId()));
            }
            String name = list.get(n).getName() + "-image";
            BitmapTools.saveBitmap(BitmapTools.ScaleBitmap(bitmap, 150, 150, true), MusicApplication.getAlbumImagePath(), name, Bitmap.CompressFormat.PNG, 80);
        }
        SharedUtils.saveBoolean(this, "SavaAlbumImage", true);
    }

}
