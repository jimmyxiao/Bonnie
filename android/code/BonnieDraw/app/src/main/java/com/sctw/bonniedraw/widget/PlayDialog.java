package com.sctw.bonniedraw.widget;


import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.sctw.bonniedraw.R;
import com.sctw.bonniedraw.paint.Brushes;
import com.sctw.bonniedraw.paint.PaintView;
import com.sctw.bonniedraw.paint.TagPoint;
import com.sctw.bonniedraw.utility.BDWFileReader;
import com.sctw.bonniedraw.utility.ConnectJson;
import com.sctw.bonniedraw.utility.FullScreenDialog;
import com.sctw.bonniedraw.utility.GlideApp;
import com.sctw.bonniedraw.utility.GlideAppModule;
import com.sctw.bonniedraw.utility.GlobalVariable;
import com.sctw.bonniedraw.utility.OkHttpUtil;
import com.sctw.bonniedraw.utility.PxDpConvert;

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

import static android.content.Context.MODE_PRIVATE;
import static com.sctw.bonniedraw.paint.PaintView.STROKE_SACLE_VALUE;

/**
 * Created by Fatorin on 2017/11/7.
 */

public class PlayDialog extends DialogFragment {
    private TextView mTvUserName, mTvWorkDescription, mTvWorkName, mTvGoodTotal, mTvCreateTime, mTvUserFollow, mTvPlayTotal;
    private ProgressBar mProgressBar;
    private ImageView mImgViewWorkImage;
    private CircleImageView mCircleImgUserPhoto;
    private ImageButton mBtnExtra, mBtnGood, mBtnMsg, mBtnShare, mBtnCollection, mBtnPlay, mBtnPause, mBtnNext, mBtnPrevious;
    private String bdwPath = ""; //"bdwPath":
    private String workUid = "";
    SharedPreferences prefs;
    private int wid, uid, miFollow;
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
    private boolean mbLike, mbCollection;

