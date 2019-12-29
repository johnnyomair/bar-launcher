package com.jo.barlauncher.ui;

import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jo.barlauncher.R;
import com.jo.barlauncher.model.App;
import com.jo.barlauncher.view.ItemTouchHelperViewHolder;
import com.jo.barlauncher.view.OnStartDragListener;

import java.util.List;

class AppsAdapter extends RecyclerView.Adapter<AppsAdapter.ViewHolder> {
    private final List<App> apps;

    private final boolean dragable;

    private final boolean selectable;

    private OnItemClickListener onItemClickListener;

    private OnStartDragListener onStartDragListener;

    public AppsAdapter(List<App> apps, boolean dragable, boolean selectable) {
        this.apps = apps;
        this.dragable = dragable;
        this.selectable = selectable;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setOnStartDragListener(OnStartDragListener onStartDragListener) {
        this.onStartDragListener = onStartDragListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_app, parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        App app = apps.get(position);

        holder.labelTextView.setText(app.label);
        holder.iconImageView.setImageBitmap(app.icon);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectable) {
                    view.setSelected(!view.isSelected());
                    return;
                }

                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(holder.getAdapterPosition());
                }
            }
        });

        if (dragable) {
            holder.dragHandleImageView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_DOWN) {
                        onStartDragListener.onStartDrag(holder);
                    }
                    return false;
                }
            });
        } else {
            holder.dragHandleImageView.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return apps.size();
    }

    interface OnItemClickListener {
        void onItemClick(int position);
    }

    static class ViewHolder extends RecyclerView.ViewHolder implements ItemTouchHelperViewHolder {
        TextView labelTextView;
        ImageView iconImageView, dragHandleImageView;

        public ViewHolder(View itemView) {
            super(itemView);

            labelTextView = (TextView) itemView.findViewById(R.id.appLabel);
            iconImageView = (ImageView) itemView.findViewById(R.id.appIcon);
            dragHandleImageView = (ImageView) itemView.findViewById(R.id.appDragHandle);
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