package com.sctw.bonnie.paint;
import com.sctw.bonnie.R;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

public class PaintActivity extends Activity {

	Button mBtnNew,mBtnConfig;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_paint);
		
		findview();
		mBtnNew.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				
				Intent intent = new Intent();
				intent.setClass(PaintActivity.this, FreePaint.class);
				startActivity(intent);			
			}
		});
		
		mBtnConfig.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(PaintActivity.this, SettingsPerferencesActivity.class);
				startActivity(intent);			
			}
		});
	}
	
	
	private void findview()
	{
		mBtnNew = (Button) findViewById(R.id.id_btn_new);
		mBtnConfig = (Button) findViewById(R.id.id_btn_config);
	}
	
}
