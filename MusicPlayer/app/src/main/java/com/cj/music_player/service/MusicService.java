package com.cj.music_player.service;

import android.support.v7.app.NotificationCompat;
import com.pgyersdk.crash.PgyCrashManager;

import com.cj.music_player.R;
import com.cj.music_player.activity.MusicActivity;
import com.cj.music_player.info.MusicInfo;
import com.cj.music_player.db.MusicInfoDB;
import com.cj.music_player.db.SharedUtils;
import com.cj.music_player.db.SettingSharedUtils;
import com.cj.music_player.tools.AlbumBitmap;
import com.cj.music_player.Constants;
import com.cj.music_player.list.SongList;

import android.app.Service;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import java.util.List;
import android.app.NotificationManager;
import android.os.Handler;
import java.util.ArrayList;
import android.content.Intent;
import android.os.IBinder;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.app.PendingIntent;
import android.widget.RemoteViews;
import android.graphics.Bitmap;
import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.widget.Toast;
import java.io.IOException;
import android.content.SharedPreferences;
import java.util.Collections;

public class MusicService extends Service
{
    private MediaPlayer media;
    private List<MusicInfo> list = new ArrayList<MusicInfo>();
    
    private NotificationManager manger;

    private MusicBroad musicBd = new MusicBroad();

    private int num = 0;

    private Handler handler;

    private int play = 0;

    private boolean isplay = false;

    private MusicInfoDB db;

    private int mode = 0;// 播放模式,0是顺序播放,1是全部循环,2是单曲循环,3是随机播放

    private List<Integer> numList = new ArrayList<Integer>();

    private Intent clickIntent; // 列表当前项集合，目的记住前面所播放的所有歌曲  

    @Override
    public IBinder onBind(Intent intent)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void onCreate()
    {
        // TODO Auto-generated method stub
        super.onCreate();

        PgyCrashManager.register(MusicService.this);

        media = new MediaPlayer();

        list = SongList.getMusicList(MusicService.this);

        handler = new Handler();

        manger = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        IntentFilter mFilter = new IntentFilter();
        mFilter.addAction(Constants.LIST_INDEX);
        mFilter.addAction(Constants.PLAY);
        mFilter.addAction(Constants.SWITCH);
        mFilter.addAction(Constants.SEEKBAR);
        mFilter.addAction(Constants.MODE);
        mFilter.addAction(Constants.SEARCH);
        mFilter.addAction(Constants.NOTIFICATION);
        mFilter.addAction(Constants.UPDATE);
        mFilter.addAction(Constants.UPDATE_LIST);
        registerReceiver(musicBd, mFilter);

        mode = SharedUtils.getInt(MusicService.this, "mode", 0);
        mode();

        if (list.size() > 1)
        {
            num = SharedUtils.getInt(MusicService.this, "num", 0);
            if (num > list.size())
            {
                num = 0;
            }

            nowIndex();
        }
        else
        {
            //关闭服务器
            Intent intent = new Intent();
            intent.setClass(MusicService.this, MusicService.class);
            stopService(intent);
        }

    }

