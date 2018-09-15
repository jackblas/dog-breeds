package com.jackblaszkowski.dogbreeds.ui;

import android.arch.lifecycle.ViewModelProviders;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.jackblaszkowski.dogbreeds.R;
import com.jackblaszkowski.dogbreeds.Utils;
import com.jackblaszkowski.dogbreeds.viewmodel.DogBreedViewModel;

// MainActivity with more-fragment
public class MainActivity extends AppCompatActivity implements MainActivityFragment.OnFragmentInteractionListener,
        SharedPreferences.OnSharedPreferenceChangeListener {

    DogBreedViewModel mViewModel;
    ProgressBar mProgressBar;
    MenuItem mRefreshItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);

        mProgressBar = (ProgressBar) findViewById(R.id.progressBar1);

        // Create a ViewModel the first time the system calls an activity's onCreate() method.
        // Re-created activities receive the same ViewModel instance created by the first activity.
        mViewModel = ViewModelProviders.of(this).get(DogBreedViewModel.class);

        if (savedInstanceState == null) {
            //mProgressBar.setVisibility(View.VISIBLE);

            MainActivityFragment fragment = new MainActivityFragment();

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.main_container, fragment)
                    .commit();
        }

    }

    @Override
    protected void onResume() {

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        sp.registerOnSharedPreferenceChangeListener(this);
        super.onResume();
    }

    @Override
    protected void onPause() {

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        sp.unregisterOnSharedPreferenceChangeListener(this);
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_refresh) {

            mProgressBar.setVisibility(View.VISIBLE);
            mViewModel.refreshData();

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        mRefreshItem = menu.findItem(R.id.action_refresh);

        int status = Utils.getServerStatus(getApplicationContext());
        if (status == Utils.STATUS_SERVER_OK)
            mRefreshItem.setEnabled(true);
        else
            mRefreshItem.setEnabled(false);

        return true;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        if (key.equals(getString(R.string.pref_server_status_key))) {


            int serverStatus = Utils.getServerStatus(getApplicationContext());

            Snackbar snackbar = Snackbar.make(findViewById(R.id.main_container), R.string.server_error_message, Snackbar.LENGTH_LONG)
                    .setAction("Action", null);

            View sbView = snackbar.getView();
            TextView textView = sbView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(Color.YELLOW);

            switch (serverStatus) {
                case Utils.STATUS_SERVER_ERROR:
                    mProgressBar.setVisibility(View.INVISIBLE);
                    snackbar.setText(R.string.server_error_message);
                    snackbar.show();
                    break;

                case Utils.STATUS_NO_CONNECTION:
                    mProgressBar.setVisibility(View.INVISIBLE);
                    snackbar.setText(R.string.no_connection_message);
                    snackbar.show();
                    break;

                default:
                    break;

            }

        }

    }


    // Implement OnFragmentInteractionListener:

    @Override
    public void onDataLoaded() {

        if (mProgressBar != null)
            mProgressBar.setVisibility(View.GONE);

    }

    @Override
    public void onConnectivityChanged(boolean enabled) {

        if (mRefreshItem != null) {
            mRefreshItem.setEnabled(enabled);
        }

    }
}