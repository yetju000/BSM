package com.bsm;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.HashMap;

public class UpdateNote extends AppCompatActivity {

    TextView textView;
    Intent i;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_note);
        textView = findViewById(R.id.editText);
        i = getIntent();
        textView.setText(i.getStringExtra("note"));
    }

    public void onClickEdit(View view) throws IOException {
        String text = textView.getText().toString();
        String password = i.getStringExtra("password");
        byte[] bytes = text.getBytes();
        HashMap<String, byte[]> map = Authorization.encryptBytes(bytes, password);
        FileOutputStream fos = openFileOutput("note.dat", Context.MODE_PRIVATE);
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(map);
        oos.close();
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("password", password);
        startActivity(intent);
    }
}
