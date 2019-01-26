package com.artifex.mupdf;

import android.graphics.Bitmap;

public class BitmapHolder {
	private Bitmap bm;

	public BitmapHolder() {
		bm = null;
	}

	public synchronized void setBm(Bitmap abm) {
		if (bm != null && bm != abm)
			bm.recycle();
		bm = abm;
	}

	/**
	 * 功能：回收资源，释放内存
	 * @param
	 */
	public void recycleBitmap(Bitmap bitmap) {
		if(bitmap != null && bitmap.isRecycled()){
			bitmap.recycle();
			bitmap = null;
			System.gc();//提醒系统回收
		}
	}

	public synchronized void drop() {
		bm = null;
	}

	public synchronized Bitmap getBm() {
		return bm;
	}
}
