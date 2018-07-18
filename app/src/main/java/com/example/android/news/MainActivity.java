package com.example.android.news;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<TechNews>>,
        SharedPreferences.OnSharedPreferenceChangeListener {

    /**
     * URL for technology news data from the guardianapis dataset
     */
    private static String QUARDIAN_REQUEST_URL =
            "https://content.guardianapis.com/search?show-tags=contributor&q=technology%20article&api-key=02a961e9-80f2-47b8-a87c-eb679abe85b4";

    private static final int NEWS_LOADER_ID = 1;

    /**
     * Adapter for the list of technology news
     */
    private TechNewsArrayAdapter mAdapter;

    /**
     * TextView that is displayed when the list is empty
     */
    private TextView mEmptyStateTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Find a reference to the {@link ListView} in the layout
        ListView techNewsListView = findViewById(R.id.listNews);

        // Adding empty view to listView
        mEmptyStateTextView = findViewById(R.id.empty_view);
        techNewsListView.setEmptyView(mEmptyStateTextView);

        // Create a new adapter that takes an empty list of tech news as input
        mAdapter = new TechNewsArrayAdapter(this, new ArrayList<TechNews>());

        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        techNewsListView.setAdapter(mAdapter);

        // Adding onItemClickListener to techNewsListView - opening website of clicked tech news
        techNewsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                // Find the current tech news that was clicked on
                TechNews currentNews = mAdapter.getItem(position);
                assert currentNews != null;
                // Convert the String URL into a URI object (to pass into the Intent constructor)
                Uri newsUri = Uri.parse(currentNews.getUrl());
                // Create a new intent to view the tech news URI
                Intent webLink = new Intent(Intent.ACTION_VIEW, newsUri);
                // Send the intent to launch a new activity
                startActivity(webLink);
            }
        });

        // Get a reference to the ConnectivityManager to check state of network connectivity
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        // Get details on the currently active default data network
        assert connMgr != null;
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        // If there is a network connection, fetch data
        if (networkInfo != null && networkInfo.isConnected()) {
            // Get a reference to the LoaderManager, in order to interact with loaders.
            LoaderManager loaderManager = getLoaderManager();

            // Initialize the loader. Pass in the int ID constant defined above and pass in null for
            // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
            // because this activity implements the LoaderCallbacks interface).
            loaderManager.initLoader(0, null, this);
        } else {
            // Otherwise, display error
            // First, hide loading indicator so error message will be visible
            View loadingIndicator = findViewById(R.id.loader);
            assert loadingIndicator != null;
            loadingIndicator.setVisibility(View.GONE);

            // Update empty state with no connection error message
            mEmptyStateTextView.setText(R.string.no_internet_connection);
        }
    }

    @Override
    public Loader<List<TechNews>> onCreateLoader(int id, Bundle args) {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        // getString retrieves a String value from the preferences.
        // The second parameter is the default value for this preference.
        String newsOnPage = sharedPrefs.getString(
                getString(R.string.settings_newsOnPage_key),
                getString(R.string.settings_newsOnPage_default));

        String orderBy = sharedPrefs.getString(
                getString(R.string.settings_order_by_key),
                getString(R.string.settings_order_by_default)
        );

        // parse breaks apart the URI string that's passed into its parameter
        Uri baseUri = Uri.parse(QUARDIAN_REQUEST_URL);

        // buildUpon prepares the baseUri that we just parsed so we can add query parameters to it
        Uri.Builder uriBuilder = baseUri.buildUpon();

        // Append query parameter and its value
        uriBuilder.appendQueryParameter("page-size", newsOnPage);
        uriBuilder.appendQueryParameter("orderby", orderBy);

        // Return the completed uri
        return new TechNewsLoader(this, uriBuilder.toString());
    }

    //After loading, display result
    @Override
    public void onLoadFinished(Loader<List<TechNews>> loader, List<TechNews> techNews) {
        // Hide loading indicator because the data has been loaded
        View loadingIndicator = findViewById(R.id.loader);
        assert loadingIndicator != null;
        loadingIndicator.setVisibility(View.GONE);

        // Set empty state text to display "No earthquakes found."
        mEmptyStateTextView.setText(R.string.no_news);

        // Clear the adapter of previous earthquake data
        mAdapter.clear();

        // If there is a valid list of {@link Earthquake}s, then add them to the adapter's
        // data set. This will trigger the ListView to update.
        if (techNews != null && !techNews.isEmpty()) {
            mAdapter.addAll(techNews);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<TechNews>> loader) {
        // TechNewsLoader reset, so we can clear out our existing data.
        mAdapter.clear();
    }

    // This method initialize the contents of the Activity's options menu
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    // This method is called whenever an item in the options menu is selected.
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getString(R.string.settings_section_key))){
            mAdapter.clear();
            mEmptyStateTextView.setVisibility(View.GONE);

            View loadingIndicator = findViewById(R.id.loader);
            loadingIndicator.setVisibility(View.VISIBLE);

            getLoaderManager().restartLoader(NEWS_LOADER_ID, null, this);
        }
    }
}