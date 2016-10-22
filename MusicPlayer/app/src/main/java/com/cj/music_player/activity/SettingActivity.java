package com.cj.music_player.activity;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import com.pgyersdk.crash.PgyCrashManager;

import com.cj.music_player.R;
import com.cj.music_player.Constants;
import com.cj.music_player.MusicApplication;
import com.cj.music_player.db.MusicInfoDB;
import com.cj.music_player.util.MusicUtils;
import com.cj.music_player.list.SongList;
import com.cj.music_player.service.SystemMediaService;
import com.cj.music_player.list.SongList;
import com.cj.music_player.info.MusicInfo;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.content.Context;
import android.preference.PreferenceScreen;
import android.preference.Preference;
import android.content.Intent;
import android.view.MenuItem;
import android.os.AsyncTask;
import android.app.ProgressDialog;
import java.io.File;
import android.content.DialogInterface;
import java.util.List;
import java.util.ArrayList;

public class SettingActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        // TODO: Implement this methoh
        super.onCreate(savedInstanceState);

        PgyCrashManager.register(SettingActivity.this);
        MusicApplication.getInstance().addActivity(SettingActivity.this);

		ActionBar bar = getSupportActionBar();
		bar.setDisplayHomeAsUpEnabled(true);

		getFragmentManager().beginTransaction().replace(android.R.id.content, new SettingFragment(SettingActivity.this)).commit();
    }

    public static class SettingFragment extends PreferenceFragment
    {
		private Context context;
		public SettingFragment(Context context)
		{
			this.context = context;
		}

        private MusicInfoDB db = new MusicInfoDB(context);
        private MusicUtils scan = new MusicUtils();
        private List<MusicInfo> list = new ArrayList<MusicInfo>();
       
        @Override
        public void onCreate(Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.setting);
        }

        @Override
        public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference)
        {
            // TODO: Implement this method
            if (preference.getKey().equals("music_list_sort"))
            {
				updateList();
            }
            if (preference.getKey().equals("album_image"))
            {
                update();
            }
            if (preference.getKey().equals("scan_media"))
            {
                list = SongList.getMusicList(context);
                if (list.size() > 1)
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setIcon(R.drawable.ic_launcher);
                    builder.setTitle("警告");
                    builder.setMessage("数据库已有音乐数据，确定要重新扫描音乐吗？");
                    builder.setNegativeButton("取消", null);
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener(){

                            @Override
                            public void onClick(DialogInterface p1, int p2)
                            {
                                // TODO: Implement this method
                                new ScanAsyncTask().execute();
                            }
                        });
                    builder.show();
                }
                else
                {
                    new ScanAsyncTask().execute();
                }
            }
            if (preference.getKey().equals("scan_system_media"))
            {
                SystemMediaService s = new SystemMediaService();
                File f = new File("/storage");
                s.scanSDCard(context, f);
                
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setIcon(R.drawable.ic_launcher);
                builder.setTitle("提示");
                builder.setMessage("请稍等几分钟后重新扫描，因为重新扫描系统媒体库需要一段时间");
                builder.setNegativeButton("确定", null);
                builder.show();
            }
            return super.onPreferenceTreeClick(preferenceScreen, preference);
        }

		private void updateList()
		{
			Intent intent = new Intent();
			intent.setAction(Constants.UPDATE_LIST);
			context.sendBroadcast(intent);
		}

		private void update()
		{
			Intent intent = new Intent();
			intent.setAction(Constants.UPDATE);
			context.sendBroadcast(intent);
		}

        //异步扫描音乐
        public class ScanAsyncTask extends AsyncTask<Void, Void, Void>
        {
            ProgressDialog pd = new ProgressDialog(context);
            @Override
            protected void onPreExecute()
            {
                // 设置mProgressDialog风格
                pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);//圆形
                // 设置mProgressDialog提示
                pd.setMessage("正在扫描...");
                // 设置mProgressDialog的进度条是否不明确
                pd.setIndeterminate(true);
                // 是否可以按回退键取消
                pd.setCancelable(false);
                pd.show();

                super.onPreExecute();

            }
            @Override
            protected Void doInBackground(Void[] p1)
            {
                // TODO: Implement this method
                db.deleteTables(context);

                scan.queryMusic(context);
                scan.queryFolder(context);
                scan.queryAlbum(context);
                scan.queryArtist(context);

                return null;
            }
            @Override
            protected void onPostExecute(Void result)
            {
                // TODO: Implement this method
                pd.dismiss();
                list = SongList.getMusicList(context);

                if (list.size() > 1)
                {
                    updateList();//刷新
                    
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setIcon(R.drawable.ic_launcher);
                    builder.setTitle("提示");
                    builder.setMessage("扫描完成，一共扫描到" + list.size() + "首音乐文件");
                    builder.setNegativeButton("确定", null);
                    builder.show();
                }
                else
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setIcon(R.drawable.ic_launcher);
                    builder.setTitle("警告");
                    builder.setMessage("系统媒体库没有音乐文件，请尝试重新扫描系统媒体库");
                    builder.setNegativeButton("确定", null);
                    builder.show();
                }
            }
        }

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
