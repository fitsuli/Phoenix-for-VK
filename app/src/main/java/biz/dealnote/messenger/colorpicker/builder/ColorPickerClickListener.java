package biz.dealnote.messenger.colorpicker.builder;

import android.content.DialogInterface;


public interface ColorPickerClickListener {
    void onClick(DialogInterface d, int lastSelectedColor, Integer[] allColors);
}