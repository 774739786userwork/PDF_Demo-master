package com.example.jammy.pdf_demo.util;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.jammy.pdf_demo.R;


/**
 * @Description:自定义对话框
 * @author http://blog.csdn.net/finddreams
 */
public class CustomProgressDialog extends Dialog {

	private Context context = null;
	private int anim=0;
	private static CustomProgressDialog customProgressDialog = null;

	public CustomProgressDialog(Context context){
		super(context);
		this.context = context;
	}

	public CustomProgressDialog(Context context, int theme, int anim) {
		super(context, theme);
		this.anim=anim;
	}

	public static CustomProgressDialog createDialog(Context context,int anim){
		customProgressDialog = new CustomProgressDialog(context, R.style.CustomProgressDialog ,anim);
		customProgressDialog.setContentView(R.layout.progress_dialog);
		customProgressDialog.getWindow().getAttributes().gravity = Gravity.CENTER;

		return customProgressDialog;
	}

	public void onWindowFocusChanged(boolean hasFocus){

		if (customProgressDialog == null){
			return;
		}

		ImageView imageView = (ImageView) customProgressDialog.findViewById(R.id.loadingIv);
		if(anim!=0) {
			imageView.setBackgroundResource(anim);
		}
		AnimationDrawable animationDrawable = (AnimationDrawable) imageView.getBackground();
		animationDrawable.start();
	}

	/**
	 * 设置标题
	 * @param strTitle
	 * @return
	 */
	public CustomProgressDialog setTitile(String strTitle){
		return customProgressDialog;
	}

	/**
	 * 设置提示内容
	 * @param strMessage
	 * @return
	 */
	public CustomProgressDialog setMessage(String strMessage){
		TextView tvMsg = (TextView)customProgressDialog.findViewById(R.id.loadingTv);

		if (tvMsg != null){
			tvMsg.setText(strMessage);
		}

		return customProgressDialog;
	}


}

