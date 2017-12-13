package com.sctw.bonniedraw.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sctw.bonniedraw.R;
import com.sctw.bonniedraw.colorpick.ColorBean;
import com.sctw.bonniedraw.paint.Brush;
import com.sctw.bonniedraw.paint.Brushes;
import com.sctw.bonniedraw.paint.PaintView;
import com.sctw.bonniedraw.utility.BDWFileWriter;
import com.sctw.bonniedraw.utility.ConnectJson;
import com.sctw.bonniedraw.utility.FullScreenDialog;
import com.sctw.bonniedraw.utility.GlobalVariable;
import com.sctw.bonniedraw.utility.OkHttpUtil;
import com.sctw.bonniedraw.utility.PxDpConvert;
import com.sctw.bonniedraw.widget.ColorPopup;
import com.sctw.bonniedraw.widget.MenuPopup;
import com.sctw.bonniedraw.widget.SizePopup;
import com.sctw.bonniedraw.widget.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class PaintActivity extends AppCompatActivity implements MenuPopup.MenuPopupOnClick, SizePopup.OnSeekChange, ColorPopup.OnPopupColorPick {
    private PaintView mPaintView;
    private FrameLayout mFrameLayoutFreePaint;
    private ImageButton mBtnRedo, mBtnUndo, mBtnOpenAutoPlay, mBtnSize, mBtnErase, mBtnChangePaint, mBtnSetting, mBtnColorChange, mBtnZoom;
    private ImageButton mBtnOpacityAdd, mBtnOpacityDecrease;
    private SeekBar mSeekbarOpacity;
    private FullScreenDialog mFullScreenDialog;
    private LinearLayout mLinearLayoutPaintSelect;
    private int miPrivacyType;
    private SharedPreferences mPrefs;
    private MenuPopup mMenuPopup;
    private SizePopup mSeekbarPopup;
    private ColorPopup mColorPopup;
    private boolean mbColorSwitch = false;
    private boolean mbHint = false;
    private int mCurrentBrushId = 3, mTempBrushId; //default brush

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paint);
        mPrefs = getSharedPreferences(GlobalVariable.MEMBER_PREFS, MODE_PRIVATE);
        //set View
        mbHint = mPrefs.getBoolean("zoomhint", false);
        mLinearLayoutPaintSelect = findViewById(R.id.linearLayout_paint_select);
        mBtnZoom = (ImageButton) findViewById(R.id.btn_paint_zoom);
        mBtnChangePaint = (ImageButton) findViewById(R.id.imgBtn_paint_change);
        mBtnRedo = (ImageButton) findViewById(R.id.imgBtn_paint_redo);
        mBtnUndo = (ImageButton) findViewById(R.id.imgBtn_paint_undo);
        mBtnOpenAutoPlay = (ImageButton) findViewById(R.id.imgBtn_paint_open_autoplay);
        mBtnSize = findViewById(R.id.imgBtn_paint_size);
        mBtnColorChange = findViewById(R.id.imgBtn_paint_colorpicker);
        mBtnErase = findViewById(R.id.imgBtn_paint_erase);
        mBtnSetting = (ImageButton) findViewById(R.id.imgBtn_paint_setting);
        mBtnOpacityAdd = (ImageButton) findViewById(R.id.imgBtn_paint_opacity_add);
        mBtnOpacityDecrease = (ImageButton) findViewById(R.id.imgBtn_paint_opacity_decrease);
        mSeekbarOpacity = (SeekBar) findViewById(R.id.seekbar_paint_opacity);
        mMenuPopup = new MenuPopup(this, this);
        mColorPopup = new ColorPopup(this, this);
        setOnClick();
        //Paint initTwitter & View
        mPaintView = new PaintView(this);
        mFrameLayoutFreePaint = (FrameLayout) findViewById(R.id.frameLayout_freepaint);
        mFrameLayoutFreePaint.addView(mPaintView);

        //********Init Brush*******
        mSeekbarOpacity.setProgress(100);
        mPaintView.initDefaultBrush(Brushes.get(getApplicationContext())[mCurrentBrushId]);
        mSeekbarPopup = new SizePopup(this, this, (int) mPaintView.getBrush().getMaxSize(), (int) mPaintView.getBrush().getMinSize());
        mPaintView.setDrawingAlpha(100 / 100.0f);
        defaultColor();
        mPaintView.onCheckSketch();
    }

    private void defaultColor() {
        ArrayList<ColorBean> colorsList;
        SharedPreferences pref = getSharedPreferences("colors", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = pref.getString("colorsInfo", "");
        if (!json.isEmpty()) {
            colorsList = gson.fromJson(json, new TypeToken<ArrayList<ColorBean>>() {
            }.getType());
            for (int i = 0; i < colorsList.size(); i++) {
                if (colorsList.get(i).isSelect()) {
                    onColorSelect(colorsList.get(i).getColor());
                }
            }
        }
    }

    //產生預覽圖&上傳
    private void publishWorkEdit() {
        //上傳要關掉格線
        mPaintView.setMiGridCol(0);
        mPaintView.saveTempBdw();
        mFullScreenDialog = new FullScreenDialog(this, R.layout.dialog_paint_save);
        final EditText workName = (EditText) mFullScreenDialog.findViewById(R.id.paint_save_work_name);
        final EditText workDescription = (EditText) mFullScreenDialog.findViewById(R.id.paint_save_work_description);
        ImageView workPreview = mFullScreenDialog.findViewById(R.id.save_paint_preview);
        Button saveWork = (Button) mFullScreenDialog.findViewById(R.id.btn_save_paint_save);
        ImageButton saveCancel = (ImageButton) mFullScreenDialog.findViewById(R.id.btn_save_paint_back);
        Spinner privacyTypes = (Spinner) mFullScreenDialog.findViewById(R.id.paint_save_work_privacytype);
        Bitmap temp = mPaintView.getDrawingCache(true);
        workPreview.setImageBitmap(temp);
        //設定公開權限與預設值
        ArrayAdapter<CharSequence> nAdapter = ArrayAdapter.createFromResource(
                this, R.array.privacies, R.layout.item_spinner);
        nAdapter.setDropDownViewResource(R.layout.item_spinner_dropdown);
        privacyTypes.setAdapter(nAdapter);
        //預設值
        privacyTypes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                miPrivacyType = position + 1;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        saveWork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!workName.getText().toString().isEmpty() && !workDescription.getText().toString().isEmpty()) {
                    try {
                        JSONObject json = new JSONObject();
                        json.put("ui", mPrefs.getString(GlobalVariable.API_UID, "null"));
                        json.put("lk", mPrefs.getString(GlobalVariable.API_TOKEN, "null"));
                        json.put("dt", GlobalVariable.LOGIN_PLATFORM);
                        json.put("ac", 1); // 1 = add , 2 = update
                        json.put("privacyType", miPrivacyType);
                        json.put("title", workName.getText().toString());
                        json.put("description", workDescription.getText().toString());
                        json.put("languageId", 2);
                        Log.d("LOGIN JSON: ", json.toString());
                        fileUpload(json, GlobalVariable.API_LINK_WORK_SAVE);
                        mFullScreenDialog.dismiss();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(PaintActivity.this, R.string.paint_save_need_name, Toast.LENGTH_SHORT).show();
                }
            }
        });

        saveCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mFullScreenDialog.dismiss();
            }
        });
        mFullScreenDialog.show();
    }

    //獲得檔案wid , 取得後上傳檔案
    public void fileUpload(JSONObject json, String url) {
        OkHttpClient okHttpClient = OkHttpUtil.getInstance();
        RequestBody body = FormBody.create(ConnectJson.MEDIA_TYPE_JSON_UTF8, json.toString());
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.createToastWindow(PaintActivity.this, "與伺服器連接失敗", PxDpConvert.getSystemHight(getApplicationContext()) / 4);
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    JSONObject responseJSON = new JSONObject(response.body().string());
                    if (responseJSON.getInt("res") == 1 && responseJSON.get("wid") != null) {
                        final int uploadWid = Integer.parseInt(responseJSON.get("wid").toString());
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                uploadFile(1, uploadWid);
                                uploadFile(2, uploadWid);
                            }
                        });
                    }
                    Log.d("RESPONSE", responseJSON.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    //上傳檔案
    public void uploadFile(final int type, int wid) {
        OkHttpClient okHttpClient = OkHttpUtil.getInstance();
        MultipartBody.Builder bodyBuilder = new MultipartBody.Builder();
        bodyBuilder.setType(MultipartBody.FORM)
                .addFormDataPart("ui", mPrefs.getString(GlobalVariable.API_UID, "null"))
                .addFormDataPart("lk", mPrefs.getString(GlobalVariable.API_TOKEN, "null"))
                .addFormDataPart("dt", GlobalVariable.LOGIN_PLATFORM)
                .addFormDataPart("fn", "1")
                .addFormDataPart("wid", String.valueOf(wid))
                .addFormDataPart("ftype", String.valueOf(type));
        switch (type) {
            //上傳圖片
            case 1:
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                mPaintView.getDrawingCache().compress(Bitmap.CompressFormat.PNG, 100, bos); //bm is the bitmap object
                bodyBuilder.addPart(Headers.of("Content-Disposition", "form-data; name=\"file\";filename=\"file.png\""), RequestBody.create(MediaType.parse("image/png"), bos.toByteArray()));
                break;
            //上傳BDW檔案
            case 2:
                bodyBuilder.addPart(Headers.of("Content-Disposition", "form-data; name=\"file\";filename=\"file.bdw\""), RequestBody.create(MediaType.parse("application/octet-stream"), mPaintView.mFileBDW));
                break;
        }

        Request request = new Request.Builder()
                .url(GlobalVariable.API_LINK_WORK_UPLOAD)
                .post(bodyBuilder.build())
                .build();

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("Upload File", "Fail");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    final JSONObject responseJSON = new JSONObject(response.body().string());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                if (responseJSON.getInt("res") == 1) {
                                    if (type == 1) {
                                        Log.d("上傳圖片", "成功");
                                    } else {
                                        Log.d("上傳BDW", "成功");
                                        ToastUtil.createToastIsCheck(getApplicationContext(), "發佈成功", true, 0);
                                    }
                                } else {
                                    ToastUtil.createToastIsCheck(getApplicationContext(), "發佈失敗", false, 0);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    //當切換成選顏色模式，畫面修改
    @Override
    public void onColorSelect(int color) {
        this.mBtnColorChange.setColorFilter(color);
        mPaintView.setDrawingColor(color);
    }

    @Override
    public void onClickOpenColorPick() {
        if (mbColorSwitch) {
            mColorPopup.dismiss();
            mColorPopup.showAtLocation(mPaintView, Gravity.CENTER, 0, mPaintView.getHeight() / 2);
            mbColorSwitch = false;
        } else {
            mColorPopup.dismiss();
            mColorPopup.toggleColorPick(true);
            mColorPopup.showAtLocation(mPaintView, Gravity.CENTER, 0, 0);
            mbColorSwitch = true;
        }

    }

    //設定各個按鍵
    public void setOnClick() {
        mBtnOpacityAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int progress = mSeekbarOpacity.getProgress() + 1;
                mSeekbarOpacity.setProgress(progress);
                mPaintView.setDrawingAlpha(progress / 100f);
                ToastUtil.createToastWindow(PaintActivity.this, progress + "%", 0);
            }
        });

        mBtnOpacityDecrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int progress = mSeekbarOpacity.getProgress() - 1;
                mSeekbarOpacity.setProgress(progress);
                mPaintView.setDrawingAlpha(progress / 100f);
                ToastUtil.createToastWindow(PaintActivity.this, progress + "%", 0);
            }
        });

        mSeekbarOpacity.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    ToastUtil.createToastWindow(PaintActivity.this, progress + "%", 0);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                ToastUtil.createToastWindow(PaintActivity.this, seekBar.getProgress() + "%", 0);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mPaintView.setDrawingAlpha(seekBar.getProgress() / 100f);
            }
        });

        mBtnColorChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mColorPopup.showAtLocation(mPaintView, Gravity.CENTER, 0, mPaintView.getHeight() / 2);
            }
        });

        mBtnErase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mPaintView.getBrush().isEraser) {
                    mTempBrushId =mPaintView.getBrush().id;
                    setBrush(9);
                    ((ImageButton) findViewById(R.id.imgBtn_paint_erase)).setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.Red));
                } else {
                    recoveryPaint();
                }
            }
        });

        mBtnErase.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mSeekbarPopup.changeProgress((int) (mPaintView.getDrawingSize()), (int) mPaintView.getBrush().getMaxSize(), (int) mPaintView.getBrush().getMinSize());
                mSeekbarPopup.showPopupWindow(v);
                return false;
            }
        });

        mBtnSize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSeekbarPopup.changeProgress((int) (mPaintView.getDrawingSize()), (int) mPaintView.getBrush().getMaxSize(), (int) mPaintView.getBrush().getMinSize());
                mSeekbarPopup.showPopupWindow(v);
            }
        });

        mBtnSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMenuPopup.showPopupWindow(v);
            }
        });

        mBtnZoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mPaintView.mbZoomMode) {
                    mPaintView.mbZoomMode = true;
                    mBtnZoom.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.zoom_down_icon));
                    ControlStateBtn();
                    if (!mbHint) {
                        mbHint = true;
                        mPrefs.edit().putBoolean("zoomhint", true).apply();
                        final FullScreenDialog dialog = new FullScreenDialog(PaintActivity.this, R.layout.item_hint_zoom);
                        ConstraintLayout layout = dialog.findViewById(R.id.ll_hint_zoom);
                        layout.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });
                        dialog.show();
                    }
                } else {
                    mPaintView.mbZoomMode = false;
                    mBtnZoom.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.zoom_up_icon));
                    ControlStateBtn();
                }
            }
        });

        mBtnZoom.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                //myView.layout(0, 0, myView.getWidth(), myView.getHeight());
                mPaintView.setTranslationX(0);
                mPaintView.setTranslationY(0);
                mPaintView.setScaleX(1);
                mPaintView.setScaleY(1);
                return true;
            }
        });


        mBtnOpenAutoPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mPaintView.mListTagPoint.size() > 0) {
                    if (mPaintView.saveTempBdw()) {
                        Intent intent = new Intent();
                        intent.setClass(getApplication(), PaintPlayActivity.class);
                        startActivity(intent);
                    }
                } else {
                    ToastUtil.createToastWindow(PaintActivity.this, getString(R.string.paint_need_draw), 0);
                }

            }
        });

        mBtnRedo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPaintView.onClickRedo();
            }
        });

        mBtnUndo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPaintView.onClickUndo();
            }
        });

        findViewById(R.id.imgBtn_paint_clear).setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                final FullScreenDialog dialog = new FullScreenDialog(PaintActivity.this, R.layout.dialog_paint_clean);
                Button btnClean = dialog.findViewById(R.id.btn_paint_back_clean);
                Button btnCancel = dialog.findViewById(R.id.btn_paint_back_cancel);
                btnClean.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mPaintView.mFileBDW.delete() && mPaintView.mFilePNG.delete()) {
                            ToastUtil.createToastWindow(PaintActivity.this, getString(R.string.paint_delete_sketch), 0);
                        }
                        mFrameLayoutFreePaint.removeAllViews();
                        Brush brush = mPaintView.getBrush();
                        int color = mPaintView.getDrawingColor();
                        float brusnSize = mPaintView.getDrawingScaledSize();
                        mPaintView = new PaintView(getApplicationContext());
                        mPaintView.initDefaultBrush(brush);
                        mPaintView.setBrush(brush);
                        mPaintView.setDrawingColor(color);
                        mPaintView.setDrawingScaledSize(brusnSize);
                        mFrameLayoutFreePaint.addView(mPaintView);
                        dialog.dismiss();
                    }
                });

                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                dialog.show();
            }
        });

        findViewById(R.id.imgBtn_paint_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPaintView.mListTagPoint.size() > 0) {
                    publishWorkEdit();
                } else {
                    ToastUtil.createToastWindow(PaintActivity.this, getString(R.string.paint_need_draw), 0);
                }
            }
        });

        mBtnChangePaint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mLinearLayoutPaintSelect.getVisibility() == View.INVISIBLE) {
                    mBtnZoom.setVisibility(View.INVISIBLE);
                    mLinearLayoutPaintSelect.setVisibility(View.VISIBLE);
                    setPaintFoucs();
                } else {
                    mLinearLayoutPaintSelect.setVisibility(View.INVISIBLE);
                    mBtnZoom.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    //切換筆
    public void selectPaint(View view) {
        if (mPaintView.getBrush().isEraser) {
            recoveryPaint();
        }
        switch (view.getId()) {
            case R.id.imgBtn_paint_left:
                if (mPaintView.miPaintNum <= 5 && mPaintView.miPaintNum > 1) {
                    mPaintView.miPaintNum--;
                }
                break;
            case R.id.imgBtn_paint_type1:
                setBrush(18);
                mPaintView.miPaintNum = 1;
                mSeekbarOpacity.setProgress((int) mPaintView.getDrawingAlpha() * 100);
                break;
            case R.id.imgBtn_paint_type2:
                setBrush(6);
                mPaintView.miPaintNum = 2;
                mSeekbarOpacity.setProgress((int) mPaintView.getDrawingAlpha() * 100);
                break;
            case R.id.imgBtn_paint_type3:
                setBrush(3);
                mPaintView.miPaintNum = 3;
                mSeekbarOpacity.setProgress((int) mPaintView.getDrawingAlpha() * 100);
                break;
            case R.id.imgBtn_paint_type4:
                setBrush(13);
                mPaintView.miPaintNum = 4;
                mSeekbarOpacity.setProgress((int) mPaintView.getDrawingAlpha() * 100);
                break;
            case R.id.imgBtn_paint_type5:
                setBrush(11);
                mPaintView.miPaintNum = 5;
                mSeekbarOpacity.setProgress((int) mPaintView.getDrawingAlpha() * 100);
                break;
            case R.id.imgBtn_paint_right:
                if (mPaintView.miPaintNum < 5 && mPaintView.miPaintNum >= 1) {
                    mPaintView.miPaintNum++;
                }
                break;
        }
        setPaintFoucs();
    }

    //顯示當前的筆
    public void setPaintFoucs() {
        findViewById(R.id.imgBtn_paint_type1).setSelected(false);
        findViewById(R.id.imgBtn_paint_type2).setSelected(false);
        findViewById(R.id.imgBtn_paint_type3).setSelected(false);
        findViewById(R.id.imgBtn_paint_type4).setSelected(false);
        findViewById(R.id.imgBtn_paint_type5).setSelected(false);

        switch (mPaintView.miPaintNum) {
            case 1:
                findViewById(R.id.imgBtn_paint_type1).setSelected(true);
                mBtnChangePaint.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.draw_pen_ic_1));
                break;
            case 2:
                findViewById(R.id.imgBtn_paint_type2).setSelected(true);
                mBtnChangePaint.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.draw_pen_ic_2));
                break;
            case 3:
                findViewById(R.id.imgBtn_paint_type3).setSelected(true);
                mBtnChangePaint.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.draw_pen_ic_3));
                break;
            case 4:
                findViewById(R.id.imgBtn_paint_type4).setSelected(true);
                mBtnChangePaint.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.draw_pen_ic_4));
                break;
            case 5:
                findViewById(R.id.imgBtn_paint_type5).setSelected(true);
                mBtnChangePaint.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.draw_pen_ic_5));
                break;
        }
    }

    public void recoveryPaint() {
        setBrush(mTempBrushId);
        ((ImageButton) findViewById(R.id.imgBtn_paint_erase)).setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.Transparent));
    }

    public void ControlStateBtn() {
        mBtnRedo.setClickable(!mPaintView.mbZoomMode);
        mBtnUndo.setClickable(!mPaintView.mbZoomMode);
        mBtnSize.setClickable(!mPaintView.mbZoomMode);
        mBtnOpenAutoPlay.setClickable(!mPaintView.mbZoomMode);
        findViewById(R.id.imgBtn_paint_save).setClickable(!mPaintView.mbZoomMode);
        findViewById(R.id.imgBtn_paint_erase).setClickable(!mPaintView.mbZoomMode);
        findViewById(R.id.imgBtn_paint_clear).setClickable(!mPaintView.mbZoomMode);
        findViewById(R.id.imgBtn_paint_colorpicker).setClickable(!mPaintView.mbZoomMode);
        findViewById(R.id.imgBtn_paint_back).setClickable(!mPaintView.mbZoomMode);
    }


    public void callSaveDialog(int num) {
        final FullScreenDialog dialog=new FullScreenDialog(this,R.layout.dialog_paint_sketch);
        FrameLayout layout=dialog.findViewById(R.id.frameLayout_paint_sketch);
        TextView tvTitle=dialog.findViewById(R.id.textView_sketch_title);
        Button btnYes=dialog.findViewById(R.id.btn_paint_sketch_yes);
        Button btnNo=dialog.findViewById(R.id.btn_paint_sketch_no);
        Button btnCancel=dialog.findViewById(R.id.btn_paint_sketch_cancel);
        switch (num) {
            case 0:
                tvTitle.setText(R.string.paint_sketch_save_title);
                break;
            case 1:
                tvTitle.setText(R.string.paint_sketch_save_add_title);
                break;
        }
        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean result = mPaintView.saveTempPhotoAndBdw();
                Toast.makeText(PaintActivity.this, "草稿儲存成功", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                if (result) finish();
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                finish();
            }
        });
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public void onBackMethod() {
        System.out.println(mPaintView.mFileBDW.exists());
        if (mPaintView.mListTagPoint.isEmpty()) {
            finish();
        } else if (mPaintView.mListTagPoint.size() != 0 && mPaintView.mBDWReader.m_tagArray == null) {
            callSaveDialog(0);
        } else if (mPaintView.mFileBDW.exists() && mPaintView.mFileBDW.length() > 0) {
            if (mPaintView.mListTagPoint.size() != mPaintView.mBDWReader.m_tagArray.size()) {
                //是否更新草稿??
                callSaveDialog(1);
            } else {
                //沒變化就離開
                finish();
            }
        } else {
            finish();
        }
    }

    public void back(View view) {
        onBackMethod();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    @Override
    public void onBackPressed() {
        onBackMethod();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        System.out.println("onRestart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        System.out.println("onResume");
    }

    //*******Brush **********

    private void setBrush(int brushID) {
        int tempSize = mPaintView.getBrush().getSize();
        Brush brush = Brushes.get(getApplicationContext())[brushID];
        mPaintView.setBrush(brush);
        mPaintView.setDrawingSize(tempSize);
    }

    //設定選單
    @Override
    public void onPopupClick(int item) {
        switch (item) {
            case MenuPopup.PAINT_SETTING_GRID:
                openGridScreen();
                break;
            case MenuPopup.PAINT_SETTING_BG_COLOR:
                mPaintView.setDrawingBgColorTag(mPaintView.getDrawingColor());
                mMenuPopup.dismiss();
                break;
            case MenuPopup.PAINT_SETTING_SAVE:
                savePicture();
                mMenuPopup.dismiss();
                break;
            case MenuPopup.PAINT_SETTING_EXTRA:
                saveBdw();
                break;
        }
    }

    public void saveBdw() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss", Locale.TAIWAN);
        Date curDate = new Date(System.currentTimeMillis());
        String filename = formatter.format(curDate);
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File vPath = new File(Environment.getExternalStorageDirectory() + "/Screenshots");
            if (!vPath.exists()) vPath.mkdirs();
            File bdwfile = new File(Environment.getExternalStorageDirectory() + "/Screenshots/" + "BDW" + filename + ".bdw");
            BDWFileWriter writer = new BDWFileWriter();
            if (writer.WriteToFile(mPaintView.mListTagPoint, bdwfile.getAbsolutePath())) {
                ToastUtil.createToastWindow(PaintActivity.this, "BDW儲存成功，檔案位於Screenshots資料夾。", PxDpConvert.getSystemHight(this) / 4);
            }
            mMenuPopup.dismiss();
        }
    }

    public void savePicture() {
        //存檔要關掉格線
        mPaintView.setMiGridCol(0);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss", Locale.TAIWAN);
        Date curDate = new Date(System.currentTimeMillis());
        String filename = formatter.format(curDate);
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            try {
                File vPath = new File(Environment.getExternalStorageDirectory() + "/Screenshots");
                if (!vPath.exists()) vPath.mkdirs();
                File pngfile = new File(Environment.getExternalStorageDirectory() + "/Screenshots/" + "BDW" + filename + ".png");
                FileOutputStream fos = new FileOutputStream(pngfile);
                mPaintView.getDrawingCache().compress(Bitmap.CompressFormat.PNG, 100, fos);
                fos.close();
                ToastUtil.createToastWindow(PaintActivity.this, "儲存成功，檔案位於Screenshots資料夾。", PxDpConvert.getSystemHight(this) / 4);
                mMenuPopup.dismiss();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (fromUser) {
            mPaintView.setDrawingSize(progress);
            ToastUtil.createToastWindowSize(this, mPaintView.getDrawingSize());
        }
    }

    @Override
    public void onSizeAdd(int progress) {
        mPaintView.setDrawingSize(progress);
        ToastUtil.createToastWindowSize(this, mPaintView.getDrawingSize());
    }

    @Override
    public void onSizeDecrease(int progress) {
        mPaintView.setDrawingSize(progress);
        ToastUtil.createToastWindowSize(this, mPaintView.getDrawingSize());
    }

    @Override
    public void onSetSize(int progress) {
        mPaintView.setDrawingSize(progress);
        ToastUtil.createToastWindowSize(this, mPaintView.getDrawingSize());
    }

    private void openGridScreen() {
        final FullScreenDialog gridDialog = new FullScreenDialog(PaintActivity.this, R.layout.dialog_paint_grid);
        Button gridNone = gridDialog.findViewById(R.id.paint_grid_none);
        Button grid3 = gridDialog.findViewById(R.id.paint_grid_3);
        Button grid6 = gridDialog.findViewById(R.id.paint_grid_6);
        Button grid10 = gridDialog.findViewById(R.id.paint_grid_10);
        Button grid20 = gridDialog.findViewById(R.id.paint_grid_20);
        Button gridCacel = gridDialog.findViewById(R.id.paint_grid_cancel);
        gridNone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPaintView.setMiGridCol(0);
                gridDialog.dismiss();
                mMenuPopup.dismiss();
            }
        });

        grid3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPaintView.setMiGridCol(3);
                gridDialog.dismiss();
                mMenuPopup.dismiss();
            }
        });

        grid6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPaintView.setMiGridCol(6);
                gridDialog.dismiss();
                mMenuPopup.dismiss();
            }
        });

        grid10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPaintView.setMiGridCol(10);
                gridDialog.dismiss();
                mMenuPopup.dismiss();
            }
        });

        grid20.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPaintView.setMiGridCol(20);
                gridDialog.dismiss();
                mMenuPopup.dismiss();
            }
        });

        gridCacel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gridDialog.dismiss();
                mMenuPopup.dismiss();
            }
        });

        gridDialog.findViewById(R.id.relativeLayout_works_extra).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gridDialog.dismiss();
                mMenuPopup.dismiss();
            }
        });

        gridDialog.show();
    }
}