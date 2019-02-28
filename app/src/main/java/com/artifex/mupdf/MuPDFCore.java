package com.artifex.mupdf;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.Log;

public class MuPDFCore
{
	/* load our native library */
	static {
		System.loadLibrary("mupdf");
	}

	/* Readable members */
	private int pageNum  = -1;;
	private int numPages = -1;
	public  float pageWidth;
	public  float pageHeight;

	/* The native functions */
	private static native int openFile(String filename);
	private static native int countPagesInternal();
	private static native void gotoPageInternal(int localActionPageNum);
	private static native float getPageWidth();
	private static native float getPageHeight();
	public static native void drawPage(Bitmap bitmap,
			int pageW, int pageH,
			int patchX, int patchY,
			int patchW, int patchH);
	public static native void updatePageInternal(Bitmap bitmap,
										   int page,
										   int pageW, int pageH,
										   int patchX, int patchY,
										   int patchW, int patchH);
	public static native RectF[] searchPage(String text);
	public static native int getPageLink(int page, float x, float y);
	public static native LinkInfo[] getPageLinksInternal(int page);
	public static native boolean hasOutlineInternal();
	public static native boolean needsPasswordInternal();
	public static native boolean authenticatePasswordInternal(String password);
	public static native void destroying();

	public MuPDFCore(String filename) throws Exception
	{
		if (openFile(filename) <= 0)
		{
			throw new Exception("Failed to open "+filename);
		}
	}

	public  int countPages()
	{
		if (numPages < 0)
			numPages = countPagesSynchronized();

		return numPages;
	}

	private synchronized int countPagesSynchronized() {
		return countPagesInternal();
	}

	/* Shim function */
	public void gotoPage(int page)
	{
		if (page > numPages-1)
			page = numPages-1;
		else if (page < 0)
			page = 0;
		if (this.pageNum == page)
			return;
		gotoPageInternal(page);
		this.pageNum = page;
		this.pageWidth = getPageWidth();
		this.pageHeight = getPageHeight();
	}

	public synchronized PointF getPageSize(int page) {
		gotoPage(page);
		return new PointF(pageWidth, pageHeight);
	}

	public synchronized void onDestroy() {
		destroying();
	}

	public synchronized Bitmap drawPage(int page,
										int pageW, int pageH,
										int patchX, int patchY,
										int patchW, int patchH) {
		gotoPage(page);
		Bitmap bm = Bitmap.createBitmap(patchW, patchH, Bitmap.Config.ARGB_8888);
		drawPage(bm, pageW, pageH, patchX, patchY, patchW, patchH);
		return bm;
	}

	public synchronized Bitmap updatePage(BitmapHolder h, int page,
										  int pageW, int pageH,
										  int patchX, int patchY,
										  int patchW, int patchH) {
		Bitmap bm = null;
		Bitmap old_bm = h.getBm();

		if (old_bm == null)
			return null;

		bm = old_bm.copy(Bitmap.Config.ARGB_8888, false);
		old_bm = null;

		updatePageInternal(bm, page, pageW, pageH, patchX, patchY, patchW, patchH);
		return bm;
	}

	public synchronized int hitLinkPage(int page, float x, float y) {
		return getPageLink(page, x, y);
	}

	public synchronized LinkInfo[] getPageLinks(int page) {
		return getPageLinksInternal(page);
	}

	public synchronized RectF[] searchPage(int page, String text) {
		gotoPage(page);
		Log.e("TAG", "searchPage: "+text);
		return searchPage(text);
	}

	public synchronized boolean hasOutline() {
		return hasOutlineInternal();
	}

	public synchronized boolean needsPassword() {
		return needsPasswordInternal();
	}

	public synchronized boolean authenticatePassword(String password) {
		return authenticatePasswordInternal(password);
	}
}
