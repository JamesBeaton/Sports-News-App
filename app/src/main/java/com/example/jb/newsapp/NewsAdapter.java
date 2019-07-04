package com.example.jb.newsapp;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class NewsAdapter extends ArrayAdapter<News> {

    private static class ViewHolder {
        public TextView titleNewsTextView;
        public TextView authorNewsTextView;
        public ImageView thumbnailImageView;
        public TextView sectionNewsTextView;
        public TextView publicationDateTextView;
    }

    public NewsAdapter(Activity context, ArrayList<News> SportsNews) {
        // Here, we initialize the ArrayAdapter's internal storage for the context and the list.
        // the second argument is used when the ArrayAdapter is populating a single TextView.
        // Because this is a custom adapter for two TextViews and an ImageView, the adapter is not
        // going to use this second argument, so it can be any value. Here, we used 0.
        super(context, 0, SportsNews);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Check if the existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder = null;
        if (viewHolder == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_item, parent, false);
        }

        // Get the current position of News
        final News currentNews = getItem(position);

        // Find the TextView in the list_item.xml (mapping)
        viewHolder.titleNewsTextView = (TextView) convertView.findViewById(R.id.news_title);
        viewHolder.authorNewsTextView = (TextView) convertView.findViewById(R.id.author_news);
        viewHolder.thumbnailImageView = (ImageView) convertView.findViewById(R.id.thumbnail_image);
        viewHolder.sectionNewsTextView = (TextView) convertView.findViewById(R.id.section_type);
        viewHolder.publicationDateTextView = (TextView) convertView.findViewById(R.id.publicationDate);

        // Set proper value in each fields
        assert currentNews != null;
        viewHolder.titleNewsTextView.setText(currentNews.getTitle());
        viewHolder.authorNewsTextView.setText(currentNews.getAuthor());
        Picasso.with(getContext()).load(currentNews.getThumbUrl()).into(viewHolder.thumbnailImageView);
        viewHolder.sectionNewsTextView.setText(String.valueOf(currentNews.getSection()));
        viewHolder.publicationDateTextView.setText(String.valueOf(currentNews.getDate()));

        return convertView;
    }
}