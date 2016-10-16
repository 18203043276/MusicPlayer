package com.cj.music_player.adapter;

import com.cj.music_player.R;
import com.cj.music_player.info.MusicInfo;
import com.cj.music_player.tools.ListImageLoader;
import com.cj.music_player.tools.ListImageLoader.Type;
import com.cj.music_player.db.SharedUtils;

import android.widget.BaseAdapter;
import android.view.LayoutInflater;
import java.util.List;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ImageView;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class QueryAdapter extends BaseAdapter
{
    private LayoutInflater mInflater;
    private List<MusicInfo> list;
    private Context context;

    public QueryAdapter(Context context, List<MusicInfo> list)
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
            convertView = LayoutInflater.from(context).inflate(R.layout.query_list_item, null);

            viewhodler.title = (TextView) convertView.findViewById(R.id.query_list_title);
            viewhodler.singer = (TextView) convertView.findViewById(R.id.query_list_singer);
            viewhodler.time = (TextView) convertView.findViewById(R.id.query_list_time);
            viewhodler.number = (TextView) convertView.findViewById(R.id.query_list_number);
            viewhodler.icon = (ImageView) convertView.findViewById(R.id.query_list_item_ImageView);

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

        String path = list.get(position).getAlbumImagePath();
        if (path == null)
        {
            viewhodler.icon.setImageResource(R.drawable.main_menu_left_image);
        }
        else
        {
            ListImageLoader.getInstance(9, Type.LIFO).loadImage(list.get(position).getAlbumImagePath(), viewhodler.icon);
        }

        return convertView;
    }
    static class viewhodler
    {
        TextView title, singer, time, number;
        ImageView icon;
    }
}
