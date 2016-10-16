package com.cj.music_player;

import android.app.Application;
import java.util.List;
import android.app.Activity;
import java.util.LinkedList;
import java.io.File;
import android.os.Environment;
import com.cj.music_player.db.SharedUtils;

public class MusicApplication extends Application
{
    private List<Activity> mList = new LinkedList<Activity>();
    private static MusicApplication instance;
    public static MusicApplication getInstance()
    {
        return instance;
    }
    
    public static String album_art_path;
    
    @Override
    public void onCreate()
    {
        // TODO Auto-generated method stub
        super.onCreate();
        
        instance = this;
        CrashHandler crashHandler = CrashHandler.getInstance();  
        crashHandler.init(this);  
        
        File music_album_art_path = getExternalFilesDir(Environment.DIRECTORY_MUSIC);
        String path = music_album_art_path.getPath() + "/album_art/";
        SharedUtils.saveString(this, "album_art_path", path);
        album_art_path = SharedUtils.getString(this, "album_art_path", path);
    }

    public void exit()
    {
        for (Activity activity:mList)
        {
            activity.finish();
        }
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(0);
    }
    /**
     * 新建了一个activity
     * 
     * @param activity
     */
    public void addActivity(Activity activity)
    {
        mList.add(activity);
        //ActionBarColor.initSystemBar(activity);//状态栏和标题栏的颜色保持一致
    }
    
}
