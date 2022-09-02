package com.example.yutish_pc.idroid;
//
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 */
public class RequestsFragment extends Fragment {


    TextToSpeech tospeech;//text to speech here
    int result;


    private RecyclerView myRequestList;
    private View myMainView;
    private DatabaseReference FriendsRequestsReference;
    private FirebaseAuth mAuth;

    String online_user_id;

    private DatabaseReference usersReference;

    private DatabaseReference FriendsDatabaseRef;
    private DatabaseReference FriendsReqDatabaseRef;

    public RequestsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        myMainView = inflater.inflate(R.layout.fragment_requests, container, false);

        myRequestList = (RecyclerView) myMainView.findViewById(R.id.requests_list);

        myRequestList.setHasFixedSize(true);

        mAuth = FirebaseAuth.getInstance();
        online_user_id = mAuth.getCurrentUser().getUid();

        FriendsRequestsReference = FirebaseDatabase.getInstance().getReference().child("Friend_Requests").child(online_user_id);
        usersReference = FirebaseDatabase.getInstance().getReference().child("Users");

        FriendsDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Friends");
        FriendsReqDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Friend_Requests");

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);

        myRequestList.setLayoutManager(linearLayoutManager);

        // Inflate the layout for this fragment
        return myMainView;
    }


    @Override
    public void onStart() {
        super.onStart();


        final FirebaseRecyclerAdapter<Requests, RequestViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Requests, RequestViewHolder>
                (
                        Requests.class, R.layout.friend_request_all_user_layout,
                        RequestsFragment.RequestViewHolder.class, FriendsRequestsReference
                ) {
            @Override
            protected void populateViewHolder(final RequestViewHolder viewHolder, Requests model, int position) {

                final String list_user_id = getRef(position).getKey();

                DatabaseReference get_type_ref = getRef(position).child("request_type").getRef();
                get_type_ref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if (dataSnapshot.exists()) {
                            String request_type = dataSnapshot.getValue().toString();

                            if (request_type.equals("received")) {
                                usersReference.child(list_user_id).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {

                                        final String userName = dataSnapshot.child("user_name").getValue().toString();

                                        viewHolder.setUsersName(userName);

                                        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {

                                                CharSequence options[] = new CharSequence[]
                                                        {
                                                                "Accept Friend Request", "Decline Friend Request"
                                                        };
                                                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                                builder.setTitle("Friend Request Options");

                                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int position) {

                                                        if (position == 0) {
                                                            Calendar callForDate = Calendar.getInstance();
                                                            SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
                                                            final String saveCurrentDate = currentDate.format(callForDate.getTime());

                                                            FriendsDatabaseRef.child(online_user_id).child(list_user_id).child("date").setValue(saveCurrentDate).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {
                                                                    FriendsDatabaseRef.child(list_user_id).child(online_user_id).child("date").setValue(saveCurrentDate).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                        @Override
                                                                        public void onSuccess(Void aVoid) {

                                                                            FriendsReqDatabaseRef.child(online_user_id).child(list_user_id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<Void> task) {

                                                                                    if (task.isSuccessful()) {
                                                                                        FriendsReqDatabaseRef.child(list_user_id).child(online_user_id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                            @SuppressLint("SetTextI18n")
                                                                                            @Override
                                                                                            public void onComplete(@NonNull Task<Void> task) {

                                                                                                if (task.isSuccessful()) {
                                                                                                    Toast.makeText(getContext(), "Friend Request Accepted Successfully", Toast.LENGTH_LONG).show();
                                                                                                }

                                                                                            }
                                                                                        });
                                                                                    }
                                                                                }
                                                                            });
                                                                        }
                                                                    });
                                                                }
                                                            });
                                                        }
                                                        if (position == 1) {
                                                            FriendsReqDatabaseRef.child(online_user_id).child(list_user_id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {

                                                                    if (task.isSuccessful()) {
                                                                        FriendsReqDatabaseRef.child(list_user_id).child(online_user_id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<Void> task) {

                                                                                if (task.isSuccessful()) {
                                                                                    Toast.makeText(getContext(), "Friend Request Cancelled Successfully", Toast.LENGTH_LONG).show();

                                                                                }

                                                                            }
                                                                        });
                                                                    }
                                                                }
                                                            });
                                                        }

                                                    }
                                                });
                                                builder.show();
                                            }
                                        });
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                            } else if (request_type.equals("sent")) {

                                Button req_sent_btn = (Button) viewHolder.mView.findViewById(R.id.request_accept_btn);
                                req_sent_btn.setText("Request Send");

                                viewHolder.mView.findViewById(R.id.request_decline_btn).setVisibility(View.INVISIBLE);

                                usersReference.child(list_user_id).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {

                                        final String userName = dataSnapshot.child("user_name").getValue().toString();

                                        viewHolder.setUsersName(userName);

                                        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {

                                                CharSequence options[] = new CharSequence[]
                                                        {
                                                                "Cancel Friend Request"
                                                        };
                                                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                                builder.setTitle("Friend Request Sent");

                                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int position) {


                                                        if (position == 0) {
                                                            FriendsReqDatabaseRef.child(online_user_id).child(list_user_id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {

                                                                    if (task.isSuccessful()) {
                                                                        FriendsReqDatabaseRef.child(list_user_id).child(online_user_id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<Void> task) {

                                                                                if (task.isSuccessful()) {
                                                                                    Toast.makeText(getContext(), "Friend Request Cancelled Successfully", Toast.LENGTH_LONG).show();

                                                                                }

                                                                            }
                                                                        });
                                                                    }
                                                                }
                                                            });
                                                        }

                                                    }
                                                });
                                                builder.show();

                                            }
                                        });
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


            }
        };
        myRequestList.setAdapter(firebaseRecyclerAdapter);

        tospeech = new TextToSpeech(RequestsFragment.this.getContext(), new TextToSpeech.OnInitListener() {

            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    result = tospeech.setLanguage(Locale.getDefault());
                } else {
                    Toast.makeText(RequestsFragment.this.getContext(), "Feature not supported in your device", Toast.LENGTH_LONG).show();
                }
                tospeech.speak("Requests List", TextToSpeech.QUEUE_FLUSH, null);
            }
        });


    }

    public static class RequestViewHolder extends RecyclerView.ViewHolder {
        View mView;

        public RequestViewHolder(View itemView) {
            super(itemView);

            mView = itemView;
        }

        public void setUsersName(String usersName) {

            TextView usersNameDisplay = (TextView) mView.findViewById(R.id.request_profile_name);
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
