package com.sctw.bonniedraw.activity;

import android.content.Context;
import android.content.SharedPreferences;
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
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.sctw.bonniedraw.R;
import com.sctw.bonniedraw.paint.PathAndPaint;
import com.sctw.bonniedraw.paint.TagPoint;
import com.sctw.bonniedraw.utility.BDWFileReader;
import com.sctw.bonniedraw.utility.ConnectJson;
import com.sctw.bonniedraw.utility.GlobalVariable;
import com.sctw.bonniedraw.utility.PxDpConvert;
import com.sctw.bonniedraw.utility.TSnackbarCall;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SingleWorkActivity extends AppCompatActivity {
    private TextView mTextViewUserName, mTextViewWorkDescription, mTextViewWorkName, mTextViewGoodTotal, mTextViewCreateTime, mTextViewClass;
    private ImageView imgViewUserPhoto, mImgViewWorkImage;
    private ImageButton worksUserExtra, worksUserGood, worksUserMsg, worksUserShare, worksUserFollow;
    private Button mBtnPlayPause, mBtnNext, mBtnPrevious;
    SharedPreferences prefs;
    int wid;

    public static final boolean HWLAYER = true;
    public static final boolean SWLAYER = false;
    private static final String SKETCH_FILE_BDW = "/temp_play_use.bdw";
    private Handler mHandlerTimerPlay = new Handler();
    private FrameLayout mFrameLayoutFreePaint;
    private Paint mPaint;
    private MyView myView;
    private List<Integer> mListTempTagLength = new ArrayList<Integer>();
    private List<TagPoint> mListTagPoint;
    private static int miPointCount = 0, miPointCurrent = 0, miAutoPlayIntervalTime = 50;
    private Boolean mbAutoPlay = false, mbPlayState = false, mbPlaying = false;
    private int displayHeight, displayWidth, offsetX, offsetY, realPaint = 0, count;
    private Xfermode eraseEffect;
    private File backLoadBDW = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_work);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            wid = bundle.getInt("wid");
        }
        prefs = getSharedPreferences(GlobalVariable.MEMBER_PREFS, MODE_PRIVATE);
        mTextViewUserName = findViewById(R.id.textView_single_work_username);
        mTextViewWorkName = findViewById(R.id.textView_single_work_title);
        mTextViewWorkDescription = findViewById(R.id.textView_single_work_description);
        mTextViewGoodTotal = findViewById(R.id.textView_single_work_good_total);
        mTextViewCreateTime = findViewById(R.id.textView_single_work_create_time);
        mTextViewClass = findViewById(R.id.textView_single_work_user_class);
        imgViewUserPhoto = findViewById(R.id.imgView_single_work_user_photo);
        mImgViewWorkImage = findViewById(R.id.imgView_single_work_img);
        worksUserExtra = findViewById(R.id.imgBtn_single_work_extra);
        worksUserGood = findViewById(R.id.imgBtn_single_work_good);
        worksUserMsg = findViewById(R.id.imgBtn_single_work_msg);
        worksUserShare = findViewById(R.id.imgBtn_single_work_share);
        worksUserFollow = findViewById(R.id.imgBtn_single_work_follow);
        getSingleWork();

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(0xFF000000);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(13);
        eraseEffect = new PorterDuffXfermode(PorterDuff.Mode.DST_OUT);

        mBtnPlayPause = findViewById(R.id.imgBtn_single_work_play_pause);
        mBtnNext = findViewById(R.id.imgBtn_single_work_next);
        mBtnPrevious = findViewById(R.id.imgBtn_single_work_previous);
        mBtnPlayPause.setOnClickListener(startPlay);
    }

    public Button.OnClickListener startPlay = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            myView = new MyView(SingleWorkActivity.this);
            mImgViewWorkImage.setVisibility(View.GONE);
            mFrameLayoutFreePaint = (FrameLayout) findViewById(R.id.frameLayout_single_work);
            myView.setLayoutParams(new FrameLayout.LayoutParams(mImgViewWorkImage.getWidth(), mImgViewWorkImage.getHeight()));
            mFrameLayoutFreePaint.addView(myView);
            mbPlayState=true;
        }
    };

    public void checkSketch() {
        backLoadBDW = new File(getFilesDir().getPath() + SKETCH_FILE_BDW);
        if (backLoadBDW.exists()) {
            BDWFileReader reader = new BDWFileReader();
            reader.readFromFile(backLoadBDW);
            mListTagPoint = new ArrayList<>(reader.m_tagArray);
            miPointCount = mListTagPoint.size();
            miPointCurrent = 0;
            mbAutoPlay = true;
            if (miPointCount > 0) mHandlerTimerPlay.postDelayed(rb_play, 1);
        }
    }

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
                mPaint.setXfermode(eraseEffect);
                break;
        }
    }

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
        private ArrayList<PathAndPaint> paths = new ArrayList<>(20);
        private ArrayList<PathAndPaint> undonePaths = new ArrayList<>(20);
        private float mX, mY;
        private static final float TOUCH_TOLERANCE = 4;
        private int width;
        RectF rectF;

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

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        protected void onDraw(Canvas canvas) {
            canvas.save();
            canvas.drawColor(Color.WHITE);
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
            if (mbAutoPlay || mbPlayState) return true;//重播功能時不准畫

            float x = event.getX();
            float y = event.getY();

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
                mBitmap = Bitmap.createBitmap(displayWidth, displayHeight, Bitmap.Config.ARGB_8888);
                mCanvas = new Canvas(mBitmap);
                for (PathAndPaint p : paths) {
                    mCanvas.drawPath(p.get_mPath(), p.get_mPaint());
                }
                invalidate();
            }
        }

        //計算中間距離
        private float spacing(MotionEvent event) {
            float x = event.getX(0) - event.getX(1);
            float y = event.getY(0) - event.getY(1);
            return (float) Math.sqrt(x * x + y * y);
        }
    }

    public void getSingleWork() {
        JSONObject json = ConnectJson.querySingleWork(prefs, wid);
        Log.d("LOGIN JSON: ", json.toString());

        OkHttpClient okHttpClient = new OkHttpClient();
        RequestBody body = FormBody.create(ConnectJson.MEDIA_TYPE_JSON_UTF8, json.toString());
        Request request = new Request.Builder()
                .url(GlobalVariable.API_LINK_WORK_LIST)
                .post(body)
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                TSnackbarCall.showTSnackbar(findViewById(R.id.coordinatorLayout_work), "Fail Load");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    final JSONObject responseJSON = new JSONObject(response.body().string());
                    if (responseJSON.getInt("res") == 1) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //下載資料
                                try {
                                    System.out.println(responseJSON.toString());
                                    getWork(responseJSON.getJSONObject("work"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                Toast.makeText(SingleWorkActivity.this, "Download work successful", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    public void getWork(JSONObject data) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.TAIWAN);
            Date date = new Date(Long.valueOf(data.getString("updateDate")));
            mTextViewUserName.setText(data.getString("userName"));
            mTextViewWorkName.setText(data.getString("title"));
            mTextViewWorkDescription.setText(data.getString("description"));
            mTextViewGoodTotal.setText(String.format(getString(R.string.work_good_total), data.getString("isFollowing")));
            mTextViewCreateTime.setText(String.format(getString(R.string.work_release_time), sdf.format(date)));
            //imgViewUserPhoto data.getString("imagePath")
            try {
                URL url = new URL(GlobalVariable.API_LINK_GET_PHOTO + data.getString("imagePath"));
                Bitmap bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                mImgViewWorkImage.setImageBitmap(bitmap);
                mImgViewWorkImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
