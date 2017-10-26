package com.sctw.bonniedraw.paint;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.sctw.bonniedraw.R;
import com.sctw.bonniedraw.paintpicker.ColorPicker;
import com.sctw.bonniedraw.paintpicker.OnColorChangedListener;
import com.sctw.bonniedraw.paintpicker.OnSizeChangedListener;
import com.sctw.bonniedraw.paintpicker.SizePicker;
import com.sctw.bonniedraw.utility.ConnectJson;
import com.sctw.bonniedraw.utility.FullScreenDialog;
import com.sctw.bonniedraw.utility.GlobalVariable;
import com.sctw.bonniedraw.utility.TSnackbarCall;
import com.sctw.bonniedraw.utility.Thumbnail;

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
public class PaintActivity extends AppCompatActivity implements OnColorChangedListener, OnSizeChangedListener {
    private static final int REQUEST_EXTERNAL_STORAGE = 0;
    private PaintView mPaintView;
    private FrameLayout mFrameLayoutFreePaint;
    private Paint mPaint;
    private ImageButton mBtnRedo, mBtnUndo, mBtnGrid, mBtnOpenAutoPlay, mBtnSize, mBtnChangePaint;
    private Button mBtnZoom;
    private ColorPicker mColorPicker;
    private SizePicker mSizePicker;
    private FullScreenDialog mFullScreenDialog;
    private LinearLayout mLinearLayoutPaintSelect;
    private int miPrivacyType;
    private SharedPreferences mPrefs;

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
        mBtnGrid = (ImageButton) findViewById(R.id.imgBtn_paint_grid);
        mBtnOpenAutoPlay = (ImageButton) findViewById(R.id.imgBtn_paint_open_autoplay);
        mBtnSize = (ImageButton) findViewById(R.id.imgBtn_paint_size);
        setOnclick();
        mColorPicker = new ColorPicker(this, this, "", Color.WHITE);
        mColorPicker.getWindow().setGravity(Gravity.END);
        mColorPicker.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        mColorPicker.getWindow().getAttributes().windowAnimations = R.style.ColorPickStyle;
        mSizePicker = new SizePicker(this, this, Color.GRAY);
        mSizePicker.getWindow().setGravity(Gravity.START);
        mSizePicker.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        mSizePicker.getWindow().getAttributes().windowAnimations = R.style.ColorPickStyle;

        //Paint init & View
        mPaintView = new PaintView(this);
        mPaintView.checkSketch();
        mFrameLayoutFreePaint = (FrameLayout) findViewById(R.id.frameLayout_freepaint);
        mFrameLayoutFreePaint.addView(mPaintView);
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
            mPaintView.getBitmap().compress(Bitmap.CompressFormat.PNG, 100, fos);
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
                mPaintView.getBitmap().compress(Bitmap.CompressFormat.PNG, 100, bos); //bm is the bitmap object
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

    //選擇大小
    public void sizesPicks(View view) {
        mSizePicker.show();
    }

    @Override
    public void sizeChanged(int size) {
        mPaintView.mPaint.setStrokeWidth(size);
        float scale = size / 13.0F;
        mBtnSize.setScaleX(scale);
        mBtnSize.setScaleY(scale);
    }

    //選擇顏色
    public void colorPicks(View view) {
        mColorPicker.show();
    }

    @Override
    public void colorChanged(int color) {
        mPaintView.mPaint.setColor(color);
        findViewById(R.id.imgBtn_paint_colorpicker).setBackgroundColor(color);
        if (mPaintView.mbEraseMode) {
            recoveryPaint();
        }
    }

