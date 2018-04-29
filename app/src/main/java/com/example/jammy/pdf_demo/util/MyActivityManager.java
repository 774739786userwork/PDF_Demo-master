package com.example.jammy.pdf_demo.util;
import java.util.Stack;
import android.app.Activity;
import android.content.Context;


/**
 * activity堆栈管理类
 */
public class MyActivityManager {

	private static Stack<Activity> activityStack;
	private static MyActivityManager instance;

	public static MyActivityManager getInstance() {
		if (instance == null) {
			instance = new MyActivityManager();
		}
		return instance;
	}

	/**
	 * 退出栈顶Activity
	 * 
	 * @param
	 */
	public void popActivity(Activity activity) {
		if (activity != null) {
			activity.finish();
			if (activityStack != null && !activityStack.empty()) {
				activityStack.remove(activity);
			}
			activity = null;
		}
	}

	/**
	 * 获得当前栈顶Activity
	 * 
	 * @return Activity
	 */
	public Activity currentActivity() {
		Activity activity = null;
		if (activityStack != null) {
			if (!activityStack.empty())
				activity = activityStack.lastElement();
		}
		return activity;
	}

	/**
	 * 退出栈中所有Activity
	 */
	public void popAllActivity() {
		Activity activity = null;
		if (activityStack != null && !activityStack.empty()) {
			int count = activityStack.size();
			for (int i = 0; i < count; i++) {
				activity = activityStack.elementAt(i);
				if (activity != null && !activity.isFinishing()) {
					activity.finish();
				}
			}
		}
		if (activityStack != null && !activityStack.empty()) {
			activityStack.clear();
			activityStack = null;
		}
	}

	/**
	 * 将当前Activity推入栈中
	 * 
	 * @param
	 */
	public void pushActivity(Activity activity) {
		if (activityStack == null) {
			activityStack = new Stack<Activity>();
		}
		activityStack.add(activity);
	}

	/**
	 * 除了当前Activity退出栈中所有其他Activity
	 * 
	 * @param cls
	 *            当前Activity
	 */
	public void popAllActivityExceptOne(Class<?> cls) {
		while (true) {
			Activity activity = currentActivity();
			if (activity == null) {
				break;
			}
			if (activity.getClass().equals(cls)) {
				break;
			}
			popActivity(activity);
		}
	}

	/**
	 * 退出应用
	 */
	public void exitApp(Context con) {
		popAllActivity();
		System.exit(0);
	}

}