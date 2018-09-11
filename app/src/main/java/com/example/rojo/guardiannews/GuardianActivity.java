package com.example.rojo.guardiannews;

import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toolbar;

import java.util.ArrayList;
import java.util.List;

public class GuardianActivity extends AppCompatActivity implements LoaderCallbacks<List<Guardian>> {
    private static final int GUARDIAN_LOADER_ID = 1;
    private static final String LOG_TAG = GuardianAdapter.class.getName();

    //Used to understand Guardian API https://open-platform.theguardian.com/documentation/
    //https://groups.google.com/forum/#!searchin/guardian-api-talk/sort$20date$20query%7Csort:date/guardian-api-talk/l873UWhh1q4/9zAIIgG-QhsJ
    private static final String GUARDIAN_REQUEST_URL =
            "https://content.guardianapis.com/search?q=film&section=film" +
                    "&order-by=newest&show-tags=contributor&page-size=10" +
                    "&api-key=3f7e3ebb-b747-4c16-91f7-bdaefedc31a7";
    private GuardianAdapter mAdapter;
    private TextView mEmptyStateTextView;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.guardian_activity);

        //Find a reference to the {@link ListView} in the layout.
        ListView guardianListView = findViewById(R.id.list);

        mEmptyStateTextView = (TextView) findViewById(R.id.empty_view);
        guardianListView.setEmptyView(mEmptyStateTextView);

        //Create a new adapter that takes an empty list of guardians as input
        mAdapter = new GuardianAdapter(this, new ArrayList<Guardian>());

        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        guardianListView.setAdapter(mAdapter);

        guardianListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // Find the current earthquake that was clicked on
                Guardian currentGuardian = mAdapter.getItem(position);

                // Convert the String URL into a URI object (to pass into the Intent constructor)
                Uri guardianUri = Uri.parse(currentGuardian.getUrl());

                // Create a new intent to view the earthquake URI
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, guardianUri);

                // Send the intent to launch a new activity
                startActivity(websiteIntent);
            }
        });

        // Get a reference to the ConnectivityManager to check state of network connectivity
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        // Get details on the currently active default data network
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        // If there is a network connection, fetch data
        if (networkInfo != null && networkInfo.isConnected()) {
            // Get a reference to the LoaderManager, in order to interact with loaders.
            LoaderManager loaderManager = getLoaderManager();
            // Initialize the loader. Pass in the int ID constant defined above and pass in null for
            // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
            // because this activity implements the LoaderCallbacks interface).
            loaderManager.initLoader(GUARDIAN_LOADER_ID, null, this);
        } else {
            // Otherwise, display error
            // First, hide loading indicator so error message will be visible
            View loadingIndicator = findViewById(R.id.loading_indicator);
            loadingIndicator.setVisibility(View.GONE);
            // Update empty state with no connection error message
            mEmptyStateTextView.setText(R.string.no_internet_connection);
        }
    }
    @Override
    public Loader<List<Guardian>> onCreateLoader(int i, Bundle bundle) {
        // Create a new loader for the given URL
        return new GuardianLoader(this, GUARDIAN_REQUEST_URL);
    }
    @Override
    public void onLoadFinished(Loader<List<Guardian>> loader, List<Guardian> guardians) {
        // Hide loading indicator because the data has been loaded
        View loadingIndicator = findViewById(R.id.loading_indicator);
        loadingIndicator.setVisibility(View.GONE);

        // Set empty state text to display "No Articles Found"
        mEmptyStateTextView.setText(R.string.no_news);

        // Clear the adapter of previous guardian data
        mAdapter.clear();

        // If there is a valid list of {@link Guardian}s, then add them to the adapter's
        // data set. This will trigger the ListView to update.
        if (guardians != null && !guardians.isEmpty()) {
            mAdapter.addAll(guardians);
        }
    }
    @Override
    public void onLoaderReset(Loader<List<Guardian>> loader) {
        // Loader reset, so we can clear out our existing data.
        mAdapter.clear();
    }
}