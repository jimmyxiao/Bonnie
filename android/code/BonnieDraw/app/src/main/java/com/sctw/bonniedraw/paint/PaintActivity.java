package com.sctw.bonniedraw.paint;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
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
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sctw.bonniedraw.R;
import com.sctw.bonniedraw.colorpick.ColorBean;
import com.sctw.bonniedraw.utility.ConnectJson;
import com.sctw.bonniedraw.utility.FullScreenDialog;
import com.sctw.bonniedraw.utility.GlobalVariable;
import com.sctw.bonniedraw.utility.OkHttpUtil;
import com.sctw.bonniedraw.utility.TSnackbarCall;
import com.sctw.bonniedraw.utility.Thumbnail;
import com.sctw.bonniedraw.widget.ColorPopup;
import com.sctw.bonniedraw.widget.MenuPopup;
import com.sctw.bonniedraw.widget.OpacityPopup;
import com.sctw.bonniedraw.widget.SeekbarPopup;
import com.sctw.bonniedraw.widget.SizePopup;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

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
public class PaintActivity extends AppCompatActivity implements MenuPopup.MenuPopupOnClick, SeekbarPopup.OnSeekChange, ColorPopup.OnPopupColorPick {
    private PaintView mPaintView;
    private FrameLayout mFrameLayoutFreePaint;
    private ImageButton mBtnRedo, mBtnUndo, mBtnOpenAutoPlay, mBtnSize, mBtnErase, mBtnChangePaint, mBtnSetting, mBtnColorChange,mBtnZoom;
    private ImageButton mBtnOpacityAdd, mBtnOpacityDecrease;
    private SeekBar mSeekbarOpacity;
    private FullScreenDialog mFullScreenDialog;
    private LinearLayout mLinearLayoutPaintSelect;
    private int miPrivacyType = 1;
    private SharedPreferences mPrefs;
    private MenuPopup mMenuPopup;
    private SeekbarPopup mSeekbarPopup;
    private SizePopup mSizePopup;
    private ColorPopup mColorPopup;
    private OpacityPopup mOpacityPopup;
    private boolean mbColorSwitch = false;

