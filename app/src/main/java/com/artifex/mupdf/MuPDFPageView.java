package com.artifex.mupdf;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.util.Log;

public class MuPDFPageView extends PageView {
	private final MuPDFCore mCore;
	public static int pdfSizeX;
	public static int pdfSizeY;
	public static int pdfPatchX;
	public static int pdfPatchY;
	public static int pdfPatchWidth;
	public static int pdfPatchHeight;
	public MuPDFPageView(Context c, MuPDFCore core, Point parentSize) {
		super(c, parentSize);
		mCore = core;
	}

	public int hitLinkPage(float x, float y) {
		// Since link highlighting was implemented, the super class
		// PageView has had sufficient information to be able to
		// perform this method directly. Making that change would
		// make MuPDFCore.hitLinkPage superfluous.
		float scale = mSourceScale*(float)getWidth()/(float)mSize.x;
		float docRelX = (x - getLeft())/scale;
		float docRelY = (y - getTop())/scale;
//		Log.v("info", "Page--->",docRelX);
		return mCore.hitLinkPage(mPageNumber, docRelX, docRelY);
	}

	/**
	 * patchX，patchY：pdf文件当前在屏幕显示的坐标原点。
	 * patchWidth，patchHeight：pdf文件原始宽和高。 1200 1696
	 * 在这里获取pdf原始宽高和当前屏幕显示坐标位置，用于定位书写坐标。return
	 */
	@Override
	protected Bitmap drawPage(int sizeX, int sizeY,
							int patchX, int patchY, int patchWidth, int patchHeight) {
		pdfSizeX = sizeX;
		pdfSizeY = sizeY;
		pdfPatchX = patchX;
		pdfPatchY = patchY;
		pdfPatchWidth = patchWidth;
		pdfPatchHeight = patchHeight;
		return mCore.drawPage(mPageNumber, sizeX, sizeY, patchX, patchY, patchWidth, patchHeight);
	}
	/*@Override
	protected Bitmap drawPage(int sizeX, int sizeY,
							  int patchX, int patchY, int patchWidth, int patchHeight) {
		return mCore.drawPage(mPageNumber, sizeX, sizeY, patchX, patchY, patchWidth, patchHeight);
	}*/

	@Override
	protected Bitmap updatePage(BitmapHolder h, int sizeX, int sizeY,
								int patchX, int patchY, int patchWidth, int patchHeight) {
		return mCore.updatePage(h, mPageNumber, sizeX, sizeY, patchX, patchY, patchWidth, patchHeight);
	}

	@Override
	protected LinkInfo[] getLinkInfo() {
		return mCore.getPageLinks(mPageNumber);
	}
	
}
