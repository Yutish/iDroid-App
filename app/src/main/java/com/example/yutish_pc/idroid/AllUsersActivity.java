package com.example.yutish_pc.idroid;
//
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Locale;

public class AllUsersActivity extends AppCompatActivity {

    TextToSpeech tospeech;//text to speech here
    int result;

    private RecyclerView allUsersList;
    private DatabaseReference allDatabaseUserReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_users);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("All Users");
        toolbar.setTitleTextColor(Color.WHITE);

        tospeech = new TextToSpeech(AllUsersActivity.this, new TextToSpeech.OnInitListener() {

            @Override
            public void onInit(int status) {                    // speaking out the name to user
                if (status == TextToSpeech.SUCCESS) {
                    result = tospeech.setLanguage(Locale.getDefault());
                } else {
                    Toast.makeText(getApplicationContext(), "Feature not supported in your device", Toast.LENGTH_LONG).show();
                }
                tospeech.speak("All Users List", TextToSpeech.QUEUE_FLUSH, null);
            }
        });

        allUsersList = (RecyclerView) findViewById(R.id.all_users_list);                            //list setup
        allUsersList.setHasFixedSize(true);
        allUsersList.setLayoutManager(new LinearLayoutManager(this));

        allDatabaseUserReference = FirebaseDatabase.getInstance().getReference().child("Users");
        allDatabaseUserReference.keepSynced(true);

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<AllUsers, AllUsersViewHolder> firebaseRecyclerAdapter =              //linking with the firebase to extract data to the list
                new FirebaseRecyclerAdapter<AllUsers, AllUsersViewHolder>(
                        AllUsers.class, R.layout.all_user_display_layout,
                        AllUsersViewHolder.class, allDatabaseUserReference
                ) {
                    @Override
                    protected void populateViewHolder(AllUsersViewHolder viewHolder, AllUsers model, final int position) {
                        viewHolder.setUser_name(model.getUser_name());

                        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                String visit_user_id = getRef(position).getKey();

                                Intent profileIntent = new Intent(AllUsersActivity.this, ProfileActivity.class);
                                profileIntent.putExtra("visit_user_id", visit_user_id);
                                startActivity(profileIntent);
                            }
                        });

                    }
                };
        allUsersList.setAdapter(firebaseRecyclerAdapter);
    }


    public static class AllUsersViewHolder extends RecyclerView.ViewHolder {                         //showing item in the recycler view

        View mView;

        public AllUsersViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setUser_name(String user_name) {
            TextView name = (TextView) mView.findViewById(R.id.all_users_username);
            name.setText(user_name);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (tospeech != null) {
            tospeech.stop();
            tospeech.shutdown();
        }
    }
}


















