package com.sctw.bonniedraw.paint;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
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
import com.sctw.bonniedraw.widget.ToastUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Fatorin on 2017/10/25.
 */

public class PaintView extends View {
    private static final String SKETCH_FILE_BDW = "/backup.bdw";
    private static final String SKETCH_FILE_PNG = "/backup.png";
    private List<Integer> mListTempPoint = new ArrayList<Integer>();
    private List<Integer> mListUndoPoint = new ArrayList<>();
    private int miGridCol = 0;
    private Paint gridPaint;
    private int miWidth, miEachConut;
    private float mfStartX, mfStartY, mfPointLength;
    private boolean mbPlayMode = false;
    public List<TagPoint> mListTagPoint;
    public List<TagPoint> mListUndoTagPoint;
    public Boolean mbZoomMode = false, mbCheckFinger = false;
    public File mFileBDW, mFilePNG;
    public BDWFileReader mBDWReader = new BDWFileReader();
    public int miPaintNum = 3;

    //********  Brush  ******************

    //private static final float STROKE_WIDTH = 20.0f;

    private Brush mBrush;
    private int mColor;
    private int mLineColor;
    private float mDrawingAlpha;
    private int mBackgroundLayerColor;

    private float mLastDrawDistance;
    private float mSpacing;

    private Bitmap mDrawingLayer;
    private Canvas mDrawingLayerCanvas;
    private Rect mOnDrawCanvasRect;
    private Bitmap mPathLayer;
    private Canvas mPathLayerCanvas;
    private float mPathWidth;
    private float mPathWidthHalf;
    private Bitmap mMergedLayer;
    private Canvas mMergedLayerCanvas;
    private Bitmap mTextureLayer;
    private Canvas mTextureLayerCanvas;
    private BitmapDrawable mTextureDrawable;
    private Bitmap mTempPathLayer;
    private Canvas mTempPathLayerCanvas;

    private RectF mLineDirtyRect;
    private RectF mDirtyRect;

