package com.example.Diary2.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.Diary2.notes.Notes;
import com.example.Diary2.db.DatabaseHelper;
import com.example.Diary2.R;

public class Login extends AppCompatActivity {

    EditText password;
    Button login;
    DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getIntent();

        db = new DatabaseHelper(this);

        password = (EditText) findViewById(R.id.editpassword);
        login = (Button) findViewById(R.id.btnlogin);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String passwd = password.getText().toString();
                Boolean checkpass = db.checkPassword(passwd);

                if (checkpass == true) {
                    Toast.makeText(getApplicationContext(), "Successfully Login", Toast.LENGTH_LONG).show();

                    Intent intent = new Intent(getApplicationContext(), Notes.class);
                    startActivity(intent);

                } else {
                    Toast.makeText(getApplicationContext(), "Incorrect Password", Toast.LENGTH_LONG).show();
                }
            }
        });

    }
}
