package com.jackblaszkowski.dogbreeds.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jackblaszkowski.dogbreeds.R;
import com.jackblaszkowski.dogbreeds.viewmodel.BreedImagesViewModel;

import java.util.List;

public class MorePhotosFragment extends Fragment{

    static final String ARG_PARAM1 = "breed";
    static final String ARG_PARAM2 = "subbreed";

    private String mBreed;
    private String mSubBreed;

    private BreedImagesViewModel mViewModel;
    private MorePhotosAdapter adapter;
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
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext(), LinearLayoutManager.HORIZONTAL, false));

        mViewModel= ViewModelProviders.of(getActivity()).get(BreedImagesViewModel.class);

        return mRootView;
    }



    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mViewModel.setBreed(mBreed, mSubBreed);
        // Add an observer on the LiveData .
        mViewModel.getMoreImages().observe(this, new Observer<List<String>>() {
            @Override
            public void onChanged(@Nullable final List<String> entityList) {
                // Update the cached copy of data in the adapter.
                adapter.setEntities(entityList);

            }
        });

    }


}
