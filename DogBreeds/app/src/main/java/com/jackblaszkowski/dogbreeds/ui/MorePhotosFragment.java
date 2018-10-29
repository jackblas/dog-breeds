package com.jackblaszkowski.dogbreeds.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.jackblaszkowski.dogbreeds.R;
import com.jackblaszkowski.dogbreeds.database.DogImageEntity;
import com.jackblaszkowski.dogbreeds.repository.Resource;
import com.jackblaszkowski.dogbreeds.viewmodel.BreedImagesViewModel;

import java.util.List;

public class MorePhotosFragment extends Fragment{

    static final String ARG_PARAM1 = "breed";
    static final String ARG_PARAM2 = "subbreed";

    private String mBreed;
    private String mSubBreed;

    private BreedImagesViewModel mViewModel;
    private MorePhotosAdapter adapter;
    private ProgressBar mProgressBar;
    private View mRootView;

    public MorePhotosFragment() {
    }

    public static MorePhotosFragment newInstance(String param1, String param2) {
        MorePhotosFragment fragment = new MorePhotosFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (getArguments() != null) {
            mBreed = getArguments().getString(ARG_PARAM1);
            mSubBreed = getArguments().getString(ARG_PARAM2);

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mRootView = inflater.inflate(R.layout.fragment_more, container, false);
        RecyclerView recyclerView = mRootView.findViewById(R.id.more_recyclerview);

        adapter = new MorePhotosAdapter(getActivity());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext(), LinearLayoutManager.VERTICAL, false));

        mProgressBar =  mRootView.findViewById(R.id.progressBar1);

        return mRootView;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mViewModel= ViewModelProviders.of(this).get(BreedImagesViewModel.class);
        mViewModel.getMoreImages().removeObservers(this);
        mViewModel.setBreed(mBreed, mSubBreed);

        // Add an observer on the LiveData .
        mViewModel.getMoreImages().observe(this, new Observer<Resource<List<DogImageEntity>>>() {
            @Override
            public void onChanged(@Nullable final Resource<List<DogImageEntity>> resource) {
                Log.d("Photos Fragment", "onChanged() - status: " + resource.status );
                // Update the cached copy of data in the adapter.
                adapter.setEntities(resource.data);

                resetProgressBar(resource.status);

                if (resource.status == Resource.Status.ERROR) {
                    showErrorMessage(resource.errorType);
                }

            }
        });

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Hide options menu in this fragment
        menu.setGroupVisible(R.id.menu_group, false);
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
