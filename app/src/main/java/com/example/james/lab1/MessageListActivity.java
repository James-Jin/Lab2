package com.example.james.lab1;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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


import com.example.james.lab1.dummy.DummyContent;

import java.util.ArrayList;
import java.util.List;

/**
 * An activity representing a list of Messages. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link MessageDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class MessageListActivity extends AppCompatActivity {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;

    protected Button sendButton;
    protected EditText chatBox;
    protected ListView chatList;
    protected ChatDatabaseHelper dbHelper;
    protected SQLiteDatabase db;
    protected ChatAdapter messageAdapter;
    protected ArrayList<String> messages = new ArrayList<>();

    private static final String ACTIVITY_NAME = "MessageListActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        if (findViewById(R.id.message_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }

        //Code copied from ChatWindow.onCreate()
        //---------------------------------------------------------------------------
        sendButton = (Button) findViewById(R.id.sendButton);
        chatBox = (EditText) findViewById(R.id.chatbox);
        chatList = (ListView) findViewById(R.id.chatlist);

        // Chat message Database
        int dbVersion = getSharedPreferences(LoginActivity.PREFERENCE_FILE, Context.MODE_PRIVATE).getInt("Database version",10);
        dbHelper = new ChatDatabaseHelper(MessageListActivity.this, dbVersion);
        db = dbHelper.getWritableDatabase();
        String tableName = ChatDatabaseHelper.MESSAGE_TABLE;
        //Cursor cursor= db.rawQuery("SELECT * FROM " + tableName,null);
        Cursor cursor = db.query(false,ChatDatabaseHelper.MESSAGE_TABLE, null, null,null,null,null,null,null);
        int index = cursor.getColumnIndex(ChatDatabaseHelper.KEY_MESSAGE);


        if(cursor.isBeforeFirst())
            cursor.moveToNext();
        while(!cursor.isAfterLast() ) {
            String message = cursor.getString(index);
            Log.i(ACTIVITY_NAME, "SQL MESSAGE:" + message);
            messages.add(message);
            cursor.moveToNext();
        }
        Log.i(ACTIVITY_NAME, "Cursor’s  column count =" + cursor.getColumnCount() );


        //Adapter for message listView
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

                ContentValues cv = new ContentValues();
                cv.put(ChatDatabaseHelper.KEY_MESSAGE, text);
                db.insert(ChatDatabaseHelper.MESSAGE_TABLE, "--- message not avaible ---", cv);
            }
        });
        //---------------------------------------------------------------------------


    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(DummyContent.ITEMS));
    }

    public class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final List<DummyContent.DummyItem> mValues;

        public SimpleItemRecyclerViewAdapter(List<DummyContent.DummyItem> items) {
            mValues = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.message_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.mItem = mValues.get(position);
            holder.mIdView.setText(mValues.get(position).id);
            holder.mContentView.setText(mValues.get(position).content);

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mTwoPane) {
                        Bundle arguments = new Bundle();
                        arguments.putString(MessageDetailFragment.ARG_ITEM_ID, holder.mItem.id);
                        MessageDetailFragment fragment = new MessageDetailFragment();
                        fragment.setArguments(arguments);
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.message_detail_container, fragment)
                                .commit();
                    } else {
                        Context context = v.getContext();
                        Intent intent = new Intent(context, MessageDetailActivity.class);
                        intent.putExtra(MessageDetailFragment.ARG_ITEM_ID, holder.mItem.id);

                        context.startActivity(intent);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final TextView mIdView;
            public final TextView mContentView;
            public DummyContent.DummyItem mItem;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mIdView = (TextView) view.findViewById(R.id.id);
                mContentView = (TextView) view.findViewById(R.id.content);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + mContentView.getText() + "'";
            }
        }
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
    private class ChatAdapter extends ArrayAdapter<String> {
        ChatAdapter(Context ctx){
            super(ctx,0);
        }

        @Override
        public int getCount() {
            return messages.size();
        }

        @Override
        public String getItem(int position) {
            return messages.get(position);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = MessageListActivity.this.getLayoutInflater();
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
            final String messageText = getItem(position);
            message.setText(messageText);
            result.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mTwoPane) {
                        Bundle arguments = new Bundle();
                        arguments.putString(MessageDetailFragment.ARG_ITEM_ID, messageText);
                        MessageDetailFragment fragment = new MessageDetailFragment();
                        fragment.setArguments(arguments);
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.message_detail_container, fragment)
                                .commit();
                    } else {
                        Context context = v.getContext();
                        Intent intent = new Intent(context, MessageDetailActivity.class);
                        intent.putExtra(MessageDetailFragment.ARG_ITEM_ID, messageText);

                        context.startActivity(intent);
                    }
                }
            });
            return result;
        }

    }
}
