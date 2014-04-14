package com.niks.utpvsec.activities;

import com.utp.vsec.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class SplashActivity extends Activity {

	private static final int SPLASH_DURATION = 2000;
	private boolean backKeyPressed = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash);
		Handler handler = new Handler();
		Runnable run = new Runnable() {

			@Override
			public void run() {
				finish();
				if (!backKeyPressed) {
					Intent intent = new Intent(SplashActivity.this, ScanActivity.class);
					SplashActivity.this.startActivity(intent);
				}
			}
		};
		handler.postDelayed(run, SPLASH_DURATION);
	}

	@Override
	public void onBackPressed() {
		backKeyPressed = true;
		super.onBackPressed();
	}

}
