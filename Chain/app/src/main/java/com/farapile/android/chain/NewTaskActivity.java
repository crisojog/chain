package com.farapile.android.chain;

import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.UUID;


public class NewTaskActivity extends ActionBarActivity {

    private int numDays = 0;
    private final String INF = "\u221e";
    private ContentProvider mContentProvider = null;
    private EditText mNameText, mDescText;
    private boolean hasFocus;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_task);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final Spinner spinner = (Spinner) findViewById(R.id.type_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.type_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        mNameText = (EditText) findViewById(R.id.text_name);
        mDescText = (EditText) findViewById(R.id.text_desc);
        hasFocus = false;

        Log.d("NewTaskActivity", mNameText.getText().toString() + " " + mDescText.getText().toString());
        mContentProvider = ContentProvider.getInstance();
        Button createButton = (Button) findViewById(R.id.createButton);
        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(NewTaskActivity.this, "Clicked", Toast.LENGTH_SHORT).show();
                int type = spinner.getSelectedItemPosition();
                Log.d("asdfadsf", type + "");

                mContentProvider.addTask(
                        UUID.randomUUID(),
                        "asdf",
                        type,
                        mNameText.getText().toString(),
                        mDescText.getText().toString(),
                        System.currentTimeMillis(),
                        numDays,
                        NewTaskActivity.this
                );
                finish();
            }
        });

        final EditText daysText = (EditText) findViewById(R.id.text_days);
        daysText.setText(INF);
        daysText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!hasFocus) {
                    if (daysText.getText().toString().equals(INF)) {
                        numDays = 0;
                    } else if (daysText.getText().toString().length() > 0 && isNumeric(daysText.getText().toString())) {
                        int d = Integer.parseInt(daysText.getText().toString());
                        if (d <= 0) d = 0;
                        if (d >= 365) d = 365;
                        numDays = d;
                        if (numDays == 0) {
                            daysText.setText(INF);
                        }
                    } else {
                        numDays = 0;
                        daysText.setText(INF);
                    }
                }
                hasFocus = !hasFocus;
            }
        });
        Button minusButton = (Button) findViewById(R.id.button_remove_days);
        minusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (numDays > 0) {
                    numDays--;
                    if (numDays == 0) {
                        daysText.setText(INF);
                    } else {
                        daysText.setText(numDays + "");
                    }
                }
            }
        });
        ((GradientDrawable) minusButton.getBackground()).setColor(getResources().getColor(R.color.accent));
        Button plusButton = (Button) findViewById(R.id.button_add_days);
        plusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (numDays < 366) {
                    numDays++;
                    daysText.setText(numDays + "");
                }
            }
        });
        ((GradientDrawable) plusButton.getBackground()).setColor(getResources().getColor(R.color.accent));

    }

    public static boolean isNumeric(String str)
    {
        for (char c : str.toCharArray())
        {
            if (!Character.isDigit(c)) return false;
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_new_task, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
