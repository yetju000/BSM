package com.bsm;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private TextView listTextView;
    private Button action;
    private Button edit;
    private Button changePassword;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        action = findViewById(R.id.button);
        edit = findViewById(R.id.Edit);
        changePassword = findViewById(R.id.changePassowrd);
        listTextView = findViewById(R.id.NotesList);
        intent = getIntent();
        String password = intent.getStringExtra("password");
        if (checkFileExist()) {
            if (!readNote(password)) {
                if (password != null && password != "")
                    listTextView.setText("wrong password");
                else
                    listTextView.setText("");
                edit.setVisibility(View.INVISIBLE);
                changePassword.setVisibility(View.INVISIBLE);
                action.setText("ZALOGUJ");
                action.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onClickAuthorize(v);
                    }
                });
            } else {
                edit.setVisibility(View.VISIBLE);
                changePassword.setVisibility(View.VISIBLE);
                action.setText("WYLOGUJ");
                action.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onClickLogOut(v);
                    }
                });
            }
        } else {
            action.setText("DODAJ");
            edit.setVisibility(View.INVISIBLE);
            changePassword.setVisibility(View.INVISIBLE);
            action.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickAddNote(v);
                }
            });
        }

    }

    public void onClickAddNote(View view) {
        Intent i = new Intent(this, AddNoteActivity.class);
        startActivity(i);
    }

    public void onClickAuthorize(View view) {
        Intent i = new Intent(MainActivity.this, PasswordWindow.class);
        i.putExtra("auth", true);
        startActivity(i);
    }

    public void onClickLogOut(View view) {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }

    public void onClickUpdate(View view) {
        Intent i = new Intent(this, UpdateNote.class);
        i.putExtra("note", listTextView.getText().toString());
        i.putExtra("password", intent.getStringExtra("password"));
        startActivity(i);
    }

    public void onClickChangePassword(View view) {
        Intent i = new Intent(MainActivity.this, PasswordWindow.class);
        i.putExtra("auth", false);
        i.putExtra("note", listTextView.getText().toString());
        startActivity(i);
    }

    private Boolean readNote(String password) {
        try {
            FileInputStream fos = openFileInput("note.dat");
            ObjectInputStream ois = new ObjectInputStream(fos);
            HashMap<String, byte[]> map = (HashMap<String, byte[]>) ois.readObject();
            byte[] decrypted = Authorization.decryptData(map, password);
            if (decrypted != null) {
                listTextView.setText(new String(decrypted));
                return true;
            }
        } catch (FileNotFoundException e) {
            return false;
        } catch (IOException e) {
            return false;
        } catch (ClassNotFoundException e) {
            return false;
        }
        return false;
    }

    private Boolean checkFileExist() {
        try {
            openFileInput("note.dat");
            return true;
        } catch (FileNotFoundException e) {
            return false;
        }
    }
}
