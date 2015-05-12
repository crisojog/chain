package com.farapile.android.chain;

import android.os.Bundle;
import android.support.v4.app.Fragment;
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
            return o1.getEndorsement() - o2.getEndorsement();
        }
    }

    public void setIsLeaderBoard(Boolean isLeaderBoard) {
        this.isLeaderBoard = isLeaderBoard;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_friend_list, container, false);

        mContentProvider = ContentProvider.getInstance();
        ArrayList<UserBean> users = mContentProvider.getFriendList();
        if (isLeaderBoard) {
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

                ((MainActivity)getActivity()).startTaskListFragment(user.getGplusID(), user.getName(), true);
            }
        });

        return rootView;
    }
}
