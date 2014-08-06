package me.dawson.applock.core;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Log;

public class AppLockImpl extends AppLock implements PageListener {
	public static final String TAG = "DefaultAppLock";

	private static final String PASSWORD_PREFERENCE_KEY = "passcode";
	private static final String PASSWORD_SALT = "7xn7@c$";

	private SharedPreferences settings;

	private int liveCount;
	private int visibleCount;

	private long lastActive;

	public AppLockImpl(Application app) {
		super();
		SharedPreferences settings = PreferenceManager
				.getDefaultSharedPreferences(app);
		this.settings = settings;
		this.liveCount = 0;
		this.visibleCount = 0;
	}

	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	public void enable() {
		BaseActivity.setListener(this);
	}

	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	public void disable() {
		BaseActivity.setListener(null);
	}

	public boolean checkPasscode(String passcode) {
		passcode = PASSWORD_SALT + passcode + PASSWORD_SALT;
		passcode = Encryptor.getSHA1(passcode);
		String storedPasscode = "";

		if (settings.contains(PASSWORD_PREFERENCE_KEY)) {
			storedPasscode = settings.getString(PASSWORD_PREFERENCE_KEY, "");
		}

		if (passcode.equalsIgnoreCase(storedPasscode)) {
			return true;
		} else {
			return false;
		}
	}

	public boolean setPasscode(String passcode) {
		SharedPreferences.Editor editor = settings.edit();

		if (passcode == null) {
			editor.remove(PASSWORD_PREFERENCE_KEY);
			editor.commit();
			this.disable();
		} else {
			passcode = PASSWORD_SALT + passcode + PASSWORD_SALT;
			passcode = Encryptor.getSHA1(passcode);
			editor.putString(PASSWORD_PREFERENCE_KEY, passcode);
			editor.commit();
			this.enable();
		}

		return true;
	}

	// Check if we need to show the lock screen at startup
	public boolean isPasscodeSet() {
		if (settings.contains(PASSWORD_PREFERENCE_KEY)) {
			return true;
		}

		return false;
	}

	private boolean shouldLockSceen(Activity activity) {
		String clazzName = activity.getClass().getName();

		// already unlock
		if (activity instanceof AppLockActivity) {
			AppLockActivity ala = (AppLockActivity) activity;
			if (ala.getType() == AppLock.UNLOCK_PASSWORD) {
				Log.d(TAG, "already unlock activity");
				return false;
			}
		}

		// no pass code set
		if (!isPasscodeSet()) {
			Log.d(TAG, "lock passcode not set.");
			return false;
		}

		// ignored activities
		if (ignoredActivities.contains(clazzName)) {
			Log.d(TAG, "ignore activity " + clazzName);
			return false;
		}

		// no enough timeout
		long passedTime = System.currentTimeMillis() - lastActive;
		if (lastActive > 0 && passedTime <= lockTimeOut) {
			Log.d(TAG, "no enough timeout " + passedTime + " for "
					+ lockTimeOut);
			return false;
		}

		// start more than one page
		if (visibleCount > 1) {
			return false;
		}

		return true;
	}

	@Override
	public void onActivityPaused(Activity activity) {
		String clazzName = activity.getClass().getName();
		Log.d(TAG, "onActivityPaused " + clazzName);
	}

	@Override
	public void onActivityResumed(Activity activity) {
		String clazzName = activity.getClass().getName();
		Log.d(TAG, "onActivityResumed " + clazzName);

		if (shouldLockSceen(activity)) {

			Intent intent = new Intent(activity.getApplicationContext(),
					AppLockActivity.class);
			intent.putExtra(AppLock.TYPE, AppLock.UNLOCK_PASSWORD);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			activity.getApplication().startActivity(intent);
		}

		lastActive = 0;
	}

	@Override
	public void onActivityCreated(Activity activity) {
		liveCount++;
	}

	@Override
	public void onActivityDestroyed(Activity activity) {
		liveCount--;
		if (liveCount == 0) {
			lastActive = System.currentTimeMillis();
			Log.d(TAG, "set last active " + lastActive);
		}
	}

	@Override
	public void onActivitySaveInstanceState(Activity activity) {
	}

	@Override
	public void onActivityStarted(Activity activity) {
		String clazzName = activity.getClass().getName();
		Log.d(TAG, "onActivityStarted " + clazzName);
		visibleCount++;
	}

	@Override
	public void onActivityStopped(Activity activity) {
		String clazzName = activity.getClass().getName();
		Log.d(TAG, "onActivityStopped " + clazzName);
		visibleCount--;
		if (visibleCount == 0) {
			lastActive = System.currentTimeMillis();
			Log.d(TAG, "set last active " + lastActive);
		}
	}
}
