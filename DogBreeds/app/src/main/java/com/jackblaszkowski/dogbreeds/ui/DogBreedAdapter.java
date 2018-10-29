package com.jackblaszkowski.dogbreeds.ui;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.jackblaszkowski.dogbreeds.R;
import com.jackblaszkowski.dogbreeds.database.DogBreedEntity;

import java.util.List;

public class DogBreedAdapter extends RecyclerView.Adapter<DogBreedAdapter.BreedViewHolder> {

    private final LayoutInflater mInflater;
    private final MainActivityFragment mFragment;
    private boolean mOnline = true;
    private List<DogBreedEntity> mEntities;

    DogBreedAdapter(MainActivityFragment mainActivityFragment) {
        mInflater = LayoutInflater.from(mainActivityFragment.getContext());
        mFragment = mainActivityFragment;
    }

    @Override
    public BreedViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        View itemView = mInflater.inflate(R.layout.recyclerview_item, parent, false);


        return new BreedViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final BreedViewHolder holder, final int position) {

        final DogBreedEntity current = mEntities.get(position);

        // Capitalize breed name
        final String capBreedName = current.getBreed().substring(0, 1).toUpperCase() + current.getBreed().substring(1);
        String capSubBreedName = current.getSubBreed().substring(0, 1).toUpperCase() + current.getSubBreed().substring(1);

        holder.breedTextView.setText(capBreedName);
        if (capBreedName.equals(capSubBreedName))
            holder.subBreedTextView.setText("");
        else
            holder.subBreedTextView.setText("-  " + capSubBreedName);

        final Context context = holder.parentLayout.getContext();


        Glide.with(context).load(current.getUrlOne())
                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                .placeholder(R.drawable.ic_image_gray_24dp)
                .error(R.drawable.ic_image_gray_24dp)
                .override(150, 150)
                .centerCrop()
                .into(holder.imgOneView);

        Glide.with(context).load(current.getUrlTwo())
                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                .placeholder(R.drawable.ic_image_gray_24dp)
                .error(R.drawable.ic_image_gray_24dp)
                .override(150, 150)
                .centerCrop()
                .into(holder.imgTwoView);

        Glide.with(context).load(current.getUrlThree())
                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                .placeholder(R.drawable.ic_image_gray_24dp)
                .error(R.drawable.ic_image_gray_24dp)
                .override(150, 150)
                .centerCrop()
                .into(holder.imgThreeView);


        if (mOnline)
            holder.button.setEnabled(true);
        else
            holder.button.setEnabled(false);

        holder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                MorePhotosFragment fragment = MorePhotosFragment.newInstance(current.getBreed(), current.getSubBreed());

                ((FragmentActivity)context).getSupportFragmentManager().beginTransaction()
                            .replace(R.id.main_container, fragment)
                            .addToBackStack(null)
                            .commit();

            }
        });

    }

    void setEntities(List<DogBreedEntity> entities) {

        mEntities = entities;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (mEntities != null)
            return mEntities.size();
        else return 0;
    }

    public void setConnectivity(boolean online) {
        mOnline = online;

    }

    class BreedViewHolder extends RecyclerView.ViewHolder {
        private final TextView breedTextView;
        private final TextView subBreedTextView;

        private final ImageView imgOneView;
        private final ImageView imgTwoView;
        private final ImageView imgThreeView;

        private final AppCompatButton button;
        private final FrameLayout placeholder;

        private final LinearLayout parentLayout;

        private BreedViewHolder(View itemView) {
            super(itemView);
            breedTextView = itemView.findViewById(R.id.breed_name);
            subBreedTextView = itemView.findViewById(R.id.sub_breed_name);

            imgOneView = itemView.findViewById(R.id.picture_one);
            imgTwoView = itemView.findViewById(R.id.picture_two);
            imgThreeView = itemView.findViewById(R.id.picture_three);
            placeholder = itemView.findViewById(R.id.more_container);

            button = itemView.findViewById(R.id.more_button);
            parentLayout = itemView.findViewById(R.id.list_item_layout);
        }
    }
}
