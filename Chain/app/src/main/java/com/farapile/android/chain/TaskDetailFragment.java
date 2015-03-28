package com.farapile.android.chain;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.farapile.android.chain.dummy.DummyContent;

public class TaskDetailFragment extends Fragment {

    private DummyContent.DummyItem mItem;

    public TaskDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_task_detail, container, false);
        Intent intent = getActivity().getIntent();

        if (intent != null && intent.hasExtra(Intent.EXTRA_TEXT)) {
            String content = intent.getStringExtra(Intent.EXTRA_TEXT);
            ((TaskDetailActivity)getActivity()).getSupportActionBar().setTitle(content);
            ((TextView) rootView.findViewById(R.id.task_detail)).setText(content);
        }

        return rootView;
    }
}
