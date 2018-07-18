package com.example.android.news;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.support.annotation.Nullable;

import java.util.List;

public class TechNewsLoader extends AsyncTaskLoader<List<TechNews>> {

    /**
     * Query URL
     */
    private String mUrl;

    //Constructor
    public TechNewsLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    // Create a background thread
    @Nullable
    @Override
    public List<TechNews> loadInBackground() {
        if (mUrl == null) {
            return null;
        }
        //Pull news data and return it
        return QueryUtils.fetchNewsData(mUrl);
    }
}