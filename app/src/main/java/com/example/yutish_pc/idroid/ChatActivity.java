package com.example.yutish_pc.idroid;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {                                               //inc


    TextToSpeech tospeech;
    int result;

    private String messageReceiverName;
    private String messageReceiverId;

    private ImageButton SendMessageButton;
    private EditText InputMessageText;

    private DatabaseReference rootRef;

    private FirebaseAuth mAuth;
    private String messageSenderId;

    private RecyclerView userMessagesList;
    private final List<Messages> messagesList = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    private MessageAdapter messageAdapter;
    private boolean b = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        tospeech = new TextToSpeech(ChatActivity.this, new TextToSpeech.OnInitListener() {

            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    result = tospeech.setLanguage(Locale.getDefault());
                } else {
                    Toast.makeText(getApplicationContext(), "Feature not supported in your device", Toast.LENGTH_LONG).show();
                }
                tospeech.speak("Chat with " + messageReceiverName, TextToSpeech.QUEUE_FLUSH, null);
            }
        });

        rootRef = FirebaseDatabase.getInstance().getReference();

        mAuth = FirebaseAuth.getInstance();
        messageSenderId = mAuth.getCurrentUser().getUid();


        SendMessageButton = (ImageButton) findViewById(R.id.send_message);
        InputMessageText = (EditText) findViewById(R.id.input_message);

        messageAdapter = new MessageAdapter(messagesList);

        userMessagesList = (RecyclerView) findViewById(R.id.messages_list_of_users);


        linearLayoutManager = new LinearLayoutManager(this);

        userMessagesList.setHasFixedSize(true);
        userMessagesList.setLayoutManager(linearLayoutManager);
        userMessagesList.setAdapter(messageAdapter);

        SendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SendMessagefun();
            }
        });

        FetchMessages();

        messageReceiverName = getIntent().getExtras().get("user_name").toString();
        messageReceiverId = getIntent().getExtras().get("visit_user_id").toString();

        Toolbar chatToolbar = (Toolbar) findViewById(R.id.chat_bar_layout);
        chatToolbar.setTitle(messageReceiverName);
        setSupportActionBar(chatToolbar);

        userMessagesList.addOnItemTouchListener(
                new RecyclerItemClickListener(ChatActivity.this, userMessagesList, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {

                        speechfun(userMessagesList);
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {
                        String messageText = InputMessageText.getText().toString();

                        if (TextUtils.isEmpty(messageText)) {
                            Toast.makeText(ChatActivity.this, "Please enter something", Toast.LENGTH_LONG).show();
                        } else {
                            String message_sender_ref = "Messages/" + messageSenderId + "/" + messageReceiverId;

                            String message_receiver_ref = "Messages/" + messageReceiverId + "/" + messageSenderId;

                            DatabaseReference user_message_key = rootRef.child("Messages").child(messageSenderId)
                                    .child(messageReceiverId).push();

                            String message_push_id = user_message_key.getKey();

                            Map messageTextBody = new HashMap();


                            messageTextBody.put("message", messageText);
                            messageTextBody.put("seen", false);
                            messageTextBody.put("type", "text");
                            messageTextBody.put("time", ServerValue.TIMESTAMP);
                            messageTextBody.put("from", messageSenderId);


                            Map messageBodyDetails = new HashMap();

                            messageBodyDetails.put(message_sender_ref + "/" + message_push_id, messageTextBody);

                            messageBodyDetails.put(message_receiver_ref + "/" + message_push_id, messageTextBody);

                            rootRef.updateChildren(messageBodyDetails, new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                                    if (databaseError != null) {
                                        Log.d("Chat_Log", databaseError.getMessage().toString());
                                    }

                                    InputMessageText.setText("");
                                }
                            });
                        }

                        tospeech.speak("Message send to" + messageReceiverName, TextToSpeech.QUEUE_FLUSH, null);

                    }
                })
        );
    }


    private void FetchMessages() {


        messageReceiverId = getIntent().getExtras().get("visit_user_id").toString();


        rootRef.child("Messages").child(messageSenderId).child(messageReceiverId).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                Messages messages = dataSnapshot.getValue(Messages.class);
                messagesList.add(messages);
                messageAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }


    private void SendMessagefun() {

        String messageText = InputMessageText.getText().toString();

        if (TextUtils.isEmpty(messageText)) {
            Toast.makeText(this, "Please enter something", Toast.LENGTH_LONG).show();
        } else {
            String message_sender_ref = "Messages/" + messageSenderId + "/" + messageReceiverId;

            String message_receiver_ref = "Messages/" + messageReceiverId + "/" + messageSenderId;

            DatabaseReference user_message_key = rootRef.child("Messages").child(messageSenderId)
                    .child(messageReceiverId).push();

            String message_push_id = user_message_key.getKey();

            Map messageTextBody = new HashMap();


            messageTextBody.put("message", messageText);
            messageTextBody.put("seen", false);
            messageTextBody.put("type", "text");
            messageTextBody.put("time", ServerValue.TIMESTAMP);
            messageTextBody.put("from", messageSenderId);


            Map messageBodyDetails = new HashMap();

            messageBodyDetails.put(message_sender_ref + "/" + message_push_id, messageTextBody);

            messageBodyDetails.put(message_receiver_ref + "/" + message_push_id, messageTextBody);

            rootRef.updateChildren(messageBodyDetails, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                    if (databaseError != null) {
                        Log.d("Chat_Log", databaseError.getMessage().toString());
                    }

                    InputMessageText.setText("");
                }
            });
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

    public void speechfun(View view) {

        Intent i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

        try {
            startActivityForResult(i, 10);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "Your device does not support speech input", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case 10:
                if (resultCode == RESULT_OK && data != null) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    InputMessageText.setText(result.get(0));
                }
        }
    }
}
