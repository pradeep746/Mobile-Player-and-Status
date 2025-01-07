package com.pradeep.mobileacess.adaptor;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.pradeep.mobileacess.CustomItemClickListener;
import com.pradeep.mobileacess.R;
import com.pradeep.mobileacess.model.HomeItemParameterForm;
import com.pradeep.mobileacess.model.ImageParameterForm;

import java.util.ArrayList;
import java.util.List;

public class HomeItemAdaptor extends RecyclerView.Adapter<HomeItemAdaptor.ViewHolder> {
    private static final String TAG = "HomeAdaptor";
    private Context mContext;
    private List<Object> mItemList;
    private CustomItemClickListener mListener;
    private boolean mDeleteStart = false;

    public HomeItemAdaptor(Context context, List<Object> recordedList, CustomItemClickListener listener) {
        Log.e(TAG, "start click");
        mContext = context;
        mItemList = recordedList;
        mListener = listener;
    }

    @Override
    public HomeItemAdaptor.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.content_item_home, parent, false);
        final HomeItemAdaptor.ViewHolder viewHolder1 = new HomeItemAdaptor.ViewHolder(view);
        return viewHolder1;
    }

    @Override
    public void onBindViewHolder(final HomeItemAdaptor.ViewHolder holder, final int position) {
        HomeItemParameterForm temp = (HomeItemParameterForm) mItemList.get(position);
        if(temp.getId() == 0) {
            holder.mName.setText("Image");
            holder.mImageView.setImageDrawable(mContext.getDrawable(R.drawable.galary));
        } else if(temp.getId() == 1) {
            holder.mName.setText("Video");
            holder.mImageView.setImageDrawable(mContext.getDrawable(R.drawable.videoplayer));
        } else if(temp.getId() == 2) {
            holder.mName.setText("Audio");
            holder.mImageView.setImageDrawable(mContext.getDrawable(R.drawable.music_player));
        } else if(temp.getId() == 3) {
            holder.mName.setText("Other File");
            holder.mImageView.setImageDrawable(mContext.getDrawable(R.drawable.other_file));
        } else if(temp.getId() == 4) {
            holder.mName.setText("Speech Listener");
            holder.mImageView.setImageDrawable(mContext.getDrawable(R.drawable.listen));
        } else if(temp.getId() == 5) {
            holder.mName.setText("Text Speech");
            holder.mImageView.setImageDrawable(mContext.getDrawable(R.drawable.text_to_speech));
        } else if(temp.getId() == 6) {
            holder.mName.setText("Voice Recorder");
            holder.mImageView.setImageDrawable(mContext.getDrawable(R.drawable.video_recorder));
        } else if(temp.getId() == 7) {
            holder.mName.setText("Phone Status");
            holder.mImageView.setImageDrawable(mContext.getDrawable(R.drawable.phone_status));
        } else if(temp.getId() == 8) {
            holder.mName.setText("SMS");
            holder.mImageView.setImageDrawable(mContext.getDrawable(R.drawable.sms));
        } else if(temp.getId() == 9) {
            holder.mName.setText("App Detail");
            holder.mImageView.setImageDrawable(mContext.getDrawable(R.drawable.app_details));
        } else if(temp.getId() == 10) {
            holder.mName.setText("Call");
            holder.mImageView.setImageDrawable(mContext.getDrawable(R.drawable.call_details));
        } else {
            holder.mName.setText("Memory");
            holder.mImageView.setImageDrawable(mContext.getDrawable(R.drawable.memory_details));
        }
        holder.mCardView.setOnClickListener(View-> {
            mListener.onItemClick(View,position,mItemList);
        });
    }

    @Override
    public int getItemCount() {
        return mItemList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView mImageView;
        public CardView mCardView;
        public TextView mName;

        public ViewHolder(View v) {
            super(v);
            mImageView = (ImageView) v.findViewById(R.id.imave_view);
            mCardView = (CardView) v.findViewById(R.id.image);
            mName = (TextView) v.findViewById(R.id.name);
        }
    }

}