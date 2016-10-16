package com.cj.music_player.adapter;

import com.cj.music_player.R;
import com.cj.music_player.info.MusicInfo;

import android.widget.BaseAdapter;
import android.view.LayoutInflater;
import java.util.List;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class SearchAdapter extends BaseAdapter
{
    private LayoutInflater mInflater;
    private List<MusicInfo> list;
    private Context context;

    public SearchAdapter(Context context, List<MusicInfo> list)
    {
        this.list = list;
        this.context = context;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount()
    {
        return list.size();
    }

    @Override
    public Object getItem(int position)
    {
        return list.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        viewhodler viewhodler;
        if (convertView == null)
        {
            viewhodler = new viewhodler();
            convertView = LayoutInflater.from(context).inflate(R.layout.search_list_item, null);

            viewhodler.title = (TextView) convertView.findViewById(R.id.list_title);
            viewhodler.singer = (TextView) convertView.findViewById(R.id.list_singer);
            viewhodler.time = (TextView) convertView.findViewById(R.id.list_time);
            viewhodler.number = (TextView) convertView.findViewById(R.id.list_number);
          
            convertView.setTag(viewhodler);
        }
        else
        {
            viewhodler = (viewhodler) convertView.getTag();
        }
        viewhodler.title.setText("" + list.get(position).getTitle());
        viewhodler.singer.setText("" + list.get(position).getArtist());
        viewhodler.time.setText("" + list.get(position).getTime());
        viewhodler.number.setText(position + 1 + "/" + list.size());
        
 
        return convertView;
    }
    static class viewhodler
    {
        TextView title, singer, time, number;
    }
    
}
