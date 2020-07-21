package com.example.twitterclone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
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
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

public class UserListActivity extends AppCompatActivity {

    ListView listView;
    static ArrayList<String> userList;
    static ArrayList<String> following;
    static ArrayList<String> followers;
    MyListAdapter arrayAdapter;

    BottomNavigationView bottomNavigationView;

    Dialog dialog;
    Button buttonTweet;
    EditText editTextTweet;

    static Activity userListActivity;

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
                        finish();
                    } else {
                        Toast.makeText(UserListActivity.this, "Log ou failed!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            return true;
        }
        return false;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);

        setTitle("What's Hot Users");
        setTitleColor(Color.parseColor("#EAFBFF"));

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavigationView);

        dialog = new Dialog(this);

        listView = (ListView) findViewById(R.id.listView);
        userList = new ArrayList<String>();
        following = new ArrayList<String>();
        followers = new ArrayList<String>();
        arrayAdapter = new MyListAdapter(this, R.layout.my_list_adapter, userList);

        userListActivity = UserListActivity.this;

        Intent intentMainActivity = getIntent();

        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereNotEqualTo("username", ParseUser.getCurrentUser().getUsername());
        query.addAscendingOrder("username");
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if (e == null) {
                    if (objects.size() > 0) {
                        for (ParseUser username : objects) {
                            userList.add(username.getUsername().toString());
                        }
                        arrayAdapter.notifyDataSetChanged();
                    }
                }
            }
        });

        ParseQuery<ParseUser> followingQuery = ParseUser.getQuery();
        followingQuery.whereEqualTo("username", ParseUser.getCurrentUser().getUsername());
        followingQuery.getInBackground(ParseUser.getCurrentUser().getObjectId(), new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser object, ParseException e) {
                if (e == null) {
                    if (object.get("following") != null)
                        following = (ArrayList<String>) object.get("following");
                    Log.i("FollowingList", following.toString());
                    /*Intent intent = new Intent(getApplicationContext(), TweetActivity.class);
                    if(following.size() > 0) {
                        intent.putExtra("isListEmpty", false);
                    } else {
                        intent.putExtra("isListEmpty", true);
                    }
                    startActivity(intent);*/
                }
            }
        });

        Log.i("LIst", userList.toString());

        listView.setAdapter(arrayAdapter);

        /*for (int i = 0; i < listView.getCount(); i++) {
            View view = listView.getChildAt(i);
            TextView textViewUser = (TextView) view.findViewById(R.id.textViewUsername);
            String user = textViewUser.getText().toString();
            if(following.contains(user)) {
                Button buttonFollowing = (Button) view.findViewById(R.id.buttonFollow);
                buttonFollowing.setText("Following");
            }
        }*/

        bottomNavigationView.setSelectedItemId(R.id.users);

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
                                if (editTextTweet.getText().toString().equals("") || editTextTweet.getText().toString() == null) {
                                    Toast.makeText(UserListActivity.this, "Is nothing on your mind?", Toast.LENGTH_SHORT).show();
                                } else {
                                    ParseObject tweet = new ParseObject("Tweet");
                                    tweet.put("username", ParseUser.getCurrentUser().getUsername());
                                    tweet.put("tweet", editTextTweet.getText().toString());
                                    tweet.saveInBackground(new SaveCallback() {
                                        @Override
                                        public void done(ParseException e) {
                                            if (e == null) {

                                            } else {
                                                Toast.makeText(UserListActivity.this, "Sorry, could not share your thought", Toast.LENGTH_SHORT).show();
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
                    case R.id.yourFeed:
                        Intent intent = new Intent(getApplicationContext(), TweetActivity.class);
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
    protected void onResume() {
        super.onResume();

        bottomNavigationView.setSelectedItemId(R.id.users);

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
