package com.sctw.bonniedraw.activity;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.sctw.bonniedraw.R;
import com.sctw.bonniedraw.paint.Brushes;
import com.sctw.bonniedraw.paint.PaintView;
import com.sctw.bonniedraw.paint.TagPoint;
import com.sctw.bonniedraw.utility.BDWFileReader;
import com.sctw.bonniedraw.utility.ConnectJson;
import com.sctw.bonniedraw.utility.FullScreenDialog;
import com.sctw.bonniedraw.utility.GlobalVariable;
import com.sctw.bonniedraw.utility.LoadImageApp;
import com.sctw.bonniedraw.utility.OkHttpUtil;
import com.sctw.bonniedraw.utility.PxDpConvert;
import com.sctw.bonniedraw.utility.TSnackbarCall;
import com.sctw.bonniedraw.widget.BasePopup;

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

import static com.sctw.bonniedraw.paint.PaintView.STROKE_SACLE_VALUE;

public class SingleWorkActivity extends AppCompatActivity implements BasePopup.OnBasePopupClick {
    private TextView mTextViewUserName, mTextViewWorkDescription, mTextViewWorkName, mTextViewGoodTotal, mTextViewCreateTime, mTextViewClass;
    private ImageView mImgViewWorkImage;
    private CircleImageView mCircleImgUserPhoto;
    private ImageButton workUserExtra, workUserGood, workUserMsg, workUserShare, workUserFollow;
    private Button mBtnPlayPause, mBtnNext, mBtnPrevious;
    private String bdwPath = ""; //"bdwPath":
    private String workUid = "";
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
    private float mfLastPosX, mfLastPosY; //replay use
    private ArrayList<Integer> mListRecordInt;
    private BasePopup mBasePopup;

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
        mImgViewWorkImage = findViewById(R.id.imgView_single_work_img);
        mCircleImgUserPhoto = findViewById(R.id.circleImg_single_work_user_photo);
        workUserExtra = findViewById(R.id.imgBtn_single_work_extra);
        workUserGood = findViewById(R.id.imgBtn_single_work_good);
        workUserMsg = findViewById(R.id.imgBtn_single_work_msg);
        workUserShare = findViewById(R.id.imgBtn_single_work_share);
        workUserFollow = findViewById(R.id.imgBtn_single_work_follow);
        mFrameLayoutFreePaint = (FrameLayout) findViewById(R.id.frameLayout_single_work);
        miViewWidth = mPaintView.getMiWidth();
        mFileBDW = new File(getFilesDir().getPath() + PLAY_FILE_BDW);
        mBasePopup = new BasePopup(this, this);
        mBDWFileReader = new BDWFileReader();
        mListRecordInt = new ArrayList<>();
        getSingleWork();
        setOnClick();
        mBtnPlayPause = findViewById(R.id.imgBtn_single_work_play_pause);
        mBtnNext = findViewById(R.id.imgBtn_single_work_next);
        mBtnPrevious = findViewById(R.id.imgBtn_single_work_previous);
        mPaintView.initDefaultBrush(Brushes.get(getApplicationContext())[0]);
    }

    private void deleteWork() {
        OkHttpClient okHttpClient = OkHttpUtil.getInstance();
        JSONObject json = ConnectJson.deleteWork(prefs, wid);
        RequestBody body = FormBody.create(ConnectJson.MEDIA_TYPE_JSON_UTF8, json.toString());
        Request request = new Request.Builder()
                .url(GlobalVariable.API_LINK_DELETE_WORK)
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
                        finish();
                    }
                    Log.d("JSON RESPONE", responseJSON.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    private void setOnClick() {
        workUserExtra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final FullScreenDialog dialog = new FullScreenDialog(SingleWorkActivity.this, R.layout.dialog_single_work_extra);
                Button btnShareWork = dialog.findViewById(R.id.btn_extra_share);
                Button btnDeleteWork = dialog.findViewById(R.id.btn_extra_delete);
                Button btnEditWorkName = dialog.findViewById(R.id.btn_extra_edit_work);
                Button btnEditDescription = dialog.findViewById(R.id.btn_extra_edit_description);
                Button btnCancel = dialog.findViewById(R.id.btn_extra_cancel);

                //刪除作品確認
                btnDeleteWork.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(SingleWorkActivity.this);
                        builder.setMessage("確認要刪除這個作品嗎?");
                        builder.setPositiveButton(R.string.public_yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                deleteWork();
                                dialog.dismiss();
                            }
                        });
                        builder.setNegativeButton(R.string.public_no, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        dialog.dismiss();
                        builder.show();
                    }
                });
                if (!prefs.getString(GlobalVariable.API_UID, "null").equals(workUid)) {
                    dialog.findViewById(R.id.view_divier_extra_delete).setVisibility(View.GONE);
                    btnDeleteWork.setVisibility(View.GONE);
                }

                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });
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
            mPaintView.onClickPrevious();
            // 兩個UP差異點數 = 減少的點數 在移除最後第一個
            int count;
            if (mListRecordInt.size() > 1) {
                count = mListRecordInt.remove(mListRecordInt.size() - 1) - mListRecordInt.get(mListRecordInt.size() - 1);
                System.out.println(count);
            } else {
                count = mListRecordInt.remove(mListRecordInt.size() - 1);
                System.out.println(count);
            }
            System.out.println(miPointCurrent);
            miPointCount = miPointCount + count;
            miPointCurrent = miPointCurrent - count;

        } else if (miPointCurrent == 0) {
            TSnackbarCall.showTSnackbar(findViewById(R.id.coordinatorLayout_activity_paint), getString(R.string.play_frist));
        }
    }

    public void replayStart() {
        mFrameLayoutFreePaint.removeAllViews();
        mPaintView = new PaintView(SingleWorkActivity.this, true);
        mPaintView.initDefaultBrush(Brushes.get(getApplicationContext())[0]);
        mFrameLayoutFreePaint.addView(mPaintView);
        mPaintView.mListTagPoint = new ArrayList<>(mBDWFileReader.m_tagArray);
        miPointCount = mPaintView.mListTagPoint.size();
        miPointCurrent = 0;
        if (miPointCount > 0) mHandlerTimerPlay.postDelayed(rb_play, miAutoPlayIntervalTime);
        mbAutoPlay = true;
    }

    public void startPlay(View view) {
        if (checkSketch()) {
            mImgViewWorkImage.setVisibility(View.GONE);
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
                        if (tagpoint.get_iBrush() != 0) {
                            mPaintView.setBrush(Brushes.get(getApplicationContext())[tagpoint.get_iBrush()]);
                        }
                        if (tagpoint.get_iColor() != 0) {
                            mPaintView.setDrawingColor(tagpoint.get_iColor());
                        }
                        if (tagpoint.get_iSize() != 0) {
                            mPaintView.setDrawingScaledSize(PxDpConvert.formatToDisplay(tagpoint.get_iSize() / STROKE_SACLE_VALUE, miViewWidth));
                        }
                        mfLastPosX = PxDpConvert.formatToDisplay(tagpoint.get_iPosX(), miViewWidth);
                        mfLastPosY = PxDpConvert.formatToDisplay(tagpoint.get_iPosY(), miViewWidth);
                        mPaintView.usePlayHnad(MotionEvent.obtain(0, 0, MotionEvent.ACTION_DOWN, mfLastPosX, mfLastPosY, 0));
                        break;
                    case MotionEvent.ACTION_MOVE:
                        //開始畫 記錄每一個時間點 即可模擬回去
                        mfLastPosX = PxDpConvert.formatToDisplay(tagpoint.get_iPosX(), miViewWidth);
                        mfLastPosY = PxDpConvert.formatToDisplay(tagpoint.get_iPosY(), miViewWidth);
                        mPaintView.usePlayHnad(MotionEvent.obtain(0, tagpoint.get_iTime(), MotionEvent.ACTION_MOVE, mfLastPosX, mfLastPosY, 0));
                        break;
                    case MotionEvent.ACTION_UP:
                        mPaintView.usePlayHnad(MotionEvent.obtain(0, 0, MotionEvent.ACTION_UP, mfLastPosX, mfLastPosY, 0));
                        mListRecordInt.add(miPointCurrent + 1);
                        mbPlaying = false;
                        brun = false;
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

        OkHttpClient okHttpClient = OkHttpUtil.getInstance();
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
        OkHttpClient okHttpClient = OkHttpUtil.getInstance();
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
            workUid = data.getString("userId");
            getBDW();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public void onBasePopupClick() {
        finish();
    }
}
