package cn.ning.audiorecord.audiorecord.record;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

public class IsAudioRecordPermision {
	/**
	 * 检测录音程序是否被禁用了
	 * @return  true:为可用;false:为禁用
	 */
	public static boolean IsAudioRecordValid() {
		boolean isValid = true;
		AudioRecord mRecorder;
		int bufferSize = AudioRecord.getMinBufferSize(8000,
				AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
		short[] mBuffer = new short[bufferSize];
		mRecorder = new AudioRecord(MediaRecorder.AudioSource.MIC, 8000,
				AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT,
				bufferSize);
		//开始录制音频  
        try{  
            // 防止某些手机崩溃，例如联想  
        	mRecorder.startRecording();
        }catch (IllegalStateException e){
            e.printStackTrace();  
            isValid = false;
        	return isValid;
        }finally{
        	
        }
		
		int readSize = mRecorder.read(mBuffer, 0, mBuffer.length);
		if(AudioRecord.ERROR_INVALID_OPERATION != readSize){
		      // 做正常的录音处理
		} else {
		     //录音可能被禁用了，做出适当的提示
			Log.i("zgn", "录音可能被禁用了");
			isValid = false;
		}
		// 停止录制
		try {
			// 防止某些手机崩溃，例如联想
			if (mRecorder != null) {
				// 停止
				mRecorder.stop();
				mRecorder.release();
				mRecorder = null;
			}
		} catch (IllegalStateException e) {
			e.printStackTrace();
		}
		return isValid;

	}
}
