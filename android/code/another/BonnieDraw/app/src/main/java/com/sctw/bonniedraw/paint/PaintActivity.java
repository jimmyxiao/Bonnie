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
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.sctw.bonniedraw.R;
import com.sctw.bonniedraw.paintpicker.ColorPicker;
import com.sctw.bonniedraw.paintpicker.OnColorChangedListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import static com.sctw.bonniedraw.activity.MainActivity.userEmail;
import static com.sctw.bonniedraw.activity.MainActivity.userName;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class PaintActivity extends AppCompatActivity implements OnColorChangedListener {
    public static final String KEY_MY_PREFERENCE = "autoplay_intervaltime";
    private static final int EVENT_PAINT_CHANGED = 0xff;
    private static final int REQUEST_EXTERNAL_STORAGE = 0;
    private MyView myView;
    private FrameLayout mViewFreePaint;
    private String fname; // file name
    private Paint mPaint, tempPaint;
    private int count = 0;
    private Button btnAutoPlay, btnPlay, btnNext, btnPrevious, btnRedo, btnUndo;
    private List<Integer> tempTagLength = new ArrayList<Integer>();
    private List<TagPoint> mTagPoint_a_record, undoTagPoint_a_record;
    private Handler handler_Timer_Play = new Handler();
    private static int miPointCount = 0, miPointCurrent = 0;
    private static int miAutoPlayIntervalTime = 10;
    private boolean mbAutoPlay = false, mbPainStarted = false;
    private static int miColor_Paint = 0xFF000000;
    private PaintInfo paintInfo;
    private ArrayList<?> paintInfoList;
    boolean checkErase, checkCover = false;
    private boolean playState = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paint);
        myView = new PaintActivity.MyView(this);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String strprefs = prefs.getString(KEY_MY_PREFERENCE, "1");
        miAutoPlayIntervalTime = Integer.valueOf(strprefs) * 100;
        mViewFreePaint = (FrameLayout) findViewById(R.id.view_freepain);
        mViewFreePaint.addView(myView);
        myView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        btnAutoPlay = (Button) findViewById(R.id.id_btn_autoplay);
        btnPlay = (Button) findViewById(R.id.id_btn_play);
        btnNext = (Button) findViewById(R.id.id_btn_next);
        btnPrevious = (Button) findViewById(R.id.id_btn_previous);
        btnRedo = (Button) findViewById(R.id.btn_paint_redo);
        btnUndo = (Button) findViewById(R.id.btn_paint_undo);
        setOnclick();

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
            }
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
            if (miPointCount == 1) {
                btnNext.setText("關閉播放");
            }

            if (miPointCount > 0) {
                TagPoint tagpoint = mTagPoint_a_record.get(miPointCurrent);
                switch (tagpoint.get_iTouchType()) {
                    case MotionEvent.ACTION_DOWN:
                        myView.touch_start(tagpoint.get_fPosX(), tagpoint.get_fPosY());
                        myView.invalidate();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        myView.touch_move(tagpoint.get_fPosX(), tagpoint.get_fPosY());
                        myView.invalidate();
                        break;
                    case MotionEvent.ACTION_UP:
                        brun = false;
                        myView.touch_up();
                        myView.invalidate();
                        break;
                    case EVENT_PAINT_CHANGED:
                        mPaint.set(tagpoint.get_iPaint());
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
        private ArrayList<PathAndPaint> paths = new ArrayList<>();
        private ArrayList<PathAndPaint> undonePaths = new ArrayList<>();
        private float mX, mY;
        private static final float TOUCH_TOLERANCE = 4;

        public MyView(Context c) {
            super(c);
            mCanvas = new Canvas();
            mPath = new Path();
            mPaint = new Paint();
            mPaint.setAntiAlias(true);
            mPaint.setDither(true);
            mPaint.setColor(miColor_Paint);
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setStrokeJoin(Paint.Join.ROUND);
            mPaint.setStrokeCap(Paint.Cap.ROUND);
            mPaint.setStrokeWidth(12);
        }

        public void loadBitmap(Bitmap bitmap) {
            loadBitmap = bitmap;
            load = true;
        }

        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            super.onSizeChanged(w, h, oldw, oldh);
            if (!load) {
                mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
                mCanvas = new Canvas(mBitmap);
                mCanvas.drawColor(Color.WHITE);
                mCanvas.drawBitmap(mBitmap,0,0,null);
            } else {
                mBitmap = loadBitmap;
                mCanvas = new Canvas(mBitmap);
            }
        }

        @Override
        protected void onDraw(Canvas canvas) {
            for (PathAndPaint p : paths) {
                canvas.drawPath(p.getmPath(), p.getmPaint());
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
            mPath.lineTo(mX, mY);
            // commit the path to our offscreen
            mCanvas.drawPath(mPath, mPaint);
            paths.add(new PathAndPaint(mPath, mPaint));
            // kill this so we don't double draw (新路徑/畫筆)
            mPath = new Path();
            tempPaint = mPaint;
            mPaint = new Paint(tempPaint);
        }

        public void onClickPrevious() {
            paths.remove(paths.size() - 1);
            count = paths.size() == 0 ? tempTagLength.get(0) : tempTagLength.get(paths.size()) - tempTagLength.get(paths.size() - 1);
            for (int x = 0; x <= count - 1; x++) {
                miPointCount++;
                miPointCurrent--;
            }
            invalidate();
        }

        public void onClickUndo() {
            if (paths.size() > 0) {
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
            if (mbAutoPlay) return true;

            if (playState) return true;//播放時禁止畫圖

            float x = event.getX();
            float y = event.getY();
            TagPoint tagpoint;
            if (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_MOVE) {
                if (!mbPainStarted) {
                    TagPoint paint_tagpoint = new TagPoint();
                    paint_tagpoint.set_fPosX(0);
                    paint_tagpoint.set_fPosY(0);
                    paint_tagpoint.set_iTouchType(EVENT_PAINT_CHANGED);
                    paint_tagpoint.set_iPaint(mPaint);
                    mTagPoint_a_record.add(paint_tagpoint);
                    mbPainStarted = true;
                }
                mbPainStarted = true;
                tagpoint = new TagPoint();
                tagpoint.set_fPosX(x);
                tagpoint.set_fPosY(y);
                tagpoint.set_iTouchType(event.getAction());
                mTagPoint_a_record.add(tagpoint);
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                tagpoint = new TagPoint();
                tagpoint.set_iTouchType(event.getAction());
                mTagPoint_a_record.add(tagpoint);
                tempTagLength.add(mTagPoint_a_record.size());
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
                case MotionEvent.ACTION_POINTER_DOWN:
                    Log.i("Finger", "Two");
                    break;
            }
            return true;
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
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final EditText input = new EditText(this);
        boolean saveResult = false;
        builder.setView(input);
        builder.setTitle("請輸入檔案名稱");
        builder.setPositiveButton("確認", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                Editable value = input.getText();
                if (value.toString().equals("")) {
                    SharedPreferences shared = getSharedPreferences("untitled", Context.MODE_PRIVATE);
                    int i = shared.getInt("number", 0);
                    i++;
                    savePicture("untitled" + i);
                    SharedPreferences.Editor editor = shared.edit();
                    editor.putInt("number", i);
                    editor.apply();
                } else {
                    savePicture(value.toString());
                }
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.dismiss();
            }
        });
        builder.show();
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

            try {
                //寫入檔案 圖的資訊 + 繪圖紀錄
                paintInfo = new PaintInfo();
                paintInfo.setImgName(fileName);
                paintInfo.setImgAuthor(userName.getText().toString());
                paintInfo.setEmail(userEmail.getText().toString());
                paintInfo.setImgGenerateTime(new Timestamp(System.currentTimeMillis()));
                paintInfo.setImgHeight(myView.getBitmap().getHeight());
                paintInfo.setImgWidth(myView.getBitmap().getWidth());
                paintInfoList = new ArrayList<>();
                ((ArrayList) paintInfoList).add(paintInfo);
                ((ArrayList) paintInfoList).add(mTagPoint_a_record);
                File logPath = new File(Environment.getExternalStorageDirectory() + "/bonniedraw/" + "Record/");
                if (!logPath.exists()) logPath.mkdirs();
                File fileLog = new File(Environment.getExternalStorageDirectory() + "/bonniedraw/" + "Record/" + fileName + ".bdw");
                FileOutputStream fosLog = new FileOutputStream(fileLog);
                ObjectOutputStream objOs = new ObjectOutputStream(fosLog);
                objOs.writeObject(paintInfoList);
                objOs.close();
                fosLog.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            Snackbar.make(mViewFreePaint, "儲存成功", Snackbar.LENGTH_LONG).show();
            return true;
        } else {
            Toast.makeText(this, "No SD Card detected", Toast.LENGTH_SHORT).show();
            return false;
        }

    }

    public void colorPicks() {
        new ColorPicker(this, this, "", Color.WHITE).show();
    }

    public void colorChanged(int color) {
        mPaint.setColor(color);
        TagPoint tagpoint = new TagPoint();
        tagpoint.set_fPosX(0);
        tagpoint.set_fPosY(0);
        tagpoint.set_iTouchType(EVENT_PAINT_CHANGED);
        tagpoint.set_iPaint(mPaint);
        mTagPoint_a_record.add(tagpoint);
    }

    @Override
    public void colorChanged(String key, int color) {
        colorChanged(color);
    }
    //End

    public void clearPaintSetting() {
        //清除選單，x=0是TEXT VIEW，從1開始
        mPaint.setXfermode(null);
        mPaint.setAlpha(0xFF);
        checkErase = false;
        checkCover = false;
        mPaint.setMaskFilter(null);
    }


    public void setOnclick() {
        //畫筆設定
        /*findViewById(R.id.id_btn_record).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ActivityCompat.checkSelfPermission(PaintActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                                REQUEST_EXTERNAL_STORAGE);
                    }
                } else {
                    Snackbar.make(mViewFreePaint, "開啟儲存紀錄(未實作)", Snackbar.LENGTH_LONG).show();
                }
            }
        });*/

        findViewById(R.id.id_btn_zoom).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });

        //撥放器
        btnAutoPlay.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewFreePaint.removeAllViews();
                myView = new MyView(PaintActivity.this);
                mViewFreePaint.addView(myView);
                miPointCount = mTagPoint_a_record.size();
                miPointCurrent = 0;
                mbAutoPlay = true;
                playStateBtn();
                if (miPointCount > 0) handler_Timer_Play.postDelayed(rb_play, 50);
            }
        });

        btnPlay.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewFreePaint.removeAllViews();
                myView = new MyView(PaintActivity.this);
                mViewFreePaint.addView(myView);
                miPointCount = mTagPoint_a_record.size();
                miPointCurrent = 0;
                playStateBtn();
                if (miPointCount > 0)
                    handler_Timer_Play.postDelayed(rb_play, 50);
            }
        });

        btnNext.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (miPointCount > 0) {
                    handler_Timer_Play.postDelayed(rb_play, 50);
                } else if (miPointCount == 0) {
                    btnRedo.setVisibility(View.VISIBLE);
                    btnUndo.setVisibility(View.VISIBLE);
                    btnNext.setVisibility(View.INVISIBLE);
                    btnPrevious.setVisibility(View.INVISIBLE);
                    Toast.makeText(PaintActivity.this, "播放完畢", Toast.LENGTH_SHORT).show();
                    btnNext.setText("下");
                    playState = false;
                }
            }
        });

        btnPrevious.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (miPointCurrent > 0) {
                    playState = true;
                    btnNext.setText("下");
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

        //設定項
        findViewById(R.id.id_btn_clear).setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                mbAutoPlay = false;
                playState = false;
                miPointCount = 0;
                miPointCurrent = 0;
                mbPainStarted = false;
                mViewFreePaint.removeAllViews();
                myView = new MyView(PaintActivity.this);
                mViewFreePaint.addView(myView);
                mTagPoint_a_record = new ArrayList<>();
                undoTagPoint_a_record.clear();
                tempTagLength.clear();
                myView.paths.clear();
                myView.undonePaths.clear();
            }
        });

        findViewById(R.id.id_btn_save).setOnClickListener(savePictureBtn);

        //顏色選擇
        findViewById(R.id.id_btn_colorpicker).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                colorPicks();
            }
        });
    }

    public void playStateBtn() {
        playState = true;
        btnNext.setVisibility(View.VISIBLE);
        btnPrevious.setVisibility(View.VISIBLE);
        btnRedo.setVisibility(View.INVISIBLE);
        btnUndo.setVisibility(View.INVISIBLE);
        btnNext.setText("下");
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

    }
}
