package com.example.liuyu.finalproject;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import java.time.Clock;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


public class NewNoteActivity extends AppCompatActivity {

    static final int REQUEST_CAMERA = 666;
    static final int REQUEST_SPEECH = 555;

    private Button btnCreate;
    private EditText etTitle,etContent,etType;
    private Toolbar mToolbar;
    private TextView mdate;

    private Button camera,speech;

    private FirebaseAuth fAuth;
    private DatabaseReference fNoteDatabase;

    private Menu mainMenu;
    private String noteID = "no";

    private boolean isExist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_note);

        try{
            noteID = getIntent().getStringExtra("noteId");

            if(noteID.equals("no")){
                mainMenu.getItem(0).setVisible(false);
                isExist = false;
            }else{
                isExist = true;
            }

        }catch (Exception e){
            e.printStackTrace();
        }

        btnCreate = findViewById(R.id.new_note_btn);
        etTitle = findViewById(R.id.new_note_title);
        etContent = findViewById(R.id.new_note_content);
        etType = findViewById(R.id.new_note_type);

        camera = findViewById(R.id.camera);
        speech = findViewById(R.id.speech);

        mToolbar = findViewById(R.id.toolbar2);
        mdate = findViewById(R.id.note_date);

        setSupportActionBar(mToolbar);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        fAuth = FirebaseAuth.getInstance();
        fNoteDatabase = FirebaseDatabase.getInstance().getReference().child("Notes")
                .child(fAuth.getCurrentUser().getUid());

        mdate.setText(getDate(System.currentTimeMillis()));

        //Camera activity start here
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent carmeraIntent = new Intent(NewNoteActivity.this,TextRecognitionActivity.class);
                startActivityForResult(carmeraIntent,REQUEST_CAMERA);
            }
        });

        //Speech to Text activity start here
        speech.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent speechIntent = new Intent(NewNoteActivity.this,SpeechToTextActivity.class);
                startActivityForResult(speechIntent,REQUEST_SPEECH);
            }
        });


        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String title = etTitle.getText().toString().trim();
                String content = etContent.getText().toString().trim();
                String type = etType.getText().toString().trim();

                if(!TextUtils.isEmpty(title)&&!TextUtils.isEmpty(content)){
                    createNote(title,content,type);
                }else{
                    Snackbar.make(v,"Fill empty fields",Snackbar.LENGTH_SHORT).show();
                }
            }
        });
        putData();

    }

    private void putData(){
        if(isExist) {
            fNoteDatabase.child(noteID).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if (dataSnapshot.hasChild("title") && dataSnapshot.hasChild("content")) {
                        String title = dataSnapshot.child("title").getValue().toString();
                        String content = dataSnapshot.child("content").getValue().toString();
                        String reTime=getDate(Long.parseLong(dataSnapshot.child("timestamp").getValue().toString()));
                        String type = dataSnapshot.child("type").getValue().toString();

                        etTitle.setText(title);
                        etContent.setText(content);
                        mdate.setText(reTime);
                        etType.setText(type);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    private void createNote(String title,String content,String type){

        if(fAuth.getCurrentUser()!=null){

            if(isExist){
                //Update an existing note
                Map updateMap = new HashMap();
                updateMap.put("title",etTitle.getText().toString().trim());
                updateMap.put("content",etContent.getText().toString().trim());
                updateMap.put("timestamp",ServerValue.TIMESTAMP);
                updateMap.put("type",etType.getText().toString().trim());
                fNoteDatabase.child(noteID).updateChildren(updateMap);

                Toast.makeText(this,"Note updated",Toast.LENGTH_SHORT).show();

            }else {
                //Create a new note

                final DatabaseReference newNoteRef = fNoteDatabase.push();

                final Map noteMap = new HashMap();
                noteMap.put("title", title);
                noteMap.put("content", content);
                noteMap.put("timestamp", ServerValue.TIMESTAMP);
                noteMap.put("type",type);

                Thread mainThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        newNoteRef.setValue(noteMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                if (task.isSuccessful()) {
                                    Toast.makeText(NewNoteActivity.this, "Note added to database!", Toast.LENGTH_SHORT).show();

                                } else {
                                    Toast.makeText(NewNoteActivity.this, "ERROR" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                                }

                            }
                        });
                    }
                });
                mainThread.start();
            }

        }else{
            Toast.makeText(this,"USER IS NOT SIGN IN",Toast.LENGTH_SHORT).show();
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.new_note_menu,menu);
        mainMenu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
            case R.id.new_note_delete_btn: {
                if (!noteID.equals("no")) {
                    deleteNote();
                }
                break;
            }
        }
        return true;
    }

    private void deleteNote(){

        fNoteDatabase.child(noteID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(NewNoteActivity.this,"Note Deleted",Toast.LENGTH_SHORT).show();
                    noteID = "no";
                    finish();
                }else{
                    Log.e("NewNoteActivity",task.getException().toString());

                    Toast.makeText(NewNoteActivity.this,"ERROR"+task.getException().getMessage(),Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

    private String getDate(long time) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time);
        String date = DateFormat.format("yyyy.MM.dd ' at ' HH:mm:ss", cal).toString();
        return date;
    }

    //Deal with to Speech Intent and Text Recognition Intent
    @Override
    protected void onActivityResult(int requestCode, int resultCode,Intent data) {

        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        if(requestCode==REQUEST_CAMERA){
            //Deal with the data get from camera here
            String text =  data.getStringExtra("TextFromCamera");
            String OriginalContent = etContent.getText().toString();
            String newText = OriginalContent + "\n" + text;
            etContent.setText(newText);

        }else{
            //Deal with the data get from Speech to text here
            String text = data.getStringExtra("TextFromSpeech");
            String OriginalContent = etContent.getText().toString().toString();
            String newText = OriginalContent+"\n" + text;
            etContent.setText(newText);
        }


    }
}
