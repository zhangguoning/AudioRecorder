package cn.ning.audiorecord.audiorecord.record;

import java.io.File;


/**
 * 调用录音模块只需实现此接口即可
 * @author ning
 *
 */
 public interface RecorderListener {

	public void onRecordCancel();
	
	public void onRecordFinish(File file, int voiceLenght);
	
}

/**
 * 内部使用的接口，外部调用时无需理会
 * @author ning
 *
 */
interface PopupWindowListener{

    public void onCancel();

    public void onSubmit();
}

/**
 * 内部使用的接口，外部调用时无需理会
 * @author ning
 *
 */
interface OnConvertListener {

   void onConvertSuccess();

   void onConvertFail();
}

