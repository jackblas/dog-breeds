package com.jackblaszkowski.dogbreeds.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.jackblaszkowski.dogbreeds.R;
import com.jackblaszkowski.dogbreeds.Utils;
import com.jackblaszkowski.dogbreeds.database.DogBreedEntity;
import com.jackblaszkowski.dogbreeds.repository.Resource;
import com.jackblaszkowski.dogbreeds.viewmodel.DogBreedViewModel;

import java.util.List;

public class MainActivityFragment extends Fragment {

    private DogBreedViewModel mViewModel;
    private RecyclerView mRecyclerView;
    private DogBreedAdapter adapter;
    private ProgressBar mProgressBar;
    private View mRootView;

    private OnFragmentInteractionListener mListener;
    private BroadcastReceiver mConnectivityChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (!(Utils.isOnline(context))) {

                //Off-line
                RecyclerView rv = mRootView.findViewById(R.id.recyclerview);
                DogBreedAdapter adapter = (DogBreedAdapter) rv.getAdapter();
                adapter.setConnectivity(false);
                adapter.notifyDataSetChanged();

                mListener.onConnectivityChanged(false);

            } else {
                //On-line

                RecyclerView rv = mRootView.findViewById(R.id.recyclerview);
                DogBreedAdapter adapter = (DogBreedAdapter) rv.getAdapter();
                adapter.setConnectivity(true);
                adapter.notifyDataSetChanged();

                mListener.onConnectivityChanged(true);
            }
        }

    };

    public MainActivityFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mRootView = inflater.inflate(R.layout.fragment_main, container, false);
        mRecyclerView = mRootView.findViewById(R.id.recyclerview);

        adapter = new DogBreedAdapter(this);
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));

        mProgressBar =  mRootView.findViewById(R.id.progressBar1);

        return mRootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mViewModel = ViewModelProviders.of(this).get(DogBreedViewModel.class);
        mViewModel.getDogBreeds().removeObservers(this);
        mViewModel.setRefresh(false);

        // Add an observer on the LiveData .
        mViewModel.getDogBreeds().observe(this, new Observer<Resource<List<DogBreedEntity>>>() {
            @Override
            public void onChanged(@Nullable final Resource<List<DogBreedEntity>> resource) {
                // Update the cached copy of data in the adapter.
                if(resource.data != null) {
                    adapter.setEntities(resource.data);
                }

                resetProgressBar(resource.status);

                if (resource.status == Resource.Status.ERROR) {
                    showErrorMessage(resource.errorType);
                }

            }
        });

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }

        getActivity().registerReceiver(
                mConnectivityChangeReceiver,
                new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        getActivity().unregisterReceiver(mConnectivityChangeReceiver);
    }

    void setRefresh(){
        mViewModel.setRefresh(true);
    }


    RecyclerView getRecyclerView() {
        return mRecyclerView;
    }

    public interface OnFragmentInteractionListener {

        void onConnectivityChanged(boolean enabled);
    }

    private void resetProgressBar(Resource.Status status){

        if (status == Resource.Status.LOADING) {
            mProgressBar.setVisibility(View.VISIBLE);
        } else {
            mProgressBar.setVisibility(View.GONE);
        }
    }

    private void showErrorMessage(Resource.ErrorType error) {

        String message;


        switch (error){
            case NOT_FOUND:
                message = getResources().getString(R.string.not_found_error_message);
                break;
            case SERVER_ERROR:
                message = getResources().getString(R.string.server_error_message);
                break;
            case NO_CONNECTION:
                message = getResources().getString(R.string.no_connection_message);
                break;

            default:
                message = getResources().getString(R.string.unexpected_error_message);
                break;
        }

        Snackbar snackbar = Snackbar.make(mRootView, message, Snackbar.LENGTH_LONG);

        View sbView = snackbar.getView();
        TextView textView = sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.YELLOW);
        snackbar.show();
    }
}