    private Paint mNormalPaint;
    private Paint mSrcPaint;
    private Paint mDstInPaint;
    private Paint mDstOutPaint;
    private OnTouchHandler mCurveDrawingHandler;
    private TouchResampler mTouchResampler;
    private float mMaxVelocityScale;
    private static float VELOCITY_MAX_SCALE = 130.0f;
    private static final Bitmap EMPTY_BITMAP = Build.VERSION.SDK_INT < 14 ? Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888) : null;

    private Bitmap[] mMaskBitmap;
    private int mMaskPadding;

    private Random mRandom;
    private Matrix mMatrix;
    private float mDeviceAngle;
    private PointF mOldPt;
    private boolean mIsJitterColor;

    private boolean mDrawingLayerNeedDrawn;
    private boolean mIsBatchDraw;

    //**add
    private OnTouchHandler mPlayDrawingHandler;
    private ArrayList<Bitmap> mBitmapList;
    private ArrayList<Bitmap> mBitmapUndoList;
    private Bitmap mSketchLayer;
    private boolean mbDirection = true;


    public PaintView(Context c, boolean direction) {
        //true = 直  false=橫
        super(c);
        this.mbDirection = direction;
        initBrush();
    }

    public PaintView(Context c, boolean mbPlayMode, boolean direction) {
        super(c);
        this.mbPlayMode = mbPlayMode;
        this.mbDirection = direction;
        initBrush();
    }

    public boolean scaleZoom(MotionEvent event) {
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

    public int getMiWidth() {
        return miWidth;
    }

    //復原、重作
    public void onClickUndo() {
        if (mBitmapList.size() > 1) {
            //回到上一個
            mBitmapUndoList.add(mBitmapList.remove(mBitmapList.size() - 1));
            this.mMergedLayer = Bitmap.createBitmap(mBitmapList.get(mBitmapList.size() - 1));
            this.mMergedLayerCanvas.setBitmap(mMergedLayer);

            mListUndoPoint.add(mListTempPoint.remove(mListTempPoint.size() - 1));
            if (mListTempPoint.size() > 0) {
                miEachConut = mListUndoPoint.get(mListUndoPoint.size() - 1) - mListTempPoint.get(mListTempPoint.size() - 1);
            } else {
                miEachConut = mListUndoPoint.get(mListUndoPoint.size() - 1);
            }
            for (int x = 0; x < miEachConut; x++) {
                mListUndoTagPoint.add(mListTagPoint.remove(mListTagPoint.size() - 1));
            }
            invalidate();
        } else {
            ToastUtil.createToastWindow(getContext(), getContext().getString(R.string.uc_undo_limit), PxDpConvert.getSystemHight(getContext()) / 4);
        }
    }

    public void onClickRedo() {
        if (mBitmapUndoList.size() > 0) {
            mBitmapList.add(mBitmapUndoList.remove(mBitmapUndoList.size() - 1));
            this.mMergedLayer = Bitmap.createBitmap(mBitmapList.get(mBitmapList.size() - 1));
            this.mMergedLayerCanvas.setBitmap(mMergedLayer);

            mListTempPoint.add(mListUndoPoint.remove(mListUndoPoint.size() - 1));
            for (int x = 0; x < miEachConut; x++) {
                mListTagPoint.add(mListUndoTagPoint.remove(mListUndoTagPoint.size() - 1));
            }

            if (mListUndoPoint.size() > 0) {
                miEachConut = mListUndoPoint.get(mListUndoPoint.size() - 1) - mListTempPoint.get(mListTempPoint.size() - 1);
            } else if (mListTempPoint.size() > 1) {
                miEachConut = mListTempPoint.get(mListTempPoint.size() - 1) - mListTempPoint.get(mListTempPoint.size() - 2);
            } else {
                miEachConut = mListTempPoint.get(mListTempPoint.size() - 1);
            }

            invalidate();
        } else {
            ToastUtil.createToastWindow(getContext(), getContext().getString(R.string.uc_redo_limit), PxDpConvert.getSystemHight(getContext()) / 4);
        }
    }

    public void onClickPrevious() {
        if (mBitmapList.size() > 1) {
            //回到上一個
            mBitmapUndoList.add(mBitmapList.remove(mBitmapList.size() - 1));
            this.mMergedLayer = Bitmap.createBitmap(mBitmapList.get(mBitmapList.size() - 1));
            this.mMergedLayerCanvas.setBitmap(mMergedLayer);
            invalidate();
        } else {
            ToastUtil.createToastWindow(getContext(), getContext().getString(R.string.uc_undo_limit), PxDpConvert.getSystemHight(getContext()) / 4);
        }
    }

    public void onCheckSketch() {
        if (mFileBDW.exists() && mFilePNG.exists()) {
            Bitmap bitmap = BitmapFactory.decodeFile(mFilePNG.getAbsolutePath()).copy(Bitmap.Config.ARGB_8888, true);
            mSketchLayer = bitmap;
            this.mMergedLayer = Bitmap.createBitmap(bitmap);
            this.mMergedLayerCanvas.setBitmap(mMergedLayer);
            mBitmapList.add(Bitmap.createBitmap(getForegroundBitmap()));
            mBDWReader.readFromFile(mFileBDW);
            mListTagPoint = new ArrayList<>(mBDWReader.m_tagArray);
            invalidate();
        }
    }

    private int getWidthSize(Context c) {
        WindowManager wm = (WindowManager) c.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        setLayoutParams(new LinearLayout.LayoutParams(size.x, size.x));
        return size.x;
    }

    private int getHieghtSize(Context c) {
        WindowManager wm = (WindowManager) c.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        setLayoutParams(new LinearLayout.LayoutParams(size.y, size.y));
        return size.y;
    }

    public boolean saveTempPhotoAndBdw() {
        try {
            File pngfile = new File(getContext().getFilesDir().getPath() + SKETCH_FILE_PNG);
            FileOutputStream fos = new FileOutputStream(pngfile);
            this.getDrawingCache().compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        BDWFileWriter bdwFileWriter = new BDWFileWriter();
        return bdwFileWriter.WriteToFile(this.mListTagPoint, getContext().getFilesDir().getPath() + SKETCH_FILE_BDW);
    }

    public boolean saveTempBdw() {
        BDWFileWriter bdwFileWriter = new BDWFileWriter();
        return bdwFileWriter.WriteToFile(this.mListTagPoint, getContext().getFilesDir().getPath() + SKETCH_FILE_BDW);
    }

    //計算中間距離
    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    public int selectPaint(int num) {
        switch (num) {
            case 1:
                return 18;
            case 2:
                return 6;
            case 3:
                return 3;
            case 4:
                return 13;
            case 5:
                return 11;
            default:
                return 0;
        }
    }
    //************* Brush ******************

    private interface OnTouchHandler {
        boolean onTouchEvent(MotionEvent motionEvent);
    }

    public void initBrush() {
        //old init
        if (mbDirection) {
            this.miWidth = getWidthSize(getContext());
        } else {
            this.miWidth = getHieghtSize(getContext());
        }
        this.mListTagPoint = new ArrayList<>();
        this.mListUndoTagPoint = new ArrayList<>();
        this.mFileBDW = new File(getContext().getFilesDir().getPath() + SKETCH_FILE_BDW);
        this.mFilePNG = new File(getContext().getFilesDir().getPath() + SKETCH_FILE_PNG);
        //  Set Grid
        gridPaint = new Paint();
        this.gridPaint.setAntiAlias(true);
        this.gridPaint.setStrokeWidth(3);
        this.gridPaint.setStyle(Paint.Style.STROKE);
        this.gridPaint.setColor(ContextCompat.getColor(getContext(), R.color.GridLineColor));

        this.mTextureDrawable = new BitmapDrawable(getResources(), BitmapFactory.decodeResource(getResources(), R.drawable.texture01));
        this.mTextureDrawable.setTileModeXY(Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);

        mNormalPaint = new Paint(Paint.FILTER_BITMAP_FLAG);
        mSrcPaint = new Paint(Paint.FILTER_BITMAP_FLAG);
        mDstInPaint = new Paint(Paint.FILTER_BITMAP_FLAG);
        mDstOutPaint = new Paint(Paint.FILTER_BITMAP_FLAG);

        mNormalPaint.setAntiAlias(true);
        //mNormalPaint.setColor(Color.BLACK);
        //mNormalPaint.setStyle(Paint.Style.STROKE);
        //mNormalPaint.setStrokeJoin(Paint.Join.ROUND);
        //mNormalPaint.setStrokeWidth(STROKE_WIDTH);

        mDrawingLayerCanvas = new Canvas();
        mPathLayerCanvas = new Canvas();
        this.mMergedLayerCanvas = new Canvas();
        this.mTextureLayerCanvas = new Canvas();
        this.mTempPathLayerCanvas = new Canvas();

        mOnDrawCanvasRect = new Rect();
        mLineDirtyRect = new RectF();
        mDirtyRect = new RectF();

        mDrawingAlpha = 1.0f;

        mSrcPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));
        mDstInPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        mDstOutPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));

        if (mbPlayMode) {
            this.mPlayDrawingHandler = new OnTouchHandler() {
                public boolean onTouchEvent(MotionEvent event) {
                    if (!PaintView.this.mBrush.traceMode) {
                        mTouchResampler.onTouchEvent(event);
                        return true;
                    }
                    return false;
                }
            };
            this.mTouchResampler = new MyPlayDistanceResampler();
        } else {
            this.mCurveDrawingHandler = new OnTouchHandler() {
                public boolean onTouchEvent(MotionEvent event) {
                    if (!PaintView.this.mBrush.traceMode) {
                        mTouchResampler.onTouchEvent(event);
                        return true;
                    }
                    return false;
                }
            };
            this.mTouchResampler = new MyTouchDistanceResampler();
        }

        mRandom = new Random();
        mMatrix = new Matrix();
        mOldPt = new PointF();

        ///add blank
        mBitmapUndoList = new ArrayList<>();
        mBitmapList = new ArrayList<>();

        if (!mFilePNG.exists()) {
            Bitmap emptyBitmap = Bitmap.createBitmap(miWidth, miWidth, Bitmap.Config.ARGB_8888);
            mBitmapList.add(emptyBitmap);
        }
    }

    public void usePlayHnad(MotionEvent event) {
        this.mPlayDrawingHandler.onTouchEvent(event);
    }

    public void initDefaultBrush(Brush brush) {
        setBrush(brush);
        setDrawingCacheEnabled(true);
        setDrawingColor(Color.BLACK);
        setDrawingBgColor(Color.WHITE);
    }

    public void setBrush(Brush brush) {
        mBrush = brush;

        mPathWidth = brush.size;
        mPathWidthHalf = brush.size / 2.0f;

        mSpacing = brush.spacing * brush.size;

        releaseBrushSizeBitmaps();

        mPathLayer = Bitmap.createBitmap((int) mPathWidth, (int) mPathWidth, Bitmap.Config.ARGB_8888);
        mPathLayerCanvas.setBitmap(this.mPathLayer);

        mMaxVelocityScale = (brush.size * brush.lineEndSpeedLength) / VELOCITY_MAX_SCALE;

        this.mMaskBitmap = new Bitmap[brush.maskImageIdArray.length];
        this.mMaskPadding = (int) (this.mPathWidth / 3.5f);
        int i = 0;
        while (i < this.mMaskBitmap.length) {
            this.mMaskBitmap[i] = decodeScaledExpandResource(getResources(), brush.maskImageIdArray[i], (int) this.mPathWidth, (int) this.mPathWidth, this.mMaskPadding);
            i++;
        }

        if (((double) brush.jitterHue) == 0.0d && ((double) brush.jitterSaturation) == 0.0d && ((double) brush.jitterBrightness) == 0.0d) {
            mIsJitterColor = false;
        } else {
            mIsJitterColor = true;
        }

        this.mTempPathLayer = Bitmap.createBitmap((int) this.mPathWidth, (int) this.mPathWidth, Bitmap.Config.ARGB_8888);
        this.mTempPathLayerCanvas.setBitmap(this.mTempPathLayer);

    }

    private static Bitmap decodeScaledExpandResource(Resources res, int id, int width, int height, int padding) {
        Bitmap src = BitmapFactory.decodeResource(res, id);
        if (src == null) {
            return null;
        }
        Bitmap dst = Bitmap.createBitmap(padding * 2 + width, padding * 2 + height, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(dst);
        c.drawBitmap(src, new Rect(0, 0, src.getWidth(), src.getHeight()), new Rect(padding, padding, padding + width, padding + height), null);
        c.setBitmap(EMPTY_BITMAP);
        if (src == dst) {
            return dst;
        }
        src.recycle();
        return dst;
    }

    public Brush getBrush() {
        return mBrush;
    }


    public void setDrawingColor(int color) {
        mColor = color;
    }

    public int getDrawingColor() {
        return mColor;
    }

    public void setDrawingBgColor(int color) {
        this.mBackgroundLayerColor = color;
        invalidate();
    }

    public void setDrawingBgColorTag(int color) {
        this.mBackgroundLayerColor = color;
        invalidate();
        TagPoint tagPointDown = new TagPoint();
        TagPoint tagPointUp = new TagPoint();
        tagPointDown.set_iAction(MotionEvent.ACTION_DOWN + 1);
        tagPointDown.set_iBrush(6);
        tagPointDown.set_iColor(color);
        tagPointUp.set_iAction(MotionEvent.ACTION_UP + 1);
        mListTagPoint.add(tagPointDown);
        mListTagPoint.add(tagPointUp);
    }

    public float getDrawingScaledSize() {
        return mBrush.getScaledSize();
    }

    public void setDrawingScaledSize(float scaledSize) {
        if (mBrush.setScaledSize(scaledSize)) {
            setBrush(mBrush);
        }
    }

    public void setDrawingSize(int size) {
        if (mBrush.setSize(size)) {
            setBrush(mBrush);
        }
    }

    public int getDrawingSize() {
        return mBrush.getSize();
    }

    public void setDrawingAlpha(float alpha) {
        this.mDrawingAlpha = alpha;
    }

    public float getDrawingAlpha() {
        return this.mDrawingAlpha;
    }


    public boolean isClear() {
        //return (this.mBitmapHistoryManager.isEmpty() && this.mBackgroundBitmap == null) ? true : false;
        return false;
    }

    public void clear() {
        setDrawingForegroundBitmap(null);
        //setDrawingBackgroundBitmap(null);
    }

    public void setDrawingForegroundBitmap(Bitmap bitmap) {
        this.mDrawingLayerCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.SRC);
        this.mMergedLayerCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.SRC);
        invalidate();
    }

    public Bitmap getForegroundBitmap() {
        return this.mMergedLayer;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        releaseViewSizeBitmaps();

        mDrawingLayer = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        mDrawingLayerCanvas.setBitmap(mDrawingLayer);
        this.mMergedLayer = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        this.mMergedLayerCanvas.setBitmap(this.mMergedLayer);
        this.mTextureLayer = Bitmap.createBitmap(w, h, Bitmap.Config.ALPHA_8);
        this.mTextureLayerCanvas.setBitmap(this.mTextureLayer);
        this.mTextureDrawable.setBounds(0, 0, w, h);
        this.mTextureDrawable.draw(this.mTextureLayerCanvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mbZoomMode) {
            return scaleZoom(event);
        }
        if (mbPlayMode) {
            return true;
        }

        this.mIsBatchDraw = false;

        if (this.mBrush == null) {
            return super.onTouchEvent(event);
        }

        if (this.mCurveDrawingHandler != null && !mbPlayMode) {
            return this.mCurveDrawingHandler.onTouchEvent(event);
        }

        return false;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();
        canvas.getClipBounds(this.mOnDrawCanvasRect);
        drawToCanvas(canvas, mOnDrawCanvasRect);
        canvas.restore();

        //invalidate(mOnDrawCanvasRect);
    }

    //** add Grid
    public void setMiGridCol(int miGridCol) {
        this.miGridCol = miGridCol;
        invalidate();
    }

    private void drawToCanvas(Canvas canvas, Rect rect) {
        //底色
        canvas.drawColor(mBackgroundLayerColor, PorterDuff.Mode.SRC);
        if (mSketchLayer != null) {
            canvas.drawBitmap(mSketchLayer, 0.0f, 0.0f, this.mSrcPaint);
        }

        if (miGridCol != 0) {
            for (int i = 0; i <= miGridCol; i++) {
                canvas.drawLine((miWidth / miGridCol) * i, 0, (miWidth / miGridCol) * i, miWidth, gridPaint);
                canvas.drawLine(0, (miWidth / miGridCol) * i, miWidth, (miWidth / miGridCol) * i, gridPaint);
            }
        }

        if (rect == null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                canvas.saveLayer(null, null);
            } else {
                canvas.saveLayer(null, null, Canvas.HAS_ALPHA_LAYER_SAVE_FLAG);
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                canvas.saveLayer((float) (rect.left - 1), (float) (this.mOnDrawCanvasRect.top - 1),
                        (float) (this.mOnDrawCanvasRect.right + 1), (float) (this.mOnDrawCanvasRect.bottom + 1),
                        null);
            } else {
                canvas.saveLayer((float) (rect.left - 1), (float) (this.mOnDrawCanvasRect.top - 1),
                        (float) (this.mOnDrawCanvasRect.right + 1), (float) (this.mOnDrawCanvasRect.bottom + 1),
                        null, Canvas.HAS_ALPHA_LAYER_SAVE_FLAG);
            }
        }

        if (mBrush.useSingleLayerStroke) {
            canvas.drawBitmap(this.mMergedLayer, 0.0f, 0.0f, this.mSrcPaint);
            if ((!this.mDrawingLayerNeedDrawn)) {
                canvas.restore();
            } else {
                Paint p;
                p = !(this.mBrush.isEraser) ? this.mNormalPaint : this.mDstOutPaint;
                p.setAlpha((int) (this.mDrawingAlpha * 255.0f));
                canvas.drawBitmap(mDrawingLayer, 0.0f, 0.0f, p);
                canvas.restore();
            }
        } else {
            canvas.drawBitmap(this.mMergedLayer, 0.0f, 0.0f, this.mSrcPaint);
            if ((this.mDrawingLayerNeedDrawn)) {
                Paint p;
                p = !(this.mBrush.isEraser) ? this.mNormalPaint : this.mDstOutPaint;
                canvas.drawBitmap(mDrawingLayer, 0.0f, 0.0f, p);
            }
            canvas.restore();
        }
    }

    private void moveToThread(float x, float y) {
        resetDrawingDirtyRect();
        moveToAction(x, y);
    }

    private void moveToAction(float x, float y) {

        mOldPt.set(x, y);

        beforeLine(x, y);
    }


    private void beforeLine(float x, float y) {
        Brush brush = mBrush;
        mLineColor = mColor;
    }

    private void addSpot(float x, float y, float tipScale, float tipAlpha) {
        float drawX = x;
        float drawY = y;
        Brush brush = mBrush;

        if (brush.spread > 0.0f) {
            float spreadAngle = this.mRandom.nextFloat() * 6.2831855f;
            drawX += (Math.cos(spreadAngle) * brush.spread) * brush.size;
            drawY += (Math.sin(spreadAngle) * brush.spread) * brush.size;
        }

        fillBrushWithColor(brush, drawX, drawY, tipAlpha);
        if (brush.useSmudging) {
            smudgingBrush(brush, this.mOldPt.x - this.mPathWidthHalf, this.mOldPt.y - this.mPathWidthHalf, tipAlpha);
        }
        maskBrushWithAngle(brush, getBrushSpotAngle(brush, this.mOldPt.x, this.mOldPt.y, x, y), tipAlpha);
        if (brush.textureDepth > 0.0f) {
            textureBrush(brush, drawX - this.mPathWidthHalf, drawY - this.mPathWidthHalf);
        }
        drawBrushWithScale(drawX, drawY, tipScale);

        mOldPt.set(x, y);
        mDirtyRect.union(drawX - this.mPathWidthHalf, drawY - this.mPathWidthHalf,
                this.mPathWidthHalf + drawX, this.mPathWidthHalf + drawY);

    }

    private void fillBrushWithColor(Brush brush, float x, float y, float tipAlpha) {
        //int color = mLineColor;
        int color;
        float drawingAlpha;

        if (mBrush.useSingleLayerStroke) {
            drawingAlpha = 1.0f;
        } else {
            drawingAlpha = mDrawingAlpha;
        }

        if ((!this.mIsJitterColor) || brush.useFirstJitter) {
            color = Color.argb((int) ((drawingAlpha * brush.colorPatchAlpha * tipAlpha) * 255.0f), Color.red(mLineColor), Color.green(mLineColor), Color.blue(mLineColor));
        } else {
            int jitterColor = jitterColor(this.mLineColor);
            color = Color.argb((int) (drawingAlpha * tipAlpha * 255.0f), Color.red(jitterColor), Color.green(jitterColor), Color.blue(jitterColor));
        }

        mPathLayerCanvas.drawColor(color, PorterDuff.Mode.SRC);

    }

    private void maskBrushWithAngle(Brush brush, float angle, float tipAlpha) {
        mDstInPaint.setAlpha((int) ((tipAlpha * tipAlpha) * 255.0f));
        if (this.mMaskBitmap == null) return;
        Bitmap maskLayer = this.mMaskBitmap.length == 1 ? this.mMaskBitmap[0] : this.mMaskBitmap[this.mRandom.nextInt(this.mMaskBitmap.length)];

        if (angle != 0.0f) {
            this.mMatrix.setTranslate((float) (-mMaskPadding), (float) (-mMaskPadding));
            this.mMatrix.postRotate((float) Math.toDegrees((double) angle), this.mPathWidthHalf, this.mPathWidthHalf);
            mPathLayerCanvas.drawBitmap(maskLayer, this.mMatrix, mDstInPaint);
        } else {
            mPathLayerCanvas.drawBitmap(maskLayer, (float) (-mMaskPadding), (float) (-mMaskPadding), mDstInPaint);
        }
    }

    private void destLineThread() {
        if (this.mBrush.isEraser) {
            mergeWithAlpha(this.mDrawingAlpha, this.mDstOutPaint, this.mLineDirtyRect);
        } else {
            mergeWithAlpha(this.mDrawingAlpha, this.mNormalPaint, this.mLineDirtyRect);
        }
    }

    private void mergeWithAlpha(float alpha, Paint paint, RectF rectF) {
        if (mBrush.useSingleLayerStroke) {
            paint.setAlpha((int) (255.0f * alpha));
        } else {
            paint.setAlpha(255);
        }
        this.mMergedLayerCanvas.save();
        this.mMergedLayerCanvas.clipRect(rectF);
        this.mMergedLayerCanvas.drawBitmap(this.mDrawingLayer, 0.0f, 0.0f, paint);
        this.mMergedLayerCanvas.restore();
        clearDrawingLayer(rectF);
        if (!(this.mIsBatchDraw)) {
            Rect rect = new Rect();
            rectF.round(rect);
            invalidate(rect);
        }

    }

    private void clearDrawingLayer(RectF rectF) {
        this.mDrawingLayerCanvas.save();
        this.mDrawingLayerCanvas.clipRect(rectF);
        this.mDrawingLayerCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.SRC);
        this.mDrawingLayerCanvas.restore();
        this.mDrawingLayerNeedDrawn = false;
    }

    private void openLine() {
        this.mDirtyRect.set(this.mOldPt.x - this.mPathWidthHalf,
                this.mOldPt.y - this.mPathWidthHalf, this.mOldPt.x + this.mPathWidthHalf,
                this.mOldPt.y + this.mPathWidthHalf);
    }

    private void closeLine() {
        this.mLineDirtyRect.union(this.mDirtyRect);
        if (!(this.mIsBatchDraw)) {
            Rect rect = new Rect();
            mDirtyRect.round(rect);
            invalidate(rect);
        }
    }

    private void resetDrawingDirtyRect() {
        this.mLineDirtyRect.setEmpty();
        this.mDrawingLayerNeedDrawn = true;
    }

    private void drawBrushWithScale(float x, float y, float tipScale) {

        this.mNormalPaint.setAlpha(255);

        if (tipScale == 1.0f) {
            mDrawingLayerCanvas.drawBitmap(mPathLayer, x - mPathWidthHalf, y - mPathWidthHalf, mNormalPaint);
        } else {
            mDrawingLayerCanvas.save();
            mDrawingLayerCanvas.translate(x, y);
            mDrawingLayerCanvas.scale(tipScale, tipScale);
            mDrawingLayerCanvas.drawBitmap(mPathLayer, -mPathWidthHalf, -mPathWidthHalf, mNormalPaint);
            mDrawingLayerCanvas.restore();
        }
    }

    private float getBrushSpotAngle(Brush brush, float oldX, float oldY, float curX, float curY) {
        float angle = brush.angle * 6.2831855f;
        if (brush.useDeviceAngle) {
            angle += this.mDeviceAngle;
        }
        if (brush.useFlowingAngle) {
            angle += ((float) Math.atan2((double) (curY - oldY), (double) (curX - oldX))) - 1.5707964f;
        }
        return brush.angleJitter > 0.0f ? angle + ((this.mRandom.nextFloat() - 0.5f) * 6.2831855f) * brush.angleJitter : angle;
    }

    private int jitterColor(int color) {
        if (!mIsJitterColor) {
            return color;
        }
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        float hue = hsv[0];
        float saturation = hsv[1];
        hsv[0] = ((hue + (((mRandom.nextFloat() - 0.5f) * 360.0f) * mBrush.jitterHue)) + 360.0f) % 360.0f;
        hsv[1] = saturation + (mRandom.nextFloat() - 0.5f) * mBrush.jitterSaturation;
        hsv[2] = hsv[2] + (mRandom.nextFloat() - 0.5f) * mBrush.jitterBrightness;
        return Color.HSVToColor(hsv);
    }

    private void textureBrush(Brush brush, float x, float y) {
        this.mDstOutPaint.setAlpha((int) (brush.textureDepth * 255.0f));
        this.mPathLayerCanvas.drawBitmap(this.mTextureLayer, -x, -y, this.mDstOutPaint);
    }

    private void smudgingBrush(Brush brush, float x, float y, float tipAlpha) {
        x = -x;
        y = -y;
        this.mTempPathLayerCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.SRC);
        this.mTempPathLayerCanvas.drawBitmap(this.mMergedLayer, x, y, null);
        this.mNormalPaint.setAlpha((int) (this.mDrawingAlpha * 255.0f));
        this.mTempPathLayerCanvas.drawBitmap(this.mDrawingLayer, x, y, this.mNormalPaint);
        this.mNormalPaint.setAlpha((int) ((brush.smudgingPatchAlpha * tipAlpha) * 255.0f));
        this.mPathLayerCanvas.drawBitmap(this.mTempPathLayer, 0.0f, 0.0f, this.mNormalPaint);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        release();
    }

    public void release() {
        releaseViewSizeBitmaps();
        releaseBrushSizeBitmaps();
    }

    private void releaseBrushSizeBitmaps() {
        this.mPathLayerCanvas.setBitmap(EMPTY_BITMAP);
        if (this.mPathLayer != null) {
            this.mPathLayer.recycle();
            this.mPathLayer = null;
        }
        this.mTempPathLayerCanvas.setBitmap(EMPTY_BITMAP);
        if (this.mTempPathLayer != null) {
            this.mTempPathLayer.recycle();
            this.mTempPathLayer = null;
        }
        if (this.mMaskBitmap != null) {
            int i = 0;
            while (i < this.mMaskBitmap.length) {
                if (this.mMaskBitmap[i] != null) {
                    this.mMaskBitmap[i].recycle();
                    this.mMaskBitmap[i] = null;
                }
                i++;
            }
            this.mMaskBitmap = null;
        }
    }

    private void releaseViewSizeBitmaps() {
        this.mMergedLayerCanvas.setBitmap(EMPTY_BITMAP);
        if (this.mMergedLayer != null) {
            this.mMergedLayer.recycle();
            this.mMergedLayer = null;
        }
        this.mDrawingLayerCanvas.setBitmap(EMPTY_BITMAP);
        if (this.mDrawingLayer != null) {
            this.mDrawingLayer.recycle();
            this.mDrawingLayer = null;
        }
        this.mTextureLayerCanvas.setBitmap(EMPTY_BITMAP);
        if (this.mTextureLayer != null) {
            this.mTextureLayer.recycle();
            this.mTextureLayer = null;
        }
    }

    private class MyTouchDistanceResampler extends TouchDistanceResampler {
        private float mLastDrawDistance;
        private float[] mTempXYV = new float[3];

        @Override
        protected void onTouchDown(float x, float y) {
            //Log.d("PaintView", "onTouchDown");
            mBitmapUndoList.clear();
            mListUndoPoint.clear();
            mListUndoTagPoint.clear();
            this.mLastDrawDistance = 0.0f;
            PaintView.this.moveToThread(x, y);
            //**add TagPoint
            onTouchDownTagPoint(x, y);
        }

        @Override
        protected void onTouchMove(float x, float y, float t) {
            //Log.d("PaintView", "onTouchMove");
            Brush brush = PaintView.this.mBrush;

            openLine();
            while (getXYVAtDistance(this.mLastDrawDistance, this.mTempXYV)) {
                float tipSpeedScale;
                float tipSpeedAlpha;
                float px = this.mTempXYV[0];
                float py = this.mTempXYV[1];
                float pv = this.mTempXYV[2];
                if (brush.lineEndSpeedLength > 0.0f) {
                    float velocityLevel;
                    velocityLevel = pv > PaintView.this.mMaxVelocityScale ? 1.0f : pv / PaintView.this.mMaxVelocityScale;
                    tipSpeedScale = brush.lineEndSizeScale + (1.0f - velocityLevel) * (1.0f - brush.lineEndSizeScale);
                    tipSpeedAlpha = brush.lineEndAlphaScale + (1.0f - velocityLevel) * (1.0f - brush.lineEndAlphaScale);
                } else {
                    tipSpeedScale = 1.0f;
                    tipSpeedAlpha = 1.0f;
                }
                if (this.mLastDrawDistance > 0.0f) {

                    //Log.d("PaintView", "onTouchMove " + px + ", " + py);
                    PaintView.this.addSpot(px, py, tipSpeedScale, tipSpeedAlpha);
                }
                this.mLastDrawDistance += PaintView.this.mSpacing * tipSpeedScale;
            }
            closeLine();
            //**add TagPoint
            onTouchMoveTagPoint(x, y, t);
        }

        @Override
        protected void onTouchUp(float x, float y) {
            //Log.d("PaintView", "onTouchUp");
            PaintView.this.destLineThread();
            //**add TagPoint
            onTouchUpTagPoint(x, y);
            if (mBitmapList.size() > 10) mBitmapList.remove(0);
            mBitmapList.add(Bitmap.createBitmap(getForegroundBitmap()));
        }
    }

    //***** Only Play
    private class MyPlayDistanceResampler extends TouchDistanceResampler {
        private float mLastDrawDistance;
        private float[] mTempXYV = new float[3];

        @Override
        protected void onTouchDown(float x, float y) {
            //Log.d("PaintView", "onTouchDown");
            this.mLastDrawDistance = 0.0f;
            PaintView.this.moveToThread(x, y);
        }

        @Override
        protected void onTouchMove(float x, float y, float t) {
            //Log.d("PaintView", "onTouchMove");
            Brush brush = PaintView.this.mBrush;

            openLine();
            while (getXYVAtDistance(this.mLastDrawDistance, this.mTempXYV)) {
                float tipSpeedScale;
                float tipSpeedAlpha;
                float px = this.mTempXYV[0];
                float py = this.mTempXYV[1];
                float pv = this.mTempXYV[2];
                if (brush.lineEndSpeedLength > 0.0f) {
                    float velocityLevel;
                    velocityLevel = pv > PaintView.this.mMaxVelocityScale ? 1.0f : pv / PaintView.this.mMaxVelocityScale;
                    tipSpeedScale = brush.lineEndSizeScale + (1.0f - velocityLevel) * (1.0f - brush.lineEndSizeScale);
                    tipSpeedAlpha = brush.lineEndAlphaScale + (1.0f - velocityLevel) * (1.0f - brush.lineEndAlphaScale);
                } else {
                    tipSpeedScale = 1.0f;
                    tipSpeedAlpha = 1.0f;
                }
                if (this.mLastDrawDistance > 0.0f) {
                    //Log.d("PaintView", "onTouchMove " + px + ", " + py);
                    PaintView.this.addSpot(px, py, tipSpeedScale, tipSpeedAlpha);
                }
                this.mLastDrawDistance += PaintView.this.mSpacing * tipSpeedScale;
            }
            closeLine();
        }

        @Override
        protected void onTouchUp(float x, float y) {
            //Log.d("PaintView", "onTouchUp");
            PaintView.this.destLineThread();
            if (mBitmapList.size() > 10) mBitmapList.remove(0);
            mBitmapList.add(Bitmap.createBitmap(getForegroundBitmap()));
        }
    }

    private void onTouchDownTagPoint(float x, float y) {
        TagPoint tagpoint = new TagPoint();
        tagpoint.set_iPosX(PxDpConvert.displayToFormat(x, miWidth));
        tagpoint.set_iPosY(PxDpConvert.displayToFormat(y, miWidth));
        tagpoint.set_iSize(PxDpConvert.displayToFormat(getDrawingSize(), miWidth));
        tagpoint.set_iBrush(miPaintNum);
        tagpoint.set_iColor(mColor);
        tagpoint.set_iReserved((int) (getDrawingAlpha() * 100));
        if (mBrush.isEraser) {
            tagpoint.set_iBrush(0);
        }
        tagpoint.set_iAction(MotionEvent.ACTION_DOWN + 1);
        mListTagPoint.add(tagpoint);
    }

    private void onTouchMoveTagPoint(float x, float y, float t) {
        TagPoint tagpoint = new TagPoint();
        tagpoint.set_iPosX(PxDpConvert.displayToFormat(x, miWidth));
        if (y < this.getHeight() && y >= 0) {
            tagpoint.set_iPosY(PxDpConvert.displayToFormat(y, miWidth));
        } else if (y > this.getHeight()) {
            tagpoint.set_iPosY(PxDpConvert.displayToFormat(this.getHeight() - 1, miWidth));
        } else if (y <= 0) {
            tagpoint.set_iPosY(PxDpConvert.displayToFormat(1, miWidth));
        }
        tagpoint.set_iSize(PxDpConvert.displayToFormat(getDrawingSize(), miWidth));
        tagpoint.set_iBrush(miPaintNum);
        tagpoint.set_iTime((int) t);
        tagpoint.set_iColor(mColor);
        tagpoint.set_iAction(MotionEvent.ACTION_MOVE + 1);
        mListTagPoint.add(tagpoint);
    }

    private void onTouchUpTagPoint(float x, float y) {
        TagPoint tagpoint = new TagPoint();
        tagpoint.set_iPosX(PxDpConvert.displayToFormat(x, miWidth));
        tagpoint.set_iPosY(PxDpConvert.displayToFormat(y, miWidth));
        tagpoint.set_iAction(MotionEvent.ACTION_UP + 1);
        mListTagPoint.add(tagpoint);
        mListTempPoint.add(mListTagPoint.size());
    }
}
