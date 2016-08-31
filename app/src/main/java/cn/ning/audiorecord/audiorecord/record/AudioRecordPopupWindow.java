package cn.ning.audiorecord.audiorecord.record;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView;

import cn.ning.audiorecord.R;
import cn.ning.audiorecord.audiorecord.widget.CircleImageView;
import cn.ning.audiorecord.audiorecord.widget.RippleBackground;


public class AudioRecordPopupWindow {

	private View rootView;
	/**
	 * 录音按钮
	 */
	private CircleImageView recorderView;
	/**
	 * 水波纹背景
	 */
	private RippleBackground ripple_rl ;
	private Context context;

	private PopupWindow window;
	private TextView window_voice_time_tv , window_cancel_tv , window_ok_tv , window_tip_tv;

	private ConfigBean.RecorderLocaltion recorderLocaltion;
	private static AudioRecordPopupWindow manager;
	private PopupWindowListener listener ;
	
	
	public interface OnPopupWindowDismissListener{
		public void OnPopupWindowDismiss();
	}
	private AudioRecordPopupWindow(Context context,
								   ConfigBean.RecorderLocaltion recorderLocaltion , PopupWindowListener listener) {
		this.context = context;
		this.recorderLocaltion = recorderLocaltion;
		this.listener = listener ;
		initView();
	}

	public void setListener(PopupWindowListener listener) {
		this.listener = listener;
	}

	public static AudioRecordPopupWindow getInstance(Context context,
													 ConfigBean.RecorderLocaltion recorderLocaltion , PopupWindowListener listener) {
//		if (manager == null) {
			manager = new AudioRecordPopupWindow(context,
					recorderLocaltion , listener);
//		}
		return manager;
	}

	private void initView(){
		rootView = LayoutInflater.from(context).inflate(R.layout.recorder_popupwindow, null);
		recorderView = (CircleImageView) rootView.findViewById(R.id.record_iv);
		ripple_rl = (RippleBackground) rootView.findViewById(R.id.ripple_rl);
		window_voice_time_tv = (TextView) rootView.findViewById(R.id.window_time_tv);
		window_cancel_tv = (TextView) rootView.findViewById(R.id.window_cancel_tv);
		window_ok_tv = (TextView) rootView.findViewById(R.id.window_ok_tv);
		window_tip_tv = (TextView) rootView.findViewById(R.id.window_tip_tv);
		
		window = new PopupWindow(rootView, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
		// 设置popWindow弹出窗体可点击，这句话必须添加，并且是true
		window.setFocusable(true);
		ColorDrawable dw = new ColorDrawable(0xffffffff);//半透明：0xb0000000
		window.setBackgroundDrawable(dw);
		// 设置popWindow的显示和消失动画
		window.setAnimationStyle(R.style.mypopwindow_anim_style);
		window_cancel_tv.setOnClickListener(new CancelOnclickListener());
		window_ok_tv.setOnClickListener(new OKOnclickListener());
		window.setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss() {
				if(listener!=null){
					listener.onCancel();
					updateVoiceTime(0);
				}
			}
		});
		window.setOutsideTouchable(false);
	}
	public void showPopwindow() {
		if(window.isShowing()){
			return ;
		}
		// 在底部显示
		if (recorderLocaltion != null) {
			window.setHeight(recorderLocaltion.height);
				window.showAtLocation(recorderLocaltion.parent,
						recorderLocaltion.gravity, recorderLocaltion.offsetX,
						recorderLocaltion.offsetY);
		}
	}
	
	public void showPlayingState(){
		recorderView.setImageResource(R.drawable.dialog_sendout_paly);
		ripple_rl.startRippleAnimation();
		window_tip_tv.setText("点击停止");
	}
	
	public void showStopedSate(){
		recorderView.setImageResource(R.drawable.dialog_sendouut_stop);
		ripple_rl.stopRippleAnimation();
		window_tip_tv.setText("点击回放");
	}
	public void resetState(){
		recorderView.setImageResource(R.drawable.dialog_sendout_star);
		ripple_rl.stopRippleAnimation();
		updateVoiceTime(0);
		window_tip_tv.setText("按住说话");
		
	}
	
	public void dismimm() {
		if (window != null) {
			window.dismiss();
		}
	}

	class CancelOnclickListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			if(listener!=null){
				listener.onCancel();
			}
			
		}
		
	}

	class OKOnclickListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			if(listener!=null){
				listener.onSubmit();
			}
			
		}
		
	}
	public void updateVoiceTime(int time){
//		if (window != null && window.isShowing()) {
		if (window != null ) {
			window_voice_time_tv.setText(Util.formatTime(time));
		}
	}
	public View getRecorderView() {
		return recorderView;
	}
	
}
