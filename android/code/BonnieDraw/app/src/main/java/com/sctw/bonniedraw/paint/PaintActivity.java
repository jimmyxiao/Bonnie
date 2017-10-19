package com.sctw.bonniedraw.paint;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
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
import android.os.Handler;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
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
import android.widget.TextView;
import android.widget.Toast;

import com.sctw.bonniedraw.R;
import com.sctw.bonniedraw.paintpicker.ColorPicker;
import com.sctw.bonniedraw.paintpicker.OnColorChangedListener;
import com.sctw.bonniedraw.paintpicker.OnSizeChangedListener;
import com.sctw.bonniedraw.paintpicker.SizePicker;
import com.sctw.bonniedraw.utility.BDWFileReader;
import com.sctw.bonniedraw.utility.BDWFileWriter;
import com.sctw.bonniedraw.utility.CircleMenuLayout;
import com.sctw.bonniedraw.utility.FullScreenDialog;
import com.sctw.bonniedraw.utility.GlobalVariable;
import com.sctw.bonniedraw.utility.PxDpConvert;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
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
    private static final String SKETCH_FILE = "/backup.bdw";
    private static final String TEMP_FILE = "/temp.bdw";
    private static final int REQUEST_EXTERNAL_STORAGE = 0;
    private static int miPointCount = 0, miPointCurrent = 0, miAutoPlayIntervalTime = 50;
    private Boolean mbAutoPlay = false, mbReplayMode = false, mbPlayState = false, mbPlaying = false, mbEarseMode = false, mbZoomMode = false, mbCheckFinger = false;
    private MyView myView;
    private FrameLayout mFrameLayoutFreePaint;
    private Paint mPaint;
    private int count = 0;
    private ImageButton mBtnAutoPlay, mBtnPlay, mBtnRedo, mBtnUndo, mBtnNext, mBtnPrevious, mBtnGrid, mBtnOpenAutoPlay, mBtnSize, mBtnChangePaint;
    private List<Integer> mListTempTagLength = new ArrayList<Integer>();
    private List<TagPoint> mListTagPoint, mListUndoTagPoint;
    private Handler mHandlerTimerPlay = new Handler();
    private ColorPicker colorPicker;
    private SizePicker sizePicker;
    private FullScreenDialog fullScreenDialog;
    private TextView mTextViewPlayProgress;
    private CircleMenuLayout mCircleMenuLayout;
    private static final int[] mItemImgs = {R.drawable.draw_pen_on_1, R.drawable.draw_pen_on_2, R.drawable.draw_pen_on_3, R.drawable.draw_pen_on_4, R.drawable.draw_pen_on_5};
    private String[] mItemTexts = new String[]{"1", "2", "3", "4", "5"};
    private int miPrivacyType, miGridCol;
    File fileLoad;
    int displayWidth, offsetX, offsetY, realPaint;
    float startX, startSacle, startY, pointLength;
    SharedPreferences prefs;
    Xfermode earseEffect;

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
        earseEffect = new PorterDuffXfermode(PorterDuff.Mode.CLEAR);
        realPaint = 0;
        prefs = getSharedPreferences(GlobalVariable.MEMBER_PREFS, MODE_PRIVATE);
        myView = new MyView(this);

        getDisplay();
        //view inti
        mFrameLayoutFreePaint = (FrameLayout) findViewById(R.id.frameLayout_freepaint);
        mFrameLayoutFreePaint.addView(myView);
        mTextViewPlayProgress = (TextView) findViewById(R.id.paint_play_progress);

        //CIRCLE MENU
        mBtnChangePaint = (ImageButton) findViewById(R.id.imgBtn_paint_change);
        mCircleMenuLayout = (CircleMenuLayout) findViewById(R.id.circlemenu_layout);
        mCircleMenuLayout.setMenuItemIconsAndTexts(mItemImgs, mItemTexts);

        mCircleMenuLayout.setOnMenuItemClickListener(new CircleMenuLayout.OnMenuItemClickListener() {

            @Override
            public void itemClick(View view, int pos) {
                Toast.makeText(PaintActivity.this, mItemTexts[pos], Toast.LENGTH_SHORT).show();
            }

            @Override
            public void itemCenterClick(View view) {
            }
        });
        mBtnChangePaint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mCircleMenuLayout.getVisibility() == View.VISIBLE) {
                    mCircleMenuLayout.setVisibility(View.INVISIBLE);
                } else {
                    mCircleMenuLayout.setVisibility(View.VISIBLE);
                }
            }
        });

        //tButton init
        mBtnAutoPlay = (ImageButton) findViewById(R.id.imgBtn_autoplay);
        mBtnPlay = (ImageButton) findViewById(R.id.imgBtn_paint_play);
        mBtnNext = (ImageButton) findViewById(R.id.imgBtn_next);
        mBtnPrevious = (ImageButton) findViewById(R.id.imgBtn_previous);
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
        mListTagPoint = new ArrayList<TagPoint>();
        mListUndoTagPoint = new ArrayList<TagPoint>();
        // Load fileLoad to bitmap
        if (getIntent().getExtras() != null) {
            if (getIntent().getExtras().getBoolean("load")) {
                Bitmap bMap = BitmapFactory.decodeFile(getIntent().getExtras().getString("fileLoad")).copy(
                        Bitmap.Config.ARGB_8888, true);
                System.out.println(getIntent().getExtras().getString("fileLoad"));
                myView.loadBitmap(bMap); // load bitmap
                mbReplayMode = true;
            }
        }

        checkSketch();
    }

    public void checkSketch() {
        fileLoad = new File(getFilesDir().getPath() + SKETCH_FILE);
        if (fileLoad.exists()) {
            reader.readFromFile(fileLoad);
            mListTagPoint = new ArrayList<>(reader.m_tagArray);
            miPointCount = mListTagPoint.size();
            miPointCurrent = 0;
            mbAutoPlay = true;
            playStateBtn(0);
            if (miPointCount > 0) mHandlerTimerPlay.postDelayed(rb_play, 1);
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

    //換筆
    public void customPaint(int paintNum) {
        switch (paintNum) {
            case 0:
                realPaint = 0;
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
                realPaint = 5;
                mPaint.setXfermode(earseEffect);
                break;
        }
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Log.v("ola_log", "landscape");
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            Log.v("ola_log", "portrait");
        }
    }

    //重播
    private Runnable rb_play = new Runnable() {
        public void run() {
            boolean brun = true;

            if (miPointCount > 0) {
                TagPoint tagpoint = mListTagPoint.get(miPointCurrent);
                switch (tagpoint.getiAction() - 1) {
                    case MotionEvent.ACTION_DOWN:
                        mbPlaying = true;
                        if (tagpoint.getiColor() != 0) {
                            mPaint.setColor(tagpoint.getiColor());
                        }
                        if (tagpoint.getiSize() != 0) {
                            mPaint.setStrokeWidth(PxDpConvert.formatToDisplay(tagpoint.getiSize(), displayWidth));
                        }
                        if (tagpoint.getiPaintType() != 0) {
                            customPaint(tagpoint.getiPaintType());
                        }
                        myView.touch_start(PxDpConvert.formatToDisplay(tagpoint.getiPosX(), displayWidth), PxDpConvert.formatToDisplay(tagpoint.getiPosY(), displayWidth));
                        myView.invalidate();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        myView.touch_move(PxDpConvert.formatToDisplay(tagpoint.getiPosX(), displayWidth), PxDpConvert.formatToDisplay(tagpoint.getiPosY(), displayWidth));
                        myView.invalidate();
                        break;
                    case MotionEvent.ACTION_UP:
                        mbPlaying = false;
                        brun = false;
                        myView.touch_up();
                        myView.invalidate();
                        break;
                }
                miPointCount--;
                miPointCurrent++;
                mTextViewPlayProgress.setText(miPointCurrent * 100 / mListTagPoint.size() + " / " + "100%");

                if (brun) {
                    mHandlerTimerPlay.postDelayed(rb_play, 50);
                } else {
                    if (mbAutoPlay) {
                        mHandlerTimerPlay.postDelayed(rb_play, miAutoPlayIntervalTime);
                    }
                }
            } else {
                mbAutoPlay = false;
            }
        }
    };

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
        boolean grid = false;
        RectF rectF;

        public MyView(Context c) {
            super(c);
            this.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
            Display display = wm.getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            int width = size.x;
            mBitmap = Bitmap.createBitmap(width, width, Bitmap.Config.ARGB_8888);
            mCanvas = new Canvas();
            mCanvas.setBitmap(mBitmap);
            mPath = new Path();
            mPaint = new Paint(mPaint);
            //  Set Grid
            gridPaint = new Paint();
            gridPaint.setAntiAlias(true);
            gridPaint.setStrokeWidth(3);
            gridPaint.setStyle(Paint.Style.STROKE);
            gridPaint.setColor(ContextCompat.getColor(getApplicationContext(), R.color.GridLineColor));
            rectF = new RectF(getLeft(), getTop(), getRight(), getBottom());
        }

        public Bitmap getBitmap() {
            return mBitmap;
        }

        public void loadBitmap(Bitmap bitmap) {
            loadBitmap = bitmap;
            load = true;
        }

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        protected void onDraw(Canvas canvas) {
            canvas.drawColor(Color.WHITE);
            if (grid) {
                //0到底部
                for (int i = 0; i <= miGridCol; i++) {
                    canvas.drawLine((displayWidth / miGridCol) * i, 0, (displayWidth / miGridCol) * i, displayWidth, gridPaint);
                    canvas.drawLine(0, (displayWidth / miGridCol) * i, displayWidth, (displayWidth / miGridCol) * i, gridPaint);
                }
            }

            for (PathAndPaint p : paths) {
                canvas.drawPath(p.get_mPath(), p.get_mPaint());
            }
            canvas.drawPath(mPath, mPaint);
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
        }

        private void touch_up() {
            mListTempTagLength.add(mListTagPoint.size());
            mPath.lineTo(mX, mY);
            // commit the path to our offscreen
            mCanvas.drawPath(mPath, mPaint);
            paths.add(new PathAndPaint(mPath, mPaint));
            // kill this so we don't double draw (新路徑/畫筆)
            mPath = new Path();
            mPaint = new Paint(mPaint);
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            if (mbReplayMode || mbAutoPlay || mbPlayState) return true;//重播功能時不准畫

            if (mbZoomMode) {
                switch (event.getActionMasked()) {
                    case MotionEvent.ACTION_DOWN:
                        startX = event.getX();
                        startY = event.getY();
                        startSacle = myView.getScaleX();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (!mbCheckFinger) {
                            offsetX = (int) (event.getX() - startX);
                            offsetY = (int) (event.getY() - startY);
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

        //復原、重作、上一步、下一步
        public void onClickPrevious() {
            if (paths.size() > 0) {
                paths.remove(paths.size() - 1);
                count = paths.size() == 0 ? mListTempTagLength.get(0) : mListTempTagLength.get(paths.size()) - mListTempTagLength.get(paths.size() - 1);
                for (int x = 0; x <= count - 1; x++) {
                    miPointCount++;
                    miPointCurrent--;
                }
                invalidate();
            }
        }

        public void onClickUndo() {
            if (paths.size() > 0 && undonePaths.size() <= 20) {
                undonePaths.add(paths.remove(paths.size() - 1));
                count = paths.size() == 0 ? mListTempTagLength.get(0) : mListTempTagLength.get(paths.size()) - mListTempTagLength.get(paths.size() - 1);
                for (int x = 0; x <= count - 1; x++) {
                    mListUndoTagPoint.add(mListTagPoint.remove(mListTagPoint.size() - 1));
                }
                invalidate();
            } else {
                Toast.makeText(PaintActivity.this, "復原次數到達上限", Toast.LENGTH_SHORT).show();
            }
        }

        public void onClickRedo() {
            if (undonePaths.size() > 0) {
                count = paths.size() == 0 ? mListTempTagLength.get(0) : mListTempTagLength.get(paths.size()) - mListTempTagLength.get(paths.size() - 1);
                paths.add(undonePaths.remove(undonePaths.size() - 1));
                for (int x = 0; x <= count - 1; x++) {
                    mListTagPoint.add(mListUndoTagPoint.remove(mListUndoTagPoint.size() - 1));
                }
                invalidate();
            } else {
                Toast.makeText(PaintActivity.this, "重作次數到達上限", Toast.LENGTH_SHORT).show();
            }
        }

        //計算中間距離
        private float spacing(MotionEvent event) {
            float x = event.getX(0) - event.getX(1);
            float y = event.getY(0) - event.getY(1);
            return (float) Math.sqrt(x * x + y * y);
        }
    }

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
            pngfile = new File(vPath + "temp.png");
            FileOutputStream fos = new FileOutputStream(pngfile);
            myView.getBitmap().compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
            workPreview.setImageBitmap(getBitmap(Uri.fromFile(pngfile)));

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
                        fileInfo(json, GlobalVariable.API_LINK_WORK_SAVE);
                        fullScreenDialog.dismiss();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    //boolean result = savePicture(workName.getText().toString());
                    //if (result) alertDialog.dismiss();
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    savePictureEdit();
                } else {
                    //使用者拒絕權限，停用檔案存取功能
                    Snackbar.make(mFrameLayoutFreePaint, R.string.public_user_permission, Snackbar.LENGTH_LONG).show();
                }
                break;
        }
    }

    public void fileInfo(JSONObject json, String url) {
        OkHttpClient okHttpClient = new OkHttpClient();
        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = FormBody.create(mediaType, json.toString());
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("Save Works", "Fail");
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

    public void uploadFile(int type, int wid) {
        OkHttpClient okHttpClient = new OkHttpClient();

        MultipartBody.Builder bodyBuilder = new MultipartBody.Builder();
        bodyBuilder.setType(MultipartBody.FORM)
                .addFormDataPart("ui", prefs.getString(GlobalVariable.API_UID, "null"))
                .addFormDataPart("lk", prefs.getString(GlobalVariable.API_TOKEN, "null"))
                .addFormDataPart("dt", GlobalVariable.LOGIN_PLATFORM)
                .addFormDataPart("wid", String.valueOf(wid))
                .addFormDataPart("ftype", String.valueOf(type));
        switch (type) {
            //上傳圖片
            case 1:
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                myView.getBitmap().compress(Bitmap.CompressFormat.PNG, 100, bos); //bm is the bitmap object
                bodyBuilder.addPart(Headers.of("Content-Disposition", "form-data; name=\"fileLoad\";filename=\"fileLoad.png\""), RequestBody.create(MediaType.parse("image/png"), bos.toByteArray()));
                break;
            //上傳BDW檔案
            case 2:
                BDWFileWriter writer = new BDWFileWriter();
                String filePath = getFilesDir().getPath() + TEMP_FILE;
                writer.WriteToFile(mListTagPoint, filePath);
                bodyBuilder.addPart(Headers.of("Content-Disposition", "form-data; name=\"fileLoad\";filename=\"fileLoad.bdw\""), RequestBody.create(MediaType.parse("application/octet-stream"), new File(filePath)));
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
                        Log.d("Upload File", "上傳成功");
                        Log.d("Upload File", responseJSON.toString());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }


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

    @Override
    public void colorChanged(int color) {
        mPaint.setColor(color);
        findViewById(R.id.imgBtn_paint_colorpicker).setBackgroundColor(color);
        if (mbEarseMode) {
            recoveryPaint();
        }
    }

    public void colorPicks(View view) {
        colorPicker.show();
    }


    //設定各個按鍵
    public void setOnclick() {
        ((Button) findViewById(R.id.btn_paint_zoom)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mbZoomMode) {
                    mbZoomMode = true;
                    view.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.Red));
                    playStateBtn(2);

                } else {
                    mbZoomMode = false;
                    view.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.Transparent));
                    playStateBtn(2);

                }
            }
        });

        ((Button) findViewById(R.id.btn_paint_zoom)).setOnLongClickListener(new View.OnLongClickListener() {
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
                    mBtnAutoPlay.setVisibility(View.VISIBLE);
                } else {
                    Toast.makeText(PaintActivity.this, R.string.paint_need_draw, Toast.LENGTH_SHORT).show();
                }

            }
        });

        //撥放器
        mBtnAutoPlay.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFrameLayoutFreePaint.removeAllViews();
                myView = new MyView(PaintActivity.this);
                recoveryPaint();
                getDisplay();
                mFrameLayoutFreePaint.addView(myView);
                miPointCount = mListTagPoint.size();
                miPointCurrent = 0;
                mbAutoPlay = true;
                playStateBtn(0);
                if (miPointCount > 0) mHandlerTimerPlay.postDelayed(rb_play, 100);
            }
        });

        mBtnPlay.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFrameLayoutFreePaint.removeAllViews();
                myView = new MyView(PaintActivity.this);
                getDisplay();
                mFrameLayoutFreePaint.addView(myView);
                miPointCount = mListTagPoint.size();
                miPointCurrent = 0;
                playStateBtn(0);
                if (miPointCount > 0)
                    mHandlerTimerPlay.postDelayed(rb_play, 100);
            }
        });

        mBtnNext.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (miPointCount > 0) {
                    mHandlerTimerPlay.postDelayed(rb_play, 100);
                } else if (miPointCount == 0 && !mbReplayMode) {
                    playStateBtn(1);
                    Toast.makeText(PaintActivity.this, R.string.play_end, Toast.LENGTH_SHORT).show();
                    customPaint(0);
                } else {
                    Toast.makeText(PaintActivity.this, R.string.play_end, Toast.LENGTH_SHORT).show();
                    PaintActivity.this.finish();
                }
            }
        });

        mBtnPrevious.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mbPlaying) {
                    Toast.makeText(PaintActivity.this, R.string.play_wait, Toast.LENGTH_SHORT).show();
                } else if (miPointCurrent > 0) {
                    mbPlayState = true;
                    myView.onClickPrevious();
                } else if (miPointCurrent == 0) {
                    Toast.makeText(PaintActivity.this, R.string.play_frist, Toast.LENGTH_SHORT).show();
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
                        myView.grid = false;
                        myView.invalidate();
                        gridDialog.dismiss();
                    }
                });

                grid3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        myView.grid = true;
                        miGridCol = 3;
                        myView.invalidate();
                        gridDialog.dismiss();
                    }
                });

                grid6.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        myView.grid = true;
                        miGridCol = 6;
                        myView.invalidate();
                        gridDialog.dismiss();
                    }
                });

                grid10.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        myView.grid = true;
                        miGridCol = 10;
                        myView.invalidate();
                        gridDialog.dismiss();
                    }
                });

                grid20.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        myView.grid = true;
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
                if (fileLoad.exists()) {
                    if (fileLoad.delete())
                        Toast.makeText(PaintActivity.this, R.string.paint_delete_sketch, Toast.LENGTH_SHORT).show();
                }
                mbAutoPlay = false;
                mbPlayState = false;
                miPointCount = 0;
                miPointCurrent = 0;
                mFrameLayoutFreePaint.removeAllViews();
                myView = new MyView(PaintActivity.this);
                getDisplay();
                mFrameLayoutFreePaint.addView(myView);
                mListTagPoint = new ArrayList<>();
                mListUndoTagPoint.clear();
                mListTempTagLength.clear();
                myView.paths.clear();
                myView.undonePaths.clear();
                mTextViewPlayProgress.setText("0/0");
                recoveryPaint();
                playStateBtn(1);
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
    }

    public void earse_mode(View view) {
        if (!mbEarseMode) {
            customPaint(5);
            ((ImageButton) findViewById(R.id.imgBtn_paint_erase)).setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.Red));
            mbEarseMode = true;
        } else {
            recoveryPaint();
        }
    }

    public void recoveryPaint() {
        customPaint(0);
        ((ImageButton) findViewById(R.id.imgBtn_paint_erase)).setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.Transparent));
        mbEarseMode = false;
    }


    public void playStateBtn(int option) {
        switch (option) {
            case 0:
                mbPlayState = true;
                mBtnAutoPlay.setVisibility(View.VISIBLE);
                mBtnNext.setVisibility(View.VISIBLE);
                mBtnPrevious.setVisibility(View.VISIBLE);
                mBtnRedo.setClickable(!mbPlayState);
                mBtnUndo.setClickable(!mbPlayState);
                mBtnSize.setClickable(!mbPlayState);
                mBtnGrid.setClickable(!mbPlayState);
                mBtnOpenAutoPlay.setClickable(!mbPlayState);
                findViewById(R.id.imgBtn_paint_erase).setClickable(!mbPlayState);
                findViewById(R.id.imgBtn_paint_clear).setClickable(!mbPlayState);
                findViewById(R.id.imgBtn_paint_colorpicker).setClickable(!mbPlayState);
                findViewById(R.id.imgBtn_paint_back).setClickable(!mbPlayState);
                break;
            case 1:
                mbPlayState = false;
                mBtnAutoPlay.setVisibility(View.INVISIBLE);
                mBtnNext.setVisibility(View.INVISIBLE);
                mBtnPrevious.setVisibility(View.INVISIBLE);
                mBtnRedo.setClickable(!mbPlayState);
                mBtnUndo.setClickable(!mbPlayState);
                mBtnSize.setClickable(!mbPlayState);
                mBtnGrid.setClickable(!mbPlayState);
                mBtnOpenAutoPlay.setClickable(!mbPlayState);
                findViewById(R.id.imgBtn_paint_save).setClickable(!mbPlayState);
                findViewById(R.id.imgBtn_paint_erase).setClickable(!mbPlayState);
                findViewById(R.id.imgBtn_paint_clear).setClickable(!mbPlayState);
                findViewById(R.id.imgBtn_paint_colorpicker).setClickable(!mbPlayState);
                findViewById(R.id.imgBtn_paint_back).setClickable(!mbPlayState);
                break;
            case 2:
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
                break;
        }

    }

    public Bitmap getBitmap(Uri uri) {
        try {
            InputStream in = getContentResolver().openInputStream(uri);

            // 第一次 decode,只取得圖片長寬,還未載入記憶體
            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(in, null, opts);
            in.close();

            // 取得動態計算縮圖長寬的 SampleSize (2的平方最佳)
            int sampleSize = computeSampleSize(opts, -1, 512 * 512);

            // 第二次 decode,指定取樣數後,產生縮圖
            in = getContentResolver().openInputStream(uri);
            opts = new BitmapFactory.Options();
            opts.inSampleSize = sampleSize;

            Bitmap bmp = BitmapFactory.decodeStream(in, null, opts);
            in.close();

            return bmp;
        } catch (Exception err) {
            return null;
        }
    }

    public static int computeSampleSize(BitmapFactory.Options options,
                                        int minSideLength, int maxNumOfPixels) {

        int initialSize = computeInitialSampleSize(options, minSideLength,
                maxNumOfPixels);
        int roundedSize;

        if (initialSize <= 8) {
            roundedSize = 1;
            while (roundedSize < initialSize) {
                roundedSize <<= 1;
            }
        } else {
            roundedSize = (initialSize + 7) / 8 * 8;
        }

        return roundedSize;
    }

    private static int computeInitialSampleSize(BitmapFactory.Options options,
                                                int minSideLength, int maxNumOfPixels) {

        double w = options.outWidth;
        double h = options.outHeight;

        int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math
                .sqrt(w * h / maxNumOfPixels));
        int upperBound = (minSideLength == -1) ? 128 : (int) Math.min(
                Math.floor(w / minSideLength), Math.floor(h / minSideLength));

        if (upperBound < lowerBound) {
            // return the larger one when there is no overlapping zone.
            return lowerBound;
        }

        if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
            return 1;
        } else if (minSideLength == -1) {
            return lowerBound;
        } else {
            return upperBound;
        }
    }


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

    }

    public void callSaveDialog(int num) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        switch (num) {
            case 0:
                builder.setMessage(R.string.paint_sketch_save_title);
                break;
            case 1:
                builder.setMessage(R.string.paint_sketch_save_title);
                break;
        }
        builder.setPositiveButton(R.string.public_yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                BDWFileWriter bdwFileWriter = new BDWFileWriter();
                boolean result = bdwFileWriter.WriteToFile(mListTagPoint, getFilesDir().getPath() + SKETCH_FILE);
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

    public void onBackMethod() {
        if (!mbZoomMode && !mbPlayState) {
            if (mListTagPoint.size() != 0 && !fileLoad.exists()) {
                callSaveDialog(0);
            } else if (fileLoad.exists() && mListTagPoint.size() != reader.m_tagArray.size()) {
                callSaveDialog(1);
            } else {
                PaintActivity.this.finish();
            }
        }

    }

    public void back(View view) {
        onBackMethod();
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
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
