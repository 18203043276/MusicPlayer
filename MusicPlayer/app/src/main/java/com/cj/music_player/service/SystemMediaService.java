package com.cj.music_player.service;

import android.content.Context;
import java.io.File;
import android.os.Build;
import android.media.MediaScannerConnection;
import android.content.Intent;
import android.net.Uri;
import android.app.ActivityManager;
import java.util.List;

public class SystemMediaService
{
    /**
     * 发送广播，通知系统扫描指定的文件
     */
    public void scanSDCard(Context context, File file)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
        {
            // 判断SDK版本是不是4.4或者高于4.4
            String[] paths = new String[]{ "/storage" };
            MediaScannerConnection.scanFile(context, paths, null, null);
        }
        else
        {
            Intent intent = new Intent(Intent.ACTION_MEDIA_MOUNTED);
            intent.setClassName("com.android.providers.media", "com.android.providers.media.MediaScannerReceiver");
            intent.setData(Uri.parse("file://" + file.getAbsolutePath()));//MediaUtils.getMusicDir()
            context.sendBroadcast(intent);
        }
    }
    /**
     * 判断服务是否启动,context上下文对象 ，className服务的name
     */
    public boolean isServiceRunning(Context mContext, String className)
    {

        boolean isRunning = false;
        ActivityManager activityManager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> serviceList = activityManager.getRunningServices(30);

        if (!(serviceList.size() > 0))
        {
            return false;
        }

        for (int i = 0; i < serviceList.size(); i++)
        {
            if (serviceList.get(i).service.getClassName().equals(className) == true)
            {
                isRunning = true;
                break;
            }
        }
        return isRunning;
    }

}