    //設定各個按鍵
    public void setOnclick() {
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

        mBtnGrid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
                        mPaintView.miGridCol = 0;
                        mPaintView.invalidate();
                        gridDialog.dismiss();
                    }
                });

                grid3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mPaintView.miGridCol = 3;
                        mPaintView.invalidate();
                        gridDialog.dismiss();
                    }
                });

                grid6.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mPaintView.miGridCol = 6;
                        mPaintView.invalidate();
                        gridDialog.dismiss();
                    }
                });

                grid10.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mPaintView.miGridCol = 10;
                        mPaintView.invalidate();
                        gridDialog.dismiss();
                    }
                });

                grid20.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mPaintView.miGridCol = 20;
                        mPaintView.invalidate();
                        gridDialog.dismiss();
                    }
                });

                gridCacel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        gridDialog.dismiss();
                    }
                });

                gridDialog.findViewById(R.id.relativeLayout_works_extra).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        gridDialog.dismiss();
                    }
                });

                gridDialog.show();
            }
        });

        findViewById(R.id.imgBtn_paint_clear).setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPaintView.mFileBDW.delete() && mPaintView.mFilePNG.delete()) {
                    TSnackbarCall.showTSnackbar(findViewById(R.id.coordinatorLayout_activity_paint), getString(R.string.paint_delete_sketch));
                }
                mFrameLayoutFreePaint.removeAllViews();
                mPaintView = new PaintView(getApplicationContext());
                mFrameLayoutFreePaint.addView(mPaintView);
                recoveryPaint();
                playStateBtn();
            }
        });

        findViewById(R.id.imgBtn_paint_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(PaintActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                                REQUEST_EXTERNAL_STORAGE);
                    }
                } else {
                    savePictureEdit();
                }
            }
        });

        mBtnChangePaint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mLinearLayoutPaintSelect.getVisibility() == View.INVISIBLE) {
                    mBtnZoom.setVisibility(View.INVISIBLE);
                    mLinearLayoutPaintSelect.setVisibility(View.VISIBLE);
                    setPaintFouns();
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
                mPaintView.changePaint(0);
                break;
            case R.id.imgBtn_paint_type2:
                mPaintView.changePaint(1);
                break;
            case R.id.imgBtn_paint_type3:
                mPaintView.changePaint(2);
                break;
            case R.id.imgBtn_paint_type4:
                mPaintView.changePaint(3);
                break;
            case R.id.imgBtn_paint_type5:
                mPaintView.changePaint(4);
                break;
            case R.id.imgBtn_paint_right:
                if (mPaintView.miPaintNum < 4 && mPaintView.miPaintNum >= 0) {
                    mPaintView.miPaintNum++;
                }
                break;
        }
        setPaintFouns();
    }

    //顯示當前的筆
    public void setPaintFouns() {
        findViewById(R.id.imgBtn_paint_type1).setSelected(false);
        findViewById(R.id.imgBtn_paint_type2).setSelected(false);
        findViewById(R.id.imgBtn_paint_type3).setSelected(false);
        findViewById(R.id.imgBtn_paint_type4).setSelected(false);
        findViewById(R.id.imgBtn_paint_type5).setSelected(false);

        switch (mPaintView.miPaintNum) {
            case 0:
                findViewById(R.id.imgBtn_paint_type1).setSelected(true);
                mBtnChangePaint.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.draw_pen_ic_1));
                break;
            case 1:
                findViewById(R.id.imgBtn_paint_type2).setSelected(true);
                mBtnChangePaint.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.draw_pen_ic_2));
                break;
            case 2:
                findViewById(R.id.imgBtn_paint_type3).setSelected(true);
                mBtnChangePaint.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.draw_pen_ic_3));
                break;
            case 3:
                findViewById(R.id.imgBtn_paint_type4).setSelected(true);
                mBtnChangePaint.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.draw_pen_ic_4));
                break;
            case 4:
                findViewById(R.id.imgBtn_paint_type5).setSelected(true);
                mBtnChangePaint.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.draw_pen_ic_5));
                break;
        }
    }

    public void erase_mode(View view) {
        if (!mPaintView.mbEraseMode) {
            mPaintView.changePaint(5);
            ((ImageButton) findViewById(R.id.imgBtn_paint_erase)).setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.Red));
            mPaintView.mbEraseMode = true;
        } else {
            recoveryPaint();
        }
        setPaintFouns();
    }

    public void recoveryPaint() {
        mPaintView.changePaint(0);
        ((ImageButton) findViewById(R.id.imgBtn_paint_erase)).setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.Transparent));
        mPaintView.mbEraseMode = false;
    }

    public void playStateBtn() {
        mBtnRedo.setClickable(!mPaintView.mbZoomMode);
        mBtnUndo.setClickable(!mPaintView.mbZoomMode);
        mBtnSize.setClickable(!mPaintView.mbZoomMode);
        mBtnGrid.setClickable(!mPaintView.mbZoomMode);
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    savePictureEdit();
                } else {
                    //使用者拒絕權限，停用檔案存取功能

                    TSnackbarCall.showTSnackbar(findViewById(R.id.coordinatorLayout_activity_paint), getString(R.string.public_user_permission));
                }
                break;
        }
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
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
