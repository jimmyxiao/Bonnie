package com.sctw.bonniedraw.widget;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sctw.bonniedraw.R;
import com.sctw.bonniedraw.colorpick.ColorBean;
import com.sctw.bonniedraw.colorpick.ColorPanelView;
import com.sctw.bonniedraw.colorpick.ColorPickerView;
import com.sctw.bonniedraw.colorpick.ColorTicket;
import com.sctw.bonniedraw.utility.PxDpConvert;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Fatorin on 2017/11/1.
 */

public class ColorPopup extends PopupWindow implements View.OnTouchListener,
        ColorPickerView.OnColorChangedListener, TextWatcher, ColorTicket.OnItemListener {

    private View conentView;
    private Context context;
    private SharedPreferences mPref;
    private Button mBtnAddTicket;
    private ImageButton mBtnOpen, mBtnRemove;
    private RecyclerView mRv;
    private ColorTicket mAdapterTicket;
    private ColorPickerView mColorPicker;
    private ColorPanelView mColorPanel;
    private EditText mEditTextHex;
    private LinearLayout mLlColorControl;
    private int color = Color.BLACK;
    private boolean fromEditText;
    private ArrayList<ColorBean> colorsList;
    private OnPopupColorPick listener;

    public ColorPopup(Context context, OnPopupColorPick listener) {
        super(context);
        this.context = context;
        this.listener = listener;
        mPref = context.getSharedPreferences("colors", MODE_PRIVATE);
        this.initBindView();
        this.setOnClick();
    }

    @Override
    public void dismiss() {
        super.dismiss();
        savePreferencesColor();
        toggleColorPick(false);
    }

    private void savePreferencesColor() {
        Gson gson = new Gson();
        String json = gson.toJson(colorsList);
        mPref.edit().clear().commit();
        mPref.edit().putString("colorsInfo", json).apply();
    }

    private void readPreferencesColor() {
        colorsList = new ArrayList<>();
        Gson gson = new Gson();
        String json = mPref.getString("colorsInfo", "");
        if (!json.isEmpty()) {
            colorsList = gson.fromJson(json, new TypeToken<ArrayList<ColorBean>>() {
            }.getType());
        } else {
            colorsList.add(new ColorBean(ContextCompat.getColor(context, R.color.Red)));
            colorsList.add(new ColorBean(ContextCompat.getColor(context, R.color.Amber)));
            colorsList.add(new ColorBean(ContextCompat.getColor(context, R.color.Yellow)));
            colorsList.add(new ColorBean(ContextCompat.getColor(context, R.color.Green)));
            colorsList.add(new ColorBean(ContextCompat.getColor(context, R.color.Blue)));
        }
    }

    private void initBindView() {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        conentView = inflater.inflate(R.layout.popup_color_select, null);
        this.setContentView(conentView);
        this.setWidth(LinearLayout.LayoutParams.MATCH_PARENT);
        this.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
        this.setFocusable(true);
        this.setOutsideTouchable(true);
        this.update();
        mLlColorControl = (LinearLayout) conentView.findViewById(R.id.linearLayout_colorpick_control);
        mColorPanel = (ColorPanelView) conentView.findViewById(R.id.cpv_colorpanel);
        mEditTextHex = (EditText) conentView.findViewById(R.id.editText_hex_color);
        mBtnAddTicket = (Button) conentView.findViewById(R.id.btn_add_ticket);
        mColorPicker = (ColorPickerView) conentView.findViewById(R.id.cpv_colorpicker);
        mBtnOpen = (ImageButton) conentView.findViewById(R.id.imgBtn_colorpick_open);
        mBtnRemove = (ImageButton) conentView.findViewById(R.id.imgBtn_ticket_remove);
        mRv = (RecyclerView) conentView.findViewById(R.id.recyclerView_color_tickets);
        mColorPicker.setColor(color, true);
        mColorPanel.setColor(color);
        setHex(color);
        conentView.setOnTouchListener(this);
        mColorPicker.setOnColorChangedListener(this);
        mEditTextHex.addTextChangedListener(this);
        readPreferencesColor();
        mAdapterTicket = new ColorTicket(colorsList, this);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 9, LinearLayoutManager.VERTICAL, false);
        mRv.setLayoutManager(gridLayoutManager);
        mRv.setAdapter(mAdapterTicket);
        mEditTextHex.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(mEditTextHex, InputMethodManager.SHOW_IMPLICIT);
                }
            }
        });
    }

    public void toggleColorPick(boolean toggle) {
        if (toggle) {
            mLlColorControl.setVisibility(View.VISIBLE);
            mColorPicker.setVisibility(View.VISIBLE);
        } else {
            mLlColorControl.setVisibility(View.GONE);
            mColorPicker.setVisibility(View.GONE);
        }
    }

    private void setOnClick() {
        mBtnOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAdapterTicket.get_mSelectedPos() != -1) {
                    color = colorsList.get(mAdapterTicket.get_mSelectedPos()).getColor();
                    mColorPicker.setColor(color);
                    mColorPanel.setColor(color);
                    setHex(color);
                }
                toggleColorPick(true);
                listener.onClickOpenColorPick();
            }
        });

        mBtnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = mAdapterTicket.get_mSelectedPos();
                if (position != -1) {
                    mAdapterTicket.removeColor();
                    savePreferencesColor();
                } else {
                    ToastUtil.createToastWindow(context, context.getString(R.string.please_select_ticket), PxDpConvert.getSystemHight(context) / 4);
                }

            }
        });

        mBtnAddTicket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAdapterTicket.getItemCount() <= 17) {
                    mAdapterTicket.set_mSelectedPos(0);
                    mAdapterTicket.addNewColor(new ColorBean(color, true));
                    mAdapterTicket.notifyDataSetChanged();
                    savePreferencesColor();
                } else {
                    ToastUtil.createToastWindow(context, context.getString(R.string.ticket_full), PxDpConvert.getSystemHight(context) / 4);
                }
            }
        });
    }

    private void setHex(int color) {
        mEditTextHex.setText(String.format("%06X", (0xFFFFFF & color)));
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (v != mEditTextHex && mEditTextHex.hasFocus()) {
            mEditTextHex.clearFocus();
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(mEditTextHex.getWindowToken(), 0);
            mEditTextHex.clearFocus();
            return true;
        }
        return false;
    }

    @Override
    public void onColorChanged(int newColor) {
        color = newColor;
        mColorPanel.setColor(newColor);
        listener.onColorSelect(color);
        if (!fromEditText) {
            setHex(newColor);
            if (mEditTextHex.hasFocus()) {
                InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mEditTextHex.getWindowToken(), 0);
                mEditTextHex.clearFocus();
            }
        }
        fromEditText = false;
    }


    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if (mEditTextHex.isFocused()) {
            int color = parseColorString(s.toString());
            if (color != mColorPicker.getColor()) {
                fromEditText = true;
                mColorPicker.setColor(color, true);
                listener.onColorSelect(color);
            }
        }
    }

    private int parseColorString(String colorString) throws NumberFormatException {
        int a, r, g, b = 0;
        if (colorString.startsWith("#")) {
            colorString = colorString.substring(1);
        }
        if (colorString.length() == 0) {
            r = 0;
            a = 255;
            g = 0;
        } else if (colorString.length() <= 2) {
            a = 255;
            r = 0;
            b = Integer.parseInt(colorString, 16);
            g = 0;
        } else if (colorString.length() == 3) {
            a = 255;
            r = Integer.parseInt(colorString.substring(0, 1), 16);
            g = Integer.parseInt(colorString.substring(1, 2), 16);
            b = Integer.parseInt(colorString.substring(2, 3), 16);
        } else if (colorString.length() == 4) {
            a = 255;
            r = Integer.parseInt(colorString.substring(0, 2), 16);
            g = r;
            r = 0;
            b = Integer.parseInt(colorString.substring(2, 4), 16);
        } else if (colorString.length() == 5) {
            a = 255;
            r = Integer.parseInt(colorString.substring(0, 1), 16);
            g = Integer.parseInt(colorString.substring(1, 3), 16);
            b = Integer.parseInt(colorString.substring(3, 5), 16);
        } else if (colorString.length() == 6) {
            a = 255;
            r = Integer.parseInt(colorString.substring(0, 2), 16);
            g = Integer.parseInt(colorString.substring(2, 4), 16);
            b = Integer.parseInt(colorString.substring(4, 6), 16);
        } else if (colorString.length() == 7) {
            a = Integer.parseInt(colorString.substring(0, 1), 16);
            r = Integer.parseInt(colorString.substring(1, 3), 16);
            g = Integer.parseInt(colorString.substring(3, 5), 16);
            b = Integer.parseInt(colorString.substring(5, 7), 16);
        } else if (colorString.length() == 8) {
            a = Integer.parseInt(colorString.substring(0, 2), 16);
            r = Integer.parseInt(colorString.substring(2, 4), 16);
            g = Integer.parseInt(colorString.substring(4, 6), 16);
            b = Integer.parseInt(colorString.substring(6, 8), 16);
        } else {
            b = -1;
            g = -1;
            r = -1;
            a = -1;
        }
        return Color.argb(a, r, g, b);
    }

    @Override
    public void onItemClick(int color) {
        onColorChanged(color);
        mColorPicker.setColor(color, true);
        listener.onColorSelect(color);
    }

    public interface OnPopupColorPick {
        void onClickOpenColorPick();

        void onColorSelect(int color);
    }
}
