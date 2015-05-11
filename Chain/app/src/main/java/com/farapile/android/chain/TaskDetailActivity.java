package com.farapile.android.chain;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.github.lzyzsd.circleprogress.CircleProgress;
import com.melnykov.fab.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TaskDetailActivity extends ActionBarActivity {

    String Id;
    String userGplusID;
    int type;
    String name;
    String description;
    long startDate;
    int duration;
    int current;

    int[] colors = {
            R.color.sports,
            R.color.diet,
            R.color.hobby,
            R.color.work
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String[] types = getResources().getStringArray(R.array.type_array);

        Intent intent = getIntent();
        Id = intent.getStringExtra("Id");
        userGplusID = intent.getStringExtra("userGplusID");
        type = intent.getIntExtra("type", 0);
        name = intent.getStringExtra("name");
        description = intent.getStringExtra("description");
        startDate = intent.getLongExtra("startDate", 0);
        duration = intent.getIntExtra("duration", 0);
        current = intent.getIntExtra("current", 0);

        Log.d("TaskDetailActivity", name + " " + types[type] + " " + description);
        getSupportActionBar().setTitle(name);

        TextView typeText = (TextView) findViewById(R.id.task_type);
        typeText.setText(types[type]);

        TextView descText = (TextView) findViewById(R.id.task_desc);
        descText.setText(description);

        TextView targetText = (TextView) findViewById(R.id.task_target);
        Date sDate = new Date(startDate), eDate = new Date(startDate);
        eDate.setDate(eDate.getDate() + duration);
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd yyyy");
        targetText.setText(duration + ", from " + sdf.format(sDate) +
                " to " + sdf.format(eDate));

        TextView progressText = (TextView) findViewById(R.id.task_progress);
        progressText.setText(current + " so far, " + (duration - current) + " to go");

        CircleProgress percentage = (CircleProgress) findViewById(R.id.circle_progress);
        percentage.setProgress(current * 100 / duration);
        percentage.setFinishedColor(colors[type]);


        final ContentProvider mContentProvider = ContentProvider.getInstance();
        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                current++;
                mContentProvider.updateCurrent(Id, TaskDetailActivity.this);
                fab.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            navigateUpTo(new Intent(this, MainActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
