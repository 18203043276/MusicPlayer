package com.cj.music_player.activity;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBar;
import com.pgyersdk.crash.PgyCrashManager;

import com.cj.music_player.R;
import com.cj.music_player.info.ArtistInfo;
import com.cj.music_player.info.MusicInfo;
import com.cj.music_player.list.SongList;
import com.cj.music_player.db.DatabaseHelper;
import com.cj.music_player.db.SettingSharedUtils;
import com.cj.music_player.adapter.ArtistAdapter;
import com.cj.music_player.adapter.QueryAdapter;
import com.cj.music_player.util.SortUtils;
import com.cj.music_player.Constants;
import com.cj.music_player.view.MusicListView;

import java.util.List;
import java.util.ArrayList;
import android.widget.ListView;
import android.os.Bundle;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView;
import android.view.View;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.Cursor;
import java.util.Collections;
import android.view.MenuItem;
import android.view.KeyEvent;

public class ArtistActivity extends AppCompatActivity
{
    private List<ArtistInfo> ArtistList = new ArrayList<ArtistInfo>();
    private List<MusicInfo> MusicList = new ArrayList<MusicInfo>();
    private MusicListView listView;
    private ArtistAdapter ArtistAdapter;
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
        setContentView(R.layout.artist);
        
        PgyCrashManager.register(ArtistActivity.this);

        ActionBar bar = getSupportActionBar();
        bar.setDisplayHomeAsUpEnabled(true);

        ArtistList = songList.getArtistList(ArtistActivity.this);

        listView = (MusicListView) findViewById(R.id.artist_ListView);
        ArtistAdapter = new ArtistAdapter(ArtistActivity.this, ArtistList);
        listView.setAdapter(ArtistAdapter);

        listView.setOnItemClickListener(new OnItemClickListener(){

                @Override
                public void onItemClick(AdapterView<?> p1, View p2, int p3, long p4)
                {
                    // TODO: Implement this method
                    if (back == 0)
                    {
                        Search(ArtistList.get(p3).getArtistKeyId());
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
    //搜索
    private void Search(String query)
    {
        // TODO: Implement this method
        DatabaseHelper helper = new DatabaseHelper(getApplicationContext());
        SQLiteDatabase db = helper.getReadableDatabase();

        Cursor cursor = db.query(DatabaseHelper.TABLE_MUSIC, new String[] { DatabaseHelper.ID, DatabaseHelper.TITLE, DatabaseHelper.ARTIST,
                                     DatabaseHelper.TIME, DatabaseHelper.ART, DatabaseHelper.ALBUM_IMAGE_PATH }, DatabaseHelper.ARTIST_KEY_ID + " like ?", new String[] { "%" + query + "%"}, null, null, null);
        while (cursor.moveToNext())
        {  
            //判断下一个下标是否有内容
            MusicInfo info = new MusicInfo();

            String id = cursor.getString(cursor.getColumnIndex(DatabaseHelper.ID));
            String title = cursor.getString(cursor.getColumnIndex(DatabaseHelper.TITLE)); 
            String artist = cursor.getString(cursor.getColumnIndex(DatabaseHelper.ARTIST)); 
            String time = cursor.getString(cursor.getColumnIndex(DatabaseHelper.TIME));
            String album_art = cursor.getString(cursor.getColumnIndex(DatabaseHelper.ART));
            String album_image_path = cursor.getString(cursor.getColumnIndex(DatabaseHelper.ALBUM_IMAGE_PATH));

            info.setId(id);
            info.setTitle(title);
            info.setArtist(artist);
            info.setTime(time);
            info.setAlbumArt(album_art);
            info.setAlbumImagePath(album_image_path);

            MusicList.add(info);
            Collections.sort(MusicList, sort.new SortMusicList());
            if (SettingSharedUtils.getBoolean(ArtistActivity.this, "artist_list_sort", true) == false)
            {
                Collections.reverse(ArtistList);
            }
        }
        QueryAdapter = new QueryAdapter(ArtistActivity.this, MusicList);
        listView.setAdapter(QueryAdapter);
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
                    listView.setAdapter(ArtistAdapter);
                    back = 0;
                    listView.setSelection(n);
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
                listView.setAdapter(ArtistAdapter);
                back = 0;
                listView.setSelection(n);
            }
            return false;
        }
        else
        {
            return super.onKeyDown(keyCode, event);
        }
    }

}
