package com.example.jammy.pdf_demo.util;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.drawable.AnimationDrawable;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.jammy.pdf_demo.R;


/**
 * @Description:自定义对话框
 */
public class CustomProgressDialog extends Dialog {

	private static int default_width = 160; //默认宽度
	public CustomProgressDialog(Context context) {
		super(context);
	}

	public CustomProgressDialog(Context context, int layout, int style) {
		this(context, default_width, layout, style);
	}

	public CustomProgressDialog(Context context, int width, int layout, int style) {
		super(context, style);
		//set content
		setContentView(layout);
		//set window params
		Window window = getWindow();
		WindowManager.LayoutParams params = window.getAttributes();
		window.setWindowAnimations(R.style.dialogWindowAnim); //设置窗口弹出动画
		//set width,height by density and gravity
		float density = getDensity(context);
		params.width = width;
//        params.height = (int) (height*density);
//        params.gravity = Gravity.CENTER;
		window.setAttributes(params);
	}

	private float getDensity(Context context) {
		Resources resources = context.getResources();
		DisplayMetrics dm = resources.getDisplayMetrics();
		return dm.density;
	}

	@Override
	public void show() {
		if (!isShowing()){
			super.show();
		}
	}


}

