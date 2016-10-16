package com.cj.music_player.activity;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;

import com.cj.music_player.R;
import com.cj.music_player.MusicApplication;
import com.cj.music_player.view.MusicListView;
import com.cj.music_player.adapter.MusicAdapter;
import com.cj.music_player.info.MusicInfo;
import com.cj.music_player.db.MusicInfoDB;
import com.cj.music_player.db.SharedUtils;
import com.cj.music_player.db.StarInfoDB;
import com.cj.music_player.db.DatabaseHelper;
import com.cj.music_player.util.MusicUtils;
import com.cj.music_player.service.MusicService;
import com.cj.music_player.tools.MusicTools;
import com.cj.music_player.tools.AlbumBitmap;
import com.cj.music_player.tools.BitmapTools;
import com.cj.music_player.Constants;

import java.util.List;
import android.widget.TextView;
import android.widget.SeekBar;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.os.Bundle;
import android.os.Build;
import android.view.WindowManager;
import java.util.ArrayList;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.view.View.OnClickListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView;
import android.media.AudioManager;
import android.content.Context;
import android.content.BroadcastReceiver;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;
import android.content.DialogInterface;
import android.app.ProgressDialog;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.EditText;
import android.os.CountDownTimer;
import android.text.InputType;
import android.os.AsyncTask;
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.database.sqlite.SQLiteDatabase;
import android.database.Cursor;
import android.view.MotionEvent;
import android.widget.AbsListView;
import java.io.File;
import android.os.Environment;

public class MusicActivity extends AppCompatActivity
{
    private MusicListView music_list;
    private MusicAdapter adapter;
	private List<MusicInfo> list;
    private MusicInfoDB db = new MusicInfoDB(this);
    private MusicUtils scan = new MusicUtils();
    private MusicTools tools = new MusicTools();

    //得到音频管理对象
    private AudioManager audioManager;
    private CardView volume_layout;
    private SeekBar volume_seekbar;
    private TextView volume_number;
    private ImageView volume_image;
    private int nowVolume;

    private Handler handler;

    //定时
    private TextView timer;
    private MusicCountDown musicTimer;
    private int timer_check = 0;
    private String timer_text = "1";

    //音乐控制
    private SeekBar play_sb;
    private ImageButton play, before, after, mode;
    private RatingBar ratingBar;

    private Boolean isplay = false;

    private int check = 0;

    private TextView now_time, max_time;

    private MusicBroad musicBd = new MusicBroad();

	private TextView show_title, show_singer, show_number;

    private TextView album_image_tips;

    private ImageView music_image, background;

    private int num = 0;

    private boolean isSign = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
        {
            //透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //透明导航栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        MusicApplication.getInstance().addActivity(this);

        //标题栏
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.actionbar_home);

