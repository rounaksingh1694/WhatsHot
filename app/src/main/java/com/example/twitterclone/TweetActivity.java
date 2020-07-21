package com.example.twitterclone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
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
import com.parse.Parse;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

public class TweetActivity extends AppCompatActivity {

    ListView listView;
    static ArrayList<String> tweet;
    static ArrayList<String> username;
    MyTweetAdapter arrayAdapter;

    BottomNavigationView bottomNavigationView;

    Dialog dialog;
    Button buttonTweet;
    EditText editTextTweet;

    TextView textViewInfo;
    Button buttonExplore;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.log_out_menu, menu);

        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);

        if (item.getItemId() == R.id.logOut) {
            ParseUser.getCurrentUser().logOutInBackground(new LogOutCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        if (UserListActivity.userListActivity != null) {
                            finish();
                            UserListActivity.userListActivity.finish();
                        }
                    } else {
                        Toast.makeText(TweetActivity.this, "Log ou failed!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            return true;
        }
        return false;

    }

    public void explore(View view) {

        if(UserListActivity.userListActivity != null) {
            UserListActivity.userListActivity.finish();
        }
        Intent intent = new Intent(getApplicationContext(), UserListActivity.class);
        startActivity(intent);
        finish();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet);

        setTitle("Your Feed");
        setTitleColor(Color.parseColor("#000000"));

        textViewInfo = (TextView) findViewById(R.id.textViewInfo);

        buttonExplore = (Button) findViewById(R.id.buttonExplore);

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavigationView);

        listView = (ListView) findViewById(R.id.listView);
        tweet = new ArrayList<String>();
        username = new ArrayList<String>();
        arrayAdapter = new MyTweetAdapter(this, R.layout.tweet_adapter, username);

        dialog = new Dialog(this);

        UserListActivity.following = new ArrayList<String>();

        textViewInfo.setVisibility(View.GONE);
        buttonExplore.setVisibility(View.GONE);

        ParseQuery<ParseUser> followingQuery = ParseUser.getQuery();
        followingQuery.whereEqualTo("username", ParseUser.getCurrentUser().getUsername());
        followingQuery.getInBackground(ParseUser.getCurrentUser().getObjectId(), new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser object, ParseException e) {
                if (e == null) {
                    if (object.get("following") != null) {
                        UserListActivity.following = (ArrayList<String>) object.get("following");
                    }
                    Log.i("FollowingListInTweet", UserListActivity.following.toString());
                    if(UserListActivity.following.size() > 0) {
                        textViewInfo.setVisibility(View.GONE);
                        buttonExplore.setVisibility(View.GONE);
                    } else {
                        textViewInfo.setVisibility(View.VISIBLE);
                        buttonExplore.setVisibility(View.VISIBLE);
                        listView.setVisibility(View.INVISIBLE);
                    }
                }
            }
        });

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Tweet");
        query.whereNotEqualTo("username", ParseUser.getCurrentUser().getUsername());
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null && objects.size() > 0) {
                    for (ParseObject object : objects) {
                        if (UserListActivity.following.contains(object.getString("username"))) {
                            username.add(object.getString("username"));
                            tweet.add(object.getString("tweet"));
                        }
                    }
                    arrayAdapter.notifyDataSetChanged();
                }
            }
        });

        listView.setAdapter(arrayAdapter);

        bottomNavigationView.setSelectedItemId(R.id.yourFeed);

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
                                    Toast.makeText(TweetActivity.this, "Is nothing on your mind?", Toast.LENGTH_SHORT).show();
                                } else {
                                    ParseObject tweet = new ParseObject("Tweet");
                                    tweet.put("username", ParseUser.getCurrentUser().getUsername());
                                    tweet.put("tweet", editTextTweet.getText().toString());
                                    tweet.saveInBackground(new SaveCallback() {
                                        @Override
                                        public void done(ParseException e) {
                                            if (e == null) {

                                            } else {
                                                Toast.makeText(TweetActivity.this, "Share failed!", Toast.LENGTH_SHORT).show();
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
                    case R.id.profile:
                        Intent intent1 = new Intent(getApplicationContext(), ProfileActivity.class);
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
            MainActivity.mainActivity.finish();
        }

    }

}
