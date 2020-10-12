package com.pradhyups.rentcharging;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.squareup.picasso.Picasso;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class PlaceListAdapter extends RecyclerView.Adapter<PlaceListAdapter.PlaceListHolder> {

    private static final String TAG = "stringuri";
    private Context mContext;
    private int mCountLimit = 1;
    private List<UploadData> mUploads;

    public PlaceListAdapter(Context context, List<UploadData> uploads) {
        mContext = context;
        mUploads = uploads;
    }

    @NonNull
    @Override
    public PlaceListAdapter.PlaceListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.list_item, parent, false);
        return new PlaceListHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaceListHolder holder, int position) {
        UploadData uploadCurrent = mUploads.get(position);
        holder.mName.setText(uploadCurrent.getName());
        String uri = uploadCurrent.getStationImage();
        Log.d(TAG, "value is" + uri);
        Glide.with(mContext).load(uploadCurrent.getStationImage())
                .fitCenter()
                .diskCacheStrategy(DiskCacheStrategy.NONE)         //ALL or NONE as your requirement
                .placeholder(R.mipmap.ic_launcher)
                .into(holder.mImage);

    }

    @Override
    public int getItemCount() {
        return mUploads.size();
    }

    public class PlaceListHolder extends RecyclerView.ViewHolder {

        public TextView mName;
        public ImageView mImage;

        public PlaceListHolder(View view) {
            super(view);

            mName = view.findViewById(R.id.text_name);
            mImage = view.findViewById(R.id.station_image);
        }
    }
}
