/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sctw.bonnie.paint;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.sctw.colorpicker.ColorPickerDlg;
import com.sctw.bonnie.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.EmbossMaskFilter;
import android.graphics.MaskFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

public class FreePaint extends Activity implements ColorPickerDialog.OnColorChangedListener {
	public static final String KEY_MY_PREFERENCE = "autoplay_intervaltime";
	private static final int SAVE_DIALOG = 0, EVENT_COLOR_CHANGED = 0xff;
	private MyView myView;
	private static FrameLayout mViewFreePaint;
	private Button mbtnAutoPlay, mbtnPlay, mbtnClear, mbtnNext, mbtnColorPicker, mbtnSave;
	private String fname; // file name
	private Paint mPaint;
	private MaskFilter mEmboss;
	private MaskFilter mBlur;
	private List<TagPoint> mTagPoint_a_record;
	private Handler handler_Timer_Play = new Handler();
	private static int miPointCount = 0, miPointCurrent = 0;
	private static int miAutoPlayIntervalTime = 10;
	private boolean mbClear = true, mbAutoPlay = false, mbPainStarted = false;
	private static Context mContext;
	private static int miColor_Paint = 0xFFFF0000;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		myView = new MyView(this);
		setContentView(R.layout.freepaint_addact);
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		String strprefs = prefs.getString(KEY_MY_PREFERENCE, "0");
		miAutoPlayIntervalTime = Integer.valueOf(strprefs) * 1000;
		mViewFreePaint = (FrameLayout) findViewById(R.id.view_freepain);
		mbtnPlay = (Button) findViewById(R.id.id_btn_play);
		mbtnClear = (Button) findViewById(R.id.id_btn_clear);
		mbtnNext = (Button) findViewById(R.id.id_btn_next);
		mbtnAutoPlay = (Button) findViewById(R.id.id_btn_autoplay);
		mbtnColorPicker = (Button) findViewById(R.id.id_btn_colorpicker);
		mbtnSave = (Button) findViewById(R.id.id_btn_save);
		mViewFreePaint.addView(myView);
		mTagPoint_a_record = new ArrayList<TagPoint>();
		mContext = this;
		// Load file to bitmap
		if (getIntent().getExtras() != null) {
			if (getIntent().getExtras().getBoolean("load")) {
				Bitmap bMap = BitmapFactory.decodeFile(getIntent().getExtras().getString("file")).copy(
						Bitmap.Config.ARGB_8888, true);

				System.out.println(getIntent().getExtras().getString("file"));
				myView.loadBitmap(bMap); // load bitmap
				fname = getIntent().getExtras().getString("name");
			}
		}

		// initiate mPaint
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setDither(true);
		mPaint.setColor(miColor_Paint);
		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setStrokeJoin(Paint.Join.ROUND);
		mPaint.setStrokeCap(Paint.Cap.ROUND);
		mPaint.setStrokeWidth(12);

		mEmboss = new EmbossMaskFilter(new float[] { 1, 1, 1 }, 0.4f, 6, 3.5f);
		mBlur = new BlurMaskFilter(8, BlurMaskFilter.Blur.NORMAL);

