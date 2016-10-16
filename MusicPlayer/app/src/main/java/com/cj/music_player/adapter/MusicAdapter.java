package com.cj.music_player.adapter;

import com.cj.music_player.R;
import com.cj.music_player.info.MusicInfo;

import android.widget.BaseAdapter;
import java.util.List;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.widget.TextView;
import android.widget.LinearLayout;
import android.graphics.Color;

public class MusicAdapter extends BaseAdapter
{
    private List<MusicInfo> list;
    private Context context;
    private int num = 0;
    
    public  MusicAdapter(Context context, List<MusicInfo> list)
    {
        // TODO Auto-generated constructor stu
        this.list = list;
        this.context = context;
    }
    
    public void setNum(int num)
    {
        this.num = num;
    }
    
    @Override
    public int getCount()
    {
        // TODO Auto-generated method stub
        return list.size();
    }

    @Override
    public Object getItem(int position)
    {
        // TODO Auto-generated method stub
        return list.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        // TODO Auto-generated method stub
        viewhodler viewhodler;
        if (convertView == null)
        {
            viewhodler = new viewhodler();
            convertView = LayoutInflater.from(context).inflate(R.layout.music_list_item, null);

            viewhodler.title = (TextView) convertView.findViewById(R.id.list_title);
            viewhodler.singer = (TextView) convertView.findViewById(R.id.list_singer);
            viewhodler.number = (TextView) convertView.findViewById(R.id.list_number);
            viewhodler.time = (TextView) convertView.findViewById(R.id.list_time);

            convertView.setTag(viewhodler);
        }
        else
        {
            viewhodler = (viewhodler) convertView.getTag();
        }
        viewhodler.title.setText(list.get(position).getTitle());
        viewhodler.singer.setText(list.get(position).getArtist());
        viewhodler.number.setText(position + 1 + "/" + list.size());
        viewhodler.time.setText(list.get(position).getTime());
        
        if (position == num)
        {
            convertView.setBackgroundColor(Color.BLACK);
        }
        else
        {
            convertView.setBackgroundColor(Color.parseColor("#00000000"));
        }
        
        return convertView;
    }
    static class viewhodler
    {
        TextView title, singer, time, number;
    }
    
}
