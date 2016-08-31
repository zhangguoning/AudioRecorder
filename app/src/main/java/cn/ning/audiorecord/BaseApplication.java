package cn.ning.audiorecord;

import android.app.Application;
import android.os.Environment;

import java.io.File;

public class BaseApplication extends Application {


	public static String CACHE_BASE_PATH ;
	public static String CACHE_UPLOAD_VOICE_PATH ;
	private static BaseApplication mMainContext;// 主线程的上下文

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		this.mMainContext = this;
		File sdcardDir = Environment.getExternalStorageDirectory();
		CACHE_BASE_PATH = sdcardDir.getPath() + "/Android/data/cn.ning.audiorecord";
		CACHE_UPLOAD_VOICE_PATH = CACHE_BASE_PATH + "/voice/";
	}


	public static BaseApplication getApplication(){
		return mMainContext;
	}

}
