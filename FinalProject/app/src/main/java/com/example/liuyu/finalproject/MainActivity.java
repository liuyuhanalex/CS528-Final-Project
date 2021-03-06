package com.example.liuyu.finalproject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.text.format.DateFormat;
import android.text.method.HideReturnsTransformationMethod;
import android.util.Log;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

//import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity{

    private FirebaseAuth fAuth;
    private RecyclerView mNoteList;
    private GridLayoutManager gridLayoutManager;

    private DatabaseReference fNotesDatabase;
//    private ImageButton searchButton;
//    private TextView searchContent;
//    private String searchText;

    private GestureDetector gestureDetector;

    private int Hidden = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //For gestureDetector
        this.gestureDetector = new GestureDetector(this, new MyGestureListener());

//        searchButton = findViewById(R.id.main_search_btn);
//        searchContent = findViewById(R.id.main_search_content);

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

//        searchButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                searchText = searchContent.getText().toString().trim();
//                //TODO:search the content which contain certain key words
//                Query query = fNotesDatabase
//                        .orderByChild("title")
//                        .equalTo(searchText);
//
//            }
//        });
        //Using gesture detection on Recycle View
        mNoteList.setOnTouchListener(touchListener);
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
                    public void onDataChange(final DataSnapshot dataSnapshot) {

                        if(dataSnapshot.hasChild("password")&&Hidden==1){
                            viewHolder.itemView.setVisibility(View.INVISIBLE);
                        }

                        if(dataSnapshot.hasChild("title")&&dataSnapshot.hasChild("timestamp")
                                &&dataSnapshot.hasChild("type")) {

                            String title = dataSnapshot.child("title").getValue().toString();
                            String timestamp = dataSnapshot.child("timestamp").getValue().toString();
                            String type = dataSnapshot.child("type").getValue().toString();

                            viewHolder.setNotetitle(title);
                            viewHolder.setNoteTime(getDate(Long.parseLong(timestamp)));
                            viewHolder.setNoteType(type);

                            if(dataSnapshot.hasChild("password")){
                                viewHolder.lockIcon.setImageResource(R.drawable.ic_lock_black_24dp);
                            }
                            viewHolder.noteCard.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if(dataSnapshot.hasChild("password")){
                                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                                        builder.setTitle("Please enter the password");

                                        final EditText input = new EditText(MainActivity.this);

                                        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                                        builder.setView(input);


                                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                String password = input.getText().toString().trim();
                                                if(password.equals(dataSnapshot.child("password").getValue().toString())){
                                                    Intent intent = new Intent(MainActivity.this, NewNoteActivity.class);
                                                    intent.putExtra("noteId", noteId);
                                                    startActivity(intent);
                                                }else{
                                                    //enter the wrong password
                                                    Toast.makeText(MainActivity.this,"You enter the wrong password",Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.cancel();
                                            }
                                        });

                                        builder.show();

                                    }else {
                                        Intent intent = new Intent(MainActivity.this, NewNoteActivity.class);
                                        intent.putExtra("noteId", noteId);
                                        startActivity(intent);
                                    }
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

            case R.id.main_log_out:
                fAuth.signOut();
                Intent intent = new Intent(MainActivity.this,StartAvtivity.class);
                startActivity(intent);
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
        String date = DateFormat.format("yyyy.MM.dd 'at' HH:mm:ss z", cal).toString();
        return date;
    }

    //Gesture detection start here
    View.OnTouchListener touchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            return gestureDetector.onTouchEvent(event);
        }
    };

    class MyGestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onDown(MotionEvent event) {
            Log.d("TAG","onDown: ");
            return true;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            Log.i("TAG", "onSingleTapConfirmed: ");
            return true;
        }

        @Override
        public void onLongPress(MotionEvent e) {
            //Long press to log out
            Log.i("TAG", "onLongPress: ");
            Toast.makeText(getApplicationContext(),"Long press to log out directly!",Toast.LENGTH_SHORT).show();
            fAuth.signOut();
            Intent intent = new Intent(MainActivity.this,StartAvtivity.class);
            startActivity(intent);
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            Log.i("TAG", "onDoubleTap: ");
            if(Hidden==1){
                Hidden = 0;
            }else{
                Hidden = 1;
            }
            onStart();
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2,
                                float distanceX, float distanceY) {
            Log.i("TAG", "onScroll: ");
            return true;
        }

        @Override
        public boolean onFling(MotionEvent event1, MotionEvent event2,
                               float velocityX, float velocityY) {
            Log.d("TAG", "onFling: ");
            return true;
        }
    }
}
