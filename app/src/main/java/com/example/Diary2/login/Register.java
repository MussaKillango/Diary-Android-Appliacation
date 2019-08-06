package com.example.Diary2.login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.Diary2.R;
import com.example.Diary2.db.DatabaseHelper;

public class Register extends AppCompatActivity {

    public static final String MY_PREFS_NAME = "reg_preferences";
    EditText password, confirm;
    Button register;
    DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        getIntent();

        db = new DatabaseHelper(this);
        password = (EditText) findViewById(R.id.txtpasword);
        confirm = (EditText) findViewById(R.id.txtconfirm);
        register = (Button) findViewById(R.id.btnregister);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String strPassword = password.getText().toString();
                String strConfirm = confirm.getText().toString();

                userRegister(strPassword, strConfirm);

            }


        });
    }

    public boolean userRegister (String password, String confirm) {
        if (!password.equals(confirm))
            Toast.makeText(getApplicationContext(), "Password do not match", Toast.LENGTH_LONG).show();


        if (password.equals("") || confirm.equals(""))
            Toast.makeText(getApplicationContext(), "Please Enter Password", Toast.LENGTH_LONG).show();
        else {
            if (password.equals(confirm)) {
                Boolean insert = db.insert(password);
                Toast.makeText(getApplicationContext(), "Registered Successfully", Toast.LENGTH_LONG).show();

                SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                editor.putBoolean("userRegistered", true);
                editor.apply();

                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);

            }
        }

        return true;
    }
}
