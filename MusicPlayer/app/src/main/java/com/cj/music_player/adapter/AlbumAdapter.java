package com.cj.music_player.adapter;

import com.cj.music_player.R;
import com.cj.music_player.info.AlbumInfo;
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

public class AlbumAdapter extends BaseAdapter
{
    private Context context;
    private List<AlbumInfo> list;
 
    public AlbumAdapter(Context context, List<AlbumInfo> list)
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
            convertView = LayoutInflater.from(context).inflate(R.layout.album_list_item, null);

            viewhodler.name = (TextView) convertView.findViewById(R.id.album_list_item_name);
            viewhodler.number = (TextView) convertView.findViewById(R.id.album_number);
            viewhodler.list_number = (TextView) convertView.findViewById(R.id.album_list_item_number);
            viewhodler.artist = (TextView) convertView.findViewById(R.id.album_list_item_artist);
            viewhodler.image = (ImageView) convertView.findViewById(R.id.album_list_item_image);

            convertView.setTag(viewhodler);
        }
        else
        {
            viewhodler = (viewhodler) convertView.getTag();
        }
        viewhodler.name.setText(list.get(position).getTitle());
        viewhodler.number.setText(list.get(position).getNumber());
        viewhodler.list_number.setText(position + 1 + "/" + list.size());
        viewhodler.artist.setText(list.get(position).getAlbumArtist());
        ListImageLoader.getInstance(9, Type.LIFO).loadImage(list.get(position).getAlbumArt(), viewhodler.image);
        
        return convertView;
    }
    static class viewhodler
    {
        TextView name, number, artist, list_number;
        ImageView image;
    }

}
