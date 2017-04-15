package com.example.shaimaalzahrani.udacity8newsfeed;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by shaimaalzahrani on 15/04/2017.
 */

public class NewsList extends ArrayAdapter<News> {

    private Context context;
    ArrayList<News> NewsList;
    private static final String LOG_TAG = NewsList.class.getSimpleName();

    static class ViewHolder {
        public TextView title;
        public TextView Description;
    }

    public NewsList(Context context, ArrayList<News> NewsList) {
        super(context, -1, NewsList);
        this.context = context;
        this.NewsList = NewsList;
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public int getItemViewType(int position) {
        return 1;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            LayoutInflater layoutInflater = (LayoutInflater) getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(R.layout.list_item, null);
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.title = (TextView) view.findViewById(R.id.news_title);
            viewHolder.Description = (TextView) view.findViewById(R.id.news_description);
            view.setTag(viewHolder);
        }

        ViewHolder viewHolder = (ViewHolder) view.getTag();
        viewHolder.title.setText(NewsList.get(position).getTitle());
        viewHolder.Description.setText(NewsList.get(position).getDescription());

        return view;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public int getCount() {
        return NewsList.size();
    }

}