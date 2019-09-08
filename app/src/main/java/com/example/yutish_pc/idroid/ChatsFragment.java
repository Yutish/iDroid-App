package com.example.yutish_pc.idroid;

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
public class ChatsFragment extends Fragment {


    TextToSpeech tospeech;
    int result;

    private RecyclerView myChatsList;
    private View myMainView;

    private DatabaseReference friendsReference;
    private DatabaseReference UsersReference;
    private FirebaseAuth mAuth;

    String online_user_id;

    public ChatsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        myMainView = inflater.inflate(R.layout.fragment_chats, container, false);

        myChatsList = (RecyclerView) myMainView.findViewById(R.id.chats_list);


        mAuth = FirebaseAuth.getInstance();
        online_user_id = mAuth.getCurrentUser().getUid();

        friendsReference = FirebaseDatabase.getInstance().getReference().child("Friends").child(online_user_id);
        friendsReference.keepSynced(true);
        UsersReference = FirebaseDatabase.getInstance().getReference().child("Users");
        UsersReference.keepSynced(true);

        myChatsList.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);

        myChatsList.setLayoutManager(linearLayoutManager);

        // Inflate the layout for this fragment
        return myMainView;


    }


    @Override
    public void onStart() {

        super.onStart();

        FirebaseRecyclerAdapter<Chats, ChatsFragment.ChatsViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<Chats, ChatsViewHolder>(
                        Chats.class, R.layout.all_user_display_layout,
                        ChatsFragment.ChatsViewHolder.class, friendsReference
                ) {
                    @Override
                    protected void populateViewHolder(final ChatsFragment.ChatsViewHolder viewHolder, Chats model, int position) {

                        final String list_user_id = getRef(position).getKey();
                        UsersReference.child(list_user_id).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                final String userName = dataSnapshot.child("user_name").getValue().toString();

                                viewHolder.setUsersName(userName);


                                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {


                                        Intent chatIntent = new Intent(ChatsFragment.this.getActivity(), ChatActivity.class);
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

        myChatsList.setAdapter(firebaseRecyclerAdapter);

        tospeech = new TextToSpeech(ChatsFragment.this.getContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    result = tospeech.setLanguage(Locale.getDefault());
                } else {
                    Toast.makeText(ChatsFragment.this.getContext(), "Feature not supported in your device", Toast.LENGTH_LONG).show();
                }
                tospeech.speak("Chats List", TextToSpeech.QUEUE_FLUSH, null);
            }
        });

    }


    public static class ChatsViewHolder extends RecyclerView.ViewHolder {


        View mView;

        public ChatsViewHolder(View itemView) {
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
