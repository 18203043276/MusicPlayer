package com.cj.music_player.activity;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBar;

import com.cj.music_player.R;

import android.os.Bundle;
import android.view.MenuItem;
import android.support.v4.app.Fragment;
import com.cj.music_player.fragment.SettingFragment;

public class FragmentActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        // TODO: Implement this method
        super.onCreate(savedInstanceState);
        setTheme(R.style.SettingTheme);
        setContentView(R.layout.fragment);
        
        ActionBar bar = getSupportActionBar();
        bar.setDisplayHomeAsUpEnabled(true);
        
        Fragment fragment = new SettingFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
        setTitle("设置");
        
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // TODO: Implement this method
        switch(item.getItemId())
        {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
   
}
