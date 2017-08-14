package com.sctw.bonniedraw.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;

import java.lang.reflect.Field;

/**
 * Created by professor on 6/28/16.
 */
public class NumberPicker extends android.widget.NumberPicker {
    public NumberPicker(Context context) {
        super(context);
    }

    public NumberPicker(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NumberPicker(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public NumberPicker(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void setDividerColor(int color) {
        Field[] pickerFields = android.widget.NumberPicker.class.getDeclaredFields();
        for (Field pf : pickerFields) {
            if (pf.getName().equals("mSelectionDivider")) {
                pf.setAccessible(true);
                try {
                    pf.set(this, new ColorDrawable(color));
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (Resources.NotFoundException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }
}
