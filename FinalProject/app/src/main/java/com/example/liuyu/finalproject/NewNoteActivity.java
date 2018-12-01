package com.example.liuyu.finalproject;

import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

import java.util.HashMap;
import java.util.Map;


public class NewNoteActivity extends AppCompatActivity {

    private Button btnCreate;
    private EditText etTitle,etContent;
    private Toolbar mToolbar;

    private FirebaseAuth fAuth;
    private DatabaseReference fNoteDatabase;

    private Menu mainMenu;
    private String noteID = "no";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_note);

        try{
            noteID = getIntent().getStringExtra("noteId");

            if(noteID.equals("no")){
                mainMenu.getItem(0).setVisible(false);
            }

        }catch (Exception e){
            e.printStackTrace();
        }

        btnCreate = findViewById(R.id.new_note_btn);
        etTitle = findViewById(R.id.new_note_title);
        etContent = findViewById(R.id.new_note_content);

        mToolbar = findViewById(R.id.toolbar2);

        setSupportActionBar(mToolbar);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        fAuth = FirebaseAuth.getInstance();
        fNoteDatabase = FirebaseDatabase.getInstance().getReference().child("Notes")
                .child(fAuth.getCurrentUser().getUid());

        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String title = etTitle.getText().toString().trim();
                String content = etContent.getText().toString().trim();

                if(!TextUtils.isEmpty(title)&&!TextUtils.isEmpty(content)){
                    createNote(title,content);
                }else{
                    Snackbar.make(v,"Fill empty fields",Snackbar.LENGTH_SHORT).show();
                }

            }
        });

    }

    private  void createNote(String title,String content){

        if(fAuth.getCurrentUser()!=null){

            final DatabaseReference newNoteRef = fNoteDatabase.push();

            final Map noteMap = new HashMap();
            noteMap.put("title",title);
            noteMap.put("content",content);
            noteMap.put("timestamp", ServerValue.TIMESTAMP);

            Thread mainThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    newNoteRef.setValue(noteMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if(task.isSuccessful()){
                                Toast.makeText(NewNoteActivity.this,"Note added to database!",Toast.LENGTH_SHORT).show();

                            }else {
                                Toast.makeText(NewNoteActivity.this,"ERROR"+task.getException().getMessage(),Toast.LENGTH_SHORT).show();

                            }

                        }
                    });
                }
            });
            mainThread.start();

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
}
