package com.cj.music_player.activity;

import android.support.v7.app.AppCompatActivity;
import com.pgyersdk.crash.PgyCrashManager;

import com.cj.music_player.R;
import com.cj.music_player.info.MusicInfo;
import com.cj.music_player.list.SongList;
import com.cj.music_player.tools.BitmapTools;
import com.cj.music_player.tools.AlbumBitmap;
import com.cj.music_player.db.SettingSharedUtils;

import android.widget.ImageView;
import android.widget.TextView;
import java.util.List;
import android.widget.Button;
import android.graphics.Bitmap;
import android.os.Bundle;
import java.util.ArrayList;
import android.content.Intent;
import android.graphics.Color;
import android.view.View.OnClickListener;
import android.view.View;
import android.widget.Toast;
import java.util.Collections;

public class AlbumImageActivity extends AppCompatActivity
{
    private int num;
    private ImageView imageView;
    private TextView text;

    private List<MusicInfo> list = new ArrayList<MusicInfo>();
    private BitmapTools bitmap_tools =new BitmapTools();

    private Button save, select;
    private Bitmap bitmap = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        // TODO: Implement this method
        super.onCreate(savedInstanceState);
        setContentView(R.layout.album_image);
        
        PgyCrashManager.register(AlbumImageActivity.this);
     
        list = SongList.getMusicList(AlbumImageActivity.this);
        if (SettingSharedUtils.getBoolean(AlbumImageActivity.this, "music_list_sort", true) == false)
        {
            Collections.reverse(list);
        }

        Intent intent = this.getIntent();
        num = intent.getIntExtra("num", 0);
        
        imageView = (ImageView) findViewById(R.id.album_image_ImageView);
        text = (TextView) findViewById(R.id.album_image_TextView);
        save = (Button) findViewById(R.id.album_image_save);
        if (SettingSharedUtils.getBoolean(AlbumImageActivity.this,"save_album_image", true) == false)
        {
            save.setVisibility(View.INVISIBLE);
        }

        bitmap = AlbumBitmap.AlbumImage(list.get(num).getPath());
        if (bitmap == null)
        {
            bitmap = AlbumBitmap.getAlbumImage(AlbumImageActivity.this, num, Integer.valueOf(list.get(num).getAlbumId()));
            if (bitmap == null)
            {
                text.setText("没有专辑图片");
                text.setTextColor(Color.RED);
            }
            else
            {
                imageView.setImageBitmap(bitmap);
                text.setText(bitmap_tools.bitmapWidthHeight(bitmap));
            }
        }
        else
        {
            imageView.setImageBitmap(bitmap);
            text.setText(bitmap_tools.bitmapWidthHeight(bitmap));
        }
        
        save.setOnClickListener(new OnClickListener(){

                @Override
                public void onClick(View p1)
                {
                    // TODO: Implement this method
                    BitmapTools.saveBitmap(bitmap, SettingSharedUtils.getString(AlbumImageActivity.this, "sava_album_image_path", "/sdcard/音乐专辑图片") + "/", list.get(num).getName() + ".png", Bitmap.CompressFormat.PNG, 100);
                    Toast.makeText(AlbumImageActivity.this, "保存成功", Toast.LENGTH_SHORT).show();
                }
            });
        select = (Button) findViewById(R.id.album_image_select);

    }

}
