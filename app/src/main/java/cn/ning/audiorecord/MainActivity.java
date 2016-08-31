package cn.ning.audiorecord;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.util.Date;

import cn.ning.audiorecord.audiorecord.record.AudioPermisionValidListener;
import cn.ning.audiorecord.audiorecord.record.ConfigBean;
import cn.ning.audiorecord.audiorecord.record.RecorderListener;
import cn.ning.audiorecord.audiorecord.record.RecorderManager;
import cn.ning.audiorecord.audiorecord.record.ScreenUtils;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private Button but ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        but = (Button) this.findViewById(R.id.but);
        but.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        recorder();
    }

    private void recorder() {

        ConfigBean.RecorderLocaltion recorderLocaltion = new ConfigBean.RecorderLocaltion();
        recorderLocaltion.parent = but;
        recorderLocaltion.height = ScreenUtils.getWindowsHight() * 2 / 5;
        ConfigBean.VoiceFileConfig voiceFileConfig = new ConfigBean.VoiceFileConfig();
        voiceFileConfig.fileName = "voice_temp_" + (new Date().getTime());
        voiceFileConfig.filePath = BaseApplication.CACHE_UPLOAD_VOICE_PATH;
        File f = new File(BaseApplication.CACHE_UPLOAD_VOICE_PATH);
        if (!f.exists()) {
            f.mkdirs();
        }
        final RecorderManager recorderManager = RecorderManager.getInstance(
                this, recorderLocaltion, voiceFileConfig,
                new RecorderListener() {
                    @Override
                    public void onRecordFinish(File file, int voiceLenght) {

                        //录音结束后在此拿到录音MP3文件
                    }

                    @Override
                    public void onRecordCancel() {

                    }
                },audioPermisionValidListener);
        recorderManager.showRecorder();
    }
    private AudioPermisionValidListener audioPermisionValidListener = new AudioPermisionValidListener() {

        @Override
        public void onPermisionInValid() {
            //如果没有录音权限
            String str = "录音被禁用,请在 \r设置-系统-应用-汽车头条-权限管理\r(将录音权限打开)";
            Toast.makeText(MainActivity.this,str,Toast.LENGTH_LONG).show();
        }
    };
}
