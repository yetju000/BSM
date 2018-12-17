package com.bsm;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.CancellationSignal;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.HashMap;

public class PasswordWindow extends AppCompatActivity {

    TextView password;
    Authorization authorization;
    Button button;

    private FingerPrintAuthHelper fingerPrintAuthHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_window);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        password = findViewById(R.id.password);
        button = findViewById(R.id.button);
        authorization = new Authorization();
        getWindow().setLayout((int) (width * .8), (int) (height * .8));
        Intent i = getIntent();
        boolean auth = i.getBooleanExtra("auth", false);
        if (auth) {
            startFingerPrintAuthHelper();
            fingerPrintAuthHelper.getPassword(new CancellationSignal(), getAuthListener(true));
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickReturnPassowrd(v);
                }
            });
        } else {
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        onClickSave(v);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    public void onClickSave(View view) throws IOException {
        Intent i = getIntent();
        String text = i.getStringExtra("note");
        byte[] bytes = text.getBytes();
        String passwordText = password.getText().toString();
        HashMap<String, byte[]> map = Authorization.encryptBytes(bytes, passwordText);
        FileOutputStream fos = openFileOutput("note.dat", Context.MODE_PRIVATE);
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(map);
        oos.close();
        Toast toast = Toast.makeText(getApplicationContext(), "fingerprint", Toast.LENGTH_LONG);
        toast.show();
        startFingerPrintAuthHelper();
        fingerPrintAuthHelper.savePassword(passwordText, new CancellationSignal(), getAuthListener(false));
        //Intent intent = new Intent(this, MainActivity.class);
        //intent.putExtra("password", password.getText().toString());
        //startActivity(intent);
    }

    public void onClickReturnPassowrd(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("password", password.getText().toString());
        startActivity(intent);
    }

    private void startFingerPrintAuthHelper() {
        fingerPrintAuthHelper = new FingerPrintAuthHelper(this);
        fingerPrintAuthHelper.init();
    }

    @NonNull
    private FingerPrintAuthHelper.Callback getAuthListener(final boolean isGetPass) {
        return new FingerPrintAuthHelper.Callback() {
            @Override
            public void onSuccess(String result) {
                if (isGetPass) {
                    Intent i = new Intent(PasswordWindow.this, MainActivity.class);
                    i.putExtra("password", result);
                    startActivity(i);
                } else {
                    Intent i = new Intent(PasswordWindow.this, MainActivity.class);
                    i.putExtra("password", result);
                    startActivity(i);
                }
            }

            @Override
            public void onFailure(String message) {
                Toast toast = Toast.makeText(getApplicationContext(), "wrong finger", Toast.LENGTH_LONG);
                toast.show();
            }

            @Override
            public void onHelp(int helpCode, String helpString) {
                Toast toast = Toast.makeText(getApplicationContext(), "wrong finger", Toast.LENGTH_LONG);
                toast.show();
            }
        };
    }

}
