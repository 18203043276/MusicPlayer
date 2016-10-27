package com.cj.music_player.activity;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBar;
import com.pgyersdk.crash.PgyCrashManager;

import com.cj.music_player.R;
import com.cj.music_player.info.AlbumInfo;
import com.cj.music_player.info.MusicInfo;
import com.cj.music_player.db.DatabaseHelper;
import com.cj.music_player.db.MusicInfoDB;
import com.cj.music_player.db.SettingSharedUtils;
import com.cj.music_player.adapter.AlbumAdapter;
import com.cj.music_player.adapter.QueryAdapter;
import com.cj.music_player.util.SortUtils;
import com.cj.music_player.Constants;
import com.cj.music_player.view.MusicListView;
import com.cj.music_player.list.SongList;

import java.util.List;
import java.util.ArrayList;
import android.widget.ListView;
import android.os.Bundle;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView;
import android.view.View;
import android.content.Intent;
import android.database.Cursor;
import java.util.Collections;
import android.view.MenuItem;
import android.view.KeyEvent;

public class AlbumActivity extends AppCompatActivity
{
    private List<AlbumInfo> AlbumList = new ArrayList<AlbumInfo>();
    private List<MusicInfo> MusicList = new ArrayList<MusicInfo>();
    private MusicListView listView;
    private AlbumAdapter AlbumAdapter;
    private QueryAdapter QueryAdapter;
    private int back = 0;
    private SortUtils sort = new SortUtils();
    private SongList songList = new SongList();
    private int n = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        // TODO: Implement this method
        super.onCreate(savedInstanceState);
        setContentView(R.layout.album);

        PgyCrashManager.register(AlbumActivity.this);

        ActionBar bar = getSupportActionBar();
        bar.setDisplayHomeAsUpEnabled(true);

        AlbumList = songList.getAlbumList(AlbumActivity.this);

        listView = (MusicListView) findViewById(R.id.album_ListView);
        AlbumAdapter = new AlbumAdapter(AlbumActivity.this, AlbumList);
        listView.setAdapter(AlbumAdapter);
        listView.setOnItemClickListener(new OnItemClickListener(){

                @Override
                public void onItemClick(AdapterView<?> p1, View p2, int p3, long p4)
                {
                    // TODO: Implement this method
                    if (back == 0)
                    {
                        MusicInfoDB db = new MusicInfoDB(AlbumActivity.this);
                        Cursor cursor = db.query(DatabaseHelper.ALBUM_KEY_ID, AlbumList.get(p3).getAlbumKeyId(), 
                                                 new String[] { DatabaseHelper.TITLE, DatabaseHelper.ARTIST, 
                                                     DatabaseHelper.TIME, DatabaseHelper.ALBUM_IMAGE_PATH});
                        while (cursor.moveToNext())
                        {  
                            //判断下一个下标是否有内容
                            MusicInfo info = new MusicInfo();

                            String title = cursor.getString(cursor.getColumnIndex(DatabaseHelper.TITLE)); 
                            String artist = cursor.getString(cursor.getColumnIndex(DatabaseHelper.ARTIST)); 
                            String time = cursor.getString(cursor.getColumnIndex(DatabaseHelper.TIME));
                            String album_image_path = cursor.getString(cursor.getColumnIndex(DatabaseHelper.ALBUM_IMAGE_PATH));

                            info.setTitle(title);
                            info.setArtist(artist);
                            info.setTime(time);
                            info.setAlbumImagePath(album_image_path);

                            MusicList.add(info);
                            Collections.sort(MusicList, sort.new SortMusicList());
                            if (SettingSharedUtils.getBoolean(AlbumActivity.this, "album_list_sort", true) == false)
                            {
                                Collections.reverse(MusicList);
                            }
                        }
                        QueryAdapter = new QueryAdapter(AlbumActivity.this, MusicList);
                        listView.setAdapter(QueryAdapter);
                        back = 1;
                        n = p3;
                    }
                    else
                    {
                        Intent intent = new Intent();
                        intent.setAction(Constants.SEARCH);
                        intent.putExtra(Constants.SEARCH, MusicList.get(p3).getTitle());
                        sendBroadcast(intent);

                        isPlay();
                    }
                }
            });
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // TODO: Implement this method
        switch (item.getItemId())
        {
            case android.R.id.home:
                if (back == 0)
                {
                    finish();
                }
                else
                {
                    MusicList.removeAll(MusicList);
                    listView.setAdapter(AlbumAdapter);
                    listView.setSelection(n);
                    back = 0;
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void isPlay()
    {
        Intent intent = new Intent();
        intent.setAction(Constants.ISPLAY);
        intent.putExtra(Constants.ISPLAY, true);
        sendBroadcast(intent);
    }
    //按键
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        //返回键
        if (keyCode == KeyEvent.KEYCODE_BACK)
        {
            if (back == 0)
            {
                finish();
            }
            else
            {
                MusicList.removeAll(MusicList);
                listView.setAdapter(AlbumAdapter);
                listView.setSelection(n);
                back = 0;
            }
            return false;
        }
        else
        {
            return super.onKeyDown(keyCode, event);
        }
    }

}
