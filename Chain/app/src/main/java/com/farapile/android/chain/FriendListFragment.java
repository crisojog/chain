package com.farapile.android.chain;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.farapile.android.chain.backend.myApi.model.UserBean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
/**
 * Created by Cristi on 5/12/2015.
 */
public class FriendListFragment extends Fragment {

    private FriendAdapter mFriendAdapter;
    private ListView mListView;
    private ContentProvider mContentProvider;
    private Boolean isLeaderBoard;

    public FriendListFragment() {
    }

    public class CustomComparator implements Comparator<UserBean> {
        @Override
        public int compare(UserBean o1, UserBean o2) {
            return o2.getEndorsement() - o1.getEndorsement();
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_friend_list, container, false);

        isLeaderBoard = getArguments().getBoolean("isLeaderBoard");
        mContentProvider = ContentProvider.getInstance();
        ArrayList<UserBean> users = (ArrayList<UserBean>) mContentProvider.getFriendList().clone();
        if (isLeaderBoard) {
            if (!users.contains(mContentProvider.getMyUser()))
                users.add(mContentProvider.getMyUser());
            Collections.sort(users, new CustomComparator());
        }

        mFriendAdapter = new FriendAdapter(
                getActivity(),
                R.layout.list_item_friend,
                users,
                isLeaderBoard
        );

        mListView = (ListView) rootView.findViewById(R.id.friendList);
        mListView.setAdapter(mFriendAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                UserBean user = mFriendAdapter.getItem(position);
                boolean isFriend = !user.getGplusID().equals(mContentProvider.getMyUser().getGplusID());
                ((MainActivity) getActivity()).startTaskListFragment(user.getGplusID(), user.getName(), isFriend);
            }
        });

        if (!isLeaderBoard) {
            ((ActionBarActivity) getActivity()).getSupportActionBar().setTitle(R.string.friends_chains);
        } else {
            ((ActionBarActivity) getActivity()).getSupportActionBar().setTitle(R.string.leaderboard);
        }
        return rootView;
    }
}
