package com.example.yutish_pc.idroid;
//
import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 */
public class FriendsFragment extends Fragment {

    TextToSpeech tospeech;//text to speech here
    int result;

    private RecyclerView myfriendlist;
    private DatabaseReference friendsReference;
    private DatabaseReference UsersReference;
    private FirebaseAuth mAuth;

    String online_user_id;

    private View myMainView;


    public FriendsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        myMainView = inflater.inflate(R.layout.fragment_friends, container, false);


        myfriendlist = (RecyclerView) myMainView.findViewById(R.id.friends_list);

        mAuth = FirebaseAuth.getInstance();
        online_user_id = mAuth.getCurrentUser().getUid();

        friendsReference = FirebaseDatabase.getInstance().getReference().child("Friends").child(online_user_id);
        friendsReference.keepSynced(true);
        UsersReference = FirebaseDatabase.getInstance().getReference().child("Users");
        UsersReference.keepSynced(true);
        myfriendlist.setLayoutManager(new LinearLayoutManager(getContext()));


        return myMainView;

    }

    @Override
    public void onStart() {
        super.onStart();


        FirebaseRecyclerAdapter<Friends, FriendsViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<Friends, FriendsViewHolder>(
                        Friends.class, R.layout.all_user_display_layout,
                        FriendsViewHolder.class, friendsReference
                ) {
                    @Override
                    protected void populateViewHolder(final FriendsViewHolder viewHolder, Friends model, int position) {

                        final String list_user_id = getRef(position).getKey();
                        UsersReference.child(list_user_id).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                final String userName = dataSnapshot.child("user_name").getValue().toString();

                                viewHolder.setUsersName(userName);

                                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {


                                        Intent chatIntent = new Intent(FriendsFragment.this.getActivity(), ChatActivity.class);
                                        chatIntent.putExtra("visit_user_id", list_user_id);
                                        chatIntent.putExtra("user_name", userName);
                                        startActivity(chatIntent);
                                    }
                                });

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                    }
                };

        myfriendlist.setAdapter(firebaseRecyclerAdapter);


        tospeech = new TextToSpeech(FriendsFragment.this.getContext(), new TextToSpeech.OnInitListener() {

            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    result = tospeech.setLanguage(Locale.getDefault());
                } else {
                    Toast.makeText(FriendsFragment.this.getContext(), "Feature not supported in your device", Toast.LENGTH_LONG).show();
                }
                tospeech.speak("Friends List", TextToSpeech.QUEUE_FLUSH, null);
            }
        });
    }


    public static class FriendsViewHolder extends RecyclerView.ViewHolder {


        View mView;

        public FriendsViewHolder(View itemView) {
            super(itemView);

            mView = itemView;
        }

        public void setUsersName(String usersName) {

            TextView usersNameDisplay = (TextView) mView.findViewById(R.id.all_users_username);
            usersNameDisplay.setText(usersName);
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        if (tospeech != null) {
            tospeech.stop();
        }
    }

}
