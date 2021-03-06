package com.example.liuyu.finalproject;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.speech.RecognizerIntent;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;

public class SpeechToTextActivity extends AppCompatActivity {

    final static int AUDIO_REQUEST_CODE = 888;
    final static int REQ_CODE_SPEECH_INPUT = 111;
    private TextView voiceInput;
    private Button speakButton,save;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speech_to_text);

        voiceInput = findViewById(R.id.voiceInput);
        speakButton = findViewById(R.id.btnSpeak);
        save = findViewById(R.id.speech_text_save);

        speakButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                askSpeechInput();
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("TextFromSpeech",voiceInput.getText().toString());
                setResult(RESULT_OK,intent);
                finish();
            }
        });
    }

    //Using this function to check audio permission
    public boolean checkAudioPermission(){
        boolean result = ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO)== (PackageManager.PERMISSION_GRANTED);
        return result;
    }

    //Request audio permission
    public void requestAudioPermission(){
        String audioPermission[] = new String[]{Manifest.permission.RECORD_AUDIO};
        ActivityCompat.requestPermissions(this,audioPermission,
                AUDIO_REQUEST_CODE);
    }

    //Handle permission result
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(grantResults.length>0){
            boolean AudioRequestAccepted =
                    grantResults[0]==PackageManager.PERMISSION_GRANTED;
            if(AudioRequestAccepted){
                //Start google speech to text here

            }else{
                Toast.makeText(this,"permission denied",Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void askSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                "You can say something now!");
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    voiceInput.setText(result.get(0));
                }
                break;
            }
        }
    }


}
