package com.example.twitterclone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.LogOutCallback;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends AppCompatActivity {

    static ArrayList<String> myTweets, myFollowers, myUsername;
    MyAdapter arrayAdapter;

    TextView textViewUsername, textViewFollowers, textViewFollowing, textViewHotThoughts;
    ListView listView;

    BottomNavigationView bottomNavigationView;

    Dialog dialog;

    EditText editTextTweet;

    public void logOut(View view) {

        ParseUser.getCurrentUser().logOutInBackground(new LogOutCallback() {
            @Override
            public void done(ParseException e) {
                if(e == null) {
                    Toast.makeText(ProfileActivity.this, "Logged out!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(ProfileActivity.this, "Cannot log out! Try again", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        });

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavigationView);

        dialog = new Dialog(this);

        listView = (ListView) findViewById(R.id.listView);

        textViewUsername = (TextView) findViewById(R.id.textViewUsername);
        textViewFollowers = (TextView) findViewById(R.id.textViewFollowers);
        textViewFollowing = (TextView) findViewById(R.id.textViewFollowing);
        textViewHotThoughts = (TextView) findViewById(R.id.textViewHotThoughts);

        myTweets = new ArrayList<String>();
        myFollowers = new ArrayList<String>();

        arrayAdapter = new MyAdapter(this, R.layout.tweet_adapter, myTweets);

        textViewUsername.setText(ParseUser.getCurrentUser().getUsername());

        ParseQuery<ParseObject> tweetQuery = ParseQuery.getQuery("Tweet");
        tweetQuery.whereEqualTo("username", ParseUser.getCurrentUser().getUsername());

        tweetQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if(e == null) {
                    textViewHotThoughts.setText("HotThoughts: " + Integer.toString(objects.size()));
                    for(ParseObject object : objects) {
                        myTweets.add(object.getString("tweet"));
                    }
                    arrayAdapter.notifyDataSetChanged();
                }
            }
        });

        listView.setAdapter(arrayAdapter);

        textViewFollowing.setText("Following: " + Integer.toString(UserListActivity.following.size()));

        ParseQuery<ParseObject> followersQuery = ParseQuery.getQuery("Follower");
        followersQuery.whereEqualTo("username", ParseUser.getCurrentUser().getUsername());

        followersQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if(e == null) {
                    if(objects.size() > 0) {
                        for (ParseObject object : objects) {
                            if(object.getString("username").equals(ParseUser.getCurrentUser().getUsername()))
                            textViewFollowers.setText("Followers: " + Integer.toString(object.getList("followers").size()));
                        }
                    }
                }
            }
        });

        bottomNavigationView.setSelectedItemId(R.id.profile);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.tweet:
                        dialog.setContentView(R.layout.tweet_dialog);

                        Button buttonTweet = (Button) dialog.findViewById(R.id.buttonTweet);
                        editTextTweet = (EditText) dialog.findViewById(R.id.editTextTweet);

                        buttonTweet.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                if(editTextTweet.getText().toString().equals("") || editTextTweet.getText().toString() == null) {
                                    Toast.makeText(ProfileActivity.this, "Is nothing on your mind?", Toast.LENGTH_SHORT).show();
                                } else {
                                    ParseObject tweet = new ParseObject("Tweet");
                                    tweet.put("username", ParseUser.getCurrentUser().getUsername());
                                    tweet.put("tweet", editTextTweet.getText().toString());
                                    tweet.saveInBackground(new SaveCallback() {
                                        @Override
                                        public void done(ParseException e) {
                                            if (e == null) {

                                            } else {
                                                Toast.makeText(ProfileActivity.this, "Share failed!", Toast.LENGTH_SHORT).show();
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                                    dialog.dismiss();
                                }
                            }
                        });

                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        dialog.show();
                        return false;
                    case R.id.users:
                        if(UserListActivity.userListActivity != null) {
                            UserListActivity.userListActivity.finish();
                        }
                        Intent intent = new Intent(getApplicationContext(), UserListActivity.class);
                        startActivity(intent);
                        finish();
                        return false;
                    case R.id.yourFeed:
                        Intent intent1 = new Intent(getApplicationContext(), TweetActivity.class);
                        startActivity(intent1);
                        finish();
                        return false;
                    default:
                        return false;
                }

            }
        });

        ParseAnalytics.trackAppOpenedInBackground(getIntent());

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if (ParseUser.getCurrentUser() != null) {
            finish();
        }

    }

}
