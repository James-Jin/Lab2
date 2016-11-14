package com.example.james.lab1;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class TestToolbar extends AppCompatActivity {
    protected String messageInSnack = "You selected item 1";
    protected View inflatedDialogView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_toolbar);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "This is my own message!!!", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu m){
        getMenuInflater().inflate(R.menu.toolbar_menu, m );
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem mi){
        int menuItemId = mi.getItemId();
        switch (menuItemId) {
            case R.id.action_one:
                Snackbar.make(findViewById(R.id.toolbar), messageInSnack, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                break;
            case R.id.action_two:
                AlertDialog.Builder builder = new AlertDialog.Builder(TestToolbar.this);
                builder.setTitle(R.string.go_back_alert_title);
                builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        TestToolbar.this.finish();
                    }
                });
                builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();

                break;
            case R.id.action_three:

                LayoutInflater inflater = TestToolbar.this.getLayoutInflater();
                inflatedDialogView=  inflater.inflate(R.layout.custom_dialog, null);
                new AlertDialog.Builder(TestToolbar.this)
                    .setView(inflatedDialogView)
                    // Add action buttons
                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            EditText newMessage = (EditText)inflatedDialogView.findViewById(R.id.new_message);
                            messageInSnack = newMessage.getText().toString();
                        }
                    })
                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    })
                    .create().show();
                break;
            case R.id.action_about:
                Toast.makeText(this, "Version 1.0, by Mochen Jin", Toast.LENGTH_LONG)
                    .show();
                break;
            default:
                //
                break;
        }
        //?
        return super.onOptionsItemSelected(mi);
    }



}
