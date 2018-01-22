package com.sctw.bonniedraw.paint;

/**
 * Created by marco.granatiero on 08/08/2014.
 */


import android.os.Build.VERSION;
import android.view.MotionEvent;

public abstract class TouchResampler {

    private static final String TAG = "LineBrush";

    public void feedXYT(float[] xyt) {
        startPath(xyt[0], xyt[1], (long) xyt[2]);
        int i = 3;
        while (i < xyt.length) {
            addToPath(xyt[i], xyt[i + 1], (long) xyt[i + 2], true);
            i += 3;
        }
        i -= 3;
        addToPath(xyt[i], xyt[i + 1], (long) xyt[i + 2], false);
        //Empty var x,y
        endPath(0,0);
    }

    public boolean getXYVAtDistance(float distance, float[] xyv) {
        return false;
    }

    public void onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        TagPoint tagPoint;
        long t = event.getEventTime() - event.getDownTime();
        switch ((VERSION.SDK_INT >= 8 ? event.getActionMasked() : event.getAction() & 255)) {
            case MotionEvent.ACTION_DOWN:
                //Log.d("TouchResampler", "ACTION_DOWN");
                startPath(x, y, t);
                break;
            case MotionEvent.ACTION_UP:
                //Log.d("TouchResampler", "ACTION_UP");
                addToPath(x, y, t, false);
                endPath(x,y);
                break;
            case MotionEvent.ACTION_MOVE:
                //Log.d("TouchResampler", "ACTION_MOVE");
                int i = 0;
                while (i < event.getHistorySize()) {
                   // addToPath(event.getHistoricalX(i), event.getHistoricalY(i), event.getHistoricalEventTime(i) - event.getDownTime(), true);
                    addToPath(event.getHistoricalX(i), event.getHistoricalY(i), event.getHistoricalEventTime(i) , true);
                    i++;
                }
                addToPath(x, y, t, true);
                break;
            default:
                break;
        }
    }

    protected abstract void onTouchDown(float f, float f2);

    protected abstract void onTouchMove(float f, float f2, float f3);

    protected abstract void onTouchUp(float f,float f2);

    protected void startPath(float x, float y, long t) {
        onTouchDown(x, y);
    }

    protected void addToPath(float x, float y, long t, boolean isMove) {
        onTouchMove(x, y, (float) t);
    }

    protected void endPath(float x,float y) {
        onTouchUp(x,y);
    }


}