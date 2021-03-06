package com.example.gaosach.Helper;

import android.graphics.Canvas;
import android.view.View;

import com.example.gaosach.Interface.RecycleItemTouchHelperListener;
import com.example.gaosach.ViewHolder.CartViewHolder;
import com.example.gaosach.ViewHolder.FavouritesViewHolder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

public class RecycleItemTouchHelper extends ItemTouchHelper.SimpleCallback {

    private RecycleItemTouchHelperListener listener;

    public RecycleItemTouchHelper(int dragDirs, int swipeDirs,RecycleItemTouchHelperListener listener) {
        super(dragDirs, swipeDirs);
        this.listener= listener;
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        return true;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        if(listener!= null)
            listener.onSwiped(viewHolder,direction,viewHolder.getAdapterPosition());
    }

    @Override
    public int convertToAbsoluteDirection(int flags, int layoutDirection) {
        return super.convertToAbsoluteDirection(flags, layoutDirection);
    }

    @Override
    public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {

        if(viewHolder instanceof CartViewHolder) {
            View foregroundView = ((CartViewHolder) viewHolder).view_foreground;
            getDefaultUIUtil().clearView(foregroundView);
        }
        else if(viewHolder instanceof FavouritesViewHolder)
        {
            View foregroundView =((FavouritesViewHolder)viewHolder).view_foreground;
            getDefaultUIUtil().clearView(foregroundView);

        }


    }

    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        if(viewHolder instanceof CartViewHolder) {
            View foregroundView = ((CartViewHolder) viewHolder).view_foreground;
            getDefaultUIUtil().onDraw(c, recyclerView, foregroundView, dX, dY, actionState, isCurrentlyActive);
        }
        else if(viewHolder instanceof FavouritesViewHolder)
        {
            View foregroundView = ((FavouritesViewHolder) viewHolder).view_foreground;
            getDefaultUIUtil().onDraw(c, recyclerView, foregroundView, dX, dY, actionState, isCurrentlyActive);

        }
    }

    @Override
    public void onSelectedChanged(@Nullable RecyclerView.ViewHolder viewHolder, int actionState) {
        if(viewHolder!= null)
        {
            if(viewHolder instanceof CartViewHolder)
            {
                View foregroundView = ((CartViewHolder) viewHolder).view_foreground;
                getDefaultUIUtil().onSelected(foregroundView);


            }
            else if(viewHolder instanceof FavouritesViewHolder)
            {
                View foregroundView = ((FavouritesViewHolder) viewHolder).view_foreground;
                getDefaultUIUtil().onSelected(foregroundView);

            }
        }

    }

    @Override
    public void onChildDrawOver(@NonNull Canvas c, @NonNull RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {


        if(viewHolder instanceof CartViewHolder)
        {
            View foregroundView=((CartViewHolder)viewHolder).view_foreground;
            getDefaultUIUtil().onDrawOver(c,recyclerView,foregroundView,dX,dY,actionState,isCurrentlyActive);


        }
        else if(viewHolder instanceof FavouritesViewHolder)
        {
            View foregroundView=((FavouritesViewHolder)viewHolder).view_foreground;
            getDefaultUIUtil().onDrawOver(c,recyclerView,foregroundView,dX,dY,actionState,isCurrentlyActive);

        }
    }
}
