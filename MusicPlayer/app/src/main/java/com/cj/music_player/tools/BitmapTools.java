package com.cj.music_player.tools;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Matrix;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.LinearGradient;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.BitmapDrawable;
import android.os.Environment;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import android.graphics.BitmapFactory;
import java.io.ByteArrayOutputStream;
import android.media.ExifInterface;
import android.util.Log;
import java.io.ByteArrayInputStream;
import android.content.Context;
import android.renderscript.ScriptIntrinsicBlur;
import android.renderscript.Element;
import android.renderscript.Allocation;
import android.renderscript.RenderScript;

public class BitmapTools
{
    public static Bitmap Blurbitmap(Context context, Bitmap bitmap, int scaleWidget, int scaleHeight, float blurRadius)
    {
        // 产生一个可以缩放的图片
        Bitmap inputBitmap = Bitmap.createScaledBitmap(bitmap, scaleWidget, scaleHeight, false);
        Bitmap outputBitmap = Bitmap.createBitmap(inputBitmap);
        // 创建RenderScript内核对象
        RenderScript rs = RenderScript.create(context);
        ScriptIntrinsicBlur blurScript = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
        // 由于RenderScript并没有使用VM来分配内存,所以需要使用Allocation类来创建和分配内存空间。
        // 创建Allocation对象的时候其实内存是空的,需要使用copyTo()将数据填充进去。
        Allocation tmpIn = Allocation.createFromBitmap(rs, inputBitmap);
        Allocation tmpOut = Allocation.createFromBitmap(rs, outputBitmap);
        // 设置渲染的模糊程度
        blurScript.setRadius(blurRadius);
        // 设置blurScript对象的输入内存
        blurScript.setInput(tmpIn);
        // 将输出数据保存到输出内存中
        blurScript.forEach(tmpOut);
        // 将数据填充到Allocation中
        tmpOut.copyTo(outputBitmap);

        return outputBitmap;
    }

    private Bitmap compressImage(Bitmap image)
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream(); 
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中 
        int options = 100; 
        while (baos.toByteArray().length / 1024 > 100) 
        {
            //循环判断如果压缩后图片是否大于100kb,大于继续压缩 
            baos.reset();//重置baos即清空baos 
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中 
            options -= 10;//每次都减少10 
        } 
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中 
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//把ByteArrayInputStream数据生成图片 
        return bitmap; 
    }

    //压缩图片
    public static Bitmap compressBitmap(Bitmap bitmap, Bitmap.CompressFormat format, int size)
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream(); 
        bitmap.compress(format, size, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中 
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中 
        bitmap = BitmapFactory.decodeStream(isBm, null, null);//把ByteArrayInputStream数据生成图片 
        return bitmap; 
    }

    //缩放图片
    public static Bitmap ScaleBitmap(Bitmap bitmap, int scaleWidget, int scaleHeight, boolean keepProtion)
    {
        if (keepProtion == true)
        {
            int width = bitmap.getWidth();  
            int height = bitmap.getHeight();  
            float kp = ((float) scaleWidget) / width;  
            Matrix matrix = new Matrix();  
            matrix.postScale(kp, kp); 
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, width,  height, matrix, true);
        }
        else
        {
            Bitmap inputBitmap = Bitmap.createScaledBitmap(bitmap, scaleWidget, scaleHeight, false);
            bitmap = Bitmap.createBitmap(inputBitmap);
        }
        return bitmap;
    }
    /**
     * 带倒影的图像
     */
    public Bitmap createReflectionBitmap(Bitmap src)
    {
        // 两个图像间的空隙
        final int spacing = 1;
        final int w = src.getWidth();
        final int h = src.getHeight();
        // 绘制高质量32位图
        Bitmap bitmap = Bitmap.createBitmap(w, h + h / 2 + spacing, Config.ARGB_8888);
        // 创建沿X轴的倒影图像
        Matrix m = new Matrix();
        m.setScale(1, -1);
        Bitmap t_bitmap = Bitmap.createBitmap(src, 0, h / 2, w, h / 2, m, true);

        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        // 绘制原图像
        canvas.drawBitmap(src, 0, 0, paint);
        // 绘制倒影图像
        canvas.drawBitmap(t_bitmap, 0, h + spacing, paint);
        // 线性渲染-沿Y轴高到低渲染
        Shader shader = new LinearGradient(0, h + spacing, 0, h + spacing + h / 2, 0x70ffffff, 0x00ffffff, Shader.TileMode.MIRROR);
        paint.setShader(shader);
        // 取两层绘制交集。显示下层。
        paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
        // 绘制渲染倒影的矩形
        canvas.drawRect(0, h + spacing, w, h + h / 2 + spacing, paint);
        return bitmap;
    }
    /**
     * Bitmap转Drawable
     */
    public Drawable bitmapToDrawable(Bitmap bitmap)
    {
        Drawable drawable = new BitmapDrawable(bitmap);
        return drawable;
	}

    //得到位图的宽高
    public String bitmapWidthHeight(Bitmap bitmap)
    {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        return width + "×" + height;
    }

    //保存Bitmap对象到sd卡
    //参数一要保存的Bitmap，参数二保存后的文件名字
    public static void saveBitmap(Bitmap bitmap, String path, String name, Bitmap.CompressFormat format, int compressSize)
    {
        //得到手机的默认存储目录
        
        String dir = Environment.getExternalStorageDirectory().getPath().toString();
        File f = new File(path + name);
        if (!f.getParentFile().exists())
        {
            //如果这个文件的目录不存在，创建它
            f.getParentFile().mkdir();
        }
        try
        {
            FileOutputStream out = new FileOutputStream(f);
            //设置输出类型为PNG和文件名后缀要对应
            bitmap.compress(format, compressSize, out);
            out.flush();
            out.close();
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    //检查文件 path为完整路径
    public static boolean CheckFile(String path)
    {
        String dir = Environment.getExternalStorageDirectory().getPath().toString();
        if (new File(dir + path).exists())
        {
            return true; 
        }
        return false;
    }

}
