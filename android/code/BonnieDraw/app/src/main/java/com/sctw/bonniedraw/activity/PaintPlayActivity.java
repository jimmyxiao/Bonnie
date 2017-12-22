package com.sctw.bonniedraw.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import com.sctw.bonniedraw.R;
import com.sctw.bonniedraw.paint.Brush;
import com.sctw.bonniedraw.paint.Brushes;
import com.sctw.bonniedraw.paint.PaintView;
import com.sctw.bonniedraw.paint.TagPoint;
import com.sctw.bonniedraw.utility.BDWFileReader;
import com.sctw.bonniedraw.utility.FullScreenDialog;
import com.sctw.bonniedraw.utility.GlobalVariable;
import com.sctw.bonniedraw.utility.PxDpConvert;
import com.sctw.bonniedraw.widget.ToastUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

public class PaintPlayActivity extends AppCompatActivity {
    private static final String BACKUP_FILE_BDW = "/backup.bdw";
    private Handler mHandlerTimerPlay = new Handler();
    private TextView mTextViewPlayProgress, mTvPlaySpeed;
    private ImageButton mBtnBack, mBtnAutoPlay, mBtnNext, mBtnPrevious, mImgBtnReplay, mBtnPause, mBtnZoom, mBtnFast, mBtnSlow;
    private int miPointCount = 0, miPointCurrent = 0, miAutoPlayIntervalTime = 16, miSpeedCount = 0;
    private FrameLayout mFrameLayoutFreePaint;
    private PaintView mPaintView;
    private int miViewWidth;
    private boolean mbPlaying = false, mbAutoPlay = false;
    private BDWFileReader mBDWFileReader;
    private int mCurrentBrushId = 3;
    private float mfLastPosX, mfLastPosY; //replay use
    private ArrayList<Integer> mListRecordInt;
    private boolean mbStop = false, mbHint = false;
    private SharedPreferences mPrefs;
    private File mFileBDW;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paint_play);
        mPrefs = getSharedPreferences(GlobalVariable.MEMBER_PREFS, MODE_PRIVATE);
        mPaintView = new PaintView(this, true);
        mFrameLayoutFreePaint = (FrameLayout) findViewById(R.id.frameLayout_freepaint);
        mFrameLayoutFreePaint.addView(mPaintView);
        mListRecordInt = new ArrayList<>();
        mTextViewPlayProgress = findViewById(R.id.textView_paint_play_title);
        mTvPlaySpeed = findViewById(R.id.textView_play_speed);
        mBtnAutoPlay = findViewById(R.id.imgBtn_autoplay);
        mBtnNext = findViewById(R.id.imgBtn_next);
        mBtnPrevious = findViewById(R.id.imgBtn_previous);
        mBtnBack = findViewById(R.id.imgBtn_paint_back);
        mBtnPause = findViewById(R.id.imgBtn_pause);
        mBtnZoom = findViewById(R.id.btn_paint_zoom);
        mBtnFast = findViewById(R.id.imgBtn_fast);
        mBtnSlow = findViewById(R.id.imgBtn_slow);
        mImgBtnReplay = findViewById(R.id.imgBtn_replay);
        miViewWidth = mPaintView.getMiWidth();
        mFileBDW = new File(getFilesDir().getPath() + BACKUP_FILE_BDW);
        mBDWFileReader = new BDWFileReader();
        if (mFileBDW.exists()) mBDWFileReader.readFromFile(mFileBDW);
        Brush brush = Brushes.get(getApplicationContext())[mCurrentBrushId];
        mPaintView.initDefaultBrush(brush);
        setImgBtnOnClick();
        showSpeed();
    }

    private Runnable rb_play = new Runnable() {
        public void run() {
            if (!mbStop) {
                boolean brun = true;
                if (miPointCount > 0) {
                    TagPoint tagpoint = mPaintView.mListTagPoint.get(miPointCurrent);
                    switch (tagpoint.get_iAction() - 1) {
                        case MotionEvent.ACTION_DOWN:
                            mbPlaying = true;
                            if (tagpoint.get_iBrush() == 6) {
                                mPaintView.setDrawingBgColor(tagpoint.get_iColor());
                            } else if (tagpoint.get_iBrush() != 0) {
                                int paintId = mPaintView.selectPaint(tagpoint.get_iBrush());
                                mPaintView.setBrush(Brushes.get(getApplicationContext())[paintId]);
                                System.out.println("筆代號 = " + paintId);
                            } else {
                                mPaintView.setBrush(Brushes.get(getApplicationContext())[9]);
                            }
                            if (tagpoint.get_iColor() != 0) {
                                mPaintView.setDrawingColor(tagpoint.get_iColor());
                            }
                            if (tagpoint.get_iSize() != 0) {
                                mPaintView.setDrawingSize((int) PxDpConvert.formatToDisplay(tagpoint.get_iSize(), miViewWidth));
                            }
                            if (tagpoint.get_iReserved() != 0) {
                                mPaintView.setDrawingAlpha(tagpoint.get_iReserved() / 100.0f);
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
                            mfLastPosX = PxDpConvert.formatToDisplay(tagpoint.get_iPosX(), miViewWidth);
                            mfLastPosY = PxDpConvert.formatToDisplay(tagpoint.get_iPosY(), miViewWidth);
                            mPaintView.usePlayHnad(MotionEvent.obtain(0, 0, MotionEvent.ACTION_UP, mfLastPosX, mfLastPosY, 0));
                            mListRecordInt.add(miPointCurrent + 1);
                            mbPlaying = false;
                            brun = false;
                            break;
                    }
                    miPointCount--;
                    miPointCurrent++;
                    showProgress();

                    if (brun) {
                        mHandlerTimerPlay.postDelayed(rb_play, miAutoPlayIntervalTime);
                    } else {
                        if (mbAutoPlay) {
                            mHandlerTimerPlay.postDelayed(rb_play, miAutoPlayIntervalTime);
                        }
                    }
                } else {
                    mbAutoPlay = false;
                    mBtnPause.setVisibility(View.GONE);
                    mBtnAutoPlay.setVisibility(View.VISIBLE);
                    mImgBtnReplay.setVisibility(View.VISIBLE);
                }
            }

        }
    };

    private void showSpeed() {
        int absCount = Math.abs(miSpeedCount);
        if (miSpeedCount >= 0) {
            mTvPlaySpeed.setText(getString(R.string.play_speed) + (int)Math.pow(2, absCount) + "x");
        } else if (miSpeedCount < 0) {
            mTvPlaySpeed.setText(getString(R.string.play_speed) + "1/" + (int)Math.pow(2, absCount) + "x");
        }
    }

    private void showProgress(){
        if(miPointCurrent>0){
            mTextViewPlayProgress.setText(String.format(Locale.TAIWAN, " %d%%", 100 * miPointCurrent / mPaintView.mListTagPoint.size()));
        }
    }

    private void setImgBtnOnClick() {
        mBtnFast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (miSpeedCount < 4) {
                    miSpeedCount++;
                    miAutoPlayIntervalTime = miAutoPlayIntervalTime / 2;
                    showSpeed();
                }
            }
        });

        mBtnSlow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (miSpeedCount > -4) {
                    miSpeedCount--;
                    miAutoPlayIntervalTime = miAutoPlayIntervalTime * 2;
                    showSpeed();
                }
            }
        });

        mBtnZoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mPaintView.mbZoomMode) {
                    mPaintView.mbZoomMode = true;
                    mBtnZoom.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.zoom_down_icon));
                    if (!mbHint) {
                        mbHint = true;
                        mPrefs.edit().putBoolean("zoomhint", true).apply();
                        final FullScreenDialog dialog = new FullScreenDialog(PaintPlayActivity.this, R.layout.item_hint_zoom);
                        ConstraintLayout layout = dialog.findViewById(R.id.ll_hint_zoom);
                        layout.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });
                        dialog.show();
                    }
                } else {
                    mPaintView.mbZoomMode = false;
                    mBtnZoom.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.zoom_up_icon));
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

        mBtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mBtnPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBtnAutoPlay.setVisibility(View.VISIBLE);
                mbAutoPlay = false;
            }
        });

        mBtnNext.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (miPointCurrent == 0) {
                    ToastUtil.createToastWindow(PaintPlayActivity.this, getString(R.string.please_touch_play_start), PxDpConvert.getSystemHight(getApplicationContext()) / 3);
                } else if (miPointCount > 0) {
                    mHandlerTimerPlay.postDelayed(rb_play, miAutoPlayIntervalTime);
                } else if (miPointCount == 0) {
                    ToastUtil.createToastWindow(PaintPlayActivity.this, getString(R.string.play_end), PxDpConvert.getSystemHight(getApplicationContext()) / 3);
                }
                showProgress();
            }
        });

        mBtnPrevious.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mbPlaying) {
                    ToastUtil.createToastWindow(PaintPlayActivity.this, getString(R.string.play_wait), PxDpConvert.getSystemHight(getApplicationContext()) / 3);
                } else if (miPointCurrent > 0) {
                    mPaintView.onClickPrevious();
                    // 兩個UP差異點數 = 減少的點數 在移除最後第一個
                    int count;
                    if (mListRecordInt.size() > 1) {
                        count = mListRecordInt.remove(mListRecordInt.size() - 1) - mListRecordInt.get(mListRecordInt.size() - 1);
                    } else {
                        count = mListRecordInt.remove(mListRecordInt.size() - 1);
                    }
                    System.out.println(miPointCurrent);
                    miPointCount = miPointCount + count;
                    miPointCurrent = miPointCurrent - count;

                } else if (miPointCurrent == 0) {
                    ToastUtil.createToastWindow(PaintPlayActivity.this, getString(R.string.play_frist), PxDpConvert.getSystemHight(getApplicationContext()) / 3);
                }
                showProgress();
            }
        });


        Button.OnClickListener autoPlayAndReplay = new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (miPointCount == 0) {
                    mFrameLayoutFreePaint.removeAllViews();
                    mPaintView = new PaintView(PaintPlayActivity.this, true);
                    mPaintView.initDefaultBrush(Brushes.get(getApplicationContext())[mCurrentBrushId]);
                    mFrameLayoutFreePaint.addView(mPaintView);
                    mPaintView.mListTagPoint = new ArrayList<>(mBDWFileReader.m_tagArray);
                    miPointCount = mPaintView.mListTagPoint.size();
                    miPointCurrent = 0;
                    if (miPointCount > 0)
                        mHandlerTimerPlay.postDelayed(rb_play, miAutoPlayIntervalTime);
                    mImgBtnReplay.setVisibility(View.INVISIBLE);
                    mbAutoPlay = true;
                    mListRecordInt.clear();
                    mBtnPause.setVisibility(View.VISIBLE);
                    mBtnAutoPlay.setVisibility(View.GONE);
                } else {
                    if (miPointCount > 0)
                        mHandlerTimerPlay.postDelayed(rb_play, miAutoPlayIntervalTime);
                }
            }
        };


        mBtnAutoPlay.setOnClickListener(autoPlayAndReplay);
        mImgBtnReplay.setOnClickListener(autoPlayAndReplay);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }


    @Override
    public void onStop() {
        mbStop = true;
        mHandlerTimerPlay.removeCallbacks(rb_play);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        mFileBDW.delete();
        super.onDestroy();
    }
}
