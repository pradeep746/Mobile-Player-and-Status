package com.pradeep.mobileacess.adaptor;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.pradeep.mobileacess.CustomItemClickListener;
import com.pradeep.mobileacess.R;
import com.pradeep.mobileacess.model.HomeItemParameterForm;

import java.util.List;

public class LanguageAdaptor extends RecyclerView.Adapter<LanguageAdaptor.ViewHolder> {
    private static final String TAG = "LanguageAdaptor";
    private Context mContext;
    private List<Object> mItemList;
    private CustomItemClickListener mListener;

    public LanguageAdaptor(Context context, List<Object> recordedList, CustomItemClickListener listener) {
        mContext = context;
        mItemList = recordedList;
        mListener = listener;
    }

    @Override
    public LanguageAdaptor.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.content_item_home, parent, false);
        final LanguageAdaptor.ViewHolder viewHolder1 = new LanguageAdaptor.ViewHolder(view);
        return viewHolder1;
    }

    @Override
    public void onBindViewHolder(final LanguageAdaptor.ViewHolder holder, final int position) {
        HomeItemParameterForm temp = (HomeItemParameterForm) mItemList.get(position);
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