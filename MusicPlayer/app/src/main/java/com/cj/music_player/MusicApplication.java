package com.cj.music_player;

import com.pgyersdk.crash.PgyCrashManager;

import android.app.Application;
import java.util.List;
import android.app.Activity;
import java.util.LinkedList;
import java.io.File;
import android.os.Environment;

public class MusicApplication extends Application
{
    private List<Activity> activityList = new LinkedList<Activity>();
    private static MusicApplication instance;
    
    private static File app_path;
    
    @Override
    public void onCreate()
    {
        // TODO Auto-generated method stub
        super.onCreate();
        
        instance = this;
        CrashHandler crashHandler = CrashHandler.getInstance();  
        crashHandler.init(this);  
        
        PgyCrashManager.register(this);
        
        app_path = getExternalFilesDir(Environment.DIRECTORY_MUSIC);
    }
    
    public static MusicApplication getInstance()
    {
        return instance;
    }
    
    public static String getAlbumImagePath()
    {
        return app_path.getPath() + "/album_image/";
    }

    public void exit()
    {
        for (Activity activity:activityList)
        {
            activity.finish();
        }
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(0);
    }
    /**
     * 新建了一个activity
     */
    public void addActivity(Activity activity)
    {
        activityList.add(activity);
        //ActionBarColor.initSystemBar(activity);//状态栏和标题栏的颜色保持一致
    }

}
