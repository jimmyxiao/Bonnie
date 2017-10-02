package com.sctw.bonniedraw.utility;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;

import com.sctw.bonniedraw.R;

/**
 * Created by Fatorin on 2017/10/2.
 */

public class FullScreenDialog extends Dialog {
    public FullScreenDialog(@NonNull Context context) {
        super(context, R.style.FullScreenDialog);
        setContentView(R.layout.paint_save_dialog);
    }
}
