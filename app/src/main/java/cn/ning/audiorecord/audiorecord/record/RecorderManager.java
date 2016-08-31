package cn.ning.audiorecord.audiorecord.record;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;


import java.io.File;
import java.util.Date;

@SuppressLint("HandlerLeak")
/**
 * 录音模块管理类,单例模式，取得该类对象后，在回调接口中取得录音完成后的录音文件
 * @author LIANDONG
 *
 */
public class RecorderManager implements PopupWindowListener  , OnConvertListener{

	
	private AudioRecordPopupWindow popupWindowManager;

	private AudioRecordDialog dialogManager;

	private File voiceFile;

	private Context context;

	private View recorderView;

	private MediaPlayer mp;

	private Date date = new Date();

	private long startTime = 0l;

	private long endTime = 0l;

	private RecorderListener listener;
	private OnConvertListener onConvertListener ;
	private CtrlButtonState cbstate = CtrlButtonState.recorder;
	private static RecorderManager manager;
	private int voiceLenght = 0;

	private static int maxVoiceLenght = 60; // -1 为不限制时长
	private static int minVoiceLenght = 5; // 最短时长
	private boolean isRunning = true;
	private boolean isPlaying = false;
	private static final int UPDATETIME = 1344;
	private static final int ABORT = 4567;
	private static final int ConvertFile = 234;
	private boolean convertFileFlag = false;
	private float time_J = 0;// 计时器用(录音时)
	private int time_D = 0;// 定时器用(播放时)

	private Object lock = new Object();

	private AudioRecorder2Mp3Util mp3Util;
	private ConfigBean.VoiceFileConfig voiceFileConfig;
	private AudioPermisionValidListener permisionValidListener;

	private RecorderManager(Context context,
							ConfigBean.RecorderLocaltion recorderLocaltion,
							ConfigBean.VoiceFileConfig voiceFileConfig, RecorderListener listener,
							AudioPermisionValidListener permisionValidListener) {
		onConvertListener = this ;
		this.context = context;
		popupWindowManager = AudioRecordPopupWindow.getInstance(context,
				recorderLocaltion, this);
		this.voiceFileConfig = voiceFileConfig;
		recorderView = popupWindowManager.getRecorderView();
		dialogManager = AudioRecordDialog.getInstance(context);
		this.listener = listener;
		this.permisionValidListener = permisionValidListener;
		initView();
		mp = new MediaPlayer();
		initMp3Util();
		voiceFile = null;
	}

	private void initMp3Util() {

		mp3Util = new AudioRecorder2Mp3Util(context, voiceFileConfig.filePath
				+ voiceFileConfig.fileName + ".raw", voiceFileConfig.filePath
				+ voiceFileConfig.fileName + ".mp3");

	}

	public static RecorderManager getInstance(Context context,
											  ConfigBean.RecorderLocaltion recorderLocaltion,
											  ConfigBean.VoiceFileConfig voiceFileConfig, RecorderListener listener,
											  AudioPermisionValidListener permisionValidListener) {
		// if(manager==null){
		manager = new RecorderManager(context, recorderLocaltion,
				voiceFileConfig, listener, permisionValidListener);
		// }
		return manager;
	}

	public void showRecorder() {
		// if(context instanceof Activity){
		// Activity a = (Activity) context ;
		// View view = a.getWindow().peekDecorView();
		// if (view != null) {
		// InputMethodManager inputmanger = (InputMethodManager)
		// a.getSystemService(Context.INPUT_METHOD_SERVICE);
		// inputmanger.hideSoftInputFromWindow(view.getWindowToken(), 0);
		// }
		// }
		InputMethodManager imm = (InputMethodManager) context
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		// 得到InputMethodManager的实例
		// if (imm.isActive()) {
		// //如果开启
		// imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT,
		// InputMethodManager.HIDE_NOT_ALWAYS);
		// }
		popupWindowManager.showPopwindow();
	}

	private void initView() {
		recorderView.setOnTouchListener(new RecorderViewOnTouchListener());
		updateVoiceTime(0);

	}

	private void printcbstate() {
		if (cbstate == CtrlButtonState.recorder) {
			System.out.println("cbstate == CtrlButtonState.recorder");
		}
		if (cbstate == CtrlButtonState.stoped_Record) {
			System.out.println("cbstate == CtrlButtonState.stoped_Record");
		}
		if (cbstate == CtrlButtonState.stoped_paly) {
			System.out.println("cbstate == CtrlButtonState.stoped_paly");

		}
		if (cbstate == CtrlButtonState.playing) {
			System.out.println("cbstate == CtrlButtonState.playing");
		}
	}

	class RecorderViewOnTouchListener implements OnTouchListener {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			// printcbstate();
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:

