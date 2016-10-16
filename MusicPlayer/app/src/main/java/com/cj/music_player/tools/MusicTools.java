package com.cj.music_player.tools;

import org.jaudiotagger.audio.mp3.MP3File;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.mp3.MP3AudioHeader;
import org.jaudiotagger.tag.TagException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.FieldKey;

import java.io.File;
import java.io.IOException;
import android.content.Context;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;

public class MusicTools
{
    /**
     * 拿到专辑图片的路径
     */
    public String getAlbumArt(Context context, int album_id)
    {
        ContentResolver resolver = context.getContentResolver();
        String mUriAlbums = "content://media/external/audio/albums";
        String[] projection = new String[] { "album_art" };
        Cursor cur = resolver.query(Uri.parse(mUriAlbums + "/" + Integer.toString(album_id)), projection, null, null, null);
        String album_art = null;
        if (cur.getCount() > 0 && cur.getColumnCount() > 0)
        {
            cur.moveToNext();
            album_art = cur.getString(0);
        }
        cur.close();
        cur = null;
        return album_art;
    }
    
    //时间转换毫秒至分钟
    public String TimeConversion(int time)
    {
        int m = time / 1000 / 60;
        int s = time / 1000 % 60;
        if (m < 10)
        {
            if (s < 10)
            {
                return "0" + m + ":" + "0" + s ;
            }
            else
            {
                return "0" + m + ":" + s ;
            }
        }
        else
        {
            if (s < 10)
            {
                return m + ":" + "0" + s ;
            }
            else
            {
                return m + ":" + s ;
            }
        }
    }
    //时间转换小时至毫秒
    public int TimeHourConversion(int time)
    {
        return time * 60 * 60 * 1000;
    } 

    //时间转换分钟至毫秒
    public int TimeMinuteConversion(int time)
    {
        return time * 60 * 1000;
    }

    //得到音乐格式
    public String getFormat(String path)
    {
        String format = "未知";
        File file = new File(path);
        if (file.exists())
        {
            try
            {
                MP3File mp3File = (MP3File) AudioFileIO.read(file);
                MP3AudioHeader header = mp3File.getMP3AudioHeader();
                format = header.getEncodingType();// 格式(编码类型)
            }
            catch (CannotReadException e)
            {}
            catch (InvalidAudioFrameException e)
            {}
            catch (IOException e)
            {}
            catch (TagException e)
            {}
            catch (ReadOnlyFileException e)
            {}
        }
        return format;
    }

    //得到音乐声道
    public String getChannels(String path)
    {
        String channels = "未知";
        File file = new File(path);
        if (file.exists())
        {
            try
            {
                MP3File mp3File = (MP3File) AudioFileIO.read(file);
                MP3AudioHeader header = mp3File.getMP3AudioHeader();
                channels = header.getChannels();
                if (channels.equals("Joint Stereo"))
                {
                    channels = "立体声";
                }
                else
                {
                    channels = header.getChannels();// 声道
				}
            }
            catch (CannotReadException e)
            {}
            catch (InvalidAudioFrameException e)
            {}
            catch (IOException e)
            {}
            catch (TagException e)
            {}
            catch (ReadOnlyFileException e)
            {}
        }
        return channels;
    }

    //得到音乐比特率
    public String getKbps(String path)
    {
        String kbps = "未知";
        File file = new File(path);
        if (file.exists())
        {
            try
            {
                MP3File mp3File = (MP3File) AudioFileIO.read(file);
                MP3AudioHeader header = mp3File.getMP3AudioHeader();
                kbps = header.getBitRate() + "Kbps";// 比特率
            }
            catch (CannotReadException e)
            {}
            catch (InvalidAudioFrameException e)
            {}
            catch (IOException e)
            {}
            catch (TagException e)
            {}
            catch (ReadOnlyFileException e)
            {}
        }
        return kbps;
    }

    //得到音乐采样率
    public String getHz(String path)
    {
        String Hz = "未知";
        File file = new File(path);
        if (file.exists())
        {
            try
            {
                MP3File mp3File = (MP3File) AudioFileIO.read(file);
                MP3AudioHeader header = mp3File.getMP3AudioHeader();
                Hz = header.getSampleRate() + "Hz";// 采样率
            }
            catch (CannotReadException e)
            {}
            catch (InvalidAudioFrameException e)
            {}
            catch (IOException e)
            {}
            catch (TagException e)
            {}
            catch (ReadOnlyFileException e)
            {}
        }
        return Hz;
    }

    //得到音乐年代
    public String getYears(String path)
    {
        String Years = "未知";
        File file = new File(path);
        if (file.exists())
        {
            try
            {
                MP3File mp3File = (MP3File) AudioFileIO.read(file);
                if (mp3File.hasID3v1Tag())
                {
					Tag tag = mp3File.getTag();
                    Years = tag.getFirst(FieldKey.YEAR);
                    if (Years == null || Years.equals(""))
                    {
                        Years = "未知";
                    }
                    else
                    {
                        Years = new String(Years.getBytes("ISO-8859-1"), "GB2312");// 年代                   
                    }
                }
            }
            catch (CannotReadException e)
            {}
            catch (InvalidAudioFrameException e)
            {}
            catch (IOException e)
            {}
            catch (TagException e)
            {}
            catch (ReadOnlyFileException e)
            {}
        }
        return Years;
    }

    //得到音乐风格
    public String getGener(String path)
    {
        String Gener = "未知";
        File file = new File(path);
        if (file.exists())
        {
            try
            {
                MP3File mp3File = (MP3File) AudioFileIO.read(file);
                if (mp3File.hasID3v1Tag())
                {
                    Tag tag = mp3File.getTag();
                    Gener = tag.getFirst(FieldKey.GENRE);
                    if (Gener == null || Gener.equals(""))
                    {
                        Gener = "未知";
                    }
                    else
                    {
                        Gener = new String(Gener.getBytes("ISO-8859-1"), "GB2312");// 风格
                    }
                }
            }
            catch (CannotReadException e)
            {}
            catch (InvalidAudioFrameException e)
            {}
            catch (IOException e)
            {}
            catch (TagException e)
            {}
            catch (ReadOnlyFileException e)
            {}
        }
        return Gener;
    }

    //大小转换B至MB
    public String SizeCoversion(int size)
    {
        int m = size / 1024 / 1024;
        int k = size / 1024 % 60;
        if (k < 10)
        {
            return m + "." + "0" + k + "MB";
        }
        else
        {
            return m + "." + k + "MB";
        }
    }

}
