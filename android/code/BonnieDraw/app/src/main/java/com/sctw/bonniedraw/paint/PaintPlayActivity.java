package com.sctw.bonniedraw.paint;

import android.content.Context;
import android.graphics.Bitmap;
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
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
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
import java.util.List;

public class PaintPlayActivity extends AppCompatActivity {
    public static final boolean HWLAYER = true;
    public static final boolean SWLAYER = false;
    private static final String SKETCH_FILE_BDW = "/backup.bdw";
    private Handler mHandlerTimerPlay = new Handler();
    private TextView mTextViewPlayProgress;
    private ImageButton mBtnBack, mBtnAutoPlay, mBtnNext, mBtnPrevious, mBtnGrid, mImgBtnReplay;
    private FrameLayout mFrameLayoutFreePaint;
    private Paint mPaint;
    private MyView myView;
    private List<Integer> mListTempTagLength = new ArrayList<Integer>();
    private List<TagPoint> mListTagPoint;
    private static int miPointCount = 0, miPointCurrent = 0, miAutoPlayIntervalTime = 50;
    private Boolean mbAutoPlay = false, mbPlayState = false, mbPlaying = false, mbZoomMode = false, mbCheckFinger = false;
    private int displayWidth, offsetX, offsetY, realPaint = 0, miGridCol, count;
    private float startX, startSacle, startY, pointLength;
    private Xfermode eraseEffect;
    private File backLoadBDW=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paint_play);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(0xFF000000);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(13);
        eraseEffect = new PorterDuffXfermode(PorterDuff.Mode.DST_OUT);
        myView = new MyView(this);
        mFrameLayoutFreePaint = (FrameLayout) findViewById(R.id.frameLayout_freepaint);
        mFrameLayoutFreePaint.addView(myView);
        getDisplay();

        mTextViewPlayProgress = findViewById(R.id.paint_play_progress);
        mBtnAutoPlay = findViewById(R.id.imgBtn_autoplay);
        mBtnNext = findViewById(R.id.imgBtn_next);
        mBtnPrevious = findViewById(R.id.imgBtn_previous);
        mBtnGrid = findViewById(R.id.imgBtn_paint_grid);
        mBtnBack = findViewById(R.id.imgBtn_paint_back);
        mImgBtnReplay = findViewById(R.id.imgBtn_replay);
        setImgBtnOnClick();
        checkSketch();
    }

    public void checkSketch() {
        backLoadBDW = new File(getFilesDir().getPath() + SKETCH_FILE_BDW);
        if (backLoadBDW.exists()) {
            BDWFileReader reader = new BDWFileReader();
            reader.readFromFile(backLoadBDW);
            mListTagPoint = new ArrayList<>(reader.m_tagArray);
            miPointCount = mListTagPoint.size();
            miPointCurrent = 0;
            mbAutoPlay = true;
            if (miPointCount > 0) mHandlerTimerPlay.postDelayed(rb_play, miAutoPlayIntervalTime);
            mImgBtnReplay.setVisibility(View.INVISIBLE);
        }
    }

    //強制正方形
    public void getDisplay() {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        displayWidth = size.x;
        myView.setLayoutParams(new FrameLayout.LayoutParams(displayWidth, displayWidth));
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
                mTextViewPlayProgress.setText(miPointCurrent * 100 / mListTagPoint.size() + " / " + "100%");


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

    public class MyView extends View {
        private Bitmap mBitmap;
        private Canvas mCanvas;
        private Path mPath;
        private ArrayList<PathAndPaint> paths = new ArrayList<>(20);
        private ArrayList<PathAndPaint> undonePaths = new ArrayList<>(20);
        private float mX, mY;
        private static final float TOUCH_TOLERANCE = 4;
        private Paint gridPaint;
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
            if (mbAutoPlay || mbPlayState) return true;//重播功能時不准畫

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
                mBitmap = Bitmap.createBitmap(displayWidth, displayWidth, Bitmap.Config.ARGB_8888);
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
                    mbPlayState = true;
                    myView.onClickPrevious();
                } else if (miPointCurrent == 0) {
                    TSnackbarCall.showTSnackbar(findViewById(R.id.coordinatorLayout_activity_paint), getString(R.string.play_frist));
                }
            }
        });


        Button.OnClickListener autoPlayAndReplay = new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFrameLayoutFreePaint.removeAllViews();
                myView = new MyView(PaintPlayActivity.this);
                customPaint(0);
                getDisplay();
                mFrameLayoutFreePaint.addView(myView);
                miPointCount = mListTagPoint.size();
                miPointCurrent = 0;
                mbAutoPlay = true;
                mImgBtnReplay.setVisibility(View.INVISIBLE);
                if (miPointCount > 0) mHandlerTimerPlay.postDelayed(rb_play, miAutoPlayIntervalTime);
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
