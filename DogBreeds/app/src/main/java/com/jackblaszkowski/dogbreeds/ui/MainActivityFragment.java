package com.jackblaszkowski.dogbreeds.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jackblaszkowski.dogbreeds.R;
import com.jackblaszkowski.dogbreeds.Utils;
import com.jackblaszkowski.dogbreeds.database.DogBreedEntity;
import com.jackblaszkowski.dogbreeds.viewmodel.DogBreedViewModel;

import java.util.List;

public class MainActivityFragment extends Fragment {

    private DogBreedViewModel mViewModel;
    private RecyclerView mRecyclerView;
    private DogBreedAdapter adapter;
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
                Utils.setServerStatus(context, Utils.STATUS_NO_CONNECTION);

            } else {
                //On-line

                RecyclerView rv = mRootView.findViewById(R.id.recyclerview);
                DogBreedAdapter adapter = (DogBreedAdapter) rv.getAdapter();
                adapter.setConnectivity(true);
                adapter.notifyDataSetChanged();

                mListener.onConnectivityChanged(true);
                Utils.setServerStatus(context, Utils.STATUS_SERVER_OK);

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
        //return inflater.inflate(R.layout.fragment_main, container, false);
        mRootView = inflater.inflate(R.layout.fragment_main, container, false);
        mRecyclerView = mRootView.findViewById(R.id.recyclerview);

        adapter = new DogBreedAdapter(this);
        //adapter.setHasStableIds(true);
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));

        return mRootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        mViewModel = ViewModelProviders.of(getActivity()).get(DogBreedViewModel.class);

        // Add an observer on the LiveData .
        mViewModel.getDogBreeds().observe(this, new Observer<List<DogBreedEntity>>() {
            @Override
            public void onChanged(@Nullable final List<DogBreedEntity> entityList) {
                // Update the cached copy of data in the adapter.
                adapter.setEntities(entityList);
                // Hide progress bar:
                //Log.d("MainActivityFragment","Hide progress bar");
                mListener.onDataLoaded();
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


    RecyclerView getRecyclerView() {
        return mRecyclerView;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        //void onFragmentInteraction();
        void onDataLoaded();

        void onConnectivityChanged(boolean enabled);
    }
}
