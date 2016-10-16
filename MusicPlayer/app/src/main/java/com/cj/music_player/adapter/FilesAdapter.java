package com.cj.music_player.adapter;

import com.cj.music_player.R;

import android.widget.BaseAdapter;
import android.content.Context;
import java.io.File;
import android.content.pm.PackageManager;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.TextView;
import android.graphics.Color;
import android.widget.Toast;

public class FilesAdapter extends BaseAdapter
{
    private Context gg;
    private File[] companies;
    
    public PackageManager getPackageManager()
    {
        return gg.getPackageManager();
    }
    @Override
    public int getCount()
    {
        return companies.length;
    }
    @Override
    public Object getItem(int p1)
    {
        return getItem(p1);
    }
    @Override
    public long getItemId(int p1)
    {
        return p1;
    }
    public FilesAdapter(Context context, File[] g) 
    {
        this.gg = context;
        this.companies = g;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        try
        {
            convertView = LayoutInflater.from(gg).inflate(R.layout.files_list_item, null);
            ImageView image=(ImageView)convertView.findViewById(R.id.files_icon);
            TextView name=(TextView)convertView.findViewById(R.id.files_name);
            TextView path=(TextView)convertView.findViewById(R.id.pach);
            TextView size=(TextView)convertView.findViewById(R.id.files_size);
            File c=companies[position];
            if (c != null)
            {
                if (c.isFile())
                {
                    image.setImageResource(R.drawable.format_unkown);
                    
                    size.setText(getsize(c.getPath()));
                    
                    String sb = c.getPath().substring(c.getPath().length() - 2, c.getPath().length());
                    if (sb.equalsIgnoreCase("7z"))
                    {
                        image.setImageResource(R.drawable.format_zip);
                    }
                    
                    String sbr = c.getPath().substring(c.getPath().length() - 3, c.getPath().length());
                    if (sbr.equalsIgnoreCase("mp3"))
                    {
                        image.setImageResource(R.drawable.format_music);
                    }
                    if (sbr.equalsIgnoreCase("txt"))
                    {
                        image.setImageResource(R.drawable.format_text);
                    }
                    if (sbr.equalsIgnoreCase("apk"))
                    {
                        image.setImageResource(R.drawable.format_apk);
                    }
                    if (sbr.equalsIgnoreCase("zip"))
                    {
                        image.setImageResource(R.drawable.format_zip);
                    }
                    if (sbr.equalsIgnoreCase("rar"))
                    {
                        image.setImageResource(R.drawable.format_zip);
                    }
                    if (sbr.equalsIgnoreCase("mp4"))
                    {
                        image.setImageResource(R.drawable.format_media);
                    }
                    if (sbr.equalsIgnoreCase("mkv"))
                    {
                        image.setImageResource(R.drawable.format_media);
                    }
                    if (sbr.equalsIgnoreCase("3gp"))
                    {
                        image.setImageResource(R.drawable.format_media);
                    }
                    if (sbr.equalsIgnoreCase("png"))
                    {
                        image.setImageResource(R.drawable.format_picture);
                    }
                    
                    String sbrt = c.getPath().substring(c.getPath().length() - 4, c.getPath().length());
                    if (sbrt.equalsIgnoreCase("rmvb"))
                    {
                        image.setImageResource(R.drawable.format_media);
                    }
                }
                else
                {
                    image.setImageResource(R.drawable.format_folder);
                    size.setText("目录");
                }
                
                String h=c.getName();
                name.setText(h);
                path.setText(c.getPath());
                
            }
            else
            {
                //name.setText("上一级");
                size.setText(" . . .");
                size.setTextColor(Color.BLUE);
                size.setTextSize(20);
                image.setImageResource(R.drawable.format_folder);
                path.setVisibility(View.GONE);
            }
        }
        catch (Exception e)
        {
            Toast.makeText(gg, e.toString(), 1).show();
            e.printStackTrace();
        }
        return convertView;
    }
    
    public static String getsize(String path)
    {
        try
        {
            int le=(int)new File(path).length();
            int MB,KB,B;
            MB = le / (1024 * 1024);//MB
            KB = (le % (1024 * 1024)) / 1024;//KB
            B = le % 1024;//B
            String size="";
            if (MB > 0)
            {
                String gh=KB + "";
                gh = gh.length() < 2 ?gh: gh.substring(0, 2);
                size = MB + (KB == 0 ?"": "." + gh) + "MB";
            }
            else
            if (KB > 0)
            {
                String gh=B + "";
                gh = gh.length() < 2 ?gh: gh.substring(0, 2);
                size = (KB + (B == 0 ?"": "." + gh)) + "KB";
            }
            else
            {
                size = B + "B";
            }
            return size;
        }
        catch (Exception e)
        {
            return e.toString();
        }
    }
  
}
