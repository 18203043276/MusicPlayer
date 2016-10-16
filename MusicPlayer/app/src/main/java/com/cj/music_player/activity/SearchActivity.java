package com.cj.music_player.activity;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.app.ActionBar;

import com.cj.music_player.R;
import com.cj.music_player.info.MusicInfo;
import com.cj.music_player.view.MusicListView;
import com.cj.music_player.adapter.SearchAdapter;
import com.cj.music_player.util.SortUtils;
import com.cj.music_player.db.MusicInfoDB;
import com.cj.music_player.db.DatabaseHelper;
import com.cj.music_player.Constants;

import java.util.List;
import android.os.Bundle;
import java.util.ArrayList;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView;
import android.view.View;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.Cursor;
import java.util.Collections;
import android.view.Menu;
import android.view.MenuInflater;
import android.app.SearchManager;
import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.MenuItem;

public class SearchActivity extends AppCompatActivity
{
    private List<MusicInfo> list;
    private MusicListView listview;
    private SearchAdapter adapter;
    private MusicInfoDB db;
    private SortUtils sort = new SortUtils();

    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search);

        ActionBar bar = getSupportActionBar();
        bar.setHomeAsUpIndicator(R.drawable.search);
        bar.setDisplayHomeAsUpEnabled(true);

        listview = (MusicListView) findViewById(R.id.search_list);
        list = new ArrayList<MusicInfo>();

        db = new MusicInfoDB(this);
        list = db.getMusicInfo();
        adapter = new SearchAdapter(SearchActivity.this, list);
        listview.setAdapter(adapter);

        listview.setOnItemClickListener(new OnItemClickListener(){

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

    //搜索
    private void search(String query)
    {
        // TODO: Implement this method
        DatabaseHelper helper = new DatabaseHelper(getApplicationContext());
        SQLiteDatabase db = helper.getReadableDatabase();

        Cursor cursor = db.query(DatabaseHelper.TABLE_MUSIC, new String[] { DatabaseHelper.ID, DatabaseHelper.TITLE, DatabaseHelper.ARTIST,
                                     DatabaseHelper.TIME }, DatabaseHelper.TITLE + " like ?", new String[] { "%" + query + "%"}, null, null, null);
        while (cursor.moveToNext())
        {  
            //判断下一个下标是否有内容
            MusicInfo info = new MusicInfo();

            String id = cursor.getString(cursor.getColumnIndex(DatabaseHelper.ID));
            String title = cursor.getString(cursor.getColumnIndex(DatabaseHelper.TITLE)); 
            String artist = cursor.getString(cursor.getColumnIndex(DatabaseHelper.ARTIST)); 
            String time = cursor.getString(cursor.getColumnIndex(DatabaseHelper.TIME));

            info.setId(id);
            info.setTitle(title);
            info.setArtist(artist);
            info.setTime(time);

            list.add(info);
            Collections.sort(list, sort.new SortMusicList());//排序
        }
        adapter = new SearchAdapter(SearchActivity.this, list);
        listview.setAdapter(adapter);
    }

    //清除列表
    private void cleanlist()
    {
        if (list.size() > 0)
        {
            list.removeAll(list);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // TODO: Implement this method
        MenuInflater inflater=new MenuInflater(this);
        inflater.inflate(R.menu.search_menu, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        //searchView.setSubmitButtonEnabled(true);//是否显示确认搜索按钮
        searchView.setIconifiedByDefault(true);//设置展开后图标的样式,这里只有两种,一种图标在搜索框外,一种在搜索框内
        searchView.setIconified(false);//设置是否展开
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener(){

                @Override
                public boolean onQueryTextSubmit(String p1)
                {
                    // TODO: Implement this method
                    cleanlist();
                    search(p1);
                    return false;
                }

                @Override
                public boolean onQueryTextChange(final String p1)
                {
                    // TODO: Implement this method
                    cleanlist();
                    search(p1);
                    return true;
                }
            });

        SearchView.SearchAutoComplete textView = ( SearchView.SearchAutoComplete) searchView.findViewById(R.id.search_src_text);
        textView = (SearchView.SearchAutoComplete) searchView.findViewById(R.id.search_src_text);
        textView.setTextColor(Color.WHITE); 
        textView.setHint("搜索...");
        textView.setHintTextColor(Color.parseColor("#50000000"));
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);

        return super.onCreateOptionsMenu(menu);
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

    private void isPlay()
    {
        Intent intent = new Intent();
        intent.setAction(Constants.ISPLAY);
        intent.putExtra(Constants.ISPLAY, true);
        sendBroadcast(intent);
    }

}
