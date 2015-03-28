package com.farapile.android.chain;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.farapile.android.chain.dummy.DummyContent;

public class TaskListFragment extends Fragment {

    private TaskAdapter mTaskAdapter;
    private ListView mListView;
    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public TaskListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_task_list, container, false);

        mTaskAdapter = new TaskAdapter(
                getActivity(),
                R.layout.list_item_task,
                DummyContent.ITEMS);

        mListView = (ListView) rootView.findViewById(R.id.task_list);
        mListView.setAdapter(mTaskAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                DummyContent.DummyItem task = mTaskAdapter.getItem(position);
                Intent intent = new Intent(getActivity(), TaskDetailActivity.class)
                        .putExtra(Intent.EXTRA_TEXT, task.content);
                startActivity(intent);
            }
        });
        return rootView;
    }
}
