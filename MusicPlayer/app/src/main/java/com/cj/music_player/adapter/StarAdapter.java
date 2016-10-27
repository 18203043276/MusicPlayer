package com.cj.music_player.adapter;

import com.cj.music_player.R;
import com.cj.music_player.info.StarInfo;
import com.cj.music_player.tools.ListImageLoader;
import com.cj.music_player.tools.ListImageLoader.Type;

import android.widget.BaseAdapter;
import android.content.Context;
import java.util.List;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.RatingBar;

public class StarAdapter extends BaseAdapter
{
    private Context context;
    private List<StarInfo> list;

    public StarAdapter(Context context, List<StarInfo> list)
    {
        this.context = context;
        this.list = list;
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
            convertView = LayoutInflater.from(context).inflate(R.layout.star_list_item, null);

            viewhodler.title = (TextView) convertView.findViewById(R.id.star_list_item_title);
            viewhodler.number = (TextView) convertView.findViewById(R.id.star_number);
            viewhodler.artist = (TextView) convertView.findViewById(R.id.star_list_item_artist);
            viewhodler.image = (ImageView) convertView.findViewById(R.id.star_list_item_ImageView);
            viewhodler.star = (RatingBar) convertView.findViewById(R.id.star_list_item_RatingBar);

            convertView.setTag(viewhodler);
        }
        else
        {
            viewhodler = (viewhodler) convertView.getTag();
        }
        viewhodler.title.setText(list.get(position).getTitle());
        viewhodler.artist.setText(list.get(position).getArtist());
        viewhodler.number.setText(position + 1 + "/" + list.size());
        viewhodler.star.setRating(Integer.valueOf(list.get(position).getStar()));
        if (list.get(position).getAlbumImagePath() != null)
        {
            ListImageLoader.getInstance(9, Type.LIFO).loadImage(list.get(position).getAlbumImagePath(), viewhodler.image);
        }

        return convertView;
    }
    static class viewhodler
    {
        TextView title, number, artist;
        ImageView image;
        RatingBar star;
    }

}
