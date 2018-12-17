package com.bsm;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class AddNoteActivity extends AppCompatActivity {

    TextView textView ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);
        textView = findViewById(R.id.note);
    }

    public void onClickSave(View view){
        Intent i = new Intent(AddNoteActivity.this, PasswordWindow.class);
        i.putExtra("note", textView.getText().toString());
        startActivity(i);
    }
}
