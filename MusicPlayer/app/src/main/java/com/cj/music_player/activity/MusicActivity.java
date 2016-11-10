package com.cj.music_player.activity;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v4.widget.DrawerLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.CoordinatorLayout;

import com.pgyersdk.crash.PgyCrashManager;
import com.pgyersdk.feedback.PgyFeedback;
import com.pgyersdk.views.PgyerDialog;
import com.pgyersdk.activity.FeedbackActivity;
import com.pgyersdk.update.PgyUpdateManager;
import com.pgyersdk.update.UpdateManagerListener;
import com.pgyersdk.javabean.AppBean;

import com.cj.music_player.R;
import com.cj.music_player.MusicApplication;
import com.cj.music_player.Constants;
import com.cj.music_player.view.MusicListView;
import com.cj.music_player.adapter.MusicAdapter;
import com.cj.music_player.info.MusicInfo;
import com.cj.music_player.db.MusicInfoDB;
import com.cj.music_player.db.SharedUtils;
import com.cj.music_player.db.StarInfoDB;
import com.cj.music_player.db.DatabaseHelper;
import com.cj.music_player.db.SettingSharedUtils;
import com.cj.music_player.util.MusicUtils;
import com.cj.music_player.service.MusicService;
import com.cj.music_player.service.AlbumImageCacheService;
import com.cj.music_player.tools.MusicTools;
import com.cj.music_player.tools.AlbumBitmap;
import com.cj.music_player.tools.BitmapTools;
import com.cj.music_player.list.SongList;

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
import java.util.Collections;
import android.widget.Toast;
import android.widget.LinearLayout;

public class MusicActivity extends AppCompatActivity implements View.OnClickListener
{
    private MusicListView music_list;
    private MusicAdapter adapter;
	private List<MusicInfo> list = new ArrayList<MusicInfo>();
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
    private TextView timer_layout;
    private MusicTimer musicTimer;
    private boolean isTimer = false;

    //音乐控制
    private SeekBar play_sb;
    private ImageButton play, before, after, mode;
    private RatingBar ratingBar;

    private Boolean isPlay = false;

    private int check = 0;

    private TextView now_time, max_time;

    private MusicBroad musicBd = new MusicBroad();

	private TextView show_title, show_singer, show_number;

    private TextView album_image_tips;

    private ImageView music_image, background;
    private CardView album_image_card;

    private int num = 0;

    private CoordinatorLayout coordinator_layout;

    private DrawerLayout drawer; //侧滑菜单
    private LinearLayout drawer_layout_left, drawer_layout_right;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        //透明状态栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        //透明导航栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        PgyCrashManager.register(MusicActivity.this);
        MusicApplication.getInstance().addActivity(MusicActivity.this);

        Control();//控件

        MusicControl();//音乐控制

        Star();//星级

        Mode();//播放模式

        list = SongList.getMusicList(MusicActivity.this);
        adapter = new MusicAdapter(MusicActivity.this, list);
        music_list.setAdapter(adapter);
        num = SharedUtils.getInt(MusicActivity.this, "num", 0);
        music_list.setSelection(num);

        //启动服务器
        Intent intent=new Intent();
        intent.setClass(MusicActivity.this, MusicService.class);
        startService(intent);

