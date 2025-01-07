package com.pradeep.mobileacess;


import android.view.View;

import java.util.List;

/**
 * Created by pradeep on 20-02-2018.
 */

public interface CustomItemClickListener {
    public void onItemClick(View v, int position, final List<Object> list);
    public boolean onItemLongClick(View v, int position, final List<Object> list);
}
