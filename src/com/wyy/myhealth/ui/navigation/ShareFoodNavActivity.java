package com.wyy.myhealth.ui.navigation;

import com.wyy.myhealth.R;
import com.wyy.myhealth.analytics.UmenAnalyticsUtility;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class ShareFoodNavActivity extends Activity {

	private static final String TAG = ShareFoodNavActivity.class
			.getSimpleName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_share_nav);
		findViewById(R.id.share_nav).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		UmenAnalyticsUtility.onResume(this);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		UmenAnalyticsUtility.onPause(this);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		setIsFristUse(ShareFoodNavActivity.this, false);
	}

	public static boolean getIsFirstUse(Context context) {
		boolean isFirstUse = false;
		SharedPreferences sharedPreferences = context.getSharedPreferences(TAG,
				Context.MODE_PRIVATE);
		isFirstUse = sharedPreferences.getBoolean("isfirstuse", true);
		return isFirstUse;

	}

	public static void setIsFristUse(Context context, boolean isFirstUse) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(TAG,
				Context.MODE_PRIVATE);
		Editor editor = sharedPreferences.edit();
		editor.putBoolean("isfirstuse", isFirstUse);
		editor.commit();
	}

}
