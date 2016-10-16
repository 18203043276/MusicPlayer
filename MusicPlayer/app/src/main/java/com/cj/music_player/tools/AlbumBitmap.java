package com.cj.music_player.tools;

import org.jaudiotagger.audio.mp3.MP3File;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.images.Artwork;
import org.jaudiotagger.tag.id3.AbstractID3v2Tag;

import android.graphics.Bitmap;
import java.io.File;
import android.graphics.BitmapFactory;
import android.content.Context;
import java.io.FileDescriptor;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.content.ContentUris;
import java.io.FileNotFoundException;

public class AlbumBitmap
{
    public static Bitmap AlbumImage(String path)
    {
        File file = new File(path);
        Bitmap bitmap = null;

        if (file.exists())
        {
            try
            {
                MP3File mp3File = (MP3File) AudioFileIO.read(file);
                if (mp3File.hasID3v1Tag())
                {
                    Tag tag = mp3File.getTag();
                    Artwork artwork = tag.getFirstArtwork();// 获得专辑图片
                    if (artwork != null)
                    {
                        byte[] byteArray = artwork.getBinaryData();// 将读取到的专辑图片转成二进制
                        bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length); // 通过BitmapFactory转成Bitmap
                    }
                }
                else if (mp3File.hasID3v2Tag())
                {
                    // 如果上面的条件不成立，才执行下面的方法
                    AbstractID3v2Tag v2Tag = mp3File.getID3v2Tag();
                    Artwork artwork = v2Tag.getFirstArtwork();// 获得专辑图片
                    if (artwork != null)
                    {
                        byte[] byteArray = artwork.getBinaryData();// 将读取到的专辑图片转成二进制
                        bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length); // 通过BitmapFactory转成Bitmap
                    }
                }
            }
            catch (Exception e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return bitmap;
    }

    public static Bitmap getAlbumImage(Context context, long songid, long albumid)
    {
        Bitmap bm = null;
        if (albumid < 0 && songid < 0)
        {
            //抛出异常
            throw new IllegalArgumentException("专辑或者id不能为空");
        }
        try
        {
            BitmapFactory.Options options = new BitmapFactory.Options();
            FileDescriptor fd = null;
            if (albumid < 0)
            {
                Uri uri = Uri.parse("content://media/external/audio/media/" + songid + "/albumart");
                ParcelFileDescriptor pfd = context.getContentResolver().openFileDescriptor(uri, "r");
                if (pfd != null)
                {
                    fd = pfd.getFileDescriptor();
                }
            }
            else
            {
                Uri uri = ContentUris.withAppendedId(Uri.parse("content://media/external/audio/albumart"), albumid);
                ParcelFileDescriptor pfd = context.getContentResolver().openFileDescriptor(uri, "r");
                if (pfd != null)
                {
                    fd = pfd.getFileDescriptor();
                }
            }
            bm = BitmapFactory.decodeFileDescriptor(fd, null, options);
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        return bm;
    }

}
