package com.example.liuyu.finalproject;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.liuyu.finalproject.user_sign.LoginActivity;
import com.example.liuyu.finalproject.user_sign.RegisterActivity;
import com.google.firebase.auth.FirebaseAuth;

public class StartAvtivity extends AppCompatActivity {

    private Button btnReg,btnLog;

    private FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_avtivity);

        btnReg = findViewById(R.id.start_reg_btn);
        btnLog = findViewById(R.id.start_log_btn);

        fAuth = FirebaseAuth.getInstance();

        updateUI();

        btnLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

        btnReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });
    }

    private void register(){
        Intent regIntent = new Intent(StartAvtivity.this, RegisterActivity.class);
        startActivity(regIntent);
    }

    private void login(){
        Intent logIntent = new Intent(StartAvtivity.this,LoginActivity.class);
        startActivity(logIntent);
    }

    private void updateUI(){
        if(fAuth.getCurrentUser()!=null){
            Log.i("StartActivity","fAuth!=null");
            Intent startIntent = new Intent(StartAvtivity.this,MainActivity.class);
            startActivity(startIntent);
            finish();

        }else{
            Log.i("StartActivity","fAuth==null");
        }
    }


}
