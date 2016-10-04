package com.example.james.lab1;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class ChatWindow extends AppCompatActivity {
    private static final String TAG = "jamesdebug";

    protected Button sendButton;
    protected EditText chatBox;
    protected ListView chatList;
    protected ChatAdapter messageAdapter;
    protected ArrayList<String> messages = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState == null)
        Log.i(TAG,"it is null" );
        setContentView(R.layout.activity_chat_window);

        sendButton = (Button) findViewById(R.id.sendButton);
        chatBox = (EditText) findViewById(R.id.chatbox);
        chatList = (ListView) findViewById(R.id.chatlist);

        messageAdapter = new ChatAdapter(this);
        chatList.setAdapter(messageAdapter);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = chatBox.getText().toString();
                chatBox.setText("");
                messages.add(text);
                messageAdapter.notifyDataSetChanged();
                scrollMyListViewToBottom();
            }
        });

    }


    //scroll the list to bottom
    //not required by Lab 4
    private void scrollMyListViewToBottom() {
        chatList.post(new Runnable() {
            @Override
            public void run() {
                // Select the last row so it will scroll into view...
                chatList.setSelection(messageAdapter.getCount() - 1);
            }
        });
    }

    private class ChatAdapter extends ArrayAdapter<String>{
        ChatAdapter(Context ctx){
            super(ctx,0);
        }

        @Override
        public int getCount() {
            return messages.size();
        }

        @Nullable
        @Override
        public String getItem(int position) {
            return messages.get(position);
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = ChatWindow.this.getLayoutInflater();
            View result = null ;
            int image_id=0;
            if(position%2 == 0) {
                result = inflater.inflate(R.layout.chat_row_incoming, null);
                image_id = R.drawable.hillary;
            }

            else {
                result = inflater.inflate(R.layout.chat_row_outgoing, null);
                image_id = R.drawable.trump;
            }

            ImageView imageView1 = (ImageView) result.findViewById(R.id.text_image);


            Bitmap bm = BitmapFactory.decodeResource(getResources(),image_id);
            Drawable roundedImage = new RoundImage(bm);
            imageView1.setImageDrawable(roundedImage);

            TextView message = (TextView) result.findViewById(R.id.message_text);
            message.setText(getItem(position));
            return result;
        }

    }
}
