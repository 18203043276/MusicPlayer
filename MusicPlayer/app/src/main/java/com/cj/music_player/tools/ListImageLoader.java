package com.cj.music_player.tools;

import android.graphics.Bitmap;
import java.util.concurrent.ExecutorService;
import java.util.LinkedList;
import android.os.Handler;
import java.util.concurrent.Semaphore;
import android.os.Looper;
import android.os.Message;
import java.util.concurrent.Executors;
import android.widget.ImageView;
import android.graphics.BitmapFactory;
import android.util.DisplayMetrics;
import java.lang.reflect.Field;
import android.view.ViewGroup.LayoutParams;
import android.graphics.BitmapFactory.Options;
import android.util.LruCache;

/**
 * 图片异步加载类
 */
public class ListImageLoader
{
    private static ListImageLoader mInstance;
    /**
     * 图片缓存的核心对象
     */
    private LruCache<String, Bitmap> mLruCache;
    /**
     * 线程池
     */
    private ExecutorService mThreadPool;
    private static final int DEAFULT_THREAD_COUNT = 1;
    /**
     * 队列调度方式
     */
    private Type mType = Type.LIFO;
    /**
     * 任务队列
     */
    private LinkedList<Runnable> mTaskQueue;
    /**
     * 后台轮询线程
     */
    private Thread mPoolThread;
    private Handler mPoolThreadHandler;
    /**
     * UI线程中的Handler
     */
    private Handler mUIHandler;

    private Semaphore mSemaphorePoolThreadHandler = new Semaphore(0);

    private Semaphore mSemaphoreThreadPool;
    public enum Type
    {
        FIFO, LIFO;
    }