        //注册广播
        IntentFilter mFilter = new IntentFilter();
        mFilter.addAction(Constants.MAXTIME);
        mFilter.addAction(Constants.NOWTIME);
        mFilter.addAction(Constants.NOWINDEX);
        mFilter.addAction(Constants.ISPLAY);
        mFilter.addAction(Constants.SAVE_ALBUM_IMAGE_CACHE);
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
            Bitmap bm = BitmapFactory.decodeFile(list.get(num).getAlbumImagePath());
            if (bm == null)
            {
                new Handler().postDelayed(new Runnable(){

                        @Override
                        public void run()
                        {
                            //要执行的代码
                            startService(new Intent(MusicActivity.this, AlbumImageCacheService.class));
                        }
                    }, 3000);
            }
            else
            {
                update();//刷新
            }
        }

        //检查应用更新
        CheckUpdate();

        audioManager = (AudioManager) MusicActivity.this.getSystemService(Context.AUDIO_SERVICE);
        //获取当前音乐音量
        nowVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        Volume();//音量
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
                    String path = list.get(num).getPath();

                    if (StarInfoDB.queryExist(path) == true)
                    {
                        if (t == 0)
                        {
                            StarInfoDB.delete(path);
                        }
                        else
                        {
                            StarInfoDB.update(path, star);
                        }
                    }
                    else if (t > 0)
                    {
                        StarInfoDB.saveStarInfo(id, star, title, artist, album, album_art, album_image_path, path);
                    }
                }
            });

    }

    //控件
    private void Control()
    {
        //标题栏
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.actionbar_home);
        //侧滑菜单
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        drawer_layout_left = (LinearLayout) findViewById(R.id.drawer_layout_left);
        drawer_layout_right = (LinearLayout) findViewById(R.id.drawer_layout_right);

        coordinator_layout = (CoordinatorLayout) findViewById(R.id.coordinator_layout);
        //定时显示
        timer_layout = (TextView) findViewById(R.id.main_Timer);
        timer_layout.setVisibility(View.INVISIBLE);
        //右侧滑菜单
        Button Synchronization = (Button) findViewById(R.id.music_list_Synchronization);//音乐列表同步
        Synchronization.setOnClickListener(this);
        //左侧滑菜单
        Button about = (Button) findViewById(R.id.about);
        about.setOnClickListener(this);
        Button setting = (Button) findViewById(R.id.setting);
        setting.setOnClickListener(this);
        Button folder = (Button) findViewById(R.id.folder);
        folder.setOnClickListener(this);
        Button album = (Button) findViewById(R.id.album);
        album.setOnClickListener(this);
        Button artist = (Button) findViewById(R.id.artist);
        artist.setOnClickListener(this);
        Button star = (Button) findViewById(R.id.star);
        star.setOnClickListener(this);
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
                    isPlay = true;
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

        show_title = (TextView) findViewById(R.id.show_title);
        show_singer = (TextView) findViewById(R.id.show_singer);
        show_number = (TextView) findViewById(R.id.show_number);

        album_image_tips = (TextView) findViewById(R.id.album_image_tips);

        music_image = (ImageView) findViewById(R.id.music_image);
        background = (ImageView) findViewById(R.id.music_image_background);
        album_image_card = (CardView) findViewById(R.id.main_main_image_CardView);

        before = (ImageButton) findViewById(R.id.before);
        before.setOnClickListener(this);
        after = (ImageButton) findViewById(R.id.after);
        after.setOnClickListener(this);
        play = (ImageButton) findViewById(R.id.play);
        play.setOnClickListener(this);
        mode = (ImageButton) findViewById(R.id.mode);
        mode.setOnClickListener(this);
    }

    @Override
    public void onClick(View view)
    {
        // TODO: Implement this method
        switch (view.getId())
        {
                //右侧滑菜单
            case R.id.music_list_Synchronization:
                music_list.setSelection(num);
                break;
                //左侧滑菜单
            case R.id.folder:
                startActivity(new Intent(this, FolderActivity.class));
                break;
            case R.id.album:
                startActivity(new Intent(this, AlbumActivity.class));
                break;
            case R.id.artist:
                startActivity(new Intent(this, ArtistActivity.class));
                break;
            case R.id.star:
                startActivity(new Intent(this, StarActivity.class));
                break;
            case R.id.setting:
                startActivity(new Intent(this, FragmentActivity.class));
                break;
            case R.id.about:
                about();
                break;
                //音乐控制
            case R.id.play:
                isPlay = !isPlay;
                musicPlay(isPlay);
                break;
            case R.id.before:
                musicSwitch(0);
                play.setImageResource(R.drawable.music_pause);
                isPlay = true;
                break;
            case R.id.after:
                musicSwitch(1);
                play.setImageResource(R.drawable.music_pause);
                isPlay = true;
                break;
            case R.id.mode:
                check++;
                if (check > 3)
                {
                    check = 0;
                }
                musicMode(check);
                break;
        }
    };
    
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
    private void musicPlay(boolean isPlay)
    {
        Intent intent=new Intent();
        intent.setAction(Constants.PLAY);
        intent.putExtra(Constants.PLAY, isPlay);
        sendBroadcast(intent);
        if (isPlay == true)
        {
            play.setImageResource(R.drawable.music_pause);
        }
        else
        {
            play.setImageResource(R.drawable.music_play);
        }
    }

    //上一曲，下一曲，0上一曲，1下一曲
    private void musicSwitch(int n)
    {
        Intent intent=new Intent();
        intent.setAction(Constants.SWITCH);
        intent.putExtra(Constants.SWITCH, n);
        sendBroadcast(intent);
    }

    //音乐模式
    private void musicMode(int checks)
    {
        if (checks == 0)
        {
            mode.setImageResource(R.drawable.music_mode_none);
            Snackbar.make(coordinator_layout, "顺序播放", Snackbar.LENGTH_SHORT).show();
        }
        if (checks == 1)
        {
            mode.setImageResource(R.drawable.music_mode_cycle);
            Snackbar.make(coordinator_layout, "全部循环", Snackbar.LENGTH_SHORT).show();
        }
        if (checks == 2)
        {
            mode.setImageResource(R.drawable.music_mode_singles);
            Snackbar.make(coordinator_layout, "单曲循环", Snackbar.LENGTH_SHORT).show();
        }
        if (checks == 3)
        {
            mode.setImageResource(R.drawable.music_mode_random);
            Snackbar.make(coordinator_layout, "随机播放", Snackbar.LENGTH_SHORT).show();
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

                Bitmap bitmap = AlbumBitmap.AlbumImage(list.get(num).getPath());
                if (bitmap == null)
                {
                    bitmap = AlbumBitmap.getAlbumImage(MusicActivity.this, num, Integer.valueOf(list.get(num).getAlbumId()));
                }
                if (bitmap != null)
                {
                    album_image_tips.setText("");
                    if (SettingSharedUtils.getBoolean(MusicActivity.this, "album_image", true) == true)
                    {
                        album_image_card.setVisibility(View.VISIBLE);
                        music_image.setImageBitmap(bitmap);
                    }
                    else
                    {
                        album_image_card.setVisibility(View.INVISIBLE);
                    }
                }
                else
                {
                    album_image_card.setVisibility(View.INVISIBLE);
                    album_image_tips.setText("没有专辑图片");
                }

                //异步淡化专辑图片
                new BlurAlbumImage().execute();

                show_singer.setText(list.get(num).getArtist());
                show_title.setText(list.get(num).getTitle());
                show_number.setText(num + 1 + "/" + list.size());
            }
            if (intent.getAction().equals(Constants.ISPLAY))
            {
                Boolean is=intent.getBooleanExtra(Constants.ISPLAY, false);
                if (is == false)
                {
                    isPlay = false;
                    play.setImageResource(R.drawable.music_play);
                }
                else
                {
                    isPlay = true;
                    play.setImageResource(R.drawable.music_pause);
                }
            }
        }
    }

    //异步淡化图片
    private class BlurAlbumImage extends AsyncTask<Void, Void, Bitmap>
    {
        @Override
        protected Bitmap doInBackground(Void[] p1)
        {
            // TODO: Implement this method
            Bitmap bm = BitmapFactory.decodeFile(list.get(num).getAlbumImagePath());
            if (bm == null)
            {
                bm = AlbumBitmap.AlbumImage(list.get(num).getPath());
                if (bm == null)
                {
                    bm = AlbumBitmap.getAlbumImage(MusicActivity.this, num, Integer.valueOf(list.get(num).getAlbumId()));
                    if (bm == null)
                    {
                        bm = BitmapFactory.decodeResource(getResources(), R.drawable.main_menu_left_image);
                    }
                }
            }
            bm = BitmapTools.Blurbitmap(MusicActivity.this, bm, 10, 10, 0.3f);
            return bm;
        }
        @Override
        protected void onPostExecute(Bitmap result)
        {
            // TODO: Implement this method
            background.setImageBitmap(result);
            super.onPostExecute(result);
        }
    }

    //按键
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        //返回键
        if (keyCode == KeyEvent.KEYCODE_BACK)
        {
            if (drawer.isDrawerOpen(drawer_layout_left) == true)
            {
                drawer.closeDrawer(drawer_layout_left);
            }
            else if (drawer.isDrawerOpen(drawer_layout_right) == true)
            {
                drawer.closeDrawer(drawer_layout_right);
            }
            else
            {
                moveTaskToBack(false);
            }
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
                startActivity(new Intent(this, FragmentActivity.class).setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP));
                break;
            case R.id.info:
                if (list.size() > 1)
                {
                    music_info();
                }
                else
                {
                    Snackbar.make(coordinator_layout, "没有音乐文件", Snackbar.LENGTH_INDEFINITE).setAction("重新扫描", new View.OnClickListener(){

                            @Override
                            public void onClick(View p1)
                            {
                                // TODO: Implement this method
                                scan_music();
                            }
                        }).show();
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
            case R.id.scan:
                scan_music();
                break;
            case R.id.delete:
                Delete(num);
                break;
            case R.id.feedback:
                feedback();
                break;
            case R.id.about:
                about();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //扫描音乐
    private void scan_music()
    {
        // TODO: Implement this method
        if (list.size() > 1)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(MusicActivity.this);
            builder.setIcon(R.drawable.warn);
            builder.setTitle("警告");
            builder.setMessage("媒体库已有音乐数据，确定要重新扫描音乐吗？");
            builder.setNegativeButton("取消", null);
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener(){

                    @Override
                    public void onClick(DialogInterface p1, int p2)
                    {
                        // TODO: Implement this method
                        new ScanMusicAsyncTask().execute();
                    }
                });
            builder.show();
        }
        else
        {
            new ScanMusicAsyncTask().execute();
        }
    }

    //异步扫描音乐
    private class ScanMusicAsyncTask extends AsyncTask<Void, Void, Void>
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
            list.removeAll(list);
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
                AlertDialog.Builder builder = new AlertDialog.Builder(MusicActivity.this);
                builder.setIcon(R.drawable.sdcard);
                builder.setTitle("提示");
                builder.setMessage("扫描完成，一共扫描到" + list.size() + "首音乐文件");
                builder.setNegativeButton("确定", null);
                builder.show();

                if (SharedUtils.getBoolean(MusicActivity.this, "SavaAlbumImage", false) == false)
                {
                    startService(new Intent(MusicActivity.this, AlbumImageCacheService.class));
                }
            }
            else
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(MusicActivity.this);
                builder.setIcon(R.drawable.warn);
                builder.setTitle("警告");
                builder.setMessage("系统媒体库没有音乐文件，请尝试重新扫描系统媒体库");
                builder.setNegativeButton("确定", null);
                builder.show();
            }
        }
    }

    //关于
    private void about()
    {
        boolean isSign = SharedUtils.getBoolean(MusicActivity.this, "sign", false);
        String sign = "未注册";
        if (isSign == true)
        {
            sign = "已注册";
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(MusicActivity.this);
        builder.setIcon(R.mipmap.ic_launcher);
        builder.setTitle("关于");
        builder.setMessage("版本：V2016.11.06\n\n此软件由陈江编写\n\n" + sign);
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
        boolean isSign = SharedUtils.getBoolean(MusicActivity.this, "sign", false);
        LayoutInflater inflater = getLayoutInflater();
        View dialog = inflater.inflate(R.layout.sign_dialog, (ViewGroup) findViewById(R.id.sign_dialog_LinearLayout));
        final EditText edittext = (EditText) dialog.findViewById(R.id.sign_dialog_EditText);

        AlertDialog.Builder builder = new AlertDialog.Builder(MusicActivity.this);
        builder.setTitle("注册");
        builder.setIcon(R.mipmap.ic_launcher);
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
                                Snackbar.make(coordinator_layout, "注册成功", Snackbar.LENGTH_LONG).show();
                                SharedUtils.saveBoolean(MusicActivity.this, "sign", true);
                            }
                            else
                            {
                                Toast.makeText(MusicActivity.this, "请输入正确的注册码", Toast.LENGTH_SHORT).show();
                                sign();
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
        final String timer = SharedUtils.getString(MusicActivity.this, "timer", "1");

        LayoutInflater inflater = getLayoutInflater();
        View dialog = inflater.inflate(R.layout.timer_dialog, (ViewGroup) findViewById(R.id.timer_dialog_LinearLayout));
        final EditText edittext = (EditText) dialog.findViewById(R.id.timer_dialog_EditText);
        edittext.setText(timer);

        AlertDialog.Builder builder = new AlertDialog.Builder(MusicActivity.this);
        builder.setIcon(R.drawable.timer);
        builder.setView(dialog);
        builder.setTitle("定时");
        builder.setMessage("请输入定时分钟数");
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener(){

                @Override
                public void onClick(DialogInterface p1, int p2)
                {
                    // TODO: Implement this method
                    if (isTimer == true)
                    {
                        musicTimer.cancel();
                        timer_layout.setVisibility(View.INVISIBLE);
                        isTimer = false;
                    }
                }
            });
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
                    SharedUtils.saveString(MusicActivity.this, "timer", etc);
                    int et = Integer.valueOf(etc);
                    int time = tools.TimeMinuteConversion(et);
                    if (isTimer == true)
                    {
                        musicTimer.cancel();
                    }
                    //初始化时间
                    musicTimer = new MusicTimer(time, 1000);
                    musicTimer.start();
                    timer_layout.setVisibility(View.VISIBLE);
                    isTimer = true;

                    if (isPlay == true)
                    {
                        Snackbar.make(coordinator_layout, et + "分钟后停止播放", Snackbar.LENGTH_LONG).show();
                    }
                    else
                    {
                        Snackbar.make(coordinator_layout, et + "分钟后开始播放", Snackbar.LENGTH_LONG).show();
                    }
                }
            });
        builder.show();
    }

    //定时类
    private class MusicTimer extends CountDownTimer
    {
        public MusicTimer(long millisInFuture, long countDownInterval)
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
            timer_layout.setText(time);
        }

        @Override
        public void onFinish()
        {
            // TODO: Implement this method
            if (isPlay == true)
            {
                //停止播放
                isPlay = false;                      
                musicPlay(isPlay);
                play.setImageResource(R.drawable.music_play);
            }
            else
            {
                //开始播放
                isPlay = true;                      
                musicPlay(isPlay);
                play.setImageResource(R.drawable.music_pause);
            }
            isTimer = false;
            timer_layout.setVisibility(View.INVISIBLE);
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
        AlertDialog.Builder builder = new AlertDialog.Builder(MusicActivity.this);
        builder.setIcon(new BitmapTools().bitmapToDrawable(BitmapFactory.decodeFile(list.get(num).getAlbumImagePath())));
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
        builder.show();
    }

    //反馈
    private void feedback()
    {
        // TODO: Implement this method
        // 打开沉浸式,默认为false
        FeedbackActivity.setBarImmersive(true);
        PgyFeedback.getInstance().showActivity(MusicActivity.this);
        // 设置顶部导航栏和底部bar的颜色
        FeedbackActivity.setBarBackgroundColor("#438FFF");
        // 设置顶部按钮和底部按钮按下时的反馈色
        FeedbackActivity.setBarButtonPressedColor("#ff0000");
        // 设置颜色选择器的背景色
        FeedbackActivity.setColorPickerBackgroundColor("#00000000");
    }

    //检查应用更新
    private void CheckUpdate()
    {
        PgyUpdateManager.register(MusicActivity.this, new UpdateManagerListener() {

                @Override
                public void onUpdateAvailable(final String result)
                {
                    // 将新版本信息封装到AppBean中
                    final AppBean appBean = getAppBeanFromString(result);
                    AlertDialog.Builder builder = new AlertDialog.Builder(MusicActivity.this);
                    builder.setIcon(R.mipmap.ic_launcher);
                    builder.setTitle("更新");
                    builder.setMessage("版本：V" + appBean.getVersionName() + "\n\n" + "更新内容：" + appBean.getReleaseNote());
                    builder.setPositiveButton("更新", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                startDownloadTask(MusicActivity.this, appBean.getDownloadURL());
                            }
                        });
                    builder.setNegativeButton("取消", null);
                    builder.show();
                }
                @Override
                public void onNoUpdateAvailable()
                {
                }
            });
    }

    //删除
    private void Delete(final int num)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(MusicActivity.this);
        builder.setIcon(R.drawable.delete);
        builder.setTitle("删除");
        builder.setMessage("确定要删除这首音乐吗？");
        builder.setNegativeButton("取消", null);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener(){

                @Override
                public void onClick(DialogInterface p1, int p2)
                {
                    // TODO: Implement this method
                    db.delete(list.get(num).getPath());
                    adapter.notifyDataSetInvalidated();
                    musicPlay(false);
                    if (StarInfoDB.queryExist(list.get(num).getPath()) == true)
                    {
                        StarInfoDB.delete(list.get(num).getPath());
                    }

                    if (db.queryExist(list.get(num).getPath()) == true)
                    {
                        AlertDialog.Builder builder = new AlertDialog.Builder(MusicActivity.this);
                        builder.setIcon(R.drawable.warn);
                        builder.setTitle("警告");
                        builder.setMessage("删除失败");
                        builder.setNegativeButton("确定", null);
                        builder.show();
                    }
                    else
                    {
                        AlertDialog.Builder builder = new AlertDialog.Builder(MusicActivity.this);
                        builder.setIcon(R.mipmap.ic_launcher);
                        builder.setTitle("提示");
                        builder.setMessage("删除成功");
                        builder.setNegativeButton("确定", null);
                        builder.show();
                    }
                }
            });
        builder.show();
    }

}