    private final static String NOTIFICATION_ID = "Id";
    /** 上一首 按钮点击 ID */
    private final static int NOTIFICATION_PLAY_ID = 1;
    /** 播放/暂停 按钮点击 ID */
    private final static int NOTIFICATION_AFTER_ID = 2;
    /** 下一首 按钮点击 ID */
    private final static int NOTIFICATION_BEFOER_ID = 3;
    //关闭通知栏
    private final static int NOTIFICATION_CIOSE_ID = 4;
    //通知栏
    private void Notification()
    {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(MusicService.this);
        builder.setContentTitle(list.get(num).getTitle());
        builder.setContentText(list.get(num).getArtist());
        builder.setTicker(list.get(num).getTitle());
        builder.setPriority(NotificationCompat.PRIORITY_MAX);
        builder.setSmallIcon(R.drawable.ic_launcher);
        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher));
        builder.setOngoing(true);
        // 通知行为点击后进入应用界面
        Intent intent1 = new Intent();
        intent1.setClass(MusicService.this, MusicActivity.class);
        PendingIntent pe = PendingIntent.getActivity(MusicService.this, 100, intent1, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pe);

        RemoteViews remove = new RemoteViews(getPackageName(), R.layout.notification);
        remove.setTextViewText(R.id.notification_title, list.get(num).getTitle());
        remove.setTextViewText(R.id.notification_arstst, list.get(num).getArtist());
        remove.setTextViewText(R.id.notification_number, num + 1 + "/" + list.size());

        Bitmap bitmap = null;
        bitmap = AlbumBitmap.AlbumImage(list.get(num).getPath());
        if (bitmap == null)
        {
            bitmap = AlbumBitmap.getAlbumImage(MusicService.this, num, Integer.valueOf(list.get(num).getAlbumId()));
            if (bitmap == null)
            {
                remove.setTextViewText(R.id.notification_tip, "没有专辑图片");
            }
        }
        if (bitmap != null)
        {
            remove.setTextViewText(R.id.notification_tip, "");
        }
        remove.setImageViewBitmap(R.id.notification_image, bitmap);

        if (isplay == false)
        {
            remove.setImageViewResource(R.id.notification_play, R.drawable.music_play);
        }
        else
        {
            remove.setImageViewResource(R.id.notification_play, R.drawable.music_pause);
        }

        Intent intent = new Intent(Constants.NOTIFICATION);

        intent.putExtra(NOTIFICATION_ID, NOTIFICATION_PLAY_ID);
        PendingIntent intent_play = PendingIntent.getBroadcast(MusicService.this, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        remove.setOnClickPendingIntent(R.id.notification_play, intent_play);

        intent.putExtra(NOTIFICATION_ID, NOTIFICATION_AFTER_ID);
        PendingIntent intent_after = PendingIntent.getBroadcast(MusicService.this, 2, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        remove.setOnClickPendingIntent(R.id.notification_after, intent_after);

        intent.putExtra(NOTIFICATION_ID, NOTIFICATION_BEFOER_ID);
        PendingIntent intent_before = PendingIntent.getBroadcast(MusicService.this, 3, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        remove.setOnClickPendingIntent(R.id.notification_before, intent_before);

        intent.putExtra(NOTIFICATION_ID, NOTIFICATION_CIOSE_ID);
        PendingIntent intent_close = PendingIntent.getBroadcast(MusicService.this, 4, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        remove.setOnClickPendingIntent(R.id.notification_close, intent_close);

        Notification notification = builder.build();
        notification.bigContentView = remove;

        manger.notify(14, notification);
    }

    //接收广播
    private class MusicBroad extends BroadcastReceiver
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            // TODO Auto-generated method stub
            if (intent.getAction().equals(Constants.NOTIFICATION))
            {
                int id = intent.getIntExtra(NOTIFICATION_ID, 0);
                switch (id)
                {
                    case NOTIFICATION_PLAY_ID:
                        if (isplay == false)
                        {
                            play();
                            isPlay(true);
                        }
                        else
                        {
                            pause();
                            isPlay(false);
                        }
                        break;
                    case NOTIFICATION_BEFOER_ID:
                        before();
                        isPlay(true);
                        break;
                    case NOTIFICATION_AFTER_ID:
                        after();
                        isPlay(true);
                        break;
                    case NOTIFICATION_CIOSE_ID:
                        manger.cancel(14);
                        pause();
                        break;
                    default:
                        break;
                }
            }
            else if (intent.getAction().equals(Constants.LIST_INDEX))
            {
                num = intent.getIntExtra(Constants.LIST_INDEX, 0);

                String path = list.get(num).getPath();
                setdata(path);             
                play();                             
            }
            else if (intent.getAction().equals(Constants.SEARCH))
            {
                String title = intent.getStringExtra(Constants.SEARCH);

                for (int i = 0; i < list.size(); i++)
                {
                    if (title.equals(list.get(i).getTitle()))
                    {
                        num = i;
                        String path = list.get(num).getPath();
                        setdata(path);
                        play();
                    }
                }
            }
            else if (intent.getAction().equals(Constants.PLAY))
            {
                boolean isplay = intent.getBooleanExtra(Constants.PLAY, false);
                if (isplay == false)
                {
                    pause();
                    handler.removeCallbacks(mRunnable);       
                }
                else
                {
                    if (play == 0)
                    {
                        String path = list.get(num).getPath();
                        setdata(path);
                        play();

                        play = 1;
                    }
                    else
                    {
                        play();
                    }
                }                    
            }
            else if (intent.getAction().equals(Constants.SWITCH))
            {
                int next = intent.getIntExtra(Constants.SWITCH, 0);
                if (next == 0)
                {
                    before();
                }
                if (next == 1)
                {
                    after();  
                }
            }
            else if (intent.getAction().equals(Constants.SEEKBAR))
            {
                int progress = intent.getIntExtra(Constants.SEEKBAR, 0);
                media.seekTo(progress);
            }
            else if (intent.getAction().equals(Constants.MODE))
            {
                mode = intent.getIntExtra(Constants.MODE, 0);

                if (mode == 0)
                {
                    mode = 0;
                    Toast.makeText(MusicService.this, "顺序播放", 1000).show();
                }
                if (mode == 1)
                {
                    mode = 1;
                    Toast.makeText(MusicService.this, "全部循环", 1000).show();
                }
                if (mode == 2)
                {
                    mode = 2;
                    Toast.makeText(MusicService.this, "单曲循环", 1000).show();
                }
                if (mode == 3)
                {
                    mode = 3;
                    Toast.makeText(MusicService.this, "随机播放", 1000).show();
                }       
                mode();
                SharedUtils.saveInt(MusicService.this, "mode", mode);
            }
            else if (intent.getAction().equals(Constants.UPDATE))
            {
                nowIndex();//刷新
            }
            else if (intent.getAction().equals(Constants.UPDATE_LIST))
            {
                list = SongList.getMusicList(MusicService.this);
                for (int i = 0; i < list.size(); i++)
                {
                    if (SharedUtils.getString(MusicService.this, "nowTitle", list.get(num).getTitle()).equals(list.get(i).getTitle()))
                    {
                        num = i;
                    }
                }
                nowIndex();
            }
        }
    }
    /**
     * 播放操作
     */
    private void play()
    {
        media.start();
        handler.postDelayed(mRunnable, 1000);

        maxTime();
        nowIndex();

        isplay = true;
        Notification();
        isPlay(true);

        SharedUtils.saveInt(MusicService.this, "num", num);
        SharedUtils.saveString(MusicService.this, "nowTitle", list.get(num).getTitle());

        numList.add(num);
    }
    //暂停
    private void pause()
    {
        media.pause();
        isplay = false;
        Notification();
    }
    /**
     * 自动播放操作
     */
    private void autoPlay()
    {
        if (mode == 1)
        {
            if (num != list.size() - 1)
            {
                after();
            }
            else
            {
                pause();
            }
        }
        else if (mode == 2)
        {
            play();
        }
        else
        {
            after();
        }
    }
    /**
     * 上一首操作
     */
    private void before()
    {      
        if (list.size() > 0)
        {
            if (mode == 3)
            {
                if (numList.size() > 1)
                {
                    numList.remove(numList.size() - 1);
                    num = numList.get(numList.size() - 1);
                }
                else
                {
                    num = (int) (Math.random() * list.size());
                }
                numList.clear();
            }
            else
            {
                if (num == 0)
                {
                    num = list.size() - 1;
                }
                else
                {
                    num--;
                }
            }
            String path = list.get(num).getPath();
            setdata(path);                   
            play();
        }
    }
    /**
     * 下一首操作
     */
    private void after()
    {
        if (list.size() > 0)
        {
            if (mode == 3)
            {
                num = (int) (Math.random() * list.size());
            }
            else
            {
                if (num == list.size() - 1)
                {
                    num = 0;
                }
                else
                {
                    num++;
                }
            }
            String path = list.get(num).getPath();
            setdata(path);                   
            play();
        }
    }

    //播放模式
    private void mode()
    {
        media.setOnCompletionListener(new OnCompletionListener(){

                @Override
                public void onCompletion(MediaPlayer mp)
                {
                    // TODO: Implement this method
                    if (mode == 0)
                    {
                        num++;
                        if (num > list.size() - 1)
                        {
                            num = 0;
                        }
                        String path = list.get(num).getPath();
                        setdata(path);      
                        play(); 
                        if (num == 0)
                        {
                            pause();
                            isPlay(false);
                        }
                    }
                    if (mode == 1)
                    {
                        num++;
                        if (num > list.size() - 1)
                        {
                            num = 0;
                        }
                        String path = list.get(num).getPath();
                        setdata(path);      
                        play(); 
                    }
                    if (mode == 2)
                    {
                        String path = list.get(num).getPath();
                        setdata(path);                
                        play();
                    }
                    if (mode == 3)
                    {
                        num = (int) (Math.random() * list.size());
                        String path = list.get(num).getPath();
                        setdata(path);                   
                        play();
                    }
                }
            });
    }
    /**
     * 播放音乐路径
     */
    private void setdata(String path)
    {
        media.reset();
        try
        {
            media.setDataSource(path);
            media.prepare();
        }
        catch (IllegalArgumentException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (IllegalStateException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    //通知播放
    private void isPlay(Boolean isplay)
    {
        Intent intent = new Intent();
        intent.setAction(Constants.ISPLAY);
        intent.putExtra(Constants.ISPLAY, isplay);
        sendBroadcast(intent);
    }

    private void maxTime()
    {
        Intent intent = new Intent();
        intent.setAction(Constants.MAXTIME);
        intent.putExtra(Constants.MAXTIME, media.getDuration());
        sendBroadcast(intent);       
    }

    private void nowTime()
    {       
        Intent intent = new Intent();
        intent.setAction(Constants.NOWTIME);
        intent.putExtra(Constants.NOWTIME, media.getCurrentPosition());
        sendBroadcast(intent);        
    }

    private void nowIndex()
    {
        Intent intent = new Intent();
        intent.setAction(Constants.NOWINDEX);
        intent.putExtra(Constants.NOWINDEX, num);
        sendBroadcast(intent);
    }

    Runnable mRunnable = new Runnable() {

        @Override
        public void run()
        {
            // TODO Auto-generated method stub
            nowTime();

            maxTime();
            /*if (media.isPlaying())
             {
             isPlay(true);
             }
             else
             {
             isPlay(false);
             }*/

            handler.postDelayed(mRunnable, 1000);
        }
    };

}
