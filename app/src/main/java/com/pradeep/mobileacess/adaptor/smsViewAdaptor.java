package com.pradeep.mobileacess.adaptor;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.pradeep.mobileacess.CustomItemClickListener;
import com.pradeep.mobileacess.R;
import com.pradeep.mobileacess.model.UserSmsDataParameterForm;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pradeep on 12-02-2018.
 */

public class smsViewAdaptor extends RecyclerView.Adapter<smsViewAdaptor.ViewHolder> {
    private static final String TAG = "smsViewAdaptor";
    private Context mContext;
    private List<Object> mRecordedList;
    private CustomItemClickListener mListener;

    public smsViewAdaptor(Context context, List<Object> recordedList, CustomItemClickListener listener) {
        mContext = context;
        mRecordedList = recordedList;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.sms_send_view_list_items, parent, false);
        final ViewHolder viewHolder1 = new ViewHolder(view);
        return viewHolder1;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        UserSmsDataParameterForm filePath = (UserSmsDataParameterForm) mRecordedList.get(position);
        holder.fileName.setText("" + filePath.getBody());
        holder.user_number.setText(""+filePath.getUserName());
        holder.mFirstLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TAG,"mFirstLayout");
                mListener.onItemClick(v, position, mRecordedList);
            }
        });

        holder.mFirstLayout.setOnLongClickListener(new OnLongClickListener() {
            public boolean onLongClick(View v) {
                Log.e(TAG,"mFirstLayout");
                mListener.onItemLongClick(v, position, mRecordedList);
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return mRecordedList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView user_number;
        public TextView fileName;
        public ConstraintLayout mFirstLayout;

        public ViewHolder(View v) {
            super(v);
            user_number = (TextView) v.findViewById(R.id.user_number);
            fileName = (TextView) v.findViewById(R.id.sms_look);
            mFirstLayout = (ConstraintLayout) v.findViewById(R.id.second_layout);
        }
    }

}



