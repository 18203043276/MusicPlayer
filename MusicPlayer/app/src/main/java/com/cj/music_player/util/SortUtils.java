package com.cj.music_player.util;

import com.cj.music_player.info.MusicInfo;
import com.cj.music_player.info.FolderInfo;
import com.cj.music_player.info.AlbumInfo;
import com.cj.music_player.info.ArtistInfo;
import com.cj.music_player.info.StarInfo;

import java.util.Comparator;
import java.text.Collator;

public class SortUtils 
{
    private Collator collator = Collator.getInstance(java.util.Locale.CHINA);

    public class SortMusicList implements Comparator
    {
        public int compare(Object o1, Object o2)
        {
            MusicInfo s1 = (MusicInfo) o1;
            MusicInfo s2 = (MusicInfo) o2;
            if (collator.compare(s1.getTitle(), s2.getTitle()) < 0)
            {
                return -1;
            }
            else if (collator.compare(s1.getTitle(), s2.getTitle()) > 0)
            {
                return 1;
            }
            else
            {
                return 0;
            }
        }
    }

    public class SortFolderList implements Comparator
    {
        public int compare(Object o1, Object o2)
        {
            FolderInfo s1 = (FolderInfo) o1;
            FolderInfo s2 = (FolderInfo) o2;
            if (collator.compare(s1.getTitle(), s2.getTitle()) < 0)
            {
                return -1;
            }
            else if (collator.compare(s1.getTitle(), s2.getTitle()) > 0)
            {
                return 1;
            }
            else
            {
                return 0;
            }
        }
    }

    public class SortAlbumList implements Comparator
    {
        public int compare(Object o1, Object o2)
        {
            AlbumInfo s1 = (AlbumInfo) o1;
            AlbumInfo s2 = (AlbumInfo) o2;
            if (collator.compare(s1.getTitle(), s2.getTitle()) < 0)
            {
                return -1;
            }
            else if (collator.compare(s1.getTitle(), s2.getTitle()) > 0)
            {
                return 1;
            }
            else
            {
                return 0;
            }
        }
    }

    public class SortArtistList implements Comparator
    {
        public int compare(Object o1, Object o2)
        {
            ArtistInfo s1 = (ArtistInfo) o1;
            ArtistInfo s2 = (ArtistInfo) o2;
            if (collator.compare(s1.getTitle(), s2.getTitle()) < 0)
            {
                return -1;
            }
            else if (collator.compare(s1.getTitle(), s2.getTitle()) > 0)
            {
                return 1;
            }
            else
            {
                return 0;
            }
        }
    }

    public class SortStarList implements Comparator
    {
        public int compare(Object o1, Object o2)
        {
            StarInfo s1 = (StarInfo) o1;
            StarInfo s2 = (StarInfo) o2;
            if (s1.getStar().compareTo(s2.getStar()) < 0)
            {
                return 1;
            }
            else if (s1.getStar().compareTo(s2.getStar()) > 0)
            {
                return -1;
            }
            else
            {
                return 0;
            }
        }
    }
}