    private ListImageLoader(int threadCount, Type type)
    {
        init(threadCount, type);
    }
    /*
     * 初始化操作
     */
    private void init(int threadCount, Type type)
    {
        // 后台轮询线程
        mPoolThread = new Thread() {

            @Override
            public void run()
            {
                Looper.prepare();//给线程创建一个消息循环
                mPoolThreadHandler = new Handler() {

                    @Override
                    public void handleMessage(Message msg)
                    {
                        // 线程池取出一个任务去执行
                        mThreadPool.execute(getTask());
                        try
                        {
                            mSemaphoreThreadPool.acquire();
                        }
                        catch (InterruptedException e)
                        {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                };
                //释放一个信号量
                mSemaphorePoolThreadHandler.release();
                Looper.loop();//使消息循环起作用，Looper.loop()内部会结束整个子线程的执行， 所以Looper.loop()之后的语句是不会运行的
            }
        };
       /* mPoolThread = new Thread(new Runnable() {

                @Override
                public void run()
                {
                    // TODO Auto-generated method stub
                    Looper.prepare();
                    mPoolThreadHandler = new Handler() {

                        @Override
                        public void handleMessage(Message msg)
                        {
                            // 线程池取出一个任务去执行
                            mThreadPool.execute(getTask());
                            try
                            {
                                mSemaphoreThreadPool.acquire();
                            }
                            catch (InterruptedException e)
                            {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }
                    };
                    //释放一个信号量
                    mSemaphorePoolThreadHandler.release();
                    Looper.loop();
                }
            });*/
        mPoolThread.start();

        // 获取我们应用的最大可用内存
        int MaxMemory = (int) Runtime.getRuntime().maxMemory();
        //设置缓存大小为最大可用内存的1/8
        int cacheMemory = MaxMemory / 8;
        mLruCache = new LruCache<String, Bitmap>(cacheMemory) {

            @Override
            protected int sizeOf(String key, Bitmap value)
            {// 重写此方法来衡量每张图片的大小
                return value.getRowBytes() * value.getHeight();//getRowBytes()获取Bitmap每一行所占用的内存字节数。
            }
        };

        //创建线程池
        mThreadPool = Executors.newFixedThreadPool(threadCount);//创建一个固定大小的线程池,线程池执行数量不能超过threadcount
        mTaskQueue = new LinkedList<Runnable>();
        mType = type;

        mSemaphoreThreadPool = new Semaphore(threadCount);//创建一个信号量为threadcount的许可集
    }
    /**
     * 从任务队列取出一个方法
     */
    private Runnable getTask()
    {
        if (mType == Type.FIFO)
        {
            return mTaskQueue.removeFirst();
        }
        else if (mType == Type.LIFO)
        {
            return mTaskQueue.removeLast();
        }
        return null;
    }

    public static ListImageLoader getInstance(int threadCount, Type type)
    {
        // 懒加载
        if (mInstance == null)
        {
            // 同步资源锁，防止不同线程多次调用，占用内存，提高效率
            synchronized (ListImageLoader.class)
            {
                if (mInstance == null)
                {
                    mInstance = new ListImageLoader(threadCount, type);
                }
            }
        }
        return mInstance;
    }
    /**
     *根据path为imageView设置图片
     */
    public void loadImage(final String path, final ImageView imageView)
    {
        imageView.setTag(path);
        if (mUIHandler == null)
        {
            mUIHandler = new Handler(){

                public void handleMessage(Message msg)
                {
                    //获取得到图片，为imageview回调设置图片
                    ImgBeanHolder holder = (ImgBeanHolder) msg.obj;
                    Bitmap bm = holder.bitmap;
                    ImageView imageview = holder.imageView;
                    String path = holder.path;
                    //将path与gettag存储路径进行比较
                    if (imageview.getTag().toString().equals(path))
                    {
                        imageview.setImageBitmap(bm);
                    }
                };
            };
        }
        //根据path在缓存中获取Bitmap
        Bitmap bm = getBitmapFromLruCache(path);
        if (bm != null)
        {
            refreshBitmap(path, imageView, bm);
        }
        else
        {
            addTask(new Runnable() {

                    @Override
                    public void run()
                    {
                        //加载图片
                        //图片的压缩
                        //1.获得图片需要显示的大小
                        ImageSize imageSize = getImageViewSize(imageView);
                        //2.压缩图片
                        Bitmap bm = decodeSampledBitmapFromPath(path, imageSize.width, imageSize.height);
                        //3.把图片加入到缓存
                        addBitmapToLruCache(path, bm);

                        refreshBitmap(path, imageView, bm);
                        mSemaphoreThreadPool.release();
                    }
                });
        }
    }

    private void refreshBitmap(final String path, final ImageView imageView, Bitmap bm)
    {
        Message message =Message.obtain();//从整个Messge池中返回一个新的Message实例，在许多情况下使用它，因为它能避免分配新的对象，避免内存开销
        ImgBeanHolder holder = new ImgBeanHolder();
        holder.bitmap = bm;
        holder.path = path;
        holder.imageView = imageView;
        message.obj = holder;
        mUIHandler.sendMessage(message);
    }
    /**
     * 将图片加入缓存
     */
    protected void addBitmapToLruCache(String path, Bitmap bm)
    {
        // TODO Auto-generated method stub
        if (getBitmapFromLruCache(path) == null)
        {
            if (bm != null)
            {
                mLruCache.put(path, bm);
            }
        }
    }
    /**
     * 根据图片需要显示的宽和高进行压缩
     */
    protected Bitmap decodeSampledBitmapFromPath(String path, int width, int height)
    {
        //获取图片的宽和高并不把图片加载到内存中
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);//options中保存图片的真实宽高

        options.inSampleSize = caculateInSampleSize(options, width, height);//处理得到压缩比例

        //使用获取到的InSampleSize再次解析图片
        options.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeFile(path, options);
      
        return bitmap;
    }
    /**
     * 根据需求的宽和高以及图片实际的宽和高计算SampleSize
     */
    private int caculateInSampleSize(Options options, int reqwidth, int reqheight)
    {
        int width = options.outWidth;
        int height = options.outHeight;
        int inSampleSize = 1;
        if (width > reqwidth || height > reqheight)
        {
            int widthRadio = Math.round(width * 1.0f / reqwidth);
            int heightRadio = Math.round(height * 1.0f / reqheight);

            inSampleSize = Math.max(widthRadio, heightRadio);
        }
        return inSampleSize;
    }
    /**
     * 根据imageView获取适当的压缩的宽和高
     */
    protected ImageSize getImageViewSize(ImageView imageView)
    {
        ImageSize imageSize = new ImageSize();
        DisplayMetrics displayMetrics = imageView.getContext().getResources().getDisplayMetrics();//将当前窗口的一些信息放在DisplayMetrics类中

        LayoutParams lp = imageView.getLayoutParams();
        int width = imageView.getWidth();//获取imageView的实际宽度
        if (width <= 0)
        {
            width = lp.width;//获取imageview在layout中声明的宽度
        }
        if (width <= 0)
        {
            //width = imageView.getMaxWidth();//检查最大值
            width = getImageViewFieldValue(imageView, "mMaxWidth");
        }
        if (width <= 0)
        {
            width = displayMetrics.widthPixels;//屏幕宽度
        }

        int height = imageView.getHeight();//获取imageView的实际宽度
        if (height <= 0)
        {
            height = lp.height;//获取imageview在layout中声明的宽度
        }
        if (height <= 0)
        {
            //height = imageView.getMaxHeight();//检查最大值
            height = getImageViewFieldValue(imageView, "mMaxHeight");
        }
        if (height <= 0)
        {
            height = displayMetrics.heightPixels;
        }
        imageSize.width = width;
        imageSize.height = height;

        return imageSize;
    }
    /**
     * 通过反射获取ImageView的某个属性值
     */
    private static int getImageViewFieldValue(Object object, String fieldName)
    {
        int value = 0;
        try
        {
            Field field = ImageView.class.getDeclaredField(fieldName);
            field.setAccessible(true);

            int fieldValue = field.getInt(object);
            if (fieldValue > 0 && fieldValue < Integer.MAX_VALUE)
            {
                value = fieldValue;
            }
        }
        catch (Exception e)
        {
        }
        return value;
    }
    /**
     * 同步锁一次只能
     */
    private synchronized void addTask(Runnable runnable)
    {
        mTaskQueue.add(runnable);
        //if(mPoolThreadHandler == null) wait();
        try
        {
            if (mPoolThreadHandler == null)
                mSemaphorePoolThreadHandler.acquire();
        }
        catch (InterruptedException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        mPoolThreadHandler.sendEmptyMessage(0x110);
    }
    /**
     * 根据path在缓存中获取Bitmap
     */
    private Bitmap getBitmapFromLruCache(String key)
    {
        return mLruCache.get(key);
    }
    private class ImageSize
    {
        int width;
        int height;
    }
    private class ImgBeanHolder
    {
        Bitmap bitmap;
        ImageView imageView;
        String path;
    }
    
}
