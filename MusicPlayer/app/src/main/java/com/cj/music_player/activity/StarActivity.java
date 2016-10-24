package com.cj.music_player.activity;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBar;
import com.pgyersdk.crash.PgyCrashManager;

import com.cj.music_player.R;
import com.cj.music_player.list.SongList;
import com.cj.music_player.info.StarInfo;
import com.cj.music_player.view.MusicListView;
import com.cj.music_player.adapter.StarAdapter;
import com.cj.music_player.Constants;
import com.cj.music_player.util.SortUtils;
import com.cj.music_player.db.SettingSharedUtils;

import android.os.Bundle;
import android.view.MenuItem;
import java.util.List;
import java.util.ArrayList;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView;
import android.widget.Adapter;
import android.view.View;
import android.content.Intent;
import java.util.Collections;

public class StarActivity extends AppCompatActivity
{
    private List<StarInfo> list = new ArrayList<StarInfo>();
    private SortUtils sort = new SortUtils();
    private SongList songList = new SongList();
    private StarAdapter adapter;
    private MusicListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        // TODO: Implement this method
        super.onCreate(savedInstanceState);
        setContentView(R.layout.star);
        
        PgyCrashManager.register(StarActivity.this);

        ActionBar bar = getSupportActionBar();
        bar.setDisplayHomeAsUpEnabled(true);

        list = songList.getStarList(StarActivity.this);
        Collections.sort(list, sort.new SortStarList());
        if (SettingSharedUtils.getBoolean(StarActivity.this, "star_list_sort", true) == false)
        {
            Collections.reverse(list);
        }
        listView = (MusicListView) findViewById(R.id.star_ListView);
        adapter = new StarAdapter(StarActivity.this, list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new OnItemClickListener(){

                @Override
                public void onItemClick(AdapterView<?> p1, View p2, int p3, long p4)
                {
                    // TODO: Implement this method
                    Intent intent = new Intent();
                    intent.setAction(Constants.SEARCH);
                    intent.putExtra(Constants.SEARCH, list.get(p3).getTitle());
                    sendBroadcast(intent);

                    isPlay();
                }
            });
    }
    
    private void isPlay()
    {
        Intent intent = new Intent();
        intent.setAction(Constants.ISPLAY);
        intent.putExtra(Constants.ISPLAY, true);
        sendBroadcast(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // TODO: Implement this method
        switch (item.getItemId())
        {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}
