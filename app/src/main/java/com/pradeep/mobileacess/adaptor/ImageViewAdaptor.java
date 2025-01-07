package com.pradeep.mobileacess.adaptor;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.pradeep.mobileacess.CustomItemClickListener;
import com.pradeep.mobileacess.R;

import java.util.ArrayList;

/**
 * Created by pradeep on 12-02-2018.
 */

public class ImageViewAdaptor extends RecyclerView.Adapter<ImageViewAdaptor.ViewHolder> {
    private static final String TAG = "RecordedAdapter";
    private Context mContext;
    private ArrayList<Object> mRecordedList;
    private CustomItemClickListener mListener;

    public ImageViewAdaptor(Context context, ArrayList<Object> recordedList, CustomItemClickListener listener) {
        mContext = context;
        mRecordedList = recordedList;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.record_list_items, parent, false);
        final ViewHolder viewHolder1 = new ViewHolder(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onItemClick(v, viewHolder1.getAdapterPosition(), mRecordedList);
            }
        });

        view.setOnLongClickListener(new OnLongClickListener() {
            public boolean onLongClick(View v) {
                mListener.onItemLongClick(v, viewHolder1.getAdapterPosition(), mRecordedList);
                return true;
            }
        });

        return viewHolder1;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        String filePath = ""+mRecordedList.get(position);
        Glide.with(mContext).load(filePath).placeholder(R.drawable.maxresdefault).error(R.drawable.no_thumbnail).into(holder.thumbnail);
    }

    @Override
    public int getItemCount() {
        return mRecordedList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView thumbnail;
        public LinearLayout linearLayout;

        public ViewHolder(View v) {
            super(v);
            thumbnail = (ImageView) v.findViewById(R.id.thumbnail);
            linearLayout = (LinearLayout)v.findViewById(R.id.recorded_linear_layout);

        }
    }

}



