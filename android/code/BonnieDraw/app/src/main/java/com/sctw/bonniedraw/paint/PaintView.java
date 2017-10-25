package com.sctw.bonniedraw.paint;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.sctw.bonniedraw.R;
import com.sctw.bonniedraw.utility.BDWFileReader;
import com.sctw.bonniedraw.utility.BDWFileWriter;
import com.sctw.bonniedraw.utility.PxDpConvert;
import com.sctw.bonniedraw.utility.TSnackbarCall;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Fatorin on 2017/10/25.
 */

public class PaintView extends View {
    private static final boolean HWLAYER = true;
    private static final boolean SWLAYER = false;
    private static final String SKETCH_FILE_BDW = "/backup.bdw";
    private static final String SKETCH_FILE_PNG = "/backup.png";
    private Bitmap mBitmap;
    private Canvas mCanvas;
    private Path mPath;
    private ArrayList<PathAndPaint> paths = new ArrayList<>(20);
    private ArrayList<PathAndPaint> undonePaths = new ArrayList<>(20);
    private List<Integer> mListTempTagLength = new ArrayList<Integer>();
    private float mX, mY;
    private static final float TOUCH_TOLERANCE = 4;
    private Paint gridPaint;
    private int miWidth, miEachConut = 0;
    private float mfStartX, mfStartY, mfPointLength;
    Boolean mbEraseMode = false, mbZoomMode = false, mbCheckFinger = false;
    Paint mPaint;
    File mFileBDW, mFilePNG;
    List<TagPoint> mListTagPoint, mListUndoTagPoint;
    BDWFileReader mBDWReader = new BDWFileReader();
    int miGridCol = 0, miPaintNum = 0;

