package com.example.yaoa.bluetooth2;

import android.app.Activity;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
//import android.speech.tts.TextToSpeech.OnInitListener;
//import android.content.Intent;

/**
 * Created by yaoa on 7/29/2015.
 */
public class Text2SpeechTest extends Activity implements TextToSpeech.OnInitListener {

    TextToSpeech talker;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.main);
        talker = new TextToSpeech(this, this);
    }

    public void say(String text2say){
        talker.speak(text2say, TextToSpeech.QUEUE_FLUSH, null, null);
    }

    @Override
    public void onInit(int status) {
//        say("Hello");

    }

    @Override
    public void onDestroy() {
        if (talker != null) {
            talker.stop();
            talker.shutdown();
        }

        super.onDestroy();
    }
}
