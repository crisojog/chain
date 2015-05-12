package com.farapile.android.chain;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.farapile.android.chain.backend.myApi.model.UserBean;
import com.google.common.collect.HashBasedTable;

import java.util.ArrayList;
import java.util.Hashtable;

/**
 * Created by Cristi on 5/12/2015.
 */

public class FriendAdapter extends ArrayAdapter<UserBean> {
    private Context mContext;
    private Boolean isLeaderBoard;

    public FriendAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
        mContext = context;
    }

    public FriendAdapter(Context context, int resource, ArrayList<UserBean> items, Boolean isLeaderBoard) {
        super(context, resource, items);
        mContext = context;
        this.isLeaderBoard = isLeaderBoard;
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


            ContentProvider cp = ContentProvider.getInstance();
            Hashtable<String, String> m =  cp.getImageMap();
            ImageView imgProfile = (ImageView) v.findViewById(R.id.friendIcon);
            if (m.containsKey(p.getGplusID()))
                new ContentProvider.LoadProfileImage(imgProfile).execute(m.get(p.getGplusID()));

            TextView tasks = (TextView) v.findViewById(R.id.numTasks);
            if (!isLeaderBoard)
                tasks.setText(p.getNumTasks() + "");
            else
                tasks.setText(p.getEndorsement() + "");
        }

        return v;
    }
}
