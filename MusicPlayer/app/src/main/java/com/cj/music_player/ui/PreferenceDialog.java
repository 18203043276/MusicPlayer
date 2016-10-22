package com.cj.music_player.ui;

import com.cj.music_player.R;
import android.preference.DialogPreference;
import android.content.Context;
import android.util.AttributeSet;

public class PreferenceDialog extends DialogPreference
{
    public PreferenceDialog(Context context, AttributeSet attr)
    {
        super(context, attr);
        setDialogLayoutResource(R.layout.timer_dialog);
    }
}
