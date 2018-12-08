package com.example.liuyu.finalproject;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

public class SpeechToTextActivity extends AppCompatActivity {

    final static int AUDIO_REQUEST_CODE = 888;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speech_to_text);
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
}
