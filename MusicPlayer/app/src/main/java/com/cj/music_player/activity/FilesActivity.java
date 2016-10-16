package com.cj.music_player.activity;

import android.support.v7.app.AppCompatActivity;

import com.cj.music_player.R;
import com.cj.music_player.adapter.FilesAdapter;
import com.cj.music_player.Constants;
import com.cj.music_player.view.MusicListView;

import android.widget.TextView;
import android.widget.ListView;
import java.io.File;
import android.os.Bundle;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView;
import android.view.View;
import android.widget.Toast;
import android.content.Intent;
import android.widget.ImageView;
import android.view.View.OnClickListener;
import android.os.Environment;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import android.content.Context;
import android.view.KeyEvent;

public class FilesActivity extends AppCompatActivity
{
    private TextView text;
    private MusicListView list;
    private String path = "/storage";//Environment.getExternalStorageDirectory().getAbsolutePath();
    private File[] files;
    private int n = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        // TODO: Implement this method
        super.onCreate(savedInstanceState);
        setContentView(R.layout.files);

        text = (TextView) findViewById(R.id.files_path);
        list = (MusicListView) findViewById(R.id.files_list);
        init(new File(path)); 
        list.setOnItemClickListener(new OnItemClickListener(){
                
                @Override
                public void onItemClick(AdapterView<?> p1, View p2, int p3, long p4)
                {
                    n = p3;
                    if (files[p3] == null)
                    {
                        try
                        {
                            File rh=new File(path);
                            if (!rh.getPath().equals("/") && rh.exists())
                            {
                                init(new File(rh.getParent()));
                            }
                        }
                        catch (Exception e)
                        {
                            Toast.makeText(FilesActivity.this, e.toString(), 1).show();
                        }
                    }
                    else
                    {
                        // 获取单击的文件或文件夹的名称  
                        File d=files[p3];
                        if (d.isDirectory())
                        {
                            init(d);
                        }
                        else
                        {
                            //点击文件后
                            Toast.makeText(FilesActivity.this, d.getName(), 1).show();

                            String p = d.getPath();
                            String sbr = p.substring(p.length() - 3, p.length());
                            if (sbr.equalsIgnoreCase("mp3"))
                            {
                                Intent intent = new Intent();
                                intent.setAction(Constants.SELECT);
                                intent.putExtra("path", d.getPath());
                                intent.putExtra("name", d.getName());
                                sendBroadcast(intent);

                                isPlay();
                            }
                        }
                    }
                }
            });

        ImageView back = (ImageView) findViewById(R.id.files_back);
        back.setOnClickListener(new OnClickListener(){

                @Override
                public void onClick(View p1)
                {
                    // TODO: Implement this method
                    finish();
                }
            });
    }

    //界面初始化  
    private void init(File f)
    {  
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
        {  
            // 获取SDcard目录下所有文件名  
            files = f.listFiles();
            Arrays.sort(files);
            int dd=0;
            List<File> f2 = new ArrayList<File>();
            f2.add(null);
            for (File dd1:files)
            {
                if (dd1.isDirectory() && dd1.getName().matches("^\\..+$"))
                {
                    f2.add(dd1);
                    dd++;
                }
            }
            for (File dd1:files)
            {
                if (dd1.isDirectory())
                {
                    f2.add(dd1);
                    dd++;
                }
            }
            for (File dd1:files)
            {
                if (dd1.isFile())
                {
                    f2.add(dd1);
                    dd++;
                }
            }

            files = f2.toArray(new File[f2.size()]);
            path = f.getPath();  
            text.setText(f.getPath());
            Context h=FilesActivity.this;
            list.setAdapter(new FilesAdapter(h, files));
        }
    }

    //返回
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK)
        {
            File rh=new File(path);
            if (!rh.getPath().equals("/") && rh.exists())
            {
                init(new File(rh.getParent()));
                list.setSelection(n);
            }
            else
            {
                finish();
            }
            return false;
        }
        else
        {
            return super.onKeyDown(keyCode, event);
        }
    }

    //通知播放
    private void isPlay()
    {
        Intent intent = new Intent();
        intent.setAction(Constants.ISPLAY);
        intent.putExtra(Constants.ISPLAY, true);
        sendBroadcast(intent);
    }

}
