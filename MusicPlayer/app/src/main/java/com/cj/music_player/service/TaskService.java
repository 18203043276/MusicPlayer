package com.cj.music_player.service;

import com.cj.music_player.R;
import com.cj.music_player.MusicApplication;
import com.cj.music_player.list.SongList;
import com.cj.music_player.info.MusicInfo;
import com.cj.music_player.tools.AlbumBitmap;
import com.cj.music_player.tools.BitmapTools;

import android.app.IntentService;
import java.util.List;
import java.util.ArrayList;
import android.content.Intent;
import java.io.File;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class TaskService extends IntentService
{
    private List<MusicInfo> musicList = new ArrayList<MusicInfo>();
    
    public TaskService()
    {
        super("TaskService");
    }

    @Override
    public void onCreate()
    {
        // TODO: Implement this method
        super.onCreate();

        musicList = SongList.getMusicList(TaskService.this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        // TODO: Implement this method
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onStart(Intent intent, int startId)
    {
        // TODO: Implement this method
        super.onStart(intent, startId);
    }

    @Override
    protected void onHandleIntent(Intent intent)
    {
        // TODO: Implement this method
        String task = intent.getStringExtra("task");
        if (task.equals("sava_album_image"))
        {
            SaveAlbumImage();
        }
    }

    @Override
    public void onDestroy()
    {
        // TODO: Implement this method
        super.onDestroy();
    }

    private void SaveAlbumImage()
    {
        File f = new File(MusicApplication.getAlbumImagePath());
        BitmapTools.deleteFile(f);
        for (int n = 0; n < musicList.size(); n ++)
        {
            Bitmap bitmap = null;
            bitmap = AlbumBitmap.AlbumImage(musicList.get(n).getPath());
            if (bitmap == null)
            {
                bitmap = AlbumBitmap.getAlbumImage(TaskService.this, n, Integer.valueOf(musicList.get(n).getAlbumId()));
                if (bitmap == null)
                {
                    bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.main_menu_left_image);
                }
            }
            String name = musicList.get(n).getName() + "-image";
            BitmapTools.saveBitmap(BitmapTools.ScaleBitmap(bitmap, 500, 500, true), MusicApplication.getAlbumImagePath(), name, Bitmap.CompressFormat.PNG, 80);
        }
    }

}
