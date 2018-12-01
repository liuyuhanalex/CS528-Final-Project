package com.example.liuyu.finalproject;

import android.content.Intent;
import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

//import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth fAuth;
    private RecyclerView mNoteList;
    private GridLayoutManager gridLayoutManager;

    private DatabaseReference fNotesDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNoteList = findViewById(R.id.main_notes_list);

        gridLayoutManager = new GridLayoutManager(this,3, LinearLayoutManager.VERTICAL,false);

        mNoteList.setHasFixedSize(true);
        mNoteList.setLayoutManager(gridLayoutManager);


        fAuth = FirebaseAuth.getInstance();

        if(fAuth.getCurrentUser()!=null){

            fNotesDatabase = FirebaseDatabase.getInstance().getReference().child("Notes")
                    .child(fAuth.getCurrentUser().getUid());

        }else{

        }

        updateUI();
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<NoteModel,NoteViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<NoteModel,NoteViewHolder>(

                NoteModel.class,
                R.layout.single_note_layout,
                NoteViewHolder.class,
                fNotesDatabase

        ) {

            @Override
            protected void populateViewHolder(final NoteViewHolder viewHolder, NoteModel model, int position) {

                final String noteId = getRef(position).getKey();

                fNotesDatabase.child(noteId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if(dataSnapshot.hasChild("title")&&dataSnapshot.hasChild("timestamp")
                                &&dataSnapshot.hasChild("type")) {

                            String title = dataSnapshot.child("title").getValue().toString();
                            String timestamp = dataSnapshot.child("timestamp").getValue().toString();
                            String type = dataSnapshot.child("type").getValue().toString();

                            viewHolder.setNotetitle(title);
                            viewHolder.setNoteTime(getDate(Long.parseLong(timestamp)));
                            viewHolder.setNoteType(type);
                            viewHolder.noteCard.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(MainActivity.this, NewNoteActivity.class);
                                    intent.putExtra("noteId", noteId);
                                    startActivity(intent);
                                }
                            });
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        };

        mNoteList.setAdapter(firebaseRecyclerAdapter);
    }

    private void updateUI(){
        if(fAuth.getCurrentUser()!=null){
            Log.i("MainActivity","fAuth!=null");

        }else{
            Intent startIntent = new Intent(MainActivity.this,StartAvtivity.class);
            startActivity(startIntent);
            finish();
            Log.i("MainActivity","fAuth==null");

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()){
            case R.id.main_new_note_btn:
                Intent newIntent = new Intent(MainActivity.this,NewNoteActivity.class);
                startActivity(newIntent);
                break;
        }
        return true;
    }

    private int dpToPx(int dp){
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,dp,r.getDisplayMetrics()));
    }

    //Transfer timestamp into actual Date

    private String getDate(long time) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time);
        String date = DateFormat.format("yyyy.MM.dd'at' HH:mm:ss z", cal).toString();
        return date;
    }
}
