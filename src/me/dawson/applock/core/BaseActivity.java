package me.dawson.applock.core;

import android.app.Activity;
import android.os.Bundle;

public class BaseActivity extends Activity {

	private static PageListener pageListener;

	public static void setListener(PageListener listener) {
		pageListener = listener;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (pageListener != null) {
			pageListener.onActivityCreated(this);
		}
	}

	@Override
	protected void onStart() {
		super.onStart();

		if (pageListener != null) {
			pageListener.onActivityStarted(this);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();

		if (pageListener != null) {
			pageListener.onActivityResumed(this);
		}
	}

	@Override
	protected void onPause() {
		super.onPause();

		if (pageListener != null) {
			pageListener.onActivityPaused(this);
		}
	}

	@Override
	protected void onStop() {
		super.onStop();

		if (pageListener != null) {
			pageListener.onActivityStopped(this);
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		if (pageListener != null) {
			pageListener.onActivityDestroyed(this);
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		if (pageListener != null) {
			pageListener.onActivitySaveInstanceState(this);
		}
	}
}
