package com.cj.music_player.adapter;

import com.cj.music_player.R;
import com.cj.music_player.info.MusicInfo;
import com.cj.music_player.tools.ListImageLoader;
import com.cj.music_player.tools.ListImageLoader.Type;

import android.widget.BaseAdapter;
import android.view.LayoutInflater;
import java.util.List;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ImageView;

public class QueryAdapter extends BaseAdapter
{
    private List<MusicInfo> list;
    private Context context;

    public QueryAdapter(Context context, List<MusicInfo> list)
    {
        this.list = list;
        this.context = context;
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
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        viewhodler viewhodler;
        if (convertView == null)
        {
            viewhodler = new viewhodler();
            convertView = LayoutInflater.from(context).inflate(R.layout.query_list_item, null);

            viewhodler.title = (TextView) convertView.findViewById(R.id.query_list_title);
            viewhodler.singer = (TextView) convertView.findViewById(R.id.query_list_singer);
            viewhodler.time = (TextView) convertView.findViewById(R.id.query_list_time);
            viewhodler.number = (TextView) convertView.findViewById(R.id.query_list_number);
            viewhodler.image = (ImageView) convertView.findViewById(R.id.query_list_item_ImageView);

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
        if (list.get(position).getAlbumImagePath() != null)
        {
            ListImageLoader.getInstance(9, Type.LIFO).loadImage(list.get(position).getAlbumImagePath(), viewhodler.image);
        }

        return convertView;
    }
    static class viewhodler
    {
        TextView title, singer, time, number;
        ImageView image;
    }

}