		mbtnAutoPlay.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				mViewFreePaint.removeAllViews();
				myView = new MyView(FreePaint.this);
				mViewFreePaint.addView(myView);
				miPointCount = mTagPoint_a_record.size();
				miPointCurrent = 0;
				mbAutoPlay = true;
				if (miPointCount > 0)
					handler_Timer_Play.postDelayed(rb_play, 100);
			}
		});

		mbtnPlay.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				mViewFreePaint.removeAllViews();
				myView = new MyView(FreePaint.this);
				mViewFreePaint.addView(myView);
				miPointCount = mTagPoint_a_record.size();
				miPointCurrent = 0;
				if (miPointCount > 0)
					handler_Timer_Play.postDelayed(rb_play, 100);
			}
		});

		mbtnClear.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				mbAutoPlay = false;
				miPointCount = 0;
				miPointCurrent = 0;
				mbPainStarted = false;
				mViewFreePaint.removeAllViews();
				myView = new MyView(FreePaint.this);
				mViewFreePaint.addView(myView);
				mTagPoint_a_record = new ArrayList<TagPoint>();
			}
		});

		mbtnNext.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (miPointCount > 0)
					handler_Timer_Play.postDelayed(rb_play, 100);
			}
		});

		mbtnColorPicker.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				final ColorPickerDlg d = new ColorPickerDlg(mContext, miColor_Paint);
				d.setAlphaSliderVisible(true);

				d.setButton("Á¢∫Â?", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						colorChanged(d.getColor());
					}
				});

				d.setButton2("?ñÊ?", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {

					}
				});
				d.show();
			}
		});

		mbtnSave.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				showDialog(SAVE_DIALOG);				
			}
		});
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
			Log.v("ola_log", "landscape");
		} else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
			Log.v("ola_log", "portrait");
		}
	}

	public void colorChanged(int color) {
		miColor_Paint = color;
		mPaint.setColor(miColor_Paint);

		TagPoint tagpoint = new TagPoint();
		tagpoint.set_fPosX(0);
		tagpoint.set_fPosY(0);
		tagpoint.set_iTouchType(EVENT_COLOR_CHANGED);
		tagpoint.set_iColor(miColor_Paint);
		mTagPoint_a_record.add(tagpoint);

	}

	private Runnable rb_play = new Runnable() {
		public void run() {

			boolean brun = true;
			if (miPointCount > 0) {
				TagPoint tagpoint = mTagPoint_a_record.get(miPointCurrent);
				switch (tagpoint.get_iTouchType()) {
				case MotionEvent.ACTION_DOWN:
					myView.touch_start(tagpoint.get_fPosX(), tagpoint.get_fPosY());
					myView.invalidate();
					break;
				case MotionEvent.ACTION_MOVE:
					myView.touch_move(tagpoint.get_fPosX(), tagpoint.get_fPosY());
					myView.invalidate();
					break;
				case MotionEvent.ACTION_UP:
					brun = false;
					myView.touch_up();
					myView.invalidate();
					break;

				case EVENT_COLOR_CHANGED:
					mPaint.setColor(tagpoint.get_iColor());
					break;
				}
				miPointCount--;
				miPointCurrent++;
				if (brun) {
					handler_Timer_Play.postDelayed(rb_play, 100);
				} else {
					if (mbAutoPlay == true) {
						handler_Timer_Play.postDelayed(rb_play, miAutoPlayIntervalTime);
					}
				}
			} else {
				mbAutoPlay = false;
			}
		}
	};

	public class MyView extends View {

		private static final float MINP = 0.25f;
		private static final float MAXP = 0.75f;

		private Bitmap mBitmap;
		private Canvas mCanvas;
		private Path mPath;
		private Paint mBitmapPaint;
		private boolean load = false;
		private Bitmap loadBitmap;

		private float mX, mY;
		private static final float TOUCH_TOLERANCE = 4;

		public MyView(Context c) {
			super(c);
			mPath = new Path();
			mBitmapPaint = new Paint(Paint.DITHER_FLAG);
		}

		public void loadBitmap(Bitmap bitmap) {
			loadBitmap = bitmap;
			load = true;
		}

		@Override
		protected void onSizeChanged(int w, int h, int oldw, int oldh) {
			super.onSizeChanged(w, h, oldw, oldh);
			if (!load) {
				mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
				mCanvas = new Canvas(mBitmap);
			} else {
				mBitmap = loadBitmap;
				mCanvas = new Canvas(mBitmap);
			}
		}

		@Override
		protected void onDraw(Canvas canvas) {
			canvas.drawColor(0xFFFFFFFF);
			canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);
			canvas.drawPath(mPath, mPaint);
		}

		public Bitmap getBitmap() {
			return mBitmap;
		}

		private void touch_start(float x, float y) {
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
		}

		private void touch_up() {
			mPath.lineTo(mX, mY);
			// commit the path to our offscreen
			mCanvas.drawPath(mPath, mPaint);
			// kill this so we don't double draw
			mPath.reset();
		}

		@Override
		public boolean onTouchEvent(MotionEvent event) {
			if (mbAutoPlay == true)
				return true;

			float x = event.getX();
			float y = event.getY();
			TagPoint tagpoint;
			if (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_MOVE) {
				if (!mbPainStarted) {
					TagPoint color_tagpoint = new TagPoint();
					color_tagpoint.set_fPosX(0);
					color_tagpoint.set_fPosY(0);
					color_tagpoint.set_iTouchType(EVENT_COLOR_CHANGED);
					color_tagpoint.set_iColor(mPaint.getColor());
					mTagPoint_a_record.add(color_tagpoint);
					mbPainStarted = true;
				}
				mbPainStarted = true;
				tagpoint = new TagPoint();
				tagpoint.set_fPosX(x);
				tagpoint.set_fPosY(y);
				tagpoint.set_iTouchType(event.getAction());
				mTagPoint_a_record.add(tagpoint);
			} else if (event.getAction() == MotionEvent.ACTION_UP) {
				tagpoint = new TagPoint();
				tagpoint.set_iTouchType(event.getAction());
				mTagPoint_a_record.add(tagpoint);
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
	}

	private static final int COLOR_MENU_ID = Menu.FIRST;
	private static final int EMBOSS_MENU_ID = Menu.FIRST + 1;
	private static final int BLUR_MENU_ID = Menu.FIRST + 2;
	private static final int ERASE_MENU_ID = Menu.FIRST + 3;
	private static final int SRCATOP_MENU_ID = Menu.FIRST + 4;

	/**
	 * menyimpan bitmap dengan filename di root sdcard
	 * 
	 * @param fileName
	 * @return
	 */
	public boolean savePicture(String fileName) {

		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			try {
				File vPath = new File( Environment.getExternalStorageDirectory() + "/bonnie" );
	            if( !vPath.exists() )
	                  vPath.mkdirs();
	               
				File file = new File(Environment.getExternalStorageDirectory() + "/bonnie/" + fileName + ".png");
				FileOutputStream fos = new FileOutputStream(file);
				myView.getBitmap().compress(Bitmap.CompressFormat.PNG, 100, fos);
				fos.close();

			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return true;
		} else {
			Toast.makeText(this, "No SD Card detected", Toast.LENGTH_SHORT).show();
			return false;
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);

		menu.add(0, COLOR_MENU_ID, 0, "Color").setShortcut('3', 'c');
		menu.add(0, EMBOSS_MENU_ID, 0, "Emboss").setShortcut('4', 's');
		menu.add(0, BLUR_MENU_ID, 0, "Blur").setShortcut('5', 'z');
		menu.add(0, ERASE_MENU_ID, 0, "Erase").setShortcut('5', 'z');
		menu.add(0, SRCATOP_MENU_ID, 0, "SrcATop").setShortcut('5', 'z');

		/****
		 * Is this the mechanism to extend with filter effects? Intent intent =
		 * new Intent(null, getIntent().getData());
		 * intent.addCategory(Intent.CATEGORY_ALTERNATIVE);
		 * menu.addIntentOptions( Menu.ALTERNATIVE, 0, new ComponentName(this,
		 * NotesList.class), null, intent, 0, null);
		 *****/
		return true;
	}

	// override back pressed
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		/*
		 * if (fname == null) { // prompt dialog untuk save
		 * showDialog(SAVE_DIALOG);
		 * 
		 * } else { // finger paint ini meload suatu gambar, save kembali gambar
		 * // tersebut dengan nama file yang dibuka savePicture(fname);
		 * super.onBackPressed(); }
		 */
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		// TODO Auto-generated method stub
		switch (id) {
		case SAVE_DIALOG: {
			// membuat dialog save
			AlertDialog.Builder alert = new AlertDialog.Builder(this);
			final EditText input = new EditText(this); // input file name yang
														// akan disave
			alert.setView(input);
			alert.setTitle("Ë´ãËº∏?•‰??ÅÂ?Á®?"); // set
																					// title
			alert.setPositiveButton("Á¢∫Â?", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					Editable value = input.getText();
					if (value.toString().equals("")) {
						// bila tidak diberikan nama, maka file akan disimpan
						// dengan nama untitledXX, dengan XX merupakan nomor
						// urut yang disimpan
						// nomor urut disimpan di shared preferences
						SharedPreferences shared = getSharedPreferences("untitled", Context.MODE_PRIVATE);
						int i = shared.getInt("number", 0);
						i++;
						savePicture("untitled" + i); // save gambar dengan nama
														// untitledXX
						SharedPreferences.Editor editor = shared.edit();
						editor.putInt("number", i);
						editor.commit();
					} else {
						// save saja sesuai dengan nama yang diinputkan
						savePicture(value.toString());
					}
					finish();
				}
			});

			alert.setNegativeButton("?ñÊ?", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					// Canceled.
					finish();
				}
			});
			return alert.create();
		}

		}
		return super.onCreateDialog(id);
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		mPaint.setXfermode(null);
		mPaint.setAlpha(0xFF);

		switch (item.getItemId()) {
		case COLOR_MENU_ID:
			new ColorPickerDialog(this, this, mPaint.getColor()).show();
			return true;
		case EMBOSS_MENU_ID:
			if (mPaint.getMaskFilter() != mEmboss) {
				mPaint.setMaskFilter(mEmboss);
			} else {
				mPaint.setMaskFilter(null);
			}
			return true;
		case BLUR_MENU_ID:
			if (mPaint.getMaskFilter() != mBlur) {
				mPaint.setMaskFilter(mBlur);
			} else {
				mPaint.setMaskFilter(null);
			}
			return true;
		case ERASE_MENU_ID:
			mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
			return true;
		case SRCATOP_MENU_ID:
			mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP));
			mPaint.setAlpha(0x80);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
