package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class ResultsAdapter extends ArrayAdapter<ResultItem> {

    public ResultsAdapter(Context context, List<ResultItem> items) {
        super(context, 0, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_result, parent, false);
        }

        ResultItem item = getItem(position);

        TextView title = convertView.findViewById(R.id.result_title);
        TextView subtitle = convertView.findViewById(R.id.result_subtitle);

        if (item != null) {
            title.setText(item.getName());
            subtitle.setText(item.getArtist());
        }

        return convertView;
    }
}
