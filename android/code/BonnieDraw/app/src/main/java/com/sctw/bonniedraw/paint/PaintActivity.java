package com.sctw.bonniedraw.paint;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Toast;

import com.sctw.bonniedraw.R;
import com.sctw.bonniedraw.utility.ConnectJson;
import com.sctw.bonniedraw.utility.FullScreenDialog;
import com.sctw.bonniedraw.utility.GlobalVariable;
import com.sctw.bonniedraw.utility.TSnackbarCall;
import com.sctw.bonniedraw.utility.Thumbnail;
import com.sctw.bonniedraw.widget.MenuPopup;
import com.sctw.bonniedraw.widget.SeekbarPopup;
import com.sctw.bonniedraw.widget.SizePopup;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

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
public class PaintActivity extends AppCompatActivity implements MenuPopup.MenuPopupOnClick, SeekbarPopup.OnSeekChange {
    private PaintView mPaintView;
    private FrameLayout mFrameLayoutFreePaint;
    private ImageButton mBtnRedo, mBtnUndo, mBtnOpenAutoPlay, mBtnSize, mBtnChangePaint, mBtnSetting;
    private Button mBtnZoom;
    private FullScreenDialog mFullScreenDialog;
    private LinearLayout mLinearLayoutPaintSelect;
    private int miPrivacyType;
    private SharedPreferences mPrefs;
    private MenuPopup mMenuPopup;
    private SeekbarPopup mSeekbarPopup;
    private SizePopup mSizePopup;

    private int mCurrentBrushId = 0; //default brush

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paint);
        mPrefs = getSharedPreferences(GlobalVariable.MEMBER_PREFS, MODE_PRIVATE);
        //set View
        mLinearLayoutPaintSelect = findViewById(R.id.linearLayout_paint_select);
        mBtnZoom = (Button) findViewById(R.id.btn_paint_zoom);
        mBtnChangePaint = (ImageButton) findViewById(R.id.imgBtn_paint_change);
        mBtnRedo = (ImageButton) findViewById(R.id.imgBtn_paint_redo);
        mBtnUndo = (ImageButton) findViewById(R.id.imgBtn_paint_undo);
        mBtnOpenAutoPlay = (ImageButton) findViewById(R.id.imgBtn_paint_open_autoplay);
        mBtnSize = (ImageButton) findViewById(R.id.imgBtn_paint_size);
        mBtnSetting = (ImageButton) findViewById(R.id.imgBtn_paint_setting);
        mMenuPopup = new MenuPopup(this, this);
        mSeekbarPopup = new SeekbarPopup(this, this);
        mSizePopup=new SizePopup(this);
        mSizePopup.setPopupGravity(Gravity.CENTER);
        setOnclick();

        //Paint init & View
        mPaintView = new PaintView(this);
        mPaintView.checkSketch();
        mFrameLayoutFreePaint = (FrameLayout) findViewById(R.id.frameLayout_freepaint);
        mFrameLayoutFreePaint.addView(mPaintView);

        //********Init Brush*******
        mPaintView.initDefaultBrush(Brushes.get(getApplicationContext())[mCurrentBrushId]);
        mPaintView.setDrawingScaledSize(30/100.f);
        mPaintView.setDrawingAlpha(1);
    }

    //產生預覽圖&上傳
    private void savePictureEdit() {
        mFullScreenDialog = new FullScreenDialog(this, R.layout.paint_save_dialog);
        final EditText workName = (EditText) mFullScreenDialog.findViewById(R.id.paint_save_work_name);
        final EditText workDescription = (EditText) mFullScreenDialog.findViewById(R.id.paint_save_work_description);
        ImageView workPreview = mFullScreenDialog.findViewById(R.id.save_paint_preview);
        Button saveWork = (Button) mFullScreenDialog.findViewById(R.id.btn_save_paint_save);
        ImageButton saveCancel = (ImageButton) mFullScreenDialog.findViewById(R.id.btn_save_paint_back);
        RadioGroup privacyTypes = (RadioGroup) mFullScreenDialog.findViewById(R.id.paint_save_work_privacytype);

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
        privacyTypes.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                switch (i) {
                    case R.id.work_privacytype_public:
                        miPrivacyType = 1;
                        break;
                    case R.id.work_privacytype_private:
                        miPrivacyType = 2;
                        break;
                    case R.id.work_privacytype_close:
                        miPrivacyType = 3;
                        break;
                }
            }
        });
        privacyTypes.check(R.id.work_privacytype_public);

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
        OkHttpClient okHttpClient = new OkHttpClient();
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
        OkHttpClient okHttpClient = new OkHttpClient();
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

    //設定各個按鍵
    public void setOnclick() {
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
                    view.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.Red));
                    playStateBtn();

                } else {
                    mPaintView.mbZoomMode = false;
                    view.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.Transparent));
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
                if (mPaintView.mFileBDW.delete() && mPaintView.mFilePNG.delete()) {
                    TSnackbarCall.showTSnackbar(findViewById(R.id.coordinatorLayout_activity_paint), getString(R.string.paint_delete_sketch));
                }
                mFrameLayoutFreePaint.removeAllViews();
                Brush brush = mPaintView.getBrush();
                int brushNum = mPaintView.miPaintNum;
                float brusnSize = mPaintView.getDrawingScaledSize();
                mPaintView = new PaintView(getApplicationContext());
                mPaintView.initDefaultBrush(brush);
                setBrush(brushNum);
                mPaintView.setDrawingScaledSize(brusnSize);
                mFrameLayoutFreePaint.addView(mPaintView);
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

    public void erase_mode(View view) {
        if (!mPaintView.getBrush().isEraser) {
            mPaintView.getBrush().isEraser = true;
            ((ImageButton) findViewById(R.id.imgBtn_paint_erase)).setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.Red));
        } else {
            recoveryPaint();
        }
        setPaintFoucs();
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
    private void setColor(int color) {
        //this.mColorButton.setColor(getColorWithAlpha(color, this.mPaintView.getDrawingAlpha()));
        this.mPaintView.setDrawingColor(color);
    }

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
                Toast.makeText(this, "還沒實作儲存", Toast.LENGTH_SHORT).show();
                break;
            case MenuPopup.PAINT_SETTING_EXTRA:
                Toast.makeText(this, "還沒實作額外按鈕", Toast.LENGTH_SHORT).show();
                break;
        }
    }


    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (fromUser) {
            mPaintView.setDrawingScaledSize(progress / 100.f);
            mSizePopup.setConvertedValue(mPaintView.getBrush().getSizeFromScaledSize(progress/100.0f));
        }
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        mSizePopup.dismiss();
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        mSizePopup.showPopupWindow();
        mSizePopup.setConvertedValue(mPaintView.getBrush().getSizeFromScaledSize(seekBar.getProgress()/100.0f));
    }

    private void openGridScreen() {
        final FullScreenDialog gridDialog = new FullScreenDialog(PaintActivity.this, R.layout.paint_grid_dialog);
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
