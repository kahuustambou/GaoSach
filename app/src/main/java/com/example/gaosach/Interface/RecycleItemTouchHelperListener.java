package com.example.gaosach.Interface;

import androidx.recyclerview.widget.RecyclerView;

public interface RecycleItemTouchHelperListener {
    void onSwiped(RecyclerView.ViewHolder viewHolder,int direction,int position);
}
