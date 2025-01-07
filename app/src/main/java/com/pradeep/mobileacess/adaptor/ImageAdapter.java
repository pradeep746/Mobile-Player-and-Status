package com.pradeep.mobileacess.adaptor;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.pradeep.mobileacess.CustomItemClickListener;
import com.pradeep.mobileacess.model.ImageParameterForm;
import com.pradeep.mobileacess.R;

import java.util.ArrayList;

/**
 * Created by pradeep on 12-02-2018.
 */

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {
    private static final String TAG = "ImageAdapter";
    private Context mContext;
    private ArrayList<Object> mRecordedList;
    private CustomItemClickListener mListener;
    private boolean mDeleteStart = false;

    public ImageAdapter(Context context, ArrayList<Object> recordedList, CustomItemClickListener listener) {
        Log.e(TAG, "start click");
        mContext = context;
        mRecordedList = recordedList;
        mListener = listener;
        Log.e(TAG, "end click");
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.image_list_items, parent, false);
        final ViewHolder viewHolder1 = new ViewHolder(view);
        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Log.e(TAG, "long click");
                mDeleteStart = true;
                mListener.onItemLongClick(v, viewHolder1.getAdapterPosition(), mRecordedList);
                final ImageParameterForm imageObject = (ImageParameterForm) mRecordedList.get(viewHolder1.getAdapterPosition());
                imageObject.setSelect(!imageObject.isSelect());
                viewHolder1.linearLayout.setBackground(ContextCompat.getDrawable(mContext, R.drawable.image_delete_select));
                return true;
            }
        });
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TAG, "click click");
                if(mDeleteStart) {
                    final ImageParameterForm imageObject = (ImageParameterForm) mRecordedList.get(viewHolder1.getAdapterPosition());
                    imageObject.setSelect(!imageObject.isSelect());
                    if (imageObject.isSelect()) {
                        viewHolder1.linearLayout.setBackground(ContextCompat.getDrawable(mContext, R.drawable.image_delete_select));
                    } else {
                        viewHolder1.linearLayout.setBackground(ContextCompat.getDrawable(mContext, R.drawable.button_popup));
                    }
                } else {
                    mListener.onItemClick(v, viewHolder1.getAdapterPosition(), mRecordedList);
                }
            }
        });
        return viewHolder1;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final ImageParameterForm imageObject = (ImageParameterForm) mRecordedList.get(position);
        String filePath = imageObject.getmPath();
        Glide.with(mContext).load(filePath).placeholder(R.drawable.maxresdefault).error(R.drawable.no_thumbnail).into(holder.thumbnail);
        if(imageObject.isSelect()) {
            holder.linearLayout.setBackground(ContextCompat.getDrawable(mContext, R.drawable.image_delete_select));
        } else {
            holder.linearLayout.setBackground(ContextCompat.getDrawable(mContext, R.drawable.button_popup));
        }

    }

    @Override
    public int getItemCount() {
        return mRecordedList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView thumbnail;
        public ConstraintLayout linearLayout;

        public ViewHolder(View v) {
            super(v);
            thumbnail = (ImageView) v.findViewById(R.id.thumbnail);
            linearLayout = (ConstraintLayout) v.findViewById(R.id.recorded_linear_layout);
        }
    }

}



