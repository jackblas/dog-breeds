package com.jackblaszkowski.dogbreeds.ui;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.jackblaszkowski.dogbreeds.R;


public class MainActivity extends AppCompatActivity implements MainActivityFragment.OnFragmentInteractionListener
        {

    MainActivityFragment fragment;
    MenuItem mRefreshItem;
    Boolean refreshEnabled=true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);

        if (savedInstanceState == null) {

            fragment = new MainActivityFragment();

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.main_container, fragment)
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_refresh) {

            fragment.setRefresh();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        mRefreshItem = menu.findItem(R.id.action_refresh);

        if(refreshEnabled){
            mRefreshItem.setEnabled(true);
        } else {
            mRefreshItem.setEnabled(false);
        }

        return true;
    }


    // Implement OnFragmentInteractionListener:
    @Override
    public void onConnectivityChanged(boolean enabled) {

        if (mRefreshItem != null) {
            mRefreshItem.setEnabled(enabled);
        }
        refreshEnabled=enabled;
    }
}