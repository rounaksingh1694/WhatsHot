package com.example.twitterclone;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

public class MyListAdapter extends ArrayAdapter<String> {

    private int resourceLayout;
    private Context mContext;
    ArrayList<String> userFollowedList;


    public MyListAdapter(Context context, int resource, ArrayList<String> items) {
        super(context, resource, items);
        this.resourceLayout = resource;
        this.mContext = context;
        userFollowedList = new ArrayList<String>();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(mContext);
            v = vi.inflate(resourceLayout, null);
        }

        TextView textViewUsername = (TextView) v.findViewById(R.id.textViewUsername);

        textViewUsername.setText(UserListActivity.userList.get(position));

        final Button buttonFollow = (Button) v.findViewById(R.id.buttonFollow);

        if (UserListActivity.following.contains(UserListActivity.userList.get(position))) {
            buttonFollow.setText("Following");
            buttonFollow.setTextSize(14f);
        }

        buttonFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (buttonFollow.getText().toString().equalsIgnoreCase("Follow")) {
                    buttonFollow.setText("Following");
                    buttonFollow.setTextSize(14f);
                    if (!UserListActivity.following.contains(UserListActivity.userList.get(position))) {
                        UserListActivity.following.add(UserListActivity.userList.get(position));
                        ParseUser user = ParseUser.getCurrentUser();
                        user.put("following", UserListActivity.following);
                        user.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null) {
                                    Log.i("Followed", UserListActivity.userList.get(position));
                                } else {
                                    e.printStackTrace();
                                }
                            }
                        });

                        ParseQuery<ParseObject> queryUserFollowed = ParseQuery.getQuery("Follower");
                        queryUserFollowed.whereEqualTo("username", UserListActivity.userList.get(position));
                        queryUserFollowed.findInBackground(new FindCallback<ParseObject>() {
                            @Override
                            public void done(List<ParseObject> objects, ParseException e) {
                                if (e == null) {
                                    if (objects.size() > 0) {
                                        for (ParseObject userFollowed : objects) {
                                            if (userFollowed.get("username").equals(UserListActivity.userList.get(position))) {

                                                ParseQuery<ParseObject> query = ParseQuery.getQuery("Follower");
                                                query.whereEqualTo("username", UserListActivity.userList.get(position));
                                                query.getInBackground(userFollowed.getObjectId(), new GetCallback<ParseObject>() {
                                                    @Override
                                                    public void done(ParseObject object, ParseException e) {
                                                        if (e == null) {
                                                            if (object.get("followers") != null) {
                                                                userFollowedList = new ArrayList<String>();
                                                                userFollowedList = (ArrayList<String>) object.get("followers");
                                                                if (userFollowedList != null) {
                                                                    Log.i("userFollowedList", userFollowedList.toString());
                                                                } else {
                                                                    Log.i("userFollowedList", "NOTHING");
                                                                }
                                                            }
                                                        } else {
                                                            Log.i("ERROR IN FOLLOWER", "CHECK FOLLOWER");
                                                            e.printStackTrace();
                                                        }
                                                    }
                                                });

                                                if (!userFollowedList.contains(ParseUser.getCurrentUser().getUsername())) {
                                                    userFollowedList.add(ParseUser.getCurrentUser().getUsername());
                                                    userFollowed.put("followers", userFollowedList);
                                                    userFollowed.saveInBackground(new SaveCallback() {
                                                        @Override
                                                        public void done(ParseException e) {
                                                            if (e == null) {
                                                                Log.i("Follower", userFollowedList.toString());
                                                            } else {
                                                                Log.i("ERROR IN FOLLOWERS", "FOLLOWERS DID NOT WORK");
                                                                e.printStackTrace();
                                                            }
                                                        }
                                                    });
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        });


                    }

                } else {
                    buttonFollow.setText("Follow");
                    buttonFollow.setTextSize(16f);
                    if (UserListActivity.following.contains(UserListActivity.userList.get(position))) {
                        UserListActivity.following.remove(UserListActivity.userList.get(position));
                        ParseUser user = ParseUser.getCurrentUser();
                        user.put("following", UserListActivity.following);
                        user.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null) {
                                    Log.i("Unfollowed", UserListActivity.userList.get(position));

                                    ParseQuery<ParseObject> queryUserUnfollowed = ParseQuery.getQuery("Follower");
                                    queryUserUnfollowed.whereEqualTo("username", UserListActivity.userList.get(position));
                                    queryUserUnfollowed.findInBackground(new FindCallback<ParseObject>() {
                                        @Override
                                        public void done(List<ParseObject> objects, ParseException e) {
                                            if (e == null) {
                                                if (objects.size() > 0) {
                                                    for (ParseObject userUnfollowed : objects) {
                                                        if (userUnfollowed.get("username").equals(UserListActivity.userList.get(position))) {

                                                            ParseQuery<ParseObject> query = ParseQuery.getQuery("Follower");
                                                            query.whereEqualTo("username", UserListActivity.userList.get(position));
                                                            query.getInBackground(userUnfollowed.getObjectId(), new GetCallback<ParseObject>() {
                                                                @Override
                                                                public void done(ParseObject object, ParseException e) {
                                                                    if (e == null) {
                                                                        if (object.get("followers") != null) {
                                                                            userFollowedList = new ArrayList<String>();
                                                                            userFollowedList = (ArrayList<String>) object.get("followers");
                                                                        }
                                                                    } else {
                                                                        Log.i("UNFOLLOWERS ERROR", "CHECK");
                                                                        e.printStackTrace();
                                                                    }
                                                                }
                                                            });

                                                            if (userFollowedList.contains(ParseUser.getCurrentUser().getUsername())) {
                                                                userFollowedList.remove(ParseUser.getCurrentUser().getUsername());
                                                                userUnfollowed.put("followers", userFollowedList);
                                                                userUnfollowed.saveInBackground(new SaveCallback() {
                                                                    @Override
                                                                    public void done(ParseException e) {
                                                                        if (e == null) {
                                                                            Log.i("UnFollower", userFollowedList.toString());
                                                                        } else {
                                                                            Log.i("ERROR IN UNFOLLOWERS", "UNFOLLOWERS DID NOT WORK");
                                                                            e.printStackTrace();
                                                                        }
                                                                    }
                                                                });
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    });

                                } else {
                                    e.printStackTrace();
                                }
                            }
                        });

                    }
                }
            }
        });

        return v;

    }

}
