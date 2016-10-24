package com.cj.music_player;

import android.support.v7.app.AppCompatActivity;

import com.cj.music_player.activity.MusicActivity;
import com.cj.music_player.db.SharedUtils;
import com.cj.music_player.info.MusicInfo;
import com.cj.music_player.tools.BitmapTools;
import com.cj.music_player.tools.AlbumBitmap;
import com.cj.music_player.list.SongList;

import java.util.List;
import android.widget.RelativeLayout;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import java.util.ArrayList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.content.Intent;
import android.view.KeyEvent;
import java.util.Collections;

public class LogoActivity extends AppCompatActivity
{
    private BitmapTools bitmap_tools = new BitmapTools();
    private List<MusicInfo> list = new ArrayList<MusicInfo>();
    private RelativeLayout layout;
    private int num;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        // TODO: Implement this method
        Window window = getWindow();//第一行
        window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,//第二行
                        WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);//第三行
        //在这个地方加入以上三行，你就能获得iOS一样的沉浸任务栏了！
        super.onCreate(savedInstanceState);
        setContentView(R.layout.logo);

        MusicApplication.getInstance().addActivity(this);

        list = SongList.getMusicList(LogoActivity.this);

        num = SharedUtils.getInt(LogoActivity.this, "num", 0);
        if (num > list.size())
        {
            num = 0;
        }
        layout = (RelativeLayout) findViewById(R.id.logo_RelativeLayout);

        if (list.size() > 1)
        {
            Bitmap bitmap = null;
            bitmap = AlbumBitmap.AlbumImage(list.get(num).getPath());
            if (bitmap != null)
            {
                if (bitmap.getWidth() > 1200 && bitmap.getHeight() > 1200)
                {
                    bitmap = BitmapTools.ScaleBitmap(bitmap, 1200, 1200, true);
                }
            }
            if (bitmap == null)
            {
                bitmap = AlbumBitmap.getAlbumImage(LogoActivity.this, num, Integer.valueOf(list.get(num).getAlbumId()));
            }
            if (bitmap != null)
            {
                layout.setBackgroundDrawable(bitmap_tools.bitmapToDrawable(bitmap_tools.createReflectionBitmap(bitmap)));
            }
            run();
        }
        else
        {
            run();
        }
    }

    private void run()
    {
        // TODO: Implement this method
        new Handler().postDelayed(new Runnable(){

                @Override
                public void run()
                {
                    Intent intent = new Intent(LogoActivity.this, MusicActivity.class);
                    startActivity(intent);
                    finish();
                }
            }, 3000);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        //在欢迎界面屏蔽BACK键
        if (keyCode == KeyEvent.KEYCODE_BACK)
        {
            return false;
        }
        return false;
    }

}
