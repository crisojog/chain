package com.farapile.android.chain;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.farapile.android.chain.dummy.DummyContent;

import java.util.List;

/**
 * Created by crisojog on 26/03/15.
 */
public class TaskAdapter extends ArrayAdapter<DummyContent.DummyItem> {
    public TaskAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    public TaskAdapter(Context context, int resource, List<DummyContent.DummyItem> items) {
        super(context, resource, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {

            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.list_item_task, null);

        }

        DummyContent.DummyItem p = getItem(position);

        if (p != null) {

            TextView t1 = (TextView) v.findViewById(R.id.taskName);
            if (t1 != null) {
                t1.setText(p.content);
            }

            LinearLayout c1 = (LinearLayout) v.findViewById(R.id.taskSoFarLayout);
            ((GradientDrawable) c1.getBackground()).setColor(Color.argb(255, 255, position * 80, position * 80));
            LinearLayout c2 = (LinearLayout) v.findViewById(R.id.taskToGoLayout);
            ((GradientDrawable) c2.getBackground()).setColor(Color.argb(255, 255, position * 80, position * 80));
        }

        return v;

    }
}
