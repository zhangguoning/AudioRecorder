# AudioRecord
一个封装好的录音器模块，具有录音、回放、转码（转为MP3格式）功能
<br/>
![](https://github.com/zhangguoning/AudioRecord/raw/master/voice.gif)
<br/>
```java
ConfigBean.RecorderLocaltion recorderLocaltion = new ConfigBean.RecorderLocaltion();
        recorderLocaltion.parent = but; //录音器弹出时候出现的位置，默认在此空间下方
        recorderLocaltion.height = ScreenUtils.getWindowsHight() * 2 / 5; //录音器的高度
        ConfigBean.VoiceFileConfig voiceFileConfig = new ConfigBean.VoiceFileConfig(); //录音文件保存路径
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
                      //录音取消
                    }
                },audioPermisionValidListener);
        recorderManager.showRecorder(); //显示录音器

```