				if (cbstate == CtrlButtonState.recorder) {// 准备就绪，点击开始录音
					try {
						if (!IsAudioRecordPermision.IsAudioRecordValid()) {
							// 录音被禁用
							permisionValidListener.onPermisionInValid();
							return false;
						}
						dialogManager.recording();
						dialogManager.showRecordingDialog();
						dialogManager.startRecordingAnimation();
						startTimer();
						startRecord();
						startTime = date.getTime();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					return true;
				}
				if (cbstate == CtrlButtonState.stoped_Record) {// 已停止录音，点击或许可回放

					playVoice();
					return true;
				}
				if (cbstate == CtrlButtonState.stoped_paly) {// 已停止播放，点击开始播放
					playVoice();
					return true;
				}
				if (cbstate == CtrlButtonState.playing) {// 正在播放，点击停止播放
					stopPlayVoice();
					return true;
				}

				break;

			case MotionEvent.ACTION_MOVE:

				if (cbstate == CtrlButtonState.recorder) {
					if (isInsideTouch(v, event.getX(), event.getY())) {
						dialogManager.recording();
					} else {
						dialogManager.wantToCancel();
					}
				}
				return true;

			case MotionEvent.ACTION_UP:

				if (cbstate == CtrlButtonState.recorder) {
					stopRecord();
					timer.removeMessages(UPDATETIME);
					endTime = date.getTime();
					//FIXME :
						dialogManager.stopRecordingAnimation();
						dialogManager.dimissDialog();
					
					if (isInsideTouch(v, event.getX(), event.getY())) {
						if (time_J < minVoiceLenght) {
							switchToStoppedRecord(StopReason.tooShort);
							return true;
						}
						if (maxVoiceLenght != -1 && time_J > maxVoiceLenght) {
							switchToStoppedRecord(StopReason.tooLong);
//							stopRecordingAndConvertFile();
							convertFile();
							return true;
						}

						voiceLenght = (int) time_J;
						switchToStoppedRecord(StopReason.byUserOK);
						System.out.println("正常停止录音，时长：" + voiceLenght);
						convertFile();
					} else {
						switchToStoppedRecord(StopReason.byUserCancel);
//						stopRecordingAndConvertFile();
//						stopRecord();
					}
				}
				return true;

			}
			return true;
		}

	}

	/**
	 * 更新时间
	 * 
	 * @param time
	 */
	private void updateVoiceTime(int time) {
		popupWindowManager.updateVoiceTime(time);
		dialogManager.updateVoiceTime(time);
	}

	private void switchTORecord() {// 切换至准备录音状态
		resetSate();
	}

	private void switchToStoppedPlay() {
		cbstate = CtrlButtonState.stoped_paly;
		dialogManager.dimissDialog();
		popupWindowManager.showStopedSate();
	}

	private void switchToStoppedRecord(StopReason st) {
		stopTimer();
		if (st == StopReason.byUserOK) {
			cbstate = CtrlButtonState.stoped_Record;
			popupWindowManager.showStopedSate();
			dialogManager.dimissDialog();
		}
		if (st == StopReason.byUserCancel) {
			cbstate = CtrlButtonState.recorder;
			resetSate();
		}
		if (st == StopReason.tooLong) {
			cbstate = CtrlButtonState.stoped_Record;
			popupWindowManager.showStopedSate();
//			dialogManager.tooLong();
			try {
				Toast.makeText(context, "录音时间太长，已自动停止", Toast.LENGTH_LONG).show();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (st == StopReason.tooShort) {
//			dialogManager.tooShort();
			try {
				Toast.makeText(context, "录音时间太短", Toast.LENGTH_LONG).show();
			} catch (Exception e) {
				e.printStackTrace();
			}
			popupWindowManager.resetState();
			cbstate = CtrlButtonState.recorder;
		}
	}

	private void switchToPlaying() {
		popupWindowManager.showPlayingState();
		cbstate = CtrlButtonState.playing;
		dialogManager.dimissDialog();
	}

	private void resetSate() {
		voiceFile = null;
		time_J = 0;
		time_D = 0;
		isPlaying = false;
		// timer.sendEmptyMessage(ABORT);
		voiceLenght = 0;
		cbstate = CtrlButtonState.recorder;
		popupWindowManager.resetState();
		dialogManager.dimissDialog();
		updateVoiceTime(0);
	}

	public static int getMaxVoiceLenght() {
		return maxVoiceLenght;
	}

	public static void setMaxVoiceLenght(int maxVoiceLenght) {
		RecorderManager.maxVoiceLenght = maxVoiceLenght;
	}

	public void setListener(RecorderListener listener) {
		this.listener = listener;
	}

	public static int getMinVoiceLenght() {
		return minVoiceLenght;
	}
	
	public static void setMinVoiceLenght(int minVoiceLenght) {
		RecorderManager.minVoiceLenght = minVoiceLenght;
	}
	
	// -------------私有工具方法---------------------


	private void playVoice() {
		System.out.println("-------开始播放------" + time_D);
		if (voiceFile != null && !isPlaying) {
			try {
				// mp = MediaPlayer.create(context, Uri.fromFile(new
				// File(voiceFileConfig.filePath+voiceFileConfig.fileName)));
				System.out.println("voiceFile :" + voiceFile.getAbsolutePath());
				mp = MediaPlayer.create(context, Uri.fromFile(voiceFile));
				mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
				mp.start();
				isPlaying = true;
				switchToPlaying();
				timer.sendEmptyMessage(ABORT);
				mp.setOnCompletionListener(new OnCompletionListener() {

					@Override
					public void onCompletion(MediaPlayer mp) {
						System.out.println("OnCompletionListener");
						stopPlayVoice();

					}
				});
			} catch (IllegalStateException e) {

				e.printStackTrace();
			}

		}
	}

	private void stopPlayVoice() {
		System.out.println("---停止播放----" + time_D);
		timer.removeMessages(ABORT);
		if (isPlaying && mp != null) {
			time_D = 0;
			isPlaying = false;
			if (mp.isPlaying()) {
				mp.stop();
			}
			mp.reset();
			mp.release();
			updateVoiceTime(voiceLenght);
			switchToStoppedPlay();
		}
	}

	/**
	 * 开始录音
	 */
	private void startRecord() {
		mp3Util.startRecording();
	}


	
	private void stopRecordingAndConvertFile() {
	//	DialogUtils.showLoadingMessage(context, "处理中,请稍后..", false);
		Log.e("重要提示:","类-RecorderManager 方法-stopRecordingAndConvertFile() 行-397，请在此给出录音正在转换得提示。。。。");
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				convertFileFlag = mp3Util.stopRecordingAndConvertFile();
				if (convertFileFlag) {
					voiceFile = new File(mp3Util.getFilePath(AudioRecorder2Mp3Util.MP3));
					onConvertListener.onConvertSuccess();
				} else {
					onConvertListener.onConvertFail();
					resetSate();
				}
				
			}
		}).start();
	}

	
	private void stopRecord(){
		mp3Util.stopRecording();
	}
	
	private void convertFile(){
//		DialogUtils.showLoadingMessage(context, "处理中,请稍后..", false);
		Log.e("重要提示:","类-RecorderManager 方法-convertFile() 行-422，请在此给出录音正在转换得提示。。。。");
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				convertFileFlag = mp3Util.convertFile();
				if (convertFileFlag) {
					voiceFile = new File(mp3Util.getFilePath(AudioRecorder2Mp3Util.MP3));
					onConvertListener.onConvertSuccess();
				} else {
					onConvertListener.onConvertFail();
					resetSate();
				}
				
			}
		}).start();
	}
	/**
	 * 判断（x,y） 是否在View 范围内
	 * 
	 * @param v
	 * @param x
	 * @param y
	 * @return
	 */
	private boolean isInsideTouch(View v, float x, float y) {

		if (x < 0) {
			return false;
		}
		if (x > v.getWidth()) {
			return false;
		}
		if (y < 0) {
			return false;
		}
		if (y > v.getHeight()) {
			return false;
		}
		return true;
	}

	private void startTimer() {
		isRunning = true;
		timer.sendEmptyMessage(UPDATETIME);
	}

	private void stopTimer() {
		isRunning = false;
		time_J = 0;
	}

	/**
	 * 计时器
	 */
	private Handler timer = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case UPDATETIME:// 计时器
				if (isRunning) {
					if (maxVoiceLenght == -1 || time_J < maxVoiceLenght) {
						time_J += 0.5;
						updateVoiceTime((int) time_J);
						timer.sendEmptyMessageDelayed(UPDATETIME, 500);
					} else {
						stopRecordingAndConvertFile();
						time_J += 0.5;
						updateVoiceTime((int) time_J);
						voiceLenght = (int) time_J;
						switchToStoppedRecord(StopReason.tooLong);
					}
				}
				return;
			case ABORT:// 定时器
				synchronized (lock) {
					if (isPlaying) {
						if (time_D <= voiceLenght) {
							updateVoiceTime(time_D);
							time_D++;
							timer.sendEmptyMessageDelayed(ABORT, 1000);
						}
					}
				}
				return;

			case ConvertFile:
				if (convertFileFlag) {
					Log.e("---", "录音并转换格式完成");
					System.out.println("录音并转换格式完成");
					voiceFile = new File(
							mp3Util.getFilePath(AudioRecorder2Mp3Util.MP3));
				} else {
					System.out.println("录音失败！");
				}

				return;
			}
		};
	};

	@Override
	public void onCancel() {
		stopPlayVoice();
		if (listener != null) {
			listener.onRecordCancel();
		}
		resetSate();
		popupWindowManager.dismimm();
		dialogManager.dimissDialog();
		mp3Util.cleanFile(AudioRecorder2Mp3Util.RAW);
		// mp3Util.close();
	}

	@Override
	public void onSubmit() {
		stopPlayVoice();
		if (listener != null) {
			listener.onRecordFinish(voiceFile, voiceLenght);
		}
		resetSate();
		popupWindowManager.dismimm();
		dialogManager.dimissDialog();
		mp3Util.cleanFile(AudioRecorder2Mp3Util.RAW);
		// mp3Util.close();
	}

	private boolean isValid() {
		return (endTime - startTime) / 1000 < 1 ? false : true;
	}
	
	@Override
	public void onConvertSuccess() {
//		DialogUtils.dismiss();
		
	}

	@Override
	public void onConvertFail() {
//		DialogUtils.dismiss();
		Toast.makeText(context, "文件保存出错,请重新录制", Toast.LENGTH_SHORT).show();
	}
}
