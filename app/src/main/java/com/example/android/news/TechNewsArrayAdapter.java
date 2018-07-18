package com.example.android.news;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class TechNewsArrayAdapter extends ArrayAdapter<TechNews> {

    /**
     * Constructs a new {@link TechNewsArrayAdapter}.
     *
     * @param context of the app
     * @param tNews   is the list of technology news, which is the data source of the adapter
     */
    public TechNewsArrayAdapter(Context context, ArrayList<TechNews> tNews) {
        super(context, 0, tNews);
    }

    /**
     * Returns a list item view that displays information about the technology news
     */
    @SuppressLint("SimpleDateFormat")
    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        // Check if there is an existing list item view (called convertView) that we can reuse,
        // otherwise, if convertView is null, then inflate a new list item layout.
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_item, parent, false);
        }

        // Find the tech news at the given position in the list of technology news
        TechNews currentNews = getItem(position);

        // Find the TextView with view ID date and display the date of technology news in this TextView
        TextView dateView = listItemView.findViewById(R.id.date);
        //Formatting date
        Date dateOfNews = new Date();
        try {
            assert currentNews != null;
            dateOfNews = new SimpleDateFormat("EEE, MMM d, ''yy").parse(currentNews.getDate());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        // Create a Date String
        String date = String.valueOf(dateOfNews);
        // Putting date of tech news to textView
        dateView.setText(date);

        // Find the TextView with view ID section and display the section of technology news in this TextView
        TextView sectionView = listItemView.findViewById(R.id.section);
        // Create a section String
        String section = currentNews.getSection();
        // Putting section of tech news to textView
        sectionView.setText(section);

        // Find the TextView with view ID title and display the title of technology news in this TextView
        TextView titleView = listItemView.findViewById(R.id.title);
        // Create a title String
        String title = currentNews.getTitle();
        // Putting title of tech news to textView
        titleView.setText(title);

        // Find the TextView with view ID author and display the author of technology news in this TextView
        TextView authorView = listItemView.findViewById(R.id.author);
        // Create a title String
        String author = currentNews.getAuthor();
        // Putting title of tech news to textView
        authorView.setText(author);

        // Return the list item view that is now showing the appropriate data
        return listItemView;
    }
}