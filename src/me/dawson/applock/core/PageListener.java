package me.dawson.applock.core;

import android.app.Activity;

public interface PageListener {

	void onActivityCreated(Activity activity);

	void onActivityStarted(Activity activity);

	void onActivityResumed(Activity activity);

	void onActivityPaused(Activity activity);

	void onActivityStopped(Activity activity);

	void onActivitySaveInstanceState(Activity activity);

	void onActivityDestroyed(Activity activity);
}
