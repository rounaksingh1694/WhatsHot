package com.example.twitterclone;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.parse.ParseUser;

import java.util.ArrayList;

public class MyAdapter extends ArrayAdapter {

    private int resourceLayout;
    private Context mContext;

    Button buttonFollow;

    public MyAdapter(Context context, int resource, ArrayList<String> items) {
        super(context, resource, items);
        this.resourceLayout = resource;
        this.mContext = context;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(mContext);
            v = vi.inflate(resourceLayout, null);
        }

        TextView textViewUsernameOfUser = (TextView) v.findViewById(R.id.textViewUsernameOfUser);

        textViewUsernameOfUser.setText("~ " + ParseUser.getCurrentUser().getUsername());

        TextView textViewTweetOfUser = (TextView) v.findViewById(R.id.textViewTweetOfUser);
        textViewTweetOfUser.setText(ProfileActivity.myTweets.get(position));

        return v;

    }

}
