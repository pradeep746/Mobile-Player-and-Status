package com.pradeep.mobileacess.adaptor;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.pradeep.mobileacess.model.ContactDataParameterForm;
import com.pradeep.mobileacess.CustomItemClickListener;
import com.pradeep.mobileacess.R;

import java.util.ArrayList;
import java.util.List;
public class SearchViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable {
    private Context mContext;
    CustomItemClickListener mListener;
    private List<Object> mChanelProgramList;
    private List<Object> mChanelProgramListFiltered;
    private String TAG = "SearchViewAdapter";

    public SearchViewAdapter(Context context, List<Object> ChanelProgramList, CustomItemClickListener listener) {
        this.mContext = context;
        this.mChanelProgramList = ChanelProgramList;
        this.mListener = listener;
        mChanelProgramListFiltered = new ArrayList<Object>();
        mChanelProgramListFiltered.addAll(ChanelProgramList);
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.contact_list, parent, false);
        final ViewHolder viewHolder = new ViewHolder(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TAG,""+viewHolder.getAdapterPosition());
                mListener.onItemClick(v, viewHolder.getAdapterPosition(), mChanelProgramListFiltered);
            }
        });
        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mListener.onItemLongClick(v,viewHolder.getAdapterPosition(),mChanelProgramListFiltered);
                return true;
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof ViewHolder) {
            ViewHolder itemHolder = (ViewHolder) holder;
            ContactDataParameterForm number = (ContactDataParameterForm) mChanelProgramListFiltered.get(position);
            String name = number.getUserName();
            itemHolder.mSearchName.setText(""+name);
            Bitmap photo = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.no_thumbnail);
            String uriddata = number.getThumbNail();
            if(uriddata!= null) {
                Uri data = Uri.parse(uriddata);
                itemHolder.mThumbnail.setImageURI(data);
            } else {
                itemHolder.mThumbnail.setImageBitmap(photo);
            }
        }

    }

    @Override
    public int getItemCount() {
        if (mChanelProgramListFiltered == null) {
            return 0;
        }
        return mChanelProgramListFiltered.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mSearchName;
        public ImageView mThumbnail;

        public ViewHolder(View v) {
            super(v);
            mSearchName = (TextView) v.findViewById(R.id.phone_number_data);
            mThumbnail = (ImageView) v.findViewById(R.id.user_image);

        }

    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                mChanelProgramListFiltered.clear();
                String charString = charSequence.toString().toLowerCase().trim();
                if (mChanelProgramList != null) {
                    Log.v(TAG, "Start filter");
                } else {
                    Log.v(TAG, "no data available");
                    return null;
                }
                if(charString.isEmpty()){
                    Log.v(TAG, "empty search");
                    mChanelProgramListFiltered.addAll(mChanelProgramList);
                    FilterResults filterResults = new FilterResults();
                    filterResults.values = mChanelProgramListFiltered;
                    return filterResults;
                }
                for (int i = 0; i < mChanelProgramList.size(); i++) {
                    ContactDataParameterForm channel = (ContactDataParameterForm) mChanelProgramList.get(i);
                    String name = channel.getUserName();
                    if (name.toLowerCase().contains(charString.toLowerCase())) {
                        mChanelProgramListFiltered.add( 0,channel);
                    }
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = mChanelProgramListFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                if(filterResults == null){
                    notifyDataSetChanged();
                    return;
                }
                mChanelProgramListFiltered = (ArrayList<Object>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }
}