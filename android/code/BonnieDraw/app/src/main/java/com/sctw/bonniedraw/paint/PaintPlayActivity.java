package com.sctw.bonniedraw.paint;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import com.sctw.bonniedraw.R;
import com.sctw.bonniedraw.utility.BDWFileReader;
import com.sctw.bonniedraw.utility.FullScreenDialog;
import com.sctw.bonniedraw.utility.PxDpConvert;
import com.sctw.bonniedraw.utility.TSnackbarCall;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

import static com.sctw.bonniedraw.paint.PaintView.STROKE_SACLE_VALUE;

public class PaintPlayActivity extends AppCompatActivity {
    private static final String SKETCH_FILE_BDW = "/backup.bdw";
    private Handler mHandlerTimerPlay = new Handler();
    private TextView mTextViewPlayProgress;
    private ImageButton mBtnBack, mBtnAutoPlay, mBtnNext, mBtnPrevious, mBtnGrid, mImgBtnReplay;
    private int miPointCount = 0, miPointCurrent = 0, miAutoPlayIntervalTime = 10;
    private FrameLayout mFrameLayoutFreePaint;
    private PaintView mPaintView;
    private int miViewWidth;
    private boolean mbPlaying = false, mbAutoPlay = false;
    private File mFileBDW;
    private BDWFileReader mBDWFileReader;
    private int mCurrentBrushId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paint_play);

        mPaintView = new PaintView(this, true);
        mFrameLayoutFreePaint = (FrameLayout) findViewById(R.id.frameLayout_freepaint);
        mFrameLayoutFreePaint.addView(mPaintView);

        mTextViewPlayProgress = findViewById(R.id.paint_play_progress);
        mBtnAutoPlay = findViewById(R.id.imgBtn_autoplay);
        mBtnNext = findViewById(R.id.imgBtn_next);
        mBtnPrevious = findViewById(R.id.imgBtn_previous);
        mBtnGrid = findViewById(R.id.imgBtn_paint_grid);
        mBtnBack = findViewById(R.id.imgBtn_paint_back);
        mImgBtnReplay = findViewById(R.id.imgBtn_replay);
        miViewWidth = mPaintView.getMiWidth();
        mFileBDW = new File(getFilesDir().getPath() + SKETCH_FILE_BDW);
        mBDWFileReader = new BDWFileReader();
        if(mFileBDW.exists()) mBDWFileReader.readFromFile(mFileBDW);

        Brush brush = Brushes.get(getApplicationContext())[mCurrentBrushId];
        mPaintView.setDrawingCacheEnabled(true);
        mPaintView.setBrush(brush);
        mPaintView.setDrawingScaledSize(1);
        mPaintView.setDrawingColor(brush.defaultColor);
        mPaintView.setDrawingBgColor(Color.WHITE);
        setImgBtnOnClick();
        replayStart();
    }

    public void replayStart() {
        mPaintView.mListTagPoint = new ArrayList<>(mBDWFileReader.m_tagArray);
        miPointCount = mPaintView.mListTagPoint.size();
        miPointCurrent = 0;
        if (miPointCount > 0) mHandlerTimerPlay.postDelayed(rb_play, miAutoPlayIntervalTime);
        mImgBtnReplay.setVisibility(View.INVISIBLE);
        mbAutoPlay = true;
    }

    private float mfLastPosX;
    private float mfLastPosY;
    private Runnable rb_play = new Runnable() {
        public void run() {
            boolean brun = true;
            if (miPointCount > 0) {
                TagPoint tagpoint = mPaintView.mListTagPoint.get(miPointCurrent);
                mfLastPosX=PxDpConvert.formatToDisplay(tagpoint.get_iPosX(),miViewWidth);
                mfLastPosY=PxDpConvert.formatToDisplay(tagpoint.get_iPosY(),miViewWidth);
                switch (tagpoint.get_iAction() - 1) {
                    case MotionEvent.ACTION_DOWN:
                        mbPlaying = true;
                        if (tagpoint.get_iColor() != 0) {
                            mPaintView.setDrawingColor(tagpoint.get_iColor());
                        }
                        if (tagpoint.get_iSize() != 0) {
                            mPaintView.setDrawingScaledSize(PxDpConvert.formatToDisplay(tagpoint.get_iSize()/STROKE_SACLE_VALUE, miViewWidth));
                        }
                        if (tagpoint.get_iBrush() != 0) {
                            mPaintView.setBrush(Brushes.get(getApplicationContext())[tagpoint.get_iBrush()]);
                        }
                        //開始畫 記錄每一個時間點 即可模擬回去
                        mPaintView.onTouchEvent(MotionEvent.obtain(0,0,MotionEvent.ACTION_DOWN,mfLastPosX, mfLastPosY,0));
                        break;
                    case MotionEvent.ACTION_MOVE:
                        mPaintView.onTouchEvent(MotionEvent.obtain(0,tagpoint.get_iTime(),MotionEvent.ACTION_MOVE,mfLastPosX, mfLastPosY,0));
                        break;
                    case MotionEvent.ACTION_UP:
                        //mPaintView.onTouchEvent(MotionEvent.obtain(0,0,MotionEvent.ACTION_UP,mfLastPosX, mfLastPosY,0));
                        mbPlaying = false;
                        brun = false;
                        break;
                }
                miPointCount--;
                miPointCurrent++;
                mTextViewPlayProgress.setText(String.format(Locale.TAIWAN,"%d/ 100%%", 100 * miPointCurrent / mPaintView.mListTagPoint.size()));

                if (brun) {
                    mHandlerTimerPlay.postDelayed(rb_play, miAutoPlayIntervalTime);
                } else {
                    if (mbAutoPlay) {
                        mHandlerTimerPlay.postDelayed(rb_play, miAutoPlayIntervalTime);
                    }
                }
            } else {
                mbAutoPlay = false;
                mImgBtnReplay.setVisibility(View.VISIBLE);
            }
        }
    };

    void setImgBtnOnClick() {
        mBtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mBtnNext.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (miPointCount > 0) {
                    mHandlerTimerPlay.postDelayed(rb_play, miAutoPlayIntervalTime);
                } else if (miPointCount == 0) {
                    TSnackbarCall.showTSnackbar(findViewById(R.id.coordinatorLayout_activity_paint), getString(R.string.play_end));
                }
            }
        });

        mBtnPrevious.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mbPlaying) {
                    TSnackbarCall.showTSnackbar(findViewById(R.id.coordinatorLayout_activity_paint), getString(R.string.play_wait));
                } else if (miPointCurrent > 0) {
                    for (int x = 0; x <= mPaintView.onClickPrevious() - 1; x++) {
                        miPointCount++;
                        miPointCurrent--;
                    }
                    Log.d("miPointCurrent",String.valueOf(miPointCurrent));
                } else if (miPointCurrent == 0) {
                    TSnackbarCall.showTSnackbar(findViewById(R.id.coordinatorLayout_activity_paint), getString(R.string.play_frist));
                }
            }
        });


        Button.OnClickListener autoPlayAndReplay = new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFrameLayoutFreePaint.removeAllViews();
                mPaintView = new PaintView(PaintPlayActivity.this, true);
                mFrameLayoutFreePaint.addView(mPaintView);
                replayStart();
            }
        };


        mBtnAutoPlay.setOnClickListener(autoPlayAndReplay);
        mImgBtnReplay.setOnClickListener(autoPlayAndReplay);

        mBtnGrid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final FullScreenDialog gridDialog = new FullScreenDialog(PaintPlayActivity.this, R.layout.paint_grid_dialog);
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
                        mPaintView.setMiGridCol(0);
                        gridDialog.dismiss();
                    }
                });

                grid3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mPaintView.setMiGridCol(3);
                        gridDialog.dismiss();
                    }
                });

                grid6.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mPaintView.setMiGridCol(6);
                        gridDialog.dismiss();
                    }
                });

                grid10.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mPaintView.setMiGridCol(10);
                        gridDialog.dismiss();
                    }
                });

                grid20.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mPaintView.setMiGridCol(20);
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
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
