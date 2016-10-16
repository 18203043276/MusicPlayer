package com.cj.music_player.adapter;

import com.cj.music_player.R;
import com.cj.music_player.info.FolderInfo;

import android.widget.BaseAdapter;
import android.view.ViewGroup;
import android.view.View;
import android.content.Context;
import java.util.List;
import android.view.LayoutInflater;
import android.widget.TextView;
import android.widget.ImageView;

public class FolderAdapter extends BaseAdapter
{
    private Context context;
    private List<FolderInfo> list;
    
    public FolderAdapter(Context context, List<FolderInfo> list)
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
            convertView = LayoutInflater.from(context).inflate(R.layout.folder_list_item, null);

            viewhodler.name = (TextView) convertView.findViewById(R.id.folder_list_item_name);
            viewhodler.path = (TextView) convertView.findViewById(R.id.folder_list_item_path);
            viewhodler.icon = (ImageView) convertView.findViewById(R.id.folder_list_item_icon);
            
            convertView.setTag(viewhodler);
        }
        else
        {
            viewhodler = (viewhodler) convertView.getTag();
        }
        viewhodler.name.setText(list.get(position).getTitle());
        viewhodler.path.setText(list.get(position).getPath());
       
        return convertView;
    }
    static class viewhodler
    {
        TextView name, path;
        ImageView icon;
    }

}
