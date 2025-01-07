package com.pradeep.mobileacess.adaptor;
import android.content.Context;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.pradeep.mobileacess.CustomItemClickListener;
import com.pradeep.mobileacess.R;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by pradeep on 12-02-2018.
 */

public class RecordedAdapter extends RecyclerView.Adapter<RecordedAdapter.ViewHolder> {
    private static final String TAG = "RecordedAdapter";
    private Context mContext;
    private ArrayList<Object> mRecordedList;
    private CustomItemClickListener mListener;
    private int mTypeList;

    public RecordedAdapter(Context context, ArrayList<Object> recordedList, CustomItemClickListener listener,int type) {
        mContext = context;
        mRecordedList = recordedList;
        mListener = listener;
        mTypeList = type;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.record_list_items, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        String filePath = ""+mRecordedList.get(position);
        File file = new File(filePath);
        String str = ""+file.getName();
        Log.e("TAG",".........."+str);
        if(str != null && str.length() > 20) {
            String last12Chars =  str.substring(str.length() - 20);
            holder.fileName.setText(".." + last12Chars);
        } else {
            holder.fileName.setText(""+str);
        }
        if(mTypeList == 0) {
            Glide.with(mContext).load(filePath).placeholder(R.drawable.maxresdefault).error(R.drawable.no_thumbnail).into(holder.thumbnail);
        } else {
            ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) holder.thumbnail.getLayoutParams();
            int marginInPixels = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50,mContext.getResources().getDisplayMetrics());
            layoutParams.topMargin = marginInPixels;
            holder.thumbnail.setLayoutParams(layoutParams);
            holder.thumbnail.setImageDrawable(mContext.getDrawable(R.drawable.ic_audio_file));
        }
        holder.linearLayout.setOnClickListener(View-> {
            mListener.onItemClick(View,position,mRecordedList);
        });
    }

    @Override
    public int getItemCount() {
        return mRecordedList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView thumbnail;
        public TextView fileName;
        public ConstraintLayout linearLayout;

        public ViewHolder(View v) {
            super(v);
            thumbnail = (ImageView) v.findViewById(R.id.thumbnail);
            fileName = (TextView) v.findViewById(R.id.fileName);
            linearLayout = (ConstraintLayout) v.findViewById(R.id.recorded_linear_layout);
        }
    }

}



