package com.pradeep.mobileacess.adaptor;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.widget.ImageView;
import android.view.View;
import android.widget.TextView;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.pradeep.mobileacess.CustomItemClickListener;
import com.pradeep.mobileacess.R;

import java.io.File;
import java.util.List;

public class AppDataDisplayAdaptor extends RecyclerView.Adapter<AppDataDisplayAdaptor.ViewHolder> {
    private Context mContext;
    private List<Object> stringList;
    private CustomItemClickListener mListener;

    public AppDataDisplayAdaptor(Context context, List<Object> list, CustomItemClickListener listener) {
        mContext = context;
        stringList = list;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.cardview_layout, parent, false);
        final ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int position) {
        final String ApplicationPackageName = (String) stringList.get(position);
        String ApplicationLabelName = "";
        ApplicationInfo applicationInfo;
        Drawable drawable;
        try {
            PackageManager packageManager = mContext.getPackageManager();
            applicationInfo = packageManager.getApplicationInfo(ApplicationPackageName, 0);
            if (applicationInfo != null) {
                ApplicationLabelName = (String) packageManager.getApplicationLabel(applicationInfo);
            }
            drawable = packageManager.getApplicationIcon(ApplicationPackageName);
            viewHolder.textView_App_Name.setText(ApplicationLabelName);
            double size = new File(mContext.getPackageManager().getApplicationInfo(ApplicationPackageName, 0).publicSourceDir).length();
            String packagesize = "";
            if(size >1000000) {
                size = Math.round(size/1000000);
                packagesize += size +"MB";
            } else if(size > 1000) {
                size = Math.round(size/1000);
                packagesize += size +"KB";
            } else {
                size = Math.round(size);
                packagesize += size +"B";
            }
            viewHolder.textView_App_Package_Name.setText(packagesize);
            viewHolder.imageView.setImageDrawable(drawable);
            viewHolder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mListener.onItemClick(view, viewHolder.getAdapterPosition(), stringList);
                }
            });
            viewHolder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    mListener.onItemLongClick(v, viewHolder.getAdapterPosition(), stringList);
                    return true;
                }
            });
        } catch (Exception e){
            e.printStackTrace();
            viewHolder.textView_App_Package_Name.setText("" + 0 + "MB");
        }
    }

    @Override
    public int getItemCount() {
        return stringList.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ConstraintLayout cardView;
        public ImageView imageView;
        public TextView textView_App_Name;
        public TextView textView_App_Package_Name;

        public ViewHolder(View view) {
            super(view);
            cardView = (ConstraintLayout) view.findViewById(R.id.card_view);
            imageView = (ImageView) view.findViewById(R.id.image_view);
            textView_App_Name = (TextView) view.findViewById(R.id.app_name);
            textView_App_Package_Name = (TextView) view.findViewById(R.id.app_package_Name);
        }
    }
}