    private int mCurrentBrushId = 0; //default brush

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paint);
        mPrefs = getSharedPreferences(GlobalVariable.MEMBER_PREFS, MODE_PRIVATE);
        //set View
        mLinearLayoutPaintSelect = findViewById(R.id.linearLayout_paint_select);
        mBtnZoom = (ImageButton) findViewById(R.id.btn_paint_zoom);
        mBtnChangePaint = (ImageButton) findViewById(R.id.imgBtn_paint_change);
        mBtnRedo = (ImageButton) findViewById(R.id.imgBtn_paint_redo);
        mBtnUndo = (ImageButton) findViewById(R.id.imgBtn_paint_undo);
        mBtnOpenAutoPlay = (ImageButton) findViewById(R.id.imgBtn_paint_open_autoplay);
        mBtnSize = (ImageButton) findViewById(R.id.imgBtn_paint_size);
        mBtnColorChange = findViewById(R.id.imgBtn_paint_colorpicker);
        mBtnErase = findViewById(R.id.imgBtn_paint_erase);
        mBtnSetting = (ImageButton) findViewById(R.id.imgBtn_paint_setting);
        mBtnOpacityAdd = (ImageButton) findViewById(R.id.imgBtn_paint_opacity_add);
        mBtnOpacityDecrease = (ImageButton) findViewById(R.id.imgBtn_paint_opacity_decrease);
        mSeekbarOpacity = (SeekBar) findViewById(R.id.seekbar_paint_opacity);
        mMenuPopup = new MenuPopup(this, this);
        mSeekbarPopup = new SeekbarPopup(this, this);
        mSizePopup = new SizePopup(this);
        mColorPopup = new ColorPopup(this, this);
        mOpacityPopup = new OpacityPopup(this);

        setOnClick();
        //Paint init & View
        mPaintView = new PaintView(this);
        mPaintView.checkSketch();
        mFrameLayoutFreePaint = (FrameLayout) findViewById(R.id.frameLayout_freepaint);
        mFrameLayoutFreePaint.addView(mPaintView);

        //********Init Brush*******
        mSeekbarOpacity.setProgress(100);

        mPaintView.initDefaultBrush(Brushes.get(getApplicationContext())[mCurrentBrushId]);
        mPaintView.setDrawingScaledSize(30 / 100.f);
        mPaintView.setDrawingAlpha(100 / 100.0f);
        defaultColor();
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
    private void savePictureEdit() {
        mFullScreenDialog = new FullScreenDialog(this, R.layout.dialog_paint_save);
        final EditText workName = (EditText) mFullScreenDialog.findViewById(R.id.paint_save_work_name);
        final EditText workDescription = (EditText) mFullScreenDialog.findViewById(R.id.paint_save_work_description);
        ImageView workPreview = mFullScreenDialog.findViewById(R.id.save_paint_preview);
        Button saveWork = (Button) mFullScreenDialog.findViewById(R.id.btn_save_paint_save);
        ImageButton saveCancel = (ImageButton) mFullScreenDialog.findViewById(R.id.btn_save_paint_back);
        Spinner privacyTypes = (Spinner) mFullScreenDialog.findViewById(R.id.paint_save_work_privacytype);

        File pngfile = null;
        try {
            File vPath = new File(getFilesDir() + "/bonniedraw");
            if (!vPath.exists()) vPath.mkdirs();
            pngfile = new File(vPath + "thumbnail.png");
            FileOutputStream fos = new FileOutputStream(pngfile);
            mPaintView.getDrawingCache().compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
            workPreview.setImageBitmap(Thumbnail.getBitmap(getApplicationContext(), Uri.fromFile(pngfile)));

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (pngfile != null && pngfile.exists()) pngfile.delete();
        }

        //設定公開權限與預設值
        ArrayAdapter<CharSequence> nAdapter = ArrayAdapter.createFromResource(
                this, R.array.privacies, android.R.layout.simple_spinner_item);
        privacyTypes.setAdapter(nAdapter);
        nAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item);
        //預設值
        privacyTypes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                miPrivacyType = position;
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
        mFullScreenDialog.getWindow().getAttributes().windowAnimations = R.style.FullScreenDialogStyle;
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
                TSnackbarCall.showTSnackbar(findViewById(R.id.coordinatorLayout_activity_paint), "與伺服器連接失敗");
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
    public void uploadFile(int type, int wid) {
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
                //mPaintView.getBitmap().compress(Bitmap.CompressFormat.PNG, 100, bos); //bm is the bitmap object
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
                    JSONObject responseJSON = new JSONObject(response.body().string());
                    if (responseJSON.getInt("res") == 1) {
                        TSnackbarCall.showTSnackbar(findViewById(R.id.coordinatorLayout_activity_paint), "上傳成功");
                    } else {
                        TSnackbarCall.showTSnackbar(findViewById(R.id.coordinatorLayout_activity_paint), "上傳失敗");
                    }
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
        mSeekbarOpacity.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mOpacityPopup.setText(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mOpacityPopup.showPopupWindow();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mPaintView.setDrawingAlpha(seekBar.getProgress() / 100f);
                mOpacityPopup.dismiss();
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
                    mPaintView.getBrush().isEraser = true;
                    ((ImageButton) findViewById(R.id.imgBtn_paint_erase)).setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.Red));
                } else {
                    recoveryPaint();
                }
            }
        });

        mBtnErase.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mSeekbarPopup.showPopupWindow(v);
                return false;
            }
        });

        mBtnSize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                    mBtnZoom.setImageDrawable(getDrawable(R.drawable.zoom_down_icon));
                    playStateBtn();

                } else {
                    mPaintView.mbZoomMode = false;
                    mBtnZoom.setImageDrawable(getDrawable(R.drawable.zoom_up_icon));
                    playStateBtn();
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
                    TSnackbarCall.showTSnackbar(findViewById(R.id.coordinatorLayout_activity_paint), getString(R.string.paint_need_draw));
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
                final FullScreenDialog dialog = new FullScreenDialog(PaintActivity.this, R.layout.dialog_paint_back);
                Button btnClean = dialog.findViewById(R.id.btn_paint_back_clean);
                Button btnCancel = dialog.findViewById(R.id.btn_paint_back_cancel);
                btnClean.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mPaintView.mFileBDW.delete() && mPaintView.mFilePNG.delete()) {
                            TSnackbarCall.showTSnackbar(findViewById(R.id.coordinatorLayout_activity_paint), getString(R.string.paint_delete_sketch));
                        }
                        mFrameLayoutFreePaint.removeAllViews();
                        Brush brush = mPaintView.getBrush();
                        float brusnSize = mPaintView.getDrawingScaledSize();
                        mPaintView = new PaintView(getApplicationContext());
                        mPaintView.initDefaultBrush(brush);
                        mPaintView.setBrush(brush);
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
                savePictureEdit();
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
        switch (view.getId()) {
            case R.id.imgBtn_paint_left:
                if (mPaintView.miPaintNum <= 4 && mPaintView.miPaintNum > 0) {
                    mPaintView.miPaintNum--;
                }
                break;
            case R.id.imgBtn_paint_type1:
                setBrush(0);
                mPaintView.miPaintNum = 0;
                break;
            case R.id.imgBtn_paint_type2:
                setBrush(1);
                mPaintView.miPaintNum = 1;
                break;
            case R.id.imgBtn_paint_type3:
                setBrush(2);
                mPaintView.miPaintNum = 2;
                break;
            case R.id.imgBtn_paint_type4:
                setBrush(3);
                mPaintView.miPaintNum = 3;
                break;
            case R.id.imgBtn_paint_type5:
                setBrush(4);
                mPaintView.miPaintNum = 4;
                break;
            case R.id.imgBtn_paint_right:
                if (mPaintView.miPaintNum < 4 && mPaintView.miPaintNum >= 0) {
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
            case 0:
                findViewById(R.id.imgBtn_paint_type1).setSelected(true);
                mBtnChangePaint.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.draw_pen_ic_1));
                break;
            case 1:
                findViewById(R.id.imgBtn_paint_type2).setSelected(true);
                mBtnChangePaint.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.draw_pen_ic_2));
                break;
            case 2:
                findViewById(R.id.imgBtn_paint_type3).setSelected(true);
                mBtnChangePaint.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.draw_pen_ic_3));
                break;
            case 3:
                findViewById(R.id.imgBtn_paint_type4).setSelected(true);
                mBtnChangePaint.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.draw_pen_ic_4));
                break;
            case 4:
                findViewById(R.id.imgBtn_paint_type5).setSelected(true);
                mBtnChangePaint.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.draw_pen_ic_5));
                break;
        }
    }

    public void recoveryPaint() {
        mPaintView.getBrush().isEraser = false;
        ((ImageButton) findViewById(R.id.imgBtn_paint_erase)).setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.Transparent));
    }

    public void playStateBtn() {
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
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        switch (num) {
            case 0:
                builder.setMessage(R.string.paint_sketch_save_title);
                break;
            case 1:
                builder.setMessage(R.string.paint_sketch_save_add_title);
                break;
        }
        builder.setPositiveButton(R.string.public_yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                boolean result = mPaintView.saveTempPhotoAndBdw();
                Toast.makeText(PaintActivity.this, "Successful", Toast.LENGTH_SHORT).show();
                dialogInterface.dismiss();
                if (result) finish();
            }
        });
        builder.setNegativeButton(R.string.public_no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                finish();
            }
        });
        builder.setNeutralButton(R.string.public_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        builder.create().show();
    }

    public void onBackMethod() {
        System.out.println(mPaintView.mFileBDW.exists());
        if (mPaintView.mListTagPoint.size() != 0 && !mPaintView.mFileBDW.exists()) {
            callSaveDialog(0);
        } else if (mPaintView.mFileBDW.exists() && mPaintView.mBDWReader.m_tagArray != null) {
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
        Brush brush = Brushes.get(getApplicationContext())[brushID];
        mPaintView.setBrush(brush);
    }

    //設定選單
    @Override
    public void onPopupClick(int item) {
        switch (item) {
            case MenuPopup.PAINT_SETTING_GRID:
                openGridScreen();
                break;
            case MenuPopup.PAINT_SETTING_BG_COLOR:
                Toast.makeText(this, "還沒實作換背景", Toast.LENGTH_SHORT).show();
                break;
            case MenuPopup.PAINT_SETTING_SAVE:
                savePicture();
                break;
            case MenuPopup.PAINT_SETTING_EXTRA:
                Toast.makeText(this, "還沒實作額外按鈕", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    public void savePicture() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        Date curDate = new Date(System.currentTimeMillis());
        String filename = formatter.format(curDate);
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            try {
                File vPath = new File(Environment.getExternalStorageDirectory() + "/bonniedraw");
                if (!vPath.exists()) vPath.mkdirs();
                File pngfile = new File(Environment.getExternalStorageDirectory() + "/bonniedraw/" + filename + ".png");
                FileOutputStream fos = new FileOutputStream(pngfile);
                mPaintView.getDrawingCache().compress(Bitmap.CompressFormat.PNG, 100, fos);
                fos.close();
                TSnackbarCall.showTSnackbar(findViewById(R.id.coordinatorLayout_activity_paint), "儲存成功，檔案位於Bonniedraw資料夾。");
                mMenuPopup.dismiss();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (fromUser) {
            mPaintView.setDrawingScaledSize(progress / 100.f);
            mSizePopup.setConvertedValue(mPaintView.getBrush().getSizeFromScaledSize(progress / 100.0f));
        }
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        mSizePopup.dismiss();
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        mSizePopup.showPopupWindow();
        mSizePopup.setConvertedValue(mPaintView.getBrush().getSizeFromScaledSize(seekBar.getProgress() / 100.0f));
    }

    private void openGridScreen() {
        final FullScreenDialog gridDialog = new FullScreenDialog(PaintActivity.this, R.layout.dialog_paint_grid);
        Button gridNone = gridDialog.findViewById(R.id.paint_grid_none);
        Button grid3 = gridDialog.findViewById(R.id.paint_grid_3);
        Button grid6 = gridDialog.findViewById(R.id.paint_grid_6);
        Button grid10 = gridDialog.findViewById(R.id.paint_grid_10);
        Button grid20 = gridDialog.findViewById(R.id.paint_grid_20);
        Button gridCacel = gridDialog.findViewById(R.id.paint_grid_cancel);
        gridDialog.getWindow().getAttributes().windowAnimations = R.style.FullScreenDialogStyle;
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
