package com.farapile.android.chain;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.farapile.android.chain.backend.myApi.model.TaskBean;

import java.util.ArrayList;

/**
 * Created by crisojog on 26/03/15.
 */
public class TaskAdapter extends ArrayAdapter<TaskBean> {
    private Context mContext;
    int[] drawables = {
            R.drawable.sports,
            R.drawable.diet,
            R.drawable.hobby,
            R.drawable.work
    };
    int[] colors = {
            R.color.sports,
            R.color.diet,
            R.color.hobby,
            R.color.work
    };

    public TaskAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
        mContext = context;
    }

    public TaskAdapter(Context context, int resource, ArrayList<TaskBean> items) {
        super(context, resource, items);
        mContext = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {

            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.list_item_task, null);

        }

        TaskBean p = getItem(position);

        if (p != null) {

            TextView t1 = (TextView) v.findViewById(R.id.taskName);
            if (t1 != null) {
                t1.setText(p.getName());
            }

            TextView soFar = (TextView) v.findViewById(R.id.taskSoFar);
            TextView toGo = (TextView) v.findViewById(R.id.taskToGo);

            soFar.setText("" + p.getCurrent());
            toGo.setText("" + (p.getDuration() - p.getCurrent()));

            ImageView imgType = (ImageView) v.findViewById(R.id.taskIcon);
            imgType.setImageDrawable(mContext.getDrawable(drawables[p.getType()]));

            LinearLayout t = (LinearLayout) v.findViewById(R.id.taskIconLayout);
            ((GradientDrawable) t.getBackground()).setColor(mContext.getResources().getColor(colors[p.getType()]));
            LinearLayout c1 = (LinearLayout) v.findViewById(R.id.taskSoFarLayout);
            ((GradientDrawable) c1.getBackground()).setColor(mContext.getResources().getColor(colors[p.getType()]));
            LinearLayout c2 = (LinearLayout) v.findViewById(R.id.taskToGoLayout);
            ((GradientDrawable) c2.getBackground()).setColor(mContext.getResources().getColor(colors[p.getType()]));
        }

        return v;

    }
}