    public PaintView(Context c, Paint mPaint) {
        super(c);
        this.mPaint = mPaint;
        this.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        miWidth = getWidth(c);
        mBitmap = Bitmap.createBitmap(miWidth, miWidth, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);
        mPath = new Path();
        mListTagPoint = new ArrayList<>();
        mListUndoTagPoint = new ArrayList<>();
        mFileBDW = new File(getContext().getFilesDir().getPath() + SKETCH_FILE_BDW);
        mFilePNG = new File(getContext().getFilesDir().getPath() + SKETCH_FILE_PNG);
        //  Set Grid
        gridPaint = new Paint();
        gridPaint.setAntiAlias(true);
        gridPaint.setStrokeWidth(3);
        gridPaint.setStyle(Paint.Style.STROKE);
        gridPaint.setColor(ContextCompat.getColor(c, R.color.GridLineColor));
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

    public Bitmap getBitmap() {
        return mBitmap;
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
                canvas.drawLine((miWidth / miGridCol) * i, 0, (miWidth / miGridCol) * i, miWidth, gridPaint);
                canvas.drawLine(0, (miWidth / miGridCol) * i, miWidth, (miWidth / miGridCol) * i, gridPaint);
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
        if (mbZoomMode) {
            switch (event.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    mfStartX = event.getX();
                    mfStartY = event.getY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (!mbCheckFinger) {
                        int offsetX = (int) (event.getX() - mfStartX);
                        int offsetY = (int) (event.getY() - mfStartY);
                        this.setTranslationX(this.getTranslationX() + offsetX);
                        this.setTranslationY(this.getTranslationY() + offsetY);

                    } else {
                        float length = mfPointLength - spacing(event);
                        if (PaintView.this.getScaleX() >= 1.1) {
                            if (length > 0) {
                                PaintView.this.setScaleX(PaintView.this.getScaleX() - 0.1f);
                                PaintView.this.setScaleY(PaintView.this.getScaleY() - 0.1f);
                            } else if (length < 0) {
                                PaintView.this.setScaleX(PaintView.this.getScaleX() + 0.1f);
                                PaintView.this.setScaleY(PaintView.this.getScaleY() + 0.1f);
                            }
                        } else {
                            if (length > 0) {
                                return true;
                            } else if (length < 0) {
                                PaintView.this.setScaleX(PaintView.this.getScaleX() + 0.1f);
                                PaintView.this.setScaleY(PaintView.this.getScaleY() + 0.1f);
                            }
                        }
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    break;
                case MotionEvent.ACTION_POINTER_DOWN:
                    mfPointLength = spacing(event);
                    if (mfPointLength > 10) {
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
            paint_tagpoint.setiPosX(PxDpConvert.displayToFormat(x, miWidth));
            paint_tagpoint.setiPosY(PxDpConvert.displayToFormat(y, miWidth));
            paint_tagpoint.setiSize(PxDpConvert.displayToFormat(mPaint.getStrokeWidth(), miWidth));
            paint_tagpoint.setiPaintType(miPaintNum);
            paint_tagpoint.setiColor(mPaint.getColor());
            paint_tagpoint.setiAction(MotionEvent.ACTION_DOWN + 1);
            mListTagPoint.add(paint_tagpoint);
        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            tagpoint = new TagPoint();
            tagpoint.setiPosX(PxDpConvert.displayToFormat(x, miWidth));
            tagpoint.setiPosY(PxDpConvert.displayToFormat(y, miWidth));
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

    //復原、重作
    public void onClickUndo() {
        if (paths.size() > 0 && undonePaths.size() <= 20) {
            undonePaths.add(paths.remove(paths.size() - 1));
            miEachConut = paths.size() == 0 ? mListTempTagLength.get(0) : mListTempTagLength.get(paths.size()) - mListTempTagLength.get(paths.size() - 1);
            for (int x = 0; x <= miEachConut - 1; x++) {
                mListUndoTagPoint.add(mListTagPoint.remove(mListTagPoint.size() - 1));
            }
            if (mFilePNG.exists()) {
                mBitmap = BitmapFactory.decodeFile(mFilePNG.toString()).copy(Bitmap.Config.ARGB_8888, true);
            } else {
                mBitmap = Bitmap.createBitmap(miWidth, miWidth, Bitmap.Config.ARGB_8888);
            }
            mCanvas = new Canvas(mBitmap);
            for (PathAndPaint p : paths) {
                mCanvas.drawPath(p.get_mPath(), p.get_mPaint());
            }
            invalidate();
        } else {
            TSnackbarCall.showTSnackbar(PaintView.this, "復原次數到達上限");
        }
    }

    public void onClickRedo() {
        if (undonePaths.size() > 0) {
            miEachConut = paths.size() == 0 ? mListTempTagLength.get(0) : mListTempTagLength.get(paths.size()) - mListTempTagLength.get(paths.size() - 1);
            paths.add(undonePaths.remove(undonePaths.size() - 1));
            for (int x = 0; x <= miEachConut - 1; x++) {
                mListTagPoint.add(mListUndoTagPoint.remove(mListUndoTagPoint.size() - 1));
            }

            for (PathAndPaint p : paths) {
                mCanvas.drawPath(p.get_mPath(), p.get_mPaint());
            }
            invalidate();
        } else {
            TSnackbarCall.showTSnackbar(PaintView.this, "重作次數到達上限");
        }
    }

    public void onDrawSketch() {
        mBitmap = BitmapFactory.decodeFile(mFilePNG.toString()).copy(Bitmap.Config.ARGB_8888, true);
        mCanvas = new Canvas(mBitmap);
        invalidate();
    }

    public void checkSketch() {
        if (mFileBDW.exists() && mFilePNG.exists()) {
            onDrawSketch();
            mBDWReader.readFromFile(mFileBDW);
            mListTagPoint = new ArrayList<>(mBDWReader.m_tagArray);
        }
    }

    public boolean saveTempPhotoAndBdw() {
        try {
            File pngfile = new File(getContext().getFilesDir().getPath() + SKETCH_FILE_PNG);
            FileOutputStream fos = new FileOutputStream(pngfile);
            getBitmap().compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        BDWFileWriter bdwFileWriter = new BDWFileWriter();
        return bdwFileWriter.WriteToFile(this.mListTagPoint, getContext().getFilesDir().getPath() + SKETCH_FILE_BDW);
    }

    //計算中間距離
    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    private int getWidth(Context c) {
        WindowManager wm = (WindowManager) c.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        setLayoutParams(new LinearLayout.LayoutParams(size.x, size.x));
        return size.x;
    }
}
