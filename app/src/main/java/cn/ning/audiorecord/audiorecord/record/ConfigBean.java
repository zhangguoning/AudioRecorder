package cn.ning.audiorecord.audiorecord.record;

import android.os.Environment;
import android.view.Gravity;
import android.view.View;

import java.io.File;

public class ConfigBean {

	/**
	 * PopupWindow的出现位置
	 * @author LIANDONG
	 *
	 */
	public static class RecorderLocaltion {
		public View parent = null;
		/**
		 *缺省值：gravity = Gravity.BOTTOM
		 */
		public  int gravity = Gravity.BOTTOM;
		/**
		 *缺省值：offsetX = 0 ;
		 */
		public  int offsetX = 0;
		/**
		 *缺省值：offsetY = 0 ;
		 */
		public  int offsetY = 0;
		/**
		 *缺省值：height = 400 ;
		 *
		 **/
		public int height = 400;
	}
	
	/**
	 * 录音文件的存放路径和文件名(无需设置后缀名,默认返回.mp3文件)
	 */
	public static class VoiceFileConfig{
		
		public String filePath ;
		
		public String fileName;
		
		public static String getSDCardPath(){
			if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
				File path = Environment.getExternalStorageDirectory();
				return path.getAbsolutePath();
		}
			return null;  
		}
	}
}
