package com.pradeep.mobileacess.adaptor;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.pradeep.mobileacess.R;

import java.util.List;

@SuppressWarnings("unchecked")
public class UserAllSmsAdaptor extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private List<Object> mChanelProgramList;
    private String TAG = "SearchViewAdapter";

    public UserAllSmsAdaptor(Context context, List<Object> ChanelProgramList) {
        this.mContext = context;
        this.mChanelProgramList = ChanelProgramList;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.all_sms_lst, parent, false);
        final ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof ViewHolder) {
            ViewHolder itemHolder = (ViewHolder) holder;
            String number = (String) mChanelProgramList.get(position);
            itemHolder.mSmsMessage.setText(number);
        }

    }


    @Override
    public int getItemCount() {
        if (mChanelProgramList == null) {
            return 0;
        }
        return mChanelProgramList.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mSmsMessage;

        public ViewHolder(View v) {
            super(v);
            mSmsMessage = (TextView) v.findViewById(R.id.user_all_sms);
        }

    }
}