package cn.ning.audiorecord.audiorecord.record;


import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import cn.ning.audiorecord.R;


public class AudioRecordDialog {

	/**
	 * 以下为dialog的初始化控件，包括其中的布局文件
	 */

	private Dialog mDialog;

	private ImageView mIcon;
	private ImageView mVoice;

	private TextView mLable;

	private Context mContext;
	
	private TextView dilog_voice_time;

	private AnimationDrawable animationDrawable;
	
	
	
	private static AudioRecordDialog dialogManager ;

	private AudioRecordDialog(Context context) {
		mContext = context;
		//设置缺省监听器
	}

	public static AudioRecordDialog getInstance(Context context ){
//		if(dialogManager==null){
			dialogManager = new AudioRecordDialog(context);
//		}
		return dialogManager;
	}
	
	/**
	 * 将录音开关和录音对话框绑定
	 */
	
	public void showRecordingDialog() {
		// TODO Auto-generated method stub

		mDialog = new Dialog(mContext, R.style.Theme_audioDialog);
		
		LayoutInflater inflater = LayoutInflater.from(mContext);
		View view = inflater.inflate(R.layout.dialog_manager, null);
		mDialog.setContentView(view);
		
		
		mIcon = (ImageView) mDialog.findViewById(R.id.dialog_icon);
		mVoice = (ImageView) mDialog.findViewById(R.id.dialog_voice);
		mLable = (TextView) mDialog.findViewById(R.id.recorder_dialogtext);
		dilog_voice_time = (TextView) mDialog.findViewById(R.id.dilog_voice_time);
//		mDialog.setCancelable(false);
//		mDialog.setCanceledOnTouchOutside(false);
		mDialog.show();
		
	}

	/**
	 * 设置正在录音时的dialog界面
	 */
	public void recording() {
		if (mDialog != null && mDialog.isShowing()) {
			mIcon.setVisibility(View.VISIBLE);
			mVoice.setVisibility(View.VISIBLE);
			mLable.setVisibility(View.VISIBLE);

			mIcon.setImageResource(R.drawable.recorder);
			mLable.setText("手指上滑，取消发送");
		}
	}

	/**
	 * 取消界面
	 */
	public void wantToCancel() {
		// TODO Auto-generated method stub
		if (mDialog != null && mDialog.isShowing()) {
			mIcon.setVisibility(View.VISIBLE);
			mVoice.setVisibility(View.GONE);
			mLable.setVisibility(View.VISIBLE);

			mIcon.setImageResource(R.drawable.cancel);
			mLable.setText("松开手指，取消发送");
		}

	}

	// 时间过短
	public void tooShort() {
		// TODO Auto-generated method stub
		if (mDialog != null && mDialog.isShowing()) {
			mIcon.setVisibility(View.VISIBLE);
			mVoice.setVisibility(View.GONE);
			mLable.setVisibility(View.VISIBLE);

			mIcon.setImageResource(R.drawable.voice_to_short);
			mLable.setText("录音时间过短");
		}

	}
	// 时间过长
		public void tooLong() {
			// TODO Auto-generated method stub
			if (mDialog != null && mDialog.isShowing()) {
				mIcon.setVisibility(View.VISIBLE);
				mVoice.setVisibility(View.GONE);
				mLable.setVisibility(View.VISIBLE);

				mIcon.setImageResource(R.drawable.voice_to_short);
				mLable.setText("录音时间太长,已自动停止");
			}

		}

	// 隐藏dialog
	public void dimissDialog() {
		// TODO Auto-generated method stub

		if (mDialog != null && mDialog.isShowing()) {
			mDialog.dismiss();
			mDialog = null;
		}

	}

	private  void updateVoiceLevel(int level) {
		// TODO Auto-generated method stub

		if (mDialog != null && mDialog.isShowing()) {

			//先不改变它的默认状态
//			mIcon.setVisibility(View.VISIBLE);
//			mVoice.setVisibility(View.VISIBLE);
//			mLable.setVisibility(View.VISIBLE);

			//通过level来找到图片的id，也可以用switch来寻址，但是代码可能会比较长
			int resId = mContext.getResources().getIdentifier("v" + level,
					"drawable", mContext.getPackageName());
			
			mVoice.setImageResource(resId);
		}

	}
	public void updateVoiceTime(int time){
		if (mDialog != null && mDialog.isShowing()) {
			dilog_voice_time.setText(Util.formatTime(time));
		}
	}
	
	public void startRecordingAnimation(){
		mVoice.setImageResource(R.drawable.recording_animation);
		animationDrawable  = (AnimationDrawable) mVoice.getDrawable();
		animationDrawable.start();
	}
	
	public void stopRecordingAnimation(){
		animationDrawable.stop();
	}
	

	public boolean isShowing(){
		if(mDialog == null){
			return false ;
		}
		
		return mDialog.isShowing();
	}
	

}
