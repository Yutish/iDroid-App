package com.example.yutish_pc.idroid;

import android.graphics.Color;
import android.speech.tts.TextToSpeech;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.util.List;
import java.util.Locale;

/**
 * Created by Yutish-pc on 18-02-2018.
 */

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {
    String ss = null;
    private List<Messages> userMessageList;
    TextToSpeech tospeech;
    int result;

    private FirebaseAuth mAuth;

    public MessageAdapter(List<Messages> userMessageList) {
        this.userMessageList = userMessageList;
    }

    @Override
    public MessageViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.messages_layout_of_user, parent, false);

        tospeech = new TextToSpeech(parent.getContext(), new TextToSpeech.OnInitListener() {

            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    result = tospeech.setLanguage(Locale.getDefault());
                } else {
                    Toast.makeText(parent.getContext(), "Feature not supported in your device", Toast.LENGTH_LONG).show();
                }
            }
        });

        mAuth = FirebaseAuth.getInstance();

        return new MessageViewHolder(v);
    }


    @Override
    public void onBindViewHolder(MessageViewHolder holder, int position) {                          //sending and receiving the messages

        String message_sender_id = mAuth.getCurrentUser().getUid();

        Messages messages = userMessageList.get(position);

        String fromUserId = messages.getFrom();

        holder.messageText.setText(messages.getMessage());

        if (fromUserId.equals(message_sender_id)) {

            holder.messageText.setBackgroundResource(R.drawable.message_text_background_two);
            holder.messageText.setTextColor(Color.BLACK);
            holder.messageText.setGravity(Gravity.RIGHT);
        } else {

            holder.messageText.setBackgroundResource(R.drawable.message_text_background);
            holder.messageText.setTextColor(Color.BLACK);
            holder.messageText.setGravity(Gravity.LEFT);
            if (ss != messages.getMessage()) {
                tospeech.speak("Message Recived " + messages.getMessage(), TextToSpeech.QUEUE_FLUSH, null);
                ss = messages.getMessage();
            }
        }
    }


    @Override
    public int getItemCount() {

        return userMessageList.size();
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder {

        public TextView messageText;

        public MessageViewHolder(View view) {
            super(view);

            messageText = (TextView) view.findViewById(R.id.message_text);

        }
    }


}
