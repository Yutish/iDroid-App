package com.example.yutish_pc.idroid;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

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
import java.util.HashMap;


public class ProfileActivity extends AppCompatActivity {

    private DatabaseReference userReference;
    private Button sendFriendRequest;
    private Button cancelFriendRequest;
    private TextView profileName;

    private String CURRENT_STATE;
    private DatabaseReference FriendRequestReference;
    private FirebaseAuth mAuth;
    String sender_user_id;
    String receiver_user_id;

    private DatabaseReference FriendsReference;
    private DatabaseReference NotificationsReference;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        FriendRequestReference = FirebaseDatabase.getInstance().getReference().child("Friend_Requests");
        FriendRequestReference.keepSynced(true);
        mAuth = FirebaseAuth.getInstance();
        sender_user_id = mAuth.getCurrentUser().getUid();

        FriendsReference = FirebaseDatabase.getInstance().getReference().child("Friends");
        FriendsReference.keepSynced(true);

        NotificationsReference = FirebaseDatabase.getInstance().getReference().child("Notifications");
        NotificationsReference.keepSynced(true);


        final DatabaseReference userReference = FirebaseDatabase.getInstance().getReference().child("Users");

        receiver_user_id = getIntent().getExtras().get("visit_user_id").toString();

        sendFriendRequest = (Button) findViewById(R.id.profile_visit_sentreq_butt);
        cancelFriendRequest = (Button) findViewById(R.id.profile_decline_reqbutt);
        profileName = (TextView) findViewById(R.id.profile_visit_username);

        CURRENT_STATE = "not_friend";


        userReference.child(receiver_user_id).addValueEventListener(new ValueEventListener() {      //checking whether person is friend or not
            @Override
            //otherwise sending and deleting request
            public void onDataChange(DataSnapshot dataSnapshot) {                                   // friend and un-friend options...

                String name = dataSnapshot.child("user_name").getValue().toString();
                profileName.setText(name);

                FriendRequestReference.child(sender_user_id).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {


                        if (dataSnapshot.hasChild(receiver_user_id)) {
                            String req_type = dataSnapshot.child(receiver_user_id).child("request_type").getValue().toString();

                            if (req_type.equals("sent")) {
                                CURRENT_STATE = "request_sent";
                                sendFriendRequest.setText("Cancel Friend Request");

                                cancelFriendRequest.setVisibility(View.INVISIBLE);
                                cancelFriendRequest.setEnabled(false);
                            } else if (req_type.equals("received")) {
                                CURRENT_STATE = "request_received";
                                sendFriendRequest.setText("Accept Friend Request");

                                cancelFriendRequest.setVisibility(View.VISIBLE);
                                cancelFriendRequest.setEnabled(true);

                                cancelFriendRequest.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        DeclineFriendRequest();
                                    }
                                });
                            }
                        } else {
                            FriendsReference.child(sender_user_id).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    if (dataSnapshot.hasChild(receiver_user_id)) {
                                        CURRENT_STATE = "friends";
                                        sendFriendRequest.setText("Unfriend");

                                        cancelFriendRequest.setVisibility(View.INVISIBLE);
                                        cancelFriendRequest.setEnabled(false);
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        cancelFriendRequest.setVisibility(View.INVISIBLE);
        cancelFriendRequest.setEnabled(false);

        if (!sender_user_id.equals(receiver_user_id)) {
            sendFriendRequest.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    sendFriendRequest.setEnabled(false);

                    if (CURRENT_STATE.equals("not_friend")) {
                        sendFriendRequestfun();
                    }
                    if (CURRENT_STATE.equals("request_sent")) {
                        CancelFriendRequest();
                    }
                    if (CURRENT_STATE.equals("request_received")) {
                        AcceptFriendRequest();
                    }
                    if (CURRENT_STATE.equals("friends")) {
                        UnfriendAFriend();
                    }

                }
            });
        } else {
            cancelFriendRequest.setVisibility(View.INVISIBLE);
            sendFriendRequest.setVisibility(View.INVISIBLE);
        }

    }


    private void DeclineFriendRequest() {

        FriendRequestReference.child(sender_user_id).child(receiver_user_id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()) {
                    FriendRequestReference.child(receiver_user_id).child(sender_user_id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()) {
                                sendFriendRequest.setEnabled(true);
                                CURRENT_STATE = "not_friend";
                                sendFriendRequest.setText("Send Friend Request");

                                cancelFriendRequest.setVisibility(View.INVISIBLE);
                                cancelFriendRequest.setEnabled(false);
                            }

                        }
                    });
                }
            }
        });
    }


    private void UnfriendAFriend() {

        FriendsReference.child(sender_user_id).child(receiver_user_id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()) {
                    FriendsReference.child(receiver_user_id).child(sender_user_id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()) {
                                sendFriendRequest.setEnabled(true);
                                CURRENT_STATE = "not_friend";
                                sendFriendRequest.setText("Send Friend Request");

                                cancelFriendRequest.setVisibility(View.INVISIBLE);
                                cancelFriendRequest.setEnabled(false);

                            }
                        }
                    });
                }
            }
        });
    }


    private void AcceptFriendRequest() {

        Calendar callForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
        final String saveCurrentDate = currentDate.format(callForDate.getTime());

        FriendsReference.child(sender_user_id).child(receiver_user_id).child("date").setValue(saveCurrentDate).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                FriendsReference.child(receiver_user_id).child(sender_user_id).child("date").setValue(saveCurrentDate).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        FriendRequestReference.child(sender_user_id).child(receiver_user_id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                if (task.isSuccessful()) {
                                    FriendRequestReference.child(receiver_user_id).child(sender_user_id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @SuppressLint("SetTextI18n")
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if (task.isSuccessful()) {
                                                sendFriendRequest.setEnabled(true);
                                                CURRENT_STATE = "friends";
                                                sendFriendRequest.setText("Unfriend");

                                                cancelFriendRequest.setVisibility(View.INVISIBLE);
                                                cancelFriendRequest.setEnabled(false);

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


    private void CancelFriendRequest() {

        FriendRequestReference.child(sender_user_id).child(receiver_user_id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()) {
                    FriendRequestReference.child(receiver_user_id).child(sender_user_id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()) {
                                sendFriendRequest.setEnabled(true);
                                CURRENT_STATE = "not_friend";
                                sendFriendRequest.setText("Send Friend Request");

                                cancelFriendRequest.setVisibility(View.INVISIBLE);
                                cancelFriendRequest.setEnabled(false);
                            }

                        }
                    });
                }
            }
        });
    }


    private void sendFriendRequestfun() {
        FriendRequestReference.child(sender_user_id).child(receiver_user_id).child("request_type").setValue("sent")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()) {
                            FriendRequestReference.child(receiver_user_id).child(sender_user_id)
                                    .child("request_type").setValue("received").addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {

                                        HashMap<String, String> notificationHashmap = new HashMap<String, String>();
                                        notificationHashmap.put("from", sender_user_id);
                                        notificationHashmap.put("type", "request");

                                        NotificationsReference.child(receiver_user_id).push().setValue(notificationHashmap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {

                                                if (task.isSuccessful()) {
                                                    sendFriendRequest.setEnabled(true);
                                                    CURRENT_STATE = "request_sent";
                                                    sendFriendRequest.setText("Cancel Friend Request");

                                                    cancelFriendRequest.setVisibility(View.INVISIBLE);
                                                    cancelFriendRequest.setEnabled(false);
                                                }
                                            }
                                        });


                                    }
                                }
                            });
                        }
                    }
                });
    }

}
