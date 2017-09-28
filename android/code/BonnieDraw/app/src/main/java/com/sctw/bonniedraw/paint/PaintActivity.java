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
import android.graphics.Xfermode;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
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
import android.widget.LinearLayout;
import android.widget.Toast;

import com.sctw.bonniedraw.R;
import com.sctw.bonniedraw.paintpicker.ColorPicker;
import com.sctw.bonniedraw.paintpicker.OnColorChangedListener;
import com.sctw.bonniedraw.paintpicker.OnSizeChangedListener;
import com.sctw.bonniedraw.paintpicker.SizePicker;
import com.sctw.bonniedraw.utility.BDWFileReader;
import com.sctw.bonniedraw.utility.BDWFileWriter;
import com.sctw.bonniedraw.utility.PxDpConvert;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class PaintActivity extends AppCompatActivity implements OnColorChangedListener, OnSizeChangedListener {
    public static final String KEY_MY_PREFERENCE = "autoplay_intervaltime";
    private static final String SKETCH_FILE = "/backup.bdw";
    private static final int REQUEST_EXTERNAL_STORAGE = 0;
    private Boolean mbAutoPlay = false, replayMode = false, playState = false, playing = false, earseMode = false, zoomMode = false;
    private Boolean sketch = false, checkFinger = false;
    private MyView myView;
    private FrameLayout mViewFreePaint;
    private String fname; // file name
    private Paint mPaint;
    private int count = 0;
    private ImageButton btnAutoPlay, btnPlay, btnRedo, btnUndo, btnColorpicker, btnNext, btnPrevious, btnGrid, btnOpenAutoPlay, btnSize;
    private List<Integer> tempTagLength = new ArrayList<Integer>();
    private List<TagPoint> mTagPoint_a_record, undoTagPoint_a_record;
    private Handler handler_Timer_Play = new Handler();
    private static int miPointCount = 0, miPointCurrent = 0;
    private static int miAutoPlayIntervalTime = 10;
    private int displayWidth;
    private ColorPicker colorPicker;
    private SizePicker sizePicker;
    BDWFileReader reader = new BDWFileReader();
    File file, filePath;
    float startX, startSacle, startY, pointLength;
    int offsetX, offsetY, realPaint;
    Xfermode earseEffect;

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

        myView = new MyView(this);
        getDisplay();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String strprefs = prefs.getString(KEY_MY_PREFERENCE, "1");
        miAutoPlayIntervalTime = Integer.valueOf(strprefs) * 100;
        //view inti
        mViewFreePaint = (FrameLayout) findViewById(R.id.view_freepaint);
        mViewFreePaint.addView(myView);


        //tButton init
        btnAutoPlay = (ImageButton) findViewById(R.id.id_btn_autoplay);
        btnPlay = (ImageButton) findViewById(R.id.id_btn_play);
        btnNext = (ImageButton) findViewById(R.id.id_btn_next);
        btnPrevious = (ImageButton) findViewById(R.id.id_btn_previous);
        btnRedo = (ImageButton) findViewById(R.id.btn_paint_redo);
        btnUndo = (ImageButton) findViewById(R.id.btn_paint_undo);
        btnGrid = (ImageButton) findViewById(R.id.id_btn_grid);
        btnOpenAutoPlay = (ImageButton) findViewById(R.id.id_btn_open_autoplay);
        btnSize = (ImageButton) findViewById(R.id.id_btn_size);
        setOnclick();

        colorPicker = new ColorPicker(myView.getContext(), this, "", Color.WHITE);
        colorPicker.getWindow().setGravity(Gravity.END);
        colorPicker.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        colorPicker.getWindow().getAttributes().windowAnimations = R.style.ColorPickStyle;
        sizePicker = new SizePicker(this, this, "", Color.WHITE);
        sizePicker.getWindow().setGravity(Gravity.START);
        sizePicker.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        sizePicker.getWindow().getAttributes().windowAnimations = R.style.ColorPickStyle;
        btnColorpicker = (ImageButton) findViewById(R.id.id_btn_colorpicker);

        mTagPoint_a_record = new ArrayList<TagPoint>();
        undoTagPoint_a_record = new ArrayList<TagPoint>();
        // Load file to bitmap
        if (getIntent().getExtras() != null) {
            if (getIntent().getExtras().getBoolean("load")) {
                Bitmap bMap = BitmapFactory.decodeFile(getIntent().getExtras().getString("file")).copy(
                        Bitmap.Config.ARGB_8888, true);
                System.out.println(getIntent().getExtras().getString("file"));
                myView.loadBitmap(bMap); // load bitmap
                fname = getIntent().getExtras().getString("name");
                replayMode = true;
            }
        }

        file = new File(getFilesDir().getPath() + SKETCH_FILE);
        checkSketch();
    }

    public void checkSketch() {
        if (file.exists()) {
            reader.readFromFile(file);
            mTagPoint_a_record = new ArrayList<>(reader.m_tagArray);
            miPointCount = mTagPoint_a_record.size();
            miPointCurrent = 0;
            mbAutoPlay = true;
            playStateBtn(0);
            if (miPointCount > 0) handler_Timer_Play.postDelayed(rb_play, 1);
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

    //
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

    private Runnable rb_play = new Runnable() {
        public void run() {
            boolean brun = true;
            //提示關閉撥放
            if (miPointCount <= 1) {
                // Close
            }

            if (miPointCount > 0) {
                TagPoint tagpoint = mTagPoint_a_record.get(miPointCurrent);
                switch (tagpoint.getiAction() - 1) {
                    case MotionEvent.ACTION_DOWN:
                        playing = true;
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
                        playing = false;
                        brun = false;
                        myView.touch_up();
                        myView.invalidate();
                        break;
                }
                miPointCount--;
                miPointCurrent++;
                if (brun) {
                    handler_Timer_Play.postDelayed(rb_play, 50);
                } else {
                    if (mbAutoPlay) {
                        handler_Timer_Play.postDelayed(rb_play, miAutoPlayIntervalTime);
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
            //  Set paint options
            gridPaint = new Paint();
            gridPaint.setAntiAlias(true);
            gridPaint.setStrokeWidth(3);
            gridPaint.setStyle(Paint.Style.STROKE);
            gridPaint.setColor(ContextCompat.getColor(getApplicationContext(), R.color.GridLineColor));
        }

        public void loadBitmap(Bitmap bitmap) {
            loadBitmap = bitmap;
            load = true;
        }

        @Override
        protected void onDraw(Canvas canvas) {
            if (grid) {
                //0到底部
                int gridCol = 20;
                for (int i = 0; i <= gridCol; i++) {
                    canvas.drawLine((displayWidth / gridCol) * i, 0, (displayWidth / gridCol) * i, displayWidth, gridPaint);
                    canvas.drawLine(0, (displayWidth / gridCol) * i, displayWidth, (displayWidth / gridCol) * i, gridPaint);

                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    canvas.saveLayer(myView.getLeft(), myView.getTop(), displayWidth, displayWidth, gridPaint);
                }
            }

            for (PathAndPaint p : paths) {
                canvas.drawPath(p.get_mPath(), p.get_mPaint());
            }
            canvas.drawPath(mPath, mPaint);
        }

        public Bitmap getBitmap() {
            return mBitmap;
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
            tempTagLength.add(mTagPoint_a_record.size());
            mPath.lineTo(mX, mY);
            // commit the path to our offscreen
            mCanvas.drawPath(mPath, mPaint);
            paths.add(new PathAndPaint(mPath, mPaint));
            // kill this so we don't double draw (新路徑/畫筆)
            mPath = new Path();
            mPaint = new Paint(mPaint);
        }

        public void onClickPrevious() {
            if (paths.size() > 0) {
                paths.remove(paths.size() - 1);
                count = paths.size() == 0 ? tempTagLength.get(0) : tempTagLength.get(paths.size()) - tempTagLength.get(paths.size() - 1);
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
                count = paths.size() == 0 ? tempTagLength.get(0) : tempTagLength.get(paths.size()) - tempTagLength.get(paths.size() - 1);
                for (int x = 0; x <= count - 1; x++) {
                    undoTagPoint_a_record.add(mTagPoint_a_record.remove(mTagPoint_a_record.size() - 1));
                }
                invalidate();
            } else {
                Toast.makeText(PaintActivity.this, "復原次數到達上限", Toast.LENGTH_SHORT).show();
            }
        }

        public void onClickRedo() {
            if (undonePaths.size() > 0) {
                count = paths.size() == 0 ? tempTagLength.get(0) : tempTagLength.get(paths.size()) - tempTagLength.get(paths.size() - 1);
                paths.add(undonePaths.remove(undonePaths.size() - 1));
                for (int x = 0; x <= count - 1; x++) {
                    mTagPoint_a_record.add(undoTagPoint_a_record.remove(undoTagPoint_a_record.size() - 1));
                }
                invalidate();
            } else {
                Toast.makeText(PaintActivity.this, "重作次數到達上限", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            if (replayMode || mbAutoPlay || playState) return true;//重播功能時不准畫

            if (zoomMode) {
                switch (event.getActionMasked()) {
                    case MotionEvent.ACTION_DOWN:
                        startX = event.getX();
                        startY = event.getY();
                        startSacle = myView.getScaleX();
                        Log.d("GET POINT", String.valueOf(startSacle));
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (!checkFinger) {
                            offsetX = (int) (event.getX() - startX);
                            offsetY = (int) (event.getY() - startY);
                            myView.setTranslationX(myView.getTranslationX() + offsetX);
                            myView.setTranslationY(myView.getTranslationY() + offsetY);

                        } else {
                            float length = pointLength - spacing(event);
                            if (myView.getScaleX() >= 0.5) {
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
                            checkFinger = true;
                        }
                        break;
                    case MotionEvent.ACTION_POINTER_UP:
                        checkFinger = false;
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
                Log.d("realPaint=", String.valueOf(realPaint));
                paint_tagpoint.setiColor(mPaint.getColor());
                paint_tagpoint.setiAction(MotionEvent.ACTION_DOWN + 1);
                mTagPoint_a_record.add(paint_tagpoint);
            } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                tagpoint = new TagPoint();
                tagpoint.setiPosX(PxDpConvert.displayToFormat(x, displayWidth));
                tagpoint.setiPosY(PxDpConvert.displayToFormat(y, displayWidth));
                tagpoint.setiAction(event.getAction() + 1);
                mTagPoint_a_record.add(tagpoint);
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                tagpoint = new TagPoint();
                tagpoint.setiAction(event.getAction() + 1);
                mTagPoint_a_record.add(tagpoint);
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
    }

    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    public void callSaveDialog(int num) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        switch (num) {
            case 0:
                builder.setMessage("是否要保留草稿");
                break;
            case 1:
                builder.setMessage("是否要更新草稿");
                break;
        }

        builder.setPositiveButton("是", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                BDWFileWriter bdwFileWriter = new BDWFileWriter();
                boolean result = bdwFileWriter.WriteToFile(mTagPoint_a_record, getFilesDir().getPath() + SKETCH_FILE);
                Toast.makeText(PaintActivity.this, "Successful", Toast.LENGTH_SHORT).show();
                if (!result) PaintActivity.this.finish();
            }
        });

        builder.setNegativeButton("否", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                PaintActivity.this.finish();
            }
        });
        AlertDialog alertdialog = builder.create();
        alertdialog.show();
    }

    public void back(View view) {
        if (mTagPoint_a_record.size() != 0 && !file.exists()) {
            callSaveDialog(0);
        } else if (file.exists() && mTagPoint_a_record.size() != reader.m_tagArray.size()) {
            callSaveDialog(1);
        } else {
            PaintActivity.this.finish();
        }
    }

    public Button.OnClickListener savePictureBtn = new Button.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (ActivityCompat.checkSelfPermission(PaintActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                            REQUEST_EXTERNAL_STORAGE);
                }
            } else {
                savePictureEdit();
            }
        }
    };

    private void savePictureEdit() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.paint_save_dialog, null);
        final EditText workName = (EditText) view.findViewById(R.id.paint_save_work_name);
        final EditText workDescription = (EditText) view.findViewById(R.id.paint_save_work_description);
        Button saveWork = (Button) view.findViewById(R.id.paint_save_done);
        Button saveCancel = (Button) view.findViewById(R.id.paint_save_cencel);

        builder.setView(view);
        final AlertDialog alertDialog = builder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.show();

        saveWork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!workName.getText().toString().isEmpty() && !workDescription.getText().toString().isEmpty()) {
                    boolean result = savePicture(workName.getText().toString());
                    if (result) alertDialog.dismiss();
                } else {
                    Toast.makeText(PaintActivity.this, "請輸入檔名或按取消", Toast.LENGTH_SHORT).show();
                }
            }
        });

        saveCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
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
                    Snackbar.make(mViewFreePaint, "使用者拒絕權限 無法存取", Snackbar.LENGTH_LONG).show();
                }
                break;
        }
    }

    public boolean savePicture(String fileName) {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            try {
                File vPath = new File(Environment.getExternalStorageDirectory() + "/bonniedraw");
                if (!vPath.exists()) vPath.mkdirs();
                File file = new File(Environment.getExternalStorageDirectory() + "/bonniedraw/" + fileName + ".png");
                FileOutputStream fos = new FileOutputStream(file);
                myView.getBitmap().compress(Bitmap.CompressFormat.PNG, 100, fos);
                fos.close();

            } catch (IOException e) {
                e.printStackTrace();
            }

            File logPath = new File(Environment.getExternalStorageDirectory() + "/bonniedraw/" + "Record/");
            if (!logPath.exists()) logPath.mkdirs();
            BDWFileWriter mBDWFileWriter = new BDWFileWriter();
            boolean writeResult = mBDWFileWriter.WriteToFile(mTagPoint_a_record, Environment.getExternalStorageDirectory() + "/bonniedraw/" + "Record/" + fileName + ".bdw");
            if (!writeResult) {
                Snackbar.make(mViewFreePaint, "儲存成功", Snackbar.LENGTH_LONG).show();
                return true;
            } else {
                Snackbar.make(mViewFreePaint, "Save Fail", Snackbar.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(this, "No SD Card detected", Toast.LENGTH_SHORT).show();
            return false;
        }
        return false;
    }

    public void colorPicks(View view) {
        colorPicker.show();
    }

    public void sizesPicks(View view) {
        sizePicker.show();
    }

    @Override
    public void sizeChanged(int size) {
        mPaint.setStrokeWidth(size);
        Log.d("SIZES = ", String.valueOf(size));
        float scale = size / 13.0F;
        Log.d("scale = ", String.valueOf(scale));
        btnSize.setScaleX(scale);
        btnSize.setScaleY(scale);
    }

    @Override
    public void colorChanged(int color) {
        mPaint.setColor(color);
        findViewById(R.id.id_btn_colorpicker).setBackgroundColor(color);
        if (earseMode) {
            recoveryPaint();
        }
    }

    //End
    public void setOnclick() {
        ((Button) findViewById(R.id.id_paint_zoom)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!zoomMode) {
                    zoomMode = true;
                    view.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.Red));
                } else {
                    zoomMode = false;
                    view.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.Transparent));
                }
            }
        });

        ((Button) findViewById(R.id.id_paint_zoom)).setOnLongClickListener(new View.OnLongClickListener() {
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


        btnOpenAutoPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnAutoPlay.setVisibility(View.VISIBLE);
            }
        });

        //撥放器
        btnAutoPlay.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewFreePaint.removeAllViews();
                myView = new MyView(PaintActivity.this);
                recoveryPaint();
                getDisplay();
                mViewFreePaint.addView(myView);
                miPointCount = mTagPoint_a_record.size();
                miPointCurrent = 0;
                mbAutoPlay = true;
                playStateBtn(0);
                if (miPointCount > 0) handler_Timer_Play.postDelayed(rb_play, 100);
            }
        });

        btnPlay.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewFreePaint.removeAllViews();
                myView = new MyView(PaintActivity.this);
                getDisplay();
                mViewFreePaint.addView(myView);
                miPointCount = mTagPoint_a_record.size();
                miPointCurrent = 0;
                playStateBtn(0);
                if (miPointCount > 0)
                    handler_Timer_Play.postDelayed(rb_play, 100);
            }
        });

        btnNext.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (miPointCount > 0) {
                    handler_Timer_Play.postDelayed(rb_play, 100);
                } else if (miPointCount == 0 && !replayMode) {
                    playStateBtn(1);
                    Toast.makeText(PaintActivity.this, "播放完畢", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(PaintActivity.this, "播放完畢", Toast.LENGTH_SHORT).show();
                    PaintActivity.this.finish();
                }
            }
        });

        btnPrevious.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (playing) {
                    Toast.makeText(PaintActivity.this, "請等畫完這一段再按", Toast.LENGTH_SHORT).show();
                } else if (miPointCurrent > 0) {
                    playState = true;
                    myView.onClickPrevious();
                } else if (miPointCurrent == 0) {
                    Toast.makeText(PaintActivity.this, "已經是最前步驟", Toast.LENGTH_SHORT).show();
                }
            }
        });


        btnRedo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myView.onClickRedo();
            }
        });

        btnUndo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myView.onClickUndo();
            }
        });

        btnGrid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //開啟格線
                if (!myView.grid) {
                    myView.grid = true;
                    myView.invalidate();
                } else {
                    myView.grid = false;
                    myView.invalidate();
                }
            }
        });

        //設定項
        findViewById(R.id.id_btn_clear).setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (file.exists()) {
                    if (file.delete())
                        Toast.makeText(PaintActivity.this, "刪除草稿", Toast.LENGTH_SHORT).show();
                }
                mbAutoPlay = false;
                playState = false;
                miPointCount = 0;
                miPointCurrent = 0;
                mViewFreePaint.removeAllViews();
                myView = new MyView(PaintActivity.this);
                getDisplay();
                mViewFreePaint.addView(myView);
                mTagPoint_a_record = new ArrayList<>();
                undoTagPoint_a_record.clear();
                tempTagLength.clear();
                myView.paths.clear();
                myView.undonePaths.clear();
                recoveryPaint();
                playStateBtn(1);
            }
        });

        findViewById(R.id.id_btn_save).setOnClickListener(savePictureBtn);
    }

    public void earse_mode(View view) {
        if (!earseMode) {
            customPaint(5);
            ((ImageButton) findViewById(R.id.id_btn_erase)).setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.Red));
            earseMode = true;
        } else {
            recoveryPaint();
        }
    }

    public void recoveryPaint() {
        customPaint(0);
        ((ImageButton) findViewById(R.id.id_btn_erase)).setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.Transparent));
        earseMode = false;
    }


    public void playStateBtn(int option) {
        switch (option) {
            case 0:
                playState = true;
                btnAutoPlay.setVisibility(View.VISIBLE);
                btnNext.setVisibility(View.VISIBLE);
                btnPrevious.setVisibility(View.VISIBLE);
                btnRedo.setVisibility(View.INVISIBLE);
                btnUndo.setVisibility(View.INVISIBLE);
                break;
            case 1:
                playState = false;
                btnAutoPlay.setVisibility(View.INVISIBLE);
                btnNext.setVisibility(View.INVISIBLE);
                btnPrevious.setVisibility(View.INVISIBLE);
                btnRedo.setVisibility(View.VISIBLE);
                btnUndo.setVisibility(View.VISIBLE);
                break;
        }

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
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
