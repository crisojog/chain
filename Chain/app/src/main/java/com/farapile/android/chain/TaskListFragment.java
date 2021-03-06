package com.farapile.android.chain;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.farapile.android.chain.backend.myApi.model.TaskBean;
import com.melnykov.fab.FloatingActionButton;

import java.util.ArrayList;
import java.util.concurrent.Callable;

public class TaskListFragment extends Fragment {

    private TaskAdapter mTaskAdapter;
    private ListView mListView;
    private FloatingActionButton fab;
    private ContentProvider mContentProvider;
    private ProgressBar mProgressBar;

    private String gPlusId, name;
    private boolean isFriend;

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

        gPlusId = getArguments().getString("gPlusId");
        name = getArguments().getString("name");
        isFriend = getArguments().getBoolean("isFriend");

        mContentProvider = ContentProvider.getInstance();
        if (isFriend) {
            mTaskAdapter = new TaskAdapter(
                    getActivity(),
                    R.layout.list_item_task,
                    new ArrayList<TaskBean>());
        } else {
            mTaskAdapter = new TaskAdapter(
                    getActivity(),
                    R.layout.list_item_task,
                    mContentProvider.getTaskList());
        }
        mListView = (ListView) rootView.findViewById(R.id.task_list);
        mListView.setAdapter(mTaskAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TaskBean task = mTaskAdapter.getItem(position);
                Log.d("bla", task.toString());
                Intent intent = new Intent(getActivity(), TaskDetailActivity.class)
                        .putExtra("Id", task.getId())
                        .putExtra("userGplusID", task.getUserGplusID())
                        .putExtra("type", task.getType())
                        .putExtra("name", task.getName())
                        .putExtra("description", task.getDescription())
                        .putExtra("startDate", task.getStartDate())
                        .putExtra("duration", task.getDuration())
                        .putExtra("current", task.getCurrent());
                startActivity(intent);
            }
        });

        mProgressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
        //if (!isFriend && (mContentProvider.getTaskList() == null || mContentProvider.getTaskList().size() == 0)) {
        //    refreshTasks();
        //} else if (isFriend) {
            refreshTasks();
        //}

        if (isFriend)
            ((ActionBarActivity) getActivity()).getSupportActionBar().setTitle(name + "\'s chains");
        else
            ((ActionBarActivity) getActivity()).getSupportActionBar().setTitle(R.string.my_chains);

        return rootView;
    }

    public void refreshTasks() {
        mProgressBar.setVisibility(View.VISIBLE);

        mContentProvider.getTasks(new Pair<String, Callable>(gPlusId, new Callable() {
            @Override
            public Integer call() throws Exception {
                //mTaskAdapter.notifyDataSetChanged();
                mProgressBar.setVisibility(View.GONE);
                mTaskAdapter.clear();
                mTaskAdapter.addAll(mContentProvider.getTaskList());
                mTaskAdapter.notifyDataSetChanged();
                Log.d("TaskListFragment", "" + mContentProvider.getTaskList().size());
                return 0;
            }
        }));
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        Log.d("TAG", "onViewCreated");
        fab = (FloatingActionButton) getActivity().findViewById(R.id.fab);
        if (isFriend) {
            fab.setVisibility(View.GONE);
        } else {
            fab.attachToListView(mListView);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    createNewTask();
                }
            });
        }
    }
    private void createNewTask() {
        Intent intent = new Intent(getActivity(), NewTaskActivity.class);
        intent.putExtra("user", gPlusId);
        startActivity(intent);
    }

}