    public static PlayDialog newInstance(int wid) {
        PlayDialog f = new PlayDialog();
        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putInt("wid", wid);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.FullScreenDialog);
        wid = getArguments().getInt("wid");
        prefs = getActivity().getSharedPreferences(GlobalVariable.MEMBER_PREFS, MODE_PRIVATE);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        getDialog().setCanceledOnTouchOutside(true);
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        View rootView = inflater.inflate(R.layout.dialog_single_work, container, false);
        //Do something
        final Window window = getDialog().getWindow();
        window.setBackgroundDrawableResource(R.color.Transparent);
        window.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.BOTTOM;
        wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
        wlp.height = WindowManager.LayoutParams.MATCH_PARENT;
        window.setAttributes(wlp);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mPaintView = new PaintView(getContext());
        mTvUserName = view.findViewById(R.id.textView_single_work_username);
        mTvWorkName = view.findViewById(R.id.textView_single_work_title);
        mTvWorkDescription = view.findViewById(R.id.textView_single_work_description);
        mTvGoodTotal = view.findViewById(R.id.textView_single_work_good_total);
        mTvCreateTime = view.findViewById(R.id.textView_single_work_create_time);
        mTvPlayTotal = view.findViewById(R.id.textView_single_work_current_total);
        mProgressBar = view.findViewById(R.id.progressBar_single_work);
        mImgViewWorkImage = view.findViewById(R.id.imgView_single_work_img);
        mCircleImgUserPhoto = view.findViewById(R.id.circleImg_single_work_user_photo);
        mBtnExtra = view.findViewById(R.id.imgBtn_single_work_extra);
        mBtnGood = view.findViewById(R.id.imgBtn_single_work_good);
        mBtnMsg = view.findViewById(R.id.imgBtn_single_work_msg);
        mBtnShare = view.findViewById(R.id.imgBtn_single_work_share);
        mBtnCollection = view.findViewById(R.id.imgBtn_single_work_collection);
        mBtnPlay = view.findViewById(R.id.imgBtn_single_work_play);
        mBtnPause = view.findViewById(R.id.imgBtn_single_work_pause);
        mBtnNext = view.findViewById(R.id.imgBtn_single_work_next);
        mBtnPrevious = view.findViewById(R.id.imgBtn_single_work_previous);
        mFrameLayoutFreePaint = (FrameLayout) view.findViewById(R.id.frameLayout_single_work);
        miViewWidth = mPaintView.getMiWidth();
        mFileBDW = new File(getActivity().getFilesDir().getPath() + PLAY_FILE_BDW);
        mBDWFileReader = new BDWFileReader();
        mListRecordInt = new ArrayList<>();
        getSingleWork(false);
        setOnClick();
        mPaintView.initDefaultBrush(Brushes.get(getActivity().getApplicationContext())[0]);
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
                ToastUtil.createToastWindow(getContext(), "讀取錯誤");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    final JSONObject responseJSON = new JSONObject(response.body().string());
                    if (responseJSON.getInt("res") == 1) {
                        dismiss();
                    }
                    Log.d("JSON RESPONE", responseJSON.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    private void setOnClick() {
        mBtnExtra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final FullScreenDialog dialog = new FullScreenDialog(getContext(), R.layout.dialog_single_work_extra);
                Button btnShareWork = dialog.findViewById(R.id.btn_extra_share);
                Button btnDeleteWork = dialog.findViewById(R.id.btn_extra_delete);
                Button btnEditWorkName = dialog.findViewById(R.id.btn_extra_edit_work);
                Button btnEditDescription = dialog.findViewById(R.id.btn_extra_edit_description);
                Button btnCancel = dialog.findViewById(R.id.btn_extra_cancel);

                //刪除作品確認
                btnDeleteWork.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
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

        mBtnMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MessageDialog messageDialog = MessageDialog.newInstance(wid);
                messageDialog.show(getFragmentManager(), "TAG");
            }
        });

        mBtnGood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mbLike) {
                    mBtnGood.setSelected(false);
                    setLike(0, wid);
                } else {
                    mBtnGood.setSelected(true);
                    setLike(1, wid);
                }
            }
        });

        mBtnCollection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mbCollection) {
                    mBtnCollection.setSelected(false);
                    setCollection(0, wid);
                } else {
                    mBtnCollection.setSelected(true);
                    setCollection(1, wid);
                }
            }
        });

        /*mBtnUserFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (miFollow == 1) {
                    mBtnUserFollow.setSelected(false);
                    setFollow(0, uid);
                } else {
                    mBtnUserFollow.setSelected(true);
                    setFollow(1, uid);
                }
            }
        });*/

        mBtnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkSketch() && !mbAutoPlay) {
                    mImgViewWorkImage.setVisibility(View.GONE);
                    replayStart();
                    mBtnNext.setVisibility(View.VISIBLE);
                    mBtnPrevious.setVisibility(View.VISIBLE);
                    mBtnPlay.setVisibility(View.GONE);
                    mBtnPause.setVisibility(View.VISIBLE);
                } else {
                    if (miPointCount > 0)
                        mHandlerTimerPlay.postDelayed(rb_play, miAutoPlayIntervalTime);
                    mbAutoPlay = true;
                }
            }
        });

        mBtnPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mbAutoPlay = false;
                mBtnPlay.setVisibility(View.VISIBLE);
                mBtnPause.setVisibility(View.GONE);
            }
        });

        mBtnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (miPointCount > 0) {
                    mHandlerTimerPlay.postDelayed(rb_play, miAutoPlayIntervalTime);
                } else if (miPointCount == 0) {
                    ToastUtil.createToastWindow(getContext(), getString(R.string.play_end));
                }
            }
        });

        mBtnPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mbPlaying) {
                    ToastUtil.createToastWindow(getContext(), getString(R.string.play_wait));
                } else if (miPointCurrent > 0) {
                    mPaintView.onClickPrevious();
                    // 兩個UP差異點數 = 減少的點數 在移除最後第一個
                    int count;
                    if (mListRecordInt.size() > 1) {
                        count = mListRecordInt.remove(mListRecordInt.size() - 1) - mListRecordInt.get(mListRecordInt.size() - 1);
                    } else {
                        count = mListRecordInt.remove(mListRecordInt.size() - 1);
                    }
                    miPointCount = miPointCount + count;
                    miPointCurrent = miPointCurrent - count;

                } else if (miPointCurrent == 0) {
                    ToastUtil.createToastWindow(getContext(), getString(R.string.play_frist));
                }
            }
        });
    }

    private void replayStart() {
        mFrameLayoutFreePaint.removeAllViews();
        mPaintView = new PaintView(getContext(), true);
        mPaintView.initDefaultBrush(Brushes.get(getActivity().getApplicationContext())[0]);
        mFrameLayoutFreePaint.addView(mPaintView);
        mPaintView.mListTagPoint = new ArrayList<>(mBDWFileReader.m_tagArray);
        miPointCount = mPaintView.mListTagPoint.size();
        miPointCurrent = 0;
        if (miPointCount > 0) mHandlerTimerPlay.postDelayed(rb_play, miAutoPlayIntervalTime);
        mbAutoPlay = true;
    }

    private boolean checkSketch() {
        if (mFileBDW.exists()) {
            mBDWFileReader.readFromFile(mFileBDW);
            mPaintView.mListTagPoint = new ArrayList<>(mBDWFileReader.m_tagArray);
            return true;
        } else {
            ToastUtil.createToastWindow(getContext(), "讀取錯誤");
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
                            int paintId = mPaintView.selectPaint(tagpoint.get_iBrush());
                            mPaintView.setBrush(Brushes.get(getActivity().getApplicationContext())[paintId]);
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
                int progress = 100 * miPointCurrent / mPaintView.mListTagPoint.size();
                mTvPlayTotal.setText(String.format(Locale.TAIWAN, "%d/ 100%%", progress));
                mProgressBar.setProgress(progress);

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

    private void getSingleWork(final boolean refresh) {
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
                ToastUtil.createToastWindow(getContext(), "讀取失敗");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    final JSONObject responseJSON = new JSONObject(response.body().string());
                    if (responseJSON.getInt("res") == 1) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //下載資料
                                try {
                                    getWork(responseJSON.getJSONObject("work"), refresh);
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

    private void getBDW() {
        OkHttpClient okHttpClient = OkHttpUtil.getInstance();
        Request request = new Request.Builder()
                .url(GlobalVariable.API_LINK_GET_FILE + bdwPath)
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                ToastUtil.createToastWindow(getContext(), "讀取失敗");
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

    private void getWork(JSONObject data, boolean refresh) {
        try {
            String profilePictureUrl = "";
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.TAIWAN);
            Date date = new Date(Long.valueOf(data.getString("updateDate")));
            mTvUserName.setText(data.getString("userName"));
            mTvWorkName.setText(data.getString("title"));
            mTvWorkDescription.setText(data.getString("description"));
            mTvGoodTotal.setText(String.format(getString(R.string.work_good_total), data.getInt("likeCount")));
            mTvCreateTime.setText(String.format(getString(R.string.work_release_time), sdf.format(date)));
            uid = data.getInt("userId");
            mbLike = data.getBoolean("like");
            mbCollection = data.getBoolean("collection");
            miFollow = data.getInt("isFollowing");
            if (mbLike) {
                mBtnGood.setPressed(true);
            } else {
                mBtnGood.setPressed(false);
            }
            /*if (miFollow == 1) {
                mBtnUserFollow.setPressed(true);
            } else {
                mBtnUserFollow.setPressed(false);
            }*/
            if (mbCollection) {
                mBtnCollection.setPressed(true);
            } else {
                mBtnCollection.setPressed(false);
            }

            GlideApp.with(getContext())
                    .load(GlobalVariable.API_LINK_GET_FILE + data.getString("imagePath"))
                    .fitCenter()
                    .error(R.drawable.loading_fail)
                    .into(mImgViewWorkImage);
            GlideApp.with(getContext())
                    .load(GlobalVariable.API_LINK_GET_FILE + data.getString("profilePicture"))
                    .apply(GlideAppModule.getUserOptions())
                    .into(mCircleImgUserPhoto);
            bdwPath = data.getString("bdwPath");
            workUid = data.getString("userId");
            if (!refresh) {
                getBDW();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void setLike(final int fn, int wid) {
        // fn = 1 點讚, 0 取消讚
        JSONObject json = ConnectJson.setLike(prefs, fn, wid);
        Log.d("LOGIN JSON: ", json.toString());
        OkHttpClient okHttpClient = OkHttpUtil.getInstance();
        RequestBody body = FormBody.create(ConnectJson.MEDIA_TYPE_JSON_UTF8, json.toString());
        Request request = new Request.Builder()
                .url(GlobalVariable.API_LINK_SET_LIKE)
                .post(body)
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("Get List Works", "Fail");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    final JSONObject responseJSON = new JSONObject(response.body().string());
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                if (responseJSON.getInt("res") == 1) {
                                    //點讚成功或刪除成功 0 = DEL , 1 = ADD
                                    getSingleWork(true);
                                } else {
                                    //點讚失敗或刪除失敗
                                    switch (fn) {
                                        case 0:
                                            //del
                                            mBtnGood.setSelected(true);
                                            break;
                                        case 1:
                                            //add
                                            mBtnGood.setSelected(false);
                                            break;
                                    }
                                }
                                Log.d("LOGIN JSON", responseJSON.toString());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void setCollection(final int fn, int wid) {
        // fn = 1 點讚, 0 取消讚
        JSONObject json = ConnectJson.setCollection(prefs, fn, wid);
        Log.d("LOGIN JSON: ", json.toString());
        OkHttpClient okHttpClient = OkHttpUtil.getInstance();
        RequestBody body = FormBody.create(ConnectJson.MEDIA_TYPE_JSON_UTF8, json.toString());
        Request request = new Request.Builder()
                .url(GlobalVariable.API_LINK_SET_COLLECTION)
                .post(body)
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("Get List Works", "Fail");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    final JSONObject responseJSON = new JSONObject(response.body().string());
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                if (responseJSON.getInt("res") == 1) {
                                    //點讚成功或刪除成功 0 = DEL , 1 = ADD
                                    getSingleWork(true);
                                } else {
                                    //點讚失敗或刪除失敗
                                    switch (fn) {
                                        case 0:
                                            //del
                                            mBtnCollection.setSelected(true);
                                            break;
                                        case 1:
                                            //add
                                            mBtnCollection.setSelected(false);
                                            break;
                                    }
                                }
                                Log.d("LOGIN JSON", responseJSON.toString());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void setFollow(final int fn, int followId) {
        // fn = 1 點讚, 0 取消讚
        JSONObject json = ConnectJson.setFollow(prefs, fn, followId);
        Log.d("LOGIN JSON: ", json.toString());
        OkHttpClient okHttpClient = OkHttpUtil.getInstance();
        RequestBody body = FormBody.create(ConnectJson.MEDIA_TYPE_JSON_UTF8, json.toString());
        Request request = new Request.Builder()
                .url(GlobalVariable.API_LINK_SET_FOLLOW)
                .post(body)
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("Get List Works", "Fail");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    final JSONObject responseJSON = new JSONObject(response.body().string());
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                if (responseJSON.getInt("res") == 1) {
                                    //點讚成功或刪除成功 0 = DEL , 1 = ADD
                                    getSingleWork(true);
                                } else {
                                    //點讚失敗或刪除失敗
                                    switch (fn) {
                                        case 0:
                                            //del
                                            //mBtnUserFollow.setSelected(true);
                                            break;
                                        case 1:
                                            //add
                                            //mBtnUserFollow.setSelected(false);
                                            break;
                                    }
                                }
                                Log.d("LOGIN JSON", responseJSON.toString());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onStop() {
        mHandlerTimerPlay.removeCallbacks(rb_play);
        mPaintView.release();
        mPaintView.clear();
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}