package com.cj.music_player.fragment;

import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.Preference;
import android.support.v7.app.AlertDialog;

import com.cj.music_player.R;
import com.cj.music_player.Constants;
import com.cj.music_player.service.AlbumImageCacheService;

import android.os.Bundle;
import android.widget.Toast;
import android.content.Intent;

public class SettingFragment extends PreferenceFragmentCompat
{
    @Override
    public void onCreatePreferences(Bundle bundle, String s)
    {
        // TODO: Implement this method
        addPreferencesFromResource(R.xml.setting);
       
        //重新生成专辑图片缓存
        findPreference("re_album_image_cache").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener(){

                @Override
                public boolean onPreferenceClick(Preference p1)
                {
                    // TODO: Implement this method
                    getActivity().startService(new Intent(getActivity(), AlbumImageCacheService.class));
                    Toast.makeText(getActivity(), "请稍等，正在生成专辑图片缓存", Toast.LENGTH_LONG).show();
                    return false;
                }
            });
    }
}
