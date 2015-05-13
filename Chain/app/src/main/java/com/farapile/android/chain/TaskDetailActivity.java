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
    boolean isFriend = false;
    ContentProvider mContentProvider;

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
        mContentProvider = ContentProvider.getInstance();

        Intent intent = getIntent();
        Id = intent.getStringExtra("Id");
        userGplusID = intent.getStringExtra("userGplusID");
        type = intent.getIntExtra("type", 0);
        name = intent.getStringExtra("name");
        description = intent.getStringExtra("description");
        startDate = intent.getLongExtra("startDate", 0);
        duration = intent.getIntExtra("duration", 0);
        current = intent.getIntExtra("current", 0);
        isFriend = !userGplusID.equals(mContentProvider.getMyUser().getGplusID());

        Log.d("TaskDetailActivity", name + " " + types[type] + " " + description);
        getSupportActionBar().setTitle(name);

        TextView typeText = (TextView) findViewById(R.id.task_type);
        typeText.setText(types[type]);

        TextView descText = (TextView) findViewById(R.id.task_desc);
        descText.setText(description);

        TextView targetText = (TextView) findViewById(R.id.task_target);
        TextView targetLabel = (TextView) findViewById(R.id.task_target_label);
        if (duration != 0) {
            Date sDate = new Date(startDate), eDate = new Date(startDate);
            eDate.setDate(eDate.getDate() + duration);
            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd yyyy");
            targetText.setText(duration + ", from " + sdf.format(sDate) +
                    " to " + sdf.format(eDate));
        } else {
            targetLabel.setVisibility(View.GONE);
            targetText.setVisibility(View.GONE);
        }

        TextView progressText = (TextView) findViewById(R.id.task_progress);
        if (duration != 0) {
            progressText.setText(current + " so far, " + (duration - current) + " to go");
        } else {
            progressText.setText("Ongoing activity");
        }

        CircleProgress percentage = (CircleProgress) findViewById(R.id.circle_progress);
        if (duration != 0) {
            percentage.setProgress(current * 100 / duration);
            percentage.setFinishedColor(colors[type]);
        } else {
            percentage.setVisibility(View.GONE);
        }

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        if (isFriend) {
            fab.setImageDrawable(getResources().getDrawable(R.drawable.endorse));
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mContentProvider.endorse(mContentProvider.getMyUser().getGplusID(), userGplusID, Id, TaskDetailActivity.this);
                    fab.setVisibility(View.GONE);
                }
            });
        } else {
            fab.setImageDrawable(getResources().getDrawable(R.drawable.ic_check_today));
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    current++;
                    mContentProvider.updateCurrent(Id, TaskDetailActivity.this);
                    fab.setVisibility(View.GONE);
                }
            });
        }
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
