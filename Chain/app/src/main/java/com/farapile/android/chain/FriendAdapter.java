package com.farapile.android.chain;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.farapile.android.chain.backend.myApi.model.UserBean;

import java.util.ArrayList;

/**
 * Created by Cristi on 5/12/2015.
 */

public class FriendAdapter extends ArrayAdapter<UserBean> {
    private Context mContext;

    public FriendAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
        mContext = context;
    }

    public FriendAdapter(Context context, int resource, ArrayList<UserBean> items) {
        super(context, resource, items);
        mContext = context;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.list_item_friend, null);
        }

        UserBean p = getItem(position);

        if (p != null) {
            TextView name = (TextView) v.findViewById(R.id.friendName);
            if (name != null) {
                name.setText(p.getName());
            }

            ImageView imgProfile = (ImageView) v.findViewById(R.id.friendIcon);
            //TODO setImgProfile

            TextView tasks = (TextView) v.findViewById(R.id.numTasks);
            tasks.setText(p.getNumTasks() + "");
        }

        return v;
    }
}