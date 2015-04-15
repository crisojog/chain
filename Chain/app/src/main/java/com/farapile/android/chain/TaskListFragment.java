package com.farapile.android.chain;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.farapile.android.chain.backend.myApi.model.TaskBean;
import com.farapile.android.chain.dummy.DummyContent;
import com.google.android.gms.plus.Plus;
import com.melnykov.fab.FloatingActionButton;

import java.util.concurrent.Callable;

public class TaskListFragment extends Fragment {

    private TaskAdapter mTaskAdapter;
    private ListView mListView;
    private FloatingActionButton fab;
    private ContentProvider cp;
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
                ContentProvider.taskList);

        mListView = (ListView) rootView.findViewById(R.id.task_list);
        mListView.setAdapter(mTaskAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TaskBean task = mTaskAdapter.getItem(position);
                Intent intent = new Intent(getActivity(), TaskDetailActivity.class)
                        .putExtra(Intent.EXTRA_TEXT, "Task " + task.getType());
                startActivity(intent);
            }
        });

        cp = ContentProvider.getInstance();
        //new ContentProvider.EndpointsAsyncTask().execute(new Pair<Context, String>(getActivity(), "Manfred"));
        refreshTasks();

        ((ActionBarActivity) getActivity()).getSupportActionBar().setTitle(R.string.my_chains);

        return rootView;
    }

    public void refreshTasks() {
        String user = GoogleApi.mGoogleApiClient != null ? Plus.PeopleApi.getCurrentPerson(GoogleApi.mGoogleApiClient).getUrl() : "asdf";
        cp.getTasks(new Pair<String, Callable>(user, new Callable() {
            @Override
            public Integer call() throws Exception {
                //mTaskAdapter.notifyDataSetChanged();
                mTaskAdapter.clear();
                mTaskAdapter.addAll(ContentProvider.taskList);
                Log.d("TaskListFragment", "" + ContentProvider.taskList.size());
                return 0;
            }
        }));
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        Log.d("TAG", "onViewCreated");
        fab = (FloatingActionButton) getActivity().findViewById(R.id.fab);
        fab.attachToListView(mListView);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNewTask();
            }
        });
    }
    private void createNewTask() {
        Intent intent = new Intent(getActivity(), NewTask.class);
        startActivity(intent);
    }

}
