package com.sctw.bonniedraw.utility;

import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

import com.androidadvance.topsnackbar.TSnackbar;

/**
 * Created by Fatorin on 2017/10/20.
 */

public class TSnackbarCall {
    public static void showTSnackbar(View view, String string){
        TSnackbar snackbar = TSnackbar.make(view, "", TSnackbar.LENGTH_SHORT);
        snackbar.setActionTextColor(Color.WHITE);
        View snackbarView = snackbar.getView();
        snackbarView.setBackgroundColor(Color.parseColor("#ff5722"));
        TextView textView = (TextView) snackbarView.findViewById(com.androidadvance.topsnackbar.R.id.snackbar_text);
        textView.setTextColor(Color.WHITE);
        textView.setText(string);
        snackbar.show();
    }
}
