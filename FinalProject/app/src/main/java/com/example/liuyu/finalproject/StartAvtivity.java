package com.example.liuyu.finalproject;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.liuyu.finalproject.user_sign.LoginActivity;
import com.example.liuyu.finalproject.user_sign.RegisterActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class StartAvtivity extends AppCompatActivity {
    private static final int RC_SIGN_IN = 5;

    private Button btnReg,btnLog;
    private SignInButton btnGoogleLog;

    private FirebaseAuth fAuth;

    GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("604417706203-c6g3fqa0ftpemks2io3t1mfb7fsb1n39.apps.googleusercontent.com")
            .requestEmail()
            .build();

    GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_avtivity);

        btnReg = findViewById(R.id.start_reg_btn);
        btnLog = findViewById(R.id.start_log_btn);
        btnGoogleLog = findViewById(R.id.google_login);

        fAuth = FirebaseAuth.getInstance();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

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

        btnGoogleLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                googleLogin();
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

    private void googleLogin() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w("Google sign in failed", e);
                // ...
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d("firebaseAuthWithGoogle:", acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        fAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            System.out.println("signInWithCredential:success");
                            FirebaseUser user = fAuth.getCurrentUser();
                            updateUI();
                        } else {
                            // If sign in fails, display a message to the user.
                            System.out.println("signInWithCredential:failure");
                            System.out.println("signInWithCredential:failure");

                            Toast.makeText(getApplicationContext(), "Authentication Failed!" , Toast.LENGTH_LONG).show();
                            updateUI();
                        }

                    }
                });
    }


}