        //左侧滑
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);

        audioManager = (AudioManager) MusicActivity.this.getSystemService(Context.AUDIO_SERVICE);
        //获取当前音乐音量
        nowVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        Volume();//音量

        MusicControl();//音乐控制

        Control();//控件

        Star();//星级

        Mode();//播放模式

        list = new ArrayList<MusicInfo>();
        list = db.getMusicInfo();
        adapter = new MusicAdapter(MusicActivity.this, list);
		music_list.setAdapter(adapter);

        //启动服务器
        Intent intent=new Intent();
        intent.setClass(MusicActivity.this, MusicService.class);
        startService(intent);

        IntentFilter mFilter = new IntentFilter();
        mFilter.addAction(Constants.MAXTIME);
        mFilter.addAction(Constants.NOWTIME);
        mFilter.addAction(Constants.NOWINDEX);
        mFilter.addAction(Constants.ISPLAY);
        mFilter.addAction(Constants.PATH);
        registerReceiver(musicBd, mFilter);

        handler = new Handler();

        //判断音乐库是否有数据，如果没有就扫描。
        if (list.size() < 1)
        {
            new Handler().postDelayed(new Runnable(){

                    @Override
                    public void run()
                    {
                        //要执行的代码
                        scan_music();
                    }
                }, 3000);
        }
        else
        {
            //判断音乐库专辑图片路径是否正确,不正确就重新扫描
            Bitmap bm = BitmapFactory.decodeFile(list.get(num).getAlbumArt());
            if (bm == null)
            {
                new Handler().postDelayed(new Runnable(){

                        @Override
                        public void run()
                        {
                            //要执行的代码
                            scan_music();
                        }
                    }, 3000);
            }
            else
            {
                update();//刷新
            }
        }
        
    }
    
    //星级
    private void Star()
    {
        // TODO: Implement this method
        ratingBar = (RatingBar) findViewById(R.id.rating_bar);
        ratingBar.setOnRatingBarChangeListener(new OnRatingBarChangeListener(){

                @Override
                public void onRatingChanged(RatingBar p1, float p2, boolean p3)
                {
                    // TODO: Implement this method
                    int t = (int) p2;
                    String star = String.valueOf(t);

                    String id = String.valueOf(list.get(num).getId());
                    String title = list.get(num).getTitle();
                    String artist = list.get(num).getArtist();
                    String album = list.get(num).getAlbum();
                    String album_art = list.get(num).getAlbumArt();
                    String album_image_path = list.get(num).getAlbumImagePath();

                    if (StarInfoDB.queryExist(title) == true)
                    {
                        if (t == 0)
                        {
                            StarInfoDB.delete(title);
                        }
                        else
                        {
                            StarInfoDB.update(title, star);
                        }
                    }
                    else if (t > 0)
                    {
                        StarInfoDB.saveStarInfo(id, star, title, artist, album, album_art, album_image_path);
                    }
                }
            });

    }

    //控件
    private void Control()
    {
        //定时显示
        timer = (TextView) findViewById(R.id.main_Timer);
        timer.setVisibility(View.INVISIBLE);

        //右侧滑菜单
        Button Synchronization = (Button) findViewById(R.id.music_list_Synchronization);//音乐列表同步
        Synchronization.setOnClickListener(new OnClickListener(){

                @Override
                public void onClick(View p1)
                {
                    // TODO: Implement this method
                    music_list.setSelection(num);
                }
            });

        //左侧滑菜单
        Button about = (Button) findViewById(R.id.about);
        about.setOnClickListener(new OnClickListener(){

                @Override
                public void onClick(View p1)
                {
                    // TODO: Implement this method
                    about();
                }
            });
        Button setting = (Button) findViewById(R.id.setting);
        setting.setOnClickListener(new OnClickListener(){

                @Override
                public void onClick(View p1)
                {
                    // TODO: Implement this method
                    Intent intent = new Intent(MusicActivity.this, SettingActivity.class);
                    startActivity(intent);
                }
            });
        Button folder = (Button) findViewById(R.id.folder);
        folder.setOnClickListener(new OnClickListener(){

                @Override
                public void onClick(View p1)
                {
                    // TODO: Implement this method
                    Intent intent = new Intent(MusicActivity.this, FolderActivity.class);
                    startActivity(intent);
                }
            });
        Button album = (Button) findViewById(R.id.album);
        album.setOnClickListener(new OnClickListener(){

                @Override
                public void onClick(View p1)
                {
                    // TODO: Implement this method
                    Intent intent = new Intent(MusicActivity.this, AlbumActivity.class);
                    startActivity(intent);
                }
            });
        Button artist = (Button) findViewById(R.id.artist);
        artist.setOnClickListener(new OnClickListener(){

                @Override
                public void onClick(View p1)
                {
                    // TODO: Implement this method
                    Intent intent = new Intent(MusicActivity.this, ArtistActivity.class);
                    startActivity(intent);
                }
            });
        Button star = (Button) findViewById(R.id.star);
        star.setOnClickListener(new OnClickListener(){

                @Override
                public void onClick(View p1)
                {
                    // TODO: Implement this method
                    Intent intent = new Intent(MusicActivity.this, StarActivity.class);
                    startActivity(intent);
                }
            });
    }

    //播放模式
    private void Mode()
    {
        // TODO: Implement this method
        int mod = SharedUtils.getInt(MusicActivity.this, "mode", 0);
        if (mod == 0)
        {
            mode.setImageResource(R.drawable.music_mode_none);
        }
        if (mod == 1)
        {
            mode.setImageResource(R.drawable.music_mode_cycle);
        }
        if (mod == 2)
        {
            mode.setImageResource(R.drawable.music_mode_singles);
        }
        if (mod == 3)
        {
            mode.setImageResource(R.drawable.music_mode_random);
        }
    }

    //音乐控制
    private void MusicControl()
    {
        // TODO: Implement this method
        music_list = (MusicListView) findViewById(R.id.music_list);
        music_list.setOnItemClickListener(new OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
                {
                    // TODO Auto-generated method stub
                    isplay = true;
                    play.setImageResource(R.drawable.music_pause);
                    adapter.setNum(arg2);
                    adapter.notifyDataSetChanged();

                    Intent intent=new Intent();
                    intent.setAction(Constants.LIST_INDEX);
                    intent.putExtra(Constants.LIST_INDEX, arg2);
                    sendBroadcast(intent);
                }
            });
        play_sb = (SeekBar) findViewById(R.id.play_seekbar);       
        play_sb.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

                @Override
                public void onStopTrackingTouch(SeekBar seekBar)
                {
                    // TODO Auto-generated method stub
                }
                @Override
                public void onStartTrackingTouch(SeekBar seekBar)
                {
                    // TODO Auto-generated method stub
                }
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
                {
                    // TODO Auto-generated method stub
                    if (fromUser == true)
                    {
                        Intent intent=new Intent();
                        intent.setAction(Constants.SEEKBAR);
                        intent.putExtra(Constants.SEEKBAR, progress);
                        sendBroadcast(intent);
                    }
                }
            });

        now_time = (TextView) findViewById(R.id.now_time);
        max_time = (TextView) findViewById(R.id.max_time);

        before = (ImageButton) findViewById(R.id.before);
        after = (ImageButton) findViewById(R.id.after);
        play = (ImageButton) findViewById(R.id.play);
        mode = (ImageButton) findViewById(R.id.mode);

        show_title = (TextView) findViewById(R.id.show_title);
        show_singer = (TextView) findViewById(R.id.show_singer);
        show_number = (TextView) findViewById(R.id.show_number);

        music_image = (ImageView) findViewById(R.id.music_image);
        background = (ImageView) findViewById(R.id.music_image_background);

        album_image_tips = (TextView) findViewById(R.id.album_image_tips);

        play.setOnClickListener(new OnClickListener(){

                @Override
                public void onClick(View p1)
                {
                    // TODO: Implement this method
                    isplay = !isplay;
                    if (isplay == true)
                    {
                        play.setImageResource(R.drawable.music_pause);
                    }
                    else
                    {
                        play.setImageResource(R.drawable.music_play);
                    }
                    musicPlay(isplay);
                }
            });
        before.setOnClickListener(new OnClickListener(){

                @Override
                public void onClick(View p1)
                {
                    // TODO: Implement this method
                    musicSwitch(0);

                    play.setImageResource(R.drawable.music_pause);
                    isplay = true;
                }
            });
        after.setOnClickListener(new OnClickListener(){

                @Override
                public void onClick(View p1)
                {
                    // TODO: Implement this method
                    musicSwitch(1);

                    play.setImageResource(R.drawable.music_pause);
                    isplay = true;
                }
            });
        mode.setOnClickListener(new OnClickListener(){

                @Override
                public void onClick(View p1)
                {
                    // TODO: Implement this method
                    check++;
                    if (check > 3)
                    {
                        check = 0;
                    }
                    musicMode(check);
                }
            });
        Button slide = (Button) findViewById(R.id.music_slide);
        slide.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent event)
                {
                    int actionType = event.getAction();
                    int downX = 0;
                    int downY;
                    int upX = 0;
                    int upY;
                    //事件类型
                    if (actionType == MotionEvent.ACTION_DOWN)
                    {
                        //按下:action_down  滑动:action_move   松开：action_up
                        downX = (int) event.getX();
                        //得到x坐标
                        downY = (int) event.getY();
                        //得到y坐标
                        //upX = (int) event.getX();
                        //upY = (int) event.getY();

                        /*  if (downX < upX)
                         {
                         musicSwitch(1);
                         play.setImageResource(R.drawable.music_pause);
                         isplay = true;
                         }*/
                        Toast.makeText(MusicActivity.this, "hhh" + downX, Toast.LENGTH_SHORT).show();
                    }
                    if (actionType == MotionEvent.ACTION_UP)
                    {
                        upX = (int) event.getX();
                        upY = (int) event.getY();
                        Toast.makeText(MusicActivity.this, "ggg" + upX, Toast.LENGTH_SHORT).show();

                        if (upX != downX)
                        {
                            if (downX > upX)
                            {
                                musicSwitch(1);
                                play.setImageResource(R.drawable.music_pause);
                                isplay = true;
                                Toast.makeText(MusicActivity.this, "1", Toast.LENGTH_SHORT).show();
                            }
                            if (downX < upX)
                            {
                                musicSwitch(0);
                                play.setImageResource(R.drawable.music_pause);
                                isplay = true;
                                Toast.makeText(MusicActivity.this, "0", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                    return false;
                }
            });

    }

    //音量
    private void Volume()
    {
        // TODO: Implement this method
        volume_layout = (CardView)findViewById(R.id.volume_layout);
        volume_layout.setVisibility(View.INVISIBLE);

        volume_seekbar = (SeekBar)findViewById(R.id.volume_seekbar);
        volume_number = (TextView)findViewById(R.id.volume_number);
        volume_image = (ImageView)findViewById(R.id.volume_image);

        volume_number.setText("" + nowVolume);
        if (nowVolume == 0)
        {
            volume_image.setBackgroundResource(R.drawable.music_volume_off);
        }
        else
        {
            volume_image.setBackgroundResource(R.drawable.music_volume_on);
        }
        //设置为seekbar音量的最大阶数
        volume_seekbar.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
        //设置seekbar为当前音量进度 
        volume_seekbar.setProgress(nowVolume);
        //seekbar进度改变的监听
        volume_seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onStopTrackingTouch(SeekBar seekBar)
                {
                    handler.postDelayed(ValumeRun, 3000);
                }
                @Override
                public void onStartTrackingTouch(SeekBar seekBar)
                {
                    handler.removeCallbacks(ValumeRun);
                }
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
                {
                    //拖动seekbar时改变音量
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0); 
                    nowVolume = progress;
                    volume_number.setText("" + progress);
                    if (progress == 0)
                    {
                        volume_image.setBackgroundResource(R.drawable.music_volume_off);
                    }
                    else
                    {
                        volume_image.setBackgroundResource(R.drawable.music_volume_on);
                    }
                }
            });
    }

    //发送，播放，暂停广播
    private void musicPlay(boolean isplay)
    {
        Intent intent=new Intent();
        intent.setAction(Constants.PLAY);
        intent.putExtra(Constants.PLAY, isplay);
        sendBroadcast(intent);
    }

    //上一曲，下一曲，0上一曲，1下一曲
    private void musicSwitch(int num)
    {
        Intent intent=new Intent();
        intent.setAction(Constants.SWITCH);
        intent.putExtra(Constants.SWITCH, num);
        sendBroadcast(intent);
    }

    //音乐模式
    private void musicMode(int checks)
    {
        if (checks == 0)
        {
            mode.setImageResource(R.drawable.music_mode_none);
        }
        if (checks == 1)
        {
            mode.setImageResource(R.drawable.music_mode_cycle);
        }
        if (checks == 2)
        {
            mode.setImageResource(R.drawable.music_mode_singles);
        }
        if (checks == 3)
        {
            mode.setImageResource(R.drawable.music_mode_random);
        }

        Intent intent=new Intent();
        intent.setAction(Constants.MODE);
        intent.putExtra(Constants.MODE, checks);
        sendBroadcast(intent);
	}

    //接收广播
    private class MusicBroad extends BroadcastReceiver
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            // TODO Auto-generated method stub
            if (intent.getAction().equals(Constants.MAXTIME))
            {
                int maxtime=intent.getIntExtra(Constants.MAXTIME, 0);
                play_sb.setMax(maxtime);
                max_time.setText(tools.TimeConversion(maxtime));
            }
            if (intent.getAction().equals(Constants.NOWTIME))
            {
                int nowtime=intent.getIntExtra(Constants.NOWTIME, 0);
                play_sb.setProgress(nowtime);
                now_time.setText(tools.TimeConversion(nowtime));
            }
            if (intent.getAction().equals(Constants.NOWINDEX))
            {
                num = intent.getIntExtra(Constants.NOWINDEX, 0);

                adapter.setNum(num);
                adapter.notifyDataSetChanged();

                DatabaseHelper helper = new DatabaseHelper(getApplicationContext());
                SQLiteDatabase db = helper.getReadableDatabase();

                Cursor cursor = db.query(DatabaseHelper.TABLE_STAR, new String[]{ DatabaseHelper.STAR_NUMBER }, DatabaseHelper.STAR_TITLE + " like ?", new String[] { "%" + list.get(num).getTitle() + "%"}, null, null, null);
                if (cursor.getCount() == 0)
                {
                    ratingBar.setRating(0);
                }
                while (cursor.moveToNext())
                {  
                    String star = cursor.getString(cursor.getColumnIndex(DatabaseHelper.STAR_NUMBER));
                    ratingBar.setRating(Integer.valueOf(star));
                }

                Bitmap bitmap = null;
                bitmap = AlbumBitmap.AlbumImage(list.get(num).getPath());
                if (bitmap == null)
                {
                    bitmap = AlbumBitmap.getAlbumImage(MusicActivity.this, num, Integer.valueOf(list.get(num).getAlbumId()));
                    if (bitmap == null)
                    {
                        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.main_menu_left_image);
                        album_image_tips.setText("没有专辑图片");
                    }
                    else
                    {
                        album_image_tips.setText("");
                    }
                }
                else
                {
                    album_image_tips.setText("");
                }
                music_image.setImageBitmap(bitmap);

                //异步淡化专辑图片
                FadeBitmapAsyncTask at = new FadeBitmapAsyncTask();
                at.execute();

                show_singer.setText(list.get(num).getArtist());
                show_title.setText(list.get(num).getTitle());
                show_number.setText(num + 1 + "/" + list.size());
            }
            if (intent.getAction().equals(Constants.ISPLAY))
            {
                Boolean is=intent.getBooleanExtra(Constants.ISPLAY, false);
                if (is == false)
                {
                    isplay = false;
                    play.setImageResource(R.drawable.music_play);
                }
                else
                {
                    isplay = true;
                    play.setImageResource(R.drawable.music_pause);
                }
            }
            if (intent.getAction().equals(Constants.PATH))
            {
                String path = intent.getStringExtra("path");
                String name = intent.getStringExtra("name");

                Bitmap bitmap = null;
                bitmap = AlbumBitmap.AlbumImage(path);
                if (bitmap == null)
                {
                    bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.main_menu_left_image);
                    album_image_tips.setText("没有专辑图片");
                }
                else
                {
                    music_image.setImageBitmap(bitmap);
                    album_image_tips.setText("");
                }
                background.setImageBitmap(BitmapTools.Blurbitmap(MusicActivity.this, bitmap, 10, 10, 0.5f));

                show_title.setText(name);
                show_singer.setText("");
                show_number.setText("");
            }
        }
    }

    //异步保存专辑图片
    private class SaveBitmapAsyncTask extends AsyncTask<Void, Void, Void>
    {
        @Override
        protected Void doInBackground(Void[] p1)
        {
            // TODO: Implement this method
            for (int n = 0; n < list.size(); n ++)
            {
                Bitmap bitmap = null;
                bitmap = AlbumBitmap.AlbumImage(list.get(n).getPath());
                if (bitmap == null)
                {
                    bitmap = AlbumBitmap.getAlbumImage(MusicActivity.this, n, Integer.valueOf(list.get(n).getAlbumId()));
                    if (bitmap == null)
                    {
                        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.main_menu_left_image);
                    }
                }
                if (bitmap != null)
                {
                    String name = list.get(n).getName() + "-image";
                    BitmapTools.saveBitmap(BitmapTools.ScaleBitmap(bitmap, 100, 100, false), MusicApplication.album_art_path, name, Bitmap.CompressFormat.PNG, 80);
                }
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void result)
        {
            // TODO: Implement this method
            SharedUtils.saveBoolean(MusicActivity.this, "SavaAlbumImage", true);
            super.onPostExecute(result);
        }
    }

    //异步淡化图片
    private class FadeBitmapAsyncTask extends AsyncTask<Void, Void, Void>
    {
        Bitmap bm = null;

        @Override
        protected Void doInBackground(Void[] p1)
        {
            // TODO: Implement this method
            bm = AlbumBitmap.AlbumImage(list.get(num).getPath());
            if (bm != null)
            {
                if (bm.getWidth() > 1200 && bm.getHeight() > 1200)
                {
                    bm = BitmapTools.ScaleBitmap(bm, 1200, 1200, true);
                }
            }
            if (bm == null)
            {
                bm = AlbumBitmap.getAlbumImage(MusicActivity.this, num, Integer.valueOf(list.get(num).getAlbumId()));
                if (bm == null)
                {
                    bm = BitmapFactory.decodeResource(getResources(), R.drawable.main_menu_left_image);
                }
            }
            bm = BitmapTools.Blurbitmap(MusicActivity.this, bm, 10, 10, 0.5f);
            return null;
        }
        @Override
        protected void onPostExecute(Void result)
        {
            // TODO: Implement this method
            background.setImageBitmap(bm);
            super.onPostExecute(result);
        }
    }

    //按键
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        //返回键
        if (keyCode == KeyEvent.KEYCODE_BACK)
        {
            moveTaskToBack(false);
            return true;
        }
        //音量下键
        if (keyCode == event.KEYCODE_VOLUME_DOWN)
        {
            if (nowVolume > 0)
            {
                nowVolume = nowVolume - 1;
            }
            Volume();
            volume_layout.setVisibility(View.VISIBLE);

            handler.removeCallbacks(ValumeRun);
            handler.postDelayed(ValumeRun, 3000);
            return true;
        }
        //音量上键
        if (keyCode == event.KEYCODE_VOLUME_UP)
        {
            if (nowVolume < audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC))
            {
                nowVolume = nowVolume + 1;
            }
            Volume();
            volume_layout.setVisibility(View.VISIBLE);

            handler.removeCallbacks(ValumeRun);
            handler.postDelayed(ValumeRun, 3000);
            return true;
        }
        return false;
    }

    Runnable ValumeRun = new Runnable() {

        @Override
        public void run()
        {
            // TODO: Implement this method
            volume_layout.setVisibility(View.INVISIBLE);
        }
    };

    //菜单
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // TODO: Implement this method
        MenuInflater inflater=new MenuInflater(this);
        inflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // TODO: Implement this method
        switch (item.getItemId())
        {
            case R.id.setting:
                Intent intent3 = new Intent(MusicActivity.this, SettingActivity.class);
                startActivity(intent3);
                break;
            case R.id.info:
                if (list.size() > 1)
                {
                    music_info();
                }
                else
                {
                    Toast.makeText(MusicActivity.this, "没有音乐文件", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.search:
                Intent intent=new Intent(MusicActivity.this, SearchActivity.class);
                startActivity(intent);
                break;
            case R.id.volume:
                if (volume_layout.isShown())
                {
                    volume_layout.setVisibility(View.INVISIBLE);
                }
                else
                {
                    volume_layout.setVisibility(View.VISIBLE);
                }
                new Handler().postDelayed(new Runnable(){

                        @Override
                        public void run()
                        {
                            //要执行的代码
                            volume_layout.setVisibility(View.INVISIBLE);
                        }
                    }, 5000);
                break;
            case R.id.album_image:
                Intent intent2 = new Intent(MusicActivity.this, AlbumImageActivity.class);
                intent2.putExtra("num", num);
                startActivity(intent2);
                break;
            case R.id.timer:
                timer();
                break;
            case R.id.scan_music:
                scan(); 
                break;
            case R.id.files:
                Intent intent4 = new Intent(MusicActivity.this, FilesActivity.class);
                startActivity(intent4);
                break;
            case R.id.about:
                about();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //扫描
    private void scan()
    {
        // TODO: Implement this method
        if (list.size() > 1)
        {
            android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(MusicActivity.this);
            builder.setIcon(R.drawable.ic_launcher);
            builder.setTitle("警告");
            builder.setMessage("数据库已有音乐数据，确定要重新扫描音乐吗？");
            builder.setNegativeButton("取消", null);
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener(){

                    @Override
                    public void onClick(DialogInterface p1, int p2)
                    {
                        // TODO: Implement this method
                        scan_music();
                    }
                });
            builder.show();
        }
        else
        {
            scan_music();
        }
    }

    //扫描音乐
    private void scan_music()
    {
        // TODO: Implement this method
        ScanAsyncTask s = new ScanAsyncTask();
        s.execute();
    }

    //异步扫描音乐
    private class ScanAsyncTask extends AsyncTask<Void, Void, Void>
    {
        ProgressDialog pd = new ProgressDialog(MusicActivity.this);
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
            db.deleteTables(MusicActivity.this);

            scan.queryMusic(getApplicationContext());
            scan.queryFolder(getApplicationContext());
            scan.queryAlbum(getApplicationContext());
            scan.queryArtist(getApplicationContext());

            return null;
        }
        @Override
        protected void onProgressUpdate(Void[] values)
        {
            // TODO: Implement this method
          
            super.onProgressUpdate(values);
        }
        @Override
        protected void onPostExecute(Void result)
        {
            // TODO: Implement this method
            pd.dismiss();
            list = db.getMusicInfo();
            adapter = new MusicAdapter(MusicActivity.this, list);
            music_list.setAdapter(adapter);
            //启动服务器
            Intent intent=new Intent();
            intent.setClass(MusicActivity.this, MusicService.class);
            startService(intent);

            update();//刷新

            if (list.size() > 1)
            {
                android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(MusicActivity.this);
                builder.setIcon(R.drawable.ic_launcher);
                builder.setTitle("提示");
                builder.setMessage("扫描完成");
                builder.setNegativeButton("确定", null);
                builder.show();

                SaveBitmapAsyncTask b = new SaveBitmapAsyncTask();
                b.execute();
            }
            else
            {
                android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(MusicActivity.this);
                builder.setIcon(R.drawable.ic_launcher);
                builder.setTitle("警告");
                builder.setMessage("没有音乐文件");
                builder.setNegativeButton("确定", null);
                builder.show();
            }
        }
    }

    //关于
    private void about()
    {
        LayoutInflater inflater = getLayoutInflater();
        View dialog = inflater.inflate(R.layout.about_dialog, (ViewGroup) findViewById(R.id.about_dialog_LinearLayout));
        final TextView text = (TextView) dialog.findViewById(R.id.about_dialog_TextView);
        
        isSign = SharedUtils.getBoolean(MusicActivity.this, "sign", false);
        String sign = "未注册";
        if (isSign == true)
        {
            sign = "已注册";
        }
        text.setText(sign);
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(MusicActivity.this);
        builder.setIcon(R.drawable.ic_launcher);
        builder.setTitle("关于");
        builder.setView(dialog);
        builder.setPositiveButton("确定", null);
        builder.setNegativeButton("注册", new DialogInterface.OnClickListener(){

                @Override
                public void onClick(DialogInterface p1, int p2)
                {
                    // TODO: Implement this method
                    sign();
                }
            });
        builder.setNeutralButton("发送邮件", new DialogInterface.OnClickListener(){

                @Override
                public void onClick(DialogInterface p1, int p2)
                {
                    // TODO: Implement this method
                    Intent intent=new Intent(Intent.ACTION_SENDTO);   
                    intent.setData(Uri.parse("mailto:1607686294@qq.com"));   
                    intent.putExtra(Intent.EXTRA_SUBJECT, "MusicPlayer");   
                    startActivity(intent);  
                }
            });
        builder.show();
    }

    //注册
    private void sign()
    {
        isSign = SharedUtils.getBoolean(MusicActivity.this, "sign", false);
        LayoutInflater inflater = getLayoutInflater();
        View dialog = inflater.inflate(R.layout.sign_dialog, (ViewGroup) findViewById(R.id.sign_dialog_LinearLayout));
        final EditText edittext = (EditText) dialog.findViewById(R.id.sign_dialog_EditText);

        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(MusicActivity.this);
        builder.setTitle("注册");
        builder.setIcon(R.drawable.ic_launcher);
        if (isSign == false)
        {
            builder.setMessage("请输入注册码");
            builder.setView(dialog);
            builder.setNegativeButton("取消", null);
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener(){

                    @Override
                    public void onClick(DialogInterface p1, int p2)
                    {
                        // TODO: Implement this method
                        String s = edittext.getText().toString().trim();
                        if (s.isEmpty())
                        {
                            Toast.makeText(MusicActivity.this, "请输入注册码" , Toast.LENGTH_SHORT).show();
                            sign();
                        }
                        else
                        {
                            int si = Integer.parseInt(s, 16);
                            String sign = String.valueOf(si);
                            if (sign.equals("1595415"))
                            {
                                Toast.makeText(MusicActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
                                SharedUtils.saveBoolean(MusicActivity.this, "sign", true);
                            }
                            else
                            {
                                Toast.makeText(MusicActivity.this, "请输入正确的注册码", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
        }
        else
        {
            String sign = "未注册";
            if (isSign == true)
            {
                sign = "已注册";
            }
            builder.setMessage("\n恭喜，你" + sign + "。");
            builder.setNegativeButton("取消注册", new DialogInterface.OnClickListener(){

                    @Override
                    public void onClick(DialogInterface p1, int p2)
                    {
                        // TODO: Implement this method
                        SharedUtils.saveBoolean(MusicActivity.this, "sign", false);
                        about();
                    }
                });
        }
        builder.show();
    }

    //定时
    private void timer()
    {
        LayoutInflater inflater = getLayoutInflater();
        View dialog = inflater.inflate(R.layout.timer_dialog, (ViewGroup) findViewById(R.id.timer_dialog_LinearLayout));
        final EditText edittext = (EditText) dialog.findViewById(R.id.timer_dialog_EditText);
        edittext.setText(timer_text);

        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(MusicActivity.this);
        builder.setIcon(R.drawable.ic_launcher);
        builder.setView(dialog);
        builder.setTitle("定时");
        builder.setMessage("请输入定时分钟数");
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener(){

                @Override
                public void onClick(DialogInterface p1, int p2)
                {
                    // TODO: Implement this method
                    musicTimer.cancel();
                    timer.setVisibility(View.INVISIBLE);
                    timer_text = "";
                    timer_check = 0;
                }
            });
        if (timer_check == 0)
        {
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener(){

                    @Override
                    public void onClick(DialogInterface p1, int p2)
                    {
                        // TODO: Implement this method
                        String etc = edittext.getText().toString();
                        if (etc.equals(""))
                        {
                            etc = "1";
                        }
                        timer_text = etc;
                        int et = Integer.valueOf(etc);
                        int time = tools.TimeMinuteConversion(et);
                        //初始化时间
                        musicTimer = new MusicCountDown(time, 1000);
                        musicTimer.start();
                        timer.setVisibility(View.VISIBLE);
                        timer_check = 1;

                        if (isplay == true)
                        {
                            Toast.makeText(MusicActivity.this, et + "分钟后停止播放", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            Toast.makeText(MusicActivity.this, et + "分钟后开始播放", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        }
        builder.show();
    }

    //定时类
    private class MusicCountDown extends CountDownTimer
    {
        public MusicCountDown(long millisInFuture, long countDownInterval)
        {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long p1)
        {
            // TODO: Implement this method
            String t = String.valueOf(p1);
            int i = Integer.valueOf(t);
            String time = tools.TimeConversion(i);
            timer.setText(time);
        }

        @Override
        public void onFinish()
        {
            // TODO: Implement this method
            if (isplay == true)
            {
                //停止播放
                isplay = false;                      
                musicPlay(isplay);
                play.setImageResource(R.drawable.music_play);
            }
            else
            {
                //开始播放
                isplay = true;                      
                musicPlay(isplay);
                play.setImageResource(R.drawable.music_pause);
            }
            timer_check = 0;
            timer.setVisibility(View.INVISIBLE);
        }
    }

    //刷新
    private void update()
    {
        Intent intent=new Intent();
        intent.setAction(Constants.UPDATE);
        sendBroadcast(intent);
    }

    //音乐信息
    private void music_info()
    {
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(MusicActivity.this);
        builder.setIcon(R.drawable.ic_launcher);
        builder.setTitle("音乐信息");
        builder.setMessage("歌曲: " + list.get(num).getTitle() + "\n"  
                           + "歌手: " + list.get(num).getArtist() + "\n" 
                           + "专辑: " + list.get(num).getAlbum() + "\n"
                           + "专辑歌手: " + list.get(num).getAlbumArtist() + "\n"
                           + "时间: " + list.get(num).getTime() + "\n" 
                           + "大小: " + list.get(num).getSize() + "\n" 
                           + "格式: " + tools.getFormat(list.get(num).getPath()) + "\n" 
                           + "声道: " + tools.getChannels(list.get(num).getPath()) + "\n"
                           + "采样率: " + tools.getHz(list.get(num).getPath()) + "\n" 
                           + "比特率: " + tools.getKbps(list.get(num).getPath()) + "\n" 
                           + "年代: " + tools.getYears(list.get(num).getPath()) + "\n"
                           + "风格: " + tools.getGener(list.get(num).getPath()) + "\n"
                           + "路径: " + list.get(num).getPath());
        builder.setNegativeButton("确定", null);

        if (list.size() > 1)
        {
            builder.show();
        }
    }

}
