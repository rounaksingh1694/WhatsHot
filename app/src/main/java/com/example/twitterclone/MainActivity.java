package com.example.twitterclone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.LogOutCallback;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    EditText editTextUsername, editTextPassword;
    Button button;
    TextView textView, textViewHello;
    ConstraintLayout constraintLayout;

    static Activity mainActivity;

    public void goToTweetActivity(boolean isUserLoggedIn) {
        Intent intent = new Intent(getApplicationContext(), TweetActivity.class);
        intent.putExtra("Is User Logged In", isUserLoggedIn);
        startActivity(intent);
        finish();
    }

    public void signUpOrLogIn(View view) {

        String username = "", password = "";
        username = editTextUsername.getText().toString();
        password = editTextPassword.getText().toString();

        final ParseUser user = new ParseUser();

        if (username == null || password == null || username.equals("") || password.equals("")) {
            Toast.makeText(this, "A username and a password are required", Toast.LENGTH_SHORT).show();
        } else {
            if (button.getText().toString().equalsIgnoreCase("Sign Up")) {
                user.setUsername(username);
                user.setPassword(password);
                final String finalUsername = username;
                user.signUpInBackground(new SignUpCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            Toast.makeText(MainActivity.this, "Sign Up Successful!", Toast.LENGTH_SHORT).show();
                            user.put("following", new ArrayList<String>());
                            user.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if (e == null) {
                                        Log.i("ArrayList", "Added");
                                    } else {
                                        Log.i("ArrayList", "Not Added");
                                        e.printStackTrace();
                                    }
                                }
                            });
                            ParseObject follower = new ParseObject("Follower");
                            follower.put("username", finalUsername);
                            follower.put("followers", new ArrayList<String>());
                            follower.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if(e == null) {
                                        Log.i("FollowerArrayList", "Added");
                                    } else {
                                        Log.i("FollowerArrayList", "Not Added");
                                        e.printStackTrace();
                                    }
                                }
                            });
                            goToTweetActivity(false);
                        } else {
                            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    }
                });
            } else if (button.getText().toString().equalsIgnoreCase("Log In")) {
                user.logInInBackground(username, password, new LogInCallback() {
                    @Override
                    public void done(ParseUser user, ParseException e) {
                        if (e == null && user != null) {
                            Toast.makeText(MainActivity.this, "Log In Successful!", Toast.LENGTH_SHORT).show();
                            goToTweetActivity(false);
                        } else {
                            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    }
                });
            }
        }

    }

    public void changeSignUpOrLogIn(View view) {

        if (textView.getText().toString().equalsIgnoreCase("Already a user? Log In")) {
            textView.setText("Don't have an account? Sign Up");
            button.setText("Log In");
        } else {
            textView.setText("Already a user? Log In");
            button.setText("Sign Up");
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextUsername = (EditText) findViewById(R.id.editTextUsername);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);

        textView = (TextView) findViewById(R.id.textView);
        textViewHello = (TextView) findViewById(R.id.textViewHello);

        button = (Button) findViewById(R.id.button);

        constraintLayout = (ConstraintLayout) findViewById(R.id.constraintLayout);

        mainActivity = MainActivity.this;

        if (ParseUser.getCurrentUser() != null) {
            goToTweetActivity(true);
        }

        editTextPassword.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
                    signUpOrLogIn(button);
                }

                return false;
            }
        });

        constraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            }
        });

        textViewHello.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            }
        });

        if (ParseUser.getCurrentUser() != null) {
            goToTweetActivity(true);
        }

        ParseAnalytics.trackAppOpenedInBackground(getIntent());

    }

}
