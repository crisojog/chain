package com.farapile.android.chain;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.farapile.android.chain.backend.myApi.model.UserBean;

/**
 * Created by Cristi on 5/12/2015.
 */
public class FriendListFragment extends Fragment {

    private FriendAdapter mFriendAdapter;
    private ListView mListView;
    private ContentProvider mContentProvider;

    public FriendListFragment() {
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_friend_list, container, false);

        mContentProvider = ContentProvider.getInstance();
        mFriendAdapter = new FriendAdapter(
                getActivity(),
                R.layout.list_item_friend,
                mContentProvider.getFriendList()
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
