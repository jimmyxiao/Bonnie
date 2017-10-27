package com.sctw.bonniedraw.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.sctw.bonniedraw.R;
import com.sctw.bonniedraw.paint.PaintView;
import com.sctw.bonniedraw.paint.TagPoint;
import com.sctw.bonniedraw.utility.BDWFileReader;
import com.sctw.bonniedraw.utility.ConnectJson;
import com.sctw.bonniedraw.utility.GlobalVariable;
import com.sctw.bonniedraw.utility.LoadImageApp;
import com.sctw.bonniedraw.utility.PxDpConvert;
import com.sctw.bonniedraw.utility.TSnackbarCall;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
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
    private CircleImageView mCircleImgUserPhoto;
    private ImageButton worksUserExtra, worksUserGood, worksUserMsg, worksUserShare, worksUserFollow;
    private Button mBtnPlayPause, mBtnNext, mBtnPrevious;
    private String bdwPath = ""; //"bdwPath":
    SharedPreferences prefs;
    private int wid;
    private static final String PLAY_FILE_BDW = "/temp_play_use.bdw";
    private Handler mHandlerTimerPlay = new Handler();
    private FrameLayout mFrameLayoutFreePaint;
    private PaintView mPaintView;
    private static int miPointCount = 0, miPointCurrent = 0, miAutoPlayIntervalTime = 50;
    private boolean mbPlaying = false, mbAutoPlay = false;
    private int miViewWidth;
    private File mFileBDW;
    private BDWFileReader mBDWFileReader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_work);
        prefs = getSharedPreferences(GlobalVariable.MEMBER_PREFS, MODE_PRIVATE);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            wid = bundle.getInt("wid");
        }

        mPaintView = new PaintView(this);
        mTextViewUserName = findViewById(R.id.textView_single_work_username);
        mTextViewWorkName = findViewById(R.id.textView_single_work_title);
        mTextViewWorkDescription = findViewById(R.id.textView_single_work_description);
        mTextViewGoodTotal = findViewById(R.id.textView_single_work_good_total);
        mTextViewCreateTime = findViewById(R.id.textView_single_work_create_time);
        mTextViewClass = findViewById(R.id.textView_single_work_user_class);
        imgViewUserPhoto = findViewById(R.id.circleImg_single_work_user_photo);
        mImgViewWorkImage = findViewById(R.id.imgView_single_work_img);
        mCircleImgUserPhoto = findViewById(R.id.circleImg_single_work_user_photo);
        worksUserExtra = findViewById(R.id.imgBtn_single_work_extra);
        worksUserGood = findViewById(R.id.imgBtn_single_work_good);
        worksUserMsg = findViewById(R.id.imgBtn_single_work_msg);
        worksUserShare = findViewById(R.id.imgBtn_single_work_share);
        worksUserFollow = findViewById(R.id.imgBtn_single_work_follow);
        mFrameLayoutFreePaint = (FrameLayout) findViewById(R.id.frameLayout_single_work);
        miViewWidth = mPaintView.getMiWidth();
        mFileBDW = new File(getFilesDir().getPath() + PLAY_FILE_BDW);
        mBDWFileReader = new BDWFileReader();
        getSingleWork();
        mBtnPlayPause = findViewById(R.id.imgBtn_single_work_play_pause);
        mBtnNext = findViewById(R.id.imgBtn_single_work_next);
        mBtnPrevious = findViewById(R.id.imgBtn_single_work_previous);
    }

    public void next(View view) {
        if (miPointCount > 0) {
            mHandlerTimerPlay.postDelayed(rb_play, miAutoPlayIntervalTime);
        } else if (miPointCount == 0) {
            TSnackbarCall.showTSnackbar(findViewById(R.id.coordinatorLayout_work), getString(R.string.play_end));
        }
    }

    public void previous(View view) {
        if (mbPlaying) {
            TSnackbarCall.showTSnackbar(findViewById(R.id.coordinatorLayout_activity_paint), getString(R.string.play_wait));
        } else if (miPointCurrent > 0) {
            for (int x = 0; x <= mPaintView.onClickPrevious() - 1; x++) {
                miPointCount++;
                miPointCurrent--;
            }
            Log.d("miPointCurrent", String.valueOf(miPointCurrent));
        } else if (miPointCurrent == 0) {
            TSnackbarCall.showTSnackbar(findViewById(R.id.coordinatorLayout_activity_paint), getString(R.string.play_frist));
        }
    }

    public void replayStart() {
        mPaintView.mListTagPoint = new ArrayList<>(mBDWFileReader.m_tagArray);
        miPointCount = mPaintView.mListTagPoint.size();
        miPointCurrent = 0;
        if (miPointCount > 0) mHandlerTimerPlay.postDelayed(rb_play, miAutoPlayIntervalTime);
        mbAutoPlay = true;
    }

    public void startPlay(View view) {
        if (checkSketch()) {
            mImgViewWorkImage.setVisibility(View.GONE);
            mFrameLayoutFreePaint.removeAllViews();
            mPaintView = new PaintView(this, true);
            mFrameLayoutFreePaint.addView(mPaintView);
            replayStart();
        }
    }

    public boolean checkSketch() {
        if (mFileBDW.exists()) {
            mBDWFileReader.readFromFile(mFileBDW);
            mPaintView.mListTagPoint = new ArrayList<>(mBDWFileReader.m_tagArray);
            return true;
        } else {
            TSnackbarCall.showTSnackbar(findViewById(R.id.coordinatorLayout_work), "讀取檔案失敗");
            return false;
        }
    }

    private Runnable rb_play = new Runnable() {
        public void run() {
            boolean brun = true;
            if (miPointCount > 0) {
                TagPoint tagpoint = mPaintView.mListTagPoint.get(miPointCurrent);
                switch (tagpoint.get_iAction() - 1) {
                    case MotionEvent.ACTION_DOWN:
                        mbPlaying = true;
                        if (tagpoint.get_iColor() != 0) {
                            mPaintView.mPaint.setColor(tagpoint.get_iColor());
                        }
                        if (tagpoint.get_iSize() != 0) {
                            mPaintView.mPaint.setStrokeWidth(PxDpConvert.formatToDisplay(tagpoint.get_iSize(), miViewWidth));
                        }
                        if (tagpoint.get_iBrush() != 0) {
                            //mPaintView.changePaint(tagpoint.getiPaintType());
                        }
                        //mPaintView.touch_start(PxDpConvert.formatToDisplay(tagpoint.get_iPosX(), miViewWidth), PxDpConvert.formatToDisplay(tagpoint.get_iPosY(), miViewWidth));
                        mPaintView.invalidate();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        //mPaintView.touch_move(PxDpConvert.formatToDisplay(tagpoint.get_iPosX(), miViewWidth), PxDpConvert.formatToDisplay(tagpoint.get_iPosY(), miViewWidth));
                        mPaintView.invalidate();
                        break;
                    case MotionEvent.ACTION_UP:
                        mbPlaying = false;
                        brun = false;
                        //mPaintView.touch_up();
                        mPaintView.invalidate();
                        break;
                }
                miPointCount--;
                miPointCurrent++;

                if (brun) {
                    mHandlerTimerPlay.postDelayed(rb_play, miAutoPlayIntervalTime);
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

    public void getBDW() {
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .url(GlobalVariable.API_LINK_GET_FILE + bdwPath)
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                TSnackbarCall.showTSnackbar(findViewById(R.id.coordinatorLayout_work), "Fail Load");
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                InputStream is = null;
                byte[] buf = new byte[2048];
                int len = 0;
                FileOutputStream fos = null;
                try {
                    is = response.body().byteStream();
                    File file = mFileBDW;
                    fos = new FileOutputStream(file);
                    while ((len = is.read(buf)) != -1) {
                        fos.write(buf, 0, len);
                    }
                    fos.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (is != null) is.close();
                    if (fos != null) fos.close();
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
            ImageLoader.getInstance().displayImage(GlobalVariable.API_LINK_GET_FILE + data.getString("imagePath"), mImgViewWorkImage, LoadImageApp.optionsWorkImg);
            ImageLoader.getInstance().displayImage(GlobalVariable.API_LINK_GET_FILE + data.getString("profilePicture"), mCircleImgUserPhoto, LoadImageApp.optionsUserImg);
            bdwPath = data.getString("bdwPath");
            getBDW();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
