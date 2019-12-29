package com.jo.barlauncher.ui;

import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.jo.barlauncher.R;
import com.jo.barlauncher.model.App;
import com.jo.barlauncher.model.DataManager;
import com.jo.barlauncher.view.ItemTouchHelperViewHolder;
import com.jo.barlauncher.view.OnStartDragListener;

import java.util.List;

class RowsAdapter extends RecyclerView.Adapter<RowsAdapter.ViewHolder> {
    private List<Long> rows;
    private DataManager dataManager;
    private OnItemClickListener onItemClickListener;
    private OnStartDragListener onStartDragListener;

    public RowsAdapter(List<Long> rows, DataManager dataManager) {
        this.rows = rows;
        this.dataManager = dataManager;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public void setOnStartDragListener(OnStartDragListener onStartDragListener) {
        this.onStartDragListener = onStartDragListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View itemView = layoutInflater.inflate(R.layout.list_item_row, parent, false);

        return new ViewHolder(itemView, layoutInflater);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        List<App> apps = dataManager.loadAppList(rows.get(position));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemClickListener.onItemClick(holder.getAdapterPosition());
            }
        });

        holder.rowLayout.removeAllViews();

        if (apps.isEmpty()) {
            holder.rowLayout.addView(holder.layoutInflater.inflate(R.layout.text_row_empty, holder.rowLayout, false));
        } else {
            for (App app : apps) {
                ImageView iconImageView = (ImageView) holder.layoutInflater.inflate(R.layout.image_app, holder.rowLayout, false);
                iconImageView.setImageBitmap(app.icon);

                holder.rowLayout.addView(iconImageView);
            }
        }

        // Start a drag whenever the handle view it touched
        holder.handleView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_DOWN) {
                    onStartDragListener.onStartDrag(holder);
                }
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return rows.size();
    }

    interface OnItemClickListener {
        void onItemClick(int position);
    }

    static class ViewHolder extends RecyclerView.ViewHolder implements ItemTouchHelperViewHolder {
        LayoutInflater layoutInflater;
        LinearLayout rowLayout;
        ImageView handleView;

        public ViewHolder(View itemView, LayoutInflater layoutInflater) {
            super(itemView);

            this.layoutInflater = layoutInflater;
            rowLayout = (LinearLayout) itemView.findViewById(R.id.layout_row);
            handleView = (ImageView) itemView.findViewById(R.id.drag_handle);
        }

        @Override
        public void onItemSelected() {
            itemView.setAlpha(0.5f);
        }

        @Override
        public void onItemClear() {
            itemView.setAlpha(1.0f);
        }
    }
}