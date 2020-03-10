package com.lys.androidos.tts;

import android.app.AlertDialog;
import android.os.Build;
import android.speech.tts.TextToSpeech;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.lys.androidos.ttslibrary.MediaTTSManager;
import com.lys.androidos.ttslibrary.WhyTTS;

import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private TextToSpeech mSpeech = null;
    private EditText ttsEditor = null;
    private String choosedLanguage;
    private RadioButton english,chainese,german,french,taiWan;
    private RadioGroup languageGroup;
    private WhyTTS whyTTS;
    String testText="What can I do for you,please tell me";

    public static class SharedData {
        //语速
        public static float voice_speed=0.5f;
        //音调
        public static float voice_pitch=1.0f;

        //
        public static Map<String,Locale> languageList=new HashMap<String,Locale>();

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initUI();
        initLanguageList();
        mSpeech = new TextToSpeech(MainActivity.this, new TTSListener());
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void initUI() {
        ttsEditor = findViewById(R.id.test_text);

        //Achieve TTS pause and resume by Android original TTS and MediaPlayer
        whyTTS= MediaTTSManager.getInstance(this);
        
        languageGroup=findViewById(R.id.language_Group);
        english = findViewById(R.id.language_English);
        chainese = findViewById(R.id.language_Chainese);
        german = findViewById(R.id.language_German);
        french = findViewById(R.id.language_French);
        taiWan=findViewById(R.id.language_TaiWan);
    }



    public void startTest(View view){
        if(!ttsEditor.getText().toString().isEmpty()){
            whyTTS.speak(ttsEditor.getText().toString());
        }
        else {
            whyTTS.speak(testText);
        }
    }

    public void pause(View view){
        whyTTS.pause();
    }

    public void resume(View view){
        whyTTS.resume();
    }










    private class TTSListener implements OnInitListener {
        @Override
        public void onInit(int status) {
            // TODO Auto-generated method stub
            if (status == TextToSpeech.SUCCESS) {
//                int supported = mSpeech.setLanguage(Locale.US);
//                if ((supported != TextToSpeech.LANG_AVAILABLE) && (supported != TextToSpeech.LANG_COUNTRY_AVAILABLE)) {
//                    Toast.makeText(MainActivity.this, "不支持当前语言！", Toast.LENGTH_SHORT).show();
//                    Log.i(TAG, "onInit: 支持当前选择语言");
//                }else{
//                    mSpeech.speak("i love you", TextToSpeech.QUEUE_FLUSH, null);
//                }
                Log.i(TAG, "onInit: TTS引擎初始化成功");
            }
            else{
                Log.i(TAG, "onInit: TTS引擎初始化失败");
            }
        }
    }


    private void initLanguageList() {

        SharedData.languageList.put("英语",Locale.ENGLISH);
        SharedData.languageList.put("中文",Locale.CHINESE);
        SharedData.languageList.put("德语",Locale.GERMAN);
        SharedData.languageList.put("法语",Locale.FRENCH);
        SharedData.languageList.put("台湾话",Locale.TAIWAN);
    }

    public void openAudioFile(View view) {
        choosedLanguage=getLanguage(languageGroup);
        int supported = mSpeech.setLanguage(SharedData.languageList.get(choosedLanguage));
        mSpeech.setSpeechRate(SharedData.voice_speed);
        mSpeech.setPitch(SharedData.voice_pitch);

        Log.i(TAG, "选择语言: "+choosedLanguage+"--"+SharedData.languageList.get(choosedLanguage));
        if((supported != TextToSpeech.LANG_AVAILABLE) && (supported != TextToSpeech.LANG_COUNTRY_AVAILABLE)){
            //语言设置失败
            Log.i(TAG, "语言设置失败: "+choosedLanguage);
        }
        else{
            Log.i(TAG, "语言设置成功: "+choosedLanguage);
        }

        String tempStr = ttsEditor.getText().toString();
        mSpeech.speak(tempStr, TextToSpeech.QUEUE_FLUSH, null);
        Log.i(TAG, "测试文本: "+tempStr);
        Log.i(TAG, "当前语速: "+SharedData.voice_speed+"， 最快语速1.5");
        Log.i(TAG, "当前音调："+SharedData.voice_pitch+"， 最高音调2.0");
        //Log.i(TAG, "test: 执行了");
    }

    //保存音频文件
    public void saveAudioFile(View view) {
        HashMap<String, String> myHashRender = new HashMap<>();
        myHashRender.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, ttsEditor.getText().toString());
        mSpeech.synthesizeToFile(ttsEditor.getText().toString(), myHashRender,
                "/mnt/sdcard/"+ new Date().toString().replace(" ","_").trim()+".wav");
        Log.i(TAG, "saveAudioFile: "+"/mnt/sdcard/"+ new Date().toString().replace(" ","_").trim()+".wav"+"文件保存成功");
        Toast.makeText(this,"文件保存成功",Toast.LENGTH_SHORT).show();
    }

    private String getLanguage(RadioGroup languageGroup) {
        int choosedButtonID=languageGroup.getCheckedRadioButtonId();
        String tempStr="";
        if(choosedButtonID==english.getId()){
            tempStr="英语";
        }
        else if(choosedButtonID==chainese.getId()){
            tempStr="中文";
        }
        else if(choosedButtonID==german.getId()){
            tempStr="德语";
        }
        else if(choosedButtonID==french.getId()){
            tempStr="法语";
        }
        else if(choosedButtonID==taiWan.getId()){
            tempStr="台湾话";
        }
        else{

        }
        return tempStr;
    }

    //增加音量
    public void increVoice(View view){
        if(SharedData.voice_speed>=1.5f){
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setMessage("速度已经最快，无法调整");
            dialog.show();
        }
        else{
            SharedData.voice_speed+=0.1f;
        }
    }
    //减小音量
    public void decreVoice(View view){
        if(SharedData.voice_speed<=0.1f){
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setMessage("速度已经最慢，无法调整");
            dialog.show();
        }
        else{
            SharedData.voice_speed-=0.1f;
        }
    }
    //升高音调
    public void increPitch(View view){
        if(SharedData.voice_pitch>=2.0f){
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setMessage("音调已经最高，无法调整");
            dialog.show();
        }
        else{
            SharedData.voice_pitch+=0.1f;
        }
    }
    //减低音调
    public void decrePitch(View view){
        if(SharedData.voice_pitch<=0.1f){
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setMessage("音调已经最低，无法调整");
            dialog.show();
        }
        else{
            SharedData.voice_pitch-=0.1f;
        }
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        if (mSpeech != null) {
            mSpeech.stop();
            mSpeech.shutdown();
            mSpeech = null;
        }
        super.onDestroy();
    }
}
