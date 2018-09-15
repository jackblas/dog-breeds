package com.jackblaszkowski.dogbreeds.ui;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.jackblaszkowski.dogbreeds.R;

import java.util.List;

public class MorePhotosAdapter extends RecyclerView.Adapter<MorePhotosAdapter.PhotoViewHolder> {

    class PhotoViewHolder extends RecyclerView.ViewHolder {

        private final ImageView imageView;
        private final CardView cardView;

        private final LinearLayout parentLayout;

        private PhotoViewHolder(View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.photo);
            cardView = itemView.findViewById(R.id.more_card_view);
            parentLayout = itemView.findViewById(R.id.more_item_layout);
        }
    }

    MorePhotosAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
        mContext=context;
    }

    private final LayoutInflater mInflater;
    private List<String> mUrls;
    private Context mContext;

    @NonNull
    @Override
    public MorePhotosAdapter.PhotoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.more_recyclerview_item, parent, false);

        return new PhotoViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MorePhotosAdapter.PhotoViewHolder holder, int position) {
        final String current = mUrls.get(position);


            Glide.with(mContext).load(current)
                    .diskCacheStrategy(DiskCacheStrategy.RESULT)
                    .placeholder(R.drawable.ic_image_gray_24dp)
                    .error(R.drawable.ic_image_gray_24dp)
                    .override(150, 150)
                    .centerCrop()
                    .into(holder.imageView);

    }

    void setEntities(List<String> entities){
        mUrls = entities;
        notifyDataSetChanged();
    }


    @Override
    public int getItemCount() {
        if (mUrls != null)
            return mUrls.size();
        else return 0;
    }
}
