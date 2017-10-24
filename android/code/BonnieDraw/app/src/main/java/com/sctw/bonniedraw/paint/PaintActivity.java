package com.sctw.bonniedraw.paint;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Xfermode;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
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
import com.sctw.bonniedraw.utility.BDWFileReader;
import com.sctw.bonniedraw.utility.BDWFileWriter;
import com.sctw.bonniedraw.utility.ConnectJson;
import com.sctw.bonniedraw.utility.FullScreenDialog;
import com.sctw.bonniedraw.utility.GlobalVariable;
import com.sctw.bonniedraw.utility.PxDpConvert;
import com.sctw.bonniedraw.utility.TSnackbarCall;
import com.sctw.bonniedraw.utility.Thumbnail;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

    public static final boolean HWLAYER = true;
    public static final boolean SWLAYER = false;

    private static final String SKETCH_FILE_BDW = "/backup.bdw";
    private static final String SKETCH_FILE_PNG = "/backup.png";
    private static final String TEMP_FILE = "/temp.bdw";
    private static final int REQUEST_EXTERNAL_STORAGE = 0;
    private Boolean mbEraseMode = false, mbZoomMode = false, mbCheckFinger = false;
    private MyView myView;
    private FrameLayout mFrameLayoutFreePaint;
    private Paint mPaint;
    private int count = 0;
    private ImageButton mBtnRedo, mBtnUndo, mBtnGrid, mBtnOpenAutoPlay, mBtnSize, mBtnChangePaint;
    private Button mBtnZoom;
    private List<Integer> mListTempTagLength = new ArrayList<Integer>();
    private List<TagPoint> mListTagPoint, mListUndoTagPoint;
    private ColorPicker colorPicker;
    private SizePicker sizePicker;
    private FullScreenDialog fullScreenDialog;
    private LinearLayout linearLayoutPaintSelect;
    private int miPrivacyType, miGridCol;
    private File backLoadBDW, backLoadPNG;
    private int displayWidth;
    private int realPaint = 0;
    private float startX, startY;
    private float pointLength;
    private SharedPreferences prefs;
    private Xfermode eraseEffect;
    BDWFileReader reader = new BDWFileReader();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paint);
        //Paint init
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(0xFF000000);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(13);
        eraseEffect = new PorterDuffXfermode(PorterDuff.Mode.DST_OUT);
        prefs = getSharedPreferences(GlobalVariable.MEMBER_PREFS, MODE_PRIVATE);
        myView = new MyView(this);
        getDisplay();
        //view inti
        mFrameLayoutFreePaint = (FrameLayout) findViewById(R.id.frameLayout_freepaint);
        mFrameLayoutFreePaint.addView(myView);
        mListTagPoint = new ArrayList<TagPoint>();
        mListUndoTagPoint = new ArrayList<TagPoint>();

        linearLayoutPaintSelect = findViewById(R.id.linearLayout_paint_select);
        mBtnZoom = (Button) findViewById(R.id.btn_paint_zoom);
        mBtnChangePaint = (ImageButton) findViewById(R.id.imgBtn_paint_change);
        mBtnRedo = (ImageButton) findViewById(R.id.imgBtn_paint_redo);
        mBtnUndo = (ImageButton) findViewById(R.id.imgBtn_paint_undo);
        mBtnGrid = (ImageButton) findViewById(R.id.imgBtn_paint_grid);
        mBtnOpenAutoPlay = (ImageButton) findViewById(R.id.imgBtn_paint_open_autoplay);
        mBtnSize = (ImageButton) findViewById(R.id.imgBtn_paint_size);
        setOnclick();
        colorPicker = new ColorPicker(myView.getContext(), this, "", Color.WHITE);
        colorPicker.getWindow().setGravity(Gravity.END);
        colorPicker.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        colorPicker.getWindow().getAttributes().windowAnimations = R.style.ColorPickStyle;
        sizePicker = new SizePicker(this, this, Color.GRAY);
        sizePicker.getWindow().setGravity(Gravity.START);
        sizePicker.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        sizePicker.getWindow().getAttributes().windowAnimations = R.style.ColorPickStyle;

        //檢查草稿
        checkSketch();
    }

    public class MyView extends View {
        private Bitmap mBitmap;
        private Canvas mCanvas;
        private Path mPath;
        private boolean load = false;
        private Bitmap loadBitmap;
        private ArrayList<PathAndPaint> paths = new ArrayList<>(20);
        private ArrayList<PathAndPaint> undonePaths = new ArrayList<>(20);
        private float mX, mY;
        private static final float TOUCH_TOLERANCE = 4;
        private Paint gridPaint;
        RectF rectF;
        int width;

        public MyView(Context c) {
            super(c);
            this.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
            Display display = wm.getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            width = size.x;
            mBitmap = Bitmap.createBitmap(width, width, Bitmap.Config.ARGB_8888);
            mCanvas = new Canvas(mBitmap);
            mPath = new Path();
            mPaint = new Paint(mPaint);
            //  Set Grid
            gridPaint = new Paint();
            gridPaint.setAntiAlias(true);
            gridPaint.setStrokeWidth(3);
            gridPaint.setStyle(Paint.Style.STROKE);
            gridPaint.setColor(ContextCompat.getColor(getApplicationContext(), R.color.GridLineColor));
            rectF = new RectF(getLeft(), getTop(), getRight(), getBottom());
            //this.setBackground(getResources().getDrawable(R.drawable.transparent));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                if (HWLAYER) {
                    setLayerType(View.LAYER_TYPE_HARDWARE, null);
                } else if (SWLAYER) {
                    setLayerType(View.LAYER_TYPE_SOFTWARE, null);
                } else {
                    setLayerType(View.LAYER_TYPE_NONE, null);
                }
            }

        }

        public Bitmap getBitmap() {
            return mBitmap;
        }

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        protected void onDraw(Canvas canvas) {
            canvas.save();
            if (miGridCol == 0) {
                canvas.drawColor(Color.WHITE);
            } else {
                canvas.drawColor(Color.WHITE);
                for (int i = 0; i <= miGridCol; i++) {
                    canvas.drawLine((width / miGridCol) * i, 0, (width / miGridCol) * i, width, gridPaint);
                    canvas.drawLine(0, (width / miGridCol) * i, width, (width / miGridCol) * i, gridPaint);
                }
            }

            canvas.drawBitmap(mBitmap, 0, 0, null);
            canvas.restore();
        }

        private void touch_start(float x, float y) {
            undonePaths.clear();
            mPath.reset();
            mPath.moveTo(x, y);
            mX = x;
            mY = y;
        }

        private void touch_move(float x, float y) {
            float dx = Math.abs(x - mX);
            float dy = Math.abs(y - mY);
            if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
                mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
                mX = x;
                mY = y;
            }
            mCanvas.drawPath(mPath, mPaint);
        }

        private void touch_up() {
            mListTempTagLength.add(mListTagPoint.size());
            mPath.lineTo(mX, mY);
            // commit the path to our offscreen
            paths.add(new PathAndPaint(mPath, mPaint));
            // kill this so we don't double draw (新路徑/畫筆)
            mPath = new Path();
            mPaint = new Paint(mPaint);
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            if (mbZoomMode) {
                switch (event.getActionMasked()) {
                    case MotionEvent.ACTION_DOWN:
                        startX = event.getX();
                        startY = event.getY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (!mbCheckFinger) {
                            int offsetX = (int) (event.getX() - startX);
                            int offsetY = (int) (event.getY() - startY);
                            myView.setTranslationX(myView.getTranslationX() + offsetX);
                            myView.setTranslationY(myView.getTranslationY() + offsetY);

                        } else {
                            float length = pointLength - spacing(event);
                            if (myView.getScaleX() >= 1.1) {
                                if (length > 0) {
                                    myView.setScaleX(myView.getScaleX() - 0.1f);
                                    myView.setScaleY(myView.getScaleY() - 0.1f);
                                } else if (length < 0) {
                                    myView.setScaleX(myView.getScaleX() + 0.1f);
                                    myView.setScaleY(myView.getScaleY() + 0.1f);
                                }
                            } else {
                                if (length > 0) {
                                    return true;
                                } else if (length < 0) {
                                    myView.setScaleX(myView.getScaleX() + 0.1f);
                                    myView.setScaleY(myView.getScaleY() + 0.1f);
                                }
                            }
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        break;
                    case MotionEvent.ACTION_POINTER_DOWN:
                        pointLength = spacing(event);
                        if (pointLength > 10) {
                            mbCheckFinger = true;
                        }
                        break;
                    case MotionEvent.ACTION_POINTER_UP:
                        mbCheckFinger = false;
                        break;
                }

                return true;
            }

            float x = event.getX();
            float y = event.getY();
            TagPoint tagpoint;
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                TagPoint paint_tagpoint = new TagPoint();
                paint_tagpoint.setiPosX(PxDpConvert.displayToFormat(x, displayWidth));
                paint_tagpoint.setiPosY(PxDpConvert.displayToFormat(y, displayWidth));
                paint_tagpoint.setiSize(PxDpConvert.displayToFormat(mPaint.getStrokeWidth(), displayWidth));
                paint_tagpoint.setiPaintType(realPaint);
                paint_tagpoint.setiColor(mPaint.getColor());
                paint_tagpoint.setiAction(MotionEvent.ACTION_DOWN + 1);
                mListTagPoint.add(paint_tagpoint);
            } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                tagpoint = new TagPoint();
                tagpoint.setiPosX(PxDpConvert.displayToFormat(x, displayWidth));
                tagpoint.setiPosY(PxDpConvert.displayToFormat(y, displayWidth));
                tagpoint.setiAction(event.getAction() + 1);
                mListTagPoint.add(tagpoint);
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                tagpoint = new TagPoint();
                tagpoint.setiAction(event.getAction() + 1);
                mListTagPoint.add(tagpoint);
            }

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    touch_start(x, y);
                    invalidate();
                    break;
                case MotionEvent.ACTION_MOVE:
                    touch_move(x, y);
                    invalidate();
                    break;
                case MotionEvent.ACTION_UP:
                    touch_up();
                    invalidate();
                    break;
            }
            return true;
        }

        //復原、重作
        public void onClickUndo() {
            if (paths.size() > 0 && undonePaths.size() <= 20) {
                //避免主執行緒LAG
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        undonePaths.add(paths.remove(paths.size() - 1));
                        count = paths.size() == 0 ? mListTempTagLength.get(0) : mListTempTagLength.get(paths.size()) - mListTempTagLength.get(paths.size() - 1);
                        for (int x = 0; x <= count - 1; x++) {
                            mListUndoTagPoint.add(mListTagPoint.remove(mListTagPoint.size() - 1));
                        }
                        if (backLoadPNG.exists()) {
                            mBitmap = BitmapFactory.decodeFile(backLoadPNG.toString()).copy(Bitmap.Config.ARGB_8888, true);
                        } else {
                            mBitmap = Bitmap.createBitmap(width, width, Bitmap.Config.ARGB_8888);
                        }
                        mCanvas = new Canvas(mBitmap);
                        for (PathAndPaint p : paths) {
                            mCanvas.drawPath(p.get_mPath(), p.get_mPaint());
                        }
                        invalidate();
                    }
                });
            } else {
                TSnackbarCall.showTSnackbar(PaintActivity.this.findViewById(R.id.coordinatorLayout_activity_paint), "復原次數到達上限");
            }
        }

        public void onClickRedo() {
            if (undonePaths.size() > 0) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        count = paths.size() == 0 ? mListTempTagLength.get(0) : mListTempTagLength.get(paths.size()) - mListTempTagLength.get(paths.size() - 1);
                        paths.add(undonePaths.remove(undonePaths.size() - 1));
                        for (int x = 0; x <= count - 1; x++) {
                            mListTagPoint.add(mListUndoTagPoint.remove(mListUndoTagPoint.size() - 1));
                        }

                        for (PathAndPaint p : paths) {
                            mCanvas.drawPath(p.get_mPath(), p.get_mPaint());
                        }
                        invalidate();
                    }
                });
            } else {
                TSnackbarCall.showTSnackbar(PaintActivity.this.findViewById(R.id.coordinatorLayout_activity_paint), "重作次數到達上限");
            }
        }

        public void onDrawSketch() {
            mBitmap = BitmapFactory.decodeFile(backLoadPNG.toString()).copy(Bitmap.Config.ARGB_8888, true);
            mCanvas = new Canvas(mBitmap);
            invalidate();
        }

        //計算中間距離
        private float spacing(MotionEvent event) {
            float x = event.getX(0) - event.getX(1);
            float y = event.getY(0) - event.getY(1);
            return (float) Math.sqrt(x * x + y * y);
        }
    }

    //產生預覽圖&上傳
    private void savePictureEdit() {
        fullScreenDialog = new FullScreenDialog(this, R.layout.paint_save_dialog);
        final EditText workName = (EditText) fullScreenDialog.findViewById(R.id.paint_save_work_name);
        final EditText workDescription = (EditText) fullScreenDialog.findViewById(R.id.paint_save_work_description);
        ImageView workPreview = fullScreenDialog.findViewById(R.id.save_paint_preview);
        Button saveWork = (Button) fullScreenDialog.findViewById(R.id.btn_save_paint_save);
        ImageButton saveCancel = (ImageButton) fullScreenDialog.findViewById(R.id.btn_save_paint_back);
        RadioGroup privacyTypes = (RadioGroup) fullScreenDialog.findViewById(R.id.paint_save_work_privacytype);

        File pngfile = null;
        try {
            File vPath = new File(getFilesDir() + "/bonniedraw");
            if (!vPath.exists()) vPath.mkdirs();
            pngfile = new File(vPath + "thumbnail.png");
            FileOutputStream fos = new FileOutputStream(pngfile);
            myView.getBitmap().compress(Bitmap.CompressFormat.PNG, 100, fos);
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
                        json.put("ui", prefs.getString(GlobalVariable.API_UID, "null"));
                        json.put("lk", prefs.getString(GlobalVariable.API_TOKEN, "null"));
                        json.put("dt", GlobalVariable.LOGIN_PLATFORM);
                        json.put("ac", 1); // 1 = add , 2 = update
                        json.put("privacyType", miPrivacyType);
                        json.put("title", workName.getText().toString());
                        json.put("description", workDescription.getText().toString());
                        json.put("languageId", 2);
                        Log.d("LOGIN JSON: ", json.toString());
                        fileUpload(json, GlobalVariable.API_LINK_WORK_SAVE);
                        fullScreenDialog.dismiss();
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
                fullScreenDialog.dismiss();
            }
        });
        fullScreenDialog.getWindow().getAttributes().windowAnimations = R.style.FullScreenDialogStyle;
        fullScreenDialog.show();
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
                .addFormDataPart("ui", prefs.getString(GlobalVariable.API_UID, "null"))
                .addFormDataPart("lk", prefs.getString(GlobalVariable.API_TOKEN, "null"))
                .addFormDataPart("dt", GlobalVariable.LOGIN_PLATFORM)
                .addFormDataPart("fn", "1")
                .addFormDataPart("wid", String.valueOf(wid))
                .addFormDataPart("ftype", String.valueOf(type));
        switch (type) {
            //上傳圖片
            case 1:
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                myView.getBitmap().compress(Bitmap.CompressFormat.PNG, 100, bos); //bm is the bitmap object
                bodyBuilder.addPart(Headers.of("Content-Disposition", "form-data; name=\"file\";filename=\"file.png\""), RequestBody.create(MediaType.parse("image/png"), bos.toByteArray()));
                break;
            //上傳BDW檔案
            case 2:
                BDWFileWriter writer = new BDWFileWriter();
                String filePath = getFilesDir().getPath() + TEMP_FILE;
                writer.WriteToFile(mListTagPoint, filePath);
                bodyBuilder.addPart(Headers.of("Content-Disposition", "form-data; name=\"file\";filename=\"file.bdw\""), RequestBody.create(MediaType.parse("application/octet-stream"), new File(filePath)));
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
        sizePicker.show();
    }

    @Override
    public void sizeChanged(int size) {
        mPaint.setStrokeWidth(size);
        float scale = size / 13.0F;
        mBtnSize.setScaleX(scale);
        mBtnSize.setScaleY(scale);
    }

    //選擇顏色
    public void colorPicks(View view) {
        colorPicker.show();
    }

    @Override
    public void colorChanged(int color) {
        mPaint.setColor(color);
        findViewById(R.id.imgBtn_paint_colorpicker).setBackgroundColor(color);
        if (mbEraseMode) {
            recoveryPaint();
        }
    }

    //設定各個按鍵
    public void setOnclick() {
        mBtnZoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mbZoomMode) {
                    mbZoomMode = true;
                    view.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.Red));
                    playStateBtn();

                } else {
                    mbZoomMode = false;
                    view.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.Transparent));
                    playStateBtn();
                }
            }
        });

        mBtnZoom.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                //myView.layout(0, 0, myView.getWidth(), myView.getHeight());
                myView.setTranslationX(0);
                myView.setTranslationY(0);
                myView.setScaleX(1);
                myView.setScaleY(1);
                return true;
            }
        });


        mBtnOpenAutoPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListTagPoint.size() > 0) {
                    if (saveTempBdw()) {
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
                myView.onClickRedo();
            }
        });

        mBtnUndo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myView.onClickUndo();
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
                        miGridCol = 0;
                        myView.invalidate();
                        gridDialog.dismiss();
                    }
                });

                grid3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        miGridCol = 3;
                        myView.invalidate();
                        gridDialog.dismiss();
                    }
                });

                grid6.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        miGridCol = 6;
                        myView.invalidate();
                        gridDialog.dismiss();
                    }
                });

                grid10.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        miGridCol = 10;
                        myView.invalidate();
                        gridDialog.dismiss();
                    }
                });

                grid20.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        miGridCol = 20;
                        myView.invalidate();
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
                if (backLoadBDW.delete() && backLoadPNG.delete()) {
                    TSnackbarCall.showTSnackbar(findViewById(R.id.coordinatorLayout_activity_paint), getString(R.string.paint_delete_sketch));
                }
                mFrameLayoutFreePaint.removeAllViews();
                myView = new MyView(PaintActivity.this);
                getDisplay();
                mFrameLayoutFreePaint.addView(myView);
                mListTagPoint = new ArrayList<>();
                mListUndoTagPoint.clear();
                mListTempTagLength.clear();
                myView.paths.clear();
                myView.undonePaths.clear();
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
                if (linearLayoutPaintSelect.getVisibility() == View.INVISIBLE) {
                    mBtnZoom.setVisibility(View.INVISIBLE);
                    linearLayoutPaintSelect.setVisibility(View.VISIBLE);
                    setPaintFouns();
                } else {
                    linearLayoutPaintSelect.setVisibility(View.INVISIBLE);
                    mBtnZoom.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    //切換筆
    public void selectPaint(View view) {
        switch (view.getId()) {
            case R.id.imgBtn_paint_left:
                if (realPaint <= 4 && realPaint > 0) {
                    realPaint--;
                }
                break;
            case R.id.imgBtn_paint_type1:
                customPaint(0);
                break;
            case R.id.imgBtn_paint_type2:
                customPaint(1);
                break;
            case R.id.imgBtn_paint_type3:
                customPaint(2);
                break;
            case R.id.imgBtn_paint_type4:
                customPaint(3);
                break;
            case R.id.imgBtn_paint_type5:
                customPaint(4);
                break;
            case R.id.imgBtn_paint_right:
                if (realPaint < 4 && realPaint >= 0) {
                    realPaint++;
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

        switch (realPaint) {
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

    //換筆
    public void customPaint(int paintNum) {
        //筆的效果 放置於此
        realPaint = paintNum;
        switch (paintNum) {
            case 0:
                mPaint.setXfermode(null);
                break;
            case 1:
                break;
            case 2:
                break;
            case 3:
                break;
            case 4:
                break;
            case 5:
                //橡皮擦
                mPaint.setXfermode(eraseEffect);
                break;
        }
    }

    //檢查有沒有草稿
    public void checkSketch() {
        backLoadBDW = new File(getFilesDir().getPath() + SKETCH_FILE_BDW);
        backLoadPNG = new File(getFilesDir().getPath() + SKETCH_FILE_PNG);
        if (backLoadBDW.exists() && backLoadPNG.exists()) {
            myView.onDrawSketch();
            reader.readFromFile(backLoadBDW);
            mListTagPoint = new ArrayList<>(reader.m_tagArray);
        }
    }

    //強制正方形
    public void getDisplay() {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        displayWidth = size.x;
        myView.setLayoutParams(new LinearLayout.LayoutParams(displayWidth, displayWidth));
    }

    public void erase_mode(View view) {
        if (!mbEraseMode) {
            customPaint(5);
            ((ImageButton) findViewById(R.id.imgBtn_paint_erase)).setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.Red));
            mbEraseMode = true;
        } else {
            recoveryPaint();
        }
    }

    public void recoveryPaint() {
        customPaint(0);
        ((ImageButton) findViewById(R.id.imgBtn_paint_erase)).setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.Transparent));
        mbEraseMode = false;
    }

    public void playStateBtn() {
        mBtnRedo.setClickable(!mbZoomMode);
        mBtnUndo.setClickable(!mbZoomMode);
        mBtnSize.setClickable(!mbZoomMode);
        mBtnGrid.setClickable(!mbZoomMode);
        mBtnOpenAutoPlay.setClickable(!mbZoomMode);
        findViewById(R.id.imgBtn_paint_save).setClickable(!mbZoomMode);
        findViewById(R.id.imgBtn_paint_erase).setClickable(!mbZoomMode);
        findViewById(R.id.imgBtn_paint_clear).setClickable(!mbZoomMode);
        findViewById(R.id.imgBtn_paint_colorpicker).setClickable(!mbZoomMode);
        findViewById(R.id.imgBtn_paint_back).setClickable(!mbZoomMode);
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
                try {
                    File pngfile = new File(getFilesDir().getPath() + SKETCH_FILE_PNG);
                    FileOutputStream fos = new FileOutputStream(pngfile);
                    myView.getBitmap().compress(Bitmap.CompressFormat.PNG, 100, fos);
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                boolean result = saveTempBdw();
                Toast.makeText(PaintActivity.this, "Successful", Toast.LENGTH_SHORT).show();
                dialogInterface.dismiss();
                if (!result) PaintActivity.this.finish();
            }
        });
        builder.setNegativeButton(R.string.public_no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                PaintActivity.this.finish();
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

    public boolean saveTempBdw() {
        BDWFileWriter bdwFileWriter = new BDWFileWriter();
        return !bdwFileWriter.WriteToFile(mListTagPoint, getFilesDir().getPath() + SKETCH_FILE_BDW);
    }

    public void onBackMethod() {
        if (mListTagPoint.size() != 0 && !backLoadBDW.exists()) {
            callSaveDialog(0);
        } else if (backLoadBDW.exists() && reader.m_tagArray != null) {
            if (mListTagPoint.size() != reader.m_tagArray.size()) {
                //是否更新草稿??
                callSaveDialog(1);
            } else {
                //沒變化就離開
                PaintActivity.this.finish();
            }
        } else {
            PaintActivity.this.finish();
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
