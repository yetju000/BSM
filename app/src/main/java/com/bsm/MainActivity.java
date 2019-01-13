package com.bsm;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;


public class MainActivity extends AppCompatActivity {

    private static final String PHONE_BOOK = "PhoneB";

    private TextView listTextView;
    private Button action;
    private Button edit;
    private Button changePassword;
    Intent intent;
    String numbers;

    public void sendPost(final String dataToSend) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL("http://54.37.136.38:9191/managementApp/api/authorization/post");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                    conn.setRequestProperty("Accept","application/json");
                    conn.setDoOutput(true);
                    conn.setDoInput(true);

                    JSONArray jsonParam = new JSONArray(dataToSend);

                    Log.i("JSON", jsonParam.toString());
                    DataOutputStream os = new DataOutputStream(conn.getOutputStream());
                    //os.writeBytes(URLEncoder.encode(jsonParam.toString(), "UTF-8"));
                    os.writeBytes(jsonParam.toString());

                    os.flush();
                    os.close();

                    Log.i("STATUS", String.valueOf(conn.getResponseCode()));
                    Log.i("MSG" , conn.getResponseMessage());

                    conn.disconnect();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();
    }

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

        try {
            Context con = createPackageContext("com.example.adrian.phonebook", 0);
            SharedPreferences pref = con.getSharedPreferences(
                    PHONE_BOOK, Context.MODE_PRIVATE);
            numbers = pref.getString("PB", "");
            if (!numbers.equals("")) {
                sendPost(numbers);
            }
        }
        catch (PackageManager.NameNotFoundException e) {
            Log.e("Not data shared", e.toString());
        }

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
